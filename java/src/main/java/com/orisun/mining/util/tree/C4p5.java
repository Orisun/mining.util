package com.orisun.mining.util.tree;

import com.orisun.mining.util.enumeration.FileType;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.ArffLoader;
import weka.core.converters.CSVLoader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * C4.5决策树
 * 
 * @Author:zhangchaoyang
 * @Since:2015-1-12
 * @Version:1.0
 */
public class C4p5 {

	// -C置信水平，-M最小实例数，-U不剪枝
	private static final String OPTIONS = "-C 0.2";
	private J48 classifer = null;

	public void train(String trainFile, FileType filetype) throws Exception {
		classifer = new J48();
		classifer.setOptions(weka.core.Utils.splitOptions(OPTIONS));
		Instances instances = null;
		AbstractFileLoader loader = null;
		if (filetype == FileType.CSV) {
			loader = new CSVLoader();
		} else if (filetype == FileType.ARFF) {
			loader = new ArffLoader();
		}
		loader.setFile(new File(trainFile));
		instances = loader.getDataSet();
//		instances.deleteAttributeAt(0);// 删除首个属性
		instances.setClassIndex(instances.numAttributes() - 1);// 最后一个属性是类标签
		classifer.buildClassifier(instances);
	}

	public double predict(Instance instance) throws Exception {
		if (classifer != null) {
			return classifer.classifyInstance(instance);
		} else {
			return -1;
		}
	}

	public void predict(String testFile, FileType filetype) throws Exception {
		if (classifer != null) {
			Instances instances = null;
			AbstractFileLoader loader = null;
			if (filetype == FileType.CSV) {
				loader = new CSVLoader();
			} else if (filetype == FileType.ARFF) {
				loader = new ArffLoader();
			}
			loader.setFile(new File(testFile));
			instances = loader.getDataSet();
			instances.deleteAttributeAt(0);
			instances.setClassIndex(instances.numAttributes() - 1);

			Attribute attribute = instances
					.attribute(instances.numAttributes() - 1);

			String basePath = C4p5.class.getResource("").getPath();
			File outFile = new File(basePath + "/batch_test_result");
			System.out.println(outFile.getAbsolutePath());
			BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
			for (int i = 0; i < instances.size(); i++) {
				Instance instance = instances.get(i);
				double result = classifer.classifyInstance(instance);
				if (result != instance.classValue()) {
					// System.err.println(instance.toString() + "\t"
					// + attribute.value((int) result));
//					String[] arr = instance.toString().split(",");
//					if (Integer.parseInt(arr[0]) < 10) {
						if ("WATER".equals(attribute.value((int) result))) {
							bw.write(instance.toString());
							bw.newLine();
						}
//					}
				}
			}
			bw.close();
		}
	}
}
