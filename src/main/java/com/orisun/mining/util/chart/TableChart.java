package com.orisun.mining.util.chart;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class TableChart {

	public static String toHtml(TableDataSet dataset) {
		String title = dataset.getTitle();
		List<String> header = dataset.getHeader();
		List<List<String>> value = dataset.getValue();
		if (header == null || value == null) {
			return null;
		}
		int colSize = header.size();
		int rowSize = value.size();
		StringBuffer sb = new StringBuffer();
		sb.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"5\">");
		if (title != null) {
			sb.append("<tr bgColor=\"#00B38A\"><th align=\"center\" colspan=\""
					+ colSize + "\">" + title + "</th></tr>");
		}
		sb.append("<tr bgColor=\"#00B38A\">");
		for (int i = 0; i < colSize; i++) {
			sb.append("<th align=\"center\">" + header.get(i) + "</th>");
		}
		sb.append("</tr>");
		for (int i = 0; i < rowSize; i++) {
			sb.append("<tr>");
			for (int j = 0; j < colSize && j < value.get(i).size(); j++) {
				sb.append("<td>" + value.get(i).get(j) + "</td>");
			}
			sb.append("</tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}

	public static void writeHtmlFile(TableDataSet dataset, String outfile)
			throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
		bw.write("<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/></head>");
		String html = toHtml(dataset);
		if (html != null) {
			bw.write(html);
		}
		bw.flush();
		bw.close();
	}

}
