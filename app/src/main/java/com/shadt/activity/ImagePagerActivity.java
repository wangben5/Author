package com.shadt.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shadt.bean.txtandpicBean;
import com.shadt.fragment.ImageDetailFragment;
import com.shadt.imageutil.HackyViewPager;
import com.shadt.caibian_news.R;
import com.shadt.util.OtherFinals;

public class ImagePagerActivity extends FragmentActivity implements
		OnClickListener {
	private static final String STATE_POSITION = "STATE_POSITION";
	public static final String EXTRA_IMAGE_INDEX = "position1";
	public static final String EXTRA_IMAGE_URLS = "image_urls";
	private HackyViewPager mPager;
	private int pagerPosition;
	private TextView indicator, txt_choose;
	LinearLayout chose, line_back;
	int img_num = 0;
	ImageView img_choose;
	LinearLayout btn_ok;
	String class_name = "";
	private ArrayList<txtandpicBean> Arraylist;
	private int my_position;
	private int position0;
	boolean is_add = true; // 判断是添加还是修改图片
	private String canshu = "";
	private DisplayMetrics dm;
	boolean tupian_caijian = false;
	private AlertDialog mAlertDialog;
	private TextView tip_msg;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_detail_pager);
		init_dialog();
		Intent it = getIntent();
		pagerPosition = it.getIntExtra(EXTRA_IMAGE_INDEX, 0);
		img_num = it.getIntExtra("num", 100);
		class_name = it.getStringExtra("class_name");
		my_position = it.getIntExtra("position", 0);
		position0 = it.getIntExtra("position0", 101);
		Log.v("ceshi2", "my_position" + position0);
		img_num = it.getIntExtra("num", 100);
		is_add = it.getBooleanExtra("add", true);
		canshu = it.getStringExtra("canshu");
		class_name = it.getStringExtra("class_name");
		mPager = (HackyViewPager) findViewById(R.id.pager);
		ImagePagerAdapter mAdapter = new ImagePagerAdapter(
				getSupportFragmentManager(), ImggridActivity.mImgs);
		mPager.setAdapter(mAdapter);
		txt_choose = (TextView) findViewById(R.id.txt_choose);
		txt_choose.setText("选择图片(" + ImggridActivity.mSelectedImage.size()
				+ "/" + img_num + ")");
		indicator = (TextView) findViewById(R.id.txt_sel_num);
		chose = (LinearLayout) findViewById(R.id.chose);
		chose.setOnClickListener(this);
		btn_ok = (LinearLayout) findViewById(R.id.btn_ok);
		btn_ok.setOnClickListener(this);
		img_choose = (ImageView) findViewById(R.id.img_choose);
		CharSequence text = getString(R.string.viewpager_indicator, 1, mPager
				.getAdapter().getCount());
		indicator.setText(text);
		line_back = (LinearLayout) findViewById(R.id.line_back);
		line_back.setOnClickListener(this);
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
				CharSequence text = getString(R.string.viewpager_indicator,
						arg0 + 1, mPager.getAdapter().getCount());
				indicator.setText(text);
				pagerPosition = arg0;
				if (ImggridActivity.mSelectedImage
						.contains(ImggridActivity.mImgDir.getAbsolutePath()
								+ "/" + ImggridActivity.mImgs.get(arg0))) {
					img_choose.setImageResource(R.drawable.img_choose);
				} else {
					img_choose.setImageResource(R.drawable.img_choose_no);
				}
			}

		});
		if (savedInstanceState != null) {
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}

		mPager.setCurrentItem(pagerPosition);
		if (ImggridActivity.mSelectedImage.contains(ImggridActivity.mImgDir
				.getAbsolutePath()
				+ "/"
				+ ImggridActivity.mImgs.get(pagerPosition))) {
			img_choose.setImageResource(R.drawable.img_choose);
		} else {
			img_choose.setImageResource(R.drawable.img_choose_no);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, mPager.getCurrentItem());
	}

	private class ImagePagerAdapter extends FragmentStatePagerAdapter {

		public List<String> fileList;

		public ImagePagerAdapter(FragmentManager fm, List<String> fileList) {
			super(fm);
			this.fileList = fileList;
		}

		@Override
		public int getCount() {
			return fileList == null ? 0 : fileList.size();
		}

		@Override
		public Fragment getItem(int position) {
			String url = fileList.get(position);
			return ImageDetailFragment.newInstance(url);
		}
	}

	private int daoru_num = 1;
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (msg.what == 2) {
				mAlertDialog.show();
			} else if (msg.what == 3) {
				tip_msg.setText("正在导入第" + daoru_num + "张图片");
			}
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.chose:

			if (ImggridActivity.mSelectedImage.contains(ImggridActivity.mImgDir
					.getAbsolutePath()
					+ "/"
					+ ImggridActivity.mImgs.get(pagerPosition))) {
				ImggridActivity.mSelectedImage.remove(ImggridActivity.mImgDir
						.getAbsolutePath()
						+ "/"
						+ ImggridActivity.mImgs.get(pagerPosition));
				img_choose.setImageResource(R.drawable.img_choose_no);
			} else
			// 未选择该图片
			{
				if (ImggridActivity.mSelectedImage.size() >= img_num) {
					Toast.makeText(ImagePagerActivity.this,
							"最多选择" + img_num + "张图片", 0).show();
				} else {
					img_choose.setImageResource(R.drawable.img_choose);
					ImggridActivity.mSelectedImage.add(ImggridActivity.mImgDir
							.getAbsolutePath()
							+ "/"
							+ ImggridActivity.mImgs.get(pagerPosition));
				}
			}
			txt_choose.setText("选择图片(" + ImggridActivity.mSelectedImage.size()
					+ "/" + img_num + ")");
			break;
		case R.id.btn_ok:
			new Thread(networkTask_tijiao).start();
			break;
		case R.id.line_back:
			finish();
			break;
		default:
			break;
		}
	}

	Runnable networkTask_tijiao = new Runnable() {

		@Override
		public void run() {
			// TODO
			// 在这里进行 http request.网络请求相关操作
			Looper.prepare();
			do_picture();
			Looper.loop();
		}
	};

	private void do_picture() {
		// TODO Auto-generated method stub
		if (ImggridActivity.mSelectedImage.size() == 0) {
			Toast.makeText(ImagePagerActivity.this, "至少选择一张图片", 0).show();
		} else {
			if (!TextUtils.isEmpty(class_name)) {
				mHandler.sendEmptyMessage(2);
				if (class_name.equals(AddNewsActivity.class.getName())) {
					if (img_num == 100) {
						// 第一次进来 添加图片
						Arraylist = new ArrayList<txtandpicBean>();
						if (ImggridActivity.mSelectedImage.size() == 1) {
							txtandpicBean bean = new txtandpicBean();
							String s = compress(ImggridActivity.mSelectedImage
									.get(0));
							if (TextUtils.isEmpty(s)) {
								Toast.makeText(ImagePagerActivity.this,
										"图片读取出错", 0).show();
							} else {
								bean.setPic(s);
								bean.setTxt("");
								Arraylist.add(my_position, bean);
							}
						} else {
							for (int i = 0; i < ImggridActivity.mSelectedImage
									.size(); i++) {
								daoru_num = i + 1;
								mHandler.sendEmptyMessage(3);
								txtandpicBean bean = new txtandpicBean();
								String s = compress(ImggridActivity.mSelectedImage
										.get(i));
								if (TextUtils.isEmpty(s)) {
									my_position = my_position - 1;
								} else {
									bean.setPic(s);
									bean.setTxt("");
									Arraylist.add(my_position + i, bean);

								}
							}

						}
						if (Arraylist.size() != 0) {
							Intent it = new Intent(ImagePagerActivity.this,
									AddActivity2.class);
							Bundle bundle = new Bundle();
							bundle.putSerializable("array",
									(Serializable) Arraylist);
							bundle.putString("canshu", canshu);
							// AddnewsAcitivty 设置内容的位置
							bundle.putInt("position0", position0);
							// 这个位置是添加图片的位置
							bundle.putInt("position", my_position);
							it.putExtra("class_name",
									ImggridActivity.class.getName());
							it.putExtras(bundle);
							startActivity(it);
						} else {

						}
						finish();
						finishbefore();
					} else {
						// 更改图片
						String s = compress(ImggridActivity.mSelectedImage
								.get(0));
						if (TextUtils.isEmpty(s)) {
							Toast.makeText(ImagePagerActivity.this, "图片读取出错", 0)
									.show();
						} else {
							AddNewsActivity.channel_list.get(my_position)
									.setFieldcontext(s);
						}
						finish();
						finishbefore();
					}
				} else if (class_name.equals(AddActivity2.class.getName())) {
					// 图文混编更换图片
					if (is_add == false) {
						txtandpicBean bean = new txtandpicBean();

						String s = compress(ImggridActivity.mSelectedImage
								.get(0));
						if (TextUtils.isEmpty(s)) {
							Toast.makeText(ImagePagerActivity.this, "图片读取出错", 0)
									.show();
						} else {
							bean.setTxt(""
									+ AddActivity2.tuwenList.get(my_position)
											.getTxt());
							bean.setPic(s);
							AddActivity2.tuwenList.set(my_position, bean);
						}

					} else {
						// 图文混编添加图片栏目
						if (ImggridActivity.mSelectedImage.size() == 1) {
							txtandpicBean bean = new txtandpicBean();
							String s = compress(ImggridActivity.mSelectedImage
									.get(0));
							if (TextUtils.isEmpty(s)) {
								Toast.makeText(ImagePagerActivity.this,
										"图片读取出错", 0).show();
							} else {
								bean.setPic(s);
								bean.setTxt("");
								AddActivity2.tuwenList.add(my_position + 1,
										bean);
							}

						} else {
							for (int i = 0; i < ImggridActivity.mSelectedImage
									.size(); i++) {
								daoru_num = i + 1;
								mHandler.sendEmptyMessage(3);
								txtandpicBean bean = new txtandpicBean();

								String s = compress(ImggridActivity.mSelectedImage
										.get(i));
								if (TextUtils.isEmpty(s)) {
									my_position = my_position - 1;
								} else {
									bean.setPic(s);
									bean.setTxt("");
									AddActivity2.tuwenList.add(my_position + 1
											+ i, bean);
								}
							}

						}
					}
					finish();
					finishbefore();
				} else if (class_name.equals(PictrueActivity.class.getName())) {
					String s = compress(ImggridActivity.mSelectedImage.get(0));
					if (TextUtils.isEmpty(s)) {
						Toast.makeText(ImagePagerActivity.this, "图片读取出错", 0)
								.show();
					} else {
						PictrueActivity.img_path = s;
					}

					finish();
					finishbefore();
				} else if (class_name.equals(MyNewsDetailActivity.class
						.getName())) {
					// 我的详情 可以添加图片 可以更改 100 添加
					if (img_num == 100) {
						tupian_caijian = true;
						Arraylist = new ArrayList<txtandpicBean>();
						my_position = 0;
						if (ImggridActivity.mSelectedImage.size() == 1) {
							txtandpicBean bean = new txtandpicBean();
							String s = compress(ImggridActivity.mSelectedImage
									.get(0));
							if (TextUtils.isEmpty(s)) {
								Toast.makeText(ImagePagerActivity.this,
										"图片读取出错", 0).show();
							} else {
								bean.setPic(s);
								bean.setTxt("");
								Arraylist.add(my_position + 1, bean);
							}

						} else {
							for (int i = 0; i < ImggridActivity.mSelectedImage
									.size(); i++) {
								daoru_num = i + 1;
								mHandler.sendEmptyMessage(3);
								txtandpicBean bean = new txtandpicBean();
								String s = compress(ImggridActivity.mSelectedImage
										.get(i));
								if (TextUtils.isEmpty(s)) {
									my_position = my_position - 1;
								} else {
									bean.setPic(s);
									bean.setTxt("");
									Arraylist.add(my_position + i, bean);

								}
							}

						}
						if (Arraylist.size() != 0) {

							Intent it = new Intent(ImagePagerActivity.this,
									AddActivity2.class);
							Bundle bundle = new Bundle();
							bundle.putSerializable("array",
									(Serializable) Arraylist);
							bundle.putString("canshu", canshu);
							// AddnewsAcitivty 设置内容的位置
							bundle.putInt("position0", position0);
							// 这个位置是添加图片的位置
							bundle.putInt("position", my_position);
							it.putExtra("class_name",
									ImggridActivity.class.getName());
							it.putExtras(bundle);
							startActivity(it);
							finish();
							finishbefore();
						} else {

						}
					} else {
						String s = compress(ImggridActivity.mSelectedImage
								.get(0));
						if (TextUtils.isEmpty(s)) {
							Toast.makeText(ImagePagerActivity.this, "图片读取出错", 0)
									.show();
						} else {
							MyNewsDetailActivity.channel_list.get(my_position)
									.setFieldcontext(s);
						}

						finish();
						finishbefore();
					}
				}
			}
		}
	}

	@SuppressLint("NewApi")
	public void init_dialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(ImagePagerActivity.this,
						R.style.Theme_Transparent));
		mAlertDialog = builder.create();
		LayoutInflater inflater = (LayoutInflater) ImagePagerActivity.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.prodialog, null);
		mAlertDialog.setView(view, 0, 0, 0, 0);
		mAlertDialog.setCancelable(false);
		// dialog.show();
		tip_msg = (TextView) view.findViewById(R.id.tips_loading_msg);
	}

	public void finishbefore() {
		Intent it = new Intent("finish");
		sendBroadcast(it);
	}

	@SuppressLint("NewApi")
	public void send_broadcast(int i, boolean is_finish) {
		Intent brodcast = new Intent("recevie");
		brodcast.putExtra("num", i);
		brodcast.putExtra("finish", is_finish);
		sendBroadcast(brodcast);
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
		try {
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);

		} catch (Exception e) {
			// TODO: handle exception
			return "";
		}
		SharedPreferences preferences = getSharedPreferences("user",
				Context.MODE_PRIVATE);
		String token = preferences.getString("vsOutData3", "");
		int pic_size = Integer.parseInt(token);
		while (baos.toByteArray().length > pic_size * 1024) {
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