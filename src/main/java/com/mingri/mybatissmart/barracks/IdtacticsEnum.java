package com.mingri.mybatissmart.barracks;

/**
 * id生成策略
 * 
 */
public enum IdtacticsEnum {
	
	
	/**
	 * 数据库自增
	 */
	SQL_INCR,
	
	
	/**
	 * mybatis-smart 默认生成
	 */
	DFT,
	
	
	/**
	 * 自定义
	 */
	DEFINED
}