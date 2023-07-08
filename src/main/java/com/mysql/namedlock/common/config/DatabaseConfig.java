package com.mysql.namedlock.common.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@Lazy
@EnableTransactionManagement
@MapperScan(basePackages = "com.mysql.namedlock", sqlSessionFactoryRef = "sqlSessionFactory")
@Slf4j
public class DatabaseConfig {

  @Bean(destroyMethod = "close")
  @ConfigurationProperties("spring.datasource.primary")
  public DataSource dataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean(name = "transactionManager")
  public DataSourceTransactionManager transactionManager() {
    DataSourceTransactionManager dataTransactionManager = new DataSourceTransactionManager();
    dataTransactionManager.setDataSource(dataSource());
    return dataTransactionManager;
  }

  @Bean(name = "sqlSessionFactory")
  public SqlSessionFactoryBean sqlSessionFactory(ApplicationContext applicationContext) throws Exception {
    SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
    sessionFactory.setDataSource(dataSource());
    sessionFactory.setConfigLocation(new ClassPathResource("/mybatis-config.xml"));
    sessionFactory.setTypeAliasesPackage("com.mysql.namedlock");
    sessionFactory.setMapperLocations(applicationContext.getResources("classpath:**/mapper/*Mapper.xml"));
    return sessionFactory;
  }

  @Bean(name = "sqlSessionTemplate")
  public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
    return new SqlSessionTemplate(sqlSessionFactory);
  }
}
