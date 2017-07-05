package com.orisun.mining.util.classification;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NaiveBayes {

	static Set<String> features = new HashSet<String>(); // 特征词
	static int classnum; // 类别数
	static int[] number_of_class; // 每个类别有多少篇文档
	static double[] classProb; // 每个类别所占的比例
	static Map<String, int[]> word_cat_count; // 每个特征词在各个类别的多少篇文档中出现
	static Map<String, double[]> word_cat_prob; // 由类别推出特征词的后验概率

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

		// 计算每个类别的概率
		int sum_doc = 0;
		int length = number_of_class.length;
		classProb = new double[length];
		for (int i = 0; i < length; i++) {
			sum_doc += number_of_class[i];
		}
		for (int i = 0; i < number_of_class.length; i++) {
			classProb[i] = 1.0 * number_of_class[i] / sum_doc;
		}
	}

	public static void getPosteriorProbability() {
		word_cat_prob = new HashMap<String, double[]>();
		for (String feature : features) {
			if (word_cat_count.containsKey(feature)) {
				double[] postprob = new double[classnum];
				int[] arr = word_cat_count.get(feature);
				// 求出后验概率
				for (int i = 0; i < postprob.length; i++) {
					postprob[i] = 1.0 * arr[i] / number_of_class[i];
				}
				word_cat_prob.put(feature, postprob);
			}
		}

		word_cat_count = null; // 释放内存
	}

	public static void trainModel(String featurefile, String corpus)
			throws Exception {
		initFeature(featurefile);
		initWordCatCount(corpus);
		getPosteriorProbability();
	}

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
					prob[i] *= word_cat_prob.get(word)[i]; // 该类别下多个特征词同时出现的联合概率
				}
			}
			if (prob[i] == 1.0) {
				prob[i] = 0.0;
			}
			prob[i] *= classProb[i];
		}
		// 取概率最大者
		double maxprob = 0.0;
		for (int i = 0; i < prob.length; i++) {
			if (prob[i] > maxprob) {
				maxprob = prob[i];
				cat = i;
			}
		}
		return cat;
	}

	public static void main(String[] args) throws Exception {
		String featurefile = "CHI收集的敏感词";
		String corpus = "E:\\MinGW\\msys\\1.0\\home\\Administrator\\tc\\wc";
		NaiveBayes.trainModel(featurefile, corpus);

		File testdir = new File(
				"E:\\MinGW\\msys\\1.0\\home\\Administrator\\tc\\train");

		File outFile = new File(
				"E:\\MinGW\\msys\\1.0\\home\\Administrator\\tc\\test_result");
		BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));

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
					String[] words = sentence.split("\\s+");
					total++;
					int cat = NaiveBayes.classify(words);
					bw.write(cat + "\t" + sentence);
					bw.newLine();
					// System.out.println(cat);
					if (cat != i) {
						err++;
					}
				}
			}

			br.close();
			System.out.println("第" + i + "类正确率：" + (1 - 1.0 * err / total));
		}
		bw.flush();
		bw.close();
	}
}
