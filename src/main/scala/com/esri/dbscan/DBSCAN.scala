package com.esri.dbscan

import com.esri.core.geometry.{Envelope2D, QuadTree}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.collection.parallel.ParMap

class DBSCAN(eps: Double, minPoints: Int) extends Serializable {

  val eps2 = eps * eps
  val visited = mutable.Set[DBSCANPoint]()

  def clusterWithID(points: Iterable[DBSCANPoint]): Iterable[DBSCANPoint] = {
    cluster(points)
      .zipWithIndex.flatMap {
      case (iter, index) => {
        iter.map(DBSCANPoint(_, index))
      }
    }
  }

  def cluster(points: Iterable[DBSCANPoint]): Iterable[Seq[DBSCANPoint]] = {

    val (xmin, ymin, xmax, ymax) = points.par.foldLeft(
      (Double.NegativeInfinity, Double.NegativeInfinity, Double.PositiveInfinity, Double.PositiveInfinity)) {
      case ((xmin, ymin, xmax, ymax), point) => {
        (xmin min point.x, ymin min point.y, xmax max point.x, ymax max point.y)
      }
    }

    val qt = new QuadTree(new Envelope2D(xmin, ymin, xmax, ymax), 16)
    val index2Point = points.zipWithIndex.map {
      case (point, index) => {
        qt.insert(index, new Envelope2D(point.x, point.y, point.x, point.y)) -> point
      }
    }.toMap

    val neighborhood = points.par.map(p => {
      val arr = new ArrayBuffer[DBSCANPoint]()
      val xmin = p.x - eps
      val ymin = p.y - eps
      val xmax = p.x + eps
      val ymax = p.y + eps
      val iterator = qt.getIterator(new Envelope2D(xmin, ymin, xmax, ymax), 0.0000001)
      var index = iterator.next
      while (index != -1) {
        val q = index2Point(index)
        if ((p distance2 q) < eps2) {
          arr += q
        }
        index = iterator.next
      }
      p -> arr.toArray
    }).toMap

    points.flatMap(point => {
      if (!visited.contains(point)) {
        visited += point
        val neighbors = neighborhood(point)
        if (neighbors.length < minPoints) {
          point.flag = Flag.NOISE
          None
        } else {
          point.flag = Flag.CORE
          expand(point, neighbors, neighborhood)
        }
      }
      else {
        None
      }
    })
  }

  def expand(point: DBSCANPoint,
             neighbors: Array[DBSCANPoint],
             neighborhood: ParMap[DBSCANPoint, Array[DBSCANPoint]]
            ) = {
    val cluster = new ArrayBuffer[DBSCANPoint]()
    cluster += point
    val queue = mutable.Queue(neighbors)
    while (queue.nonEmpty) {
      for {
        neighbor <- queue.dequeue
        if !visited.contains(neighbor)
      } {
        visited += neighbor
        cluster += neighbor
        val neighborNeighbors = neighborhood(neighbor)
        if (neighborNeighbors.length >= minPoints) {
          neighbor.flag = Flag.CORE
          queue += neighborNeighbors
        }
      }
    }
    Some(cluster)
  }
}

object DBSCAN extends Serializable {
  def apply(eps: Double, minPoints: Int) = {
    new DBSCAN(eps, minPoints)
  }
}
