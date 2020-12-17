package com.mingri.mybatissmart.provider;

import java.sql.SQLException;

import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mingri.mybatissmart.barracks.Constant;
import com.mingri.mybatissmart.config.MybatisSmartContext;
import com.mingri.mybatissmart.dbo.SmartTableInfo;
import com.mingri.mybatissmart.dbo.Where;
import com.mingri.mybatissmart.ex.SmartConfiguration;

/**
 *  mapper sql构建所需要的参数类
 * @author ljl
 * 2019年11月30日
 */
public class SqlBuildParam {


	private Class<?> clazz;
	private Object param;
	private  Where filterSqlBuild;
	private SmartTableInfo smti;

	public Class<?> getClazz() {
		return clazz;
	}

	private void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Object getParam() {
		return param;
	}

	private void setParam(Object param) {
		this.param = param;
	}

	private void setFilterSqlBuild(Where filterSqlBuild) {
		this.filterSqlBuild = filterSqlBuild;
	}

	public SmartTableInfo getSmti() {
		return smti;
	}

	public void setSmti(SmartTableInfo smti) {
		this.smti = smti;
	}

	public Where getFilterSqlBuild() {
		return filterSqlBuild;
	}

	private SqlBuildParam() {
	}

	public static class Builder {
		private static final Logger LOGGER = LoggerFactory.getLogger(Builder.class);
		
		private Object providerParam;
		private Class<?> clazz;

		public Class<?> getClazz() {
			return clazz;
		}

		public Builder setClazz(Class<?> clazz) {
			this.clazz = clazz;
			return this;
		}

		/**
		 * 构建
		 * 
		 * @param providerParam
		 * @return
		 * @throws SQLException 
		 */
		private void construct(SqlBuildParam paramWrapper) throws SQLException {
			Object param = null;
			ParamMap<?> paramMap=null;
			Where filterSqlBuild=null;
			if (providerParam instanceof ParamMap) {
				paramMap=(ParamMap<?>) providerParam;
				param = paramMap.getOrDefault(Constant.PARAM_KEY,null);
				filterSqlBuild = (Where)paramMap.getOrDefault(Constant.COND_KEY,null);
			} else {
				param = providerParam;
			}
			
			this.clazz=SmartConfiguration.currentModel();
			SmartTableInfo smti = MybatisSmartContext.getSmartTableInfo(this.clazz);
			paramWrapper.setClazz(this.clazz);
			paramWrapper.setSmti(smti);
			paramWrapper.setParam(param);
			paramWrapper.setFilterSqlBuild(filterSqlBuild);
		}

		public Builder(Object providerParam) {
			this.providerParam = providerParam;
		}

		public SqlBuildParam build()  {
			SqlBuildParam param = new SqlBuildParam();
			try {
				construct(param);
				return param;
			} catch (SQLException e) {
				LOGGER.error("sql构建异常: {}", e);
			}
			return null;
		}
	}

}
