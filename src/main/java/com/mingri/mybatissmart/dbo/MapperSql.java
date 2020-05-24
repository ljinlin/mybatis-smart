package com.mingri.mybatissmart.dbo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mingri.langhuan.cabinet.constant.LogicCmp;
import com.mingri.langhuan.cabinet.constant.NexusCmp;
import com.mingri.langhuan.cabinet.constant.ValTypeEnum;
import com.mingri.langhuan.cabinet.tool.StrTool;
import com.mingri.mybatissmart.MybatisSmartException;
import com.mingri.mybatissmart.barracks.Constant;
import com.mingri.mybatissmart.barracks.DialectEnum;
import com.mingri.mybatissmart.barracks.SqlKwd;
import com.mingri.mybatissmart.provider.MapperSqlProvider;

class MapperSql {
	private static final Logger LOGGER = LoggerFactory.getLogger(MapperSql.class);
	private StringBuilder statementSql = new StringBuilder();

	private MapperSql() {
	}

	class Select {

		private WhereSql where;
		private String orderBy;
		private Integer limit;
		private Integer offset;
		private DialectEnum dialect;

		Select(String columns, String table) {
			statementSql = new StringBuilder(SqlKwd.SELECT).append(columns).append(SqlKwd.FROM).append(table);
		}

		Select where(WhereSql where) {
			this.where = where;
			return this;
		}

		Select orderBy(String orderBy) {
			this.orderBy = orderBy;
			return this;
		}

		Select limit(Integer limit, Integer offset, DialectEnum dialect) {
			this.limit = limit;
			this.offset = offset;
			this.dialect = dialect;
			return this;
		}

		MapperSql build() {
			if (where != null) {
				statementSql.append(where.build());
			}
			if (StrTool.checkNotEmpty(orderBy)) {
				statementSql.append(orderBy);
			}

			if (dialect != null) {

				switch (dialect) {
				case MYSQL:
					if (limit != null) {
						statementSql.append(SqlKwd.LIMIT).append(limit);
						if (offset != null) {
							statementSql.append(SqlKwd.OFFSET).append(offset);

						}
					}
					break;
				case SQLSERVER:
					if (limit != null && offset != null) {
						statementSql.append(SqlKwd.OFFSET).append(offset).append(" rows fetch next ").append(limit)
								.append(" rows only ");
					}
					break;
				default:
					break;
				}
			}

			return MapperSql.this;
		}
	}

	class Insert {

		private StringBuilder intoColumns = null;
		private StringBuilder intoValues = null;

		Insert(String table) {
			intoColumns = new StringBuilder();
			intoValues = new StringBuilder();
			statementSql.append(SqlKwd.INSERT_INTO).append(table);
		}

		Insert intoColumn(String column) {
			intoColumns.append(column).append(",");
			return this;
		}

		Insert intoColumn(String column, Integer index) {
			if (index == null || index == 0) {
				intoColumns.append(column).append(",");
			}
			return this;
		}

		Insert intoValue(String value) {
			intoValues.append(value).append(",");
			return this;
		}

		Insert intoRowEnd() {
			intoValues.deleteCharAt(intoValues.length() - 1).append("),(");
			return this;
		}

		MapperSql build() {
			if (StrTool.isEmpty(intoColumns)) {
				return MapperSql.this;
			}
			statementSql.append(" (").append(intoColumns.deleteCharAt(intoColumns.length() - 1).append(")"));
			intoValues.insert(0, " values(");
			statementSql.append(intoValues.delete(intoValues.length() - 2, intoValues.length()));
			return MapperSql.this;
		}

	}

	class Delete {

		private WhereSql where = null;

		Delete(String table) {
			statementSql = new StringBuilder(SqlKwd.DELETE).append(SqlKwd.FROM).append(table);
		}

		Delete setWhere(WhereSql where) {
			this.where = where;
			return this;
		}

		MapperSql build() {
			if (where != null) {
				statementSql.append(where.build());
			}
			return MapperSql.this;
		}
	}

	class Update {

		private StringBuilder sets = new StringBuilder();
		private WhereSql where = null;

		Update(String table,SetSql setSql) {
			statementSql.append(SqlKwd.UPDATE).append(table).append(SqlKwd.SET);
			setSql.getSets().forEach((k,v)->{
				if(setSql.isSetEmpty()||StrTool.checkNotEmpty(v)) {
					this.set(k, StrTool.concat("#{",Constant.PARAM_KEY,".sets.",k,"}"));
				}
			});
		}

		Update(String table) {
			statementSql.append(SqlKwd.UPDATE).append(table).append(SqlKwd.SET);
		}

		Update set(String column, String value) {
			sets.append(column).append("=").append(value).append(",");
			return this;
		}

		Update setWhere(WhereSql where) {
			this.where = where;
			return this;
		}

		MapperSql build() {
			if(sets.length()==0) {
				throw new MybatisSmartException("update必须要有set语句段");
			}
			statementSql.append(sets.substring(0, sets.length() - 1));
			if (where != null) {
				statementSql.append(where.build());
			}
			return MapperSql.this;
		}
	}

	private static final String OR_UPCASE = LogicCmp.OR.code.toUpperCase();
	private static final String AND_UPCASE = LogicCmp.AND.code.toUpperCase();

	class WhereSql {

		private StringBuilder nodes = new StringBuilder();

		WhereSql add(LogicCmp logicCmp, String columnName, NexusCmp nexusCmp, String columnVal) {
			if (nodes.length() > 0 && logicCmp != null) {
				nodes.append(Constant.SPACE).append(logicCmp.code);
			}
			nodes.append(Constant.SPACE);
			nodes.append(columnName).append(Constant.SPACE).append(nexusCmp.code).append(Constant.SPACE)
					.append(columnVal).append(Constant.SPACE);
			return this;
		}

		WhereSql add(LogicCmp logicCmp, WhereSql whereSql) {
			if (nodes.length() > 0 && logicCmp != null) {
				nodes.append(Constant.SPACE).append(logicCmp.code);
			}
			nodes.append(Constant.SPACE);
			if (whereSql != null && whereSql.nodes.length() > 0) {
				String condSql = trimCondSql(whereSql.nodes.toString());
				nodes.append(" ( ").append(condSql).append(" ) ");
			}
			return this;
		}

		WhereSql add(String afterConditionSql) {
			if (nodes.length() > 0) {
				nodes.append(Constant.SPACE).append(afterConditionSql);
			} else {
				afterConditionSql = trimCondSql(afterConditionSql);
				nodes.append(Constant.SPACE).append(afterConditionSql);
			}
			return this;
		}

		private String trimCondSql(String condSql) {
			condSql = condSql.trim();
			int len = condSql.length();
			condSql = StrTool.trimEdge(condSql, LogicCmp.OR.code);
			if (len == condSql.length())
				condSql = StrTool.trimEdge(condSql, LogicCmp.AND.code);
			if (len == condSql.length())
				condSql = StrTool.trimEdge(condSql, OR_UPCASE);
			if (len == condSql.length())
				condSql = StrTool.trimEdge(condSql, AND_UPCASE);
			return condSql;
		}

		boolean isEmpty() {
			return nodes.length() == 0;
		}

		String build() {
			if (nodes.length() > 0) {
				nodes.insert(0, SqlKwd.WHERE_PRE);
			}
			return nodes.toString();

		}

	}

	static Insert insertInto(String table) {
		return new MapperSql().new Insert(table);
	}

	static Delete delete(String table) {
		return new MapperSql().new Delete(table);
	}

	static Update update(String table) {
		return new MapperSql().new Update(table);
	}
	static Update update(String table,SetSql sets) {
		return new MapperSql().new Update(table,sets);
	}

	static Select select(String columns, String table) {
		return new MapperSql().new Select(columns, table);
	}

	static WhereSql where() {
		return new MapperSql().new WhereSql();
	}

	@Override
	public String toString() {
		return statementSql.toString();
	}

}
