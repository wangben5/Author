/*
    ShengDao Android Client, BroadcastManager
    Copyright (c) 2015 ShengDao Tech Company Limited
*/

package com.shadt.service.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.shadt.ui.DownGuanliActivity;
import com.shadt.util.MyLog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class GuanliDownReciver extends BroadcastReceiver {

    private Context mContext;
    boolean is_stop = false;
    private List<LocalMedia> selectImages = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        //没想到解决办法，目前就这样，虽然不好
        Bundle bundle = intent.getBundleExtra("content");

        selectImages = (List<LocalMedia>) bundle.getSerializable(PictureConfig.EXTRA_SELECT_LIST);
        String type = intent.getStringExtra("type");
        MyLog.i("type1" + type + "selectImages" + selectImages.size());
        Intent i = new Intent(context, DownGuanliActivity.class);


        bundle.putSerializable(PictureConfig.EXTRA_SELECT_LIST, (Serializable) selectImages);
        i.putExtra("content", bundle);
        i.putExtra("type", type);
        context.startActivity(i);


    }
}
