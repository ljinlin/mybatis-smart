package com.ws.mybatissmart;

import java.util.ArrayList;
import java.util.List;

import com.ws.commons.constant.CmpChar;

public class WhereSql {

	private String orderBy="";
	private String limit="";
	private List<WhereCond> conds=new ArrayList<WhereCond>();
	
	
	public String getLimit() {
		return limit;
	}
	public void setLimit(String limit) {
		this.limit = limit;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy,boolean isAsc) {
		this.orderBy = orderBy;
		if(orderBy!=null&&orderBy.length()>0&&!isAsc){
			orderBy=orderBy.concat(" desc");
		}
	}
	public void setConds(List<WhereCond> conds) {
		this.conds = conds;
	}


	public WhereSql add(String nemeColumn,CmpChar cmpChar){
		conds.add(new WhereCond(nemeColumn, cmpChar));
		return this;
	}
	public WhereSql addEq(String nemeColumn){
		conds.add(new WhereCond(nemeColumn, CmpChar.eq));
		return this;
	}
	
	public WhereSql addLt(String nemeColumn){
		conds.add(new WhereCond(nemeColumn, CmpChar.lt));
		return this;
	}
	public WhereSql addGt(String nemeColumn){
		conds.add(new WhereCond(nemeColumn, CmpChar.gt));
		return this;
	}
	public WhereSql addLtEq(String nemeColumn){
		conds.add(new WhereCond(nemeColumn, CmpChar.lt_eq));
		return this;
	}
	public WhereSql addGtEq(String nemeColumn){
		conds.add(new WhereCond(nemeColumn, CmpChar.gt_eq));
		return this;
	}
	
	public WhereSql addLtGT(String nemeColumn){
		conds.add(new WhereCond(nemeColumn, CmpChar.lt_gt));
		return this;
	}
	public WhereSql addLike(String nemeColumn){
		conds.add(new WhereCond(nemeColumn, CmpChar.like));
		return this;
	}
	public WhereSql addEq(String nemeColumn,Object val){
		conds.add(new WhereCond(nemeColumn, CmpChar.eq,val));
		return this;
	}
	
	public WhereSql addLt(String nemeColumn,Object val){
		conds.add(new WhereCond(nemeColumn, CmpChar.lt,val));
		return this;
	}
	public WhereSql addGt(String nemeColumn,Object val){
		conds.add(new WhereCond(nemeColumn, CmpChar.gt,val));
		return this;
	}
	public WhereSql addLtEq(String nemeColumn,Object val){
		conds.add(new WhereCond(nemeColumn, CmpChar.lt_eq,val));
		return this;
	}
	public WhereSql addGtEq(String nemeColumn,Object val){
		conds.add(new WhereCond(nemeColumn, CmpChar.gt_eq,val));
		return this;
	}
	
	public WhereSql addLtGT(String nemeColumn,Object val){
		conds.add(new WhereCond(nemeColumn, CmpChar.lt_gt,val));
		return this;
	}
	public WhereSql addLike(String nemeColumn,Object val){
		conds.add(new WhereCond(nemeColumn, CmpChar.like,val));
		return this;
	}
	public List<WhereCond> getConds(){
		return conds;
	}
	
}
