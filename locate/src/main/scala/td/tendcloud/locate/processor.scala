package td.tendcloud.locate

import org.apache.spark.sql.SQLContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions
import org.apache.spark.sql.Column

object processor {
  def derivePartition(rdd: RDD[entity.RawLog]) = {
    val sc = rdd.sparkContext
    val sqc = SQLContext.getOrCreate(sc)
    import sqc.implicits._

    val df = rdd.toDF()
                .withColumn("tmp", functions.from_unixtime($"probeTime", "yyyyMMddHHmm"))
                .withColumn("t1", functions.concat($"tmp", functions.lit("00")))
                .withColumn("t2", functions.concat($"tmp", functions.lit("30")))
                .withColumn("probeTime", functions.from_unixtime($"probeTime", "yyyyMMddHHmmss"))

    df.registerTempTable("tmpdf")
    val sql = "select deviceMac, probeMac, rss, case when probeTime >= t2 then t2 else t1 end as partition from tmpdf"

    val tmp = sqc.sql(sql)
                 .withColumn("dummy", functions.from_unixtime(functions.unix_timestamp($"partition", "yyyyMMddHHmmss")))
                 .withColumn("dummy", functions.format_string("%02d", functions.floor(functions.minute($"dummy") / 2) * 2))
                 .withColumn("partition2", functions.concat(functions.substring($"partition", 0, 10), $"dummy", functions.lit("00")))

    val n = 3
    var cuttingPoint = "20990101000000"

    val part = tmp.select("partition2").orderBy($"partition2".desc).distinct()
    val numOfRows = part.count()

    if (numOfRows > n) {
      cuttingPoint = part.take(n).last(0).toString()
    } else if (numOfRows > 0) {
      cuttingPoint = part.take(1).last(0).toString()
    }
    println(cuttingPoint)

    val lastCachedMinimumPartition = utils.getMinimumPartition2()
    println(lastCachedMinimumPartition)
    
    val cached = sc.parallelize(utils.loadCache()).toDF().cache()
    val unioned = tmp.select("deviceMac", "probeMac", "rss", "partition", "partition2").unionAll(cached)

    utils.resetCache()
    unioned.filter(($"partition2" >= cuttingPoint && $"partition2" >= lastCachedMinimumPartition))
           .select("deviceMac", "probeMac", "rss", "partition", "partition2")
           .foreachPartition { x => utils.addBatch2Cache(x) }
//            .foreach { x => utils.add2Cache(x(0).toString(), x(1).toString(), x(2).toString(), x(3).toString(), x(4).toString()) }

    unioned.filter($"partition2" < cuttingPoint && $"partition2" >= lastCachedMinimumPartition)
           .select("deviceMac", "probeMac", "rss", "partition")
           .rdd.map { x => entity.LogWithPartition(x(0).toString(), x(1).toString(), x(2).toString().toInt, x(3).toString()) }
    
  }

  /*
   * 对收集的数据进行30秒分割, 并求这 30 秒内RSS的特征值 (max, mean, min)
   * 
   */
  def calcWithinPartition(rdd: RDD[entity.LogWithPartition]) = {
    val sqc = SQLContext.getOrCreate(rdd.sparkContext)
    import sqc.implicits._

    val df = rdd.toDF()
                .repartition($"deviceMac")
                .groupBy($"deviceMac", $"probeMac", $"partition")
                .agg(functions.max($"rss") as "maxRss", functions.mean($"rss") as "meanRss", functions.min($"rss") as "minRss")
                .select("deviceMac", "probeMac", "partition", "maxRss", "meanRss", "minRss")
                
    val tmpRdd = df.rdd.map { x => entity.StatisWithPartition(x(0).toString(), 
                                                              x(1).toString(), 
                                                              x(2).toString(), 
                                                              x(3).toString().toDouble, 
                                                              x(4).toString().toDouble, 
                                                              x(5).toString().toDouble) }
    tmpRdd
  }

    /*
   * 计算用户指定的时长 partition (如 2 分钟)
   * 将 30 秒的特征值合并为用户指定的 partition的特征值
   * 
   */
  def mergePartition(rdd: RDD[entity.StatisWithPartition]) = {
    val sqc = SQLContext.getOrCreate(rdd.sparkContext)
    import sqc.implicits._
    
    val df = rdd.toDF()
                .withColumn("dummy", functions.from_unixtime(functions.unix_timestamp($"partition", "yyyyMMddHHmmss")))
                .withColumn("dummy", functions.format_string("%02d", functions.floor(functions.minute($"dummy") / 2) * 2))
                .withColumn("partition2", functions.concat(functions.substring($"partition", 0, 10), $"dummy",functions.lit("00")))

    // 将 30 秒的特征值合并为用户指定的 partition的特征值
    val df2 = df.repartition($"deviceMac")
                .groupBy($"deviceMac", $"probeMac", $"partition2")
                .agg(functions.mean($"maxRss") as "mergedMaxRss", functions.mean($"meanRss") as "mergedMeanRss", functions.mean($"minRss") as "mergedMinRss")
                .select("deviceMac", "probeMac", "partition2", "mergedMaxRss", "mergedMeanRss", "mergedMinRss")
    
    val tmp = df2.rdd.map { x => entity.StatisWithPartition(x(0).toString(), 
                                                     x(1).toString(), 
                                                     x(2).toString(), 
                                                     x(3).toString().toDouble, 
                                                     x(4).toString().toDouble, 
                                                     x(5).toString().toDouble) }
    
    tmp
  }

  /*
   * 由 rss 特征值计算距离和权重, 并按权重倒序排列
   * 
   */
  def calcWeight(rdd: RDD[entity.StatisWithPartition]) = {
    val sqc = SQLContext.getOrCreate(rdd.sparkContext)
    import sqc.implicits._
    
    val df = rdd.toDF()
                 .withColumn("maxDist", rss2Distance($"maxRss"))
                 .withColumn("meanDist", rss2Distance($"meanRss"))
                 .withColumn("minDist", rss2Distance($"minRss"))
                 .withColumn("maxWeight", distance2Weight($"maxDist"))
                 .withColumn("meanWeight", distance2Weight($"meanDist"))
                 .withColumn("minWeight", distance2Weight($"minDist"))
                 .select("deviceMac", "probeMac", "partition", "maxRss", "meanRss", "minRss", "maxWeight", "meanWeight", "minWeight")
                 .orderBy($"meanWeight".desc)

    val tmp = df.rdd.map { x => entity.SatisWithWeight(x(0).toString(), 
                                                x(1).toString(), 
                                                x(2).toString(), 
                                                x(3).toString().toDouble,
                                                x(4).toString().toDouble, 
                                                x(5).toString().toDouble,
                                                x(6).toString().toDouble,
                                                x(7).toString().toDouble,
                                                x(8).toString().toDouble) }
    tmp
  }

  /*
   * 距离转换公式
   * 
   */
  private def rss2Distance(rss: Column): Column = {
    functions.pow(10, (functions.abs(rss) - 50) / (10 * 3.2))
  }

  /*
   * 权重转换公式
   * 
   */
  private def distance2Weight(dist: Column): Column = {
    functions.pow(dist * dist, -1)
  }
}