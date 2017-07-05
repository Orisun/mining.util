package com.orisun.mining.util.dvm;

import com.orisun.mining.util.WordSegHandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 文档向量模型
 * 
 * @Author:orisun
 * @Since:2015-6-30
 * @Version:1.0
 */
public class DVM {

	private static int DIMENSION = 2000;// 文档向量的长度。如果特征词文件中存放的特征词小于这个数，则DIMENSION会被修改
	private static Map<String, Integer> dfMap = new HashMap<String, Integer>();// 特征词的文档频率
	private static Map<String, Integer> termIndex = new HashMap<String, Integer>();// 特征词的索引编号
	private static int totalDocCount = 0;

	/**
	 * 给DIMENSION和dfMap赋值
	 * 
	 * @param featureFile
	 *            存放特征词的文件，一行一个特征词，已按重要程序降序排列好
	 * @param dfFile
	 *            文件中存放了每个词的文档频率，一行一个词
	 * @param docCount
	 *            总的文档数
	 * @throws IOException
	 */
	public static void init(String featureFile, String dfFile, int docCount)
			throws IOException {
		assert docCount > 0;
		totalDocCount = docCount;
		BufferedReader br = new BufferedReader(new FileReader(featureFile));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] arr = line.split("\\s+");
			if (arr.length == 2) {
				dfMap.put(arr[0], 0);
			}
			if (dfMap.size() >= DIMENSION) {
				break;
			}
		}
		br.close();
		DIMENSION = dfMap.size();
		br = new BufferedReader(new FileReader(dfFile));
		while ((line = br.readLine()) != null) {
			String[] arr = line.split("\\s+");
			if (arr.length == 2) {
				if (dfMap.containsKey(arr[0])) {
					dfMap.put(arr[0], Integer.parseInt(arr[1]));
				}
			}
		}
		br.close();
		int index = 0;
		for (Entry<String, Integer> entry : dfMap.entrySet()) {
			termIndex.put(entry.getKey(), index++);
		}
	}

	/**
	 * 把文档转换为向量
	 * 
	 * @param doc
	 *            把文档的全部原始内容放在一个String里即可
	 * @return
	 * @throws IOException
	 */
	public static double[] doc2vec(String doc) throws IOException {
		double[] rect = new double[DIMENSION];
		double[] weight = new double[DIMENSION];
		Map<String, Integer> termCount = new HashMap<String, Integer>();
		List<String> list = WordSegHandler.wordSeg(doc);
		double squareSum = 0.0;
		for (String word : list) {
			// 属于特征词
			if (dfMap.containsKey(word)) {
				Integer cnt = termCount.get(word);
				if (cnt == null) {
					cnt = new Integer(0);
				}
				cnt += 1;
				termCount.put(word, cnt);
			}
		}
		for (Entry<String, Integer> entry : termCount.entrySet()) {
			String term = entry.getKey();
			int tf = entry.getValue();
			double idf = 1.0 * totalDocCount / dfMap.get(term);
			int index = termIndex.get(term);
			weight[index] = tf * Math.log(idf) / Math.log(2);
//			weight[index] = tf * idf;
			squareSum += Math.pow(weight[index], 2);
		}
		squareSum = Math.sqrt(squareSum);
		for (Entry<String, Integer> entry : termCount.entrySet()) {
			String term = entry.getKey();
			int index = termIndex.get(term);
			rect[index] = weight[index] / squareSum;
		}
		return rect;
	}
}
