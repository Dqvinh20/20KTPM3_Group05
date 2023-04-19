package com.example.tripblog.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    private Integer id;
    private String email;
    @SerializedName("user_name")
    private String userName;
    @SerializedName("name")
    private  String name;
    @SerializedName("name_non_accent")
    private String nameNonAccent;
    @SerializedName("followers_count")
    private Integer followersCount;
    @SerializedName("following_count")
    private Integer followingsCount;
    private String avatar;
    private Date createdAt;
    private Date updatedAt;

    public User(User user) {
        this(user.getUserName(), user.getId(), user.getName(), user.getAvatar(), user.getNameNonAccent(), user.getFollowersCount(), user.getFollowingsCount(), user.getEmail(), user.getCreatedAt(), user.getUpdatedAt());
    }

    public User(String userName, Integer id, String name, String avatar, String userNameNonAccent, Integer followersCount, Integer followingsCount, String email, Date createdAt, Date updatedAt) {
        this.userName = userName;
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.nameNonAccent = userNameNonAccent;
        this.followersCount = followersCount;
        this.followingsCount = followingsCount;
        this.email = email;
        if(createdAt != null && updatedAt != null) {
            this.createdAt = new Date(createdAt.getTime());
            this.updatedAt = new Date(updatedAt.getTime());
        }

    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", userName='" + userName + '\'' +
                ", NameNonAccent='" + nameNonAccent + '\'' +
                ", followersCount=" + followersCount +
                ", followingsCount=" + followingsCount +
                ", avatar='" + avatar + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", name=" +name+
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

    public String getNameNonAccent() {
        return nameNonAccent;
    }
    public String getName(){ return name;}

    public void setNameNonAccent(String nameNonAccent) {
        this.nameNonAccent = nameNonAccent;
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
    public void setName(String name) {this.name= name;}

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
