package com.morango.chat.chatapplication;

public class Requests {
    private String userName,userThumbImage,userStatus;

    public  Requests(){}

    public Requests(String userName, String userThumbImage, String userStatus) {
        this.userName = userName;
        this.userThumbImage = userThumbImage;
        this.userStatus = userStatus;
    }



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserThumbImage() {
        return userThumbImage;
    }

    public void setUserThumbImage(String userThumbImage) {
        this.userThumbImage = userThumbImage;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

}
