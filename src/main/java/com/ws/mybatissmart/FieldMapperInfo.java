package com.ws.mybatissmart;

import java.lang.reflect.Field;

import com.ws.mybatissmart.annotation.ColumnInfo;

public class FieldMapperInfo {

	private Field field;
	private ColumnInfo columnInfo;

	public FieldMapperInfo(Field field, ColumnInfo columnInfo) {
		super();
		this.field = field;
		this.columnInfo = columnInfo;
	}

	public FieldMapperInfo() {
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
