package com.cloudera.oryx.example

import java.util

import com.typesafe.config.Config
import org.apache.hadoop.fs.Path
import org.apache.spark.api.java.{JavaRDD, JavaSparkContext}
import org.dmg.pmml.PMML

/**
  * Created by Administrator on 2017/7/12.
  */
class MyScalaUpdate(config:Config, message:String) extends MyMLUpdate[String](config){

  def this(config:Config){
    this(config, "call MyMLUpdate constructor")
    println("MyScalaUpdate constructor")
  }

  override def sayHello(word: String): Unit = {
    println(s"hello $word")
  }

  override def buildModel(sparkContext: JavaSparkContext,
                          trainData: JavaRDD[String],
                          hyperParameters: util.List[_],
                          candidatePath: Path): PMML = {
    println("say build model")
    return null
  }
}
