package com.ws.mybatissmart;

import java.util.ArrayList;
import java.util.List;

import com.ws.commons.constant.LogicCmp;
import com.ws.commons.constant.NexusCmp;
import com.ws.commons.constant.OrderChar;
import com.ws.commons.constant.ValTypeEnum;

public class WhereSql {

	private String orderBy = "";
	private String limit = "";
	private List<WhereCond> conds = new ArrayList<WhereCond>();

	private static class Constant{
		private static final String  ORDER_BY_SQL = " order by ";
		
	}
	
	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderByDesc(String orderByFieldName) {
		this.orderBy =Constant.ORDER_BY_SQL.concat(orderByFieldName).concat(" "+OrderChar.DESC.code);
	}
	public void setOrderByAsc(String orderByFieldName) {
		this.orderBy = Constant.ORDER_BY_SQL.concat(orderByFieldName).concat(" "+OrderChar.ASC.code);
	}

	public void setConds(List<WhereCond> conds) {
		this.conds = conds;
	}

	public List<WhereCond> getConds() {
		return conds;
	}

	/*
	 * ================================================================与
	 * ================================================================条
	 * ================================================================件
	 */
	public WhereSql and(String columnName, NexusCmp nexusCmp) {
		conds.add(new WhereCond(LogicCmp.and, columnName, nexusCmp));
		return this;
	}

	public WhereSql andEq(String columnName) {
		conds.add(new WhereCond(LogicCmp.and, columnName, NexusCmp.eq));
		return this;
	}

	public WhereSql andLt(String columnName) {
		conds.add(new WhereCond(LogicCmp.and, columnName, NexusCmp.lt));
		return this;
	}

	public WhereSql andGt(String columnName) {
		conds.add(new WhereCond(LogicCmp.and, columnName, NexusCmp.gt));
		return this;
	}

	public WhereSql andLtEq(String columnName) {
		conds.add(new WhereCond(LogicCmp.and, columnName, NexusCmp.lt_eq));
		return this;
	}

	public WhereSql andGtEq(String columnName) {
		conds.add(new WhereCond(LogicCmp.and, columnName, NexusCmp.gt_eq));
		return this;
	}

	public WhereSql andLtGT(String columnName) {
		conds.add(new WhereCond(LogicCmp.and, columnName, NexusCmp.lt_gt));
		return this;
	}

	public WhereSql andLike(String columnName) {
		conds.add(new WhereCond(LogicCmp.and, columnName, NexusCmp.like));
		return this;
	}

	public WhereSql andLikeL(String columnName) {
		conds.add(new WhereCond(LogicCmp.and, columnName, NexusCmp.like_l));
		return this;
	}

	public WhereSql andLikeR(String columnName) {
		conds.add(new WhereCond(LogicCmp.and, columnName, NexusCmp.like_r));
		return this;
	}

	public WhereSql andLikeLR(String columnName) {
		conds.add(new WhereCond(LogicCmp.and, columnName, NexusCmp.like_lr));
		return this;
	}

	public WhereSql andEq(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.and, columnName, NexusCmp.eq, val));
		return this;
	}
	public WhereSql andNoEq(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.and, columnName, NexusCmp.no_eq, val));
		return this;
	}

	public WhereSql andLt(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.and, columnName, NexusCmp.lt, val));
		return this;
	}

	public WhereSql andGt(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.and, columnName, NexusCmp.gt, val));
		return this;
	}

	public WhereSql andLtEq(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.and, columnName, NexusCmp.lt_eq, val));
		return this;
	}

	public WhereSql andGtEq(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.and, columnName, NexusCmp.gt_eq, val));
		return this;
	}

	public WhereSql andLtGT(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.and, columnName, NexusCmp.lt_gt, val));
		return this;
	}

	public WhereSql andLike(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.and, columnName, NexusCmp.like, val));
		return this;
	}

	public WhereSql andLikeL(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.and, columnName, NexusCmp.like_l, val));
		return this;
	}

	public WhereSql andLikeR(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.and, columnName, NexusCmp.like_r, val));
		return this;
	}

	public WhereSql andLikeLR(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.and, columnName, NexusCmp.like_lr, val));
		return this;
	}

	public WhereSql andIsNull(String columnName) {
		conds.add(new WhereCond(LogicCmp.and, columnName, NexusCmp.is, ValTypeEnum.NULL.code));
		return this;
	}

	public WhereSql andIsNotNull(String columnName) {
		conds.add(new WhereCond(LogicCmp.and, columnName, NexusCmp.is_not, ValTypeEnum.NULL.code));
		return this;
	}
	public WhereSql andIn(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.and, columnName, NexusCmp.in, val));
		return this;
	}
	public WhereSql andNotIn(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.and, columnName, NexusCmp.not_in, val));
		return this;
	}


	/*
	 * ================================================================或
	 * ================================================================条
	 * ================================================================件
	 */
	public WhereSql or(String columnName, NexusCmp nexusCmp) {
		conds.add(new WhereCond(LogicCmp.or, columnName, nexusCmp));
		return this;
	}

	public WhereSql orEq(String columnName) {
		conds.add(new WhereCond(LogicCmp.or, columnName, NexusCmp.eq));
		return this;
	}

	public WhereSql orLt(String columnName) {
		conds.add(new WhereCond(LogicCmp.or, columnName, NexusCmp.lt));
		return this;
	}

	public WhereSql orGt(String columnName) {
		conds.add(new WhereCond(LogicCmp.or, columnName, NexusCmp.gt));
		return this;
	}

	public WhereSql orLtEq(String columnName) {
		conds.add(new WhereCond(LogicCmp.or, columnName, NexusCmp.lt_eq));
		return this;
	}

	public WhereSql orGtEq(String columnName) {
		conds.add(new WhereCond(LogicCmp.or, columnName, NexusCmp.gt_eq));
		return this;
	}

	public WhereSql orLtGT(String columnName) {
		conds.add(new WhereCond(LogicCmp.or, columnName, NexusCmp.lt_gt));
		return this;
	}

	public WhereSql orLike(String columnName) {
		conds.add(new WhereCond(LogicCmp.or, columnName, NexusCmp.like));
		return this;
	}

	public WhereSql orLikeL(String columnName) {
		conds.add(new WhereCond(LogicCmp.or, columnName, NexusCmp.like_l));
		return this;
	}

	public WhereSql orLikeR(String columnName) {
		conds.add(new WhereCond(LogicCmp.or, columnName, NexusCmp.like_r));
		return this;
	}

	public WhereSql orLikeLR(String columnName) {
		conds.add(new WhereCond(LogicCmp.or, columnName, NexusCmp.like_lr));
		return this;
	}

	public WhereSql orEq(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.or, columnName, NexusCmp.eq, val));
		return this;
	}

	public WhereSql orLt(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.or, columnName, NexusCmp.lt, val));
		return this;
	}

	public WhereSql orGt(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.or, columnName, NexusCmp.gt, val));
		return this;
	}

	public WhereSql orLtEq(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.or, columnName, NexusCmp.lt_eq, val));
		return this;
	}

	public WhereSql orGtEq(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.or, columnName, NexusCmp.gt_eq, val));
		return this;
	}

	public WhereSql orLtGT(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.or, columnName, NexusCmp.lt_gt, val));
		return this;
	}

	public WhereSql orLike(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.or, columnName, NexusCmp.like, val));
		return this;
	}

	public WhereSql orLikeL(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.or, columnName, NexusCmp.like_l, val));
		return this;
	}

	public WhereSql orLikeR(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.or, columnName, NexusCmp.like_r, val));
		return this;
	}

	public WhereSql orLikeLR(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.or, columnName, NexusCmp.like_lr, val));
		return this;
	}

	public WhereSql orIsNull(String columnName) {
		conds.add(new WhereCond(LogicCmp.or, columnName, NexusCmp.is, ValTypeEnum.NULL.code));
		return this;
	}

	public WhereSql orIsNotNull(String columnName) {
		conds.add(new WhereCond(LogicCmp.or, columnName, NexusCmp.is_not, ValTypeEnum.NULL.code));
		return this;
	}
	public WhereSql orIn(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.or, columnName, NexusCmp.in, val));
		return this;
	}
	public WhereSql orNotIn(String columnName, Object val) {
		conds.add(new WhereCond(LogicCmp.or, columnName, NexusCmp.not_in, val));
		return this;
	}

}
