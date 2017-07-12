package com.cloudera.oryx.example.batch

import java.io.ByteArrayInputStream
import java.util

import com.cloudera.oryx.example.utils.ScalaMLFunctions
import com.cloudera.oryx.ml.MLUpdate
import com.cloudera.oryx.ml.param.{HyperParamValues, HyperParams}
import com.google.common.base.Preconditions
import com.typesafe.config.Config
import org.apache.hadoop.fs.Path
import org.apache.spark.api.java.{JavaRDD, JavaSparkContext}
import org.apache.spark.mllib.classification.{LogisticRegressionModel, LogisticRegressionWithLBFGS}
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.dmg.pmml.PMML
import org.jpmml.model.PMMLUtil
import org.slf4j.{Logger, LoggerFactory}


/**
  * Created by michaelxu on 2017/7/7.
  */
class LRScalaUpdate(config: Config, message: String) extends MLUpdate[String](config) {
  private val log: Logger = LoggerFactory.getLogger("LRScalaUpdate")
  private var hyperParamValues: util.List[HyperParamValues[_]] = _

  def this(config: Config) = {
    this(config, "call MLUpdate constructor")
    //TODO set logistic regression paramter
    log.info("begin to init LRScalaUpdate")
    hyperParamValues = new util.ArrayList[HyperParamValues[_]]()
    hyperParamValues.add(HyperParams.fromConfig(config, "oryx.lr.hyperparams.numClasses"))
    log.info("end to init LRScalaUpdate")
  }

   override def buildModel(sparkContext: JavaSparkContext,
                          trainData: JavaRDD[String],
                          hyperParameters: util.List[_],
                          candidatePath: Path): PMML = {
    println("call buildModel")
    if(hyperParameters.size() == 0){
      return null
    }
    //1.Set params
    val numClasses = hyperParameters.get(0).asInstanceOf[Integer]
    Preconditions.checkArgument(numClasses > 0)

    //2.Transform rdd
    val parsedTrainRDD = parseToLabeledPointRDD(trainData)

    //3.Train model
    val model = new LogisticRegressionWithLBFGS()
      .setNumClasses(numClasses)
      .run(parsedTrainRDD)

    //4.Transform model to PMML
    val pmml: PMML = buildPMML(model)

    return pmml
  }

  override def evaluate(sparkContext: JavaSparkContext,
                        model: PMML,
                        modelParentPath: Path,
                        testData: JavaRDD[String],
                        trainData: JavaRDD[String]): Double = {
    //1.Transform testRDD
    val parsedTestRDD = parseToLabeledPointRDD(testData)
    //2.Transform pmml to model
    val logisticRegressionModle = model.getModels.get(0).asInstanceOf[LogisticRegressionModel]
    //3.Compute raw scores on the test set.
    val predictionAndLabels = parsedTestRDD.map {
      case LabeledPoint(label, features) =>
        val prediction = logisticRegressionModle.predict(features)
        (prediction, label)
    }
    //4.Get evaluation metrics.
    val metrics = new MulticlassMetrics(predictionAndLabels)
    val accuracy = metrics.accuracy

    return accuracy
  }

  override def getHyperParameterValues: util.List[HyperParamValues[_]] = super.getHyperParameterValues

  private def parseToLabeledPointRDD(trainData: JavaRDD[String]) = {
    trainData.rdd.map(ScalaMLFunctions.parse_fn)
      .map(line => line.map(_.toDouble))
      .map { line =>
        val features = Vectors.dense(line.init)
        val label = line.last - 1
        LabeledPoint(label, features)
      }
  }

  def buildPMML(model: LogisticRegressionModel): PMML = {
    val is = new ByteArrayInputStream(model.toPMML().getBytes("utf-8"))
    PMMLUtil.unmarshal(is)
    //PMMLUtils.fromString((model.toPMML())
  }


}
