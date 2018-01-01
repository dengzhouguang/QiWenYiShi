package com.dzg.readclient.mvp.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;


/**
 * @author lling
 * @ClassName: Praise
 * @Description: 本地顶和踩数据记录
 * @date 2015-6-28
 */
@Entity
public class DingOrCai implements Serializable {
    //是否上传服务器标记
    @Transient
    public static int NOT_UPLOAD = 0;
    @Transient
    public static int UPLOAD = 1;
    //顶踩标记
    @Transient
    public static int CAI = 0;
    @Transient
    public static int DING = 1;
    @Transient
    private static long serialVersionUID = -2565933558042038491L;
    /**
     * id
     */

    @Id(autoincrement = true)
    private Long id;
    /**
     * 笑话id
     */


    private int jokeId;
    /**
     * 用户id，游客为-1
     */
    private int userId;
    /**
     * 标记顶和踩
     * 1：顶
     * 0：踩
     */
    private int dingOrCai;
    /**
     * 顶或者踩之后的数量
     */
    private int num;
    /**
     * 是否上传数据
     * 1：是
     * 0：否
     */
    private int isUpload;

    @Generated(hash = 771926411)
    public DingOrCai(Long id, int jokeId, int userId, int dingOrCai, int num, int isUpload) {
        this.id = id;
        this.jokeId = jokeId;
        this.userId = userId;
        this.dingOrCai = dingOrCai;
        this.num = num;
        this.isUpload = isUpload;
    }

    @Generated(hash = 2115568338)
    public DingOrCai() {
    }

    public int getJokeId() {
        return jokeId;
    }

    public void setJokeId(int jokeId) {
        this.jokeId = jokeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getIsUpload() {
        return isUpload;
    }

    public void setIsUpload(int isUpload) {
        this.isUpload = isUpload;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getDingOrCai() {
        return dingOrCai;
    }

    public void setDingOrCai(int dingOrCai) {
        this.dingOrCai = dingOrCai;
    }

    public boolean isDing() {
        return dingOrCai == DING;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }


}
