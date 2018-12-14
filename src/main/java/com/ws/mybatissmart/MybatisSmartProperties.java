package com.ws.mybatissmart;

import javax.annotation.PostConstruct;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = MybatisSmartProperties.MYBATIS_SMART_PREFIX)
public class MybatisSmartProperties {
	public static final String MYBATIS_SMART_PREFIX = "mybatis-smart";
//	select * from information_schema.COLUMNS where TABLE_SCHEMA = (select database()) and TABLE_NAME=#{tableName}
	
	private String modelPackage;
	


	public String getModelPackage() {
		return modelPackage;
	}

	public void setModelPackage(String modelPackage) {
		this.modelPackage = modelPackage;
	}

	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	public static SqlSessionFactory staticSqlSessionFactory;
	public static MybatisSmartContext mybatisSmartContext;

	@PostConstruct
	private void init() {
		MybatisSmartProperties.mybatisSmartContext = new MybatisSmartContext();
		MybatisSmartProperties.staticSqlSessionFactory = sqlSessionFactory;
	}

}