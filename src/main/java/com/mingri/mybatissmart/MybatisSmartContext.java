package com.mingri.mybatissmart;

import static org.springframework.util.StringUtils.tokenizeToStringArray;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;

import com.mingri.langhuan.cabinet.constant.FileSufx;
import com.mingri.langhuan.cabinet.tool.ClassTool;
import com.mingri.langhuan.cabinet.tool.FileTool;
import com.mingri.langhuan.cabinet.tool.StrTool;
import com.mingri.mybatissmart.annotation.ColumnInfo;
import com.mingri.mybatissmart.annotation.RefTable;
import com.mingri.mybatissmart.annotation.TableInfo;
import com.mingri.mybatissmart.barracks.DialectEnum;
import com.mingri.mybatissmart.barracks.Tool;
import com.mingri.mybatissmart.dbo.ColumnField;
import com.mingri.mybatissmart.dbo.TableClass;
import com.mingri.mybatissmart.mapper.InternalMapper;

public class MybatisSmartContext {

	private MybatisSmartContext() {
	}

	private static SqlSessionFactory sessionFactory;
	private static MybatisSmartProperties mybatisSmartProperties;

	private static final Map<Class<?>, TableClass> MAPPERS_INFO = new HashMap<>();

	private static final Logger LOGGER = LoggerFactory.getLogger(MybatisSmartContext.class);

	static void initConf(SqlSessionFactory sessionFactory, MybatisSmartProperties mybatisSmartProperties) {
		MybatisSmartContext.sessionFactory = sessionFactory;
		MybatisSmartContext.mybatisSmartProperties = mybatisSmartProperties;
	}

	public static TableClass getClassMapperInfo(Class<?> cl) throws SQLException {
		TableClass res = MAPPERS_INFO.get(cl);
		Class<?> tmpCl = cl;
		if (res != null) {
			return res;
		} else {
			cl = cl.getSuperclass();
			while (cl != null && cl != Object.class) {
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
							return loadClassMapperInfo(refTable.value());
						}
						return res;
					}
				}
				cl = cl.getSuperclass();
			}
			throw new MybatisSmartException(
					"Class:" + tmpCl.getCanonicalName() + " 没有配置或者没有关联注解:" + TableInfo.class.getSimpleName());
		}
	}

	/**
	 * 获取java字段名称和数据库字段名称
	 * 
	 * @param tableName 表名
	 * @return key:fieldName val:columnName
	 */
	private static Map<String, String> getColumnAndFieldName(String tableName) {
		List<String> columns = MybatisSmartContext.selectColumns(tableName);
		Map<String, String> columnsCamel = new HashMap<>();
		columns.forEach(column -> columnsCamel.put(StrTool.camel(column.toLowerCase()), column.toLowerCase()));
		return columnsCamel;
	}

	private static List<String> selectColumns(String tableName) {
		try (SqlSession session = MybatisSmartContext.sessionFactory.openSession()) {
			return session.selectList(InternalMapper.SELECTFIELDS_STATEMENT,
					tableName.replace("[", "").replace("]", ""));
		} catch (Exception e) {
			LOGGER.error("捕获到异常,打印日志：{}", e);
		}
		return Collections.emptyList();
	}

	private static final Object LOAD_LOCAL = new Object();

	/**
	 * 加载类的相关映射信息
	 * 
	 * @param cl
	 * @return
	 * @throws SQLException
	 */
	private static TableClass loadClassMapperInfo(Class<?> cl) throws SQLException {
		TableClass clmif = MAPPERS_INFO.get(cl);
		if (clmif != null) {
			return clmif;
		}
		synchronized (LOAD_LOCAL) {
			clmif = MAPPERS_INFO.get(cl);
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
			LinkedHashMap<String, ColumnField> fieldsMapperMap = new LinkedHashMap<>();

			TableClass res = new TableClass();
			res.setDialect(getDialect());
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
					ColumnField fieldMapperInfo = fieldsMapperMap.get(columnName);
					if (fieldMapperInfo == null) {
						fieldsMapperMap.put(columnName, new ColumnField(field, columnInfo));
					} else if (fieldMapperInfo.getField().getDeclaringClass()
							.isAssignableFrom(field.getDeclaringClass())) {// 子类字段覆盖父类字段
						fieldMapperInfo.setField(field);
					}
				} else {
					columnName = columnsCamel.get(field.getName());
					if (columnName != null) {
						ColumnField fieldMapperInfo = fieldsMapperMap.get(columnName);
						if (fieldMapperInfo == null) {
							fieldsMapperMap.put(columnName, new ColumnField(field, columnInfo));
						} else if (fieldMapperInfo.getField().getDeclaringClass()
								.isAssignableFrom(field.getDeclaringClass())) {// 子类字段覆盖父类字段
							fieldMapperInfo.setField(field);
						}
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

	/**
	 * 获取数据库方言
	 * 
	 * @return
	 * @throws SQLException
	 */
	private static DialectEnum getDialect() throws SQLException {
		if (!MAPPERS_INFO.isEmpty()) {
			return MAPPERS_INFO.entrySet().iterator().next().getValue().getDialect();
		}
		try (Connection conn = MybatisSmartContext.sessionFactory.openSession().getConnection()) {
			return Tool.getDialect(conn);
		}
	}

	/**
	 * 扫描数据模型
	 */
	public static void scanTableModel() throws ClassNotFoundException, SQLException {
		String mdpkg = mybatisSmartProperties.getModelPackage();
		LOGGER.info("==============================MybatisSmart开始扫描实体类,扫描的包{}", mdpkg);
		if (StrTool.isNotEmpty(mdpkg)) {
			String[] mdpkgArray = tokenizeToStringArray(mdpkg,
					ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			for (String packageName : mdpkgArray) {
				LOGGER.info("==============================MybatisSmart开始扫描包{}", packageName);
				List<String> classList = FileTool.scanClass(packageName, true);
				LOGGER.info("==============================MybatisSmart扫描到的类:{}", classList);
				for (String classFileName : classList) {
						try {
							Class<?> cl = classLoader.loadClass(classFileName.substring(0,
									classFileName.length() - FileSufx.CLAZZ.length()));
							MybatisSmartContext.loadClassMapperInfo(cl);
							LOGGER.info("MybatisSmart Scanned class: {} for modelPackage: {}", classFileName,
									packageName);
						} catch (ClassNotFoundException | SQLException e) {
							LOGGER.error("MybatisSmart 扫描modelPackage出错： {}", e);
							throw e;
						}
				}

			}
		}
	}


	public static Set<String> getColumns(Class<?> cl) {
		TableClass res = null;
		try {
			res = getClassMapperInfo(cl);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (res != null) {
			return res.getFieldsMapperMap().keySet();
		}
		return Collections.emptySet();
	}

	public static String getTable(Class<?> cl) {
		TableClass res = null;
		try {
			res = getClassMapperInfo(cl);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (res != null) {
			return res.getTableInfo().value();
		}
		return null;
	}

}