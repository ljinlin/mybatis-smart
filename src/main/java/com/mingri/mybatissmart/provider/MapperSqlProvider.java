package com.mingri.mybatissmart.provider;

import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mingri.mybatissmart.barracks.Constant;
import com.mingri.mybatissmart.barracks.SqlPrint;
import com.mingri.mybatissmart.config.MybatisSmartContext;
import com.mingri.mybatissmart.dbo.SetSql;
import com.mingri.mybatissmart.dbo.SmartTableInfo;
import com.mingri.mybatissmart.dbo.Where;
import com.mingri.mybatissmart.ex.SmartConfiguration;

/**
 * mapper sql 提供者
 * @author ljl
 * 2019年11月30日
 */
public class MapperSqlProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(MapperSqlProvider.class);
	
	 
		public static  String select(Object obj) {
			Class<?> cl=SmartConfiguration.currentModel();
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
	
	public static  String selectById(Object idV) {
		SmartTableInfo smti;
		Class<?> cl=SmartConfiguration.currentModel();
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

	public  static String count(Object obj,@Param(Constant.COND_KEY) Where where) {
		String sql = null;
		Class<?> cl=SmartConfiguration.currentModel();
		try {
			SmartTableInfo smti = MybatisSmartContext.getSmartTableInfo(cl);			
			sql = smti.getCountByWhereSql(obj, where);
			SqlPrint.instance().print(LOGGER,sql);
		} catch (Exception e) {
			LOGGER.error("sql构建异常:", e);
		}
		return sql;
	}


	public  static String delete(@Param(Constant.PARAM_KEY) Object obj, @Param(Constant.COND_KEY) Where where) {
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
	public  static String inserts(@Param(Constant.PARAM_KEY) Object obj) {
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

	public static  String updateById(@Param(Constant.PARAM_KEY) Object obj) {
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
	public  static String updateBySets(@Param(Constant.TABLE_KEY) Class<?> tableClazz,@Param(Constant.PARAM_KEY) Object obj, @Param(Constant.COND_KEY) Where where) {
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

	public static  String updateByWhere(@Param(Constant.PARAM_KEY) Object obj, @Param(Constant.COND_KEY) Where where) {
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

	public static  String deleteById(Object idV) {
		Class<?> cl=SmartConfiguration.currentModel();
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
