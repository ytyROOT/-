package com.example.chat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

public class startAcitivity extends AppCompatActivity {
    int i=0;
    String mytag="mytag";
    private Handler handler=new Handler();
    ImageView iv=null;
    private Runnable r=new Runnable() {
        @Override
        public void run() {
            if(i==0){iv.setImageResource(R.drawable.mao1);}
            if(i==1){iv.setImageResource(R.drawable.mao2);}
            if(i==2){iv.setImageResource(R.drawable.mao3);}
            if(i==3){
                Intent _intent=new Intent(startAcitivity.this,LoginActivity.class);
                startActivity(_intent);
                finish();
                Log.i(mytag,"欢迎");
            }
            i+=1;
            handler.postDelayed(this,1000);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        MyApp myApp = new MyApp();
        myApp.onCreate();
        iv=(ImageView)findViewById(R.id.imageView);
        handler.postDelayed(r,1000);
    }

}
