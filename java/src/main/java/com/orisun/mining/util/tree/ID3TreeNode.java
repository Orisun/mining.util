package com.orisun.mining.util.tree;

import java.util.List;

public class ID3TreeNode {
	private int attrbuteIndexToDecide; // 当前节点是依赖第几个属性进行决策
	private int valueIndexFromParent; // 从父节点到本节点是取的父节点的第几个值
	private int resultIndex = -1; // 在当前节点上作决策时应该取第几个决策变量。如果在当前节点上不能作出决策，则resultIndex为负数
	private List<ID3TreeNode> children;// 孩子节点。按顺序取第attrbuteIndexToDecide个属性的各个值时，依次对应的各个孩子节点

	public ID3TreeNode() {

	}

	public int getAttrbuteIndexToDecide() {
		return attrbuteIndexToDecide;
	}

	public void setAttrbuteIndexToDecide(int attrbuteIndexToDecide) {
		this.attrbuteIndexToDecide = attrbuteIndexToDecide;
	}

	public int getValueIndexFromParent() {
		return valueIndexFromParent;
	}

	public void setValueIndexFromParent(int valueIndexFromParent) {
		this.valueIndexFromParent = valueIndexFromParent;
	}

	public int getResultIndex() {
		return resultIndex;
	}

	public void setResultIndex(int resultIndex) {
		this.resultIndex = resultIndex;
	}

	public List<ID3TreeNode> getChildren() {
		return children;
	}

	public void setChildren(List<ID3TreeNode> children) {
		this.children = children;
	}

}
