package com.orisun.mining.util.filter;

import com.orisun.mining.util.Pair;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class TestHtmlUtil {

	private static String html1 = null;
	private static String html2 = null;
	private static String html3 = null;
	private static String html4 = null;
	private static String html5 = null;
	private static String html6 = null;
	private static String html7 = null;

	@BeforeClass
	public static void setup() throws IOException {
		String basePath = TestHtmlUtil.class.getResource("/").getPath();
		StringBuffer sb = new StringBuffer();
		File infile = new File(basePath + "/data/html1");
		BufferedReader br = new BufferedReader(new FileReader(infile));
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		br.close();
		html1 = sb.toString();
		sb.setLength(0);

		infile = new File(basePath + "/data/html2");
		br = new BufferedReader(new FileReader(infile));
		line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		br.close();
		html2 = sb.toString();
		sb.setLength(0);

		infile = new File(basePath + "/data/html3");
		br = new BufferedReader(new FileReader(infile));
		line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		br.close();
		html3 = sb.toString();
		sb.setLength(0);

		infile = new File(basePath + "/data/html4");
		br = new BufferedReader(new FileReader(infile));
		line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		br.close();
		html4 = sb.toString();
		sb.setLength(0);

		infile = new File(basePath + "/data/html5");
		br = new BufferedReader(new FileReader(infile));
		line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		br.close();
		html5 = sb.toString();
		sb.setLength(0);

		infile = new File(basePath + "/data/html6");
		br = new BufferedReader(new FileReader(infile));
		line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		br.close();
		html6 = sb.toString();
		sb.setLength(0);

		infile = new File(basePath + "/data/html7");
		br = new BufferedReader(new FileReader(infile));
		line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		br.close();
		html7 = sb.toString();
		sb.setLength(0);
	}

	@Test
	public void testHidden1() {
		String hiddenContent = HtmlUtil.getHiddenContent(html1);
		System.out.println(hiddenContent);
	}

	@Test
	public void testHidden7() {
		String hiddenContent = HtmlUtil.getHiddenContent(html7);
		System.out.println(hiddenContent);
		for (int i = 0; i < hiddenContent.length(); i++) {
			char ch = hiddenContent.charAt(i);
			System.out.println(ch + 0);
		}
		System.out.println(hiddenContent.length());
	}

	@Test
	public void testVisible1() {
		String visibleContent = HtmlUtil.rmHidden(html1);
		System.out.println(visibleContent);
	}

	@Test
	public void testPureText1() {
		String visibleContent = HtmlUtil.rmHidden(html1);
		String pureVisibleText = HtmlUtil.rmTags(visibleContent);
		System.out.println(pureVisibleText);
	}

	@Test
	public void testHidden2() {
		String hiddenContent = HtmlUtil.getHiddenContent(html2);
		System.out.println(hiddenContent);
	}

	@Test
	public void testVisible2() {
		String visibleContent = HtmlUtil.rmHidden(html2);
		System.out.println(visibleContent);
	}

	@Test
	public void testPureText2() {
		String visibleContent = HtmlUtil.rmHidden(html2);
		String pureVisibleText = HtmlUtil.rmTags(visibleContent);
		System.out.println(pureVisibleText);
	}

	@Test
	public void testHidden3() {
		String hiddenContent = HtmlUtil.getHiddenContent(html3);
		System.out.println(hiddenContent);
	}

	@Test
	public void testVisible3() {
		String visibleContent = HtmlUtil.rmHidden(html3);
		System.out.println(visibleContent);
	}

	@Test
	public void testPureText3() {
		String visibleContent = HtmlUtil.rmHidden(html3);
		String pureVisibleText = HtmlUtil.rmTags(visibleContent);
		System.out.println(pureVisibleText);
	}

	@Test
	public void testHidden4() {
		String hiddenContent = HtmlUtil.getHiddenContent(html4);
		System.out.println(hiddenContent);
	}

	@Test
	public void testHidden5() {
		String hiddenContent = HtmlUtil.getHiddenContent(html5);
		System.out.println(hiddenContent);
	}

	@Test
	public void testPureText5() {
		String visibleContent = HtmlUtil.rmHidden(html5);
		String pureVisibleText = HtmlUtil.rmTags(visibleContent);
		System.out.println(pureVisibleText);
	}

	@Test
	public void testHidden6() {
		String hiddenContent = HtmlUtil.getHiddenContent(html6);// 全部是8025，即零宽度字符&zwj;
		for (int i = 0; i < hiddenContent.length(); i++) {
			System.out.println((int) hiddenContent.charAt(i));
		}
	}

	@Test
	public void testPureText6() {
		String visibleContent = HtmlUtil.rmHidden(html6);
		String pureVisibleText = HtmlUtil.rmTags(visibleContent);
		System.out.println(pureVisibleText);
	}

	@Test
	public void testPosition1() {
		boolean havePosition = HtmlUtil.havePosition(html1);
		Assert.assertFalse(havePosition);
	}

	@Test
	public void testPosition2() {
		String html2 = "<p style='font-size:24px;position:300'>大人</style>";
		boolean havePosition = HtmlUtil.havePosition(html2);
		Assert.assertTrue(havePosition);
	}

	@Test
	public void testPosition3() {
		String html2 = "<p color='red' position='300'>大人</style>";
		boolean havePosition = HtmlUtil.havePosition(html2);
		Assert.assertTrue(havePosition);
	}

	@Test
	public void testColorText1() {
		String visibleContent = HtmlUtil.rmHidden(html1);
		List<Pair<RGB, String>> colorText = HtmlUtil
				.getColorText(visibleContent);
		for (Pair<RGB, String> ele : colorText) {
			System.out.print(ele.second + "&");
		}
		System.out.println();
	}

	@Test
	public void testColorText2() {
		String visibleContent = HtmlUtil.rmHidden(html2);
		List<Pair<RGB, String>> colorText = HtmlUtil
				.getColorText(visibleContent);
		for (Pair<RGB, String> ele : colorText) {
			System.out.print(ele.second + "&");
		}
		System.out.println();
	}

	@Test
	public void testColorText3() {
		String visibleContent = HtmlUtil.rmHidden(html3);
		List<Pair<RGB, String>> colorText = HtmlUtil
				.getColorText(visibleContent);
		for (Pair<RGB, String> ele : colorText) {
			System.out.print(ele.second + "&");
		}
		System.out.println();
	}

	/**
	 * 性能测试结果：<br>
	 * 252_宠物狗:一共535489条信息,34个信息含隐藏内容.耗时41682毫秒，平均检一条信息需要0.0778毫秒.<br>
	 * 36_二手手机:一共629025条信息,23282个信息含隐藏内容.耗时34353毫秒，平均检一条信息需要0.0546毫秒
	 * 
	 * @throws IOException
	 */
	@Test
	public void batchTestHidden() throws IOException {
		long begin = System.currentTimeMillis();
		int lines = 0;
		int num = 0;
		File infile = new File("D:\\灌水终结者\\文本隐藏第二批\\36_二手手机");
		BufferedReader br = new BufferedReader(new FileReader(infile));
		String line = null;
		while ((line = br.readLine()) != null) {
			lines++;
			String[] arr = line.split("\\t");
			if (arr.length == 3) {
				String content = arr[2];
				String hidden = HtmlUtil.getHiddenContent(content);
				if (hidden != null && hidden.trim().length() > 0) {
					num++;
				}
			}
		}
		br.close();
		long end = System.currentTimeMillis();
		System.out.println("一共" + lines + "条信息," + num + "个信息含隐藏内容.耗时"
				+ (end - begin) + "毫秒，平均检一条信息需要" + 1.0 * (end - begin) / lines
				+ "毫秒");
	}

	/**
	 * 一共629025条信息,rmTags耗时13855毫秒<br>
	 * 一共629025条信息,removeHtml耗时32669毫秒
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void batchTestRmTags() throws IOException {
		long begin = System.currentTimeMillis();
		int lines = 0;
		File infile = new File("D:\\灌水终结者\\文本隐藏第二批\\36_二手手机");
		BufferedReader br = new BufferedReader(new FileReader(infile));
		String line = null;
		while ((line = br.readLine()) != null) {
			lines++;
			String[] arr = line.split("\\t");
			if (arr.length == 3) {
				String content = arr[2];
				HtmlUtil.rmTags(content);
			}
		}
		br.close();
		long end = System.currentTimeMillis();
		System.out
				.println("一共" + lines + "条信息,rmTags耗时" + (end - begin) + "毫秒");

		begin = System.currentTimeMillis();
		br = new BufferedReader(new FileReader(infile));
		lines = 0;
		while ((line = br.readLine()) != null) {
			lines++;
			String[] arr = line.split("\\t");
			if (arr.length == 3) {
				String content = arr[2];
				HtmlUtil.removeHtml(content);
			}
		}
		br.close();
		end = System.currentTimeMillis();
		System.out.println("一共" + lines + "条信息,removeHtml耗时" + (end - begin)
				+ "毫秒");
	}
}
