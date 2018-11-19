package com.shadt.activity;

import java.io.BufferedReader;
import java.io.File;
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
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.shadt.application.MyApp;
import com.shadt.bean.Friend;
import com.shadt.bean.UpdateInfo;
import com.shadt.caibian_news.R;
import com.shadt.service.UPDATADownloadService;
import com.shadt.util.Contacts;
import com.shadt.util.MyLog;
import com.shadt.util.OtherFinals;
import com.shadt.util.XMLParserUtil;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

/**
 * 现在大家说的phonegap就是Cordova<br>
 * phonegap实际上指的是一个在线打包工具。<br>
 * 而是用phonegap开发实质就是是用Cordova。<br>
 * 因为两家公司有过并购历史，导致难以区分。
 * */
public class MainActivity extends BaseActivity implements  RongIM.UserInfoProvider{

    UserInfo mUserInfo;


	ImageView add_news;
	protected static final String TAG = MainActivity.class.getName();
	String daiding_url = "";
	String redexamine = "", rededit = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initPages();
		handler.sendEmptyMessage(2);

        userIdList = new ArrayList<Friend>();

        userIdList.add(new Friend(""+get_sharePreferences_ronguserid(), ""+get_sharePreferences_ronguser(), ""+get_sharePreferences_ronguserimg()));

        RongIM.setUserInfoProvider(this, true);
 	}

	RelativeLayout rela_examine, rela_setting, rela_mine, rela3_daiding;
	private long time;
	private UpdateInfo updateInfo;

	@Override
	public void initPages() {
		// TODO Auto-generated method stub
		Intent it = getIntent();
		rededit = it.getStringExtra("recordedit");
		redexamine = it.getStringExtra("recordexamine");
		File dir = new File(OtherFinals.DIR_IMG);
		Log.v("ceshi", "dsds" + get_sharePreferences_pagenum());
		if (!dir.exists()) {// Launch camera to take photo for
			dir.mkdirs();// 创建照片的存储目录
			Log.v("ceshi", "bucunzai");
		} else {
			Log.v("ceshi", "cunzai");
		}
		add_news = (ImageView) findViewById(R.id.add_news);
		add_news.setOnClickListener(this);
		rela_examine = (RelativeLayout) findViewById(R.id.rela_examine);
		rela_examine.setOnClickListener(this);
		rela_setting = (RelativeLayout) findViewById(R.id.rela_setting);
		rela_setting.setOnClickListener(this);
		rela_mine = (RelativeLayout) findViewById(R.id.rela_my_news);
		rela_mine.setOnClickListener(this);
		rela3_daiding = (RelativeLayout) findViewById(R.id.rela3_daiding);
		rela3_daiding.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// if (TextUtils.isEmpty(daiding_url)) {

				// }else{
                //新功能
//				 Intent it=new Intent(mContext,NewfunctionActivity.class);
//
//				 startActivity(it);

//                String token="UslOSCZoc0M38DhYbr7CWwiYfgIYM1tv/+bXBOmeh25NVVRflQeWy9LFN7gwLoc2wJ4XofGbklhofa65tGHW3Q==";
                String token="+b2a6xIPmYo/NaTKNUp9OyizoCNjg41oxSR2jp/e98jxKpYYS5Vx9t8mRZXfhLleUmEvk7eORXw806MnMmFPlg==";
                setconnection(token);
				// }
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 1235645488 - 0 > 3000

            moveTaskToBack(false);
		}
		return false;
	}

	@Override
	public void onClickListener(View v) {
		// TODO Auto-generated method stub
		Intent it = new Intent();
		switch (v.getId()) {
		case R.id.add_news:
			if (rededit.equals("true")) {
				it.setClass(this, AddNewsActivity.class);
			} else {
				Toast.makeText(mContext, "您没有权限进行此操作", 0).show();
				return;
			}

			break;
		case R.id.rela_examine:
			if (redexamine.equals("true")) {
				it.setClass(this, ExamineActivity.class);
			} else {
				Toast.makeText(mContext, "您没有权限进行此操作", 0).show();
				return;
			}
			break;
		case R.id.rela_my_news:
			if (rededit.equals("true")) {
				it.setClass(this, MineNewsActivity.class);
			} else {
				Toast.makeText(mContext, "您没有权限进行此操作", 0).show();
				return;
			}

			break;
		case R.id.rela_setting:
			it.setClass(this, SettingActivity.class);
			break;

		}
		startActivity(it);
	}

	// 用handle进行 更新ui
	Handler handler = new Handler() {
		@Override
		@SuppressLint("ShowToast")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 6:
				updata_dialog();
				break;
			case 2:
				new Thread(networkTask2).start();
				break;
			default:
				break;
			}
		};
	};
	public static boolean is_finish = false;
	// 线程发送请求 这个是 获取 logo 图标 和 app_name 版本信息
	Runnable networkTask2 = new Runnable() {

		@Override
		public void run() {
			// TODO
			// 在这里进行 http request.网络请求相关操作
			Looper.prepare();
			try {
				SubmitPost(Contacts.AREA_ID, "0", null, null, null, null, null,
						Contacts.UPDATA);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Looper.loop();
		}
	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		if (is_finish == true) {
//			is_finish = false;
//
//			finish();
//		}
		displayBriefMemory();
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		
		if (is_finish == true) {
			is_finish = false;
			
			Intent it = new Intent(mContext, LoginActivity.class);
			startActivity(it);
			finish();
		}
		
	}
	private void displayBriefMemory() {    
        final ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);    
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();   
        activityManager.getMemoryInfo(info);    
        Log.i("ceshi","系统剩余内存:"+(info.availMem >> 10)+"k");   
        Log.i("ceshi","系统是否处于低内存运行："+info.lowMemory);
        Log.i("ceshi","当系统剩余内存低于"+info.threshold+"时就看成低内存运行");
    } 
	// 获取 板块内容 和 logo name.
	private MyApp app;
	private Context mContext = MainActivity.this;
	private String version_name;
	private int currentVersionCode;

	public void SubmitPost(String string1, String string2, String string3,
			String string4, String string5, String string6, String string7,
			String url) throws IOException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);

		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("vsInData0", string1));
		formparams.add(new BasicNameValuePair("vsInData1", string2));
		formparams.add(new BasicNameValuePair("vsInData2", string3));
		formparams.add(new BasicNameValuePair("vsInData3", string4));
		formparams.add(new BasicNameValuePair("vsInData4", string5));
		formparams.add(new BasicNameValuePair("vsInData5", string6));
		formparams.add(new BasicNameValuePair("vsInData6", string7));
		UrlEncodedFormEntity uefEntity;
		uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
		httppost.setEntity(uefEntity);
		HttpResponse response = httpclient.execute(httppost);

		HttpEntity entity = response.getEntity();
		if (entity != null) {

			updateInfo = new UpdateInfo();
			InputStream instreams = entity.getContent();

			String str = ConvertStreamToString(instreams);
			Log.v("ceshi", "" + str);
			try {
				updateInfo = XMLParserUtil.Parser_version(str);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			app = (MyApp) getApplication();
			PackageManager manager = mContext.getPackageManager();
			try {
				PackageInfo info = manager.getPackageInfo(
						mContext.getPackageName(), 0);
				UPDATADownloadService.apkUrl = updateInfo.getVsOutData6();
				daiding_url = updateInfo.getVsOutData7();
				version_name = info.versionName; // 版本名
				currentVersionCode = info.versionCode; // 版本号
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch blockd
				e.printStackTrace();
			}
            MyLog.i("version_name"+version_name+"updateInfo.getVsOutData4()"+updateInfo.getVsOutData4());
			// 上面是获取manifest中的版本数据，我是使用versionCode
			// 在从服务器获取到最新版本的versionCode,比较
 			if (!version_name.equals(updateInfo.getVsOutData4())) {
				handler.sendEmptyMessageDelayed(6, 1000);

			} else {
			}
		}
	}

	public static String ConvertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		StringBuilder sb = new StringBuilder();

		String line = null;

		try {

			while ((line = reader.readLine()) != null) {

				sb.append(line + "\n");

			}

		} catch (IOException e) {

			System.out.println("Error=" + e.toString());

		} finally {

			try {

				is.close();

			} catch (IOException e) {

				System.out.println("Error=" + e.toString());

			}
		}
		return sb.toString();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			deleteFolderFile(OtherFinals.DIR_IMG, true);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 删除指定目录下文件及目录
	 * 
	 * @param deleteThisPath
	 * @param filepath
	 * @return
	 */
	public void deleteFolderFile(String filePath, boolean deleteThisPath) {
		if (!TextUtils.isEmpty(filePath)) {
			try {
				File file = new File(filePath);
				if (file.isDirectory()) {// 处理目录
					File files[] = file.listFiles();
					for (int i = 0; i < files.length; i++) {
						deleteFolderFile(files[i].getAbsolutePath(), true);
					}
				}
				if (deleteThisPath) {
					if (!file.isDirectory()) {// 如果是文件，删除
						file.delete();
					} else {// 目录
						if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
							file.delete();
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


    public void setconnection(String token){
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {

                Log.v(TAG,"onTokenIncorrect");

            }

            @Override
            public void onSuccess(String s) {

                Log.v(TAG,">>>>>>>>>>>>>>>>成功"+s);
//                finish();
                Intent it=new Intent(getApplication(),NewfunctionActivity.class);
                startActivity(it);
//                RongIM.getInstance().startPrivateChat(getApplication(),"1","0");


            }

            @Override
            public void onError(final RongIMClient.ErrorCode e) {
                Log.v(TAG,">>>>>>>>>>>>>>>>失败");
            }

        });
    }
    private List<Friend> userIdList;
    @Override
    public UserInfo getUserInfo(String s) {
        for (Friend i : userIdList) {
            if (i.getUserId().equals(s)) {
                Log.e(TAG, i.getPortraitUri());
                return new UserInfo(i.getUserId(),i.getUserName(), Uri.parse(i.getPortraitUri()));
            }
        }
        Log.e("wangben","UserId is ：" +s );
        return null;
    }


}