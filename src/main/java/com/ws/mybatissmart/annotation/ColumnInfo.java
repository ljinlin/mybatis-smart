package com.ws.mybatissmart.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ws.commons.constant.ObjTypeEnum;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ColumnInfo {

	String value() default "";

	/**
	 * 
	 * 值是 "",null 时不insert
	 */
	ObjTypeEnum[] noInsertVal() default { ObjTypeEnum.BLANK, ObjTypeEnum.NULL };

	/**
	 * 
	 * 值是 "",null 时不update
	 */
	ObjTypeEnum[] noUpdateVal() default { ObjTypeEnum.BLANK, ObjTypeEnum.NULL };

	/**
	 * 时间字段格式化，如果是日期类型，需要格式化，则配置此字段
	 */
	String dateFormart() default "";

}
