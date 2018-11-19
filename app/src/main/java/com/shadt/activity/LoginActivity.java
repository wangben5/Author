package com.shadt.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.shadt.bean.UpdateInfo;
import com.shadt.caibian_news.R;
import com.shadt.ui.MainActivity_tab;
 import com.shadt.ui.contacts.Contact;
import com.shadt.ui.db.UserInfo;
import com.shadt.ui.utils.JsonUtils;
import com.shadt.util.Contacts;
import com.shadt.util.MyLog;
import com.shadt.util.OtherFinals;
import com.shadt.util.XMLParserUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private static final int REQUEST_TIMEOUT = 10 * 1000;// 设置请求超时10秒钟
    private static final int SO_TIMEOUT = 10 * 1000; // 设置等待数据超时时间10秒钟
    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to examin_main_activity real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@com.shadt.ui.example.com:hello", "bar@com.shadt.ui.example.com:world"};
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    // <?xml version="1.0" encoding="UTF-8"?>
    // <xml><return_code>true</return_code>
    // <return_msg>登录成功!</return_msg>
    // <data>
    // <token>911ae34d-72a5-4305-a45e-256538825e01</token>
    // <username>admi</username>
    // <fullname>超级管理员</fullname>
    // </data>
    // </xml>
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private Context mContext = LoginActivity.this;
    SharedPreferences preferences;
    Button mEmailSignInButton;
    LinearLayout line2;
    public static String initresult = ""; // 二维码扫描 返回的字符串 用来请求 获取 哪个地区的 标识
    public static boolean is_init = false; // 判断 是否 初始化 数据
    TextView txt1, txt2;
    private AlertDialog mAlertDialog;
    private TextView tip_msg;
    private boolean isSaveUserInfo;
    private LinearLayout layout_choosesave;
    private ImageView chooseImage;
    private ImageView img_amap;
    private TextView text_amap;


    @SuppressLint("NewApi")
    public void init_dialog() {
        View view = View.inflate(getApplicationContext(), R.layout.prodialog,
                null);
        mAlertDialog = new AlertDialog.Builder(LoginActivity.this).create();
        mAlertDialog.setContentView(view);
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.create();
        mAlertDialog.show();
        tip_msg = (TextView) view.findViewById(R.id.tips_loading_msg);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);

        isSaveUserInfo = preferences.getBoolean("isSaveUserInfo", true);

        Contact.rong_ip = preferences.getString("vsOutData6", "");
        Editor editor = preferences.edit();
        editor.putString("html_native", "");
        editor.putString("html_web", "");
        String zimeiti_ip = preferences.getString("zimeiti_ip", "");
        String zimeiti_key = preferences.getString("zimeiti_key", "");
        String zimeiti_upload = preferences.getString("zimeiti_upload", "");

        Contact.meizi_ip = zimeiti_ip;
        Contact.meizi_apikey = zimeiti_key;
        Contact.meizi_upload = zimeiti_upload;

        editor.commit();
        File dir = new File(OtherFinals.DIR_IMG);
        if (!dir.exists()) {// Launch camera to take photo for
            dir.mkdirs();// 创建照片的存储目录
        } else {
        }

        layout_choosesave = (LinearLayout) findViewById(R.id.layout_choosesave);
        chooseImage = (ImageView) findViewById(R.id.choose_save);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);


        layout_choosesave.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (isSaveUserInfo) {
                    isSaveUserInfo = false;
                    chooseImage.setImageResource(R.drawable.chooseno);
                    Editor editor = preferences.edit();
                    editor.putBoolean("isSaveUserInfo", false);
                    editor.apply();
                } else {
                    isSaveUserInfo = true;
                    chooseImage.setImageResource(R.drawable.chooseyes);
                    Editor editor = preferences.edit();
                    editor.putBoolean("isSaveUserInfo", true);
                    editor.apply();
                }
            }
        });

        if (isSaveUserInfo) {
            chooseImage.setImageResource(R.drawable.chooseyes);

            mEmailView.setText("" + get_sharePreferences_ronguser());
            mPasswordView = (EditText) findViewById(R.id.password);
            if (mEmailView.getText().toString().trim().equals("")) {
                mPasswordView.setText("");
            } else {
                mPasswordView.setText("" + get_sharePreferences_rongpas());
            }

        } else {
            chooseImage.setImageResource(R.drawable.chooseno);

            mEmailView.setText("" + get_sharePreferences_name());
            mPasswordView = (EditText) findViewById(R.id.password);
            if (mEmailView.getText().toString().trim().equals("")) {
                mPasswordView.setText("");
            } else {
                mPasswordView.setText("");
            }

        }
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.email_login_form);
        txt1 = (TextView) findViewById(R.id.text1);
        txt2 = (TextView) findViewById(R.id.text2);

        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);

        line2 = (LinearLayout) findViewById(R.id.line2);


        line2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent it = new Intent(mContext, CaptureActivity.class);
                startActivity(it);
            }
        });

    }

    private String account;
    private int uid;


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    String email = null, password = null;

    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        email = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for examin_main_activity valid password, if the user entered one.

        // Check for examin_main_activity valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
            focusView.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_null_password));
            focusView = mPasswordView;
            cancel = true;
            focusView.requestFocus();
            return;
        }
        if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
            focusView.requestFocus();
            return;
        }
        if (webutils.isNetworkConnected(mContext)) {

            handler.sendEmptyMessage(3);
            handler.sendEmptyMessageDelayed(1, 1000);
        } else {
            Toast.makeText(mContext, getResources().getText(R.string.erro_net),
                    0).show();
        }

    }


    private boolean isPasswordValid(String password) {
        // TODO: Replace this with your own logic
        return password.length() >= 1;
    }

    public void setClickable(boolean able) {
        if (able == true) {
            mEmailSignInButton.setClickable(able);
            mEmailView.setFocusable(able);
            mEmailView.setFocusableInTouchMode(able);
            mPasswordView.setFocusable(able);
            mPasswordView.setFocusableInTouchMode(able);
        } else {
            mEmailSignInButton.setClickable(able);
            mEmailView.setFocusable(able);
            mEmailView.setFocusableInTouchMode(able);
            mPasswordView.setFocusable(able);
            mPasswordView.setFocusableInTouchMode(able);
        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    /**
     * Shows the progress UI and hides the login form.
     */
    private interface ProfileQuery {
        String[] PROJECTION = {ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,};

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 1235645488 - 0 > 3000
            finish();
        }
        return false;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */

    @Override
    public void initPages() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClickListener(View v) {
        // TODO Auto-generated method stub

    }

    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            Looper.prepare();

            if (TextUtils.isEmpty(Contact.rong_ip)) {
                handler.sendEmptyMessage(5);

            } else {
                PhoneToLogin(email, password);

            }
            Looper.loop();
        }

    };
    Runnable init_dia = new Runnable() {

        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            Looper.prepare();
            init_dialog();
            Looper.loop();
        }

    };
    Runnable init_data_task = new Runnable() {

        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            Looper.prepare();
            try {
                MyLog.i("initresult" + initresult);
                SubmitPost(initresult, Contacts.Init_data);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Looper.loop();
        }

    };
    Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:

                    if (get_sharePreferences_is_have_init().equals("1")) {
                        Getlunbotu();
                    } else {

                        hidedialogs();
                        Toast.makeText(mContext, "初始化数据错误，请进行扫描设置", 0).show();
                    }
                    break;
                case 0:
                    hidedialogs();
                    break;
                case 2:
                    txt1.setText("" + get_sharePreferences_appname());
                    txt2.setText("" + get_sharePreferences_tvname());
                    break;
                case 3:
                    showdialog();
                    break;
                case 4:
                    new Thread(init_data_task).start();
                    break;
                case 5:
                    Toast.makeText(mContext, "初始化数据错误，请进行扫描设置", 0).show();
                    hidedialogs();
                    break;
                case 6:
                    Toast.makeText(mContext, "请求失败,请联系管理员", 0).show();
                    break;
                case 7:
                    Toast.makeText(mContext, "数据错误，请联系后台管理员", 0).show();
                    break;

            }
        }

        ;
    };
    private UpdateInfo Init_data;


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        txt1.setText("" + get_sharePreferences_appname());
        txt2.setText("" + get_sharePreferences_tvname());
        if (is_init == true) {
            handler.sendEmptyMessage(3);
            is_init = false;
            handler.sendEmptyMessageDelayed(4, 1000);
        } else {
        }
    }

    public void SubmitPost(String string1, String url) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("vsInData0", string1));
        UrlEncodedFormEntity uefEntity;
        uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
        httppost.setEntity(uefEntity);
        HttpResponse response = httpclient.execute(httppost);

        HttpEntity entity = response.getEntity();
        if (entity != null) {

            Init_data = new UpdateInfo();
            InputStream instreams = entity.getContent();

            String str = ConvertStreamToString(instreams);
            MyLog.i("result:aaa" + str);
            try {
                Init_data = XMLParserUtil.Parser_version(str);
                SharedPreferences preferences = getSharedPreferences("user",
                        Context.MODE_PRIVATE);
                Editor editor = preferences.edit();
                // 获取失败
                handler.sendEmptyMessage(0);

                if (!Init_data.getVnResult().equals("0")) {
                    Toast.makeText(mContext, "初始化数据失败，请联系管理员", 0).show();
                    editor.putString("is_have", "0"); // 表示失败
                } else {
                    editor.clear();
                    if (!TextUtils.isEmpty(Init_data.getVsOutData10())) {
//                        editor.putString("swkey", Init_data.getVsOutData9());//声网
                        editor.putString("rykey", Init_data.getVsOutData10());//融云
                        Intent mIntent = new Intent("init");

                        //发送广播
                        sendBroadcast(mIntent);
                    } else {
                        editor.putString("is_have", "0"); // 表示不欧克
                        handler.sendEmptyMessage(0);
                        handler.sendEmptyMessage(7);
                        editor.commit();
                        return;
                    }
                    if (!TextUtils.isEmpty(Init_data.getVsOutData11())) {
                        try {
                            String zimeiti_ip = Init_data.getVsOutData11().substring(0, Init_data.getVsOutData11().indexOf(";"));
                            String zimeiti_upload = Init_data.getVsOutData11().substring(zimeiti_ip.length() + 1);
//                            String zimeiti_key = Init_data.getVsOutData7();    不用了，不在这里配了。

                            editor.putString("zimeiti_ip", zimeiti_ip);
                            editor.putString("zimeiti_upload", zimeiti_upload);

                            // app name
                            editor.putString("vsOutData0", Init_data.getVsOutData0());
                            // 电视台
                            editor.putString("vsOutData1", Init_data.getVsOutData1());
                            // 服务器ip
                            editor.putString("vsOutData2", Init_data.getVsOutData2());
                            // 图片 宽度
                            editor.putString("vsOutData3", Init_data.getVsOutData3());
                            // 每次加载的条数
                            editor.putString("vsOutData4", Init_data.getVsOutData4());
                            editor.putString("vsOutData5", Init_data.getVsOutData5());

                            editor.putString("vsOutData12", Init_data.getVsOutData12());
                            editor.putString("vsOutData13", Init_data.getVsOutData13());
                            editor.putString("vsOutData14", Init_data.getVsOutData14());
                            editor.putString("vsOutData15", Init_data.getVsOutData15());

                            editor.putString("is_have", "1"); // 表示ok
                            Contact.rong_ip = Init_data.getVsOutData6();

                            editor.putString("vsOutData6", Init_data.getVsOutData6());
                        } catch (Exception e) {

                            handler.sendEmptyMessage(7);
                            return;
                        }
                    }



                 /*   Contact.rong_ip="http://192.168.2.15";

                    editor.putString("vsOutData6", "http://192.168.2.15");*/

                    editor.commit();

                    handler.sendEmptyMessage(2);
                }

            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                handler.sendEmptyMessage(0);
            }
        } else {
            handler.sendEmptyMessage(0);
        }
    }

    public static String ConvertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        StringBuilder sb = new StringBuilder();

        String line = null;

        try {

            while ((line = reader.readLine()) != null) {

                sb.append(line + "\n");

            }

        } catch (IOException e) {

            System.out.println("Error=" + e.toString());

        } finally {

            try {

                is.close();

            } catch (IOException e) {

                System.out.println("Error=" + e.toString());

            }
        }
        return sb.toString();

    }

    String lunbotu;

    public void Getlunbotu() {

        RequestParams params = new RequestParams();


        HttpUtils httpUtils = new HttpUtils();

        httpUtils.send(HttpRequest.HttpMethod.POST, Contact.get_lunbotu, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // TODO Auto-generated method stub
                SharedPreferences preferences = mContext.getSharedPreferences("user",
                        Context.MODE_PRIVATE);
                lunbotu = preferences.getString("lunbotu", "");
                new Thread(networkTask).start();
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                // TODO Auto-generated method stub
                SharedPreferences preferences = mContext.getSharedPreferences("user",
                        Context.MODE_PRIVATE);


                lunbotu = arg0.result;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("lunbotu", lunbotu);
                editor.apply();
                new Thread(networkTask).start();
            }
        });
    }


    public void PhoneToLogin(final String username, final String password) {

        RequestParams params = new RequestParams();
        params.addBodyParameter("password", password);
        params.addBodyParameter("username", username);
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        String token = preferences.getString("vsOutData6", "");

        HttpUtils httpUtils = new HttpUtils();
//        Contact.rong_ip = "http://192.168.2.15";
        MyLog.i("地址：" + Contact.rong_ip + Contact.rong_login);
        httpUtils.send(HttpRequest.HttpMethod.POST, Contact.rong_ip + Contact.rong_login, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // TODO Auto-generated method stub
                MyLog.i("失败：" + arg1.toString());
                setClickable(true);
                handler.sendEmptyMessage(0);
                handler.sendEmptyMessage(6);
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                // TODO Auto-generated method stub
                handler.sendEmptyMessage(0);
                String str = arg0.result;
                UserInfo userInfo = JsonUtils.getModel(str, UserInfo.class);//这里已经解析出来了
                MyLog.i("融媒体账号登录返回数据：" + str);
                if (userInfo.getCode() == 0) {

                    SharedPreferences preferences = getSharedPreferences("user",
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("rong_userinfo", str);
                    editor.putString("zimeiti_key", userInfo.getResult().getMediaApikey());
//                    editor.putString("zimeiti_key",  "82D039ED09DD4E2CBDFBA9B81BD71403");
                    editor.apply();
                    String ps = password;
                    String user = username;
                    String token = userInfo.getResult().getRyToken();
                    String userid = "" + userInfo.getResult().getUserId();
                    String ry_video_id = "" + userInfo.getResult().getRyId();
                    String name = "" + userInfo.getResult().getName();
                    String sportraiUri = "" + userInfo.getResult().getPortraituri();
                    write_sharePreferencesrong(user, ps, token, userid, sportraiUri, ry_video_id, userInfo.getResult().getMediaUserid());
                    write_sharePreferUserToken(userInfo.getResult().getToken());
                    write_sharePreferRealName(name);

                    Intent it = new Intent(LoginActivity.this, MainActivity_tab.class);
                    it.putExtra("lunbotu", lunbotu);
                    startActivity(it);
                    finish();
                } else {
                    setClickable(true);
                    handler.sendEmptyMessage(6);
                    handler.sendEmptyMessage(0);
                }
            }
        });
    }
}
