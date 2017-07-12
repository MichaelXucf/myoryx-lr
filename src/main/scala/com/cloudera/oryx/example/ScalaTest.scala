package com.cloudera.oryx.example

import com.cloudera.oryx.api.batch.BatchLayerUpdate

/**
  * Created by Administrator on 2017/7/11.
  */
object ScalaTest{

  def main(args: Array[String]): Unit = {
    matchTest("UP")
    matchTest("MODEL")
    matchTest("MODEL_REF")
    matchTest("A")
    matchTest("B")
    matchTest("AB")
    matchTest("OTHER")

    if (true || true){}

  }


  def matchTest(key:String): Unit ={
    val regex = """A.*||B.*""".r

    key match{
      case "UP" => println("UP")
      case "MODEL" => println("MODEL")
      case "MODEL_REF" => println("MODEL_REF")
      case regex() => println("regex")
      case """A.*||B.*""" => println("regex")
      case _ => println("default")
    }
  }

}
