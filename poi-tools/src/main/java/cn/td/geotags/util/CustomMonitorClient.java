package cn.td.geotags.util;

import static java.lang.System.getProperty;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.talkingdata.monitor.client.Client;

public class CustomMonitorClient extends Client {
	public static String toJson() {
        Map<String, Object> performance_indexMap = new HashMap<String, Object>();
        
        /*
         * 限制输出 _yyyyMMdd 结尾的 counter
         */
        for (String key : getCounters().keySet()) {
//        	System.out.println(key.substring(key.lastIndexOf("_")+1));
			if (key.lastIndexOf("_") < 0 || (key.lastIndexOf("_") + 1 < key.length()
					&& !key.substring(key.lastIndexOf("_") + 1).matches("(\\d{4})(\\d{1,2})(\\d{1,2})"))) {
        		performance_indexMap.put("counters." + key, getCounters().get(key).get());
        	}
        }
        for (String key : getGauges().keySet())
            performance_indexMap.put("gauges." + key, getGauges().get(key).get());
        for (String key : getMetrics().keySet()) {
            Map<String, Number> metricsMap = getMetrics().get(key).toMap();
            performance_indexMap.put("metrics." + key + ".sum", metricsMap.get("sum"));
            performance_indexMap.put("metrics." + key + ".count", metricsMap.get("count"));
            performance_indexMap.put("metrics." + key + ".average", new DecimalFormat("#.00").format(metricsMap.get("average")));
            performance_indexMap.put("metrics." + key + ".max", metricsMap.get("max"));
            performance_indexMap.put("metrics." + key + ".min", metricsMap.get("min"));
        }
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("host", getProperty("ip"));
        jsonMap.put("app_name", getProperty("appname"));
        jsonMap.put("domain", getProperty("domain"));
        jsonMap.put("performance_index", performance_indexMap);
        return new Gson().toJson(jsonMap).replaceAll("-", "_");
    }

	public static String toJson(boolean sort, boolean pretty) {
        JsonObject performance_indexMap = new JsonObject();
        List<String> counterKeys = new ArrayList<String>(getCounters().keySet());
        List<String> gaugeKeys = new ArrayList<String>(getGauges().keySet());
        List<String> metricKeys = new ArrayList<String>(getMetrics().keySet());
        if (sort) {
            Collections.sort(counterKeys);
            Collections.sort(gaugeKeys);
            Collections.sort(metricKeys);
        }
        
        /*
         * 限制输出 _yyyyMMdd 结尾的 counter
         */
        for (String key : counterKeys) {
			if (key.lastIndexOf("_") < 0 || (key.lastIndexOf("_") + 1 < key.length()
					&& !key.substring(key.lastIndexOf("_") + 1).matches("(\\d{4})(\\d{1,2})(\\d{1,2})"))) {
        		performance_indexMap.addProperty("counters." + key, getCounters().get(key).get());
        	}
        }
        for (String key : gaugeKeys)
            performance_indexMap.addProperty("gauges." + key, getGauges().get(key).get().toString());
        for (String key : metricKeys) {
            Map<String, Number> metricsMap = getMetrics().get(key).toMap();
            performance_indexMap.addProperty("metrics." + key + ".sum", metricsMap.get("sum"));
            performance_indexMap.addProperty("metrics." + key + ".count", metricsMap.get("count"));
            performance_indexMap.addProperty("metrics." + key + ".average", new DecimalFormat("#.00").format(metricsMap.get("average")));
            performance_indexMap.addProperty("metrics." + key + ".max", metricsMap.get("max"));
            performance_indexMap.addProperty("metrics." + key + ".min", metricsMap.get("min"));
        }
        JsonObject root = new JsonObject();
        root.addProperty("host", getProperty("ip"));
        root.addProperty("app_name", getProperty("appname"));
        root.addProperty("domain", getProperty("domain"));
        root.add("performance_index", performance_indexMap);
        GsonBuilder builder = new GsonBuilder();
        if (pretty) {
            builder.setPrettyPrinting();
        }
        return builder.create().toJson(root).replaceAll("-", "_");
    }
}
