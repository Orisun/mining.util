package com.orisun.mining.util.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RGB {

	private static Pattern pattern_rgb = Pattern
			.compile("rgb\\((\\d{1,3}),\\s*(\\d{1,3}),\\s*(\\d{1,3})\\)");
	private static Pattern pattern_rgba1 = Pattern
			.compile("rgba\\((\\d{1,3}),\\s*(\\d{1,3}),\\s*(\\d{1,3}),\\s*([0-9]{1,3})%\\)");
	private static Pattern pattern_rgba2 = Pattern
			.compile("rgba\\((\\d{1,3}),\\s*(\\d{1,3}),\\s*(\\d{1,3}),\\s*([0-9\\.]+)\\)");
	private static Pattern pattern_color = Pattern.compile("\\w+");
	private static Pattern pattern_value1 = Pattern
			.compile("#([0-9a-fA-F]{2})([0-9a-fA-F]{2})([0-9a-fA-F]{2})");
	private static Pattern pattern_value2 = Pattern
			.compile("#([0-9a-fA-F]{1})([0-9a-fA-F]{1})([0-9a-fA-F]{1})");

	int red;
	int green;
	int blue;
	double alpha;

	RGB(int r, int g, int b) {
		this.red = r;
		this.green = g;
		this.blue = b;
		this.alpha = 1.0;// 默认不透明
	}

	RGB(int r, int g, int b, double alpha) {
		this.red = r;
		this.green = g;
		this.blue = b;
		this.alpha = alpha;
	}

	@Override
	public String toString() {
		return "(" + red + "," + green + "," + blue + "," + alpha + ")";
	}

	boolean similarTo(RGB another) {
		return Math.abs(red - another.getRed()) <= 10
				&& Math.abs(green - another.getGreen()) <= 10
				&& Math.abs(blue - another.getBlue()) <= 10
				&& Math.abs(alpha - another.getAlpha()) <= 0.1;
	}

	public int getRed() {
		return red;
	}

	public int getGreen() {
		return green;
	}

	public int getBlue() {
		return blue;
	}

	public double getAlpha() {
		return alpha;
	}

	/**
	 * 颜色的字符串表达式转换为RGB
	 * 
	 * @param color
	 * @return
	 */
	public static RGB colorToRGB(String color) {

		Matcher matcher_rgb = pattern_rgb.matcher(color);
		Matcher matcher_rgba1 = pattern_rgba1.matcher(color);
		Matcher matcher_rgba2 = pattern_rgba2.matcher(color);
		Matcher matcher_color = pattern_color.matcher(color);
		Matcher matcher_value1 = pattern_value1.matcher(color);
		Matcher matcher_value2 = pattern_value2.matcher(color);

		RGB rgb = null;
		if (matcher_color.matches()) {
			if ("maroon".equals(color)) {
				rgb = new RGB(0x80, 0x0, 0x0);
			} else if ("red".equals(color)) {
				rgb = new RGB(0xff, 0x0, 0x0);
			} else if ("orange".equals(color)) {
				rgb = new RGB(0xff, 0xa5, 0x0);
			} else if ("yellow".equals(color)) {
				rgb = new RGB(0xff, 0xff, 0x0);
			} else if ("olive".equals(color)) {
				rgb = new RGB(0x80, 0x80, 0x0);
			} else if ("purple".equals(color)) {
				rgb = new RGB(0x80, 0x0, 0x80);
			} else if ("fuchsia".equals(color)) {
				rgb = new RGB(0xff, 0x0, 0xff);
			} else if ("white".equals(color)) {
				rgb = new RGB(0xff, 0xff, 0xff);
			} else if ("lime".equals(color)) {
				rgb = new RGB(0x0, 0xff, 0x0);
			} else if ("green".equals(color)) {
				rgb = new RGB(0x0, 0x80, 0x0);
			} else if ("navy".equals(color)) {
				rgb = new RGB(0x0, 0x0, 0x80);
			} else if ("blue".equals(color)) {
				rgb = new RGB(0x0, 0x0, 0xff);
			} else if ("aqua".equals(color)) {
				rgb = new RGB(0x0, 0xff, 0xff);
			} else if ("teal".equals(color)) {
				rgb = new RGB(0x00, 0x80, 0x80);
			} else if ("black".equals(color)) {
				rgb = new RGB(0x0, 0x0, 0x0);
			} else if ("silver".equals(color)) {
				rgb = new RGB(0xc0, 0xc0, 0xc0);
			} else if ("gray".equals(color)) {
				rgb = new RGB(0x80, 0x80, 0x80);
			}
		} else if (matcher_rgb.find()) {
			int r = Integer.parseInt(matcher_rgb.group(1));
			int g = Integer.parseInt(matcher_rgb.group(2));
			int b = Integer.parseInt(matcher_rgb.group(3));
			rgb = new RGB(r, g, b);
		} else if (matcher_rgba1.find()) {
			int r = Integer.parseInt(matcher_rgba1.group(1));
			int g = Integer.parseInt(matcher_rgba1.group(2));
			int b = Integer.parseInt(matcher_rgba1.group(3));
			double alpha = Double.parseDouble(matcher_rgba1.group(4)) / 100;
			rgb = new RGB(r, g, b, alpha);
		} else if (matcher_rgba2.find()) {
			int r = Integer.parseInt(matcher_rgba2.group(1));
			int g = Integer.parseInt(matcher_rgba2.group(2));
			int b = Integer.parseInt(matcher_rgba2.group(3));
			double alpha = Double.parseDouble(matcher_rgba2.group(4));
			rgb = new RGB(r, g, b, alpha);
		} else if (matcher_value1.find()) {
			int r = Integer.parseInt(matcher_value1.group(1), 16);
			int g = Integer.parseInt(matcher_value1.group(2), 16);
			int b = Integer.parseInt(matcher_value1.group(3), 16);
			rgb = new RGB(r, g, b);
		} else if (matcher_value2.find()) {
			int r = Integer.parseInt(
					matcher_value2.group(1) + matcher_value2.group(1), 16);
			int g = Integer.parseInt(
					matcher_value2.group(2) + matcher_value2.group(2), 16);
			int b = Integer.parseInt(
					matcher_value2.group(3) + matcher_value2.group(3), 16);
			rgb = new RGB(r, g, b);
		}
		return rgb;
	}

	/**
	 * 判断颜色是否白色
	 * 
	 * @param rgb
	 * @return
	 */
	public static boolean isWhite(RGB rgb) {
		if (rgb != null) {
			// R、G、B三个值都大于235
			if (rgb.getRed() > 235 && rgb.getGreen() > 235
					&& rgb.getBlue() > 235) {
				return true;
			}
			// 或者透明度小于5%
			if (rgb.alpha <= 0.05) {
				return true;
			}
		}
		return false;
	}
}
