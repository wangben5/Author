/*
    ShengDao Android Client, BroadcastManager
    Copyright (c) 2015 ShengDao Tech Company Limited
*/

package com.shadt.service.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.shadt.service.LongRunningService;
import com.shadt.util.MyLog;


public class AlarmReceiver extends BroadcastReceiver {

    private Context mContext;
    boolean is_stop = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        //没想到解决办法，目前就这样，虽然不好
//        if (SettingActivity.is_location==true){
        is_stop = intent.getBooleanExtra("is_stop", false);
 //        if (is_stop == true) {
//            MyLog.i("stop");
//            Intent i = new Intent(context, LongRunningService.class);
//            context.stopService(i);
//        } else {
            MyLog.i("start");
            Intent i = new Intent(context, LongRunningService.class);
            context.startService(i);
//        }
//        }else{
//            Intent i = new Intent(context, LongRunningService.class);
//            context.stopService(i);
//        }

    }
}
