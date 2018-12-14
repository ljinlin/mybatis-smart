package com.ws.mybatissmart;

import java.util.List;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

public interface SmartMapper<E> {

	@SelectProvider(method = "selectByWhere", type = BaseMapperSqlProvider.class)
	List<E> selectByWhere(@Param(MybatisSmartAutoConfiguration.E_K)E e, @Param(MybatisSmartAutoConfiguration.C_K)WhereSql filterSqlBuild);
	
	@SelectProvider(method = "selectForWhere", type = BaseMapperSqlProvider.class)
    List<E> selectForWhere(@Param("clazz")Class<E> clazz,@Param(MybatisSmartAutoConfiguration.C_K)WhereSql filterSqlBuild);

	@SelectProvider(method = "countByWhere", type = BaseMapperSqlProvider.class)
	int countByWhere(@Param(MybatisSmartAutoConfiguration.E_K)E e, @Param(MybatisSmartAutoConfiguration.C_K)WhereSql filterSqlBuild);
	@SelectProvider(method = "countForWhere", type = BaseMapperSqlProvider.class)
	int countForWhere(@Param("clazz")Class<E> clazz, @Param(MybatisSmartAutoConfiguration.C_K)WhereSql filterSqlBuild);

	@SelectProvider(method = "selectById", type = BaseMapperSqlProvider.class)
	E selectById(Object idV, Class<E> cl);

	@InsertProvider(method = "insert", type = BaseMapperSqlProvider.class)
	int insert(@Param(MybatisSmartAutoConfiguration.E_K)E e);

	@UpdateProvider(method = "updateById", type = BaseMapperSqlProvider.class)
	int updateById(@Param(MybatisSmartAutoConfiguration.E_K)E e);

	@DeleteProvider(method = "deleteById", type = BaseMapperSqlProvider.class)
	int deleteById(Object idV, Class<E> cl);

	@DeleteProvider(method = "deleteByWhere", type = BaseMapperSqlProvider.class)
	int deleteByWhere(@Param(MybatisSmartAutoConfiguration.E_K)E e, @Param(MybatisSmartAutoConfiguration.C_K)WhereSql filterSqlBuild);

	@Select("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME=#{tableName}")
	List<String> selectFields(@Param("tableName") String tableName);

	static final String SELECTFIELDS_STATEMENT = SmartMapper.class.getCanonicalName() + ".selectFields";
}
