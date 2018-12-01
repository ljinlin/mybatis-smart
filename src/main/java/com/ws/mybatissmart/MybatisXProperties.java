package com.ws.mybatissmart;

import javax.annotation.PostConstruct;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="mybatisx")
public class MybatisXProperties{
	  public static final String MYBATISX_PREFIX = "mybatisx";
//	select * from information_schema.COLUMNS where TABLE_SCHEMA = (select database()) and TABLE_NAME=#{tableName}
	
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	public static SqlSessionFactory staticSqlSessionFactory;
   public static MybatisXContext mybatisXContext;	
	@PostConstruct
	private void init() {
		MybatisXProperties.mybatisXContext=new MybatisXContext();
//		AutoConfiguredMapperScannerRegistrar
		MybatisXProperties.staticSqlSessionFactory=sqlSessionFactory;
	}
	
	
	public static String selectColumns(String tableName){
		SqlSession session= MybatisXProperties.staticSqlSessionFactory.openSession();
		System.out.println(session);
		return "";
	}
}