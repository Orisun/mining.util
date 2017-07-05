package com.orisun.mining.util;

import com.orisun.mining.util.hash.SimpleHashFunction;
import org.junit.Test;
  
public class TestHashFunction {

	@Test
	public void testDEKHash(){
		String text="A51F092E-3C83-4D23-AE53-3AD1ACD0FA47";
		System.out.println(SimpleHashFunction.DEKHash(text));
		text="865982024623467";
		System.out.println(SimpleHashFunction.DEKHash(text));
	}
}
