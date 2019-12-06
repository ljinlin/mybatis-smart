package com.mingri.mybatissmart;

import org.apache.ibatis.session.SqlSessionFactory;

public class MybatisSmartConfiguration {

	private String tablePackages;
	private SqlSessionFactory sqlSessionFactory;
	private String sqlSessionFactoryBeanName;
	private String dialect;
	
	public MybatisSmartConfiguration(String tablePackages, SqlSessionFactory sqlSessionFactory, String dialect) {
		super();
		this.tablePackages = tablePackages;
		this.sqlSessionFactory = sqlSessionFactory;
		this.dialect = dialect;
	}
	
	public MybatisSmartConfiguration(String tablePackages, SqlSessionFactory sqlSessionFactory, String dialect,String sqlSessionFactoryBeanName) {
		super();
		this.tablePackages = tablePackages;
		this.sqlSessionFactory = sqlSessionFactory;
		this.sqlSessionFactoryBeanName = sqlSessionFactoryBeanName;
		this.dialect = dialect;
	}


	public MybatisSmartConfiguration() {
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


	public void setSqlSessionFactoryBeanName(String sqlSessionFactoryBeanName) {
		this.sqlSessionFactoryBeanName = sqlSessionFactoryBeanName;
	}
}
