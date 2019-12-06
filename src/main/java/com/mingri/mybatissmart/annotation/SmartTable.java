package com.mingri.mybatissmart.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

import com.mingri.mybatissmart.barracks.IdtacticsEnum;

/**
 * 数据库表信息
 * @author ljl
 *
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SmartTable {

	/**
	 *  表名
	 */
	String value() default "";
	
	/**
	 *  表名
	 */
	@AliasFor("value")
	String name() default "";

	/**
	 * 
	 * 表别名
	 */
	String alias() default "";
	
	/**
	 * id生成策略
	 * @return id生成策略枚举
	 */
	IdtacticsEnum idtactics() default IdtacticsEnum.DFT;

	/**
	 * id列对应的字段名称
	 * @return id字段
	 */
	String idFieldName() default "id";
}
