package com.ws.mybatissmart;

import static org.springframework.util.StringUtils.hasLength;
import static org.springframework.util.StringUtils.tokenizeToStringArray;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.ibatis.io.VFS;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.server.Session;
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
		Class<?> tmpCl = cl;
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
		List<String> columns=null;
		try(SqlSession session=MybatisSmartContext.sessionFactory.openSession()) {
			 columns = session.selectList(SelfMapper.SELECTFIELDS_STATEMENT,
					tableName.replace("[", "").replace("]", ""));
		} 
		return columns;
	}

	private static final Object LOAD_LOCAL = new Object();

	/**
	 * 加载类的相关映射信息
	 * 
	 * @param cl
	 * @return
	 */
	private static ClassMapperInfo loadClassMapperInfo(Class<?> cl) {
		ClassMapperInfo clmif = MAPPERS_INFO.get(cl);
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
			Map<String, String> columnsCamel=getColumnAndFieldName(tableInfo.value());  
			
			
			List<Field> flist = ClassTool.getDecararedFields(cl, false);
			LinkedHashMap<String, FieldMapperInfo> fieldsMapperMap = new LinkedHashMap<String, FieldMapperInfo>();

			ClassMapperInfo res = new ClassMapperInfo();
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
					FieldMapperInfo fieldMapperInfo = fieldsMapperMap.get(columnName);
					if (fieldMapperInfo == null) {
						fieldsMapperMap.put(columnName, new FieldMapperInfo(field, columnInfo));
					} else if (fieldMapperInfo.getField().getDeclaringClass()
							.isAssignableFrom(field.getDeclaringClass())) {// 子类字段覆盖父类字段
						fieldMapperInfo.setField(field);
					}  
				} else {
					columnName = columnsCamel.get(field.getName());
					if (columnName != null) {
						FieldMapperInfo fieldMapperInfo = fieldsMapperMap.get(columnName);
						if (fieldMapperInfo == null) {
							fieldsMapperMap.put(columnName, new FieldMapperInfo(field, columnInfo));
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

	private static final int CLASSFILE_SUFX_LEN = ".class".length();

	private static DialectEnums getDialect() {
		if(MAPPERS_INFO.size()>0) {
			return MAPPERS_INFO.entrySet().iterator().next().getValue().getDialect();
		}
		SqlSession session=null;
		Connection conn=null;
		try {
			session = MybatisSmartContext.sessionFactory.openSession();
			conn=session.getConnection();
			return Tool.getDialect(conn);
		} finally {
			if(conn!=null) {
				session.close();
			}
			if(session!=null) {
				session.close();
			}
		}
	}
	
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



	public static Set<String> getColumns(Class<?> cl) {
		ClassMapperInfo res = getClassMapperInfo(cl);
		if (res != null) {
			return res.getFieldsMapperMap().keySet();
		}
		return null;
	}

	public static String getTable(Class<?> cl) {
		ClassMapperInfo res = getClassMapperInfo(cl);
		if (res != null) {
			return res.getTableInfo().value();
		}
		return null;
	}

}