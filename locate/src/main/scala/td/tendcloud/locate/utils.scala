package td.tendcloud.locate

import org.apache.spark.SparkContext
import org.apache.spark.sql.Row

import org.apache.commons.io.IOUtils
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import cn.talkingdata.util.tools.CacheUtil
import cn.talkingdata.util.tools.CacheRow

object utils {
  val cache = new CacheUtil()
  
  def initCache() = {
    cache.init()
  }
  
  def add2Cache(deviceMac: String, probeMac: String, rss: String, partition: String, partition2: String) {
    val arr = new Array[Object](5)
    arr(0) = deviceMac
    arr(1) = probeMac
    arr(2) = rss
    arr(3) = partition
    arr(4) = partition2

    cache.add(arr)
  }
  
  def addBatch2Cache(it: Iterator[Row]) = {
    
    var list = new java.util.ArrayList[Array[Object]]()
    while (it.hasNext) {
      val row = it.next()
      val arr = new Array[Object](5)
      
      arr(0) = row.getString(0)
      arr(1) = row.getString(1)
      arr(2) = row.getInt(2).toString()
      arr(3) = row.getString(3)
      arr(4) = row.getString(4)
      
      list.add(arr)
    }

    println()
    println("partition size:" + list.size())
    if (list.size() > 0) {
      cache.addBatch(list)
    }
    
    // return Iterator[U]
//    Iterator.empty
  }
  
  def getMinimumPartition2() = {
    val min = cache.getMin("partition2")
    var res = ""
    if (min == null) {
      res = "19000101000000"
    } else {
      res = min
    }
    res
  }

  def loadCache() = {
    cache.get().toArray().map { o =>
      val p = o.asInstanceOf[CacheRow]
      (p.getDeviceMac, p.getProbeMac, p.getRss, p.getPartition, p.getPartition2)
    }
  }
  
  def resetCache() = {
    cache.clean()
  }
  
  def parseLog(line: String) = {
    val pieces = line.split(' ')
    val probeMac = pieces(0)
    val probeTime = pieces(1)
    val collectTime = pieces(2)
    val deviceMac = pieces(3)
    val rss = pieces(4).toInt
    
    entity.RawLog(probeMac, probeTime, collectTime, deviceMac, rss)
  }

  /*
   * 解析探针的配置文件, json 格式, 打包在 jar 包里
   * 
   */
  def getProbeConfig(sc: SparkContext) = {

    val inputStream = this.getClass.getResourceAsStream("/probeConfig.json")
    val probeConfigJson = IOUtils.toString(inputStream)

    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)

    val probeList = mapper.readValue(probeConfigJson, classOf[entity.ProbeList])
    val probeListRdd = sc.parallelize(probeList.probe.toArray()).map(p => p.asInstanceOf[entity.Probe])
    
    probeListRdd
  }
  
}