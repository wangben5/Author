package com.shadt.ui.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shadt.caibian_news.R;
import com.shadt.ui.base.BaseFragment;
import com.shadt.ui.contacts.Contact;
import com.shadt.ui.db.Tongxunlu;
import com.shadt.ui.itemdecorationdemo.adapter.CityAdapter;
import com.shadt.ui.itemdecorationdemo.decoration.DividerItemDecoration;
import com.shadt.ui.utils.AnimationUtil;
import com.shadt.ui.utils.JsonUtils;
import com.shadt.util.MyLog;

import indexlib.IndexBar.widget.IndexBar;
import indexlib.suspension.SuspensionDecoration;

public class FriendFragment extends BaseFragment implements OnRefreshListener {

    private static final String TAG = "zxt";
    private static final String INDEX_STRING_TOP = "↑";
    private RecyclerView mRv;
    private CityAdapter mAdapter;
    private LinearLayoutManager mManager;
    private SmartRefreshLayout mSmartRefreshLayout;
    private TextView mTvTip;
    private SuspensionDecoration mDecoration;

    /**
     * 右侧边栏导航区域
     */
    private IndexBar mIndexBar;
    /**
     * 显示指示器DialogText
     */
    private TextView mTvSideBarHint;

    public static FriendFragment instance = null;

    public static FriendFragment getInstance() {
        if (instance == null) {
            instance = new FriendFragment();
        }
        return instance;
    }

    boolean is_refresh = false;
    RelativeLayout rela_erro;

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {

        mSmartRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(4);
                is_refresh = true;
                handler.sendEmptyMessageDelayed(6, 1000);
            }
        }, 0);
    }


    @Override
    public void initView(View rootView) {
        mRv = (RecyclerView) findViewById(R.id.rv);
        rela_erro = findViewById(R.id.rela_erro);
        rela_erro.setVisibility(View.GONE);
        mRv.setLayoutManager(mManager = new LinearLayoutManager(getActivity()));
        //使用indexBar
        mTvSideBarHint = (TextView) findViewById(R.id.tvSideBarHint);//HintTextView
        mIndexBar = (IndexBar) findViewById(R.id.indexBar);//IndexBar
        //如果add两个，那么按照先后顺序，依次渲染。
        mRv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        //indexbar初始化
        mIndexBar.setmPressedShowTextView(mTvSideBarHint)//设置HintTextView
                .setNeedRealIndex(true)//设置需要真实的索引
                .setmLayoutManager(mManager);//设置RecyclerView的LayoutManager
        mSmartRefreshLayout = findViewById(R.id.refreshLayout);
        mSmartRefreshLayout.setOnRefreshListener(this);
        mSmartRefreshLayout.setEnableOverScrollBounce(false);//是否启用越界回弹
        mSmartRefreshLayout.setEnableOverScrollDrag(false);
        mTvTip = findViewById(R.id.tv_tip);
        rela_erro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData();
            }
        });
    }


    @Override
    protected int provideContentViewId() {
        mContext = getActivity();
        preferences = mContext.getSharedPreferences("user",
                Context.MODE_PRIVATE);
        return R.layout.activity_wechat;
    }

    @Override
    protected void loadData() {

        handler.sendEmptyMessage(4);
        handler.sendEmptyMessage(6);
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
                    PhoneToLogin();
                    break;
            }
        }


    };


    SharedPreferences preferences;
    Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getActivity().getSharedPreferences("user",
                Context.MODE_PRIVATE);
        Contact.rong_ip = preferences.getString("vsOutData6", "");
    }

    String str_tongxunlu = "";

    public void PhoneToLogin() {

        RequestParams params = new RequestParams();
        mContext.getSharedPreferences("user",
                Context.MODE_PRIVATE);
        String token = preferences.getString("user_token", "");

        HttpUtils httpUtils = new HttpUtils();
        MyLog.i("Contact.rong_ip + Contact.rong_friend_list" + Contact.rong_ip + Contact.rong_friend_list+ "?token=" + token);
        httpUtils.send(HttpRequest.HttpMethod.GET, Contact.rong_ip + Contact.rong_friend_list + "?token=" + token, params, new RequestCallBack<String>() {
            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // TODO Auto-generated method stub
                MyLog.i("arg1：" + arg1.toString());
                mSmartRefreshLayout.finishRefresh(0);
                mTvTip.setText("刷新失败");
                AnimationUtil.showTipView(mTvTip, mRv);
                handler.sendEmptyMessage(5);
                str_tongxunlu = preferences.getString("tongxunlu", "");
                if (is_refresh == false) {
                    setdata(str_tongxunlu);
                }

            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                // TODO Auto-generated method stub
                String str = arg0.result;
//                UserInfo userInfo = JsonUtils.getModel(str, UserInfo.class);//这里已经解析出来了
                MyLog.i("融媒体通讯录：" + str);
                mTvTip.setText("刷新完成");
                mSmartRefreshLayout.finishRefresh(0);
                AnimationUtil.showTipView(mTvTip, mRv);
                handler.sendEmptyMessage(5);
                str_tongxunlu = preferences.getString("tongxunlu", "");
                if (str_tongxunlu.equals(str)) {
                    MyLog.i("一样的数据");
                    //如果是刷新的话
                    if (is_refresh == false) {
                        setdata(str);
                    }

                } else {
                    if (!TextUtils.isEmpty(str)) {
                        MyLog.i("xinshuju");
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("tongxunlu", str);
                        editor.apply();
                        setdata(str);
                    }
                }

            }
        });
    }


    public void setdata(String str) {
        Tongxunlu tongxunlu = JsonUtils.getModel(str, Tongxunlu.class);

        if (tongxunlu != null) {
            if (tongxunlu.getCode() == 0) {
                if (tongxunlu.getResult() != null) {

                    tongxunlu.getResult().add(0, (Tongxunlu.ResultBean) new Tongxunlu.ResultBean("聊天室").setTop(true).setBaseIndexTag(INDEX_STRING_TOP));

                /*  tongxunlu.getResult().add(1,(Tongxunlu.ResultBean) new Tongxunlu.ResultBean("聊天室").setTop(true).setBaseIndexTag(INDEX_STRING_TOP));
                    tongxunlu.getResult().add(2,(Tongxunlu.ResultBean) new Tongxunlu.ResultBean("A聊天室").setTop(false).setBaseIndexTag(INDEX_STRING_TOP));
                    tongxunlu.getResult().add(3,(Tongxunlu.ResultBean) new Tongxunlu.ResultBean("B聊天室").setTop(false).setBaseIndexTag(INDEX_STRING_TOP));
                    tongxunlu.getResult().add(4,(Tongxunlu.ResultBean) new Tongxunlu.ResultBean("C聊天室").setTop(false).setBaseIndexTag(INDEX_STRING_TOP));
                    tongxunlu.getResult().add(5,(Tongxunlu.ResultBean) new Tongxunlu.ResultBean("D聊天室").setTop(false).setBaseIndexTag(INDEX_STRING_TOP));
                    tongxunlu.getResult().add(6,(Tongxunlu.ResultBean) new Tongxunlu.ResultBean("F聊天室").setTop(false).setBaseIndexTag(INDEX_STRING_TOP));
                    tongxunlu.getResult().add(7,(Tongxunlu.ResultBean) new Tongxunlu.ResultBean("G聊天室").setTop(false).setBaseIndexTag(INDEX_STRING_TOP));
                    tongxunlu.getResult().add(8,(Tongxunlu.ResultBean) new Tongxunlu.ResultBean("AD聊天室").setTop(false).setBaseIndexTag(INDEX_STRING_TOP));
                    tongxunlu.getResult().add(9,(Tongxunlu.ResultBean) new Tongxunlu.ResultBean(".A聊天室").setTop(false).setBaseIndexTag(INDEX_STRING_TOP));*/
                    for (int i = tongxunlu.getResult().size() - 1; i >= 0; i--) {
                      /*  UserInfo muser = new UserInfo("" + ResultBean.getUserId(), ResultBean.getName(), Uri.parse("" + ResultBean.getPortraituri()));
                        RongIM.getInstance().refreshUserInfoCache(muser);*/
                        if (TextUtils.isEmpty(tongxunlu.getResult().get(i).getName())) {
                            tongxunlu.getResult().remove(i);
                        } else {
                            if (tongxunlu.getResult().get(i).getName().contains("指挥中心")) {
                                MyLog.i(">>>>指挥" + tongxunlu.getResult().get(i).getName());

                                tongxunlu.getResult().add(1, (Tongxunlu.ResultBean) new Tongxunlu.ResultBean(tongxunlu.getResult().get(i).getName(), tongxunlu.getResult().get(i).getRyId(), tongxunlu.getResult().get(i).getRyToken()).setTop(true).setBaseIndexTag(INDEX_STRING_TOP));
                                tongxunlu.getResult().remove(i+1);

                            }
                        }

                    }
//                                tongxunlu.getResult().remove(tongxunlu.getResult().size()-1);

                    if (mAdapter == null) {

                    } else {
                        mAdapter = null;
                    }
                    rela_erro.setVisibility(View.GONE);
                    mAdapter = new CityAdapter(getActivity(), tongxunlu);
                    mRv.setAdapter(mAdapter);
                    mIndexBar.setmSourceDatas(tongxunlu.getResult())//设置数据
                            .invalidate();
                }
            } else {
                rela_erro.setVisibility(View.VISIBLE);
            }
        } else {
            rela_erro.setVisibility(View.VISIBLE);
        }
    }
}
