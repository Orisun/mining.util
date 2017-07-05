package com.orisun.mining.util.regression;

import com.orisun.mining.util.exception.ArgumentException;
import com.orisun.mining.util.math.Vector;
import com.orisun.mining.util.math.function.Function;

public class LogisticLossFunction extends Function {

	private int population; // 样本数量
	private double[] Y; // 因变量集合
	private double[][] X; // 自变量集合
	private double C;// 正则项系数

	public LogisticLossFunction(int numberOfSample, int dim) {
		this.population = numberOfSample;
		this.dimension = dim;
		Y = new double[population];
		X = new double[population][];
		for (int i = 0; i < population; i++) {
			X[i] = new double[dimension];
		}
	}

	public LogisticLossFunction(int numberOfSample, int dim, double c) {
		this(numberOfSample, dim);
		this.setC(c);
	}

	public void setC(double c) {
		this.C = c;
	}

	public double getC() {
		return C;
	}

	public void setY(double[] y) {
		assert y.length == population;
		for (int i = 0; i < population; i++) {
			Y[i] = y[i];
		}
	}

	public void setX(double[][] x) {
		assert x.length == population;
		for (int i = 0; i < population; i++) {
			assert x[i].length == dimension;
			for (int j = 0; j < dimension; j++) {
				X[i][j] = x[i][j];
			}
		}
	}

	@Override
	public double getValue(Vector W) {
		double likelihood = 0;
		try {
			for (int i = 0; i < population; i++) {
				double z_i = W.dotProduct(new Vector(X[i]));
				double logValue = Math.log(sigmoid(Y[i] * z_i));
				likelihood += logValue;
			}
		} catch (ArgumentException e) {
			System.err.println("计算向量内积时参数有误！");
		}
		double penalty = 0;
		for (int i = 1; i < W.getDimention(); i++) {
			penalty += W.get(i) * W.get(i);
		}
		penalty = penalty * C / 2;
		return 0 - (likelihood - penalty);// 似然函数减去惩罚项，再取相反数，这样转化为求极小值的最优化问题
	}

	@Override
	public Vector getGradient(Vector W) {
		double[] gradient = new double[dimension];
		try {
			for (int k = 0; k < dimension; k++) {
				double sum = 0;
				for (int i = 0; i < population; i++) {
					double z_i = W.dotProduct(new Vector(X[i]));
					double logValue = sigmoid(0 - Y[i] * z_i);
					sum += (Y[i] * X[i][k] * logValue);
				}
				if (k != 0) {
					gradient[k] = 0 - (sum - C * W.get(k));
				} else {
					gradient[k] = 0 - sum;
				}
			}
		} catch (ArgumentException e) {
			System.err.println("计算向量内积时参数有误！");
		}
		return new Vector(gradient);
	}

	public static double sigmoid(double z) {
		return 1.0 / (1 + Math.pow(Math.E, 0 - z));
	}
}
