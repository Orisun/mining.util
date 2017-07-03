package com.orisun.mining.util.math.function;

import com.orisun.mining.util.exception.ArgumentException;
import com.orisun.mining.util.math.Vector;

/**
 * @author Fido
 * @since 20/2/2017
 */
public class Sigmoid extends ActivationFunction{

    @Override
    public Vector apply(Vector X) {
        double[] values = X.getArray();
        for (int i=0; i < values.length; i++) {
            values[i] = sigmoid(values[i]);
        }
        return new Vector(values);
    }

    @Override
    public double applySingle(double x) {
        return sigmoid(x);
    }

    @Override
    public Vector getGradient(Vector X){
        Vector rect = null;
        try {
            Vector partial = new Vector(X.getDimention(), 0.0);
            int idx = 0;
            for (Double v : X) {
                double sig = sigmoid(v);
                partial.set(idx++, sig * (1 - sig));
            }
            rect = X.multipleBy(partial);
        } catch (ArgumentException e) {
            // 自身求梯度不会报错
        }
        return rect;
    }

    public double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

}
