package com.shadt.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shadt.adpter.ExamineAdapter;
import com.shadt.adpter.MyPagerAdapter;
import com.shadt.adpter.PopAdapter;
import com.shadt.bean.ExamineBean;
import com.shadt.bean.PindaoBean;
import com.shadt.bean.ResultBean;
import com.shadt.caibian_news.R;
import com.shadt.util.Contacts;
import com.shadt.util.Get_Sign;
import com.shadt.util.RefreshLayout;
import com.shadt.util.RefreshLayout.OnLoadListener;
import com.shadt.util.XMLParserUtil;

public class ExamineActivity extends BaseActivity {
	String channel_id; // 新闻类型channelid
	private ListView context_list, context_list2;
	private ListView poplist;
	private Animation animation_in, animation_out;
	ExamineAdapter examineAdapter1, examineAdapter2;
	ExamineBean examineBean;
	public static List<ExamineBean> list_examine1, list_examine2,
			list_examine11, list_examine22; // 11 和 22 只是获取的 拿来存放在 1 和2 里面
	TextView txt_null1, txt_null2;
	public final int INT_HIDE_DIALOG = 101;
	public final int INT_HIDE_DIALOG_CONTEXT = 1011; // 内容数据获取 赋值
	public final int INT_HIDE_DIALOG_CONTEXT2 = 1012; // 内容数据获取 赋值

	public final int INT_SHOW_DIALOG = 100;
	public final int INT_ERROR_TIME = 404;// 超时
	public final int INT_ERROR_TIME2 = 4042;// 超时

	public final int INT_FIRST_SUBMIT = 1; // 第一次接口请求
	public final int INT_SECOND_SUBMIT = 2; // 第二个
	public final int INT_SECOND_SUBMIT2 = 22; // 第3个

	public final int INT_GET_INFO = 11; // 得到顶部数据 设置adpter
	public final int INT_CONTENT_NULL = 403;// 数据为空
	public final int INT_CONTENT_NULL2 = 4032;// 数据为空

	public final int INT_TOAST = 5001;// 数据为空

	Button btn1, btn2;
	public boolean is_fresh_data = true; // 判断是否刷新 ui更新数据
	public int type = 0; // 判断是viewpager 是 处于 0 还是 1 板块。 1 是 已经审核数据 ，0 未审核数据
	public int page_index = 1; // 定义加载的页数
	public int page_index2 = 1; // 定义加载的页数
	com.shadt.util.RefreshLayout swipeRefreshLayout1, swipeRefreshLayout2;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case INT_HIDE_DIALOG:
				hidedialogs();
				break;
			case INT_SHOW_DIALOG:
				showdialog();
				break;
			case INT_FIRST_SUBMIT:
				new Thread(networkTask).start();
				break;
			case INT_SECOND_SUBMIT:
				new Thread(networkTask2).start();
				break;
			case INT_SECOND_SUBMIT2:
				new Thread(networkTask3).start();
				break;
			case INT_GET_INFO:
				init_channel_data();
				break;
			case INT_HIDE_DIALOG_CONTEXT:
				hidedialogs();

				if (list_examine1 != null) {
					if (list_examine1.size() == 0) {
						handler.sendEmptyMessage(INT_CONTENT_NULL);
					} else {
						txt_null1.setVisibility(View.GONE);
						if (shuaxin == true) {
							examineAdapter1 = new ExamineAdapter(mContext,
									list_examine1,
									get_sharePreferences_fuwuqi());
							context_list.setAdapter(examineAdapter1);
						} else {

							if (examineAdapter1 != null) {
								examineAdapter1.notifyDataSetChanged();
								Log.v("ceshi2", "-------------------");
							} else {
								examineAdapter1 = new ExamineAdapter(mContext,
										list_examine1,
										get_sharePreferences_fuwuqi());
								context_list.setAdapter(examineAdapter1);
							}
						}
					}
				} else {
					handler.sendEmptyMessage(INT_CONTENT_NULL);
				}
				break;
			case INT_HIDE_DIALOG_CONTEXT2:
				hidedialogs();
				if (list_examine2 != null) {
					if (list_examine2.size() == 0) {
						handler.sendEmptyMessage(INT_CONTENT_NULL2);
					} else {
						txt_null2.setVisibility(View.GONE);
						if (shuaxin == true) {
							examineAdapter2 = new ExamineAdapter(mContext,
									list_examine2,
									get_sharePreferences_fuwuqi());
							context_list2.setAdapter(examineAdapter2);
						} else {

							if (examineAdapter2 != null) {
								examineAdapter2.notifyDataSetChanged();
								Log.v("ceshi2", "-------------------");
							} else {
								examineAdapter2 = new ExamineAdapter(mContext,
										list_examine2,
										get_sharePreferences_fuwuqi());
								context_list2.setAdapter(examineAdapter2);
							}
						}
					}
				} else {
					handler.sendEmptyMessage(INT_CONTENT_NULL2);
				}
				break;
			case INT_ERROR_TIME:
				hidedialogs();
				examineAdapter1 = new ExamineAdapter(mContext, list_examine1,
						get_sharePreferences_fuwuqi());
				context_list.setAdapter(examineAdapter1);
				txt_null1.setVisibility(View.VISIBLE);
				txt_null1.setText("" + resultBean.getRetrun_msg());
				break;
			case INT_CONTENT_NULL:
				hidedialogs();
				examineAdapter1 = new ExamineAdapter(mContext, list_examine1,
						get_sharePreferences_fuwuqi());
				context_list.setAdapter(examineAdapter1);
				txt_null1.setVisibility(View.VISIBLE);
				txt_null1.setText("没有需要操作的数据");
				break;
			case INT_ERROR_TIME2:
				hidedialogs();
				examineAdapter2 = new ExamineAdapter(mContext, list_examine2,
						get_sharePreferences_fuwuqi());
				context_list2.setAdapter(examineAdapter2);
				txt_null2.setVisibility(View.VISIBLE);
				txt_null2.setText("" + resultBean.getRetrun_msg());
				break;
			case INT_CONTENT_NULL2:
				hidedialogs();
				examineAdapter2 = new ExamineAdapter(mContext, list_examine2,
						get_sharePreferences_fuwuqi());
				context_list2.setAdapter(examineAdapter2);
				txt_null2.setVisibility(View.VISIBLE);
				txt_null2.setText("没有需要操作的数据");
				break;
			case INT_TOAST:
				Toast.makeText(mContext, "已经到底了", 0).show();
				break;
			}
		};
	};
	int page_num = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.examin_main_activity);
		initPages();
		handler.sendEmptyMessage(INT_SHOW_DIALOG);
		handler.sendEmptyMessageDelayed(INT_FIRST_SUBMIT, 1000);

		page_num = Integer.parseInt("" + get_sharePreferences_pagenum());

	}

	private LayoutInflater inflater;
	private View tab01, tab02;
	private List<View> viewList; // 布局集合
	private ViewPager viewPager;
	boolean shuaxin = true;

	@Override
	public void initPages() {
		// TODO Auto-generated method stub

		inflater = LayoutInflater.from(this);
		tab01 = inflater.inflate(R.layout.list_fragment, null);
		tab02 = inflater.inflate(R.layout.list_fragment, null);
		viewList = new ArrayList<View>();
		viewList.add(tab01);
		viewList.add(tab02);
		viewPager = (ViewPager) findViewById(R.id.id_viewpage);
		viewPager.setAdapter(new MyPagerAdapter(viewList));
		viewPager.setOffscreenPageLimit(2);
		viewPager.setOnPageChangeListener(pageListener);
		txt_null1 = (TextView) tab01.findViewById(R.id.txt_null);
		txt_null2 = (TextView) tab02.findViewById(R.id.txt_null);
		btn1 = (Button) findViewById(R.id.btn1);
		btn2 = (Button) findViewById(R.id.btn2);
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		initTop();
		init1();
		init2();
	}

	// @Override
	// public void loadMore() {
	// if (type==0) {
	// page_index++;
	// handler.sendEmptyMessage(INT_SECOND_SUBMIT);
	//
	// }else{
	// page_index2++;
	// handler.sendEmptyMessage(INT_SECOND_SUBMIT2);
	//
	// }
	// }
	// // 加载更多的方法
	// @Override
	// public void loadMore() {
	// page_index++;
	// handler.sendEmptyMessage(INT_SECOND_SUBMIT);
	// }
	private void init1() {
		// TODO Auto-generated method stub
		swipeRefreshLayout1 = (RefreshLayout) tab01
				.findViewById(R.id.swip_index);
		swipeRefreshLayout1.setColorScheme(R.color.holo_blue_bright,
				R.color.holo_green_light, R.color.holo_orange_light,
				R.color.holo_red_light);
		context_list = (ListView) tab01.findViewById(R.id.list);
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
					Intent it = new Intent(mContext,
							ExamineDetailActivity.class);
					it.putExtra("position", position);
					startActivity(it);
				}
			}
		});
		// 设置下拉刷新监听器
		swipeRefreshLayout1.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {

				swipeRefreshLayout1.postDelayed(new Runnable() {

					@Override
					public void run() {
						page_index = 1;
						shuaxin = true;
						handler.sendEmptyMessage(INT_SECOND_SUBMIT);
						swipeRefreshLayout1.setRefreshing(false);
						// 更新数据
					}
				}, 1000);
			}
		});
		// 加载监听器
		swipeRefreshLayout1.setOnLoadListener(new OnLoadListener() {

			@Override
			public void onLoad() {

				swipeRefreshLayout1.postDelayed(new Runnable() {

					@Override
					public void run() {
						if (list_examine1 != null) {

							if (list_examine1.size() < page_num) {
								Log.v("ceshi", "aaaaaaabbb");
							} else {
								Log.v("ceshi", "ccccccccc");
								shuaxin = false;
								page_index++;
								handler.sendEmptyMessage(INT_SECOND_SUBMIT);
								swipeRefreshLayout1.setLoading(false);
							}
						}

					}
				}, 1000);

			}
		});
	}

	private void init2() {
		// TODO Auto-generated method stub
		swipeRefreshLayout2 = (RefreshLayout) tab02
				.findViewById(R.id.swip_index);
		context_list2 = (ListView) tab02.findViewById(R.id.list);
		swipeRefreshLayout2.setColorScheme(R.color.holo_blue_bright,
				R.color.holo_green_light, R.color.holo_orange_light,
				R.color.holo_red_light);
		context_list2.setOnTouchListener(new OnTouchListener() {

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
		context_list2.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (is_show_list == true) {
					hideList();
				} else {
					Intent it = new Intent(mContext,
							ExamineOkDetailActivity.class);
					it.putExtra("position", position);
					startActivity(it);
				}
			}
		});
		// 设置下拉刷新监听器
		swipeRefreshLayout2.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {

				swipeRefreshLayout2.postDelayed(new Runnable() {

					@Override
					public void run() {
						// 更新数据
						page_index2 = 1;
						shuaxin = true;
						handler.sendEmptyMessage(INT_SECOND_SUBMIT2);
						swipeRefreshLayout2.setRefreshing(false);
					}
				}, 1000);
			}
		});
		// 加载监听器
		swipeRefreshLayout2.setOnLoadListener(new OnLoadListener() {

			@Override
			public void onLoad() {

				swipeRefreshLayout2.postDelayed(new Runnable() {

					@Override
					public void run() {

						if (list_examine2 != null) {

							if (list_examine2.size() < page_num) {

							} else {
								page_index2++;
								shuaxin = false;
								handler.sendEmptyMessage(INT_SECOND_SUBMIT2);
								swipeRefreshLayout2.setLoading(false);
							}
						}
					}
				}, 1000);

			}
		});
	}

	OnPageChangeListener pageListener = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int index) {
			try {
				if (index == 0) {
					type = 0;
					btn1.setBackgroundResource(R.drawable.img_btn_top_sel);
					btn2.setBackgroundResource(R.drawable.img_btn_top_nor);
					if (list_examine1 != null) {

						if (is_fresh_data == true) {
							page_index = 1;
							handler.sendEmptyMessage(INT_SHOW_DIALOG);
							handler.sendEmptyMessageDelayed(INT_SECOND_SUBMIT,
									500);
							is_fresh_data = false;
						} else {

						}

					} else {
						page_index = 1;
						handler.sendEmptyMessage(INT_SHOW_DIALOG);
						handler.sendEmptyMessageDelayed(INT_SECOND_SUBMIT, 500);
						is_fresh_data = false;
					}
				} else if (index == 1) {
					type = 1;
					btn2.setBackgroundResource(R.drawable.img_btn_top_sel);
					btn1.setBackgroundResource(R.drawable.img_btn_top_nor);

					if (list_examine2 != null) {
						Log.v("ceshi2", "hello111");
						if (is_fresh_data == true) {
							page_index2 = 1;
							handler.sendEmptyMessage(INT_SHOW_DIALOG);
							handler.sendEmptyMessageDelayed(INT_SECOND_SUBMIT2,
									500);
							is_fresh_data = false;
							Log.v("ceshi2", "hello22222");
						} else {

						}
					} else {
						Log.v("ceshi2", "hello3333");
						page_index2 = 1;
						handler.sendEmptyMessage(INT_SHOW_DIALOG);
						handler.sendEmptyMessageDelayed(INT_SECOND_SUBMIT2, 500);
						is_fresh_data = false;
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		@Override
		public void onPageScrolled(int index, float ratio, int offset) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};
	private int position_menu = 0;

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (type == 0) {
			if (examineAdapter1 != null) {
				examineAdapter1.notifyDataSetChanged();
			} else {

			}
		} else {
			if (examineAdapter2 != null) {
				examineAdapter2.notifyDataSetChanged();
			} else {

			}
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (list_examine1 != null) {
			list_examine1 = null;
		}
		if (list_examine2 != null) {
			list_examine2 = null;
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
						// 表示要更新数据
						is_fresh_data = true;
						handler.sendEmptyMessage(INT_SHOW_DIALOG);
						popAdapter.setSelectedItem(position);
						shuaxin = true;
						if (type == 0) {
							page_index = 1;
							handler.sendEmptyMessageDelayed(INT_SECOND_SUBMIT,
									1000); // 请求获取栏目下列表
						} else {
							page_index2 = 1;
							handler.sendEmptyMessageDelayed(INT_SECOND_SUBMIT2,
									1000); // 请求获取栏目下列表

						}
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
		case R.id.btn1:
			viewPager.setCurrentItem(0, false);
			break;
		case R.id.btn2:
			viewPager.setCurrentItem(1, false);

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

					} else {
						submit_getlist_examine(title_list.get(position_menu)
								.getId(), "" + page_num, "" + page_index, 0,
								shuaxin);
					}
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Looper.loop();
		}
	};
	// 获取已经审核列表
	Runnable networkTask3 = new Runnable() {

		@Override
		public void run() {
			// TODO
			// 在这里进行 http request.网络请求相关操作
			Looper.prepare();
			if (title_list != null) {
				if (title_list.size() != 0) {

					try {
						submit_getlist_examine(title_list.get(position_menu)
								.getId(), "10", "" + page_index2, 1, shuaxin);
					} catch (Exception e) {
						// TODO: handle exception
					}
				} else {
					hidedialogs();
					Toast.makeText(mContext, "获取栏目为空,请联系管理人员", 0).show();

				}

			}
			Looper.loop();
		}
	};

	private PopAdapter popAdapter;
	Context mContext = ExamineActivity.this;

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
					handler.sendEmptyMessage(INT_ERROR_TIME);

					// 返回false，表示失败
				} else {
					is = new ByteArrayInputStream(string.getBytes("UTF-8"));
					title_list = new ArrayList<PindaoBean>();
					title_list = XMLParserUtil.parse_pindao(is);
					handler.sendEmptyMessage(INT_GET_INFO);
					handler.sendEmptyMessage(INT_SECOND_SUBMIT);
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {
			handler.sendEmptyMessage(INT_HIDE_DIALOG);
			Log.v("ceshi", "请求失败");
		}
	}

	// 获取审核的新闻列表
	public void submit_getlist_examine(String id, String index, String page,
			int examine_yes_no, boolean shuaxin) throws IOException {
		// examine_yes_no 1 表示 已经审核 0 表示未审核
		Map<String, String> map = new TreeMap<String, String>();
		map.put("channelId", id);
		map.put("indexsize", index);
		map.put("pagesize", page);
		map.put("username", get_sharePreferences_name());

		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("channelId", id));
		signParams.add(new BasicNameValuePair("indexsize", index));
		signParams.add(new BasicNameValuePair("pagesize", page));
		signParams.add(new BasicNameValuePair("username",
				get_sharePreferences_name()));
		// 获取sign。
		// String sign = Get_Sign.paixu_map(map, get_sharePreferences_token());
		String sign = Get_Sign.genAppSign(signParams,
				get_sharePreferences_token());

		// String sign = Get_Sign.paixu_map(map, get_sharePreferences_token());
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
		String requst_url = "";
		if (examine_yes_no == 1) {
			requst_url = get_sharePreferences_fuwuqi()
					+ Contacts.GET_EXAMINE_YES;
		} else {
			requst_url = get_sharePreferences_fuwuqi() + Contacts.GET_EXAMINE;
		}
		HttpPost httpPost = new HttpPost(requst_url);
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
					handler.sendEmptyMessage(INT_ERROR_TIME);
					// 返回false，表示失败
				} else {
					is = new ByteArrayInputStream(string.getBytes("UTF-8"));
					if (examine_yes_no == 1) { // 已经审核
						if (shuaxin == true) {
							list_examine2 = null;
							list_examine2 = new ArrayList<ExamineBean>();
							list_examine2 = XMLParserUtil.examineparse(is);
						} else {
							list_examine22 = new ArrayList<ExamineBean>();
							list_examine22 = XMLParserUtil.examineparse(is);
							if (list_examine22.size() == 0) {
								page_index2--;
								handler.sendEmptyMessage(INT_TOAST);
								Log.v("ceshi2", "没有更多数据");
							} else {
								for (int i = 0; i < list_examine22.size(); i++) {
									ExamineBean bean = new ExamineBean();
									bean.setId(list_examine22.get(i).getId());
									bean.setImg(list_examine22.get(i).getImg());
									bean.setUpdateUser(list_examine22.get(i)
											.getUpdateUser());
									bean.setRecordtitle(list_examine22.get(i)
											.getRecordtitle());

									bean.setUpdateTime(list_examine22.get(i)
											.getUpdateTime());
									bean.setUuid(list_examine22.get(i)
											.getUuid());
									if (list_examine2 != null) {
										list_examine2.add(bean);
									} else {
										list_examine2 = new ArrayList<ExamineBean>();
										list_examine2.add(bean);
									}
								}
							}
						}
						handler.sendEmptyMessage(INT_HIDE_DIALOG_CONTEXT2);

					} else {
						if (shuaxin == true) {

							list_examine1 = new ArrayList<ExamineBean>();
							list_examine1 = XMLParserUtil.examineparse(is);

						} else {

							list_examine11 = new ArrayList<ExamineBean>();
							list_examine11 = XMLParserUtil.examineparse(is);
							if (list_examine11.size() == 0) {
								handler.sendEmptyMessage(INT_TOAST);
								page_index--;
							} else {
								for (int i = 0; i < list_examine11.size(); i++) {
									ExamineBean bean = new ExamineBean();
									bean.setId(list_examine11.get(i).getId());
									bean.setImg(list_examine11.get(i).getImg());

									bean.setRecordtitle(list_examine11.get(i)
											.getRecordtitle());
									bean.setUpdateUser(list_examine11.get(i)
											.getUpdateUser());

									bean.setUpdateTime(list_examine11.get(i)
											.getUpdateTime());

									bean.setUuid(list_examine11.get(i)
											.getUuid());
									if (list_examine1 != null) {
										list_examine1.add(bean);
									} else {
										list_examine1 = new ArrayList<ExamineBean>();
										list_examine1.add(bean);
									}
								}
							}
						}
						handler.sendEmptyMessage(INT_HIDE_DIALOG_CONTEXT);
					}
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {
			handler.sendEmptyMessage(INT_ERROR_TIME);
			Log.v("ceshi", "请求失败");
		}
	}
}
