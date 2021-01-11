package com.mingri.mybatissmart.barracks;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.binding.MapperMethod.ParamMap;

import com.mingri.mybatissmart.MybatisSmartException;
import com.mingri.mybatissmart.annotation.SmartTable;

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

	public static Class<?> getClassByParam(Object parameter) {
		if (parameter instanceof ParamMap) {
			Object enParam = ((ParamMap<?>) parameter).get(Constant.PARAM_KEY);
			if (enParam == null) {
				return null;
			}
			return enParam instanceof List ? (((List<?>) enParam).get(0).getClass()) : enParam.getClass();
		}
		return null;
	}

	public static String getTableName(SmartTable tableInfo) {
		String tableName=tableInfo.value();
		return tableName.isEmpty()?tableInfo.name():tableName;
		
	}
	
}
