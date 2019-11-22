package com.mingri.mybatissmart.mapper;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import com.mingri.mybatissmart.barracks.Constant;
import com.mingri.mybatissmart.dbo.WhereSql;
import com.mingri.mybatissmart.provider.MapperSqlProvider;

public interface BaseSmartMapper {

	@SelectProvider(method = "count", type = MapperSqlProvider.class)
	int countByObjAndWere(@Param(Constant.PARAM_KEY) Object e,
			@Param(Constant.COND_KEY) WhereSql filterSqlBuild);

	@SelectProvider(method = "count", type = MapperSqlProvider.class)
	int countByWhere(@Param(Constant.PARAM_KEY) Class<?> clazz,
			@Param(Constant.COND_KEY) WhereSql filterSqlBuild);

	@InsertProvider(method = "insert", type = MapperSqlProvider.class)
	int insert(@Param(Constant.PARAM_KEY) Object e);

	@UpdateProvider(method = "updateById", type = MapperSqlProvider.class)
	int updateById(@Param(Constant.PARAM_KEY) Object e);

	@UpdateProvider(method = "updateByWhere", type = MapperSqlProvider.class)
	int updateByWhere(@Param(Constant.PARAM_KEY) Object e,
			@Param(Constant.COND_KEY) WhereSql filterSqlBuild);
	
	@DeleteProvider(method = "deleteById", type = MapperSqlProvider.class)
	int deleteById(Object idV, Class<?> cl);

	@DeleteProvider(method = "delete", type = MapperSqlProvider.class)
	int deleteByObjAndWere(@Param(Constant.PARAM_KEY) Object e,
			@Param(Constant.COND_KEY) WhereSql filterSqlBuild);

	@DeleteProvider(method = "delete", type = MapperSqlProvider.class)
	int deleteByWhere(@Param(Constant.PARAM_KEY) Class<?> clazz,
			@Param(Constant.COND_KEY) WhereSql filterSqlBuild);

}
