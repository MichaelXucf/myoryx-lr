package com.cloudera.oryx.example.serving.model

import com.cloudera.oryx.api.serving.{AbstractScalaServingModelManager, ServingModel}
import com.cloudera.oryx.app.pmml.AppPMMLUtils
import com.cloudera.oryx.app.schema.InputSchema
import com.google.common.base.Preconditions
import com.typesafe.config.Config
import org.apache.hadoop.conf.Configuration
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by Administrator on 2017/7/10.
  */
class LRScalaServingModelManager(config: Config, message: String) extends AbstractScalaServingModelManager[String](config) {
  private val log: Logger = LoggerFactory.getLogger("LRScalaServingModelManager")
  private var minModelLoadFraction: Double = .0
  private var model:LRScalaServingModel = null
  private var inputSchema:InputSchema = null

  def this(config: Config) = {
    this(config, "")
    //TODO do something
    minModelLoadFraction = config.getDouble("oryx.serving.min-model-load-fraction")
    Preconditions.checkArgument(minModelLoadFraction >= 0.0 && minModelLoadFraction <= 1.0)
    inputSchema = new InputSchema(config)
  }

  override def consumeKeyMessage(key: String, message: String, hadoopConf: Configuration): Unit = {
    //TODO
    val regex = """MODEL|MODEL-REF""".r
    key match {
      case "UP" =>
      //do nothing
      case regex() =>
        log.info("Loading new model")
        val pmml = AppPMMLUtils.readPMMLFromUpdateKeyMessage(key, message, hadoopConf)
        if (pmml == null) {
          return
        }

        /*val pmmlModel = pmml.getModels.get(0).asInstanceOf[RegressionModel]

        if(model == null || model.getNumberClasses() != md.numClasses){
          model = new LRScalaServingModel(md, inputSchema)
        }*/

        if(model == null ){
          model = new LRScalaServingModel(pmml, inputSchema)
        }

      case _ => throw new IllegalArgumentException("Bad hey: " + key)
    }

  }

  override def getModel: ServingModel = model


}
