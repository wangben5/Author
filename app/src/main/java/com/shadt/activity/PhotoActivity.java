package com.shadt.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shadt.fragment.ImageDetailFragment;
import com.shadt.imageutil.HackyViewPager;
import com.shadt.caibian_news.R;

public class PhotoActivity extends FragmentActivity {
	private static final String STATE_POSITION = "STATE_POSITION";
	public static final String EXTRA_IMAGE_INDEX = "position";
	public static final String EXTRA_IMAGE_URLS = "image_urls";
	private HackyViewPager mPager;
	private int pagerPosition;
	// txt_choose 选择的 数量
	// txt_sel_num 图片当前下标
	TextView txt_sel_num, txt_choose;
	LinearLayout btn_ok;
	TextView chose;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_detail_pager);

		pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
		init();
	}

	@SuppressWarnings("deprecation")
	public void init() {
		Log.v("ceshi", "geitem>>>>>>>>>>>");
		mPager = (HackyViewPager) findViewById(R.id.pager);
		txt_choose = (TextView) findViewById(R.id.txt_choose);
		txt_sel_num = (TextView) findViewById(R.id.txt_sel_num);
		btn_ok = (LinearLayout) findViewById(R.id.btn_ok);
		chose = (TextView) findViewById(R.id.chose);
		if (com.shadt.activity.ImggridActivity.mSelectedImage.contains(com.shadt.activity.ImggridActivity.mImgDir + "/" + com.shadt.activity.ImggridActivity.mImgs.get(pagerPosition))) {
			chose.setText("已经选择");
		}else{
			chose.setText("选择");
		}
		txt_choose.setText("选择图片(" + com.shadt.activity.ImggridActivity.mSelectedImage.size() + "/100)");
		chose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (com.shadt.activity.ImggridActivity.mSelectedImage.contains(com.shadt.activity.ImggridActivity.mImgDir + "/" + com.shadt.activity.ImggridActivity.mImgs.get(pagerPosition))) {
					chose.setText("选择");
					com.shadt.activity.ImggridActivity.mSelectedImage.remove(pagerPosition);
					txt_choose.setText("选择图片(" + com.shadt.activity.ImggridActivity.mSelectedImage.size() + "/100)");
				}else{
					if (com.shadt.activity.ImggridActivity.mSelectedImage.size() > 100) {
						Toast.makeText(PhotoActivity.this, "最多选择100张图片", 0)
								.show();
					} else {
						com.shadt.activity.ImggridActivity.mSelectedImage.add(com.shadt.activity.ImggridActivity.mImgDir + "/" + com.shadt.activity.ImggridActivity.mImgs.get(pagerPosition));
						chose.setText("已经选择");
					}
					
				}
			}
		});
		btn_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		ImagePagerAdapter mAdapter = new ImagePagerAdapter(
				getSupportFragmentManager());
		mPager.setAdapter(mAdapter);

		// CharSequence text = getString(R.string.viewpager_indicator, 1, mPager
		// .getAdapter().getCount());
		// indicator.setText(text);
		// 更新下标
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int arg0) {
				// indicator.setText(text);
				txt_sel_num.setText(1 + com.shadt.activity.ImggridActivity.mSelectedImage.size() + "/"
						+ ImageGridActivitytuwen.dataList.size());
				pagerPosition = arg0;
					if (com.shadt.activity.ImggridActivity.mSelectedImage.contains(com.shadt.activity.ImggridActivity.mImgDir + "/" + com.shadt.activity.ImggridActivity.mImgs.get(arg0))) {
						chose.setText("已经选择");
					}else{
						chose.setText("选择");
					}
			}
		});
		txt_sel_num.setText(1 + pagerPosition + "/"
				+ com.shadt.activity.ImggridActivity.mImgs.size());
		mPager.setCurrentItem(pagerPosition);
	}

	

	private class ImagePagerAdapter extends FragmentStatePagerAdapter {

		public ImagePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return ImageGridActivitytuwen.dataList == null ? 0
					: ImageGridActivitytuwen.dataList.size();
		}

		@Override
		public Fragment getItem(int position) {
			String url = com.shadt.activity.ImggridActivity.mImgs.get(position);
			return ImageDetailFragment.newInstance(com.shadt.activity.ImggridActivity.mImgDir+"/"+url);
		}

	}
}