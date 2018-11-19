package com.shadt.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shadt.caibian_news.R;
import com.shadt.ui.adapater.TxtListFragmentAdapter2;
import com.shadt.ui.utils.FileComparator2;
import com.shadt.ui.utils.FileUtils;
import com.shadt.ui.utils.SpaceItemDecoration;
import com.shadt.ui.utils.help.ItemTouchHelperCallback;
import com.shadt.ui.utils.help.OnDragListener;
import com.shadt.util.MyLog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class MytxtActivity extends BaseActivity {
    RecyclerView mRecyclerView;
    TextView txt_changge;
    LinearLayout line_back;
    private Activity activity = MytxtActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mytxt);
        initPages();
        onrejestreceiv();

    }

    ArrayList<File> list;
    RelativeLayout rela_erro;
    private TxtListFragmentAdapter2 historyAdapter;

    @Override
    public void initPages() {
        line_back = findViewById(R.id.line_back);
        title = findViewById(R.id.title);
        title.setText("我的文稿");
        txt_changge = findViewById(R.id.txt_changge);
        txt_changge.setText("新建");
        txt_changge.setVisibility(View.VISIBLE);
        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        rela_erro = (RelativeLayout) findViewById(R.id.rela_erro);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(10, 10));
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        list = new ArrayList<File>();
        list = getFileName(FileUtils.path);
        MyLog.i("list" + list);
        historyAdapter = new TxtListFragmentAdapter2(activity, handler);
        if (list.size() == 0 || list == null) {
            rela_erro.setVisibility(View.VISIBLE);
        } else {
            rela_erro.setVisibility(View.GONE);
        }
        historyAdapter.setList(list);
        mRecyclerView.setAdapter(historyAdapter);
        //historyAdapter就是RecyclerView的适配器,historyAdapter实现了接口OnMoveAndSwipedListener
        ItemTouchHelperCallback callback = new ItemTouchHelperCallback(historyAdapter);
        //ItemTouchHelper的构造器需要传入callback,拖拽和滑动事件需要回调callback中的3个方法
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        //把RecyclerView和ItemTouchHelper关联起来用此方法
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        //设置条目拖拽接口
        historyAdapter.setOnDragListener(new OnDragListener() {
            /**
             * @param viewHolder
             * @description:当条目需要拖拽的时候,适配器调用onDrag
             * @author:袁东华 created at 2016/8/31 0031 下午 1:26
             */
            @Override
            public void startDrag(RecyclerView.ViewHolder viewHolder) {
                itemTouchHelper.startDrag(viewHolder);
            }
        });
        txt_changge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getApplication(), Edite_txt_Activity.class);
                startActivity(it);
            }
        });
        line_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rela_erro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MytxtActivity.this, "没有发现本地文稿，快去新建文稿吧.", 0).show();
            }
        });

    }

    @Override
    public void onClickListener(View v) {

    }

    public ArrayList<File> getFileName(String fileAbsolutePath) {
        ArrayList<File> fileList = new ArrayList<File>();
        String path = fileAbsolutePath;//文件夹路径
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                fileList.add(files[i]);
            }
            Collections.sort(fileList, new FileComparator2());
        }
        return fileList;
    }

    public boolean refresh = false;
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;

    public void onrejestreceiv() {
        //动态接受网络变化的广播接收器
        intentFilter = new IntentFilter();
        intentFilter.addAction("refresh");

        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    //自定义接受网络变化的广播接收器
    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            refresh = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (refresh == true) {
            refresh = false;
            list = new ArrayList<File>();
            list = getFileName(FileUtils.path);
            MyLog.i("list" + list);
            if (list.size() == 0 || list == null) {
                rela_erro.setVisibility(View.VISIBLE);
            } else {
                historyAdapter.setList(list);
                rela_erro.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
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

                    break;
            }
        }
    };

}
