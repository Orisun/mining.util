package com.orisun.mining.util.neuralnetwork;

/**
 * Back Propagation Network
 * 
 * @Author:zhangchaoyang
 * @Since:2014-10-1
 * @Version:
 */
public class BPNetwork {

	private final int inputSize;// 输入层节点个数
	private final int hiddenSize;// 隐藏层节点个数
	private final int outputSize;// 输出层节点个数
	private double[][] w;// 隐藏层到输出层的权值矩阵
	private double[][] v;// 输入层到隐藏层的权值矩阵
	private double[][] preDeltaOfW;// 隐藏层到输出层的权值矩阵在上一次的迭代中的变化量
	private double[][] preDeltaOfV;// 输入层到隐藏层的权值矩阵在上一次的迭代中的变化量
	private final int population;// 训练样本的个数
	private final double[][] x;// 输入值
	private double[][] y;// 隐藏层的输出值
	private double[][] o;// 实际输出值
	private final double[][] d;// 期望输出值
	private static final double TOLERANCE = 0.03;// 系统误判小于该值时停止迭代训练
	private static final int ITERATION = 1500;// 训练时的最大迭代次数
	private double ALPHA = 0.95;// 动量项系数
	private double ETA = 1;// 学习率，即步长
	private static final double BETA = 0.75;// 学习率动态调整系数。本次权值调整使系统误判增加时，学习率乘以该值
	private static final double THETA = 1.01;// 学习率动态调整系数。本次权值调整使系统误判减小时，学习率乘以该值
	private double LAMBDA = 1;// 陡度因子。我们对sigmoid函数稍做变形，让自变量x除以该值。进入平坦区域（即系统误判变化量很小）后，该值应该大于1，退出平坦区域后恢复成1
	private static final int JAMA = 4;// 陡度因子的增长系数
	private static final double FLAT = 0.01;// 前后两次迭代系统误差之差小于该值时说明进入平坦区域，陡度因子需要调整

	/**
	 * 根据输入层和输出层的神经元个数，给出一种建议的隐藏层神经元个数
	 * 
	 * @param inputSize
	 * @param outputSize
	 * @return
	 */
	public static int getHiddenSize(int inputSize, int outputSize) {
		return (int) (Math.sqrt(0.43 * inputSize * outputSize + 0.12
				* outputSize * outputSize + 2.54 * inputSize + 0.77
				* outputSize + 0.35) + 0.51);
	}

	public BPNetwork(int hiddenSize, int population, double[][] x, double[][] d) {
		this.population = population;
		assert x.length == population;
		this.inputSize = x[0].length + 1;// 要加集偏置，所以这里维度要加1
		assert d.length == population;
		this.outputSize = d[0].length;
		this.hiddenSize = hiddenSize;
		this.x = new double[population][];
		for (int i = 0; i < population; i++) {
			this.x[i] = new double[inputSize];
			this.x[i][0] = 1;// x[i][0]是偏置，值为1
			for (int j = 1; j < inputSize; j++) {
				this.x[i][j] = x[i][j - 1];
			}
		}
		this.d = d;
		// 把所有权值初始化为[0,1)上的随机值
		w = new double[hiddenSize][];
		preDeltaOfW = new double[hiddenSize][];
		System.out.println("Init W=");
		for (int i = 0; i < hiddenSize; i++) {
			w[i] = new double[outputSize];
			preDeltaOfW[i] = new double[outputSize];
			for (int j = 0; j < outputSize; j++) {
				w[i][j] = Math.random();
				System.out.print(w[i][j] + "\t");
				// w[i][j] = 1.0 / outputSize;
				preDeltaOfW[i][j] = 0;
			}
			System.out.println();
		}
		v = new double[inputSize][];
		System.out.println("Init V=");
		preDeltaOfV = new double[inputSize][];
		for (int i = 0; i < inputSize; i++) {
			v[i] = new double[hiddenSize];
			preDeltaOfV[i] = new double[hiddenSize];
			for (int j = 1; j < hiddenSize; j++) {// 没必要给v[i][0]赋值，因为隐藏层的第一个神经元没有和任意一个输入层的神经元相连，它是偏置，值为1
				v[i][j] = Math.random();
				// v[i][j] = 1.0 / outputSize;
				System.out.print(v[i][j] + "\t");
				preDeltaOfV[i][j] = 0;
			}
			System.out.println();
		}
		// 初始化隐藏层和实际输出层的值
		y = new double[population][];
		for (int i = 0; i < population; i++) {
			y[i] = new double[hiddenSize];
			y[i][0] = 1;// y[i][0]是偏置，值为1
		}
		o = new double[population][];
		for (int i = 0; i < population; i++) {
			o[i] = new double[outputSize];
		}
	}

	/**
	 * sigmoid激活函数
	 * 
	 * @param net
	 *            自变量
	 * @param lambda
	 *            陡度因子
	 * @return
	 */
	private double sigmoid(double net, double lambda) {
		double rect = 1.0 / (1 + Math.pow(Math.E, -net / lambda));
		return rect;
	}

	/**
	 * 前向传播，由输入层计算出输出层的值
	 */
	private void forward() {
		for (int i = 0; i < population; i++) {
			for (int j = 1; j < hiddenSize; j++) {
				double net = 0;// 隐藏层的净输入
				for (int k = 0; k < inputSize; k++) {
					net += x[i][k] * v[k][j];
				}
				y[i][j] = sigmoid(net, LAMBDA);// 隐藏层的输出
			}
			for (int j = 0; j < outputSize; j++) {
				double net = 0;// 输出层的净输入
				for (int k = 0; k < hiddenSize; k++) {
					net += y[i][k] * w[k][j];
				}
				o[i][j] = sigmoid(net, LAMBDA);// 输出层的输出
			}
		}
	}

	/**
	 * 后向反馈，由系统误判的梯度下降，调整各层的权值向量
	 */
	private void backward() {
		double[][] deltaOfW = new double[hiddenSize][];
		for (int i = 0; i < hiddenSize; i++) {
			deltaOfW[i] = new double[outputSize];
		}
		double[][] deltaOfV = new double[inputSize][];
		for (int i = 0; i < inputSize; i++) {
			deltaOfV[i] = new double[hiddenSize];
		}
		double[][] delta = new double[population][];
		for (int i = 0; i < population; i++) {
			delta[i] = new double[outputSize];
			for (int j = 0; j < outputSize; j++) {
				delta[i][j] = (d[i][j] - o[i][j]) * o[i][j] * (1 - o[i][j]);
			}
		}
		for (int i = 0; i < hiddenSize; i++) {
			for (int j = 0; j < outputSize; j++) {
				for (int k = 0; k < population; k++) {
					deltaOfW[i][j] += delta[k][j] * y[k][i];
				}
				deltaOfW[i][j] = (1 - ALPHA) * ETA * deltaOfW[i][j] + ALPHA
						* preDeltaOfW[i][j];
				w[i][j] += deltaOfW[i][j];
				preDeltaOfW = deltaOfW;
			}
		}
		for (int i = 0; i < inputSize; i++) {
			for (int j = 1; j < hiddenSize; j++) {
				for (int k = 0; k < population; k++) {
					double temp = 0;
					for (int l = 0; l < outputSize; l++) {
						temp += delta[k][l] * w[j][l];
					}
					deltaOfV[i][j] += temp * y[k][j] * (1 - y[k][j]) * x[k][i];
				}
				deltaOfV[i][j] = (1 - ALPHA) * ETA * deltaOfV[i][j] + ALPHA
						* preDeltaOfV[i][j];
				v[i][j] += deltaOfV[i][j];
				preDeltaOfV = deltaOfV;
			}
		}
	}

	/**
	 * 计算系统平方误判
	 * 
	 * @return
	 */
	private double getSE() {
		double error = 0;
		for (int i = 0; i < population; i++) {
			for (int j = 0; j < outputSize; j++) {
				error += Math.pow(d[i][j] - o[i][j], 2);
			}
		}
		error /= 2;
		return error;
	}

	/**
	 * 训练权值参数
	 * 
	 * @param maxIteration
	 *            训练最大迭代次数
	 * @param tolerance
	 *            系统误判低于该值时结束训练
	 */
	public void train(int maxIteration, double tolerance) {
		double preError = Double.MAX_VALUE;
		for (int itr = 0; itr < maxIteration; itr++) {
			forward();// 由输入计算隐藏层和输出层的值
			double error = getSE();
			System.out
					.println("iteration " + itr + " square error is " + error);
			if (error < tolerance) {
				System.out
						.println("square error less than tolerance, parameter training finished.");
				break;
			}
			// 动态调整运量项系数
			if (error > 1.04 * preError) {
				ALPHA = 0;
			} else if (error < preError) {
				ALPHA = 0.95;
			}
			// 动态调整学习率
			if (error > preError) {
				ETA *= BETA;
				System.out.println("decrease ETA to " + ETA);
			} else if (error < preError) {
				ETA *= THETA;
				System.out.println("increase ETA to " + ETA);
			}
			// 动态调整陡度因子
			if (Math.abs(preError - error) < FLAT && error > 10 * tolerance) { // 误差变化量很小（进入平坦区）,而误差仍很大
				LAMBDA *= JAMA;
				System.out.println("increase LAMBDA to " + LAMBDA);
			} else if (Math.abs(preError - error) > FLAT) {// 退出平坦区域
				LAMBDA = 1;
				System.out.println("decrease LAMBDA to " + LAMBDA);
			}
			preError = error;
			backward();// 反向调整权值
		}
	}

	/**
	 * 训练权值参数
	 */
	public void train() {
		train(ITERATION, TOLERANCE);
	}

	public double[][] getW() {
		return w;
	}

	public double[][] getV() {
		return v;
	}

	/**
	 * 根据一个样本的输入，计算该样本的输出
	 * 
	 * @param x
	 * @return
	 */
	public double[] getOutput(double[] x) {
		assert x.length == inputSize - 1;
		double[] input = new double[inputSize];
		input[0] = 1;
		for (int i = 1; i < inputSize; i++) {
			input[i] = x[i - 1];
		}
		double[] hidden = new double[hiddenSize];
		hidden[0] = 1;
		double[] output = new double[outputSize];
		for (int j = 1; j < hiddenSize; j++) {
			double net = 0;// 隐藏层的净输入
			for (int k = 0; k < inputSize; k++) {
				net += input[k] * v[k][j];
			}
			hidden[j] = sigmoid(net, 1);// 隐藏层的输出
		}
		for (int j = 0; j < outputSize; j++) {
			double net = 0;// 输出层的净输入
			for (int k = 0; k < hiddenSize; k++) {
				net += hidden[k] * w[k][j];
			}
			output[j] = sigmoid(net, 1);// 输出层的输出
		}
		return output;
	}

}
