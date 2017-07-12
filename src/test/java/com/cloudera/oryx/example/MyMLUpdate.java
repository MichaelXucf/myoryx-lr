package com.cloudera.oryx.example;

import com.typesafe.config.Config;
import org.apache.hadoop.fs.Path;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.dmg.pmml.PMML;

import java.util.List;

/**
 * Created by Administrator on 2017/7/12.
 */
public abstract class MyMLUpdate<M> implements MyBatchUpdateLayer{

    protected MyMLUpdate(Config config){
        System.out.println("MyMLUpdate constructor");
    }

    public abstract void sayHello(String word);

    public abstract PMML buildModel(JavaSparkContext sparkContext,
                                    JavaRDD<M> trainData,
                                    List<?> hyperParameters,
                                    Path candidatePath);

    public void sayBye(String name){
        System.out.println("Bye, "+name);
    }

    @Override
    public PMML runUpdate() {
        System.out.println("runUpdate in MyMLUpdate");
        buildModel(null,null,null,null);
        return null;
    }
}
