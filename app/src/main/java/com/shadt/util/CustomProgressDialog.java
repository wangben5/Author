package com.shadt.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shadt.caibian_news.R;

/**
 * @Description:自定义对话框
 * @author http://blog.csdn.net/finddreams
 */
public class CustomProgressDialog extends ProgressDialog {

	private AnimationDrawable mAnimation;
	private Context mContext;
	private ProgressBar mImageView;
	private String mLoadingTip;
	private TextView mLoadingTv;
	private int count = 0;

	public CustomProgressDialog(Context context) {
		super(context);
		this.mContext = context;
		setCanceledOnTouchOutside(false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}

	private void initData() {

		// mImageView.setBackgroundResource(R.anim.f);
		// // 通过ImageView对象拿到背景显示的AnimationDrawable
		// mAnimation = (AnimationDrawable) mImageView.getBackground();
		// 为了防止在onCreate方法中只显示第一帧的解决方案之一
		// mImageView.post(new Runnable() {
		// @Override
		// public void run() {
		// mAnimation.start();
		// }
		// });

	}

	public void setContent(String str) {
		mLoadingTv.setText(str);
	}

	private void initView() {
		setContentView(R.layout.public_dialog);
		mLoadingTv = (TextView) findViewById(R.id.public_pro_txt);
		mImageView = (ProgressBar) findViewById(R.id.progressBar1);
	}

	/*
	 * @Override public void onWindowFocusChanged(boolean hasFocus) { // TODO
	 * Auto-generated method stub mAnimation.start();
	 * super.onWindowFocusChanged(hasFocus); }
	 */
}
