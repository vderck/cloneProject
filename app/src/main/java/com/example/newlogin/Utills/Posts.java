package com.example.newlogin.Utills;

public class Posts {
    private String dataPost,postDescription,postImageUri,userprofileImage,username;

    public Posts() {
    }

    public Posts(String dataPost, String postDescription, String postImageUri, String userprofileImage, String username) {
        this.dataPost = dataPost;
        this.postDescription = postDescription;
        this.postImageUri = postImageUri;
        this.userprofileImage = userprofileImage;
        this.username = username;
    }

    public String getDataPost() {
        return dataPost;
    }

    public void setDataPost(String dataPost) {
        this.dataPost = dataPost;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getPostImageUri() {
        return postImageUri;
    }

    public void setPostImageUri(String postImageUri) {
        this.postImageUri = postImageUri;
    }

    public String getUserprofileImage() {
        return userprofileImage;
    }

    public void setUserprofileImage(String userprofileImage) {
        this.userprofileImage = userprofileImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}