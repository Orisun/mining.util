package com.orisun.mining.util.featureselection;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * 
 * @description 卡方法收集特征词
 * @author zhangchaoyang
 * @date 2013-8-3
 */
public class CHI {

	Map<String, Double> word_chi_map = new HashMap<String, Double>();
	int classnum = 0;
	int wordnum = 0;
	int[][] wcmatrix = null;

	/**
	 * 获得全部语料中互不相同的词
	 * 
	 * @param dir
	 * @throws Exception
	 */
	public void initWordSet(String dir) throws Exception {
		try {
			File indir = new File(dir);
			if (!indir.isDirectory()) {
				throw new Exception(dir + " not directory.");
			}
			for (File file : indir.listFiles()) {
				if (file.isFile()) {
					BufferedReader br = new BufferedReader(new FileReader(file));
					String line = null;
					while ((line = br.readLine()) != null) {
						String[] arr = line.split("\\s+");
						if (arr.length == 2) {
							String word = arr[0];
							// 词的长度在[2,6]之间，不能以数字开头，58和360除外
							if (word.length() > 1
									&& word.length() < 7
									&& (!word.matches("[0-9]+.*")
											|| word.startsWith("58") || word
												.startsWith("360"))) {
								word_chi_map.put(word, 0.0);
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
	 * 得到word-class矩阵
	 * 
	 * @param dir
	 * @throws Exception
	 */
	public void initWcMatrix(String dir) throws Exception {
		initWordSet(dir);
		try {
			File indir = new File(dir);
			if (!indir.isDirectory()) {
				throw new Exception(dir + " not directory.");
			}
			classnum = indir.listFiles().length;
			wordnum = word_chi_map.size();
			wcmatrix = new int[classnum + 1][];
			for (int i = 0; i < classnum; i++) {
				wcmatrix[i] = new int[wordnum + 1];
				// 读取当前类别下词的计数
				File file = indir.listFiles()[i];
				if (file.isFile()) {
					BufferedReader br = new BufferedReader(new FileReader(file));
					Map<String, Integer> wcmap = new HashMap<String, Integer>();
					String line = null;
					while ((line = br.readLine()) != null) {
						String[] arr = line.split("\\s+");
						if (arr.length == 2) {
							String word = arr[0];
							int count = Integer.parseInt(arr[1]);
							wcmap.put(word, count);
						}
					}
					br.close();
					// 填充word-class矩阵的第i行
					int index = 0;
					for (Entry<String, Double> entry : word_chi_map.entrySet()) {
						String word = entry.getKey();
						if (wcmap.containsKey(word)) {
							wcmatrix[i][index] = wcmap.get(word);
						} else {
							wcmatrix[i][index] = 0;
						}
						index++;
					}
				}
			}
			// 计算每一行的行和
			for (int i = 0; i < classnum; i++) {
				for (int j = 0; j < wordnum; j++) {
					wcmatrix[i][wordnum] += wcmatrix[i][j];
				}
			}
			wcmatrix[classnum] = new int[wordnum + 1]; // 初始化矩阵的最后一行
			// 计算每一列的列和(同时把全部元素的和也计算了)
			for (int i = 0; i < wordnum + 1; i++) {
				for (int j = 0; j < classnum; j++) {
					wcmatrix[classnum][i] += wcmatrix[j][i];
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 计算每个词的卡方
	 */
	public void getCHI() {
		int i = 0;
		for (Entry<String, Double> entry : word_chi_map.entrySet()) {
			String word = entry.getKey();
			double chi = 0.0;
			for (int j = 0; j < classnum; j++) {
				double row_sum = wcmatrix[j][wordnum];
				double col_sum = wcmatrix[classnum][i];
				double sum = wcmatrix[classnum][wordnum];
				double excep = row_sum * col_sum / sum;
				chi += Math.pow(wcmatrix[j][i] - excep, 2) / excep;
			}
			word_chi_map.put(word, chi);
			i++;
		}
	}

	/**
	 * map按value排序
	 * 
	 * @param map
	 * @param reverse
	 * @return
	 */
	public LinkedHashMap<String, Double> sortByChi(Map<String, Double> map,
			final boolean reverse) {
		List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(
				map.entrySet());
		Collections.sort(list, new Comparator<Entry<String, Double>>() {

			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2) {
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

	public static void main(String[] args) throws Exception {
		CHI inst = new CHI();
		String path = "E:\\MinGW\\msys\\1.0\\home\\Administrator\\tc\\wc";
		inst.initWcMatrix(path);
		inst.getCHI();
		LinkedHashMap<String, Double> sortedWordChi = inst.sortByChi(
				inst.word_chi_map, true);
		int count = 0;
		Iterator<Entry<String, Double>> itr = sortedWordChi.entrySet()
				.iterator();
		// 输出卡方最大的前300个词
		// String basepath=IG.class.getClassLoader().getResource("").getPath();
		// //bin目录
		File outfile = new File("CHI收集的敏感词");
		BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
		while (count++ < 300 && itr.hasNext()) {
			Entry<String, Double> entry = itr.next();
			bw.write(entry.getKey());
			bw.newLine();
		}
		bw.flush();
		bw.close();
	}

}
