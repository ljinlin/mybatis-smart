package com.mingri.mybatissmart.dbo;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.mingri.langhuan.cabinet.constant.LogicCmp;
import com.mingri.langhuan.cabinet.constant.NexusCmp;
import com.mingri.langhuan.cabinet.constant.ValTypeEnum;
import com.mingri.langhuan.cabinet.tool.ClassTool;
import com.mingri.langhuan.cabinet.tool.CollectionTool;
import com.mingri.langhuan.cabinet.tool.StrTool;
import com.mingri.mybatissmart.annotation.SmartColumn;
import com.mingri.mybatissmart.annotation.SmartTable;
import com.mingri.mybatissmart.barracks.Constant;
import com.mingri.mybatissmart.barracks.IdtacticsEnum;
import com.mingri.mybatissmart.barracks.SqlKwd;
import com.mingri.mybatissmart.config.MybatisSmartContext;
import com.mingri.mybatissmart.dbo.MapperSql.WhereSql;

class MapperSqlTool {

	static WhereSql buildWhere(Object obj, Where where, SmartTableInfo tableClass)
			throws IllegalArgumentException, IllegalAccessException {

		List<WhereNode> nodes = where == null ? null : where.getNodes();
		if (where == null || CollectionTool.isEmpty(nodes)) {
			return null;
		}

		MapperSql.WhereSql whereSql = MapperSql.where();
		if (obj != null) {
			MapperSqlTool.buildWhere(obj, nodes, tableClass, whereSql);
		}

		String afterConditionSql = where.getAfterConditionSql();
		if (StrTool.isNotEmpty(afterConditionSql)) {
			whereSql.add(afterConditionSql);
		}
		return whereSql;
	}

	private static WhereSql buildWhere(Object obj, List<WhereNode> conds, SmartTableInfo tableClass, WhereSql whereSql)
			throws IllegalArgumentException, IllegalAccessException {
		if (conds.isEmpty()) {
			return null;
		}

		for (WhereNode cond : conds) {
			List<WhereNode> childConds = cond.getChildCond();
			LogicCmp logicCmp = cond.getLogicCmp();
			if (CollectionTool.notEmpty(childConds)) {
				WhereSql childWhere = MapperSqlTool.buildWhere(obj, childConds, tableClass, MapperSql.where());
				whereSql.add(logicCmp, childWhere);
			}
			String columnName = cond.getColumnName();
			String columnVal = null;
			Object condSrcVal = cond.getVal();
			NexusCmp nexusCmp = cond.getNexusCmp();
			SmartColumnInfo fim = tableClass.smartColumnInfoMap.get(columnName);

			if (condSrcVal != null) {
				if (cond.isStatementVal()) {
					columnVal = condSrcVal.toString();
				} else {
					columnVal = MapperSqlTool.buildWhereNode(nexusCmp, null, condSrcVal);
				}
			} else if (fim != null) {
				if (!(obj instanceof Class)) {
					Object objSrcVal = ClassTool.reflexVal(obj, fim.getField());
					columnVal = MapperSqlTool.buildWhereNode(nexusCmp, fim, objSrcVal);
				}
			}
			if (columnVal != null) {
				whereSql.add(logicCmp, columnName, nexusCmp, columnVal);
			}
		}

		return whereSql;
	}

	/**
	 * 构建计算值
	 * 
	 * @param srcVal
	 * @param fmi      可以为null，无需#{} 解析，则必传此参数
	 * @param cexusCmp
	 * @return 关系运算符 columnVal
	 */
	private static String buildWhereNode(NexusCmp cexusCmp, SmartColumnInfo fmi, Object srcVal) {
		if (srcVal == null) {
			return null;
		}
		boolean isString = srcVal instanceof String;
		String columnVal = null;
		if (isString && srcVal.toString().length() == 0) {
			return null;
		}
		if ((cexusCmp == NexusCmp.IN || cexusCmp == NexusCmp.NOT_IN) && isString) {
			columnVal = srcVal.toString();
		} else if (fmi != null) {// 。对象传值
			columnVal = MapperSqlTool.buildColumnVal(srcVal, fmi, null);
		} else {// 。直接传值
			columnVal = MapperSqlTool.buildColumnValOfWhereNodeVal(srcVal);
		}
		if (columnVal == null) {
			return null;
		}
		int len = columnVal.length();
		if (len == 0) {
			return null;
		}
		switch (cexusCmp) {
		case LIKE_LR:
			columnVal = " concat('%',".concat(columnVal).concat(",'%')");
			break;
		case LIKE_L:
			columnVal = " concat('%',".concat(columnVal).concat(")");
			break;
		case LIKE_R:
			columnVal = " concat(".concat(columnVal).concat(",'%')");
			break;
		case IN:
		case NOT_IN:
			columnVal = " (".concat(columnVal).concat(")");
			break;
		default:
			break;
		}
		return columnVal;
	}

	static String getColumns(SmartTableInfo tableClass) {
		StringBuilder cls = new StringBuilder();
		String clsStr = null;
		tableClass.smartColumnInfoMap.forEach((k, v) -> {
			cls.append(k).append(SqlKwd.AS).append(v.getField().getName()).append(",");
		});
		if (cls.length() > 0) {
			clsStr = cls.substring(0, cls.length() - 1);
		}
		return clsStr;
	}

	/**
	 * 构建column值：<br>
	 * 1、如果是java.util.Date类型，而且在@SmartColumn注解配置了dateFormart则按dateFormart格式化<br>
	 * 2、如果是java.util.Date类型，但是没有配置dateFormart；如果是String类型， 则让mybatis解析（用#{}包裹）<br>
	 * 2、不是1和2的情况直接toString<br>
	 * 
	 * @param srcVal 原始值
	 * @param fmi    对应的字段映射信息
	 * @param index  批量插入时使用的索引
	 * @return 返回column值
	 * @throws IllegalAccessException
	 */
	private static String buildColumnValOfWhereNodeVal(Object srcVal) {
		if (srcVal == null) {
			return null;
		}
		String columnVal = null;
		String srcStrVal = srcVal.toString();
		if (srcVal.getClass() == String.class) {
			columnVal = "'".concat(srcStrVal).concat("'");
		} else if (srcVal == ValTypeEnum.BLANK) {
			columnVal = "''";
		} else if (srcVal == ValTypeEnum.NULL) {
			columnVal = ValTypeEnum.NULL.code;
		} else {
			if (srcVal.getClass().isArray()) {
				srcVal = JSONArray.parseArray(JSONArray.toJSONString(srcVal), Object.class);
			}
			if (srcVal instanceof Collection) {
				Collection<?> clt = (Collection<?>) srcVal;
				if (clt.isEmpty()) {
					return null;
				}
				StringBuilder inVal = new StringBuilder();
				clt.forEach(e -> {
					if (e instanceof String) {
						inVal.append("'").append(e.toString()).append("'").append(",");
					} else {
						inVal.append(e.toString()).append(",");
					}
				});
				if (inVal.length() > 0) {
					columnVal = inVal.substring(0, inVal.length() - 1);
				} else {
					return null;
				}
			}
		}
		return columnVal == null ? srcStrVal : columnVal;
	}

	/**
	 * 构建column值：<br>
	 * 1、如果是java.util.Date或者java.time.temporal.TemporalAccessor，<br>
	 * &nbsp;&nbsp;1.1而且在@SmartColumn注解配置了dateFormart则按dateFormart格式化<br>
	 * &nbsp;&nbsp;1.2但是没有在@SmartColumn配置dateFormart；如果是String类型，
	 * 则让mybatis解析（用#{}包裹）<br>
	 * 2、不是1和2的情况直接toString<br>
	 * 
	 * @param srcVal 原始值
	 * @param fmi    对应的字段映射信息
	 * @param index  批量插入时使用的索引
	 * @return 返回column值
	 * @throws IllegalAccessException
	 */
	static String buildColumnVal(Object srcVal, SmartColumnInfo fmi, Integer index) {
		if (srcVal == null) {
			return null;
		}
		String columnVal = null;
		Field fi = fmi.getField();
		SmartColumn columInfo = fmi.getSmartColumn();
		Class<?> fieldType = fi.getType();
		if (columInfo != null && columInfo.dateFormart().length() > 0) {
			if (srcVal instanceof Date) {
				return "'" + new SimpleDateFormat(columInfo.dateFormart()).format((Date) srcVal) + "'";
			} else if (srcVal instanceof TemporalAccessor) {
				return "'" + DateTimeFormatter.ofPattern(columInfo.dateFormart()).format((TemporalAccessor) srcVal)
						+ "'";
			}
		}
		if (fieldType == String.class || srcVal instanceof Date || srcVal instanceof TemporalAccessor) {
			columnVal = MapperSqlTool.toBatisVal(srcVal, fi.getName(), index);
		} else {
			columnVal = srcVal.toString();
		}
		return columnVal;
	}

	/**
	 * 如果是 null、""、" "则处理为对应的sql值，反之不处理
	 * 
	 * @param emptyVal null、""、" "
	 * @return 返回处理后的值
	 */
	static String toSqlEmptyIfStringEmpty(String emptyVal) {
		return emptyVal == null ? ValTypeEnum.NULL.code
				: (emptyVal.trim().length() == 0 ? "'" + emptyVal + "'" : emptyVal);
	}

	/**
	 * 如果是id字段，并且是默认的id成策略
	 * 
	 * @param field
	 * @param rowObj
	 * @return
	 */
	static String generateIdIfIdFieldAndDftIdtactic(Field field, Object rowObj, SmartTable tableInfo) {
		String idVal = null;
		if (field.getName().equals(tableInfo.idFieldName()) && tableInfo.idtactics() == IdtacticsEnum.DFT) {
			idVal = MybatisSmartContext.getSequenceGenerate().nexId(tableInfo.value());
			MapperSqlTool.injectIdVal(field, rowObj, idVal);
		}
		return idVal;
	}

	/**
	 * 注入id值
	 * 
	 * @param idField
	 * @param rowObj
	 * @param idVal
	 */
	private static void injectIdVal(Field idField, Object rowObj, String idVal) {
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

	/**
	 * 
	 * 将原始值处理成mybatis可解析的值，如下：<br>
	 * 1、int 1 转成 字符串1<br>
	 * 2、String name 大神 转成 '大神'<br>
	 * 3、其他类型 fieleName 转成 #{fieleName} 或者 #{参数名称.fieleName} 或者
	 * 。#{参数名称.[索引].字段名称.name}<br>
	 * 4、枚举 ValTypeEnum.BLANK 转成 空字符串值<br>
	 * 5、枚举 ValTypeEnum.NULL 转成 null<br>
	 * 
	 * @param srcVal    原始值
	 * @param fieldName 字段名称
	 * @param index     inset集合时使用
	 * @return
	 */
	private static String toBatisVal(Object srcVal, String fieldName, Integer index) {
		String indexStr = index == null ? "." : "[" + index + "].";
		return "#{".concat(Constant.PARAM_KEY).concat(indexStr).concat(fieldName).concat("}");
	}
}