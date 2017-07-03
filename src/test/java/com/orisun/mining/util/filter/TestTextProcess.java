package com.orisun.mining.util.filter;

import com.orisun.mining.util.FileUtil;
import com.orisun.mining.util.Path;
import com.orisun.mining.util.filter.TextProcess.WordProcess;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

public class TestTextProcess {

    @BeforeClass
    public static void setup() {
        TransferFont.SetFanJianFile(Path.getCurrentPath() + "/data/jianFan.dic");
    }

    @Test
    public void testPreprocess() {
        String str = "Ａ	ｂｃ,。開";
        String rect = TextProcess.preProcess(str, EnumSet.of(WordProcess.LOWER, WordProcess.RMPUNC,
            WordProcess.RMZEROWIDTH, WordProcess.SIMPLIFIED));
        Assert.assertEquals(rect, "a bc开");
    }

    @Test
    public void testRmPunc() {
        Set<Character> exclude = new HashSet<Character>();
        exclude.add('.');
        exclude.add('+');
        exclude.add('#');
        exclude.add('-');
        exclude.add('/');
        String str = "c/c++/c#/node.js/unity-3d/<java>";
        String rect = TextProcess.rmPunc(str, exclude);
        Assert.assertEquals(rect, "c/c++/c#/node.js/unity-3d/java");

        str = "c c++ .net c# node.js unity-3d <java>";
        rect = TextProcess.rmPunc(str, exclude);
        Assert.assertEquals(rect, "c c++ .net c# node.js unity-3d java");

        str = "++c --java c++ .net c#";
        rect = TextProcess.rmPunc(str, exclude);
        Assert.assertEquals(rect, "c java c++ .net c#");

        str = "--- ...... #bbf ---android +提成5天8小时制电话销售 --运营专员互联网/移动互联网";
        rect = TextProcess.rmPunc(str, exclude);
        Assert.assertEquals(rect, "bbf android 提成5天8小时制电话销售 运营专员互联网/移动互联网");
    }

    @Test
    public void testRmBacket() {
        String str = "电商运营（平台活动/KOL运营/C卖家运营）专家";
        System.out.println(TextProcess.rmBacket(str));
        str = "电商运营(平台活动/KOL运营/C卖家运营）专家";
        System.out.println(TextProcess.rmBacket(str));
        str = "电商运营[平台活动/KOL运营/C卖家运营]专家";
        System.out.println(TextProcess.rmBacket(str));
        str = "电商运营【平台活动/KOL运营/C卖家运营】专家";
        System.out.println(TextProcess.rmBacket(str));
        str = "（平台活动/KOL运营/C卖家运营）专家";
        System.out.println(TextProcess.rmBacket(str));
        str = "电商运营（平台活动/KOL运营/C卖家运营）";
        System.out.println(TextProcess.rmBacket(str));
        str = "（平台活动/KOL运营/C卖家运营）";
        System.out.println(TextProcess.rmBacket(str));
        str = "专家";
        System.out.println(TextProcess.rmBacket(str));
        str = "【奇虎360搜索团队】【广告高级研发工程师】";
        System.out.println(TextProcess.rmBacket(str));
        str = "【推荐引擎高级研发工程师】";
        System.out.println(TextProcess.rmBacket(str));
    }

    @Test
    public void testIsPunc() {
        String str1 = "~!@#$%^&*()_+{}:~！@#￥%……&*（）——+{}：|”《》【】\',./`";
        Assert.assertTrue(TextProcess.isPunc(str1));
        String str2 = "~!@#$%^&*()_+{}:~！@#￥%……&*（）——+{}：|”《》【】\',./`l";
        Assert.assertFalse(TextProcess.isPunc(str2));
    }

    /**
     * 性能测试。
     * 
     * @throws IOException
     */
    @Test
    public void performTest() throws IOException {
        long begin = System.currentTimeMillis();
        String file = TransferFont.class.getResource("/").getPath() + "/jianFan.dic";
        TransferFont.SetFanJianFile(file);
        String filepath = this.getClass().getResource("/").getPath() + "/corpus/whitesent.txt";
        List<String> lines = new LinkedList<String>();
        FileUtil.readLines(filepath, lines);

        for (String line : lines) {
            String text = TextProcess.preProcess(line, EnumSet.of(WordProcess.DBC,
                WordProcess.LOWER, WordProcess.SIMPLIFIED, WordProcess.RMPUNC));
            System.out.println(text);
        }
        long end = System.currentTimeMillis();
        System.out.println("time elapsed " + (end - begin) + " milliseconds");
    }

    @Test
    public void testToDBC() {
        String sent = "鸿雁几时到－江湖　秋水多";
        String result = TextProcess.preProcess(sent, EnumSet.of(WordProcess.DBC));
        Assert.assertEquals("鸿雁几时到-江湖 秋水多", result);
    }

    @Test
    public void testRmSymbol() {
        String sent1 = "花落水流红，闲愁万种，无语怨东风。";
        String rect1 = "花落水流红， 闲愁万种，无语怨东风。";
        Assert.assertFalse(sent1.equals(rect1));

        String sent2 = TextProcess.preProcess(sent1,
            EnumSet.of(WordProcess.RMPUNC, WordProcess.DBC));
        String rect2 = TextProcess.preProcess(rect1,
            EnumSet.of(WordProcess.RMPUNC, WordProcess.DBC));
        Assert.assertTrue(sent2.equals(rect2));

        String sent3 = TextProcess.preProcess("鸿雁几时到－江湖秋水多58",
            EnumSet.of(WordProcess.RMPUNC, WordProcess.DBC));
        String rect3 = TextProcess.preProcess("鸿雁几时到-江湖秋水多58",
            EnumSet.of(WordProcess.RMPUNC, WordProcess.DBC));
        Assert.assertTrue(sent3.equals(rect3));

        String sent4 = "鷄塨a2自己家泰迪下黑色的宝	贝贰百【甩*卖】!。，！";
        String rect4 = TextProcess.preProcess(sent4, EnumSet.of(WordProcess.RMPUNC));
        Assert.assertEquals("鷄塨a2自己家泰迪下黑色的宝贝贰百甩卖", rect4);
    }

    @Test
    public void testT2S() {
        String sent = "清瑟怨遙夜，繞弦風雨哀。颁奨^验証";
        String rect = TextProcess.preProcess(sent, EnumSet.of(WordProcess.SIMPLIFIED));
        Assert.assertEquals("清瑟怨遥夜，绕弦风雨哀。颁奖^验证", rect);

    }

    @Test
    public void testLower() {
        String sent = "I^&T*GIULafwe";
        String rect = TextProcess.preProcess(sent, EnumSet.of(WordProcess.LOWER));
        Assert.assertEquals("i^&t*giulafwe", rect);
    }
}
