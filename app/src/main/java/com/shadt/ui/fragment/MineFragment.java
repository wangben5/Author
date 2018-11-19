package com.shadt.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.shadt.activity.AboutActivity;
import com.shadt.activity.SettingActivity;
import com.shadt.application.MyApp;
import com.shadt.caibian_news.R;
import com.shadt.service.LongRunningService;
import com.shadt.ui.UsaerInfoActivity;
import com.shadt.ui.base.BaseFragment;
import com.shadt.ui.contacts.Contact;
import com.shadt.ui.widget.CircleImageView;
import com.shadt.ui.widget.supertext.SuperTextView;
import com.shadt.util.MyLog;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.rong.imageloader.core.ImageLoader;

/**
 * Created by donglinghao on 2016-01-28.
 */
public class MineFragment extends BaseFragment{

    private View mRootView;
    RelativeLayout rela_userinfo, rela_about,rela_set;
    TextView text_rong_username;
    CircleImageView img_head;
    ImageView img_location,img_switch_guanlian;
    Context mContext;
    SuperTextView sp_location;

    @Override
    protected int provideContentViewId() {
        return R.layout.mine_fragment;
    }

    @Override
    protected void loadData() {
        rela_userinfo = (RelativeLayout) findViewById(R.id.rela_userinfo);
        rela_about = (RelativeLayout) findViewById(R.id.rela_about);
        rela_set = (RelativeLayout) findViewById(R.id.rela_set);
        img_head = (CircleImageView) findViewById(R.id.img_head);
        text_rong_username = (TextView) findViewById(R.id.text_rong_username);
        img_location = (ImageView) findViewById(R.id.img_location);
        sp_location=findViewById(R.id.sp_location);
        init();
    }


    boolean is_location = false;
    String rong_ip;
    String real_name;
    String rong_img;
    SharedPreferences preferences;
    String rong_userid;
    String token;
    public void init() {

        mContext=getActivity();
        onrejestreceiv();
        // TODO Auto-generated method stub
        preferences = mContext.getSharedPreferences("user",
                Context.MODE_PRIVATE);
        real_name= preferences.getString("real_name", "");
        rong_img=preferences.getString("rong_img","");

        rong_ip=preferences.getString("vsOutData6","");

        rong_userid = preferences.getString("rong_id", "");
        token = preferences.getString("user_token", "");
        ImageLoader.getInstance().displayImage(rong_ip+rong_img, img_head, MyApp.getOptions());
        is_location = preferences.getBoolean("is_location", false);
        if (is_location == false) {
            img_location.setImageResource(R.drawable.img_location_off);
        } else {
            img_location.setImageResource(R.drawable.img_location_on);
        }

        text_rong_username.setText(real_name);
        rela_userinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent it=new Intent(getActivity(), UsaerInfoActivity.class);
                startActivity(it);

            }
        });

        rela_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itd = new Intent(mContext, AboutActivity.class);
                startActivity(itd);
            }
        });
        rela_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it=new Intent(getActivity(), SettingActivity.class);
                startActivity(it);
            }
        });
         img_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_location == false) {
                    is_location = true;
                    img_location.setImageResource(R.drawable.img_location_on);
                    Intent intent = new Intent(getActivity(), LongRunningService.class);
                    LongRunningService.start();
                    mContext.startService(intent);
                } else {
                    LongRunningService.stop();
                    img_location.setImageResource(R.drawable.img_location_off);
                    is_location = false;
                }
                SharedPreferences preferences = mContext.getSharedPreferences("user",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("is_location", is_location);
                editor.apply();

            }
        });
        sp_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp_location.setLeftString("正在定位...");
                mLocationClient.startLocation();
            }
        });
        initlocation();
    }
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;
    public void onrejestreceiv() {
        //动态接受网络变化的广播接收器
        intentFilter = new IntentFilter();
        intentFilter.addAction("refreshimg");

        networkChangeReceiver = new NetworkChangeReceiver();
        mContext.registerReceiver(networkChangeReceiver, intentFilter);
    }

    //自定义接受网络变化的广播接收器
    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MyLog.i("更新头像");
            String name=intent.getStringExtra("name");
                if (!TextUtils.isEmpty(name)){
                    text_rong_username.setText(name);
                    return;
                }

            rong_img=preferences.getString("rong_img","");

            ImageLoader.getInstance().displayImage("file://"+rong_img, img_head);


        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (networkChangeReceiver!=null) {
            mContext.unregisterReceiver(networkChangeReceiver);
        }
    }

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    public void initlocation(){
        mLocationClient = new AMapLocationClient(mContext);
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

                    SharedPreferences preferences = mContext.getSharedPreferences("user",
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("location", amapLocation.getAddress());
                    editor.putString("x", x);
                    editor.putString("y", y);
                    editor.commit();

                    if (TextUtils.isEmpty(rong_userid)){

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
                    sp_location.setLeftString(amapLocation.getAddress());
                } else {

                    sp_location.setLeftString("定位失败!");
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                }
            }else{
                sp_location.setLeftString("定位失败!");
            }
        }
    };

    public void post_loacation(String x,String y) {
        MyLog.i("x"+x+"y="+y);
        RequestParams params = new RequestParams();
        params.addBodyParameter("positionXaxis",x);
        params.addBodyParameter("positionYaxis",y);
        params.addBodyParameter("userId",rong_userid);
        params.addBodyParameter("token",token);
        HttpUtils httpUtils = new HttpUtils();
        MyLog.i("Contact.rong_ip+Contact.rong_location+\"?token=\"+token"+Contact.rong_ip+Contact.rong_location);
        httpUtils.send(HttpRequest.HttpMethod.POST, Contact.rong_ip+Contact.rong_location, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // TODO Auto-generated method stub
                Toast.makeText(mContext,"定位上传失败!",0).show();
            }
            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                // TODO Auto-generated method stub
                String str = arg0.result;
                MyLog.i("上传位置：" + str);

            }
        });
    }
}
