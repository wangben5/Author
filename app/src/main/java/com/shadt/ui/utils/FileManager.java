package com.shadt.ui.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;


import com.luck.picture.lib.PictureExternalPreviewActivity;
import com.luck.picture.lib.PicturePlayAudioActivity;
import com.luck.picture.lib.PictureVideoPlayActivity;
import com.shadt.caibian_news.R;
import com.shadt.util.MyLog;

import java.io.File;

/**
 * Created by 4ndroidev on 16/10/13.
 */
public class FileManager {

    private Context context;

    public FileManager(Context context) {
        this.context = context;
    }

    public String getExtension(String path) {
        int index = path.lastIndexOf(".");
        if (index == -1 || index == path.length() - 1) return "unknown";
        return path.substring(index + 1).toLowerCase();
    }

    public boolean isApk(String extension) {
        return "apk".equals(extension);
    }

    public boolean isMusic(String extension) {
        return "mp3".equals(extension) ||
                "flac".equals(extension) ||
                "aac".equals(extension) ||
                "amr".equals(extension) ||
                "wav".equals(extension) ||
                "m4a".equals(extension) ||
                "ogg".equals(extension);
    }

    public boolean isVideo(String extension) {
        return "mp4".equals(extension) ||
                "avi".equals(extension) ||
                "3gp".equals(extension) ||
                "wmv".equals(extension);
    }

    public boolean isZip(String extension) {
        return "zip".equals(extension) ||
                "gz".equals(extension) ||
                "7z".equals(extension);
    }
    public boolean isImg(String extension){
        return "png".equals(extension) ||
                "PNG".equals(extension) ||
                "gif".equals(extension) ||
                "jpg".equals(extension) ||
                "webp".equals(extension) ||
                "jpge".equals(extension) ||
                "WEBP".equals(extension) ||
                "JPEG".equals(extension) ||
                "bmp".equals(extension);
    }
    public boolean isTxt(String extension){
        return "txt".equals(extension);
    }
    public boolean isRar(String extension) {
        return "rar".equals(extension);
    }

    public void open(String name, String path) {
        int index = path.lastIndexOf(".");
        if (index == -1 || index == path.length() - 1) return;
        String extension = path.substring(index + 1).toLowerCase();
        if (isApk(extension)) {
            install(path);
        } else if (isMusic(extension)) {

            Intent intent = new Intent(context, PicturePlayAudioActivity.class);
            intent.putExtra("audio_path", path);
            context.startActivity(intent);
//            context.overridePendingTransition(com.luck.picture.lib.R.anim.a5, 0);

         /*   Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri data = Uri.parse("file://" + path);
            intent.setDataAndType(data, "audio/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);*/
        } else if (isVideo(extension)) {
            Intent intent = new Intent(context, PictureVideoPlayActivity.class);
            intent.putExtra("video_path", path);
            context.startActivity(intent);

          /*  Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri data = Uri.parse("file://" + path);
            intent.setDataAndType(data, "video/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);*/
        } else if (isZip(extension)) {

        } else if (isRar(extension)) {

        }else if(isImg(extension)){
         /*   Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri data = Uri.parse("file://" + path);
            intent.setDataAndType(data, "image/*");
            context.startActivity(intent);*/
            Intent intent = new Intent(context, PictureExternalPreviewActivity.class);
            intent.putExtra("img_path", path);
            context.startActivity(intent);
        }else if(isTxt(extension)){

        }else {
            Toast.makeText(context, R.string.open_failed, Toast.LENGTH_SHORT).show();
        }
    }


    private void install(String path) {
        if (TextUtils.isEmpty(path)) return;
        File file = new File(path);
        if (!file.exists()) return;
        Intent installIntent = new Intent();
        installIntent.setAction(Intent.ACTION_VIEW);
        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        installIntent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
        context.startActivity(installIntent);
    }
}