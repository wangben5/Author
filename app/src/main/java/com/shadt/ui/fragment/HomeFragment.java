package com.shadt.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
 import com.shadt.activity.AddNewsActivity;
import com.shadt.activity.ExamineActivity;
import com.shadt.activity.MineNewsActivity;
import com.shadt.caibian_news.R;
import com.shadt.ui.WebActivity;

import com.shadt.ui.adapater.UrlImgAdapter;
import com.shadt.ui.base.BaseFragment;
import com.shadt.ui.db.LunboInfo;
import com.shadt.ui.utils.JsonUtils;
import com.shadt.ui.widget.lbanners.LMBanners;
import com.shadt.ui.widget.lbanners.transformer.TransitionEffect;

import java.util.ArrayList;
import java.util.List;

import io.rong.imageloader.cache.disc.naming.Md5FileNameGenerator;
import io.rong.imageloader.core.DisplayImageOptions;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imageloader.core.ImageLoaderConfiguration;
import io.rong.imageloader.core.assist.QueueProcessingType;

/**
 * Created by donglinghao on 2016-01-28.
 */
public class HomeFragment extends BaseFragment {

    private View mRootView;
    private Context mcontext;
    private LMBanners mLBanners;
    LinearLayout line_mynews, line_bianji, line_live, line_check_news, line_caifang, line_xuanticehua, line_chat_call;
    //网络图片
    private List<String> networkImages = new ArrayList<>();
    RelativeLayout  rela_caifang, rela_xuanticehua;
    LinearLayout rela_fabuxuanti,rela_renwuqueren;
    List<String> img = new ArrayList<>();
    List<String> url = new ArrayList<>();
    LunboInfo mLunboInfo;
    public static String str;

    public static HomeFragment newInstance(String s) {
        HomeFragment f = new HomeFragment();
        Bundle b = new Bundle();
        str = s;
        f.setArguments(b);
        return f;
    }


    public void setdata(String str) {
        try {
            mLunboInfo = JsonUtils.getModel(str, LunboInfo.class);//
            if (mLunboInfo != null) {
                if (mLunboInfo.getReturnCode() == 0) {
                    if (mLunboInfo.getData().size() != 0) {
                        img = new ArrayList<String>();
                        url = new ArrayList<String>();
                        for (int i = 0; i < mLunboInfo.getData().size(); i++) {
                            img.add(mLunboInfo.getData().get(i).getImg());
                            url.add(mLunboInfo.getData().get(i).getRecordJumpUrl());
                        }

                    } else {
                        //加载错误的数据占位。
                        for (int i = 0; i < 3; i++) {
                            img.add("dddd");
                            url.add("");
                        }

                    }
                } else {
                    for (int i = 0; i < 3; i++) {
                        img.add("dddd");
                        url.add("");
                    }
                }
            } else {
                for (int i = 0; i < 3; i++) {
                    img.add("dddd");
                    url.add("");
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            for (int i = 0; i < 3; i++) {
                img.add("dddd");
                url.add("");
            }
            e.printStackTrace();
        }
    }


    @Override
    protected int provideContentViewId() {
        return R.layout.home_fragment;
    }

    String str12, str13, str14, str15;

    @Override
    protected void loadData() {
        initImageLoader();
        setdata(str);
        mcontext = getActivity();
        line_mynews = (LinearLayout) findViewById(R.id.line_mynews);
        line_bianji = (LinearLayout) findViewById(R.id.line_bianji);
        line_live = (LinearLayout) findViewById(R.id.line_live);
        line_check_news = (LinearLayout) findViewById(R.id.line_check_news);
        rela_fabuxuanti = (LinearLayout) findViewById(R.id.rela_fabuxuanti);
        line_caifang = (LinearLayout) findViewById(R.id.line_caifang);
        line_xuanticehua = (LinearLayout) findViewById(R.id.line_xuanticehua);
        rela_renwuqueren = (LinearLayout) findViewById(R.id.rela_renwuqueren);
        mLBanners = (LMBanners) findViewById(R.id.banners);

        init();
    }

    String redexamine, rededit;
    SharedPreferences preferences;

    public void init() {
        preferences = getActivity().getSharedPreferences("user",
                Context.MODE_PRIVATE);

        str12 = preferences.getString("vsOutData12", "");
        str13 = preferences.getString("vsOutData13", "");
        str14 = preferences.getString("vsOutData14", "");
        str15 = preferences.getString("vsOutData15", "");
        //参数设置
        mLBanners.isGuide(false);//是否为引导页
        mLBanners.setAutoPlay(true);//自动播放
        mLBanners.setVertical(false);//是否锤子播放
        mLBanners.setScrollDurtion(2000);//两页切换时间
        mLBanners.setCanLoop(true);//循环播放
        mLBanners.setSelectIndicatorRes(R.drawable.guide_indicator_select);//选中的原点
        mLBanners.setUnSelectUnIndicatorRes(R.drawable.guide_indicator_unselect);//未选中的原点
        //若自定义原点到底部的距离,默认20,必须在setIndicatorWidth之前调用
        mLBanners.setIndicatorBottomPadding(10);
        mLBanners.setIndicatorWidth(10);//原点默认为5dp
        mLBanners.setHoriZontalTransitionEffect(TransitionEffect.ZoomCenter);//选中喜欢的样式
//        mLBanners.setHoriZontalCustomTransformer(new ParallaxTransformer(R.id.id_image));//自定义样式
        mLBanners.setDurtion(5000);//轮播切换时间
//        mLBanners.hideIndicatorLayout();//隐藏原点
//        mLBanners.showIndicatorLayout();//显示原点
        mLBanners.setIndicatorPosition(LMBanners.IndicaTorPosition.BOTTOM_MID);//设置原点显示位置
//        mLBanners.setAdapter(new LocalImgAdapter(mcontext,url), localImages);
//        addUrilImg();

        mLBanners.setAdapter(new UrlImgAdapter(mcontext, url), img);
        initevent();
    }

    public void initevent() {
        line_mynews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(preferences.getString("name", ""))) {
                    rededit = preferences.getString("rededit", "false");

                    if (rededit.equals("true")) {
                        Intent it = new Intent(mcontext, MineNewsActivity.class);
                        startActivity(it);
                    } else {
                        Toast.makeText(mcontext, "您没有权限进行此操作", 0).show();
                        return;
                    }
                } else {
                    Toast.makeText(mcontext, "请到个人中心绑定记者一线账号", 0).show();
                }
            }
        });
        line_bianji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(preferences.getString("name", ""))) {
                    rededit = preferences.getString("rededit", "false");
                    if (rededit.equals("true")) {
                        Intent it = new Intent(mcontext, AddNewsActivity.class);
                        startActivity(it);
                    } else {
                        Toast.makeText(mcontext, "您没有权限进行此操作", 0).show();
                        return;
                    }
                } else {
                    Toast.makeText(mcontext, "请到个人中心绑定记者一线账号", 0).show();
                }
            }
        });

        line_live.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(preferences.getString("liveurl", ""))) {
                    Intent it = new Intent(mcontext, com.aodianyun.adandroidbridge.MainActivity.class);

                    startActivity(it);
                } else {
                    Toast.makeText(mcontext, "请到个人中心设置直播流地址.", 0).show();
                }
            }
        });

        line_check_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(preferences.getString("name", ""))) {
                    redexamine = preferences.getString("redexamine", "false");
                    if (redexamine.equals("true")) {
                        Intent it = new Intent(mcontext, ExamineActivity.class);
                        startActivity(it);
                    } else {
                        Toast.makeText(mcontext, "您没有权限进行此操作", 0).show();
                        return;
                    }
                } else {
                    Toast.makeText(mcontext, "请到个人中心绑定记者一线账号", 0).show();

                }
            }
        });
        line_xuanticehua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(str12)) {
                    Toast.makeText(mcontext, "该功能还未开放", 0).show();
                } else {
                    Intent it = new Intent(mcontext, WebActivity.class);
                    it.putExtra("url", str12);
                    startActivity(it);
                }

            }
        });
        line_caifang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(str13)) {
                    Toast.makeText(mcontext, "该功能还未开放", 0).show();
                } else {
                    Intent it = new Intent(mcontext, WebActivity.class);
                    it.putExtra("url", str13);
                    startActivity(it);

                }
            }
        });
        rela_fabuxuanti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(str14)) {
                    Toast.makeText(mcontext, "该功能还未开放", 0).show();
                } else {
                    Intent it = new Intent(mcontext, WebActivity.class);
                    it.putExtra("url", str14);
                    startActivity(it);
                }
            }
        });
        rela_renwuqueren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(str15)) {
                    Toast.makeText(mcontext, "该功能还未开放", 0).show();
                } else {
                    Intent it = new Intent(mcontext, WebActivity.class);
                    it.putExtra("url", str15);
                    startActivity(it);
                }

            }
        });


    }

    //本地图片
    private ArrayList<Integer> localImages = new ArrayList<Integer>();


    private void initImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
                showImageForEmptyUri(R.drawable.ic_launcher)
                .cacheInMemory(true).cacheOnDisk(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext()).defaultDisplayImageOptions(defaultOptions)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLBanners != null) {
            mLBanners.stopImageTimerTask();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLBanners != null) {
            mLBanners.startImageTimerTask();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLBanners != null) {
            mLBanners.clearImageTimerTask();
        }
    }


}
