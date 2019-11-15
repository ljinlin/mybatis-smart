package com.mingri.mybatissmart.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.mingri.langhuan.cabinet.constant.ObjTypeEnum;

/**
 * 数据库列信息
 * 
 * @author ljl
 *
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ColumnInfo {

	/**
	 * 列名<br>
	 * 默认是"",则将字段名称转驼峰匹配：userName 配 user_name 或者 username
	 * 
	 * @return 字段名称
	 */
	String value() default "";

	/**
	 * 值是 "",null 时不insert
	 * 
	 * @return 要新增的值的类型
	 */
	ObjTypeEnum[] insertValType() default { ObjTypeEnum.OBJ };

	/**
	 * 值是 "",null 时不update
	 * 
	 * @return 要更新的值的类型
	 */
	ObjTypeEnum[] updateValType() default { ObjTypeEnum.OBJ };

	/**
	 * 时间字段格式化，如果是日期类型，需要格式化，则配置此字段
	 * 
	 * @return 日期格式化字符串
	 */
	String dateFormart() default "";

	/**
	 * 是否插入
	 * 
	 * @return 布尔值
	 */
	boolean isInsert() default true;

	/**
	 * 是否更新
	 * 
	 * @return 布尔值
	 */
	boolean isUpdate() default true;
}
