package com.ws.mybatissmart;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseMapperSqlProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(MybatisSmartAutoConfiguration.class);

	public String selectByWhere(@Param(MybatisSmartAutoConfiguration.E_K) Object obj,
			@Param(MybatisSmartAutoConfiguration.C_K) WhereSql cond) {
		Object entity = obj;
		if (obj instanceof ParamMap) {
			entity = ((ParamMap<?>) obj).get(MybatisSmartAutoConfiguration.E_K);
		}
		ClassMapperInfo cmi = MybatisSmartContext.getClassMapperInfo(entity.getClass());
		String sql = cmi.getSelectByWhereSql(entity, cond);
		LOGGER.info(sql);
		return sql;
	}
	public String selectForWhere(@Param("clazz") Class<?> clazz,
			@Param(MybatisSmartAutoConfiguration.C_K) WhereSql cond) {
	
		ClassMapperInfo cmi = MybatisSmartContext.getClassMapperInfo(clazz);
		String sql = cmi.getSelectByWhereSql(clazz, cond);
		LOGGER.info(sql);
		return sql;
	}
	public String countForWhere(@Param("clazz") Class<?> clazz,
			@Param(MybatisSmartAutoConfiguration.C_K) WhereSql cond) {
		
		ClassMapperInfo cmi = MybatisSmartContext.getClassMapperInfo(clazz);
		String sql = cmi.getCountByWhereSql(clazz, cond);
		LOGGER.info(sql);
		return sql;
	}

	public String countByWhere(@Param(MybatisSmartAutoConfiguration.E_K) Object obj,
			@Param(MybatisSmartAutoConfiguration.C_K) WhereSql cond) {
		Object entity = obj;
		if (obj instanceof ParamMap) {
			entity = ((ParamMap<?>) obj).get(MybatisSmartAutoConfiguration.E_K);
		}
		ClassMapperInfo cmi = MybatisSmartContext.getClassMapperInfo(entity.getClass());
		String sql = cmi.getCountByWhereSql(entity, cond);
		LOGGER.info(sql);
		return sql;
	}

	public String selectById(Object idV, Class<?> cl) {
		ClassMapperInfo cmi = MybatisSmartContext.getClassMapperInfo(cl);
		String sql = cmi.getSelectByIdSql(idV);
		LOGGER.info(sql);
		return sql;
	}

	public String insert(@Param(MybatisSmartAutoConfiguration.E_K) Object obj) {
		Object entity = obj;
		Class<?> clazz=null;
		if (obj instanceof ParamMap) {
			 entity = ((ParamMap<?>) obj).get(MybatisSmartAutoConfiguration.E_K);
			if (entity instanceof List) {
				clazz= ((List) entity).get(0).getClass();
			} else {
				clazz=entity.getClass();
			}
		}
		ClassMapperInfo cmi = MybatisSmartContext.getClassMapperInfo(clazz);
		String sql = null;
		try {
			sql = cmi.getInsertSql(entity);
			LOGGER.info(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql;
	}

	public String updateById(@Param(MybatisSmartAutoConfiguration.E_K) Object obj) {
		Object entity = obj;
		if (obj instanceof ParamMap) {
			entity = ((ParamMap<?>) obj).get(MybatisSmartAutoConfiguration.E_K);
		}
		ClassMapperInfo cmi = MybatisSmartContext.getClassMapperInfo(entity.getClass());
		String sql = null;
		try {
			sql = cmi.getUpdateByIdSql(entity);
			LOGGER.info(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql;
	}
	public String updateByWhere(@Param(MybatisSmartAutoConfiguration.E_K) Object obj,
			@Param(MybatisSmartAutoConfiguration.C_K) WhereSql cond) {
		Object entity = obj;
		if (obj instanceof ParamMap) {
			entity = ((ParamMap<?>) obj).get(MybatisSmartAutoConfiguration.E_K);
		}
		ClassMapperInfo cmi = MybatisSmartContext.getClassMapperInfo(entity.getClass());
		String sql = cmi.getDeleteByWhereSql(entity, cond);
		LOGGER.info(sql);
		return sql;
	}

	public String deleteById(Object obj, Class<?> cl) {
		Object entity = obj;
		if (obj instanceof ParamMap) {
			entity = ((ParamMap<?>) obj).get(MybatisSmartAutoConfiguration.E_K);
		}
		ClassMapperInfo cmi = MybatisSmartContext.getClassMapperInfo(cl);
		String sql = cmi.getDeleteByIdSql(entity);
		LOGGER.info(sql);
		return sql;
	}

	public String deleteByWhere(@Param(MybatisSmartAutoConfiguration.E_K) Object obj,
			@Param(MybatisSmartAutoConfiguration.C_K) WhereSql cond) {
		Object entity = obj;
		if (obj instanceof ParamMap) {
			entity = ((ParamMap<?>) obj).get(MybatisSmartAutoConfiguration.E_K);
		}
		ClassMapperInfo cmi = MybatisSmartContext.getClassMapperInfo(entity.getClass());
		String sql = cmi.getDeleteByWhereSql(entity, cond);
		LOGGER.info(sql);
		return sql;
	}
	public String deleteForWhere(@Param("clazz") Class<?> clazz,
			@Param(MybatisSmartAutoConfiguration.C_K) WhereSql cond) {
		ClassMapperInfo cmi = MybatisSmartContext.getClassMapperInfo(clazz);
		String sql = cmi.getDeleteByWhereSql(clazz, cond);
		LOGGER.info(sql);
		return sql;
	}

}
