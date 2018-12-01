package com.ws.mybatissmart;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.ws.commons.tool.ClassTool;
import com.ws.commons.tool.StrTool;
import com.ws.mybatissmart.annotation.ColumnInfo;
import com.ws.mybatissmart.annotation.TableInfo;

public class MybatisXContext {

	private static SqlSessionFactory sessionFactory;

	static void initConf(SqlSessionFactory sessionFactory) {
		MybatisXContext.sessionFactory = sessionFactory;
	}

	public static final Map<Class<?>, ClassMapperInfo> MAPPERS_INFO = new HashMap<Class<?>, ClassMapperInfo>();

	static ClassMapperInfo getClassMapperInfo(Class<?> cl) {
		ClassMapperInfo res = MAPPERS_INFO.get(cl);
		if (res != null) {
			return res;
		}
		try {
			res = loadClassMapperInfo(cl);
			if (res == null) {
				throw new MybatisXException("Class:" + cl.getCanonicalName() + " 没有MybatisX的配置");
			}
			return res;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * 
	 * @param tableName
	 * @return key:fieldName val:columnName
	 */
	private static Map<String, String> getColumnAndFieldName(String tableName) {
		SqlSession session = MybatisXContext.sessionFactory.openSession();
		List<String> columns = session.selectList(SmartMapper.SELECTFIELDS_STATEMENT,
				tableName.replace("[", "").replace("]", ""));
		session.close();
		Map<String, String> columnsCamel = new HashMap<>();
		columns.forEach(column -> {
			columnsCamel.put(StrTool.camel(column.toLowerCase()), column.toLowerCase());
		});
		return columnsCamel;
	}

	private static ClassMapperInfo loadClassMapperInfo(Class<?> cl) {

		TableInfo tableInfo = cl.getAnnotation(TableInfo.class);
		String idFieldName = tableInfo.idFieldName();
		if (tableInfo == null) {
			return null;
		}
		if (tableInfo.value().length() == 0) {
			throw new MybatisXException(TableInfo.class.getCanonicalName() + " 的value 不能为空");
		}
		if (idFieldName.length() == 0) {
			throw new MybatisXException(TableInfo.class.getCanonicalName() + " 的idFieldName value 不能为空");
		}
		Map<String, String> columnsCamel = getColumnAndFieldName(tableInfo.value());
		List<Field> flist = ClassTool.getDecararedFields(cl, false);
		LinkedHashMap<String, FieldMapperInfo> fieldsMapperMap = new LinkedHashMap<String, FieldMapperInfo>();

		ClassMapperInfo res = new ClassMapperInfo();
		flist.forEach(field -> {
			ColumnInfo columnInfo = field.getAnnotation(ColumnInfo.class);
			String columnName = "";
			if (columnInfo != null) {
				columnName = columnInfo.value().toLowerCase();
			}
			if (columnName.length() > 0) {
				if (!columnsCamel.containsValue(columnName)) {
					throw new MybatisXException("table:" + tableInfo.value() + " 没有该column:" + columnName);
				}
				fieldsMapperMap.put(columnName, new FieldMapperInfo(field, columnInfo));
			} else {
				columnName = columnsCamel.get(field.getName());
				if (columnName != null) {
					fieldsMapperMap.put(columnName, new FieldMapperInfo(field, columnInfo));
				}
			}
			if (field.getName().equals(idFieldName)) {
				res.setIdField(field);
				res.setIdColumnName(columnName);
			}
		});
		res.setClazz(cl);
		res.setFieldsMapperMap(fieldsMapperMap);
		res.setTableInfo(tableInfo);
		MAPPERS_INFO.put(cl, res);
		return res;
	}
}