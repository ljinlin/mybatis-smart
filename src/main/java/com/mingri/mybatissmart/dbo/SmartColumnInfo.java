package com.mingri.mybatissmart.dbo;

import java.lang.reflect.Field;

import com.mingri.mybatissmart.annotation.SmartColumn;

public class SmartColumnInfo {

	private Field field;
	private SmartColumn smartColumn;

	public SmartColumnInfo(Field field, SmartColumn columnInfo) {
		super();
		this.field = field;
		this.smartColumn = columnInfo;
	}

	public SmartColumnInfo() {
	} 

	public Field getField() {
		return field;
	}

	
	public void setField(Field field) {
		this.field = field;
	}

	public SmartColumn getSmartColumn() {
		return smartColumn;
	}

	public void setSmartColumn(SmartColumn columnInfo) {
		this.smartColumn = columnInfo;
	}
}
