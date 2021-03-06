package com.shadt.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.shadt.application.MyApp;
import com.shadt.bean.UpdateInfo;
import com.shadt.caibian_news.R;
import com.shadt.service.UPDATADownloadService;
import com.shadt.util.Contacts;
import com.shadt.util.XMLParserUtil;

public class AboutActivity extends BaseActivity {
	LinearLayout line_back;
	RelativeLayout line_exit, line_version, line_about, line_change_pwd;
	TextView title, txt_content;
	public String ACTION_FINISH = "finish";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		initPages();
	}

	@Override
	public void initPages() {
		// TODO Auto-generated method stub
		initTop();
	}

	public void initTop() {
		line_back = (LinearLayout) findViewById(R.id.line_back);
		line_back.setOnClickListener(this);
		title = (TextView) findViewById(R.id.title);
		title.setText("关于");
		txt_content = (TextView) findViewById(R.id.txt);
		txt_content.setText("  " + get_sharePreferences_about());
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

	// 用handle进行 更新ui
	Handler handler = new Handler() {
		@Override
		@SuppressLint("ShowToast")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 6:
				hidedialogs();
				updata_dialog();
				break;
			case 0:
				showdialog();
				break;
			case 1:
				hidedialogs();
				break;
			case 2:
				break;
			default:
				break;
			}
		};
	};

}
