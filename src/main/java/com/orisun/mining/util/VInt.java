package com.orisun.mining.util;

import java.nio.ByteBuffer;

public class VInt implements Comparable<VInt> {

	final byte[] arr;

	public VInt(int num) {
		ByteBuffer buffer = ByteBuffer.allocate(32);
		while ((num & ~0x7F) != 0) {
			buffer.put((byte) ((num & 0x7F) | 0x80));
			// java中有3种位移运算：>> 1，符号位不动，相当于除以2；<< 1，符号位不动，相当于乘以2；>>> 循环右移，
			// 符号位要跟着移，高位用0填充。位移运算只对32位和64位值有意义。位移运算返回一个新值，但是不改变原值。
			num >>>= 7;// 等价于num=num>>>7;
		}
		buffer.put((byte) num);
		arr = new byte[buffer.position()];
		buffer.flip();
		buffer.get(arr);
	}

	public int getValue() {
		int i = 0;
		byte b = arr[i++];
		int num = b & 0x7F;
		for (int shift = 7; (b & 0x80) != 0; shift += 7) {
			b = arr[i++];
			num |= (b & 0x7F) << shift;
		}
		return num;
	}

	public byte[] getBytes() {
		return arr;
	}

	@Override
	public String toString() {
		return String.valueOf(getValue());
	}

	@Override
	public int hashCode() {
		return getValue();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VInt) {
			VInt other = (VInt) obj;
			byte[] brr = other.getBytes();
			if (arr.length != brr.length) {
				return false;
			}
			for (int i = 0; i < arr.length; i++) {
				if (arr[i] != brr[i]) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public int compareTo(VInt o) {
		return this.getValue() - o.getValue();
	}
}
