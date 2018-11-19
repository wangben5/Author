package com.shadt.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.http.client.multipart.MultipartEntity;
import com.lidroid.xutils.http.client.multipart.content.FileBody;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.shadt.activity.GuanlianActivity;
import com.shadt.activity.LoginActivity;
import com.shadt.application.MyApp;
import com.shadt.caibian_news.R;
import com.shadt.ui.contacts.Contact;
import com.shadt.ui.db.UserInfo;
import com.shadt.ui.utils.JsonUtils;
import com.shadt.ui.widget.CircleImageView;
import com.shadt.ui.widget.supertext.SuperTextView;
import com.shadt.util.MyLog;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongIM;


public class UsaerInfoActivity extends BaseActivity {


    private TextView mTitle;
    private LinearLayout img_back, line_chat_msg;
    private StringBuffer mCallNumberText = new StringBuffer("");
    private String channelName = "channelid";
    private final int REQUEST_CODE = 0x01;
    Context mContext;
    CircleImageView img_head;
    TextView text_username, text_qiyename, text_loginname, text_bumen, text_real_name, text_phone, text_email;
    UserInfo userInfo;
    TextView text_guanlian_name;
    EditText mEdittext_live;
    TextView txt_changge;
    LinearLayout line_userinfo_change;
    SuperTextView sp_info_text,sp_change_pas,sp_zuxiao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo2);
        themeId = R.style.picture_default_style;
        onrejestreceiv();
        String rong_userinfo = get_sharePreferences_userinofo();
        userInfo = JsonUtils.getModel(rong_userinfo, UserInfo.class);
        mContext = UsaerInfoActivity.this;

        init();
        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    PictureFileUtils.deleteCacheDirFile(UsaerInfoActivity.this);
                } else {
                    Toast.makeText(UsaerInfoActivity.this,
                            getString(R.string.picture_jurisdiction), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });

    }

    @Override
    public void initPages() {

    }

    @Override
    public void onClickListener(View v) {
        switch (v.getId()) {
            case R.id.img_switch_guanlian:
                if (is_guanlian == true) {
                    finishi_dialog();
                } else {
                    Intent it = new Intent(this, GuanlianActivity.class);
                    startActivity(it);
                }
                break;

            case R.id.sp_info_text:
                Intent it=new Intent(mContext, JibenInfoActivity.class);
                startActivity(it);
                break;
            case R.id.sp_change_pas:
                Intent its=new Intent(mContext, Change_pwd_Activity.class);
                startActivity(its);
                break;
            case R.id.sp_zuxiao:
                loginAgain();
                break;
            default:
                break;
        }
    }
    public void loginAgain(){

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        RongIM.getInstance().logout();

    }
    public void finishi_dialog() {
        // TODO Auto-generated method stub

        myDialog = new AlertDialog.Builder(mContext).create();
        myDialog.show();
        myDialog.getWindow().setContentView(R.layout.face_dialog);
        myDialog.getWindow().findViewById(R.id.cancel)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();

                    }
                });
        myDialog.getWindow().findViewById(R.id.sure)
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                        write_sharePreferences("", "", "");
                        write_permission("", "");
                        is_guanlian = false;
                        text_guanlian_name.setText("未关联");
                        img_switch_guanlian.setImageResource(R.drawable.img_location_off);

                    }
                });
    }


    LinearLayout line_chat_call;

    public void init() {
     /*   txt_changge = findViewById(R.id.txt_changge);
        txt_changge.setText("编辑资料");
        txt_changge.setVisibility(View.VISIBLE);*/
        img_head = (CircleImageView) findViewById(R.id.img_head);
        img_back = (LinearLayout) findViewById(R.id.line_back);
        String img=get_sharePreferences_ronguserimg();
        if (!TextUtils.isEmpty(img))
        if (!img.contains("storage")){
            ImageLoader.getInstance().displayImage(get_sharePreferences_rongip() + img, img_head, MyApp.getOptions());
        }else{
            ImageLoader.getInstance().displayImage("file://" + img, img_head);
        }

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        title = (TextView) findViewById(R.id.title);
        title.setText("个人中心");
        str_call_phone = "" + userInfo.getResult().getUserId();
        mMyAccount = get_sharePreferences_ronguserid();

        text_username = (TextView) findViewById(R.id.text_username);
        sp_info_text=findViewById(R.id.sp_info_text);
        sp_change_pas=findViewById(R.id.sp_change_pas);
        sp_zuxiao=findViewById(R.id.sp_zuxiao);
        sp_info_text.setOnClickListener(this);
        sp_change_pas.setOnClickListener(this);
        sp_zuxiao.setOnClickListener(this);


        text_username.setText("" + userInfo.getResult().getName());


    }

    ImageView img_switch_guanlian;
    boolean is_guanlian = false;
    ImageView img_switch_living;
    boolean isIs_guanlian_live = false;

    @Override
    protected void onResume() {
        super.onResume();

    }

    public List<LocalMedia> selectList = new ArrayList<>();
    private int themeId;
    private int chooseMode = PictureMimeType.ofImage();



    String path = "";


    // 用handle进行 更新ui
    Handler handler = new Handler() {
        @Override
        @SuppressLint("ShowToast")
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    showLoadingDialog("头像上传中");
                    break;
                case 2:
                    dismissLoadingDialog();
                    break;
                case 3:
                    ImageLoader.getInstance().displayImage("file://" + path, img_head);
                    break;
                case 4:
                    Intent it = new Intent();
                    it.setAction("refreshimg");
                    sendBroadcast(it);
                    break;
                case 5:
                    showNotifyDialog("上传成功");
                    break;
                case 6:
                    showNotifyDialog("上传失败");
                    break;
                default:
                    break;
            }
        }

        ;
    };



    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;
    public void onrejestreceiv() {
        //动态接受网络变化的广播接收器
        intentFilter = new IntentFilter();
        intentFilter.addAction("refreshimg");

        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);
    }
    String rong_img;
    SharedPreferences preferences;
    //自定义接受网络变化的广播接收器
    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MyLog.i("你怎么不更新头像");
            String name=intent.getStringExtra("name");
                if (!TextUtils.isEmpty(name)){
                    text_username.setText(name);
                    return;
                }

             preferences = mContext.getSharedPreferences("user",
                    Context.MODE_PRIVATE);
            rong_img=preferences.getString("rong_img","");
            ImageLoader.getInstance().displayImage("file://"+rong_img, img_head);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkChangeReceiver!=null) {
            unregisterReceiver(networkChangeReceiver);
        }
    }
}