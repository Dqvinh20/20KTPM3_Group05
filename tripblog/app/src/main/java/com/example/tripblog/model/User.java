package com.example.tripblog.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    private Integer id;
    private String email;
    @SerializedName("user_name")
    private String userName;
    @SerializedName("user_name_non_accent")
    private String userNameNonAccent;
    @SerializedName("followers_count")
    private Integer followersCount;
    @SerializedName("following_count")
    private Integer followingsCount;
    private String avatar;
    private Date createdAt;
    private Date updatedAt;



    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", userName='" + userName + '\'' +
                ", userNameNonAccent='" + userNameNonAccent + '\'' +
                ", followersCount=" + followersCount +
                ", followingsCount=" + followingsCount +
                ", avatar='" + avatar + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public Integer getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(Integer followersCount) {
        this.followersCount = followersCount;
    }

    public Integer getFollowingsCount() {
        return followingsCount;
    }

    public void setFollowingsCount(Integer followingsCount) {
        this.followingsCount = followingsCount;
    }

    public String getUserNameNonAccent() {
        return userNameNonAccent;
    }

    public void setUserNameNonAccent(String userNameNonAccent) {
        this.userNameNonAccent = userNameNonAccent;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
