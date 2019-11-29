package com.mingri.mybatissmart.dbo;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mingri.langhuan.cabinet.constant.ObjTypeEnum;
import com.mingri.langhuan.cabinet.constant.ValTypeEnum;
import com.mingri.langhuan.cabinet.tool.ClassTool;
import com.mingri.langhuan.cabinet.tool.StrTool;
import com.mingri.mybatissmart.MybatisSmartException;
import com.mingri.mybatissmart.annotation.ColumnInfo;
import com.mingri.mybatissmart.annotation.TableInfo;
import com.mingri.mybatissmart.barracks.DialectEnum;
import com.mingri.mybatissmart.dbo.SQL.WhereSql;

public class TableClass {

	private TableInfo tableInfo;
	private Field idField;
	private String idColumnName;
	private Class<?> clazz;
	LinkedHashMap<String, ColumnField> fieldsMapperMap;// key:columnName
	private DialectEnum dialect;

	private static final Logger LOGGER = LoggerFactory.getLogger(TableClass.class);

	public Field getIdField() {
		return idField;
	}

	public void setIdField(Field idField) {
		this.idField = idField;
	}

	public TableInfo getTableInfo() {
		return tableInfo;
	}

	public void setTableInfo(TableInfo tableInfo) {
		this.tableInfo = tableInfo;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public LinkedHashMap<String, ColumnField> getFieldsMapperMap() {
		return fieldsMapperMap;
	}

	public void setFieldsMapperMap(LinkedHashMap<String, ColumnField> fieldsMapperMap) {
		this.fieldsMapperMap = fieldsMapperMap;
	}

	public String getIdColumnName() {
		return idColumnName;
	}

	public void setIdColumnName(String idColumnName) {
		this.idColumnName = idColumnName;
	}

	public DialectEnum getDialect() {
		return dialect;
	}

	public void setDialect(DialectEnum dialect) {
		this.dialect = dialect;
	}

	@SuppressWarnings("unchecked")
	public String getInsertSql(Object obj) throws Exception {
		SQL.Insert insert = SQL.insertInto(tableInfo.value());

		Collection<Object> dataList = obj instanceof Collection ? dataList = (Collection<Object>) obj
				: Collections.singletonList(obj);
		Integer index = dataList.size() > 1 ? 0 : null;
		ColumnInfo columnInfo = null;
		String colmVal = null;
		String colmName = null;
		Field field = null;
		for (Object rowObj : dataList) {
			for (Map.Entry<String, ColumnField> en : fieldsMapperMap.entrySet()) {
				columnInfo = en.getValue().getColumnInfo();
				colmName = en.getKey();
				if (columnInfo != null && columnInfo.isInsert() == false) {
					continue;
				}
				field = en.getValue().getField();

				/*
				 * id处理
				 */
				colmVal = StatementTool.generateIdIfIdFieldAndDftIdtactic(field, rowObj, tableInfo);
				if (colmVal != null) {
					insert.intoColumn(colmName, index);
					insert.intoValue("'" + colmVal + "'");
					continue;
				}

				Object srcVal = ClassTool.reflexVal(rowObj, field);

				/*
				 * 没配置insertValType
				 */
				colmVal = StatementTool.buildColumnVal(srcVal, en.getValue(), index);
				if (columnInfo == null || columnInfo.insertValType().length == 0) {
					/*
					 * 不插入空字符串和null值、"null"、" "
					 */
					if (StrTool.checkNotEmpty(srcVal)) {
						insert.intoColumn(colmName, index);
						insert.intoValue(colmVal);
					} else if (index != null) {// 批量新增时
						insert.intoColumn(colmName, index);
						insert.intoValue(ValTypeEnum.NULL.code);
					}
				} else {
					List<ObjTypeEnum> otEs = Arrays.asList(columnInfo.insertValType());

					if (otEs.contains(ObjTypeEnum.ALL)) {
						insert.intoColumn(colmName, index);
						insert.intoValue(StatementTool.toSqlEmptyIfStringEmpty(colmVal));

						/*
						 * 不插入空字符串和null值
						 */
					} else if (otEs.contains(ObjTypeEnum.OBJ)) {
						if (StrTool.checkNotEmpty(srcVal)) {
							insert.intoColumn(colmName, index);
							insert.intoValue(colmVal);
						} else if (index != null) {// 。批量新增时
							insert.intoColumn(colmName, index);
							insert.intoValue(ValTypeEnum.NULL.code);
						}
					}
				}

			}
			insert.intoRowEnd();
			if (index != null) {
				index++;
			}
		}
		return insert.build().toString();
	}

	public String getUpdateByIdSql(Object obj) throws Exception {
		Where where = new Where().andEq(idColumnName, ClassTool.reflexVal(obj, idField));
		return getUpdateByWhereSql(obj, where);
	}

	public String getUpdateByWhereSql(Object obj, Where where) throws Exception {
		ColumnInfo columnInfo = null;
		String colmVal = null;
		Field field = null;
		WhereSql whereSql = StatementTool.buildWhere(obj, where, this);
		if (whereSql == null || whereSql.isEmpty()) {
			throw new MybatisSmartException("必须设置where条件");
		}
		SQL.Update updateSql = SQL.update(tableInfo.value());
		String column = null;
		for (Map.Entry<String, ColumnField> en : fieldsMapperMap.entrySet()) {
			field = en.getValue().getField();
			if (field.getName().equals(tableInfo.idFieldName())) {
				continue;
			}
			columnInfo = en.getValue().getColumnInfo();
			if (columnInfo != null && !columnInfo.isUpdate()) {
				continue;
			}
			column = en.getKey();
			Object srcVal = ClassTool.reflexVal(obj, field);
			colmVal = StatementTool.buildColumnVal(srcVal, en.getValue(), null);
			if (columnInfo == null || columnInfo.updateValType().length == 0) {
				if (StrTool.checkNotEmpty(srcVal)) {
					updateSql.set(column, colmVal);
				}
			} else {
				List<ObjTypeEnum> otEs = Arrays.asList(columnInfo.updateValType());
				if (otEs.contains(ObjTypeEnum.ALL)) {
					updateSql.set(column, StatementTool.toSqlEmptyIfStringEmpty(colmVal));
				} else if (otEs.contains(ObjTypeEnum.OBJ)) {
					if (StrTool.checkNotEmpty(srcVal)) {
						updateSql.set(column, colmVal);
					}
				}
			}
		}
		return updateSql.setWhere(whereSql).build().toString();
	}

	public String getSelectByIdSql(Object idV) {
		SQL sql = SQL.select(StatementTool.getColumns(this), tableInfo.value())
				.where(SQL.where().add( " " + idColumnName + "='" + idV + "'")).build();
		return sql.toString();
	}

	public String getSelectByWhereSql(Object obj, Where where) throws IllegalArgumentException, IllegalAccessException {
		WhereSql whereSql = StatementTool.buildWhere(obj, where, this);
		return SQL.select(StatementTool.getColumns(this), tableInfo.value()).where(whereSql).orderBy(where.getOrderBy())
				.limit(where.getLimit(), where.getOffset(), dialect).build().toString();
	}

	public String getCountByWhereSql(Object obj, Where where) throws IllegalArgumentException, IllegalAccessException {
		WhereSql whereSql = StatementTool.buildWhere(obj, where, this);
		SQL sql = SQL.select(" count(*) ", tableInfo.value()).where(whereSql).build();
		return sql.toString();
	}

	public String getDeleteByWhereSql(Object obj, Where where) throws IllegalArgumentException, IllegalAccessException {
		WhereSql whereSql = StatementTool.buildWhere(obj, where, this);
		return SQL.delete(tableInfo.value()).setWhere(whereSql).build().toString();
	}

	public String getDeleteByIdSql(Object idV) {
		return SQL.delete(tableInfo.value()).setWhere(SQL.where().add( " " + idColumnName + "='" + idV + "'")).build().toString();
	}
}
