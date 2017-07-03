package com.orisun.mining.util.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 对请求参数进行安全检查
 * 
 * @author orisun
 * @date 2016年12月21日
 */
public class SafeCheck {

	/** 合法的jsonp的callback参数必然满足这个正则 **/
	private static Pattern callbackPattern = Pattern.compile("^[0-9a-zA-Z_.]+$");

	/**
	 * 检查普通请求参数是否安全。字符串中不能同时包含"<"和">"，否则可能存在脚本攻击
	 * 
	 * @param str
	 * @return
	 */
	public static boolean safeArg(String str) {
		boolean safe = true;
		if (str != null && str.contains("<") && str.contains(">")) {
			safe = false;
		}
		return safe;
	}

	/**
	 * 检查jsonp的callback参数是否安全
	 * 
	 * @param str
	 * @return
	 */
	public static boolean safeCallBack(String str) {
		boolean safe = true;
		if (str != null) {
			Matcher matcher = callbackPattern.matcher(str);
			if (!matcher.find()) {
				safe = false;
			}
		}
		return safe;
	}
}
