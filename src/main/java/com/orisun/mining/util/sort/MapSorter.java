package com.orisun.mining.util.sort;

import java.util.*;
import java.util.Map.Entry;

public class MapSorter {

	/**
	 * Map按Value进行排序
	 * 
	 * @param map
	 * @param desc
	 *            true:降序排列；false:升序排列
	 * @return
	 */
	public static <K, T extends Comparable<T>> List<Entry<K, T>> sortMapByValue(
			Map<K, T> map, final boolean desc) {
		List<Entry<K, T>> list = new LinkedList<Entry<K, T>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, T>>() {
			@Override
			public int compare(Entry<K, T> o1, Entry<K, T> o2) {
				if (!desc) {
					return o1.getValue().compareTo(o2.getValue());
				} else {
					return o2.getValue().compareTo(o1.getValue());
				}
			}
		});
		return list;
	}
}
