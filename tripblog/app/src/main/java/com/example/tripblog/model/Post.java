package com.example.tripblog.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Post implements Serializable {
    private Integer id;
    private String title;
    @SerializedName("brief_description")
    private String briefDescription;
    @SerializedName("cover_img")
    private String coverImg;
    @SerializedName("start_date")
    private Date startDate;
    @SerializedName("end_date")
    private Date endDate;
    @SerializedName("view_count")
    private Integer viewCount;
    @SerializedName("like_count")
    private Integer likeCount;
    @SerializedName("avg_rating")
    private Float avgRating;
    @SerializedName("rating_count")
    private Integer ratingCount;
    @SerializedName("is_public")
    private Boolean isPublic;
    private Date createdAt;
    private Date updatedAt;
    private User author;

    private List<Schedule> schedules;

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }


    @SerializedName("is_liked_by_you")
    private boolean isLikedByYou;

    public boolean isLikedByYou() {
        return isLikedByYou;
    }

    public void setLikedByYou(boolean likedByYou) {
        isLikedByYou = likedByYou;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", briefDescription='" + briefDescription + '\'' +
                ", coverImg='" + coverImg + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", viewCount=" + viewCount +
                ", likeCount=" + likeCount +
                ", avgRating=" + avgRating +
                ", ratingCount=" + ratingCount +
                ", isPublic=" + isPublic +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                "author=" + (author != null ? author.toString() : "null") +
                "schedules=" + (schedules != null ? schedules.toString() : "null") +
                '}';
    }

    public Post() {
        this.isPublic = true;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBriefDescription() {
        return briefDescription;
    }

    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Float getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Float avgRating) {
        this.avgRating = avgRating;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
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
