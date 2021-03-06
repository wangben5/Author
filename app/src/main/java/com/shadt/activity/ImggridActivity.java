package com.shadt.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.shadt.activity.ListImageDirPopupWindow.OnImageDirSelected;
import com.shadt.adpter.CommonAdapter;
import com.shadt.adpter.ViewHolder;
import com.shadt.bean.ImageFloder;
import com.shadt.bean.txtandpicBean;
import com.shadt.caibian_news.R;
import com.shadt.util.OtherFinals;

public class ImggridActivity extends BaseActivity implements OnImageDirSelected {

	/**
	 * 存储文件夹中的图片数量
	 */
	private int mPicsSize;
	/**
	 * 图片数量最多的文件夹
	 */
	public static File mImgDir;
	/**
	 * 所有的图片
	 */
	public static List<String> mImgs;
	public static List<String> mSelectedImage;

	private GridView mGirdView;
	private MyAdapter mAdapter;
	/**
	 * 临时的辅助类，用于防止同一个文件夹的多次扫描
	 */
	private HashSet<String> mDirPaths = new HashSet<String>();

	/**
	 * 扫描拿到所有的图片文件夹
	 */
	private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();
	private RelativeLayout mBottomLy;
	private TextView mChooseDir;
	private TextView mImageCount;
	int totalCount = 0;
	private int mScreenHeight;
	private ListImageDirPopupWindow mListImageDirPopupWindow;
	boolean is_add = true; // 判断是添加还是修改图片
	int daoru_num = 1;
	boolean is_show = false;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 2) {
				is_show = true;
				if (mAlertDialog==null) {
					init_dialog();
				}
				mAlertDialog.show();

			} else if (msg.what == 3) {
				tip_msg.setText("正在导入第" + daoru_num + "张图片");
			} else {
				hidedialogs();
				// 为View绑定数据
				data2View();
				// 初始化展示文件夹的popupWindw
				initListDirPopupWindw();
			}
		}
	};
	private LinearLayout line_back;

	private View btn_ok;

	private TextView txt_choose;
	// my_position add2 添加图片的开始位置
	private int my_position, img_num = 0;
	String canshu = "";
	// 记录 AddNewsAcitivy添加图文的 位置
	int position0 = 0;
	String class_name = "";
	private AlertDialog mAlertDialog;
	TextView tip_msg;

	/**
	 * 为View绑定数据
	 */
	private void data2View() {
		if (mImgDir == null) {
			Toast.makeText(getApplicationContext(), "一张图片没扫描到",
					Toast.LENGTH_SHORT).show();
			return;
		}

		mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".jpg") || filename.endsWith(".png")
						|| filename.endsWith(".jpeg"))
					return true;
				return false;
			}
		}));
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdapter = new MyAdapter(getApplicationContext(), mImgs,
				R.layout.grid_item, mImgDir.getAbsolutePath());
		mGirdView.setAdapter(mAdapter);
		mImageCount.setText(mImgs.size() + "张");
		mChooseDir.setText(mImgDir.getName());
	};
	
	/**
	 * 初始化展示文件夹的popupWindw
	 */
	private void initListDirPopupWindw() {
		mListImageDirPopupWindow = new ListImageDirPopupWindow(
				LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
				mImageFloders, LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.list_dir, null));

		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		// 设置选择文件夹的回调
		mListImageDirPopupWindow.setOnImageDirSelected(this);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gridview_pic);
		init_dialog();
		bitmaputil = new BitmapUtils(this);

		mSelectedImage = new ArrayList<String>();
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		mScreenHeight = outMetrics.heightPixels;
		Intent it = getIntent();
		my_position = it.getIntExtra("position", 0);
		img_num = it.getIntExtra("num", 100);
		is_add = it.getBooleanExtra("add", true);
		canshu = it.getStringExtra("canshu");
		class_name = it.getStringExtra("class_name");
		initView();
		getImages();
		initEvent();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("finish"); // 为BroadcastReceiver指定action，使之用于接收同action的广播
		registerReceiver(myReceiver, intentFilter);
	}

	@SuppressLint("NewApi")
	public void init_dialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(ImggridActivity.this,
						R.style.Theme_Transparent));
		mAlertDialog = builder.create();
		LayoutInflater inflater = (LayoutInflater) ImggridActivity.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.prodialog, null);
		mAlertDialog.setView(view, 0, 0, 0, 0);
		mAlertDialog.setCanceledOnTouchOutside(false);
		mAlertDialog.setCancelable(false);
		tip_msg = (TextView) view.findViewById(R.id.tips_loading_msg);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
	 */
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
			return;
		}
		// 显示进度条
		try {
			showdialog();
		} catch (Exception e) {
			// TODO: handle exception
		}

		new Thread(new Runnable() {
			@Override
			public void run() {

				String firstImage = null;
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = ImggridActivity.this
						.getContentResolver();

				// 只查询jpeg和png的图片
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" },
						MediaStore.Images.Media.DATE_MODIFIED);

				while (mCursor.moveToNext()) {
					// 获取图片的路径
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));

					// Log.e("TAG", path);
					// 拿到第一张图片的路径
					if (firstImage == null)
						firstImage = path;
					// 获取该图片的父路径名
					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dirPath = parentFile.getAbsolutePath();
					ImageFloder imageFloder = null;
					// 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
					if (mDirPaths.contains(dirPath)) {
						continue;
					} else {
						mDirPaths.add(dirPath);
						// 初始化imageFloder
						imageFloder = new ImageFloder();
						imageFloder.setDir(dirPath);
						imageFloder.setFirstImagePath(path);
					}

					int picSize = parentFile.list(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String filename) {
							if (filename.endsWith(".jpg")
									|| filename.endsWith(".png")
									|| filename.endsWith(".jpeg"))
								return true;
							return false;
						}
					}).length;
					totalCount += picSize;
					imageFloder.setCount(picSize);
					mImageFloders.add(imageFloder);
					if (picSize > mPicsSize) {
						mPicsSize = picSize;
						mImgDir = parentFile;
					}
				}
				mCursor.close();
				// 扫描完成，辅助的HashSet也就可以释放内存了
				mDirPaths = null;
				// 通知Handler扫描图片完成
				mHandler.sendEmptyMessage(0x110);
			}
		}).start();

	}

	public static List<txtandpicBean> Arraylist;
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
	boolean tupian_caijian = false;

	/**
	 * 初始化View
	 */
	private void initView() {
		mGirdView = (GridView) findViewById(R.id.id_gridView);
		mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
		mImageCount = (TextView) findViewById(R.id.id_total_count);
		mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);
		txt_choose = (TextView) findViewById(R.id.txt_choose);
		btn_ok = (LinearLayout) findViewById(R.id.btn_ok);
		btn_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new Thread(networkTask_tijiao).start();
			}
		});
		line_back = (LinearLayout) findViewById(R.id.line_back);
		line_back.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});
	}

	public void do_picture() {

		// TODO Auto-generated method stub

		if (mSelectedImage.size() == 0) {
			Toast.makeText(ImggridActivity.this, "至少选择一张图片", 0).show();
		} else {
			mHandler.sendEmptyMessage(2);
			if (TextUtils.isEmpty(class_name)) {
				if (img_num == 100) {
					// 第一次进来 添加图片
					tupian_caijian = true;
					Arraylist = new ArrayList<txtandpicBean>();
					my_position = 0;
					if (mSelectedImage.size() == 1) {
						txtandpicBean bean = new txtandpicBean();
						String s = compress(mSelectedImage.get(0));
						if (TextUtils.isEmpty(s)) {
							Toast.makeText(ImggridActivity.this, "图片读取出错", 0)
									.show();
						} else {
							bean.setPic(s);
							bean.setTxt("");
							Arraylist.add(my_position, bean);
						}
					} else {
						for (int i = 0; i < mSelectedImage.size(); i++) {
							daoru_num = i + 1;
							mHandler.sendEmptyMessage(3);
							txtandpicBean bean = new txtandpicBean();
							String s = compress(mSelectedImage.get(i));
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
						Intent it = new Intent(ImggridActivity.this,
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
				}
			} else {

				if (class_name.equals(AddNewsActivity.class.getName())) {
					if (img_num == 100) {
						// 第一次进来 添加图片
						tupian_caijian = true;
						Arraylist = new ArrayList<txtandpicBean>();
						my_position = 0;
						if (mSelectedImage.size() == 1) {
							txtandpicBean bean = new txtandpicBean();
							String s = compress(mSelectedImage.get(0));
							if (TextUtils.isEmpty(s)) {
								Toast.makeText(ImggridActivity.this, "图片读取出错",
										0).show();
							} else {
								bean.setPic(s);
								bean.setTxt("");
								Arraylist.add(my_position, bean);
							}

						} else {

							for (int i = 0; i < mSelectedImage.size(); i++) {
								daoru_num = i + 1;
								mHandler.sendEmptyMessage(3);
								txtandpicBean bean = new txtandpicBean();
								String s = compress(mSelectedImage.get(i));
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
							Intent it = new Intent(ImggridActivity.this,
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
					} else {
						// 更改图片
						String s = compress(mSelectedImage.get(0));
						if (TextUtils.isEmpty(s)) {
							Toast.makeText(ImggridActivity.this, "图片读取出错", 0)
									.show();
						} else {
							AddNewsActivity.channel_list.get(my_position)
									.setFieldcontext(s);
						}
						finish();
					}
				} else if (class_name.equals(AddActivity2.class.getName())) {
					// 图文混编更换图片
					if (is_add == false) {

						txtandpicBean bean = new txtandpicBean();
						String s = compress(mSelectedImage.get(0));
						if (TextUtils.isEmpty(s)) {
							Toast.makeText(ImggridActivity.this, "图片读取出错", 0)
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
						if (mSelectedImage.size() == 1) {
							txtandpicBean bean = new txtandpicBean();
							String s = compress(mSelectedImage.get(0));
							if (TextUtils.isEmpty(s)) {
								Toast.makeText(ImggridActivity.this, "图片读取出错",
										0).show();
							} else {
								bean.setPic(s);
								bean.setTxt("");
								AddActivity2.tuwenList.add(my_position + 1,
										bean);
							}
						} else {

							for (int i = 0; i < mSelectedImage.size(); i++) {
								daoru_num = i + 1;
								mHandler.sendEmptyMessage(3);
								txtandpicBean bean = new txtandpicBean();
								String s = compress(mSelectedImage.get(i));
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
				} else if (class_name.equals(PictrueActivity.class.getName())) {
					String s = compress(mSelectedImage.get(0));
					if (TextUtils.isEmpty(s)) {
						Toast.makeText(ImggridActivity.this, "图片读取出错", 0)
								.show();

					} else {
						PictrueActivity.img_path = s;
					}

					finish();
				} else if (class_name.equals(MyNewsDetailActivity.class
						.getName())) {
					if (img_num == 100) {
						tupian_caijian = true;
						Arraylist = new ArrayList<txtandpicBean>();
						my_position = 0;
						if (mSelectedImage.size() == 1) {
							txtandpicBean bean = new txtandpicBean();
							String s = compress(mSelectedImage.get(0));
							if (TextUtils.isEmpty(s)) {
								Toast.makeText(ImggridActivity.this, "图片读取出错",
										0).show();
								finish();
							} else {

								bean.setPic(s);
								bean.setTxt("");
								Arraylist.add(my_position, bean);
							}

						} else {

							for (int i = 0; i < mSelectedImage.size(); i++) {

								daoru_num = i + 1;
								mHandler.sendEmptyMessage(3);
								txtandpicBean bean = new txtandpicBean();
								String s = compress(mSelectedImage.get(i));
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
							Intent it = new Intent(ImggridActivity.this,
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

					} else {
						String s = compress(mSelectedImage.get(0));
						if (TextUtils.isEmpty(s)) {
							Toast.makeText(ImggridActivity.this, "图片读取出错", 0)
									.show();
						} else {

							MyNewsDetailActivity.channel_list.get(my_position)
									.setFieldcontext(compress(s));
						}
						finish();
					}
				}
			}
		}

	}

	private void initEvent() {
		/**
		 * 为底部的布局设置点击事件，弹出popupWindow
		 */
		mBottomLy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListImageDirPopupWindow
						.setAnimationStyle(R.style.anim_popup_dir);
				mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

				// 设置背景颜色变暗
				// WindowManager.LayoutParams lp = getWindow().getAttributes();
				// lp.alpha = .3f;
				// getWindow().setAttributes(lp);
			}
		});
	}

	@Override
	public void selected(ImageFloder floder) {

		mImgDir = new File(floder.getDir());
		mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".jpg") || filename.endsWith(".png")
						|| filename.endsWith(".jpeg"))
					return true;
				return false;
			}
		}));
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdapter = new MyAdapter(getApplicationContext(), mImgs,
				R.layout.grid_item, mImgDir.getAbsolutePath());
		mGirdView.setAdapter(mAdapter);
		// mAdapter.notifyDataSetChanged();
		mImageCount.setText(floder.getCount() + "张");
		mChooseDir.setText(floder.getName());
		mListImageDirPopupWindow.dismiss();
	}

	private DisplayMetrics dm;
	BitmapUtils bitmaputil;

	public class MyAdapter extends CommonAdapter<String> {

		/**
		 * 用户选择的图片，存储为图片的完整路径
		 */

		/**
		 * 文件夹路径
		 */
		private String mDirPath;

		public MyAdapter(Context context, List<String> mDatas,
				int itemLayoutId, String dirPath) {
			super(context, mDatas, itemLayoutId);
			this.mDirPath = dirPath;
		}
		@Override
		public void convert(final ViewHolder helper,
				final String item) {
			// 设置no_pic
			helper.setImageResource(R.id.id_item_image, R.drawable.pictures_no);
			// 设置no_selected
			helper.setImageResource(R.id.id_item_select,
					R.drawable.picture_unselected);
			helper.setImageByUrl(R.id.id_item_image, mDirPath + "/" + item);
			final ImageView mImageView = helper.getView(R.id.id_item_image);
//			bitmaputil.display(mImageView, mDirPath + "/" + item);
			
			// 设置图片
			final ImageView mSelect = helper.getView(R.id.id_item_select);
			mImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent it = new Intent(mContext, ImagePagerActivity.class);
					it.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX,
							helper.getPosition());
					Log.v("ceshi2", "my_position：" + my_position);
					it.putExtra("num", img_num);// 能选择多少张图片
					it.putExtra("class_name", class_name);
					it.putExtra("canshu", canshu);
					it.putExtra("position", my_position);
					it.putExtra("is_add", is_add);
					startActivity(it);
				}
			});
			mSelect.setColorFilter(null);
			// 设置ImageView的点击事件
			mSelect.setOnClickListener(new OnClickListener() {
				// 选择，则将图片变暗，反之则反之
				@Override
				public void onClick(View v) {

					// 已经选择过该图片
					if (mSelectedImage.contains(mDirPath + "/" + item)) {
						Log.v("ceshi", "选择了mDirPath + \"/\" + item:" + mDirPath
								+ "/" + item);
						mSelectedImage.remove(mDirPath + "/" + item);
						mSelect.setImageResource(R.drawable.picture_unselected);
						mImageView.setColorFilter(null);
					} else
					// 未选择该图片
					{
						if (mSelectedImage.size() >= img_num) {
							Toast.makeText(mContext, "最多选择" + img_num + "张图片",
									0).show();
						} else {
							mSelectedImage.add(mDirPath + "/" + item);
							mSelect.setImageResource(R.drawable.pictures_selected);
						}
					}
					txt_choose.setText("选择图片(" + mSelectedImage.size() + "/"
							+ img_num + ")");
				}
			});
			txt_choose.setText("选择图片(" + mSelectedImage.size() + "/" + img_num
					+ ")");
			/**
			 * 已经选择过的图片，显示出选择过的效果
			 */
			if (mSelectedImage.contains(mDirPath + "/" + item)) {
				mSelect.setImageResource(R.drawable.pictures_selected);
			}
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
		try {
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);

		} catch (Exception e) {
			// TODO: handle exception
			return "";
		}
		int pic_size = Integer.parseInt(get_sharePreferences_pic_width());
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

	@Override
	public void initPages() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClickListener(View v) {
		// TODO Auto-generated method stub

	}

	private BroadcastReceiver myReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}

	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mImgs = null;
		mAdapter = null;
		mSelectedImage = null;
		mAlertDialog=null;
		unregisterReceiver(myReceiver);
	}
}
