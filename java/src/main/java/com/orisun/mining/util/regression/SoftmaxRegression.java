package com.orisun.mining.util.regression;

import com.orisun.mining.util.DataTransform;
import com.orisun.mining.util.exception.ArgumentException;
import com.orisun.mining.util.math.Normalize;
import com.orisun.mining.util.math.Vector;
import com.orisun.mining.util.math.optimization.L_BFGS;

import java.io.*;

public class SoftmaxRegression {
	/**
	 * 读取降维之后的训练数据
	 * 
	 * @param imgFile
	 * @param labelFile
	 * @return
	 * @throws IOException
	 * @throws ArgumentException 
	 */
	private static SoftmaxLossFunction getFunction(String imgFile,
			String labelFile, double c, int k) throws IOException, ArgumentException {
		int population;
		int dimenstion;
		int[] y;
		double[][] x;

		byte[] itemNum_b = new byte[4];
		File infile = new File(labelFile);
		BufferedInputStream br = new BufferedInputStream(new FileInputStream(
				infile));
		br.skip(4);
		br.read(itemNum_b);
		population = DataTransform.bytesToInt(itemNum_b, false);
		y = new int[population];
		x = new double[population][];
		for (int i = 0; i < population; i++) {
			int label = br.read();
			y[i] = label + 1;
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
			// Vector normalize = tmp;
			Vector normalize = Normalize.linearConvert(tmp);
			for (int j = 1; j < dimenstion; j++) {
				x[i][j] = normalize.get(j - 1);
			}
		}
		br.close();

		SoftmaxLossFunction function = new SoftmaxLossFunction(population, k,
				dimenstion * k, c);
		function.setX(x);
		function.setY(y);
		return function;
	}

	private static Vector getWeight(SoftmaxLossFunction logFunction)
			throws ArgumentException {
		L_BFGS optimization = new L_BFGS();
		optimization.setEps(1E-2);
		int dimension = logFunction.getDimension();
		Vector initWeight = new Vector(dimension, 1.0 / dimension);
		return optimization.getBestX(logFunction, initWeight);
	}

	private static int getLabel(Vector X, Vector W, int K, int dimension)
			throws ArgumentException {
		double[] probs = SoftmaxLossFunction.getLabelProb(X, W, K, dimension);
		int label = 0;
		double maxProb = Double.MIN_VALUE;
		for (int i = 0; i < probs.length; i++) {
			if (probs[i] > maxProb) {
				label = i + 1;
				maxProb = probs[i];
			}
		}
		return label;
	}

	public static void main(String[] args) throws IOException,
			ArgumentException {
		int K = 10;// 10个Label
		// Softmax回归计算W
		String imgFile = "E:/dataset_MNIST/MNIST/train-images-lowdimension.ubyte";
		String labelFile = "E:/dataset_MNIST/MNIST/train-labels.idx1-ubyte";
		for (int cross = 0; cross <= 0; cross++) {
			long begin = System.currentTimeMillis();
			System.out.println("C=" + cross);
			SoftmaxLossFunction logFunction = getFunction(imgFile, labelFile,
					cross, K);
			Vector bestWight = getWeight(logFunction);
			System.out.println("W=" + bestWight);

			// 将W写入文件
			String weightFile = "E:/dataset_MNIST/MNIST/weight-lowdimension.ubyte";
			BufferedOutputStream bw = new BufferedOutputStream(
					new FileOutputStream(new File(weightFile)));
			bw.write(DataTransform.intToBytes(logFunction.getDimension(), false));
			for (int i = 0; i < logFunction.getDimension(); i++) {
				bw.write(DataTransform.doubleToBytes(bestWight.get(i), false));
			}
			bw.close();

			// 对测试数据进行分类
			String imgTestFile = "E:/dataset_MNIST/MNIST/test-images-lowdimension.ubyte";
			String labelTestFile = "E:/dataset_MNIST/MNIST/t10k-labels.idx1-ubyte";
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
			int[] total = new int[K];
			int[] errNum = new int[K];
			int errSum = 0;
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
				// Vector normalize = tmp;
				Vector normalize = Normalize.linearConvert(tmp);
				for (int j = 1; j < dimenstion; j++) {
					x[j] = normalize.get(j - 1);
				}
				int exceptedLabel = br2.read() + 1;
				// System.out.print("第" + i + "个图片\t" + exceptedLabel + "\t");
				int label = getLabel(new Vector(x), bestWight, K, dimenstion);
				if (label < 1 || label > K) {
					System.err.println("label=" + label);
				}
				total[exceptedLabel - 1]++;
				if (exceptedLabel != label) {
					System.err.println(exceptedLabel + "\t" + label);
					errNum[exceptedLabel - 1]++;
					errSum++;
				}
			}
			br1.close();
			br2.close();

			// for (int k = 0; k < K; k++) {
			// System.out.println(k + "识别错误率:" + 1.0 * errNum[k] / total[k]);
			// }
			System.out.println("总错误率:" + 1.0 * errSum / population);

			long endtime = System.currentTimeMillis();
			System.out.println("耗时" + 1.0 * (endtime - begin) / 1000 + "秒");
		}
	}
}
