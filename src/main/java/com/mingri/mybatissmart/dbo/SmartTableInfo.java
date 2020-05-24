package com.mingri.mybatissmart.dbo;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mingri.langhuan.cabinet.constant.ObjTypeEnum;
import com.mingri.langhuan.cabinet.constant.ValTypeEnum;
import com.mingri.langhuan.cabinet.tool.ClassTool;
import com.mingri.langhuan.cabinet.tool.StrTool;
import com.mingri.mybatissmart.MybatisSmartException;
import com.mingri.mybatissmart.annotation.SmartColumn;
import com.mingri.mybatissmart.annotation.SmartTable;
import com.mingri.mybatissmart.barracks.DialectEnum;
import com.mingri.mybatissmart.dbo.MapperSql.Select;
import com.mingri.mybatissmart.dbo.MapperSql.WhereSql;

public class SmartTableInfo {

	private SmartTable smartTable;
	private Field idField;
	private String idColumnName;
	private Class<?> clazz;
	LinkedHashMap<String, SmartColumnInfo> smartColumnInfoMap;// key:columnName
	private DialectEnum dialect;
	private SqlSessionFactory sqlSessionFactory;
	private String[] keyProperties;

	private static final Logger LOGGER = LoggerFactory.getLogger(SmartTable.class);

	public Field getIdField() {
		return idField;
	}

	public void setIdField(Field idField) {
		this.idField = idField;
	}

	public SmartTable getSmartTable() {
		return smartTable;
	}

	public void setSmartTable(SmartTable smartTable) {
		this.smartTable = smartTable;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public LinkedHashMap<String, SmartColumnInfo> getSmartColumnInfoMap() {
		return smartColumnInfoMap;
	}

	public void setSmartColumnInfoMap(LinkedHashMap<String, SmartColumnInfo> smartColumnInfoMap) {
		this.smartColumnInfoMap = smartColumnInfoMap;
	}

	public String getIdColumnName() {
		return idColumnName;
	}

	public void setIdColumnName(String idColumnName) {
		this.idColumnName = idColumnName;
	}

	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public DialectEnum getDialect() {
		return dialect;
	}

	public void setDialect(DialectEnum dialect) {
		this.dialect = dialect;
	}

	@SuppressWarnings("unchecked")
	public String getInsertSql(Object obj) throws Exception {
		MapperSql.Insert insert = MapperSql.insertInto(smartTable.value());

		Collection<Object> dataList = null;
		Integer index = null;
		if (obj instanceof Collection) {
			index = 0;
			dataList = (Collection<Object>) obj;
		} else {
			dataList = Collections.singletonList(obj);
		}
		SmartColumn columnInfo = null;
		String colmVal = null;
		String colmName = null;
		Field field = null;
		for (Object rowObj : dataList) {
			for (Map.Entry<String, SmartColumnInfo> en : smartColumnInfoMap.entrySet()) {
				columnInfo = en.getValue().getSmartColumn();
				colmName = en.getKey();
				if (columnInfo != null && columnInfo.isInsert() == false) {
					continue;
				}
				field = en.getValue().getField();

				/*
				 * id处理
				 */
				colmVal = MapperSqlTool.generateIdIfIdFieldAndDftIdtactic(field, rowObj, smartTable);
				if (colmVal != null) {
					insert.intoColumn(colmName, index);
					insert.intoValue("'" + colmVal + "'");
					continue;
				}

				Object srcVal = ClassTool.reflexVal(rowObj, field);

				/*
				 * 没配置insertValType
				 */
				colmVal = MapperSqlTool.buildColumnVal(srcVal, en.getValue(), index);
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
						insert.intoValue(MapperSqlTool.toSqlEmptyIfStringEmpty(colmVal));

						/*
						 * 不插入空字符串和null值
						 */
					} else if (otEs.contains(ObjTypeEnum.OBJ)) {
						if (StrTool.checkNotEmpty(srcVal)) {
							insert.intoColumn(colmName, index);
							insert.intoValue(colmVal);
						} else if (index != null) {// 批量新增时
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


	public String getUpdateBySetSAndWhereSql(SetSql sets, Where where) throws Exception {
		WhereSql whereSql = MapperSqlTool.buildWhere(sets, where, this);
		if (whereSql == null || whereSql.isEmpty()) {
			throw new MybatisSmartException("必须设置where条件");
		}
		MapperSql.Update updateSql = MapperSql.update(smartTable.value(),sets);
		return updateSql.setWhere(whereSql).build().toString();
	}
	
	public String getUpdateByWhereSql(Object obj, Where where) throws Exception {
		SmartColumn columnInfo = null;
		String colmVal = null;
		Field field = null;
		WhereSql whereSql = MapperSqlTool.buildWhere(obj, where, this);
		if (whereSql == null || whereSql.isEmpty()) {
			throw new MybatisSmartException("必须设置where条件");
		}
		MapperSql.Update updateSql = MapperSql.update(smartTable.value());
		String column = null;
		for (Map.Entry<String, SmartColumnInfo> en : smartColumnInfoMap.entrySet()) {
			field = en.getValue().getField();
			if (field.getName().equals(smartTable.idFieldName())) {
				continue;
			}
			columnInfo = en.getValue().getSmartColumn();
			if (columnInfo != null && !columnInfo.isUpdate()) {
				continue;
			}
			column = en.getKey();
			Object srcVal = ClassTool.reflexVal(obj, field);
			colmVal = MapperSqlTool.buildColumnVal(srcVal, en.getValue(), null);
			if (columnInfo == null || columnInfo.updateValType().length == 0) {
				if (StrTool.checkNotEmpty(srcVal)) {
					updateSql.set(column, colmVal);
				}
			} else {
				List<ObjTypeEnum> otEs = Arrays.asList(columnInfo.updateValType());
				if (otEs.contains(ObjTypeEnum.ALL)) {
					updateSql.set(column, MapperSqlTool.toSqlEmptyIfStringEmpty(colmVal));
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
		MapperSql sql = MapperSql.select(MapperSqlTool.getColumns(this), smartTable.value())
				.where(MapperSql.where().add(" " + idColumnName + "='" + idV + "'")).build();
		return sql.toString();
	}

	public String getSelectByWhereSql(Object obj, Where where) throws IllegalArgumentException, IllegalAccessException {
		WhereSql whereSql = MapperSqlTool.buildWhere(obj, where, this);
		Select selectSql = MapperSql.select(MapperSqlTool.getColumns(this), smartTable.value()).where(whereSql);
		if (where != null) {
			selectSql.orderBy(where.getOrderBy()).limit(where.getLimit(), where.getOffset(), dialect);
		}
		return selectSql.build().toString();
	}

	public String getCountByWhereSql(Object obj, Where where) throws IllegalArgumentException, IllegalAccessException {
		WhereSql whereSql = MapperSqlTool.buildWhere(obj, where, this);
		MapperSql sql = MapperSql.select(" count(*) ", smartTable.value()).where(whereSql).build();
		return sql.toString();
	}

	public String getDeleteByWhereSql(Object obj, Where where) throws IllegalArgumentException, IllegalAccessException {
		WhereSql whereSql = MapperSqlTool.buildWhere(obj, where, this);
		return MapperSql.delete(smartTable.value()).setWhere(whereSql).build().toString();
	}

	public String getDeleteByIdSql(Object idV) {
		return MapperSql.delete(smartTable.value())
				.setWhere(MapperSql.where().add(" " + idColumnName + "='" + idV + "'")).build().toString();
	}

	public static class Builder {

		private SmartTableInfo smartTableInfo;

		public Builder(Class<?> smartTableClazz, SqlSessionFactory sqlSessionFactory, DialectEnum dialect) {
			smartTableInfo = new SmartTableInfo();
			SmartTable smartTable = smartTableClazz.getAnnotation(SmartTable.class);
			try {
				validSmartTable(smartTable);
				Field idField = ClassTool.searchDecararedField(smartTableClazz, smartTable.idFieldName());
				if (idField == null) {
					throw new MybatisSmartException(
							StrTool.concat(smartTableClazz.getCanonicalName(), "没有定义字段：", smartTable.idFieldName())
									.toString());
				}
				smartTableInfo.setIdField(idField);
			} catch (Exception e) {
				LOGGER.error("", e);
				throw new MybatisSmartException(StrTool.concat(smartTableClazz.getCanonicalName(), " @",
						SmartTable.class.getSimpleName(), " 配置有误：", e.getMessage()).toString());
			}
			smartTableInfo.setSmartTable(smartTable);
			smartTableInfo.setClazz(smartTableClazz);
			smartTableInfo.setSqlSessionFactory(sqlSessionFactory);
			smartTableInfo.setDialect(dialect);
		}

		public SmartTableInfo builder(LinkedHashMap<String, SmartColumnInfo> smartColumnInfoMap) {
			smartTableInfo.setSmartColumnInfoMap(smartColumnInfoMap);
			for (Map.Entry<String, SmartColumnInfo> entry : smartColumnInfoMap.entrySet()) {
				if (entry.getValue().getField().equals(smartTableInfo.getIdField())) {
					smartTableInfo.setIdColumnName(entry.getKey());
					break;
				}
			}
			if (StrTool.isEmpty(smartTableInfo.getIdColumnName())) {
				throw new MybatisSmartException(StrTool.concat(smartTableInfo.clazz.getCanonicalName(), " @",
						SmartTable.class.getSimpleName(), " 配置有误：默认配置idFieldName值是\"id\",", "表中没有\"id\"列，请正确配置idFieldName").toString());
			}
			return smartTableInfo;
		}

		private static void validSmartTable(SmartTable smartTable) {
			String idFieldName = smartTable.idFieldName();
			if (smartTable.value().length() == 0) {
				throw new MybatisSmartException("没有配置映射表");
			}

			if (idFieldName.length() == 0) {
				throw new MybatisSmartException("没有配置唯一字段");
			}
		}

	}

	private static final Object MODIFYKEYPROPERTIES_LOCK=new Object();
	public void modifyKeyProperties(MappedStatement ms) {
		if (keyProperties != null) {
			return;
		}
		final String[] keyProperties2 = ms.getKeyProperties();
 		if (keyProperties2 == null) {
			LOGGER.warn("keyProperties is  null");
			return;
		}
		int len = keyProperties2.length;
		if (len == 0) {
			LOGGER.warn("keyProperties is  empty");
		}
		len = len == 0 ? 1 : len;
		synchronized (MODIFYKEYPROPERTIES_LOCK) {
			if (keyProperties != null) {
				return;
			}
			keyProperties = new String[len];
			for (int i = 0; i < keyProperties.length; i++) {
				keyProperties[i] = this.idField.getName();
				keyProperties2[i] = keyProperties[i];
			}

		}
	}
}
