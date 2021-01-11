package com.mingri.mybatissmart.mapper;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Param;

import com.mingri.mybatissmart.barracks.Constant;
import com.mingri.mybatissmart.dbo.Where;
import com.mingri.mybatissmart.provider.MapperSqlProvider;

public interface DeleteMapper<E>{
	
	@DeleteProvider(method = "deleteById", type = MapperSqlProvider.class)
	int deleteById(Object id);

	@DeleteProvider(method = "delete", type = MapperSqlProvider.class)
	int deleteByWhere(@Param(Constant.COND_KEY) Where filterSqlBuild);
}
