package com.pingan.tags.config;

import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;

import com.pingan.tags.db.Repository;
import com.pingan.tags.db.jdbc.JdbcRepository;

@Configuration
@ComponentScan(basePackages = "com.pingan.tags")
public class PinganConfig {
	
	@Profile("dev")
	@Bean(destroyMethod="shutdown")
	public DataSource embeddedDataSource() {
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).addScript("classpath:schema.sql").build();
	}
	
	@Bean
	@Profile("prod")
	public BasicDataSource dataSource() {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
		ds.setUrl("jdbc:mysql://127.0.0.1:3306/segment?useUnicode=true&characterEncoding=UTF-8&useSSL=false&rewriteBatchedStatements=true&serverTimezone=Asia/Shanghai");
		ds.setUsername("root");
		ds.setPassword("");
		ds.setInitialSize(5);
		ds.setMaxTotal(10);
		return ds;
	}
	
	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
	
	@Bean
	public Repository repository(JdbcTemplate jdbcTemplate) {
		return new JdbcRepository(jdbcTemplate);
	}
	
	@Bean
	public PlatformTransactionManager transactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
}
