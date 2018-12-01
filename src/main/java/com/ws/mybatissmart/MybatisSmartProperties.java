package com.ws.mybatissmart;

import javax.annotation.PostConstruct;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix=MybatisSmartProperties.MYBATISX_PREFIX)
public class MybatisSmartProperties{
	  public static final String MYBATISX_PREFIX = "mybatis-smart";
//	select * from information_schema.COLUMNS where TABLE_SCHEMA = (select database()) and TABLE_NAME=#{tableName}
	
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	public static SqlSessionFactory staticSqlSessionFactory;
   public static MybatisSmartContext mybatisXContext;	
	@PostConstruct
	private void init() {
		MybatisSmartProperties.mybatisXContext=new MybatisSmartContext();
//		AutoConfiguredMapperScannerRegistrar
		MybatisSmartProperties.staticSqlSessionFactory=sqlSessionFactory;
	}
	
	
	public static String selectColumns(String tableName){
		SqlSession session= MybatisSmartProperties.staticSqlSessionFactory.openSession();
		System.out.println(session);
		return "";
	}
}