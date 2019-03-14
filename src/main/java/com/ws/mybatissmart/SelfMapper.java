package com.ws.mybatissmart;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface SelfMapper {
	@Select("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME=#{tableName}")
	List<String> selectFields(@Param("tableName") String tableName);

	static final String SELECTFIELDS_STATEMENT = SelfMapper.class.getCanonicalName() + ".selectFields";
}
