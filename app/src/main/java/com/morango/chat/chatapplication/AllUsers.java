package com.morango.chat.chatapplication;

public class AllUsers {
    String userName, userStatus, userImage;

    public AllUsers() {
    }

    public AllUsers(String userName, String userStatus, String userImage) {
        this.userName = userName;
        this.userStatus = userStatus;
        this.userImage = userImage;
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
