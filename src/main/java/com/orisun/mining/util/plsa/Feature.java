package com.orisun.mining.util.plsa;

/**
 * 词
 * 
 * @author orisun
 * @date 2016年7月10日
 */
class Feature {

    /** 该词在所有词中的编号 **/
    int dim;
    /** 该词在指定文档中的权重 **/
    double weight;

    Feature(int index, double weight) {
        this.dim = index;
        this.weight = weight;
    }
}
