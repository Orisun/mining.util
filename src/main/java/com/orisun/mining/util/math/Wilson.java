package com.orisun.mining.util.math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.orisun.mining.util.sort.BinarySearch;

/**
 * 
 * @Description: Wilson置信区间
 * @Author orisun
 * @Date 2016年7月27日
 */
public class Wilson {

	private static class ConfZ implements Comparable<ConfZ> {
		private double confidence;
		private double z;

		ConfZ(double confidence, double z) {
			this.setConfidence(confidence);
			this.setZ(z);
		}

		public double getConfidence() {
			return confidence;
		}

		public void setConfidence(double confidence) {
			this.confidence = confidence;
		}

		public double getZ() {
			return z;
		}

		public void setZ(double z) {
			this.z = z;
		}

		@Override
		public int compareTo(ConfZ o) {
			if (this.getConfidence() < o.getConfidence()) {
				return -1;
			} else if (this.getConfidence() > o.getConfidence()) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	public static List<ConfZ> table = new ArrayList<ConfZ>();

	static {
		/**
		 * alpha为显著性水平，显著性水平+置信度=1
		 */
		table.add(new ConfZ(0.99, 2.58));
		table.add(new ConfZ(0.99, 2.58));
		table.add(new ConfZ(0.98, 2.33));
		table.add(new ConfZ(0.97, 2.17));
		table.add(new ConfZ(0.96, 2.06));
		table.add(new ConfZ(0.95, 1.96));
		table.add(new ConfZ(0.9, 1.65));
		table.add(new ConfZ(0.85, 1.44));
		table.add(new ConfZ(0.8, 1.28));
		table.add(new ConfZ(0.75, 1.15));
		table.add(new ConfZ(0.7, 1.04));
		table.add(new ConfZ(0.65, 0.94));

		Collections.sort(table);
	}

	/**
	 * 计算Wilson下限
	 * 
	 * @param value
	 *            必须位于[0,1]是
	 * @param count
	 *            必须大于0
	 * @param confidence
	 *            必须位于[0.65,1)是
	 * @return
	 */
	public static double wilsonFloor(double value, int count, double confidence) {
		assert count > 0;
		assert value <= 1;
		// table中不可能穷举所有的置信度，所以只能找到最接近的置信度
		int index = BinarySearch.searchNearest(table, new ConfZ(confidence, 1.0));
		double z = table.get(index).getZ();
		double z2 = z * z;
		return (value + z2 / (2 * count) - z * Math.sqrt(value * (1 - value) / count + z2 / (4 * count * count)))
				/ (1 + z2 / count);
	}
}
