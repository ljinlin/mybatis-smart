package com.ws.mybatissmart;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

public class Tool {
	/**
	 * 获取url
	 *
	 * @param dataSource
	 * @return
	 */
	private static String getUrl(Connection conn) {
		try {
			return conn.getMetaData().getURL();
		} catch (SQLException e) {
			throw new MybatisSmartException(e.getMessage());
		}
	}

	static DialectEnums getDialect(Connection conn) {
		String url=getUrl(conn);
		DialectEnums[] enums = DialectEnums.values();
		for (DialectEnums dialectEnum : enums) {
			if (url.indexOf(dialectEnum.name) != -1) {
				return dialectEnum;
			}
		}
		throw new MybatisSmartException("请设置数据库方言");
	}


}
