package com.mingri.mybatissmart.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import com.mingri.mybatissmart.barracks.Constant;
import com.mingri.mybatissmart.dbo.WhereSql;
import com.mingri.mybatissmart.provider.MapperSqlProvider;

public interface SmartMapper<E> extends BaseSmartMapper {

	@SelectProvider(method = "select", type = MapperSqlProvider.class)
	List<E> selectByObjAndWere(@Param(Constant.PARAM_KEY) E e, @Param(Constant.COND_KEY) WhereSql filterSqlBuild);

	@SelectProvider(method = "select", type = MapperSqlProvider.class)
	List<E> selectByWere(@Param(Constant.PARAM_KEY) Class<E> clazz, @Param(Constant.COND_KEY) WhereSql filterSqlBuild);

	@SelectProvider(method = "select", type = MapperSqlProvider.class)
	E selectOneByWere(@Param("clazz") Class<E> clazz, @Param(Constant.COND_KEY) WhereSql filterSqlBuild);

	@SelectProvider(method = "selectById", type = MapperSqlProvider.class)
	E selectById(Object idV, Class<E> cl);

}
