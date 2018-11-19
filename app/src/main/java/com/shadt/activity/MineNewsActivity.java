package com.shadt.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.shadt.adpter.ExamineAdapter;
import com.shadt.adpter.PopAdapter;
import com.shadt.bean.ChannelBean;
import com.shadt.bean.ExamineBean;
import com.shadt.bean.PindaoBean;
import com.shadt.bean.ResultBean;
import com.shadt.bean.UserBean;
import com.shadt.caibian_news.R;
import com.shadt.util.Contacts;
import com.shadt.util.Get_Sign;
import com.shadt.util.XMLParserUtil;

public class MineNewsActivity extends BaseActivity {
	int position = 0;
	String channel_id; // 新闻类型channelid
	private ListView context_list;
	private ListView poplist;
	private Animation animation_in, animation_out;
	ExamineAdapter examineAdapter;
	ExamineBean examineBean;
	public static List<ExamineBean> list_examine;
	public static boolean is_update = false;
	TextView txt_null;
	SwipeRefreshLayout swipeRefreshLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my);
		initPages();
		handler.sendEmptyMessage(0);
		handler.sendEmptyMessageDelayed(2, 1000);
	}

	@Override
	public void initPages() {
		// TODO Auto-generated method stub
		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeRefreshLayout.setColorScheme(R.color.holo_blue_bright,
				R.color.holo_green_light, R.color.holo_orange_light,
				R.color.holo_red_light);
		txt_null = (TextView) findViewById(R.id.txt_null);
		swipeRefreshLayout
				.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

					@Override
					public void onRefresh() {
						// 重新刷新页面
						swipeRefreshLayout.postDelayed(new Runnable() {

							@Override
							public void run() {
								handler.sendEmptyMessage(3);
								// 更新数据
							}
						}, 1000);
					}
				});
		context_list = (ListView) findViewById(R.id.list);
		context_list.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (is_show_list == true) {
					hideList();
				} else {

				}
				return false;
			}
		});
		context_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (is_show_list == true) {
					hideList();
				} else {
					Intent it = new Intent(mContext, MyNewsDetailActivity.class);
					it.putExtra("id", list_examine.get(position).getId());
					it.putExtra("key", title_list.get(position_menu).getKey());
					startActivity(it);
				}
			}
		});
		initTop();
	}

	private int position_menu = 0;

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (is_update == true) {
			handler.sendEmptyMessage(0);
			handler.sendEmptyMessageDelayed(2, 1000);
			is_update = false;
		} else {
			if (examineAdapter != null) {
				examineAdapter.notifyDataSetChanged();
			} else {

			}
			if (list_examine != null) {
				if (list_examine.size() == 0) {
					txt_null.setVisibility(View.VISIBLE);
				} else {
					txt_null.setVisibility(View.GONE);
				}
			} else {
				txt_null.setVisibility(View.GONE);
			}
		}
	}

	public void initTop() {
		line_back = (LinearLayout) findViewById(R.id.line_back);
		line_back.setOnClickListener(this);
		title = (TextView) findViewById(R.id.title);
		title.setOnClickListener(this);
		img_xiala = (ImageView) findViewById(R.id.img_xiala);
		poplist = (ListView) findViewById(R.id.pop_list);
		poplist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				hideList();

				if (webutils.isNetworkConnected(mContext)) {
					hideList();
					if (position_menu == position) {

					} else {
						handler.sendEmptyMessage(0);
						popAdapter.setSelectedItem(position);
						handler.sendEmptyMessageDelayed(3, 1000); // 请求获取栏目下列表
					}
					position_menu = position;
					title.setText(title_list.get(position_menu).getTitle());

					// init_channel_data();
				} else {
					Toast.makeText(mContext,
							getResources().getText(R.string.erro_net), 0)
							.show();
				}
			}
		});
		animation_in = AnimationUtils.loadAnimation(mContext,
				R.anim.push_bottom_in2);
		img_xiala.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (is_show_list == true) {
					hideList();
				} else {
					showList();
				}
			}
		});
	}

	boolean is_show_list = false;
	private ImageView img_xiala;

	// 隐藏显示顶部listview
	public void showList() {
		poplist.startAnimation(animation_in);
		poplist.setVisibility(View.VISIBLE);
		is_show_list = true;
		RotateAnimation animation = new RotateAnimation(0, 180,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		animation.setDuration(350);
		animation.setFillAfter(true);
		img_xiala.startAnimation(animation);
	}

	public void hideList() {
		poplist.setVisibility(View.GONE);
		is_show_list = false;
		RotateAnimation animation = new RotateAnimation(180, 0,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		animation.setDuration(350);
		animation.setFillAfter(true);
		img_xiala.startAnimation(animation);

	}

	@Override
	public void onClickListener(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title:
			if (is_show_list == true) {
				hideList();
			} else {
				showList();
			}
			break;
		case R.id.line_back:
			if (is_show_list == true) {
				hideList();
			} else {
				finish();
			}
			break;
		default:
			break;
		}
	}

	Runnable networkTask = new Runnable() {

		@Override
		public void run() {
			// TODO
			// 在这里进行 http request.网络请求相关操作
			Looper.prepare();
			try {
				submit_first();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Looper.loop();
		}
	};
	Runnable networkTask2 = new Runnable() {

		@Override
		public void run() {
			// TODO
			// 在这里进行 http request.网络请求相关操作
			Looper.prepare();
			try {
				if (title_list != null) {

					if (title_list.size() == 0) {
						hidedialogs();
						Toast.makeText(mContext, "获取栏目为空,请联系管理人员", 0).show();

					} else {
						submit_getlist_examine(title_list.get(position_menu)
								.getId());
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Looper.loop();
		}
	};
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				swipeRefreshLayout.setRefreshing(false);
				hidedialogs();
				break;
			case 0:
				showdialog();
				break;
			case 2:
				new Thread(networkTask).start();
				break;
			case 3:
				new Thread(networkTask2).start();
				break;
			case 4:
				txt_null.setVisibility(View.GONE);
				init_channel_data();
				break;
			case 5:
				swipeRefreshLayout.setRefreshing(false);
				hidedialogs();
				examineAdapter = new ExamineAdapter(mContext, list_examine,
						get_sharePreferences_fuwuqi());
				context_list.setAdapter(examineAdapter);
				if (list_examine.size() == 0) {
					txt_null.setVisibility(View.VISIBLE);
				} else {
					txt_null.setVisibility(View.GONE);
				}
				break;
			case 6:
				hidedialogs();
				txt_null.setVisibility(View.VISIBLE);
				break;
			}
		};
	};
	private PopAdapter popAdapter;
	Context mContext = MineNewsActivity.this;

	// 初始化数据
	public void init_channel_data() {
		if (title_list == null) {
		} else {
			if (title_list.size() <= 0) {
				img_xiala.setVisibility(View.GONE);
			} else {
				title.setText(title_list.get(position_menu).getTitle());
				img_xiala.setVisibility(View.VISIBLE);
				popAdapter = new PopAdapter(mContext, title_list);
				popAdapter.setSelectedItem(position_menu);
				poplist.setAdapter(new PopAdapter(mContext, title_list));
				// 执行获取控件布局
			}
		}
	}

	private ResultBean resultBean;
	private List<PindaoBean> title_list;

	public void submit_first() throws IOException {

		Map<String, String> map = new TreeMap<String, String>();

		map.put("username", get_sharePreferences_name());

		String sign = Get_Sign.paixu_map(map, get_sharePreferences_token());
		// TODO Auto-generated method stub
		// 组建xml数据
		StringBuilder xml = new StringBuilder();

		xml.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		xml.append("<xml>");
		for (Map.Entry<String, String> entity : map.entrySet()) {
			String value = entity.getValue();
			xml.append("<" + entity.getKey() + ">" + entity.getValue() + "</"
					+ entity.getKey() + ">");
		}
		xml.append("<sign>" + sign + "</sign>");
		xml.append("</xml>");
		// 实例化一个默认的Http客户端
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(get_sharePreferences_fuwuqi()
				+ Contacts.GET_TYPE);
		httpPost.setHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=utf-8");
		StringEntity entity = new StringEntity(xml.toString());
		httpPost.setEntity(entity);
		HttpResponse httpResponse = client.execute(httpPost);

		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			// 通过HttpEntity获得响应流
			InputStream is = httpResponse.getEntity().getContent();
			// 将流转换成string
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int len;
			while ((len = is.read(buf)) != -1) {
				out.write(buf, 0, len);
			}
			String string = out.toString("UTF-8");
			Log.v("ceshi", "result:" + string);

			is = new ByteArrayInputStream(string.getBytes("UTF-8"));

			try {
				resultBean = new ResultBean();
				resultBean = XMLParserUtil.parse_result(is);
				if (resultBean.getRetrun_code().equals("false")) {
					handler.sendEmptyMessage(6);

					// 返回false，表示失败
				} else {
					is = new ByteArrayInputStream(string.getBytes("UTF-8"));
					title_list = new ArrayList<PindaoBean>();
					title_list = XMLParserUtil.parse_pindao(is);
					handler.sendEmptyMessage(4);
					handler.sendEmptyMessageDelayed(3, 1000);
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {
			Log.v("ceshi", "请求失败");
			handler.sendEmptyMessage(1);

		}
	}

	// 获取审核的新闻列表
	public void submit_getlist_examine(String id) throws IOException {

		Map<String, String> map = new TreeMap<String, String>();
		map.put("username", get_sharePreferences_name());
		map.put("channelId", id);
		String sign = Get_Sign.paixu_map(map, get_sharePreferences_token());
		
		Log.i("OTH", "username:"+get_sharePreferences_name());
		Log.i("OTH", "channelId:"+id);
		Log.i("OTH", "sign:"+sign);
		
		// TODO Auto-generated method stub
		// 组建xml数据
		StringBuilder xml = new StringBuilder();

		xml.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		xml.append("<xml>");
		for (Map.Entry<String, String> entity : map.entrySet()) {
			String value = entity.getValue();
			xml.append("<" + entity.getKey() + ">" + entity.getValue() + "</"
					+ entity.getKey() + ">");
		}
		xml.append("<sign>" + sign + "</sign>");
		xml.append("</xml>");
		Log.v("ceshi", "xml:" + xml.toString());
		// 实例化一个默认的Http客户端
		DefaultHttpClient client = new DefaultHttpClient();
//		HttpPost httpPost = new HttpPost(get_sharePreferences_fuwuqi()
//				+ Contacts.GET_MY_NEWS);
		HttpPost httpPost = new HttpPost(get_sharePreferences_fuwuqi()
				+ Contacts.GET_MY_NEWS);
		httpPost.setHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=utf-8");
		StringEntity entity = new StringEntity(xml.toString());
		httpPost.setEntity(entity);
		HttpResponse httpResponse = client.execute(httpPost);

		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			// 通过HttpEntity获得响应流
			InputStream is = httpResponse.getEntity().getContent();
			// 将流转换成string
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int len;
			while ((len = is.read(buf)) != -1) {
				out.write(buf, 0, len);
			}
			String string = out.toString("UTF-8");
			Log.v("ceshi", "result:" + string);

			is = new ByteArrayInputStream(string.getBytes("UTF-8"));

			try {
				resultBean = new ResultBean();
				resultBean = XMLParserUtil.parse_result(is);
				if (resultBean.getRetrun_code().equals("false")) {
					handler.sendEmptyMessage(1);
					// 返回false，表示失败
					Toast.makeText(mContext, resultBean.getRetrun_msg(), 0)
							.show();
				} else {
					is = new ByteArrayInputStream(string.getBytes("UTF-8"));
					list_examine = XMLParserUtil.examineparse(is);

					handler.sendEmptyMessage(5);
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {
			Log.v("ceshi", "请求失败");
		}
	}

}
