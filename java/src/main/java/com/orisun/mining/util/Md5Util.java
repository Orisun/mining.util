package com.orisun.mining.util;

import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @Author:orisun
 * @Since:2015-12-8
 * @Version:1.0
 */
public class Md5Util {

	/**
	 * 对输入字符串进行散列，返回固定长度（32）的字符串。即使输入字符串长度为0，最终散列值也是32个字符的字符串
	 * 
	 * @param str
	 * @return
	 */
	public static String md5(String str) {
		byte[] btInput = str.getBytes();
		MessageDigest mdInst = null;
		try {
			mdInst = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		if (mdInst == null) {
			return null;
		}
		mdInst.update(btInput);
		byte[] md = mdInst.digest();
		return new String(Hex.encodeHex(md));
	}

	/**
	 * 
	 * 
	 * @param file
	 * @return
	 */
	public static String md5File(String file) {
		String result = null;
		try {
			byte[] cont = FileUtil.read(file);
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(cont);
			byte[] digest = mdInst.digest();
			result = new String(Hex.encodeHex(digest));
		} catch (IOException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return result;
	}
}
