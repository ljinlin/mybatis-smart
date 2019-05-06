package com.ws.mybatissmart;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.ws.mybatissmart.Constant.SQL;
import com.ws.mybatissmart.annotation.ColumnInfo;
import com.ws.mybatissmart.annotation.TableInfo;
import com.ws.mybatissmart.annotation.TableInfo.IdtacticsEnum;

public class ClassMapperInfo {

	private TableInfo tableInfo;
	private Field idField;
	private String idColumnName;
	private Class<?> clazz;
	private LinkedHashMap<String, FieldMapperInfo> fieldsMapperMap;// key:columnName
	private DialectEnums dialect;
	
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

	public DialectEnums getDialect() {
		return dialect;
	}

	public void setDialect(DialectEnums dialect) {
		this.dialect = dialect;
	}

	public String getInsertSql(Object obj) throws Exception {
		ColumnInfo ci = null;
		String v = null;
		Field fi = null;

		StringBuilder sql = new StringBuilder(SQL.INSERT_INTO).append(tableInfo.value());
		StringBuilder clSb = new StringBuilder(" (");
		StringBuilder cvSb = new StringBuilder(" values(");

		Collection<Object> objList = new ArrayList<>();
		Integer i = null;
		if (obj instanceof Collection) {
			i = 0;
			objList = (Collection<Object>) obj;
		} else {
			objList.add(obj);
		}
		for (Object paramEntity : objList) {
			for (Map.Entry<String, FieldMapperInfo> en : fieldsMapperMap.entrySet()) {
				fi = en.getValue().getField();
				ci = en.getValue().getColumnInfo();
				if (ci != null && ci.isInsert()==false) {
					continue;
				}
				
				/*
				 *  id处理
				 */
				if (fi.getName().equals(tableInfo.idFieldName()) && tableInfo.idtactics() == IdtacticsEnum.DFT) {
					v = SequenceGenerate.nexId(tableInfo.value());
					fi.setAccessible(true);
					fi.set(paramEntity, v);
					if (fi.getType() == String.class) {
						v = "'" + v + "'";
					}
					if (i == null || i == 0) {
						clSb.append(en.getKey()).append(",");
					}
					cvSb.append(v).append(",");
					continue;
				}
				/*
				 * 没配置insertValType 或者insertValType为''
				 */

				v = ClassMapperInfo.adornSqlVal(paramEntity, en.getValue(), null, i);
				if (ci == null || ci.insertValType().length == 0) {
					/*
					 * 不插入空字符串和null值、"null"、" "
					 */
					if (StrTool.isNotEmpty(v)) {
						if (i == null || i == 0) {
							clSb.append(en.getKey()).append(",");
						}
						cvSb.append(v).append(",");
					}else if(i!=null){//批量新增时
						if(i==0) {
							clSb.append(en.getKey()).append(",");
						}
						cvSb.append("null").append(",");
					}
				} else {
					// List<ObjTypeEnum> otEs = ClassMapperInfo.arrayToList(ci.insertValType(),
					// ObjTypeEnum.class);
					List<ObjTypeEnum> otEs = Arrays.asList(ci.insertValType());

					if (otEs.contains(ObjTypeEnum.ALL)) {
						if (i == null || i == 0) {
							clSb.append(en.getKey()).append(",");
						}
						cvSb.append(adornIfEmpty(v)).append(",");
						/*
						 * 不插入空字符串和null值
						 */
					} else if (otEs.contains(ObjTypeEnum.OBJ)) {
						if (StrTool.checkNotEmpty(v)) {
							if (i == null || i == 0) {
								clSb.append(en.getKey()).append(",");
							}
							cvSb.append(v).append(",");
						}else if(i!=null) {//批量新增时
							if(i==0) {
								clSb.append(en.getKey()).append(",");
							}
							cvSb.append("null").append(",");
						}
					}
				}

			}
			cvSb = cvSb.deleteCharAt(cvSb.length() - 1).append("),(");
			if (i != null) {
				i++;
			}
		}
		if (clSb.length() > 0) {
			sql = sql.append(clSb.deleteCharAt(clSb.length() - 1).append(")"));
			sql = sql.append(cvSb.delete(cvSb.length() - 2, cvSb.length()));
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

		ColumnInfo ci = null;

		String v = null;
		Field fi = null;
		String where = buildWhere(obj, filterSqlBuild);
		if (StrTool.isEmpty(where)) {
			throw new MybatisSmartException("必须设置where条件");
		}
		StringBuilder sql = new StringBuilder(" update ").append(tableInfo.value()).append(" set ");
		StringBuilder setSb = new StringBuilder();

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
			v = ClassMapperInfo.adornSqlVal(obj, en.getValue(), null, null);
			if (ci == null || ci.updateValType().length == 0) {
				if (StrTool.checkNotEmpty(v)) {
					setSb.append(en.getKey()).append("=").append(v).append(",");
				}
			} else {
				// List<ObjTypeEnum> otEs = ClassMapperInfo.arrayToList(ci.updateValType(),
				// ObjTypeEnum.class);
				List<ObjTypeEnum> otEs = Arrays.asList(ci.updateValType());
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
		StringBuilder sql = new StringBuilder(SQL.SELECT).append(this.getColumns()).append(SQL.FROM)
				.append(tableInfo.value()).append(SQL.WHERE_PRE).append(idColumnName).append("='" + idV + "'");

		return sql.toString();
	}

	private String buildWhere(Object obj, List<WhereCond> conds) {
		final StringBuilder where = new StringBuilder();
		if (!conds.isEmpty()) {
			int index=0;
			for (WhereCond cond : conds) {
				List<WhereCond> childConds=cond.getChildCond();
				if(childConds!=null&&childConds.size()>0) {
					String childWhere=this.buildWhere(obj, childConds);
					if(childWhere.length()>0) {
						where.append(cond.getLogicCmp().code).append(" ( ").append(childWhere).append(" ) ");
					}
				}
				String columnName = cond.getColumnName();
				Object srcVal = cond.getVal();
				NexusCmp cexusCmp = cond.getNexusCmp();
				FieldMapperInfo fim = this.fieldsMapperMap.get(columnName);
				String val = null;
				if (srcVal == null && fim != null) {
					try {
						if (!(obj instanceof Class)) {
							val = ClassMapperInfo.adornSqlVal(obj, fim, cexusCmp, null);
						}
					} catch (Exception exc) {
						exc.printStackTrace();
					}
				} else if (srcVal != null) {
					if(cond.isSqlVal()) {
						val = cexusCmp.code.concat(Constant.SPACE).concat(srcVal.toString());
					}else {
						val = buildCmpVal(srcVal, null, cexusCmp,null);  
					}
				}
				if (val != null) {
					if(index>0) {
						where.append(cond.getLogicCmp().code);
					}
						where.append(Constant.SPACE).append(columnName).append(Constant.SPACE).append(val)
						.append(Constant.SPACE);
						index++;
				}
			}
			
		}
		return where.toString();
	}
	private String buildWhere(Object obj, WhereSql filterSqlBuild) {
		 StringBuilder where = new StringBuilder();
		if (obj != null && filterSqlBuild != null) {
			where.append(this.buildWhere(obj, filterSqlBuild.getConds()));
		}
		String nativeSqlConds=filterSqlBuild.getNativeSqlConds();
		if(StrTool.isNotEmpty(nativeSqlConds)) {
			if(where.length()>0) {
				where.append(Constant.SPACE).append(nativeSqlConds);
			}else {
				nativeSqlConds=StrTool.trimStr(StrTool.trimStr(nativeSqlConds.trim(),"or"),"OR");
				nativeSqlConds=StrTool.trimStr(StrTool.trimStr(nativeSqlConds.trim(),"and"),"AND");
				where.append(nativeSqlConds);
			}
		}
		if(where.length()>0) {
			where.insert(0, SQL.WHERE_PRE);
		}
		return where.toString();
	}

	public String getSelectByWhereSql(Object obj, WhereSql filterSqlBuild) {
		String where = buildWhere(obj, filterSqlBuild);

		String orderBy = filterSqlBuild == null ? StrTool.EMPTY : filterSqlBuild.getOrderBy();
		// if()
		StringBuilder sql = new StringBuilder(SQL.SELECT).append(this.getColumns())
				.append(SQL.FROM).append(tableInfo.value());
		sql.append(where);
		if (StrTool.checkNotEmpty(orderBy)) {
			sql.append(orderBy);
		}
		Integer limit = filterSqlBuild == null ?null:filterSqlBuild.getLimit();
		if(limit!=null) {
			switch (dialect) {
			case MYSQL:
				sql.append(SQL.LIMIT).append(limit).append(SQL.OFFSET).append(filterSqlBuild.getOffset());
				break;
			case SQLSERVER:
				sql.append(SQL.OFFSET).append(filterSqlBuild.getOffset()).append(" rows fetch next ").append(limit).append(" rows only ");
				break;
			default:
				break;
			}
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
			StringBuilder sql = new StringBuilder(SQL.DELETE).append(SQL.FROM).append(tableInfo.value()).append(where);
			return sql.toString();
		}
		return null;
	}

	public String getDeleteByIdSql(Object idV) {
		if (StrTool.checkEmpty(idV)) {
			throw new MybatisSmartException("条件字段" + tableInfo.idFieldName() + "的值不能为null");
		}
		StringBuilder sql = new StringBuilder(SQL.DELETE).append(SQL.FROM).append(tableInfo.value()).append(SQL.WHERE_PRE)
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
	 * @param srcObj
	 *            原始值所属对象
	 * @param fmi
	 *            对应的字段映射信息
	 * @param cexusCmp
	 *            逻辑计算符号，如insert、update..时不需计算则可为null
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private static String adornSqlVal(Object srcObj, FieldMapperInfo fmi, NexusCmp cexusCmp, Integer index)
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
				val = buildCmpVal(srcVal, fmi, cexusCmp, index);
			} else {
				if (fiClass == String.class || fiClass == Date.class) {
					ColumnInfo ci = fmi.getColumnInfo();
					if (fiClass == Date.class && (ci != null && ci.dateFormart().length() > 0)) {
						val = "'" + new SimpleDateFormat(ci.dateFormart()).format((Date) srcVal) + "'";
					} else {
						String indexStr = index == null ? "." : "[" + index + "].";
						val = "#{".concat(Constant.PARAM_KEY).concat(indexStr).concat(fi.getName()).concat("}");
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
	private static String boundSqlVal(Object srcVal, String fieldName, Integer index) {
		String srcValStr = srcVal.toString();
		if (fieldName == null) {
			if (srcVal.getClass() == String.class) {
				srcValStr = "'".concat(srcValStr).concat("'");
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
	 * @param fmi
	 *            可以为null，无需#{} 解析，则必传此参数
	 * @param cexusCmp
	 * @return
	 */
	private static String buildCmpVal(Object srcVal, FieldMapperInfo fmi, NexusCmp cexusCmp, Integer index) {
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
		} else if (cexusCmp == NexusCmp.in || cexusCmp == NexusCmp.not_in) {
			if (srcVal.getClass().isArray()) {
				srcVal = Arrays.asList((Object[]) srcVal);
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
			sqlVal = boundSqlVal(srcVal, fieldName,index);
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
			cls.append(k).append(SQL.AS).append(v.getField().getName()).append(",");
			// cls.append(k).append(",");
		});
		if (cls.length() > 0) {
			clsStr = cls.substring(0, cls.length() - 1);
		}
		return clsStr;
	}

	// @SuppressWarnings("unchecked")
	// public static <E> List<E> arrayToList(Object[] ary, Class<E> E) {
	// List<E> list = new ArrayList<E>();
	// for (Object e : ary) {
	// list.add((E) e);
	// }
	// return list;
	// }

}
