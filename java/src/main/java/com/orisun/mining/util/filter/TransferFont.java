package com.orisun.mining.util.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 繁简体转换
 * 
 * @Author:zhangchaoyang
 * @Since:2014-7-9
 * @Version:
 */
public class TransferFont {

	private static Log logger = LogFactory.getLog(TransferFont.class);
	private static Map<Character, Character> jianToFan = new HashMap<Character, Character>();
	private static Map<Character, Character> fanToJian = new HashMap<Character, Character>();

	static {
		String fileName = null;
		try {
			fileName = TransferFont.class.getClassLoader().getResource("")
					.getPath()
					+ "jianFan.dic";
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] arr = line.split("\\s+");
				if (arr.length == 2) {
					if (arr[1].length() == 1 && arr[0].length() == 1) {
						jianToFan.put(arr[1].charAt(0), arr[0].charAt(0));
						fanToJian.put(arr[0].charAt(0), arr[1].charAt(0));
					}
				}
			}
			br.close();
		} catch (IOException e) {
			logger.warn("FanJianFile not found: " + fileName
					+ ". please set it when init system.");
		}
	}

	public static void SetFanJianFile(String fileName) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] arr = line.split("\\s+");
				if (arr.length == 2) {
					if (arr[1].length() == 1 && arr[0].length() == 1) {
						jianToFan.put(arr[1].charAt(0), arr[0].charAt(0));
						fanToJian.put(arr[0].charAt(0), arr[1].charAt(0));
					}
				}
			}
			br.close();
		} catch (IOException e) {
			logger.error("read FanJian file failed.",e);
		}
	}

	/**
	 * 繁简体互相转换。 n==0,简转繁 n==1,繁转简
	 * 
	 * @param st
	 * @param n
	 * @return
	 */
	public static String conver(String st, int n) {
		if (n == 0) {
			return traditionalized(st);
		} else {
			return simplized(st);
		}
		
	}

	/**
	 * 简体转繁体
	 * 
	 * @param st
	 * @return
	 */
	private static String traditionalized(String st) {
		char[] arr=st.toCharArray();
		for (int i = 0; i < arr.length; i++) {
			char ch=arr[i];
			if (jianToFan.containsKey(ch)){
				arr[i]=jianToFan.get(ch);
			}
		}
		return new String(arr);
	}

	/**
	 * 繁体转简体
	 * 
	 * @param st
	 * @return
	 */
	private static String simplized(String st) {
		char[] arr=st.toCharArray();
		for (int i = 0; i < arr.length; i++) {
			char ch=arr[i];
			if (fanToJian.containsKey(ch)){
				arr[i]=fanToJian.get(ch);
			}
		}
		return new String(arr);
	}

	/**
	 * 判断文本中是否含有繁体字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean containFanti(String str) {
		for (int i = 0; i < str.length(); i++) {
			char temp = str.charAt(i);
			if (fanToJian.containsKey(temp)) {
				return true;
			}
		}
		return false;
	}
}
