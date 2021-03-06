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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.shadt.bean.ChannelBean;
import com.shadt.bean.ResultBean;
import com.shadt.caibian_news.R;
import com.shadt.util.Contacts;
import com.shadt.util.Get_Sign;
import com.shadt.util.XMLParserUtil;
import com.shadt.util.xBase64;

public class ExamineOkDetailActivity extends BaseActivity {
	int position = 0;
	String channel_id; // 新闻类型channelid
	private Context mContext = ExamineOkDetailActivity.this;
	ExamineDetailAdapter examineDetailAdapter;
	BitmapUtils xutils;
	ListView listview;
	LinearLayout btn1, btn2, btn3, btn4;
	String checkStatus = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_examine_ok);
		xutils = new BitmapUtils(mContext);
		xutils.configDefaultLoadFailedImage(R.drawable.img_no_pic);
		Intent it = getIntent();
		position = it.getIntExtra("position", 0);
		channel_id = ExamineActivity.list_examine2.get(position).getId();
		initPages();
		handler.sendEmptyMessage(100);
		handler.sendEmptyMessageDelayed(1, 1000);

	}

	public void initTop() {
		line_back = (LinearLayout) findViewById(R.id.line_back);
		line_back.setOnClickListener(this);
		title = (TextView) findViewById(R.id.title);
		title.setOnClickListener(this);
		title.setText("审核详情");

	}

	@Override
	public void initPages() {
		// TODO Auto-generated method stub
		initTop();
		listview = (ListView) findViewById(R.id.list);
	}

	@Override
	public void onClickListener(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			finish();
			break;

		case R.id.line1:// 上一个
			before_one();
			break;
		case R.id.line2:// 下一个
			next_one();
			break;
		case R.id.line3:
			checkStatus = "-1";// 通过
			handler.sendEmptyMessage(100);
			new Thread(networkTask2).start();
			break;
		case R.id.line4:
			handler.sendEmptyMessage(100);
			checkStatus = "0";// bu通过
			new Thread(networkTask2).start();
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
				submit_first(channel_id);

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
				// channelkey 栏目字段key
				submit_do_examine(channel_id, checkStatus);
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
			case 100:
				showdialog();
				break;
			case 101:
				// new Thread(networkTask2).start();
				hidedialogs();
				examineDetailAdapter = new ExamineDetailAdapter(mContext);
				listview.setAdapter(examineDetailAdapter);
				break;
			case 1:
				new Thread(networkTask).start();
				break;
			}
		};
	};
	private ResultBean resultBean;
	private List<ChannelBean> channel_list;

	public void submit_first(String id) throws IOException {

		Map<String, String> map = new TreeMap<String, String>();

		map.put("username", get_sharePreferences_name());
		map.put("recordId", id);
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
				+ Contacts.GET_NEWS_DETAIL);
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
					// 返回false，表示失败
				} else {
					is = new ByteArrayInputStream(string.getBytes("UTF-8"));
					channel_list = new ArrayList<ChannelBean>();
					channel_list = XMLParserUtil.Channelparse(is);
					handler.sendEmptyMessage(101);
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {
			Log.v("ceshi", "请求失败");
		}
	}

	// yes_or_no 0表示 打回 -1 表示通过
	public void submit_do_examine(String id, String yes_or_no)
			throws IOException {

		Map<String, String> map = new TreeMap<String, String>();
		map.put("checkStatus", yes_or_no);
		map.put("username", get_sharePreferences_name());
		map.put("recordId", id);
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
		HttpPost httpPost = new HttpPost(Contacts.DO_EXAMINE_NEWS);
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
			handler.sendEmptyMessage(101);

			try {
				resultBean = new ResultBean();
				resultBean = XMLParserUtil.parse_result(is);
				builder.setMessage("" + resultBean.getRetrun_msg());
				if (resultBean.getRetrun_code().equals("false")) {
					// 返回false，表示失败
					builder.setPositiveButton("确定",
							new AlertDialog.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							}).create().show();
				} else {

					builder.setPositiveButton("确定",
							new AlertDialog.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
									do_next_one();
								}
							}).create().show();
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {
			handler.sendEmptyMessage(101);

			Log.v("ceshi", "请求失败");
		}
	}

	public void before_one() {

		if (position == 0) {
			Toast.makeText(mContext, "已经是第一条了", 0).show();
		} else {
			position = position - 1;
			channel_id = ExamineActivity.list_examine2.get(position).getId();
			handler.sendEmptyMessage(100);
			handler.sendEmptyMessageDelayed(1, 1000);
		}
	}

	public void next_one() {
		if (position == ExamineActivity.list_examine2.size() - 1) {
			Toast.makeText(mContext, "已经是最后一条了", 0).show();
		} else {
			position = position + 1;
			Log.v("ceshi", "position" + position);
			channel_id = ExamineActivity.list_examine2.get(position).getId();
			handler.sendEmptyMessage(100);
			handler.sendEmptyMessageDelayed(1, 1000);
		}

	}

	public void do_next_one() {

		// 先判断集合 是不是 只有 一个
		if (ExamineActivity.list_examine2.size() == 1) {
			ExamineActivity.list_examine2.remove(position);
			finish();
			return;
		} else {
			// 如果位置是最后一个 position-1，如果不是最后一个 位置还是以前的位置
			if (position == ExamineActivity.list_examine2.size() - 1) {

				ExamineActivity.list_examine2.remove(position);
				position = position - 1;
			} else {
				ExamineActivity.list_examine2.remove(position);
				position = position;
			}
		}
		channel_id = ExamineActivity.list_examine2.get(position).getId();
		handler.sendEmptyMessage(100);
		handler.sendEmptyMessageDelayed(1, 1000);
	}

	public void do_before_one() {
		position = position - 1;
		channel_id = ExamineActivity.list_examine2.get(position).getId();
		handler.sendEmptyMessage(100);
		handler.sendEmptyMessageDelayed(1, 1000);
	}

	// 获取审核的新闻列表
	boolean is_switch = false;
	private AlertDialog resulDialog;

	public class ExamineDetailAdapter extends BaseAdapter {
		Context context;
		int my_onclic_position;

		public ExamineDetailAdapter(Context context) {
			// TODO Auto-generated constructor stub
			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return channel_list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return channel_list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			final holder holder;

			if (convertView == null) {
				holder = new holder();

				convertView = LayoutInflater.from(context).inflate(
						R.layout.add_list_item, null);
				holder.quanping = (ImageView) convertView
						.findViewById(R.id.img_quanping);
				holder.txt_img = (TextView) convertView
						.findViewById(R.id.txt_img);
				holder.edit_pic = (ImageView) convertView
						.findViewById(R.id.edit_pic);
				holder.edit_context = (TextView) convertView
						.findViewById(R.id.edit_context);
				holder.edit_title = (TextView) convertView
						.findViewById(R.id.edit_title);
				holder.rela_context = (RelativeLayout) convertView
						.findViewById(R.id.rela_context);
				holder.rela_title = (RelativeLayout) convertView
						.findViewById(R.id.rela_title);
				holder.rela_pic = (RelativeLayout) convertView
						.findViewById(R.id.rela_pic);
				holder.rela_switch = (RelativeLayout) convertView
						.findViewById(R.id.rela_switch);
				holder.img_switch = (ImageView) convertView
						.findViewById(R.id.img_switch);
				holder.txt_switch = (TextView) convertView
						.findViewById(R.id.txt_switch);
				holder.txt_shangchuantupian = (TextView) convertView
						.findViewById(R.id.txt_shangchuantupian);
				holder.txt_shangchuantupian.setVisibility(View.GONE);
				holder.txt_tuwen = (RelativeLayout) convertView
						.findViewById(R.id.txt_tuwen);
				holder.img_tuwen = (ImageView) convertView
						.findViewById(R.id.img_tuwen);
				convertView.setTag(holder);
			} else {
				holder = (holder) convertView.getTag();
			}
			holder.rela_context.setVisibility(View.GONE);
			holder.rela_pic.setVisibility(View.GONE);
			holder.rela_title.setVisibility(View.GONE);
			holder.rela_switch.setVisibility(View.GONE);
			holder.txt_tuwen.setVisibility(View.GONE);
			holder.txt_tuwen.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
				
					if (TextUtils.isEmpty(channel_list.get(position)
							.getFieldcontext())) {
						Toast.makeText(context, "内容为空", 0).show();
					}else{
						Intent it=new Intent(mContext,WebViewActivity.class);
						it.putExtra("position", position);
						it.putExtra("html", xBase64.getBase64frmat((channel_list.get(position)
								.getFieldcontext())));
						it.putExtra("class_name", ExamineOkDetailActivity.class.getName());
						startActivity(it);
					}
				}
			});
			holder.edit_pic.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (channel_list.get(position).getFieldcontext() != null) {
						
						/*
						 * if (channel_list.get(position).getFieldcontext()
						 * .length() < 1) { is_delete = false; } else {
						 * is_delete = true; }
						 */
						Intent it = new Intent(context, PictrueActivity.class);
						it.putExtra("context", channel_list.get(position)
								.getFieldcontext());
						it.putExtra("title", channel_list.get(position)
								.getFieldtitle());
						it.putExtra("delete", false);
						it.putExtra("position", position);
						startActivity(it);

					}

				}
			});

			String type = channel_list.get(position).getFieldtype();
			if (type.equals("1") || type.equals("2")) {
				holder.rela_title.setVisibility(View.VISIBLE);
				if (type.equals("2")) {
					holder.edit_title.setInputType(InputType.TYPE_CLASS_NUMBER);
				}
				holder.edit_title.setHint(""
						+ channel_list.get(position).getFieldtitle());
				String s = "";
				s = "" + channel_list.get(position).getFieldcontext();
				if (s.length() == 0) {
				} else {
					if (s.contains("null")) {
						s = s.replace("null", "");
					}
					holder.edit_title.setText(s);
				}
				holder.edit_title.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						Intent it = new Intent(mContext, QuanpingActivity.class);
						it.putExtra("context", channel_list.get(position)
								.getFieldcontext());
						it.putExtra("length", 255);
						it.putExtra("title", channel_list.get(position)
								.getFieldtitle());
						it.putExtra("position", position);
						startActivity(it);
					}
				});

			} else if (type.equals("3")) {

			} else if (type.equals("4")) {
				holder.rela_switch.setVisibility(View.VISIBLE);
				holder.txt_switch.setText(channel_list.get(position)
						.getFieldtitle());
				if (channel_list.get(position).getFieldcontext() != null) {
					if (channel_list.get(position).getFieldcontext()
							.equals("1")) {
						holder.img_switch
								.setImageResource(R.drawable.img_switch_yes);
						is_switch = true;
					} else {
						holder.img_switch
								.setImageResource(R.drawable.img_switch_no);
						is_switch = false;
					}
				} else {
					is_switch = false;
					channel_list.get(position).setFieldcontext("0");
					holder.img_switch
							.setImageResource(R.drawable.img_switch_no);
				}

			} else if (type.equals("5")) {
				holder.rela_context.setVisibility(View.VISIBLE);
				if (!TextUtils.isEmpty(channel_list.get(position)
						.getFieldcontext())) {
					holder.edit_context.setText(xBase64.getBase64frmat(channel_list.get(position)
							.getFieldcontext()));
				}else{					
					
				}
				holder.edit_context.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent it = new Intent(mContext, QuanpingActivity.class);
						if (!TextUtils.isEmpty(channel_list.get(position)
								.getFieldcontext())) {
						it.putExtra("context", xBase64.getBase64frmat(channel_list.get(position)
								.getFieldcontext()));
						}
						it.putExtra("length", 30000);
						it.putExtra("title", channel_list.get(position)
								.getFieldtitle());
						it.putExtra("position", position);
						startActivity(it);
					}
				});
			} else if (type.equals("6")) {
				holder.rela_pic.setVisibility(View.VISIBLE);
				holder.txt_img.setText(channel_list.get(position)
						.getFieldtitle());
				xutils.configDefaultLoadFailedImage(R.drawable.img_no_pic);
				if (channel_list.get(position).getFieldcontext() != null) {
					
					if(channel_list.get(position).getFieldcontext().contains("http") || channel_list.get(position)
							.getFieldcontext().contains("HTTP")){
						
						xutils.display(holder.edit_pic,channel_list.get(position)
												.getFieldcontext());
						
					}else{
						xutils.display(holder.edit_pic,
							get_sharePreferences_fuwuqi()
									+ Contacts.IP
									+ "/"
									+ channel_list.get(position)
											.getFieldcontext());
					}
					
					
				} else {
					holder.edit_pic
							.setImageResource(R.drawable.img_take_pictrue);
				}
			}else if (type.equals("8")) {
				holder.txt_tuwen.setVisibility(View.VISIBLE);
				if (!TextUtils.isEmpty(channel_list.get(position)
						.getFieldcontext())) {
					try {

						String path_img = XMLParserUtil.parsehtml(xBase64
								.getBase64frmat(channel_list.get(position)
										.getFieldcontext()));
						xutils.display(holder.img_tuwen, path_img);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
			return convertView;
		}

		class holder {
			TextView txt_img, txt_switch, txt_shangchuantupian;
			ImageView edit_pic, quanping, img_switch;
			ImageView img_tuwen;
			RelativeLayout rela_context, rela_title, rela_pic, rela_switch,txt_tuwen;
			TextView edit_title, edit_context;
		}
	}
}
