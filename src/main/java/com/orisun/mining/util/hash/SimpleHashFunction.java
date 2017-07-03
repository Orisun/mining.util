package com.orisun.mining.util.hash;

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
public class SimpleHashFunction {

	public static long RSHash(String str) {
		int b = 378551;
		int a = 63689;
		long hash = 0;

		for (int i = 0; i < str.length(); i++) {
			hash = hash * a + str.charAt(i);
			a = a * b;
		}

		return hash;
	}

	public static long JSHash(String str) {
		long hash = 1315423911;

		for (int i = 0; i < str.length(); i++) {
			hash ^= ((hash << 5) + str.charAt(i) + (hash >> 2));
		}

		return hash;
	}

	/**
	 * 散列均匀性：好<br>
	 * 速度：快
	 * 
	 * @param str
	 * @return
	 */
	public static long PJWHash(String str) {
		long BitsInUnsignedInt = (long) (4 * 8);
		long ThreeQuarters = (long) ((BitsInUnsignedInt * 3) / 4);
		long OneEighth = (long) (BitsInUnsignedInt / 8);
		long HighBits = (long) (0xFFFFFFFF) << (BitsInUnsignedInt - OneEighth);
		long hash = 0;
		long test = 0;

		for (int i = 0; i < str.length(); i++) {
			hash = (hash << OneEighth) + str.charAt(i);

			if ((test = hash & HighBits) != 0) {
				hash = ((hash ^ (test >> ThreeQuarters)) & (~HighBits));
			}
		}

		return hash;
	}

	public static long ELFHash(String str) {
		long hash = 0;
		long x = 0;

		for (int i = 0; i < str.length(); i++) {
			hash = (hash << 4) + str.charAt(i);

			if ((x = hash & 0xF0000000L) != 0) {
				hash ^= (x >> 24);
			}
			hash &= ~x;
		}

		return hash;
	}

	public static long BKDRHash(String str) {
		long seed = 131; // 31 131 1313 13131 131313 etc..
		long hash = 0;

		for (int i = 0; i < str.length(); i++) {
			hash = (hash * seed) + str.charAt(i);
		}

		return hash;
	}

	public static long SDBMHash(String str) {
		long hash = 0;

		for (int i = 0; i < str.length(); i++) {
			hash = str.charAt(i) + (hash << 6) + (hash << 16) - hash;
		}

		return hash;
	}

	public static long DJBHash(String str) {
		long hash = 5381;

		for (int i = 0; i < str.length(); i++) {
			hash = ((hash << 5) + hash) + str.charAt(i);
		}

		return hash;
	}

	public static long DEKHash(String str) {
		long hash = str.length();

		for (int i = 0; i < str.length(); i++) {
			hash = ((hash << 5) ^ (hash >> 27)) ^ str.charAt(i);
		}

		return hash;
	}

	public static long BPHash(String str) {
		long hash = 0;

		for (int i = 0; i < str.length(); i++) {
			hash = hash << 7 ^ str.charAt(i);
		}

		return hash;
	}

	public static long FNVHash(String str) {
		long fnv_prime = 0x811C9DC5;
		long hash = 0;

		for (int i = 0; i < str.length(); i++) {
			hash *= fnv_prime;
			hash ^= str.charAt(i);
		}

		return hash;
	}

	public static long APHash(String str) {
		long hash = 0xAAAAAAAA;

		for (int i = 0; i < str.length(); i++) {
			if ((i & 1) == 0) {
				hash ^= ((hash << 7) ^ str.charAt(i) * (hash >> 3));
			} else {
				hash ^= (~((hash << 11) + str.charAt(i) ^ (hash >> 5)));
			}
		}

		return hash;
	}
}
