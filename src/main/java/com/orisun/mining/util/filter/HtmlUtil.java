package com.orisun.mining.util.filter;

import com.orisun.mining.util.Pair;
import com.orisun.mining.util.exception.DmArithmeticException;
import com.orisun.mining.util.math.Vector;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtil {

	public static String escape(String html) {
		String rect = new String(html);
		// 注意对&的转义必须放在最开始
		rect = rect.replaceAll("&", "&amp;");
		rect = rect.replaceAll("\"", "&quot;");
		rect = rect.replaceAll("<", "&lt;");
		rect = rect.replaceAll(">", "&gt;");
		rect = rect.replaceAll(" ", "&nbsp;");
		return rect;
	}

	public static String reverseEscape(String str) {
		String rect = new String(str);
		rect = rect.replaceAll("&amp;", "&");
		rect = rect.replaceAll("&quot;", "\"");
		rect = rect.replaceAll("&lt;", "<");
		rect = rect.replaceAll("&gt;", ">");
		rect = rect.replaceAll("&nbsp;", " ");
		return rect;
	}
	
	/**
	 * 去除所有html标签，去除样式部分和脚本部分
	 * 
	 * @param text
	 * @return
	 */
	public static String rmTags(String text) {
		if (text == null) {
			return "";
		}
		Document doc = Jsoup.parse(text.toLowerCase());
		return doc.text();
	}
	
	/**
	 * 过滤html标签<br>
	 * 该方法没有rmTags健壮，并且耗时是rmTags的2倍。
	 * 
	 * @param str
	 * @return
	 */
	@Deprecated
	public static String removeHtml(String str) {
		Pattern p = Pattern.compile("(<[^>]*>)");
		Matcher m = p.matcher(str);
		String findValue = null;
		while (m.find()) {
			findValue = m.group(0);
			str = str.replace(findValue, "");
		}
		return str;
	}

	/**
	 * 某些节点有position属性
	 * 
	 * @param html
	 * @return
	 */
	public static boolean havePosition(String html) {
		Document doc = Jsoup.parse(html.toLowerCase());
		Element body = doc.body();
		PositionVisitor visitor = new PositionVisitor();
		body.traverse(visitor);
		return visitor.isHavePosition();
	}

	/**
	 * 获取隐藏的内容. <br>
	 * &zwnj;和&zwj;这两个char都是零宽度的，其十进制整数分别是8024和8025
	 * 
	 * @param html
	 * @return
	 */
	public static String getHiddenContent(String html) {
		Document doc = Jsoup.parse(html.toLowerCase());
		Element body = doc.body();
		return getHiddenContent(body);//
	}

	/**
	 * 计算文本中html的节点数
	 * 
	 * @param html
	 * @return
	 */
	public static int getNodeCount(String html) {
		Document doc = Jsoup.parse(html.toLowerCase());
		Element body = doc.body();
		NodeCountVisitor visitor = new NodeCountVisitor();
		body.traverse(visitor);
		return visitor.getNodeCount() - 1;
	}

	/**
	 * 去除所有隐藏的不可见节点
	 * 
	 * @param html
	 * @return
	 */
	public static String rmHidden(String html) {
		Document doc = Jsoup.parse(html.toLowerCase());
		Element body = doc.body();
		rmHidden(body);
		return body.outerHtml().trim();
	}

	/**
	 * 抽取带颜色（不包括黑色和白色）的文本，把相同颜色的文本拼接在一起
	 * 
	 * @param html
	 * @return 返回一个list，list中的每个元素是一种颜色的文本
	 */
	public static List<Pair<RGB, String>> getColorText(String html) {
		Document doc = Jsoup.parse(html.toLowerCase());
		Element body = doc.body();
		List<Pair<RGB, String>> colortext = new ArrayList<Pair<RGB, String>>();
		ColorTextVisitor visitor = new ColorTextVisitor();
		body.traverse(visitor);
		List<Pair<RGB, String>> texts = visitor.getColorText();
		for (Pair<RGB, String> ele : texts) {
			addOneColorText2Collection(colortext, ele);
		}
		return colortext;
	}

	/**
	 * 节点是白色
	 * 
	 * @param element
	 * @return
	 */
	private static boolean nodeIsWhite(final Element element) {
		if (element == null) {
			return false;
		}

		Attributes attrs = element.attributes();
		String color = null;
		if (attrs.hasKey("style")) {
			Map<String, String> style = getStyle(attrs.get("style"));
			if (style.containsKey("color")) {
				color = style.get("color");
			}
		} else if (attrs.hasKey("color")) {
			color = attrs.get("color");
		}

		if (color != null) {
			RGB rgb = RGB.colorToRGB(color);
			if (rgb != null) {
				if (RGB.isWhite(rgb)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 节点是隐藏的
	 * 
	 * @param element
	 * @return
	 */
	private static boolean nodeIsHidden(final Element element) {
		if (element == null) {
			return false;
		}

		Attributes attrs = element.attributes();
		if (attrs.hasKey("style")) {
			Map<String, String> style = getStyle(attrs.get("style"));
			// style="display:none"
			if (style.containsKey("display")) {
				if ("none".equals(style.get("display"))) {
					return true;
				}
			}
		}
		// 只要有hidden属性就一定是隐藏的
		else if (attrs.hasKey("hidden")) {
			return true;
		}

		// 对于input标签，type="hidden"就是隐藏
		if ("input".equals(element.nodeName())) {
			if (attrs.hasKey("type")) {
				if ("hidden".equals(attrs.get("hidden"))) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * 节点的背景颜色是彩色
	 * 
	 * @param element
	 * @return
	 */
	private static boolean nodeBkgHaveColor(Node element) {
		if (element == null) {
			return false;
		}

		Attributes attrs = element.attributes();
		// 背景颜色包括background-color和text-shadow
		String bgkColor = null;
		String shadowColor = null;
		if (attrs.hasKey("style")) {
			Map<String, String> style = getStyle(attrs.get("style"));
			if (style.containsKey("background-color")) {
				bgkColor = style.get("background-color");
			} else if (style.containsKey("background")) {
				bgkColor = style.get("background");
			} else if (style.containsKey("text-shadow")) {
				shadowColor = style.get("text-shadow");
			}
		} else if (attrs.hasKey("background-color")) {
			bgkColor = attrs.get("background-color");
		} else if (attrs.hasKey("background")) {
			bgkColor = attrs.get("background");
		} else if (attrs.hasKey("text-shadow")) {
			shadowColor = attrs.get("text-shadow");
		}

		if (bgkColor != null) {
			RGB rgb = RGB.colorToRGB(bgkColor);
			if (rgb != null) {
				// background-color不是白色就可以返回true
				if (!RGB.isWhite(rgb)) {
					return true;
				}
			}
		} else if (shadowColor != null) {
			shadowColor = shadowColor
					.substring(0, shadowColor.indexOf(')') + 1);
			RGB rgb = RGB.colorToRGB(shadowColor);
			if (rgb != null) {
				// text-shadow不是白色就返回true
				if (!RGB.isWhite(rgb)) {
					return true;
				}
			}
		}

		return false;
	}

	private static String getHiddenContent(Element element) {
		if (element == null) {
			return null;
		}

		// 隐藏节点下的所有子节点都是隐藏的，即使某个子节点设置了style="display:bloack"
		if (nodeIsHidden(element)) {
			// 可以直接返回，不需要递归遍历子节点
			return element.text();
		}

		StringBuffer sb = new StringBuffer();

		// 节点是白色
		if (nodeIsWhite(element)) {
			String content = element.ownText();
			// 节点下直接有文本
			if (content != null && content.trim().length() > 0) {
				// 该节点的所有父节点背景颜色都不是彩色
				boolean ancestorHaveNoColorBkg = true;
				Element currNode = element;
				while (currNode != null) {
					if (nodeBkgHaveColor(currNode)) {
						ancestorHaveNoColorBkg = false;
						break;
					}
					currNode = currNode.parent();
				}

				if (ancestorHaveNoColorBkg) {
					// 把白色节点下的直接文本添加到sb当中
					sb.append(content.trim());
				}
			}
		}

		// 递归遍历子节点，查找隐藏文本
		Elements children = element.children();
		for (Element child : children) {
			String cont = getHiddenContent(child);
			if (cont != null && cont.trim().length() > 0) {
				sb.append(cont.trim());
			}
		}
		return sb.toString();
	}

	private static void rmHidden(Element element) {
		if (element == null) {
			return;
		}

		if (nodeIsHidden(element)) {// 节点是隐藏的
			element.remove();
		} else if (nodeIsWhite(element)) {// 节点是白色
			String content = element.ownText();
			// 节点下直接有文本
			if (content != null && content.trim().length() > 0) {
				// 该节点的所有父节点背景颜色都不是彩色
				boolean ancestorHaveNoColorBkg = true;
				Element currNode = element;
				while (currNode != null) {
					if (nodeBkgHaveColor(currNode)) {
						ancestorHaveNoColorBkg = false;
						break;
					}
					currNode = currNode.parent();
				}

				if (ancestorHaveNoColorBkg) {
					List<Node> children = element.childNodes();
					for (int i = 0; i < children.size(); i++) {
						String nodeName = children.get(i).nodeName();
						if ("#text".equals(nodeName)) {
							children.get(i).remove();
							i--;
						}
					}
				}
			}

		}
		Elements children = element.children();
		for (Element child : children) {
			rmHidden(child);
		}
	}

	/**
	 * 将style属性值组装成map
	 * 
	 * @param style
	 * @return
	 */
	private static Map<String, String> getStyle(String style) {
		Map<String, String> rect = new HashMap<String, String>();
		String[] arr = style.split(";");
		for (String ele : arr) {
			if (ele.trim().length() > 0) {
				String[] brr = ele.split(":");
				if (brr.length == 2) {
					rect.put(brr[0].trim(), brr[1].trim());
				}
			}
		}
		return rect;
	}

	/**
	 * 遍历节点是否有position属性
	 * 
	 * @Author:zhangchaoyang
	 * @Since:2014-8-6
	 * @Version:
	 */
	private static class PositionVisitor implements NodeVisitor {

		boolean havePosition = false;

		public boolean isHavePosition() {
			return havePosition;
		}

		public void head(Node node, int depth) {
			Attributes attrs = node.attributes();
			if (attrs.hasKey("style")) {
				Map<String, String> style = getStyle(attrs.get("style"));
				if (style.containsKey("position")) {
					havePosition = true;
				}
			} else if (attrs.hasKey("position")) {
				havePosition = true;
			}
		}

		public void tail(Node node, int depth) {

		}
	}

	/**
	 * 遍历节点，把同一种颜色（不包括黑色）且没有背景色的文本拼接在一起
	 * 
	 * @Author:zhangchaoyang
	 * @Since:2014-8-6
	 * @Version:
	 */
	private static class ColorTextVisitor implements NodeVisitor {

		List<Pair<RGB, String>> colorText = new ArrayList<Pair<RGB, String>>();

		List<Pair<RGB, String>> getColorText() {
			return colorText;
		}

		public void head(Node node, int depth) {
			Attributes attrs = node.attributes();
			String color = null;
			if (attrs.hasKey("style")) {
				Map<String, String> style = getStyle(attrs.get("style"));
				if (style.containsKey("color")) {
					color = style.get("color");
				}
			} else if (attrs.hasKey("color")) {
				color = attrs.get("color");
			}

			if (color != null) {
				RGB rgb = RGB.colorToRGB(color);

				if (rgb != null) {
					Vector vec = new Vector(rgb.getRed(), rgb.getGreen(),
							rgb.getBlue());
					double variance = 0;
					try {
						variance = vec.meanAndVariance().second;// R、G、B三个值判别比较明显（即方差较大），才可能是彩色
					} catch (DmArithmeticException e) {
						// 此处不可能抛出异常
					}
					// 字体颜色不是黑色或灰色
					if (!(rgb.getRed() < 50 && rgb.getGreen() < 50 && rgb
							.getBlue() < 50) && variance > 50) {
						// 背景颜色不是彩色
						if (!nodeBkgHaveColor(node)) {
							String newText = rmTags(node.outerHtml());
							addOneColorText2Collection(colorText,
									Pair.of(rgb, newText));
						}
					}
				}
			}
		}

		public void tail(Node node, int depth) {

		}
	}

	/**
	 * 计算子节点的个数
	 * 
	 * @Author:zhangchaoyang
	 * @Since:2014-8-11
	 * @Version:
	 */
	private static class NodeCountVisitor implements NodeVisitor {

		int cnt = 0;

		int getNodeCount() {
			return cnt;
		}

		public void head(Node node, int depth) {
			cnt++;
		}

		public void tail(Node node, int depth) {

		}

	}

	/**
	 * 把一个Pair<RGB, String>合并到一个list中
	 * 
	 * @param collection
	 * @param colorText
	 */
	private static void addOneColorText2Collection(
			List<Pair<RGB, String>> collection, Pair<RGB, String> colorText) {
		RGB rgb = colorText.first;
		String newText = colorText.second;
		boolean findNewColor = true;
		for (int i = 0; i < collection.size(); i++) {
			if (rgb.similarTo(collection.get(i).first)) {
				findNewColor = false;
				String text = collection.get(i).second;
				text = text + "|" + newText;
				collection.set(i, Pair.of(collection.get(i).first, text));
				break;
			}
		}
		if (findNewColor) {
			collection.add(Pair.of(rgb, newText));
		}
	}
}
