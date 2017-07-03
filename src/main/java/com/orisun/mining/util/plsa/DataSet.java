package com.orisun.mining.util.plsa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文档集合
 * 
 * @author orisun
 * @date 2016年7月10日
 */
class Dataset {

    /** 文档集合 **/
    List<Data> datas = new ArrayList<Data>();
    /** 记录每个词的编号 **/
    Map<String, Integer> featureIndex = new HashMap<String, Integer>();
    List<String> features = new ArrayList<String>();

    int size() {
        return datas.size();
    }

    int getFeatureNum() {
        return featureIndex.size();
    }

    Data getDataAt(int i) {
        return datas.get(i);
    }

    /**
     * 
     * @param dataDir
     *            如果dataDir是文档集所在的目录。文档格式：每行存储一个词及词在文件中的权重，空格分隔。每篇文档中词可以有重复。<br>
     *            如果所有文档都放在dataDir这一个文件里面，则文件每行的格式为:文件名\t词:权重\t词:权重……
     * @throws IOException
     */
    Dataset(String dataDir) throws IOException {
        File path = new File(dataDir);
        if (path.exists()) {
            int featureNum = 0;
            if (path.isDirectory()) {
                File[] files = path.listFiles();
                for (File file : files) {
                    Data data = new Data();
                    data.docName = file.getName();
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        String[] arr = line.trim().split("\\s+");
                        if (arr.length == 2) {
                            String word = arr[0];
                            double weight = Double.parseDouble(arr[1]);
                            Integer index = featureIndex.get(word);
                            if (index == null) {
                                featureIndex.put(word, featureNum);
                                features.add(word);
                                index = featureNum;
                                featureNum++;
                            }
                            Feature feature = new Feature(index, weight);
                            data.features.add(feature);
                        }
                    }
                    br.close();
                    datas.add(data);
                }
            } else if (path.isFile()) {
                BufferedReader br = new BufferedReader(new FileReader(path));
                String line = null;
                while ((line = br.readLine()) != null) {
                    String[] arr = line.trim().split("\\s+");
                    if (arr.length >= 2) {
                        Data data = new Data();
                        data.docName = arr[0];
                        for (int i = 1; i < arr.length; i++) {
                            String[] brr = arr[i].split(":");
                            if (brr.length == 2) {
                                String word = brr[0];
                                double weight = Double.parseDouble(brr[1]);
                                Integer index = featureIndex.get(word);
                                if (index == null) {
                                    featureIndex.put(word, featureNum);
                                    features.add(word);
                                    index = featureNum;
                                    featureNum++;
                                }
                                Feature feature = new Feature(index, weight);
                                data.features.add(feature);
                            }
                        }
                        datas.add(data);
                    }
                }
                br.close();
            }
        }
    }

}
