package com.shadt.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.shadt.caibian_news.R;

public class ImageFragment1 extends BaseFragment{
	private static final String IMG_PATH = "path";
	String img_path="";
	private View view;
	ImageView img_pic;
	private Bitmap bitmaps;

	public static ImageFragment1 newInstance(String img_path) {
		ImageFragment1 f = new ImageFragment1();
		Bundle b = new Bundle();
		b.putString(IMG_PATH, img_path);
		f.setArguments(b);
		return f;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		img_path = getArguments().getString(IMG_PATH);
	}
	@Override
	protected void lazyLoad() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public View initView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		if (null != view) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (null != parent) {
				parent.removeView(view);
			}
		} else {
			view = inflater.inflate(R.layout.fragment_img, null);
		}
		return view;
	}
	
	@Override
	protected void initFindViewById(View view) {
		// TODO Auto-generated method stub
//		img_pic=(ImageView) view.findViewById(R.id.img_pic);
		bitmaps = BitmapFactory.decodeFile(img_path);
		Log.v("ceshi", "img_path:"+img_path);
		img_pic.setImageBitmap(bitmaps);
	}
	@Override
	public void initData() {
		// TODO Auto-generated method stub
		
	}
}	
