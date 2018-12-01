package com.ws.mybatissmart;

import java.util.LinkedHashMap;

import com.ws.commons.constant.CmpChar;

public class FilterSqlBuild {

	private String orderBy = "";
	private String limit = "";
	private LinkedHashMap<String, CmpChar> conds = new LinkedHashMap<String, CmpChar>();

	public FilterSqlBuild(String columnName, CmpChar cmpChar) {
		add(columnName, cmpChar);
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy, boolean isAsc) {
		this.orderBy = orderBy;
		if (orderBy != null && orderBy.length() > 0 && !isAsc) {
			orderBy = orderBy.concat(" desc");
		}
	}

	public void setConds(LinkedHashMap<String, CmpChar> conds) {
		this.conds = conds;
	}

	public FilterSqlBuild add(String columnName, CmpChar cmpChar) {
		conds.put(columnName, cmpChar);
		return this;
	}

	public FilterSqlBuild addEq(String columnName) {
		conds.put(columnName, CmpChar.eq);
		return this;
	}

	public FilterSqlBuild addLt(String columnName) {
		conds.put(columnName, CmpChar.lt);
		return this;
	}

	public FilterSqlBuild addGt(String columnName) {
		conds.put(columnName, CmpChar.gt);
		return this;
	}

	public FilterSqlBuild addLtEq(String columnName) {
		conds.put(columnName, CmpChar.lt_eq);
		return this;
	}

	public FilterSqlBuild addGtEq(String columnName) {
		conds.put(columnName, CmpChar.gt_eq);
		return this;
	}

	public FilterSqlBuild addLtGT(String columnName) {
		conds.put(columnName, CmpChar.lt_gt);
		return this;
	}

	public FilterSqlBuild addLike(String columnName) {
		conds.put(columnName, CmpChar.like);
		return this;
	}

	public CmpChar get(String columnName) {
		return conds.get(columnName);
	}

	public LinkedHashMap<String, CmpChar> getConds() {
		return conds;
	}

}
