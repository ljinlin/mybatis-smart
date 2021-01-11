package com.mingri.mybatissmart.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;

import com.mingri.mybatissmart.barracks.Constant;
import com.mingri.mybatissmart.dbo.SetSql;
import com.mingri.mybatissmart.dbo.Where;
import com.mingri.mybatissmart.provider.MapperSqlProvider;

public interface UpdateMapper<E>{
	
	@UpdateProvider(method = "updateById", type = MapperSqlProvider.class)
	int updateById(@Param(Constant.PARAM_KEY) E e);

	@UpdateProvider(method = "updateBySets", type = MapperSqlProvider.class)
	int updateBySets(@Param(Constant.PARAM_KEY) SetSql sets,
			@Param(Constant.COND_KEY) Where where);
	
	@UpdateProvider(method = "updateByWhere", type = MapperSqlProvider.class)
	int updateByWhere(@Param(Constant.PARAM_KEY) E e,
			@Param(Constant.COND_KEY) Where where);
	

}
