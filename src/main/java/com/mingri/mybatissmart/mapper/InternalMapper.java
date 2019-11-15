package com.mingri.mybatissmart.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 内部mapper,不提供外外部使用
 * @author ljl
 *
 */
public interface InternalMapper {
	
	@Select("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME=#{tableName}")
	List<String> selectFields(@Param("tableName") String tableName);
	
	/**
	 *  查询表字段的statement
	 */
	static final String SELECTFIELDS_STATEMENT = InternalMapper.class.getCanonicalName() + ".selectFields";
}
