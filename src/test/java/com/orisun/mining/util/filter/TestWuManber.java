package com.orisun.mining.util.filter;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

/**
 * 
 *
 *@Author:zhangchaoyang 
 *@Since:2014-7-9  
 *@Version:
 */
public class TestWuManber {

	@Test
	public void performTest() throws IOException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			ClassNotFoundException, NoSuchMethodException, SecurityException,
			InstantiationException {
		String wordfilepath = this.getClass().getResource("/").getPath()
				+ "/corpus/keywords.txt";
		File infile = new File(wordfilepath);
		BufferedReader br = new BufferedReader(new FileReader(infile));
		String line = null;
		Set<String> keywords = new HashSet<String>();
		while ((line = br.readLine()) != null) {
			keywords.add(line);
		}
		br.close();

		WuManber inst = new WuManber();
		if (inst.addFilterKeyWord(keywords, 0)) { // 加入要过滤的词
			String textfilepath = this.getClass().getResource("/").getPath()
					+ "/corpus/whitesent.txt";
			infile = new File(textfilepath);
			br = new BufferedReader(new FileReader(infile));
			line = null;
			long begin = System.currentTimeMillis();
			while ((line = br.readLine()) != null) {
				LinkedHashMap<Integer, String> sResult = inst.match(line,
						new Vector<Integer>());
				for (Entry<Integer, String> entry : sResult.entrySet()) {
					System.out
							.println(entry.getKey() + "\t" + entry.getValue());
				}
			}
			br.close();
			long end = System.currentTimeMillis();
			System.out.println("WuManber match Time Elapsed " + (end - begin)
					+ " milliseconds");
		}
	}

	@Test
	public void testMatch() throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, ClassNotFoundException,
			InstantiationException {

		Set<String> vKey = new HashSet<String>();
		vKey.add("法轮");
		vKey.add("法轮功");
		vKey.add("中国");
		vKey.add("58");
		vKey.add("宜信卓越财富投资管理北京有限公司");

		WuManber inst = new WuManber();
		inst.clear();
		if (inst.addFilterKeyWord(vKey, 0)) { // 加入要过滤的词
			String text = "中国的法轮     功58请@运营童鞋针对线宜信卓越财富投资管理北京有限公司上的用户反馈进行跟踪，如遇问题请直接联系测试人员，我们会尽快配合解答。";
			LinkedHashMap<Integer, String> sResult = inst.match(text,
					new Vector<Integer>());
			for (Entry<Integer, String> entry : sResult.entrySet()) {
				System.out.println(entry.getKey() + "\t" + entry.getValue());
			}
			Assert.assertEquals(sResult.get(1), "中国");
			Assert.assertEquals(sResult.get(4), "法轮");
			Assert.assertNull(sResult.get(5));
			Assert.assertEquals(sResult.get(12), "58");
		}

		inst.clear();
	}

	@Test
	public void testMatch_2() throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, ClassNotFoundException,
			InstantiationException {
		Set<String> vKey = new HashSet<String>();
		vKey.add("刷");
		vKey.add("信誉");

		WuManber inst = new WuManber();
		inst.clear();
		if (inst.addFilterKeyWord(vKey, 0)) { // 加入要过滤的词
			String text = "代刷信誉";
			LinkedHashMap<Integer, String> sResult = inst.match(text,
					new Vector<Integer>());
			for (Entry<Integer, String> entry : sResult.entrySet()) {
				System.out.println(entry.getKey() + "\t" + entry.getValue());
			}
			Assert.assertEquals(sResult.get(1), "刷");
			Assert.assertEquals(sResult.get(3), "信誉");
		}

		inst.clear();
	}
}

