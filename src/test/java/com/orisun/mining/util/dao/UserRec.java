package com.orisun.mining.util.dao;

@DataBase(name = DBName.RECOMMEND)
@Table(name = "resume_rec_new")
public class UserRec {
	@Id
    private long uid;
    @Column("rec_ids")
    private String recids;
    @Column("rec_scores")
    private String recscores;

    public UserRec() {

    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getUid() {
        return uid;
    }

    public void setRecids(String recids) {
        this.recids = recids;
    }

    public String getRecids() {
        return recids;
    }

    public void setRecscores(String recscores) {
        this.recscores = recscores;
    }

    public String getRecscores() {
        return recscores;
    }

}
