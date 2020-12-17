package com.mingri.mybatissmart.config;

import org.apache.ibatis.session.SqlSessionFactory;

/**
 * 一个 SqlSessionFactory一个Configuration
 * 
 * @author ljl
 * @date 2020-11-12 Dscription:
 *
 */
public class MappingConfig {

	private String tablePackages;
	private SqlSessionFactory sqlSessionFactory;
	private String sqlSessionFactoryBeanName;
	private String dialect;

	/**
	 * 
	 * @param tablePackages             要扫描的实体类对应的包
	 * @param sqlSessionFactory
	 * @param sqlSessionFactoryBeanName sqlSessionFactory对应的spirng IOC
	 *                                  bean名称（一个sqlSessionFactory默认名称就是"sqlSessionFactory"）
	 */
	public MappingConfig(String tablePackages, SqlSessionFactory sqlSessionFactory) {
		super();
		this.tablePackages = tablePackages;
		this.sqlSessionFactory = sqlSessionFactory;
	}



	public MappingConfig() {
	}

	public String getTablePackages() {
		return tablePackages;
	}

	public void setTablePackages(String tablePackages) {
		this.tablePackages = tablePackages;
	}

	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public String getDialect() {
		return dialect;
	}

	public void setDialect(String dialect) {
		this.dialect = dialect;
	}

	public String getSqlSessionFactoryBeanName() {
		return sqlSessionFactoryBeanName;
	}

	 void setSqlSessionFactoryBeanName(String sqlSessionFactoryBeanName) {
		this.sqlSessionFactoryBeanName = sqlSessionFactoryBeanName;
	}
	
	
}
