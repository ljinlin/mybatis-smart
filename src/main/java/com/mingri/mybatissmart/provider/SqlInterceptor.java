package com.mingri.mybatissmart.provider;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mingri.langhuan.cabinet.tool.ClassTool;
import com.mingri.mybatissmart.mapper.SmartMapper;

/**
 * 自定义 MyBatis 拦截器
 * MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler
 */
@Intercepts({ 
	@Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
})
public class SqlInterceptor implements Interceptor {

	private static final Logger logger = LoggerFactory.getLogger(SqlInterceptor.class);

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

	/**
	 * intercept 方法用来对拦截的sql进行具体的操作
	 * 
	 * @param invocation
	 * @return
	 * @throws Throwable
	 */
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		

		Object[] args = invocation.getArgs();
		MappedStatement ms = (MappedStatement) args[0];

		String id = ms.getId();
		logger.info("执行intercept方法：{}", id);
		Class<?> model = getIfSmartMapperSubOfModel(id);
		if (model == null) {
			return invocation.proceed();
		}
		try {
			MapperSqlProvider.MODEL.set(model);
			return invocation.proceed();
		} finally {
			MapperSqlProvider.MODEL.remove();
		}
	}

	@Override
	public Object plugin(Object target) {
		logger.info("plugin方法：{}", target);

		if (target instanceof Executor) {
			return Plugin.wrap(target, this);
		}
		return target;

	}

	@Override
	public void setProperties(Properties properties) {
		logger.info("properties方法：{}", properties.toString());
	}

}