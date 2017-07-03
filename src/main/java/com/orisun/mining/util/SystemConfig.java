package com.orisun.mining.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配置文件读写的工具类
 * 
 * @Author:zhangchaoyang
 * @Since:2014-8-18
 * @Version:1.0
 */
public class SystemConfig {
	private static Map<String, String> configMap = null;
	private static Properties property = null;
	private static String path = null;

	/**
	 * 从一个文件中读取配置
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public synchronized static void init(String path) throws IOException {
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		if (path != null) {
			SystemConfig.path = path;
			property = new Properties();
			property.load(new FileInputStream(path));
			configMap = new ConcurrentHashMap<String, String>();
			Iterator<Map.Entry<Object, Object>> it = property.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry<Object, Object> entry = it.next();
				String key = entry.getKey().toString().trim();
				String value = entry.getValue().toString().trim();
				configMap.put(key, value);
			}
		}
	}

	public synchronized static void refresh() throws IOException {
		if (path != null) {
			property.clear();
			property.load(new FileInputStream(path));
			Map<String, String> configMapTmp = new ConcurrentHashMap<String, String>();
			Iterator<Map.Entry<Object, Object>> it = property.entrySet()
					.iterator();
			while (it.hasNext()) {
				Map.Entry<Object, Object> entry = it.next();
				String key = entry.getKey().toString().trim();
				String value = entry.getValue().toString().trim();
				configMapTmp.put(key, value);
			}
			configMap = configMapTmp;
		}
	}

	/**
	 * 获取某个属性值
	 * 
	 * @param key
	 * @return
	 */
	public static String getValue(String key) {
		key=key.trim();
		if (configMap != null) {
			return configMap.get(key);
		} else {
			return null;
		}
	}

	public static String getValue(String key, String defaultvalue) {
		key=key.trim();
		if (configMap != null && configMap.containsKey(key)) {
			return configMap.get(key);
		} else {
			return defaultvalue;
		}
	}

	public static int getIntValue(String key, int defaultValue) {
		key=key.trim();
		int rect = -1;
		String value = getValue(key);
		if (value != null) {
			rect = Integer.parseInt(getValue(key));
		}
		if (rect == -1) {
			rect = defaultValue;
		}
		return rect;
	}

	public static double getDoubleValue(String key, double defaultValue) {
		key=key.trim();
		double rect = -1.0;
		String value = getValue(key);
		if (value != null) {
			rect = Double.parseDouble(getValue(key));
		}
		if (rect == -1.0) {
			rect = defaultValue;
		}
		return rect;
	}

	/**
	 * 将指定的key-value序列化到配置文件中
	 * 
	 * @param key
	 * @param value
	 */
	public synchronized static void serializeValue(String key, String value) {
		key=key.trim();
		if (path != null) {
			try {
				FileOutputStream fos = new FileOutputStream(path);
				property.setProperty(key, value);
				property.store(fos, "update " + key + " periodically");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
