package com.mingri.mybatissmart;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.mingri.mybatissmart.barracks.SqlPrint;

/**
 * 配置属性类
 * 
 * @author ljl 2019年11月30日
 */
@ConfigurationProperties(prefix = MybatisSmartProperties.MYBATIS_SMART_PREFIX)
public class MybatisSmartProperties {
	static final String MYBATIS_SMART_PREFIX = "mybatis-smart";


	/**
	 * 表映射实体扫描包:key:包，value:sessionFactory的beanName
	 */
	private Map<String, String> tablePackages;
	private SqlPrint sqlPrint;
	
	public Map<String, String> getTablePackages() {
		return tablePackages;
	}

	public void setTablePackages(Map<String, String> tablePackages) {
		this.tablePackages = tablePackages;
	}

	public SqlPrint getSqlPrint() {
		return sqlPrint;
	}

	public void setSqlPrint(SqlPrint sqlPrint) {
		this.sqlPrint = sqlPrint;
	}





}