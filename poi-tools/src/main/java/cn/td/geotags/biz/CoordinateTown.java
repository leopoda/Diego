package cn.td.geotags.biz;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.td.geotags.domain.CoordAddress;
import cn.td.geotags.service.CoordService;
import cn.td.geotags.util.Contants;
import cn.td.geotags.util.ParserUtil;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CoordinateTown {
	@Autowired
	CoordService coordService;
	
	@Deprecated
	public void calc(String inputFilePath, String outputFile) {
		calc(Contants.PARAM_COORD_SYS_GPS, inputFilePath, outputFile);
	}
	
	public void calc(String coordsys, String inputFilePath, String outputFile) {
		try (Stream<String> lines = Files.lines(Paths.get(inputFilePath))) {
			PrintStream strm = new PrintStream(outputFile);
			lines.map(ParserUtil::parseAsCoordinate)
				 .parallel()
				 .filter(c -> c.isValid() == true)
				 .map(c -> coordService.getCoordAddress(c, coordsys))
				 .map(CoordAddress::asFlatText)
				 .forEach(s -> strm.println(s));
			
			strm.close();
		} catch (IOException e) {
			log.error("", e);
		}
	}
}