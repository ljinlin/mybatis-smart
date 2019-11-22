package com.mingri.mybatissmart.dbo;

import java.util.ArrayList;
import java.util.List;

import com.mingri.langhuan.cabinet.constant.LogicCmp;
import com.mingri.langhuan.cabinet.constant.NexusCmp;
import com.mingri.langhuan.cabinet.constant.OrderChar;
import com.mingri.langhuan.cabinet.constant.ValTypeEnum;
import com.mingri.langhuan.cabinet.tool.StrTool;
import com.mingri.mybatissmart.barracks.Constant;
import com.mingri.mybatissmart.barracks.SqlKwd;

/**
 * 模仿sql的where语句
 * @author vn0wr5w
 *
 */
public class WhereSql {

	private String orderBy = StrTool.EMPTY;
	private Integer offset;
	private Integer limit;
	private List<WhereCond> conds = new ArrayList<>();

	private String nativeSqlConds;
	
	public Integer getLimit() {
		return limit;
	}
	public Integer getOffset() {
		return offset;
	}

	public void setLimit(int offset,int limit) {
		this.offset = offset;
		this.limit = limit;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderByDesc(String orderByFieldName) {
		this.orderBy = SqlKwd.ORDER_BY_SQL.concat(orderByFieldName).concat(Constant.SPACE + OrderChar.DESC);
	}

	public void setOrderByAsc(String orderByFieldName) {
		this.orderBy = SqlKwd.ORDER_BY_SQL.concat(orderByFieldName).concat(Constant.SPACE + OrderChar.ASC);
	}

	public void setConds(List<WhereCond> conds) {
		this.conds = conds;
	}

	public List<WhereCond> getConds() {
		return conds;
	}
	
	public WhereSql andChildConds(List<WhereCond> conds) {
		conds.add(new WhereCond(LogicCmp.AND, conds));
		return this;
	}
	public WhereSql orChildConds(List<WhereCond> conds) {
		conds.add(new WhereCond(LogicCmp.OR, conds));
		return this;
	}

	/*
	 * ================================================================与
	 * ================================================================条
	 * ================================================================件
	 */
	public WhereSql addCond(WhereCond whereCond) {
		this.conds.add(whereCond);
		return this;
	}
	public WhereSql and(String columnName, NexusCmp nexusCmp) {
		conds.add(new WhereCond(LogicCmp.AND, columnName, nexusCmp));
		return this;
	}

	public WhereSql andEq(String columnName) {
		conds.add(new WhereCond(LogicCmp.AND, columnName, NexusCmp.eq));
		return this;
	}

	public WhereSql andLt(String columnName) {
		conds.add(new WhereCond(LogicCmp.AND, columnName, NexusCmp.lt));
		return this;
	}

	public WhereSql andGt(String columnName) {
		conds.add(new WhereCond(LogicCmp.AND, columnName, NexusCmp.gt));
		return this;
	}

	public WhereSql andLtEq(String columnName) {
		conds.add(new WhereCond(LogicCmp.AND, columnName, NexusCmp.lt_eq));
		return this;
	}

	public WhereSql andGtEq(String columnName) {
		conds.add(new WhereCond(LogicCmp.AND, columnName, NexusCmp.gt_eq));
		return this;
	}

	public WhereSql andLtGT(String columnName) {
		conds.add(new WhereCond(LogicCmp.AND, columnName, NexusCmp.lt_gt));
		return this;
	}

	public WhereSql andLike(String columnName) {
		conds.add(new WhereCond(LogicCmp.AND, columnName, NexusCmp.like));
		return this;
	}

	public WhereSql andLikeL(String columnName) {
		conds.add(new WhereCond(LogicCmp.AND, columnName, NexusCmp.like_l));
		return this;
	}

	public WhereSql andLikeR(String columnName) {
		conds.add(new WhereCond(LogicCmp.AND, columnName, NexusCmp.like_r));
		return this;
	}

	public WhereSql andLikeLR(String columnName) {
		conds.add(new WhereCond(LogicCmp.AND, columnName, NexusCmp.like_lr));
		return this;
	}

	public WhereSql andEq(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.AND, columnName, NexusCmp.eq, val));
		return this;
	}

	public WhereSql andNoEq(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.AND, columnName, NexusCmp.no_eq, val));
		return this;
	}

	public WhereSql andLt(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.AND, columnName, NexusCmp.lt, val));
		return this;
	}

	public WhereSql andGt(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.AND, columnName, NexusCmp.gt, val));
		return this;
	}

	public WhereSql andLtEq(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.AND, columnName, NexusCmp.lt_eq, val));
		return this;
	}

	public WhereSql andGtEq(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.AND, columnName, NexusCmp.gt_eq, val));
		return this;
	}

	public WhereSql andLtGT(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.AND, columnName, NexusCmp.lt_gt, val));
		return this;
	}

	public WhereSql andLike(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.AND, columnName, NexusCmp.like, val));
		return this;
	}

	public WhereSql andLikeL(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.AND, columnName, NexusCmp.like_l, val));
		return this;
	}

	public WhereSql andLikeR(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.AND, columnName, NexusCmp.like_r, val));
		return this;
	}

	public WhereSql andLikeLR(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.AND, columnName, NexusCmp.like_lr, val));
		return this;
	}

	public WhereSql andIsNull(String columnName) {
		conds.add(new WhereCond(LogicCmp.AND, columnName, NexusCmp.is, ValTypeEnum.NULL.code));
		return this;
	}

	public WhereSql andIsNotNull(String columnName) {
		conds.add(new WhereCond(LogicCmp.AND, columnName, NexusCmp.is_not, ValTypeEnum.NULL.code));
		return this;
	}

	public WhereSql andIn(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.AND, columnName, NexusCmp.in, val));
		return this;
	}

	public WhereSql andNotIn(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.AND, columnName, NexusCmp.not_in, val));
		return this;
	}

	/*
	 * ================================================================或
	 * ================================================================条
	 * ================================================================件
	 */
	public WhereSql or(String columnName, NexusCmp nexusCmp) {
		conds.add(new WhereCond(LogicCmp.OR, columnName, nexusCmp));
		return this;
	}

	public WhereSql orEq(String columnName) {
		conds.add(new WhereCond(LogicCmp.OR, columnName, NexusCmp.eq));
		return this;
	}

	public WhereSql orLt(String columnName) {
		conds.add(new WhereCond(LogicCmp.OR, columnName, NexusCmp.lt));
		return this;
	}

	public WhereSql orGt(String columnName) {
		conds.add(new WhereCond(LogicCmp.OR, columnName, NexusCmp.gt));
		return this;
	}

	public WhereSql orLtEq(String columnName) {
		conds.add(new WhereCond(LogicCmp.OR, columnName, NexusCmp.lt_eq));
		return this;
	}

	public WhereSql orGtEq(String columnName) {
		conds.add(new WhereCond(LogicCmp.OR, columnName, NexusCmp.gt_eq));
		return this;
	}

	public WhereSql orLtGT(String columnName) {
		conds.add(new WhereCond(LogicCmp.OR, columnName, NexusCmp.lt_gt));
		return this;
	}

	public WhereSql orLike(String columnName) {
		conds.add(new WhereCond(LogicCmp.OR, columnName, NexusCmp.like));
		return this;
	}

	public WhereSql orLikeL(String columnName) {
		conds.add(new WhereCond(LogicCmp.OR, columnName, NexusCmp.like_l));
		return this;
	}

	public WhereSql orLikeR(String columnName) {
		conds.add(new WhereCond(LogicCmp.OR, columnName, NexusCmp.like_r));
		return this;
	}

	public WhereSql orLikeLR(String columnName) {
		conds.add(new WhereCond(LogicCmp.OR, columnName, NexusCmp.like_lr));
		return this;
	}

	public WhereSql orEq(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.OR, columnName, NexusCmp.eq, val));
		return this;
	}

	public WhereSql orLt(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.OR, columnName, NexusCmp.lt, val));
		return this;
	}

	public WhereSql orGt(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.OR, columnName, NexusCmp.gt, val));
		return this;
	}

	public WhereSql orLtEq(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.OR, columnName, NexusCmp.lt_eq, val));
		return this;
	}

	public WhereSql orGtEq(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.OR, columnName, NexusCmp.gt_eq, val));
		return this;
	}

	public WhereSql orLtGT(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.OR, columnName, NexusCmp.lt_gt, val));
		return this;
	}

	public WhereSql orLike(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.OR, columnName, NexusCmp.like, val));
		return this;
	}

	public WhereSql orLikeL(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.OR, columnName, NexusCmp.like_l, val));
		return this;
	}

	public WhereSql orLikeR(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.OR, columnName, NexusCmp.like_r, val));
		return this;
	}

	public WhereSql orLikeLR(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.OR, columnName, NexusCmp.like_lr, val));
		return this;
	}

	public WhereSql orIsNull(String columnName) {
		conds.add(new WhereCond(LogicCmp.OR, columnName, NexusCmp.is, ValTypeEnum.NULL.code));
		return this;
	}

	public WhereSql orIsNotNull(String columnName) {
		conds.add(new WhereCond(LogicCmp.OR, columnName, NexusCmp.is_not, ValTypeEnum.NULL.code));
		return this;
	}

	public WhereSql orIn(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.OR, columnName, NexusCmp.in, val));
		return this;
	}

	public WhereSql orNotIn(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.OR, columnName, NexusCmp.not_in, val));
		return this;
	}
	public String getNativeSqlConds() {
		return nativeSqlConds;
	}
	public void setNativeSqlConds(String nativeSqlConds) {
		this.nativeSqlConds = nativeSqlConds;
	}
	
	public WhereSql removeCond(String columnName) {
		conds.removeIf(cond->cond.getColumnName().equals(columnName));
		return this;
	}
}
