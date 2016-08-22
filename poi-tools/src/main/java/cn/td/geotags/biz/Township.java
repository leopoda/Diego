package cn.td.geotags.biz;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import cn.td.geotags.config.RootConfig;
import cn.td.geotags.domain.CoordAddress;
import cn.td.geotags.service.CoordService;
import cn.td.geotags.util.CoordinateParser;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Township {
	@Autowired
	CoordService coordService;
	
	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.out.println("Error, at least two input parameters!");
			return;
		}
//		String filePath = "D:/datahub/pa_list_loc@20160715.txt";
//		String prefix = "d:/datahub/output/coord-geo-%s.txt";
		
		String inputFile = args[0];
		String prefix = args[1];
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String outputFile = String.format(prefix, sdf.format(System.currentTimeMillis()));
		
		try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(RootConfig.class)) {
			Township coordStreet = ctx.getBean(Township.class);
			coordStreet.calc(inputFile, outputFile);
		}
	}
	
	public void calc(String inputFilePath, String outputFile) {
		try (Stream<String> lines = Files.lines(Paths.get(inputFilePath))) {
			PrintStream strm = new PrintStream(outputFile);
			lines.map(CoordinateParser::parse)
//				 .limit(10)
				 .parallel()
				 .filter(c -> c.isValid() == true)
				 .map(c -> coordService.getCoordAddress(c))
				 .map(CoordAddress::asFlatText)
//				 .forEach(System.out::println);
				 .forEach(s -> strm.println(s));
			
			strm.close();
		} catch (IOException e) {
			log.error("", e);
		}
	}
}