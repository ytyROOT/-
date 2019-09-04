package com.example.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText userpass;

    private SharedPreferences sp=null;
    //SharedPreferences.Editor editor=sp.edit();
    CheckBox chkremember;
    CheckBox chkautologin;

    public int IsValidUserNameAndPassword()
    {



        String sql="select sid from users where(qq='"+username.getText().toString()+"'or email='"+username.getText().toString()+"'or mobile='"+username.getText().toString()+"')and password1='"+ userpass.getText().toString()+"';";
        DataBaseCon dataBaseCon = new DataBaseCon();

        int nCount = dataBaseCon.getCount(sql);

        if(1 == nCount)
        {
            String myid=dataBaseCon.searchBySql(sql,"sid");

            try {
                MyApp.instance().setUserId(myid);

                MyApp.instance().updataBySql("insert into loginhis(loginuser,logintime) value('"+username.getText().toString()+"','"+MyApp.instance().getTime()+"')");
                MyApp.instance().updataBySql("update users set online = '1' where sid = '" + MyApp.instance().getUserId()+ "';");
            }
            catch (Exception e)
            {
                Log.i("MySql2", "Error:" + e);
            }


            return 1;
        }
        else
        {
            return -1;
        }

    }

    Handler h = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);

            Bundle bd = msg.getData();

            Log.i("MySql:", "2" );
            String message = bd.getString("message");

            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            if(message.equals("登陆成功"))

            {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };

    Runnable r = new Runnable() {
        @Override
        public void run() {
            int nRes = IsValidUserNameAndPassword();

            Message msg = new Message();
            Bundle bundle = new Bundle();
            if(1 == nRes)
            {
                bundle.putString("message", "登陆成功");
                if(chkremember.isChecked()||chkautologin.isChecked()){
//                    //editor.putString("username",username.getText().toString());
//                    //editor.commit();
                    sp.edit().putString("username",username.getText().toString()).commit();
                }
                if(chkautologin.isChecked()){
                    sp.edit().putString("userpass",userpass.getText().toString()).commit();
                }
            }
            else
            {
                bundle.putString("message", "登路失败");
            }

            msg.setData(bundle);
            h.sendMessage(msg);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username=(EditText)findViewById(R.id.txtusername);
        userpass=(EditText)findViewById(R.id.txtpassword);
        chkremember=((CheckBox)findViewById(R.id.chkremember));
        chkautologin=((CheckBox)findViewById(R.id.chkautologin));

        Button btnLogin = (Button) findViewById(R.id.button1);
        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                new Thread(r).start();
            }
        });

        Button btnReg = (Button) findViewById(R.id.button2);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(LoginActivity.this, Register.class);
               startActivity(intent);
               finish();
            }
        });

        sp=this.getSharedPreferences("echatl", Context.MODE_PRIVATE);

        if(sp!=null){
            username.setText(sp.getString("username","").toCharArray(),0,sp.getString("username","").length());
        }
        if(sp.getBoolean("isremember",false)){
            chkremember.setChecked(true);
            if(sp.getString("username","")!=null){
                username.setText(sp.getString("username","").toCharArray(),0,sp.getString("username","").length());
            }
            if(username.getText().toString()!=""&&userpass.getText().toString()!="")
                AutoLogin();
        }
        if(sp.getBoolean("isautologin",false)){
            chkautologin.setChecked(true);
            if(sp.getString("userpass","")!=null){
                userpass.setText(sp.getString("userpass","").toCharArray(),0,sp.getString("userpass","").length());
            }
            if(username.getText().toString()!=""&&userpass.getText().toString()!="")
                AutoLogin();
        }
        chkremember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.edit().putBoolean("isremember",isChecked).commit();
                if(isChecked)
                    sp.edit().putString("username",username.getText().toString()).commit();

            }
        });
        chkautologin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.edit().putBoolean("isautologin",isChecked).commit();
                if(isChecked)
                    sp.edit().putString("userpass",userpass.getText().toString()).commit();

            }
        });
    }

    void AutoLogin(){
        new Thread(r).start();
    }
}
