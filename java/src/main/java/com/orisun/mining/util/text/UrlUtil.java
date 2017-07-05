package com.orisun.mining.util.text;  

import java.util.HashMap;
import java.util.Map;
  
public class UrlUtil {

	/**
	 * 解析url中的参数
	 * 
	 * @param url
	 * @return
	 */
	public static Map<String, String> parseUrlParam(String url) {
		Map<String, String> rect = new HashMap<String, String>();
		String[] arr = url.split("\\?");
		if (arr.length == 2) {
			String[] brr = arr[1].split("&");
			for (String ele : brr) {
				String[] crr = ele.split("=");
				if (crr.length == 2) {
					rect.put(crr[0], crr[1]);
				}
			}
		}
		return rect;
	}
}
