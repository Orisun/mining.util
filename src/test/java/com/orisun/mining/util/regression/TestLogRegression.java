package com.orisun.mining.util.regression;

import com.orisun.mining.util.DataTransform;
import com.orisun.mining.util.exception.ArgumentException;
import com.orisun.mining.util.math.Vector;

import java.io.*;

public class TestLogRegression {

	/**
	 * 根据训练数据训练出一个LR模型
	 * 
	 * @param imgFile
	 * @param labelFile
	 * @return
	 * @throws IOException
	 * @throws ArgumentException
	 */
	private static LogRegression getLR(String imgFile, String labelFile,
			double c) throws IOException, ArgumentException {
		int population;
		int dimenstion;
		double[] y;
		double[][] x;

		byte[] itemNum_b = new byte[4];
		File infile = new File(labelFile);
		BufferedInputStream br = new BufferedInputStream(new FileInputStream(
				infile));
		br.skip(4);
		br.read(itemNum_b);
		population = DataTransform.bytesToInt(itemNum_b, false);
		y = new double[population];
		x = new double[population][];
		for (int i = 0; i < population; i++) {
			int label = br.read();
			if (label == 7) {// 数字7的类别标签为1
				y[i] = 1;
			} else if (label == 9) {// 数字9的类别标签为-1
				y[i] = -1;
			}
		}
		br.close();

		byte[] dimmension_b = new byte[4];
		infile = new File(imgFile);
		br = new BufferedInputStream(new FileInputStream(infile));
		br.skip(8);
		br.read(dimmension_b);
		dimenstion = DataTransform.bytesToInt(dimmension_b, false) + 1;// 插入首列偏置
		for (int i = 0; i < population; i++) {
			x[i] = new double[dimenstion];
			x[i][0] = 1;// X的第一维上为1
			Vector tmp = new Vector(dimenstion - 1);
			for (int j = 0; j < dimenstion - 1; j++) {
				byte[] data = new byte[8];
				br.read(data);
				tmp.set(j, DataTransform.bytesToDouble(data, false));
			}
			// 输入向量进行正规化
			Vector normalize = tmp;
			// Vector normalize = Normalize.gaussConvert(tmp);
			for (int j = 1; j < dimenstion; j++) {
				x[i][j] = normalize.get(j - 1);
			}
		}
		br.close();

		LogRegression lr = new LogRegression();
		lr.setC(c);
		return lr;
	}

	public static void main(String[] args) throws IOException,
			ArgumentException {
		// 逻辑斯谛回归计算W
		String imgFile = "E:/dataset_MNIST/MNIST/train-images-79-lowdimension.ubyte";
		String labelFile = "E:/dataset_MNIST/MNIST/train-labels-79.ubyte";
		for (int cross = 0; cross < 1; cross++) {
			System.out.println("C=" + cross);
			LogRegression lr = getLR(imgFile, labelFile, cross);
			double[] bestWight = lr.getWight();
			System.out.println("W=" + bestWight);

			// 将W写入文件
			String weightFile = "E:/dataset_MNIST/MNIST/weight-79-lowdimension.ubyte";
			BufferedOutputStream bw = new BufferedOutputStream(
					new FileOutputStream(new File(weightFile)));
			bw.write(DataTransform.intToBytes(lr.getTargetFunction()
					.getDimension(), false));
			for (int i = 0; i < lr.getTargetFunction().getDimension(); i++) {
				bw.write(DataTransform.doubleToBytes(bestWight[i], false));
			}
			bw.close();

			// 对测试数据进行分类
			String imgTestFile = "E:/dataset_MNIST/MNIST/test-images-79-lowdimension.ubyte";
			String labelTestFile = "E:/dataset_MNIST/MNIST/test-labels-79.ubyte";
			BufferedInputStream br1 = new BufferedInputStream(
					new FileInputStream(new File(imgTestFile)));
			BufferedInputStream br2 = new BufferedInputStream(
					new FileInputStream(new File(labelTestFile)));
			byte[] itemNum_b = new byte[4];
			br2.skip(4);
			br2.read(itemNum_b);
			int population = DataTransform.bytesToInt(itemNum_b, false);
			byte[] dimmension_b = new byte[4];
			br1.skip(8);
			br1.read(dimmension_b);
			int dimenstion = DataTransform.bytesToInt(dimmension_b, false) + 1;//
			// 插入首列偏置
			int errNum7 = 0;
			int errNum9 = 0;
			int tatal7 = 0;
			int tatal9 = 0;
			for (int i = 0; i < population; i++) {
				double[] x = new double[dimenstion];
				x[0] = 1;// X的第一维上为1
				Vector tmp = new Vector(dimenstion - 1);
				for (int j = 0; j < dimenstion - 1; j++) {
					byte[] data = new byte[8];
					br1.read(data);
					tmp.set(j, DataTransform.bytesToDouble(data, false));
				}
				// 输入向量进行正规化
				Vector normalize = tmp;
				// Vector normalize = Normalize.gaussConvert(tmp);
				for (int j = 1; j < dimenstion; j++) {
					x[j] = normalize.get(j - 1);
				}
				int exceptedLabel = br2.read();
				// System.out.print("第" + i + "个图片\t" + exceptedLabel + "\t");
				int label = lr.predictLabel(x);
				if (label == 1) {
					label = 7;
				} else if (label == -1) {
					label = 9;
				}
				if (exceptedLabel != label) {
					if (exceptedLabel == 7) {
						errNum7++;
						tatal7++;
					} else if (exceptedLabel == 9) {
						errNum9++;
						tatal9++;
					}
					// System.out.println("分类错误");
				} else {
					if (exceptedLabel == 7) {
						tatal7++;
					} else if (exceptedLabel == 9) {
						tatal9++;
					}
					// System.out.println("分类正确");
				}
			}
			br1.close();
			br2.close();
			System.out.println("7预测错了" + errNum7 + "次,错误率" + 1.0 * errNum7
					/ tatal7);
			System.out.println("9预测错了" + errNum9 + "次,错误率" + 1.0 * errNum9
					/ tatal9);
			System.out
					.println("总错误率:" + 1.0 * (errNum7 + errNum9) / population);
		}
	}
}
