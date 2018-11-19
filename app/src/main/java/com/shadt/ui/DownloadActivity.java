package com.shadt.ui;

import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.shadt.caibian_news.R;
import com.shadt.ui.fragment.DownloadedFragment;
import com.shadt.ui.fragment.DownloadingFragment;
import com.shadt.ui.utils.BackEventHandler;
import com.shadt.ui.widget.SwipeBackLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 4ndroidev on 16/10/8.
 */
public class DownloadActivity extends BaseActivity {

    private ViewPager viewPager;

    private List<PageInfo> pages = new ArrayList<>();
    LinearLayout line_back;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        SwipeBackLayout.attachTo(this);
        setContentView(R.layout.activity_download);
        initContentView();
        initPages();
    }

    @Override
    public void initPages() {
        line_back=(LinearLayout) findViewById(R.id.line_back);
        title=(TextView) findViewById(R.id.title);
        title.setText("我的下载");
        line_back.setOnClickListener(this);
        line_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onClickListener(View v) {

    }

    public void initContentView() {
        Resources resources = getResources();


        DownloadingFragment downloadingFragment = new DownloadingFragment();
        DownloadedFragment downloadedFragment = new DownloadedFragment();
        pages.add(new PageInfo(resources.getString(R.string.download_title_downloading), downloadingFragment));
        pages.add(new PageInfo(resources.getString(R.string.download_title_downloaded), downloadedFragment));
        viewPager = (ViewPager) findViewById(R.id.download_viewpager);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return pages.get(position).fragment;
            }

            @Override
            public int getCount() {
                return pages.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return pages.get(position).title;
            }
        });
        ((TabLayout) findViewById(R.id.download_tabs)).setupWithViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = pages.get(viewPager.getCurrentItem()).fragment;
        if (fragment instanceof BackEventHandler && ((BackEventHandler) fragment).onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    private class PageInfo {
        String title;
        Fragment fragment;

        private PageInfo(String title, Fragment fragment) {
            this.title = title;
            this.fragment = fragment;
        }
    }
}
