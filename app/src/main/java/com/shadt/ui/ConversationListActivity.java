package com.shadt.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.shadt.caibian_news.R;
import com.shadt.util.MyLog;

import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * Created by Bob on 15/11/3.
 * 聚合会话列表
 */
public class ConversationListActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.conversationlist);
        RongIMClient.getInstance().getConversationList(new RongIMClient.ResultCallback<List<Conversation>>() {

            @Override

            public void onSuccess(List<Conversation> conversations) {

                String si = conversations.get(0).getTargetId();

                Conversation.ConversationType type = conversations.get(0).getConversationType();
                MyLog.i("si"+si);
            }

            @Override

            public void onError(RongIMClient.ErrorCode errorCode) {

            }

        });
    }
}
