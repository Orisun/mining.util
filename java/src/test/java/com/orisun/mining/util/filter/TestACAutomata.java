package com.orisun.mining.util.filter;

import com.orisun.mining.util.text.AhoCorasickDoubleArrayTrie;
import com.orisun.mining.util.text.Hit;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * @Author: Fido
 * @Date: 22/9/2016
 */
public class TestACAutomata {

	@Test
	public void testACTrie() {
		TreeMap<String, String> map = new TreeMap<>();
		String[] keyArray = new String[] { "hers", "his", "she", "he" };
		for (String key : keyArray) {
			map.put(key, key);
		}
		AhoCorasickDoubleArrayTrie<String> act = new AhoCorasickDoubleArrayTrie<>();
		try {
			act.build(map);
			act.parseText("uhers", new AhoCorasickDoubleArrayTrie.IHit<String>() {
				@Override
				public void hit(int begin, int end, String value) {
					System.out.printf("[%d:%d]=%s\n", begin, end, value);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test2(){
		Set<String> keywords = new HashSet<String>();
		keywords.add("有数金融");
		keywords.add("杭州有好数据科技有限公司");
		keywords.add("A");
		keywords.add("B");
		AhoCorasickDoubleArrayTrie<String> act = new AhoCorasickDoubleArrayTrie<>();
		try {
			act.build(keywords);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<Hit<String>> hits = act.parseText("杭州有数金融信息服务有限公司AB");
		for (Hit<String> hit : hits) {
			System.out.println(hit.getValue());
		}
	}
}
