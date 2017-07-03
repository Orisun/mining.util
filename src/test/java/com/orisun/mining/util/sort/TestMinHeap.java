package com.orisun.mining.util.sort;

import org.junit.Test;

import java.util.Random;
  
public class TestMinHeap {

	@Test
	public void testTopK(){
		MinHeap<Integer> minHeap = new MinHeap<Integer>(10);
		Random rnd=new Random();
		for(int i=0;i<100;i++){
			int num=rnd.nextInt(100);
//			System.out.println(num);
			minHeap.add(num);
		}
		System.out.println("===========");
		System.out.println("root:"+minHeap.getRoot());
		for(Integer i:minHeap.getTopK()){
			System.out.println(i);
		}
		System.out.println("===========");
		
		System.out.println("size:"+minHeap.size());
		for(int i=0;i<100;i++){
			int num=rnd.nextInt(100);
//			System.out.println(num);
			minHeap.add(num);
		}
		System.out.println("===========");
		System.out.println("root:"+minHeap.getRoot());
		for(Integer i:minHeap.getTopK()){
			System.out.println(i);
		}
		minHeap.delRoot();
		System.out.println("after delRoot");
		for(Integer i:minHeap.getTopK()){
			System.out.println(i);
		}
		System.out.println("===========");
		while(!minHeap.isEmpty()){
			System.out.println(minHeap.poll());
		}
	}
}
