/*
package com.shadt.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.shadt.activity.LoginActivity;
import com.shadt.application.MyApp;
import com.shadt.bean.Friend;
import com.shadt.bean.UpdateInfo;
import com.shadt.caibian_news.R;
import com.shadt.service.LongRunningService;
import com.shadt.service.UPDATADownloadService;
import com.shadt.ui.contacts.Contact;
import com.shadt.ui.db.LunboInfo;
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

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class MainActivity extends BaseActivity implements RongIM.UserInfoProvider {

    private FragmentTabHost mTabHost;
    private ViewPager mViewPager;
    private List<Fragment> mFragmentList;
    private Class mClass[] = {HomeFragment.class, ReportFragment.class, MessageFragment.class, MineFragment.class};
    private String mTitles[] = {"融媒", "内容库", "互动", "我的"};
    private int mImages[] = {
            R.drawable.tab_home,
            R.drawable.tab_report,
            R.drawable.tab_message,
            R.drawable.tab_mine,
    };
    boolean is_location = false;
    String lunbotu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment);
        lunbotu= getIntent().getStringExtra("lunbotu");


        init();
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        is_location = preferences.getBoolean("is_location", false);
        if (is_location == true) {
            Intent intent = new Intent(this, LongRunningService.class);
            startService(intent);
        }

    }

    @Override
    public void initPages() {

    }

    @Override
    public void onClickListener(View v) {
        switch (v.getId()) {

        }

    }

    String daiding_url = "";
    String redexamine = "", rededit = "";
    private UpdateInfo updateInfo;
    String rong_userid, rong_username, rong_img, rong_token;

    private void init() {

        initView();

        initEvent();

        handler.sendEmptyMessage(2);

        userIdList = new ArrayList<Friend>();
        rong_userid = get_sharePreferences_ronguserid();
        rong_username = get_sharePreferences_ronguser();
        rong_img = get_sharePreferences_ronguserimg();
        MyLog.i("rong_img" + rong_img);
//        RongIM.getInstance().refreshUserInfoCache(new UserInfo(rong_userid, rong_username, Uri.parse(rong_img)));
        userIdList.add(new Friend(rong_userid, rong_username, rong_img));
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

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        init_fragment();
    }

    public void init_fragment() {
        dismissLoadingDialog();
        mFragmentList = new ArrayList<Fragment>();

        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null);
        mViewPager.setOffscreenPageLimit(4);
        for (int i = 0; i < 4; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTitles[i]).setIndicator(getTabView(i));
            mTabHost.addTab(tabSpec, mClass[i], null);
            if (i == 0) {
                mFragmentList.add(i, HomeFragment.newInstance(lunbotu));
            } else if (i == 1) {
                mFragmentList.add(i, new ReportFragment());

            } else if (i == 2) {
                mFragmentList.add(i, new MessageFragment());

            } else if (i == 3) {
                mFragmentList.add(i, new MineFragment());

            }


            mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.WHITE);
        }

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }
        });
    }

    private View getTabView(int index) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_item, null);

        ImageView image = (ImageView) view.findViewById(R.id.image);
        TextView title = (TextView) view.findViewById(R.id.title);

        image.setImageResource(mImages[index]);
        title.setText(mTitles[index]);

        return view;
    }

    private void initEvent() {

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                mViewPager.setCurrentItem(mTabHost.getCurrentTab());
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTabHost.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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

displayBriefMemory();

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

 private void displayBriefMemory() {
         final ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
         ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
         activityManager.getMemoryInfo(info);
         Log.i("ceshi","系统剩余内存:"+(info.availMem >> 10)+"k");
         Log.i("ceshi","系统是否处于低内存运行："+info.lowMemory);
         Log.i("ceshi","当系统剩余内存低于"+info.threshold+"时就看成低内存运行");
     }

    // 获取 板块内容 和 logo name.
    private MyApp app;
    private Context mContext = com.shadt.ui.MainActivity.this;
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

*
     * 删除指定目录下文件及目录
     *
     * @param deleteThisPath
     * @param filepath
     * @return


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

            }

            @Override
            public void onSuccess(String s) {

                MyLog.i("成功");

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
        Log.e("wangben", "UserId is ：" + s);
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

    public void Getlunbotu() {

        RequestParams params = new RequestParams();


        HttpUtils httpUtils = new HttpUtils();

        httpUtils.send(HttpRequest.HttpMethod.POST, Contact.get_lunbotu, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // TODO Auto-generated method stub
                SharedPreferences preferences = mContext.getSharedPreferences("user",
                        Context.MODE_PRIVATE);
                String s = preferences.getString("lunbotu", "");


            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                // TODO Auto-generated method stub
                SharedPreferences preferences = mContext.getSharedPreferences("user",
                        Context.MODE_PRIVATE);


                String str = arg0.result;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("lunbotu", str);
                editor.apply();
            }
        });
    }




}
*/
