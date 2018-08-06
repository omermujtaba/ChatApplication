package com.morango.chat.chatapplication;

public class Chats {

    private String userStatus;

    public Chats(String userStatus) {
        this.userStatus = userStatus;
    }

    public Chats() {
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }


}
