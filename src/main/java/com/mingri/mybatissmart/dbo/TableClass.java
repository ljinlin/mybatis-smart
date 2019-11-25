package com.mingri.mybatissmart.dbo;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.jdbc.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.mingri.langhuan.cabinet.algorithm.SequenceGenerate;
import com.mingri.langhuan.cabinet.constant.LogicCmp;
import com.mingri.langhuan.cabinet.constant.NexusCmp;
import com.mingri.langhuan.cabinet.constant.ObjTypeEnum;
import com.mingri.langhuan.cabinet.constant.ValTypeEnum;
import com.mingri.langhuan.cabinet.tool.ClassTool;
import com.mingri.langhuan.cabinet.tool.CollectionTool;
import com.mingri.langhuan.cabinet.tool.StrTool;
import com.mingri.mybatissmart.MybatisSmartException;
import com.mingri.mybatissmart.annotation.ColumnInfo;
import com.mingri.mybatissmart.annotation.TableInfo;
import com.mingri.mybatissmart.barracks.Constant;
import com.mingri.mybatissmart.barracks.DialectEnum;
import com.mingri.mybatissmart.barracks.IdtacticsEnum;
import com.mingri.mybatissmart.barracks.SqlKwd;

public class TableClass {

	private TableInfo tableInfo;
	private Field idField;
	private String idColumnName;
	private Class<?> clazz;
	private LinkedHashMap<String, ColumnField> fieldsMapperMap;// key:columnName
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

	/**
	 * 如果是id字段，并且是默认的id成策略
	 * 
	 * @param field
	 * @param rowObj
	 * @return
	 */
	private String generateIdIfIdFieldAndDftIdtactic(Field field, Object rowObj) {
		String idVal = null;
		if (field.getName().equals(tableInfo.idFieldName()) && tableInfo.idtactics() == IdtacticsEnum.DFT) {
			idVal = SequenceGenerate.nexId(tableInfo.value());
			injectIdVal(field, rowObj, idVal);
		}
		return idVal;
	}

	private void injectIdVal(Field idField, Object rowObj, String idVal) {
		idField.setAccessible(true);
		Class<?> typeClazz = idField.getType();
		try {
			if (typeClazz == String.class) {
				idField.set(rowObj, idVal);
			} else if (typeClazz == Long.class || typeClazz == long.class) {
				idField.set(rowObj, Long.valueOf(idVal));
			} else if (typeClazz == Integer.class || typeClazz == int.class) {
				idField.set(rowObj, Integer.valueOf(idVal));
			} else if (typeClazz == Short.class || typeClazz == short.class) {
				idField.set(rowObj, Short.valueOf(idVal));
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private void buildColumnSql(StringBuilder colmSql, String colmName, Integer index) {
		if (index == null || index == 0) {
			colmSql.append(colmName).append(",");
		}
	}


	@SuppressWarnings("unchecked")
	public String getInsertSql(Object obj) throws Exception {

		StringBuilder sql = new StringBuilder(SqlKwd.INSERT_INTO).append(tableInfo.value());
		StringBuilder colmSql = new StringBuilder();
		StringBuilder valusSql = new StringBuilder(" values(");

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
				colmVal = generateIdIfIdFieldAndDftIdtactic(field, rowObj);
				if (colmVal != null) {
					colmVal = boundSqlVal(colmVal, null, index);
					buildColumnSql(colmSql, colmName, index);
					valusSql.append(colmVal).append(",");
					continue;
				}

				/*
				 * 没配置insertValType 或者insertValType为''
				 */
				Object srcVal = ClassTool.reflexVal(rowObj, field);
				colmVal = TableClass.buildColumnVal(srcVal, en.getValue(), index);
				if (columnInfo == null || columnInfo.insertValType().length == 0) {
					/*
					 * 不插入空字符串和null值、"null"、" "
					 */
					if (StrTool.isNotEmpty(colmVal)) {
						buildColumnSql(colmSql, colmName, index);
						valusSql.append(colmVal).append(",");
					} else if (index != null) {// 批量新增时
						buildColumnSql(colmSql, colmName, index);
						valusSql.append("null").append(",");
					}
				} else {
					List<ObjTypeEnum> otEs = Arrays.asList(columnInfo.insertValType());

					if (otEs.contains(ObjTypeEnum.ALL)) {
						buildColumnSql(colmSql, colmName, index);
						valusSql.append(adornIfEmpty(colmVal)).append(",");
						/*
						 * 不插入空字符串和null值
						 */
					} else if (otEs.contains(ObjTypeEnum.OBJ)) {
						if (StrTool.checkNotEmpty(colmVal)) {
							buildColumnSql(colmSql, colmName, index);
							valusSql.append(colmVal).append(",");
						} else if (index != null) {// 。批量新增时
							buildColumnSql(colmSql, colmName, index);
							valusSql.append("null").append(",");
						}
					}
				}

			}
			valusSql = valusSql.deleteCharAt(valusSql.length() - 1).append("),(");
			if (index != null) {
				index++;
			}
		}
		if (colmSql.length() > 0) {
			sql = sql.append(" (").append(colmSql.deleteCharAt(colmSql.length() - 1).append(")"));
			sql = sql.append(valusSql.delete(valusSql.length() - 2, valusSql.length()));
			return sql.toString();
		}
		return null;
	}

	public String getUpdateByIdSql(Object obj) throws Exception {
		idField.setAccessible(true);
		WhereSql filterSqlBuild = new WhereSql().andEq(idColumnName, idField.get(obj));
		return getUpdateByWhereSql(obj, filterSqlBuild);
	}

	public String getUpdateByWhereSql(Object obj, WhereSql filterSqlBuild) throws Exception {

		ColumnInfo columnInfo = null;

		String colmVal = null;
		Field field = null;
		String where = buildWhere(obj, filterSqlBuild);
		if (StrTool.isEmpty(where)) {
			throw new MybatisSmartException("必须设置where条件");
		}
		StringBuilder sql = new StringBuilder(SqlKwd.UPDATE).append(tableInfo.value()).append(SqlKwd.SET);
		StringBuilder setSb = new StringBuilder();

		for (Map.Entry<String, ColumnField> en : fieldsMapperMap.entrySet()) {
			field = en.getValue().getField();
			if (field.getName().equals(tableInfo.idFieldName())) {
				continue;
			}
			columnInfo = en.getValue().getColumnInfo();
			if (columnInfo != null && !columnInfo.isUpdate()) {
				continue;
			}
			Object srcVal = ClassTool.reflexVal(obj, field);
			colmVal = TableClass.buildColumnVal(srcVal, en.getValue(), null);
			if (columnInfo == null || columnInfo.updateValType().length == 0) {
				if (StrTool.checkNotEmpty(colmVal)) {
					setSb.append(en.getKey()).append("=").append(colmVal).append(",");
				}
			} else {
				List<ObjTypeEnum> otEs = Arrays.asList(columnInfo.updateValType());
				if (otEs.contains(ObjTypeEnum.ALL)) {
					setSb.append(en.getKey()).append("=").append(adornIfEmpty(colmVal)).append(",");
				} else if (otEs.contains(ObjTypeEnum.OBJ)) {
					if (StrTool.checkNotEmpty(colmVal)) {
						setSb.append(en.getKey()).append("=").append(colmVal).append(",");
					}
				}
			}

		}

		if (setSb.length() > 0) {
			sql = sql.append(setSb.substring(0, setSb.length() - 1)).append(where);
			return sql.toString();
		}
		return null;
	}

	public String getSelectByIdSql(Object idV) {
		if (StrTool.checkEmpty(idV)) {
			throw new MybatisSmartException("条件字段" + tableInfo.idFieldName() + "的值不能为null");
		}
		StringBuilder sql = new StringBuilder(SqlKwd.SELECT).append(this.getColumns()).append(SqlKwd.FROM)
				.append(tableInfo.value()).append(SqlKwd.WHERE_PRE).append(idColumnName).append("='" + idV + "'");

		return sql.toString();
	}

	private String buildWhere(Object obj, List<WhereCond> conds) {
		if (conds.isEmpty()) {
			return null;
		}
		final StringBuilder where = new StringBuilder();
		int index = 0;
		for (WhereCond cond : conds) {
			List<WhereCond> childConds = cond.getChildCond();
			if (CollectionTool.notEmpty(childConds)) {
				String childWhere = this.buildWhere(obj, childConds);
				if (StrTool.isNotEmpty(childWhere)) {
					where.append(cond.getLogicCmp().code).append(" ( ").append(childWhere).append(" ) ");
				}
			}
			String columnName = cond.getColumnName();
			Object srcVal = cond.getVal();
			NexusCmp cexusCmp = cond.getNexusCmp();
			ColumnField fim = this.fieldsMapperMap.get(columnName);
			String val = null;
			if (srcVal == null && fim != null) {
				try {
					if (!(obj instanceof Class)) {
						val = TableClass.buildWhereValSql(obj, fim, cexusCmp);
					}
				} catch (Exception exc) {
					LOGGER.error("捕获到异常,打印日志：{}", exc);
				}
			} else if (srcVal != null) {
				if (cond.isSqlVal()) {
					val = cexusCmp.code.concat(Constant.SPACE).concat(srcVal.toString());
				} else {
					val = buildCmpVal(srcVal, null, cexusCmp, null);
				}
			}
			if (val != null) {
				if (index > 0) {
					where.append(cond.getLogicCmp().code);
				}
				where.append(Constant.SPACE).append(columnName).append(Constant.SPACE).append(val)
						.append(Constant.SPACE);
				index++;
			}
		}

		return where.toString();
	}

	private String buildWhere(Object obj, WhereSql filterSqlBuild) {
		StringBuilder where = new StringBuilder();
		if (obj != null && filterSqlBuild != null) {
			String whereCondsSql = this.buildWhere(obj, filterSqlBuild.getConds());
			if (StrTool.isNotEmpty(whereCondsSql)) {
				where.append(whereCondsSql);
			}
		}
		String nativeSqlConds = null;
		if (filterSqlBuild != null) {
			nativeSqlConds = filterSqlBuild.getNativeSqlConds();
		}
		if (StrTool.isNotEmpty(nativeSqlConds)) {
			if (where.length() > 0) {
				where.append(Constant.SPACE).append(nativeSqlConds);
			} else {
				nativeSqlConds = StrTool.toString(nativeSqlConds);
				nativeSqlConds = StrTool.trimStr(StrTool.trimStr(nativeSqlConds.trim(), LogicCmp.OR.code),
						LogicCmp.OR.code);
				nativeSqlConds = StrTool.trimStr(StrTool.trimStr(nativeSqlConds.trim(), LogicCmp.AND.code),
						LogicCmp.AND.code);
				where.append(nativeSqlConds);
			}
		}
		if (where.length() > 0) {
			where.insert(0, SqlKwd.WHERE_PRE);
		}
		return where.toString();
	}

	public String getSelectByWhereSql(Object obj, WhereSql filterSqlBuild) {
		String where = buildWhere(obj, filterSqlBuild);

		String orderBy = filterSqlBuild == null ? StrTool.EMPTY : filterSqlBuild.getOrderBy();
		StringBuilder sql = new StringBuilder(SqlKwd.SELECT).append(this.getColumns()).append(SqlKwd.FROM)
				.append(tableInfo.value()).append(where);

		if (StrTool.checkNotEmpty(orderBy)) {
			sql.append(orderBy);
		}
		Integer limit = filterSqlBuild == null ? null : filterSqlBuild.getLimit();
		if (limit != null) {
			switch (dialect) {
			case MYSQL:
				sql.append(SqlKwd.LIMIT).append(limit).append(SqlKwd.OFFSET).append(filterSqlBuild.getOffset());
				break;
			case SQLSERVER:
				sql.append(SqlKwd.OFFSET).append(filterSqlBuild.getOffset()).append(" rows fetch next ").append(limit)
						.append(" rows only ");
				break;
			default:
				break;
			}
		}

		return sql.toString();
	}

	public String getCountByWhereSql(Object obj, WhereSql filterSqlBuild) {
		String where = buildWhere(obj, filterSqlBuild);
		return new StringBuilder(" select count(*) from ").append(tableInfo.value()).append(where).toString();
	}

	public String getDeleteByWhereSql(Object obj, WhereSql filterSqlBuild) {
		String where = buildWhere(obj, filterSqlBuild);
		if (StrTool.checkNotEmpty(where)) {
			return new StringBuilder(SqlKwd.DELETE).append(SqlKwd.FROM).append(tableInfo.value()).append(where)
					.toString();
		}
		return null;
	}

	public String getDeleteByIdSql(Object idV) {
		if (StrTool.checkEmpty(idV)) {
			throw new MybatisSmartException("条件字段" + tableInfo.idFieldName() + "的值不能为null");
		}
		return new StringBuilder(SqlKwd.DELETE).append(SqlKwd.FROM).append(tableInfo.value()).append(SqlKwd.WHERE_PRE)
				.append(idColumnName).append("='" + idV + "'").toString();
	}

	private static String adornIfEmpty(String val) {
		return val == null ? ValTypeEnum.NULL.code : (val.trim().length() == 0 ? "'" + val + "'" : val);
	}

	/**
	 * 装饰sql值
	 * 
	 * @param srcObj 原始值所属对象
	 * @param fmi    对应的字段映射信息
	 * @param index  批量插入时使用的索引
	 * @return
	 * @throws IllegalAccessException
	 */
	private static String buildColumnVal(Object srcVal, ColumnField fmi, Integer index) throws IllegalAccessException {
		if (srcVal == null) {
			return null;
		}
		String valSql = null;
		Field fi = fmi.getField();
		Class<?> fieldType = fi.getType();
		ColumnInfo columInfo = fmi.getColumnInfo();
		if (fieldType == Date.class && (columInfo != null && columInfo.dateFormart().length() > 0)) {
			return "'" + new SimpleDateFormat(columInfo.dateFormart()).format((Date) srcVal) + "'";
		}
		if (fieldType == String.class || fieldType == Date.class) {
			valSql = boundSqlVal(srcVal, fi.getName(), index);
		} else {
			valSql = srcVal.toString();
		}

		return valSql;
	}

	/**
	 * 装饰sql值
	 * 
	 * @param srcObj 原始值所属对象
	 * @param fmi    对应的字段映射信息
	 * @param index  批量插入时使用的索引
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private static String buildWhereValSql(Object srcObj, ColumnField fmi, NexusCmp cexusCmp)
			throws IllegalArgumentException, IllegalAccessException {
		Object srcVal = ClassTool.reflexVal(srcObj, fmi.getField());
		if (srcVal == null) {
			return null;
		}
		if (cexusCmp != null) {
			return buildCmpVal(srcVal, fmi, cexusCmp, null);
		}
		return buildColumnVal(srcVal, fmi, null);
	}

	/**
	 * 包裹值
	 * 
	 * @param srcVal    未经过任何处理的原始值
	 * @param fieldName 构建wher条件时使用该字段
	 * @param index     inset集合时使用
	 * @return
	 */
	private static String boundSqlVal(Object srcVal, String fieldName, Integer index) {
		String srcValStr = srcVal.toString();
		if (fieldName == null) {
			if (srcVal.getClass() == String.class) {
				srcValStr = "'".concat(srcValStr).concat("'");
			} else if (srcVal == ValTypeEnum.BLANK) {
				srcValStr = ValTypeEnum.BLANK.code;
			}
			// ......其他数据类型，暂时不支持处理，只能用户在外面处理
		} else {
			String indexStr = index == null ? "." : "[" + index + "].";
			srcValStr = "#{".concat(Constant.PARAM_KEY).concat(indexStr).concat(fieldName).concat("}");
		}
		return srcValStr;
	}

	/**
	 * 构建计算值
	 * 
	 * @param srcVal
	 * @param fmi      可以为null，无需#{} 解析，则必传此参数
	 * @param cexusCmp
	 * @return
	 */
	private static String buildCmpVal(Object srcVal, ColumnField fmi, NexusCmp cexusCmp, Integer index) {
		if (srcVal == null) {
			return null;
		}
		String sqlVal = srcVal.toString();
		int len = sqlVal.length();
		if (len == 0) {
			return null;
		}
		Class<?> fiClass = srcVal.getClass();
		String fieldName = fmi == null ? null : fmi.getField().getName();

		if (cexusCmp == NexusCmp.like_lr) {
			sqlVal = boundSqlVal(srcVal, fieldName, index);
			sqlVal = cexusCmp.code.concat(" concat('%',").concat(sqlVal).concat(",'%')");
		} else if (cexusCmp == NexusCmp.like_l) {
			sqlVal = boundSqlVal(srcVal, fieldName, index);
			sqlVal = cexusCmp.code.concat(" concat('%',").concat(sqlVal).concat(")");
		} else if (cexusCmp == NexusCmp.like_r) {
			sqlVal = boundSqlVal(srcVal, fieldName, index);
			sqlVal = cexusCmp.code.concat(" concat(").concat(sqlVal).concat(",'%')");
		} else if (sqlVal.equalsIgnoreCase(ValTypeEnum.NULL.code)) {
			sqlVal = cexusCmp.code.concat(Constant.SPACE).concat(sqlVal);
		} else if (srcVal == ValTypeEnum.BLANK) {
			sqlVal = cexusCmp.code.concat(Constant.SPACE).concat("''");
		} else if (cexusCmp == NexusCmp.in || cexusCmp == NexusCmp.not_in) {
			if (srcVal.getClass().isArray()) {
				srcVal = JSONArray.parseArray(JSONArray.toJSONString(srcVal), Object.class);
			}
			if (srcVal instanceof Collection) {
				Collection<?> clt = (Collection<?>) srcVal;
				StringBuilder inVal = new StringBuilder();
				clt.forEach(e -> {
					if (e instanceof String) {
						inVal.append("'").append(e.toString()).append("'").append(",");
					} else {
						inVal.append(e.toString()).append(",");
					}
				});
				if (inVal.length() > 0) {
					sqlVal = inVal.substring(0, inVal.length() - 1);
				} else {
					return null;
				}
			}

			sqlVal = cexusCmp.code.concat(" (").concat(sqlVal).concat(")");
		} else if (fiClass == String.class || srcVal instanceof Date) {
			sqlVal = boundSqlVal(srcVal, fieldName, index);
			sqlVal = cexusCmp.code.concat(Constant.SPACE).concat(sqlVal);
		} else {
			sqlVal = boundSqlVal(srcVal, fieldName, index);
			sqlVal = cexusCmp.code.concat(sqlVal);
		}
		return sqlVal;
	}

	private String getColumns() {
		StringBuilder cls = new StringBuilder();
		String clsStr = null;
		this.fieldsMapperMap.forEach((k, v) -> {
			cls.append(k).append(SqlKwd.AS).append(v.getField().getName()).append(",");
			// cls.append(k).append(",");
		});
		if (cls.length() > 0) {
			clsStr = cls.substring(0, cls.length() - 1);
		}
		return clsStr;
	}

}
