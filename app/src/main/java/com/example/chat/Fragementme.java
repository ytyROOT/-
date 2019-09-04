package com.example.chat;

import android.app.NotificationManager;
import android.app.Notification;
import android.app.PendingIntent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


public class Fragementme extends Fragment{

    View v = null;
    WebView webView=null;

    public Fragementme()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.layout_web,container,false);
        webView=(WebView) v.findViewById(R.id.webview);
        if(isNetWorkCanAccess(v.getContext()))
        {
            webView.loadUrl("https://m.thepaper.cn/");
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setCacheMode( WebSettings.LOAD_DEFAULT );
           webSettings.setSupportZoom( true );
           webSettings.setBuiltInZoomControls( true );
           webSettings.setLayoutAlgorithm( WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING );
            webSettings.setDefaultTextEncodingName("utf-8");
           // webView.setWebViewClient(new Fragementme.myWebViewClient());
            webView.setWebChromeClient(new Fragementme.myWebChromViewClient());
            webView.setWebViewClient(new WebViewClient() );
        }


        return v;
    }



    private class myWebChromViewClient extends WebChromeClient
    {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            AlertDialog.Builder b=new AlertDialog.Builder(v.getContext());
            b.setTitle("提示");
            b.setMessage(message);
            b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.confirm();
                }
            });
            b.setCancelable(false);
            b.create().show();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            return super.onJsConfirm(view, url, message, result);
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }
    }

    private class myWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //return super.shouldOverrideUrlLoading(view, url);
            if(url.startsWith("http:"))
            {
                return false;
            }
            try
            {
                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
            catch (Exception e)
            {

            }
            return true;
        }


        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            view.loadData("<!DOCTYPE html><html><head><meta charset=\"gbk\" /></head><body> Page Error</body></html>", "text/html", "gbk");
            if(errorCode == -6 || errorCode == -8)
            {
                //显示对话框
            }
        }
    }

    public static boolean isNetWorkCanAccess(Context context)
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo!=null && networkInfo.isConnected());
    }
}
