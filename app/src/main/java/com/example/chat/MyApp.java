package com.example.chat;

import android.app.Application;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;


public class MyApp extends Application {
    private static MyApp app;
    private String userId="";
    private String userNickName="";
    private DataBaseCon dbc;
    private String sql;
    private boolean isLogin=true;
    private static ArrayList<onLineData> onLineDatas;

    class onLineData
    {
        int sid;
        int online;

        onLineData(int sid, int online)
        {
            this.sid = sid;
            this.online = online;
        }
    }

    public void setBool()
    {
        isLogin = !isLogin;
    }

    public boolean getBool()
    {
        return isLogin;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app=this;
        dbc=new DataBaseCon();
    }

    public void setUserId(String userid)
    {

        app.userId=userid;
    }

    public String getUserId()
    {
        return this.userId;
    }

    public String getUserNickName()
    {
        return this.userNickName;
    }

    public static MyApp instance()
    {
        return app;
    }

    public boolean isLogin()
    {
        return userId==""?false:true;
    }

    public void updataBySql(String sql)
    {
        this.sql=sql;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("Updata", ":" + app);
                app.dbc.ExecQuery(app.sql);
            }
        }).start();
    }

    public String getTime()
    {
        Date d=new Date(System.currentTimeMillis());
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return sdf.format(d).toString();
    }
}
