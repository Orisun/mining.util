package com.orisun.mining.util.regression;

import com.orisun.mining.util.exception.ArgumentException;
import com.orisun.mining.util.math.optimization.L_BFGS;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class TestLR {

	@Test
	public void testHRScore() throws IOException, ArgumentException {
		long start = System.currentTimeMillis();
		String trainDataFile = "D:\\workspace_lagou\\flow_balance\\flow.train";
		String trainLabelFile = "D:\\workspace_lagou\\flow_balance\\tag.train";
		LogRegression lr = new LogRegression();
		L_BFGS optimization = new L_BFGS();
		optimization.setEps(1E-8);
		lr.setMinOptimization(optimization);
		lr.fit(trainDataFile, trainLabelFile);

		String testDataFile = "D:\\workspace_lagou\\flow_balance\\flow.test";
		String testLabelFile = "D:\\workspace_lagou\\flow_balance\\tag.test";
		String line = null;
		List<Integer> testLabels = new LinkedList<Integer>();
		List<Double> predictLabels = new LinkedList<Double>();
		double mes = 0.0;
		BufferedReader br = new BufferedReader(new FileReader(testLabelFile));
		while ((line = br.readLine()) != null) {
			testLabels.add(Integer.parseInt(line.trim()));
		}
		br.close();
		br = new BufferedReader(new FileReader(testDataFile));
		while ((line = br.readLine()) != null) {
			String[] arr = line.trim().split("\\s+");
			double[] x = new double[arr.length];
			for (int i = 0; i < arr.length; i++) {
				x[i] = Double.parseDouble(arr[i]);
			}
			double p = lr.predict(x);
			predictLabels.add(p);
		}
		br.close();

		int totalSize = testLabels.size();
		assert totalSize == predictLabels.size();
		int errNum = 0;
		for (int i = 0; i < totalSize; i++) {
			double err = Math.abs(predictLabels.get(i) - testLabels.get(i));
			mes += Math.pow(err, 2);
			if (err > 0.5) {
				errNum++;
			}
		}
		mes = Math.sqrt(mes / totalSize);// 均方误差
		double precision = 1.0 * (totalSize - errNum) / totalSize;
		long end = System.currentTimeMillis();
		System.out.println("Use Time:" + (end - start) / 1000 + "s");
		System.out.println("Precision=" + precision);
		System.out.println("MES=" + mes);
		System.out.print("W=");
		double[] weight = lr.getWight();
		for (int i = 0; i < weight.length; i++) {
			System.out.print(weight[i] + "\t");
		}
		System.out.println();
	}
}
