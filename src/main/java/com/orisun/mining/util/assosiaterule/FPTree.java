package com.orisun.mining.util.assosiaterule;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * @Description:FPTree强关联规则挖掘算法
 * @Author:zhangchaoyang
 * @Since:2014-7-20
 * @Version:1.1.0
 */
public class FPTree {
    /**频繁模式的最小支持数**/
    private int minSuport;
    /**关联规则的最小置信度**/
    private double confident;
    /**事务项的总数**/
    private int totalSize;
    /**存储每个频繁项及其对应的计数**/
    private Map<List<String>, Integer> frequentMap = new HashMap<List<String>, Integer>();
    /**关联规则中，哪些项可作为被推导的结果，默认情况下所有项都可以作为被推导的结果**/
    private Set<String> decideAttr = null;

    public int getMinSuport() {
        return this.minSuport;
    }

    /**
     * 设置最小支持数
     * 
     * @param minSuport
     */
    public void setMinSuport(int minSuport) {
        this.minSuport = minSuport;
    }

    public double getConfident() {
        return confident;
    }

    /**
     * 设置最小置信度
     * 
     * @param confident
     */
    public void setConfident(double confident) {
        this.confident = confident;
    }

    /**
     * 设置决策属性。如果要调用{@linkplain #readTransRocords(String[])}，需要在调用{@code readTransRocords}之后再调用{@code setDecideAttr}
     * 
     * @param decideAttr
     */
    public void setDecideAttr(Set<String> decideAttr) {
        this.decideAttr = decideAttr;
    }

    /**
     * 获取频繁项集
     * 
     * @return
     * @Description:
     */
    public Map<List<String>, Integer> getFrequentItems() {
        return frequentMap;
    }

    public int getTotalSize() {
        return totalSize;
    }

    /**
     * 根据一条频繁模式得到若干关联规则
     * 
     * @param list
     * @return
     */
    private List<StrongAssociationRule> getRules(List<String> list) {
        List<StrongAssociationRule> rect = new LinkedList<StrongAssociationRule>();
        if (list.size() > 1) {
            for (int i = 0; i < list.size(); i++) {
                String result = list.get(i);
                if (decideAttr.contains(result)) {
                    List<String> condition = new ArrayList<String>();
                    condition.addAll(list.subList(0, i));
                    condition.addAll(list.subList(i + 1, list.size()));
                    StrongAssociationRule rule = new StrongAssociationRule();
                    rule.condition = condition;
                    rule.result = result;
                    rect.add(rule);
                } else {
                }
            }
        }
        return rect;
    }

    /**
     * 从若干个文件中读入Transaction Record，同时把所有项设置为decideAttr
     * 
     * @param filenames
     * @return
     * @Description:
     */
    public List<List<String>> readTransRocords(String[] filenames) {
        Set<String> set = new HashSet<String>();
        List<List<String>> transaction = null;
        if (filenames.length > 0) {
            transaction = new LinkedList<List<String>>();
            for (String filename : filenames) {
                try {
                    FileReader fr = new FileReader(filename);
                    BufferedReader br = new BufferedReader(fr);
                    try {
                        String line = null;
                        // 一项事务占一行
                        while ((line = br.readLine()) != null) {
                            if (line.trim().length() > 0) {
                                // 每个item之间用","分隔
                                String[] str = line.split(",");
                                List<String> record = new LinkedList<String>();
                                for (String w : str) {
                                    record.add(w);
                                    set.add(w);
                                }
                                transaction.add(record);
                            }
                        }
                    } finally {
                        br.close();
                    }
                } catch (IOException ex) {
                    System.out.println("Read transaction records failed." + ex.getMessage());
                    System.exit(1);
                }
            }
        }

        this.setDecideAttr(set);
        return transaction;
    }

    /**
     * 根据事务集合构建FPTree
     * 
     * @param transRecords
     * @Description:
     */
    public void buildFPTree(List<List<String>> transRecords) {
        totalSize = transRecords.size();
        //先把频繁1项集添加到频繁模式中
        Map<String, Integer> freqMap = getFrequency(transRecords);
        for (Entry<String, Integer> entry : freqMap.entrySet()) {
            String name = entry.getKey();
            int cnt = entry.getValue();
            if (cnt >= minSuport) {
                List<String> rule = new ArrayList<String>();
                rule.add(name);
                frequentMap.put(rule, cnt);
            }
        }
        FPGrowth(transRecords, null);
    }

    /**
     * FP树递归生长，从而得到所有的频繁模式
     * 
     * @param cpb  条件模式基
     * @param postPattern   后缀模式
     */
    private void FPGrowth(List<List<String>> cpb, List<String> postPattern) {
        Map<String, Integer> freqMap = getFrequency(cpb);
        Map<String, TreeNode> headers = new HashMap<String, TreeNode>();
        for (Entry<String, Integer> entry : freqMap.entrySet()) {
            String name = entry.getKey();
            int cnt = entry.getValue();
            if (cnt >= minSuport) {
                TreeNode node = new TreeNode(name);
                node.setCount(cnt);
                headers.put(name, node);
            }
        }

        //如果只剩下虚根节点，则递归结束
        TreeNode treeRoot = buildSubTree(cpb, freqMap, headers);
        if ((treeRoot.getChildren() == null) || (treeRoot.getChildren().size() == 0)) {
            return;
        }
        //表头项+后缀模式  构成一条频繁模式，频繁度为表头项的计数
        if (postPattern != null) {
            for (TreeNode header : headers.values()) {
                List<String> rule = new ArrayList<String>();
                rule.add(header.getName());
                rule.addAll(postPattern);
                Collections.sort(rule);//所有的频繁模式都按字典序排好
                frequentMap.put(rule, header.getCount());
            }
        }

        //内层递归
        for (TreeNode header : headers.values()) {
            //新的后缀模式：在上一个后缀模式的基础之上加上表头项
            List<String> newPostPattern = new LinkedList<String>();
            newPostPattern.add(header.getName());
            if (postPattern != null)
                newPostPattern.addAll(postPattern);
            //新的条件模式基
            List<List<String>> newCPB = new LinkedList<List<String>>();
            TreeNode nextNode = header.getNextHomonym();
            while (nextNode != null) {
                int counter = nextNode.getCount();
                //获得从虚根节点（不包括虚根节点）到当前节点（不包括当前节点）的路径，即一条条件模式基
                List<String> path = new ArrayList<String>();
                TreeNode parent = nextNode;
                while ((parent = parent.getParent()).getName() != null) {//虚根节点的name为null
                    path.add(parent.getName());
                }
                //事务要重复添加counter次
                while (counter-- > 0) {
                    newCPB.add(path);
                }
                nextNode = nextNode.getNextHomonym();
            }
            FPGrowth(newCPB, newPostPattern);
        }
    }

    /**
     * 计算事务集中每一项的频数
     * 
     * @param transRecords
     * @return
     */
    private Map<String, Integer> getFrequency(List<List<String>> transRecords) {
        Map<String, Integer> rect = new HashMap<String, Integer>();
        for (List<String> record : transRecords) {
            for (String item : record) {
                Integer cnt = rect.get(item);
                if (cnt == null) {
                    cnt = new Integer(0);
                }
                rect.put(item, ++cnt);
            }
        }
        return rect;
    }

    /**
     * 把所有事务插入到一个FP树当中
     * 
     * @param transRecords
     * @param F1
     * @return
     */
    private TreeNode buildSubTree(List<List<String>> transRecords,
                                  final Map<String, Integer> freqMap,
                                  final Map<String, TreeNode> headers) {
        TreeNode root = new TreeNode();//虚根节点
        for (List<String> transRecord : transRecords) {
            //事务中的各项按频繁度降序排列
            LinkedList<String> record = new LinkedList<String>(transRecord);
            Collections.sort(record, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return freqMap.get(o2) - freqMap.get(o1);
                }
            });
            TreeNode subTreeRoot = root;
            TreeNode tmpRoot = null;
            if (root.getChildren() != null) {
                //延已有的分支，令各节点计数加1
                while (!record.isEmpty()
                       && (tmpRoot = subTreeRoot.findChild(record.peek())) != null) {
                    tmpRoot.countIncrement(1);
                    subTreeRoot = tmpRoot;
                    record.poll();
                }
            }
            //长出新的分支
            addNodes(subTreeRoot, record, headers);
        }
        return root;
    }

    /**
     * 往特定的节点下插入一串后代节点，同时维护表头项到同名节点的链表指针
     * 
     * @param ancestor
     * @param record
     * @param headers
     */
    private void addNodes(TreeNode ancestor, LinkedList<String> record,
                          final Map<String, TreeNode> headers) {
        while (!record.isEmpty()) {
            String item = (String) record.poll();
            //单个项的出现频数必须大于最小支持数。同时满足这个条件的项全部在headers中
            if (headers.containsKey(item)) {
                TreeNode leafnode = new TreeNode(item);
                leafnode.setCount(1);
                leafnode.setParent(ancestor);
                ancestor.addChild(leafnode);

                TreeNode header = headers.get(item);
                while (header.getNextHomonym() != null) {
                    header = header.getNextHomonym();
                }
                header.setNextHomonym(leafnode);
                addNodes(leafnode, record, headers);
            }
        }
    }

    /**
     * 获取所有的强规则
     * 
     * @return
     */
    public List<StrongAssociationRule> getAssociateRule() {
        assert totalSize > 0;
        List<StrongAssociationRule> rect = new ArrayList<StrongAssociationRule>();
        //遍历所有频繁模式
        for (Entry<List<String>, Integer> entry : frequentMap.entrySet()) {
            List<String> items = entry.getKey();
            int count1 = entry.getValue();
            //一条频繁模式可以生成很多关联规则
            List<StrongAssociationRule> rules = getRules(items);
            //计算每一条关联规则的支持度和置信度
            for (StrongAssociationRule rule : rules) {
                if (frequentMap.containsKey(rule.condition)) {
                    int count2 = frequentMap.get(rule.condition);
                    double confidence = 1.0 * count1 / count2;
                    if (confidence >= this.confident) {
                        rule.support = 1.0 * count1 / totalSize;
                        rule.confidence = confidence;
                        rect.add(rule);
                    }
                } else {
                    System.err.println(rule.condition + " is not a frequent pattern, however "
                                       + items + " is a frequent pattern");
                }
            }
        }
        return rect;
    }

    public static void main(String[] args) throws IOException {
        String infile = "buy.txt";
        FPTree fpTree = new FPTree();
        fpTree.setConfident(0.5);
        fpTree.setMinSuport(3);
        if (args.length >= 2) {
            double confidence = Double.parseDouble(args[0]);
            int suport = Integer.parseInt(args[1]);
            fpTree.setConfident(confidence);
            fpTree.setMinSuport(suport);
        }

        List<List<String>> trans = fpTree.readTransRocords(new String[] { infile });
        Set<String> decideAttr = new HashSet<String>();
        decideAttr.add("图书");
        fpTree.setDecideAttr(decideAttr);
        fpTree.buildFPTree(trans);
        List<StrongAssociationRule> rules = fpTree.getAssociateRule();

        String outfile = "rule.txt";
        BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));
        System.out.println("条件\t\t结果\t支持度\t\t\t置信度");
        bw.write("条件\t\t结果\t支持度\t\t\t置信度");
        bw.newLine();
        DecimalFormat dfm = new DecimalFormat("#.##");
        for (StrongAssociationRule rule : rules) {
            System.out.println(rule.condition + "->" + rule.result + "\t" + dfm.format(rule.support)
                               + "\t" + dfm.format(rule.confidence));
            bw.write(rule.condition + "->" + rule.result + "\t" + dfm.format(rule.support) + "\t"
                     + dfm.format(rule.confidence));
            bw.newLine();
        }
        bw.close();
    }
}