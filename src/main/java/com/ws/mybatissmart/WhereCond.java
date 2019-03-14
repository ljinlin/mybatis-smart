package com.ws.mybatissmart;

import com.ws.commons.constant.LogicCmp;
import com.ws.commons.constant.NexusCmp;


public class WhereCond {

	private NexusCmp nexusCmp;
	private LogicCmp logicCmp;
	private String columnName;
	private String valName;
	private Object val;
	private boolean isSqlVal=false;
	public WhereCond(LogicCmp logicCmp,String columnName,NexusCmp nexusCmp, Object val) {
		this.logicCmp = logicCmp;
		this.nexusCmp = nexusCmp;
		this.columnName = columnName;
		this.val = val;
	}
	public WhereCond(LogicCmp logicCmp,String columnName,NexusCmp nexusCmp, Object val,boolean isSqlVal) {
		this.logicCmp = logicCmp;
		this.nexusCmp = nexusCmp;
		this.columnName = columnName;
		this.val = val;
		this.isSqlVal = isSqlVal;
	}
	public WhereCond(LogicCmp logicCmp,String columnName,NexusCmp nexusCmp) {
		this.logicCmp = logicCmp;
		this.nexusCmp = nexusCmp;
		this.columnName = columnName;
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
	public String getValName() {
		return valName;
	}
	public void setValName(String valName) {
		this.valName = valName;
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
	public boolean isSqlVal() {
		return isSqlVal;
	}
	public void setSqlVal(boolean isSqlVal) {
		this.isSqlVal = isSqlVal;
	}

	
}
