package com.pingan.tags.config;

import org.springframework.context.annotation.Configuration;

import java.io.UnsupportedEncodingException;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;

import com.pingan.tags.dao.AmapRepository;
import com.pingan.tags.dao.Repository;
import com.pingan.tags.service.CoordService;
import com.pingan.tags.service.CoordServiceImpl;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.CacheManager;

import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@EnableCaching
@ComponentScan(basePackages = "com.pingan.tags")
@PropertySource({"classpath:app.properties", "classpath:poi.properties"})
public class RootConfig {
	@Autowired
	Environment env;
	
	@Profile("dev")
	@Bean(destroyMethod="shutdown")
	public DataSource embeddedDataSource() {
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).addScript("classpath:schema.sql").build();
	}
	
	@Bean
	@Profile("prod")
	public BasicDataSource dataSource() {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(env.getProperty("jdbc.driver"));
		ds.setUrl(env.getProperty("jdbc.url"));
		ds.setUsername(env.getProperty("jdbc.username"));
		ds.setPassword(env.getProperty("jdbc.password"));
		ds.setInitialSize(5);
		ds.setMaxTotal(10);
		return ds;
	}
	
	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
	
	@Bean
	public Repository repository() {
		return new AmapRepository();
	}
	
	@Bean
	public PlatformTransactionManager transactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
	
	@Bean
	public CacheManager cacheManager(RedisTemplate<String, String> redisTemplate) {
		return new RedisCacheManager(redisTemplate);
	}

	@Bean
	public JedisConnectionFactory redisConnectionFactory() {
		JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
		jedisConnectionFactory.setHostName(env.getProperty("redis.hostname"));
		jedisConnectionFactory.setPort(Integer.parseInt(env.getProperty("redis.port")));
		jedisConnectionFactory.afterPropertiesSet();

		return jedisConnectionFactory;
	}
	
	@Bean
	public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisCF) {
		RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisCF);
		redisTemplate.afterPropertiesSet();

		return redisTemplate;
	}
	
	@Bean
	public MapConfig mapConfig() {
		MapConfig conf = new MapConfig();
		String baseURL = env.getProperty("amap.api.base.url");
		
		conf.setToken(env.getProperty("amap.api.token"));
		conf.setConvertURL(baseURL + env.getProperty("amap.api.convert.path"));
		conf.setRegeoURL(baseURL + env.getProperty("amap.api.regeo.path"));
		conf.setAroundURL(baseURL + env.getProperty("amap.api.around.path"));
		return conf;
	}
	
	@Bean
	public PoiConfig poiConfig() {
		PoiConfig poiConfig = new PoiConfig();
		try {
			poiConfig.setPoiTypeInfo(new String(env.getProperty("poi.type.info").getBytes("ISO-8859-1"), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		
		poiConfig.setPoiAroundRadius(Integer.parseInt(env.getProperty("poi.around.radius")));
		poiConfig.setPoiAroundPageSize(Integer.parseInt(env.getProperty("poi.around.page.size")));
		
		return poiConfig;
	}
	
	@Bean
	public CoordService coordService() {
		return new CoordServiceImpl();
	}
}
