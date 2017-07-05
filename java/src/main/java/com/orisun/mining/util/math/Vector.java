package com.orisun.mining.util.math;

import java.util.*;

import com.orisun.mining.util.Pair;
import com.orisun.mining.util.exception.ArgumentException;
import com.orisun.mining.util.exception.DmArithmeticException;

/**
 * 自定义向量类
 * 
 * @Author:zhangchaoyang
 * @Since:2014-7-9
 * @Version:
 */
public class Vector implements Iterable<Double>{
	private int dimention;
	private double[] array;

	public Vector() {

	}

	public Vector(int n) {
		this.dimention = n;
		array = new double[n];
	}

	/**
	 * 
	 * @param n
	 * @param defaultValue
	 *            向量中的每个元素都是defaultValue
	 */
	public Vector(int n, double defaultValue) {
		this.dimention = n;
		array = new double[n];
		for (int i = 0; i < n; i++) {
			array[i] = defaultValue;
		}
	}

	public Vector(int... args) {
		int n = args.length;
		this.dimention = n;
		array = new double[n];
		for (int i = 0; i < n; i++) {
			array[i] = args[i];
		}
	}

	/**
	 * 由一维数组构造向量
	 * 
	 * @param arr
	 */
	public Vector(double[] arr) {
		this.dimention = arr.length;
		this.array = arr;
	}

	public double get(int index) {
		return array[index];
	}

	public void set(int index, double value) {
		array[index] = value;
	}

	/**
	 * 向量和向量点乘
	 * 
	 * @param vec
	 * @return
	 * @throws ArgumentException
	 *             两向量长度不一致时抛出该异常
	 */
	public double dotProduct(Vector vec) throws ArgumentException {
		if (vec.getDimention() != this.getDimention()) {
			throw new ArgumentException("向量长度不一致");
		}
		double product = 0.0;
		for (int i = 0; i < dimention; i++) {
			product += (this.array[i] * vec.getArray()[i]);
		}
		return product;
	}

	/**
	 * 向量每个元素乘以一个因子
	 * 
	 * @param m
	 * @return
	 */
	public Vector multipleBy(double m) {
		double[] arr = new double[this.getDimention()];
		for (int i = 0; i < this.getDimention(); i++) {
			arr[i] = m * this.getArray()[i];
		}
		return new Vector(arr);
	}

	/**
	 * 向量每个元素乘以另一个向量对应的元素
	 *
	 * @param vec
	 * @return
	 */
	public Vector multipleBy(Vector vec) throws ArgumentException {
		if (vec.getDimention() != this.getDimention()) {
			throw new ArgumentException("向量长度不一致");
		}
		Vector ret = new Vector(this.getDimention(), 0.0);
		for (int i = 0; i < dimention; i++) {
			ret.set(i, this.array[i] * vec.getArray()[i]);
		}
		return ret;
	}

	/**
	 * 向量2范数
	 * 
	 * @return
	 */
	public double norm2() {
		double rect = 0;
		try {
			rect = Math.sqrt(this.dotProduct(this));
		} catch (ArgumentException e) {
			// 自己和自己点乘时不会抛出异常
		}
		return rect;
	}

	/**
	 * 样本均值
	 * 
	 * @return
	 * @throws DmArithmeticException
	 *             样本容量为0时抛出该异常
	 */
	public double mean() throws DmArithmeticException {
		if (dimention <= 0) {
			throw new DmArithmeticException("样本容量为0，无法计算均值");
		}
		double sum = 0;
		for (double ele : array) {
			sum += ele;
		}
		return sum / dimention;
	}

	/**
	 * 同时计算样本均值和方差
	 * 
	 * @return <样本均值，样本方差>
	 * @throws DmArithmeticException
	 *             样本容量为0时抛出该异常
	 */
	public Pair<Double, Double> meanAndVariance() throws DmArithmeticException {
		if (dimention <= 0) {
			throw new DmArithmeticException("样本容量为0，无法计算方差");
		}
		double sum = 0;
		double sumSquare = 0;
		for (double ele : array) {
			sum += ele;
			sumSquare += ele * ele;
		}
		double mean = sum / dimention;
		double variance = sumSquare / dimention - mean * mean;
		return Pair.of(mean, variance);
	}

	/**
	 * 计算n分位点
	 * 
	 * @param n
	 * @return
	 * @throws DmArithmeticException
	 */
	public List<Double> getQuantile(int n) throws DmArithmeticException {
		if (dimention < n) {
			throw new DmArithmeticException("数组长度小于要计算的分位点数");
		}
		List<Double> rect = new ArrayList<Double>(n + 1);
		List<Double> data = new LinkedList<Double>();
		for (double ele : array) {
			data.add(ele);
		}
		Collections.sort(data);
		double slice = 1.0 * dimention / n;
		for (int i = 0; i < n; i++) {
			int index = (int) Math.round(i * slice);
			rect.add(data.get(index));
		}
		rect.add(data.get(data.size() - 1));
		return rect;
	}

	/**
	 * 转换成Matrix实体
	 * 
	 * @see {@link com.orisun.mining.util.math.Matrix}
	 * @return
	 */
	public Matrix toMatrix() {
		int len = this.getDimention();
		double[][] rect = new double[1][];
		rect[0] = new double[len];
		for (int i = 0; i < len; i++) {
			rect[0][i] = this.getArray()[i];
		}
		return new Matrix(rect);
	}

	/**
	 * 两向量相加
	 * 
	 * @param vec
	 * @return
	 * @throws ArgumentException
	 *             两向量长度不一致时抛出该异常
	 */
	public Vector add(Vector vec) throws ArgumentException {
		if (this.getDimention() != vec.getDimention()) {
			throw new ArgumentException("向量长度不一致");
		}
		Vector rect = new Vector(this.getDimention());
		for (int i = 0; i < this.getDimention(); i++) {
			rect.getArray()[i] = this.getArray()[i] + vec.getArray()[i];
		}
		return rect;
	}

	/**
	 * 两向量相减
	 * 
	 * @param vec
	 * @return
	 * @throws ArgumentException
	 *             两向量长度不一致时抛出该异常
	 */
	public Vector sub(Vector vec) throws ArgumentException {
		if (this.getDimention() != vec.getDimention()) {
			throw new ArgumentException("向量长度不一致");
		}
		Vector rect = new Vector(this.getDimention());
		for (int i = 0; i < this.getDimention(); i++) {
			rect.getArray()[i] = this.getArray()[i] - vec.getArray()[i];
		}
		return rect;
	}

	@Override
	public Iterator<Double> iterator() {
		Iterator<Double> it = new Iterator<Double>() {

			private int currentIndex = 0;

			@Override
			public boolean hasNext() {
				return currentIndex < dimention;
			}

			@Override
			public Double next() {
				return array[currentIndex++];
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		return it;
	}


	@Override
	public String toString() {
		return Arrays.toString(this.getArray());
	}

	public double[] getArray() {
		return array;
	}

	public void setArray(double[] array) {
		this.array = array;
	}

	public int getDimention() {
		return dimention;
	}

}
