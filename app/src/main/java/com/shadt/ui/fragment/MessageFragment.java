package com.shadt.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.nukc.stateview.StateView;
import com.shadt.caibian_news.R;
import com.shadt.ui.base.BaseFragment;
import com.shadt.util.MyLog;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;

/**
 * Created by donglinghao on 2016-01-28.
 */
public class MessageFragment extends Fragment {

    private View mRootView;
    ViewPager mViewpager;
    private FragmentPagerAdapter fragmentPagerAdapter;
    private android.support.v4.app.Fragment mConversationlist;
    private android.support.v4.app.Fragment mConversationFragment=null;
    private List<Fragment> mFragment=new ArrayList<>();
    TabLayout mTabLayout;



    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.message_fragment,container,false);


            loadData(rootView);
        } else {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
        }

        return rootView;
    }


    protected void loadData(View v) {
        mViewpager = (ViewPager) v.findViewById(R.id.main_viewpager);
        mTabLayout = (TabLayout) v.findViewById(R.id.mTabLayout);
        init();
    }

    public void init(){
        mConversationlist=initConversationList();
        mFragment.add(mConversationlist);
        mFragment.add(new FriendFragment());
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
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
        mTabLayout.setupWithViewPager(mViewpager);

        mTabLayout.getTabAt(0).setText("消息");
        mTabLayout.getTabAt(1).setText("通讯录");
        mTabLayout.getTabAt(0).select();
//

    }
    private android.support.v4.app.Fragment initConversationList(){
        if (mConversationFragment==null){
            ConversationListFragment listFragment = new ConversationListFragment();
            Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
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
