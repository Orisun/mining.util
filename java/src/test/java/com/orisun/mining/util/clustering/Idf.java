package com.orisun.mining.util.clustering;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Idf {

	private static final Log logger = LogFactory.getLog(Idf.class);
	private static Map<String, Double> termIdf = new HashMap<String, Double>();

	public static void init(String file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] arr = line.trim().split("\\s+");
			if (arr.length == 2) {
				termIdf.put(arr[0], Double.parseDouble(arr[1]));
			}
		}
		br.close();
	}

	public static double getIdf(String term) {
		Double rect = termIdf.get(term);
		if (rect == null) {
			rect = 0.002;
			logger.warn(term + " not in idf dict");
		}
		return rect;
	}


}
