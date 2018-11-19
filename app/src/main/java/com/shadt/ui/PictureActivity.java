package com.shadt.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.ParseException;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.shadt.caibian_news.R;
import com.shadt.ui.adapater.GridImageAdapter;
import com.shadt.ui.base.FullyGridLayoutManager;
import com.shadt.ui.contacts.Contact;
import com.shadt.ui.db.MsgInfo;
import com.shadt.ui.db.UploadResultInfo;
import com.shadt.ui.db.WenjianjiaInfo;
import com.shadt.ui.utils.JsonUtils;
import com.shadt.ui.widget.pupwindow.PopItemAction;
import com.shadt.ui.widget.pupwindow.PopWindow;
import com.shadt.util.MyLog;
import com.shadt.util.XMLParserUtil;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class PictureActivity extends BaseActivity implements View.OnTouchListener {
    public  static int RESULT_VIOCELINEVIEW=123;
    private final static String TAG = PictureActivity.class.getSimpleName();
    public List<LocalMedia> selectList = new ArrayList<>();
    private RecyclerView recyclerView;
    private GridImageAdapter adapter;
    private int maxSelectNum = 9;
    private TextView tv_select_num;
    private ImageView left_back, minus, plus;
    private int aspect_ratio_x, aspect_ratio_y;
    private int themeId;
    private int chooseMode = PictureMimeType.ofAll();
    TextView txt_changge;
    LinearLayout back;
    TextView title;
    RelativeLayout rela_upload, rela_choose, rela_bottom;
    TextView text_mywenjian;
    public static TextView text_size;
    public static TextView text_leixin;
    EditText ed_miaosu;
    //录音不显示录音按钮
    boolean is_Camera = true;
    EditText ed_key;
    String tupe;
    ScrollView msc;
    boolean is_caijian = true;
    boolean is_vid_caijian = true;

    RelativeLayout rela_task;
    TextView text_task;
    String token="";



    ImageView img_voiceline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onrejestreceiv();
        setContentView(R.layout.activity_picture_new);



        Contact.meizi_ip = get_sharePreferences_zimeitiip();
        Contact.meizi_apikey = get_sharePreferences_zimeitikey();
        Contact.meizi_upload = get_sharePreferences_zimeitiupload();
        token=get_sharePreferences_UserToken();
        is_caijian = get_sharePreferences_caijian();
        is_vid_caijian = get_sharePreferences_vidcaijian();

        themeId = R.style.picture_default_style;

        txt_changge = (TextView) findViewById(R.id.txt_changge);
        back = (LinearLayout) findViewById(R.id.line_back);
        title = (TextView) findViewById(R.id.title);
        txt_changge.setVisibility(View.GONE);
        rela_choose = (RelativeLayout) findViewById(R.id.rela_choose);
        rela_upload = (RelativeLayout) findViewById(R.id.rela_upload);
        rela_upload.setOnClickListener(this);
        rela_choose.setOnClickListener(this);
        text_mywenjian = (TextView) findViewById(R.id.text_mywenjian);
        text_leixin = (TextView) findViewById(R.id.text_leixin);
        text_size = (TextView) findViewById(R.id.text_size);
        text_size.setText("文件大小:");
        text_leixin.setText("文件类型:");
        ed_miaosu = findViewById(R.id.ed_miaosu);
        ed_miaosu.setOnTouchListener(this);
        ed_key = findViewById(R.id.ed_key);
        minus = (ImageView) findViewById(R.id.minus);
        plus = (ImageView) findViewById(R.id.plus);
        tv_select_num = (TextView) findViewById(R.id.tv_select_num);
        rela_task = findViewById(R.id.rela_task);
        text_task = findViewById(R.id.text_task);
        rela_task.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        msc = findViewById(R.id.msc);
        msc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlKeyboardLayout(msc, PictureActivity.this);
            }
        });
        img_voiceline=findViewById(R.id.img_voiceline);
        img_voiceline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it=new Intent(PictureActivity.this,VioceLineViewActivity.class);
                it.putExtra("activity",TAG);
                startActivityForResult(it,RESULT_VIOCELINEVIEW);

            }
        });

        tupe = getIntent().getStringExtra("type");
        if (tupe.equals("1")) {
            chooseMode = PictureMimeType.ofImage();
            is_Camera = true;
            title.setText("上传图片");
            maxSelectNum = 9;
        } else if (tupe.equals("2")) {
            chooseMode = PictureMimeType.ofVideo();
            is_Camera = true;
            title.setText("上传视频");
            maxSelectNum = 1;
        } else if (tupe.equals("3")) {
            chooseMode = PictureMimeType.ofAudio();
            is_Camera = true;
            title.setText("上传音频");
            maxSelectNum = 1;
        }

        minus.setOnClickListener(this);
        plus.setOnClickListener(this);

        FullyGridLayoutManager manager = new FullyGridLayoutManager(PictureActivity.this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
       /* LocalMedia mLocalMedia=new LocalMedia();
        mLocalMedia.setMimeType(0);
        mLocalMedia.setPictureType("2");
        mLocalMedia.setPath("/storage/emulated/0/Video/ScreenRecorder/Screenrecord-2018-08-29-10-19-45-499.mp4");
        mLocalMedia.setDuration(3000);
        mLocalMedia.setPosition(1);
        mLocalMedia.setNum(1);
//        mLocalMedia.setCompressPath("/storage/emulated/0/Video/ScreenRecorder/Screenrecord-2018-08-29-10-19-45-499.mp4");
        selectList.add(mLocalMedia);*/
        adapter = new GridImageAdapter(PictureActivity.this, onAddPicClickListener, handler);
        adapter.setList(selectList);
        adapter.setSelectMax(maxSelectNum);
        recyclerView.setAdapter(adapter);
        txt_changge.setOnClickListener(this);
        back.setOnClickListener(this);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (selectList.size() > 0) {
                    LocalMedia media = selectList.get(position);
                    String pictureType = media.getPictureType();
                    int mediaType = PictureMimeType.pictureToVideo(pictureType);
                    switch (mediaType) {
                        case 1:
                            // 预览图片 可自定长按保存路径
                            //PictureSelector.create(PictureActivity.this).themeStyle(themeId).externalPicturePreview(position, "/custom_file", selectList);
                            PictureSelector.create(PictureActivity.this).themeStyle(themeId).openExternalPreview(position, selectList);
                            break;
                        case 2:
                            // 预览视频
                            PictureSelector.create(PictureActivity.this).externalPictureVideo(media.getPath());
                            break;
                        case 3:
                            // 预览音频
                            PictureSelector.create(PictureActivity.this).externalPictureAudio(media.getPath());
                            break;
                    }
                }
            }
        });

        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    PictureFileUtils.deleteCacheDirFile(PictureActivity.this);
                } else {
                    Toast.makeText(PictureActivity.this,
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


    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            boolean mode = true;
            if (mode) {
                // 进入相册 以下是例子：不需要的api可以不写
                PictureSelector.create(PictureActivity.this)
                        .openGallery(chooseMode)// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                        .theme(themeId)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                        .maxSelectNum(maxSelectNum)// 最大图片选择数量
                        .minSelectNum(1)// 最小选择数量
                        .imageSpanCount(4)// 每行显示个数
                        .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                        .previewImage(true)// 是否可预览图片
                        .previewVideo(true)// 是否可预览视频
                        .enablePreviewAudio(true) // 是否可播放音频
                        .isCamera(is_Camera)// 是否显示拍照按钮
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                        //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                        .enableCrop(is_caijian)// 是否裁剪
                        .compress(is_caijian)// 是否压缩
                        .synOrAsy(true)//同步true或异步false 压缩 默认同步
                        //.compressSavePath(getPath())//压缩图片保存地址
                        //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                        .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                        .withAspectRatio(aspect_ratio_x, aspect_ratio_y)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                        .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示
                        .isGif(false)// 是否显示gif图片
                        .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                        .circleDimmedLayer(false)// 是否圆形裁剪
                        .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                        .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                        .openClickSound(false)// 是否开启点击声音
                        .selectionMedia(selectList)// 是否传入已选图片
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
                PictureSelector.create(PictureActivity.this)
                        .openCamera(chooseMode)// 单独拍照，也可录像或也可音频 看你传入的类型是图片or视频
                        .theme(themeId)// 主题样式设置 具体参考 values/styles
                        .maxSelectNum(maxSelectNum)// 最大图片选择数量
                        .minSelectNum(1)// 最小选择数量
                        .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                        .previewImage(true)// 是否可预览图片
                        .previewVideo(true)// 是否可预览视频
                        .enablePreviewAudio(true) // 是否可播放音频
                        .isCamera(is_Camera)// 是否显示拍照按钮
                        .enableCrop(is_caijian)// 是否裁剪
                        .compress(is_caijian)// 是否压缩
                        .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                        .withAspectRatio(aspect_ratio_x, aspect_ratio_y)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                        .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示
                        .isGif(false)// 是否显示gif图片
                        .freeStyleCropEnabled(false)// 裁剪框是否可拖拽
                        .circleDimmedLayer(false)// 是否圆形裁剪
                        .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                        .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                        .openClickSound(false)// 是否开启点击声音
                        .selectionMedia(selectList)// 是否传入已选图片
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

    };

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
                    msize = 0;
                    for (LocalMedia media : selectList) {
//                        MyLog.i("图片-----》"+media.getCompressPath());


                        int mediaType = PictureMimeType.pictureToVideo(media.getPictureType());
                        if (mediaType == 1) {
                            text_leixin.setText("文件类型:图片");

                            getFileSize(media.getPath());

                        } else if (mediaType == 2) {
                            text_leixin.setText("文件类型:视频");
                            text_size.setText("文件大小:" + getFileSize(media.getPath()));
                        } else if (mediaType == 3) {
                            text_leixin.setText("文件类型:音频");
                            for (int i = 0; i < selectList.size(); i++) {
                                text_size.setText("文件大小:" + getFileSize(media.getPath()));
                            }

                        }
                    }
                    text_size.setText("文件大小:" + new DecimalFormat("0.00").format((double) (msize / 1024f) / 1024f) + "MB");
                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }else if(requestCode==10086){
            if (data!=null) {
                String title = data.getStringExtra("title");
                String taskid = data.getStringExtra("id");
                if (!TextUtils.isEmpty(title)) {
                    text_task.setText(title);
                }
                if (!TextUtils.isEmpty(taskid)) {
                    task_id = taskid;
                }
            }
        }else if(requestCode==RESULT_VIOCELINEVIEW){
            if (data!=null) {
                ed_miaosu.setText(ed_miaosu.getText().toString()+ data.getStringExtra("result"));
                if (ed_miaosu.getText().length()>0){
                    ed_miaosu.setSelection(ed_miaosu.getText().length());
                }
                MyLog.i("返回的语音信息" + data.getStringExtra("result"));
            }
        }
    }
    String task_id="";
    public void delete() {
        msize = 0;
        if (selectList.size() == 0) {
            text_leixin.setText("文件类型:");
            text_size.setText("文件大小:");

        } else {
            for (LocalMedia media : selectList) {
//                        MyLog.i("图片-----》"+media.getCompressPath());


                int mediaType = PictureMimeType.pictureToVideo(media.getPictureType());
                if (mediaType == 1) {
                    text_leixin.setText("文件类型:图片");

                    getFileSize(media.getPath());

                } else if (mediaType == 2) {
                    text_leixin.setText("文件类型:视频");
                    text_size.setText("文件大小:" + getFileSize(media.getPath()));
                } else if (mediaType == 3) {
                    text_leixin.setText("文件类型:音频");
                    for (int i = 0; i < selectList.size(); i++) {
                        text_size.setText("文件大小:" + getFileSize(media.getPath()));
                    }

                }
            }
            text_size.setText("文件大小:" + new DecimalFormat("0.00").format((double) (msize / 1024f) / 1024f) + "MB");
        }
    }

    @Override
    public void onClickListener(View v) {
        switch (v.getId()) {
            case R.id.line_back:
                finish();
                break;
            case R.id.minus:
                if (maxSelectNum > 1) {
                    maxSelectNum--;
                }
                tv_select_num.setText(maxSelectNum + "");
                adapter.setSelectMax(maxSelectNum);
                break;
            case R.id.plus:
                maxSelectNum++;
                tv_select_num.setText(maxSelectNum + "");
                adapter.setSelectMax(maxSelectNum);
                break;
            case R.id.txt_changge:
                Intent it = new Intent(this, MyTaskActivity.class);
                startActivity(it);
                break;
            case R.id.rela_choose:
                //userinfo  等于空 就请求  获取文件夹
                if (userInfo == null) {
                    rela_choose.setEnabled(false);
                    Upload_wenjianjia(Contact.meizi_apikey);
                } else {
//                    showPopwindow();
                    showpop();
                }

                break;
            case R.id.rela_upload:
                if (selectList.size() == 0) {
                    Toast.makeText(this, "您还没有选择上传的文件", 0).show();
                } else {
                    if (TextUtils.isEmpty(choose_wenjianjia)) {
                        Toast.makeText(this, "您还没有选择上传的文件夹", 0).show();
                    } else {
                        if (TextUtils.isEmpty(task_id)) {
                            Toast.makeText(this, "您还没有选择任务绑定", 0).show();
                        }else {
                            new Thread(networkTask_tijiao).start();
                        }
                    }
                }
                break;
            case R.id.rela_task:

                Intent its = new Intent(this,MyTaskActivity.class);
                startActivityForResult(its, 10086);
                break;
        }
    }


    String choose_wenjianjia = "";
    String wenjian_bianhao;
    String type;


    public void showpop() {
        PopWindow popWindow = new PopWindow.Builder(this)
                .setStyle(PopWindow.PopWindowStyle.PopUp)
                .setTitle("文件夹选择")

                .addItemAction(new PopItemAction("取消", PopItemAction.PopItemStyle.Cancel))
                .create();
        if (userInfo.getDATA().size() == 1) {

            if (userInfo.getDATA().get(0).get(1).contains("登录无效")) {
                popWindow.addItemAction(new PopItemAction("" + userInfo.getDATA().get(0).get(1), PopItemAction.PopItemStyle.Warning, new PopItemAction.OnClickListener() {
                    @Override
                    public void onClick() {

                    }
                }));

            } else {
                final String choose_wenjianjiaa = userInfo.getDATA().get(0).get(1);
                final String wenjian_bianhaos = userInfo.getDATA().get(0).get(0);
                popWindow.addItemAction(new PopItemAction("" + userInfo.getDATA().get(0).get(1), PopItemAction.PopItemStyle.Normal, new PopItemAction.OnClickListener() {
                    @Override
                    public void onClick() {
                        choose_wenjianjia = choose_wenjianjiaa;
                        wenjian_bianhao = wenjian_bianhaos;
                        text_mywenjian.setText(choose_wenjianjia);
                    }
                }));
            }
        } else {
            for (int i = 0; i < userInfo.getDATA().size(); i++) {
                final String choose_wenjianjiaa = userInfo.getDATA().get(i).get(1);
                final String wenjian_bianhaos = userInfo.getDATA().get(i).get(0);
                popWindow.addItemAction(new PopItemAction("" + userInfo.getDATA().get(i).get(1), PopItemAction.PopItemStyle.Normal, new PopItemAction.OnClickListener() {
                    @Override
                    public void onClick() {
                        choose_wenjianjia = choose_wenjianjiaa;
                        wenjian_bianhao = wenjian_bianhaos;
                        text_mywenjian.setText(choose_wenjianjia);
                    }
                }));
            }
        }

        popWindow.show();
    }

    long msize = 0;

    private String getFileSize(String path) {
        File f = new File(path);
        if (!f.exists()) {
            return "0 MB";
        } else {
            long size = f.length();
            msize = msize + size;

            return "";
        }
    }

    Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    if (mLoadingDialog != null) {
                        MyLog.i("mLoadingDialog"+mLoadingDialog.isShowing());
                        if (mLoadingDialog.isShowing()) {
                            MyLog.i(">>>>>>>111" + img_position);
                            mLoadingDialog.setContent("素材("+ (img_position + 1)+")上传中..." );
                        } else {
                            MyLog.i(">>>>>>>222" + img_position);
                            showLoadingDialog("素材("+ (img_position + 1)+")上传中..." );
                        }
                    } else {
                        MyLog.i(">>>>>>>33333" + img_position);
                        showLoadingDialog("素材("+ (img_position + 1)+")上传中..." );

                    }
                    break;
                case 0:
                    dismissLoadingDialog();
                    break;
                case 2:
                    showLoadingDialog("视频压缩中......");
                    break;
                case 3:


                    break;
                case 4:
                    delete();
                    break;
                case 5:

                    break;

            }
        }

        ;
    };
    WenjianjiaInfo userInfo;

    //文件夹
    public void Upload_wenjianjia(String api_key) {

        RequestParams params = new RequestParams();
        params.addBodyParameter("method", "getfolders");
        params.addBodyParameter("api_key", api_key);
        MyLog.i("api_key" + api_key);

        HttpUtils httpUtils = new HttpUtils();
        String postUrl = Contact.meizi_ip + Contact.meizi_getwenjianjia;
        MyLog.i(postUrl);
        httpUtils.send(HttpRequest.HttpMethod.POST, postUrl, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // TODO Auto-generated method stub
                rela_choose.setEnabled(true);
                MyLog.i("失败" + arg1);
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                // TODO Auto-generated method stub
                handler.sendEmptyMessage(0);
                String str = arg0.result;
                MyLog.i("成功" + str);
                rela_choose.setEnabled(true);
                userInfo = JsonUtils.getModel(str, WenjianjiaInfo.class);//这里已经解析出来了
//                showPopwindow();
                showpop();
            }
        });
    }

    public int img_position = 0;
    Runnable networkTask_tijiao = new Runnable() {
        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            Looper.prepare();
            try {

                String pictureType = selectList.get(0).getPictureType();
                int mediaType = PictureMimeType.pictureToVideo(pictureType);

                if (mediaType == 1) {

                    for (int i = 0; i < selectList.size(); i++) {
                        img_position = i;
                        handler.sendEmptyMessage(1);
                        if (is_caijian == true) {
                            Submit_tuwen_img(selectList.get(i).getCompressPath());
                        } else {
                            Submit_tuwen_img(selectList.get(i).getPath());
                        }
                    }
                } else if (mediaType == 2) {
                    if (is_vid_caijian == true) {
                        compressvid(selectList.get(0).getPath());
                    } else {
                        destPath = selectList.get(0).getPath();
                        new Thread(sub_vid).start();
                    }

                } else if (mediaType == 3) {
                    handler.sendEmptyMessage(1);
                    Submit_tuwen_img(selectList.get(0).getPath());
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Looper.loop();
        }
    };
    Runnable sub_vid = new Runnable() {
        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            Looper.prepare();
            try {
                handler.sendEmptyMessage(1);
                Submit_tuwen_img(destPath);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Looper.loop();
        }
    };
    List<UploadResultInfo> list_upload;
    String meadiaid="";

    public void Submit_tuwen_img(String url) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        String meta_description = "&meta_img_description=";
        String meta_keywords = "&meta_img_keywords=";
        try {
            if (tupe.equals("1")) {
                meta_description = "&meta_img_description=";
                meta_keywords = "&meta_img_keywords=";
            } else if (tupe.equals("2")) {
                meta_description = "&meta_vid_description=";
                meta_keywords = "&meta_vid_keywords=";
            } else if (tupe.equals("3")) {
                meta_description = "&meta_aud_description=";
                meta_keywords = "&meta_aud_keywords=";
            }
            String Posturl = Contact.meizi_upload + "?fa=c.apiupload&api_key=" + Contact.meizi_apikey + "&metadata=1" + "&meta_lang_id_r=1" + "&zip_extract=0" + "&meta_location=仙霞路350" + "&destfolderid=" + wenjian_bianhao + meta_description + URLEncoder.encode(ed_miaosu.getText().toString()) + meta_keywords + URLEncoder.encode(ed_key.getText().toString(), "UTF-8");
            MyLog.i("Posturl" + Posturl);

            HttpPost httppost = new HttpPost(Posturl);

            MultipartEntity reqEntity = new MultipartEntity();
            FileBody file = new FileBody(new File(url));
            reqEntity.addPart("filedata", file);

            // file2为请求后台的File
            // upload;属性
            httppost.setEntity(reqEntity);

            HttpResponse httpResponse;
            try {
                httpResponse = httpclient.execute(httppost);
                final int statusCode = httpResponse.getStatusLine()
                        .getStatusCode();
                //取消dialog

                if (statusCode == HttpStatus.SC_OK) {
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

                    is = new ByteArrayInputStream(string.getBytes("UTF-8"));



                    try {

                        list_upload = new ArrayList<UploadResultInfo>();
                        list_upload = XMLParserUtil.parse_upload_result(is);
                        if (list_upload != null) {
                            if (list_upload.get(0).getResponsecode().equals("0")) {

                                 if (selectList.size() == img_position + 1) {
//                                    showNotifyDialog("上传成功");
                                     handler.sendEmptyMessage(0);
                                     meadiaid=meadiaid+list_upload.get(0).getAssetid();
                                    LoadSource(task_id,meadiaid,token);
                                } else {
                                    MyLog.i("上传成功" + img_position + 1);
                                    selectList.get(img_position).setUpload(true);
                                     meadiaid=meadiaid+list_upload.get(0).getAssetid()+",";
                                }
                            } else {
                                if (selectList.size() == img_position + 1) {
                                    failed();
                                 } else {
                                    //不是最后一个，上传还要继续

                                }

                            }
                        }
                    } catch (Exception e1) {
                        MyLog.i("失败1111");
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                        failed();
                    }

                } else {
                    failed();
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                MyLog.i("失败222");
                failed();

            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            failed();
            e.printStackTrace();
        }
    }
    public void LoadSource(String taskid,String mediaid,String token) {

        RequestParams params = new RequestParams();
        params.addBodyParameter("taskid",taskid);
        MyLog.i("mediaid"+mediaid);
        params.addBodyParameter("mediaids",mediaid);
        params.addBodyParameter("token",token);

        HttpUtils httpUtils = new HttpUtils();
        String postUlr = Contact.rong_ip + Contact.add_task;
        MyLog.i("postUlr" + postUlr);
        httpUtils.send(HttpRequest.HttpMethod.POST, postUlr, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // TODO Auto-generated method stub
                showNotifyDialog("绑定任务失败");

            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                // TODO Auto-generated method stub
                handler.sendEmptyMessage(5);

                MyLog.i("chengong：" + arg0.result);

                MsgInfo mUserInfo= JsonUtils.getModel(arg0.result, MsgInfo.class);

                    showNotifyDialog(""+mUserInfo.getMsg());

            }
        });
    }

    public void failed() {
        if (selectList.size() == img_position + 1) {
            handler.sendEmptyMessage(0);
            showNotifyDialog("上传失败");
            selectList.get(img_position).setUpload(false);
        }
    }

    //键盘的问题
    private void controlKeyboardLayout(final ScrollView root, final Activity context) {
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {


                Rect rect = new Rect();
                root.getWindowVisibleDisplayFrame(rect);
                int rootInvisibleHeight = root.getRootView().getHeight() - rect.bottom;
                //若不可视区域高度大于100，则键盘显示
                if (rootInvisibleHeight > 100) {
                    int[] location = new int[2];
                    View focus = context.getCurrentFocus();
                    if (focus != null) {
                        focus.getLocationInWindow(location);
                        int scrollHeight = (location[1] + focus.getHeight()) - rect.bottom;
                        if (rect.bottom < location[1] + focus.getHeight()) {
                            root.scrollTo(0, scrollHeight);
                        }
                    }
                } else {
                    //键盘隐藏
                    root.scrollTo(0, 0);
                }
            }
        });
    }

    private int x = 0, y = 0;
    private String outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    String destPath;

    public void compressvid(String inputDir) {

        destPath = outputDir + File.separator + "VID_" + new SimpleDateFormat("yyyyMMdd_HHmmss", getLocale()).format(new Date()) + ".mp4";
/*
        VideoCompress.compressVideoLow(inputDir, destPath, new VideoCompress.CompressListener() {
            @Override
            public void onStart() {
                handler.sendEmptyMessage(2);
                MyLog.i("压缩开始");
            }

            @Override
            public void onSuccess() {
                MyLog.i("压缩完成" + getFileSize(destPath));
                handler.sendEmptyMessage(0);
//                openFile(new File(destPath));
                new Thread(sub_vid).start();
            }

            @Override
            public void onFail() {
                MyLog.i("压缩失败");
                handler.sendEmptyMessage(0);

            }

            @Override
            public void onProgress(float percent) {

                MyLog.i("压缩中" + String.valueOf(percent) + "%");
            }
        });*/
    }

    private Locale getLocale() {
        Configuration config = getResources().getConfiguration();
        Locale sysLocale = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sysLocale = getSystemLocale(config);
        } else {
            sysLocale = getSystemLocaleLegacy(config);
        }
        return sysLocale;
    }

    @SuppressWarnings("deprecation")
    public static Locale getSystemLocaleLegacy(Configuration config) {
        return config.locale;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static Locale getSystemLocale(Configuration config) {
        return config.getLocales().get(0);
    }


    /**
     * 自定义压缩存储地址
     *
     * @return
     */
    private String getPath() {
        String path = Environment.getExternalStorageDirectory() + "/Luban/image/";
        File file = new File(path);
        if (file.mkdirs()) {
            return path;
        }
        return path;
    }

    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;

    public void onrejestreceiv() {
        //动态接受网络变化的广播接收器
        intentFilter = new IntentFilter();
        intentFilter.addAction("update");

        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    //自定义接受网络变化的广播接收器
    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MyLog.i("收到了吗");
            ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            LocalMedia smLocalMedia = new LocalMedia();
            if (intent.getBooleanExtra("lvjing", false) == true) {
                com.shadt.news.cameresdk.model.LocalMedia localMedia = new com.shadt.news.cameresdk.model.LocalMedia();
                localMedia = intent.getParcelableExtra("content");
                smLocalMedia.setPath(localMedia.getPath());
                smLocalMedia.setCompressPath(localMedia.getPath());
                smLocalMedia.setPictureType(localMedia.getPictureType());
                smLocalMedia.setMimeType(localMedia.getMimeType());
                smLocalMedia.setPosition(localMedia.getPosition());
                smLocalMedia.setNum(localMedia.getNum());
                selectList.add(smLocalMedia);
            } else {
                Bundle bundle = intent.getBundleExtra("content");
//                smLocalMedia = intent.getParcelableExtra("content");
                selectList = new ArrayList<>();
                selectList = (List<LocalMedia>) bundle.getSerializable(PictureConfig.EXTRA_SELECT_LIST);
//                selectList.add(smLocalMedia);

            }

            for (LocalMedia media : selectList) {

                int mediaType = PictureMimeType.pictureToVideo(media.getPictureType());

                if (mediaType == 1) {
                    text_leixin.setText("文件类型:图片");
                    getFileSize(media.getPath());
                  /*  if (is_caijian == true) {
                        text_size.setText("文件大小:" + getFileSize(media.getCompressPath()));
                    } else {
                        text_size.setText("文件大小:" + getFileSize(media.getPath()));
                    }*/
                } else if (mediaType == 2) {
                    text_leixin.setText("文件类型:视频");
                    text_size.setText("文件大小:" + getFileSize(media.getPath()));
                } else if (mediaType == 3) {
                    text_leixin.setText("文件类型:音频");
                    text_size.setText("文件大小:" + getFileSize(media.getPath()));
                }
            }
            text_size.setText("文件大小:" + new DecimalFormat("0.00").format((double) (msize / 1024f) / 1024f) + "MB");
            adapter.setList(selectList);
            adapter.notifyDataSetChanged();

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
        }
        //取消动态网络变化广播接收器的注册
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        //触摸的是EditText并且当前EditText可以滚动则将事件交给EditText处理；否则将事件交由其父类处理
        if ((view.getId() == R.id.ed_miaosu && canVerticalScroll(ed_miaosu))) {
            view.getParent().requestDisallowInterceptTouchEvent(true);
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                view.getParent().requestDisallowInterceptTouchEvent(false);
            }
        }
        return false;
    }

    /**
     * EditText竖直方向是否可以滚动
     * @param editText 需要判断的EditText
     * @return true：可以滚动  false：不可以滚动
     */
    private boolean canVerticalScroll(EditText editText) {
        //滚动的距离
        int scrollY = editText.getScrollY();
        //控件内容的总高度
        int scrollRange = editText.getLayout().getHeight();
        //控件实际显示的高度
        int scrollExtent = editText.getHeight() - editText.getCompoundPaddingTop() -editText.getCompoundPaddingBottom();
        //控件内容总高度与实际显示高度的差值
        int scrollDifference = scrollRange - scrollExtent;

        if(scrollDifference == 0) {
            return false;
        }

        return (scrollY > 0) || (scrollY < scrollDifference - 1);
    }


}
