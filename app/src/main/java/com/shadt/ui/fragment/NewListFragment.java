package com.shadt.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shadt.caibian_news.R;
import com.shadt.ui.adapater.NewListFragmentAdapter2;
import com.shadt.ui.base.BaseFragment;
import com.shadt.ui.contacts.Contact;
import com.shadt.ui.db.MediaInfo;
import com.shadt.ui.db.StickyEvent;
import com.shadt.ui.utils.AnimationUtil;
import com.shadt.ui.utils.JsonUtils;
import com.shadt.ui.utils.SpaceItemDecoration;
import com.shadt.ui.widget.magicindicator.MagicIndicator;
import com.shadt.util.MyLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by donglinghao on 2016-01-28.
 */
public class NewListFragment extends BaseFragment implements OnRefreshListener{
    Context mContext;
    private ViewPager mViewPager;
    private MagicIndicator mIndicator;
    private String[] channels;
    private List<BaseFragment> fragments = new ArrayList<>();
    private static final String TYPE = "type";
    private LinearLayoutManager mManager;
    private SmartRefreshLayout mSmartRefreshLayout;
    private TextView mTvTip;
    private TextView mTvButtom;
    String type;
    RecyclerView mGridview;
    MediaInfo mMediaInfo;
    RelativeLayout rela_erro;
    TextView text_erro;
    public boolean is_all = true;


    public static NewListFragment newInstance(String mtype) {
        NewListFragment f = new NewListFragment();
        Bundle b = new Bundle();
        b.putString(TYPE, mtype);
        f.setArguments(b);
        return f;
    }

    public void ChangeChannel(String s) {
        handler.sendEmptyMessage(4);
        handler.sendEmptyMessageDelayed(6, delaytime);
    }

    @Override
    protected int provideContentViewId() {
        type = getArguments().getString(TYPE);
        MyLog.i("type" + type);
        mContext = getActivity();
        return R.layout.newlistfragment;
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {

        mSmartRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(4);
                handler.sendEmptyMessageDelayed(6, delaytime);
            }
        }, 0);

    }

    @Override
    public void initView(View rootView) {
        rela_erro = (RelativeLayout) findViewById(R.id.rela_erro);
        rela_erro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.sendEmptyMessage(4);
                handler.sendEmptyMessageDelayed(6, delaytime);
            }
        });
        rela_erro.setVisibility(View.GONE);
        text_erro = (TextView) findViewById(R.id.text_erro);
        mGridview = (RecyclerView) findViewById(R.id.rv);
//        mGridview.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mGridview.addItemDecoration(new SpaceItemDecoration(10, 10));
        mGridview.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));



        mSmartRefreshLayout = findViewById(R.id.refreshLayout);
        mSmartRefreshLayout.setOnRefreshListener(this);
        mSmartRefreshLayout.setEnableOverScrollBounce(false);//是否启用越界回弹
        mSmartRefreshLayout.setEnableOverScrollDrag(false);
 //        mSmartRefreshLayout.autoLoadMore();
        mTvTip = findViewById(R.id.tv_tip);
        mTvButtom=findViewById(R.id.tv_bottom_tip);
        SharedPreferences preferences = mContext.getSharedPreferences("user",
                Context.MODE_PRIVATE);
        Contact.meizi_ip = preferences.getString("zimeiti_ip", "");
        Contact.meizi_apikey = preferences.getString("zimeiti_key", "");

        EventBus.getDefault().register(this);
    }

    String mtype;
    int delaytime=800;
    @Override
    protected void loadData() {

        if (type.equals("0")) {
            mtype = "all";
        } else if (type.equals("1")) {
            mtype = "img";
        } else if (type.equals("2")) {
            mtype = "vid";
        } else if (type.equals("3")) {
            mtype = "aud";
        } else if (type.equals("4")) {
            mtype = "doc";
        }
        handler.sendEmptyMessage(4);
        handler.sendEmptyMessageDelayed(6, delaytime);
    }


    Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 4:
                    showLoadingDialog();
                    break;
                case 5:
                    dismissLoadingDialog();
                    break;
                case 6:
                    LoadSource(Contact.meizi_apikey, mtype);
                    break;
            }
        }

        ;


    };

    public  String getDateToString(long milSecond) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    public void LoadSource(String apikey, String show) {

        RequestParams params = new RequestParams();
        params.addBodyParameter("method", "searchassets");
        params.addBodyParameter("api_key", apikey);
        params.addBodyParameter("searchfor", "*");
        params.addBodyParameter("show", show);
        params.addBodyParameter("maxrows", "0");
        params.addBodyParameter("sortby", "dateadd");

        long timeStamp = System.currentTimeMillis();
        MyLog.i("timeStamp" + timeStamp);
        String finishtime = getDateToString(timeStamp);
        String begintime = getDateToString(timeStamp - 86400 * 1000 * 5);

        params.addBodyParameter("datecreaterange", begintime + "%20TO%20" + finishtime);


        HttpUtils httpUtils = new HttpUtils();

        httpUtils.send(HttpRequest.HttpMethod.POST, Contact.meizi_ip + Contact.meizi_serch, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // TODO Auto-generated method stub
                SharedPreferences preferences = mContext.getSharedPreferences("user",
                        Context.MODE_PRIVATE);

                String s = preferences.getString(mtype, "");
                MyLog.i("失败：" + s);
                mSmartRefreshLayout.finishRefresh(0);
                mTvTip.setText("刷新失败");
                AnimationUtil.showTipView(mTvTip, mGridview);
                setdata(s);
                handler.sendEmptyMessage(5);
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                // TODO Auto-generated method stub
                SharedPreferences preferences = mContext.getSharedPreferences("user",
                        Context.MODE_PRIVATE);
                MyLog.i("chengong：" + arg0.result);
                mTvTip.setText("刷新完成");
                mSmartRefreshLayout.finishRefresh(0);
                AnimationUtil.showTipView(mTvTip, mGridview);
                String str = arg0.result;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(mtype, str);
                editor.apply();
                handler.sendEmptyMessage(5);
                setdata(str);
            }
        });
    }

    NewListFragmentAdapter2 mNewListFragmentAdapter;

    public void setdata(String str) {
        mMediaInfo = JsonUtils.getModel(str, MediaInfo.class);//

        try {
            if (mMediaInfo != null) {
                if (mMediaInfo.getDATA() != null) {
                    if (mMediaInfo.getDATA().size() != 0) {
                        if (type.equals("0") ||type.equals("4") ) {
                            MyLog.i("开始" + mMediaInfo.getDATA().size());
                            for (int i = mMediaInfo.getDATA().size() - 1; i >= 0; i--) {


                                if (!TextUtils.isEmpty(mMediaInfo.getDATA().get(i).get(7))) {
                                    if (mMediaInfo.getDATA().get(i).get(7).equals("doc")) {

                                        if (!mMediaInfo.getDATA().get(i).get(8).equals("txt")) {
                                            mMediaInfo.getDATA().remove(i);
                                        }
                                    }
                                } else {
                                    mMediaInfo.getDATA().remove(i);
                                }
                            }
                        } else {
                            for (int i = mMediaInfo.getDATA().size() - 1; i >= 0; i--) {


                                if (!TextUtils.isEmpty(mMediaInfo.getDATA().get(i).get(7))) {

                                } else {
                                    mMediaInfo.getDATA().remove(i);
                                }
                            }
                        }
                        if (ReportFragment.is_mine == true) {

                            for (int i = mMediaInfo.getDATA().size() - 1; i >= 0; i--) {


                                if (!TextUtils.isEmpty(mMediaInfo.getDATA().get(i).get(30))) {

                                    if (!mMediaInfo.getDATA().get(i).get(30).equals(ReportFragment.meizi_id)) {
                                        mMediaInfo.getDATA().remove(i);
                                    }
                                }
                                if (mMediaInfo.getDATA().size() == 0) {

                                    mNewListFragmentAdapter = null;
                                    mGridview.removeAllViews();
                                    mNewListFragmentAdapter = new NewListFragmentAdapter2(getActivity(), mMediaInfo);
                                    mGridview.setAdapter(mNewListFragmentAdapter);
                                    rela_erro.setVisibility(View.VISIBLE);
                                    text_erro.setText("数据为空!");
                                    return;

                                }
                            }
                        }
                        rela_erro.setVisibility(View.GONE);
                        if (mNewListFragmentAdapter != null) {
                            mNewListFragmentAdapter = null;
                        }
                        MyLog.i(">>>>>数据" + mMediaInfo.getDATA().size());
                        mNewListFragmentAdapter = new NewListFragmentAdapter2(getActivity(), mMediaInfo);
                        mGridview.setAdapter(mNewListFragmentAdapter);
                    } else {
                        if (mNewListFragmentAdapter == null) {
                            rela_erro.setVisibility(View.VISIBLE);
                            text_erro.setText("数据为空!");
                        }
                    }
                } else {
                    if (mNewListFragmentAdapter == null) {
                        rela_erro.setVisibility(View.VISIBLE);
                        text_erro.setText("数据为空!");
                    }
                }
            } else {
                if (mNewListFragmentAdapter == null) {

                    rela_erro.setVisibility(View.VISIBLE);
                    text_erro.setText("数据为空!");
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            if (mNewListFragmentAdapter == null) {
                rela_erro.setVisibility(View.VISIBLE);
                text_erro.setText("数据为空!");
            }
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(StickyEvent event) {
        MyLog.i(">>" + event.msg);
        if (type.equals(event.msg)) {
            loadData();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
