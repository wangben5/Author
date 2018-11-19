package com.shadt.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.shadt.caibian_news.R;
import com.shadt.ui.interfaces.AndroidJSInterface;
import com.shadt.ui.interfaces.PermissionListener;
import com.shadt.ui.utils.FileUtils;
import com.shadt.ui.widget.LoadingView;
import com.shadt.util.MyLog;
import com.shadt.util.WebUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

/*import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebBackForwardList;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;*/

@SuppressWarnings("unused")
@SuppressLint({"NewApi", "SetJavaScriptEnabled"})
public class WebActivity extends BaseWebActivity {

    public static String result_erweima;
    public static String url = null;
    public static String share_img = "", share_content = "", share_title = "", share_id = "", type = "";
    public static String share_ip = null;
    public static boolean is_reload = false;
    public static String redirect_uri = null; // 支付后跳转url
    public static String method_name = null; // 登录后 走的方法名
    private static String userPhone = "";
    private String api_key = null;
    private final int FILECHOOSER_RESULTCODE = 1;
    private final int CAMERACHOOSER_RESULTCODE = 2;
    private LinearLayout container;
    private PopupWindow pop;
    private ImageView mRefrashImage;
    private FrameLayout fragment_web;
    private RelativeLayout layout_yindao;
    private WebView webview3;
    private TextView tx_title;
    private ImageView back;
    //	private TextView share_btn;
    private WebChromeClient wvcc;
    private AudioManager mAudioManager;
    private RelativeLayout rel_default;
    private int save = 1001;
    private ImageView close;

    private WebUtils is_net = new WebUtils();
    private Context mContext = WebActivity.this;

    private StringBuffer sb;
    private String tar2;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private FrameLayout mFrameLayout;
    private LinearLayout top_back;
    private String userID = ""; //此处是查询萌宝详情专用用户ID
    private SharedPreferences preferences;
    private String my_id;
    private String isRefresh = "0";  // 为0 时  正常下拉刷新页面    为1时 不下拉刷新功能
    private boolean isHasTaskFlag = false; //判断url是否包含 分享得红包标识 TaskFlag
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private WebSettings webSettings;
    private int ProgressNum = 30;
    private String titlecolor = "ff0000"; //默认红色
    private int titlecolor_int = 0xffff0000; //默认红色
    private View daohang_bar;
    private String mRedbagIP = ""; //
    private BroadcastReceiver broadcastReceiver;

    LoadingView loadingview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        setContentView(R.layout.activity_web_x5);


        // 网页中的视频，上屏幕的时候，可能出现闪烁的情况，需要如下设置
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        //避免输入法界面弹出后遮挡输入光标的问题
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        initData();

        initFindView();

        initWebview();

        initonclick();


    }

    /**
     * 改变状态栏颜色
     */
    private void setTitle_bg_color(String url, boolean b, int setColor) {
        String myTastUrl = url.replace("#", "");
        try {
            titlecolor = Uri.parse(myTastUrl).getQueryParameter("titlecolor");
        } catch (Exception e) {
            // TODO: handle exception
            titlecolor = "FF0000";
        }
        if (TextUtils.isEmpty(titlecolor)) {
            titlecolor = "FF0000";
        }

        try {
            titlecolor_int = Color.parseColor("#" + titlecolor);
        } catch (Exception e) {
            // TODO: handle exception
            titlecolor_int = 0xffff0000;
        }

        if (b) {
            top_back.setBackgroundColor(setColor);
            fragment_web.setBackgroundColor(setColor);
        } else {
            top_back.setBackgroundColor(titlecolor_int);
            fragment_web.setBackgroundColor(titlecolor_int);
        }

    }


    /**
     * 初始化数据
     */
    private void initData() {
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        Intent it = getIntent();
        url = it.getStringExtra("url");



        if (url.contains("&amp;")) {
            url = url.replace("&amp;", "&");
        }
        if (it.getStringExtra("userID") != null) {
            userID = it.getStringExtra("userID");
        }
        if (url.contains("titlecolor")) {
            setTitle_bg_color(url, false, 0xffff0000);
        }
    }

    //文件存储位置
    private String docPath = "/mnt/sdcard/doc/";
    //文件名称
    private String docName = "test.doc";
    //html文件存储位置
    private String savePath = "/mnt/sdcard/doc/";

    /**
     * 初始化控件
     */
    private void initFindView() {
        loadingview = (LoadingView) findViewById(R.id.loadingview);
        top_back = (LinearLayout) findViewById(R.id.top_back);
        fragment_web = (FrameLayout) findViewById(R.id.fragment_web);
        my_id = preferences.getString("id", "");
        mRedbagIP = preferences.getString("redbagIP", "");


        /**刷新默认图片**/
        mRefrashImage = (ImageView) findViewById(R.id.refrash_image);
        mRefrashImage.setVisibility(View.GONE);
        mRefrashImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // 重新刷新页面
                if (is_net.isNetworkConnected(mContext) == true) {

                    if (webview3 != null) {
                        if (webview3.getUrl() != null) {
                            webview3.loadUrl(webview3.getUrl());
                        } else {
                            webview3.loadUrl(url);
                        }
                    }
                } else {
                    Toast.makeText(mContext,
                            getResources().getString(R.string.no_net),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


        mFrameLayout = (FrameLayout) findViewById(R.id.mFrameLayout);

        tx_title = (TextView) findViewById(R.id.title);
        tx_title.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (webview3 != null) {
                    webview3.scrollTo(0, 0);
                }
            }
        });
        layout_yindao = (RelativeLayout) findViewById(R.id.layout_share_yindao);
        layout_yindao.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

            }
        });
        View iKnow = findViewById(R.id.iknow);
        iKnow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                layout_yindao.setVisibility(View.GONE);
            }
        });

        back = (ImageView) findViewById(R.id.back);

//		share_btn = (TextView) findViewById(R.id.share_btn);
        close = (ImageView) findViewById(R.id.close);
        close.setVisibility(View.VISIBLE);
//		share_btn.setVisibility(View.VISIBLE);
        rel_default = (RelativeLayout) findViewById(R.id.Rel_fault);

    }

    /**
     * 初始化webview
     */
    private void initWebview() {
        // TODO Auto-generated method stub

        webview3 = (WebView) findViewById(R.id.web);
        webview3.requestFocus(View.FOCUS_DOWN);

        //以下接口禁止(直接或反射)调用，避免视频画面无法显示：
        //webView.setLayerType();

        webview3.setDrawingCacheEnabled(true);
        webSettings = webview3.getSettings();

        // 修改ua使得web端正确判断(加标识+++++++++++++++++++++++++++++++++++++++++++++++++++++)
//        String ua = webSettings.getUserAgentString();
//        webSettings.setUserAgentString(ua + "这里是增加的标识");

        // 网页内容的宽度是否可大于WebView控件的宽度
        webSettings.setLoadWithOverviewMode(false);
        // 保存表单数据
        webSettings.setSaveFormData(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); //关闭webview中缓存
        // 是否应该支持使用其屏幕缩放控件和手势缩放

        webview3.clearCache(true);

        webview3.clearHistory();
        // 缓存白屏
        String appCachePath = getApplicationContext().getCacheDir()
                .getAbsolutePath() + "/webcache";
// 设置 Application Caches 缓存目录
        webSettings.setAppCachePath(appCachePath);
        webSettings.setDatabasePath(appCachePath);
        // 应用可以有缓存 true false 没有缓存
        webSettings.setAppCacheEnabled(false);

        //隐藏原生的缩放控件
        webSettings.setDisplayZoomControls(false);

        webview3.requestFocus(); //此句可使html表单可以接收键盘输入
        webview3.setFocusable(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSavePassword(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        // 启动应用缓存
        webSettings.setAppCacheEnabled(false);
        // 设置缓存模式
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 设置此属性，可任意比例缩放。
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放

        AndroidJSInterface ajsi = new AndroidJSInterface(WebActivity.this);
        webview3.addJavascriptInterface(ajsi, ajsi.getInterface());
        //  页面加载好以后，再放开图片
        //mSettings.setBlockNetworkImage(false);
        // 使用localStorage则必须打开
        webSettings.setDomStorageEnabled(true);
        // 排版适应屏幕
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // WebView是否支持多个窗口。
        webSettings.setSupportMultipleWindows(false);
        webSettings.setUseWideViewPort(true); // 关键点
        webSettings.setAllowFileAccess(true); // 允许访问文件


        //将图片调整到适合webview的大小
        webSettings.setUseWideViewPort(true);
        // webview从5.0开始默认不允许混合模式,https中不能加载http资源,需要设置开启。
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
        // 缩放至屏幕的大小
        webSettings.setLoadWithOverviewMode(true);
        //其他细节操作
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        webview3.getSettings().setUseWideViewPort(true); //自适应屏幕
        webview3.setDrawingCacheEnabled(true);

        webview3.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String urls, Bitmap favicon) {
                super.onPageStarted(view, urls, favicon);
                loadingview.start();
                rel_default.setVisibility(View.GONE);
                MyLog.i("开始加载网页：" + urls);
                loadingview.setVisibility(View.VISIBLE);

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String urls) {

                view.loadUrl(url);
                //获取cookies
                CookieManager cm = CookieManager.getInstance();
                String cookies = cm.getCookie(url);
                MyLog.i("cook"+cookies);
                return true;

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                writeData();
                loadingview.stop();
                loadingview.setVisibility(View.GONE);
                if (webview3 != null) {
                    webview3.setVisibility(View.VISIBLE);
                }

                MyLog.i("网页加载完成。URL：" + url);
                MyLog.i("网页加载完成。标题：" + view.getTitle());


                tx_title.setText("" + view.getTitle());
                share_title = "" + view.getTitle();


                if (url.contains("guajiang")) {

                    mRefrashImage.setVisibility(View.GONE);

                } else {

                    mRefrashImage.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                MyLog.i("jiazshibai");


                if (webview3 != null) {
                    loadingview.setVisibility(View.GONE);
                    webview3.setVisibility(View.GONE);
                    rel_default.setVisibility(View.VISIBLE);
                }


            }
        });

//        loadUrls();
      /*  webview3.evaluateJavascript(script, new ValueCallback<String>() {

            @Override
            public void onReceiveValue(String value) {
                Log.d(TAG, "onReceiveValue value=" + value);

                if(value!=null){
                    flag_get_deviceid=true;
                }
            }});*/

        webview3.setWebChromeClient(new WebChromeClient() {


            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                tx_title.setText(title);
                MyLog.i("获取网页标题：" + title);
                WebActivity.share_title = title;
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);

            }


            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {

                MyLog.i("For Android 3.0+，acceptType=" + acceptType);
                WebActivity.this.mUploadMessage = uploadMsg;


                if (acceptType != null && acceptType.contains("camera")) {
                    isChoosed = true;
                    compressPath = Environment.getExternalStorageDirectory().getPath() + "/A_QCZL_Download/camera_img";
                    new File(compressPath).mkdirs();
                    compressPath = compressPath + File.separator + (System.currentTimeMillis() + "_s.jpg");
                    openCarcme();
                } else {
                    selectImage();
                }
//
            }

            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                MyLog.i("For Android <3.0，acceptType=");
                WebActivity.this.mUploadMessage = uploadMsg;
                openFileChooser(uploadMsg, "");
            }

            // For Android  > 4.1.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                MyLog.i("For Android 4.1+，acceptType=" + acceptType + ",capture=" + capture);
                WebActivity.this.mUploadMessage = uploadMsg;
                openFileChooser(uploadMsg, acceptType);
            }

            // For Android  >= 5.0
            public boolean onShowFileChooser(WebView webView,
                                             final ValueCallback<Uri[]> filePathCallback,
                                             final WebChromeClient.FileChooserParams fileChooserParams) {

                MyLog.i("openFileChooser >= 5.0:" + filePathCallback.toString());
                MyLog.i("fileChooserParams:" + fileChooserParams.getAcceptTypes().length);
                MyLog.i("fileChooserParams:" + fileChooserParams.getAcceptTypes()[0]);


                //拍照权限申请

                requestRunTimePermission(new String[]{
                        Manifest.permission.CAMERA, //拍照权限
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,  //允许程序写入外部储存 ，如SD卡上写文件 写入权限会自动获取读取权限
                        Manifest.permission.RECORD_AUDIO

                }, new PermissionListener() {
                    @Override
                    public void onGranted() {

                        WebActivity.this.mUploadCallbackAboveL = filePathCallback;

                        if (fileChooserParams.getAcceptTypes().length > 0
                                && fileChooserParams.getAcceptTypes()[0].contains("camera")) {

                            isChoosed = true;

                            compressPath = Environment.getExternalStorageDirectory().getPath() + "/A_QCZL_Download/camera_img";
                            new File(compressPath).mkdirs();
                            compressPath = compressPath + File.separator + (System.currentTimeMillis() + "_s.jpg");

                            openCarcme();

                        } else {
                            selectImage();
                        }


                    }

                    @Override
                    public void onGranted(List<String> grantedPermission) {

                    }

                    @Override
                    public void onDenied(List<String> deniedPermission) {

                    }
                });

                return true;
            }
        });


        int ziti_size = preferences.getInt("ziti_size", 50);
        if (ziti_size == 0) {
            webview3.getSettings().setTextSize(WebSettings.TextSize.SMALLER);
        } else if (ziti_size == 50) {
            webview3.getSettings().setTextSize(WebSettings.TextSize.NORMAL);
        } else {
            webview3.getSettings().setTextSize(WebSettings.TextSize.LARGER);
        }

        // html里的内容，test1就是本地的html,在项目assets文件夹下
//		url = url+"&time="+System.currentTimeMillis();
//        url="http://192.168.2.74/#/app";
//        url="https://hbzq.chinashadt.com/videocall/index.html#/";
        webview3.loadUrl(url);
        MyLog.i("准备加载网页：" + url);

    }
    public void writeData(){
        String key = "user_Data";
        String val =  preferences.getString("rong_userinfo", "");
        String key2 ="user_Data";
        String val2 = preferences.getString("rong_userinfo", "");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            webview3.evaluateJavascript("window.localStorage.setItem('"+ key +"','"+ val +"');", null);
            webview3.evaluateJavascript("window.localStorage.setItem('"+ key2 +"','"+ val2 +"');", null);
        } else {
            webview3.loadUrl("javascript:localStorage.setItem('"+ key +"','"+ val +"');");
            webview3.loadUrl("javascript:localStorage.setItem('"+ key2 +"','"+ val2 +"');");
        }
    }

    boolean flag_get_deviceid = true;


    private void loadUrl() {
        String key = "";
        String androidID = "";
        try {
            androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            MyLog.d("androidID:" + androidID);

        } catch (Exception e) {
            MyLog.e("");
        } finally {
            String script = String.format("javascript:getUserInfo('" + androidID + "')");

            webview3.evaluateJavascript(script, new ValueCallback<String>() {

                @Override
                public void onReceiveValue(String value) {
                    MyLog.d("onReceiveValue value=" + value);

                    if (value != null) {
                        flag_get_deviceid = true;
                    }
                }
            });
        }
    }

    /**
     * 检查SD卡是否存在
     *
     * @return
     */
    public final boolean checkSDcard() {
        boolean flag = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (!flag) {
            Toast.makeText(this, "请插入手机存储卡再使用本功能", Toast.LENGTH_SHORT).show();
        }
        return flag;
    }


    boolean isChoosed = false; // 判断是否选择了相机还是想相册  没选的话点了空白处就false

    protected final void selectImage() {
        if (!checkSDcard())
            return;


        String[] selectPicTypeStr = {"相机", "相册"};
        new AlertDialog.Builder(this)
//		.setCancelable(false)
                .setOnDismissListener(new OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // TODO Auto-generated method stub

                        if (isChoosed) {
                            MyLog.i("做了选择");
                            isChoosed = false;
                        } else {
                            MyLog.i("没做选择");
                            if (mUploadMessage != null) {
                                mUploadMessage.onReceiveValue(null);
                                mUploadMessage = null;
                            } else if (mUploadCallbackAboveL != null) {
                                mUploadCallbackAboveL.onReceiveValue(null);
                                mUploadCallbackAboveL = null;
                            }
                        }


                        return;
                    }
                }).setItems(selectPicTypeStr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    // 相机拍摄
                    case 0:
                        isChoosed = true;
                        openCarcme();
                        break;
                    // 手机相册
                    case 1:
                        isChoosed = true;
//						chosePic();

                        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                        i.addCategory(Intent.CATEGORY_OPENABLE);
//						i.setType("image/*");
                        i.setType("*/*");
                        WebActivity.this.startActivityForResult(
                                Intent.createChooser(i, "File Browser"),
                                FILECHOOSER_RESULTCODE);

                        break;
                    default:
                        break;
                }
                compressPath = Environment.getExternalStorageDirectory().getPath() + "/A_QCZL_Download/camera_img";
                new File(compressPath).mkdirs();
                compressPath = compressPath + File.separator + (System.currentTimeMillis() + "_s.jpg");
            }
        }).show();


    }

    String compressPath = "";  //压缩后的图片路径
    String imagePaths;
    Uri cameraUri;   // 原图片路径

    /**
     * 打开照相机
     */
    private void openCarcme() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        imagePaths = Environment.getExternalStorageDirectory().getPath()
                + "/A_QCZL_Download/camera_img/"
                + (System.currentTimeMillis() + ".jpg");
        // 必须确保文件夹路径存在，否则拍照后无法完成回调
        File vFile = new File(imagePaths);
        if (!vFile.exists()) {
            File vDirPath = vFile.getParentFile();
            vDirPath.mkdirs();
        } else {
            if (vFile.exists()) {
                vFile.delete();
            }
        }
        cameraUri = Uri.fromFile(vFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(intent, CAMERACHOOSER_RESULTCODE);
    }

    /**
     * 拍照结束后
     */
    private Uri afterOpenCamera() {
        File f = new File(imagePaths);
        addImageGallery(f);

        if (!f.exists()) {
            MyLog.i("拍照文件不存在");
            return null;
        }


        File vFile = FileUtils.compressFile(f.getPath(), compressPath);

        return Uri.fromFile(vFile);


    }

    /**
     * 解决拍照后在相册中找不到的问题
     */
    private void addImageGallery(File file) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    /**
     * 本地相册选择图片
     */
    private void chosePic() {
//		FileUtils.delFile(compressPath);
        Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); // "android.intent.action.GET_CONTENT"
        String IMAGE_UNSPECIFIED = "image/*";
        Intent wrapperIntent = Intent.createChooser(innerIntent, null);
        startActivityForResult(wrapperIntent, FILECHOOSER_RESULTCODE);
    }


    /**
     * 选择照片后结束
     *
     * @param uri
     */
    @SuppressWarnings("deprecation")
    private Uri afterChosePic(Uri uri) {

        // 获取图片的路径：
        String[] proj = {MediaStore.Images.Media.DATA};
        // 好像是android多媒体数据库的封装接口，具体的看Android文档
        Cursor cursor = managedQuery(uri, proj, null, null, null);
        if (cursor == null) {
            Toast.makeText(this, "上传的图片仅支持png或jpg格式", Toast.LENGTH_SHORT).show();
            return null;
        }
        // 按我个人理解 这个是获得用户选择的图片的索引值
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        // 将光标移至开头 ，这个很重要，不小心很容易引起越界
        cursor.moveToFirst();
        // 最后根据索引值获取图片路径
        String path = cursor.getString(column_index);
        if (path != null && (path.endsWith(".png") || path.endsWith(".PNG") || path.endsWith(".jpg") || path.endsWith(".JPG"))) {
            File newFile = FileUtils.compressFile(path, compressPath);
            return Uri.fromFile(newFile);
        } else {
            Toast.makeText(this, "上传的图片仅支持png或jpg格式", Toast.LENGTH_SHORT).show();
        }
        return null;
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        //
        if (is_reload == true) {
            is_reload = false;
            webview3.reload();
        } else {

        }
    }

    // 获取网页具体内容
    String get_score = "";

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub


        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (layout_yindao.getVisibility() == View.VISIBLE) {
                layout_yindao.setVisibility(View.GONE);
                return true;
            }


            share_img = "";
            share_content = "";
            share_id = "";
            share_title = "";
            if (webview3 != null) {


                if (webview3.canGoBack()) {

                    // webview3.goBack(); // goBack()表示返回WebView的上一页面
                    goBackInWebView();

                } else {
                    finish();
                }
            } else {
                finish();
            }


        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            // 降低音量，调出系统音量控制
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_LOWER,
                    AudioManager.FX_FOCUS_NAVIGATION_UP);

        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_RAISE,
                    AudioManager.FX_FOCUS_NAVIGATION_UP);
        }
        return true;
    }

    public void goBackInWebView() {
        WebBackForwardList history = webview3.copyBackForwardList();
        int index = -1;
        String url = null;

        while (webview3.canGoBackOrForward(index)) {
            if (!history.getItemAtIndex(history.getCurrentIndex() + index)
                    .getUrl().equals("about:blank")) {
                webview3.goBackOrForward(index);
                url = history.getItemAtIndex(-index).getUrl();
                MyLog.i("first non empty" + url);
                break;
            }
            index--;

        }
        // no history found that is not empty,
        if (url == null) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //记录最后一条新闻


        if (webview3 != null) {
            webview3.setVisibility(View.GONE);
        }


        // Destroy the AdView.
        if (webview3 != null) {

            webview3.setWebChromeClient(null);
            webview3.setWebViewClient(null);
            webview3.getSettings().setJavaScriptEnabled(false);
            webview3.clearCache(true);


            webview3.stopLoading();
            webview3.removeAllViews();
            webview3.destroy();
            webview3 = null;


//		    	System.exit(0);  //开单独进程的时候关闭用     目前耦合性太多  无法开启新进程   待优化

        }


    }

    ////删除
    public void setConfigCallback(WindowManager windowManager) {
        try {
            Field field = WebView.class.getDeclaredField("mWebViewCore");
            field = field.getType().getDeclaredField("mBrowserFrame");
            field = field.getType().getDeclaredField("sConfigCallback");
            field.setAccessible(true);
            Object configCallback = field.get(null);

            if (null == configCallback) {
                return;
            }

            field = field.getType().getDeclaredField("mWindowManager");
            field.setAccessible(true);
            field.set(configCallback, windowManager);
        } catch (Exception e) {

        }

    }


    public void initonclick() {
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                share_img = "";
                share_content = "";
                share_id = "";
                share_title = "";
                if (webview3 != null) {
                    if (webview3.canGoBack()) {

                        goBackInWebView(); // goBack()表示返回WebView的上一页面

                        webview3.requestFocus(View.FOCUS_DOWN);
                    } else {
                        finish();
                    }
                } else {
                    finish();
                }


            }
        });
        close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                share_img = "";
                share_content = "";
                share_id = "";
                share_title = "";
                finish();
            }
        });


        rel_default.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (webview3 != null) {
                    webview3.loadUrl(url);
                }

            }
        });

    }


    // webview调用手机相册

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILECHOOSER_RESULTCODE) {  //老的
            if (null == mUploadMessage && null == mUploadCallbackAboveL)
                return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data
                    .getData();
            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        } else if (requestCode == CAMERACHOOSER_RESULTCODE) {

            if (mUploadMessage == null && mUploadCallbackAboveL == null) {
                return;
            }

            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();

            result = afterOpenCamera();

            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (mUploadMessage != null) {


                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }

        } else if (resultCode == 1002) {

            String shareType = data.getStringExtra("shareType");
            String shareURL = data.getStringExtra("shareURL");


            String userID = preferences.getString("id", "");

            if (TextUtils.isEmpty(userID)) {
//				finish();
            } else {


                /**获取积分*/
//				request_score(userID, "3");


            }


        } else if (resultCode == 1003) {

            if (data.getStringExtra("isToShare").equals("0")) {
//				Intent it = new Intent(WebActivity.this,
//						Sharedialog_Activity.class);
//				it.putExtra("save", save);
//				it.putExtra("is_video", 0);
//				it.putExtra("userID", userID);//萌宝详情 用到
//
//				it.putExtra("listTitle", listTitle);
//				it.putExtra("listImages", listImages);
//				it.putExtra("listUrl", listUrl);
//
////				Log.i("OTH", "传递到分享ID："+userID);
////				startActivity(it);
//				startActivityForResult(it, 1002);


            }

        }

    }

    private void onActivityResultAboveL(int requestCode, int resultCode,
                                        Intent data) {

        MyLog.i("data:" + data);

        if (requestCode == CAMERACHOOSER_RESULTCODE) {

            if (requestCode != CAMERACHOOSER_RESULTCODE
                    || mUploadCallbackAboveL == null) {
                return;
            }

            Uri[] results = null;
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {

                    Uri result = afterOpenCamera();

                    results = new Uri[]{result};
                } else {
                    String dataString = data.getDataString();
                    ClipData clipData = data.getClipData();

                    if (clipData != null) {
                        results = new Uri[clipData.getItemCount()];
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            results[i] = item.getUri();
                        }
                    }
                    if (dataString != null)
                        results = new Uri[]{Uri.parse(dataString)};
                }
            }
            mUploadCallbackAboveL.onReceiveValue(results);
            mUploadCallbackAboveL = null;

        } else {

            if (requestCode != FILECHOOSER_RESULTCODE
                    || mUploadCallbackAboveL == null) {
                return;
            }
            Uri[] results = null;
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {

                } else {
                    String dataString = data.getDataString();
                    ClipData clipData = data.getClipData();

                    if (clipData != null) {
                        results = new Uri[clipData.getItemCount()];
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            results[i] = item.getUri();
                        }
                    }

                    if (dataString != null)
                        results = new Uri[]{Uri.parse(dataString)};
                }
            }
            mUploadCallbackAboveL.onReceiveValue(results);
            mUploadCallbackAboveL = null;

        }


        return;
    }

    /**
     * 播放视频全屏
     */
    @Override
    public void onConfigurationChanged(Configuration config) {

        super.onConfigurationChanged(config);

        switch (config.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                break;
        }

    }



    /* *//**
     * word文档转成html格式
     * *//*
    public void convert2Html(String fileName, String outPutFile) {
        HWPFDocument wordDocument = null;
        try {
            wordDocument = new HWPFDocument(new FileInputStream(fileName));
            WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
                    DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
            //设置图片路径
            wordToHtmlConverter.setPicturesManager(new PicturesManager() {
                public String savePicture(byte[] content,
                                          PictureType pictureType, String suggestedName,
                                          float widthInches, float heightInches) {
                    String name = docName.substring(0, docName.indexOf("."));
                    return name + "/" + suggestedName;
                }
            });
            //保存图片
            List<Picture> pics=wordDocument.getPicturesTable().getAllPictures();
            if(pics!=null){
                for(int i=0;i<pics.size();i++){
                    Picture pic = (Picture)pics.get(i);
                    System.out.println( pic.suggestFullFileName());
                    try {
                        String name = docName.substring(0,docName.indexOf("."));
                        String file = savePath+ name + "/"
                                + pic.suggestFullFileName();
                        FileUtils.makeDirs(file);
                        pic.writeImageContent(new FileOutputStream(file));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            wordToHtmlConverter.processDocument(wordDocument);
            Document htmlDocument = wordToHtmlConverter.getDocument();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DOMSource domSource = new DOMSource(htmlDocument);
            StreamResult streamResult = new StreamResult(out);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer serializer = tf.newTransformer();
            serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty(OutputKeys.METHOD, "html");
            serializer.transform(domSource, streamResult);
            out.close();
            //保存html文件
            writeFile(new String(out.toByteArray()), outPutFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    *//**
     * 将html文件保存到sd卡
     * *//*
    public void writeFile(String content, String path) {
        FileOutputStream fos = null;
        BufferedWriter bw = null;
        try {
            File file = new File(path);
            if(!file.exists()){
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            bw = new BufferedWriter(new OutputStreamWriter(fos,"utf-8"));
            bw.write(content);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fos != null)
                    fos.close();
            } catch (IOException ie) {
            }
        }
    }*/

}
