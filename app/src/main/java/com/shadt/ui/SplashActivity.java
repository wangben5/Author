package com.shadt.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.shadt.activity.LoginActivity;
import com.shadt.application.SealAppContext;
import com.shadt.bean.Friend;
import com.shadt.caibian_news.R;
import com.shadt.ui.contacts.Contact;
import com.shadt.ui.db.UserInfo;
import com.shadt.ui.utils.JsonUtils;
import com.shadt.util.MyLog;


import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * Created by AMing on 16/8/5.
 * Company RongCloud
 */
public class SplashActivity extends BaseActivity {

    private Context context;
    private android.os.Handler handler = new android.os.Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        context = this;


        String cacheToken = get_sharePreferences_rongtoken();
        if (!TextUtils.isEmpty(cacheToken)) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    PhoneToLogin(get_sharePreferences_ronguser(),get_sharePreferences_rongpas());
                }
            }, 800);



        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToLogin();
                }
            }, 800);
        }
    }

    @Override
    public void initPages() {

    }

    @Override
    public void onClickListener(View v) {

    }


    private void goToMain() {
        startActivity(new Intent(context, MainActivity_tab.class));
        finish();
    }

    private void goToLogin() {
        startActivity(new Intent(context, LoginActivity.class));
        finish();
    }

    private boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }
    String lunbotu;

    public void PhoneToLogin(final String username, final String password) {

        RequestParams params = new RequestParams();
        params.addBodyParameter("password", password);
        params.addBodyParameter("username", username);
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
       lunbotu = preferences.getString("lunbotu", "");
        Contact.rong_ip=preferences.getString("vsOutData6", "");
        HttpUtils httpUtils = new HttpUtils();
        MyLog.i("地址：" + Contact.rong_ip+Contact.rong_login);
        httpUtils.send(HttpRequest.HttpMethod.POST, Contact.rong_ip+Contact.rong_login, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // TODO Auto-generated method stub
                MyLog.i("失败：" + arg1.toString());
                goToLogin();

            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                // TODO Auto-generated method stub

                handler.sendEmptyMessage(0);
                String str = arg0.result;
                UserInfo userInfo = JsonUtils.getModel(str, UserInfo.class);//这里已经解析出来了
                MyLog.i("融媒体账号登录返回数据：" + str);
                if (userInfo.getCode()==0){

                    SharedPreferences preferences = getSharedPreferences("user",
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("rong_userinfo", str);
                    editor.apply();
                    String ps=password;
                    String user=username;
                    String token=userInfo.getResult().getRyToken();
                    String userid=""+userInfo.getResult().getUserId();
                    String ry_video_id=""+userInfo.getResult().getRyId();
                    String name=""+userInfo.getResult().getName();
                    String sportraiUri=""+userInfo.getResult().getPortraituri();
                    write_sharePreferencesrong(user,ps,token,userid,sportraiUri,ry_video_id);
                    RongIM.connect(userInfo.getResult().getRyToken(), SealAppContext.getInstance().getConnectCallback());

                    write_sharePreferUserToken(userInfo.getResult().getToken());

                    Intent it=new Intent(SplashActivity.this, MainActivity_tab.class);
                    it.putExtra("lunbotu",lunbotu);
                    startActivity(it);
                    finish();
                }else{
                    goToLogin();

                }

            }
        });
    }

}
