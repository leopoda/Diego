package cn.td.geotags.job;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;

public class ResourceManager {

	@Autowired
	private StringRedisTemplate redisTemplate;

	private volatile long jobId;
	private final static String JOB_ID_KEY = "jobId";
	
	public ResourceManager() {
		jobId = 0L;
	}
	
	public String getJobIdKey() {
		return JOB_ID_KEY;
	}
	
//	@Cacheable(value="jobIdCache", key="#p0", unless="1=1") // do not put return value into cache
	private synchronized long getJobId(String jobIdKey) {
		redisTemplate.opsForValue().get(JOB_ID_KEY);
		return jobId;
	}
	
	public synchronized long getJobId() {
		return this.getJobId(JOB_ID_KEY);
	}

	@CachePut(value="jobIdCache", key="#root.target.getJobIdKey()")
	public synchronized long incrJobId() {
		long currentJobId = this.getJobId();
		currentJobId = currentJobId + 1;
		
		System.out.println("cache put xxx:" + currentJobId);
		
		return currentJobId;
	}
}
