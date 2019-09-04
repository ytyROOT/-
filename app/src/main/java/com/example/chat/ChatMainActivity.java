package com.example.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;



public class ChatMainActivity extends AppCompatActivity {
    private TextView tvOther=null;
    private TextView tvShow=null;

    private static String myId="";
    private String myNickName="";
    private static String friendId="";
    private String friendNickName="";
    private static ImageView im;

    private static LinearLayout llshow;
    private EditText et_input;
    private Button btsend;

    private static ScrollView sv_chat;
    private static Handler mhandle=new Handler();

    private static Context m_context;
    private static Handler recvHandler=new Handler();
    private ArrayList<ChatHis> chatlist;
    private ArrayList<ChatHis> chathislist;

    class ChatHis{
        public String fromId;
        public String toId;
        public String message;
        public String sendtime;

        ChatHis(String a,String b,String c,String d)
        {
            this.fromId=a;
            this.toId=b;
            this.message=c;
            this.sendtime=d;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk_main);
        m_context=getApplicationContext();

        tvOther=(TextView)findViewById(R.id.tv_other);
        tvShow=(TextView)findViewById(R.id.tv_show);

        sv_chat=(ScrollView)findViewById(R.id.sv_chat);

        Bundle bd=this.getIntent().getExtras();
        this.friendId=bd.getString("friendid");
        this.friendNickName=bd.getString("friendnickname");
        this.myId=MyApp.instance().getUserId();
        this.myNickName=MyApp.instance().getUserNickName();
        String s=String.format("与%s聊天",friendNickName);

        tvOther.setText(s.toCharArray(),0,s.toCharArray().length);
        llshow=(LinearLayout)findViewById(R.id.ll_show);
        et_input=(EditText)findViewById(R.id.et_input);
        recvHandler.postDelayed(r,1000);
        new Thread(loadChatHis).start();
        btsend=(Button)findViewById(R.id.btn_send);
        btsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //new Thread(r).start();
                String sql="insert into chathis (fromid,toid,message,addtime,isread) values";
                sql+="(";
                String addtime=MyApp.instance().getTime();
                sql+="'"+myId+"','"+friendId+"','"+et_input.getText().toString()+"','"+addtime+"','0'";
                sql+=")";
                Log.i("Updata", ":" + MyApp.instance());
                MyApp.instance().updataBySql(sql);
                appendMessage(myId,"我说："+et_input.getText().toString()+"\r\n"+addtime);
                et_input.setText("");
            }
        });

    }

    static Runnable mscroll=new Runnable() {
        @Override
        public void run() {
            sv_chat.fullScroll(ScrollView.FOCUS_DOWN);
        }
    };

    Runnable loadChatHis=new Runnable() {
        @Override
        public void run() {
            try {
                DataBaseCon dbc = new DataBaseCon();
                String sql = "select fromid,message,addtime from chathis where ((fromid='" + friendId + "' and toid='" + myId + "') or (toid='" + friendId + "' and fromid='" + myId + "')) order by addtime asc";
                ResultSet rs = null;
                Connection con = dbc.getCon();
                try {
                    //创建一个statement陈述对象
                    chathislist=new ArrayList<ChatHis>();
                    Statement st = con.createStatement();
                    //陈述对象可以执行sql语句
                    rs = (ResultSet) st.executeQuery(sql);//rs获得结果集
                    //遍历结果集
                    while (rs.next()) {
                        String message = rs.getString("message");
                        String addtime = rs.getString("addtime");
                        int fromid = rs.getInt("fromid");
                        Log.i("myTag","select isread from chathis where fromid='" + friendId + "' and toid='" + myId + "' and message='"+message+"'  and addtime='"+addtime+"'");
                        //appendMessage(myId, "你说:" + message.toString() + "\r\n" + addtime);
                        chathislist.add(new ChatHis(""+fromid,myId,message,addtime));
                    }
                    st.close();
                    con.close();
                    Message msg=new Message();
                    Bundle bd=new Bundle();
                    bd.putString("loadDataFindish","ok");
                    msg.setData(bd);
                    recvHisHandler.sendMessage(msg);

                } catch (Exception e) {
                    Log.i("myTag","1:"+e.toString());
                }
            }
            catch (Exception e)
            {
                Log.i("myTag","2:"+e.toString());
            }
        }
    };

    Handler recvHisHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bd=msg.getData();
            if(bd.getString("loadDataFindish")=="ok")
            {
                try
                {
                    for (int i = 0; i < chathislist.size(); i++) {
                        if(chathislist.get(i).fromId.equals(myId))
                            appendMessage(myId, "我说:" + chathislist.get(i).message + "\r\n" + chathislist.get(i).sendtime);
                        if(chathislist.get(i).fromId.equals(friendId))
                            appendMessage(friendId, "你说:" + chathislist.get(i).message + "\r\n" + chathislist.get(i).sendtime);
                    }
                }
                catch (Exception e)
                {

                }
            }
        }
    };


    Runnable r=new Runnable() {
        @Override
        public void run() {
            //receiveMessage();
            Log.i("ReadRun","1");
            new Thread(readMessage).start();

            try
            {
                for(int i=0;i<chatlist.size();i++)
                {
                    appendMessage(friendId,friendNickName+"说："+chatlist.get(i).message+"\r\n"+chatlist.get(i).sendtime);
                }

            }
            catch (Exception e)
            {

            }
            recvHandler.postDelayed(this,1000);
        }
    };

    Runnable readMessage=new Runnable() {
        @Override
        public void run() {
            try
            {
                //String message="wo bu hao";
                DataBaseCon dbc=new DataBaseCon();
                String sql="select fromid,message,addtime from chathis where fromid='"+friendId+"' and toid='"+myId+"' and isread='0'";
                ResultSet rs=null;
                Connection con=dbc.getCon();
                try
                {
                    chatlist=new ArrayList<ChatHis>();
                    Statement st=con.createStatement();
                    rs=(ResultSet)st.executeQuery(sql);
                    while(rs.next())
                    {
                        String message=rs.getString("message");
                        String addtime=rs.getString("addtime");
                        //  int fromid=rs.getInt("fromid");
                        String isread=dbc.searchBySql("select isread from chathis where fromid='"+friendId+"' and toid='"+myId+"'and message='"+message+"'and addtime='"+addtime+"'","isread");
                        if(isread.equals("0"))
                        {
                            chatlist.add(new ChatHis(friendId,myId,message,addtime));
                            MyApp.instance().updataBySql("update chathis set isread='1'where fromid='"+friendId+"' and toid='"+myId+"'and isread='0'and addtime='"+addtime+"'");
                        }
                    }
                    st.close();
                    con.close();
                }
                catch(Exception e)
                {
                    Log.i("myTag","1:"+e.toString());
                }
            }
            catch (Exception e)
            {
                Log.i("myTag","2:"+e.toString());
            }
        }
    };

//    void receiveMessage()
//    {
//
//    }

    private static void appendMessage(String thisId,String message)
    {
        try
        {
            LinearLayout ll_append=new LinearLayout(m_context);
            LinearLayout.LayoutParams llparams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            llparams.setMargins(5,5,5,5);
            ll_append.setLayoutParams(llparams);

           //int im=R.drawable.qp;
            int gravity=thisId.equals(myId)? Gravity.RIGHT:Gravity.LEFT;
           // TextView tv_append=getTextView(message,gravity,im);
           TextView tv_append=getTextView( message,gravity );
           mhandle.postDelayed(mscroll,1000);

            ll_append.addView(tv_append);
            llshow.addView(ll_append);
        }
        catch (Exception e)
        {
            Log.i("myTag",e.toString());
        }
    }

    private static TextView getTextView(String content,int gravity)
    {
        TextView tv=new TextView( m_context );
        tv.setText( content.toCharArray(),0,content.toCharArray().length );
        LinearLayout.LayoutParams tvparams=new LinearLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT );
        tv.setLayoutParams( tvparams );
        tv.setGravity( gravity );
        tv.setTextColor( Color.BLACK );
        return tv;
    }

}
