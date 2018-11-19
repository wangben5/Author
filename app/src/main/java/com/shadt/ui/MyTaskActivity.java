package com.shadt.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
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
import com.shadt.ui.adapater.TaskListFragmentAdapter;
import com.shadt.ui.contacts.Contact;
import com.shadt.ui.db.TaskInfo;
import com.shadt.ui.utils.AnimationUtil;
import com.shadt.ui.utils.JsonUtils;
import com.shadt.ui.utils.SpaceItemDecoration;
import com.shadt.util.MyLog;

import java.io.File;
import java.util.ArrayList;

public class MyTaskActivity extends BaseActivity implements OnRefreshListener, OnLoadMoreListener {
    RecyclerView mRecyclerView;
    TextView txt_changge;
    LinearLayout line_back;
    private Activity activity = MyTaskActivity.this;
    SmartRefreshLayout mSmartRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        initPages();
    }

    ArrayList<File> list;
    RelativeLayout rela_erro;
    private TaskListFragmentAdapter mTaskListFragmentAdapter;
    String token;
    private TextView mTvTip;
    private TextView mTvButtom;

    @Override
    public void initPages() {
        line_back = findViewById(R.id.line_back);
        title = findViewById(R.id.title);
        title.setText("我的任务");
        mTvTip = findViewById(R.id.tv_tip);
        mTvButtom = findViewById(R.id.tv_bottom_tip);
        token = get_sharePreferences_UserToken();
        mSmartRefreshLayout = findViewById(R.id.refreshLayout);
        mSmartRefreshLayout.setOnRefreshListener(this);
        mSmartRefreshLayout.setEnableOverScrollBounce(false);//是否启用越界回弹
        mSmartRefreshLayout.setEnableOverScrollDrag(false);
        mSmartRefreshLayout.setOnLoadMoreListener(this);
        mSmartRefreshLayout.setEnableAutoLoadMore(true);
        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        rela_erro = (RelativeLayout) findViewById(R.id.rela_erro);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(10, 10));
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        list = new ArrayList<File>();
        MyLog.i("list" + list);


        loadData();


        line_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rela_erro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData();
            }
        });

    }

    @Override
    public void onClickListener(View v) {

    }


    protected void loadData() {


        handler.sendEmptyMessage(4);
        handler.sendEmptyMessageDelayed(6, delaytime);
    }


    public void LoadSource(int page) {

        RequestParams params = new RequestParams();

        HttpUtils httpUtils = new HttpUtils();
        String postUlr = Contact.rong_ip + Contact.task + "?limit=10&page=" + mpage + "&token=" + token;
        MyLog.i("postUlr" + postUlr);
        httpUtils.send(HttpRequest.HttpMethod.GET, postUlr, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // TODO Auto-generated method stub
                handler.sendEmptyMessage(5);
                if (is_refresh == true) {
                    mTvTip.setText("刷新完成");
                    mSmartRefreshLayout.finishRefresh(0);
                    AnimationUtil.showTipView(mTvTip, mRecyclerView);
                    handler.sendEmptyMessage(1);
                } else {
                    mpage--;
                    mSmartRefreshLayout.finishLoadMore();
                    AnimationUtil.showBottomTipView(mTvButtom);
                }
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                // TODO Auto-generated method stub
                handler.sendEmptyMessage(5);
                if (is_refresh == true) {
                    mTvTip.setText("刷新完成");
                    mSmartRefreshLayout.finishRefresh(0);
                    AnimationUtil.showTipView(mTvTip, mRecyclerView);
                    setData(arg0.result);
                } else {
                    mSmartRefreshLayout.finishLoadMore();

                    setloadmore(arg0.result);
                }
                MyLog.i("chengong：" + arg0.result);


            }
        });
    }

    TaskInfo mTaskInfo;

    public void setData(String str) {
        mTaskInfo = JsonUtils.getModel(str, TaskInfo.class);//
        if (mTaskInfo.getResult().getList().size() == 0) {
            handler.sendEmptyMessage(1);
        } else {
            rela_erro.setVisibility(View.GONE);
            mTaskListFragmentAdapter = new TaskListFragmentAdapter(activity, handler);
            mRecyclerView.setAdapter(mTaskListFragmentAdapter);
            mTaskListFragmentAdapter.setList(mTaskInfo);
        }

    }

    TaskInfo mTaskInfo_loadmore;

    public void setloadmore(String str) {
        mTaskInfo_loadmore = JsonUtils.getModel(str, TaskInfo.class);//
        if (mTaskInfo_loadmore.getResult().getList().size() == 0) {
            mpage--;
            AnimationUtil.showBottomTipView(mTvButtom);
        } else {
            for (int i = 0; i < mTaskInfo_loadmore.getResult().getList().size(); i++) {
                mTaskInfo.getResult().getList().add(mTaskInfo_loadmore.getResult().getList().get(i));
            }
            mTaskListFragmentAdapter.setList(mTaskInfo);
        }

    }

    public Handler handler = new Handler() {
        /**
         * Subclasses must implement this to receive messages.
         *
         * @param msg
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //获取电影列表成功
                case 1:
                    rela_erro.setVisibility(View.VISIBLE);
                    break;
                //获取电影列表失败
                case -1:
                case 4:
                    showLoadingDialog();
                    break;
                case 5:
                    dismissLoadingDialog();

                    break;
                case 6:
                    LoadSource(mpage);
                    break;
            }
        }
    };
    boolean is_refresh = true;

    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        mpage++;
        is_refresh = false;
        handler.sendEmptyMessage(6);
    }

    public int delaytime = 800;
    public int mpage = 1;

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        mSmartRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mpage = 1;
                is_refresh = true;
                handler.sendEmptyMessage(4);
                handler.sendEmptyMessageDelayed(6, delaytime);
            }
        }, 0);
    }
}
