package com.orisun.mining.util.math.function;

import com.orisun.mining.util.math.Vector;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @Author: Fido
 * @Date: 20/2/2017
 */
public class TestActivationFunction {

    private static Vector X;

    @BeforeClass
    public static void setup() {
        double[] arr = new double[] { 1, 1 };
        X = new Vector(arr);
    }

    @Test
    public void testSigmoid(){
        Sigmoid sig = new Sigmoid();
        double y = sig.apply(X).get(0);
        Assert.assertTrue(y == (1/(1 + Math.exp(-1))));
    }

    @Test
    public void testSigMoidGrad(){
        Sigmoid sig = new Sigmoid();
        double y = sig.getGradient(X).get(0);
        Assert.assertTrue(y == sig.applySingle(X.get(0)) * (1 - sig.applySingle(X.get(0))));
    }
}
