package com.ws.mybatissmart;

import javax.annotation.PostConstruct;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = { MybatisXProperties.class })
public class MybatisXAutoConfiguration {

	public static final String dataMapName = "dataMap";

	@Autowired
	private SqlSessionFactory sessionFactory;

	@PostConstruct
	public void init() {
		org.apache.ibatis.session.Configuration configuration = sessionFactory.getConfiguration();
		configuration.addMapper(SmartMapper.class);
		MybatisXContext.initConf(sessionFactory);
	}
}
