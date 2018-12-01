package com.ws.mybatissmart;

import java.util.List;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

public interface SmartMapper<E> {

	@SelectProvider(method = "selectList", type = BaseMapperSqlProvider.class)
	List<E> selectList(E e, FilterSqlBuild filterSqlBuild);

	@SelectProvider(method = "selectById", type = BaseMapperSqlProvider.class)
	E selectById(Object idV, Class<E> cl);

	@InsertProvider(method = "insert", type = BaseMapperSqlProvider.class)
	int insert(E e);

	@UpdateProvider(method = "updateById", type = BaseMapperSqlProvider.class)
	int updateById(E e);

	@UpdateProvider(method = "deleteById", type = BaseMapperSqlProvider.class)
	int deleteById(Object idV, Class<E> cl);

	@Select("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME=#{tableName}")
	List<String> selectFields(@Param("tableName") String tableName);

	static final String SELECTFIELDS_STATEMENT = SmartMapper.class.getCanonicalName() + ".selectFields";
}
