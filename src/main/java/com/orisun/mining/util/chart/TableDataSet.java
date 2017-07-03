package com.orisun.mining.util.chart;

import java.util.ArrayList;
import java.util.List;

public class TableDataSet {

	private String title;
	private List<String> header;
	private List<List<String>> value;

	public void setHeader(String... args) {
		header = new ArrayList<String>();
		for (String arg : args) {
			header.add(arg);
		}
	}
	
	public List<String> getHeader(){
		return header;
	}
	
	public List<List<String>> getValue(){
		return value;
	}

	public void addValue(String... args) {
		if (value == null) {
			value = new ArrayList<List<String>>();
		}
		List<String> list = new ArrayList<String>();
		for (String arg : args) {
			list.add(arg);
		}
		value.add(list);
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
