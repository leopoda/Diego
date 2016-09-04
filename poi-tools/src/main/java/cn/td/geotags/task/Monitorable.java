package cn.td.geotags.task;

import java.io.File;
import java.util.Date;

import com.talkingdata.monitor.client.Client;

public interface Monitorable {
	default void monitorFileSize(long jobId, long taskId, String stageName, String filePath) {
		long fileSize = new File(filePath).length();
		String key = String.format("%s_%s_%s_size", jobId, taskId, stageName);
		Client.count(key, fileSize);
	}
	
	default void monitorProcessTimeAt(long jobId, long taskId, String stageName, Date date) {
		String key = String.format("%s_%s_%s_at", jobId, taskId, stageName);
		Client.count(key, date.toInstant().getEpochSecond());
	}
}
