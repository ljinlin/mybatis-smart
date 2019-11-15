package com.mingri.mybatissmart.provider;

import java.util.List;

import org.apache.ibatis.binding.MapperMethod.ParamMap;

import com.mingri.mybatissmart.MybatisSmartContext;
import com.mingri.mybatissmart.barracks.Constant;
import com.mingri.mybatissmart.dbo.TableClass;

public class SqlBuildParam {

	private Class<?> clazz;
	private Object param;
	private TableClass cmi;

	public Class<?> getClazz() {
		return clazz;
	}

	private void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Object getParam() {
		return param;
	}

	public void setParam(Object param) {
		this.param = param;
	}

	public TableClass getCmi() {
		return cmi;
	}

	public void setCmi(TableClass cmi) {
		this.cmi = cmi;
	}

	private SqlBuildParam() {
	}

	public static class Builder {
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
		 */
		private void construct(SqlBuildParam paramWrapper) {
			Object param = null;
			if (providerParam instanceof ParamMap) {
				param = ((ParamMap<?>) providerParam).get(Constant.PARAM_KEY);
			} else {
				param = providerParam;
			}
			if (this.clazz == null) {
				if (providerParam instanceof Class) {
					this.clazz = ((Class<?>) providerParam);
				} else {
					if (param instanceof List) {
						this.clazz = ((List<?>) param).get(0).getClass();
					} else {
						this.clazz = param.getClass();
					}
				}
			}
			TableClass cmi = MybatisSmartContext.getClassMapperInfo(this.clazz);
			paramWrapper.setClazz(this.clazz);
			paramWrapper.setCmi(cmi);
			paramWrapper.setParam(param);
		}

		public Builder(Object providerParam) {
			this.providerParam = providerParam;
		}

		public SqlBuildParam build() {
			SqlBuildParam param = new SqlBuildParam();
			construct(new SqlBuildParam());
			return param;
		}
	}

}
