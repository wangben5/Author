package com.aodianyun.adandroidbridge;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.widget.Toast;

import com.aodianyun.adandroidbridge.jsApi.PublishApi;
import com.aodianyun.adandroidbridge.util.GetPathFromUri4kitkat;
import com.aodianyun.adandroidbridge.util.PickPhotoUtil;
import com.shadt.news.danbin.R;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


import cn.nodemedia.NodeCameraView;
import cn.nodemedia.NodePublisher;
import cn.nodemedia.NodePublisherDelegate;
import websdk.CompletionHandler;
import websdk.DWebView;

public class MainActivity extends AppCompatActivity implements NodePublisherDelegate, EasyPermissions.PermissionCallbacks {

    private static final String TAG = "MainActivity";
    CompletionHandler<Object> mHandler;

    private static final int FILE_SELECT_CODE = 0;
    private ValueCallback<Uri> mUploadMessage;
    public static ValueCallback<Uri[]> mFilePathCallback;

    public static String mCM;
    public static ValueCallback<Uri> mUM;
    public static ValueCallback<Uri[]> mUMA;
    public  static int FCR=1;

    private MyWebChromeClient mWebChromeClient;

    NodePublisher np;
    NodeCameraView npv;

    private boolean isPreview = false;

    private boolean isStarting = false;
    private boolean isMicOn = true;
    private boolean isCamOn = true;
    private boolean isFlsOn = true;

    DWebView dwebView;
    private static final int RC_CAMERA_PERM = 123;
    private UIMyHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danbin);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        handler =  new  UIMyHandler(this);
        dwebView= (DWebView) findViewById(R.id.webview);
        // set debug mode
        dwebView.setWebContentsDebuggingEnabled(true);

        /**
         *  Webview在安卓5.0之前默认允许其加载混合网络协议内容
         *  在安卓5.0之后，默认不允许加载http与https混合内容，需要设置webview允许其加载混合网络协议内容
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dwebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        }
        mWebChromeClient=new MyWebChromeClient(new PickPhotoUtil(this));
        dwebView.setWebChromeClient(mWebChromeClient);
        dwebView.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        PublishApi publishApi = new PublishApi();
        publishApi.setPublishlistener(this,new PublishApi.PublishJSListener() {
            @Override
            public void startPreview(final boolean bPreview) {
                android.util.Log.d(TAG, "startPreview: "+"ViewRootImpl "+android.os.Process.myPid()+" Thread: "+android.os.Process.myTid()+" name "+Thread.currentThread().getName());
                handler.sendEmptyMessage(4100);
            }

            @Override
            public JSONObject getLocation() {

                return null;
            }

            @Override
            public void startPublish(String publiUrl) {
                np.setOutputUrl(publiUrl);
                Log.v("shadt","url+"+publiUrl);
                /**
                 * @brief rtmpdump 风格的connect参数
                 * Append arbitrary AMF data to the Connect message. The type must be B for Boolean, N for number, S for string, O for object, or Z for null.
                 * For Booleans the data must be either 0 or 1 for FALSE or TRUE, respectively. Likewise for Objects the data must be 0 or 1 to end or begin an object, respectively.
                 * Data items in subobjects may be named, by prefixing the type with 'N' and specifying the name before the value, e.g. NB:myFlag:1.
                 * This option may be used multiple times to construct arbitrary AMF sequences. E.g.
                 */
                np.setConnArgs("S:info O:1 NS:uid:10012 NB:vip:1 NN:num:209.12 O:0");
                np.start();
            }

            @Override
            public void stopPublish() {
                np.stop();
            }

            @Override
            public void switchmic() {
                if (isStarting) {
                    isMicOn = !isMicOn;
                    np.setAudioEnable(isMicOn);
                    if (isMicOn) {
                        handler.sendEmptyMessage(3101);
                    } else {
                        handler.sendEmptyMessage(3100);
                    }
                }
            }

            @Override
            public void switchcamera() {
                isCamOn = !isCamOn;
                np.switchCamera();
            }

            @Override
            public void switchflash() {
                int ret;
                if (isFlsOn) {
                    ret = np.setFlashEnable(false);
                } else {
                    ret = np.setFlashEnable(true);
                }
                if (ret == -1) {
                    // 无闪光灯,或处于前置摄像头,不支持闪光灯操作
                } else if (ret == 0) {
                    // 闪光灯被关闭
                    //flashBtn.setBackgroundResource(R.drawable.ic_flash_off);
                    isFlsOn = false;
                } else {
                    // 闪光灯被打开
                    //flashBtn.setBackgroundResource(R.drawable.ic_flash_on);
                    isFlsOn = true;
                }
            }

            @Override
            public void setVideoParam(int videoPreset, int fps, int bitRate) {
//                np.stopPreview();
                np.setVideoParam(videoPreset, fps, bitRate * 1000, NodePublisher.VIDEO_PROFILE_MAIN, false);

//                np.startPreview();
            }

            @Override
            public void onOpenVideo() {

            }

            @Override
            public void changeOrientation(boolean bFullscreen) {
                MainActivity.this.changeOrientation(bFullscreen);
            }

            @Override
            public void enableAudio(boolean bAudioEnable) {
                np.setAudioEnable(bAudioEnable);
            }
        });
        dwebView.addJavascriptObject(publishApi, null);
        dwebView.loadUrl("file:///android_asset/soldierWap.html");
        dwebView.saveState(savedInstanceState);

        initPermissionForCamera();
        initPublish();
        //cameraTask();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFlsOn) {
            np.setFlashEnable(false);
        } else {
           np.setFlashEnable(true);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        dwebView.restoreState(savedInstanceState);
    }

    private void initPublish(){
        npv = (NodeCameraView) findViewById(R.id.camera_preview);
        np = new NodePublisher(this);
        np.setNodePublisherDelegate(MainActivity.this);
        np.setCameraPreview(npv, NodePublisher.CAMERA_BACK, true);
        np.setAudioParam(32 * 1000, NodePublisher.AUDIO_PROFILE_LCAAC);
        np.setVideoParam(NodePublisher.VIDEO_PPRESET_16X9_360, 24, 500 * 1000, NodePublisher.VIDEO_PROFILE_MAIN, false);
        np.setDenoiseEnable(true);
        np.setBeautyLevel(3);
        np.setOutputUrl("rtmp://1011.lsspublish.aodianyun.com/demo/game2");
//        np.startPreview();
    }


    private boolean hasCameraPermission() {
        return EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA);
    }

    public void onTest(View view){
        np.start();
        // np.stopPreview();
//        dwebView.callHandler("addValue", new Object[]{3, 4}, new OnReturnValue<Integer>() {
//            @Override
//            public void onValue(Integer retValue) {
//                showToast(retValue);
//            }
//        });
    }


    @AfterPermissionGranted(RC_CAMERA_PERM)
    public void cameraTask() {
        if (hasCameraPermission()) {
            // Have permission, do the thing!
            Toast.makeText(this, "TODO: Camera things", Toast.LENGTH_LONG).show();
            initPublish();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(
                    this,
                    "打开摄像头",
                    RC_CAMERA_PERM,
                    Manifest.permission.CAMERA);
        }
    }

    void showToast(Object o) {
        Toast.makeText(this, o.toString(), Toast.LENGTH_SHORT).show();
    }


    public void changeOrientation(boolean bFullscreen){
//            [self.bridge registerHandler:@"changeOrientation" handler:^(id data, WVJBResponseCallback responseCallback) {
//            NSDictionary *params = (NSDictionary *)data;
//            BOOL bFullscreen = [[params objectForKey:@"bFullscreen"] boolValue];
//        [self changeOrientation:bFullscreen];
//        }];


        if (bFullscreen){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //显示状态栏
        }
    }

    /**
     * Android 6.0以上版本，需求添加运行时权限申请；否则，可能程序崩溃
     */
    private static final int REQUEST_CODE_PERMISSION = 0x110;
    private void initPermissionForCamera() {
        int flag = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (PackageManager.PERMISSION_GRANTED != flag) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_CODE_PERMISSION == requestCode) {
            switch (grantResults[0]) {
                case PackageManager.PERMISSION_DENIED:
                    boolean isSecondRequest = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA);
                    if (isSecondRequest)
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION);
                    else
                        Toast.makeText(this, "拍照权限被禁用，请在权限管理修改", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 处理页面返回或取消选择结果
         */
        switch (requestCode) {
            case PickPhotoUtil.REQUEST_FILE_PICKER:
                pickPhotoResult(resultCode, data);
                break;
            case PickPhotoUtil.REQUEST_CODE_PICK_PHOTO:
                pickPhotoResult(resultCode, data);
                break;
            case PickPhotoUtil.REQUEST_CODE_TAKE_PHOTO:
                takePhotoResult(resultCode);
                break;
            case PickPhotoUtil.REQUEST_CODE_PREVIEW_PHOTO:
                cancelFilePathCallback();
                break;
            default:

                break;
        }
    }

    private void pickPhotoResult(int resultCode, Intent data) {
        if (PickPhotoUtil.mFilePathCallback != null) {
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (result != null) {
                String path = GetPathFromUri4kitkat.getPath(this, result);
                Uri uri = Uri.fromFile(new File(path));
                PickPhotoUtil.mFilePathCallback.onReceiveValue(new Uri[]{uri});
                /**
                 * 将路径赋值给常量photoFile4，记录第一张上传照片路径
                 */
                PickPhotoUtil.photoPath = path;

                android.util.Log.d(TAG, "onActivityResult: " + path);
            } else {
                /**
                 * 点击了file按钮，必须有一个返回值，否则会卡死
                 */
                PickPhotoUtil.mFilePathCallback.onReceiveValue(null);
                PickPhotoUtil.mFilePathCallback = null;
            }
            /**
             * 针对API 19之前的版本
             */
        } else if (PickPhotoUtil.mFilePathCallback4 != null) {
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (result != null) {
                String path = GetPathFromUri4kitkat.getPath(this, result);
                Uri uri = Uri.fromFile(new File(path));
                PickPhotoUtil.mFilePathCallback4.onReceiveValue(uri);
                /**
                 * 将路径赋值给常量photoFile
                 */
                android.util.Log.d(TAG, "onActivityResult: " + path);
            } else {
                /**
                 * 点击了file按钮，必须有一个返回值，否则会卡死
                 */
                PickPhotoUtil.mFilePathCallback4.onReceiveValue(null);
                PickPhotoUtil.mFilePathCallback4 = null;
            }
        }
    }

    private void takePhotoResult(int resultCode) {
        if (PickPhotoUtil.mFilePathCallback != null) {
            if (resultCode == RESULT_OK) {
                String path = PickPhotoUtil.photoPath;
                Uri uri = Uri.fromFile(new File(path));
                PickPhotoUtil.mFilePathCallback.onReceiveValue(new Uri[]{uri});

                android.util.Log.d(TAG, "onActivityResult: " + path);
            } else {
                /**
                 * 点击了file按钮，必须有一个返回值，否则会卡死
                 */
                PickPhotoUtil.mFilePathCallback.onReceiveValue(null);
                PickPhotoUtil.mFilePathCallback = null;
            }
            /**
             * 针对API 19之前的版本
             */
        } else if (PickPhotoUtil.mFilePathCallback4 != null) {
            if (resultCode == RESULT_OK) {
                String path = PickPhotoUtil.photoPath;
                Uri uri = Uri.fromFile(new File(path));
                PickPhotoUtil.mFilePathCallback4.onReceiveValue(uri);

                android.util.Log.d(TAG, "onActivityResult: " + path);
            } else {
                /**
                 * 点击了file按钮，必须有一个返回值，否则会卡死
                 */
                PickPhotoUtil.mFilePathCallback4.onReceiveValue(null);
                PickPhotoUtil.mFilePathCallback4 = null;
            }
        }
    }

    private void cancelFilePathCallback() {
        if (PickPhotoUtil.mFilePathCallback4 != null) {
            PickPhotoUtil.mFilePathCallback4.onReceiveValue(null);
            PickPhotoUtil.mFilePathCallback4 = null;
        } else if (PickPhotoUtil.mFilePathCallback != null) {
            PickPhotoUtil.mFilePathCallback.onReceiveValue(null);
            PickPhotoUtil.mFilePathCallback = null;
        }
    }





    @Override
    public void onEventCallback(NodePublisher nodePublisher, int event, String msg) {
        handler.sendEmptyMessage(event);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(getApplicationContext(), "用户授权失败", Toast.LENGTH_LONG).show();
        /**
         * 若是在权限弹窗中，用户勾选了'NEVER ASK AGAIN.'或者'不在提示'，且拒绝权限。
         * 这时候，需要跳转到设置界面去，让用户手动开启。
         */
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    private static class UIMyHandler extends UIHandler<MainActivity>{
        public UIMyHandler(MainActivity cls) {
            super(cls);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = ref.get();
            if (activity != null){
                if (activity.isFinishing())
                    return;
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("event",msg.what);
                    activity.dwebView.callHandler("addValue", new Object[]{msg.what});
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                switch (msg.what) {
                    case 2000:
                        //Toast.makeText(LivePublisherDemoActivity.this, "正在发布视频", Toast.LENGTH_SHORT).show();
                        break;
                    case 2001:
                        //Toast.makeText(LivePublisherDemoActivity.this, "视频发布成功", Toast.LENGTH_SHORT).show();
                        //videoBtn.setBackgroundResource(R.drawable.ic_video_start);
                        activity.isStarting = true;
                        break;
                    case 2002:
                        //Toast.makeText(LivePublisherDemoActivity.this, "视频发布失败", Toast.LENGTH_SHORT).show();
                        break;
                    case 2004:
                        //Toast.makeText(LivePublisherDemoActivity.this, "视频发布结束", Toast.LENGTH_SHORT).show();
                        //videoBtn.setBackgroundResource(R.drawable.ic_video_stop);
                        activity.isStarting = false;
                        break;
                    case 2005:
                        //Toast.makeText(LivePublisherDemoActivity.this, "网络异常,发布中断", Toast.LENGTH_SHORT).show();
                        break;
                    case 2100:
                        // 发布端网络阻塞，已缓冲了2秒的数据在队列中
                        //Toast.makeText(LivePublisherDemoActivity.this, "网络阻塞，发布卡顿", Toast.LENGTH_SHORT).show();
                        break;
                    case 2101:
                        // 发布端网络恢复畅通
                        //Toast.makeText(LivePublisherDemoActivity.this, "网络恢复，发布流畅", Toast.LENGTH_SHORT).show();
                        break;
                    case 2102:
                        //Toast.makeText(LivePublisherDemoActivity.this, "截图保存成功", Toast.LENGTH_SHORT).show();
                        break;
                    case 2103:
                        //Toast.makeText(LivePublisherDemoActivity.this, "截图保存失败", Toast.LENGTH_SHORT).show();
                        break;
                    case 2104:
                        //Toast.makeText(LivePublisherDemoActivity.this, "网络阻塞严重,无法继续推流,断开连接", Toast.LENGTH_SHORT).show();
                        break;
                    case 2300:
                        //Toast.makeText(LivePublisherDemoActivity.this, "摄像头和麦克风都不能打开,用户没有给予访问权限或硬件被占用", Toast.LENGTH_SHORT).show();
                        break;
                    case 2301:
                        //Toast.makeText(LivePublisherDemoActivity.this, "麦克风无法打开", Toast.LENGTH_SHORT).show();
                        break;
                    case 2302:
                        //Toast.makeText(LivePublisherDemoActivity.this, "摄像头无法打开", Toast.LENGTH_SHORT).show();
                        break;
                    case 3100:
                        // mic off
                        //micBtn.setBackgroundResource(R.drawable.ic_mic_off);
                        //Toast.makeText(LivePublisherDemoActivity.this, "麦克风静音", Toast.LENGTH_SHORT).show();
                        break;
                    case 3101:
                        // mic on
                        //micBtn.setBackgroundResource(R.drawable.ic_mic_on);
                        //Toast.makeText(LivePublisherDemoActivity.this, "麦克风恢复", Toast.LENGTH_SHORT).show();
                        break;
                    case 3102:
                        // camera off
                        //camBtn.setBackgroundResource(R.drawable.ic_cam_off);
                        //Toast.makeText(LivePublisherDemoActivity.this, "摄像头传输关闭", Toast.LENGTH_SHORT).show();
                        break;
                    case 3103:
                        // camera on
                        //camBtn.setBackgroundResource(R.drawable.ic_cam_on);
                        //Toast.makeText(LivePublisherDemoActivity.this, "摄像头传输打开", Toast.LENGTH_SHORT).show();
                        break;
                    case 4100: {
                        activity.isPreview = !activity.isPreview;
                        if (activity.isPreview) {
                            activity.dwebView.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                            int ret =  activity.np.startPreview();
                        } else {
                            activity.dwebView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            int ret = activity.np.stopPreview();
                        }
                    }
                    default:
                        break;
                }
            }
        }
    }

}
