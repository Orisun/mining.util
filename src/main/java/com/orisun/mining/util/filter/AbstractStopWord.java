package com.orisun.mining.util.filter;

import com.orisun.mining.util.FileUtil;
import com.orisun.mining.util.WordSegHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class AbstractStopWord {

	private static Log logger = LogFactory.getLog(AbstractStopWord.class);
	private Set<String> stopWords = new CopyOnWriteArraySet<String>();

	/**
	 * 读取停用词表，同时把停用词加到公词词典中
	 * 
	 * @param file
	 */
	public void loadStopWords(String file) {
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
		stopWords = stopWord_tmp;
		logger.info("load " + stopWords.size() + " stop words from file " + file);
	}

	/**
	 * 判断一个词是否为停用词
	 * 
	 * @param word
	 * @return
	 */
	public boolean isStopWord(String word) {
		return stopWords.contains(word);
	}
}
