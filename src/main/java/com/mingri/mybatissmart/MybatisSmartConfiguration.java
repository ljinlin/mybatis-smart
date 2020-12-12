package com.mingri.mybatissmart;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.mingri.langhuan.cabinet.tool.StrTool;

/**
 * 一个 SqlSessionFactory一个Configuration
 * 
 * @author ljl
 * @date 2020-11-12 Dscription:
 *
 */
public class MybatisSmartConfiguration {

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
	public MybatisSmartConfiguration(String tablePackages, SqlSessionFactory sqlSessionFactory) {
		super();
		this.tablePackages = tablePackages;
		this.sqlSessionFactory = sqlSessionFactory;
	}

	@Autowired(required = false)
	private Map<String, SqlSessionFactory> sessionFactoryMap;

	@PostConstruct
	public void afterInit() {
		if(StrTool.isNotEmpty(sqlSessionFactoryBeanName)){
			return;
		}
		Assert.notNull(sessionFactoryMap, "在spring IOC 中没有找到sqlSessionFactory");
		
		for (Map.Entry<String, SqlSessionFactory> en : sessionFactoryMap.entrySet()) {
			if (en.getValue() == this.sqlSessionFactory) {
				this.sqlSessionFactoryBeanName = en.getKey();
				return;
			}
		}
		Assert.hasText(this.sqlSessionFactoryBeanName, "sqlSessionFactory 没有注入sping IOC");
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
