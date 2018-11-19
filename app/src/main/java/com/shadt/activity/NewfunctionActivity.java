package com.shadt.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.shadt.ui.SealSearchActivity;
import com.shadt.ui.fragment.FriendFragment;
import com.shadt.fragment.HomeFragment;
import com.shadt.caibian_news.R;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;


public class NewfunctionActivity extends FragmentActivity {
    String TAG="shadt";
    ViewPager mViewpager;
    private FragmentPagerAdapter fragmentPagerAdapter;
    private android.support.v4.app.Fragment mConversationlist;
    private android.support.v4.app.Fragment mConversationFragment=null;
    private List<android.support.v4.app.Fragment> mFragment=new ArrayList<>();

    ImageView ac_iv_search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newfunction);
        ac_iv_search=(ImageView) findViewById(R.id.ac_iv_search);
        mViewpager = (ViewPager) findViewById(R.id.main_viewpager);
        mConversationlist=initConversationList();
        mFragment.add(new HomeFragment());
        mFragment.add(mConversationlist);
        mFragment.add(new FriendFragment());
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public android.support.v4.app.Fragment getItem(int position) {
                return mFragment.get(position);
            }

            @Override
            public int getCount() {
                return mFragment.size();
            }
        };
        mViewpager.setAdapter(fragmentPagerAdapter);
        ac_iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplication(), SealSearchActivity.class));

            }
        });
    }

    private android.support.v4.app.Fragment initConversationList(){
        if (mConversationFragment==null){
            ConversationListFragment listFragment = new ConversationListFragment();
            Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversationlist")
                    .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
                    .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//群组
//                    .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
//                    .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//订阅号
                    .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//系统
                    .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")
                    .build();
            listFragment.setUri(uri);

            return listFragment;
        }
        return mConversationFragment;
    };



}
