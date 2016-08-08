package cn.td.geotags.backend;

import java.util.Random;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import cn.td.geotags.config.RootConfig;
import cn.td.geotags.domain.CoordAddress;
import cn.td.geotags.domain.Coordinate;
import cn.td.geotags.service.CoordService;

@Configuration
@Import({InfraConfig.class, RootConfig.class})
public class CoordAddrJobConfig {

	@Autowired
	private JobBuilderFactory jobBuilders;
	
	@Autowired
	private StepBuilderFactory stepBuilders;
	
	@Autowired
	private DataSource dataSource; // just for show...
	
	@Autowired
	CoordService coordService;
	
	@Bean
	public Job coord2Addr(){
		return jobBuilders.get("coord2Addr" + rand())
				.start(step())
				.build();
	}
	
	@Bean
	public Step step(){
		return stepBuilders.get("step")
				.<Coordinate,CoordAddress>chunk(10000)
				.reader(reader())
				.processor(processor())
				.writer(writer())
				.build();
	}

	private ItemWriter<CoordAddress> writer() {
		return new CoordAddrWriter();
	}

	private ItemProcessor<Coordinate, CoordAddress> processor() {
		return new CoordinateProcessor(coordService);
	}

	private ItemReader<Coordinate> reader() {
		return new CoordinateReader();
	}
	
	private int rand() {
		return new Random().nextInt(100);
	}
}
