package com.mingri.mybatissmart.barracks;

import com.mingri.langhuan.cabinet.tool.StrTool;

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
	
	public static DialectEnum  ofName(String name) {
		if(StrTool.isEmpty(name)) {
			return null;
		}
		for (DialectEnum dialect : DialectEnum.values()) {
			if(dialect.name.equals(name)) {
				return dialect;
			}
		}
		return null;
	}
	
}
