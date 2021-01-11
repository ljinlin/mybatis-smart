package com.mingri.mybatissmart.dbo;

import java.util.LinkedHashMap;
import java.util.Map;

public class SetSql {

	// 该字段名字不能随便修改，有#{}中使用
	private Map<String, Object> sets = new LinkedHashMap<>();
	private boolean setEmpty = false;

	Map<String, Object> getSets() {
		return sets;
	}

	private SetSql() {
	}

	boolean isSetEmpty() {
		return setEmpty;
	}

	public static Builder builder() {
		Builder b=new Builder();
		return b;
	}
	public static Builder builder(String column, Object value) {
		Builder b=new Builder(column,value);
		return b;
	}

	public static class Builder {

		private SetSql setSql;

		public  Builder() {
			setSql = new SetSql();
		}

		public  Builder(String column, Object value) {
			setSql = new SetSql();
			set(column, value);
		}
		
		/**
		 * 当值为null,""时，是否也set
		 * 
		 * @param isSetEmpty 当值为null,""时，是否也set
		 * @return Builder对象
		 */
		public Builder setEmpty(boolean isSetEmpty) {
			setSql.setEmpty = isSetEmpty;
			return this;
		}

		public Builder set(String column, Object value) {
			setSql.sets.put(column, value);
			return this;
		}

		public SetSql build() {
			return setSql;
		}

	}

}
