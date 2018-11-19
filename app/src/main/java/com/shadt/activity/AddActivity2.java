package com.shadt.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.RecyclerListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.http.client.multipart.MultipartEntity;
import com.lidroid.xutils.http.client.multipart.content.FileBody;
import com.shadt.adpter.CustomAdapter;
import com.shadt.adpter.CustomAdapter.OnItemPressedListener;
import com.shadt.bean.txtandpicBean;
import com.shadt.caibian_news.R;
import com.shadt.util.Contacts;
import com.shadt.util.Html_Save;
import com.shadt.util.XMLParserUtil;

public class AddActivity2 extends BaseActivity {
	Context context;
	public static List<txtandpicBean> tuwenList;
	public static List<txtandpicBean> tuwenList2;

	String html_start = "<!DOCTYPE html>\n" + "<html lang=\"en\">\n"
			+ "<head>\n" + "<meta charset=\"UTF-8\">\n"
			+ "<title>Document</title>\n" + "</head>\n" + "<body>\n";
	String html_end = "</body>\n" + "</html>\n";
	// 图片
	String img_path1 = null, img_path2 = null, pic_name = null; // 储存照相一张原图片
	private static final int TAKE_PICTURE = 0;
	private DisplayMetrics dm;
	// 存储每个item 视图 来实现动画效果。
	Map<String, Object> map_list = new HashMap<String, Object>();
	LinearLayout btn_ok;
	String canshu = "";
	BitmapUtils xbitmap;
	// addnewsactivity 图文混编的添加位置内容
	int postion0 = 0;
	String html_content = "", class_name = "";
	TextView txt_ok;
	
	
	private static final String TAG = "MainActivity";
	private static final String KEY_LAYOUT_MANAGER = "layoutManager";

	private enum LayoutManagerType {
		GRID_LAYOUT_MANAGER, LINEAR_LAYOUT_MANAGER
	}

	private RadioButton mLinearlayoutButton;
	private RadioButton mGridLayoutButton;

	private RecyclerView mRecyclerView;
	private CustomAdapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private LayoutManagerType mCurrentLayoutManagerType;
	// 网格布局中的设置列数
	private static int SpanCount = 3;
	public static int onclick_position=0;
	//判断 pictrue  页面 是否删除  
	public static boolean is_delete=false; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context=AddActivity2.this;
		xbitmap = new BitmapUtils(AddActivity2.this);
		setContentView(R.layout.activity_add2);
		Intent it = getIntent();
		html_content = it.getStringExtra("html");
		class_name = it.getStringExtra("class_name");
		Bundle bundle = getIntent().getExtras();
		tuwenList = (List<txtandpicBean>) bundle.get("array");
		canshu = bundle.getString("canshu");
		postion0 = bundle.getInt("postion0");
		initPages();
		initViews(savedInstanceState);
		init_dialog();
	}
	private void initViews(Bundle savedInstanceState) {
	
		mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
		if (savedInstanceState != null) {
			mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
					.getSerializable(KEY_LAYOUT_MANAGER);
			Log.i(TAG, "mCurrentLayoutManagerType=" + mCurrentLayoutManagerType);
		}
		
		mRecyclerView = (RecyclerView) findViewById(R.id.list);
		setRecyclerViewManagerType(mCurrentLayoutManagerType);
		if (TextUtils.isEmpty(html_content)) {
			mAdapter = new CustomAdapter(tuwenList,context,get_sharePreferences_fuwuqi());
			mRecyclerView.setAdapter(mAdapter);
		} else {
			if (!TextUtils.isEmpty(class_name)) {
				if (class_name.equals(AddNewsActivity.class.getName())) {
					SharedPreferences preferences = getSharedPreferences(
							"user", Context.MODE_PRIVATE);
					parsehtml(preferences.getString("html_native", ""));
				} else {
					parsehtml(html_content);
				}
			} else {

			}
		}
	
		// 设置recycler拥有固定的大小，提高展示效率
		mRecyclerView.setHasFixedSize(true);
		// 设置默认的动画，在移除和添加的效果下展现，现在github上可以找到许多拓展，有兴趣的可以找找
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		// 实现我们给Adapter监听的方法，因为recyclerview没有Listview的OnClick和OnlongClick相似的方法
		mAdapter.setOnItemPressedListener(new OnItemPressedListener() {
			@Override
			public void onItemClick(int position) {
				removeItemByPosition(position);
			}
			@Override
			public boolean OnItemLongClick(int position) {
				// 这里模拟了删除的功能
				 insertItemByPosition(position);
				return true;
			}
		});
		mRecyclerView.setRecyclerListener(new RecyclerListener() {
			// 资源回收后被调用
			@Override
			public void onViewRecycled(ViewHolder viewHolder) {
				CustomAdapter.ViewHolder vh = (CustomAdapter.ViewHolder) viewHolder;
			}
		});
	}


	/**
	 * 可以改变recycler的布局显示方式
	 * 
	 * @param type
	 */
	protected void setRecyclerViewManagerType(LayoutManagerType type) {
		int scrollPosition = 0;
		if (mRecyclerView.getLayoutManager() != null) {
			scrollPosition = ((LinearLayoutManager) mRecyclerView
					.getLayoutManager())
					.findFirstCompletelyVisibleItemPosition();
		}
		switch (type) {
		// 网状
		case GRID_LAYOUT_MANAGER:
			mLayoutManager = new GridLayoutManager(AddActivity2.this, SpanCount);
			mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
			break;
		// 线性，如list
		case LINEAR_LAYOUT_MANAGER:
			mLayoutManager = new LinearLayoutManager(this);
			mLayoutManager.canScrollHorizontally();
			mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
			break;
		default:
			mLayoutManager = new LinearLayoutManager(this);
			mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
			break;
		}
		mRecyclerView.setLayoutManager(mLayoutManager);
		mRecyclerView.scrollToPosition(scrollPosition);
	}

	/**
	 * 通过RecyclerView的adapter来移除指定位置的数据
	 * 
	 * @param position
	 */
	protected void removeItemByPosition(int position) {
			mAdapter.notifyItemRemoved(position);
			tuwenList.remove(position);
			mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
			// 你如果用了这个 ，就没有动画效果了。
			// mAdapter.notifyDataSetChanged();
	}
	
	// 对应这是可以新增指定索引的
	protected void insertItemByPosition(int position) {
		if (mAdapter != null && position > 0) {
				txtandpicBean bean=new txtandpicBean();
				bean.setTxt("possition"+position);
				tuwenList.add(position,bean);
				mAdapter.notifyItemInserted(position);
				mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}
	

	boolean is_edit = true;
	LinearLayout line_back;
	
	public void initPages() {
		// TODO Auto-generated method stub
		btn_ok = (LinearLayout) findViewById(R.id.btn_ok);
		btn_ok.setOnClickListener(this);
		line_back = (LinearLayout) findViewById(R.id.line_back);
		line_back.setOnClickListener(this);
		txt_ok = (TextView) findViewById(R.id.txt_ok);
		context = AddActivity2.this;
	}

	// 解析分解html。
	Runnable loadweb = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Looper.prepare();
			Document doc = Jsoup.parse(html_start + all + html_end);
			web_path = Html_Save.html_save(doc);
			Looper.loop();
		}
	};

	@Override
	public void onClickListener(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_ok:
			handler.sendEmptyMessage(2);
			handler.sendEmptyMessageDelayed(4, 1000);

		
			break;
		case R.id.line_back:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (mAdapter != null) {
//			mAdapter.notifyItemChanged(onclick_position);
//			if (tuwenList.size()>=onclick_position+1) {
//				mAdapter.notifyItemChanged(onclick_position+1);
//			}
			if (is_delete==true) {
				is_delete=false;
//				tuwenList.remove(onclick_position);
				tuwenList.get(onclick_position).setPic("");
				mAdapter.notifyItemChanged(onclick_position);
			}
			mAdapter.notifyDataSetChanged();
		}
//		displayBriefMemory();
	}
	private void displayBriefMemory() {    
        final ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);    
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();   
        activityManager.getMemoryInfo(info);    
        Log.i("ceshi","系统剩余内存:"+(info.availMem >> 10)+"k");   
        Log.i("ceshi","系统是否处于低内存运行："+info.lowMemory);
        Log.i("ceshi","当系统剩余内存低于"+info.threshold+"时就看成低内存运行");
    } 

	int take_photo_position = 0; // 定义照相的 位置

	
	/**
	 * 从相册选
	 */
	boolean tuwen = true;

	private void selectFromAlbum(int p, boolean addnext, int num) {
		Intent it = new Intent(context, ImggridActivity.class);
		it.putExtra("type", tuwen);
		it.putExtra("position", p);
		it.putExtra("add", addnext);
		it.putExtra("class_name", AddActivity2.class.getName());
		it.putExtra("num", num);
		startActivity(it);
	}

	// 将URI转换为真实路径
	private String changeUriToPath(Uri uri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor actualImageCursor = managedQuery(uri, proj, null, null, null);
		int actual_image_column_index = actualImageCursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		actualImageCursor.moveToFirst();
		String currentImagePath = actualImageCursor
				.getString(actual_image_column_index);
		return currentImagePath;
	}

	boolean show_txt_pic_vid = false; // 判斷是否展現 添加 文本圖片視頻


	public static boolean add = false;
	Object val = null, val1 = null;

	@SuppressLint("NewApi")

	Runnable networkTask_tijiao = new Runnable() {

		@Override
		public void run() {
			// TODO
			// 在这里进行 http request.网络请求相关操作
			Looper.prepare();
			try {
				Submit_tuwen_img(get_sharePreferences_fuwuqi()
						+ Contacts.HUNBIAN_DO_PIC);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Looper.loop();
		}
	};
	// 解析分解html。
	String web_path = "";
	Runnable r = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Looper.prepare();
			Document doc = Jsoup.parse(html_start + all + html_end);
			web_path = Html_Save.html_save(doc);
			Intent it = new Intent(context, WebViewActivity.class);
			it.putExtra("url", web_path);
			startActivity(it);
			finish();
			Looper.loop();
		}
	};
	String path = "";
	List<String> listimg;
	int a = 0; // 记录解析的图片添加位置

	// 因为有时候 只有文字 ，没有图片 所以 会找不到 图片对应位置，所以我 把 获取的 图片存入集合，应该有图片的位置我在放上去
	public void parsehtml(String htmlcontent) {
		Document doc = Jsoup.parse(html_start + htmlcontent + html_end);
		tuwenList = new ArrayList<txtandpicBean>();
		web_path = Html_Save.html_save(doc);
		Elements eles = doc.getElementsByTag("div");// 将a标签的列表存储成元素集合
		listimg = new ArrayList<String>();
		Elements pngs = doc.select("img");
		// 遍历元素
		for (Element e : pngs) {
			String src = e.attr("src");// 获取img中的src路径
			listimg.add(src);
		}
		for (Element link : eles) {
			// 获取a标签中href的属性值
			// String linkHref = link.attr("href");
			// //获取a标签中的文本值
			String text = link.toString();
			
			Log.i("OTH", "解析的内容："+ text);
			String title = "", next_str = "";
			if (link.toString().contains("<pre")) {
				title = link.toString().substring(
						link.toString().indexOf("<pre"),
						link.toString().indexOf("</pre>"));
				title = title.substring(title.indexOf(">") + 1, title.length());
				next_str = link.toString().replace(
						link.toString().substring(
								link.toString().indexOf("<pre"),
								link.toString().indexOf("</pre>") + 4), "");
			} else {
				next_str = link.toString();
			}
			String img = "";
			if (next_str.contains("<img")) {
				img = listimg.get(a);
				a = a + 1;
			}
			txtandpicBean bean = new txtandpicBean();
			bean.setTxt(title);
			bean.setPic(img);
			tuwenList.add(bean);
		}
		mAdapter = new CustomAdapter(tuwenList,context,get_sharePreferences_fuwuqi());
		mRecyclerView.setAdapter(mAdapter);
	}


	String s = "", s2 = "";
	String all = "", all2 = "";
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				// 本地html
				for (int i = 0; i < tuwenList.size(); i++) {
					s = s + "<div>\n";
					if (!TextUtils.isEmpty(tuwenList.get(i).getTxt())) {
                        s = s + "<pre style=\"font-size: 40px;word-wrap:break-word ;white-space:pre-wrap;border-style:none;\">"
                                + tuwenList.get(i).getTxt() + "</pre>\n";
					}
					if (!TextUtils.isEmpty(tuwenList.get(i).getPic())) {
						s = s + "<img style=\"width: 100%\" src=\""
								+ tuwenList.get(i).getPic() + "\"/>\n";
					}
					s = s + "</div>\n";
				}
				for (int i = 0; i < tuwenList2.size(); i++) {
					s2 = s2 + "<div>\n";
					if (!TextUtils.isEmpty(tuwenList2.get(i).getTxt())) {
                        s2 = s2 + "<pre style=\"font-size: 15px;word-wrap:break-word ;white-space:pre-wrap;border-style:none;\">"
                                + tuwenList2.get(i).getTxt() + "</pre>\n";
					}
					if (!TextUtils.isEmpty(tuwenList2.get(i).getPic())) {
						if (tuwenList2.get(i).getPic().contains("http")) {
							s2 = s2 + "<img style=\"width: 100%\" src=\""
									+ tuwenList2.get(i).getPic() + "\"/>\n";
						} else {
							s2 = s2 + "<img style=\"width: 100%\" src=\""
									+ get_sharePreferences_fuwuqi()
									+ Contacts.IP + "/"
									+ tuwenList2.get(i).getPic() + "\"/>\n";
						}
					}
					s2 = s2 + "</div>\n";
				}
				all = s;
				s = "";
				all2 = s2;
				s2 = "";
				SharedPreferences preferences = getSharedPreferences("user",
						Context.MODE_PRIVATE);
				Editor editor = preferences.edit();
				editor.putString("html_native", all);
				editor.putString("html_web", all2);
				editor.commit();
				new Thread(r).start();
			}else if(msg.what==2){
				mAlertDialog.show();
			}else if(msg.what==3){
				mAlertDialog.dismiss();
			}else if(msg.what==4){
				new Thread(networkTask_tijiao).start();
			}
		};
	};
	private AlertDialog mAlertDialog;
	private TextView tip_msg;
	// 用来存储 图片 在 listview 的 位置
	public  ArrayList<String> position_item;

	public void Submit_tuwen_img(String url) throws IOException {
		HttpClient httpclient = new DefaultHttpClient();
		try {
			HttpPost httppost = new HttpPost(url);
			MultipartEntity reqEntity = new MultipartEntity();
			position_item=new ArrayList<String>();
			for (int i = 0; i < tuwenList.size(); i++) {
				if (!TextUtils.isEmpty(tuwenList.get(i).getPic())) {
					if (tuwenList.get(i).getPic().contains("/News/image/")) {
						FileBody file = new FileBody(new File(tuwenList.get(i)
								.getPic()));
						reqEntity.addPart(canshu, file);
						position_item.add("" + i);
					}
				}
			}
 			// file2为请求后台的File
			// upload;属性
			httppost.setEntity(reqEntity);

			HttpResponse httpResponse;
			try {
				httpResponse = httpclient.execute(httppost);
				final int statusCode = httpResponse.getStatusLine()
						.getStatusCode();
				//取消dialog
				handler.sendEmptyMessage(3);
				if (statusCode == HttpStatus.SC_OK) {
					// Toast.makeText(AnjianBianjiActivity.this, "上传成功",
					// 0).show();
					String result = EntityUtils.toString(
							httpResponse.getEntity(), "UTF-8");
					Log.v("ceshi2", "result" + result);
					List<String> str;
					str = XMLParserUtil.parse_img(result);
					Log.v("ceshi", "str_size:"+str.size());
					int a = 0;
					tuwenList2 = new ArrayList<txtandpicBean>();
					for (int i = 0; i < tuwenList.size(); i++) {
						txtandpicBean bean = new txtandpicBean();
						bean.setTxt(tuwenList.get(i).getTxt());
						if (!TextUtils.isEmpty(tuwenList.get(i).getPic())) {
							// 是否位置是提交图片的位置，如果是就添加解析出来的图片，如果不是就复制以前的图片

							if (position_item.contains("" + i)) {
								if (str.size() == 0) {
									bean.setPic(tuwenList.get(i).getPic());
								} else {
									Log.v("ceshi", "str.get(examin_main_activity):"+str.get(a));
									bean.setPic(str.get(a));
									a = a + 1;
								}
							} else {
								bean.setPic(tuwenList.get(i).getPic());
							}
						} else {
							bean.setPic("");
						}
						tuwenList2.add(bean);
					}
					handler.sendEmptyMessage(1);
				} else {
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				Log.v("ceshi2", "失败");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				handler.sendEmptyMessage(3);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@SuppressLint("NewApi")
	public void init_dialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(AddActivity2.this, R.style.Theme_Transparent));  
		mAlertDialog = builder.create();  
		LayoutInflater inflater = (LayoutInflater) AddActivity2.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
		View view = inflater.inflate(R.layout.prodialog, null);  
		mAlertDialog.setView(view,0,0,0,0); 
		mAlertDialog.setCanceledOnTouchOutside(false);
		mAlertDialog.setCancelable(false);
		tip_msg = (TextView) view.findViewById(R.id.tips_loading_msg);
		tip_msg.setText("正在同步，请稍后...");
	}
	public void delete_dialog(final View view, final int position) {
		// TODO Auto-generated method stub
		myDialog = new AlertDialog.Builder(context).create();
		myDialog.show();
		myDialog.getWindow().setContentView(R.layout.popu_delete);
		myDialog.getWindow().findViewById(R.id.txt_save_no)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						myDialog.dismiss();
					}
				});
		myDialog.getWindow().findViewById(R.id.txt_save)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						tuwenList.remove(position);
						mAdapter.notifyItemRemoved(position);
						myDialog.dismiss();
					}
				});
	}


}
