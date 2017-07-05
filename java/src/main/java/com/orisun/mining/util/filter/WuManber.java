package com.orisun.mining.util.filter;  
  
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Vector;

/**
 * 关键词多模匹配算法
 * 
 * @author zhangchaoyang
 * @since 2013-8-9
 */
public class WuManber {
	private int B = 1;// 块字符X的长度(模式串后缀字符的个数)，初始化跳跃表时要用到。按道理说B设为2是可以的，但是目前发现无法匹配关键字“刷”，所以设为B=1。B=1比B=2速度慢一倍

	private boolean initFlag = false;// 是否初始化
	// private UnionPatternSet unionPatternSet = new UnionPatternSet();
	private int maxIndex = (int) java.lang.Math.pow(2, 16);
	private int shiftTable[] = new int[maxIndex];
	public Vector<Vector<AtomicPattern>> hashTable = null;
	private UnionPatternSet tmpUnionPatternSet = new UnionPatternSet();

	public WuManber() {
		hashTable = new Vector<Vector<AtomicPattern>>();
		hashTable.setSize(maxIndex);
	}

	/**
	 * WM多模匹配
	 * 
	 * @param content
	 * @param levelSet
	 *            ？？？
	 * @return 返回命中的所有关键词及其在content中的位置（注意这里的位置是关键词结束的位置）
	 */
	public LinkedHashMap<Integer, String> match(String content,
			Vector<Integer> levelSet) {
		// StringBuffer sResult = new StringBuffer();
		LinkedHashMap<Integer, String> result = new LinkedHashMap<Integer, String>();
		if (initFlag == false)
			init();
		Vector<AtomicPattern> aps = new Vector<AtomicPattern>();
		for (int i = 0; i < content.length();) {
			char checkChar = content.charAt(i);
			if (shiftTable[checkChar] == 0) {
				Vector<AtomicPattern> tmpAps = new Vector<AtomicPattern>();
				tmpAps = findMathAps(content.substring(0, i + 1),
						hashTable.get(checkChar));
				aps.addAll(tmpAps);
				if (tmpAps.size() > 0) {
					result.put(i, tmpAps.get(0).getPattern().str);
					// sResult.append(tmpAps.get(0).getPattern().str).append("|");
				}
				i++;
			} else
				i = i + shiftTable[checkChar];
		}
		parseAtomicPatternSet(aps, levelSet);
		// return sResult.toString();
		return result;
	}

	/**
	 * 加入关键词
	 * 
	 * @param keyWord
	 * @param level
	 *            ???
	 * @return
	 */
	public boolean addFilterKeyWord(Set<String> keyWord, int level) {
		if (initFlag == true)
			return false;
		UnionPattern unionPattern = new UnionPattern();
		Object[] strArray = keyWord.toArray();
		for (int i = 0; i < strArray.length; i++) {
			String sPattern = (String) strArray[i];
			Pattern pattern = new Pattern(sPattern);
			AtomicPattern atomicPattern = new AtomicPattern(pattern);
			unionPattern.addNewAtomicPattrn(atomicPattern);
			unionPattern.setLevel(level);
			atomicPattern.setBelongUnionPattern(unionPattern);
		}
		tmpUnionPatternSet.addNewUnionPattrn(unionPattern);
		return true;
	}

	// 封装原子模式集
	private void parseAtomicPatternSet(Vector<AtomicPattern> aps,
			Vector<Integer> levelSet) {
		while (aps.size() > 0) {
			AtomicPattern ap = aps.get(0);
			UnionPattern up = ap.belongUnionPattern;
			if (up.isIncludeAllAp(aps) == true) {
				levelSet.add(new Integer(up.getLevel()));
			}
			aps.remove(0);
		}
	}

	// 查找原子模式
	private Vector<AtomicPattern> findMathAps(String src,
			Vector<AtomicPattern> destAps) {
		Vector<AtomicPattern> aps = new Vector<AtomicPattern>();
		for (int i = 0; i < destAps.size(); i++) {
			AtomicPattern ap = destAps.get(i);
			if (ap.findMatchInString(src) == true)
				aps.add(ap);
		}
		return aps;
	}

	// shift table and hash table of initialize
	private void init() {
		initFlag = true;
		for (int i = 0; i < maxIndex; i++)
			hashTable.set(i, new Vector<AtomicPattern>());
		shiftTableInit();
		hashTableInit();
	}

	// 清除
	public void clear() {
		tmpUnionPatternSet.clear();
		initFlag = false;
	}

	public boolean isEmptyWordsMatch() {
		return (tmpUnionPatternSet == null || tmpUnionPatternSet.getSet()
				.size() == 0);
	}

	// 初始化跳跃表
	private void shiftTableInit() {
		for (int i = 0; i < maxIndex; i++)
			shiftTable[i] = B;
		Vector<UnionPattern> upSet = tmpUnionPatternSet.getSet();
		for (int i = 0; i < upSet.size(); i++) {
			Vector<AtomicPattern> apSet = upSet.get(i).getSet();
			for (int j = 0; j < apSet.size(); j++) {
				AtomicPattern ap = apSet.get(j);
				Pattern pattern = ap.getPattern();
				// System.out.print(pattern.charAtEnd(1)+"\t");
				if (shiftTable[pattern.charAtEnd(1)] != 0)
					shiftTable[pattern.charAtEnd(1)] = 1;
				if (shiftTable[pattern.charAtEnd(0)] != 0)
					shiftTable[pattern.charAtEnd(0)] = 0;
			}
		}
	}

	// 初始化HASH表
	private void hashTableInit() {
		Vector<UnionPattern> upSet = tmpUnionPatternSet.getSet();
		for (int i = 0; i < upSet.size(); i++) {
			Vector<AtomicPattern> apSet = upSet.get(i).getSet();
			for (int j = 0; j < apSet.size(); j++) {
				AtomicPattern ap = apSet.get(j);
				Pattern pattern = ap.getPattern();
				if (pattern.charAtEnd(0) != 0) {
					hashTable.get(pattern.charAtEnd(0)).add(ap);
				}
			}
		}
	}
}

// 模式类
class Pattern {
	public String str;

	Pattern(String str) {
		this.str = str;
	}

	public char charAtEnd(int index) {
		if (str.length() > index) {
			return str.charAt(str.length() - index - 1);
		} else
			return 0;
	}

	public String getStr() {
		return str;
	};
}

// 原子模式类
class AtomicPattern {
	public boolean findMatchInString(String str) {
		if (this.pattern.str.length() > str.length())
			return false;
		int beginIndex = str.length() - this.pattern.str.length();
		String eqaulLengthStr = str.substring(beginIndex);
		if (this.pattern.str.equalsIgnoreCase(eqaulLengthStr))
			return true;
		return false;
	}

	AtomicPattern(Pattern pattern) {
		this.pattern = pattern;
	};

	private Pattern pattern;
	public UnionPattern belongUnionPattern;

	public UnionPattern getBelongUnionPattern() {
		return belongUnionPattern;
	}

	public void setBelongUnionPattern(UnionPattern belongUnionPattern) {
		this.belongUnionPattern = belongUnionPattern;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}
}

// 相同的原子模式集类
class SameAtomicPatternSet {
	SameAtomicPatternSet() {
		SAPS = new Vector<AtomicPattern>();
	}

	public Vector<AtomicPattern> SAPS;
}

// 合并的模式类
class UnionPattern {
	// union string
	UnionPattern() {
		this.apSet = new Vector<AtomicPattern>();
	}

	public Vector<AtomicPattern> apSet;

	public void addNewAtomicPattrn(AtomicPattern ap) {
		this.apSet.add(ap);
	}

	public Vector<AtomicPattern> getSet() {
		return apSet;
	}

	public boolean isIncludeAllAp(Vector<AtomicPattern> inAps) {
		if (apSet.size() > inAps.size())
			return false;
		for (AtomicPattern ap : apSet) {
			if (isInAps(ap, inAps) == false)
				return false;
		}
		return true;
	}

	private boolean isInAps(AtomicPattern ap, Vector<AtomicPattern> inAps) {
		for (AtomicPattern destAp : inAps) {
			if (ap.getPattern().str.equalsIgnoreCase(destAp.getPattern().str) == true)
				return true;
		}
		return false;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getLevel() {
		return this.level;
	}

	private int level;
}

// 合并的模式集子类

class UnionPatternSet { // union string set
	public Vector<UnionPattern> unionPatternSet;

	UnionPatternSet() {
		this.unionPatternSet = new Vector<UnionPattern>();
	}

	public void addNewUnionPattrn(UnionPattern up) {
		this.unionPatternSet.add(up);
	}

	public Vector<UnionPattern> getSet() {
		return unionPatternSet;
	}

	public void clear() {
		unionPatternSet.clear();
	}
}
