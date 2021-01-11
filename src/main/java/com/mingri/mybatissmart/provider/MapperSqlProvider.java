package com.mingri.mybatissmart.provider;

import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mingri.mybatissmart.MybatisSmartContext;
import com.mingri.mybatissmart.barracks.Constant;
import com.mingri.mybatissmart.barracks.SqlPrint;
import com.mingri.mybatissmart.dbo.SetSql;
import com.mingri.mybatissmart.dbo.SmartTableInfo;
import com.mingri.mybatissmart.dbo.Where;

/**
 * mapper sql 提供者
 * @author ljl
 * 2019年11月30日
 */
public class MapperSqlProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(MapperSqlProvider.class);
	
	 static final ThreadLocal<Class<?>> MODEL=new ThreadLocal<>();
	 static final ThreadLocal<String> MAPPER_ID=new ThreadLocal<>();
	

//	public String select(@Param(Constant.PARAM_KEY) Object obj, @Param(Constant.COND_KEY) Where where) {
		public String select(Object obj) {
			Class<?> cl=MODEL.get();
			String ss=MAPPER_ID.get();
			System.out.println(cl+"-----"+ss);
			String sql = null;
			try {
		SqlBuildParam paramWrapper=new SqlBuildParam.Builder(obj).build();
		SmartTableInfo smti = paramWrapper.getSmti();
		Where filterSqlBuild=paramWrapper.getFilterSqlBuild();
			smti = MybatisSmartContext.getSmartTableInfo(cl);
			sql = smti.getSelectByWhereSql(paramWrapper.getParam(), filterSqlBuild);
			SqlPrint.instance().print(LOGGER,sql);
		} catch (Exception e) {
			LOGGER.error("sql构建异常:", e);
		}
		return sql;
	}
	
	public String selectById(Object idV) {
		SmartTableInfo smti;
		Class<?> cl=MODEL.get();
		String sql = null;
		try {
			smti = MybatisSmartContext.getSmartTableInfo(cl);
			 sql = smti.getSelectByIdSql(idV);
			LOGGER.info(sql);
		} catch (Exception e) {
			LOGGER.error("sql构建异常:", e);
		}
		return sql;
	}

	public String count(Object obj,@Param(Constant.COND_KEY) Where where) {
		String sql = null;
		Class<?> cl=MODEL.get();
		try {
			SmartTableInfo smti = MybatisSmartContext.getSmartTableInfo(cl);			
			sql = smti.getCountByWhereSql(obj, where);
			SqlPrint.instance().print(LOGGER,sql);
		} catch (Exception e) {
			LOGGER.error("sql构建异常:", e);
		}
		return sql;
	}


	public String delete(@Param(Constant.PARAM_KEY) Object obj, @Param(Constant.COND_KEY) Where where) {
		SqlBuildParam paramWrapper=new SqlBuildParam.Builder(obj).build();
		SmartTableInfo smti = paramWrapper.getSmti();
		
		String sql = null;
		try {
			sql = smti.getDeleteByWhereSql(paramWrapper.getParam(), where);
			SqlPrint.instance().print(LOGGER,sql);
		} catch (Exception e) {
			LOGGER.error("sql构建异常:", e);
		}
		return sql;
	}
	public String inserts(@Param(Constant.PARAM_KEY) Object obj) {
		SqlBuildParam paramWrapper=new SqlBuildParam.Builder(obj).build();
		SmartTableInfo smti = paramWrapper.getSmti();
		String sql = null;
		try {
			sql = smti.getInsertSql(paramWrapper.getParam());
		} catch (Exception e) {
			LOGGER.error("sql构建异常:", e);
		}
		return sql;
	}

	public String updateById(@Param(Constant.PARAM_KEY) Object obj) {
		SqlBuildParam paramWrapper=new SqlBuildParam.Builder(obj).build();
		SmartTableInfo smti = paramWrapper.getSmti();
		String sql = null;
		try {
			sql = smti.getUpdateByIdSql(paramWrapper.getParam());
			SqlPrint.instance().print(LOGGER,sql);
		} catch (Exception e) {
			LOGGER.error("sql构建异常:", e);
		}
		return sql;
	}
	public String updateBySets(@Param(Constant.TABLE_KEY) Class<?> tableClazz,@Param(Constant.PARAM_KEY) Object obj, @Param(Constant.COND_KEY) Where where) {
		SqlBuildParam paramWrapper=new SqlBuildParam.Builder(obj).setClazz(tableClazz).build();
		SmartTableInfo smti = paramWrapper.getSmti();
		String sql = null;
		try {
			sql = smti.getUpdateBySetSAndWhereSql((SetSql)paramWrapper.getParam(),where);
			SqlPrint.instance().print(LOGGER,sql);
		} catch (Exception e) {
			LOGGER.error("sql构建异常:", e);
		}
		return sql;
	}

	public String updateByWhere(@Param(Constant.PARAM_KEY) Object obj, @Param(Constant.COND_KEY) Where where) {
		SqlBuildParam paramWrapper=new SqlBuildParam.Builder(obj).build();
		SmartTableInfo smti = paramWrapper.getSmti();
		String sql = null;
		try {
			sql = smti.getUpdateByWhereSql(paramWrapper.getParam(), where);
			SqlPrint.instance().print(LOGGER,sql);
		} catch (Exception e) {
			LOGGER.error("sql构建异常:", e);
		}
		return sql;
	}

	public String deleteById(Object idV, Class<?> cl) {
		SqlBuildParam paramWrapper=new SqlBuildParam.Builder(idV).setClazz(cl).build();
		SmartTableInfo smti;
		String sql = null;
		try {
			 smti = paramWrapper.getSmti();
			sql = smti.getDeleteByIdSql(paramWrapper.getParam());
			SqlPrint.instance().print(LOGGER,sql);
		} catch (Exception e) {
			LOGGER.error("sql构建异常: ", e);
		}
		return sql;
	}



}
