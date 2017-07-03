package com.orisun.mining.util.filter;  
  
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * 
 * @Author:zhangchaoyang
 * @Since:2014-7-9
 * @Version:
 */
public class TestTransferFont {

	@BeforeClass
	public static void setup() {
//		String file = TransferFont.class.getResource("/").getPath()
//				+ "/jianFan.dic";
//		TransferFont.SetFanJianFile(file);
	}

	@Test
	public void testS2T() {
		String sent = "清瑟怨遥夜，绕弦风雨哀。沟";
		String rect = TransferFont.conver(sent, 0);
		Assert.assertEquals(rect, "凊瑟葾滛液，隢妶颩雨哀。芶");

		sent = "清瑟怨遙夜，繞弦風雨哀。溝";
		rect = TransferFont.conver(sent, 1);
		Assert.assertEquals(rect, "清瑟怨遥夜，绕弦风雨哀。沟");
	}

	@Test
	public void testT2S() {
		String sent = "花落水流红， 閑愁萬種，無語怨東風。";
		String rect = TransferFont.conver(sent, 1);
		Assert.assertEquals(rect, "花落水流红， 闲愁万种，无语怨东风。");

		sent = "花落水流红， 闲愁万种，无语怨东风。";
		rect = TransferFont.conver(sent, 0);
		Assert.assertEquals(rect, "埖落渁蓅葒， 娴僽萭種，嘸娪葾崬颩。");
	}

	@Test
	public void textContainFanti() {
		String sent = "花落水流红， 閑愁萬種，無語怨東風。";
		Assert.assertTrue(TransferFont.containFanti(sent));
	}
}
