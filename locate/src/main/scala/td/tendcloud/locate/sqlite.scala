package td.tendcloud.locate

import org.apache.spark.SparkConf
//import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{ Seconds, StreamingContext }
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext

object sqlite {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("sqlite")
    val sc = new SparkContext(conf)

    val sqc = new SQLContext(sc)
    //    val df = sqc.read.format("jdbc").options(Map("url" -> "jdbc:sqlite://d:/test.db")).load()
    //    
    //    df.show()

    import sqc.implicits._
    val df = utils.getProbeConfig(sc).toDF()
    df.show()

//    df.write.jdbc("jdbc:sqlite:/d:/test.db", "test")
  }
}