package cn.td.geotags.config;

import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

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
import cn.td.geotags.util.Contants;

@Configuration
@EnableCaching
@ComponentScan(basePackages = "cn.td.geotags")
@PropertySource({"classpath:app.properties", "classpath:poi.properties"})
@Import(JobConfig.class)
public class RootConfig {
	@Autowired
	private Environment env;
	
	@Bean
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

	@Bean
	public CacheManager cacheManager(RedisTemplate<String, String> redisTemplate) {
		RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);

		// 对缓存非常大的 region, 设置失效时间
		long expireInSeconds = Long.parseLong(env.getProperty("redis.default.expire"));
		Map<String, Long> expireConfig = new HashMap<>();
		expireConfig.put(Contants.CACHE_REGION_REGEO, expireInSeconds);
		expireConfig.put(Contants.CACHE_REGION_POI_AROUND, expireInSeconds);
		cacheManager.setExpires(expireConfig);
		return cacheManager;
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
		
		conf.setReadTimeout(Integer.parseInt(env.getProperty("read.timeout.interval")));
		conf.setConnectTimeout(Integer.parseInt(env.getProperty("connect.timeout.interval")));
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
