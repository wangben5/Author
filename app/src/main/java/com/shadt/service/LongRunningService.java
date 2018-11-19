package com.shadt.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.shadt.service.broadcast.AlarmReceiver;
import com.shadt.ui.contacts.Contact;
import com.shadt.ui.db.Tongxunlu;
import com.shadt.ui.utils.JsonUtils;
import com.shadt.util.MyLog;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LongRunningService extends Service {

    private static final String TAG = "MyService";
    private final IBinder myBinder = new LocalBinder();



    // 调用startService方法启动Service时调用该方法
    @Override
    public void onStart(Intent intent, int startId) {


    }


    // Service创建并启动后在调用stopService方法或unbindService方法时调用该方法

    //提供给客户端访问
    public class LocalBinder extends Binder {
        LongRunningService getService() {
            return LongRunningService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour =300 * 1000; // 这是 秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;


        if (is_stop==false) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //启动定位
                    mLocationClient.startLocation();
                }
            }).start();
            Intent i = new Intent(this, AlarmReceiver.class);
            i.putExtra("is_stop", false);
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        }
        return super.onStartCommand(intent, flags, startId);
    }
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    String rong_userid;
    String token;
    @Override
    public void onCreate() {
        super.onCreate();
        MyLog.i("服务开始");
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
         Contact.rong_ip= preferences.getString("vsOutData6", "");
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();

        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
        //        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);

        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
//        mLocationOption.setInterval(5000);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否允许模拟位置,默认为true，允许模拟位置
        mLocationOption.setMockEnable(false);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
    }
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {


                    //可在其中解析amapLocation获取相应内容。
                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    String y=""+amapLocation.getLatitude();//获取纬度y
                    String x=""+amapLocation.getLongitude();//获取经度x
                    SharedPreferences preferences = getSharedPreferences("user",
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("location", amapLocation.getAddress());
                    editor.putString("x", x);
                    editor.putString("y", y);
                    editor.commit();

                    if (TextUtils.isEmpty(rong_userid)){
                        getrongid();
                        if (!TextUtils.isEmpty(rong_userid)){
                            post_loacation(x,y);
                        }
                    }else{
                        post_loacation(x,y);
                    }

               /*     amapLocation.getAccuracy();//获取精度信息
                    amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                    amapLocation.getCountry();//国家信息
                    amapLocation.getProvince();//省信息
                    amapLocation.getCity();//城市信息
                    amapLocation.getDistrict();//城区信息
                    amapLocation.getStreet();//街道信息
                    amapLocation.getStreetNum();//街道门牌号信息
                    amapLocation.getCityCode();//城市编码
                    amapLocation.getAdCode();//地区编码
                    amapLocation.getAoiName();//获取当前定位点的AOI信息
                    amapLocation.getBuildingId();//获取当前室内定位的建筑物Id
                    amapLocation.getFloor();//获取当前室内定位的楼层
                    amapLocation.getGpsAccuracyStatus();//获取GPS的当前状态*/
                    //获取定位时间
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(amapLocation.getTime());
                    df.format(date);
                } else {
                    Log.d("wangben", "失败 ");

                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。

                }
            }
        }
    };
    public void getrongid(){
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        rong_userid = preferences.getString("rong_id", "");
        token = preferences.getString("user_token", "");
    }
    public static boolean is_stop = false;
    public static void start() {
        is_stop = false;
    }
    public static void stop() {
        is_stop = true;
    }

    public void post_loacation(String x,String y) {
        MyLog.i("x"+x+"y="+y);
        RequestParams params = new RequestParams();
        params.addBodyParameter("positionXaxis",x);
        params.addBodyParameter("positionYaxis",y);
         params.addBodyParameter("userId",rong_userid);
        params.addBodyParameter("token",token);
        HttpUtils httpUtils = new HttpUtils();

        httpUtils.send(HttpRequest.HttpMethod.POST, Contact.rong_ip+Contact.rong_location, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // TODO Auto-generated method stub

            }
            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                // TODO Auto-generated method stub
                String str = arg0.result;
                MyLog.i("上传位置：" + str);
            }
        });
    }

    @Override
    public void onDestroy() {
        MyLog.i("停止");
        super.onDestroy();
    }
}