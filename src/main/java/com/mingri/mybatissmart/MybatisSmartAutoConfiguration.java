package com.mingri.mybatissmart;

import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.mingri.mybatissmart.mapper.InternalMapper;

//@ConditionalOnClass(value = { SqlSessionFactory.class })
@Configuration
@EnableConfigurationProperties(value = { MybatisSmartProperties.class})
@ConditionalOnClass({ SqlSessionFactory.class, SqlSessionFactoryBean.class,MybatisAutoConfiguration.class })
@ConditionalOnBean(DataSource.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class MybatisSmartAutoConfiguration {
	@Autowired
	private SqlSessionFactory sessionFactory;
	@Autowired
	private MybatisSmartProperties mybatisSmartProperties;
	@PostConstruct
	public void init() throws ClassNotFoundException, SQLException {
		org.apache.ibatis.session.Configuration configuration = sessionFactory.getConfiguration();
		configuration.addMapper(InternalMapper.class);
		MybatisSmartContext.initConf(sessionFactory, mybatisSmartProperties);
		MybatisSmartContext.scanTableModel();
	}

}
