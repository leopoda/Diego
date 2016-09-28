package cn.td.geotags.config;

import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.CacheManager;

import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import cn.td.geotags.dao.AmapRepository;
import cn.td.geotags.dao.MapRepository;
import cn.td.geotags.job.JobConfig;
import cn.td.geotags.service.CoordService;
import cn.td.geotags.service.CoordServiceImpl;

@Configuration
@EnableCaching
@ComponentScan(basePackages = "cn.td.geotags")
@PropertySource({"classpath:app.properties", "classpath:poi.properties"})
@Import(JobConfig.class)
public class RootConfig {
	@Autowired
	private Environment env;
	
	/*
	@Profile("dev")
	@Bean(destroyMethod="shutdown")
	public DataSource embeddedDataSource() {
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).addScript("classpath:schema.sql").build();
	}
*/
	
	@Bean
//	@Profile("prod")
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
	public MapRepository repository() {
		return new AmapRepository();
	}
	
	/*
	@Bean
	public PlatformTransactionManager transactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
	*/
	
	@Bean
	public CacheManager cacheManager(RedisTemplate<String, String> redisTemplate) {
		return new RedisCacheManager(redisTemplate);
	}

	@Bean
	public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisCF) {
		RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisCF);
		redisTemplate.afterPropertiesSet();
		
		return redisTemplate;
	}

	@Bean
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory cf) {
		return new StringRedisTemplate(cf);
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
	public MapConfig mapConfig() {
		MapConfig conf = new MapConfig();
		String baseURL = env.getProperty("amap.api.base.url");
		
		conf.setToken(env.getProperty("amap.api.token"));
		conf.setConvertURL(baseURL + env.getProperty("amap.api.convert.path"));
		conf.setRegeoURL(baseURL + env.getProperty("amap.api.regeo.path"));
		conf.setAroundURL(baseURL + env.getProperty("amap.api.around.path"));
		conf.setDistrictURL(baseURL + env.getProperty("amap.api.district.path"));
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
