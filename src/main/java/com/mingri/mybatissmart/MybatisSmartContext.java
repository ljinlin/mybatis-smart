package com.mingri.mybatissmart;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.mingri.langhuan.cabinet.algorithm.SequenceGenerate;
import com.mingri.langhuan.cabinet.constant.FileSufx;
import com.mingri.langhuan.cabinet.tool.ClassTool;
import com.mingri.langhuan.cabinet.tool.FileTool;
import com.mingri.langhuan.cabinet.tool.StrTool;
import com.mingri.mybatissmart.annotation.SmartColumn;
import com.mingri.mybatissmart.annotation.SmartTable;
import com.mingri.mybatissmart.barracks.Constant;
import com.mingri.mybatissmart.barracks.DialectEnum;
import com.mingri.mybatissmart.barracks.IdtacticsEnum;
import com.mingri.mybatissmart.barracks.Tool;
import com.mingri.mybatissmart.dbo.SmartColumnInfo;
import com.mingri.mybatissmart.dbo.SmartTableInfo;
import com.mingri.mybatissmart.mapper.InsertMapper;
import com.mingri.mybatissmart.mapper.InternalMapper;
import com.mingri.mybatissmart.mapper.SmartMapper;

/**
 * mybatis-smart 上下文,单例的
 * 
 * @author ljl 2019年11月30日
 */
public class MybatisSmartContext {

	private final static Logger LOGGER = LoggerFactory.getLogger(MybatisSmartContext.class);

	private List<MybatisSmartConfiguration> configurations;

	private static SequenceGenerate sequenceGenerate;


	/**
	 * 表类映射信息
	 */
	private static final Map<Class<?>, SmartTableInfo> SMART_TABLE_MAP = new HashMap<>();

	private static final AtomicInteger SQL_INCR_COUNT = new AtomicInteger();

	private volatile static int LOAD_STATUS = -1;

	
	public static SequenceGenerate getSequenceGenerate() {
		if (sequenceGenerate == null) {
			sequenceGenerate = SequenceGenerate.getInstance();
		}
		return sequenceGenerate;
	}

	public void setSequenceGenerate(SequenceGenerate sequenceGenerate) {
		MybatisSmartContext.sequenceGenerate = sequenceGenerate;
	}

	public List<MybatisSmartConfiguration> getConfigurations() {
		return configurations;
	}

	public void setConfigurations(List<MybatisSmartConfiguration> configurations) {
		this.configurations = configurations;
	}

	
	@PostConstruct
	public void start() {
		try {
			LOAD_STATUS=0;
			org.apache.ibatis.session.Configuration ibatisConfiguration = null;
			SqlSessionFactory sessionFactory = null;
			for (MybatisSmartConfiguration configuration : configurations) {
				sessionFactory = configuration.getSqlSessionFactory();
				ibatisConfiguration = sessionFactory.getConfiguration();
				if (!ibatisConfiguration.hasMapper(InternalMapper.class)) {
					ibatisConfiguration.addMapper(InternalMapper.class);
				}
				scanSmartTable(configuration);
			}
			replaceKeyGenerator();
		} catch (Exception e) {
			LOGGER.error("捕获到异常,打印日志", e);
			throw new MybatisSmartException(e.getLocalizedMessage() + "---" + e.getMessage());
		} finally {
			LOAD_STATUS = 1;
		}
	}

	public static SmartTableInfo getSmartTableInfo(Class<?> tableClass) {
		// 没有scanMapping
		SmartTableInfo smti = SMART_TABLE_MAP.get(tableClass);
		if (smti != null) {
			return smti;
		}

		Class<?> tmpTableClass = tableClass;
		checkisNeedWaitLoad();
		while (tmpTableClass != null && tmpTableClass != Object.class) {
			tmpTableClass = tmpTableClass.getSuperclass();
			if (tmpTableClass.getAnnotation(SmartTable.class) != null) {// 。 拿父类的
				smti = SMART_TABLE_MAP.get(tmpTableClass);
				if (smti != null) {
					return smti;
				}
			}
		}
		throw new MybatisSmartException(StrTool
				.concat("Class:", tableClass.getCanonicalName(), " 或者其父类 没有配置注解:@", SmartTable.class.getSimpleName())
				.toString());
	}

	private static void checkisNeedWaitLoad() {
		int waitNum = 1;
		while (LOAD_STATUS==-1 && waitNum < 800) {
			try {
				Thread.sleep(50);
				waitNum++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(LOAD_STATUS==-1) {
			LOAD_STATUS = 1;
			throw new MybatisSmartException("未扫描实体类和表的映射关系");
		}
		if(LOAD_STATUS==0) {
			LOAD_STATUS = 1;
			throw new MybatisSmartException("扫描实体类和表的映射关系超时");
		}
	}

	public static Set<String> getColumns(Class<?> cl) {
		SmartTableInfo smti = getSmartTableInfo(cl);
		if (smti != null) {
			return smti.getSmartColumnInfoMap().keySet();
		}
		return Collections.emptySet();
	}

	public static String getTable(Class<?> cl) {
		SmartTableInfo smti = getSmartTableInfo(cl);
		if (smti != null) {
			return smti.getSmartTable().value();
		}
		return null;
	}

	private List<String> selectColumns(String tableName, SqlSessionFactory sessionFactory) {
		try (SqlSession session = sessionFactory.openSession()) {
			return session.selectList(InternalMapper.SELECTFIELDS_STATEMENT,
					tableName.replace("[", "").replace("]", ""));
		} catch (Exception e) {
			LOGGER.error("捕获到异常,打印日志", e);
		}
		return Collections.emptyList();
	}

	private final static Object MAPPING_LOCAL = new Object();

	private void validSmartTable(SmartTable tableInfo) {
		String idFieldName = tableInfo.idFieldName();
		String tableName = Tool.getTableName(tableInfo);
		if (tableName.isEmpty()) {
			throw new MybatisSmartException(SmartTable.class.getCanonicalName() + " 的value 不能为空");
		}

		if (idFieldName.length() == 0) {
			throw new MybatisSmartException(SmartTable.class.getCanonicalName() + " 的idFieldName value 不能为空");
		}
	}

	/**
	 * 映射表模型
	 * 
	 * @param smartTableClass
	 * @return
	 * @throws SQLException
	 */
	private LinkedHashMap<String, SmartColumnInfo> mappingSmartTable(Class<?> smartTableClass,
			SqlSessionFactory sqlSessionFactory) throws SQLException {

		SmartTable tableInfo = smartTableClass.getAnnotation(SmartTable.class);
		if (tableInfo == null) {
			return null;
		}
		validSmartTable(tableInfo);
		String tableName = Tool.getTableName(tableInfo);
		Map<String, String> fieldColNameDbMap = getFieldAndColumnNameMap(tableName, sqlSessionFactory);

		if (fieldColNameDbMap == null || fieldColNameDbMap.isEmpty()) {
			LOGGER.warn("---------------扫描警告：{} 没有映射表", smartTableClass);
			return null;
		}

		List<Field> fieldList = ClassTool.getDecararedFields(smartTableClass, false);
		LinkedHashMap<String, SmartColumnInfo> columnFieldMap = new LinkedHashMap<>();

		for (Field field : fieldList) {

			SmartColumn columnInfo = field.getAnnotation(SmartColumn.class);
			String columnName = columnInfo == null ? StrTool.EMPTY
					: Tool.unifiedColumnName(StrTool.toString(columnInfo.value()));

			if (columnName.isEmpty()) {
				// 。根据驼峰规则自动映射
				columnName = fieldColNameDbMap.get(field.getName());
				if (columnName == null) {
					// 。 该字段没有映射的列
					continue;
				}
			} else {
				// 。根据注解配置映射
				if (!fieldColNameDbMap.containsValue(columnName)) {
					throw new MybatisSmartException(StrTool.concat(tableName, " 表中没有字段:", columnName).toString());
				}
			}

			SmartColumnInfo smci = columnFieldMap.get(columnName);
			if (smci == null) {
				columnFieldMap.put(columnName, new SmartColumnInfo(field, columnInfo));
			} else {
				if (field.getDeclaringClass() == smci.getField().getDeclaringClass()) {
					throw new MybatisSmartException(StrTool.concat(smartTableClass.getCanonicalName(),
							" 类中字段名称解析相同，字段:", smci.getField().getName(), "和", field.getName()).toString());
				} else {
					smci.setField(field);
					smci.setSmartColumn(columnInfo);
				}
			}
		}
		return columnFieldMap;
	}

	/**
	 * 扫描数据模型
	 */
	private void scanSmartTable(MybatisSmartConfiguration configuration) throws ClassNotFoundException, SQLException {
		String mdpkg = configuration.getTablePackages();
		SqlSessionFactory sqlSessionFactory = configuration.getSqlSessionFactory();
		DialectEnum dialect = DialectEnum.ofName(configuration.getDialect());
		String sqlSessionFactoryBeanName = StrTool.toString(configuration.getSqlSessionFactoryBeanName());
		if (StrTool.isNotEmpty(mdpkg)) {
			String[] mdpkgArray = StringUtils.tokenizeToStringArray(mdpkg,
					ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			for (String packageName : mdpkgArray) {
				LOGGER.info("==============================MybatisSmart {} 开始扫描tablePackage:{}",
						sqlSessionFactoryBeanName, packageName);
				List<String> classList = FileTool.scanClass(packageName, true);
				LOGGER.info("==============================MybatisSmart {} 开始扫描{}", sqlSessionFactoryBeanName,
						classList);
				for (String classFileName : classList) {
					try {
						Class<?> smartTableClazz = classLoader.loadClass(
								classFileName.substring(0, classFileName.length() - FileSufx.CLAZZ.length()));
						if (smartTableClazz.getAnnotation(SmartTable.class) == null) {
							continue;
						}
						synchronized (MAPPING_LOCAL) {
							SmartTableInfo tableModelInfo = SMART_TABLE_MAP.get(smartTableClazz);
							if (tableModelInfo != null) {
								return;
							}
							LinkedHashMap<String, SmartColumnInfo> smartColumnInfoMap = mappingSmartTable(
									smartTableClazz, sqlSessionFactory);
							if (smartColumnInfoMap == null) {
								continue;
							}
							DialectEnum dialectEnum = getDialect(sqlSessionFactory);
							if (dialectEnum != null) {
								dialect = dialectEnum;
							}
							SmartTableInfo smartTableInfo = new SmartTableInfo.Builder(smartTableClazz,
									sqlSessionFactory, dialect).builder(smartColumnInfoMap);

							SMART_TABLE_MAP.put(smartTableClazz, smartTableInfo);
							if (smartTableInfo.getSmartTable().idtactics() == IdtacticsEnum.SQL_INCR) {
								SQL_INCR_COUNT.incrementAndGet();
							}
						}
						LOGGER.info("MybatisSmart {} Scanned class: {} for tablePackage: {}", sqlSessionFactoryBeanName,
								classFileName, packageName);
					} catch (ClassNotFoundException | SQLException e) {
						LOGGER.error("MybatisSmart {} 扫描tablePackage出错： {}", sqlSessionFactoryBeanName, e);
						throw e;
					}
				}

			}
		}
	}

	/**
	 * 获取数据库方言
	 * 
	 * @return
	 * @throws SQLException
	 */
	private DialectEnum getDialect(SqlSessionFactory sessionFactory) throws SQLException {
		try (Connection conn = sessionFactory.openSession().getConnection()) {
			return Tool.getDialect(conn);
		}
	}

	/**
	 * 获取java字段名称和数据库字段名称
	 * 
	 * @param tableName 表名
	 * @return key:fieldName(columnName转驼峰后的name) val:columnName（大写）
	 */
	private Map<String, String> getFieldAndColumnNameMap(String tableName, SqlSessionFactory sqlSessionFactory) {
		List<String> columns = selectColumns(tableName, sqlSessionFactory);
		Map<String, String> columnsCamel = new HashMap<>();
		columns.forEach(column -> {
			String columnName = Tool.unifiedColumnName(column);
			String fieldName = StrTool.camel(columnName);
			columnsCamel.put(fieldName, columnName);
		});
		return columnsCamel;
	}

	/**
	 * 为InsertSmartMapper 子类更换id生成器
	 * 
	 * @author jinlin Li
	 */
	private void replaceKeyGenerator() {
		if (SMART_TABLE_MAP.isEmpty()) {
			return;
		}
		org.apache.ibatis.session.Configuration ibatisConfiguration = null;
		Field fi = ClassTool.searchDecararedField(MappedStatement.class, "keyGenerator");
		fi.setAccessible(true);
		for (MybatisSmartConfiguration configuration : configurations) {
			ibatisConfiguration = configuration.getSqlSessionFactory().getConfiguration();
			Collection<MappedStatement> stList = ibatisConfiguration.getMappedStatements();
			for (Object obj : stList) {
				boolean isSmartSubMapper = false;
				if (!(obj instanceof MappedStatement)) {
					continue;
				}
				MappedStatement mappedStatement = (MappedStatement) obj;
				SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
				String resource = mappedStatement.getResource();
				try {
					if (sqlCommandType != SqlCommandType.INSERT || !(fi.get(mappedStatement) instanceof NoKeyGenerator)
							|| !(mappedStatement.getId().endsWith(Constant.INSERT_METHOD))) {
						continue;
					}
					int index = resource.indexOf(".java");
					resource = FileTool.dirToPkg(resource.substring(0, index));

					Class<?> mapperClazz = Class.forName(resource);
					boolean isInsertMapper = SmartMapper.class.isAssignableFrom(mapperClazz)
							|| InsertMapper.class.isAssignableFrom(mapperClazz);
					if (isInsertMapper == false) {
						continue;
					}

					List<Type> types = ClassTool.getExtendGenericity(mapperClazz, SmartMapper.class);
					if (types.isEmpty()) {
						types = ClassTool.getExtendGenericity(mapperClazz, InsertMapper.class);
						if (types.isEmpty()) {
							Assert.notEmpty(types, "mapper：" + mappedStatement.getResource() + "没有设定泛型！！！");
						}
					}

					isSmartSubMapper = true;
					Class<?> tableClazz = null;
					try {
						tableClazz = Class.forName(types.get(0).getTypeName());

						SmartTableInfo smtb = getSmartTableInfo(tableClazz);
						if (smtb == null) {
							LOGGER.warn("============》 {} 的泛型类没有配置注解:@ {} ", mappedStatement.getResource(),
									SmartTable.class.getCanonicalName());
							continue;
						}
						if (smtb.getSmartTable().idtactics() != IdtacticsEnum.SQL_INCR) {
							continue;
						}
					} catch (Exception e) {
						if (tableClazz == null) {
							LOGGER.error("扫描", mappedStatement.getResource(), "的泛型出错", e);
						}
						continue;
					}
					fi.set(mappedStatement, SmartKeyGenerator.INSTANCE);
					LOGGER.info("\"数据库id自增\"扫描===》：{}", tableClazz.getCanonicalName());
				} catch (Exception e) {
					if (isSmartSubMapper && SQL_INCR_COUNT.getAndDecrement() > 0) {
//							fi.set(mappedStatement, SmartKeyGenerator.INSTANCE);
						LOGGER.warn("\"数据库id自增扫描\"===》：{},", resource, "未扫描到它的泛型实体类");
					}
					LOGGER.error("捕获到异常,打印日志", e);
				}
			}
		}
	}

}