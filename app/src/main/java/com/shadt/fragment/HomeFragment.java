package com.shadt.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeFragment extends Fragment {

    public static HomeFragment instance=null;
    public static HomeFragment getInstance(){
     if(instance==null){
         instance=new HomeFragment();
     }
     return  instance;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView tx=new TextView(getContext());
        tx.setText("第一页");
        return tx;

    }
}
