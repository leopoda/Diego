package cn.td.geotags.job;

import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.beans.factory.annotation.Autowired;

public class JobRepo {
	@Autowired
	private JobInstanceDao jobInstanceDao;
	
	@Autowired
	private JobExecutionDao jobExecutionDao;
	
	@Autowired
	private StepExecutionDao stepExecutionDao;
	
	@Autowired
	private ExecutionContextDao executionContextDao;
}
