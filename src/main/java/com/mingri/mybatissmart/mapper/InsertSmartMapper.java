package com.mingri.mybatissmart.mapper;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;

import com.mingri.mybatissmart.barracks.Constant;
import com.mingri.mybatissmart.provider.MapperSqlProvider;

public interface InsertSmartMapper<E>{
	
	@InsertProvider(method = "insert", type = MapperSqlProvider.class)
	int insert(@Param(Constant.PARAM_KEY) Object e);
}
