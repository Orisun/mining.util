package com.orisun.mining.util.monitor;

import com.orisun.mining.util.Path;
import com.orisun.mining.util.SystemConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestSendMail {

	public static void main(String[] args) throws IOException {
		String basePath = Path.getCurrentPath();
		SystemConfig.init(basePath + "/config/system.properties");
		String subject = "Test";
		String receiver = "orisun@lagou.com";
		String content = "hello!";
		List<String> imgs = new ArrayList<String>();
		imgs.add(basePath + "/data/hist.png");
		List<String> files = new ArrayList<String>();
		files.add(basePath + "/data/buy.txt");
		boolean rect = SendMail.getInstance().sendMail(subject, receiver, content, imgs, files);
		System.out.println(rect);
	}
}
