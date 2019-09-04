package com.example.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static java.lang.Thread.sleep;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ViewPager mViewPager;

    private FragmentPagerAdapter mAdapter;
    private ArrayList<Fragment> mFragments;

    private LinearLayout mTabChat;
    private LinearLayout mTabAddressBook;
    private LinearLayout mTabDis;
    private LinearLayout mTabMe;

    private ImageButton mImgchat;
    private ImageButton mImgAddressBook;
    private ImageButton mImgDis;
    private ImageButton mImgMe;

    private boolean isExit = false;
    private boolean islogin = false;

    Handler mhandle = new Handler(){

        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            isExit=false;
        }
    };

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            exit();
            return false;
        }

        return super.onKeyDown(keyCode,event);
    }

    private void exit()
    {
        if(!isExit)
        {
            isExit=true;
            Toast.makeText(getApplicationContext(),"再点击一次返回程序退出",Toast.LENGTH_SHORT).show();
            mhandle.sendEmptyMessageDelayed(0,2000);
        }
        else
        {
            Log.i("LogOut", "LogOut1");
            MyApp.instance().updataBySql("update users set online = '0' where sid = '" + MyApp.instance().getUserId()+ "';");
            finish();
            MyApp.instance().setBool();
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.exit(0);
        }
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*islogin=MyApp.instance().isLogin();
            if(!islogin)
            {
                System.exit(0);
            }
            else {
                //Intent intent=new Intent(MainActivity.this,TreeActivity.class);
               // startActivity(intent);*/

                initView();
                initData();
                initEvent();


    }

    private void initView() {
       mTabChat=(LinearLayout)findViewById(R.id.id_tab_wechat);
       mTabAddressBook=(LinearLayout)findViewById(R.id.id_tab_friend);
       mTabDis=(LinearLayout)findViewById(R.id.id_tab_dis);
       mTabMe=(LinearLayout)findViewById(R.id.id_tab_me);

       mImgchat=(ImageButton)findViewById(R.id.id_tab_wechat_img);
       mImgAddressBook=(ImageButton)findViewById(R.id.id_tab_friend_img);
       mImgDis=(ImageButton)findViewById(R.id.id_tab_dis_img);
       mImgMe=(ImageButton)findViewById(R.id.id_tab_me_img);

       mViewPager = (ViewPager)findViewById(R.id.id_viewpager);
    }

    private void resetimgs() {
        mImgchat.setImageResource(R.drawable.chat);
        mImgAddressBook.setImageResource(R.drawable.addressbook);
        mImgDis.setImageResource(R.drawable.dis);
        mImgMe.setImageResource(R.drawable.me);
    }

    private void initData() {
        mFragments = new ArrayList<Fragment>();
        mFragments.add(new Fragementechat());
        mFragments.add(new Fragementaddressbook());
        mFragments.add(new Fragementdis());
        mFragments.add(new Fragementme());
        FragmentPagerAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(),
                mFragments);
        mViewPager.setAdapter(fragmentAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                Log.i("MyViewP", "3");
                try{
                    mViewPager.setCurrentItem(i);

                    resetimgs();
                    selectTab(i);
                }
                catch (Exception e)
                {
                    Log.i("MYTAG", e.toString());
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    void selectTab(int position) {
        Log.i("MyViewP", "4");
        switch (position) {
            case 0:
                mImgchat.setImageResource(R.drawable.chat1);
                break;
            case 1:
                mImgDis.setImageResource(R.drawable.dis1);
                break;

                case 2:
                mImgAddressBook.setImageResource(R.drawable.addressbook1);
                break;

            case 3:
                mImgMe.setImageResource(R.drawable.me1);
                break;
            default:
                break;
        }
        mViewPager.setCurrentItem(position);
    }


    private void initEvent() {
        mTabChat.setOnClickListener(this);
        mTabAddressBook.setOnClickListener(this);;
        mTabDis.setOnClickListener(this);;
        mTabMe.setOnClickListener(this);;
    }


    @Override
    public void onClick(View v) {
            int i = 0;
            switch (v.getId()) {
                case R.id.id_tab_wechat:
                    mImgchat.setImageResource(R.drawable.chat1);
                    i=0;
                    break;
                case R.id.id_tab_dis:
                    mImgDis.setImageResource(R.drawable.dis1);
                    i=1;
                    break;

                    case R.id.id_tab_friend:
                    mImgAddressBook.setImageResource(R.drawable.addressbook1);
                    i=2;
                    break;

                case R.id.id_tab_me:
                    mImgMe.setImageResource(R.drawable.me1);
                    i=3;
                    break;

                    default:
                        break;
            }

            Log.i("MyViewP", "1");

            try
            {
                mViewPager.setCurrentItem(i);
            }
            catch (Exception e)
            {
                Log.i("MY", ":" + e);
            }

        Log.i("MyViewP", "2");
        }

    }

