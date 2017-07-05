package com.orisun.mining.util.math;  
  
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * 
 * @Author:zhangchaoyang
 * @Since:2014-7-9
 * @Version:
 */
public class TestNormalize {

	private static Vector vec;

	@BeforeClass
	public static void setup() {
		double[] arr = new double[3];
		arr[0] = 4;
		arr[1] = 5;
		arr[2] = 6;
		vec = new Vector(arr);
		System.out.println("vec=");
		System.out.println(vec);
	}

	@Test
	public void testGauss() {
		Vector nv = Normalize.gaussConvert(vec);
		System.out.println("Gauss Normalize:" + nv);
	}

	@Test
	public void testLinear() {
		Vector nv = Normalize.linearConvert(vec);
		System.out.println("Linear Normalize:" + nv);
	}

	@Test
	public void testLog() {
		Vector nv = Normalize.logConvert(vec);
		System.out.println("Log Normalize:" + nv);
	}

	@Test
	public void testArctan() {
		Vector nv = Normalize.arctanConvert(vec);
		System.out.println("Arctan Normalize:" + nv);
	}
}

