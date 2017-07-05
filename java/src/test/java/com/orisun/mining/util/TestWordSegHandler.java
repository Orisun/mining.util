package com.orisun.mining.util;


import com.orisun.mining.util.WordSegHandler.SegPair;
import com.orisun.mining.util.filter.TransferFont;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TestWordSegHandler {

	private static String configFolder = null;

	@BeforeClass
	public static void setup() throws IOException {
		URL systemBaseURL = ClassLoader.getSystemResource("");
		String sysBasePath = systemBaseURL.getPath();
		configFolder = sysBasePath + "config";
		// 加载用户自定义词典
		WordSegHandler.importUserDict(configFolder + "/dict/user.dic");
		// 读入繁简字体对照表
		TransferFont.SetFanJianFile(configFolder + "/dict/jianFan.dic");
	}

	@Test
	public void testUserDict() throws IOException {
		// 没有加载用户自定义词典前，“则已”不是一个词
		String content = "说得好,不如做的好。不干则已,干则一流!";
		List<String> words = WordSegHandler.wordSeg(content);
		for (String ele : words) {
			System.out.print(ele + "\t");
		}
		System.out.println();
		// 加载用户自定义词典后，“则已”是一个词
		WordSegHandler.importUserDict(configFolder + "/dict/user.dic");
		words = WordSegHandler.wordSeg(content);
		for (String ele : words) {
			System.out.print(ele + "\t");
		}
		System.out.println();
	}

	@Test
	public void testSeg() throws IOException {
		List<String> sents = new ArrayList<String>();
		// sents.add("淘宝美工兼职,,淘宝推广兼职,看图片下方我的联系方式.我叫艾宝艺");
		// sents.add("本人做涂硅己有十几年了,纸上涂硅和pet离型膜涂硅,有贵公司招聘请联系我。");
		// sents.add("说得好,不如做的好。不干则已,干则一流!");
		// sents.add("我主要是做市场流通的,快消品居多。");
		// sents.add("我们可以根据＜方＞你的需求,做你想要的证+件,快捷,详情咨询此QQ:30142-90188＜醋阎拐拍度母指倬＞");
		// sents.add("方便,快.速,办.出＜路＞各种有-效-证+件,你不要为办难而烦心了,有需要快速联系此QQ:30142-90188＜既偃即瓷倍涨感＞");
		// sents.add("方便,快.速,办.出各种有-效-证+件,＜滕＞你＜傅＞不要为办难而烦心了,有需要快速联系此QQ:30142-90188＜僖寻＞");
		// sents.add("可以快速办出＜蔺＞你想要的证+件,你有此项需要吗,快速方便,联系QQ:3014290188＜字挥赶蔷＞<br>普洱你需要做-证-件吗？");
		// sents.add("本人能够熟练操作办公室软件，性格比较沉稳，能吃苦耐劳，坚信吃得苦中苦，才能一步步走向成功。");
		// sents.add("本人为人诚实守信,待人和善.对公司工作认真负责,能吃苦耐劳,原IT业做过，门店销售,仓库,返修客服,渠道兼部门帐目等岗位做过。望贵公司给一次机会!");
		// sents.add("服务周到，为人正直，有门市做担保。。本人自配车，可拉大物件，速度快。。代排队、代接送、代发货。只需一个电话，即取即送。不是无所不能，但一定竭尽所能！！！");
		// sents.add("在淘宝开过网店");
		//sents.add("您好 , 找工作吗 ?");                                                                            也可以直接联络我 (13646206451) 金助理:");
		sents.add("公司主要生产：笔记本电脑、平板电脑、手机、主板、显卡、服务器、光存储.一、职位描述：作业员：岗位涉及笔记本电脑与手机的测试、组装、包装、插件、品检、物料、维修、SMT、CNC数控等，不穿无尘衣，坐着上班，工作安全、轻松、简单易操作"); 
		for (String sent : sents) {
			List<String> pairs = WordSegHandler.wordSeg(sent);
			for (String ele : pairs) {
				System.out.print(ele + "\t");
			}
			System.out.println();
		}
	}

	@Test
	public void testSegWithPos() throws IOException {
		List<String> sents = new ArrayList<String>();
		// sents.add("淘宝美工兼职,,淘宝推广兼职,看图片下方我的联系方式.我叫艾宝艺");
		// sents.add("本人做涂硅己有十几年了,纸上涂硅和pet离型膜涂硅,有贵公司招聘请联系我。");
		// sents.add("说得好,不如做的好。不干则已,干则一流!");
		// sents.add("我主要是做市场流通的,快消品居多。");
		// sents.add("我们可以根据＜方＞你的需求,做你想要的证+件,快捷,详情咨询此QQ:30142-90188＜醋阎拐拍度母指倬＞");
		// sents.add("方便,快.速,办.出＜路＞各种有-效-证+件,你不要为办难而烦心了,有需要快速联系此QQ:30142-90188＜既偃即瓷倍涨感＞");
		// sents.add("方便,快.速,办.出各种有-效-证+件,＜滕＞你＜傅＞不要为办难而烦心了,有需要快速联系此QQ:30142-90188＜僖寻＞");
		// sents.add("可以快速办出＜蔺＞你想要的证+件,你有此项需要吗,快速方便,联系QQ:3014290188＜字挥赶蔷＞<br>普洱你需要做-证-件吗？");
		// sents.add("本人能够熟练操作办公室软件，性格比较沉稳，能吃苦耐劳，坚信吃得苦中苦，才能一步步走向成功。");
		// sents.add("本人为人诚实守信,待人和善.对公司工作认真负责,能吃苦耐劳,原IT业做过，门店销售,仓库,返修客服,渠道兼部门帐目等岗位做过。望贵公司给一次机会!");
		// sents.add("服务周到，为人正直，有门市做担保。。本人自配车，可拉大物件，速度快。。代排队、代接送、代发货。只需一个电话，即取即送。不是无所不能，但一定竭尽所能！！！");
		// sents.add("在淘宝开过网店");
		sents.add("您好 , 找工作吗 ?                                                                             也可以直接联络我 (13646206451) 金助理:");
		for (String sent : sents) {
			List<SegPair> pairs = WordSegHandler.wordSegWithPos(sent);
			for (SegPair ele : pairs) {
				// System.out.print(ele.getWord() + "/" + ele.getPos() + "\t");
				System.out.print(ele.getWord() + "\t");
			}
			System.out.println();
		}
	}
}

