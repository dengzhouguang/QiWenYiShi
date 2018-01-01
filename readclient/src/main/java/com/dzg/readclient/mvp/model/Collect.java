package com.dzg.readclient.mvp.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.util.Date;


/**
 * @author lling
 * @ClassName: Collect
 * @Description: 用户搜藏
 * @date 2015年7月13日
 */
@Entity
public class Collect implements Serializable {
    //是否上传服务器标记
    @Transient
    public static int NOT_UPLOAD = 0;
    @Transient
    public static int UPLOAD = 1;
    @Transient
    private static long serialVersionUID = -6295764401529594309L;
    protected Date createAt;
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
     * 是否上传数据
     * 1：是
     * 0：否
     */
    private int isUpload;
    /**
     * 收藏的joke的序列化对象
     */
    private String jokeContent;

    @Generated(hash = 896410199)
    public Collect(Date createAt, Long id, int jokeId, int userId, int isUpload,
            String jokeContent) {
        this.createAt = createAt;
        this.id = id;
        this.jokeId = jokeId;
        this.userId = userId;
        this.isUpload = isUpload;
        this.jokeContent = jokeContent;
    }

    @Generated(hash = 1726975718)
    public Collect() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getJokeContent() {
        return jokeContent;
    }

    public void setJokeContent(String jokeContent) {
        this.jokeContent = jokeContent;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

}
