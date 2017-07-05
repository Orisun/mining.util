package com.orisun.mining.util.sort;

import com.orisun.mining.util.VInt;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestBinarySearch {

	@Test
	public void test() {
		List<VInt> list = new ArrayList<VInt>();
		list.add(new VInt(1)); // 0
		list.add(new VInt(2)); // 1
		list.add(new VInt(3)); // 2
		list.add(new VInt(3)); // 3
		list.add(new VInt(3)); // 4
		list.add(new VInt(3)); // 5
		list.add(new VInt(3)); // 6
		list.add(new VInt(3)); // 7
		list.add(new VInt(4)); // 8
		list.add(new VInt(5)); // 9
		list.add(new VInt(6)); // 10
		list.add(new VInt(6)); // 11
		list.add(new VInt(6)); // 12
		list.add(new VInt(7)); // 13
		list.add(new VInt(8)); // 14
		list.add(new VInt(9)); // 15
		list.add(new VInt(9)); // 16

		Collections.sort(list);
		int index = BinarySearch.search(list, new VInt(5));
		Assert.assertEquals(index, 9);
		index = BinarySearch.search(list, new VInt(3));
		Assert.assertEquals(index, 3);
		index = BinarySearch.search(list, new VInt(6));
		Assert.assertEquals(index, 12);
		index = BinarySearch.search(list, new VInt(9));
		Assert.assertEquals(index, 15);
		index = BinarySearch.search(list, new VInt(10));
		Assert.assertTrue(index < 0);
		index = BinarySearch.search(list, new VInt(0));
		Assert.assertTrue(index < 0);
	}

	@Test
	public void testSearchNearest() {
		List<VInt> list = new ArrayList<VInt>();
		list.add(new VInt(1)); // 0
		list.add(new VInt(2)); // 1
		list.add(new VInt(3)); // 2
		list.add(new VInt(3)); // 3
		list.add(new VInt(3)); // 4
		list.add(new VInt(3)); // 5
		list.add(new VInt(3)); // 6
		list.add(new VInt(3)); // 7
		list.add(new VInt(4)); // 8
		list.add(new VInt(5)); // 9
		list.add(new VInt(6)); // 10
		list.add(new VInt(6)); // 11
		list.add(new VInt(6)); // 12
		list.add(new VInt(7)); // 13
		list.add(new VInt(8)); // 14
		list.add(new VInt(9)); // 15
		list.add(new VInt(9)); // 16

		Collections.sort(list);
		int index = BinarySearch.searchNearest(list, new VInt(0));
		Assert.assertEquals(index, 0);
		index = BinarySearch.searchNearest(list, new VInt(1));
		Assert.assertEquals(index, 0);
		index = BinarySearch.searchNearest(list, new VInt(6));
		Assert.assertEquals(index, 12);
		index = BinarySearch.searchNearest(list, new VInt(11));
		Assert.assertEquals(index, 16);
	}

	@Test
	public void testSection() {
		List<Double> list = new ArrayList<Double>();
		list.add(1.0);
		list.add(3.0);
		list.add(6.0);
		list.add(10.0);
		Collections.sort(list);
		int index = BinarySearch.searchSection(list, 1.0);
		Assert.assertEquals(index, 0);
		index = BinarySearch.searchSection(list, 3.0);
		Assert.assertEquals(index, 1);
		index = BinarySearch.searchSection(list, 6.0);
		Assert.assertEquals(index, 2);
		index = BinarySearch.searchSection(list, 10.0);
		Assert.assertEquals(index, 3);
		index = BinarySearch.searchSection(list, -1.0);
		Assert.assertEquals(index, 0);
		index = BinarySearch.searchSection(list, 1.5);
		Assert.assertEquals(index, 1);
		index = BinarySearch.searchSection(list, 3.5);
		Assert.assertEquals(index, 2);
		index = BinarySearch.searchSection(list, 6.5);
		Assert.assertEquals(index, 3);
		index = BinarySearch.searchSection(list, 11.5);
		Assert.assertEquals(index, 4);
	}
}
