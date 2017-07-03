package com.orisun.mining.util;

import com.orisun.mining.util.exception.ArgumentException;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 各种数据类型的相互转换<br>
 * <ul>
 * <li>{@code <<} 左移，符号位不动
 * <li>{@code >>} 右移，符号位不动
 * <li>{@code >>>} 循环右移，符号位要跟着移，高位用0填充
 * </ul>
 * 位移运算只对32位和64位值有意义。位移运算返回一个新值，但是不改变原值。
 * 
 * @Author:zhangchaoyang
 * @Since:2014-7-9
 * @Version:
 */
public class DataTransform {

	private static final char[] Digit = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * byte数组转换成int
	 * 
	 * @param bRefArr
	 *            byte数组
	 * @param LowEndian
	 *            byte数组是否按小端字节序存储
	 * @return int值
	 * @throws ArgumentException
	 *             byte数组长度超过4时抛出该异常
	 */
	public static int bytesToInt(byte[] bRefArr, boolean LowEndian)
			throws ArgumentException {
		int len = bRefArr.length;
		if (len > 4) {
			throw new ArgumentException("字节数组长度不能超过4");
		}

		int iOutcome = 0;
		byte bLoop;
		for (int i = 0; i < len; i++) {
			bLoop = bRefArr[i];
			int shift;
			if (LowEndian) {
				shift = i;
			} else {
				shift = len - 1 - i;
			}
			iOutcome += (bLoop & 0xFF) << (8 * shift);// 之所以要跟0xFF进行与运行是为了把bLoop转换成int,去除符号位的影响
		}
		return iOutcome;
	}

	/**
	 * byte数组转换成long
	 * 
	 * @param bRefArr
	 *            byte数组
	 * @param LowEndian
	 *            byte数组是否按小端字节序存储
	 * @return long值
	 * @throws ArgumentException
	 *             byte数组长度超过8时抛出该异常
	 */
	public static long bytesToLong(byte[] bRefArr, boolean LowEndian)
			throws ArgumentException {
		int len = bRefArr.length;
		if (len > 8) {
			throw new ArgumentException("字节数组长度不能超过8");
		}

		long iOutcome = 0;
		byte bLoop;
		for (int i = 0; i < len; i++) {
			bLoop = bRefArr[i];
			int shift;
			if (LowEndian) {
				shift = i;
			} else {
				shift = len - 1 - i;
			}
			iOutcome += (bLoop & 0xFFL) << (8 * shift);// 之所以要跟0xFFL进行与运行是为了把bLoop转换成long,去除符号位的影响
		}
		return iOutcome;
	}

	/**
	 * byte数组转换成double
	 * 
	 * @param bRefArr
	 *            byte数组
	 * @param LowEndian
	 *            byte数组是否按小端字节序存储
	 * @return double值
	 * @throws ArgumentException
	 *             byte数组长度超过8时抛出该异常
	 */
	public static double bytesToDouble(byte[] bRefArr, boolean LowEndian)
			throws ArgumentException {
		long l = bytesToLong(bRefArr, LowEndian);
		return Double.longBitsToDouble(l);
	}

	/**
	 * int转换为byte数组,采用大端字节序会更快一些
	 * 
	 * @param number
	 *            int数
	 * @param LowEndian
	 *            byte数组是否按小端字节序存储
	 * @return byte数组
	 */
	public static byte[] intToBytes(int number, boolean LowEndian) {
		int len = 4;
		byte[] rect = new byte[len];
		for (int i = 0; i < len; i++) {
			rect[i] = (byte) (number >>> (len - 1 - i) * 8);
		}
		if (LowEndian) {
			for (int i = 0; i < len / 2; i++) {
				byte swap = rect[i];
				rect[i] = rect[len - i - 1];
				rect[len - i - 1] = swap;
			}
		}
		return rect;
	}

	/**
	 * 仿照Lucene的可变长度整型:最高位表示是否还有字节要读取，低七位就是就是具体的有效位，添加到结果数据中.<br>
	 * 比如00000001 最高位表示0，那么说明这个数就是一个字节表示，有效位是后面的七位0000001，值为1。10000010 00000001
	 * 第一个字节最高位为1
	 * ，表示后面还有字节，第二位最高位0表示到此为止了，即就是两个字节，那么具体的值注意，是从最后一个字节的七位有效数放在最前面，依次放置
	 * ，最后是第一个自己的七位有效位，所以这个数表示 0000001 0000010，换算成整数就是130。<br>
	 * 用VInt来表示Integer.MAX_VALUE时需要5个字节.
	 * 
	 * @param num
	 * @return
	 */
	public static byte[] vintToByte(int num) {
		ByteBuffer buffer = ByteBuffer.allocate(32);
		while ((num & ~0x7F) != 0) {
			buffer.put((byte) ((num & 0x7F) | 0x80));
			num >>>= 7;// 等价于num=num>>>7;
		}
		buffer.put((byte) num);
		byte[] rect = new byte[buffer.position()];
		buffer.flip();
		buffer.get(rect);
		return rect;
	}

	public static byte[] vintArrToByteArr(int[] arr) {
		ByteBuffer buffer = ByteBuffer.allocate(32 * arr.length);
		for (int ele : arr) {
			byte[] brr = vintToByte(ele);
			buffer.put(brr);
		}
		byte[] rect = new byte[buffer.position()];
		buffer.flip();
		buffer.get(rect);
		return rect;
	}

	/**
	 * 仿照Lucene的可变长度整型
	 * 
	 * @see #vintToByte
	 * @param bytes
	 * @return
	 */
	public static int byteToVInt(byte[] bytes) {
		int i = 0;
		byte b = bytes[i++];
		int num = b & 0x7F;
		for (int shift = 7; (b & 0x80) != 0; shift += 7) {
			b = bytes[i++];
			num |= (b & 0x7F) << shift;
		}
		return num;
	}

	public static int[] byteArrToVIntArr(byte[] bytes) {
		List<Integer> list = new ArrayList<Integer>();
		int i = 0;
		while (i < bytes.length) {
			byte b = bytes[i++];
			int num = b & 0x7F;
			for (int shift = 7; (b & 0x80) != 0; shift += 7) {
				b = bytes[i++];
				num |= (b & 0x7F) << shift;
			}
			list.add(num);
		}
		int[] rect = new int[list.size()];
		for (int j = 0; j < rect.length; j++) {
			rect[j] = list.get(j);
		}
		return rect;
	}

	/**
	 * 仿照Lucene的可变长度整型
	 * 
	 * @see #vintToByte
	 * @param num
	 * @return
	 */
	public static byte[] vlongToByte(long num) {
		ByteBuffer buffer = ByteBuffer.allocate(64);
		while ((num & ~0x7F) != 0) {
			buffer.put((byte) ((num & 0x7F) | 0x80));
			num >>>= 7;
		}
		buffer.put((byte) num);
		byte[] rect = new byte[buffer.position()];
		buffer.flip();
		buffer.get(rect);
		return rect;
	}

	/**
	 * 仿照Lucene的可变长度整型
	 * 
	 * @see #vintToByte
	 * @param bytes
	 * @return
	 */
	public static long byteToVLong(byte[] bytes) {
		int i = 0;
		byte b = bytes[i++];
		long num = b & 0x7FL;
		for (int shift = 7; (b & 0x80) != 0; shift += 7) {
			b = bytes[i++];
			num |= (b & 0x7FL) << shift;
		}
		return num;
	}

	/**
	 * long转换为byte数组
	 * 
	 * @param number
	 *            long数
	 * @param LowEndian
	 *            byte数组是否按小端字节序存储
	 * @return byte数组,长度为8
	 */
	public static byte[] longToBytes(long number, boolean LowEndian) {
		int len = 8;
		byte[] rect = new byte[len];
		for (int i = 0; i < len; i++) {
			rect[i] = (byte) (number >>> (len - 1 - i) * 8);
		}
		if (LowEndian) {
			for (int i = 0; i < len / 2; i++) {
				byte swap = rect[i];
				rect[i] = rect[len - i - 1];
				rect[len - i - 1] = swap;
			}
		}
		return rect;
	}

	/**
	 * double转换为byte数组
	 * 
	 * @param number
	 *            double数值
	 * @param LowEndian
	 *            byte数组是否按小端字节序存储
	 * @return byte数组,长度为8
	 */
	public static byte[] doubleToBytes(double number, boolean LowEndian) {
		long l = Double.doubleToLongBits(number);
		return longToBytes(l, LowEndian);
	}

	/**
	 * IP转换成int值,int在全域上和IP是一一对应的
	 * 
	 * @param ip
	 * @return
	 * @throws ArgumentException
	 *             IP范围超界时抛出该异常
	 */
	public static int ip2int(String ip) throws ArgumentException {
		String[] arr = ip.trim().split("\\.");
		int part1 = Integer.parseInt(arr[0]);
		int part2 = Integer.parseInt(arr[1]);
		int part3 = Integer.parseInt(arr[2]);
		int part4 = Integer.parseInt(arr[3]);
		if (part1 >= 0 && part1 < 256 && part2 >= 0 && part2 < 256
				&& part3 >= 0 && part3 < 256 && part4 >= 0 && part4 < 256) {
			// 左移，正数左移之后有可能把最高位变为1，从而成为负数
			int rect = part1 << 24;
			rect += part2 << 16;
			rect += part3 << 8;
			rect += part4;
			return rect;
		} else {
			throw new ArgumentException("IP范围超界");
		}
	}

	/**
	 * int值转换成IP,int在全域上和IP是一一对应的
	 * 
	 * @param number
	 * @return
	 */
	public static String int2ip(int number) {
		StringBuilder sb = new StringBuilder();
		int part1 = number >>> 24;// 右移，如果是负数最高位的1会向右移，且最高位变为0
		int part2 = (0x00ff0000 & number) >>> 16;// 位移的优先级高于与运算的优先级
		int part3 = (0x0000ff00 & number) >>> 8;
		int part4 = 0x000000ff & number;
		sb.append(String.valueOf(part1));
		sb.append(".");
		sb.append(String.valueOf(part2));
		sb.append(".");
		sb.append(String.valueOf(part3));
		sb.append(".");
		sb.append(String.valueOf(part4));
		return sb.toString();
	}

	/**
	 * 一个将字节转化为十六进制ASSIC码的函数
	 * 
	 * @param ib
	 * @return
	 */
	public static String byteHEX(byte ib) {
		char[] ob = new char[2];
		ob[0] = Digit[(ib >>> 4) & 0X0F];
		ob[1] = Digit[ib & 0X0F];
		String s = new String(ob);
		return s;
	}

	public static String byteHEX(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte ib : bytes) {
			char[] ob = new char[2];
			ob[0] = Digit[(ib >>> 4) & 0X0F];
			ob[1] = Digit[ib & 0X0F];
			String s = new String(ob);
			sb.append(s);
		}
		return sb.toString();
	}

	/**
	 * 把一个byte表示成二进制的字符串字面值
	 * 
	 * @param ib
	 * @return
	 */
	public static String byteLiteral(byte ib) {
		StringBuilder sb = new StringBuilder();
		for (int i = 7; i >= 0; i--) {
			int v = (ib >>> i) & 0x01;
			if (v == 0) {
				sb.append("0");
			} else {
				sb.append("1");
			}
		}
		return sb.toString();
	}

	public static String byteLiteral(byte[] ib) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ib.length; i++) {
			sb.append(byteLiteral(ib[i]));
		}
		return sb.toString();
	}
}
