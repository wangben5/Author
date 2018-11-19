package com.shadt.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shadt.caibian_news.R;

/**
 * Created by Bob on 15/11/3.
 * 聚合会话列表
 */
public class ConversationActivity extends FragmentActivity {
    TextView title;
    LinearLayout img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.conversation);
        title = (TextView) findViewById(R.id.title);
        title.setText(getIntent().getData().getQueryParameter("title"));
        img_back = (android.widget.LinearLayout) findViewById(R.id.line_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
