package td.tendcloud.locate

object entity {
  case class Probe(mac: String, x: String, y: String, z: String)
  case class ProbeList(probe: java.util.ArrayList[Probe])

  case class RawLog(probeMac: String, probeTime: String, collectTime: String, deviceMac: String, rss: Int)

  case class LogWithPartition(deviceMac: String, probeMac: String, rss: Int, partition: String)
  case class StatisWithPartition(deviceMac: String, probeMac: String, partition: String, maxRss: Double, meanRss: Double, minRss: Double)
  case class SatisWithWeight(deviceMac: String, probeMac: String, partition: String, maxRss: Double, meanRss: Double, minRss: Double, maxWeight: Double, meanWeight: Double, minWeight: Double)
}