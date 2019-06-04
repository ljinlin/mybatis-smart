package com.mingri.mybatissmart;

public interface Constant {
	static final String PARAM_KEY = "ek";
	static final String COND_KEY = "ck";
	public static final String SPACE = " ";

	public static interface SQL {
		static final String WHERE_PRE = " where ";
		static final String ORDER_BY_SQL = " order by ";
		static final String LIMIT = " limit ";
		static final String OFFSET = " offset ";
		static final String FROM = " from ";
		static final String INSERT_INTO = " insert into ";
		static final String SELECT = " select ";
		static final String DELETE = " delete ";
		static final String AS = " as ";
	}
}
