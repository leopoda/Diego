package cn.td.geotags.job;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.dao.JdbcJobExecutionDao;
import org.springframework.batch.core.repository.dao.JdbcJobInstanceDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.item.database.support.DefaultDataFieldMaxValueIncrementerFactory;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.td.geotags.task.CellAroundTasklet;
import cn.td.geotags.task.GatherPointTasklet;
import cn.td.geotags.task.PoisAroundTasklet;
import cn.td.geotags.task.TownshipTasklet;

@Configuration
@EnableBatchProcessing
public class JobConfig extends DefaultBatchConfigurer {
	@Autowired
	private Environment env;
	
	private final static String databaseType = "MYSQL";
	private final static String tablePrefix = "BATCH_";
	
	@Bean
	public CellAroundTasklet cellAroundTasklet() {
		return new CellAroundTasklet();
	}
	
	@Bean
	public TownshipTasklet townshipTasklet() {
		return new TownshipTasklet();
	}
	
	@Bean
	public PoisAroundTasklet poisAroundTasklet() {
		return new PoisAroundTasklet();
	}

	@Bean
	public GatherPointTasklet gatherPointTasklet() {
		return new GatherPointTasklet();
	}
	
	@Bean
	public JobManager jobManager() {
		return new JobManager();
	}
	
	@Bean
	public JobDao jobRepo() {
		return new JobDao();
	}
	
	@Bean
	public JobInstanceDao jobInstanceDao (JdbcTemplate jdbcTemplate, DataSource dataSource) {
		DefaultDataFieldMaxValueIncrementerFactory incrementerFactory = incrementerFactory(dataSource);
		JdbcJobInstanceDao dao = new JdbcJobInstanceDao();
		dao.setJdbcTemplate(jdbcTemplate);
		dao.setJobIncrementer(incrementerFactory.getIncrementer(databaseType, tablePrefix + "JOB_SEQ"));
		dao.setTablePrefix(tablePrefix);
		return dao;
	}

	@Bean
	public DefaultDataFieldMaxValueIncrementerFactory incrementerFactory(DataSource dataSource) {
		DefaultDataFieldMaxValueIncrementerFactory incrementerFactory = new DefaultDataFieldMaxValueIncrementerFactory(dataSource);
		return incrementerFactory;
	}
	
	@Bean
	public JobExecutionDao jobExecutionDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
		DefaultDataFieldMaxValueIncrementerFactory incrementerFactory = incrementerFactory(dataSource);
		JdbcJobExecutionDao dao = new JdbcJobExecutionDao();
		dao.setJdbcTemplate(jdbcTemplate);
		dao.setJobExecutionIncrementer(incrementerFactory.getIncrementer(databaseType, tablePrefix + "JOB_EXECUTION_SEQ"));
		dao.setTablePrefix(tablePrefix);
		return dao;
	}

	@Override
	public JobLauncher getJobLauncher() {
		SimpleJobLauncher simpleJobLauncher = new SimpleJobLauncher();
		simpleJobLauncher.setJobRepository(getJobRepository());
		simpleJobLauncher.setTaskExecutor(getAsyncTaskExecutor());
		return simpleJobLauncher;
	}

	@Bean
	public TaskExecutor getAsyncTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Integer.parseInt(env.getProperty("executor.core.pool.size")));
        executor.setMaxPoolSize(Integer.parseInt(env.getProperty("executor.max.pool.size")));
        executor.setQueueCapacity(Integer.parseInt(env.getProperty("executor.queue.capacity")));
        executor.setKeepAliveSeconds(Integer.parseInt(env.getProperty("executor.keep.alive.seconds")));
        executor.initialize();
        return executor;
	}
}
