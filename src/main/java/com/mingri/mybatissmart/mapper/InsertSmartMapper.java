package com.mingri.mybatissmart.mapper;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;

import com.mingri.mybatissmart.barracks.Constant;
import com.mingri.mybatissmart.provider.MapperSqlProvider;

/**
 * 
 * @author ljl
 * @date 2020-11-12 Dscription:
 *
 */
public interface InsertSmartMapper<E> {

	/**
	 * 该类的函数名必须是{@code com.mingri.mybatissmart.barracks.Constant.INSERT_METHOD}
	 * 为前缀，否则MybatisSmart配置的的id自增会失效
	 * 
	 * @author ljl
	 * @date 2020-11-12
	 * @param e 要insert的对象或者集合,必须是"@SmartTable"的类或者子类
	 * @return
	 */
	@InsertProvider(method = Constant.INSERT_METHOD, type = MapperSqlProvider.class)
	int inserts(@Param(Constant.PARAM_KEY) Object e);
	
}
