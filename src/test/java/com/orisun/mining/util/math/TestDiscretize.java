package com.orisun.mining.util.math;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestDiscretize {

	@Test
	public void testGetIndexOfBin() {
		long[] splitPoints = new long[] { 1600, 2500, 3200, 3800, 4400, 5000, 5700, 6300, 7000, 7800, 8700, 9500, 10500,
				11500, 12600, 13800, 15100, 16500, 17900, 19400, 20900, 22600, 24300, 26100, 28100, 30100, 32300, 34600,
				37000, 39700, 42500, 45600, 48900, 52400, 56300, 60700, 65400, 70400, 75600, 81700, 89200, 98000,
				108900, 124100, 145200, 174200, 219700, 310500, 550300, 1798000 };
		List<Double> splitPoint = new ArrayList<Double>();
		for (long ele : splitPoints) {
			splitPoint.add(1.0 * ele);
		}
		for (int i = 0; i < splitPoints.length; i++) {
			long ele = splitPoints[i];
			int index = Discretize.getIndexOfBin(splitPoint, 1.0 * ele);
			Assert.assertTrue(i == index);
		}
		for (int i = 0; i < splitPoints.length - 1; i++) {
			long ele = splitPoints[i] + 1;
			int index = Discretize.getIndexOfBin(splitPoint, 1.0 * ele);
			Assert.assertTrue((i + 1) == index);
		}
		for (int i = 0; i < splitPoints.length; i++) {
			long ele = splitPoints[i] - 1;
			int index = Discretize.getIndexOfBin(splitPoint, 1.0 * ele);
			Assert.assertTrue(i == index);
		}
	}

	@Test
	public void testGetIndexOfBin2() {
		long[] splitPoints = new long[] { 2, 4, 6 };
		List<Double> splitPoint = new ArrayList<Double>();
		for (long ele : splitPoints) {
			splitPoint.add(1.0 * ele);
		}
		for (int i = 1; i < 8; i++) {
			System.out.println(i + "\t" + Discretize.getIndexOfBin(splitPoint, i));
		}
	}
	
	@Test
	public void testBinConstwidth() {
		List<Double> data = new ArrayList<Double>();
		data.add(2.0);
		data.add(4.0);
		data.add(6.0);
		data.add(1.0);
		data.add(3.0);
		data.add(5.0);
		data.add(9.0);
		data.add(9.0);
		data.add(9.0);
		data.add(9.0);
		data.add(9.0);
		data.add(9.0);
		List<Double> splitPoint = Discretize.binConstWidth(data, 2);
		for (double ele : splitPoint) {
			System.out.println(ele);
		}
	}

	@Test
	public void testBinConstDepth() {
		List<Double> data = new ArrayList<Double>();
		data.add(2.0);
		data.add(4.0);
		data.add(6.0);
		data.add(1.0);
		data.add(3.0);
		data.add(5.0);
		data.add(9.0);
		data.add(9.0);
		data.add(9.0);
		data.add(9.0);
		data.add(9.0);
		data.add(9.0);
		List<Double> splitPoint = Discretize.binConstDepth(data, 2);
		for (double ele : splitPoint) {
			System.out.println(ele);
		}
	}
}
