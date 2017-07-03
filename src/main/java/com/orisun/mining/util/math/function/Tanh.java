package com.orisun.mining.util.math.function;

import com.orisun.mining.util.exception.ArgumentException;
import com.orisun.mining.util.math.Vector;

/**
 * @author Fido
 * @since 20/2/2017
 */
public class Tanh extends ActivationFunction{

    @Override
    public Vector apply(Vector X) {
        double[] values = X.getArray();
        for (int i=0; i < values.length; i++) {
            values[i] = tanh(values[i]);
        }
        return new Vector(values);
    }

    @Override
    public double applySingle(double x) {
        return tanh(x);
    }

    @Override
    public Vector getGradient(Vector X){
        Vector rect = null;
        try {
            Vector partial = new Vector(X.getDimention(), 0.0);
            int idx = 0;
            for (Double v : X) {
                double tanhv = tanh(v);
                partial.set(idx++, 1 - Math.pow(tanhv, 2));
            }
            rect = X.multipleBy(partial);
        } catch (ArgumentException e) {
            // 自身求梯度不会报错
        }
        return rect;
    }

    public double tanh(double x) {
        return Math.tanh(x);
    }
}
