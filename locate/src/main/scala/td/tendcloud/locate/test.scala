package td.tendcloud.locate

import org.apache.spark.SparkConf
//import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext

// for disable detail logs on console
import org.apache.log4j.Logger
import org.apache.log4j.Level


/**
 * @author ${user.name} 
 * 
 * step 1: nc -lk 8987
 * step 2: spark-submit --class td.tendcloud.locate.Locator --master local[2] ./locate-0.0.1-SNAPSHOT.jar
 * 
 */
object test {
  def main(args : Array[String]) { 
    // 关掉烦人的 log
    quietLogs()
    
    val hostname = "fe.tendcloud.local"
    val port = 8987
    
    val conf = new SparkConf()
    val sc = new SparkContext(conf)

    val ssc = new StreamingContext(sc, Seconds(10))
    val sqc = new SQLContext(sc)
    import sqc.implicits._
    
    // 获取探针配置
    val probes = utils.getProbeConfig(sc).cache()
    val lines = ssc.socketTextStream(hostname, port)
    val records = lines.map(utils.parseLog(_))
     
    utils.initCache()
    records.window(Seconds(30), Seconds(30)).foreachRDD(rdd => {
      
      val rdd2 = processor.derivePartition(rdd)

      // 对收集的数据进行30秒分割, 并求这 30 秒内RSS的特征值
      val rdd3 = processor.calcWithinPartition(rdd2)

      // 计算用户指定的时长 partition (如 2 分钟), 将 30 秒的特征值合并为用户指定的 partition的特征值
      val rdd4 = processor.mergePartition(rdd3)

      wknn.locate(rdd4, probes)
//          .write
//          .format("com.databricks.spark.csv")
//          .option("header", "true")
//          .save("/home/wherehows/location") 
      .show(200)
    })
    
    ssc.start()
    ssc.awaitTermination() 
  }

  def quietLogs() = {
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)
  }
}