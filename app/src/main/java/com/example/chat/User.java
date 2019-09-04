package com.example.chat;

public class User {
    private int sid;
    public String nickname;
    User(int sid0, String nickname0)
    {
        this.sid=sid0;
        this.nickname=nickname0;
    }
    public int getUserid()
    {
        return this.sid;
    }
    public String getUserNickName()
    {
        return this.nickname;
    }
}
