package com.ws.mybatissmart;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ws.commons.constant.ObjTypeEnum;
import com.ws.commons.tool.StrTool;
import com.ws.mybatissmart.annotation.ColumnInfo;
import com.ws.mybatissmart.annotation.TableInfo;

public class SqlBuildTool {

	public String getUpdateByIdSql(ClassMapperInfo cmif, Object obj) {
		ColumnInfo ci = null;
		Field idField = cmif.getIdField();
		TableInfo tableInfo = cmif.getTableInfo();
		String idColumnName = cmif.getIdColumnName();
		LinkedHashMap<String, FieldMapperInfo> fieldsMapperMap = cmif.getFieldsMapperMap();

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

		List<String> sets = new ArrayList<>();
		StringBuilder sql = new StringBuilder(" update").append(tableInfo.value()).append(" set ");
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
			try {
				fi.setAccessible(true);
				ov = fi.get(obj);
				v = SqlBuildTool.adornSqlVal(ov, en.getValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (ci != null && ci.noInsertVal().length > 0) {
				List<ObjTypeEnum> otEs = ClassMapperInfo.arrayToList(ci.noUpdateVal(), ObjTypeEnum.class);
				if (otEs.contains(ObjTypeEnum.BLANK) && otEs.contains(ObjTypeEnum.NULL)) {
					if (StrTool.isNotEmpty(v)) {
						setSb.append(en.getKey()).append("=").append(v).append(",");
						sets.add(en.getKey().concat("=#{}"));
					}
				} else if (otEs.contains(ObjTypeEnum.NULL)) {
					if (v != null) {
						setSb.append(en.getKey()).append("=").append(v).append(",");
					}
				} else if (otEs.contains(ObjTypeEnum.BLANK)) {
					if (StrTool.isNotBlank(v)) {
						if (ov == null) {
							setSb.append(en.getKey()).append(" is ").append(v).append(",");
						} else {
							setSb.append(en.getKey()).append("=").append(v).append(",");
						}
					}
				} else if (otEs.size() == 0) {
					if (StrTool.checkNotEmpty(v)) {
						setSb.append(en.getKey()).append("=").append(v).append(",");
					}
				}
			} else {
				if (StrTool.checkNotEmpty(v)) {
					setSb.append(en.getKey()).append("=").append(v).append(",");
				}
			}
		}

		if (setSb.length() > 0) {
			sql = sql.append(setSb.substring(0, setSb.length() - 1)).append(where);
			return sql.toString();
		}
		return null;
	}

	private static String adornSqlVal(Object srcVal, FieldMapperInfo fmi) {
		String v = null;
		Field fi = fmi.getField();
		ColumnInfo ci = fmi.getColumnInfo();
		if (srcVal != null) {
			if (fi.getType() == Date.class && ci.dateFormart().length() > 0) {
				v = "'" + new SimpleDateFormat(ci.dateFormart()).format((Date) srcVal) + "'";
			} else {
				v = String.valueOf(srcVal);
				if (fi.getType() == String.class) {
					v = "'" + v + "'";
				}
			}
		} else {
			v = ObjTypeEnum.NULL.code;
		}
		return v;
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
