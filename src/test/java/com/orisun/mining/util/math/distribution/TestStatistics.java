package com.orisun.mining.util.math.distribution;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestStatistics {
	@Test
	public void test() {
		System.out.println("Gini系数\t方差");
		Statistics inst = new Statistics();
		List<Double> list = new ArrayList<Double>();
		list.add(0.1);
		list.add(0.2);
		list.add(0.3);
		list.add(0.4);
		list.add(0.5);
		list.add(0.6);
		list.add(0.7);
		System.out.println(inst.getGini(list) + "\t" + inst.getMeanAndVariance(list).second);
		System.out.println(inst.getQuantile(list, 2));
		
		list.clear();
		list.add(0.0);
		list.add(0.0);
		list.add(0.0);
		list.add(0.1);
		list.add(0.2);
		list.add(0.3);
		list.add(0.4);
		list.add(0.5);
		list.add(0.6);
		list.add(0.7);
		System.out.println(inst.getGini(list) + "\t" + inst.getMeanAndVariance(list).second);
		
		list.clear();
		list.add(0.0);
		list.add(0.0);
		list.add(0.0);
		System.out.println(inst.getGini(list) + "\t" + inst.getMeanAndVariance(list).second);

		list.clear();
		list.add(0.1);
		list.add(0.1);
		list.add(0.1);
		System.out.println(inst.getGini(list) + "\t" + inst.getMeanAndVariance(list).second);

		list.clear();
		list.add(0.1);
		list.add(0.1);
		list.add(0.1);
		list.add(0.1);
		list.add(0.1);
		list.add(0.1);
		System.out.println(inst.getGini(list) + "\t" + inst.getMeanAndVariance(list).second);

		list.clear();
		System.out.println(inst.getGini(list) + "\t" + inst.getMeanAndVariance(list).second);
	}
}
