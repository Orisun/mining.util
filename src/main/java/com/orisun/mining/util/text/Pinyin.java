package com.orisun.mining.util.text;

import com.orisun.mining.util.FileUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 汉字转拼音
 * 
 * @Author:orisun
 * @Since:2015-6-5
 * @Version:1.0
 */
public class Pinyin {

	private static Map<Character, String> word2pinyin = new ConcurrentHashMap<Character, String>();

	/**
	 * 读取汉字拼音对照表
	 * 
	 * @param file
	 */
	public static void init(String file) {
		List<String> fileCont = new LinkedList<String>();
		FileUtil.readLines(file, fileCont);
		for (String line : fileCont) {
			String[] arr = line.split("\\s+");
			if (arr.length == 2) {
				Character key = arr[0].trim().charAt(0);
				String value = arr[1].trim();
				word2pinyin.put(key, value);
			}
		}
	}

	/**
	 * 获取一个汉字的拼音（不带音调）
	 * 
	 * @param word
	 * @return
	 */
	public static String getPinyin(Character word) {
		return word2pinyin.get(word);
	}
}
