package com.cloudera.oryx.example;

import com.cloudera.oryx.api.batch.BatchLayerUpdate;
import com.cloudera.oryx.common.lang.ClassUtils;
import com.typesafe.config.Config;

/**
 * Created by Administrator on 2017/7/12.
 */
public class TestMLUpdate {

    public static void main(String[] args) {
        /*MyScalaUpdate myc = new MyScalaUpdate(null);
        myc.sayHello("tom");
        myc.sayBye("tom");
        myc.buildModel(null,null,null,null);*/

        MyBatchUpdateLayer updateLayer = loadUpdateInstance("com.cloudera.oryx.example.MyScalaUpdate");
        updateLayer.runUpdate();

    }

    public static MyBatchUpdateLayer loadUpdateInstance(String updateClassName) {
        Class<?> updateClass = ClassUtils.loadClass(updateClassName);

        if (MyBatchUpdateLayer.class.isAssignableFrom(updateClass)) {
            try {
                return ClassUtils.loadInstanceOf(
                        updateClassName,
                        MyBatchUpdateLayer.class,
                        new Class<?>[] { Config.class },
                        new Object[] { null });
            } catch (IllegalArgumentException iae) {
                return ClassUtils.loadInstanceOf(updateClassName, MyBatchUpdateLayer.class);
            }

        } else {
            throw new IllegalArgumentException("Bad update class: " + updateClassName);
        }
    }



}
