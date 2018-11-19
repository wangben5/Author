package com.shadt.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream.PutField;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shadt.adpter.ImageGridAdapter.TextCallback;
import com.shadt.bean.ImageBucket;
import com.shadt.bean.ImageItem;
import com.shadt.bean.txtandpicBean;
import com.shadt.fragment.ImageFragmetn;
import com.shadt.caibian_news.R;
import com.shadt.util.AlbumHelper;
import com.shadt.util.Bimp;
import com.shadt.util.BitmapCache;
import com.shadt.util.BitmapCache.ImageCallback;
import com.shadt.util.OtherFinals;

public class ImageGridActivitytuwen extends BaseActivity implements OnClickListener {
	public static final String EXTRA_IMAGE_LIST = "imagelist";
	public static List<ImageItem> dataList;
	GridView gridView;
	ImageGridAdapter adapter;
	AlbumHelper helper;
	List<ImageBucket> List;
	LinearLayout line_back;
	int my_position = 0;
	private DisplayMetrics dm;
	TextView txt_null, title;
	boolean tuwen=false;
	LinearLayout btn_ok;
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				break;
			default:
				break;
			}
		}
	};
	private boolean addnext=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_image_grid);
		Intent it = getIntent();
		my_position = it.getIntExtra("position", 101);
		tuwen=it.getBooleanExtra("type", false);
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());
		List = helper.getImagesBucketList(false);
		addnext = it.getBooleanExtra("addnext", false); //表示 编辑的 是 下一个栏目的 。
		List<ImageItem> mList = new ArrayList<ImageItem>();
		dataList = new ArrayList<ImageItem>();
		for (int i = 0; i < List.size(); i++) {
			mList = List.get(i).imageList;
			dataList.addAll(mList);
			dataList.get(i).setSelected(false);
		}
		
		// dataList = (List<ImageItem>) getIntent().getSerializableExtra(
		// EXTRA_IMAGE_LIST);
		
		initView();
		btn_ok=(LinearLayout) findViewById(R.id.btn_ok);
		btn_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showdialog();
				for (int i = 0; i < dataList.size(); i++) {
					if (dataList.get(i).isSelected==true) {
						if (tuwen==true) {
							if (addnext==true) {
								Log.v("ceshi", "I+"+dataList.get(i).imagePath);
								txtandpicBean bean = new txtandpicBean();
								bean.setPic(compress(dataList.get(i).imagePath));
								bean.setTxt("");
								AddActivity2.tuwenList.add(my_position + 1, bean);
							}else{
								AddActivity2.tuwenList.get(my_position).setPic(dataList.get(i).imagePath);
							}
						}else{
							AddNewsActivity.channel_list.get(my_position)
							.setFieldcontext(img_path);
						}
					}
				}
				finish();
			}
		});
		line_back = (LinearLayout) findViewById(R.id.line_back);
		line_back.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				ArrayList<String> list = new ArrayList<String>();
				Collection<String> c = adapter.map.values();
				Iterator<String> it = c.iterator();
				for (; it.hasNext();) {
					list.add(it.next());
				}
				if (Bimp.act_bool) {
					Bimp.act_bool = false;
				}
				for (int i = 0; i < list.size(); i++) {
					if (Bimp.drr.size() < 1) {
						Bimp.drr.add(list.get(i));
					}
				}
				clearTag();
				finish();
			}

		});
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what==1) {
				
			}
		};
	};
	String img_path = "";

	private void initView() {
		title = (TextView) findViewById(R.id.title);
		title.setText("相册");
		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new ImageGridAdapter(ImageGridActivitytuwen.this, dataList,
				mHandler);
		txt_null = (TextView) findViewById(R.id.txt_null);

		if (dataList.size() == 0) {
			txt_null.setVisibility(View.VISIBLE);
		} else {
			txt_null.setVisibility(View.GONE);
		}
		gridView.setAdapter(adapter);
	}
	
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		// case R.id.ivMyPhotoReturn:
		// clearTag();
		// finish();
		// break;

		default:
			break;
		}
	}
	private void clearTag() {
		for (int i = 0; i < List.size(); i++) {
			dataList.get(i).setSelected(false);
		}
	}
	// 压缩图片
	public String compress(String srcPath) {
		File file = new File(OtherFinals.DIR_IMG);
		if (!file.exists()) {
			file.mkdir();
		}
		Log.v("ceshi", ">>>>>>>>>>>>>>>>");
		String s = OtherFinals.DIR_IMG
				+ String.valueOf(System.currentTimeMillis()) + ".jpg";
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		float hh = dm.heightPixels;
		float ww = dm.widthPixels;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, opts);
		opts.inJustDecodeBounds = false;
		int w = opts.outWidth;
		int h = opts.outHeight;
		int size = 0;
		if (w <= ww && h <= hh) {
			size = 1;
		} else {
			double scale = w >= h ? w / ww : h / hh;
			double log = Math.log(scale) / Math.log(2);
			double logCeil = Math.ceil(log);
			size = (int) Math.pow(2, logCeil);
		}
		opts.inSampleSize = size;
		bitmap = BitmapFactory.decodeFile(srcPath, opts);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int quality = 100;
		bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
		System.out.println(baos.toByteArray().length);
		while (baos.toByteArray().length > 50 * 1024) {
			baos.reset();
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
			quality -= 10;
			System.out.println(baos.toByteArray().length);
		}
		try {
			baos.writeTo(new FileOutputStream(s));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				baos.flush();
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return s;
	}
	
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
					Intent it=new Intent(act,PhotoActivity.class);
					it.putExtra("position", position);
					startActivity(it);
				}
			});
			return convertView;
		}
	}

	@Override
	public void initPages() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onClickListener(View v) {
		// TODO Auto-generated method stub
		
	}
}
