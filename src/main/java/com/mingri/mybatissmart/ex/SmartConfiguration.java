package com.mingri.mybatissmart.ex;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mingri.langhuan.cabinet.tool.ClassTool;
import com.mingri.mybatissmart.mapper.SmartMapper;

/**
 * 
 * @author ljl
 * @date 2020-12-16 Dscription: 扩展 mybatis 的 Configuration
 *
 */
public class SmartConfiguration extends Configuration {
	private static final Logger logger = LoggerFactory.getLogger(SmartConfiguration.class);
	public static final Map<String, Class<?>> MODEL_MAP = new ConcurrentHashMap<>();

	public static Class<?> getIfSmartMapperSubOfModel(String id) {

		return MODEL_MAP.computeIfAbsent(id, (k) -> {
			int idx = k.lastIndexOf(".");
			String className = k.substring(0, idx);
			try {
				Class<?> clazz = Class.forName(className);
				if (!SmartMapper.class.isAssignableFrom(clazz)) {
					return null;
				}
				List<Type> tps = ClassTool.getExtendGenericity(clazz, SmartMapper.class);
				if (tps == null || tps.isEmpty()) {
					logger.warn("继承SmartMapper接口必须设置泛型");
					return null;
				}
				return Class.forName(tps.get(0).getTypeName());
			} catch (Exception e) {
				if (!(e instanceof ClassNotFoundException)) {
					logger.warn("不支持SmartMapper的子接口的子接口调用SmartMapper的函数");
				}
				e.printStackTrace();
			}

			return null;
		});
	}
	
	 static final ThreadLocal<Class<?>> MODEL=new ThreadLocal<>();
	
	public MappedStatement getMappedStatement(String id, boolean validateIncompleteStatements) {
		removeModel();
		Class<?> model=getIfSmartMapperSubOfModel(id);
		setModel(model);
		return super.getMappedStatement(id, validateIncompleteStatements);
	}
	
	
	
	public static void setModel(Class<?> model) {
		MODEL.set(model);
	}
	
	
	private static void removeModel() {
		MODEL.remove();
	}
	
	
	public static Class<?> currentModel() {
		Class<?> clazz= MODEL.get();
		return clazz;
	}

}
