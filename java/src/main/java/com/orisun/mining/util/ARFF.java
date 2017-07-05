package com.orisun.mining.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Attribute-Relation File Format文件解析类
 *
 *@Author:zhangchaoyang 
 *@Since:2014-8-11  
 *@Version:
 */
public class ARFF {
	private ArrayList<String> attribute = new ArrayList<String>(); // 存储属性的名称
	private ArrayList<ArrayList<String>> attributevalue = new ArrayList<ArrayList<String>>(); // 如果是离散属性，则存储每个属性的取值；否则存储数据的类型
	private ArrayList<String[]> data = new ArrayList<String[]>(); // 原始数据
	String relationName;// 关系名
	public static final String attributePattern = "@attribute(.*)[{](.*?)[}]";
	public static final String relationPattern = "@relation\\s+(\\S+)";

	public ARFF(String file) {
		parseFile(file);
	}

	private void parseFile(String file) {
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			Pattern attrPattern = Pattern.compile(attributePattern);
			Pattern relaPattern = Pattern.compile(relationPattern);
			while ((line = br.readLine()) != null) {
				Matcher attrMatch = attrPattern.matcher(line);
				if (attrMatch.find()) {
					attribute.add(attrMatch.group(1).trim());
					String[] values = attrMatch.group(2).split(",");
					ArrayList<String> al = new ArrayList<String>(values.length);
					for (String value : values) {
						al.add(value.trim());
					}
					attributevalue.add(al);
				} else if (line.startsWith("@data")) {
					while ((line = br.readLine()) != null) {
						if (line == "")
							continue;
						String[] row = line.split(",");
						data.add(row);
					}
				} else {
					Matcher relaMatch = relaPattern.matcher(line);
					if (relaMatch.find()) {
						relationName = relaMatch.group(1).trim();
					} else {
						continue;
					}
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> getAttribute() {
		return attribute;
	}

	public ArrayList<ArrayList<String>> getAttributevalue() {
		return attributevalue;
	}

	public ArrayList<String[]> getData() {
		return data;
	}

	public String getRelationName() {
		return relationName;
	}
	
}
