/*package com.ws.mybatissmart;

import java.util.Properties;
import java.util.concurrent.Executor;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

@Intercepts({
		@Signature(type= Executor.class,method="select", args = {  })
})
public class SmartSelectPlugin implements Interceptor{

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		return null;
	}

	@Override
	public Object plugin(Object target) {
		return null;
	}

	@Override
	public void setProperties(Properties properties) {
	}

}
*/