package com.cloudera.oryx.example.utils

import com.cloudera.oryx.api.TopicProducer
import com.cloudera.oryx.api.batch.ScalaBatchLayerUpdate
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

/**
  * Created by Administrator on 2017/7/12.
  */
class ScalaMLUpdate[K,M,U] extends ScalaBatchLayerUpdate[K,M,U]{
  override def configureUpdate(sparkContext: SparkContext,
                               timestamp: Long, newData: RDD[(K, M)],
                               pastData: RDD[(K, M)],
                               modelDirString: String,
                               modelUpdateTopic: TopicProducer[String, U]): Unit = {
    //TODO do something

  }
}
