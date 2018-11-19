package com.shadt.application;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.iflytek.cloud.SpeechUtility;
import com.shadt.activity.LoginActivity;
import com.shadt.caibian_news.R;
import com.shadt.ui.download.DefaultNotifier;
import com.shadt.ui.download.DownloadManager;
import com.shadt.util.ActivityManagerTool;
import com.shadt.util.MyLog;


import io.rong.imageloader.core.DisplayImageOptions;
import io.rong.imageloader.core.display.FadeInBitmapDisplayer;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.push.RongPushClient;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class MyApp extends Application  {

    private boolean isDownload;

    public boolean isDownload() {
        return isDownload;
    }

    static Context mContext;

    public void setDownload(boolean isDownload) {
        this.isDownload = isDownload;
    }

    private static DisplayImageOptions options;
    private static RequestOptions Glideoptions;
    String swkey, rykey;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

    }


    @Override
    public void onCreate() {
        super.onCreate();
        //初始化讯飞语音听写
        SpeechUtility.createUtility(this,"appid=5be9314c");
  /*    new Thread(new Runnable() {
            @Override
            public void run() {
                QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
                    @Override
                    public void onCoreInitFinished() {

                    }
                    @Override
                    public void onViewInitFinished(boolean b) {
                        MyLog.i("beoo"+b);
                    }
                };
                QbSdk.initX5Environment(getApplicationContext(),cb);

            }
        }).start();*/
        RongPushClient.registerHWPush(this);
        registerBoradcastReceiver();
        DownloadManager.getInstance().initialize(this, 3);
        DownloadManager.getInstance().setDownloadNotifier(new DefaultNotifier(this));
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);

        mContext = getApplicationContext();
        rykey = preferences.getString("rykey", "");
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {
            if (!TextUtils.isEmpty(rykey) ) {
                MyLog.i("融云初始化成功" );
                RongIM.init(this, rykey);
            /*    String token = preferences.getString("rong_token", "");
                    SealAppContext.init(this);
                RongIM.connect(token, SealAppContext.getInstance().getConnectCallback());*/

            } else {
                MyLog.i("融云初始化失败");
            }
        }
        /**
         * 设置消息体内是否携带用户信息。
         *
         * @param state 是否携带用户信息，true 携带，false 不携带。
         */
        RongIM.getInstance().setMessageAttachedUserInfo(true);

         options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.de_default_portrait)
                .showImageOnFail(R.drawable.de_default_portrait)
//                .showImageOnLoading(R.drawable.de_default_portrait)
                .displayer(new FadeInBitmapDisplayer(300))
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        Glideoptions = new RequestOptions()
                .centerCrop()
                .placeholder(R.color.color_f6)
                .error(R.drawable.de_default_portrait)
                .placeholder(R.drawable.de_default_portrait)
                .diskCacheStrategy(DiskCacheStrategy.ALL);


    }

    public static DisplayImageOptions getOptions() {
        return options;
    }

    public static RequestOptions getGlideOptions() {
        return Glideoptions;
    }

    public void registerBoradcastReceiver() {

        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("init");
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);

    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals("init")) {
                MyLog.i("收到广播了");
                SharedPreferences preferences = getSharedPreferences("user",
                        Context.MODE_PRIVATE);

                mContext = getApplicationContext();
                rykey = preferences.getString("rykey", "");


                if (!TextUtils.isEmpty(rykey) ) {
                    MyLog.i("融云初始化成功" );
                    RongIM.init(getApplicationContext(), rykey);

                } else {
                    MyLog.i("融云初始化失败");
                }
            }
        }
    };

    public static Context getContext() {
        return mContext;
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }



}
