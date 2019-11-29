package com.mingri.mybatissmart.provider;

import java.sql.SQLException;

import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mingri.mybatissmart.MybatisSmartContext;
import com.mingri.mybatissmart.barracks.Constant;
import com.mingri.mybatissmart.dbo.TableClass;
import com.mingri.mybatissmart.dbo.Where;

public class MapperSqlProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(MapperSqlProvider.class);

	

	public String select(@Param(Constant.PARAM_KEY) Object obj, @Param(Constant.COND_KEY) Where where) {
		SqlBuildParam paramWrapper=new SqlBuildParam.Builder(obj).build();
		TableClass cmi = paramWrapper.getCmi();
		String sql = null;
		try {
			sql = cmi.getSelectByWhereSql(paramWrapper.getParam(), where);
			LOGGER.info(sql);
		} catch (Exception e) {
			LOGGER.error("sql构建异常:{}", e);
		}
		return sql;
	}
	
	public String selectById(Object idV, Class<?> cl) {
		TableClass cmi;
		String sql = null;
		try {
			cmi = MybatisSmartContext.getClassMapperInfo(cl);
			 sql = cmi.getSelectByIdSql(idV);
			LOGGER.info(sql);
		} catch (SQLException e) {
			LOGGER.error("sql构建异常:{}", e);
		}
		return sql;
	}

	public String count(@Param(Constant.PARAM_KEY) Object obj, @Param(Constant.COND_KEY) Where where) {
		SqlBuildParam paramWrapper=new SqlBuildParam.Builder(obj).build();
		TableClass cmi = paramWrapper.getCmi();
		String sql = null;
		try {
			sql = cmi.getCountByWhereSql(paramWrapper.getParam(), where);
			LOGGER.info(sql);
		} catch (Exception e) {
			LOGGER.error("sql构建异常:{}", e);
		}
		return sql;
	}


	public String delete(@Param(Constant.PARAM_KEY) Object obj, @Param(Constant.COND_KEY) Where where) {
		SqlBuildParam paramWrapper=new SqlBuildParam.Builder(obj).build();
		TableClass cmi = paramWrapper.getCmi();
		
		String sql = null;
		try {
			sql = cmi.getDeleteByWhereSql(paramWrapper.getParam(), where);
			LOGGER.info(sql);
		} catch (Exception e) {
			LOGGER.error("sql构建异常:{}", e);
		}
		return sql;
	}
	public String insert(@Param(Constant.PARAM_KEY) Object obj) {
		long sttime = System.currentTimeMillis();
		SqlBuildParam paramWrapper=new SqlBuildParam.Builder(obj).build();
		TableClass cmi = paramWrapper.getCmi();
		String sql = null;
		try {
			sql = cmi.getInsertSql(paramWrapper.getParam());
			long edtime = System.currentTimeMillis();
			LOGGER.info("构建SQL耗时:{},sql:{}", (edtime - sttime), sql);
		} catch (Exception e) {
			LOGGER.error("sql构建异常:{}", e);
		}
		return sql;
	}

	public String updateById(@Param(Constant.PARAM_KEY) Object obj) {
		SqlBuildParam paramWrapper=new SqlBuildParam.Builder(obj).build();
		TableClass cmi = paramWrapper.getCmi();
		String sql = null;
		try {
			sql = cmi.getUpdateByIdSql(paramWrapper.getParam());
			LOGGER.info(sql);
		} catch (Exception e) {
			LOGGER.error("sql构建异常:{}", e);
		}
		return sql;
	}

	public String updateByWhere(@Param(Constant.PARAM_KEY) Object obj, @Param(Constant.COND_KEY) Where where) {
		SqlBuildParam paramWrapper=new SqlBuildParam.Builder(obj).build();
		TableClass cmi = paramWrapper.getCmi();
		String sql = null;
		try {
			sql = cmi.getUpdateByWhereSql(paramWrapper.getParam(), where);
			LOGGER.info(sql);
		} catch (Exception e) {
			LOGGER.error("sql构建异常:{}", e);
		}
		return sql;
	}

	public String deleteById(Object idV, Class<?> cl) {
		SqlBuildParam paramWrapper=new SqlBuildParam.Builder(idV).setClazz(cl).build();
		TableClass cmi;
		String sql = null;
		try {
			 cmi = MybatisSmartContext.getClassMapperInfo(cl);
			sql = cmi.getDeleteByIdSql(paramWrapper.getParam());
			LOGGER.info(sql);
		} catch (Exception e) {
			LOGGER.error("sql构建异常: {}", e);
		}
		return sql;
	}



}
