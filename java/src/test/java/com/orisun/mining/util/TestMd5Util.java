package com.orisun.mining.util;

import org.junit.Assert;
import org.junit.Test;

public class TestMd5Util {

	@Test
	public void test() {
		String digest1 = Md5Util.md5("rewg5gertreht");
		String digest2 = Md5Util.md5("rewg5gertreht");
		String digest3 = Md5Util.md5("rewg7gertreht");
		Assert.assertEquals(digest1, digest2);
		Assert.assertFalse(digest1.equals(digest3));
		Assert.assertEquals(digest3.length(), 32);
		String digest4 = Md5Util.md5(
				"rewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrerewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehhtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertrehtrewg5g7rtrehtrewg5gertreht");
		String digest5 = Md5Util.md5("");
		Assert.assertEquals(digest4.length(), 32);
		Assert.assertEquals(digest5.length(), 32);
	}

	@Test
	public void testFile() {
		String file = Path.getCurrentPath() + "/config/dm.keytab";
		Assert.assertEquals("ae5b133264c8f5ed6e610c8536db6fbe", Md5Util.md5File(file));
	}
}
