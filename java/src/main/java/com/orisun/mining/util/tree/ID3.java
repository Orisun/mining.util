package com.orisun.mining.util.tree;

import com.orisun.mining.util.ARFF;
import com.orisun.mining.util.math.CommonMath;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ID3 {
	private ArrayList<String> attribute; // 存储属性的名称
	private ArrayList<ArrayList<String>> attributevalue; // 存储每个属性的取值，ID3算法只能处理每个属性都是离散变量的情况
	private List<String[]> data; // 原始数据
	String relationName;// 关系名
	int decideAttrIndex = -1; // 决策变量在属性集中的索引
	ID3TreeNode rootNode = new ID3TreeNode();// 决策树的根节点

	Document xmldoc;
	Element xmlroot;

	public ID3() {
		xmldoc = DocumentHelper.createDocument();
	}

	private boolean notNull() {
		if (attribute == null || attribute.size() == 0) {
			System.err.println("没有给attribute赋值");
			return false;
		}
		if (attributevalue == null || attributevalue.size() == 0) {
			System.err.println("没有给attributevalue赋值");
			return false;
		}
		if (data == null || data.size() == 0) {
			System.err.println("没有给data赋值");
			return false;
		}
		if (decideAttrIndex < 0) {
			System.err.println("没有指定哪一列是标签属性");
			return false;
		}
		return true;
	}

	/**
	 * 设置决策变量
	 * 
	 * @param n
	 *            决策变量在属性中的索引
	 */
	public void setDec(int n) {
		if (n < 0 || n >= attribute.size()) {
			System.err.println("决策变量指定错误。");
			System.exit(2);
		}
		decideAttrIndex = n;
	}

	/**
	 * 设置决策变量
	 * 
	 * @param name
	 *            决策变量的名称
	 */
	public void setDec(String name) {
		int n = attribute.indexOf(name);
		setDec(n);
	}

	/**
	 * 给定的数据子集的决策变量取值都相同
	 * 
	 * @param subset
	 * @return
	 */
	private boolean infoPure(List<Integer> subset) {
		String value = data.get(subset.get(0))[decideAttrIndex];
		for (int i = 1; i < subset.size(); i++) {
			String next = data.get(subset.get(i))[decideAttrIndex];
			if (!value.equals(next))
				return false;
		}
		return true;
	}

	/**
	 * 给定原始数据的子集(subset中存储行号),当以第index个属性为节点时计算它的信息熵
	 * 
	 * @param subset
	 * @param index
	 * @return
	 */
	private double calNodeEntropy(List<Integer> subset, int index) {
		int sum = subset.size();
		double entropy = 0.0;
		int[][] info = new int[attributevalue.get(index).size()][];
		for (int i = 0; i < info.length; i++)
			info[i] = new int[attributevalue.get(decideAttrIndex).size()];
		int[] count = new int[attributevalue.get(index).size()];
		for (int i = 0; i < sum; i++) {
			int n = subset.get(i);
			String nodevalue = data.get(n)[index];
			int nodeind = attributevalue.get(index).indexOf(nodevalue);
			count[nodeind]++;
			String decvalue = data.get(n)[decideAttrIndex];
			int decind = attributevalue.get(decideAttrIndex).indexOf(decvalue);
			info[nodeind][decind]++;
		}
		for (int i = 0; i < info.length; i++) {
			entropy += CommonMath.getEntropy(info[i]) * count[i] / sum;
		}
		return entropy;
	}

	/**
	 * 递归树生长
	 * 
	 * @param root
	 *            当前子树的根节点
	 * @param dataset
	 *            当前可供使用的数据集
	 * @param attr
	 *            当前可供决策的属性
	 */
	private void treeGrowth(ID3TreeNode root, List<Integer> dataset,
			List<Integer> attr) {

		if (root == null || dataset.size() == 0 || attr.size() == 0) {
			return;
		}

		/**
		 * 节点已纯，即抵达叶节点，设置该节点上的决策取值
		 */
		if (infoPure(dataset)) {
			String resultStr = data.get(dataset.get(0))[decideAttrIndex];
			ArrayList<String> decisionValues = attributevalue
					.get(decideAttrIndex);
			root.setResultIndex(decisionValues.indexOf(resultStr));
			return;
		}

		/**
		 * 计算在第几个属性上决策时，熵最小，即信息增益最大
		 */
		int minIndex = -1;
		double minEntropy = Double.MAX_VALUE;
		for (int i = 0; i < attr.size(); i++) {
			double entropy = calNodeEntropy(dataset, attr.get(i));
			if (entropy < minEntropy) {
				minIndex = attr.get(i);
				minEntropy = entropy;
			}
		}
		root.setAttrbuteIndexToDecide(minIndex);

		attr.remove(new Integer(minIndex));
		List<ID3TreeNode> children = new ArrayList<ID3TreeNode>();
		ArrayList<String> attvalues = attributevalue.get(minIndex);
		for (int i = 0; i < attvalues.size(); i++) {
			String val = attvalues.get(i);
			ID3TreeNode childNode = new ID3TreeNode();
			childNode.setValueIndexFromParent(i);

			ArrayList<Integer> al = new ArrayList<Integer>();
			for (int j = 0; j < dataset.size(); j++) {
				if (data.get(dataset.get(j))[minIndex].equals(val)) {
					al.add(dataset.get(j));
				}
			}
			children.add(childNode);
			treeGrowth(childNode, al, attr);
		}
		root.setChildren(children);
	}

	/**
	 * 构建决策树
	 * 
	 * @param attr
	 *            指定哪结属性要用于决策（即去除决策属性剩下的所有其他属性）
	 */
	public void buildTree(List<Integer> attr) {
		if (notNull()) {
			List<Integer> dataset = new ArrayList<Integer>();
			for (int i = 0; i < data.size(); i++) {
				dataset.add(i);
			}
			treeGrowth(rootNode, dataset, attr);
		}
	}

	/**
	 * 对一条新的数据，用决策树来进行决策
	 * 
	 * @param newdata
	 *            数据各维度的意义必须和训练数据保持一致，标签列可随便设一个值
	 * @return
	 */
	public int decide(String[] newdata) {
		int rect = -1;
		if (notNull()) {
			ID3TreeNode currNode = rootNode;
			while (currNode.getResultIndex() < 0
					&& currNode.getChildren() != null) {
				int attrbuteIndexToDecide = currNode.getAttrbuteIndexToDecide();
				String dataValue = newdata[attrbuteIndexToDecide];
				List<String> allValues = attributevalue
						.get(attrbuteIndexToDecide);
				for (int i = 0; i < allValues.size(); i++) {
					String value = allValues.get(i);
					if (dataValue.equals(value)) {
						currNode = currNode.getChildren().get(i);
						break;
					}
				}
			}
			rect = currNode.getResultIndex();
		}
		return rect;
	}

	/**
	 * 把决策树输出到XML文件
	 * 
	 * @param outfilename
	 */
	public void outputTreeAsXML(String outfilename) {
		xmlroot = xmldoc.addElement("root");
		Element element = xmlroot.addElement("DecisionTree").addAttribute(
				"value", relationName);
		xmlGrow(element, rootNode);

		try {
			File file = new File(outfilename);
			if (!file.exists())
				file.createNewFile();
			FileWriter fw = new FileWriter(file);
			OutputFormat format = OutputFormat.createPrettyPrint(); // 美化格式
			XMLWriter output = new XMLWriter(fw, format);
			output.write(xmldoc);
			output.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private void xmlGrow(Element xmlnode, ID3TreeNode treenode) {
		assert treenode != null;

		if (treenode.getResultIndex() >= 0) {
			xmlnode.setText(attributevalue.get(decideAttrIndex).get(
					treenode.getResultIndex()));
			return;
		}

		assert treenode.getChildren() != null;

		int attrIndex = treenode.getAttrbuteIndexToDecide();
		String nodeName = attribute.get(attrIndex);
		for (ID3TreeNode node : treenode.getChildren()) {
			int valueIndex = node.getValueIndexFromParent();
			String valueName = attributevalue.get(attrIndex).get(valueIndex);
			Element element = xmlnode.addElement(nodeName).addAttribute(
					"value", valueName);
			xmlGrow(element, node);
		}
	}

	public ArrayList<String> getAttribute() {
		return attribute;
	}

	public void setAttribute(ArrayList<String> attribute) {
		this.attribute = attribute;
	}

	public ArrayList<ArrayList<String>> getAttributevalue() {
		return attributevalue;
	}

	public void setAttributevalue(ArrayList<ArrayList<String>> attributevalue) {
		this.attributevalue = attributevalue;
	}

	public List<String[]> getData() {
		return data;
	}

	public void setData(List<String[]> data) {
		this.data = data;
	}

	public String getRelationName() {
		return relationName;
	}

	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}

	public int getDecideAttrIndex() {
		return decideAttrIndex;
	}

	public void setDecideAttrIndex(int decideAttrIndex) {
		this.decideAttrIndex = decideAttrIndex;
	}

	public ID3TreeNode getRootNode() {
		return rootNode;
	}

	public void setRootNode(ID3TreeNode rootNode) {
		this.rootNode = rootNode;
	}

	public static void main(String[] args) {
		String file = "data/phone.arff"; // 原始训练文件，arff格式
		ARFF arff = new ARFF(file);
		ID3 inst = new ID3();
		inst.setAttribute(arff.getAttribute());
		inst.setAttributevalue(arff.getAttributevalue());
		inst.setData(arff.getData());
		inst.setRelationName(arff.getRelationName());
		inst.setDec("welcome"); // " 是否受欢迎"为待预测的变量
		LinkedList<Integer> ll = new LinkedList<Integer>();
		for (int i = 0; i < inst.getAttribute().size(); i++) {
			if (i != inst.getDecideAttrIndex())
				ll.add(i);
		}

		inst.buildTree(ll); // 训练学习生成ID3决策树
		inst.outputTreeAsXML("tree.xml");// 将决策树输出到XML文件

		/**
		 * 使用训练好的ID3决策对预测一款新手机是否受欢迎
		 */
		String[] data = new String[] { "medium", "long", "normal", "adequate",
				"" };// 屏幕大小 ，待机时长， 分辨率， 软件量， 是否受欢迎
		int cls = inst.decide(data);
		System.out.println(inst.getAttributevalue()
				.get(inst.getDecideAttrIndex()).get(cls));
	}
}
