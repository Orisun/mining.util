package com.orisun.mining.util.zookeeper;

import com.orisun.mining.util.IChooseStrategy;
import com.orisun.mining.util.SystemConfig;
import com.orisun.mining.util.hash.SimpleHashFunction;
import com.orisun.mining.util.monitor.SendMail;
import com.orisun.mining.util.sort.MapSorter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class RecStrategy extends ZkConfig implements IChooseStrategy {

	private static Log logger = LogFactory.getLog(RecStrategy.class);
	protected static final String BASE_PATH = ZkClient.getInstance().getBasePath() + "/strategy";

	private List<Double> ratioList = new CopyOnWriteArrayList<Double>();// 各策略按分得的流量比量升序排好，ratioList中存放的比例的累加值
	// key是ratioList的index,value是策略编号
	private Map<Integer, Integer> strategyIndex = new ConcurrentHashMap<Integer, Integer>();
	private Random random = new Random();

	private void normalizeRatio() {
		Pattern pattern = Pattern.compile(".*_(\\d+)");
		Map<Integer, Double> ratioMap = new HashMap<Integer, Double>();// 策略编码-->策略流量占比(归一化之前)
		double total = 0.0;
		List<Double> ratioListTmp = new CopyOnWriteArrayList<Double>();// 各策略按分得的流量比量升序排好，ratioList中存放的比例的累加值
		Map<Integer, Integer> strategyIndexTmp = new ConcurrentHashMap<Integer, Integer>();
		try {
			String strategyClassName = this.getClass().getCanonicalName();
			Class<?> strategyClz = Class.forName(strategyClassName);
			String zkArgClassName = ZkParam.class.getCanonicalName();
			Class<?> zkArgClz = Class.forName(zkArgClassName);
			Field[] fields = strategyClz.getDeclaredFields();
			for (final Field field : fields) {
				String filedName = field.getName();
				Matcher matcher = pattern.matcher(filedName);
				if (matcher.find()) {
					int index = Integer.parseInt(matcher.group(1));
					field.setAccessible(true);
					if (field.getType().getCanonicalName().equals(zkArgClassName)) {
						Object zkParamInst = field.get(this);
						Method getValueMethod = zkArgClz.getMethod("getValue");
						Double fieldValue = (Double) getValueMethod.invoke(zkParamInst);
						// 占比必须大于0
						if (fieldValue > 0) {
							ratioMap.put(index, fieldValue);
							total += fieldValue;
						}
					}
				}
			}
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalArgumentException
				| IllegalAccessException | InvocationTargetException e) {
			logger.error("reflect strategy ratio failed", e);
		}
		if (ratioMap.size() <= 0) {
			logger.error("no strategy on!");
			SendMail.getInstance().sendMail(SystemConfig.getValue("mail_subject"),
					SystemConfig.getValue("mail_receiver"), "have no RecStrategy choosed!");
		}
		List<Entry<Integer, Double>> sortedRatio = MapSorter.sortMapByValue(ratioMap, false);
		double calculate = 0.0;
		logger.info(this.getClass().getCanonicalName() + " strategy normalized ratio");
		for (Entry<Integer, Double> entry : sortedRatio) {
			double norm = entry.getValue() / total;
			logger.info(entry.getKey() + ":" + norm);
			ratioListTmp.add(calculate + norm);
			calculate += norm;
			strategyIndexTmp.put(ratioListTmp.size() - 1, entry.getKey());
		}
		ratioList = ratioListTmp;
		strategyIndex = strategyIndexTmp;
	}

	@Override
	public void updateParam(String filedName, ZkParam newArgument) {
		super.updateParam(filedName, newArgument);
		normalizeRatio();
	}

	@Override
	public void readFromMysql() {
		super.readFromMysql();
		normalizeRatio();
	}

	@Override
	public int chooseStrategyBySeed(long seed) {
		Random rnd = new Random(seed);
		double num = rnd.nextDouble();
		int index = ratioList.size() - 1;
		for (; index >= 0; index--) {
			if (num > ratioList.get(index)) {
				break;
			}
		}
		int rect = index + 1;
		if (rect > ratioList.size() - 1) {
			rect = ratioList.size() - 1;
		}
		Integer si = strategyIndex.get(rect);
		if (si == null) {
			logger.error("can not find " + rect + "th strategy");
			si = 0;
		}
		logger.debug("choose strategy " + si);
		return si;
	}

	/**
	 * 
	 * @see com.orisun.mining.util.IChooseStrategy#choose(int)
	 */
	@Override
	public int choose(int number) {
		long seed = SimpleHashFunction.DEKHash(String.valueOf(number));
		return chooseStrategyBySeed(seed);
	}

	@Override
	public int choose() {
		return chooseStrategyBySeed(random.nextLong());
	}
}
