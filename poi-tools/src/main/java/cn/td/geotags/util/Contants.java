package cn.td.geotags.util;

public final class Contants {
	public final static String TYPE_CO = "co";
	public final static String TYPE_GP = "gp";
	
	public final static String COORDSYS_WJS84 = "gps";
	public final static String COORDSYS_GCJ02 = "autonavi";
	
	public final static String REQ_GEO = "geo";
	public final static String REQ_POI = "poi";
	public final static String REQ_CELL = "cell";
	
	public final static String PARAM_IN_FILE = "file_in";
	public final static String PARAM_OUT_FILE = "file_out";
	public final static String PARAM_RADIUS = "radius";
	public final static String PARAM_TYPES = "poi_types";
	
	public final static String PARAM_CONTENT = "import";
	public final static String PARAM_REQ_TYPE = "ask_for";
	
	public final static long DEFAULT_RADIUS = 500L;
	
	public final static String BIZ_CELL_NEARBY = "经纬度-最近小区";
	public final static String BIZ_TOWNSHIP = "经纬度-社区街道";
	public final static String BIZ_POI_AROUND = "经纬度-周边 POI";
	public final static String BIZ_GP_TO_TOWN = "聚集点-社区街道";
	public final static String BIZ_GP_TO_AROUND = "聚集点-周边 POI";
	public final static String BIZ_COMPRESS_ZIP = "压缩文件 ZIP";
	
	public final static String FILE_EXT_TXT = ".txt";
	public final static String FILE_EXT_DAT = ".dat";
	public final static String FILE_EXT_ZIP = ".zip";
	
	public final static String PARAM_COORD_SYS = "coordsys";
	public final static String PARAM_COORD_SYS_GPS = "gps";
	public final static String PARAM_COORD_SYS_MAPBAR = "mapbar";
	public final static String PARAM_COORD_SYS_BAIDU = "baidu";
	public final static String PARAM_COORD_SYS_AUTONAVI = "autonavi";
	
	public final static String MONITOR_CALL_AMAP_PREFIX = "call_amap_";
	public final static String MONITOR_TASK_STAGE_START = "process_start";
	public final static String MONITOR_TASK_STAGE_END = "process_end";
	public final static String MONITOR_TASK_STAGE_COMPRESS_BEGIN = "compress_begin";
	public final static String MONITOR_TASK_STAGE_COMPRESS_END = "compress_end";
	public final static String MONITOR_TASK_STAGE_INPUT_FILE = "input_file";
	public final static String MONITOR_TASK_STAGE_COMPRESSED_FILE = "compressed_file";
}
