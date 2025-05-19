package com.core.aiagent.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * 多数据源整合
 */
@Configuration
public class MultiDataSourceConfig {

  /**
   *  1. 把 spring.datasource.* 先绑定到 DataSourceProperties
   */
  @Bean
  @Primary
  @ConfigurationProperties("spring.datasource")
  public DataSourceProperties mysqlProperties() {
    return new DataSourceProperties();
  }

  /**
   * 2. 用它来构建数据源，保证 jdbcUrl 被正确设置
   */
  @Bean
  @Primary
  public DataSource mysqlDataSource(@Qualifier("mysqlProperties") DataSourceProperties mysqlProperties) {
    return mysqlProperties().initializeDataSourceBuilder().build();
  }

  /**
   * 3. PostgreSQL 同理
   */
  @Bean
  @ConfigurationProperties("spring.pg.datasource")
  public DataSourceProperties pgProperties() {
    return new DataSourceProperties();
  }

  @Bean
  public DataSource pgDataSource(@Qualifier("pgProperties") DataSourceProperties pgProperties) {
    return pgProperties
            .initializeDataSourceBuilder()
            .build();
  }

  /**
   *  4. 专用 PG JdbcTemplate
   */
  @Bean
  public JdbcTemplate pgJdbcTemplate(@Qualifier("pgDataSource") DataSource ds) {
    return new JdbcTemplate(ds);
  }
}
