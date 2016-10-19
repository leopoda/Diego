package cn.td.geotags.util;

public final class Contants {
	public final static String TYPE_CO = "co";
	public final static String TYPE_GP = "gp";
	
	public final static String COORDSYS_WJS84 = "gps";
	public final static String COORDSYS_GCJ02 = "autonavi";
	
	public final static String REQ_GEO = "geo";
	public final static String REQ_POI = "poi";
	public final static String REQ_CELL = "cell";
	public final static String REQ_RANK = "top";
	
	public final static String PARAM_IN_FILE = "file_in";
	public final static String PARAM_OUT_FILE = "file_out";
	public final static String PARAM_RADIUS = "radius";
	public final static String PARAM_TYPES = "poi_types";
	
	public final static String PARAM_CONTENT = "import";
	public final static String PARAM_REQ_TYPE = "ask_for";
	
	public final static long DEFAULT_RADIUS = 500L;
	
	public final static String BIZ_CELL_NEARBY = "co - nearby cell";
	public final static String BIZ_TOWNSHIP = "co - district";
	public final static String BIZ_POI_AROUND = "co - around poi";
	public final static String BIZ_GP_TO_TOWN = "gp - district";
	public final static String BIZ_GP_TO_AROUND = "gp - around poi";
	public final static String BIZ_COMPRESS_ZIP = "compress - zip";
	
//	public final static String BIZ_CELL_NEARBY = "a";
//	public final static String BIZ_TOWNSHIP = "b";
//	public final static String BIZ_POI_AROUND = "c";
//	public final static String BIZ_GP_TO_TOWN = "d";
//	public final static String BIZ_GP_TO_AROUND = "e";
//	public final static String BIZ_COMPRESS_ZIP = "f";
	
	public final static String FILE_EXT_TXT = ".txt";
	public final static String FILE_EXT_DAT = ".dat";
	public final static String FILE_EXT_ZIP = ".zip";
	
	public final static String PARAM_COORD_SYS = "coordsys";
	public final static String PARAM_COORD_SYS_GPS = "gps";
	public final static String PARAM_COORD_SYS_MAPBAR = "mapbar";
	public final static String PARAM_COORD_SYS_BAIDU = "baidu";
	public final static String PARAM_COORD_SYS_AUTONAVI = "autonavi";
	public final static String PARAM_RANK_CONDITION = "rank";
	public final static int PARAM_RANK_TOP_N = 1000;
	
	public final static String MONITOR_CALL_AMAP_PREFIX = "today_call_amap_";
	public final static String MONITOR_TASK_STAGE_START = "process_start";
	public final static String MONITOR_TASK_STAGE_END = "process_end";
	public final static String MONITOR_TASK_STAGE_COMPRESS_BEGIN = "compress_begin";
	public final static String MONITOR_TASK_STAGE_COMPRESS_END = "compress_end";
	public final static String MONITOR_TASK_STAGE_INPUT_FILE = "input_file";
	public final static String MONITOR_TASK_STAGE_COMPRESSED_FILE = "compressed_file";
	
	public final static String POI_RANK_PROVINCE_DEFAULT_ALL = "all";
	public final static String POI_RANK_CITY_DEFAULT_ALL = "all";
	public final static String POI_RANK_DISTRICT_DEFAULT_ALL = "all";
	public final static String POI_RANK_TYPE_CODE_BIND_ALL = "000000";
	
	public final static String CACHE_REGION_REGEO = "regeoCache";
	public final static String CACHE_REGION_POI_AROUND = "poiAroundCache";
	
	public final static String ADDITIONAL_KEY_JOB = "JobName";
}
