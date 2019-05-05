package com.getactive.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class PostModel extends PostModelId implements Serializable {

    @SerializedName("user_id")
    @Expose
    private String user_id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("timestamp")
    @Expose
    private Date timestamp;
    @SerializedName("image_url")
    @Expose
    private String image_url;
    @SerializedName("desc")
    @Expose
    private String desc;

    public PostModel() {
    }

    public PostModel(String user_id, String title, Date timestamp, String image_url, String desc) {
        this.user_id = user_id;
        this.title = title;
        this.timestamp = timestamp;
        this.image_url = image_url;
        this.desc = desc;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
