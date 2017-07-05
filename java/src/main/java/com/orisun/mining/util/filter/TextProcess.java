package com.orisun.mining.util.filter;

import org.apache.commons.lang.StringUtils;
import org.jsoup.helper.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文本预处理工具类
 * 
 * @Author:zhangchaoyang
 * @Since:2014-7-9
 * @Version:
 */
public class TextProcess {

	// XML标准规定的无效字节
	private final static Pattern INVALID_XML_PATTERN = Pattern.compile("[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]");
	private final static Pattern BACKET_PATTEN = Pattern.compile("([\\(（\\[【].*?[\\)）\\]】])");
	private static Map<Character, Integer> zhPunc = new HashMap<Character, Integer>();

	static {
		zhPunc.put('·', 183);
		zhPunc.put('×', 215);
		zhPunc.put('—', 8212);
		zhPunc.put('‘', 8216);
		zhPunc.put('’', 8217);
		zhPunc.put('“', 8220);
		zhPunc.put('”', 8221);
		zhPunc.put('…', 8230);
		zhPunc.put('、', 12289);
		zhPunc.put('。', 12290);
		zhPunc.put('《', 12298);
		zhPunc.put('》', 12299);
		zhPunc.put('『', 12302);
		zhPunc.put('』', 12303);
		zhPunc.put('【', 12304);
		zhPunc.put('】', 12304);
		zhPunc.put('！', 65281);
		zhPunc.put('（', 65288);
		zhPunc.put('）', 65289);
		zhPunc.put('，', 65292);
		zhPunc.put('：', 65306);
		zhPunc.put('；', 65307);
		zhPunc.put('？', 65311);
		zhPunc.put('￥', 65509);
	}

	/**
	 * 去除括号中的内容，如果全部内容都在括号内部则仅去除括号
	 * 
	 * @param content
	 * @return
	 */
	public static String rmBacket(String content) {
		String rect = content;
		Matcher matcher = BACKET_PATTEN.matcher(content);
		if (matcher.find()) {
			if (matcher.start(0) == 0 && matcher.end(0) == content.length()) {
				rect = content.substring(1, content.length() - 1);
			} else {
				rect = content.substring(0, matcher.start(0)) + content.substring(matcher.end(0));
			}
		}
		if (rect.equals(content)) {
			return rect;
		} else {
			return rmBacket(rect);
		}
	}

	/**
	 * 去除非法的xml字符
	 * 
	 * @param content
	 */
	public static String rmInvalidXmlChar(String content) {
		if (StringUtils.isBlank(content)) {
			return StringUtils.EMPTY;
		}
		return INVALID_XML_PATTERN.matcher(content).replaceAll("");
	}

	/**
	 * 全角转半角: 全角空格为12288，半角空格为32 ，其他字符半角(33-126)与全角 （65281-65374）的对应关系：均相差65248
	 * 
	 * @param content
	 * @return
	 */
	private static String toDBC(String content) {
		char[] charArray = content.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			if (charArray[i] == 12288) {
				charArray[i] = (char) 32;
				continue;
			}
			if (charArray[i] > 65280 && charArray[i] < 65375) {
				charArray[i] = (char) (charArray[i] - 65248);
			}
		}
		return String.valueOf(charArray);
	}

	/**
	 * 字符是否为半角阿拉伯数字
	 * 
	 * @param ch
	 * @return
	 */
	public static boolean isNumeric(char ch) {
		if (ch < 48 || ch > 57) {
			return false;
		}
		return true;
	}

	/**
	 * 字符是否为半角大小写英文字母
	 * 
	 * @param ch
	 * @return
	 */
	public static boolean isEnglish(char ch) {
		if (!((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z'))) {
			return false;
		}
		return true;
	}

	/**
	 * 判断是否英文标点
	 * 
	 * @param ch
	 * @return
	 */
	public static boolean isEnPunc(char ch) {
		if (ch < 128 && !isEnglish(ch) && !isNumeric(ch)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否为汉字
	 * 
	 * @param ch
	 * @return
	 */
	public static boolean isHanZi(char ch) {
		return (ch >= 19968 && ch <= 40869);

	}

	/**
	 * 判断是否为汉语标点
	 * 
	 * @param ch
	 * @return
	 */
	public static boolean isZhPunc(char ch) {
		return zhPunc.containsKey(ch);
	}

	/**
	 * 去除文本中的零宽度字符
	 * 
	 * @param text
	 * @return
	 */
	private static String rmZeroWidth(String text) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if (ch + 0 != 8204 && ch + 0 != 8205) {// 8204和8205是0宽度字符
				result.append(ch);
			}
		}
		return result.toString();
	}

	/**
	 * 字符串预处理
	 * 
	 * @param str
	 * @param processMode
	 * @return
	 */
	public static String preProcess(String str, Set<WordProcess> processMode) {
		if (StringUtil.isBlank(str)) {
			return "";
		}
		String text = str;
		// 无条件优先执行全角转半角
		text = toDBC(text);
		if (processMode.contains(WordProcess.SIMPLIFIED)) {
			text = TransferFont.conver(text, 1);
		}
		if (processMode.contains(WordProcess.LOWER)) {
			text = text.toLowerCase();
		}
		if (processMode.contains(WordProcess.RMPUNC)) {
			text = rmPunc(text);
		}
		if (processMode.contains(WordProcess.RMZEROWIDTH)) {
			text = rmZeroWidth(text);
		}
		return text;
	}

	/**
	 * 去除所有符号(两个英文字符中间的空格保留),但保留指定符号
	 * 
	 * @param text
	 * @return
	 */
	public static String rmPunc(String text, Set<Character> exclude) {
		if (StringUtil.isBlank(text)) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		String[] arr = text.trim().split("\\s+");
		for (int i = 0; i < arr.length; i++) {
			String str = arr[i];
			if (i > 0 && (isNumeric(str.charAt(0)) || isEnglish(str.charAt(0)) || isEnPunc(str.charAt(0)))
					&& (isNumeric(arr[i - 1].charAt(0)) || isEnglish(arr[i - 1].charAt(0))
							|| isEnPunc(arr[i - 1].charAt(0)))) {
				result.append(" ");
			}
			boolean head = true;
			for (int j = 0; j < str.length(); j++) {
				if (exclude != null && exclude.contains(str.charAt(j))
						&& !(head && !(str.length() >= 4 && str.startsWith(".net")))) {
					result.append(str.charAt(j));
				} else if (!isZhPunc(str.charAt(j)) && !isEnPunc(str.charAt(j))) {
					result.append(str.charAt(j));
					head = false;
				}
			}
		}
		return result.toString().trim();
	}

	/**
	 * 去除所有符号(两个英文字符、数字、英文标点中间的空格保留)
	 * 
	 * @param text
	 * @return
	 */
	public static String rmPunc(String text) {
		return rmPunc(text, null);
	}

	/**
	 * 判断一个字符串是不是由纯标点构成的
	 * 
	 * @param text
	 * @return
	 */
	public static boolean isPunc(String text) {
		if (text == null || text.length() == 0) {
			return false;
		}
		boolean rect = true;
		for (int i = 0; i < text.length(); i++) {
			if (!isZhPunc(text.charAt(i)) && !isEnPunc(text.charAt(i))) {
				rect = false;
				break;
			}
		}
		return rect;
	}

	public enum WordProcess {
		DBC, // 转半角
		LOWER, // 转小写
		SIMPLIFIED, // 转简体中文
		RMPUNC, // 去除所有标点符号
		RMZEROWIDTH;// 去除零宽度字符
	}

}
