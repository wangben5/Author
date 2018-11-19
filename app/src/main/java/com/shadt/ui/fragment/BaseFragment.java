package com.shadt.ui.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

public abstract class BaseFragment extends Fragment {
    Dialog dialog;
    private LayoutInflater inflaterDl;
    private ProgressBar pro;
    private TextView tx_msg;
    private View pop_view;
    private LayoutInflater mInflater;
    View layout;
    private PopupWindow pop;

    protected boolean isVisible;
    /**
     * 在这里实现Fragment数据的缓加载.
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    protected void onVisible(){
        lazyLoad();
    }

    protected abstract void lazyLoad();

    protected void onInvisible(){}

    protected void getSharedPreferences(String name,String phone,String img,String password,String id,String tuijianma){
        SharedPreferences preferences = getActivity().getSharedPreferences("user",
                Context.MODE_PRIVATE);
        name = preferences.getString("name", "");
        phone = preferences.getString("phone", "");
        img = preferences.getString("imghead", "");
        password = preferences.getString("password", "");
        id = preferences.getString("id", "");
        tuijianma = preferences.getString("other_tuijianma", "");
    }
    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }
    /**
     * 此方法可以得到上下文对象
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    /*
     * 返回一个需要展示的View
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = initView(inflater);
        initFindViewById(view);

        return view;
    }


    /**
     * 子类可以复写此方法初始化事件
     */
    protected  void initEvent(){

    }

    /*
     * 当Activity初始化之后可以在这里进行一些数据的初始化操作
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initEvent();
    }

    /**
     * 子类实现此抽象方法返回View进行展示
     *
     * @return
     */
    public abstract View initView(LayoutInflater inflater);

    /**
     * 初始化控件
     */
    protected abstract void initFindViewById(View view);

    /**
     * 子类在此方法中实现数据的初始化
     */
    public  abstract void initData() ;

}

