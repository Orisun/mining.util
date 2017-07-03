package com.orisun.mining.util.serializer;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.NotSerializableException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TestJavaSerializer {

	@SuppressWarnings("unchecked")
	@Test
	public void testCopy() throws ClassNotFoundException, IOException {
		List<String> list0 = new LinkedList<String>();
		list0.add("A");
		list0.add("B");
		System.out.println(list0);
		List<String> list1 = (List<String>) JavaSerializer.deepCopy(list0);
		System.out.println(list1);

		List<String> list2 = null;
		System.out.println(list2);
		List<String> list3 = (List<String>) JavaSerializer.deepCopy(list2);
		System.out.println(list3);

		List<String> list4 = new ArrayList<String>();
		System.out.println(list4);
		List<String> list5 = (List<String>) JavaSerializer.deepCopy(list4);
		System.out.println(list5);

		List<String> list6 = new LinkedList<String>();
		list6.add("A");
		list6.add("B");
		list6 = list6.subList(0, 1);// 经过subList后，不能序列化
		try {
			JavaSerializer.deepCopy(list6);
			Assert.assertTrue(false);
		} catch (NotSerializableException e) {
			Assert.assertTrue(true);
		}
	}
}
