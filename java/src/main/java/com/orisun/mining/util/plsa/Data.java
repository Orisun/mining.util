package com.orisun.mining.util.plsa;


import java.util.ArrayList;
import java.util.List;

/**
 * 文档
 * 
 * @author orisun
 * @date 2016年7月10日
 */
class Data {

    /** 文档中的所有词 **/
    List<Feature> features = new ArrayList<Feature>();
    /** 文档名称 **/
    String docName;

    int size() {
        return features.size();
    }

    Feature getFeatureAt(int i) {
        return features.get(i);
    }
}
