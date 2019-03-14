package com.ws.mybatissmart;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

public interface SmartMapper<E> extends BaseSmartMapper {

	@SelectProvider(method = "selectByWhere", type = BaseMapperSqlProvider.class)
	List<E> selectByWhere(@Param(Constant.PARAM_KEY) E e,
			@Param(Constant.COND_KEY) WhereSql filterSqlBuild);

	@SelectProvider(method = "selectForWhere", type = BaseMapperSqlProvider.class)
	List<E> selectForWhere(@Param("clazz") Class<E> clazz,
			@Param(Constant.COND_KEY) WhereSql filterSqlBuild);

	@SelectProvider(method = "selectById", type = BaseMapperSqlProvider.class)
	E selectById(Object idV, Class<E> cl);

}
