package com.orisun.mining.util.plsa;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * 最初的代码来自于https://code.google.com/archive/p/mltool4j/，源代码在计算p(z|d,w)时使用了p(z)，
 * 但是在传统的PLSA算法中p(z)根本就没有出现过，所以我对源代码做了改动。
 * 
 * @author orisun
 * @date 2016年7月13日
 */
public class PLSA {
	private Dataset dataset = null;
	private Posting[][] invertedIndex = null;
	private int M = -1; // 文档数
	private int V = -1; // 词汇数
	private int K = -1; // 主题数
	// p(z|d), size: M x K
	private double[][] Pz_d;
	// p(w|z), size: K x V
	private double[][] Pw_z;
	// p(z|d,w), size: M x K x doc.size()
	private double[][][] Pz_dw;

	/**
	 * 用PLSA对文档进行向量化表示，即表示出每个文档在每个主题上的概率分布
	 * 
	 * @param datafilePath
	 *            语料集。可以是一个文件，也可以是一个目录
	 * @param ntopics
	 *            主题的个数
	 * @param iters
	 *            最大迭代次数
	 * @return
	 */
	public boolean doPLSA(String datafilePath, int ntopics, int iters) {
		try {
			this.dataset = new Dataset(datafilePath);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		this.M = this.dataset.size();
		this.V = this.dataset.getFeatureNum();
		this.K = ntopics;

		Pz_d = new double[this.M][this.K];
		Pw_z = new double[this.K][this.V];
		Pz_dw = new double[this.M][this.K][];

		// 建立term-->doc的倒排索引，在计算p(w|z)时可以提高速度
		this.buildInvertedIndex(this.dataset);
		this.runEM(iters);
		return true;
	}

	/**
	 * 建立term-->doc的倒排索引，在计算p(w|z)时可以提高速度
	 * 
	 * @param ds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean buildInvertedIndex(Dataset ds) {
		ArrayList<Posting>[] list = new ArrayList[this.V];
		for (int k = 0; k < this.V; ++k) {
			list[k] = new ArrayList<Posting>();
		}

		for (int m = 0; m < this.M; m++) {
			Data d = ds.getDataAt(m);
			for (int position = 0; position < d.size(); position++) {
				int w = d.getFeatureAt(position).dim;
				list[w].add(new Posting(m, position));
			}
		}
		this.invertedIndex = new Posting[this.V][];
		for (int w = 0; w < this.V; w++) {
			this.invertedIndex[w] = list[w].toArray(new Posting[0]);
		}
		return true;
	}

	private boolean runEM(int iters) {
		// L: log-likelihood value
		double L = -1;

		// 初始时，随机初始化参数
		this.init();
		for (int it = 0; it < iters; it++) {
			System.out.println("iteration " + it);
			// E-step
			if (!this.Estep()) {
				System.out.println("EM,  in E-step");
			}

			// M-step
			if (!this.Mstep()) {
				System.out.println("EM, in M-step");
			}

			File modelPath = new File("model");
			if (modelPath.exists()) {
				if (modelPath.isFile()) {
					modelPath.delete();
					modelPath.mkdirs();
				}
			} else {
				modelPath.mkdirs();
			}
			// 进入最后几轮迭代时，保存参数
			if (it > iters - 10) {
				L = calcLoglikelihood();
				System.out.println("[" + it + "]" + "\tlikelihood: " + L);
				outputPzd("model/doc_topic." + it);// 即文档向量
				outputPwz("model/topic_word." + it);
			}
		}

		return false;
	}

	/**
	 * 拿计算好的文档向量，去计算所有文档跟第1篇文档的相似度。以此来验证PLSA得到的文档向量是合理的。
	 */
	public void test(String docVecFile) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(docVecFile));
			String line = br.readLine();
			if (line == null) {
				return;
			}
			String[] arr = line.split("\\s+");
			if (arr.length < 1 + this.K) {
				System.err.println("1st doc vector's length is less than " + this.K);
				return;
			}
			double[] vec1 = new double[this.K];
			double norm1 = 0.0;// 向量模长
			for (int i = 1; i < 1 + this.K; i++) {
				vec1[i - 1] = Double.parseDouble(arr[i]);
				norm1 += vec1[i - 1] * vec1[i - 1];
			}
			norm1 = Math.sqrt(norm1);
			Map<String, Double> simMap = new HashMap<String, Double>();
			while ((line = br.readLine()) != null) {
				arr = line.split("\\s+");
				if (arr.length == 1 + this.K) {
					String docName = arr[0];
					double[] vec2 = new double[this.K];
					double norm2 = 0.0;// 向量模长
					double prod = 0.0;// 向量内积
					for (int i = 1; i < 1 + this.K; i++) {
						vec2[i - 1] = Double.parseDouble(arr[i]);
						norm2 += vec2[i - 1] * vec2[i - 1];
						prod += vec1[i - 1] * vec2[i - 1];
					}
					norm2 = Math.sqrt(norm2);
					double sim = prod / (norm1 * norm2);
					simMap.put(docName, sim);
				}
			}

			// 按相似度从大到小排序
			List<Entry<String, Double>> simList = new ArrayList<Entry<String, Double>>(simMap.entrySet());
			Collections.sort(simList, new Comparator<Entry<String, Double>>() {
				@Override
				public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
					if (o1.getValue() > o2.getValue()) {
						return -1;
					} else if (o1.getValue() < o2.getValue()) {
						return 1;
					} else {
						return 0;
					}
				}
			});
			// 输出前100个与文档1最相似的文档
			for (int i = 0; i < 100 && i < simList.size(); i++) {
				System.out.println(simList.get(i).getKey() + "\t" + simList.get(i).getValue());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
			}
		}

	}

	private boolean init() {
		// p(z|d), size: M x K
		for (int m = 0; m < this.M; m++) {
			double norm = 0.0;
			for (int z = 0; z < this.K; z++) {
				Pz_d[m][z] = Math.random();
				norm += Pz_d[m][z];
			}

			for (int z = 0; z < this.K; z++) {
				Pz_d[m][z] /= norm;
			}
		}

		// p(w|z), size: K x V
		for (int z = 0; z < this.K; z++) {
			double norm = 0.0;
			for (int w = 0; w < this.V; w++) {
				Pw_z[z][w] = Math.random();
				norm += Pw_z[z][w];
			}

			for (int w = 0; w < this.V; w++) {
				Pw_z[z][w] /= norm;
			}
		}

		// p(z|d,w), size: M x K x doc.size()
		for (int m = 0; m < this.M; m++) {
			for (int z = 0; z < this.K; z++) {
				Pz_dw[m][z] = new double[this.dataset.getDataAt(m).size()];
			}
		}
		return false;
	}

	private boolean Estep() {
		for (int m = 0; m < this.M; m++) {
			Data data = this.dataset.getDataAt(m);
			for (int position = 0; position < data.size(); position++) {
				// get word(dimension) at current position of document m
				int w = data.getFeatureAt(position).dim;
				double norm = 0.0;
				for (int z = 0; z < this.K; z++) {
					double val = Pz_d[m][z] * Pw_z[z][w];
					Pz_dw[m][z][position] = val;
					norm += val;
				}
				// 当前文档中的当前词，在各个主题上的概率分布进行归一化
				for (int z = 0; z < this.K; z++) {
					Pz_dw[m][z][position] /= norm;
				}
			}
		}
		return true;
	}

	private boolean Mstep() {
		// p(z|d)
		for (int m = 0; m < this.M; m++) {
			double norm = 0.0;
			for (int z = 0; z < this.K; z++) {
				double sum = 0.0;
				Data d = this.dataset.getDataAt(m);
				for (int position = 0; position < d.size(); position++) {
					double n = d.getFeatureAt(position).weight;
					sum += n * Pz_dw[m][z][position];
				}
				Pz_d[m][z] = sum;
				norm += sum;
			}

			// normalization
			for (int z = 0; z < this.K; z++) {
				Pz_d[m][z] /= norm;
			}
		}

		// p(w|z)
		for (int z = 0; z < this.K; z++) {
			double norm = 0.0;
			for (int w = 0; w < this.V; w++) {
				double sum = 0.0;
				Posting[] postings = this.invertedIndex[w];
				for (Posting posting : postings) {
					int m = posting.docID;
					int position = posting.pos;
					double n = this.dataset.getDataAt(m).getFeatureAt(position).weight;
					sum += n * Pz_dw[m][z][position];
				}
				Pw_z[z][w] = sum;
				norm += sum;
			}
			// normalization
			for (int w = 0; w < this.V; w++) {
				Pw_z[z][w] /= norm;
			}
		}

		return true;
	}

	private double calcLoglikelihood() {
		double L = 0.0;
		for (int m = 0; m < this.M; m++) {
			Data d = this.dataset.getDataAt(m);
			for (int position = 0; position < d.size(); position++) {
				Feature f = d.getFeatureAt(position);
				int w = f.dim;
				double n = f.weight;

				double sum = 0.0;
				for (int z = 0; z < this.K; z++) {
					sum += Pz_d[m][z] * Pw_z[z][w];
				}
				L += n * Math.log10(sum);
			}
		}
		return L;
	}

	/**
	 * 输出每篇文档在各个主题上的概率分布
	 * 
	 * @param outFile
	 */
	public void outputPzd(String outFile) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(outFile));
			for (int i = 0; i < this.M; i++) {
				String docName = this.dataset.getDataAt(i).docName;
				bw.write(docName);
				for (int j = 0; j < this.K; j++) {
					bw.write("\t");
					bw.write(String.valueOf(Pz_d[i][j]));
				}
				bw.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 输出每个主题下的top100的词
	 * 
	 * @param outFile
	 */
	public void outputPwz(String outFile) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(outFile));
			for (int i = 0; i < Pw_z.length; i++) {
				Map<String, Double> wordWeight = new HashMap<String, Double>();// 词在该主题下的权重
				for (int j = 0; j < Pw_z[i].length; j++) {
					String word = this.dataset.features.get(j);
					wordWeight.put(word, Pw_z[i][j]);
				}
				List<Entry<String, Double>> wordWeightList = new ArrayList<Entry<String, Double>>(
						wordWeight.entrySet());
				Collections.sort(wordWeightList, new Comparator<Entry<String, Double>>() {
					@Override
					public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
						if (o1.getValue() > o2.getValue()) {
							return -1;
						} else if (o1.getValue() < o2.getValue()) {
							return 1;
						} else {
							return 0;
						}
					}
				});
				for (int j = 0; j < wordWeightList.size() && j < 100; j++) {
					bw.write(wordWeightList.get(j).getKey() + ":" + wordWeightList.get(j).getValue() + "\t");
				}
				bw.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void main(String[] args) {
		int nTopic = 50;
		int nIter = 100;
		PLSA plsa = new PLSA();
		if (args.length < 1) {
			System.err.println("train data in docs/user2vec");
			plsa.doPLSA("docs/user2vec", nTopic, nIter);
		} else {
			System.out.println("train data in " + args[0]);
			if (args.length >= 2) {
				nTopic = Integer.parseInt(args[1]);
			}
			if (args.length >= 3) {
				nIter = Integer.parseInt(args[2]);
			}
			plsa.doPLSA(args[0], nTopic, nIter);
		}
		System.out.println("end PLSA");

		String docVecFile = "model/doc_topic." + (nIter - 1);
		plsa.test(docVecFile);
	}
}