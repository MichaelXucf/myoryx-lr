package com.cloudera.oryx.example.utils

import com.cloudera.oryx.common.text.TextUtils
import org.apache.spark.mllib.regression.LabeledPoint
import org.slf4j.LoggerFactory

/**
  * Created by Administrator on 2017/7/10.
  */
object ScalaMLFunctions {
  private val log = LoggerFactory.getLogger("ScalaMLFunctions")

  def parse_fn(line:String):Array[String] = {
    if(line.startsWith("[") && line.endsWith("]")){
      TextUtils.parseJSONArray(line)
    } else {
      //CSV
      TextUtils.parseDelimited(line,',')
    }

  }


}
