package com.shadt.ui;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shadt.activity.LoginActivity;
import com.shadt.application.MyApp;
import com.shadt.bean.Friend;
import com.shadt.bean.UpdateInfo;
import com.shadt.bean.UserBean;
import com.shadt.caibian_news.R;
import com.shadt.service.LongRunningService;
import com.shadt.service.UPDATADownloadService;
import com.shadt.ui.contacts.Contact;
import com.shadt.ui.db.Tongxunlu;
import com.shadt.ui.fragment.HomeFragment;
import com.shadt.ui.fragment.MessageFragment;
import com.shadt.ui.fragment.MineFragment;
import com.shadt.ui.fragment.ReportFragment;
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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class MainActivity_tab extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, RongIM.UserInfoProvider {
    public static ViewPager mViewPager;
    private List<Fragment> mFragment = new ArrayList<>();
    private ImageView moreImage, mImageChats, mImageContact, mImageFind, mImageMe, mMineRed;
    private TextView mTextChats, mTextContact, mTextFind, mTextMe;
    boolean is_location = false;
    String lunbotu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);

        lunbotu = getIntent().getStringExtra("lunbotu");
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        is_location = preferences.getBoolean("is_location", false);
        if (is_location == true) {
            Intent intent = new Intent(this, LongRunningService.class);
            startService(intent);
        }
        initweb();


        if (!TextUtils.isEmpty(get_sharePreferences_name())) {
            long timeStamp = System.currentTimeMillis() / 1000;
            //当前时间戳大于一天的时间戳就要重新登录
            if (timeStamp >= get_sharePreferences_timestamp() + 86400) {
                new Thread(networkTask).start();
            }
        }
    }

    String daiding_url = "";
    String redexamine = "", rededit = "";
    private UpdateInfo updateInfo;
    String rong_userid, rong_username, rong_img, rong_token;
    ImageView search_image_layout;

    public void initweb() {
        initPages();
        RelativeLayout chatRLayout = (RelativeLayout) findViewById(R.id.seal_chat);
        RelativeLayout contactRLayout = (RelativeLayout) findViewById(R.id.seal_contact_list);
        RelativeLayout foundRLayout = (RelativeLayout) findViewById(R.id.seal_find);
        RelativeLayout mineRLayout = (RelativeLayout) findViewById(R.id.seal_me);
        mImageChats = (ImageView) findViewById(R.id.tab_img_chats);
        mImageContact = (ImageView) findViewById(R.id.tab_img_contact);
        mImageFind = (ImageView) findViewById(R.id.tab_img_find);
        mImageMe = (ImageView) findViewById(R.id.tab_img_me);
        mTextChats = (TextView) findViewById(R.id.tab_text_chats);
        mTextContact = (TextView) findViewById(R.id.tab_text_contact);
        mTextFind = (TextView) findViewById(R.id.tab_text_find);
        mTextMe = (TextView) findViewById(R.id.tab_text_me);
        mViewPager = findViewById(R.id.main_viewpager);
        search_image_layout = (ImageView) findViewById(R.id.mImgSearch1);
        chatRLayout.setOnClickListener(this);
        contactRLayout.setOnClickListener(this);
        foundRLayout.setOnClickListener(this);
        mineRLayout.setOnClickListener(this);
        search_image_layout.setOnClickListener(this);
        initMainViewPager();

    }

    private void initMainViewPager() {
        changeTextViewColor();
        changeSelectedTabState(0);
        mFragment.add(HomeFragment.newInstance(lunbotu));
        mFragment.add(new ReportFragment());
        mFragment.add(new MessageFragment());
        mFragment.add(new MineFragment());
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragment.get(position);
            }

            @Override
            public int getCount() {
                return mFragment.size();
            }
        };
        mViewPager.setAdapter(fragmentPagerAdapter);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setOnPageChangeListener(this);

    }

    private void changeTextViewColor() {
        mImageChats.setBackgroundDrawable(getResources().getDrawable(R.drawable.img_main_home_n));
        mImageContact.setBackgroundDrawable(getResources().getDrawable(R.drawable.img_main_living_n));
        mImageFind.setBackgroundDrawable(getResources().getDrawable(R.drawable.img_main_hudong_n));
        mImageMe.setBackgroundDrawable(getResources().getDrawable(R.drawable.img_main_me_n));
        mTextChats.setTextColor(getResources().getColor(R.color.titleColor));
        mTextContact.setTextColor(getResources().getColor(R.color.titleColor));
        mTextFind.setTextColor(getResources().getColor(R.color.titleColor));
        mTextMe.setTextColor(getResources().getColor(R.color.titleColor));

    }

    private void changeSelectedTabState(int position) {
        AlphaAnimation appearAnimation = new AlphaAnimation(0, 1);
        appearAnimation.setDuration(500);

        AlphaAnimation disappearAnimation = new AlphaAnimation(1, 0);
        disappearAnimation.setDuration(500);
        switch (position) {
            case 0:
                if (search_image_layout.getVisibility() == View.VISIBLE) {
                    search_image_layout.startAnimation(disappearAnimation);
                    disappearAnimation.setAnimationListener(new Animation.AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            search_image_layout.setVisibility(View.GONE);
                        }
                    });
                }
                mTextChats.setTextColor(getResources().getColor(R.color.titleColorSelected));
                mImageChats.setBackgroundDrawable(getResources().getDrawable(R.drawable.img_main_home_s));
                break;
            case 1:
                if (search_image_layout.getVisibility() == View.GONE) {
                    search_image_layout.startAnimation(appearAnimation);
                    search_image_layout.setVisibility(View.VISIBLE);
                }
                mTextContact.setTextColor(getResources().getColor(R.color.titleColorSelected));
                mImageContact.setBackgroundDrawable(getResources().getDrawable(R.drawable.img_main_living_s));
                break;
            case 2:
                if (search_image_layout.getVisibility() == View.VISIBLE) {
                    search_image_layout.startAnimation(disappearAnimation);
                    disappearAnimation.setAnimationListener(new Animation.AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            search_image_layout.setVisibility(View.GONE);
                        }
                    });
                }
                mTextFind.setTextColor(getResources().getColor(R.color.titleColorSelected));
                mImageFind.setBackgroundDrawable(getResources().getDrawable(R.drawable.img_main_hudong_s));
                break;
            case 3:
                if (search_image_layout.getVisibility() == View.VISIBLE) {
                    search_image_layout.startAnimation(disappearAnimation);
                    disappearAnimation.setAnimationListener(new Animation.AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            search_image_layout.setVisibility(View.GONE);
                        }
                    });
                }


                mTextMe.setTextColor(getResources().getColor(R.color.titleColorSelected));
                mImageMe.setBackgroundDrawable(getResources().getDrawable(R.drawable.img_main_me_s));
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.seal_chat:
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.seal_contact_list:
                mViewPager.setCurrentItem(1, false);
                break;
            case R.id.seal_find:
                mViewPager.setCurrentItem(2, false);
                break;
            case R.id.seal_me:
                mViewPager.setCurrentItem(3, false);

                break;
            case R.id.mImgSearch1:
                startActivity(new Intent(getApplication(), SearchActivity_miziti.class));

                break;

        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        changeTextViewColor();
        changeSelectedTabState(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    String ry_video_id;

    @Override
    public void initPages() {
        handler.sendEmptyMessage(2);
        userIdList = new ArrayList<Friend>();

        rong_username = get_sharePreferences_ronguser();
        rong_img = get_sharePreferences_ronguserimg();
        ry_video_id = get_sharePreferences_ry_video_id();
        //https://b-ssl.duitang.com/uploads/item/201409/21/20140921185525_fjGRe.png
        MyLog.i("ry_video_id" + ry_video_id + ">>d" + get_sharePreferences_rongip() + rong_img);
        userIdList.add(new Friend(ry_video_id, rong_username, get_sharePreferences_rongip() + rong_img));
        rong_token = get_sharePreferences_rongtoken();



        RongIM.setUserInfoProvider(this, true);
        setconnection(rong_token);

    }

    private void initView() {

        Intent it = getIntent();
        rededit = it.getStringExtra("recordedit");
        redexamine = it.getStringExtra("recordexamine");
        File dir = new File(OtherFinals.DIR_IMG);
        Log.v("ceshi", "dsds" + get_sharePreferences_pagenum());
        if (!dir.exists()) {// Launch camera to take photo for
            dir.mkdirs();// 创建照片的存储目录
            Log.v("ceshi", "bucunzai");
        } else {
            Log.v("ceshi", "cunzai");
        }


    }

    // 用handle进行 更新ui
    Handler handler = new Handler() {
        @Override
        @SuppressLint("ShowToast")
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 6:
                    updata_dialog();
                    break;
                case 2:
                    new Thread(networkTask2).start();
                    break;
                case 8:
                    connect_fail();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    public static boolean is_finish = false;
    // 线程发送请求 这个是 获取 logo 图标 和 app_name 版本信息
    Runnable networkTask2 = new Runnable() {

        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            Looper.prepare();
            try {
                SubmitPost(Contacts.AREA_ID, "0", null, null, null, null, null,
                        Contacts.UPDATA);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Looper.loop();
        }
    };

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        /*displayBriefMemory();*/
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();

        if (is_finish == true) {
            is_finish = false;
            Intent it = new Intent(mContext, LoginActivity.class);
            startActivity(it);
            finish();
        }

    }

    /* private void displayBriefMemory() {
         final ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
         ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
         activityManager.getMemoryInfo(info);
         Log.i("ceshi","系统剩余内存:"+(info.availMem >> 10)+"k");
         Log.i("ceshi","系统是否处于低内存运行："+info.lowMemory);
         Log.i("ceshi","当系统剩余内存低于"+info.threshold+"时就看成低内存运行");
     }*/
    // 获取 板块内容 和 logo name.
    private MyApp app;
    private Context mContext = MainActivity_tab.this;
    private String version_name;
    private int currentVersionCode;

    public void SubmitPost(String string1, String string2, String string3,
                           String string4, String string5, String string6, String string7,
                           String url) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);

        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("vsInData0", string1));
        formparams.add(new BasicNameValuePair("vsInData1", string2));
        formparams.add(new BasicNameValuePair("vsInData2", string3));
        formparams.add(new BasicNameValuePair("vsInData3", string4));
        formparams.add(new BasicNameValuePair("vsInData4", string5));
        formparams.add(new BasicNameValuePair("vsInData5", string6));
        formparams.add(new BasicNameValuePair("vsInData6", string7));
        UrlEncodedFormEntity uefEntity;
        uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
        httppost.setEntity(uefEntity);
        HttpResponse response = httpclient.execute(httppost);

        HttpEntity entity = response.getEntity();
        if (entity != null) {

            updateInfo = new UpdateInfo();
            InputStream instreams = entity.getContent();

            String str = ConvertStreamToString(instreams);
            Log.v("ceshi", "" + str);
            try {
                updateInfo = XMLParserUtil.Parser_version(str);
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            app = (MyApp) getApplication();
            PackageManager manager = mContext.getPackageManager();
            try {
                PackageInfo info = manager.getPackageInfo(
                        mContext.getPackageName(), 0);
                UPDATADownloadService.apkUrl = updateInfo.getVsOutData6();
                daiding_url = updateInfo.getVsOutData7();
                version_name = info.versionName; // 版本名
                currentVersionCode = info.versionCode; // 版本号
            } catch (PackageManager.NameNotFoundException e) {
                // TODO Auto-generated catch blockd
                e.printStackTrace();
            }
            MyLog.i("version_name" + version_name + "updateInfo.getVsOutData4()" + updateInfo.getVsOutData4());
            // 上面是获取manifest中的版本数据，我是使用versionCode
            // 在从服务器获取到最新版本的versionCode,比较
            if (!TextUtils.isEmpty(updateInfo.getVsOutData4())) {
                if (!version_name.equals(updateInfo.getVsOutData4())) {
                    handler.sendEmptyMessageDelayed(6, 1000);

                }
            }
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

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        try {
            deleteFolderFile(OtherFinals.DIR_IMG, true);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /**
     * 删除指定目录下文件及目录
     *
     * @param deleteThisPath
     * @param filepath
     * @return
     */
    public void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 处理目录
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {// 如果是文件，删除
                        file.delete();
                    } else {// 目录
                        if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    public void setconnection(String token) {
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {

                MyLog.i("onTokenIncorrect");

                handler.sendEmptyMessage(8);

            }

            @Override
            public void onSuccess(String s) {

                MyLog.i("成功");
                SharedPreferences preferences = getSharedPreferences("user",
                        Context.MODE_PRIVATE);
                String str_tongxunlu = preferences.getString("tongxunlu", "");
                if (!TextUtils.isEmpty(str_tongxunlu)) {
                    Tongxunlu tongxunlu = JsonUtils.getModel(str_tongxunlu, Tongxunlu.class);
                    for (int i = 0; i < tongxunlu.getResult().size(); i++) {
                        RongIM.getInstance().refreshUserInfoCache(new UserInfo("" + tongxunlu.getResult().get(i).getUserId(), "" + tongxunlu.getResult().get(i).getName(), Uri.parse(Contact.rong_ip + "" + tongxunlu.getResult().get(i).getPortraituri())));
                    }
                }
            }

            @Override
            public void onError(final RongIMClient.ErrorCode e) {
                MyLog.i(" ");
            }

        });
    }

    private List<Friend> userIdList;

    @Override
    public UserInfo getUserInfo(String s) {
        for (Friend i : userIdList) {
            if (i.getUserId().equals(s)) {
                return new UserInfo(i.getUserId(), i.getUserName(), Uri.parse(i.getPortraitUri()));
            }
        }

        return null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 1235645488 - 0 > 3000

            moveTaskToBack(true);

        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onClickListener(View v) {

    }

    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            Looper.prepare();
            try {
                if (!TextUtils.isEmpty(get_sharePreferences_fuwuqi())) {

                    xml(get_sharePreferences_name(), get_sharePreferences_paw());
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Looper.loop();
        }

    };

    public void connect_fail() {
        Dialog dialog = new AlertDialog.Builder(mContext).setMessage("连接聊天服务器失败...")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }

                }).create();
        dialog.show();
    }

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


                } else {
                    write_sharePreferences(name, pass, "");
                }
            } else {


            }
        } catch (Exception e) {
            // TODO: handle exception

        }
    }

}
