package com.cloudera.oryx.example;

import com.cloudera.oryx.api.batch.BatchLayerUpdate;
import com.cloudera.oryx.common.lang.ClassUtils;
import com.cloudera.oryx.common.settings.ConfigUtils;
import com.cloudera.oryx.example.batch.LRScalaUpdate;
import com.typesafe.config.Config;
import org.apache.hadoop.fs.Path;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/11.
 */
public class BatchTest {

    public static void main(String[] args) {
        String updateClassName = "com.cloudera.oryx.example.batch.LRScalaUpdate";
        Class<?> cls = ClassUtils.loadClass(updateClassName);
        System.out.println(BatchLayerUpdate.class.isAssignableFrom(cls));
        Config config = ConfigUtils.getDefault();
        List<?> alist = new ArrayList<>();
        try {
            BatchLayerUpdate batchLayerUpdate = ClassUtils.loadInstanceOf(
                    updateClassName,
                    BatchLayerUpdate.class,
                    new Class<?>[]{Config.class},
                    new Object[]{config});
            batchLayerUpdate.hashCode();
            ((LRScalaUpdate)batchLayerUpdate).buildModel(null,null,alist, new Path("hdfs://xxx"));
        } catch (IllegalArgumentException iae) {
            BatchLayerUpdate batchLayerUpdate = ClassUtils.loadInstanceOf(updateClassName, BatchLayerUpdate.class);
        }

    }

}
