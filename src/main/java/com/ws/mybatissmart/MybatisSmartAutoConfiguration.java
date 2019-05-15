package com.ws.mybatissmart;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConditionalOnClass(value = { SqlSessionFactory.class })
@Configuration
@EnableConfigurationProperties(value = { MybatisSmartProperties.class })
public class MybatisSmartAutoConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(MybatisSmartAutoConfiguration.class);
	@Autowired
	private SqlSessionFactory sessionFactory;
	@Autowired
	private MybatisSmartProperties mybatisSmartProperties;
	@PostConstruct
	public void init() throws IOException {
		org.apache.ibatis.session.Configuration configuration = sessionFactory.getConfiguration();
		configuration.addMapper(SelfMapper.class);
		MybatisSmartContext.initConf(sessionFactory, mybatisSmartProperties);
		MybatisSmartContext.scanDataModel();
	}

}
