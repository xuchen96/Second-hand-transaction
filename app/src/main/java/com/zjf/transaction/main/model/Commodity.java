package com.zjf.transaction.main.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by zhengjiafeng on 2019/3/15
 *
 * @author 郑佳锋 zhengjiafeng@bytedance.com
 */
public class Commodity {
    @SerializedName("id")
    private String id; //商品id应该用用户名和时间戳保证唯一性
    @SerializedName("userId")
    private String userId;
    @SerializedName("imageUrl")
    private String imageUrls;  //用@@@来分隔每个url
    @SerializedName("msg")
    private String msg;
    @SerializedName("price")
    private String price;
    @SerializedName("publishTime")
    private long publishTime;

    public Commodity() {
    }

    public Commodity(String id, String userId, String imageUrl, String msg, String price, long publishTime) {
        this.id = id;
        this.userId = userId;
        this.imageUrls = imageUrl;
        this.msg = msg;
        this.price = price;
        this.publishTime = publishTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String imageUrls) {
        this.imageUrls = imageUrls;
    }

    public long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(long publishTime) {
        this.publishTime = publishTime;
    }

    @Override
    public String toString() {
        return "Commodity{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", imageUrls=" + imageUrls +
                ", msg='" + msg + '\'' +
                ", price=" + price +
                ", publishTime=" + publishTime +
                '}';
    }
}
