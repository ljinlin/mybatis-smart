package com.mingri.mybatissmart.ex;

import java.sql.Statement;

import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;

import com.mingri.mybatissmart.barracks.Constant;
import com.mingri.mybatissmart.barracks.IdtacticsEnum;
import com.mingri.mybatissmart.barracks.Tool;
import com.mingri.mybatissmart.config.MybatisSmartContext;
import com.mingri.mybatissmart.dbo.SmartTableInfo;

public class SmartKeyGenerator extends Jdbc3KeyGenerator {

	public static final SmartKeyGenerator INSTANCE = new SmartKeyGenerator();

	@Override
	public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {

		Class<?> clazz = Tool.getClassByParam(parameter);
		if (clazz == null) {
			return;
		}
		SmartTableInfo smtb = MybatisSmartContext.getSmartTableInfo(clazz);
		if (smtb == null) {
			return;
		}
		if (smtb.getSmartTable().idtactics() != IdtacticsEnum.SQL_INCR) {
			return;
		}
		if (parameter instanceof ParamMap) {
			parameter = ((ParamMap<?>) parameter).get(Constant.PARAM_KEY);
		}
		smtb.modifyKeyProperties(ms);
		Jdbc3KeyGenerator.INSTANCE.processAfter(executor, ms, stmt, parameter);
	}

}
