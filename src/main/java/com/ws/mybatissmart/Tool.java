package com.ws.mybatissmart;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
