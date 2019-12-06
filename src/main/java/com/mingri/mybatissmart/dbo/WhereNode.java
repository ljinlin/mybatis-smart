package com.mingri.mybatissmart.dbo;

import java.util.List;

import com.mingri.langhuan.cabinet.constant.LogicCmp;
import com.mingri.langhuan.cabinet.constant.NexusCmp;

/**
 * where 条件 节点
 * 
 * @author ljl
 *
 */
public class WhereNode {

	/**
	 * 关系运算符：> < = ...
	 */
	private NexusCmp nexusCmp;

	/**
	 * 逻辑运算符 ：and or
	 */
	private LogicCmp logicCmp;

	/**
	 * 列名称
	 */
	private String columnName;

	/**
	 * 值
	 */
	private Object val;

	/**
	 * 是否Statement值
	 */
	private boolean isStatementVal = false;

	private List<WhereNode> childCond;

	public WhereNode(LogicCmp logicCmp, String columnName, NexusCmp nexusCmp, Object val) {
		this.logicCmp = logicCmp;
		this.nexusCmp = nexusCmp;
		this.columnName = columnName;
		this.val = val;
	}

	public WhereNode(LogicCmp logicCmp, String columnName, NexusCmp nexusCmp, Object val, boolean isStatementVal) {
		this.logicCmp = logicCmp;
		this.nexusCmp = nexusCmp;
		this.columnName = columnName; 
		this.val = val;
		this.isStatementVal = isStatementVal;
	}

	public WhereNode(LogicCmp logicCmp, String columnName, NexusCmp nexusCmp) {
		this.logicCmp = logicCmp;
		this.nexusCmp = nexusCmp;
		this.columnName = columnName;
	}

	public WhereNode(LogicCmp logicCmp, List<WhereNode> childCond) {
		this.logicCmp = logicCmp;
		this.childCond = childCond;
	}

	public NexusCmp getNexusCmp() {
		return nexusCmp;
	}

	public void setNexusCmp(NexusCmp nexusCmp) {
		this.nexusCmp = nexusCmp;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public Object getVal() {
		return val;
	}

	public void setVal(Object val) {
		this.val = val;
	}

	public LogicCmp getLogicCmp() {
		return logicCmp;
	}

	public void setLogicCmp(LogicCmp logicCmp) {
		this.logicCmp = logicCmp;
	}

	public boolean isStatementVal() {
		return isStatementVal;
	}

	public void setStatementVal(boolean isStatementVal) {
		this.isStatementVal = isStatementVal;
	}

	public List<WhereNode> getChildCond() {
		return childCond;
	}

	public void setChildCond(List<WhereNode> childCond) {
		this.childCond = childCond;
	}

}
