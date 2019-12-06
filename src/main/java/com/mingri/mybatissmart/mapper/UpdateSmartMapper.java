package com.mingri.mybatissmart.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;

import com.mingri.mybatissmart.barracks.Constant;
import com.mingri.mybatissmart.dbo.Where;
import com.mingri.mybatissmart.provider.MapperSqlProvider;

public interface UpdateSmartMapper<E>{
	
	@UpdateProvider(method = "updateById", type = MapperSqlProvider.class)
	int updateById(@Param(Constant.PARAM_KEY) Object e);

	@UpdateProvider(method = "updateByWhere", type = MapperSqlProvider.class)
	int updateByWhere(@Param(Constant.PARAM_KEY) Object e,
			@Param(Constant.COND_KEY) Where filterSqlBuild);

}
