package com.orisun.mining.util.math;

import com.orisun.mining.util.Path;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestLevelUtil {

	@Test
	public void testGetLevel(){
		List<Double> splits=new ArrayList<Double>();
		LevelUtil.initSplit(splits, Path.getCurrentPath()+"/data/deliver_will_split.txt", 5);
		System.out.println(LevelUtil.getLevel(splits, 0.559));
	}
}
