package com.ws.mybatissmart;

public class BaseMapperSqlProvider {

	public String selectList(Object obj, FilterSqlBuild filterSqlBuild) {
		ClassMapperInfo cmi = MybatisXContext.getClassMapperInfo(obj.getClass());
		String sql = cmi.getSelectListSql(obj, filterSqlBuild);
		return sql;
	}

	public String selectById(Object idV, Class<?> cl) {
		ClassMapperInfo cmi = MybatisXContext.getClassMapperInfo(cl);
		String sql = cmi.getSelectByIdSql(idV);
		return sql;
	}

	public String insert(Object obj) {
		ClassMapperInfo cmi = MybatisXContext.getClassMapperInfo(obj.getClass());
		String sql = null;
		try {
			sql = cmi.getInsertSql(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(sql);
		return sql;
	}

	public String updateById(Object obj) {
		ClassMapperInfo cmi = MybatisXContext.getClassMapperInfo(obj.getClass());
		String sql = null;
		try {
			sql = cmi.getUpdateByIdSql(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sql;
	}

	public String deleteById(Object obj) {
		ClassMapperInfo cmi = MybatisXContext.getClassMapperInfo(obj.getClass());
		String sql = cmi.getDeleteByIdSql(obj);
		return sql;
	}

}
