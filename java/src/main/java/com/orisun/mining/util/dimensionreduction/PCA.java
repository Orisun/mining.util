package com.orisun.mining.util.dimensionreduction;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

import java.io.IOException;

public class PCA {

	/**
	 * PCA降维，K是降维后的维度
	 * 
	 * @param A
	 * @param K
	 * @return
	 * @Description:
	 */
	public static Matrix pcaReduction(Matrix A, int K) {
		Matrix C = getCovarianceMatrix(A);
		Matrix V = getEigenMatrix(C);
		Matrix V_sub = interceptMatrix(V, K);
		return A.times(V_sub);
	}

	/**
	 * 得到矩阵的协方差矩阵
	 * 
	 * @param A
	 * @return
	 * @throws IOException
	 */
	private static Matrix getCovarianceMatrix(Matrix A) {
		int itemNum = A.getRowDimension();
		int dimension = A.getColumnDimension();

		// 矩阵A减去各列的均值得到矩阵B
		double[] mean = new double[dimension];
		for (int j = 0; j < dimension; j++) {
			double sum = 0;
			for (int i = 0; i < itemNum; i++) {
				sum += A.get(i, j);
			}
			mean[j] = sum / itemNum;
		}
		for (int i = 0; i < itemNum; i++) {
			for (int j = 0; j < dimension; j++) {
				A.set(i, j, A.get(i, j) - mean[j]);
			}
		}

		// 求B各列之间的协方差矩阵得到C
		double[][] C = new double[dimension][];
		for (int i = 0; i < dimension; i++) {
			C[i] = new double[dimension];
			for (int j = 0; j < dimension; j++) {
				if (j >= i) {
					double sum = 0;
					for (int k = 0; k < itemNum; k++) {
						sum += A.get(k, i) * A.get(k, j);
					}
					C[i][j] = sum / (itemNum - 1);
				} else {
					C[i][j] = C[j][i];
				}
			}
		}
		return new Matrix(C);
	}

	/**
	 * 特征值分解
	 * 
	 * @param A
	 * @return
	 * @Description:
	 */
	private static Matrix getEigenMatrix(Matrix A) {
		EigenvalueDecomposition decom = new EigenvalueDecomposition(A);
		return decom.getV();// 特征值是从小到大排序的，所以主特征向量在后几列
	}

	/**
	 * 截取矩阵A的后K列.即A的最后一列放到rect的首列，依此类推
	 * 
	 * @param A
	 * @param K
	 * @return
	 */
	private static Matrix interceptMatrix(Matrix A, int K) {
		Matrix rect = new Matrix(A.getRowDimension(), K);
		for (int i = 0; i < A.getRowDimension(); i++) {
			for (int j = A.getColumnDimension() - 1; j >= A
					.getColumnDimension() - K; j--) {
				rect.set(i, A.getColumnDimension() - 1 - j, A.get(i, j));
			}
		}
		return rect;
	}
}
