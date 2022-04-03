package com.example.newlogin.Utills;

public class Comment {
    private String username,profileImageUri,comments;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }

    public void setProfileImageUri(String profileImageUri) {
        this.profileImageUri = profileImageUri;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Comment() {
    }

    public Comment(String username, String profileImageUri, String comments) {
        this.username = username;
        this.profileImageUri = profileImageUri;
        this.comments = comments;
    }
}
