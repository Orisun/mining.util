package com.orisun.mining.util.similarity;

import com.orisun.mining.util.similarity.SequenceSim.SimWay;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
  
public class TestSeqSim {

	@Test
	public void testLcs1(){
		String str1 = "abcd6ef";
		String str2 = "ef6abcd";
		String common=SequenceSim.longestCommonSubsequence1(str1, str2);
		Assert.assertEquals(common, "abcd");
		common=SequenceSim.longestCommonSubsequence1(str1, str1);
		Assert.assertEquals(common, str1);
		common=SequenceSim.longestCommonSubsequence1(str1, "");
		Assert.assertEquals(common, "");
	}
	
	@Test
	public void testLcs2(){
		String str1 = "abcd6ef";
		String str2 = "eaf6cbde";
		String common=SequenceSim.longestCommonSubsequence2(str1, str2);
		Assert.assertEquals(common, "acde");
		common=SequenceSim.longestCommonSubsequence2(str1, str1);
		Assert.assertEquals(common, str1);
		common=SequenceSim.longestCommonSubsequence2(str1, "");
		Assert.assertEquals(common, "");
	}
	
	@Test
	public void testEditDist() {
		String str1 = "a1b1c1de";
		String str2 = "22a2b2c2d";
		List<Character> list1 = new ArrayList<Character>();
		List<Character> list2 = new ArrayList<Character>();
		for (char c : str1.toCharArray()) {
			list1.add(c);
		}
		for (char c : str2.toCharArray()) {
			list2.add(c);
		}
		int dist = SequenceSim.getEditDistance(list1, list2);
		Assert.assertEquals(dist, 6);
		double sim = SequenceSim.getSim(list1, list2, SimWay.EDIT);
		Assert.assertTrue(Math.abs(sim - 0.25) < 1E-5);
	}
}
