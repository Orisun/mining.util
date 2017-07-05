package com.orisun.mining.util.math;

import com.orisun.mining.util.exception.ArgumentException;


/**
 * 由一个二维double数组封装的Matrix实体，提供一系列的矩阵运算
 * 
 * @Author:zhangchaoyang
 * @Since:2014-7-9
 * @Version:
 */
public class Matrix {

	int dimention1;
	int dimention2;
	double[][] arr;

	public Matrix() {

	}

	public Matrix(int len1, int len2) {
		this.dimention1 = len1;
		this.dimention2 = len2;
		arr = new double[len1][];
		for (int i = 0; i < len1; i++) {
			arr[i] = new double[len2];
		}
	}

	public Matrix(double[][] arr) {
		this.dimention1 = arr.length;
		this.dimention2 = arr[0].length;
		this.setArr(arr);
	}

	/**
	 * 构造n阶单位矩阵
	 * 
	 * @param n
	 *            矩阵的阶数
	 * @return
	 */
	public static Matrix identity(int n) {
		Matrix matrix = new Matrix(n, n);
		double[][] arr = new double[n][];
		for (int i = 0; i < n; i++) {
			arr[i] = new double[n];
			for (int j = 0; j < n; j++) {
				if (i == j) {
					arr[i][j] = 1.0;
				} else {
					arr[i][j] = 0.0;
				}
			}
		}
		matrix.setArr(arr);
		return matrix;
	}

	/**
	 * 构造n阶零矩阵
	 * 
	 * @param n
	 *            矩阵的阶数
	 * @return
	 */
	public static Matrix zero(int n) {
		Matrix matrix = new Matrix(n, n);
		double[][] arr = new double[n][];
		for (int i = 0; i < n; i++) {
			arr[i] = new double[n];
			for (int j = 0; j < n; j++) {
				arr[i][j] = 0.0;
			}
		}
		matrix.setArr(arr);
		return matrix;
	}

	/**
	 * 求两个矩阵相加的和
	 * 
	 * @param matrix
	 * @return
	 * @throws ArgumentException
	 *             两个矩阵的规模不相同时抛出该异常
	 */
	public Matrix add(Matrix matrix) throws ArgumentException {
		if (this.getDimention1() != matrix.getDimention1()
				|| this.getDimention2() != matrix.getDimention2()) {
			throw new ArgumentException("两个矩阵的规模不相同");
		}
		double[][] sum = new double[this.getDimention1()][];
		for (int i = 0; i < this.getDimention1(); i++) {
			sum[i] = new double[this.getDimention2()];
			for (int j = 0; j < this.getDimention2(); j++) {
				sum[i][j] = this.getArr()[i][j] + matrix.getArr()[i][j];
			}
		}
		return new Matrix(sum);
	}

	/**
	 * 求两个矩阵相减的差
	 * 
	 * @param matrix
	 * @return
	 * @throws ArgumentException
	 *             两个矩阵的规模不相同时抛出该异常
	 */
	public Matrix sub(Matrix matrix) throws ArgumentException {
		if (this.getDimention1() != matrix.getDimention1()
				|| this.getDimention2() != matrix.getDimention2()) {
			throw new ArgumentException("两个矩阵的规模不相同");
		}
		double[][] sum = new double[this.getDimention1()][];
		for (int i = 0; i < this.getDimention1(); i++) {
			sum[i] = new double[this.getDimention2()];
			for (int j = 0; j < this.getDimention2(); j++) {
				sum[i][j] = this.getArr()[i][j] - matrix.getArr()[i][j];
			}
		}
		return new Matrix(sum);
	}

	/**
	 * 求矩阵的转置
	 * 
	 * @return
	 */
	public Matrix transport() {
		int len1 = this.getDimention1();
		int len2 = this.getDimention2();
		double[][] arr = new double[len2][];
		for (int i = 0; i < len2; i++) {
			arr[i] = new double[len1];
			for (int j = 0; j < len1; j++) {
				arr[i][j] = this.getArr()[j][i];
			}
		}
		return new Matrix(arr);
	}

	/**
	 * 求两个矩阵的点积
	 * 
	 * @param matrix
	 * @return
	 * @throws ArgumentException
	 *             第一个矩阵的列数不等于第二个的行数时抛出该异常
	 */
	public Matrix dotProduct(Matrix matrix) throws ArgumentException {
		int m = this.getDimention1();
		int k = this.getDimention2();
		int n = matrix.getDimention2();
		if (k != matrix.getDimention1()) {
			throw new ArgumentException("第一个矩阵的列数不等于第二个的行数");
		}
		double[][] product = new double[m][];
		for (int i = 0; i < m; i++) {
			product[i] = new double[n];
		}
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				double sum = 0.0;
				for (int t = 0; t < k; t++) {
					sum += this.getArr()[i][t] * matrix.getArr()[t][j];
				}
				product[i][j] = sum;
			}
		}
		Matrix rect = new Matrix(m, n);
		rect.setArr(product);
		return rect;
	}

	/**
	 * 矩阵的每一个元素都乘以一个因子
	 * 
	 * @param p
	 *            乘以的因子
	 * @return
	 */
	public Matrix multipleBy(double p) {
		int len1 = this.getDimention1();
		int len2 = this.getDimention2();
		double[][] arr = new double[len1][];
		for (int i = 0; i < len1; i++) {
			arr[i] = new double[len2];
		}
		for (int i = 0; i < len1; i++) {
			for (int j = 0; j < len2; j++) {
				arr[i][j] = p * this.getArr()[i][j];
			}
		}
		Matrix rect = new Matrix(len1, len2);
		rect.setArr(arr);
		return rect;
	}

	/**
	 * 求矩阵的二范数，即每个元素的平方和
	 * 
	 * @return
	 */
	public double norm2() {
		double[][] matrix = this.getArr();
		int len1 = matrix.length;
		int len2 = matrix[0].length;
		double rect = 0.0;
		for (int i = 0; i < len1; i++) {
			for (int j = 0; j < len2; j++) {
				rect += matrix[i][j] * matrix[i][j];
			}
		}
		return Math.sqrt(rect);

	}

	/**
	 * 抽取某一行
	 * 
	 * @param index
	 *            行索引标号
	 * @return
	 */
	public Vector getRow(int index) {
		Vector vec = new Vector(this.getDimention2());
		for (int i = 0; i < this.getDimention2(); i++) {
			vec.getArray()[i] = this.getArr()[index][i];
		}
		return vec;
	}

	/**
	 * 抽取某一列
	 * 
	 * @param index
	 *            列索引标号
	 * @return
	 */
	public Vector getCol(int index) {
		Vector vec = new Vector(this.getDimention1());
		for (int i = 0; i < this.getDimention1(); i++) {
			vec.getArray()[i] = this.getArr()[i][index];
		}
		return vec;
	}

	/**
	 * 打印输出二维数组
	 */
	public void print() {
		for (int i = 0; i < dimention1; i++) {
			System.out.print("[");
			for (int j = 0; j < dimention2; j++) {
				System.out.print(arr[i][j] + ",");
			}
			System.out.println("]");
		}
	}

	public double[][] getArr() {
		return arr;
	}

	public void setArr(double[][] arr) {
		this.arr = arr;
	}

	public int getDimention1() {
		return dimention1;
	}

	public int getDimention2() {
		return dimention2;
	}

}
