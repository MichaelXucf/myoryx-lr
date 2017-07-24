package com.cloudera.oryx.example.serving.model

import java.util

import com.cloudera.oryx.app.schema.InputSchema
import com.cloudera.oryx.app.serving.classreg.model.ClassificationRegressionServingModel
import org.apache.spark.ml.Transformer
import org.apache.spark.mllib.classification.LogisticRegressionModel
import org.apache.spark.sql.types.{DoubleType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.dmg.pmml.PMML
import org.jpmml.evaluator.spark.{EvaluatorUtil, TransformerBuilder}
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by Administrator on 2017/7/10.
  */
class LRScalaServingModel extends ClassificationRegressionServingModel {
  private val log:Logger = LoggerFactory.getLogger("LRScalaServingModel")

  private var model: LogisticRegressionModel = _

  private var inputSchema: InputSchema = _

  private var tranfomer: Transformer = _

  private var schema: StructType = _

  private var spark: SparkSession= _

  override def getFractionLoaded: Float = 1.0f

  def this(model: LogisticRegressionModel, inputSchema: InputSchema) {
    this()
    this.model = model
    this.inputSchema = inputSchema
  }

  def this(pmml: PMML, inputSchema: InputSchema) {
    this()
    this.inputSchema = inputSchema
    this.schema = getSchema()
    log.info("init spark session")
    spark = SparkSession.builder()
      .master("local[1]")
      .appName("logistic regression serving layer")
      .getOrCreate()

    log.info("convert pmml to spark transfomer")
    val evaluator = EvaluatorUtil.createEvaluator(pmml)
    val pmmlTransformerBuilder = new TransformerBuilder(evaluator)
      .withOutputCols()
      .exploded(true)
    tranfomer = pmmlTransformerBuilder.build()
  }

  def getNumberClasses() = model.numClasses

  /**
    *
    * @param str 要预测的一行数据，输入格式是已经拆分好的string数组
    * @return
    */
  def predict(str: Array[String]): String = {
    val dataFrame = getDataFrame(str)
    val res = tranfomer.transform(dataFrame)
      .select("prediction")
      .rdd
      .map{
        row =>
          row.getDouble(0)
      }.collect().mkString(",")
    return res
  }

  private def getDataFrame(str: Array[String]): DataFrame = {
    /*val rows = str
      .map(_.split(",").map(_.toDouble))
      .map(arr => arr.:+(0.0)) //add label col
      .map(attr => Row.fromSeq(attr))

    import scala.collection.JavaConversions._
    val list: util.List[Row] = rows.toList
    val testDF = spark.createDataFrame(list, schema)*/

    val line = str.map(_.toDouble).:+(0.0) //add label col
    val rows = Row.fromSeq(line)

    import scala.collection.JavaConversions._
    val list: util.List[Row] = List(rows).toList
    val testDF = spark.createDataFrame(list,schema)

    return testDF
  }

  private def getSchema(): StructType = {
    val features = (1 to inputSchema.getNumFeatures)
      .map(i => StructField("field_" + i, DoubleType, false))
    val label = StructField("label", DoubleType, false)
    return StructType(features.:+(label))
  }

}
