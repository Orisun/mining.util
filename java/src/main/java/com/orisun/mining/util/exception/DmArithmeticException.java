package com.orisun.mining.util.exception;

/**
 * 自定义的数值计算异常。在发现可能抛出{@link java.lang.ArithmeticException}前就抛出该异常。
 *               {@link java.lang.ArithmeticException}继承自
 *               {@link java.lang.RuntimeException},而此处的DmArithmeticException继承自
 *               {@link java.lang.Exception}
 *
 *@Author:zhangchaoyang 
 *@Since:2014-7-9  
 *@Version:
 */
public class DmArithmeticException extends Exception {

	private static final long serialVersionUID = 4698848072229455481L;

	public DmArithmeticException(String str) {
		super(str);
	}
}
