package com.shadt.adpter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.sax.StartElementListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.shadt.activity.AddActivity2;
import com.shadt.bean.ImageItem;
import com.shadt.caibian_news.R;
import com.shadt.util.BitmapCache;
import com.shadt.util.BitmapCache.ImageCallback;

public class ImageGridAdapter extends BaseAdapter {

	private TextCallback textcallback = null;
	final String TAG = getClass().getSimpleName();
	Context act;
	List<ImageItem> dataList;
	public Map<String, String> map = new HashMap<String, String>();
	BitmapCache cache;
	private Handler mHandler;
	private int selectTotal = 0;
	public List<Integer> sel_integers = new ArrayList<Integer>();
	
	ImageCallback callback = new ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap,
				Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				if (url != null && url.equals((String) imageView.getTag())) {
					((ImageView) imageView).setImageBitmap(bitmap);
				} else {
					Log.e(TAG, "callback, bmp not match");
				}
			} else {
				Log.e(TAG, "callback, bmp null");
			}
		}

	};

	public static interface TextCallback {
		public void onListen(int count);
	}

	public void setTextCallback(TextCallback listener) {
		textcallback = listener;
	}

	public ImageGridAdapter(Context act, List<ImageItem> list, Handler mHandler) {
		this.act = act;
		dataList = list;
		cache = new BitmapCache();
	}

	@Override
	public int getCount() {
		int count = 0;
		if (dataList != null) {
			count = dataList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	class Holder {
		private ImageView iv;
		private ImageView selected;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;

		if (convertView == null) {
			holder = new Holder();
			convertView = View.inflate(act, R.layout.item_image_grid, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.image);
			holder.selected = (ImageView) convertView
					.findViewById(R.id.isselected);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		final ImageItem item = dataList.get(position);
		
		holder.iv.setTag(item.imagePath);
		 cache.displayBmp(holder.iv, item.thumbnailPath, item.imagePath,
				callback);
		 Log.v("ceshi", "adpter>>>>>>>>..");
		if (item.isSelected) {
			holder.selected.setImageResource(R.drawable.image_no);  
		} else {
			holder.selected.setImageResource(R.drawable.ic_launcher);
		}
		holder.selected.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					if (item.isSelected==true) {
						item.isSelected=false;
						holder.selected.setImageResource(R.drawable.ic_launcher);  
					}else{
						item.isSelected=true;
						holder.selected.setImageResource(R.drawable.image_no);  
					}
			}
		});
		holder.iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent itD=new Intent(act,AddActivity2.class);
				
			}
		});
		return convertView;
	}
}
