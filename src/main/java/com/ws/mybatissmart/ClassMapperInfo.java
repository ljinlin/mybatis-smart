package com.ws.mybatissmart;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ws.commons.algorithm.SequenceGenerate;
import com.ws.commons.constant.NexusCmp;
import com.ws.commons.constant.ObjTypeEnum;
import com.ws.commons.constant.ValTypeEnum;
import com.ws.commons.tool.StrTool;
import com.ws.mybatissmart.annotation.ColumnInfo;
import com.ws.mybatissmart.annotation.TableInfo;
import com.ws.mybatissmart.annotation.TableInfo.IdtacticsEnum;

public class ClassMapperInfo {

	private TableInfo tableInfo;
	private Field idField;
	private String idColumnName;
	private Class<?> clazz;
	private LinkedHashMap<String, FieldMapperInfo> fieldsMapperMap;// key:columnName

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

	public LinkedHashMap<String, FieldMapperInfo> getFieldsMapperMap() {
		return fieldsMapperMap;
	}

	public void setFieldsMapperMap(LinkedHashMap<String, FieldMapperInfo> fieldsMapperMap) {
		this.fieldsMapperMap = fieldsMapperMap;
	}

	public String getIdColumnName() {
		return idColumnName;
	}

	public void setIdColumnName(String idColumnName) {
		this.idColumnName = idColumnName;
	}

	public String getInsertSql(Object obj) throws Exception {
		ColumnInfo ci = null;
		String v = null;
		Field fi = null;

		StringBuilder sql = new StringBuilder(" insert into ").append(tableInfo.value());
		StringBuilder clSb = new StringBuilder(" (");
		StringBuilder cvSb = new StringBuilder(" values(");
		for (Map.Entry<String, FieldMapperInfo> en : fieldsMapperMap.entrySet()) {
			fi = en.getValue().getField();
			ci = en.getValue().getColumnInfo();
			if (ci != null && !ci.isInsert()) {
				continue;
			}
			if (fi.getName().equals(tableInfo.idFieldName()) && tableInfo.idtactics() == IdtacticsEnum.DFT) {
				v = SequenceGenerate.nexId(tableInfo.value());
				fi.setAccessible(true);
				fi.set(obj, v);
				if (fi.getType() == String.class) {
					v = "'" + v + "'";
				}
				clSb.append(en.getKey()).append(",");
				cvSb.append(v).append(",");
				continue;
			}

			v = ClassMapperInfo.adornSqlVal(obj, en.getValue(), null);
			if (ci == null || ci.insertValType().length == 0) {
				/*
				 * 不插入空字符串和null值、"null"、" "
				 */
				if (StrTool.isNotEmpty(v)) {
					clSb.append(en.getKey()).append(",");
					cvSb.append(v).append(",");
				}
			} else {
				List<ObjTypeEnum> otEs = ClassMapperInfo.arrayToList(ci.insertValType(), ObjTypeEnum.class);

				if (otEs.contains(ObjTypeEnum.ALL)) {
					clSb.append(en.getKey()).append(",");
					cvSb.append(adornIfEmpty(v)).append(",");
					/*
					 * 不插入空字符串和null值
					 */
				} else if (otEs.contains(ObjTypeEnum.OBJ)) {
					if (StrTool.checkNotEmpty(v)) {
						clSb.append(en.getKey()).append(",");
						cvSb.append(v).append(",");
					}
				}
			}

		}

		if (clSb.length() > 0) {
			sql = sql.append(clSb.substring(0, clSb.length() - 1).concat(")"));
			sql = sql.append(cvSb.substring(0, cvSb.length() - 1).concat(")"));
			return sql.toString();
		}
		return null;
	}

	public String getUpdateByIdSql(Object obj) throws Exception {

		ColumnInfo ci = null;
		idField.setAccessible(true);
		String v = null;
		try {
			v = idField.get(obj) + "";
			if (StrTool.checkEmpty(v)) {
				throw new MybatisSmartException("条件字段" + tableInfo.idFieldName() + "的值不能为null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Field fi = null;

		StringBuilder sql = new StringBuilder(" update ").append(tableInfo.value()).append(" set ");
		StringBuilder setSb = new StringBuilder();
		String where = null;
		where = " where " + idColumnName + "=" + v;
		for (Map.Entry<String, FieldMapperInfo> en : fieldsMapperMap.entrySet()) {
			fi = en.getValue().getField();
			if (fi.getName().equals(tableInfo.idFieldName())) {
				continue;
			}
			ci = en.getValue().getColumnInfo();
			if (ci != null && !ci.isUpdate()) {
				continue;
			}
			fi.setAccessible(true);
			v = ClassMapperInfo.adornSqlVal(obj, en.getValue(), null);
			if (ci == null || ci.updateValType().length == 0) {
				if (StrTool.checkNotEmpty(v)) {
					setSb.append(en.getKey()).append("=").append(v).append(",");
				}
			} else {
				List<ObjTypeEnum> otEs = ClassMapperInfo.arrayToList(ci.updateValType(), ObjTypeEnum.class);
				if (otEs.contains(ObjTypeEnum.ALL)) {
					setSb.append(en.getKey()).append("=").append(adornIfEmpty(v)).append(",");
				} else if (otEs.contains(ObjTypeEnum.OBJ)) {
					if (StrTool.checkNotEmpty(v)) {
						setSb.append(en.getKey()).append("=").append(v).append(",");
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
		StringBuilder sql = new StringBuilder(" select  ").append(this.getColumns()).append(" from ")
				.append(tableInfo.value()).append(" where ").append(idColumnName).append("='" + idV + "'");

		return sql.toString();
	}

	private static final String SPACE = " ";
	private static final String WHERE_PRE = " where 1=1 ";

	private String buildWhere(Object obj, WhereSql filterSqlBuild) {
		final StringBuilder where = new StringBuilder();
		if (obj != null && filterSqlBuild != null) {
			List<WhereCond> conds = filterSqlBuild.getConds();
			if (!conds.isEmpty()) {
				where.append(WHERE_PRE);
				conds.forEach(cond -> {
					String columnName = cond.getColumnName();
					Object srcVal = cond.getVal();
					NexusCmp cexusCmp = cond.getNexusCmp();
					FieldMapperInfo fim = this.fieldsMapperMap.get(columnName);
					String val = null;
					if (srcVal == null && fim != null) {
						try {
							if (!(obj instanceof Class)) {
								val = ClassMapperInfo.adornSqlVal(obj, fim, cexusCmp);
							}
						} catch (Exception exc) {
							exc.printStackTrace();
						}
					} else if (srcVal != null) {
						val = buildCmpVal(srcVal, null, cexusCmp);
					}
					if (val != null) {
						where.append(cond.getLogicCmp().code).append(SPACE).append(columnName).append(SPACE).append(val)
								.append(SPACE);
					}

				});
			}
		}
		return where.toString();
	}

	public String getSelectByWhereSql(Object obj, WhereSql filterSqlBuild) {
		String where = buildWhere(obj, filterSqlBuild);

		String limit = filterSqlBuild == null ? "" : filterSqlBuild.getLimit();
		String orderBy = filterSqlBuild == null ? "" : filterSqlBuild.getOrderBy();
		StringBuilder sql = new StringBuilder(" select  ").append(limit).append(" ").append(this.getColumns())
				.append(" from ").append(tableInfo.value());
		sql.append(where);
		if (StrTool.checkNotEmpty(orderBy)) {
			sql.append("order by ").append(orderBy);
		}
		return sql.toString();
	}

	public String getCountByWhereSql(Object obj, WhereSql filterSqlBuild) {
		String where = buildWhere(obj, filterSqlBuild);
		StringBuilder sql = new StringBuilder(" select count(*) from ").append(tableInfo.value()).append(where);
		return sql.toString();
	}

	public String getDeleteByWhereSql(Object obj, WhereSql filterSqlBuild) {
		String where = buildWhere(obj, filterSqlBuild);
		if (StrTool.checkNotEmpty(where)) {
			StringBuilder sql = new StringBuilder(" delete  from ").append(tableInfo.value()).append(where);
			return sql.toString();
		}
		return null;
	}

	public String getDeleteByIdSql(Object idV) {
		if (StrTool.checkEmpty(idV)) {
			throw new MybatisSmartException("条件字段" + tableInfo.idFieldName() + "的值不能为null");
		}
		StringBuilder sql = new StringBuilder(" delete from ").append(tableInfo.value()).append(" where ")
				.append(idColumnName).append("='" + idV + "'");
		return sql.toString();
	}

	private static String adornIfEmpty(String val) {

		return val == null ? ValTypeEnum.NULL.code : (val.trim().length() == 0 ? "'" + val + "'" : val);
	}

	/**
	 * 反射取值
	 * 
	 * @param srcObj
	 * @param fmi
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private static Object reflexVal(Object srcObj, FieldMapperInfo fmi)
			throws IllegalArgumentException, IllegalAccessException {
		Field fi = fmi.getField();
		Object srcVal;
		fi.setAccessible(true);
		srcVal = fi.get(srcObj);
		return srcVal;
	}

	/**
	 * 装饰sql值
	 * 
	 * @param srcObj   原始值所属对象
	 * @param fmi      对应的字段映射信息
	 * @param cexusCmp 逻辑计算符号，如insert、update..时不需计算则可为null
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private static String adornSqlVal(Object srcObj, FieldMapperInfo fmi, NexusCmp cexusCmp)
			throws IllegalArgumentException, IllegalAccessException {
		Object srcVal = reflexVal(srcObj, fmi);
		String val = null;
		if (srcVal != null) {
			Field fi = fmi.getField();
			Class<?> fiClass = fi.getType();
			/*
			 * 逻辑运算
			 */
			if (cexusCmp != null) {
				val = buildCmpVal(srcVal, fmi, cexusCmp);
			} else {
				if (fiClass == String.class || fiClass == Date.class) {
					ColumnInfo ci = fmi.getColumnInfo();
					if (fiClass == Date.class && (ci != null && ci.dateFormart().length() > 0)) {
						val = "'" + new SimpleDateFormat(ci.dateFormart()).format((Date) srcVal) + "'";
					} else {
						val = "#{" + MybatisSmartAutoConfiguration.E_K + ".".concat(fi.getName()).concat("}");
					}
				} else {
					val = srcVal.toString();
				}

			}
		}
		return val;
	}

	/**
	 * 包裹值
	 * 
	 * @param srcVal
	 * @param fieldName
	 * @return
	 */
	private static String boundSqlVal(Object srcVal, String fieldName) {
		String srcValStr = srcVal.toString();
		if (fieldName == null) {
			if (srcVal.getClass() == String.class) {
				srcValStr = "'".concat(srcValStr).concat("'");
			}
			//......其他数据类型，暂时不支持处理，只能用户在外面处理
		} else {
			srcValStr = "#{".concat(MybatisSmartAutoConfiguration.E_K).concat(".").concat(fieldName).concat("}");
		}
		return srcValStr;
	}

	/**
	 * 构建计算值
	 * 
	 * @param srcVal
	 * @param fmi 可以为null，无需#{} 解析，则必传此参数
	 * @param cexusCmp
	 * @return
	 */
	private static String buildCmpVal(Object srcVal, FieldMapperInfo fmi, NexusCmp cexusCmp) {
		if (srcVal==null) {
			return null;
		}
		String sqlVal = srcVal.toString();
		int len = sqlVal.length();
		if (len == 0) {
			return null;
		}
		Class<?> fiClass = srcVal.getClass();
		String fieldName = fmi==null?null:fmi.getField().getName();

		if (cexusCmp == NexusCmp.like_lr) {
			sqlVal = boundSqlVal(srcVal, fieldName);
			sqlVal = cexusCmp.code.concat(" concat('%',").concat(sqlVal).concat(",'%')");
		} else if (cexusCmp == NexusCmp.like_l) {
			sqlVal = boundSqlVal(srcVal, fieldName);
			sqlVal = cexusCmp.code.concat(" concat('%',").concat(sqlVal).concat(")");
		} else if (cexusCmp == NexusCmp.like_r) {
			sqlVal = boundSqlVal(srcVal, fieldName);
			sqlVal = cexusCmp.code.concat(" concat(").concat(sqlVal).concat(",'%')");
		} else if (sqlVal.equalsIgnoreCase(ValTypeEnum.NULL.code)) {
			sqlVal = cexusCmp.code.concat(SPACE).concat(sqlVal);
		} else if (fiClass == String.class || srcVal instanceof Date) {
			sqlVal = boundSqlVal(srcVal, fieldName);
			sqlVal = cexusCmp.code.concat(SPACE).concat(sqlVal);
		} else if (cexusCmp == NexusCmp.in || cexusCmp == NexusCmp.not_in) {
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
		} else {
			sqlVal = boundSqlVal(srcVal, fieldName);
			sqlVal = cexusCmp.code.concat(sqlVal);
		}
		return sqlVal;
	}

	private String getColumns() {
		StringBuilder cls = new StringBuilder();
		String clsStr = null;
		this.fieldsMapperMap.forEach((k, v) -> {
			cls.append(k).append(" as ").append(v.getField().getName()).append(",");
			// cls.append(k).append(",");
		});
		if (cls.length() > 0) {
			clsStr = cls.substring(0, cls.length() - 1);
		}
		return clsStr;
	}

	@SuppressWarnings("unchecked")
	public static <E> List<E> arrayToList(Object[] ary, Class<E> E) {
		List<E> list = new ArrayList<E>();
		for (Object e : ary) {
			list.add((E) e);
		}
		return list;
	}

}
