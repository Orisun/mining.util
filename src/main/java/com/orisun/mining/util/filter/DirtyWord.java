package com.orisun.mining.util.filter;

import com.orisun.mining.util.FileUtil;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

public class DirtyWord {

	private static Set<String> dirtyWord = new CopyOnWriteArraySet<String>();
	private static WuManber wumanber = new WuManber();

	/**
	 * 读取脏字表
	 * 
	 * @param file
	 */
	public static void init(String file) {
		Set<String> dirtyWord_tmp = new CopyOnWriteArraySet<String>();
		WuManber wumanber_tmp = new WuManber();
		List<String> fileCont = new LinkedList<String>();
		FileUtil.readLines(file, fileCont);
		for (String line : fileCont) {
			String word = line.trim();
			if (word.length() > 0) {
				dirtyWord_tmp.add(word);
			}
		}
		dirtyWord = dirtyWord_tmp;
		wumanber_tmp.addFilterKeyWord(dirtyWord, 0);
		wumanber = wumanber_tmp;
	}

	/**
	 * 判断一个词是否为脏字
	 * 
	 * @param word
	 * @return
	 */
	public static boolean isDirty(String word) {
		return dirtyWord.contains(word);
	}

	/**
	 * 判断字符串中是否包含脏词
	 * 
	 * @param str
	 * @return
	 */
	public static boolean containDirtyWord(String str) {
		LinkedHashMap<Integer, String> firstKeywords = wumanber.match(str,
				new Vector<Integer>());
		// 任何一个索引敏感词都没有命中，则返回-1
		if (firstKeywords.size() == 0) {
			return false;
		} else {
			return true;
		}
	}
}
