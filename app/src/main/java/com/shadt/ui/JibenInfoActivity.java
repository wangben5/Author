package com.shadt.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.multipart.MultipartEntity;
import com.lidroid.xutils.http.client.multipart.content.FileBody;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.shadt.activity.GuanlianActivity;
import com.shadt.application.MyApp;
import com.shadt.caibian_news.R;
import com.shadt.ui.contacts.Contact;
import com.shadt.ui.db.MsgInfo;
import com.shadt.ui.db.UserInfo;
import com.shadt.ui.utils.JsonUtils;
import com.shadt.ui.widget.CircleImageView;
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


public class JibenInfoActivity extends BaseActivity {


    private TextView mTitle;
    private LinearLayout img_back, line_chat_msg;
    private StringBuffer mCallNumberText = new StringBuffer("");
    private String channelName = "channelid";
    private final int REQUEST_CODE = 0x01;
    Context mContext;
    CircleImageView img_head;
    TextView text_username, text_qiyename, text_loginname, text_bumen;

    UserInfo userInfo;
    TextView text_guanlian_name;
    EditText mEdittext_live, text_real_name, text_phone, text_email;
    TextView txt_changge;
    LinearLayout line_userinfo_change;
    String rong_userinfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        themeId = R.style.picture_default_style;

        rong_userinfo = get_sharePreferences_userinofo();
        userInfo = JsonUtils.getModel(rong_userinfo, UserInfo.class);
        mContext = JibenInfoActivity.this;


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
                    PictureFileUtils.deleteCacheDirFile(JibenInfoActivity.this);
                } else {
                    Toast.makeText(JibenInfoActivity.this,
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
            case R.id.img_head:
                into_picture();
                break;
            case R.id.txt_changge:

                if (TextUtils.isEmpty(text_real_name.getText().toString())) {

                    showNotifyDialog("用户名不能为空");
                    return;

                }

                if (userInfo.getResult().getMobile().equals(text_phone.getText().toString())) {
                    if (userInfo.getResult().getEmail().equals(text_email.getText().toString())) {
                        if (userInfo.getResult().getName().equals(text_real_name.getText().toString())) {
                            finish();
                            return;
                        }
                    }
                }
                if (!TextUtils.isEmpty(userInfo.getResult().getMobile())) {
                    if (TextUtils.isEmpty(text_phone.getText().toString())) {

                        showNotifyDialog("手机号不能为空");
                    } else {
                        if (userInfo.getResult().getMobile().equals(text_phone.getText().toString())) {
                            //手机号一样的不验证了
                            if (!TextUtils.isEmpty(userInfo.getResult().getEmail())) {
                                if (TextUtils.isEmpty(text_email.getText().toString())) {
                                    showNotifyDialog("邮箱不能为空");
                                } else {
                                    if (userInfo.getResult().getEmail().equals(text_email.getText().toString())) {
                                        showLoadingDialog("信息更新中...");
                                        //邮箱也不用验证了  。。直接改用户名    方法里面有判断
                                        handler.sendEmptyMessageDelayed(9, 1000);
                                    } else {
                                        showLoadingDialog("信息更新中...");
                                        handler.sendEmptyMessageDelayed(8, 1000);
                                    }
                                }
                            }
                        } else {
                            //手机需要验证
                            if (!TextUtils.isEmpty(userInfo.getResult().getEmail())) {
                                if (TextUtils.isEmpty(text_email.getText().toString())) {
                                    showNotifyDialog("邮箱不能为空");
                                } else {
                                    if (userInfo.getResult().getEmail().equals(text_email.getText().toString())) {
                                        //邮箱一样的，就不验证了  传1不验证邮箱
                                        showLoadingDialog("信息更新中...");
                                        handler.sendEmptyMessageDelayed(10, 1000);
                                    } else {
                                        showLoadingDialog("信息更新中...");
                                        handler.sendEmptyMessageDelayed(7, 1000);

                                    }
                                }
                            }
                        }
                    }
                }
                break;
            default:
                break;
        }
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

    public void edit_dialog() {
        // TODO Auto-generated method stub

        myDialog = new AlertDialog.Builder(mContext).create();
        myDialog.show();
        myDialog.getWindow().setContentView(R.layout.edit_live_dialog);
        myDialog.setCanceledOnTouchOutside(false);
        final EditText md = myDialog.getWindow().findViewById(R.id.medit_live_url);
        //只用下面这一行弹出对话框时需要点击输入框才能弹出软键盘
        myDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        //加上下面这一行弹出对话框时软键盘随之弹出
        myDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        md.setText(mEdittext_live.getText().toString());
        md.setEnabled(true);
        md.setSelection(mEdittext_live.getText().toString().length());
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
                        write_live_url(md.getText().toString());
                        isIs_guanlian_live = false;
                        mEdittext_live.setText(md.getText().toString());
                        img_switch_living.setImageResource(R.drawable.img_location_off);

                    }
                });

    }

    LinearLayout line_chat_call;

    public void init() {
        txt_changge = findViewById(R.id.txt_changge);
        txt_changge.setText("保存");
        txt_changge.setVisibility(View.VISIBLE);
        txt_changge.setOnClickListener(this);
        img_head = (CircleImageView) findViewById(R.id.img_head);
        img_back = (LinearLayout) findViewById(R.id.line_back);
        String img = get_sharePreferences_ronguserimg();
        if (!TextUtils.isEmpty(img))
            if (!img.contains("storage")) {
                ImageLoader.getInstance().displayImage(get_sharePreferences_rongip() + img, img_head, MyApp.getOptions());
            } else {
                ImageLoader.getInstance().displayImage("file://" + img, img_head);
            }

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        title = (TextView) findViewById(R.id.title);
        title.setText("基本资料");
        str_call_phone = "" + userInfo.getResult().getUserId();
        mMyAccount = get_sharePreferences_ronguserid();
        line_chat_call = (LinearLayout) findViewById(R.id.line_chat_call);
        line_chat_msg = (LinearLayout) findViewById(R.id.line_chat_msg);
        text_username = (TextView) findViewById(R.id.text_username);

        text_loginname = (TextView) findViewById(R.id.text_loginname);
        text_bumen = (TextView) findViewById(R.id.text_bumen);
        text_real_name = (EditText) findViewById(R.id.text_real_name);
        text_phone = (EditText) findViewById(R.id.text_phone);
        text_email = (EditText) findViewById(R.id.text_email);
        text_guanlian_name = (TextView) findViewById(R.id.text_guanlian_name);
        img_switch_guanlian = (ImageView) findViewById(R.id.img_switch_guanlian);
        line_userinfo_change = findViewById(R.id.line_userinfo_change);
        text_username.setText("" + userInfo.getResult().getName());

        text_loginname.setText("" + userInfo.getResult().getUsername());//登录名
        text_bumen.setText("" + userInfo.getResult().getDeptName());
        text_real_name.setText("" + userInfo.getResult().getName());
        text_phone.setText("" + userInfo.getResult().getMobile());
        text_email.setText("" + userInfo.getResult().getEmail());
        img_switch_guanlian.setOnClickListener(this);
        img_head.setOnClickListener(this);
        mEdittext_live = (EditText) findViewById(R.id.mEdittext_live);
        mEdittext_live.setEnabled(false);
        mEdittext_live.setText(get_sharePreferences_liveurl());
        img_switch_living = (ImageView) findViewById(R.id.img_switch_liviurl);
        img_switch_living.setImageResource(R.drawable.img_location_off);
        img_switch_living.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isIs_guanlian_live == false) {
                    edit_dialog();
                } else {
                    isIs_guanlian_live = false;
                }
            }
        });
        line_userinfo_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
      /*          Intent it=new Intent();
                startActivity(it);*/
            }
        });
    }

    ImageView img_switch_guanlian;
    boolean is_guanlian = false;
    ImageView img_switch_living;
    boolean isIs_guanlian_live = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(get_sharePreferences_name())) {
            text_guanlian_name.setText("已关联账号(" + get_sharePreferences_name() + ")");
            is_guanlian = true;
            img_switch_guanlian.setImageResource(R.drawable.img_location_on);
        } else {
            is_guanlian = false;
            text_guanlian_name.setText("未关联");
            img_switch_guanlian.setImageResource(R.drawable.img_location_off);
        }
    }

    public List<LocalMedia> selectList = new ArrayList<>();
    private int themeId;
    private int chooseMode = PictureMimeType.ofImage();

    public void into_picture() {
        boolean mode = true;
        if (mode) {
            // 进入相册 以下是例子：不需要的api可以不写
            PictureSelector.create(this)
                    .openGallery(chooseMode)// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                    .theme(themeId)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                    .maxSelectNum(1)// 最大图片选择数量
                    .minSelectNum(1)// 最小选择数量
                    .imageSpanCount(4)// 每行显示个数
                    .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                    .previewImage(true)// 是否可预览图片
                    .previewVideo(true)// 是否可预览视频
                    .enablePreviewAudio(true) // 是否可播放音频
                    .isCamera(true)// 是否显示拍照按钮
                    .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                    //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                    //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                    .enableCrop(false)// 是否裁剪
                    .compress(true)// 是否压缩
                    .synOrAsy(true)//同步true或异步false 压缩 默认同步
                    //.compressSavePath(getPath())//压缩图片保存地址
                    //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                    .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                    .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示
                    .isGif(false)// 是否显示gif图片
                    .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                    .circleDimmedLayer(false)// 是否圆形裁剪
                    .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                    .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                    .openClickSound(false)// 是否开启点击声音
                    .selectionMedia(selectList)// 是否传入已选图片
                    .iscaijian(false)
                    //.isDragFrame(false)// 是否可拖动裁剪框(固定)
//                        .videoMaxSecond(15)
//                        .videoMinSecond(10)
                    //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                    //.cropCompressQuality(90)// 裁剪压缩质量 默认100
                    .minimumCompressSize(100)// 小于100kb的图片不压缩
                    //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                    //.rotateEnabled(true) // 裁剪是否可旋转图片
                    //.scaleEnabled(true)// 裁剪是否可放大缩小图片
                    //.videoQuality()// 视频录制质量 0 or 1
                    //.videoSecond()//显示多少秒以内的视频or音频也可适用
                    //.recordVideoSecond()//录制视频秒数 默认60s
                    .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
        } else {
            // 单独拍照
            PictureSelector.create(this)
                    .openCamera(chooseMode)// 单独拍照，也可录像或也可音频 看你传入的类型是图片or视频
                    .theme(themeId)// 主题样式设置 具体参考 values/styles
                    .maxSelectNum(1)// 最大图片选择数量
                    .minSelectNum(1)// 最小选择数量
                    .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                    .previewImage(true)// 是否可预览图片
                    .previewVideo(true)// 是否可预览视频
                    .enablePreviewAudio(true) // 是否可播放音频
                    .isCamera(true)// 是否显示拍照按钮
                    .enableCrop(true)// 是否裁剪
                    .compress(true)// 是否压缩
                    .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                    .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示
                    .isGif(false)// 是否显示gif图片
                    .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                    .circleDimmedLayer(false)// 是否圆形裁剪
                    .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                    .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                    .openClickSound(false)// 是否开启点击声音
                    .selectionMedia(selectList)// 是否传入已选图片
                    .iscaijian(false)
                    .previewEggs(false)//预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                    //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                    //.cropCompressQuality(90)// 裁剪压缩质量 默认为100
                    .minimumCompressSize(100)// 小于100kb的图片不压缩
                    //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                    //.rotateEnabled() // 裁剪是否可旋转图片
                    //.scaleEnabled()// 裁剪是否可放大缩小图片
                    //.videoQuality()// 视频录制质量 0 or 1
                    //.videoSecond()////显示多少秒以内的视频or音频也可适用
                    .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
        }
    }

    String path = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                    for (LocalMedia media : selectList) {
//                        MyLog.i("图片-----》"+media.getCompressPath());
                        MyLog.i(" media.getPath()" + media.getPath());
                        path = media.getPath();
//                        Glide.with(this)
//                                .load(path)
//                                 .into(img_head);


                        handler.sendEmptyMessage(1);
                        new Thread(networkTask_img).start();
                    }
                    break;
            }
        }
    }

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
                case 7:
                    post_phone(text_phone.getText().toString(), 0);
                    break;
                case 10:
                    post_phone(text_phone.getText().toString(), 1);
                    break;
                case 8:
                    post_email(text_email.getText().toString());
                    break;
                case 9:
                    post_changeuserinfo(text_real_name.getText().toString(), text_phone.getText().toString(), text_email.getText().toString());
                    break;
                case 11:
                    Intent it11 = new Intent();
                    it11.setAction("refreshimg");
                    it11.putExtra("name", "" + text_real_name.getText().toString());
                    sendBroadcast(it11);

                    break;
                default:
                    break;
            }
        }

        ;
    };
    Runnable networkTask_img = new Runnable() {

        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            Looper.prepare();
            try {

                // channelkey 栏目字段key

                Submit_img(get_sharePreferences_UserToken(), path);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Looper.loop();
        }
    };

    public void Submit_img(final String token, String files) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        try {
            MyLog.i("token" + token);
            HttpPost httppost = new HttpPost(Contact.rong_ip + Contact.rong_upload_img + "?token=" + token);
            MultipartEntity reqEntity = new MultipartEntity();

            FileBody file = new FileBody(new File(files));
            reqEntity.addPart("img", file);
            // file2为请求后台的File
            // upload;属性
            httppost.setEntity(reqEntity);

            HttpResponse httpResponse;
            try {
                httpResponse = httpclient.execute(httppost);

                final int statusCode = httpResponse.getStatusLine()
                        .getStatusCode();

                if (statusCode == HttpStatus.SC_OK) {
                    MyLog.i("成功");
                    handler.sendEmptyMessage(4);
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
                    MyLog.i("result" + string);

                    // Toast.makeText(AnjianBianjiActivity.this, "上传成功",
                    // 0).show();
                    write_sharePreferencesrongimg(path);
                    handler.sendEmptyMessage(3);
                    handler.sendEmptyMessage(5);
                } else {
                    MyLog.i("图片上传失败");
                    handler.sendEmptyMessage(6);
                }

            } catch (ClientProtocolException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                handler.sendEmptyMessage(2);
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block

        }
    }


    public void post_changeuserinfo(String name, String phone, String email) {

        RequestParams params = new RequestParams();
        String id = get_sharePreferences_ronguserid();
        params.addBodyParameter("userId", id);


        params.addBodyParameter("name", name);


        params.addBodyParameter("phone", phone);


        params.addBodyParameter("email", email);


        String token = get_sharePreferences_UserToken();
        params.addBodyParameter("token", token);
        HttpUtils httpUtils = new HttpUtils();
        String Posturl = Contact.rong_ip + Contact.rong_change_info;
        MyLog.i("Posturl" + Posturl);
        httpUtils.send(HttpRequest.HttpMethod.POST, Posturl, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // TODO Auto-generated method stub
                dismissLoadingDialog();
                showNotifyDialog("信息修改失败!");

            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                // TODO Auto-generated method stub
                String str = arg0.result;
                MyLog.i("上传成功：" + str);
                MsgInfo mUserInfo = JsonUtils.getModel(str, MsgInfo.class);
                if (mUserInfo.getCode() == 0) {
                    dismissLoadingDialog();
                    rong_userinfo = rong_userinfo.replaceAll("\"name\"+:\"" + userInfo.getResult().getName(), "\"name\":\"" + text_real_name.getText().toString());
                    rong_userinfo = rong_userinfo.replaceAll("\"mobile\"+:\"" + userInfo.getResult().getMobile(), "\"mobile\":\"" + text_phone.getText().toString());
                    rong_userinfo = rong_userinfo.replaceAll("\"email\"+:\"" + userInfo.getResult().getEmail(), "\"email\":\"" + text_email.getText().toString());
                    if (!userInfo.getResult().getName().equals(text_real_name.getText().toString())) {
                        handler.sendEmptyMessage(11);//更新名字
                        text_username.setText("" + text_real_name.getText().toString());
                    }
                    SharedPreferences preferences = getSharedPreferences("user",
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("rong_userinfo", rong_userinfo);
                    editor.apply();
                    Dialog dialog = new AlertDialog.Builder(mContext).setMessage("信息修改成功!")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                }

                            }).create();
                    dialog.show();
                } else {
                    Dialog dialog = new AlertDialog.Builder(mContext).setMessage("" + mUserInfo.getMsg())
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                }

                            }).create();
                    dialog.show();
                }


//                loginAgain();
            }
        });
    }

    public void post_email(String email) {

        RequestParams params = new RequestParams();
        String id = get_sharePreferences_ronguserid();

        params.addBodyParameter("email", email);
        String token = get_sharePreferences_UserToken();
        params.addBodyParameter("token", token);
        HttpUtils httpUtils = new HttpUtils();
        String Posturl = Contact.rong_ip + Contact.rong_check_email;
        MyLog.i("Posturl" + Posturl);
        httpUtils.send(HttpRequest.HttpMethod.POST, Posturl, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // TODO Auto-generated method stub
                dismissLoadingDialog();
                showNotifyDialog("网络错误,请稍后再试!");

            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                // TODO Auto-generated method stub
                String str = arg0.result;
                MyLog.i("上传成功：" + str);
                dismissLoadingDialog();
                MsgInfo mUserInfo = JsonUtils.getModel(str, MsgInfo.class);
                if (mUserInfo.getCode() == 0) {
                    post_changeuserinfo(text_real_name.getText().toString(), text_phone.getText().toString(), text_email.getText().toString());
                } else {
                    Dialog dialog = new AlertDialog.Builder(mContext).setMessage("" + mUserInfo.getMsg())
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                }

                            }).create();
                    dialog.show();
                }


//                loginAgain();
            }
        });
    }

    //i 如果是手机正确，邮箱错误，直接验证手机然后调用update。  1
    public void post_phone(String phone, final int i) {

        RequestParams params = new RequestParams();
        String id = get_sharePreferences_ronguserid();

        params.addBodyParameter("phone", phone);
        String token = get_sharePreferences_UserToken();
        params.addBodyParameter("token", token);
        HttpUtils httpUtils = new HttpUtils();
        String Posturl = Contact.rong_ip + Contact.rong_check_phone;
        MyLog.i("Posturl" + Posturl);
        httpUtils.send(HttpRequest.HttpMethod.POST, Posturl, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // TODO Auto-generated method stub
                dismissLoadingDialog();
                showNotifyDialog("网络错误,请稍后再试!");

            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                // TODO Auto-generated method stub
                String str = arg0.result;
                MyLog.i("上传成功：" + str);
                MsgInfo mUserInfo = JsonUtils.getModel(str, MsgInfo.class);
                dismissLoadingDialog();
                if (mUserInfo.getCode() == 0) {
                    if (i == 1) {
                        post_changeuserinfo(text_real_name.getText().toString(), text_phone.getText().toString(), text_email.getText().toString());

                    } else {
                        post_email(text_email.getText().toString());
                    }

                } else {
                    Dialog dialog = new AlertDialog.Builder(mContext).setMessage("" + mUserInfo.getMsg())
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                }

                            }).create();
                    dialog.show();
                }


//                loginAgain();
            }
        });
    }


}