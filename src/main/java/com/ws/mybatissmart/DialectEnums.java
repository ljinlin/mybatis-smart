package com.ws.mybatissmart;

public enum DialectEnums {

	MYSQL("mysql"),
	SQLSERVER("sqlserver");
	private DialectEnums(String name) {
		this.name=name;
	}
	public final String name;
}
