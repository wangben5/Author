package com.shadt.activity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shadt.activity.BaseActivity;
import com.shadt.activity.ExamineDetailActivity;
import com.shadt.activity.ExamineOkDetailActivity;
import com.shadt.caibian_news.R;
import com.shadt.util.Html_Save;

public class WebViewActivity extends BaseActivity {

	private WebView mWebView;
	private String javascript;
	private String html_content = "";
	private String class_name = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);
		Intent it = getIntent();
		web_path = it.getStringExtra("url");
		initWebView();
		html_content = it.getStringExtra("html");
		class_name = it.getStringExtra("class_name");
		if (TextUtils.isEmpty(class_name)) {
			mWebView.loadUrl("file:///mnt/" + web_path);
		}else{
		if (class_name.equals(ExamineOkDetailActivity.class.getName())
				|| class_name.equals(ExamineDetailActivity.class.getName())) {
			// 审核详情只能查看
			new Thread(r).start();
		} 
		}
	}
	String web_path = "";
	String html_start = "<!DOCTYPE html>\n" + "<html lang=\"en\">\n"
			+ "<head>\n" + "<meta charset=\"UTF-8\">\n"
			+ "<title>Document</title>\n" + "</head>\n" + "<body>\n";
	String html_end = "</body>\n" + "</html>\n";
	Runnable r = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Looper.prepare();
            if(html_content.contains("15px")){
                html_content=html_content.replace("15px", "40px");
            }
            Document doc = Jsoup.parse(html_start + html_content + html_end);
			web_path = Html_Save.html_save(doc);
			handler.sendEmptyMessage(1);
			Looper.loop();
		}
	};
	Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			if (msg.what==1) {
				mWebView.loadUrl("file:///mnt/" + web_path);
			}
		};
	};
	private void initWebView() {
		line_back = (LinearLayout) findViewById(R.id.line_back);
		line_back.setOnClickListener(this);
		title = (TextView) findViewById(R.id.title);
		title.setText("图文预览");
		mWebView = (WebView) findViewById(R.id.web);
		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setPluginState(WebSettings.PluginState.ON);
		// settings.setPluginsEnabled(true);
		settings.setAllowFileAccess(true);
		settings.setLoadWithOverviewMode(true);
		settings.setUseWideViewPort(true);
		settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		settings.setCacheMode(WebSettings.LOAD_DEFAULT);
		InsideWebViewClient mInsideWebViewClient = new InsideWebViewClient();
		mWebView.setWebViewClient(mInsideWebViewClient);
	}

	@Override
	public void onPause() {// 继承自Activity
		super.onPause();
		mWebView.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		mWebView.onResume();
	}

	@Override
	public void onBackPressed() {
		if (mWebView.canGoBack()) {
			mWebView.goBack();
			return;
		}
		super.onBackPressed();
	}

	@Override
	public void onDestroy() {
		mWebView.destroy();
		super.onDestroy();
	}

	private class InsideWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			mWebView.loadUrl(javascript);
		}
	}

	@Override
	public void initPages() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClickListener(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.line_back:
			finish();

			break;
		default:
			break;
		}
	}

}
