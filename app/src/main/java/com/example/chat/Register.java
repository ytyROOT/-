package com.example.chat;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.Statement;

public class Register extends AppCompatActivity {



    EditText txtqq=null;
    EditText txtemail=null;
    EditText txtmoblie=null;
    EditText txtpassword=null;
    EditText txtname=null;
    EditText txtnickname=null;
    EditText txtaddress=null;
    String sexstr="";
    String sname,password1,address,qq,email,mobile,nickname;
    String sql="";
    String MyTag="myTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtqq=(EditText)findViewById(R.id.txtqq);
        txtemail=(EditText)findViewById(R.id.txtemail);
        txtmoblie=(EditText)findViewById(R.id.txtmobile);
        txtpassword=(EditText)findViewById(R.id.txtpassword);
        txtaddress=(EditText)findViewById(R.id.txtaddress);
        txtname=(EditText)findViewById(R.id.txtname);
        txtnickname=(EditText)findViewById(R.id.txtnickname);
        RadioGroup rg=(RadioGroup)findViewById(R.id.radiosexgroup);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.sex1)  sexstr="男";
                if(checkedId==R.id.sex2)  sexstr="女";
            }
        });
        Button regbutton=(Button)findViewById(R.id.regbutton);
        regbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(r).start();
            }
        });

    }

    int RegToDataBase()
    {
        int ret=0;
        sname=txtname.getText().toString();
        password1=txtpassword.getText().toString();
        address=txtaddress.getText().toString();
        qq=txtqq.getText().toString();
        mobile=txtmoblie.getText().toString();
        email=txtemail.getText().toString();
        nickname=txtnickname.getText().toString();
        if(sexstr=="")
            sexstr="男";
        sql="insert into users(sname,sex,password1,address,qq,email,mobile,nickname)values('"+sname+"','"+sexstr+"','"+password1+"','"+address+"','"+qq+"','"+email+"','"+mobile+"','"+nickname+"')";
        Log.i(MyTag,"sex sql:"+sql);
        DataBaseCon dbc=new DataBaseCon();
        if(dbc.ExecQuery(sql))
            ret=1;
      //  sql="select users.sid from users where users.sname='"+qq+"';";
        Log.i(MyTag,"Reg 82 sql:"+sql);





        return ret;
    }
    Runnable r=new Runnable() {
        @Override
        public void run() {
            int ret=RegToDataBase();
            Message msg=new Message();
            Bundle bd=new Bundle();
            if(ret==1)
            {
                Log.i(MyTag,"注册成功");
                bd.putString("message","注册成功");
                Log.i(MyTag,"sp is ok");
            }
            else {
                Log.i(MyTag,"注册失败");
                bd.putString("message","注册失败");
            }
            msg.setData(bd);
            h.sendMessage(msg);
        }
    };

    Handler h=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bd=msg.getData();
            String message=bd.getString("message");
            Log.i(MyTag,"");
            Toast.makeText(Register.this,message,Toast.LENGTH_LONG).show();
            if(message.equals("注册成功"))
            {
                Intent intent = new Intent(Register.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };

}
