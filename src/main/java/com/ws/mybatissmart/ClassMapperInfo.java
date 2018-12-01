package com.ws.mybatissmart;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ws.commons.algorithm.SequenceGenerate;
import com.ws.commons.constant.CmpChar;
import com.ws.commons.constant.ObjTypeEnum;
import com.ws.commons.tool.StrTool;
import com.ws.mybatissmart.annotation.ColumnInfo;
import com.ws.mybatissmart.annotation.TableInfo;
import com.ws.mybatissmart.annotation.TableInfo.IdtacticsEnum;

public class ClassMapperInfo {

	private TableInfo tableInfo;
	private Field idField;
	private String idColumnName;
	private Class<?> clazz;
	private LinkedHashMap<String, FieldMapperInfo> fieldsMapperMap;

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
			ci = en.getValue().getColumnInfo();
			v = ClassMapperInfo.adornSqlVal(obj, en.getValue());
			if (ci == null || ci.noInsertVal().length == 0) {
				/*
				 * 不插入空字符串和null值、"null"、" "
				 */
				if (StrTool.checkNotEmpty(v)) {
					clSb.append(en.getKey()).append(",");
					cvSb.append(v).append(",");
				}
			} else {
				List<ObjTypeEnum> otEs = ClassMapperInfo.arrayToList(ci.noInsertVal(), ObjTypeEnum.class);

				/*
				 * 不插入空字符串和null值
				 */
				if (otEs.contains(ObjTypeEnum.BLANK) && otEs.contains(ObjTypeEnum.NULL)) {
					if (StrTool.isNotEmpty(v)) {
						clSb.append(en.getKey()).append(",");
						cvSb.append(v).append(",");
					}

					/*
					 * 不插入null值
					 */
				} else if (otEs.contains(ObjTypeEnum.NULL)) {
					if (v != null) {
						clSb.append(en.getKey()).append(",");
						cvSb.append(v).append(",");
					}

					/*
					 * 不插入空字符串
					 */
				} else if (otEs.contains(ObjTypeEnum.BLANK)) {
					if (StrTool.isNotBlank(v)) {
						clSb.append(en.getKey()).append(",");
						cvSb.append(v).append(",");
					}

					/*
					 * 不插入空字符串和null值、"null"、" "
					 */
				} else if (otEs.size() == 0) {
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
				throw new MybatisXException("条件字段" + tableInfo.idFieldName() + "的值不能为null");
			}
		} catch (IllegalArgumentException | IllegalAccessException e1) {
			e1.printStackTrace();
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
			Object ov = null;
			fi.setAccessible(true);
			ov = fi.get(obj);
			v = ClassMapperInfo.adornSqlVal(obj, en.getValue());
			if (ci == null || ci.noInsertVal().length == 0) {
				if (StrTool.checkNotEmpty(v)) {
					setSb.append(en.getKey()).append("=").append(v).append(",");
				}
			} else {
				List<ObjTypeEnum> otEs = ClassMapperInfo.arrayToList(ci.noUpdateVal(), ObjTypeEnum.class);
				if (otEs.contains(ObjTypeEnum.BLANK) && otEs.contains(ObjTypeEnum.NULL)) {
					if (StrTool.isNotEmpty(v)) {
						setSb.append(en.getKey()).append("=").append(v).append(",");
					}
				} else if (otEs.contains(ObjTypeEnum.NULL)) {
					if (v != null) {
						setSb.append(en.getKey()).append("=").append(v).append(",");
					}
				} else if (otEs.contains(ObjTypeEnum.BLANK)) {
					if (StrTool.isNotBlank(v)) {
						setSb.append(en.getKey()).append("=").append(v).append(",");
					}
				} else if (otEs.size() == 0) {
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
			throw new MybatisXException("条件字段" + tableInfo.idFieldName() + "的值不能为null");
		}
		StringBuilder sql = new StringBuilder(" select  ").append(this.getColumns()).append(" from ")
				.append(tableInfo.value()).append(" where ").append(idColumnName).append("='" + idV + "'");

		return sql.toString();
	}

	public String getSelectListSql(Object obj, FilterSqlBuild filterSqlBuild) {
		idField.setAccessible(true);
		final StringBuilder where = new StringBuilder();
		if (obj != null && filterSqlBuild != null) {
			LinkedHashMap<String, CmpChar> conds = filterSqlBuild.getConds();
			if (!conds.isEmpty()) {
				where.append(" where 1=1 ");
				conds.forEach((e, v) -> {
					FieldMapperInfo fim = this.fieldsMapperMap.get(e);
					String val = ClassMapperInfo.adornSqlVal(obj, fim);
					if (StrTool.checkNotEmpty(val)) {
						where.append(" and ").append(e).append(v.code);
						if (v == CmpChar.like) {
							// val = "'%".concat(val.substring(1, val.length() - 1)).concat("%'");
							val = "'%".concat(val).concat("%'");
						}
						where.append(val);
					}
				});
			}
		}
		String limit = filterSqlBuild.getLimit();
		String orderBy = filterSqlBuild.getOrderBy();
		StringBuilder sql = new StringBuilder(" select  ").append(limit).append(" ").append(this.getColumns())
				.append(" from ").append(tableInfo.value());
		sql.append(where);
		if (StrTool.checkNotEmpty(orderBy)) {
			sql.append("order by ").append(orderBy);
		}
		return sql.toString();
	}

	public String getDeleteByIdSql(Object idV) {
		idField.setAccessible(true);
		if (StrTool.checkEmpty(idV)) {
			throw new MybatisXException("条件字段" + tableInfo.idFieldName() + "的值不能为null");
		}
		StringBuilder sql = new StringBuilder(" delete from ").append(tableInfo.value()).append(" where ")
				.append(idColumnName).append("='" + idV + "'");
		return sql.toString();
	}

	private static String adornSqlVal(Object srcObj, FieldMapperInfo fmi) {
		String v = null;
		Field fi = fmi.getField();
		Object srcVal;
		try {
			fi.setAccessible(true);
			srcVal = fi.get(srcObj);
			if (srcVal != null) {
				ColumnInfo ci = fmi.getColumnInfo();
				if (srcObj.getClass() == Date.class && ci.dateFormart().length() > 0) {
					v = "'" + new SimpleDateFormat(ci.dateFormart()).format((Date) srcVal) + "'";
				} else {
					v = srcVal.toString();
					if (v.length() > 0 && fi.getType() == String.class) {
						// srcObj.dataMap.put(fi.getName(), v);
						v = "#{" + fi.getName() + "}";
						// v = "'" + v + "'";
					}
				}
			} else {
				return ObjTypeEnum.NULL.code;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return v;
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
