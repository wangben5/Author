package com.aodianyun.adandroidbridge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.shadt.news.danbin.R;

import org.xwalk.core.XWalkView;

public class CrossActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cross);
        XWalkView xWalkView = findViewById(R.id.xwalkview);
        xWalkView.loadUrl("http://program.guangdianyun.tv/app.index/index.html?uin=1099");
    }
}
