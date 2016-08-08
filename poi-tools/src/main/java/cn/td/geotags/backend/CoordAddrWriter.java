package cn.td.geotags.backend;

import java.io.File;

import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.core.io.FileSystemResource;

import cn.td.geotags.domain.CoordAddress;

public class CoordAddrWriter extends FlatFileItemWriter<CoordAddress> {
	public CoordAddrWriter() {
		CoordAddrExtractor fieldExtractor = new CoordAddrExtractor();
		DelimitedLineAggregator<CoordAddress> la = new DelimitedLineAggregator<>();
		la.setDelimiter("\t");
		la.setFieldExtractor(fieldExtractor);
		
		this.setLineAggregator(la);
		this.setResource(new FileSystemResource(new File("target/output_data.txt")));
	}
	
	public class CoordAddrExtractor implements FieldExtractor<CoordAddress> {
		@Override
		public Object[] extract(CoordAddress item) {
			return item.asFlatText().split("\t");
		}
	}
}
