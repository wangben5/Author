package com.shadt.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.http.client.multipart.MultipartEntity;
import com.lidroid.xutils.http.client.multipart.content.FileBody;
import com.shadt.adpter.PopAdapter;
import com.shadt.bean.ChannelBean;
import com.shadt.bean.PindaoBean;
import com.shadt.bean.ResultBean;
import com.shadt.caibian_news.R;
import com.shadt.util.Contacts;
import com.shadt.util.Get_Sign;
import com.shadt.util.ImageTools;
import com.shadt.util.OtherFinals;
import com.shadt.util.XMLParserUtil;
import com.shadt.util.xBase64;
import com.shadt.wxpay.util.MD5;

public class AddNewsActivity extends BaseActivity {
	RelativeLayout btn_submit, btn_save;
	public ChannelBean mChannelBean;
	
	public static List<ChannelBean> channel_list;
	// 我定义来复制数据的时候提交的数据，但是如果提交数据失败，就不复制
	// 给 list,如果成功 就复制过去。
	public static List<ChannelBean> channel_list2;

	List<PindaoBean> title_list;
	ListView poplist, context_list;
	Context mContext = AddNewsActivity.this;
	PopAdapter popAdapter;
	AddAdapter addAdapter;
	int position_menu = 0; // 记录是那一条新闻
	ListView mlist;
	private static final int TAKE_PICTURE = 0;
	private static final int CHOOSE_PICTURE = 1;
	private static final int SCALE = 15;// 照片缩小比例
	String img_path1 = null, img_path2 = null, pic_name = null; // 储存照相一张原图片
	int img_position = 0;
	private Animation animation_in, animation_out;
	ImageView img_xiala;
	String checkStatus = "0"; // 0 是 编辑中 1是审核中

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_news);
		initPages();
		showdialog();
		if (webutils.isNetworkConnected(mContext)) {
			new Thread(networkTask_first).start();
		} else {
			Toast.makeText(mContext, getResources().getText(R.string.erro_net),
					0).show();
		}
	}

	@Override
	public void initPages() {
		// TODO Auto-generated method stub
		btn_submit = (RelativeLayout) findViewById(R.id.submit);
		btn_submit.setOnClickListener(this);
		btn_save = (RelativeLayout) findViewById(R.id.save);
		btn_save.setOnClickListener(this);
		context_list = (ListView) findViewById(R.id.list);
		context_list.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (is_show_list == true) {
					hideList();
				}
				return false;
			}
		});
		initTop();
	}

	public void initTop() {
		line_back = (LinearLayout) findViewById(R.id.line_back);
		line_back.setOnClickListener(this);
		title = (TextView) findViewById(R.id.title);
		img_xiala = (ImageView) findViewById(R.id.img_xiala);
		title.setOnClickListener(this);
		poplist = (ListView) findViewById(R.id.pop_list);
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
		poplist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (webutils.isNetworkConnected(mContext)) {

					popAdapter.setSelectedItem(position);
					if (position_menu == position) {

					} else {
						handler.sendEmptyMessage(2);
						new Thread(networkTask_channel).start();
					}
					position_menu = position;
					title.setText(title_list.get(position_menu).getTitle());
					hideList();
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
	}

	boolean is_show_list = false;
	boolean is_null = true;
	boolean is_fengmian_null = true;

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

	// 线程更新ui.
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				hidedialogs();
				if (channel_list.size() == 0) {
					btn_submit.setVisibility(View.GONE);
					btn_save.setVisibility(View.GONE);
				} else {
					btn_submit.setVisibility(View.VISIBLE);
					btn_save.setVisibility(View.VISIBLE);
				}
				addAdapter = new AddAdapter(mContext);

				if (addAdapter != null) {
					context_list.setAdapter(addAdapter);
				}
				break;
			case 0:
				init_channel_data();
				break;
			case 3:
				hidedialogs();
				break;
			case 2:
				showdialog();
				break;
			case 4:
				showdialog();
				for (int i = 0; i < channel_list2.size(); i++) {
					if (channel_list2.get(i).getFieldtype().equals("1")) {
						if (!TextUtils.isEmpty(channel_list2.get(i)
								.getFieldcontext())) {
							is_null = false;
						} else {
						}
					} else {

					}
					
					if (channel_list2.get(i).getFieldkey().equals("AnimationImg")) {
						if (!TextUtils.isEmpty(channel_list2.get(i)
								.getFieldcontext())) {
							is_fengmian_null = false;
						} else {
						}
					} else {

					}
					
					
				}
				if (is_null == false ) {
					
					if(is_fengmian_null == false){
						new Thread(networkTask_tijiao).start();
					}else{
						hidedialogs();
						Toast.makeText(mContext, "封面图不能为空", 0).show();
						tijiao = true;
					}
					
					
				} else {
					hidedialogs();
					Toast.makeText(mContext, "标题不能为空", 0).show();
					tijiao = true;
				}
				
				
				break;
			case 5:
				if (tijiao == false) {
					Toast.makeText(mContext, "不能重复操作", 0).show();
					hidedialogs();
				} else {
					new Thread(networkTask_img).start();
				}
				break;
			case 6:
				hidedialogs();
				Toast.makeText(mContext, "请求失败", 0).show();
				break;
			}
		};
	};

	@Override
	public void onClickListener(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		// 审核
		case R.id.submit:
			if (webutils.isNetworkConnected(mContext)) {
				checkStatus = "1";
				handler.sendEmptyMessage(2);
				handler.sendEmptyMessageDelayed(5, 1000);
			} else {
				Toast.makeText(mContext,
						getResources().getText(R.string.erro_net), 0).show();
			}
			break;
		// 保存
		case R.id.save:
			if (webutils.isNetworkConnected(mContext)) {
				checkStatus = "0";
				handler.sendEmptyMessage(2);
				handler.sendEmptyMessageDelayed(5, 1000);
			} else {
				Toast.makeText(mContext,
						getResources().getText(R.string.erro_net), 0).show();
			}
			break;
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
				initpop();
			}
			break;
		default:
			break;
		}
	}

	// 获取每个 栏目下字段信息
	Runnable networkTask_channel = new Runnable() {

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
						submit_channel(title_list.get(position_menu).getId(),
								title_list.get(position_menu).getKey());
					}
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Looper.loop();
		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		tijiao = true;
		title_list = null;
		channel_list = null;
		channel_list2 = null;
		SharedPreferences preferences = getSharedPreferences("user",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("html_native", "");
		editor.putString("html_web", "");
		editor.commit();
	}

	boolean tijiao = true;
	Runnable networkTask_tijiao = new Runnable() {

		@Override
		public void run() {
			// TODO
			// 在这里进行 http request.网络请求相关操作
			Looper.prepare();
			try {
				submit_tijiao(title_list.get(position_menu).getKey(),
						title_list.get(position_menu).getId());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Looper.loop();
		}
	};
	Runnable networkTask_first = new Runnable() {

		@Override
		public void run() {
			// TODO
			// 在这里进行 http request.网络请求相关操作
			Looper.prepare();
			try {
				try {
					Thread.sleep(1000);
					submit_first();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Looper.loop();
		}
	};
	Runnable networkTask_img = new Runnable() {

		@Override
		public void run() {
			// TODO
			// 在这里进行 http request.网络请求相关操作
			Looper.prepare();
			try {
				tijiao = false;
				// channelkey 栏目字段key
				String s = title_list.get(position_menu).getKey() + "&";
				Submit_img(get_sharePreferences_fuwuqi() + Contacts.DO_PIC
						+ "?channelKey=" + s);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Looper.loop();
		}
	};
	private AlertDialog resulDialog;

	private static String genAppSign(List<NameValuePair> params, String token) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=" + token);
		String appSign = MD5.getMessageDigest(sb.toString().getBytes());
		exChange(appSign);
		appSign = exChange(appSign);
		Log.e("ceshi", "----" + appSign);
		return appSign;
	}

	public static String exChange(String str) {
		String s = str.toUpperCase();
		return s;
	}

	@SuppressLint("ShowToast")
	public void submit_tijiao(String channelkey, String channelid)
			throws IOException {
		Log.v("ceshi", "token:" + get_sharePreferences_token());
		// 因为是xml格式我先要把 传输数据的转换成字符串用来签名。
		// 截取我想要的部分字符串
		String data = "";
		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("channelId", channelid));
		signParams.add(new BasicNameValuePair("channelkey", channelkey));
		signParams.add(new BasicNameValuePair("checkStatus", checkStatus));
		signParams.add(new BasicNameValuePair("data", data));
		signParams.add(new BasicNameValuePair("username",
				get_sharePreferences_name()));
		// 获取sign。
		// String sign = Get_Sign.paixu_map(map, get_sharePreferences_token());
		String sign = genAppSign(signParams, get_sharePreferences_token());
		// 得到sign
		// TODO Auto-generated method stub
		// 组建xml数据
		StringBuilder xml = new StringBuilder();
		xml.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		xml.append("<xml>");
		xml.append("<username>" + get_sharePreferences_name() + "</username>");
		xml.append("<channelId>" + channelid + "</channelId>");
		xml.append("<channelkey>" + channelkey + "</channelkey>");
		xml.append("<checkStatus>" + checkStatus + "</checkStatus>");
		xml.append("<data>");
		for (int i = 0; i < channel_list2.size(); i++) {

			xml.append("<field>");
			xml.append("<fieldkey>" + channel_list2.get(i).getFieldkey()
					+ "</fieldkey>");
			xml.append("<fieldtype>" + channel_list2.get(i).getFieldtype()
					+ "</fieldtype>");
			if (channel_list2.get(i).getFieldtype().equals("8")) {
				SharedPreferences preferences = getSharedPreferences("user",
						Context.MODE_PRIVATE);
				String html = "";
				if (!TextUtils.isEmpty(preferences.getString("html_web", ""))) {
					html = xBase64.getBase64(preferences.getString("html_web",
							""));
				}
				xml.append("<fieldcontext>" + html + "</fieldcontext>");
			} else {
				xml.append("<fieldcontext>"
						+ channel_list2.get(i).getFieldcontext()
						+ "</fieldcontext>");
			}
			xml.append("</field>");
		}
		xml.append("</data>");
		xml.append("<sign>" + sign + "</sign>");
		xml.append("</xml>");
		String string_xml = xml.toString().replace("null", "");
		Log.v("ceshi", "xml:"+xml.toString());
		// 实例化一个默认的Http客户端
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(get_sharePreferences_fuwuqi()
				+ Contacts.DO_NEWS);
		httpPost.setHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=utf-8");
		StringEntity entity = new StringEntity(string_xml, HTTP.UTF_8);
		httpPost.setEntity(entity);
		HttpResponse httpResponse = client.execute(httpPost);
		handler.sendEmptyMessage(3); // 隐藏
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
			if (TextUtils.isEmpty(string)) {
				tijiao = true;
				handler.sendEmptyMessage(6);
			} else {
				is = new ByteArrayInputStream(string.getBytes("UTF-8"));
				is_null = true;
				is_fengmian_null = true ;
				try {
					resultBean = new ResultBean();
					resultBean = XMLParserUtil.parse_result(is);
					builder.setMessage(resultBean.getRetrun_msg() + "");
					if (resultBean.getRetrun_code().equals("false")) {
						Log.v("ceshi", "" + resultBean.getRetrun_msg());
						builder.setPositiveButton("确定",
								new AlertDialog.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										tijiao = true;
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
										finish();
										dialog.dismiss();
									}
								}).create().show();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			tijiao = true;
			handler.sendEmptyMessage(6);
			Log.v("ceshi", "请求失败");
		}
	}

	ResultBean resultBean;
	private DisplayMetrics dm;

	public void submit_channel(String id, String key) throws IOException {
		Map<String, String> map = new TreeMap<String, String>();
		map.put("channelId", id);
		map.put("channelkey", key);
		map.put("username", get_sharePreferences_name());
		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("channelId", id));
		signParams.add(new BasicNameValuePair("channelkey", key));
		signParams.add(new BasicNameValuePair("username",
				get_sharePreferences_name()));
		// 获取sign。
		// String sign = Get_Sign.paixu_map(map, get_sharePreferences_token());
		String sign = genAppSign(signParams, get_sharePreferences_token());

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
		Log.v("ceshi2", "xml.to:" + xml.toString());
		// 实例化一个默认的Http客户端
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(get_sharePreferences_fuwuqi()
				+ Contacts.GET_CHANNEL);

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
				// 隐藏dialog
				handler.sendEmptyMessage(3);
				if (resultBean.getRetrun_code().equals("false")) {
					// 返回false，表示失败

				} else {
					is = new ByteArrayInputStream(string.getBytes("UTF-8"));
					channel_list = XMLParserUtil.Channelparse(is);
					
					for(int i = 0 ; i< channel_list.size() ; i++){
						
						if(channel_list.get(i).getFieldkey().equals("orderby")){
							mChannelBean = channel_list.get(i);
							channel_list.remove(i);
							handler.sendEmptyMessage(1);
							return ;
						}
						
					}
					
					handler.sendEmptyMessage(1);
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {
			Log.v("ceshi", "请求失败");
			handler.sendEmptyMessage(3);
		}
	}

	// 获取新闻类别

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

			xml.append("<" + entity.getKey() + ">" + entity.getValue() + "</"
					+ entity.getKey() + ">");
		}
		xml.append("<sign>" + sign + "</sign>");
		xml.append("</xml>");
		Log.v("ceshi2", "xml.to:" + xml.toString());
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
			is = new ByteArrayInputStream(string.getBytes("UTF-8"));

			try {
				resultBean = new ResultBean();
				resultBean = XMLParserUtil.parse_result(is);
				if (resultBean.getRetrun_code().equals("false")) {
					// 返回false，表示失败
					handler.sendEmptyMessage(3);// 隐藏dialog
				} else {
					is = new ByteArrayInputStream(string.getBytes("UTF-8"));
					title_list = new ArrayList<PindaoBean>();
					title_list = XMLParserUtil.parse_pindao(is);
					Log.v("ceshi", "size:aaa" + title_list.size());
					handler.sendEmptyMessage(0);
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {
			Log.v("ceshi", "请求失败");
		}
	}

	// 用来存储 图片 在 listview 的 位置
	public  ArrayList<String> position_item;

	public void Submit_img(String url) throws IOException {
		HttpClient httpclient = new DefaultHttpClient();
		try {
			position_item=new ArrayList<String>();
			HttpPost httppost = new HttpPost(url);
			MultipartEntity reqEntity = new MultipartEntity();
			
			//把过滤的排序好数据添加进来
			channel_list .add(mChannelBean);
			
			for (int i = 0; i < channel_list.size(); i++) {
				if (channel_list.get(i).getFieldtype().equals("6")) {
					if (channel_list.get(i).getFieldcontext() != null) {
						position_item.add("" + i);
						FileBody file = new FileBody(new File(channel_list.get(
								i).getFieldcontext()));
						reqEntity.addPart(channel_list.get(i).getFieldkey(),
								file);
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

				if (statusCode == HttpStatus.SC_OK) {
					// Toast.makeText(AnjianBianjiActivity.this, "上传成功",
					// 0).show();
					String result = EntityUtils.toString(
							httpResponse.getEntity(), "UTF-8");
					Log.v("ceshi2", "result" + result);
					channel_list2 = new ArrayList<ChannelBean>();
					for (int i = 0; i < channel_list.size(); i++) {
						ChannelBean bean = new ChannelBean();
						bean.setFieldcontext(channel_list.get(i)
								.getFieldcontext());
						bean.setFieldkey(channel_list.get(i).getFieldkey());
						bean.setFieldtype(channel_list.get(i).getFieldtype());
						channel_list2.add(bean);
					}
					channel_list2 = parse_json(result);
					handler.sendEmptyMessage(4);
				} else {
					tijiao = true;
					handler.sendEmptyMessage(3);
					Log.v("ceshi2", "失败");
				}

			} catch (ClientProtocolException e) {
				e.printStackTrace();

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				handler.sendEmptyMessage(3);
				if (httpclient != null) {
					httpclient.getConnectionManager().shutdown();
					httpclient = null;
				}
				
				removeNum();
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			tijiao = true;
			e.printStackTrace();
			removeNum();
		}
	}

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
				new Thread(networkTask_channel).start();
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			img_path2 = null;
			switch (requestCode) {
			case TAKE_PICTURE:
				// 将保存在本地的图片取出并缩小后显示在界面上
				if (img_path1 != null) {
					// 压缩过后的图片
					img_path2 = compress(OtherFinals.DIR_IMG + img_path1);
					channel_list.get(img_position).setFieldcontext(img_path2);
					addAdapter.notifyDataSetChanged();
				}
				break;
			case CHOOSE_PICTURE:
				ContentResolver resolver = getContentResolver();
				// 照片的原始资源地址
				Uri originalUri = data.getData();
				// 使用ContentProvider通过URI获取原始图片
				// 为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
				if (originalUri != null) {
					try {
						// 使用ContentProvider通过URI获取原始图片
						// 为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
						if (changeUriToPath(originalUri) != null) {
							img_path2 = compress(changeUriToPath(originalUri));
							channel_list.get(img_position).setFieldcontext(
									img_path2);
							addAdapter.notifyDataSetChanged();
							Log.v("ceshi", "buweinull:" + img_path2);
						} else {
							Bitmap photo = MediaStore.Images.Media.getBitmap(
									resolver, originalUri);
							pic_name = UuidName();
							File file = new File(OtherFinals.DIR_IMG);
							if (!file.exists()) {
								file.mkdir();
							}
							ImageTools.savePhotoToSDCard(photo,
									OtherFinals.DIR_IMG, pic_name);
							img_path2 = compress(OtherFinals.DIR_IMG + pic_name);
							channel_list.get(img_position).setFieldcontext(
									img_path2);
							Log.v("ceshi", "weinull:" + img_path2);
							addAdapter.notifyDataSetChanged();
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(mContext, "获取图片失败", 0).show();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				break;

			default:
				break;
			}
		}
	}

	boolean is_switch = false;
	BitmapUtils bitmapUtils;
	public class AddAdapter extends BaseAdapter {
		Context context;
		int my_onclic_position;
		
		public AddAdapter(Context context) {
			// TODO Auto-generated constructor stub
			this.context = context;
			bitmapUtils=new BitmapUtils(context);
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
				holder.txt_tuwen = (RelativeLayout) convertView
						.findViewById(R.id.txt_tuwen);
				holder.img_tuwen=(ImageView) convertView.findViewById(R.id.img_tuwen);
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
					if (!TextUtils.isEmpty(tuwen)) {
						Intent it = new Intent(mContext, AddActivity2.class);
						it.putExtra("position0", position);
						it.putExtra("num", 100); // 可以添加图片数量
						it.putExtra("html", tuwen);
						it.putExtra("canshu", channel_list.get(position)
								.getFieldkey());// 上传图片的字段参数
						it.putExtra("class_name",
								AddNewsActivity.class.getName());
						startActivity(it);
					} else {
						Intent it = new Intent(mContext, ImggridActivity.class);
						it.putExtra("position0", position);
						it.putExtra("num", 100); // 可以添加图片数量
						it.putExtra("canshu", channel_list.get(position)
								.getFieldkey());// 上传图片的字段参数
						it.putExtra("class_name",
								AddNewsActivity.class.getName());
						startActivity(it);
					}
				}
			});
			holder.quanping.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent it = new Intent(mContext, QuanpingActivity.class);
					it.putExtra("context", channel_list.get(position)
							.getFieldcontext());
					it.putExtra("position", position);
					startActivity(it);
				}
			});
			holder.edit_pic.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					img_position = position;
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
						it.putExtra("position", position);
						it.putExtra("class_name",
								AddNewsActivity.class.getName());
						startActivity(it);
					} else {
						open_carmare(position);
					}
				}
			});
			final String type = channel_list.get(position).getFieldtype();
			if (type.equals("1") || type.equals("2")) {
				holder.rela_title.setVisibility(View.VISIBLE);
				if (type.equals("2")) {
					holder.edit_title.setInputType(InputType.TYPE_CLASS_NUMBER);
				}
				if (TextUtils.isEmpty(channel_list.get(position)
						.getFieldcontext())) {
					holder.edit_title.setText("");
				} else {
					holder.edit_title.setText(""
							+ channel_list.get(position).getFieldcontext());
				}
				holder.edit_title.setHint(channel_list.get(position)
						.getFieldtitle());
				holder.edit_title.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						Intent it = new Intent(mContext, QuanpingActivity.class);
						it.putExtra("context", channel_list.get(position)
								.getFieldcontext());
						it.putExtra("length", 255);
						it.putExtra("enable", true);
						if (type.equals("2")) {
							it.putExtra("type", 0);// 0表示是数字
						}
						it.putExtra("title", channel_list.get(position)
								.getFieldtitle());
						it.putExtra("position", position);
						startActivity(it);
					}
				});
			} else if (type.equals("3")) {

			} else if (type.equals("4")) {
				holder.rela_switch.setVisibility(View.VISIBLE);
				holder.txt_switch.setText(""
						+ channel_list.get(position).getFieldtitle());
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

				holder.img_switch.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (is_switch == true) {
							is_switch = false;
							holder.img_switch
									.setImageResource(R.drawable.img_switch_no);
							channel_list.get(position).setFieldcontext("0");
						} else {
							is_switch = true;
							holder.img_switch
									.setImageResource(R.drawable.img_switch_yes);
							channel_list.get(position).setFieldcontext("1");
						}
					}
				});
			} else if (type.equals("5")) {
				holder.rela_context.setVisibility(View.VISIBLE);

				holder.edit_context.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						Intent it = new Intent(mContext, QuanpingActivity.class);
						
						it.putExtra("length", 30000);
						it.putExtra(
								"title",
								channel_list.get(
										position).getFieldtitle());
						if (!TextUtils.isEmpty(channel_list.get(position)
								.getFieldcontext())) {
							it.putExtra("context", xBase64.getBase64frmat(channel_list.get(
									position).getFieldcontext()));
						}
						it.putExtra("enable", true);
						it.putExtra("position", position);
						startActivity(it);
					}
				});
				if (TextUtils.isEmpty(channel_list.get(position)
						.getFieldcontext())) {
					holder.edit_context.setHint(""
							+ channel_list.get(position).getFieldtitle());
				} else {
					holder.edit_context.setText(xBase64.getBase64frmat(channel_list.get(position)
							.getFieldcontext()));
				}

			} else if (type.equals("6")) {
				holder.rela_pic.setVisibility(View.VISIBLE);
				holder.txt_img.setText(channel_list.get(position)
						.getFieldtitle());
				if (channel_list.get(position).getFieldcontext() != null) {
					Bitmap bitmap = BitmapFactory.decodeFile(channel_list.get(
							position).getFieldcontext());
					holder.edit_pic.setImageBitmap(bitmap);
				} else {
					holder.edit_pic
							.setImageResource(R.drawable.img_take_pictrue);
				}
			} else if (type.equals("8")) {
				holder.txt_tuwen.setVisibility(View.VISIBLE);
				if (!TextUtils.isEmpty(tuwen)) {
					String path_img=parsehtml(tuwen);
					Log.v("ceshi", "aaaa>"+path_img);
//					Bitmap bitmap = BitmapFactory.decodeFile(path_img);
//					holder.img_tuwen.setImageBitmap(bitmap);
					bitmapUtils.display(holder.img_tuwen, path_img);
					
				}
			}
			return convertView;
		}

		private void check(int position) {
			for (ChannelBean l : channel_list) {
				l.setFocus(false);
			}
			channel_list.get(position).setFocus(true);
		}

		class holder {
			TextView txt_img, txt_switch;
			ImageView edit_pic, quanping, img_switch;
			ImageView img_tuwen;
			RelativeLayout rela_context, rela_title, rela_pic, rela_switch,txt_tuwen;
			TextView edit_title, edit_context;
		}
	}

	String tuwen = "";

	// 如果 变量 is_delete==true,就表示在删除了图片。
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SharedPreferences preferences = getSharedPreferences("user",
				Context.MODE_PRIVATE);
		tuwen = preferences.getString("html_web", "");
		if (addAdapter != null) {
			addAdapter.notifyDataSetChanged();
		}
		
		if(channel_list != null){
			for(int i = 0 ; i< channel_list.size() ; i++){
			
				if(channel_list.get(i).getFieldkey().equals("orderby")){
					mChannelBean = channel_list.get(i);
					channel_list.remove(i);
					
					return ;
				}
				
			}
		}
		
	}
	
	/**
	 *  去除序号
	 */
	public void removeNum (){
		if(channel_list != null){
			for(int i = 0 ; i< channel_list.size() ; i++){
			
				if(channel_list.get(i).getFieldkey().equals("orderby")){
					mChannelBean = channel_list.get(i);
					channel_list.remove(i);
					
					return ;
				}
				
			}
		}
	}

	public void open_carmare(final int p) {

		String[] items = { "拍照", "相册" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("请选择照片来源")
				.setItems(items, new DialogInterface.OnClickListener() {
					private String picName;

					@Override
					public void onClick(DialogInterface dialog, int which) {
						picName = "";
						if (which == 0) {
							takePhoto();
						} else if (which == 1) {
							selectFromAlbum(p);
						}
					}
				}).create().show();
	}

	private void takePhoto() {
		// TODO Auto-generated method stub
		File file = new File(OtherFinals.DIR_IMG);
		if (!file.exists()) {
			file.mkdir();
		} else {
		}
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		img_path1 = UuidName();

		Uri imageUri = Uri.fromFile(new File(OtherFinals.DIR_IMG, img_path1));

		Log.v("ceshi", "uri:" + imageUri + "\n" + img_path1);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	private String UuidName() {
		String time = String.valueOf(System.currentTimeMillis());
		return time + ".jpg";
	}

	/**
	 * 从相册选
	 */
	private void selectFromAlbum(int p) {
		Intent it = new Intent(mContext, com.shadt.activity.ImggridActivity.class);
		it.putExtra("position", p);
		it.putExtra("num", 1);
		it.putExtra("class_name", AddNewsActivity.class.getName());
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
		int pic_size = Integer.parseInt(get_sharePreferences_pic_width());
		while (baos.toByteArray().length > pic_size * 1024) {
			baos.reset();
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
			quality -= 20;
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 1235645488 - 0 > 3000
			initpop();
		}
		return false;
	}

	AlertDialog myDialog;

	private void initpop() {
		// TODO Auto-generated method stub

		myDialog = new AlertDialog.Builder(mContext).create();
		myDialog.show();
		myDialog.getWindow().setContentView(R.layout.tuichuedit_dialog);
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
						myDialog.dismiss();
						finish();
					}
				});
	}
	String html_start = "<!DOCTYPE html>\n" + "<html lang=\"en\">\n"
			+ "<head>\n" + "<meta charset=\"UTF-8\">\n"
			+ "<title>Document</title>\n" + "</head>\n" + "<body>\n";
	String html_end = "</body>\n" + "</html>\n";
	private ArrayList<String> listimg;
	public String parsehtml(String htmlcontent) {
		Document doc = Jsoup.parse(html_start + htmlcontent + html_end);
		Elements eles = doc.getElementsByTag("div");// 将a标签的列表存储成元素集合
		listimg = new ArrayList<String>();
		Elements pngs = doc.select("img");
		// 遍历元素
		for (Element e : pngs) {
			String src = e.attr("src");// 获取img中的src路径
			listimg.add(src);
		}
		return listimg.get(0);
		
	}
	public  List<ChannelBean> parse_json(String jsonData) {

		try {
			JSONArray arr = new JSONArray(jsonData);
			for (int i = 0; i < arr.length(); i++) {
				JSONObject temp = (JSONObject) arr.get(i);
				String key = temp.getString(AddNewsActivity.channel_list2.get(
						Integer.parseInt(position_item.get(i)
								.toString())).getFieldkey());
				AddNewsActivity.channel_list2.get(
						Integer.parseInt(position_item.get(i)
								.toString())).setFieldcontext(key);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return AddNewsActivity.channel_list2;
	}
}
