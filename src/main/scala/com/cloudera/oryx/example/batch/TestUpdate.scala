package com.cloudera.oryx.example.batch

import com.cloudera.oryx.api.TopicProducer
import com.cloudera.oryx.api.batch.BatchLayerUpdate
import org.apache.spark.api.java.{JavaPairRDD, JavaSparkContext}

/**
  * Created by Administrator on 2017/7/7.
  */
class TestUpdate extends BatchLayerUpdate[String, String, String]{
  override def runUpdate(sparkContext: JavaSparkContext,
                         timestamp: Long,
                         newData: JavaPairRDD[String, String],
                         pastData: JavaPairRDD[String, String],
                         modelDirString: String,
                         modelUpdateTopic: TopicProducer[String, String]): Unit = {

  }

}

object TestUpdate{
  
}
