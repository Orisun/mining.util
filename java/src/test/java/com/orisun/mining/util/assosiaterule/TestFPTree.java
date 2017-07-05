package com.orisun.mining.util.assosiaterule;

import com.orisun.mining.util.Path;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestFPTree {

    public static void main(String[] args) throws IOException {

        FPTree fpTree = new FPTree();
        fpTree.setConfident(0.5);
        fpTree.setMinSuport(3);

        String basePath = Path.getCurrentPath();
        String infile = basePath + "/data/buy.txt";
        List<List<String>> trans = fpTree.readTransRocords(new String[] { infile });
        Set<String> decideAttr = new HashSet<String>();
        decideAttr.add("图书");
        fpTree.setDecideAttr(decideAttr);
        fpTree.buildFPTree(trans);
        List<StrongAssociationRule> rules = fpTree.getAssociateRule();

        String outfile = basePath + "/data/rule.txt";
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
