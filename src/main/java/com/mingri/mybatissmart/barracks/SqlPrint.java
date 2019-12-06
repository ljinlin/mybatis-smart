package com.mingri.mybatissmart.barracks;

import org.slf4j.Logger;

public class SqlPrint {

	private boolean isPrint = true;

	private static SqlPrint INSTANCE;

	public SqlPrint() {
		INSTANCE = this;
	}

	public boolean isPrint() {
		return isPrint;
	}

	public void setPrint(boolean isPrint) {
		this.isPrint = isPrint;
	}

	public static SqlPrint instance() {
		if (SqlPrint.INSTANCE == null) {
			synchronized (SqlPrint.class) {
				if (SqlPrint.INSTANCE == null) {
					SqlPrint.INSTANCE = new SqlPrint();
				}
			}
		}
		return SqlPrint.INSTANCE;
	}

	public void print(Logger logger, String sql) {
		if (isPrint) {
			logger.info(sql);
		}
	}
}
