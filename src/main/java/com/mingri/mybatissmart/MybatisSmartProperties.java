package com.mingri.mybatissmart;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = MybatisSmartProperties.MYBATIS_SMART_PREFIX)
public class MybatisSmartProperties {
	 static final String MYBATIS_SMART_PREFIX = "mybatis-smart";

	private String modelPackage;

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