package com.mingri.mybatissmart.dbo;

import java.lang.reflect.Field;

import com.mingri.mybatissmart.annotation.ColumnInfo;

public class ColumnField {

	private Field field;
	private ColumnInfo columnInfo;

	public ColumnField(Field field, ColumnInfo columnInfo) {
		super();
		this.field = field;
		this.columnInfo = columnInfo;
	}

	public ColumnField() {
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public ColumnInfo getColumnInfo() {
		return columnInfo;
	}

	public void setColumnInfo(ColumnInfo columnInfo) {
		this.columnInfo = columnInfo;
	}
}
