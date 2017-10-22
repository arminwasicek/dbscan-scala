package com.esri.dbscan

import com.esri.smear.Smear
import org.apache.commons.math3.ml.clustering.DoublePoint
import org.apache.commons.math3.ml.distance.{DistanceMeasure, EuclideanDistance}
import com.esri.dbscan.Flag
/**
  */
class DBSCANPoint(val id: Int, val point: DoublePoint) extends Serializable {

  var flag = Flag.BORDER
  var clusterID : Int = -1
  val dist : DistanceMeasure = new EuclideanDistance


  override def equals(other: Any): Boolean = other match {
    case that: DBSCANPoint => id == that.id
    case _ => false
  }

  override def hashCode(): Int = {
    Smear.smear(id)
  }

  def distance2(that: DBSCANPoint) : Double = {
    val d = dist.compute(point.getPoint, that.point.getPoint)
    d*d
  }

  override def toString = s"DBSCANPoint($id,$point,$flag,$clusterID)"
}

object DBSCANPoint extends Serializable {
  def apply(id: Int, p: Array[Double]) : DBSCANPoint = {
    new DBSCANPoint(id, new DoublePoint(p))
  }
}
