package com.mingri.mybatissmart.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import com.mingri.mybatissmart.barracks.Constant;
import com.mingri.mybatissmart.dbo.Where;
import com.mingri.mybatissmart.provider.MapperSqlProvider;

/**
 *  只读
 * @author ljl
 *
 * @param <E> 实体类
 */
public interface SelectSmartMapper<E>{
	
	@SelectProvider(method = "select", type = MapperSqlProvider.class)
	List<E> selectByObjAndWhere(@Param(Constant.PARAM_KEY) E e, @Param(Constant.COND_KEY) Where filterSqlBuild);

	@SelectProvider(method = "select", type = MapperSqlProvider.class)
	List<E> selectByWhere(@Param(Constant.PARAM_KEY) Class<E> clazz, @Param(Constant.COND_KEY) Where filterSqlBuild);

	@SelectProvider(method = "select", type = MapperSqlProvider.class)
	E selectOneByWhere(@Param(Constant.PARAM_KEY) Class<E> clazz, @Param(Constant.COND_KEY) Where filterSqlBuild);

	@SelectProvider(method = "selectById", type = MapperSqlProvider.class)
	E selectById(Object idV, Class<E> cl);
	
	@SelectProvider(method = "count", type = MapperSqlProvider.class)
	int countByObjAndWhere(@Param(Constant.PARAM_KEY) Object e,
			@Param(Constant.COND_KEY) Where filterSqlBuild);

	@SelectProvider(method = "count", type = MapperSqlProvider.class)
	int countByWhere(@Param(Constant.PARAM_KEY) Class<?> clazz,
			@Param(Constant.COND_KEY) Where filterSqlBuild);

}
