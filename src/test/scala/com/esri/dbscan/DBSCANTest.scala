package com.esri.dbscan

import org.scalatest._
import scala.io.Source

class DBSCANTest extends FlatSpec with Matchers {


  "DBSCAN" should "cluster" in {
    val points = Array(
      DBSCANPoint(0, Array(9, 9)),
      DBSCANPoint(1, Array(11, 9))
    )

    val clusters = DBSCAN(3, 2).cluster(points).toList

    clusters.length shouldBe 1
  }

  "DBSCAN" should "find one cluster" in {
    val points = Array(
      DBSCANPoint(0, Array(0, 0)),
      DBSCANPoint(1, Array(0, 2)),
      DBSCANPoint(2, Array(0, 4)),
      DBSCANPoint(3, Array(0, 6)),
      DBSCANPoint(4, Array(0, 8)),
      DBSCANPoint(5, Array(3, 0))
    )
    val clusters = DBSCAN(2.5, 2).cluster(points).toList

    clusters.length shouldBe 1
    clusters(0) should contain only(points(0), points(1), points(2), points(3), points(4))

    points(5).flag shouldBe Flag.NOISE
  }

  /**
    * http://people.cs.nctu.edu.tw/~rsliang/dbscan/testdatagen.html
    */
  "DBSCAN" should "have 6 clusters and 20 outliers" in {

    val points = Source.fromURL(getClass.getResource("/dat_4_6_6_20.txt")).getLines().map(line => {
      val splits = line.split(' ')
      DBSCANPoint(splits(0).toInt, Array(splits(1).toDouble, splits(2).toDouble))
    }).toArray

    val results = Source.fromURL(getClass.getResource("/res_4_6_6_20.txt")).getLines().map(line => {
      val splits = line.split(',')
      splits.tail.map(_.toInt)
    }).toArray

    val clusters = DBSCAN(4, 6).cluster(points).toList

    clusters.length shouldBe 6

    clusters.zipWithIndex.foreach {
      case (cluster, index) => {
        val result = results(index)
        cluster.foreach(point => {
          result should contain(point.id)
        })
        val ids = cluster.map(_.id)
        result.foreach(id => {
          ids should contain(id)
        })
      }
    }
    points.filter(_.flag == Flag.NOISE).length shouldBe 20
  }

  "DBSCAN" should "have 20 clusters and 20 outliers" in {

    val points = Source.fromURL(getClass.getResource("/dat_4_10_20_20.txt")).getLines().map(line => {
      val splits = line.split(' ')
      DBSCANPoint(splits(0).toInt, Array(splits(1).toDouble, splits(2).toDouble))
    }).toArray

    val results = Source.fromURL(getClass.getResource("/res_4_10_20_20.txt")).getLines().map(line => {
      val splits = line.split(',')
      splits.tail.map(_.toInt)
    }).toArray

    val clusters = DBSCAN(4, 10).cluster(points).toList

    clusters.length shouldBe 20

    clusters.zipWithIndex.foreach {
      case (cluster, index) => {
        val result = results(index)
        cluster.foreach(point => {
          result should contain(point.id)
        })
        val ids = cluster.map(_.id)
        result.foreach(id => {
          ids should contain(id)
        })
      }
    }
    points.filter(_.flag == Flag.NOISE).length shouldBe 20
  }

  /*
    case class ClusterablePoint(id: Int, x: Double, y: Double) extends Clusterable {
      override val getPoint: Array[Double] = Array(x, y)
    }

    "DBSCAN" should "match commons math" in {
      val smiley = Source.fromURL(getClass.getResource("/smiley1.txt")).getLines().map(line => {
        val splits = line.split(' ')
        (splits(0).toInt, splits(1).toDouble, splits(2).toDouble)
      }).toSeq

      val orig = smiley.map { case (id, x, y) => DBSCANPoint(id, x, y) }

      val dest = smiley.map { case (id, x, y) => ClusterablePoint(id, x, y) }

      val origRes = DBSCAN(200000, 10).cluster(orig).toSeq

      val destRes = new DBSCANClusterer[ClusterablePoint](200000, 10).cluster(dest)

      origRes.length shouldBe destRes.length

      origRes.zipWithIndex.foreach {
        case (points, i) => {
          val cluster = destRes.get(i).getPoints
          points.zipWithIndex.foreach {
            case (point, j) => {
              cluster.exists(_.id == point.id) shouldBe true
            }
          }
        }
      }
    }
  */

}
