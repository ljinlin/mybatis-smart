package com.mingri.mybatissmart;

import static org.springframework.util.StringUtils.hasLength;
import static org.springframework.util.StringUtils.tokenizeToStringArray;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
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

import com.mingri.langhuan.cabinet.tool.ClassTool;
import com.mingri.langhuan.cabinet.tool.StrTool;
import com.mingri.mybatissmart.annotation.ColumnInfo;
import com.mingri.mybatissmart.annotation.RefTable;
import com.mingri.mybatissmart.annotation.TableInfo;

public class MybatisSmartContext {

	private MybatisSmartContext() {
	}

	private static SqlSessionFactory sessionFactory;
	private static MybatisSmartProperties mybatisSmartProperties;

	private static final Map<Class<?>, ClassMapperInfo> MAPPERS_INFO = new HashMap<>();

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
	 * 
	 * @param tableName
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
			return session.selectList(SelfMapper.SELECTFIELDS_STATEMENT, tableName.replace("[", "").replace("]", ""));
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
			Map<String, String> columnsCamel = getColumnAndFieldName(tableInfo.value());

			List<Field> flist = ClassTool.getDecararedFields(cl, false);
			LinkedHashMap<String, FieldMapperInfo> fieldsMapperMap = new LinkedHashMap<>();

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

	private static final String CLASSFILE_SUFX = ".class";

	private static DialectEnums getDialect() {
		if (MAPPERS_INFO.size() > 0) {
			return MAPPERS_INFO.entrySet().iterator().next().getValue().getDialect();
		}
		SqlSession session = null;
		Connection conn = null;
		try {
			session = MybatisSmartContext.sessionFactory.openSession();
			conn = session.getConnection();
			return Tool.getDialect(conn);
		} finally {
			if (conn != null) {
				session.close();
			}
			if (session != null) {
				session.close();
			}
		}
	}

	/**
	 * 扫描数据模型
	 * 
	 * @throws IOException
	 */
	public static void scanDataModel() {
		String mdpkg = mybatisSmartProperties.getModelPackage();
		LOGGER.info("==============================MybatisSmart开始扫描实体类,扫描的包{}", mdpkg);
		if (hasLength(mdpkg)) {
			String[] typeAliasPackageArray = tokenizeToStringArray(mdpkg,
					ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			for (String packageToScan : typeAliasPackageArray) {
				LOGGER.info("==============================MybatisSmart开始扫描包{}", packageToScan);
				List<String> classList = scanClass(packageToScan, true);
				LOGGER.info("==============================MybatisSmart扫描到的类******{}", classList);
				classList.forEach(e -> {
					if (e.endsWith(CLASSFILE_SUFX)) {
						try {
							Class<?> cl = classLoader
									.loadClass(e.replace('/', '.').substring(0, e.length() - CLASSFILE_SUFX.length()));
							MybatisSmartContext.loadClassMapperInfo(cl);
							LOGGER.info("MybatisSmart Scanned class: {} for modelPackage", e);
						} catch (ClassNotFoundException e1) {
							LOGGER.error("MybatisSmart 扫描类出错:：{}", e1);
						}
					}

				});

			}
		}
	}

	/**
	 * 从包package中获取所有的Class
	 * 
	 * @param pack
	 * @param recursive 是否循环迭代
	 * @return
	 */
	private static List<String> scanClass(String packageName, boolean recursive) {

		// 第一个class类的集合
		List<String> classes = new ArrayList<>();
		// 获取包的名字 并进行替换
		String packageDirName = packageName.replace('.', '/');
		// 定义一个枚举的集合 并进行循环来处理这个目录下的things
		Enumeration<URL> dirs;
		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			// 循环迭代下去
			while (dirs.hasMoreElements()) {
				// 获取下一个元素
				URL url = dirs.nextElement();
				// 得到协议的名称
				String protocol = url.getProtocol();
				// 如果是以文件的形式保存在服务器上
				if ("file".equals(protocol)) {
					// 获取包的物理路径
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					// 以文件的方式扫描整个包下的文件 并添加到集合中
					findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
				} else if ("jar".equals(protocol)) {
					// 如果是jar包文件
					// 获取jar
					try (JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();) {
						// 从此jar包 得到一个枚举类
						Enumeration<JarEntry> entries = jar.entries();
						// 同样的进行循环迭代
						while (entries.hasMoreElements()) {
							// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
							JarEntry entry = entries.nextElement();
							String name = entry.getName();
							// 如果是以/开头的
							if (name.charAt(0) == '/') {
								// 获取后面的字符串
								name = name.substring(1);
							}
							// 如果前半部分和定义的包名相同
							if (name.startsWith(packageDirName)) {
								int idx = name.lastIndexOf('/');
								// 如果以"/"结尾 是一个包
								if (idx != -1) {
									// 获取包名 把"/"替换成"."
									packageName = name.substring(0, idx).replace('/', '.');
								}
								// 如果可以迭代下去 并且是一个包 如果是一个.class文件 而且不是目录
								if (((idx != -1) || recursive)
										&& (name.endsWith(CLASSFILE_SUFX) && !entry.isDirectory())) {
									classes.add(name.replace('/', '.'));
								}
							}
						}
					}
				}
			}
		} catch (IOException e) {
			LOGGER.error("捕获到异常,打印日志：{}", e);
		}

		return classes;
	}

	/**
	 * 以文件的形式来获取包下的所有Class
	 * 
	 * @param packageName
	 * @param packagePath
	 * @param recursive
	 * @param classes
	 */
	private static void findAndAddClassesInPackageByFile(String packageName, String packagePath,
			final boolean recursive, List<String> classes) {
		// 获取此包的目录 建立一个File
		File dir = new File(packagePath);
		// 如果不存在或者 也不是目录就直接返回
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		// 如果存在 就获取包下的所有文件 包括目录
		// 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
		File[] dirfiles = dir
				.listFiles(file -> (recursive && file.isDirectory()) || (file.getName().endsWith(CLASSFILE_SUFX)));
		// 循环所有文件
		for (File file : dirfiles) {
			// 如果是目录 则继续扫描
			if (file.isDirectory()) {
				findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive,
						classes);
			} else {
				// 如果是java类文件 去掉后面的.class 只留下类名
				classes.add(packageName + '.' + file.getName());
			}
		}
	}

	public static Set<String> getColumns(Class<?> cl) {
		ClassMapperInfo res = getClassMapperInfo(cl);
		if (res != null) {
			return res.getFieldsMapperMap().keySet();
		}
		return Collections.emptySet();
	}

	public static String getTable(Class<?> cl) {
		ClassMapperInfo res = getClassMapperInfo(cl);
		if (res != null) {
			return res.getTableInfo().value();
		}
		return null;
	}

}