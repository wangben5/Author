package com.aodianyun.adandroidbridge.jsApi;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

import websdk.CompletionHandler;


public class PublishApi {

    private Context mContext;
    private PublishJSListener m_publishlistener;

    /**
     * 开始发布直播
     * @param publiUrl
     */

    @JavascriptInterface
    public void startPublish(final Object publiUrl){
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = mContext.getSharedPreferences("user",
                        Context.MODE_PRIVATE);
                String mpubliUrl = preferences.getString("liveurl", "");
                if (!TextUtils.isEmpty(mpubliUrl)) {
                    m_publishlistener.startPublish("" + mpubliUrl);
                }
            }
        });
    }

    /**
     * 停止发布
     * @param object
     */

    @JavascriptInterface
    public void stopPublish(Object object){
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                m_publishlistener.stopPublish();
            }
        });
    }

    /**
     * 开始和关闭mic
     * @param object
     */

    @JavascriptInterface
    public void switchmic(Object object){
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                m_publishlistener.switchmic();
            }
        });

    }

    /**
     * 切换摄像头
     * @param object
     */

    @JavascriptInterface
    public void switchcamera(Object object){
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                m_publishlistener.switchcamera();
            }
        });

    }

    /**
     * 开启和关闭闪光灯
     * @param object
     */

    @JavascriptInterface
    public void switchflash(Object object){
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                m_publishlistener.switchflash();
            }
        });
    }

    /**
     * 切换屏幕方向
     * @param object
     * @param handler
     */

    @JavascriptInterface
    public void changeOrientation(final Object object, final CompletionHandler handler){
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = (JSONObject)object;
                int bFullscreent = 0;
                try {
                    bFullscreent = jsonObject.getInt("bFullscreen");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                m_publishlistener.changeOrientation(bFullscreent==1?true:false);
                handler.complete();
            }
        });
    }

    /**
     * 开启和关闭视频预览
     * @param object
     */


    @JavascriptInterface
    public void startPreview(final Object object){
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
              boolean bFullscreent = (boolean)object;
//                int bFullscreent = 0;
//                try {
//                    bFullscreent = jsonObject.getInt("bFullscreen");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                m_publishlistener.startPreview(bFullscreent);
            }
        });
    }

    /**
     * 设置发布视频参数 分别：视频分辨，视频帧率，视频码率
     * @param object
     */

    @JavascriptInterface
    public void setVideoParam(final Object object){
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonParams = (JSONObject)object;
                int videoPreset = 0;
                try {
                    videoPreset = jsonParams.getInt("videoPreset");
                    final int fps = jsonParams.getInt("fps");
                    final int bitRate = jsonParams.getInt("bitRate");
                    m_publishlistener.setVideoParam(videoPreset,fps,bitRate);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    public void setPublishlistener(Context context,PublishJSListener publishJSListener)
    {
        this.mContext = context;
        this.m_publishlistener = publishJSListener;
    }

    public interface PublishJSListener{

        void startPreview(boolean bPreview);

        JSONObject getLocation();

        void startPublish(String publiUrl);

        void stopPublish();

        void switchmic();

        void switchcamera();
        void switchflash();
        void setVideoParam(int videoPreset, int fps, int bitRate);
        void onOpenVideo();
        void changeOrientation(boolean bFullscreen);
        void enableAudio(boolean bAudioEnable);
    }
}
