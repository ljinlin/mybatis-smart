package com.ws.mybatissmart;

import com.ws.commons.constant.CmpChar;


public class WhereCond {

	private CmpChar cmpChar;
	private String columnName;
	private String valName;
	private Object val;
	public WhereCond(String columnName,CmpChar cmpChar, Object val) {
		super();
		this.cmpChar = cmpChar;
		this.columnName = columnName;
		this.val = val;
	}
	public WhereCond(String columnName,CmpChar cmpChar) {
		super();
		this.cmpChar = cmpChar;
		this.columnName = columnName;
	}
	public CmpChar getCmpChar() {
		return cmpChar;
	}
	public void setCmpChar(CmpChar cmpChar) {
		this.cmpChar = cmpChar;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getvalColumn() {
		return valName;
	}
	public void setvalColumn(String valName) {
		this.valName = valName;
	}
	public Object getVal() {
		return val;
	}
	public void setVal(Object val) {
		this.val = val;
	}	
	
}
