package com.cloudera.oryx.example.serving.model

import com.cloudera.oryx.app.schema.InputSchema
import com.cloudera.oryx.app.serving.classreg.model.ClassificationRegressionServingModel
import org.apache.spark.mllib.classification.LogisticRegressionModel
import org.apache.spark.mllib.linalg.Vectors

/**
  * Created by Administrator on 2017/7/10.
  */
class LRScalaServingModel extends ClassificationRegressionServingModel {

  private var model: LogisticRegressionModel = _

  private var inputSchema: InputSchema = _

  override def getFractionLoaded: Float = 1.0f

  def this(model: LogisticRegressionModel, inputSchema: InputSchema) {
    this()
    this.model = model
    this.inputSchema = inputSchema
  }

  def getNumberClasses() = model.numClasses

  def predict(str: Array[String]): String = {
    model.predict(Vectors.dense(str.map(_.toDouble))).toString
  }

}
