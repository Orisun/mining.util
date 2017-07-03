package com.orisun.mining.util.regression;

import com.orisun.mining.util.exception.ArgumentException;
import com.orisun.mining.util.math.Vector;
import com.orisun.mining.util.math.optimization.L_BFGS;
import com.orisun.mining.util.math.optimization.MinOptimization;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class LogRegression {

	private LogisticLossFunction targetFunction = null;// 目标函数
	private double c = 0.0;// 正则项系数，默认没有正则项
	private MinOptimization optimitor = null;// 优化目标函数时使用的优化方法，默认使用L_BFGS
	private Vector w = null;// 模型训练得到的权值
	private double threshold = 0.5;// 作二分类时使用的概率阈值，默认取0.5

	public LogRegression() {
		L_BFGS optimization = new L_BFGS();
		optimization.setEps(1);
		this.setMinOptimization(optimization);
	}

	public void setC(double c) {
		this.c = c;
		if (targetFunction != null) {
			targetFunction.setC(c);
		}
	}

	public void setMinOptimization(MinOptimization optimitor) {
		this.optimitor = optimitor;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public double[] getWight() {
		if (w == null) {
			return null;
		} else {
			return w.getArray();
		}
	}

	public LogisticLossFunction getTargetFunction() {
		return targetFunction;
	}

	/**
	 * 根据训练数据拟合Logistic函数
	 * 
	 * @param trainData
	 * @param trainLabel
	 * @throws ArgumentException
	 */
	public void fit(double[][] trainData, double[] trainLabel)
			throws ArgumentException {
		int population = trainData.length;// 训练样本的数量
		int sampleDimension = trainData[0].length;// 单个训练样本的维度
		targetFunction = new LogisticLossFunction(population, sampleDimension,
				c);
		targetFunction.setX(trainData);
		targetFunction.setY(trainLabel);
		// 初始化权值
		Vector initWeight = new Vector(sampleDimension, 1.0 / (sampleDimension));
		// 迭代优化
		w = optimitor.getBestX(targetFunction, initWeight);
	}

	/**
	 * 根据文件里的训练数据拟合Logistic函数
	 * 
	 * @param trainFile
	 * @param labelFile
	 * @throws IOException
	 * @throws ArgumentException
	 */
	public void fit(String trainFile, String labelFile) throws IOException,
			ArgumentException {
		BufferedReader br = new BufferedReader(new FileReader(labelFile));
		List<Double> labels = new LinkedList<Double>();
		String line = null;
		while ((line = br.readLine()) != null) {
			String cont = line.trim();
			if (cont.length() > 0) {
				labels.add(Double.parseDouble(cont));
			}
		}
		br.close();
		int population = labels.size();// 训练样本的数量

		double[][] trainData = new double[population][];
		br = new BufferedReader(new FileReader(trainFile));
		br.mark(1024);// 在文件开头作个标记
		line = br.readLine().trim();
		int sampleDimension = line.split("\\s+").length;// 单个训练样本的维度
		assert sampleDimension > 0;
		br.reset();// 回到文件开头
		int row = 0;
		while ((line = br.readLine()) != null) {
			String[] arr = line.trim().split("\\s+");
			if (arr.length > 0) {
				assert arr.length == sampleDimension;
				trainData[row] = new double[sampleDimension];
				for (int i = 0; i < sampleDimension; i++) {
					trainData[row][i] = Double.parseDouble(arr[i]);
				}
				row++;
			}
		}
		br.close();
		assert row == population;
		double[] trainLabel = new double[population];
		for (int i = 0; i < population; i++) {
			trainLabel[i] = labels.get(i);
		}
		this.fit(trainData, trainLabel);
	}

	/**
	 * 预测属于正例的概率值
	 * 
	 * @param testData
	 * @return
	 * @throws ArgumentException
	 */
	public double predict(double[] testData) throws ArgumentException {
		assert w != null;
		double produt = 0;// 内积
		for (int i = 0; i < testData.length; i++) {
			produt += w.get(i) * testData[i];
		}
		// 把内积交给sigmoid函数
		return LogisticLossFunction.sigmoid(produt);
	}

	/**
	 * 预测样本标签是1还是0
	 * 
	 * @param testData
	 * @return 0或1
	 * @throws ArgumentException
	 */
	public int predictLabel(double[] testData) throws ArgumentException {
		return predict(testData) > threshold ? 1 : 0;
	}
}
