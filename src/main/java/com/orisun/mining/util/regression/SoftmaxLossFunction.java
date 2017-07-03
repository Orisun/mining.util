package com.orisun.mining.util.regression;

import com.orisun.mining.util.exception.ArgumentException;
import com.orisun.mining.util.math.Vector;
import com.orisun.mining.util.math.function.Function;

public class SoftmaxLossFunction extends Function {

	private int population; // 样本数量
	private int x_dim;
	private int K;// 类别数目
	private int[] Y; // 类别标签从1到K,Y的维度是population
	private double[][] X; // 自变量集合,X的第一个维度是population，第二个维度是x_dim
	private double C;// 正则项系数

	public SoftmaxLossFunction(int numberOfSample, int labelNum, int dim,
			double c) {
		this.population = numberOfSample;
		this.K = labelNum;
		this.dimension = dim;
		this.x_dim = dimension / K;
		Y = new int[population];
		X = new double[population][];
		for (int i = 0; i < population; i++) {
			X[i] = new double[x_dim];
		}
		this.C = c;
	}

	public void setY(int[] y) {
		assert y.length == population;
		for (int i = 0; i < population; i++) {
			assert y[i] >= 1 && y[i] <= K;// 类别标签从1到K
			Y[i] = y[i];
		}
	}

	public void setX(double[][] x) {
		assert x.length == population;
		for (int i = 0; i < population; i++) {
			assert x[i].length == x_dim;
			for (int j = 0; j < x_dim; j++) {
				X[i][j] = x[i][j];
			}
		}
	}

	/**
	 * 
	 * @param xi
	 * @param weight
	 *            weight的维度等于K*dimension
	 * @param K
	 * @param dimension
	 *            xi的维度
	 * @return
	 * @Description:计算样本点x_i属于各个Label的概率
	 */
	public static double[] getLabelProb(Vector xi, Vector weight, int K,
			int dimension) {
		double[] rect = new double[K];
		double sum = 0;
		for (int i = 0; i < K; i++) {
			Vector w = new Vector(dimension);
			for (int j = 0; j < dimension; j++) {
				w.set(j, weight.get(i * dimension + j));
			}
			try {
				rect[i] = Math.exp(w.dotProduct(xi));
			} catch (ArgumentException e) {
				System.err.println("计算向量内积时参数有误！");
			}
			sum += rect[i];
		}
		for (int i = 0; i < K; i++) {
			rect[i] /= sum;
		}
		return rect;
	}

	@Override
	public double getValue(Vector W) {
		double likelihood = 0;
		for (int i = 0; i < population; i++) {
			Vector xi = new Vector(X[i]);
			double[] classProb = getLabelProb(xi, W, K, x_dim);
			int label = Y[i];
			double prob = classProb[label - 1];
			likelihood += Math.log(prob);
		}
		double penalty = 0;
		penalty = C / 2 * Math.pow(W.norm2(), 2);
		return penalty - likelihood / population;
	}

	@Override
	public Vector getGradient(Vector W) {
		double[] gradient = new double[dimension];
		for (int j = 0; j < K; j++) {
			Vector sum = new Vector(x_dim);
			for (int i = 0; i < population; i++) {
				Vector xi = new Vector(X[i]);
				double[] classProb = getLabelProb(xi, W, K, x_dim);
				double prob = classProb[j];
				double multiplier = 0 - prob;
				if (Y[i] == (j + 1)) {
					multiplier += 1;
				}
				try {
					sum = sum.add(xi.multipleBy(multiplier));
				} catch (ArgumentException e) {
					System.err.println("计算向量和时参数有误！");
				}
			}
			sum = sum.multipleBy(1.0 / population);

			for (int i = 0; i < x_dim; i++) {
				gradient[i + j * x_dim] = C * W.get(i + j * x_dim)
						- sum.get(i);
			}
		}
		return new Vector(gradient);
	}

	public int getK() {
		return K;
	}

}
