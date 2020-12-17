package com.mingri.mybatissmart.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.mingri.langhuan.cabinet.tool.StrTool;
import com.mingri.mybatissmart.ex.MybatisProperties;
import com.mingri.mybatissmart.ex.SmartConfiguration;

@org.springframework.context.annotation.Configuration
@EnableConfigurationProperties(value = { MybatisProperties.class })
@ConditionalOnSingleCandidate(DataSource.class)
@ConditionalOnClass({ SqlSessionFactory.class, SqlSessionFactoryBean.class })
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@AutoConfigureBefore(name = "org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration")
public class MybatisSmartAutoConfiguration implements InitializingBean {

	private final MybatisProperties properties;
	private final SmartProperties smartProperties;

	private final Interceptor[] interceptors;

	@SuppressWarnings("rawtypes")
	private final TypeHandler[] typeHandlers;

	private final ResourceLoader resourceLoader;

	private final DatabaseIdProvider databaseIdProvider;

	private final List<ConfigurationCustomizer> configurationCustomizers;


	@SuppressWarnings("rawtypes")
	public MybatisSmartAutoConfiguration(MybatisProperties properties,
			ObjectProvider<Interceptor[]> interceptorsProvider, ObjectProvider<TypeHandler[]> typeHandlersProvider,
			ResourceLoader resourceLoader, ObjectProvider<DatabaseIdProvider> databaseIdProvider,
			ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {
		this.properties = properties;
		this.smartProperties = properties.getSmart();
		this.interceptors = interceptorsProvider.getIfAvailable();
		this.typeHandlers = typeHandlersProvider.getIfAvailable();
		this.resourceLoader = resourceLoader;
		this.databaseIdProvider = databaseIdProvider.getIfAvailable();
		this.configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
	}

	@Bean
	@ConditionalOnMissingBean
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
		SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
		factory.setDataSource(dataSource);
		factory.setVfs(SpringBootVFS.class);
		if (StringUtils.hasText(this.properties.getConfigLocation())) {
			factory.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
		}
		applyConfiguration(factory);

		if (this.properties.getConfigurationProperties() != null) {
			factory.setConfigurationProperties(this.properties.getConfigurationProperties());
		}
		if (!ObjectUtils.isEmpty(this.interceptors)) {
			factory.setPlugins(this.interceptors);
		}
		if (this.databaseIdProvider != null) {
			factory.setDatabaseIdProvider(this.databaseIdProvider);
		}
		if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
			factory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
		}
		if (this.properties.getTypeAliasesSuperType() != null) {
			factory.setTypeAliasesSuperType(this.properties.getTypeAliasesSuperType());
		}
		if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
			factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
		}
		if (!ObjectUtils.isEmpty(this.typeHandlers)) {
			factory.setTypeHandlers(this.typeHandlers);
		}
		if (!ObjectUtils.isEmpty(this.properties.resolveMapperLocations())) {
			factory.setMapperLocations(this.properties.resolveMapperLocations());
		}

		return factory.getObject();
	}

	private void applyConfiguration(SqlSessionFactoryBean factory) {
		Configuration configuration = this.properties.getConfiguration();
		if (configuration == null && !StringUtils.hasText(this.properties.getConfigLocation())) {
			configuration = new SmartConfiguration();
		}
		if (configuration != null && !CollectionUtils.isEmpty(this.configurationCustomizers)) {
			for (ConfigurationCustomizer customizer : this.configurationCustomizers) {
				customizer.customize(configuration);
			}
		}
		factory.setConfiguration(configuration);
	}

	@Bean
	@ConditionalOnBean(SqlSessionFactory.class)
	public MybatisSmartContext mybatisSmartContext(Map<String, SqlSessionFactory> sessionFactoryMap) {
		MybatisSmartContext mybatisSmartContext = new MybatisSmartContext();
		List<MappingConfig> mappingConfigList=parseToMappingConfigList(smartProperties, sessionFactoryMap);
		mybatisSmartContext.setMappingConfigList(mappingConfigList);
		return mybatisSmartContext;
	}
	
	
	public List<MappingConfig> parseToMappingConfigList(SmartProperties smartProperties,Map<String, SqlSessionFactory> sessionFactoryMap) {
		SqlSessionFactory sqlSessionFactory = null;
		Map<String, String> tablePackages = smartProperties.getTablePackages();
		if (tablePackages == null || tablePackages.isEmpty()) {
			return Collections.emptyList();
		}
		List<MappingConfig> configurations = new ArrayList<>();
		Map<String, String> tablePackageMap = new HashMap<>();
		tablePackages.forEach((tablePackagesStr, sqlSessionFactoryBeanName) -> {
			String key = StrTool.isEmpty(sqlSessionFactoryBeanName) ? null : sqlSessionFactoryBeanName;
			tablePackageMap.compute(key, (k, v) -> {
				return StrTool.isEmpty(v) ? tablePackagesStr.trim() : (v + "," + (tablePackagesStr.trim()));
			});
		});
		String sqlSessionFactoryBeanName = null;
		for (Entry<String, String> entry : tablePackageMap.entrySet()) {
			sqlSessionFactoryBeanName = entry.getKey();
			sqlSessionFactory=sessionFactoryMap.get(sqlSessionFactoryBeanName);
			Assert.notNull(sqlSessionFactory, "spring IOC 中不存的sqlSessionFactory：" + sqlSessionFactoryBeanName);
			MappingConfig conf = new MappingConfig(entry.getValue(), sqlSessionFactory);
			conf.setSqlSessionFactoryBeanName(sqlSessionFactoryBeanName);
			configurations.add(conf);
		}

		return configurations;
	}



	@Override
	public void afterPropertiesSet() {
		checkConfigFileExists();
	}

	private void checkConfigFileExists() {
		if (this.properties.isCheckConfigLocation() && StringUtils.hasText(this.properties.getConfigLocation())) {
			Resource resource = this.resourceLoader.getResource(this.properties.getConfigLocation());
			Assert.state(resource.exists(), "Cannot find config location: " + resource
					+ " (please add config file or check your Mybatis configuration)");
		}
	}
}
