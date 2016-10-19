package cn.td.geotags.util;

import java.util.stream.Stream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GatherPointCounter {
	public static void main(String[] args) throws IOException {
		String f1 = "C:/Users/pc/Desktop/re-run/gp-3d33e01e7dd77c89a4199b60b1c058bb.dat";
		String f2 = "D:/datahub/input/co-3e3d05a43f3d425f2edd4766efbf37db.dat";
		long x = countGeoPoint(f1, 0);
		System.out.println(x);
		
		long y = countGeoPoint(f2, 1);
		System.out.println(y);
	}
	
	public static long countGeoPoint(String filePath, int fileType) throws IOException {
		try (Stream<String> strm = Files.lines(Paths.get(filePath))) {
			long num = 0;
			switch (fileType) {
			case 0 :
				num = strm.map(ParserUtil::parseAsGatherPoint)
						  .flatMap(o -> o.stream())
						  .map(o -> 1L)
						  .count();
				break;
			case 1:
				num = strm.map(ParserUtil::parseAsCoordinate)
						  .count();
				break;
			
			default:
				throw new RuntimeException("Unsupported type of input file");
			}
			return num;
		}
	}
}
