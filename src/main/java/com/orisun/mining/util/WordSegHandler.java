package com.orisun.mining.util;

import org.ansj.domain.Term;
import org.ansj.library.UserDefineLibrary;
import org.ansj.recognition.NatureRecognition;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class WordSegHandler {

	/**
	 * 不带词性标注的分词
	 */
	public static synchronized List<String> wordSeg(String text) throws IOException {
		List<String> rect = new ArrayList<String>();
		ToAnalysis udf = new ToAnalysis(new StringReader(text));
		Term term = null;
		while ((term = udf.next()) != null) {
			rect.add(term.getName());
		}
		return rect;
	}

	/**
	 * 带词性标注的分词
	 */
	public static synchronized List<SegPair> wordSegWithPos(String text) throws IOException {
		List<SegPair> rect = new ArrayList<SegPair>();
		List<Term> list = new ArrayList<Term>();
		// 分词
		ToAnalysis udf = new ToAnalysis(new StringReader(text));
		Term term = null;
		while ((term = udf.next()) != null) {
			list.add(term);
		}
		// 词性标注
		new NatureRecognition(list).recognition();
		for (Term ele : list) {
			rect.add(new SegPair(ele.getName(), ele.getNatrue().natureStr));
		}
		return rect;
	}

	/**
	 * 加载用户词典
	 * 
	 * @param path
	 *            必须是文件，不能是目录
	 * @throws IOException
	 */
	public static synchronized void importUserDict(String path) throws IOException {
		File file = new File(path);
		if (!file.isFile()) {
			throw new IOException("no such file:" + path);
		}
		UserDefineLibrary.loadFile(UserDefineLibrary.FOREST, file);
	}

	/**
	 * 增加一个用户自定义的词
	 * 
	 * @param term
	 * @param nature
	 * @param freq
	 */
	public static void addUserTerm(String term, String nature, int freq) {
		UserDefineLibrary.insertWord(term, nature, freq);
	}

	/**
	 * 增加一个用户自定义的词
	 * 
	 * @param term
	 */
	public static void addUserTerm(String term) {
		UserDefineLibrary.insertWord(term, "n", 1000);
	}

	/**
	 * 删除一个用户自定义的词
	 * 
	 * @param term
	 */
	public static void rmUserTerm(String term) {
		UserDefineLibrary.removeWord(term);
	}

	/**
	 * 清空用户词典
	 */
	public static void clear() {
		UserDefineLibrary.clear();
	}

	public static class SegPair {
		/**
		 * 词
		 */
		String word;
		/**
		 * 词性
		 */
		String pos;

		public SegPair(String word, String pos) {
			this.word = word;
			this.pos = pos;
		}

		public String getWord() {
			return word;
		}

		public String getPos() {
			return pos;
		}

	}

	/**
	 * 按标点符号把一个长句子分割成诸多短句子
	 * 
	 * @param sentence
	 * @return
	 * @throws IOException
	 */
	public static List<String> splitByPunctuation(String sentence) throws IOException {
		List<String> sents = null;
		List<SegPair> pairs = wordSegWithPos(sentence);
		if (pairs != null) {
			sents = new ArrayList<String>();
			StringBuilder sb = new StringBuilder();
			for (SegPair pair : pairs) {
				String pos = pair.getPos();
				if ("null".equals(pos) || pos.startsWith("w")) {
					if (sb.length() > 0) {
						sents.add(sb.toString());
						sb = new StringBuilder();
					}
				} else {
					sb.append(pair.getWord());
				}
			}
			if (sb.length() > 0) {
				sents.add(sb.toString());
			}
		}
		return sents;
	}

	public static void main(String[] args) throws IOException {
		String path = Path.getCurrentPath() + "/data/";
		WordSegHandler.importUserDict(path + "it.dic");
		WordSegHandler.addUserTerm("c++", "n", 1000);
		String str = "健翔桥原画师美团C++、.net，php,cocos2d-x";
		List<SegPair> list = WordSegHandler.wordSegWithPos(str);
		for (SegPair ele : list) {
			System.out.println(ele.word + "/" + ele.pos);
		}
	}
}
