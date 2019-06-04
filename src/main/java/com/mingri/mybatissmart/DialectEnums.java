package com.mingri.mybatissmart;

public enum DialectEnums {

	MYSQL("mysql"),
	SQLSERVER("sqlserver");
	public final String name;
	private DialectEnums(String name) {
		this.name=name;
	}
}
