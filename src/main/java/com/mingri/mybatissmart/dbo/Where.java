package com.mingri.mybatissmart.dbo;

import java.util.ArrayList;
import java.util.List;

import com.mingri.langhuan.cabinet.constant.LogicCmp;
import com.mingri.langhuan.cabinet.constant.NexusCmp;
import com.mingri.langhuan.cabinet.constant.OrderChar;
import com.mingri.langhuan.cabinet.constant.ValTypeEnum;
import com.mingri.langhuan.cabinet.tool.CollectionTool;
import com.mingri.langhuan.cabinet.tool.StrTool;
import com.mingri.mybatissmart.barracks.Constant;
import com.mingri.mybatissmart.barracks.SqlKwd;
import com.sun.istack.NotNull;

/**
 * 模仿sql的where语句<br>
 * 
 * @author ljl
 *
 */
public class Where {

	private String orderBy = StrTool.EMPTY;
	private Integer offset;
	private Integer limit;
	private List<WhereNode> nodes = new ArrayList<>();

	private String afterConditionSql;

	public Integer getLimit() {
		return limit;
	}

	public Integer getOffset() {
		return offset;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public Where() {
	}

	/**
	 * and columnName nexusCmp
	 * 
	 * @param columnName 列名称
	 * @param nexusCmp   关系运算符
	 */
	public Where(String columnName, NexusCmp nexusCmp) {
		nodes.add(new WhereNode(LogicCmp.AND, columnName, nexusCmp));
	}

	/**
	 * and columnName nexusCmp val
	 * 
	 * @param columnName 列名称
	 * @param nexusCmp   关系运算符
	 * @param val        列值
	 */
	public Where(String columnName, NexusCmp nexusCmp, Object val) {
		nodes.add(new WhereNode(LogicCmp.AND, columnName, nexusCmp, val));
	}

	public Where(LogicCmp logicCmp, String columnName, NexusCmp nexusCmp) {
		nodes.add(new WhereNode(logicCmp, columnName, nexusCmp));
	}

	public Where(LogicCmp logicCmp, String columnName, NexusCmp nexusCmp, Object val) {
		nodes.add(new WhereNode(logicCmp, columnName, nexusCmp, val));
	}

	public Where(LogicCmp logicCmp, String columnName, NexusCmp nexusCmp, Object val, boolean isStatementVal) {
		nodes.add(new WhereNode(logicCmp, columnName, nexusCmp, val, isStatementVal));
	}

	public void setLimit(int offset, int limit) {
		this.offset = offset;
		this.limit = limit;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public void setOrderByDesc(String column) {
		this.orderBy = StrTool.concat(SqlKwd.ORDER_BY, column, Constant.SPACE, OrderChar.DESC);
	}

	public void addOrderByDesc(String column) {
		if (StrTool.isEmpty(this.orderBy)) {
			this.setOrderByDesc(column);
		} else {
			this.orderBy = StrTool.concat(this.orderBy, ",", column, Constant.SPACE, OrderChar.DESC);
		}
	}

	public void setOrderByAsc(String column) {
		this.orderBy = StrTool.concat(SqlKwd.ORDER_BY, column, Constant.SPACE, OrderChar.ASC);
	}

	public void addOrderByAsc(String column) {
		if (StrTool.isEmpty(this.orderBy)) {
			this.setOrderByAsc(column);
		} else {
			this.orderBy = StrTool.concat(this.orderBy, ",", column, Constant.SPACE, OrderChar.ASC);
		}
	}

	public void setNodes(List<WhereNode> conds) {
		this.nodes = conds;
	}

	public List<WhereNode> getNodes() {
		return nodes;
	}

	public Where andChildNodes(List<WhereNode> nodes) {
		this.nodes.add(new WhereNode(LogicCmp.AND, nodes));
		return this;
	}

	public Where orChildNodes(List<WhereNode> nodes) {
		this.nodes.add(new WhereNode(LogicCmp.OR, nodes));
		return this;
	}

	public Where andChildNodes(LogicCmp splitLogicCmp, String column, NexusCmp splitNexusCmp, List<Object> valueList) {
		addChildNodes(LogicCmp.AND, splitLogicCmp, column, splitNexusCmp, valueList);
		return this;
	}

	public Where orChildNodes(LogicCmp splitLogicCmp, String column, NexusCmp splitNexusCmp, List<Object> valueList) {
		addChildNodes(LogicCmp.OR, splitLogicCmp, column, splitNexusCmp, valueList);
		return this;
	}

	public Where addChildNodes(LogicCmp logicCmp, LogicCmp splitLogicCmp, String column, NexusCmp splitNexusCmp,
			List<Object> valueList) {
		if (CollectionTool.notEmpty(valueList)) {
			List<WhereNode> childCondList = new ArrayList<>();
			for (int i = 0; i < valueList.size(); i++) {
				childCondList.add(new WhereNode(splitLogicCmp, column, splitNexusCmp, valueList.get(i)));
			}
			this.nodes.add(new WhereNode(logicCmp, childCondList));
		}
		return this;
	}

	/*
	 * ================================================================与
	 * ================================================================条
	 * ================================================================件
	 */
	public Where addNode(WhereNode whereCond) {
		this.nodes.add(whereCond);
		return this;
	}

	public Where and(String columnName, NexusCmp nexusCmp) {
		nodes.add(new WhereNode(LogicCmp.AND, columnName, nexusCmp));
		return this;
	}

	public Where andEq(String columnName) {
		nodes.add(new WhereNode(LogicCmp.AND, columnName, NexusCmp.EQ));
		return this;
	}

	public Where andLt(String columnName) {
		nodes.add(new WhereNode(LogicCmp.AND, columnName, NexusCmp.LT));
		return this;
	}

	public Where andGt(String columnName) {
		nodes.add(new WhereNode(LogicCmp.AND, columnName, NexusCmp.GT));
		return this;
	}

	public Where andLtEq(String columnName) {
		nodes.add(new WhereNode(LogicCmp.AND, columnName, NexusCmp.LT_EQ));
		return this;
	}

	public Where andGtEq(String columnName) {
		nodes.add(new WhereNode(LogicCmp.AND, columnName, NexusCmp.GT_EQ));
		return this;
	}

	public Where andLtGT(String columnName) {
		nodes.add(new WhereNode(LogicCmp.AND, columnName, NexusCmp.LT_GT));
		return this;
	}

	public Where andLike(String columnName) {
		nodes.add(new WhereNode(LogicCmp.AND, columnName, NexusCmp.LIKE));
		return this;
	}

	public Where andLikeL(String columnName) {
		nodes.add(new WhereNode(LogicCmp.AND, columnName, NexusCmp.LIKE_L));
		return this;
	}

	public Where andLikeR(String columnName) {
		nodes.add(new WhereNode(LogicCmp.AND, columnName, NexusCmp.LIKE_R));
		return this;
	}

	public Where andLikeLR(String columnName) {
		nodes.add(new WhereNode(LogicCmp.AND, columnName, NexusCmp.LIKE_LR));
		return this;
	}

	public Where andEq(String columnName, Object val) {
		nodes.add(new WhereNode(LogicCmp.AND, columnName, NexusCmp.EQ, val));
		return this;
	}

	public Where andNoEq(String columnName, Object val) {
		nodes.add(new WhereNode(LogicCmp.AND, columnName, NexusCmp.NO_EQ, val));
		return this;
	}

	public Where andLt(String columnName, Object val) {
		nodes.add(new WhereNode(LogicCmp.AND, columnName, NexusCmp.LT, val));
		return this;
	}

	public Where andGt(String columnName, Object val) {
		nodes.add(new WhereNode(LogicCmp.AND, columnName, NexusCmp.GT, val));
		return this;
	}

	public Where andLtEq(String columnName, Object val) {
		nodes.add(new WhereNode(LogicCmp.AND, columnName, NexusCmp.LT_EQ, val));
		return this;
	}

	public Where andGtEq(String columnName, Object val) {
		nodes.add(new WhereNode(LogicCmp.AND, columnName, NexusCmp.GT_EQ, val));
		return this;
	}

	public Where andLtGT(String columnName, Object val) {
		nodes.add(new WhereNode(LogicCmp.AND, columnName, NexusCmp.LT_GT, val));
		return this;
	}

	public Where andLike(String columnName, Object val) {
		nodes.add(new WhereNode(LogicCmp.AND, columnName, NexusCmp.LIKE, val));
		return this;
	}

	public Where andLikeL(String columnName, Object val) {
		nodes.add(new WhereNode(LogicCmp.AND, columnName, NexusCmp.LIKE_L, val));
		return this;
	}

	public Where andLikeR(String columnName, Object val) {
		nodes.add(new WhereNode(LogicCmp.AND, columnName, NexusCmp.LIKE_R, val));
		return this;
	}

	public Where andLikeLR(String columnName, Object val) {
		nodes.add(new WhereNode(LogicCmp.AND, columnName, NexusCmp.LIKE_LR, val));
		return this;
	}

	public Where andIsNull(String columnName) {
		nodes.add(new WhereNode(LogicCmp.AND, columnName, NexusCmp.IS, ValTypeEnum.NULL));
		return this;
	}

	public Where andIsNotNull(String columnName) {
		nodes.add(new WhereNode(LogicCmp.AND, columnName, NexusCmp.IS_NOT, ValTypeEnum.NULL));
		return this;
	}

	public Where andIn(String columnName, Object val) {
		nodes.add(new WhereNode(LogicCmp.AND, columnName, NexusCmp.IN, val));
		return this;
	}

	public Where andNotIn(String columnName, Object val) {
		nodes.add(new WhereNode(LogicCmp.AND, columnName, NexusCmp.NOT_IN, val));
		return this;
	}

	/*
	 * ================================================================或
	 * ================================================================条
	 * ================================================================件
	 */
	public Where or(String columnName, NexusCmp nexusCmp) {
		nodes.add(new WhereNode(LogicCmp.OR, columnName, nexusCmp));
		return this;
	}

	public Where orEq(String columnName) {
		nodes.add(new WhereNode(LogicCmp.OR, columnName, NexusCmp.EQ));
		return this;
	}

	public Where orLt(String columnName) {
		nodes.add(new WhereNode(LogicCmp.OR, columnName, NexusCmp.LT));
		return this;
	}

	public Where orGt(String columnName) {
		nodes.add(new WhereNode(LogicCmp.OR, columnName, NexusCmp.GT));
		return this;
	}

	public Where orLtEq(String columnName) {
		nodes.add(new WhereNode(LogicCmp.OR, columnName, NexusCmp.LT_EQ));
		return this;
	}

	public Where orGtEq(String columnName) {
		nodes.add(new WhereNode(LogicCmp.OR, columnName, NexusCmp.GT_EQ));
		return this;
	}

	public Where orLtGT(String columnName) {
		nodes.add(new WhereNode(LogicCmp.OR, columnName, NexusCmp.LT_GT));
		return this;
	}

	public Where orLike(String columnName) {
		nodes.add(new WhereNode(LogicCmp.OR, columnName, NexusCmp.LIKE));
		return this;
	}

	public Where orLikeL(String columnName) {
		nodes.add(new WhereNode(LogicCmp.OR, columnName, NexusCmp.LIKE_L));
		return this;
	}

	public Where orLikeR(String columnName) {
		nodes.add(new WhereNode(LogicCmp.OR, columnName, NexusCmp.LIKE_R));
		return this;
	}

	public Where orLikeLR(String columnName) {
		nodes.add(new WhereNode(LogicCmp.OR, columnName, NexusCmp.LIKE_LR));
		return this;
	}

	public Where orEq(String columnName, Object val) {
		nodes.add(new WhereNode(LogicCmp.OR, columnName, NexusCmp.EQ, val));
		return this;
	}

	public Where orLt(String columnName, Object val) {
		nodes.add(new WhereNode(LogicCmp.OR, columnName, NexusCmp.LT, val));
		return this;
	}

	public Where orGt(String columnName, Object val) {
		nodes.add(new WhereNode(LogicCmp.OR, columnName, NexusCmp.GT, val));
		return this;
	}

	public Where orLtEq(String columnName, Object val) {
		nodes.add(new WhereNode(LogicCmp.OR, columnName, NexusCmp.LT_EQ, val));
		return this;
	}

	public Where orGtEq(String columnName, Object val) {
		nodes.add(new WhereNode(LogicCmp.OR, columnName, NexusCmp.GT_EQ, val));
		return this;
	}

	public Where orLtGT(String columnName, Object val) {
		nodes.add(new WhereNode(LogicCmp.OR, columnName, NexusCmp.LT_GT, val));
		return this;
	}

	public Where orLike(String columnName, Object val) {
		nodes.add(new WhereNode(LogicCmp.OR, columnName, NexusCmp.LIKE, val));
		return this;
	}

	public Where orLikeL(String columnName, Object val) {
		nodes.add(new WhereNode(LogicCmp.OR, columnName, NexusCmp.LIKE_L, val));
		return this;
	}

	public Where orLikeR(String columnName, Object val) {
		nodes.add(new WhereNode(LogicCmp.OR, columnName, NexusCmp.LIKE_R, val));
		return this;
	}

	public Where orLikeLR(String columnName, Object val) {
		nodes.add(new WhereNode(LogicCmp.OR, columnName, NexusCmp.LIKE_LR, val));
		return this;
	}

	public Where orIsNull(String columnName) {
		nodes.add(new WhereNode(LogicCmp.OR, columnName, NexusCmp.IS, ValTypeEnum.NULL));
		return this;
	}

	public Where orIsNotNull(String columnName) {
		nodes.add(new WhereNode(LogicCmp.OR, columnName, NexusCmp.IS_NOT, ValTypeEnum.NULL));
		return this;
	}

	public Where orIn(String columnName, Object val) {
		nodes.add(new WhereNode(LogicCmp.OR, columnName, NexusCmp.IN, val));
		return this;
	}

	public Where orNotIn(String columnName, Object val) {
		nodes.add(new WhereNode(LogicCmp.OR, columnName, NexusCmp.NOT_IN, val));
		return this;
	}

	public String getAfterConditionSql() {
		return afterConditionSql;
	}

	public void setAfterConditionSql(String afterConditionSql) {
		this.afterConditionSql = afterConditionSql;
	}

	public Where removeCond(String columnName) {
		nodes.removeIf(cond -> cond.getColumnName().equals(columnName));
		return this;
	}
}
