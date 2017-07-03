package com.orisun.mining.util.correlation;

import com.orisun.mining.util.exception.ArgumentException;
import org.junit.Test;
  
public class TestPearson {

	@Test
	public void test(){
		double[]x=new double[]{2134,3456,56,76,76,6};
		double[]y=new double[]{4,32,534,564,63,5475};
		double coef;
		try {
			coef = Pearson.corrcoef(x, y);
			System.out.println(coef);
		} catch (ArgumentException e) {
			e.printStackTrace();
		}
		
	}
}
