package com.orisun.mining.util.ner;

import com.orisun.mining.util.filter.TextProcess;
import com.orisun.mining.util.filter.TextProcess.WordProcess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 判断一个字条串是否是人名
 * 
 * @Author:zhangchaoyang
 * @Since:2015-1-19
 * @Version:1.0
 */
public class NameRecognition {
	private static Map<String, Double> firstRatioMap = new ConcurrentHashMap<String, Double>();
	private static Map<String, Double> lastRatioMap = new ConcurrentHashMap<String, Double>();

	public static final double THRESHOLD2 = 7.0;// 姓名有两个字的阈值
	public static final double THRESHOLD3 = 11.0;// 姓名有三个字的阈值
	private static final double MIN_FIRST_RATIO = 1E-4;
	private static final double MIN_LAST_RATIO = 1E-5;

	public static void init(String firstNameFile, String lastNameFile)
			throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(firstNameFile));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] arr = line.split("\t");
			if (arr.length == 3) {
				firstRatioMap.put(arr[0], Double.parseDouble(arr[2]));
			}
		}
		br.close();
		br = new BufferedReader(new FileReader(lastNameFile));
		while ((line = br.readLine()) != null) {
			String[] arr = line.split("\t");
			if (arr.length == 3) {
				lastRatioMap.put(arr[0], Double.parseDouble(arr[2]));
			}
		}
		br.close();
	}

	/**
	 * 获取一个字条串不是人名的概率
	 * 
	 * @param name
	 * @return
	 */
	public static double getAbnormalNameProb(String name) {
		if (name.length() >= 2) {
			name = TextProcess.preProcess(name,
					EnumSet.of(WordProcess.SIMPLIFIED));
			double result = unionProb(name, firstRatioMap, lastRatioMap);
			return result;
		} else {
			return THRESHOLD3 + 1;
		}
	}

	public static boolean isAbnormalName(String name) {
		if (name.length() == 3) {
			return getAbnormalNameProb(name) > THRESHOLD3;
		} else {
			return getAbnormalNameProb(name) > THRESHOLD2;
		}
	}

	private static double unionProb(String line,
			Map<String, Double> firstRatioMap, Map<String, Double> lastRatioMap) {
		double result = 0;
		String word = line.substring(0, 1);
		Double ratio = firstRatioMap.get(word);
		if (ratio == null) {
			ratio = new Double(MIN_FIRST_RATIO);
		}
		result += (0 - Math.log10(ratio));
		for (int i = 1; i < line.length(); i++) {
			word = line.substring(i, i + 1);
			ratio = lastRatioMap.get(word);
			if (ratio == null) {
				ratio = new Double(MIN_LAST_RATIO);
			}
			result += (0 - Math.log10(ratio));
		}
		return result;
	}
}
