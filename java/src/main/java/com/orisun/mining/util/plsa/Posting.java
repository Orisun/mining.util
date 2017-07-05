package com.orisun.mining.util.plsa;

/**
 * 倒排索引
 * 
 * @author orisun
 * @date 2016年7月10日
 */
class Posting {

    /** 文档编号 **/
    int docID;
    /** 词在文档中的位置 **/
    int pos;

    Posting(int docID, int pos) {
        this.docID = docID;
        this.pos = pos;
    }
}
