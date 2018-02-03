package com.demo.http.api.gateway.spring.config;

import static org.springframework.boot.jdbc.DataSourceBuilder.create;

import javax.sql.DataSource;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mchange.v2.c3p0.ComboPooledDataSource;
/**
* spring配置类for db datasource
* 
* @author wang guobo 王国波
*/
@Configuration
public class DataSourceConfig {

    @Bean(name="dataSource")
    @Qualifier(value="dataSource")
    @ConfigurationProperties(prefix="datasource.mysql")
    public DataSource dataSource(){
        return create().type(ComboPooledDataSource.class).build();//创建数据源
    }
    /**
     *返回sqlSessionFactory
     */
    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(){
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource());
        return sqlSessionFactory;
    }
}