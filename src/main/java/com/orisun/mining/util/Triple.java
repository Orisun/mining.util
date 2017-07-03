package com.orisun.mining.util;

/**
 * 任意三个对象的组合可以封装成一个Triple
 * 
 * @Author:zhangchaoyang
 * @Since:2014-10-22
 * @Version:1.0
 */
public class Triple<A, B, C> implements Comparable<Triple<A, B, C>> {

	public final A first;

	public final B second;

	public final C third;

	public Triple(A fst, B snd, C thd) {
		this.first = fst;
		this.second = snd;
		this.third = thd;
	}

	@Override
	public String toString() {
		return "Triple[" + first + "," + second + "," + third + "]";
	}

	private static boolean equals(Object x, Object y) {
		return (x == null && y == null) || (x != null && x.equals(y));
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof Triple<?, ?, ?>
				&& equals(first, ((Triple<?, ?, ?>) other).first)
				&& equals(second, ((Triple<?, ?, ?>) other).second)
				&& equals(third, ((Triple<?, ?, ?>) other).third);
	}

	@Override
	public int hashCode() {
		int rect = 0;
		if (first != null) {
			rect = 31 * rect + first.hashCode();
		}
		if (second != null) {
			rect = 31 * rect + second.hashCode();
		}
		if (third != null) {
			rect = 31 * rect + third.hashCode();
		}
		return rect;
	}

	public static <A, B, C> Triple<A, B, C> of(A a, B b, C c) {
		return new Triple<A, B, C>(a, b, c);
	}

	public int compareTo(Triple<A, B, C> obj) {
		return this.hashCode() - obj.hashCode();
	}
}
