package com.orisun.mining.util.math;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 
 * @Description: 划分等级
 * @Author orisun
 * @Date 2016年8月18日
 */
public class LevelUtil {

	private static Log logger = LogFactory.getLog(LevelUtil.class);
	private static Random random = new Random();

	/**
	 * 蓄水池抽样法，决定是否要把一个样本添加到集合中
	 * 
	 * @param sample
	 * @param number
	 */
	public static void addSample(List<Double> sample, final int SAMPLE_NUM, double number) {
		int len = sample.size();
		if (len < SAMPLE_NUM) {
			sample.add(number);
		} else {
			int index = random.nextInt(len) + 1;
			if (index < SAMPLE_NUM) {
				sample.set(index, number);
			}
		}
	}

	/**
	 * 初始化分箱点。先从文件中读，若从文件中读不到则均匀初始化分箱点
	 * 
	 * @param list
	 * @param file
	 * @param LEVEL
	 */
	public static void initSplit(List<Double> list, String file, final int LEVEL) {
		// 先从文件中读
		readList(list, file, LEVEL);
		// 若从文件中读不到则均匀初始化分箱点
		if (list.size() == 0) {
			List<Double> sample = new ArrayList<Double>();
			for (int i = 0; i < 10000; i++) {
				double rnd = Math.random();
				sample.add(rnd);
			}
			List<Double> bins = Discretize.binConstDepth(sample, LEVEL);
			for (int i = 0; i < LEVEL - 1; i++) {
				list.add(bins.get(i));
			}
		}
	}

	/**
	 * 重新计算分箱点，并将新的分箱点写入文件
	 * 
	 * @param sample
	 * @param LEVEL
	 * @param split
	 * @param outFile
	 *            为null时不写文件
	 */
	public static void refreshSplit(List<Double> sample, final int LEVEL, List<Double> split, String outFile) {
		List<Double> bins = Discretize.binConstDepth(sample, LEVEL);
		if (bins.size() > 0) {
			sample.clear();
			logger.info("split point:");
			for (int i = 0; i < LEVEL - 1; i++) {
				split.set(i, bins.get(i));
				logger.info(bins.get(i));
			}
		} else {
			logger.fatal("can not split to " + LEVEL + " bins, sample.size() is " + sample.size());
		}

		writeList(split, outFile);
	}

	/**
	 * 计算等级
	 * 
	 * @param split
	 * @param number
	 * @return
	 */
	public static int getLevel(List<Double> split, double number) {
		int binIndex = Discretize.getIndexOfBin(split, number);
		return binIndex + 1;
	}

	/**
	 * 从文件中读取一个double数组，一行一个数字
	 * 
	 * @param list
	 * @param file
	 */
	private static void readList(List<Double> list, String file, final int LEVEL) {
		if (file == null || file.length() == 0) {
			return;
		}
		File infile = new File(file);
		if (infile.exists() && infile.isFile()) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(infile));
				String line = null;
				list.clear();
				while ((line = br.readLine()) != null) {
					String cont = line.trim();
					if (cont.length() > 0) {
						list.add(Double.parseDouble(cont));
					}
				}
				//如果split的长度不对，则报警并清空split point
				if (list.size() != LEVEL - 1) {
					logger.fatal("number of split is not equals to " + (LEVEL - 1));
					list.clear();
				}
			} catch (IOException e) {
				logger.fatal("read split point from file " + file + " failed", e);
			} finally {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 将一个double数组写入文件，一行一个数字
	 * 
	 * @param list
	 * @param file
	 */
	private static void writeList(List<Double> list, String file) {
		if (file == null || file.length() == 0) {
			return;
		}
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file));
			if (list != null) {
				for (Double ele : list) {
					bw.write(String.valueOf(ele));
					bw.newLine();
					logger.debug(String.valueOf(ele));
				}
			}
		} catch (IOException e) {
			logger.fatal("write list to file " + file + " failed", e);
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
			}
		}
	}
}
