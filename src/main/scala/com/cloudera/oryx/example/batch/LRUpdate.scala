package com.cloudera.oryx.example.batch

import java.util

import com.cloudera.oryx.ml.MLUpdate
import com.cloudera.oryx.ml.param.{HyperParamValues, HyperParams}
import com.typesafe.config.Config
import org.apache.hadoop.fs.Path
import org.apache.spark.api.java.{JavaRDD, JavaSparkContext}
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS
import org.dmg.pmml.PMML
import org.slf4j.{Logger, LoggerFactory}


/**
  * Created by michaelxu on 2017/7/7.
  */
class LRUpdate(config:Config, message:String) extends MLUpdate[String](config){
  private val log:Logger = LoggerFactory.getLogger("LRUpdate")
  private val hyperParamValues:List[HyperParamValues[_]] = null

  def this(config:Config) ={
    this(config, "call MLUpdate constructor")
    //TODO set logistic regression paramter
    //hyperParamValues
    //hyperParamValues.add(HyperParams.fromConfig(config, "oryx.kmeans.hyperparams.k"))

  }

  override def buildModel(sparkContext: JavaSparkContext,
                          trainData: JavaRDD[String],
                          hyperParameters: util.List[_],
                          candidatePath: Path): PMML = {
    //TODO
    val model = new LogisticRegressionWithLBFGS()
      .setNumClasses(numClasses)


    val pmml:PMML = null

    return pmml
  }

  override def evaluate(sparkContext: JavaSparkContext,
                        model: PMML,
                        modelParentPath: Path,
                        testData: JavaRDD[String],
                        trainData: JavaRDD[String]): Double = {
    //TODO

    return 0
  }


}
