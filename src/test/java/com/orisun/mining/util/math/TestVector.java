package com.orisun.mining.util.math;


import com.orisun.mining.util.exception.ArgumentException;
import com.orisun.mining.util.exception.DmArithmeticException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;


/**
 * 
 * 
 * @Author:zhangchaoyang
 * @Since:2014-7-9
 * @Version:
 */
public class TestVector {

	private static Vector vec1;
	private static Vector vec2;
	private static Vector vec3;

	@BeforeClass
	public static void setup() {
		double[] arr = new double[3];
		arr[0] = 1;
		arr[1] = 2;
		arr[2] = 3;
		vec1 = new Vector(arr);
		System.out.println("vec1=");
		System.out.println(vec1);
		double[] arr2 = new double[3];
		arr2[0] = 4;
		arr2[1] = 5;
		arr2[2] = 6;
		vec2 = new Vector(arr2);
		System.out.println("vec2=");
		System.out.println(vec2);
		vec3 = new Vector(4, 8, 16);
		System.out.println("vec3=");
		System.out.println(vec3);
	}

	@Test
	public void testContructor1() {
		System.out.println("构造了一个空向量：");
		System.out.println(new Vector(3));
	}

	@Test
	public void testContructor2() {
		System.out.println("构造了一个所有元素为8的向量：");
		System.out.println(new Vector(3, 8));
	}

	@Test
	public void testGetAndSet() {
		int index = 1;
		double orig = vec1.get(index);
		double data = 58.58;
		vec1.set(index, data);
		Assert.assertTrue(data==vec1.get(index));
		vec1.set(index, orig);
	}

	@Test
	public void testDotProduct() throws ArgumentException {
		System.out.println("两向量的点积=");
		System.out.println(vec1.dotProduct(vec2));
	}

	@Test
	public void testMultipleBy() {
		System.out.println("vec1乘以2=");
		double p = 2.0;
		System.out.println(vec1.multipleBy(p));
	}

	@Test
	public void testNorm2() {
		System.out.println("vec1的2范数=");
		System.out.println(vec1.norm2());
	}

	@Test
	public void testMean() throws DmArithmeticException {
		System.out.println("vec1的均值=");
		System.out.println(vec1.mean());
	}

	@Test
	public void testVariance() throws DmArithmeticException {
		System.out.println("vec1的方差=");
		System.out.println(vec1.meanAndVariance());
	}

	@Test
	public void testVariance2() throws DmArithmeticException {
		Vector vec1 = new Vector(10, 20, 30);
		System.out.println("vec1的方差=");
		System.out.println(vec1.meanAndVariance().second);
		Vector vec2 = new Vector(210, 220, 230);
		System.out.println("vec2的方差=");
		System.out.println(vec2.meanAndVariance().second);
	}

	@Test
	public void testToMatrix() {
		System.out.println("vec1转换成矩阵后：");
		vec1.toMatrix().print();
	}

	@Test
	public void testAdd() throws ArgumentException {
		System.out.println("vec1+vec2=");
		System.out.println(vec1.add(vec2));
	}

	@Test
	public void testSub() throws ArgumentException {
		System.out.println("vec2-vec1=");
		System.out.println(vec2.sub(vec1));
	}

	@Test
	public void testQuantile() throws DmArithmeticException {
		double[] arr = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		Vector vec = new Vector(arr);
		List<Double> quantile = vec.getQuantile(9);
		for (double ele : quantile) {
			System.out.println(ele);
		}
	}
	
	@Test
	public void testQuantile2() throws DmArithmeticException {
		double[] arr = new double[100];
		for(int i=0;i<100;i++){
			arr[i]=i;
		}
		Vector vec = new Vector(arr);
		List<Double> quantile = vec.getQuantile(9);
		for (double ele : quantile) {
			System.out.println(ele);
		}
	}
}
