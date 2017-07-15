package com.cloudera.oryx.example

import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by Administrator on 2017/7/14.
  */
object TestLogisticRegression {

  private val log:Logger = LoggerFactory.getLogger("TestLogisticRegression")

  def main(args: Array[String]): Unit = {
    log.info("1. init spark conf and spark context")
    val conf = new SparkConf().setMaster("local[*]")
      .setAppName("Logistic Regression")
    val sc = new SparkContext(conf)

    log.info("2. load data and transform")
    val input = "E:/data/covtype/covtype.data"
    val rawData =sc.textFile(input)
    val data = rawData.map { line =>
      val values = line.split(",").map(_.toDouble)
      val features = Vectors.dense(values.init)
      val label = values.last - 1
      LabeledPoint(label, features)
    }

    log.info("3. split data into (trainData,cvData,testData)")
    val Array(trainData,cvData,testData) = data.randomSplit(Array(0.8,0.1,0.1))
    trainData.cache()
    cvData.cache()
    testData.cache()

    log.info("4. train model")
    val model = new LogisticRegressionWithLBFGS()
      .setNumClasses(7)
      .run(trainData)
    trainData.unpersist()

    log.info("5. compute on cvData and get accuracy")
    val predictionAndLabels = cvData.map{
      case LabeledPoint(label,features) =>
        (model.predict(features),label)
    }
    //多元分类准确度
    val metrics = new MulticlassMetrics(predictionAndLabels)
    val accuracy = metrics.accuracy
    log.info(s"the accuracy is $accuracy")

    //测试集上的准确率
    log.info("6. compute on testData and get accuracy")
    val _predictionAndLabels = testData.map{
      case LabeledPoint(label,features) =>
        (model.predict(features),label)
    }
    //多元分类准确度
    val _metrics = new MulticlassMetrics(_predictionAndLabels)
    val _accuracy = _metrics.accuracy
    log.info(s"the accuracy on testData is "+_accuracy)

  }

  private def parseToLabeledPointRDD(rawData: RDD[String]) = {
    rawData.map{ line =>
      val values = line.split(",").map(_.toDouble)
      val features = Vectors.dense(values.init)
      val label = values.last - 1
      LabeledPoint(label, features)
    }
  }

}
