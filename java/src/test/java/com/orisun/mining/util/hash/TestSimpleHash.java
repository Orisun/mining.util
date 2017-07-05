package com.orisun.mining.util.hash;

import com.orisun.mining.util.Path;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * 对各个Hash对数字进行哈希的均匀性和速度进行了测试。结论如下：<br>
 * 均匀性较好有PJWHash、BKDRHash、BPHash、DEKHash、ELFHash，它们的耗时递增。<br>
 * 具体测试结果如下：
 * <ul>
 * <li>original uniformity is 0.33470318791326514 : 0.3311200177311514 :
 * 0.33417679435558345
 * <li>APHash use time 13, uniformity is 0.3331424771896125 : 0.1828432640094566
 * : 0.4840142588009309
 * <li>PJWHash use time 13, uniformity is 0.3348694174577962 :
 * 0.33470318791326514 : 0.3304273946289387
 * <li>BKDRHash use time 16, uniformity is 0.3350725869011119 :
 * 0.33246832403679216 : 0.332459089062096
 * <li>DJBHash use time 17, uniformity is 0.400142218610321 : 0.3009493553987662
 * : 0.2989084259909128
 * <li>BPHash use time 17, uniformity is 0.3350725869011119 :
 * 0.33246832403679216 : 0.332459089062096
 * <li>JSHash use time 17, uniformity is 0.3329854826197776 :
 * 0.03559159247903661 : 0.6314229249011858
 * <li>SDBMHash use time 18, uniformity is 0.3335672860256363 :
 * 0.15645894130250082 : 0.5099737726718628
 * <li>RSHash use time 19, uniformity is 0.3347955376602268 :
 * 0.16775331535591592 : 0.4974511469838573
 * <li>DEKHash use time 40, uniformity is 0.33116619260463226 :
 * 0.3334195264304976 : 0.33541428096487014
 * <li>FNVHash use time 47, uniformity is 0.3326899634295002 :
 * 0.1677256104318274 : 0.4995844261386724
 * <li>ELFHash use time 49, uniformity is 0.3348694174577962 :
 * 0.33470318791326514 : 0.3304273946289387
 * </ul>
 * 
 * @author orisun
 * @since 2016年9月6日
 */
public class TestSimpleHash {

	private static List<String> number = new LinkedList<String>();

	@BeforeClass
	public static void setup() throws IOException {
		String infile = Path.getCurrentPath() + "/data/userid.txt";
		String outfile = Path.getCurrentPath() + "/data/userid.hash";
		BufferedReader br = new BufferedReader(new FileReader(new File(infile)));
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outfile)));
		String line = null;
		int cnt0 = 0;
		int cnt1 = 0;
		int cnt2 = 0;
		while ((line = br.readLine()) != null) {
			String userid = line.trim();
			if (userid.length() > 0) {
				number.add(userid);
				int ele = Integer.parseInt(userid);
				if (ele % 3 == 0) {
					cnt0++;
				} else if (ele % 3 == 1) {
					cnt1++;
				} else {
					cnt2++;
				}
			}
		}
		bw.close();
		br.close();
		System.out.println("original uniformity is " + 1.0 * cnt0 / number.size() + " : " + 1.0 * cnt1 / number.size()
				+ " : " + 1.0 * cnt2 / number.size());
	}

	@Test
	public void testAPHash() {
		long begin = System.currentTimeMillis();
		List<Long> hash = new LinkedList<Long>();
		for (String str : number) {
			hash.add(SimpleHashFunction.APHash(str));
		}
		long end = System.currentTimeMillis();
		int cnt0 = 0;
		int cnt1 = 0;
		int cnt2 = 0;
		for (Long ele : hash) {
			if (ele % 3 == 0) {
				cnt0++;
			} else if (ele % 3 == 1) {
				cnt1++;
			} else {
				cnt2++;
			}
		}
		System.out.println("APHash use time " + (end - begin) + ", uniformity is " + 1.0 * cnt0 / hash.size() + " : "
				+ 1.0 * cnt1 / hash.size() + " : " + 1.0 * cnt2 / hash.size());
	}

	@Test
	public void testBKDRHash() {
		long begin = System.currentTimeMillis();
		List<Long> hash = new LinkedList<Long>();
		for (String str : number) {
			hash.add(SimpleHashFunction.BKDRHash(str));
		}
		long end = System.currentTimeMillis();
		int cnt0 = 0;
		int cnt1 = 0;
		int cnt2 = 0;
		for (Long ele : hash) {
			if (ele % 3 == 0) {
				cnt0++;
			} else if (ele % 3 == 1) {
				cnt1++;
			} else {
				cnt2++;
			}
		}
		System.out.println("BKDRHash use time " + (end - begin) + ", uniformity is " + 1.0 * cnt0 / hash.size() + " : "
				+ 1.0 * cnt1 / hash.size() + " : " + 1.0 * cnt2 / hash.size());
	}

	@Test
	public void testBPHash() {
		long begin = System.currentTimeMillis();
		List<Long> hash = new LinkedList<Long>();
		for (String str : number) {
			hash.add(SimpleHashFunction.BPHash(str));
		}
		long end = System.currentTimeMillis();
		int cnt0 = 0;
		int cnt1 = 0;
		int cnt2 = 0;
		for (Long ele : hash) {
			if (ele % 3 == 0) {
				cnt0++;
			} else if (ele % 3 == 1) {
				cnt1++;
			} else {
				cnt2++;
			}
		}
		System.out.println("BPHash use time " + (end - begin) + ", uniformity is " + 1.0 * cnt0 / hash.size() + " : "
				+ 1.0 * cnt1 / hash.size() + " : " + 1.0 * cnt2 / hash.size());
	}

	@Test
	public void testDEKHash() {
		long begin = System.currentTimeMillis();
		List<Long> hash = new LinkedList<Long>();
		for (String str : number) {
			hash.add(SimpleHashFunction.DEKHash(str));
		}
		long end = System.currentTimeMillis();
		int cnt0 = 0;
		int cnt1 = 0;
		int cnt2 = 0;
		for (Long ele : hash) {
			if (ele % 3 == 0) {
				cnt0++;
			} else if (ele % 3 == 1) {
				cnt1++;
			} else {
				cnt2++;
			}
		}
		System.out.println("DEKHash use time " + (end - begin) + ", uniformity is " + 1.0 * cnt0 / hash.size() + " : "
				+ 1.0 * cnt1 / hash.size() + " : " + 1.0 * cnt2 / hash.size());
	}

	@Test
	public void testDJBHash() {
		long begin = System.currentTimeMillis();
		List<Long> hash = new LinkedList<Long>();
		for (String str : number) {
			hash.add(SimpleHashFunction.DJBHash(str));
		}
		long end = System.currentTimeMillis();
		int cnt0 = 0;
		int cnt1 = 0;
		int cnt2 = 0;
		for (Long ele : hash) {
			if (ele % 3 == 0) {
				cnt0++;
			} else if (ele % 3 == 1) {
				cnt1++;
			} else {
				cnt2++;
			}
		}
		System.out.println("DJBHash use time " + (end - begin) + ", uniformity is " + 1.0 * cnt0 / hash.size() + " : "
				+ 1.0 * cnt1 / hash.size() + " : " + 1.0 * cnt2 / hash.size());
	}

	@Test
	public void testELFHash() {
		long begin = System.currentTimeMillis();
		List<Long> hash = new LinkedList<Long>();
		for (String str : number) {
			hash.add(SimpleHashFunction.ELFHash(str));
		}
		long end = System.currentTimeMillis();
		int cnt0 = 0;
		int cnt1 = 0;
		int cnt2 = 0;
		for (Long ele : hash) {
			if (ele % 3 == 0) {
				cnt0++;
			} else if (ele % 3 == 1) {
				cnt1++;
			} else {
				cnt2++;
			}
		}
		System.out.println("ELFHash use time " + (end - begin) + ", uniformity is " + 1.0 * cnt0 / hash.size() + " : "
				+ 1.0 * cnt1 / hash.size() + " : " + 1.0 * cnt2 / hash.size());
	}

	@Test
	public void testFNVHash() {
		long begin = System.currentTimeMillis();
		List<Long> hash = new LinkedList<Long>();
		for (String str : number) {
			hash.add(SimpleHashFunction.FNVHash(str));
		}
		long end = System.currentTimeMillis();
		int cnt0 = 0;
		int cnt1 = 0;
		int cnt2 = 0;
		for (Long ele : hash) {
			if (ele % 3 == 0) {
				cnt0++;
			} else if (ele % 3 == 1) {
				cnt1++;
			} else {
				cnt2++;
			}
		}
		System.out.println("FNVHash use time " + (end - begin) + ", uniformity is " + 1.0 * cnt0 / hash.size() + " : "
				+ 1.0 * cnt1 / hash.size() + " : " + 1.0 * cnt2 / hash.size());
	}

	@Test
	public void testPJWHash() {
		long begin = System.currentTimeMillis();
		List<Long> hash = new LinkedList<Long>();
		for (String str : number) {
			hash.add(SimpleHashFunction.PJWHash(str));
		}
		long end = System.currentTimeMillis();
		int cnt0 = 0;
		int cnt1 = 0;
		int cnt2 = 0;
		for (Long ele : hash) {
			if (ele % 3 == 0) {
				cnt0++;
			} else if (ele % 3 == 1) {
				cnt1++;
			} else {
				cnt2++;
			}
		}
		System.out.println("PJWHash use time " + (end - begin) + ", uniformity is " + 1.0 * cnt0 / hash.size() + " : "
				+ 1.0 * cnt1 / hash.size() + " : " + 1.0 * cnt2 / hash.size());
	}

	@Test
	public void testRSHash() {
		long begin = System.currentTimeMillis();
		List<Long> hash = new LinkedList<Long>();
		for (String str : number) {
			hash.add(SimpleHashFunction.RSHash(str));
		}
		long end = System.currentTimeMillis();
		int cnt0 = 0;
		int cnt1 = 0;
		int cnt2 = 0;
		for (Long ele : hash) {
			if (ele % 3 == 0) {
				cnt0++;
			} else if (ele % 3 == 1) {
				cnt1++;
			} else {
				cnt2++;
			}
		}
		System.out.println("RSHash use time " + (end - begin) + ", uniformity is " + 1.0 * cnt0 / hash.size() + " : "
				+ 1.0 * cnt1 / hash.size() + " : " + 1.0 * cnt2 / hash.size());
	}

	@Test
	public void testSDBMHash() {
		long begin = System.currentTimeMillis();
		List<Long> hash = new LinkedList<Long>();
		for (String str : number) {
			hash.add(SimpleHashFunction.SDBMHash(str));
		}
		long end = System.currentTimeMillis();
		int cnt0 = 0;
		int cnt1 = 0;
		int cnt2 = 0;
		for (Long ele : hash) {
			if (ele % 3 == 0) {
				cnt0++;
			} else if (ele % 3 == 1) {
				cnt1++;
			} else {
				cnt2++;
			}
		}
		System.out.println("SDBMHash use time " + (end - begin) + ", uniformity is " + 1.0 * cnt0 / hash.size() + " : "
				+ 1.0 * cnt1 / hash.size() + " : " + 1.0 * cnt2 / hash.size());
	}

	@Test
	public void testJSHash() {
		long begin = System.currentTimeMillis();
		List<Long> hash = new LinkedList<Long>();
		for (String str : number) {
			hash.add(SimpleHashFunction.JSHash(str));
		}
		long end = System.currentTimeMillis();
		int cnt0 = 0;
		int cnt1 = 0;
		int cnt2 = 0;
		for (Long ele : hash) {
			if (ele % 3 == 0) {
				cnt0++;
			} else if (ele % 3 == 1) {
				cnt1++;
			} else {
				cnt2++;
			}
		}
		System.out.println("JSHash use time " + (end - begin) + ", uniformity is " + 1.0 * cnt0 / hash.size() + " : "
				+ 1.0 * cnt1 / hash.size() + " : " + 1.0 * cnt2 / hash.size());
	}
}
