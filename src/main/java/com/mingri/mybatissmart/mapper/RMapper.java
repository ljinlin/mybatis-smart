package com.mingri.mybatissmart.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import com.mingri.mybatissmart.barracks.Constant;
import com.mingri.mybatissmart.dbo.Where;
import com.mingri.mybatissmart.provider.MapperSqlProvider;

/**
 * 读mapper
 * @author jinlin Li
 * 2021年1月8日
 * @param <E>
 */
public interface RMapper<E> {

	
	@SelectProvider(method = "select", type = MapperSqlProvider.class)
	List<E> selectByWhere(@Param(Constant.COND_KEY) Where where);
	

	@SelectProvider(method = "select", type = MapperSqlProvider.class)
	E getByWhere(@Param(Constant.COND_KEY) Where where);
	

	@SelectProvider(method = "selectById", type = MapperSqlProvider.class)
	E getById(Object id);
	

	@SelectProvider(method = "count", type = MapperSqlProvider.class)
	int countByWhere(@Param(Constant.COND_KEY) Where where);

}
