package cn.td.geotags.util;

import java.util.Arrays;
import java.util.List;

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
	
	public final static String BIZ_CELL_NEARBY = "经纬度-最近小区";
	public final static String BIZ_TOWNSHIP = "经纬度-社区街道";
	public final static String BIZ_POI_AROUND = "经纬度-周边 POI";
	public final static String BIZ_GP_TO_TOWN = "聚集点-社区街道";
	public final static String BIZ_GP_TO_AROUND = "聚集点-周边 POI";
	public final static String BIZ_COMPRESS_ZIP = "压缩文件 ZIP";
	
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
	
	public final static int TIMEOUT = 1000 * 60;
	
	public final static List<String> AMAP_PROVINCE_CODE = Arrays.asList(
			"110000", // 北京市
			"120000", // 天津市
			"310000", // 上海市
			"500000", // 重庆市
			"130000", // 河北省
			"140000", // 山西省
			"210000", // 辽宁省
			"220000", // 吉林省
			"230000", // 黑龙江省
			"320000", // 江苏省
			"330000", // 浙江省
			"340000", // 安徽省
			"350000", // 福建省
			"360000", // 江西省
			"370000", // 山东省
			"410000", // 河南省
			"420000", // 湖北省
			"430000", // 湖南省
			"440000", // 广东省
			"460000", // 海南省
			"510000", // 四川省
			"520000", // 贵州省
			"530000", // 云南省
			"610000", // 陕西省
			"620000", // 甘肃省
			"630000", // 青海省
			"710000", // 台湾省
			"450000", // 广西壮族自治区
			"150000", // 内蒙古自治区
			"540000", // 西藏自治区
			"640000", // 宁夏回族自治区
			"650000", // 新疆维吾尔自治区
			"810000", // 香港特别行政区
			"820000"  // 澳门特别行政区
	);
	
	public final static String POI_RANK_PROVINCE_DEFAULT_ALL = "all";
	public final static String POI_RANK_CITY_DEFAULT_ALL = "all";
	public final static String POI_RANK_DISTRICT_DEFAULT_ALL = "all";
	public final static String POI_RANK_TYPE_CODE_BIND_ALL = "000000";
}
