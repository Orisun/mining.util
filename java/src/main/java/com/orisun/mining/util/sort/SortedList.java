package com.orisun.mining.util.sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortedList<V extends Comparable<V>> {
	private List<V> list = new ArrayList<V>();

	public SortedList() {

	}

	public SortedList(List<V> arr) {
		Collections.sort(arr);
		this.setList(arr);
	}

	public void sort() {
		Collections.sort(list);
	}

	public void setList(List<V> list) {
		this.list = list;
	}

	public void add(V ele) {
		this.list.add(ele);
	}

	public int getIndex(V ele) {
		return BinarySearch.search(list, ele);
	}

	public V get(V ele) {
		int index = BinarySearch.search(list, ele);
		if (index < 0 || index >= list.size()) {
			return null;
		} else {
			return list.get(index);
		}
	}
}