package cn.td.geotags.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import cn.td.geotags.util.Contants;

@Component
public class TaskConfig {
	@Autowired
	private Environment env;
	
	public String getOutputFilePath(long jobId, String outputType) {
		return env.getProperty("file.out.dir") + "/" + outputType + "-" + jobId + Contants.FILE_EXT_CSV;
	}
}
