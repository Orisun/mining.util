package com.orisun.mining.util.monitor;

import com.alibaba.fastjson.JSONObject;
import com.orisun.mining.util.Md5Util;
import com.orisun.mining.util.NIC;
import com.orisun.mining.util.SystemConfig;
import com.orisun.mining.util.cache.TimeoutCache;
import love.cq.util.StringUtil;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @Description: 发送邮件。10分钟内相同内容的邮件不会重复发送
 * @Author orisun
 * @Date Jun 15, 2016
 */
@SuppressWarnings("restriction")
public class SendMail {

	private static final Log logger = LogFactory.getLog(SendMail.class);
	private TimeoutCache<String, Integer> mailSent = new TimeoutCache<String, Integer>();
	private String url = null;
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	private static volatile SendMail instance = null;

	private SendMail() {
		url = SystemConfig.getValue("mail_url");
	}

	/**
	 * 单例
	 * 
	 * @return
	 */
	public static SendMail getInstance() {
		if (instance == null) {
			synchronized (SendMail.class) {
				if (instance == null) {
					instance = new SendMail();
				}
			}
		}
		return instance;
	}

	/**
	 * 发邮件<br>
	 * 同样的内容短期内发送过，就不再发送
	 * 
	 * @param subject
	 * @param receivers
	 *            多个receiver之间用逗号或者分号分隔
	 * @param content
	 * @return
	 */
	public boolean sendMail(String subject, String receivers, String content) {
		return sendMail(subject, receivers, content, null, null);
	}

	/**
	 * 发邮件<br>
	 * 同样的内容短期内发送过，就不再发送
	 * 
	 * @param subject
	 * @param receivers
	 *            多个receiver之间用逗号或者分号分隔
	 * @param content
	 * @param imgFiles
	 *            邮件正文中插入图片
	 * @param attachFiles
	 *            附件
	 * @return
	 */
	public boolean sendMail(String subject, String receivers, String content, List<String> imgFiles,
			List<String> attachFiles) {
		boolean success = false;
		if (StringUtil.isBlank(subject)) {
			logger.error("subject of mail is empty");
			return success;
		}
		if (StringUtil.isBlank(receivers)) {
			logger.error("receivers of mail is empty");
			return success;
		}
		if (StringUtil.isBlank(content)) {
			logger.error("content of mail is empty");
			return success;
		}
		String selfName = NIC.getLocalHostName();
		// 在邮件正文的开头加上服务器的名称
		StringBuilder contentSB = new StringBuilder();
		contentSB.append(content);
		String digest = Md5Util.md5(content);
		Integer v = mailSent.get(digest);
		if (v != null) {
			return success; // 同样的内容短期内发送过，就不再发送
		}

		PostMethod method = null;
		try {
			method = new PostMethod(url);
			JSONObject jsonObject = new JSONObject();

			jsonObject.put("subject", subject);
			jsonObject.put("recip", receivers.split("[,;]"));
			if (imgFiles != null && imgFiles.size() > 0) {
				List<String[]> imgs = new ArrayList<String[]>();
				for (int i = 0; i < imgFiles.size(); i++) {
					imgs.add(new String[] { (i + 1) + ".png", base64File(imgFiles.get(i)) });
					contentSB.append("<br><img src=\"cid:" + (i + 1) + ".png\">");
				}
				jsonObject.put("image", imgs.toArray(new String[0][]));
			}
			// 注明邮件是哪台机器发的
			contentSB.append("<br>");
			contentSB.append("from ");
			contentSB.append(selfName);
			contentSB.append("<br>");
			contentSB.append(sdf.format(new Date()));
			jsonObject.put("content", contentSB.toString());
			if (attachFiles != null && attachFiles.size() > 0) {
				List<String[]> files = new ArrayList<String[]>();
				for (int i = 0; i < attachFiles.size(); i++) {
					files.add(new String[] { new File(attachFiles.get(i)).getName(), base64File(attachFiles.get(i)) });
				}
				jsonObject.put("file", files.toArray(new String[0][]));
			}

			String transJson = jsonObject.toString();
			RequestEntity se = new StringRequestEntity(transJson, "application/json", "UTF-8");
			method.setRequestEntity(se);
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
			method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
			HttpClient httpClient = new HttpClient();
			int statusCode = httpClient.executeMethod(method);
			if (statusCode == HttpStatus.SC_OK) {
				success = true;
				mailSent.put(digest, 1, 10, TimeUnit.MINUTES); // 发送成功后就加入缓存，防止短期内重复发送
			}
		} catch (Exception e) {
			logger.error("send mail error", e);
		}
		if (!success) {
			logger.error("send mail failed");
		}
		return success;
	}

	/**
	 * 把一个文件转换成Base64编码
	 * 
	 * @param imgFile
	 * @return
	 */
	private String base64File(String imgFile) {
		InputStream in = null;
		byte[] data = null;
		// 读取图片字节数组
		try {
			in = new FileInputStream(imgFile);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			logger.error("read file failed", e);
		}
		if (data != null) {
			// 对字节数组Base64编码
			BASE64Encoder encoder = new BASE64Encoder();
			return encoder.encode(data);// 返回Base64编码过的字节数组字符串
		}
		return "";
	}
}
