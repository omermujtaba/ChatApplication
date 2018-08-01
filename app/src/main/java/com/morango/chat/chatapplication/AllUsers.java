package com.morango.chat.chatapplication;

public class AllUsers {
    String userName, userStatus, userImage;

    String userThumbImage;

    public AllUsers(String userName, String userStatus, String userImage, String userThumbImage) {
        this.userName = userName;
        this.userStatus = userStatus;
        this.userImage = userImage;
        this.userThumbImage = userThumbImage;
    }

    public String getUserThumbImage() {
        return userThumbImage;
    }

    public AllUsers() {
    }

    public void setUserThumbImage(String userThumbImage) {
        this.userThumbImage = userThumbImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }


}
