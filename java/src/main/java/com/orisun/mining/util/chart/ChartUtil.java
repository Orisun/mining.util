package com.orisun.mining.util.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.TextAnchor;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ChartUtil {

	/**
	 * 绘制饼状图，输出到文件
	 * 
	 * @param title
	 * @param subtitle
	 * @param outFile
	 * @param data
	 * @throws IOException
	 */
	public static void drawPieChart(String title, String subtitle,
			String outFile, DefaultPieDataset data) throws IOException {
		// 创建主题样式
		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
		// 设置标题字体
		standardChartTheme.setExtraLargeFont(new Font("隶书", Font.BOLD, 20));
		// 设置图例的字体
		standardChartTheme.setRegularFont(new Font("宋书", Font.PLAIN, 15));
		// 设置轴向的字体
		standardChartTheme.setLargeFont(new Font("宋书", Font.PLAIN, 15));
		// 应用主题样式
		ChartFactory.setChartTheme(standardChartTheme);
		JFreeChart chart = ChartFactory.createPieChart(title, // 图表标题
				data, //
				true, // 是否显示图例
				false, // 是否生成工具
				false// 是否生成 URL链接
				);
		chart.addSubtitle(new TextTitle(subtitle)); // 副标题

		PiePlot pieplot = (PiePlot) chart.getPlot();
		pieplot.setLabelGenerator(new StandardPieSectionLabelGenerator(
				("{0}\n({1} {2})"), NumberFormat.getNumberInstance(),
				new DecimalFormat("0.00%")));
		pieplot.setLabelFont(new Font("宋体", 0, 11));
		pieplot.setMaximumLabelWidth(0.2);

		FileOutputStream fos_jpg = null;
		try {
			fos_jpg = new FileOutputStream(outFile);
			ChartUtilities.writeChartAsJPEG(fos_jpg, 1.0f, chart, 700, 400,
					null);
		} finally {
			try {
				fos_jpg.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 绘制竖直条状图，输出到文件
	 * 
	 * @param title
	 * @param subtitle
	 * @param axisX
	 * @param axisY
	 * @param outFile
	 * @param data
	 * @throws IOException
	 */
	public static void drawVerticalBarChart(String title, String subtitle,
			String axisX, String axisY, String outFile,
			DefaultCategoryDataset data) throws IOException {
		// 创建主题样式
		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
		// 设置标题字体
		standardChartTheme.setExtraLargeFont(new Font("隶书", Font.BOLD, 20));
		// 设置图例的字体
		standardChartTheme.setRegularFont(new Font("宋书", Font.PLAIN, 15));
		// 设置轴向的字体
		standardChartTheme.setLargeFont(new Font("宋书", Font.PLAIN, 15));
		// 应用主题样式
		ChartFactory.setChartTheme(standardChartTheme);

		JFreeChart chart = ChartFactory.createBarChart(title, // 图表标题
				axisX, // 目录轴的显示标签
				axisY, // 数值轴的显示标签
				data, // 数据集
				PlotOrientation.VERTICAL, // 图表方向：水平、垂直
				true, // 是否显示图例(对于简单的柱状图必须是 false)
				false, // 是否生成工具
				false // 是否生成 URL链接
				);
		chart.addSubtitle(new TextTitle(subtitle)); // 副标题

		CategoryPlot plot = (CategoryPlot) chart.getCategoryPlot();
		CategoryItemRenderer renderer = plot.getRenderer();
		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setBaseItemLabelsVisible(true);
		// 默认的数字显示在柱子中，通过如下两句可调整数字的显示
		renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER));

		FileOutputStream fos_jpg = null;
		try {
			fos_jpg = new FileOutputStream(outFile);
			ChartUtilities.writeChartAsJPEG(fos_jpg, 0.9f, chart, 700, 700,
					null);
		} finally {
			try {
				fos_jpg.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 绘制水平条状图，输出到文件
	 * 
	 * @param title
	 * @param subtitle
	 * @param axisX
	 * @param axisY
	 * @param outFile
	 * @param data
	 * @throws IOException
	 */
	public static void drawHorizontalBarChart(String title, String subtitle,
			String axisX, String axisY, String outFile,
			DefaultCategoryDataset data) throws IOException {
		// 创建主题样式
		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
		// 设置标题字体
		standardChartTheme.setExtraLargeFont(new Font("隶书", Font.BOLD, 20));
		// 设置图例的字体
		standardChartTheme.setRegularFont(new Font("宋书", Font.PLAIN, 15));
		// 设置轴向的字体
		standardChartTheme.setLargeFont(new Font("宋书", Font.PLAIN, 15));
		// 应用主题样式
		ChartFactory.setChartTheme(standardChartTheme);

		JFreeChart chart = ChartFactory.createBarChart(title, // 图表标题
				axisX, // 目录轴的显示标签
				axisY, // 数值轴的显示标签
				data, // 数据集
				PlotOrientation.HORIZONTAL, // 图表方向：水平、垂直
				false, // 是否显示图例(对于简单的柱状图必须是 false)
				false, // 是否生成工具
				false // 是否生成 URL链接
				);
		chart.addSubtitle(new TextTitle(subtitle)); // 副标题

		CategoryPlot plot = (CategoryPlot) chart.getCategoryPlot();
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setBaseItemLabelsVisible(true);
		// 默认的数字显示在柱子中，通过如下两句可调整数字的显示
		renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
				ItemLabelAnchor.OUTSIDE3, TextAnchor.BASELINE_LEFT));
		renderer.setItemLabelAnchorOffset(5);
		ValueAxis rangeAxis = plot.getRangeAxis();
		// 设置最高的一个 Item 与图片顶端的距离
		rangeAxis.setUpperMargin(0.15);

		FileOutputStream fos_jpg = null;
		try {
			fos_jpg = new FileOutputStream(outFile);
			ChartUtilities.writeChartAsJPEG(fos_jpg, 0.9f, chart, 700, 500,
					null);
		} finally {
			try {
				fos_jpg.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 绘制拆线图，输出到文件
	 * 
	 * @param title
	 * @param subtitle
	 * @param axisX
	 * @param axisY
	 * @param outFile
	 * @param data
	 * @throws IOException
	 */
	public static void drawTimeTrend(String title, String subtitle,
			String axisX, String axisY, String outFile,
			DefaultCategoryDataset data) throws IOException {
		// 创建主题样式
		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
		// 设置标题字体
		standardChartTheme.setExtraLargeFont(new Font("隶书", Font.BOLD, 20));
		// 设置图例的字体
		standardChartTheme.setRegularFont(new Font("宋书", Font.PLAIN, 15));
		// 设置轴向的字体
		standardChartTheme.setLargeFont(new Font("宋书", Font.PLAIN, 15));
		// 应用主题样式
		ChartFactory.setChartTheme(standardChartTheme);

		JFreeChart chart = null;
		chart = ChartFactory.createLineChart(title, // 图表标题
				axisX, // X轴标题
				axisY, // Y轴标题
				data, // 绘图数据集
				PlotOrientation.VERTICAL, // 绘制方向
				true, // 是否显示图例
				false, // 是否采用标准生成器
				false // 是否生成超链接
				);
		chart.addSubtitle(new TextTitle(subtitle)); // 副标题

		CategoryPlot plot = chart.getCategoryPlot();
		CategoryAxis categoryAxis = plot.getDomainAxis();
		// 横轴上的Lable90度倾斜
		categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
		// 设置X轴字体
		categoryAxis.setTickLabelFont(new Font("宋体", Font.PLAIN, 10));

		FileOutputStream fos_jpg = null;
		try {
			fos_jpg = new FileOutputStream(outFile);
			ChartUtilities.writeChartAsJPEG(fos_jpg, 0.9f, chart, 700, 500,
					null);
		} finally {
			try {
				fos_jpg.close();
			} catch (Exception e) {
			}
		}
	}

}
