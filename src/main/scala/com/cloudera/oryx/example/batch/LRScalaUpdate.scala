package com.cloudera.oryx.example.batch

import java.util

import com.cloudera.oryx.app.pmml.AppPMMLUtils
import com.cloudera.oryx.example.utils.ScalaMLFunctions
import com.cloudera.oryx.ml.MLUpdate
import com.cloudera.oryx.ml.param.{HyperParamValues, HyperParams}
import com.cloudera.oryx.common.pmml.PMMLUtils
import com.google.common.base.Preconditions
import com.typesafe.config.Config
import org.apache.hadoop.fs.Path
import org.apache.spark.api.java.{JavaRDD, JavaSparkContext}
import org.apache.spark.mllib.classification.{LogisticRegressionModel, LogisticRegressionWithLBFGS}
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.dmg.pmml.{MiningFunction, MiningSchema, PMML}
import org.dmg.pmml.regression.RegressionModel
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
    log.info(s"hyperParamValues size is " + hyperParamValues.size())
    log.info("end to init LRScalaUpdate")
  }

  override def buildModel(sparkContext: JavaSparkContext,
                          trainData: JavaRDD[String],
                          hyperParameters: util.List[_],
                          candidatePath: Path): PMML = {
    log.info("call buildModel")
    //1.Get params
    log.info("1.Get params")
    val numClasses = 7 //hyperParameters.get(0).asInstanceOf[Integer]
    Preconditions.checkArgument(numClasses > 0)

    //2.Transform rdd
    log.info("2.Transform rdd")
    val parsedTrainRDD = parseToLabeledPointRDD(trainData)
    parsedTrainRDD.cache()
    log.info("2.parsedTrainRDD count is: " + parsedTrainRDD.count())

    //3.Train model
    log.info("3.Train model")
    val model = new LogisticRegressionWithLBFGS()
      .setNumClasses(numClasses)
      .run(parsedTrainRDD)
    parsedTrainRDD.unpersist()

    //4.Transform model to PMML
    log.info("4.Transform model to PMML")
    val pmml: PMML = PMMLUtils.fromString(model.toPMML())
    //val pmml: PMML = buildPMML(model)

    model.save(sparkContext.sc, candidatePath.getName + "lr.model")

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
    //TODO
    val pmml: PMML = PMMLUtils.buildSkeletonPMML()
    val schema: MiningSchema = new MiningSchema()
    val m = new RegressionModel(MiningFunction.CLASSIFICATION, null, null)


    AppPMMLUtils.addExtension(pmml, "numClasses", model.numClasses)
    AppPMMLUtils.addExtension(pmml, "numFeatures", model.numFeatures)
    AppPMMLUtils.addExtension(pmml, "weights", model.weights)
    AppPMMLUtils.addExtension(pmml, "intercept", model.intercept)
    AppPMMLUtils.addExtension(pmml, "threshold", model.getThreshold)
    pmml.addModels(m)

    //val is = new ByteArrayInputStream(model.toPMML().getBytes("utf-8"))
    //PMMLUtil.unmarshal(is)
    //PMMLUtils.fromString(model.toPMML())


    return null

  }


}
