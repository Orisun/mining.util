package com.orisun.mining.util.neuralnetwork;

import com.orisun.mining.util.correlation.Pearson;
import com.orisun.mining.util.exception.ArgumentException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TestBPNetwork {

	public static void main(String[] args) throws IOException {
		final int populationNum = 1232365;
		final int featureNum = 33;
		final int trainSize = (int) (0.8 * populationNum);
		// 从文件中读取特征变量和目标变量
		double[][] x_train = new double[trainSize][];
		double[][] y_train = new double[trainSize][];
		double[][] x_test = new double[populationNum - trainSize][];
		double[][] y_test = new double[populationNum - trainSize][];
		BufferedReader br = new BufferedReader(new FileReader(
				"C:\\Users\\lagou\\Downloads\\good_corpus.txt"));
		int row = 0;
		for (; row < trainSize; row++) {
			String[] arr = br.readLine().split("\\s+");
			assert arr.length == featureNum + 1;
			x_train[row] = new double[featureNum];
			y_train[row] = new double[1];
			int col = 0;
			for (; col < featureNum; col++) {
				x_train[row][col] = Double.parseDouble(arr[col]);
			}
			y_train[row][0] = Double.parseDouble(arr[col]);
		}
		for (; row < populationNum; row++) {
			String[] arr = br.readLine().split("\\s+");
			assert arr.length == featureNum + 1;
			x_test[row - trainSize] = new double[featureNum];
			y_test[row - trainSize] = new double[1];
			int col = 0;
			for (; col < featureNum; col++) {
				x_train[row - trainSize][col] = Double.parseDouble(arr[col]);
			}
			y_test[row - trainSize][0] = Double.parseDouble(arr[col]);
		}
		br.close();

		// 训练BP网络，并在测试集上进行测试
		int hiddenSize = BPNetwork.getHiddenSize(featureNum, 1);
		BPNetwork bp = new BPNetwork(hiddenSize, trainSize, x_train, y_train);
		bp.train(300, 1E10);
		double[] y_hat = new double[populationNum - trainSize];
		for (int i = 0; i < x_test.length; i++) {
			y_hat[i] = bp.getOutput(x_test[i])[0];
		}
		// 计算测试集上目标变量预测值和真实值的Pearson相关系数
		double[] y_true = new double[y_test.length];
		for (int i = 0; i < y_test.length; i++) {
			y_true[i] = y_test[i][0];
		}
		try {
			double pearson = Pearson.corrcoef(y_true, y_hat);
			System.out.println("Pearson correlation coefficient " + pearson);
		} catch (ArgumentException e) {
			e.printStackTrace();
		}
	}
}
