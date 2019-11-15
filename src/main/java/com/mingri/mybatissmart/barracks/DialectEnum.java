package com.mingri.mybatissmart.barracks;

/**
 * 数据库方言枚举
 * @author ljl
 *
 */
public enum DialectEnum {

	MYSQL("mysql"),
	SQLSERVER("sqlserver");
	public final String name;
	private DialectEnum(String name) {
		this.name=name;
	}
}
