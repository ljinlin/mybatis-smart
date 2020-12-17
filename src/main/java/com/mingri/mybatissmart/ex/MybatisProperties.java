package com.mingri.mybatissmart.ex;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.mingri.mybatissmart.config.SmartProperties;

/**
 * Configuration properties for MyBatis.
 *
 * @author Eddú Meléndez
 * @author Kazuki Shimizu
 */
@ConfigurationProperties(prefix = MybatisProperties.MYBATIS_PREFIX)
public class MybatisProperties {

	public static final String MYBATIS_PREFIX = "mybatis";

	private static final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

	/**
	 * Location of MyBatis xml config file.
	 */
	private String configLocation;

	/**
	 * Locations of MyBatis mapper files.
	 */
	private String[] mapperLocations;

	/**
	 * Packages to search type aliases. (Package delimiters are ",; \t\n")
	 */
	private String typeAliasesPackage;

	/**
	 * The super class for filtering type alias. If this not specifies, the MyBatis
	 * deal as type alias all classes that searched from typeAliasesPackage.
	 */
	private Class<?> typeAliasesSuperType;

	/**
	 * Packages to search for type handlers. (Package delimiters are ",; \t\n")
	 */
	private String typeHandlersPackage;

	/**
	 * Indicates whether perform presence check of the MyBatis xml config file.
	 */
	private boolean checkConfigLocation = false;

	/**
	 * Execution mode for {@link org.mybatis.spring.SqlSessionTemplate}.
	 */
	private ExecutorType executorType;

	/**
	 * The default scripting language driver class. (Available when use together
	 * with mybatis-spring 2.0.2+)
	 */
	private Class<? extends LanguageDriver> defaultScriptingLanguageDriver;

	/**
	 * Externalized properties for MyBatis configuration.
	 */
	private Properties configurationProperties;
	
	/**
	 * Externalized properties for MyBatis configuration.
	 */
	private SmartProperties smart;



	/**
	 * A Configuration object for customize default settings. If
	 * {@link #configLocation} is specified, this property is not used.
	 */
	@NestedConfigurationProperty
	private SmartConfiguration configuration;

	/**
	 * @since 1.1.0
	 */
	public String getConfigLocation() {
		return this.configLocation;
	}

	/**
	 * @since 1.1.0
	 */
	public void setConfigLocation(String configLocation) {
		this.configLocation = configLocation;
	}

	public String[] getMapperLocations() {
		return this.mapperLocations;
	}

	public void setMapperLocations(String[] mapperLocations) {
		this.mapperLocations = mapperLocations;
	}

	public String getTypeHandlersPackage() {
		return this.typeHandlersPackage;
	}

	public void setTypeHandlersPackage(String typeHandlersPackage) {
		this.typeHandlersPackage = typeHandlersPackage;
	}

	public String getTypeAliasesPackage() {
		return this.typeAliasesPackage;
	}

	public void setTypeAliasesPackage(String typeAliasesPackage) {
		this.typeAliasesPackage = typeAliasesPackage;
	}

	/**
	 * @since 1.3.3
	 */
	public Class<?> getTypeAliasesSuperType() {
		return typeAliasesSuperType;
	}

	/**
	 * @since 1.3.3
	 */
	public void setTypeAliasesSuperType(Class<?> typeAliasesSuperType) {
		this.typeAliasesSuperType = typeAliasesSuperType;
	}

	public boolean isCheckConfigLocation() {
		return this.checkConfigLocation;
	}

	public void setCheckConfigLocation(boolean checkConfigLocation) {
		this.checkConfigLocation = checkConfigLocation;
	}

	public ExecutorType getExecutorType() {
		return this.executorType;
	}

	public void setExecutorType(ExecutorType executorType) {
		this.executorType = executorType;
	}

	/**
	 * @since 2.1.0
	 */
	public Class<? extends LanguageDriver> getDefaultScriptingLanguageDriver() {
		return defaultScriptingLanguageDriver;
	}

	/**
	 * @since 2.1.0
	 */
	public void setDefaultScriptingLanguageDriver(Class<? extends LanguageDriver> defaultScriptingLanguageDriver) {
		this.defaultScriptingLanguageDriver = defaultScriptingLanguageDriver;
	}

	/**
	 * @since 1.2.0
	 */
	public Properties getConfigurationProperties() {
		return configurationProperties;
	}

	/**
	 * @since 1.2.0
	 */
	public void setConfigurationProperties(Properties configurationProperties) {
		this.configurationProperties = configurationProperties;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(SmartConfiguration configuration) {
		this.configuration = configuration;
	}

	public Resource[] resolveMapperLocations() {
		return Stream.of(Optional.ofNullable(this.mapperLocations).orElse(new String[0]))
				.flatMap(location -> Stream.of(getResources(location))).toArray(Resource[]::new);
	}

	private Resource[] getResources(String location) {
		try {
			return resourceResolver.getResources(location);
		} catch (IOException e) {
			return new Resource[0];
		}
	}

	public SmartProperties getSmart() {
		return smart;
	}

	public void setSmart(SmartProperties smart) {
		this.smart = smart;
	}

}
