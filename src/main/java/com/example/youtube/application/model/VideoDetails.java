package com.example.youtube.application.model;

//VideoDetails.java

import java.time.LocalDateTime;

public class VideoDetails {
 private String videoId;
 private String title;
 private String description;
 private String thumbnailUrl;
 private String channelTitle;
 private LocalDateTime publishedAt;
 private Long viewCount;
 private Long likeCount;
 private Long commentCount;

 // Getters and Setters
 public String getVideoId() {
     return videoId;
 }

 public void setVideoId(String videoId) {
     this.videoId = videoId;
 }

 public String getTitle() {
     return title;
 }

 public void setTitle(String title) {
     this.title = title;
 }

 public String getDescription() {
     return description;
 }

 public void setDescription(String description) {
     this.description = description;
 }

 public String getThumbnailUrl() {
     return thumbnailUrl;
 }

 public void setThumbnailUrl(String thumbnailUrl) {
     this.thumbnailUrl = thumbnailUrl;
 }

 public String getChannelTitle() {
     return channelTitle;
 }

 public void setChannelTitle(String channelTitle) {
     this.channelTitle = channelTitle;
 }

 public LocalDateTime getPublishedAt() {
     return publishedAt;
 }

 public void setPublishedAt(LocalDateTime publishedAt) {
     this.publishedAt = publishedAt;
 }

 public Long getViewCount() {
     return viewCount;
 }

 public void setViewCount(Long viewCount) {
     this.viewCount = viewCount;
 }

 public Long getLikeCount() {
     return likeCount;
 }

 public void setLikeCount(Long likeCount) {
     this.likeCount = likeCount;
 }

 public Long getCommentCount() {
     return commentCount;
 }

 public void setCommentCount(Long commentCount) {
     this.commentCount = commentCount;
 }
}
