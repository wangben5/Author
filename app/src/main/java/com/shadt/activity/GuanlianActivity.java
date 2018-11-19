package com.shadt.activity;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.shadt.bean.UserBean;
import com.shadt.caibian_news.R;
import com.shadt.util.Contacts;
import com.shadt.util.MyLog;
import com.shadt.util.XMLParserUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class GuanlianActivity extends BaseActivity {
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    Button mEmailSignInButton;
    Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = GuanlianActivity.this;
        setContentView(R.layout.activity_guanlian);
        init();
    }

    @Override
    public void initPages() {

    }

    @Override
    public void onClickListener(View v) {

    }

    public void init() {
        line_back = (LinearLayout) findViewById(R.id.line_back);
        line_back.setOnClickListener(this);
        line_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                attemptLogin();
            }
        });
    }

    String email = null, mpassword = null;

    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        email = mEmailView.getText().toString();
        mpassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
            focusView.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(mpassword)) {
            mPasswordView.setError(getString(R.string.error_null_password));
            focusView = mPasswordView;
            cancel = true;
            focusView.requestFocus();
            return;
        }
        if (!isPasswordValid(mpassword)) {
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

    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            Looper.prepare();
            try {
                if (!TextUtils.isEmpty(get_sharePreferences_fuwuqi())) {

                    xml(email, mpassword);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Looper.loop();
        }

    };

    private boolean isPasswordValid(String password) {
        // TODO: Replace this with your own logic
        return password.length() >= 1;
    }

    Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    MyLog.i("email" + email + "email" + get_sharePreferences_fuwuqi());

                    new Thread(networkTask).start();

                    break;
                case 0:
                    hidedialogs();
                    break;
                case 3:
                    showdialog();
                    break;
                case 6:
                    Toast.makeText(mContext, "请求失败,请联系管理员", 0).show();
                    break;
            }
        }

    };

    public void xml(String name, String pass) throws IOException {
        try {

            String resultCode = null;

            // TODO Auto-generated method stub
            // 组建xml数据
            StringBuilder xml = new StringBuilder();

            xml.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
            xml.append("<xml>");
            xml.append("<username>" + name + "</username>");
            xml.append("<password>" + pass + "</password>");
            xml.append("</xml>");
            // 实例化一个默认的Http客户端
            DefaultHttpClient client = new DefaultHttpClient();
            client.getParams().setIntParameter(
                    CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
            HttpPost httpPost = new HttpPost(get_sharePreferences_fuwuqi()
                    + Contacts.LOGIN);
            StringEntity entity = new StringEntity(xml.toString(), HTTP.UTF_8);
            httpPost.setEntity(entity);
            HttpResponse httpResponse = client.execute(httpPost);

            if (httpResponse.getStatusLine().getStatusCode() == 200) {

                // 通过HttpEntity获得响应流
                InputStream is = httpResponse.getEntity().getContent();
                // 将流转换成string
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int len;
                while ((len = is.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
                String string = out.toString("UTF-8");
                Log.v("shadt", "result:" + string);
                is = new ByteArrayInputStream(string.getBytes("UTF-8"));
                UserBean bean = new UserBean();
                try {
                    bean = XMLParserUtil.parse(is);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // 登陆成功
                if (bean.getRetrun_code().equals("true")) { // true 是正确 false失败
                    write_sharePreferences(name, pass, bean.getToken());
                    write_permission(bean.getRecordedit(), bean.getRecordaudit());
                    finish();
                } else {
                    handler.sendEmptyMessage(0);
                    Toast.makeText(mContext, bean.getRetrun_msg(), 0).show();
                }
            } else {
                handler.sendEmptyMessage(0);
                handler.sendEmptyMessage(6);
            }
        } catch (Exception e) {
            // TODO: handle exception
            handler.sendEmptyMessage(0);
            handler.sendEmptyMessage(6);
        }
    }

}
