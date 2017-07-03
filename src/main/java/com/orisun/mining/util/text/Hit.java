package com.orisun.mining.util.text;

public class Hit<V> {
	/**
	 * the beginning index, inclusive.
	 */
	public final int begin;
	/**
	 * the ending index, exclusive.
	 */
	public final int end;
	/**
	 * the value assigned to the keyword
	 */
	public final V value;

	public Hit(int begin, int end, V value) {
		this.begin = begin;
		this.end = end;
		this.value = value;
	}

	@Override
	public String toString() {
		return String.format("[%d:%d]=%s", begin, end, value);
	}

	public int getBegin() {
		return begin;
	}

	public int getEnd() {
		return end;
	}

	public V getValue() {
		return value;
	}

}
