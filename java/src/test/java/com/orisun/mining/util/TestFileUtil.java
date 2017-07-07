package com.orisun.mining.util;

import org.junit.Assert;
import org.junit.Test;

public class TestFileUtil {

	private static byte[] arr = "随机森林的每一棵决策树之间是没有关联的。在得到森林之后，当有一个新的输入样本进入的时候，就让森林中的每一棵决策树分别进行一下判断，看看这个样本应该属于哪一类（对于分类算法），然后看看哪一类被选择最多，就预测这个样本为那一类。random forest对输入的数据要进行行、列的采样。对于行采样，采用有放回的方式，也就是在采样得到的样本集合中，可能有重复的样本。假设输入样本为N个，那么采样的样本也为N个。这样使得在训练的时候，每一棵树的输入样本都不是全部的样本，使得相对不容易出现over-fitting。然后进行列采样，从M个feature中，选择m个(m << M)。之后就是对采样之后的数据使用完全分裂的方式建立出决策树，这样决策树的某一个叶子节点要么是无法继续分裂的，要么里面的所有样本的都是指向的同一个分类。一般很多的决策树算法都一个重要的步骤 - 剪枝，但是这里不这样干，由于之前的两个随机采样的过程保证了随机性，所以就算不剪枝，也不会出现over-fitting。原始的Boost算法是在算法开始的时候，为每一个样本赋上一个权重值，初始的时候，大家都是一样重要的。在每一步训练中得到的模型，会使得数据点的估计有对有错，我们就在每一步结束后，增加分错的点的权重，减少分对的点的权重。等进行了N次迭代（由用户指定），将会得到N个简单的分类器（basic learner），然后我们将它们组合起来（比如说可以对它们进行加权、或者让它们进行投票等），得到一个最终的模型。而Gradient Boost与传统的Boost的区别是，每一次的计算是为了减少上一次的残差(residual)，而为了消除残差，我们可以在残差减少的梯度(Gradient)方向上建立一个新的模型。所以说，在Gradient Boost中，每个新的模型的简历是为了使得之前模型的残差往梯度方向减少，与传统Boost对正确、错误的样本进行加权有着很大的区别。Gradient Boost其实是一个框架，里面可以套入很多不同的算法。GBDT如何做回归和分类，可以参见我之前的slides。"
			.getBytes();

	@Test
	public void testSnappy() {
		/**
		 * 原串长度2318，压缩后1638
		 */
		byte[] compress = FileUtil.snappyCompress(arr);
		System.out.println("before compress length is " + arr.length + ", after compress length is " + compress.length);
		byte[] uncompress = FileUtil.snappyUncompress(compress);
		Assert.assertEquals(uncompress.length, arr.length);
		for (int i = 0; i < uncompress.length; i++) {
			Assert.assertEquals(uncompress[i], arr[i]);
		}
	}

	@Test
	public void testSnappyCompress() {
		/**
		 * 压缩10万次，耗时1348毫秒
		 */
		final int LOOP = 100000;
		long begin = System.currentTimeMillis();
		for (int i = 0; i < LOOP; i++) {
			FileUtil.snappyCompress(arr);
		}
		long end = System.currentTimeMillis();
		System.out.println("compress " + LOOP + " times use " + (end - begin) + " miliseconds");
	}

	@Test
	public void testSnappyUncompress() {
		/**
		 * 解压10万次，耗时313毫秒
		 */
		byte[] compress = FileUtil.snappyCompress(arr);
		final int LOOP = 100000;
		long begin = System.currentTimeMillis();
		for (int i = 0; i < LOOP; i++) {
			FileUtil.snappyUncompress(compress);
		}
		long end = System.currentTimeMillis();
		System.out.println("uncompress " + LOOP + " times use " + (end - begin) + " miliseconds");
	}
}
