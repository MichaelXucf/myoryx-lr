package com.cloudera.oryx.example.speed

import com.cloudera.oryx.api.speed.AbstractScalaSpeedModelManager
import com.typesafe.config.Config
import org.apache.hadoop.conf.Configuration
import org.apache.spark.rdd.RDD

/**
  * Created by Administrator on 2017/7/10.
  */
class LRScalaSpeedModelManager(config: Config, message: String) extends AbstractScalaSpeedModelManager[String, String, String](config){

  def this(config:Config) ={
    this(config, "call LRScalaSpeedModelManager constructor")
    //TODO do something
  }

  /**
    * consumer message from kafka update topic
    * @param key
    * @param message
    * @param hadoopConf
    */
  override def consumeKeyMessage(key: String, message: String, hadoopConf: Configuration): Unit = {
    key match {
      case "UP" =>
        //do nothing, hearing our own updates

      //TODO
      case "MODEL" =>
      case "MODEL-REF" =>

      case _ => // ignore

    }

  }

  /**
    * process message from kafka input topic
    * @param newData
    * @return
    */
  override def buildUpdates(newData: RDD[(String, String)]): Iterable[String] = {

      null
  }
}
