package com.orisun.mining.util.math.distribution;

import java.util.Random;

/**
 * 均匀分布
 * 
 * @Author:zhangchaoyang
 * @Since:2014-7-9
 * @Version:
 */
public class Uniform implements Distribution {

	/**
	 * [floor,ceil)上的均匀分布，前闭后开区间
	 */
	private double floor;
	private double ceil;

	public Uniform(double f, double c) {
		this.floor = f;
		this.ceil = c;
	}

	public double drawOnePoint() {
		Random random = new Random();
		// random.setSeed(System.nanoTime());
		double span = ceil - floor;
		double rnd = random.nextDouble();
		double rect = floor + span * rnd;
		return rect;
		// return floor + random.nextDouble() * (ceil - floor);
	}

	public double getExpection() {
		return (ceil + floor) / 2;
	}

	public double getVariance() {
		return (ceil - floor) * (ceil - floor) / 12;
	}

	public double getFloor() {
		return floor;
	}

	public void setFloor(double floor) {
		this.floor = floor;
	}

	public double getCeil() {
		return ceil;
	}

	public void setCeil(double ceil) {
		this.ceil = ceil;
	}

}
