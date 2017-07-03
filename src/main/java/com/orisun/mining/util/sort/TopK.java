package com.orisun.mining.util.sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TopK {

	/**
	 * 取出最大的topK，不保证顺序<br>
	 * 注意取出的是容器的引用
	 * 
	 * @param data
	 * @param k
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static <T extends Comparable> List<T> topK(List<T> data, int k) {
		MinHeap<T> heap = new MinHeap<T>(k);
		for (T ele : data) {
			heap.add(ele);
		}
		return heap.getTopK();
	}

	/**
	 * 从Map中取出value最大的topK，不保证顺序
	 * 
	 * @param data
	 * @param k
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static <K, V extends Comparable> List<Tuple<K, V>> topK(
			Map<K, V> data, int k) {
		List<Tuple<K, V>> list = new ArrayList<Tuple<K, V>>();
		for (Entry<K, V> entry : data.entrySet()) {
			list.add(new Tuple<K, V>(entry.getKey(), entry.getValue()));
		}
		return topK(list, k);
	}

	@SuppressWarnings("rawtypes")
	public static class Tuple<K, V extends Comparable> implements
			Comparable<Tuple<K, V>> {
		private K key;
		private V value;

		public Tuple(K key, V value) {
			this.key = key;
			this.value = value;
		}

		@SuppressWarnings("unchecked")
		@Override
		public int compareTo(Tuple<K, V> o) {
			return this.value.compareTo(o.value);
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}

	}
}
