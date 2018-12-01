package com.ws.mybatissmart.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TableInfo {

	/**
	 * 表名
	 */
	String value() default "";

	/**
	 * 
	 * 别名
	 */
	String alias() default "";
	/**
	 * id生成策略
	 * @return
	 */
	IdtacticsEnum idtactics() default IdtacticsEnum.DFT;

	/**
	 * id列对应的字段名称
	 * @return
	 */
	String idFieldName() default "id";
	/**
	 * id生成策略
	 * 
	 * @author ljl·尘无尘
	 * @date Oct 29, 2018
	 */
	enum IdtacticsEnum {
		/**
		 * 数据库自增
		 */
		SQL_INCR,
		/**
		 * 插件默认生成
		 */
		DFT,
		/**
		 * 自定义
		 */
		DEFINED
	}
}
