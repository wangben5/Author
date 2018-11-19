package com.shadt.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luck.picture.lib.widget.PhotoPopupWindow;
import com.shadt.application.MyApp;
import com.shadt.bean.UpdateInfo;
import com.shadt.caibian_news.R;
import com.shadt.service.UPDATADownloadService;
import com.shadt.ui.Change_pwd_Activity;
import com.shadt.ui.widget.CustomDialog;
import com.shadt.ui.widget.FenbianlvPopupWindow;
import com.shadt.util.Contacts;
import com.shadt.util.MyLog;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.rong.calllib.RongCallClient;
import io.rong.calllib.RongCallCommon;

public class SettingActivity extends BaseActivity {
    LinearLayout line_back;
    RelativeLayout line_exit, line_version, line_about, line_change_pwd, rela_guanlian, rela_fenbianlv;
    TextView title, version_txt, text_guanlian;
    public String ACTION_FINISH = "finish";
    ImageView img_caijian, img_switch_guanlian, vid_caijian;
    TextView text_fenbianlv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        PackageManager manager = mContext.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(
                    mContext.getPackageName(), 0);
            version_name = info.versionName; // 版本名
            currentVersionCode = info.versionCode; // 版本号
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch blockd
            e.printStackTrace();
        }
        initPages();

    }

    @Override
    public void initPages() {
        // TODO Auto-generated method stub
        initTop();
        initView();
    }

    public void initTop() {
        line_back = (LinearLayout) findViewById(R.id.line_back);
        line_back.setOnClickListener(this);
        title = (TextView) findViewById(R.id.title);
        title.setText("设置");
    }

    boolean is_guanlian = false;
    FenbianlvPopupWindow popupWindow;
    PhotoPopupWindow photoPopupWindow;
    SharedPreferences preferences;

    public void initView() {
        preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        is_caijian = preferences.getBoolean("is_caijian", false);
        is_vid_caijian = preferences.getBoolean("is_vid_caijian", false);
        img_switch_guanlian = (ImageView) findViewById(R.id.img_switch_guanlian);
        img_caijian = (ImageView) findViewById(R.id.img_caijian);
        vid_caijian = (ImageView) findViewById(R.id.vid_caijian);
        if (is_caijian == false) {
            img_caijian.setImageResource(R.drawable.img_location_off);
        } else {
            img_caijian.setImageResource(R.drawable.img_location_on);
        }
        if (is_vid_caijian == false) {
            vid_caijian.setImageResource(R.drawable.img_location_off);
        } else {
            vid_caijian.setImageResource(R.drawable.img_location_on);
        }

        line_exit = (RelativeLayout) findViewById(R.id.exit);
        line_version = (RelativeLayout) findViewById(R.id.update);
        line_about = (RelativeLayout) findViewById(R.id.about);
        line_change_pwd = (RelativeLayout) findViewById(R.id.change_pwd);
        version_txt = (TextView) findViewById(R.id.update_txt);
        rela_guanlian = (RelativeLayout) findViewById(R.id.rela_guanlian);
        text_guanlian = (TextView) findViewById(R.id.text_guanlian);
        rela_fenbianlv = (RelativeLayout) findViewById(R.id.fenbianlv);
        text_fenbianlv = (TextView) findViewById(R.id.text_fenbianlv);
        version_txt.setText("版本更新" + "(" + version_name + ")");
        line_exit.setOnClickListener(this);
        line_version.setOnClickListener(this);
        line_change_pwd.setOnClickListener(this);
        line_about.setOnClickListener(this);
        rela_guanlian.setOnClickListener(this);
        img_caijian.setOnClickListener(this);
        vid_caijian.setOnClickListener(this);
        img_switch_guanlian.setOnClickListener(this);
        rela_fenbianlv.setOnClickListener(this);
        popupWindow = new FenbianlvPopupWindow(this);
        popupWindow.setOnItemClickListener(new FenbianlvPopupWindow.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                edit(position + 1);
                txt_fenbianlv(position + 1);
            }
        });
        txt_fenbianlv(preferences.getInt("fenbianlv", 3));
    }

    public void txt_fenbianlv(int selecd) {
        if (selecd == 1) {
            text_fenbianlv.setText("(720P)");

            RongCallClient.getInstance().setVideoProfile(RongCallCommon.CallVideoProfile.VIDEO_PROFILE_720P);

        } else if (selecd == 2) {
            text_fenbianlv.setText("(480P)");
            RongCallClient.getInstance().setVideoProfile(RongCallCommon.CallVideoProfile.VIDEO_PROFILE_480P);

        } else if (selecd == 3) {
            text_fenbianlv.setText("(360P)");
            RongCallClient.getInstance().setVideoProfile(RongCallCommon.CallVideoProfile.VIDEO_PROFILE_360P);

        } else if (selecd == 4) {
            text_fenbianlv.setText("(240P)");
            RongCallClient.getInstance().setVideoProfile(RongCallCommon.CallVideoProfile.VIDEO_PROFILE_240P);

        } else if (selecd == 5) {
            text_fenbianlv.setText("(320x240)");
            RongCallClient.getInstance().setVideoProfile(RongCallCommon.CallVideoProfile.VIDEO_PROFILE_480P);

        }

    }

    boolean is_caijian = false;
    boolean is_vid_caijian = false;

    public void edit(int i) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("fenbianlv", i);
        editor.apply();

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onClickListener(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.fenbianlv:
                popupWindow.showAsDropDown(rela_fenbianlv, preferences.getInt("fenbianlv", 3));
                break;
            case R.id.rela_guanlian:

                break;
            case R.id.img_switch_guanlian:
                if (is_guanlian == true) {


                } else {

                    Intent it = new Intent(this, GuanlianActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.line_back:
                MainActivity.is_finish = false;
                finish();

                break;
            case R.id.update:
                showdialog();
                handler.sendEmptyMessageDelayed(2, 1000);
                break;
            case R.id.change_pwd:
                Intent it = new Intent(mContext, Change_pwd_Activity.class);
                startActivity(it);
                break;
            case R.id.exit:
//                finishi_dialog();
                break;
            case R.id.about:
                Intent itd = new Intent(mContext, AboutActivity.class);
                startActivity(itd);
                break;
            case R.id.img_caijian:
                if (is_caijian == false) {
                    is_caijian = true;

                    img_caijian.setImageResource(R.drawable.img_location_on);


                } else {

                    img_caijian.setImageResource(R.drawable.img_location_off);
                    is_caijian = false;

                }
                SharedPreferences preferences = getSharedPreferences("user",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("is_caijian", is_caijian);
                editor.apply();
                break;
            case R.id.vid_caijian:
                if (is_vid_caijian == false) {
                    is_vid_caijian = true;
                    vid_caijian.setImageResource(R.drawable.img_location_on);


                } else {

                    vid_caijian.setImageResource(R.drawable.img_location_off);
                    is_vid_caijian = false;

                }
                SharedPreferences preferences1 = getSharedPreferences("user",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = preferences1.edit();
                editor1.putBoolean("is_vid_caijian", is_vid_caijian);
                editor1.apply();
                break;
            default:
                break;
        }
    }

    // 用handle进行 更新ui
    Handler handler = new Handler() {
        @Override
        @SuppressLint("ShowToast")
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 6:
                    Log.v("ceshi", "show");
                    hidedialogs();
                    updata_dialog();
                    break;
                case 0:
                    showdialog();
                    break;
                case 1:
                    hidedialogs();
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
    // 获取 板块内容 和 logo name.
    private MyApp app;
    private Context mContext = SettingActivity.this;
    private String version_name;
    private int currentVersionCode;
    private UpdateInfo updateInfo;

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
            Log.v("shadt", "" + str);
            try {
                updateInfo = XMLParserUtil.Parser_version(str);
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            app = (MyApp) getApplication();
            // down_url = updateInfo.getVsOutData6();

            // 上面是获取manifest中的版本数据，我是使用versionCode
            // 在从服务器获取到最新版本的versionCode,比较
            UPDATADownloadService.apkUrl = updateInfo.getVsOutData6();
            Log.v("ceshi",
                    "version_name:" + version_name + ":"
                            + updateInfo.getVsOutData4());
            handler.sendEmptyMessage(1);
            if (!TextUtils.isEmpty(updateInfo.getVsOutData4())) {
                if (!version_name.equals(updateInfo.getVsOutData4())) {
                    handler.sendEmptyMessage(6);
                } else {
                    Toast.makeText(mContext, "当前为最新版本！", 0).show();
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 1235645488 - 0 > 3000
            MainActivity.is_finish = false;
            finish();
        }
        return false;
    }

    private void showUpdateDialog(String title, String msg) {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 设置你的操作事项
                        finish();

                    }
                });
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 设置你的操作事项
                        finish();

                    }
                });
        builder.create().show();

    }

    //设置应用

    /**
     * Get Mobile Type
     *
     * @return
     */
    private String getMobileType() {
        return Build.MANUFACTURER;
    }

    /**
     * GoTo Open Self Setting Layout
     * Compatible Mainstream Models 兼容市面主流机型
     *
     * @param context
     */
    public void jumpStartInterface(Context context) {
        Intent intent = new Intent();
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MyLog.e("******************当前手机型号为：" + getMobileType());
            ComponentName componentName = null;
            if (getMobileType().equals("Xiaomi")) { // 红米Note4测试通过
                componentName = new ComponentName("com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity");
            } else if (getMobileType().equals("Letv")) { // 乐视2测试通过
                intent.setAction("com.letv.android.permissionautoboot");
            } else if (getMobileType().equals("samsung")) { // 三星Note5测试通过
                componentName = new ComponentName("com.samsung.android.sm_cn",
                        "com.samsung.android.sm.ui.ram.AutoRunActivity");
            } else if (getMobileType().equals("HUAWEI")) { // 华为测试通过
                componentName = new ComponentName("com.huawei.systemmanager",
                        "com.huawei.systemmanager.optimize.process.ProtectActivity");
            } else if (getMobileType().equals("vivo")) { // VIVO测试通过
                componentName = ComponentName.unflattenFromString("com.iqoo.secure" +
                        "/.safeguard.PurviewTabActivity");
            } else if (getMobileType().equals("Meizu")) { //万恶的魅族
                // 通过测试，发现魅族是真恶心，也是够了，之前版本还能查看到关于设置自启动这一界面，
                // 系统更新之后，完全找不到了，心里默默Fuck！
                // 针对魅族，我们只能通过魅族内置手机管家去设置自启动，
                // 所以我在这里直接跳转到魅族内置手机管家界面，具体结果请看图
                componentName = ComponentName.unflattenFromString("com.meizu.safe" +
                        "/.permission.PermissionMainActivity");
            } else if (getMobileType().equals("OPPO")) { // OPPO R8205测试通过
                componentName = ComponentName.unflattenFromString("com.oppo.safe" +
                        "/.permission.startup.StartupAppListActivity");
                Intent intentOppo = new Intent();
                intentOppo.setClassName("com.oppo.safe/.permission.startup",
                        "StartupAppListActivity");
                if (context.getPackageManager().resolveActivity(intentOppo, 0) == null) {
                    componentName = ComponentName.unflattenFromString("com.coloros.safecenter" +
                            "/.startupapp.StartupAppListActivity");
                }

            } else if (getMobileType().equals("ulong")) { // 360手机 未测试
                componentName = new ComponentName("com.yulong.android.coolsafe",
                        ".ui.activity.autorun.AutoRunListActivity");
            } else {
                // 以上只是市面上主流机型，由于公司你懂的，所以很不容易才凑齐以上设备
                // 针对于其他设备，我们只能调整当前系统app查看详情界面
                // 在此根据用户手机当前版本跳转系统设置界面
                if (Build.VERSION.SDK_INT >= 9) {
                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                } else if (Build.VERSION.SDK_INT <= 8) {
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setClassName("com.android.settings",
                            "com.android.settings.InstalledAppDetails");
                    intent.putExtra("com.android.settings.ApplicationPkgName",
                            context.getPackageName());
                }
            }
            intent.setComponent(componentName);
            context.startActivity(intent);
        } catch (Exception e) {//抛出异常就直接打开设置页面
            intent = new Intent(Settings.ACTION_SETTINGS);
            context.startActivity(intent);
        }
    }


}
