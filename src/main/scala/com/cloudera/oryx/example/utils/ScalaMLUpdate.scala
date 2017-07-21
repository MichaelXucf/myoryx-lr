package com.cloudera.oryx.example.utils

import java.io.IOException
import java.util.stream.Collectors
import java.util

import com.cloudera.oryx.api.TopicProducer
import com.cloudera.oryx.api.batch.ScalaBatchLayerUpdate
import com.cloudera.oryx.common.collection.Pair
import com.cloudera.oryx.common.lang.ExecUtils
import org.apache.hadoop.fs.Path
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

/**
  * Created by Administrator on 2017/7/12.
  */
class ScalaMLUpdate[M] extends ScalaBatchLayerUpdate[Object,M,String]{
  private var candidates: Int = 0
  private var hyperParamSearch: String = null
  private var evalParallelism: Int = 0
  private var threshold: Double = .0
  private var maxMessageSize: Int = 0

  override def configureUpdate(sparkContext: SparkContext,
                               timestamp: Long,
                               newData: RDD[(Object, M)],
                               pastData: RDD[(Object, M)],
                               modelDirString: String,
                               modelUpdateTopic: TopicProducer[String, String]): Unit = {
    //TODO do something

  }

  def  findBestCandidatePath(sparkContext: SparkContext,
    newData: RDD[M],
    pastData: RDD[M],
    hyperParameterCombos: util.List[util.List[_]],
    candidatesPath:Path): Path  = {
    //
    /*val func1 = new Function1[Integer,_]{
      override def apply(v1: Integer) = {
        buildAndEval(v1, hyperParameterCombos, sparkContext, newData, pastData, candidatesPath)
      }
    }*/

    /*val pathToEval:Map[Path, Double] = ExecUtils.collectInParallel(
      candidates,
      Math.min(evalParallelism, candidates),
      true,
      toJavaFunction(func1),
      Collectors.toMap(Pair :: getFirst, Pair :: getSecond))*/


    return null

  }

  def functionA(func: Integer => Pair[Path, Double]) ={

  }

  /*implicit def toJavaFunction[U, V](f:Function1[U,V]): Function[U, V] = new Function[U, V] {
    override def apply(t: U): V = f(t)

    override def compose[T](before:Function[_ >: T, _ <: U]):
    Function[T, V] = toJavaFunction(f.compose(x => before.apply(x)))

    override def andThen[W](after:Function[_ >: V, _ <: W]):
    Function[U, W] = toJavaFunction(f.andThen(x => after.apply(x)))
  }*/

  implicit def fromJavaFunction[U, V](f:Function[U,V]): Function1[U, V] = f.apply


  private def buildAndEval(i: Int,
                           hyperParameterCombos: util.List[util.List[_]],
                           sparkContext: SparkContext,
                           newData: RDD[M],
                           pastData: RDD[M], candidatesPath: Path) : Pair[Path, Double] = {
    //TODO do something

    return null

  }

}
