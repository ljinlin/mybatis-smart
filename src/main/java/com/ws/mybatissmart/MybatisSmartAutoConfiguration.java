package com.ws.mybatissmart;

import javax.annotation.PostConstruct;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConditionalOnClass(value = { SqlSessionFactory.class })
@Configuration
@EnableConfigurationProperties(value = { MybatisSmartProperties.class })
public class MybatisSmartAutoConfiguration {

	public static final String dataMapName = "dataMap";

	@Autowired
	private SqlSessionFactory sessionFactory;

	@PostConstruct
	public void init() {
		org.apache.ibatis.session.Configuration configuration = sessionFactory.getConfiguration();
		configuration.addMapper(SmartMapper.class);
		MybatisSmartContext.initConf(sessionFactory);
	}
}
