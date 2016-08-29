package td.tendcloud.locate

import org.apache.spark.sql.functions
import org.apache.spark.rdd.RDD

import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.row_number

object wknn {
  
  /*
   * WKNN 算法求定位坐标
   * 
   */
  def locate(rdd: RDD[entity.StatisWithPartition], probesRdd: RDD[entity.Probe]) = {
    val sqc = new HiveContext(rdd.sparkContext)
    import sqc.implicits._
    
    // 由 rss 特征值计算权重, 并按权重倒序排列, 取前 k 条记录; 并和探针位置数据集做链接
    val k = 7;
    
    val probes = probesRdd.toDF()
    val rdd5 = processor.calcWeight(rdd)
    
    val w = Window.partitionBy($"deviceMac", $"partition").orderBy($"meanWeight".desc)
    val locate = rdd5.toDF()
                  .join(probes, $"probeMac" === $"mac")
                  .select($"deviceMac", $"probeMac", $"meanRss", $"meanWeight", $"x", $"y", $"z", $"partition")
                  .withColumn("rn", row_number.over(w)).where($"rn" <= k)

//    locate.show()

    // 权重求和
    val weighted = locate.groupBy($"deviceMac", $"partition")
                     .agg(functions.sum($"meanWeight") as "sumWeight")
                     
    // wknn 算法求出定位坐标
    weighted.join(locate, locate("deviceMac") === weighted("deviceMac") && locate("partition") === weighted("partition"))
            .select(locate("deviceMac"), locate("partition"), $"meanWeight", $"sumWeight", $"x", $"y", $"z")
            .withColumn("unifiedWeight", $"meanWeight" / $"sumWeight")
            .select($"deviceMac", $"x" * $"unifiedWeight" as "x", $"y" * $"unifiedWeight" as "y", $"z" * $"unifiedWeight" as "z", $"partition")
            .groupBy($"deviceMac", $"partition")
            .agg(functions.round(functions.sum("x"), 2) as "x", functions.round(functions.sum("y"), 2) as "y", functions.round(functions.sum("z"), 2) as "z")
            .orderBy("deviceMac", "partition")
  }
}