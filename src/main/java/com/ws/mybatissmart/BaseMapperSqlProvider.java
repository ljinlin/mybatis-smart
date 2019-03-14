package com.ws.mybatissmart;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseMapperSqlProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseMapperSqlProvider.class);

	public String selectByWhere(@Param(Constant.PARAM_KEY) Object obj, @Param(Constant.COND_KEY) WhereSql cond) {
		Object entity = obj;
		if (obj instanceof ParamMap) {
			entity = ((ParamMap<?>) obj).get(Constant.PARAM_KEY);
		}
		ClassMapperInfo cmi = MybatisSmartContext.getClassMapperInfo(entity.getClass());
		String sql = cmi.getSelectByWhereSql(entity, cond);
		LOGGER.info(sql);
		return sql;
	}

	public String selectForWhere(@Param("clazz") Class<?> clazz, @Param(Constant.COND_KEY) WhereSql cond) {

		ClassMapperInfo cmi = MybatisSmartContext.getClassMapperInfo(clazz);
		String sql = cmi.getSelectByWhereSql(clazz, cond);
		LOGGER.info(sql);
		return sql;
	}

	public String countForWhere(@Param("clazz") Class<?> clazz, @Param(Constant.COND_KEY) WhereSql cond) {

		ClassMapperInfo cmi = MybatisSmartContext.getClassMapperInfo(clazz);
		String sql = cmi.getCountByWhereSql(clazz, cond);
		LOGGER.info(sql);
		return sql;
	}

	public String countByWhere(@Param(Constant.PARAM_KEY) Object obj, @Param(Constant.COND_KEY) WhereSql cond) {
		Object entity = obj;
		if (obj instanceof ParamMap) {
			entity = ((ParamMap<?>) obj).get(Constant.PARAM_KEY);
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

	public String insert(@Param(Constant.PARAM_KEY) Object obj) {
		Object entity = obj;
		Class<?> clazz = null;
		long sttime=System.currentTimeMillis();
		if (obj instanceof ParamMap) {
			entity = ((ParamMap<?>) obj).get(Constant.PARAM_KEY);
			if (entity instanceof List) {
				clazz = ((List) entity).get(0).getClass();
			} else {
				clazz = entity.getClass();
			}
		}
		ClassMapperInfo cmi = MybatisSmartContext.getClassMapperInfo(clazz);
		String sql = null;
		try {
			sql = cmi.getInsertSql(entity);
			long edtime=System.currentTimeMillis();
			LOGGER.info("构建SQL耗时:"+(edtime-sttime)+" "+sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql;
	}

	public String updateById(@Param(Constant.PARAM_KEY) Object obj) {
		Object entity = obj;
		if (obj instanceof ParamMap) {
			entity = ((ParamMap<?>) obj).get(Constant.PARAM_KEY);
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

	public String updateByWhere(@Param(Constant.PARAM_KEY) Object obj, @Param(Constant.COND_KEY) WhereSql cond) {
		Object entity = obj;
		if (obj instanceof ParamMap) {
			entity = ((ParamMap<?>) obj).get(Constant.PARAM_KEY);
		}
		ClassMapperInfo cmi = MybatisSmartContext.getClassMapperInfo(entity.getClass());
		String sql=null;
		try {
			sql = cmi.getUpdateByWhereSql(entity, cond);
			LOGGER.info(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql;
	}

	public String deleteById(Object obj, Class<?> cl) {
		Object entity = obj;
		if (obj instanceof ParamMap) {
			entity = ((ParamMap<?>) obj).get(Constant.PARAM_KEY);
		}
		ClassMapperInfo cmi = MybatisSmartContext.getClassMapperInfo(cl);
		String sql = cmi.getDeleteByIdSql(entity);
		LOGGER.info(sql);
		return sql;
	}

	public String deleteByWhere(@Param(Constant.PARAM_KEY) Object obj, @Param(Constant.COND_KEY) WhereSql cond) {
		Object entity = obj;
		if (obj instanceof ParamMap) {
			entity = ((ParamMap<?>) obj).get(Constant.PARAM_KEY);
		}
		ClassMapperInfo cmi = MybatisSmartContext.getClassMapperInfo(entity.getClass());
		String sql = cmi.getDeleteByWhereSql(entity, cond);
		LOGGER.info(sql);
		return sql;
	}

	public String deleteForWhere(@Param("clazz") Class<?> clazz, @Param(Constant.COND_KEY) WhereSql cond) {
		ClassMapperInfo cmi = MybatisSmartContext.getClassMapperInfo(clazz);
		String sql = cmi.getDeleteByWhereSql(clazz, cond);
		LOGGER.info(sql);
		return sql;
	}

}
