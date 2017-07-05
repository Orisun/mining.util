package com.orisun.mining.util;

import com.orisun.mining.util.exception.ArgumentException;
import com.orisun.mining.util.math.distribution.Uniform;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Random;
  
public class TestDataTransform {
	@Test
	public void testByteAndInt() throws ArgumentException {
		int i = Integer.MAX_VALUE;
		byte[] arr = DataTransform.intToBytes(i, false);
		int j = DataTransform.bytesToInt(arr, false);
		Assert.assertEquals(i, j);
		arr = DataTransform.intToBytes(i, true);
		j = DataTransform.bytesToInt(arr, true);
		Assert.assertEquals(i, j);

		i = Integer.MIN_VALUE;
		arr = DataTransform.intToBytes(i, false);
		j = DataTransform.bytesToInt(arr, false);
		Assert.assertEquals(i, j);
		arr = DataTransform.intToBytes(i, true);
		j = DataTransform.bytesToInt(arr, true);
		Assert.assertEquals(i, j);

		i = 0;
		arr = DataTransform.intToBytes(i, false);
		j = DataTransform.bytesToInt(arr, false);
		Assert.assertEquals(i, j);
		arr = DataTransform.intToBytes(i, true);
		j = DataTransform.bytesToInt(arr, true);
		Assert.assertEquals(i, j);

		for (int loop = 0; loop < 10000; loop++) {
			Uniform uniform = new Uniform(-1000, 1000);
			i = (int) Math.round(uniform.drawOnePoint());
			arr = DataTransform.intToBytes(i, false);
			j = DataTransform.bytesToInt(arr, false);
			Assert.assertEquals(i, j);
			arr = DataTransform.intToBytes(i, true);
			j = DataTransform.bytesToInt(arr, true);
			Assert.assertEquals(i, j);
		}
		
		byte[] bytes=new byte[]{0x00,0x14,0x1C,0x02};
		System.out.println(DataTransform.bytesToInt(bytes, false));
	}

	@Test
	public void testByteAndLong() throws ArgumentException {
		long i = Long.MAX_VALUE;
		byte[] arr = DataTransform.longToBytes(i, false);
		long j = DataTransform.bytesToLong(arr, false);
		Assert.assertEquals(i, j);
		arr = DataTransform.longToBytes(i, true);
		j = DataTransform.bytesToLong(arr, true);
		Assert.assertEquals(i, j);

		i = Integer.MIN_VALUE;
		arr = DataTransform.longToBytes(i, false);
		j = DataTransform.bytesToLong(arr, false);
		Assert.assertEquals(i, j);
		arr = DataTransform.longToBytes(i, true);
		j = DataTransform.bytesToLong(arr, true);
		Assert.assertEquals(i, j);

		i = 0;
		arr = DataTransform.longToBytes(i, false);
		j = DataTransform.bytesToLong(arr, false);
		Assert.assertEquals(i, j);
		arr = DataTransform.longToBytes(i, true);
		j = DataTransform.bytesToLong(arr, true);
		Assert.assertEquals(i, j);

		for (int loop = 0; loop < 10000; loop++) {
			Uniform uniform = new Uniform(-1000, 1000);
			i = Math.round(uniform.drawOnePoint());
			arr = DataTransform.longToBytes(i, false);
			j = DataTransform.bytesToLong(arr, false);
			Assert.assertEquals(i, j);
			arr = DataTransform.longToBytes(i, true);
			j = DataTransform.bytesToLong(arr, true);
			Assert.assertEquals(i, j);
		}
	}

	@Test
	public void testByteAndDouble() throws ArgumentException {
		double i = Double.MAX_VALUE;
		byte[] arr = DataTransform.doubleToBytes(i, false);
		double j = DataTransform.bytesToDouble(arr, false);
		Assert.assertTrue(Math.abs(i - j) < 1E-100);
		arr = DataTransform.doubleToBytes(i, true);
		j = DataTransform.bytesToDouble(arr, true);
		Assert.assertTrue(Math.abs(i - j) < 1E-100);

		i = Integer.MIN_VALUE;
		arr = DataTransform.doubleToBytes(i, false);
		j = DataTransform.bytesToDouble(arr, false);
		Assert.assertTrue(Math.abs(i - j) < 1E-100);
		arr = DataTransform.doubleToBytes(i, true);
		j = DataTransform.bytesToDouble(arr, true);
		Assert.assertTrue(Math.abs(i - j) < 1E-100);

		i = 0;
		arr = DataTransform.doubleToBytes(i, false);
		j = DataTransform.bytesToDouble(arr, false);
		Assert.assertTrue(Math.abs(i - j) < 1E-100);
		arr = DataTransform.doubleToBytes(i, true);
		j = DataTransform.bytesToDouble(arr, true);
		Assert.assertTrue(Math.abs(i - j) < 1E-100);

		for (int loop = 0; loop < 10000; loop++) {
			Uniform uniform = new Uniform(-1000, 1000);
			i = uniform.drawOnePoint();
			arr = DataTransform.doubleToBytes(i, false);
			j = DataTransform.bytesToDouble(arr, false);
			Assert.assertTrue(Math.abs(i - j) < 1E-100);
			arr = DataTransform.doubleToBytes(i, true);
			j = DataTransform.bytesToDouble(arr, true);
			Assert.assertTrue(Math.abs(i - j) < 1E-100);
		}
	}

	@Test
	public void testIpAndInt() {
		boolean normal = true;
		try {
			String ip = "127.0.0.1";
			int ip_int = DataTransform.ip2int(ip);
			String ip_str = DataTransform.int2ip(ip_int);
			Assert.assertEquals(ip, ip_str);
		} catch (ArgumentException e) {
			normal = false;
		}
		Assert.assertTrue(normal);

		normal = true;
		try {
			String ip = "0.0.0.0";
			int ip_int = DataTransform.ip2int(ip);
			String ip_str = DataTransform.int2ip(ip_int);
			Assert.assertEquals(ip, ip_str);
		} catch (ArgumentException e) {
			normal = false;
		}
		Assert.assertTrue(normal);

		normal = true;
		try {
			String ip = "255.255.255.255";
			int ip_int = DataTransform.ip2int(ip);
			String ip_str = DataTransform.int2ip(ip_int);
			Assert.assertEquals(ip, ip_str);
		} catch (ArgumentException e) {
			normal = false;
		}
		Assert.assertTrue(normal);

		normal = true;
		try {
			String ip = "255.255.255.256";
			DataTransform.ip2int(ip);
		} catch (ArgumentException e) {
			normal = false;
		}
		Assert.assertFalse(normal);

		normal = true;
		try {
			String ip = "-1.255.255.255";
			DataTransform.ip2int(ip);
		} catch (ArgumentException e) {
			normal = false;
		}
		Assert.assertFalse(normal);

		normal = true;
		try {
			Random random = new Random();
			for (int i = 0; i < 1000; i++) {
				int ip_int = random.nextInt();
				String ip_str = DataTransform.int2ip(ip_int);
				int ip = DataTransform.ip2int(ip_str);
				Assert.assertEquals(ip_int, ip);
			}
		} catch (ArgumentException e) {
			normal = false;
		}
		Assert.assertTrue(normal);
	}

	@Test
	public void testVInt2() {
		long l = Long.MAX_VALUE;
		int in = (int) l;
		byte[] lbs = DataTransform.vlongToByte(l);// 把一个long转换为bytes
		int vint = DataTransform.byteToVInt(lbs);// 再把这个bytes转换为int
		Assert.assertTrue(vint == in);
		long vlong = DataTransform.byteToVLong(lbs);
		Assert.assertTrue(vlong == l);
	}

	@Test
	public void testVInt() {
		int num1 = 0;
		byte[] bytes = DataTransform.vintToByte(num1);
		int num2 = DataTransform.byteToVInt(bytes);
		Assert.assertTrue(num1 == num2);

		num1 = 1;
		bytes = DataTransform.vintToByte(num1);
		num2 = DataTransform.byteToVInt(bytes);
		Assert.assertTrue(num1 == num2);

		num1 = 2;
		bytes = DataTransform.vintToByte(num1);
		num2 = DataTransform.byteToVInt(bytes);
		Assert.assertTrue(num1 == num2);

		num1 = 127;
		bytes = DataTransform.vintToByte(num1);
		num2 = DataTransform.byteToVInt(bytes);
		Assert.assertTrue(num1 == num2);

		num1 = -128;
		bytes = DataTransform.vintToByte(num1);
		num2 = DataTransform.byteToVInt(bytes);
		Assert.assertTrue(num1 == num2);

		num1 = 16383;
		bytes = DataTransform.vintToByte(num1);
		num2 = DataTransform.byteToVInt(bytes);
		Assert.assertTrue(num1 == num2);

		num1 = 16384;
		bytes = DataTransform.vintToByte(num1);
		num2 = DataTransform.byteToVInt(bytes);
		Assert.assertTrue(num1 == num2);

		num1 = 16385;
		bytes = DataTransform.vintToByte(num1);
		num2 = DataTransform.byteToVInt(bytes);
		Assert.assertTrue(num1 == num2);

		num1 = Integer.MAX_VALUE;
		bytes = DataTransform.vintToByte(num1);
		Assert.assertTrue(5 == bytes.length);// 用VInt来表示Integer.MAX_VALUE时需要5个字节
		num2 = DataTransform.byteToVInt(bytes);
		Assert.assertTrue(num1 == num2);

		num1 = Integer.MIN_VALUE;
		bytes = DataTransform.vintToByte(num1);
		num2 = DataTransform.byteToVInt(bytes);
		Assert.assertTrue(num1 == num2);

		int[] arr = new int[] { 0, 1, 2, 127, 128, 16383, 16384, 16385,
				Integer.MAX_VALUE, Integer.MIN_VALUE };
		byte[] brr = DataTransform.vintArrToByteArr(arr);
		int[] crr = DataTransform.byteArrToVIntArr(brr);
		Assert.assertTrue(crr.length == arr.length);
		for (int i = 0; i < crr.length; i++) {
			Assert.assertTrue(crr[i] == arr[i]);
		}
	}

	@Test
	public void testVLong() {
		long num1 = 0L;
		byte[] bytes = DataTransform.vlongToByte(num1);
		long num2 = DataTransform.byteToVLong(bytes);
		Assert.assertTrue(num1 == num2);

		num1 = 1L;
		bytes = DataTransform.vlongToByte(num1);
		num2 = DataTransform.byteToVLong(bytes);
		Assert.assertTrue(num1 == num2);

		num1 = 16383L;
		bytes = DataTransform.vlongToByte(num1);
		num2 = DataTransform.byteToVLong(bytes);
		Assert.assertTrue(num1 == num2);

		num1 = (long) Integer.MAX_VALUE;
		bytes = DataTransform.vlongToByte(num1);
		num2 = DataTransform.byteToVLong(bytes);
		Assert.assertTrue(num1 == num2);

		num1 = (long) Integer.MAX_VALUE + 1L;
		bytes = DataTransform.vlongToByte(num1);
		num2 = DataTransform.byteToVLong(bytes);
		Assert.assertTrue(num1 == num2);

		num1 = Long.MIN_VALUE;
		bytes = DataTransform.vlongToByte(num1);
		num2 = DataTransform.byteToVLong(bytes);
		Assert.assertTrue(num1 == num2);

		num1 = Long.MAX_VALUE;
		bytes = DataTransform.vlongToByte(num1);
		num2 = DataTransform.byteToVLong(bytes);
		Assert.assertTrue(num1 == num2);

		BigInteger bigint = BigInteger.valueOf(Long.MAX_VALUE + 1);//测试BigInteger
		num1 = bigint.longValue();
		bytes = DataTransform.vlongToByte(num1);
		num2 = DataTransform.byteToVLong(bytes);
		Assert.assertTrue(num1 == num2);

	}

	@Test
	public void testHEX() {
		int i = Integer.MAX_VALUE;
		byte[] bytes1 = DataTransform.intToBytes(i, false);
		Assert.assertEquals("7fffffff", DataTransform.byteHEX(bytes1));

		long l = (long) Integer.MAX_VALUE;
		byte[] bytes2 = DataTransform.longToBytes(l, false);
		Assert.assertEquals("000000007fffffff", DataTransform.byteHEX(bytes2));

		l = (long) Integer.MAX_VALUE + 1L;
		bytes2 = DataTransform.longToBytes(l, false);
		Assert.assertEquals("0000000080000000", DataTransform.byteHEX(bytes2));
	}
	
	@Test
	public void testByteLiteral(){
		byte b=100;
		System.out.println(DataTransform.byteLiteral(b));
	}
}
