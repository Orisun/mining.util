package com.orisun.mining.util.math.function;

import com.orisun.mining.util.math.Vector;

/**
 * @author Fido
 * @since 20/2/2017
 */
public abstract class ActivationFunction {

    /**
     * 给定X求函数值
     *
     * @param X
     * @return
     */
    public abstract Vector apply(Vector X);

    /**
     * 给定X求函数值
     *
     * @param X
     * @return
     */
    public abstract double applySingle(double X);

    /**
     * 给定X求导函数值,由于要对各个自变量求偏导，所以结果是个向量
     *
     * @param X
     * @return
     */
    public abstract Vector getGradient(Vector X);
}
