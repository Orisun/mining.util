package com.orisun.mining.util.filter;

import com.orisun.mining.util.FileUtil;
import com.orisun.mining.util.WordSegHandler;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class StopWord extends AbstractStopWord{
	@Deprecated
	private static Set<String> stopWord = new CopyOnWriteArraySet<String>();

	/**
	 * 读取停用词表，同时把停用词加到公词词典中
	 * 
	 * @param file
	 */
	@Deprecated
	public static void init(String file) {
		Set<String> stopWord_tmp = new CopyOnWriteArraySet<String>();
		List<String> fileCont = new LinkedList<String>();
		FileUtil.readLines(file, fileCont);
		for (String line : fileCont) {
			String word = line.trim();
			if (word.length() > 0) {
				WordSegHandler.addUserTerm(word);
				stopWord_tmp.add(word);
			}
		}
		stopWord = stopWord_tmp;
	}

	/**
	 * 判断一个词是否为停用词
	 * 
	 * @param word
	 * @return
	 */
	@Deprecated
	public static boolean isStop(String word) {
		return stopWord.contains(word);
	}

	private static volatile StopWord instance = null;

	private StopWord() {
	}

	public static StopWord getInstance() {
		if (instance == null) {
			synchronized (StopWord.class) {
				if (instance == null) {
					instance = new StopWord();
				}
			}
		}
		return instance;
	}
	
}
