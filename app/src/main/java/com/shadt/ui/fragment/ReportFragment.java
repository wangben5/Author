package com.shadt.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.shadt.caibian_news.R;
import com.shadt.ui.DownloadActivity;
import com.shadt.ui.Edite_txt_Activity;
import com.shadt.ui.MytxtActivity;
import com.shadt.ui.PictureActivity;

import com.shadt.ui.adapater.BaseFragmentAdapter;
import com.shadt.ui.base.BaseFragment;
import com.shadt.ui.db.StickyEvent;
import com.shadt.ui.utils.UIUtils;
import com.shadt.ui.widget.magicindicator.MagicIndicator;
import com.shadt.ui.widget.magicindicator.buildins.UIUtil;
import com.shadt.ui.widget.magicindicator.buildins.commonnavigator.CommonNavigator;
import com.shadt.ui.widget.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import com.shadt.ui.widget.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import com.shadt.ui.widget.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import com.shadt.ui.widget.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import com.shadt.ui.widget.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView;
import com.shadt.ui.widget.pupwindow.PopItemAction;
import com.shadt.ui.widget.pupwindow.PopWindow;
import com.shadt.util.MyLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by donglinghao on 2016-01-28.
 */
public class ReportFragment extends BaseFragment {

    private ViewPager mViewPager;
    private MagicIndicator mIndicator;
    private String[] channels;
    private List<BaseFragment> fragments = new ArrayList<>();
    Context mContext;
    LinearLayout line_pop;
    ImageView mArrowIv;
    TextView text_all_or_mine;
    private FloatingActionMenu floating_action_menu;//改变地图类型菜单按钮
    private FloatingActionButton menu_item_img;//改变地图类型为街道图按钮
    private FloatingActionButton menu_item_video;//改变地图类型为遥感图按钮
    private FloatingActionButton menu_item_audio;//改变地图类型为地形图按钮
    private FloatingActionButton menu_item_down;//改变地图类型为地形图按钮
    private FloatingActionButton menu_item_word;//改变地图类型为地形图按钮

    public static boolean is_mine = false;
    public static String meizi_id = "";

    @Override
    protected int provideContentViewId() {
        return R.layout.report_fragment;
    }

    @Override
    public void initView(View rootView) {
        mContext = getActivity();
        floating_action_menu = (FloatingActionMenu) findViewById(R.id.floating_action_menu);
        menu_item_img = (FloatingActionButton) findViewById(R.id.menu_item_img);
        menu_item_audio = (FloatingActionButton) findViewById(R.id.menu_item_audio);
        menu_item_video = (FloatingActionButton) findViewById(R.id.menu_item_video);
        menu_item_down = (FloatingActionButton) findViewById(R.id.menu_item_down);
        menu_item_word = (FloatingActionButton) findViewById(R.id.menu_item_word);
        floating_action_menu.setOnClickListener(new MapListener());
        floating_action_menu.setClosedOnTouchOutside(true);
        menu_item_img.setOnClickListener(new MapListener());
        menu_item_audio.setOnClickListener(new MapListener());
        menu_item_video.setOnClickListener(new MapListener());
        menu_item_down.setOnClickListener(new MapListener());
        menu_item_word.setOnClickListener(new MapListener());
        mViewPager = findViewById(R.id.viewpager);
        mIndicator = findViewById(R.id.magic_indicator);
        line_pop = findViewById(R.id.line_pop);
        mArrowIv = findViewById(R.id.iv_arrow);
        text_all_or_mine = findViewById(R.id.text_all_or_mine);
        line_pop.setOnClickListener(new MapListener());
        SharedPreferences preferences = mContext.getSharedPreferences("user",
                Context.MODE_PRIVATE);
        meizi_id = preferences.getString("meizi_id", "");

        //注册订阅者
        EventBus.getDefault().register(this);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(StickyEvent event) {


    }

    //地图操作监听函数
    //改变地图类型监听监听函数
    class MapListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.menu_item_word:
                    //设置底图为街道图并收起菜单
                    floating_action_menu.close(true);
                    Intent it0 = new Intent(mContext, MytxtActivity.class);
                    startActivity(it0);
                    break;
                case R.id.menu_item_down:
                    //设置底图为街道图并收起菜单
                    floating_action_menu.close(true);
                    Intent it = new Intent(mContext, DownloadActivity.class);
                    startActivity(it);

                    break;
                case R.id.menu_item_video:
                    //设置底图为遥感图并收起菜单
                    floating_action_menu.close(true);
                    Intent it1 = new Intent(mContext, PictureActivity.class);
                    it1.putExtra("type", "2");
                    startActivity(it1);
                    break;
                case R.id.menu_item_img:
                    //设置底图为地形图并收起菜单
                    floating_action_menu.close(true);
                    Intent it2 = new Intent(mContext, PictureActivity.class);
                    it2.putExtra("type", "1");
                    startActivity(it2);
                    break;
                case R.id.menu_item_audio:
                    //设置底图为地形图并收起菜单
                    floating_action_menu.close(true);
                    Intent it3 = new Intent(mContext, PictureActivity.class);
                    it3.putExtra("type", "3");
                    startActivity(it3);
                    break;
                case R.id.line_pop:
                    onclic_pop(line_pop);
                    break;
            }
        }
    }

    NewListFragment mNewListFragment;

    @Override
    protected void loadData() {
        channels = getResources().getStringArray(R.array.provinces);

        initIndicator();
        for (int i = 0; i < channels.length; i++) {
            mNewListFragment = new NewListFragment();
            fragments.add(mNewListFragment.newInstance("" + i));
        }

        mViewPager.setAdapter(new BaseFragmentAdapter(fragments, getChildFragmentManager()));
    }
    public int myposition=0;
    @Override
    public void initListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {

                mIndicator.onPageSelected(position);

                myposition=position;

                //全部素材
                if (is_mine==false){
                    if(position==0){
                        if (is_mine!=is_mine_fmg1){
                            EventBus.getDefault().postSticky(new StickyEvent(""+myposition));
                        }
                        is_mine_fmg1=false;

                    }else if(position==1){
                        if (is_mine!=is_mine_fmg2){
                            EventBus.getDefault().postSticky(new StickyEvent(""+myposition));
                        }
                        is_mine_fmg2=false;
                    }else if(position==2){
                        if (is_mine!=is_mine_fmg3){
                            EventBus.getDefault().postSticky(new StickyEvent(""+myposition));
                        }
                        is_mine_fmg3=false;
                    }else if(position==3){
                        if (is_mine!=is_mine_fmg4){
                            EventBus.getDefault().postSticky(new StickyEvent(""+myposition));
                        }
                        is_mine_fmg4=false;
                    }else if(position==4){
                        if (is_mine!=is_mine_fmg5){
                            EventBus.getDefault().postSticky(new StickyEvent(""+myposition));
                        }
                        is_mine_fmg5=false;
                    }

                }else{
                    if(position==0){
                        if (is_mine!=is_mine_fmg1){
                            EventBus.getDefault().postSticky(new StickyEvent(""+myposition));
                        }
                        is_mine_fmg1=true;
                    }else if(position==1){
                        if (is_mine!=is_mine_fmg2){
                            EventBus.getDefault().postSticky(new StickyEvent(""+myposition));
                        }
                        is_mine_fmg2=true;
                    }else if(position==2){
                        if (is_mine!=is_mine_fmg3){
                            EventBus.getDefault().postSticky(new StickyEvent(""+myposition));
                        }
                        is_mine_fmg3=true;
                    }else if(position==3){
                        if (is_mine!=is_mine_fmg4){
                            EventBus.getDefault().postSticky(new StickyEvent(""+myposition));
                        }
                        is_mine_fmg4=true;
                    }else if(position==4){
                        if (is_mine!=is_mine_fmg5){
                            EventBus.getDefault().postSticky(new StickyEvent(""+myposition));
                        }
                        is_mine_fmg5=true;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mIndicator.onPageScrollStateChanged(state);
            }
        });

    }
    public boolean is_mine_fmg1=false;
    public boolean is_mine_fmg2=false;
    public boolean is_mine_fmg3=false;
    public boolean is_mine_fmg4=false;
    public boolean is_mine_fmg5=false;
    private void initIndicator() {
        CommonNavigator mCommonNavigator = new CommonNavigator(UIUtils.getContext());
        mCommonNavigator.setSkimOver(true);
        mCommonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return channels.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ClipPagerTitleView clipPagerTitleView = new ClipPagerTitleView(context);
                clipPagerTitleView.setText(channels[index]);
                clipPagerTitleView.setTextSize(UIUtils.sp2px(15));
                clipPagerTitleView.setClipColor(getResources().getColor(R.color.colorPrimary));
                clipPagerTitleView.setTextColor(getResources().getColor(R.color.text_hint));
                clipPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return clipPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setYOffset(UIUtil.dip2px(context, 3));
                indicator.setRoundRadius(5);
                indicator.setColors(getResources().getColor(R.color.colorPrimary));
                return indicator;
            }
        });
        mIndicator.setNavigator(mCommonNavigator);
    }

    public void onclic_pop(View view) {
        new PopWindow.Builder(getActivity())
                .setStyle(PopWindow.PopWindowStyle.PopDown)
                .setIsShowCircleBackground(false)

                .addItemAction(new PopItemAction("全部素材", PopItemAction.PopItemStyle.Normal, new PopItemAction.OnClickListener() {
                    @Override
                    public void onClick() {
                        text_all_or_mine.setText("全部素材");
                        if (is_mine == false) {

                        } else {
                            if(myposition==0){

                                is_mine_fmg1=false;
                            }else if(myposition==1){

                                is_mine_fmg2=false;
                            }else if(myposition==2){

                                is_mine_fmg3=false;
                            }else if(myposition==3){

                                is_mine_fmg4=false;
                            }else if(myposition==4){

                                is_mine_fmg5=false;
                            }
                            EventBus.getDefault().postSticky(new StickyEvent(""+myposition));
                        }
                        is_mine = false;

                    }
                }))
                .addItemAction(new PopItemAction("我上传的", PopItemAction.PopItemStyle.Normal, new PopItemAction.OnClickListener() {
                    @Override
                    public void onClick() {
                        text_all_or_mine.setText("我上传的");
                        if (is_mine == true) {

                        } else {
                            if(myposition==0){

                                is_mine_fmg1=true;
                            }else if(myposition==1){

                                is_mine_fmg2=true;
                            }else if(myposition==2){

                                is_mine_fmg3=true;
                            }else if(myposition==3){

                                is_mine_fmg4=true;
                            }else if(myposition==4){

                                is_mine_fmg5=true;
                            }
                             EventBus.getDefault().postSticky(new StickyEvent(""+myposition));
                        }
                        is_mine = true;
                    }
                }))
                .setPopWindowMargins(dip2px(-16), dip2px(0), dip2px(-16), dip2px(0))
                .setControlViewAnim(mArrowIv, R.anim.btn_rotate_anim_1, R.anim.btn_rotate_anim_2, true)
                .show(view);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
