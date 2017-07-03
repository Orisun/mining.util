package com.orisun.mining.util.chart;

import com.orisun.mining.util.Path;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

public class TestChart {

	private static String basePath;
	private static String dataPath;

	@BeforeClass
	public static void setup() {
		basePath = Path.getCurrentPath();
		dataPath = basePath + "/data/";
	}

	@Test
	public void testTable() throws IOException {
		TableDataSet ts = new TableDataSet();
		ts.setTitle("这里是表格标题");
		ts.setHeader("列1", "列2", "列3");
		ts.addValue("张三", "12", "54");
		ts.addValue("李四", "42", "3");
		ts.addValue("王五", "756", "43");
		String html = TableChart.toHtml(ts);
		System.out.println(html);
		TableChart.writeHtmlFile(ts, dataPath + "1.html");
	}

	@Test
	public void testPie() throws IOException {
		DefaultPieDataset pieData = new DefaultPieDataset();
		pieData.setValue("海军", 36);
		pieData.setValue("陆军", 65);
		pieData.setValue("空军", 80);
		ChartUtil.drawPieChart("这里是主标题", "这里是副标题", dataPath + "2.png", pieData);
	}

	@Test
	public void testHorizontalBar() throws IOException {
		DefaultCategoryDataset barData = new DefaultCategoryDataset();
		barData.addValue(36, "", "海军");
		barData.addValue(65, "", "陆军");
		barData.addValue(80, "", "空军");
		ChartUtil.drawHorizontalBarChart("这里是主标题", "这里是副标题", "军种", "军人数",
				dataPath + "3.png", barData);
	}

	@Test
	public void testVerticalBar() throws IOException {
		DefaultCategoryDataset barData = new DefaultCategoryDataset();
		barData.addValue(36, "总数", "海军");
		barData.addValue(10, "女兵数", "海军");
		barData.addValue(65, "总数", "陆军");
		barData.addValue(20, "女兵数", "陆军");
		barData.addValue(80, "总数", "空军");
		barData.addValue(40, "女兵数", "空军");
		ChartUtil.drawVerticalBarChart("这里是主标题", "这里是副标题", "军种", "军人数",
				dataPath + "4.png", barData);
	}

	@Test
	public void testTrend() throws IOException {
		DefaultCategoryDataset data = new DefaultCategoryDataset();
		data.addValue(10, "海军", "1990");
		data.addValue(20, "海军", "2000");
		data.addValue(36, "海军", "2010");
		data.addValue(10, "空军", "1990");
		data.addValue(40, "空军", "2000");
		data.addValue(86, "空军", "2010");
		ChartUtil.drawTimeTrend("这里是主标题", "这里是副标题", "时间", "军人数", dataPath
				+ "5.png", data);
	}
}
