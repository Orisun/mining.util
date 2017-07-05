package com.orisun.mining.util;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 文件操作工具类
 * 
 * @Author:zhangchaoyang
 * @Since:2014-7-9
 * @Version:
 */
public class FileUtil {

	/**
	 * 把文件压缩成zip格式，放到与原文件相同的目录下，文件名为原文件名后追加".zip"
	 * 
	 * @param inFile
	 *            输入文件
	 * @throws IOException
	 */
	public static void zip(String inFile) throws IOException {
		File f = new File(inFile);
		FileInputStream fis = new FileInputStream(f);
		BufferedInputStream bis = new BufferedInputStream(fis);
		byte[] buf = new byte[1024];
		int len;
		FileOutputStream fos = new FileOutputStream(f.getParent() + "/" + f.getName() + ".zip");
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		ZipOutputStream zos = new ZipOutputStream(bos);// 压缩包
		ZipEntry ze = new ZipEntry(f.getName());// 这是压缩包名里的文件名
		zos.putNextEntry(ze);// 写入新的 ZIP 文件条目并将流定位到条目数据的开始处

		while ((len = bis.read(buf)) != -1) {
			zos.write(buf, 0, len);
			zos.flush();
		}
		bis.close();
		zos.close();
	}

	/**
	 * 获取文件的大小。<br>
	 * FileInputStream.available()返回的int，如果文件真实长度大于Integer.MAX_VALUE就悲剧了<br>
	 * File.length()返回的是long，但是只适合于文本文件<br>
	 * FileChannel.size()是万全之策。
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static long getFileLength(File file) throws IOException {
		FileChannel fc = null;
		if (file.exists() && file.isFile()) {
			FileInputStream fis = new FileInputStream(file);
			fc = fis.getChannel();
			long len = fc.size();
			fc.close();
			fis.close();
			return len;
		} else {
			return 0L;
		}
	}

	/**
	 * 读取一个二进制文件
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static byte[] read(String file) throws IOException {
		File f = new File(file);
		long fileLen = getFileLength(f);
		FileInputStream fis = new FileInputStream(f);
		BufferedInputStream bis = new BufferedInputStream(fis);
		int len;
		byte[] buf = new byte[1024];
		ByteBuffer buffer = ByteBuffer.allocate((int) fileLen);
		while ((len = bis.read(buf)) != -1) {
			buffer.put(buf, 0, len);
		}
		bis.close();
		byte[] value = new byte[buffer.position()];
		buffer.flip();
		buffer.get(value);
		return value;
	}

	/**
	 * 把file中的内容追加到String容器中
	 * 
	 * @param file
	 *            输入参数
	 * @param lines
	 *            输出参数
	 */
	public static void readLines(String file, List<String> lines) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(file)));
			String line = null;
			while ((line = reader.readLine()) != null) {
				lines.add(line.trim());
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 把容器中的内容写入文件
	 * 
	 * @param file
	 *            输出参数
	 * @param counts
	 *            输入参数
	 */
	public static void writeLines(String file, List<?> counts) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(new File(file)));
			for (int i = 0; i < counts.size(); i++) {
				writer.write(counts.get(i) + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 把文件中的内容读取到一个set中。由于是set，无法保证句子在文件中的顺序
	 * 
	 * @param file
	 * @return
	 */
	public static Set<String> readUniqLines(String file) {
		BufferedReader reader = null;
		Set<String> set = new HashSet<String>();
		try {
			reader = new BufferedReader(new FileReader(new File(file)));
			String line = null;
			while ((line = reader.readLine()) != null) {
				set.add(line.trim());
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return set;
	}

	public static void tokenizeAndLowerCase(String line, List<String> tokens) {
		StringTokenizer strTok = new StringTokenizer(line);
		while (strTok.hasMoreTokens()) {
			String token = strTok.nextToken();
			tokens.add(token.toLowerCase().trim());
		}
	}

	public static List<String> readTokens(String file) {
		List<String> rect = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(file)));
			String line = null;
			while ((line = reader.readLine()) != null) {
				tokenizeAndLowerCase(line, rect);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return rect;
	}
}