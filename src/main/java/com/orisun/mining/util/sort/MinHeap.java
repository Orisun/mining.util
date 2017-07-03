package com.orisun.mining.util.sort;

import java.util.ArrayList;
import java.util.List;

/**
 * 小根堆。可用于取topK、排序等。<br>
 * MinHeap相对于PriorityQueue的优势在于：PriorityQueue是不能设置容量上限的；MinHeap可设置容量上限带来两个好处，
 * 一是内存占用可控
 * ，二是提高添加和删除元素的速度（PriorityQueue和MinHeap增删元素时间复杂度都是logN，但是MinHeap中的N是有上限的
 * ，所以达到上限后MinHeap更快一些）
 * 
 * @Author:orisun
 * @Since:2016-5-25
 * @Version:1.0
 */
@SuppressWarnings("rawtypes")
public class MinHeap<T extends Comparable> {

	private List<T> data;
	private int maxSize;

	/**
	 * 
	 * @param maxSize
	 *            指定堆的最大容量
	 */
	public MinHeap(int maxSize) {
		data = new ArrayList<T>(maxSize);
		if (maxSize > 0) {
			this.maxSize = maxSize;
		}
	}

	public int size() {
		return data.size();
	}

	public boolean isEmpty() {
		return data.size() == 0;
	}

	/**
	 * 取出堆中的所有元素，亦即topK，但是不保证顺序<br>
	 * 注意取出的是容器的引用<br>
	 * 时间复杂度O(1)
	 * 
	 * @return
	 */
	public List<T> getTopK() {
		return data;
	}

	private void swap(int i, int j) {
		T tmp = data.get(j);
		data.set(j, data.get(i));
		data.set(i, tmp);
	}

	@SuppressWarnings("unchecked")
	private void heapDown(int i) {
		int l = (i + 1) * 2 - 1;// 左孩子的index
		int r = (i + 1) * 2;// 右孩子的index
		int smallest = i;// 父节点、左、右孩子中的最小者
		// 如果左孩子存在，且左孩子小于smallest
		if (l < data.size() && data.get(l).compareTo(data.get(smallest)) < 0) {
			smallest = l;
		}
		// 如果右孩子存在，且左孩子小于smallest
		if (r < data.size() && data.get(r).compareTo(data.get(smallest)) < 0) {
			smallest = r;
		}
		// 如果父节点本来就比左右孩子小，则直接返回
		if (i == smallest) {
			return;
		}
		// 父节点跟较小的那个子节点交换
		swap(i, smallest);
		// 交换后影响到了子树，所以对子树递归进行heapify
		heapDown(smallest);
	}

	@SuppressWarnings({ "unchecked" })
	private void heapUp(int i) {
		while (i > 0) {
			T ele = data.get(i);
			int p = (i - 1) / 2;// 父节点的index
			if (ele.compareTo(data.get(p)) < 0) {// 如果比父节点小，则跟父节点交换
				swap(i, p);
				i = p;
			} else { // 否则停止
				break;
			}
		}
	}

	/**
	 * 获取堆顶元素（即堆中最小的元素）<br>
	 * 时间复杂度O(1)
	 * 
	 * @return
	 */
	public T getRoot() {
		return data.get(0);
	}

	/**
	 * 向堆中添加一个元素<br>
	 * 时间复杂度O(logN)
	 * 
	 * @param ele
	 */
	@SuppressWarnings("unchecked")
	public void add(T ele) {
		int size = data.size();
		if (size == 0) {// 如果容器为空，则直接插入即可
			data.add(ele);
			return;
		}
		if (size < maxSize) {// 未达容量上限
			data.add(ele);// 直接插到末位置上
			heapUp(size);// 然后从末位置开始向上调整
		} else {// 如果已达容量上限
			if (ele.compareTo(data.get(0)) > 0) {// 如果不能比根节点大，则直接丢弃
				data.set(0, ele); // 替换根节点
				heapDown(0); // 然后从根节点开始向下调整
			}
		}
	}

	/**
	 * 删除堆顶元素<br>
	 * 时间复杂度O(logN)
	 */
	public void delRoot() {
		int size = data.size();
		if (size > 0) {
			T lastEle = data.get(size - 1);
			if (size > 1) {
				// 用末元素替换根节点
				data.set(0, lastEle);
				data.remove(size - 1);
				// 然后从根节点开始向下调整
				heapDown(0);
			} else {
				data.remove(size - 1);
			}
		}
	}

	/**
	 * 删除并返回堆顶元素。通过不断地poll，可以有序地取出堆中的所有元素<br>
	 * 时间复杂度O(logN)
	 * 
	 * @return
	 */
	public T poll() {
		T rect = null;
		int size = data.size();
		if (size > 0) {
			rect = getRoot();
			delRoot();
		}
		return rect;
	}
}
