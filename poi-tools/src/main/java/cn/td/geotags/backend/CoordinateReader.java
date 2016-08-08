package cn.td.geotags.backend;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import cn.td.geotags.domain.Coordinate;

public class CoordinateReader extends FlatFileItemReader<Coordinate> {
	public CoordinateReader() {
		this.setResource(getResource());
		this.setLineMapper(lineMapper());
	}
	
	private Resource getResource() {
		return new ClassPathResource("pa_list_home_loc@20160726.dat");
	}
	
	private LineMapper<Coordinate> lineMapper() {
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter("	");
		lineTokenizer.setNames(new String[]{"lng","lat"});
		lineTokenizer.setIncludedFields(new int[]{0,1});
		
		BeanWrapperFieldSetMapper<Coordinate> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(Coordinate.class);
		
		DefaultLineMapper<Coordinate> lineMapper = new DefaultLineMapper<>();
		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);
		return lineMapper;
	}
}
