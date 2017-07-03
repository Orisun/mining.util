package com.orisun.mining.util.hmm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

/**
 * 隐马尔可夫模型参数学习。
 *
 * @Author:zhangchaoyang
 * @Since:2015年4月4日
 * @Version:1.0
 */
public class BaumWelch {

	private int stateCount;// 状态的个数
	private Map<String, Integer> observeIndexMap = new HashMap<String, Integer>();// 观察值及其索引编号
	/**
	 * 通过学习得到的模型参数
	 */
	private double[] stateProb;// 初始状态概率矩阵
	private double[][] stateTrans;// 状态转移矩阵
	private double[][] emission;// 混淆矩阵

	private List<String> observeSeqs = new LinkedList<String>();// 训练集中所有的观察序列

	/**
	 * 迭代终止条件
	 */
	private final int ITERATION_MAX = 100;
	private final double DELTA_PI = 1E-3;
	private final double DELTA_A = 1E-2;
	private final double DELTA_B = 1E-2;

	/**
	 * 
	 * @param stateCount
	 *            指定状态取值有多少种
	 * @param observeFile
	 *            存储观察序列的文件，各个观察序列用空白符或换行符隔开即可
	 * @throws IOException
	 */
	public void initParam(int stateCount, String observeFile) throws IOException {
		this.stateCount = stateCount;
		int observeCount = 0;
		BufferedReader br = new BufferedReader(new FileReader(new File(observeFile)));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] arr = line.split("\\s+");
			for (String seq : arr) {
				if (seq.length() > 1) {// 长度为1的观察序列必须过滤掉，不然在更新stateTrans时会出现NaN的情况
					observeSeqs.add(seq);
					for (int i = 0; i < seq.length(); i++) {
						String observe = seq.substring(i, i + 1);
						if (!observeIndexMap.containsKey(observe)) {
							observeIndexMap.put(observe, observeCount++);
						}
					}
				}
			}
		}
		br.close();

		stateProb = new double[stateCount];
		initWeightRandomly(stateProb, 1E5);
		// initWeightEqually(stateProb);
		stateTrans = new double[stateCount][];
		for (int i = 0; i < stateCount; i++) {
			stateTrans[i] = new double[stateCount];
			initWeightRandomly(stateTrans[i], 1E5);
			// initWeightEqually(stateTrans[i]);
		}
		emission = new double[stateCount][];
		for (int i = 0; i < stateCount; i++) {
			emission[i] = new double[observeCount];
			initWeightRandomly(emission[i], 1E9);
			// initWeightEqually(emission[i]);
		}
	}

	/**
	 * 随机地初始化权重，使得各权重非负，且和为1.
	 * 
	 * @param arr
	 * @param precision
	 */
	public void initWeightRandomly(double[] arr, double precision) {
		int len = arr.length - 1;
		int[] position = new int[len];
		for (int i = 0; i < len; i++) {
			position[i] = (int) (Math.random() * precision);
		}
		Arrays.sort(position);
		int pre = 0;
		for (int i = 0; i < len; i++) {
			arr[i] = 1.0 * (position[i] - pre) / precision;
			pre = position[i];
		}
		arr[len] = 1.0 * (precision - pre) / precision;
	}

	/**
	 * 均等地初始化权重，使得各权重非负，且和为1.
	 * 
	 * @param arr
	 */
	public void initWeightEqually(double[] arr) {
		int len = arr.length;
		for (int i = 0; i < len; i++) {
			arr[i] = 1.0 / len;
		}
	}

	/**
	 * BaumWelch算法学习HMM的模型参数
	 */
	public void baumWelch() {
		long begin = System.currentTimeMillis();
		int iter = 0;
		while (iter++ < ITERATION_MAX) {
			double[] stateProb_new = new double[stateCount];
			double[][] stateTrans_new = new double[stateCount][];
			double[][] emission_new = new double[stateCount][];
			for (int i = 0; i < stateCount; i++) {
				stateTrans_new[i] = new double[stateCount];
			}
			for (int i = 0; i < stateCount; i++) {
				emission_new[i] = new double[observeIndexMap.size()];
			}
			for (String seq : observeSeqs) {
				int T = seq.length();
				double[][] alpha = new double[T][];
				double[][] beta = new double[T][];
				double[][] gamma = new double[T][];
				for (int i = 0; i < T; i++) {
					alpha[i] = new double[stateCount];
					beta[i] = new double[stateCount];
					gamma[i] = new double[stateCount];
				}
				double[][][] xi = new double[T - 1][][];
				for (int i = 0; i < T - 1; i++) {
					xi[i] = new double[stateCount][];
					for (int j = 0; j < stateCount; j++) {
						xi[i][j] = new double[stateCount];
					}
				}
				String observe = seq.substring(0, 1);
				int observeIndex = observeIndexMap.get(observe);
				// 计算alpha
				for (int i = 0; i < stateCount; i++) {
					alpha[0][i] = stateProb[i] * emission[i][observeIndex];
				}
				for (int t = 1; t < T; t++) {
					observe = seq.substring(t, t + 1);// 当前时刻的观察值
					observeIndex = observeIndexMap.get(observe);
					for (int j = 0; j < stateCount; j++) {
						double sum = 0;
						for (int i = 0; i < stateCount; i++) {
							sum += alpha[t - 1][i] * stateTrans[i][j];
						}
						alpha[t][j] = sum * emission[j][observeIndex];
					}
				}
				// 计算beta
				for (int i = 0; i < stateCount; i++) {
					beta[T - 1][i] = 1;
				}
				for (int t = T - 2; t >= 0; t--) {
					observe = seq.substring(t + 1, t + 2);// 当前时刻的后一个观察值
					observeIndex = observeIndexMap.get(observe);
					for (int i = 0; i < stateCount; i++) {
						double sum = 0;
						for (int j = 0; j < stateCount; j++) {
							sum += beta[t + 1][j] * stateTrans[i][j] * emission[j][observeIndex];
						}
						beta[t][i] = sum;
					}
				}
				double[] denominator = new double[T];// 在计算gamma和xi时都要计算的一个变量
				// 计算gamma
				for (int t = 0; t < T; t++) {
					double sum = 0;
					for (int j = 0; j < stateCount; j++) {
						sum += alpha[t][j] * beta[t][j];
					}
					denominator[t] = sum;
					for (int i = 0; i < stateCount; i++) {
						gamma[t][i] = alpha[t][i] * beta[t][i] / sum;
					}
				}
				// 计算xi
				for (int t = 0; t < T - 1; t++) {
					observe = seq.substring(t + 1, t + 2);// 当前时刻的后一个观察值
					observeIndex = observeIndexMap.get(observe);
					for (int i = 0; i < stateCount; i++) {
						for (int j = 0; j < stateCount; j++) {
							xi[t][i][j] = alpha[t][i] * stateTrans[i][j] * beta[t + 1][j] * emission[j][observeIndex]
									/ denominator[t];
						}
					}
				}
				// 计算stateProb
				double[] curr_stateProb = new double[stateCount];
				for (int i = 0; i < stateCount; i++) {
					curr_stateProb[i] = gamma[0][i];
					stateProb_new[i] += curr_stateProb[i];
				}
				// 计算stateTrans
				double[][] curr_stateTrans = new double[stateCount][];
				for (int i = 0; i < stateCount; i++) {
					curr_stateTrans[i] = new double[stateCount];
					for (int j = 0; j < stateCount; j++) {
						double up = 0;
						double down = 0;
						for (int t = 0; t < T - 1; t++) {
							up += xi[t][i][j];
							down += gamma[t][i];
						}
						if (down > 0) {
							curr_stateTrans[i][j] = up / down;// 任何浮点操作，只要它的一个或多个操作数为NaN，其结果就是NaN。NaN不等于任何浮点数，包括它自身在内。
							stateTrans_new[i][j] += curr_stateTrans[i][j];
						} else {
							stateTrans_new[i][j] += stateTrans[i][j];// 如果分母为0无法相除，就找个值来替代
							System.err.println("up=" + up + ",down=" + down);
						}
					}
				}
				// 计算emission
				double[][] curr_emission = new double[stateCount][];
				for (int i = 0; i < stateCount; i++) {
					curr_emission[i] = new double[observeIndexMap.size()];
					for (Entry<String, Integer> entry : observeIndexMap.entrySet()) {
						String obs = entry.getKey();
						int index = entry.getValue();
						double up = 0;
						double down = 0;
						for (int t = 0; t < T; t++) {
							observe = seq.substring(t, t + 1);
							if (obs.equals(observe)) {
								up += gamma[t][i];
							}
							down += gamma[t][i];
						}
						curr_emission[i][index] = up / down;
						emission_new[i][index] += curr_emission[i][index];
					}
				}
			}
			// 批量更新模型参数
			double delta_pi = 0;
			double delta_a = 0;
			double delta_b = 0;
			int seqCount = observeSeqs.size();
			for (int i = 0; i < stateCount; i++) {
				stateProb_new[i] /= seqCount;
				delta_pi += Math.abs(stateProb_new[i] - stateProb[i]);
			}
			for (int i = 0; i < stateCount; i++) {
				for (int j = 0; j < stateCount; j++) {
					stateTrans_new[i][j] /= seqCount;
					delta_a += Math.abs(stateTrans_new[i][j] - stateTrans[i][j]);
				}
			}
			for (int i = 0; i < stateCount; i++) {
				for (int j = 0; j < observeIndexMap.size(); j++) {
					emission_new[i][j] /= seqCount;
					delta_b += Math.abs(emission_new[i][j] - emission[i][j]);
				}
			}
			System.out.println(
					"iteration " + iter + ", delta_pi=" + delta_pi + ", delta_a=" + delta_a + ", delta_b=" + delta_b);
			if (delta_pi <= DELTA_PI && delta_a <= DELTA_A && delta_b <= DELTA_B) {
				break;
			} else {
				stateProb = stateProb_new;
				stateTrans = stateTrans_new;
				emission = emission_new;
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("time elapse " + (end - begin) / 1000 + " seconds.");
	}

	public int getStateCount() {
		return stateCount;
	}

	public double[] getStateProb() {
		return stateProb;
	}

	public double[][] getStateTrans() {
		return stateTrans;
	}

	public double[][] getEmission() {
		return emission;
	}

	public Map<String, Integer> getObserveIndexMap() {
		return observeIndexMap;
	}

}
