package com.shadt.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.shadt.adpter.ImageGridAdapter;
import com.shadt.bean.ImageBucket;
import com.shadt.bean.ImageItem;
import com.shadt.bean.txtandpicBean;
import com.shadt.caibian_news.R;
import com.shadt.util.AlbumHelper;
import com.shadt.util.Bimp;
import com.shadt.util.OtherFinals;

public class ImageGridActivity extends Activity implements OnClickListener {
	public static final String EXTRA_IMAGE_LIST = "imagelist";
	List<ImageItem> dataList;
	GridView gridView;
	ImageGridAdapter adapter;
	AlbumHelper helper;
	List<ImageBucket> List;
	LinearLayout line_back;
	int my_position = 0;
	private DisplayMetrics dm;
	TextView txt_null, title;
	boolean tuwen=false;
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

	String img_path = "";

	private void initView() {
		title = (TextView) findViewById(R.id.title);
		title.setText("相册");
		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new ImageGridAdapter(ImageGridActivity.this, dataList,
				mHandler);
		txt_null = (TextView) findViewById(R.id.txt_null);

		if (dataList.size() == 0) {
			txt_null.setVisibility(View.VISIBLE);
		} else {
			txt_null.setVisibility(View.GONE);
		}
		gridView.setAdapter(adapter);

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					Log.v("ceshi", "dataList.get(position).imagePath:"
							+ dataList.get(position).imagePath);
					img_path = compress(dataList.get(position).imagePath);
				} catch (Exception e) {
					// TODO: handle exception
					Toast.makeText(ImageGridActivity.this, "获取图片失败", 0).show();
					return;
				}
				try {
					if (tuwen==true) {
						Log.v("ceshi", "otrue");
						if (addnext==true) {
							txtandpicBean bean = new txtandpicBean();
							bean.setPic(img_path);
							bean.setTxt("");

							AddActivity2.tuwenList.add(my_position + 1, bean);
						}else{
							AddActivity2.tuwenList.get(my_position).setPic(img_path);
						}
					}else{
						AddNewsActivity.channel_list.get(my_position)
						.setFieldcontext(img_path);
					}
					
				} catch (Exception e) {
					// TODO: handle exception
					try {
						MyNewsDetailActivity.channel_list.get(my_position)
								.setFieldcontext(img_path);
						
					} catch (Exception e2) {
						// TODO: handle exception
						Toast.makeText(ImageGridActivity.this, "获取图片失败", 0)
								.show();
					}
				}
				finish();
			}

		});

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
		while (baos.toByteArray().length > 100 * 1024) {
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
}
