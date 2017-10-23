package com.esri.dbscan

import org.apache.commons.math3.ml.clustering.DoublePoint
import org.apache.commons.math3.ml.distance.{DistanceMeasure, EuclideanDistance}

/**
  */
class DBSCANPoint(val id: Int, val point: DoublePoint, dist : DistanceMeasure = new EuclideanDistance)
  extends Serializable {

  var flag = Flag.BORDER
  var clusterID : Int = -1


  override def equals(other: Any): Boolean = other match {
    case that: DBSCANPoint => id == that.id
    case _ => false
  }

  override def hashCode(): Int = {
    DBSCANPoint.smear(id)
  }

  def distance2(that: DBSCANPoint) : Double = {
    val d = dist.compute(point.getPoint, that.point.getPoint)
    d*d
  }

  override def toString = s"DBSCANPoint($id,$point,$flag,$clusterID)"
}

object Flag extends Enumeration {
  val CORE, NOISE, BORDER = Value
}

object DBSCANPoint extends Serializable {
  def apply(id: Int, p: Array[Double]) : DBSCANPoint = {
    new DBSCANPoint(id, new DoublePoint(p))
  }

  def smear(hashCode: Int) = {
    val hashCode2 = hashCode ^ ((hashCode >>> 20) ^ (hashCode >>> 12))
    hashCode2 ^ (hashCode2 >>> 7) ^ (hashCode2 >>> 4)
  }
}
