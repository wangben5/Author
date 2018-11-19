package com.shadt.fragment;

import com.shadt.activity.ImageGridActivitytuwen;
import com.shadt.caibian_news.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class ImageFragmetn extends FragmentActivity {
	private FragmentPagerAdapter mAdapter;
	int myposition=0;
	ViewPager viewPager;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.fragment_imggrid);
		viewPager=(ViewPager) findViewById(R.id.view_pager);
		Intent it=getIntent();
		myposition=it.getIntExtra("position", 0);
		
		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
			@Override
			public int getCount() {
				return ImageGridActivitytuwen.dataList.size();
			}
			@Override
			public Fragment getItem(int arg0) {
				
				return ImageFragment1.newInstance(ImageGridActivitytuwen.dataList.get(myposition).imagePath);

			}
		};
		viewPager.setAdapter(mAdapter);
	}
}
