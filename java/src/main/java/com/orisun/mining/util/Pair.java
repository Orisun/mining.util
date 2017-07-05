package com.orisun.mining.util;

import java.io.Serializable;

/**
 * 任意两个对象的组合可以封装成一个pair
 * 
 * @Author:zhangchaoyang
 * @Since:2014-7-11
 * @Version:
 */
public class Pair<A, B> implements Comparable<Pair<A, B>>, Serializable {

	private static final long serialVersionUID = -9114023052232308707L;

	public final A first;

	public final B second;

	public Pair(A fst, B snd) {
		this.first = fst;
		this.second = snd;
	}

	@Override
	public String toString() {
		return first + ":" + second;
	}

	private static boolean equals(Object x, Object y) {
		return (x == null && y == null) || (x != null && x.equals(y));
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof Pair<?, ?> && equals(first, ((Pair<?, ?>) other).first)
				&& equals(second, ((Pair<?, ?>) other).second);
	}

	@Override
	public int hashCode() {
		if (first == null)
			return (second == null) ? 0 : second.hashCode() + 1;
		else if (second == null)
			return first.hashCode() + 2;
		else
			return first.hashCode() * 17 + second.hashCode();
	}

	public static <A, B> Pair<A, B> of(A a, B b) {
		return new Pair<A, B>(a, b);
	}

	public int compareTo(Pair<A, B> obj) {
		return this.hashCode() - obj.hashCode();
	}
}
