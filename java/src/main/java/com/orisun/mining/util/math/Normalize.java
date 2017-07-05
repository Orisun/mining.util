package com.orisun.mining.util.math;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 向量正规化。向量正规化并不一定要把每个元素都映射到[0,1]上, 其根本目的是为了无量纲化。
 * 
 * @Author zhangchaoyang
 * @Since 2014-7-9
 * @Version 1.0
 */
public class Normalize {
    
    private static final double MIN_DOUBLE=1E-6;

    /**
     * 假设数据点服从某种正态分布，把它映射到标准正态分布上，而标准正态分布点几乎都落在[-3,3]上
     * 
     * @param vec
     * @return
     */
    public static Vector gaussConvert(Vector vec) {
        if (vec.getDimention() == 0) {
            return new Vector();
        }
        int len = vec.getDimention();
        // 计算均值和方差
        double squareSum = 0.0;
        double sum = 0.0;
        for (double ele : vec.getArray()) {
            squareSum += ele * ele;
            sum += ele;
        }
        double mean = sum / len;
        double var = squareSum / len - mean * mean;
        double[] arr = new double[len];
        // 方差为0，则每个值都赋1
        if (Math.abs(var - 0) < MIN_DOUBLE) {
            for (int i = 0; i < len; i++) {
                arr[i] = 1.0;
            }
        } else {
            // 高斯变换归一化
            for (int i = 0; i < len; i++) {
                double oldV = vec.get(i);
                // 减去均值，除以标准差，转换到标准正态分布
                double newV = (oldV - mean) / Math.sqrt(var);
                arr[i] = newV;
            }
        }
        return new Vector(arr);
    }

    /**
     * 假设数据点服从某种正态分布，把它映射到标准正态分布上，而标准正态分布点几乎都落在[-3,3]上
     * 
     * @param vec
     * @return
     */
    public static double[] gaussConvert(double[] vec) {
        if (vec.length == 0) {
            return new double[0];
        }
        int len = vec.length;
        // 计算均值和方差
        double squareSum = 0.0;
        double sum = 0.0;
        for (double ele : vec) {
            squareSum += ele * ele;
            sum += ele;
        }
        double mean = sum / len;
        double var = squareSum / len - mean * mean;
        double[] arr = new double[len];
        // 方差为0，则每个值都赋1
        if (Math.abs(var - 0) < MIN_DOUBLE) {
            for (int i = 0; i < len; i++) {
                arr[i] = 1.0;
            }
        } else {
            // 高斯变换归一化
            for (int i = 0; i < len; i++) {
                double oldV = vec[i];
                // 减去均值，除以标准差，转换到标准正态分布
                double newV = (oldV - mean) / Math.sqrt(var);
                arr[i] = newV;
            }
        }
        return arr;
    }

    /**
     * 正规化映射函数：y=(x-MinValue)/(MaxValue-MinValue)
     * 
     * @param vec
     * @return
     */
    public static Vector linearConvert(Vector vec) {
        if (vec.getDimention() == 0) {
            return new Vector();
        }
        int len = vec.getDimention();
        // 找最大值和最小值
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (double number : vec.getArray()) {
            if (number < min) {
                min = number;
            }
            if (number > max) {
                max = number;
            }
        }

        // 线性变换归一化
        double interval = max - min;
        // 最大值等于最小值，则每个值都赋1
        if (Math.abs(interval - 0) < MIN_DOUBLE) {
            double[] arr = new double[len];
            for (int i = 0; i < len; i++) {
                arr[i] = 1.0;
            }
            return new Vector(arr);
        } else {
            double[] arr = new double[len];
            for (int i = 0; i < len; i++) {
                double oldV = vec.get(i);
                double newV = (oldV - min) / interval;
                arr[i] = newV;
            }
            return new Vector(arr);
        }

    }

    /**
     * 正规化映射函数y=log10 (x)
     * 
     * @param vec
     * @return
     */
    public static Vector logConvert(Vector vec) {
        int len = vec.getDimention();
        double[] arr = new double[len];
        for (int i = 0; i < len; i++) {
            double oldV = vec.get(i);
            double newV = Math.log10(oldV);
            arr[i] = newV;
        }
        return new Vector(arr);
    }

    /**
     * y=arctan(x)*2/PI
     * 
     * @param vec
     * @return
     */
    public static Vector arctanConvert(Vector vec) {
        int len = vec.getDimention();
        double[] arr = new double[len];
        for (int i = 0; i < len; i++) {
            double oldV = vec.get(i);
            double newV = Math.atan(oldV) * 2 / Math.PI;
            arr[i] = newV;
        }
        return new Vector(arr);
    }

    /**
     * 高斯归一化
     * 
     * @param map
     */
    public static <K extends Object> void guassNorm(Map<K, Double> map) {
        if (map.size() == 0) {
            return;
        }
        double squareSum = 0.0;
        double sum = 0.0;
        for (Double ele : map.values()) {
            squareSum += ele * ele;
            sum += ele;
        }
        double mean = sum / map.size();//均值
        double var = squareSum / map.size() - mean * mean;//方差
        //        System.out.println(var);
        // 方差为0，则每个值都赋1
        if (Math.abs(var - 0) < MIN_DOUBLE) {
            for (Entry<K, Double> entry : map.entrySet()) {
                K key = entry.getKey();
                map.put(key, 1.0);
            }
        } else {
            double stdvar = Math.sqrt(var);//标准差
            for (Entry<K, Double> entry : map.entrySet()) {
                K key = entry.getKey();
                double value = entry.getValue();
                double normValue = ((value - mean) / stdvar + 2) / 4;
                if (normValue < 0) {
                    normValue = 0.0;
                }
                if (normValue > 1) {
                    normValue = 1.0;
                }
                map.put(key, normValue);
            }
        }
    }

    /**
     * 线性归一化
     * 
     * @param map
     */
    public static <K extends Object> void linearNorm(Map<K, Double> map) {
        if (map.size() == 0) {
            return;
        }
        double maxWeight = Double.MIN_NORMAL;
        double minWeight = Double.MAX_VALUE;
        for (Entry<K, Double> entry : map.entrySet()) {
            double value = entry.getValue();
            if (value > maxWeight) {
                maxWeight = value;
            }
            if (value < minWeight) {
                minWeight = value;
            }
        }
        double interval = maxWeight - minWeight;
        // 最大值等于最小值，则每个值都赋1
        if (Math.abs(interval - 0) < MIN_DOUBLE) {
            for (Entry<K, Double> entry : map.entrySet()) {
                K term = entry.getKey();
                map.put(term, 1.0);
            }
        } else {
            for (Entry<K, Double> entry : map.entrySet()) {
                K key = entry.getKey();
                double value = (double) entry.getValue();
                map.put(key, (value - minWeight) / interval);
            }
        }
    }

    /**
     * 极值归一化，适用于取值为正实数的情况
     *
     * @param map
     */
    public static <K extends Object> void maxNorm(Map<K, Double> map) {
        if (map.size() == 0) {
            return;
        }
        double maxValue = 0.0;
        double minValue = 0.0;
        for (Entry<K, Double> entry : map.entrySet()) {
            double value = entry.getValue();
            if (value > maxValue) {
                maxValue = value;
            }
        }

        if (maxValue == minValue) {
            for (Entry<K, Double> entry : map.entrySet()) {
                K key = entry.getKey();
                map.put(key, 1.0);
            }
        } else {
            for (Entry<K, Double> entry : map.entrySet()) {
                K key = entry.getKey();
                map.put(key, entry.getValue()/maxValue);
            }
        }
    }
    
    /**
	 * 归一化，使权重之和为1.0
	 * 
	 * @param distribution
	 * @return
	 */
	public static <T> Map<T, Double> normalize(Map<T, Double> distribution) {
		Map<T, Double> rect = new HashMap<T, Double>();
		if (distribution != null && distribution.size() > 0) {
			double weightSum = 0.0;
			for (Double weight : distribution.values()) {
				weightSum += weight;
			}
			if (weightSum > 0) {
				for (Entry<T, Double> entry : distribution.entrySet()) {
					rect.put(entry.getKey(), entry.getValue() / weightSum);
				}
			}
		}
		return rect;
	}

    public static void main(String[] args) {
        double[] arr = new double[] { 1, 5, 3, 8, 6, 4, 2, 7, 9 };
        double[] brr = Normalize.gaussConvert(arr);
        for (double ele : brr) {
            System.out.print(ele + ", ");
        }
        System.out.println();

        Vector vec = Normalize.gaussConvert(new Vector(arr));
        System.out.println(vec);
    }
}
