package com.orisun.mining.util.math;

import com.orisun.mining.util.exception.ArgumentException;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * 
 * 
 * @Author zhangchaoyang
 * @Since 2014-7-9
 * @Version 1.0
 */
public class TestMatrix {

	private static Matrix m1;
	private static Matrix m2;

	@BeforeClass
	public static void setup() {
		double[][] arr = new double[3][];
		arr[0] = new double[3];
		arr[0][0] = 1;
		arr[0][1] = 2;
		arr[0][2] = 3;
		arr[1] = new double[3];
		arr[1][0] = 4;
		arr[1][1] = 5;
		arr[1][2] = 6;
		arr[2] = new double[3];
		arr[2][0] = 7;
		arr[2][1] = 8;
		arr[2][2] = 9;
		double[][] arr2 = new double[3][];
		arr2[0] = new double[3];
		arr2[0][0] = 6;
		arr2[0][1] = 5;
		arr2[0][2] = 4;
		arr2[1] = new double[3];
		arr2[1][0] = 3;
		arr2[1][1] = 2;
		arr2[1][2] = 1;
		arr2[2] = new double[3];
		arr2[2][0] = 9;
		arr2[2][1] = 8;
		arr2[2][2] = 7;
		m1 = new Matrix(arr);
		m2 = new Matrix(arr2);
		System.out.println("m1=");
		m1.print();
		System.out.println("m2=");
		m2.print();
	}

	@Test
	public void testConstructor() {
		System.out.println("构造了个3x3的矩阵：");
		new Matrix(3, 3).print();
	}

	@Test
	public void testIdentity() {
		System.out.println("构造了个3阶单位矩阵：");
		Matrix.identity(3).print();
	}

	@Test
	public void testZero() {
		System.out.println("构造了个3阶零矩阵：");
		Matrix.zero(3).print();
	}

	@Test
	public void testAdd() throws ArgumentException {
		System.out.println("m1+m2=：");
		m1.add(m2).print();
	}

	@Test
	public void testSub() throws ArgumentException {
		System.out.println("m2-m1=：");
		m2.sub(m1).print();
	}

	@Test
	public void testTransport() {
		System.out.println("m1的转置：");
		m1.transport().print();
	}

	@Test
	public void testDotProduct() throws ArgumentException {
		System.out.println("m1*m2=：");
		m1.dotProduct(m2).print();
	}

	@Test
	public void testMultipleBy() {
		System.out.println("0.1*m1=：");
		double p = 0.1;
		m1.multipleBy(p).print();
	}

	@Test
	public void testNorm2() {
		System.out.println("m1的2范数：");
		System.out.println(m1.norm2());
	}

	@Test
	public void testGetCol() {
		System.out.println("m1的第2列：");
		System.out.println(m1.getCol(1));
	}

	@Test
	public void testGetRow() {
		System.out.println("m1的第2行：");
		System.out.println(m1.getRow(1));
	}
}
