package com.cloudera.oryx.example.batch

import java.util

import com.cloudera.oryx.ml.MLUpdate
import com.cloudera.oryx.ml.param.{HyperParamValues, HyperParams}
import com.google.common.base.Preconditions
import com.typesafe.config.Config
import org.apache.hadoop.fs.Path
import org.apache.spark.api.java.{JavaRDD, JavaSparkContext}
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{DoubleType, StructField, StructType}
import org.apache.spark.sql.{Row, SQLContext}
import org.dmg.pmml.PMML
import org.jpmml.evaluator.spark.{EvaluatorUtil, TransformerBuilder}
import org.jpmml.sparkml.ConverterUtil
import org.slf4j.{Logger, LoggerFactory}


/**
  * Created by michaelxu on 2017/7/7.
  */
class LRScalaUpdateWithPipeLine(config: Config, message: String) extends MLUpdate[String](config) {
  private val log: Logger = LoggerFactory.getLogger("LRScalaUpdate")
  private var hyperParamValues: util.List[HyperParamValues[_]] = _
  private var schema: StructType = _

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
    log.info("1.Get params")
    val numClasses = 7 //hyperParameters.get(0).asInstanceOf[Integer]
    Preconditions.checkArgument(numClasses > 0)

    log.info("2.Transform RDD[String] to DataFrame")
    val spark = new SQLContext(sparkContext.sc)
    val trainDF = parseRDDToDataFrame(spark, trainData)
    trainDF.cache()
    log.info("2.parsedTrainRDD count is: " + trainDF.count())

    log.info("3.Train model")
    val inputCol = trainDF.schema.filter(!_.name.equalsIgnoreCase("label")).map(_.name).toArray
    val vectorAssembler = new VectorAssembler()
      .setInputCols(inputCol)
      .setOutputCol("features")

    vectorAssembler.transform(trainDF)
      .select("label", "features")
      .show(3)

    val mlr = new LogisticRegression()
      .setMaxIter(30)
      .setRegParam(0.0)
      .setElasticNetParam(0.5)

    val pipeline = new Pipeline()
      .setStages(Array(vectorAssembler, mlr))

    val model = pipeline.fit(trainDF)

    trainDF.unpersist()

    log.info("4.Transform model to PMML")
    val pmml = ConverterUtil.toPMML(schema, model)

    /*model.save(sparkContext.sc, candidatePath.getName + "lr.model")*/

    return pmml
  }

  override def evaluate(sparkContext: JavaSparkContext,
                        model: PMML,
                        modelParentPath: Path,
                        testData: JavaRDD[String],
                        trainData: JavaRDD[String]): Double = {
    log.info("1.Transform RDD[String] to DataFrame")
    val spark = new SQLContext(sparkContext.sc)
    val testDF = parseRDDToDataFrame(spark, testData)
    testDF.cache()
    log.info("2.Transform pmml to transfomer")
    val evaluator = EvaluatorUtil.createEvaluator(model)
    val pmmlTransformerBuilder = new TransformerBuilder(evaluator)
      .withOutputCols()
      .exploded(true)
    val transformer = pmmlTransformerBuilder.build()

    //3.Compute raw scores on the test set.
    val predictionAndLabels = transformer.transform(testDF)
      .select("label","prediction")
      .rdd
      .map{
        row =>
          (row.getDouble(1),row.getDouble(0))
      }

    //4.Get evaluation metrics.
    val metrics = new MulticlassMetrics(predictionAndLabels)
    val accuracy = metrics.accuracy

    return accuracy
  }

  override def getHyperParameterValues: util.List[HyperParamValues[_]] = super.getHyperParameterValues

  def parseRDDToDataFrame(spark: SQLContext, rawData: RDD[String]) = {
    //generate schema
    if (schema == null) {
      val oneRow = rawData.take(1).head.split(",")
      val colNums = oneRow.length
      val features = (1 until colNums)
        .map(i => StructField("field_" + i, DoubleType, false))
      val label = StructField("label", DoubleType, false)
      schema = StructType(features.:+(label))
    }

    //transfrom Rdd[String] to Rdd[Row]
    val data = rawData.map(_.split(",").map(_.toDouble))
      .map(arr => arr.init.:+(arr.last - 1)) //label begin from 0
      .map(attr => Row.fromSeq(attr.toSeq))

    val trainDF = spark.createDataFrame(data, schema)
    trainDF
  }

}
