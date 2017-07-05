package com.orisun.mining.util;

import java.nio.ByteBuffer;

public class VLong {

	final byte[] arr;

	public VLong(long num) {
		ByteBuffer buffer = ByteBuffer.allocate(64);
		while ((num & ~0x7F) != 0) {
			buffer.put((byte) ((num & 0x7F) | 0x80));
			num >>>= 7;
		}
		buffer.put((byte) num);
		arr = new byte[buffer.position()];
		buffer.flip();
		buffer.get(arr);
	}

	public long getValue() {
		int i = 0;
		byte b = arr[i++];
		long num = b & 0x7FL;
		for (int shift = 7; (b & 0x80) != 0; shift += 7) {
			b = arr[i++];
			num |= (b & 0x7FL) << shift;
		}
		return num;
	}

	public byte[] getBytes() {
		return arr;
	}

	@Override
	public int hashCode() {
		int rect = 0;
		for (byte b : arr) {
			rect += 31 * b;
		}
		return rect;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VLong) {
			VLong other = (VLong) obj;
			if (this.getValue() == other.getValue()) {
				return true;
			}
		}
		return false;
	}
}
