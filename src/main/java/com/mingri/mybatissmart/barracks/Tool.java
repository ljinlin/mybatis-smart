package com.mingri.mybatissmart.barracks;

import java.sql.Connection;
import java.sql.SQLException;

import com.mingri.mybatissmart.MybatisSmartException;

public class Tool {

	private Tool() {
	}

	/**
	 * 根据连接对象获取数据库url
	 *
	 * @param conn 连接对象
	 * @return 返回url字符串
	 */
	private static String getUrl(Connection conn) {
		try {
			return conn.getMetaData().getURL();
		} catch (SQLException e) {
			throw new MybatisSmartException(e.getMessage());
		}
	}

	/**
	 * 根据连接对象获取数据库方言
	 * 
	 * @param conn 连接对象
	 * @return 返回方言枚举
	 */
	public static DialectEnum getDialect(Connection conn) {
		String url = getUrl(conn);
		DialectEnum[] enums = DialectEnum.values();
		for (DialectEnum dialectEnum : enums) {
			if (url.indexOf(dialectEnum.name) != -1) {
				return dialectEnum;
			}
		}
		throw new MybatisSmartException("MybatisSmart无法解析出数据库方言，请配置数据库方言");
	}

	
	public static String unifiedColumnName(String columnName) {
		return columnName.toLowerCase();
	}
	
	
}
