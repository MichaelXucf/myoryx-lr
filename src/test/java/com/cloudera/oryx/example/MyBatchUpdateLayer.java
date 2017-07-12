package com.cloudera.oryx.example;

import org.dmg.pmml.PMML;

/**
 * Created by Administrator on 2017/7/12.
 */
public interface MyBatchUpdateLayer {
    PMML runUpdate();
}
