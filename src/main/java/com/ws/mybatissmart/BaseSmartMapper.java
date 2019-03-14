package com.ws.mybatissmart;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

public interface BaseSmartMapper {

	@SelectProvider(method = "countByWhere", type = BaseMapperSqlProvider.class)
	int countByWhere(@Param(Constant.PARAM_KEY) Object e,
			@Param(Constant.COND_KEY) WhereSql filterSqlBuild);

	@SelectProvider(method = "countForWhere", type = BaseMapperSqlProvider.class)
	int countForWhere(@Param("clazz") Class<?> clazz,
			@Param(Constant.COND_KEY) WhereSql filterSqlBuild);

	@InsertProvider(method = "insert", type = BaseMapperSqlProvider.class)
	int insert(@Param(Constant.PARAM_KEY) Object e);

	@UpdateProvider(method = "updateById", type = BaseMapperSqlProvider.class)
	int updateById(@Param(Constant.PARAM_KEY) Object e);

	@UpdateProvider(method = "updateByWhere", type = BaseMapperSqlProvider.class)
	int updateByWhere(@Param(Constant.PARAM_KEY) Object e,
			@Param(Constant.COND_KEY) WhereSql filterSqlBuild);

	@DeleteProvider(method = "deleteById", type = BaseMapperSqlProvider.class)
	int deleteById(Object idV, Class<?> cl);

	@DeleteProvider(method = "deleteByWhere", type = BaseMapperSqlProvider.class)
	int deleteByWhere(@Param(Constant.PARAM_KEY) Object e,
			@Param(Constant.COND_KEY) WhereSql filterSqlBuild);

	@DeleteProvider(method = "deleteForWhere", type = BaseMapperSqlProvider.class)
	int deleteForWhere(@Param("clazz") Class<?> clazz,
			@Param(Constant.COND_KEY) WhereSql filterSqlBuild);

}
