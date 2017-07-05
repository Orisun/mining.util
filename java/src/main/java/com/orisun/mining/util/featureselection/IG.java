package com.orisun.mining.util.featureselection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

/**
 * 
 * @description 信息增益法收集特征词
 * @author zhangchaoyang
 * @date 2013-8-3
 */
public class IG {

	Map<String, Double> word_ig_map = new HashMap<String, Double>();
	int classnum = 0; // 类别数
	int wordnum = 0; // 单词数
	int[] number_of_class; // 每个类别有多少篇文档
	Map<String, int[]> word_cat_count; // 每个词在各个类别的多少篇文档中出现

	public Map<String, Double> getTermIG() {
		return word_ig_map;
	}

	/**
	 * 获得全部语料中互不相同的词
	 * 
	 * @param wcfile
	 * @param minCooccur
	 * @throws Exception
	 */
	public void initWordSet(String wcfile, int minCooccur) throws Exception {
		try {
			BufferedReader br = new BufferedReader(new FileReader(wcfile));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] arr = line.split("\\s+");
				if (arr.length == 2) {
					String word = arr[0];
					int count = Integer.parseInt(arr[1]);
					if (count >= minCooccur) {
						word_ig_map.put(word, 0.0);
					}
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		wordnum = word_ig_map.size();
	}

	/**
	 * 获得全部语料中互不相同的词
	 * 
	 * @param wcfile
	 * @throws Exception
	 */
	public void initWordSet(String wcfile) throws Exception {
		initWordSet(wcfile, 0);
	}

	/**
	 * 计算每个词在各个类别的多少篇文档中出现
	 * 
	 * @param dir
	 * @throws Exception
	 */
	public void initWordDocMatrix(String dir, String wcpath, int minCooccur) throws Exception {
		initWordSet(wcpath, minCooccur);
		try {
			File indir = new File(dir);
			if (!indir.isDirectory()) {
				throw new Exception(dir + " not directory.");
			}
			File[] infiles = indir.listFiles();
			classnum = infiles.length;
			if (classnum > 200000) {
				classnum = 200000;
			}
			number_of_class = new int[classnum];
			word_cat_count = new HashMap<String, int[]>();
			for (int i = 0; i < classnum; i++) {
				if (infiles[i].isFile()) {
					// 一个文件是一个类，一行是一篇文档，文档已分好词
					BufferedReader br = new BufferedReader(new FileReader(infiles[i]));
					String line = null;
					while ((line = br.readLine()) != null) {
						String[] arr = line.split("\\s+");
						if (arr.length >= 1) {
							number_of_class[i]++; // 该类别的文档计数加1
							Set<String> wordset = new HashSet<String>(); // 提取一行出现过的词
							for (String word : arr) {
								if (word_ig_map.containsKey(word)) {
									wordset.add(word);
								}
							}
							for (String word : wordset) {
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
					br.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 计算每个词的信息增益值
	 */
	public void getIG() {
		// 计算全部文档的总数
		int doc_total = 0;
		for (int i = 0; i < number_of_class.length; i++) {
			doc_total += number_of_class[i];
		}
		// 计算每个词的信息增益值
		for (Entry<String, Double> entry : word_ig_map.entrySet()) {
			String word = entry.getKey();
			if (word_cat_count.containsKey(word)) {
				int word_concurrent = 0; // 单词在多少篇文档中出现过
				int[] arr = word_cat_count.get(word);
				for (int ele : arr) {
					word_concurrent += ele;
				}
				int word_not_concurrent = doc_total - word_concurrent; // 单词在多少篇文档中没有出现过
				double pw = 1.0 * word_concurrent / doc_total; // 出现该词的概率
				double p_w = 1.0 - pw; // 不出现该词的概率
				double part_1 = 0.0;
				double part_2 = 0.0;
				for (int i = 0; i < arr.length; i++) {
					double prob1 = 1.0 * arr[i] / word_concurrent + Double.MIN_NORMAL; // 加上一个很小的数，防止原prob1为0，导致求log时产生负的无限大
					part_1 += prob1 * Math.log(prob1);
					double prob2 = 1.0 * (number_of_class[i] - arr[i]) / word_not_concurrent + Double.MIN_NORMAL;
					part_2 += prob2 * Math.log(prob2);
				}
				double ig = pw * part_1 + p_w * part_2;
				word_ig_map.put(word, ig);
			}
		}
	}

	/**
	 * map按value排序
	 */
	public LinkedHashMap<String, Double> sortByIg(Map<String, Double> map, final boolean reverse) {
		List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(map.entrySet());
		Collections.sort(list, new Comparator<Entry<String, Double>>() {

			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
				if (reverse) { // 降序排列
					if (o2.getValue() - o1.getValue() > 0) {
						return 1;
					} else if (o2.getValue() - o1.getValue() < 0) {
						return -1;
					} else {
						return 0;
					}
				} else {
					if (o1.getValue() - o2.getValue() > 0) {
						return 1;
					} else if (o1.getValue() - o2.getValue() < 0) {
						return -1;
					} else {
						return 0;
					}
				}
			}

		});

		LinkedHashMap<String, Double> result = new LinkedHashMap<String, Double>();
		for (Iterator<Entry<String, Double>> it = list.iterator(); it.hasNext();) {
			Entry<String, Double> entry = it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

}
