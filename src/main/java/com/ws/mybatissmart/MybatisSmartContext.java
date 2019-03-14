package com.ws.mybatissmart;

import static org.springframework.util.StringUtils.hasLength;
import static org.springframework.util.StringUtils.tokenizeToStringArray;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.io.VFS;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;

import com.ws.commons.tool.ClassTool;
import com.ws.commons.tool.StrTool;
import com.ws.mybatissmart.annotation.ColumnInfo;
import com.ws.mybatissmart.annotation.RefTable;
import com.ws.mybatissmart.annotation.TableInfo;

public class MybatisSmartContext {

	private static SqlSessionFactory sessionFactory;
	private static MybatisSmartProperties mybatisSmartProperties;

	public static final Map<Class<?>, ClassMapperInfo> MAPPERS_INFO = new HashMap<Class<?>, ClassMapperInfo>();

	private static final Logger LOGGER = LoggerFactory.getLogger(MybatisSmartContext.class);

	static void initConf(SqlSessionFactory sessionFactory, MybatisSmartProperties mybatisSmartProperties) {
		MybatisSmartContext.sessionFactory = sessionFactory;
		MybatisSmartContext.mybatisSmartProperties = mybatisSmartProperties;

	}

	static ClassMapperInfo getClassMapperInfo(Class<?> cl) {
		ClassMapperInfo res = MAPPERS_INFO.get(cl);
		if (res != null) {
			return res;
		} else {
			cl = cl.getSuperclass();
			while (cl != null && cl != Object.class && res == null) {
				if (cl.getAnnotation(TableInfo.class) != null) {
					res = MAPPERS_INFO.get(cl.getSuperclass());
					if (res == null) {
						return loadClassMapperInfo(cl);
					}
					return res;
				} else {
					RefTable refTable = cl.getAnnotation(RefTable.class);
					if (refTable != null) {
						res = MAPPERS_INFO.get(refTable.value());
						if (res == null) {
							return loadClassMapperInfo(cl);
						}
						return res;
					}
				}
				cl = cl.getSuperclass();
			}
			throw new MybatisSmartException(
					"Class:" + cl.getCanonicalName() + " 没有配置或者没有关联注解:" + TableInfo.class.getSimpleName());
		}
	}

	/**
	 * 
	 * @param tableName
	 * @return key:fieldName val:columnName
	 */
	private static Map<String, String> getColumnAndFieldName(String tableName) {
		List<String> columns = MybatisSmartContext.selectColumns(tableName);
		Map<String, String> columnsCamel = new HashMap<>();
		columns.forEach(column -> {
			columnsCamel.put(StrTool.camel(column.toLowerCase()), column.toLowerCase());
		});
		return columnsCamel;
	}

	private static List<String> selectColumns(String tableName) {
		SqlSession session = MybatisSmartContext.sessionFactory.openSession();
		List<String> columns = session.selectList(SelfMapper.SELECTFIELDS_STATEMENT,
				tableName.replace("[", "").replace("]", ""));
		session.close();
		return columns;
	}

	/**
	 * 加载类的相关映射信息
	 * 
	 * @param cl
	 * @return
	 */
	private synchronized static ClassMapperInfo loadClassMapperInfo(Class<?> cl) {
		ClassMapperInfo clmif = MAPPERS_INFO.get(cl);
		if (clmif != null) {
			return clmif;
		}
		TableInfo tableInfo = cl.getAnnotation(TableInfo.class);
		if (tableInfo == null) {
			return null;
		}
		String idFieldName = tableInfo.idFieldName();
		if (tableInfo.value().length() == 0) {
			throw new MybatisSmartException(TableInfo.class.getCanonicalName() + " 的value 不能为空");
		}
		if (idFieldName.length() == 0) {
			throw new MybatisSmartException(TableInfo.class.getCanonicalName() + " 的idFieldName value 不能为空");
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
					throw new MybatisSmartException("table:" + tableInfo.value() + " 没有该column:" + columnName);
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

	private static final int CLASSFILE_SUFX_LEN = ".class".length();

	/**
	 * 扫描数据模型
	 * 
	 * @throws IOException
	 */
	public static void scanDataModel() throws IOException {
		String mdpkg = mybatisSmartProperties.getModelPackage();
		if (hasLength(mdpkg)) {
			String[] typeAliasPackageArray = tokenizeToStringArray(mdpkg,
					ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			for (String packageToScan : typeAliasPackageArray) {
				VFS.getInstance().list(packageToScan.replace('.', '/')).forEach(e -> {
					if (e.endsWith(".class")) {
						try {
							Class<?> cl = classLoader
									.loadClass(e.replace('/', '.').substring(0, e.length() - CLASSFILE_SUFX_LEN));
							ClassMapperInfo clm = MybatisSmartContext.loadClassMapperInfo(cl);
							if (clm != null) {
								LOGGER.info("MybatisSmart Scanned class: '" + e + "' for modelPackage");
							}
						} catch (ClassNotFoundException e1) {
							e1.printStackTrace();
						}
					}

				});

			}
		}
	}

	/**
	 * 获取url
	 *
	 * @param dataSource
	 * @return
	 */
	private static String getUrl(DataSource dataSource) {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return conn.getMetaData().getURL();
		} catch (SQLException e) {
			throw new MybatisSmartException(e.getMessage());
		} finally {
			if (conn != null) {
				try {
					// if (closeConn) {
					conn.close();
					// }
				} catch (SQLException e) {
					// ignore
				}
			}
		}
	}

	static String getDialect(DataSource dataSource) {
		String url = getUrl(dataSource);
		DialectEnums[] enums = DialectEnums.values();
		for (DialectEnums dialectEnum : enums) {
			if (url.indexOf(dialectEnum.name) != -1) {
				return dialectEnum.name;
			}
		}
		throw new MybatisSmartException("请设置数据库方言");
	}

}