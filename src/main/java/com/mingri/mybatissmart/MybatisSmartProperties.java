package com.mingri.mybatissmart;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 配置属性类
 * 
 * @author ljl 2019年11月30日
 */
@ConfigurationProperties(prefix = MybatisSmartProperties.MYBATIS_SMART_PREFIX)
public class MybatisSmartProperties {
	static final String MYBATIS_SMART_PREFIX = "mybatis-smart";

	/**
	 * 表映射实体扫描包
	 */
	private String modelPackage;

	/**
	 * 数据库方言,目前支持：mysql,sqlserver
	 */
	private String dialect;

	public String getModelPackage() {
		return modelPackage;
	}

	public void setModelPackage(String modelPackage) {
		this.modelPackage = modelPackage;
	}

	public String getDialect() {
		return dialect;
	}

	public void setDialect(String dialect) {
		this.dialect = dialect;
	}

}