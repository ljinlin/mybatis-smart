package com.mingri.mybatissmart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import com.mingri.langhuan.cabinet.tool.StrTool;

@Configuration
@EnableConfigurationProperties(value = { MybatisSmartProperties.class })
@ConditionalOnClass({ SqlSessionFactory.class, SqlSessionFactoryBean.class, MybatisAutoConfiguration.class })
@ConditionalOnBean({ DataSource.class, SqlSessionFactory.class })
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class MybatisSmartAutoConfiguration {

	@Autowired
	private Map<String, SqlSessionFactory> sessionFactoryMap;
	@Autowired
	private SqlSessionFactory dftSqlSessionFactory;
	@Autowired
	private MybatisSmartProperties mybatisSmartProperties;

	@Bean
	public MybatisSmartContext mybatisSmartContext(MybatisSmartProperties mybatisSmartProperties) {
		MybatisSmartContext mybatisSmartContext = new MybatisSmartContext();
		mybatisSmartContext.setConfigurations(buildConfigurations());
		return mybatisSmartContext;
	}

	private List<MybatisSmartConfiguration> buildConfigurations() {
		SqlSessionFactory sqlSessionFactory = null;
		Map<String, String> tablePackages = mybatisSmartProperties.getTablePackages();
		if(tablePackages==null||tablePackages.isEmpty()) {
			return Collections.emptyList();
		}
		List<MybatisSmartConfiguration> configurations = new ArrayList<>();
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
			sqlSessionFactory = StrTool.isEmpty(sqlSessionFactoryBeanName) ? dftSqlSessionFactory
					: sessionFactoryMap.get(sqlSessionFactoryBeanName);
			Assert.notNull(sqlSessionFactory, "springContext中不存的sqlSessionFactoryBean：" + sqlSessionFactoryBeanName);
			configurations.add(new MybatisSmartConfiguration(entry.getValue(), sqlSessionFactory, null,
					sqlSessionFactoryBeanName));
		}

		return configurations;
	}

}
