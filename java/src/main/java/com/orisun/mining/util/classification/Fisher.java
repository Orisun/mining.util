package com.orisun.mining.util.classification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Fisher {

	static Set<String> features = new HashSet<String>(); // 特征词
	static int classnum; // 类别数
	static int[] number_of_class; // 每个类别有多少篇文档
	static Map<String, int[]> word_cat_count; // 每个特征词在各个类别的多少篇文档中出现
	static Map<String, double[]> word_cat_prob; // 特征词出现时文档属于每个类别的后验概率

	/**
	 * 从文件中读取特征词
	 * 
	 * @param filename
	 * @throws Exception
	 */
	public static void initFeature(String filename) throws Exception {
		try {
			File infile = new File(filename);
			if (!infile.isFile()) {
				throw new Exception("input string isn't a file.");
			}
			BufferedReader br = new BufferedReader(new FileReader(infile));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] arr = line.split("\\s+");
				for (String word : arr) {
					if (word.length() > 0) {
						features.add(word);
					}
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 计算每个特征词在各个类别的多少篇文档中出现
	 * 
	 * @param dir
	 * @throws Exception
	 */
	public static void initWordCatCount(String dir) throws Exception {
		try {
			File indir = new File(dir);
			if (!indir.isDirectory()) {
				throw new Exception(dir + " not directory.");
			}
			File[] infiles = indir.listFiles();
			classnum = infiles.length;
			number_of_class = new int[classnum];
			word_cat_count = new HashMap<String, int[]>();
			for (int i = 0; i < classnum; i++) {
				if (infiles[i].isFile()) {
					BufferedReader br = new BufferedReader(new FileReader(
							infiles[i]));
					String line = null;
					while ((line = br.readLine()) != null) {
						String[] arr = line.split("\\s+");
						if (arr.length >= 1) {
							number_of_class[i]++; // 该类别的文档计数加1
							Set<String> wordset = new HashSet<String>(); // 提取一行出现过的词
							for (String word : arr) {
								wordset.add(word);
							}
							for (String word : wordset) {
								if (features.contains(word)) { // 如果这个词是特征词
									if (word_cat_count.containsKey(word)) {
										word_cat_count.get(word)[i]++; // 单词在该类别下的计数加1
									} else {
										int[] brr = new int[classnum];
										brr[i] = 1;
										word_cat_count.put(word, brr);
									}
								}
							}
						}
					}
					br.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void getPosteriorProbability(double pre_weight, int pre_count) {
		word_cat_prob = new HashMap<String, double[]>();
		for (String feature : features) {
			if (word_cat_count.containsKey(feature)) {
				double[] postprob = new double[classnum];
				int feature_concurrent = 0; // 特征词在多少篇文档中出现过
				int[] arr = word_cat_count.get(feature);
				for (int ele : arr) {
					feature_concurrent += ele;
				}
				// 求出后验概率
				for (int i = 0; i < postprob.length; i++) {
					postprob[i] = 1.0 * arr[i] / feature_concurrent;
				}
				// 对后验概率进行归一化
				double probsum = 0.0;
				for (int i = 0; i < postprob.length; i++) {
					probsum += postprob[i];
				}
				for (int i = 0; i < postprob.length; i++) {
					postprob[i] /= probsum;

					// 修正由于当前训练语料库中文档数太少而造成的概率计算有偏差。（此项优化可以不要）
					postprob[i] = (pre_count * pre_weight + feature_concurrent
							* postprob[i])
							/ (pre_count + feature_concurrent);
				}
				word_cat_prob.put(feature, postprob);
			}
		}

		word_cat_count = null; // 释放内存
	}

	/**
	 * 训练Fisher模型
	 * 
	 * @param featurefile
	 *            存储特征项的文件
	 * @param corpus
	 *            分词后的训练文本文件
	 * @param pre_weight
	 *            所有后验概率都从这个值开始
	 * @param pre_count
	 *            在遇到训练文本之前已经遇到了这么多文档
	 * @throws Exception
	 */
	public static void trainModel(String featurefile, String corpus,
			double pre_weight, int pre_count) throws Exception {
		initFeature(featurefile);
		initWordCatCount(corpus);
		getPosteriorProbability(pre_weight, pre_count);
	}

	/**
	 * 判断某一个篇文档属于哪个分类
	 * 
	 * @param words
	 * @return
	 */
	public static int classify(String[] words) {
		int cat = 6; // 默认是正常信息
		// 挑出互不相同的词
		Set<String> wordset = new HashSet<String>();
		for (String word : words) {
			wordset.add(word);
		}
		// 计算文档属于各个类别的概率
		double[] prob = new double[classnum];
		for (int i = 0; i < prob.length; i++) {
			prob[i] = 1.0;
			for (String word : wordset) {
				if (word_cat_prob.containsKey(word)) {
					prob[i] *= word_cat_prob.get(word)[i]; // 多个特征词同时出现的联合概率
				}
			}
			if (prob[i] == 1.0) {
				prob[i] = 0.0;
			}

			// prob[i] = -2.0 * Math.log(prob[i]);
			// prob[i] = invchi2(prob[i], wordset.size()*2);
			// //如果概率彼此独立且随机分布，则这一计算结果满足对数卡方分布
			// System.out.print(prob[i] + "\t");
		}
		// System.out.println();
		// 取概率最大者
		double maxprob = 0.0;
		for (int i = 0; i < prob.length; i++) {
			if (prob[i] > maxprob) {
				maxprob = prob[i];
				cat = i;
			}
		}
		// System.out.println(cat+"\t"+maxprob);
		// 概率要达到最小阈值才属于这个类

		switch (cat) {
		case 7:// 涉黄
			if (maxprob < 0.01) {
				cat = 6;
			}
			break;
		case 5: // 网赚
			if (maxprob < 5E-9) {
				cat = 6;
			}
			break;
		case 4: // 手工制作
			if (maxprob < 1E-10) {
				cat = 6;
			}
			break;
		case 3: // 金融违法
			if (maxprob < 1E-6) {
				cat = 6;
			}
			break;
		case 2: // 代考
			if (maxprob < 2E-6) {
				cat = 6;
			}
			break;
		case 1: // 小竞品推广
			if (maxprob < 2E-5) {
				cat = 6;
			}
			break;
		case 0: // 58推广
			if (maxprob < 0.001) {
				cat = 6;
			}
			break;
		default:
			break;
		}

		if (cat != 6) {
			// System.out.println(cat+"\t"+maxprob);
		}

		return cat;
	}

	/**
	 * 利用倒置对数卡方函数求得概率
	 * 
	 * @param chi
	 * @param len
	 * @return
	 */
	// private static double invchi2(double chi, int len) {
	// double m = chi / 2;
	// double sum = Math.exp(-m);
	// double term = sum;
	// for (int i = 0; i <= len / 2; i++) {
	// term *= m / i;
	// sum += term;
	// }
	// // return sum < 1.0 ? sum : 1.0;
	// return sum;
	// }

	/**
	 * @param args
	 * @throws Exception
	 */

	public static void main(String[] args) throws Exception {
		String featurefile = "CHI收集的敏感词";
		String corpus = "E:\\MinGW\\msys\\1.0\\home\\Administrator\\tc\\wc";
		int pre_count = 1;
		double pre_weight = 0.5;
		Fisher.trainModel(featurefile, corpus, pre_weight, pre_count);

		File testdir = new File(
				"E:\\MinGW\\msys\\1.0\\home\\Administrator\\tc\\train");
		File[] testfiles = testdir.listFiles();
		for (int i = 0; i < testfiles.length; i++) {
			File testfile = testfiles[i];
			BufferedReader br = new BufferedReader(new FileReader(testfile));
			String line = null;
			int total = 0;
			int err = 0;
			while ((line = br.readLine()) != null) {
				String sentence = line.trim();
				if (sentence.length() > 0) {
					String []words=sentence.split("\\s+");
					total++;
					int cat = Fisher.classify(words);
					// System.out.println(cat);
					if (cat != i) {
						err++;
					}
				}
			}

			br.close();
			System.out.println("第" + i + "类正确率：" + (1 - 1.0 * err / total));
		}
	}
}
