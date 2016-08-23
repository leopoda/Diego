package cn.td.geotags.job;

import org.springframework.batch.core.repository.dao.AbstractJdbcBatchMetadataDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.util.Assert;

@Component
public class JobDelete {
	private static final String DEFAULT_TABLE_PREFIX = AbstractJdbcBatchMetadataDao.DEFAULT_TABLE_PREFIX;
	private String tablePrefix = DEFAULT_TABLE_PREFIX;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private static final String  SQL_DELETE_BATCH_STEP_EXECUTION_CONTEXT = "DELETE FROM %PREFIX%STEP_EXECUTION_CONTEXT WHERE STEP_EXECUTION_ID IN (SELECT STEP_EXECUTION_ID FROM %PREFIX%STEP_EXECUTION WHERE JOB_EXECUTION_ID = ?);";
	private static final String  SQL_DELETE_BATCH_STEP_EXECUTION = "DELETE FROM %PREFIX%STEP_EXECUTION WHERE JOB_EXECUTION_ID = ?;";
	private static final String  SQL_DELETE_BATCH_JOB_EXECUTION_CONTEXT = "DELETE FROM %PREFIX%JOB_EXECUTION_CONTEXT WHERE JOB_EXECUTION_ID = ?;";
	private static final String  SQL_DELETE_BATCH_JOB_EXECUTION_PARAMS = "DELETE FROM %PREFIX%JOB_EXECUTION_PARAMS WHERE JOB_EXECUTION_ID = ?;";
	private static final String  SQL_DELETE_BATCH_JOB_EXECUTION = "DELETE FROM %PREFIX%JOB_EXECUTION WHERE JOB_EXECUTION_ID = ?;";
	private static final String  SQL_DELETE_BATCH_JOB_INSTANCE = "DELETE FROM %PREFIX%JOB_INSTANCE WHERE JOB_INSTANCE_ID NOT IN (SELECT JOB_INSTANCE_ID FROM %PREFIX%JOB_EXECUTION);";
	
	protected String getQuery(String base) {
		return StringUtils.replace(base, "%PREFIX%", tablePrefix);
	}

	public void setTablePrefix(String tablePrefix) {
		this.tablePrefix = tablePrefix;
	}

	public void  afterPropertiesSet() {
		Assert.notNull(jdbcTemplate);
	}
	
	public boolean delete(long jobId) {
		int totalCount = 0;
		
		int rowCount = jdbcTemplate.update(getQuery(SQL_DELETE_BATCH_STEP_EXECUTION_CONTEXT), jobId);
		totalCount += rowCount;

		rowCount = jdbcTemplate.update(getQuery(SQL_DELETE_BATCH_STEP_EXECUTION), jobId);
		totalCount += rowCount;
		
		rowCount = jdbcTemplate.update(getQuery(SQL_DELETE_BATCH_JOB_EXECUTION_CONTEXT), jobId);
		totalCount += rowCount;
		
		rowCount = jdbcTemplate.update(getQuery(SQL_DELETE_BATCH_JOB_EXECUTION_PARAMS), jobId);
		totalCount += rowCount;
		
		rowCount = jdbcTemplate.update(getQuery(SQL_DELETE_BATCH_JOB_EXECUTION), jobId);
		totalCount += rowCount;
		
		rowCount = jdbcTemplate.update(getQuery(SQL_DELETE_BATCH_JOB_INSTANCE));
		totalCount += rowCount;
		
		return totalCount > 0;
	}
}
