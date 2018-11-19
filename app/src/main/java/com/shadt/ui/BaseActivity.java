/**
 * 
 */
package com.shadt.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.shadt.application.MyApp;
import com.shadt.caibian_news.R;
import com.shadt.service.UPDATADownloadService;
import com.shadt.service.UPDATADownloadService.DownloadBinder;
import com.shadt.util.ActivityManagerTool;
import com.shadt.util.CustomDialog2.Builder;
import com.shadt.util.CustomProgressDialog;
import com.shadt.util.LoadingDialog;
import com.shadt.util.MyLog;
import com.shadt.ui.utils.WebUtils;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * 该Activity继承了{@link Activity},实现了{@link OnClickListener}接口，该类新增的功能有: 1)页面的全屏;
 * 2)增加网络不可用提示对话框方法 {@link #alertDialog(String, String, Class)}; 3)增加现在加载对话框方法
 * {@link #showProgressDialog()},{@link #showProgressDialog(String)}<br>
 * <br>
 * 
 * 继承该类的子类，需要实现 1){@link #initPages()}初始化页面控件 ; 2){@link #onClickListener(View)}
 * ,处理单击事件
 * 
 * @author zoutiao<br>
 * 
 *         2011-09-30 zhangchen 修改：(1) 删除setResult（）方法 (2)
 *         在onCreate方法中添加ActivityManagers add()方法 (3)
 *         在onDestroy()方法中添加ActivityManagers removeActivity()方法 (4)
 *         添加OnKeyDown()方法
 * 
 *         2011-10-10 zhangchen 修改：(1) 修改ONKeyDown方法，当不是底部导航类则按回退键关闭当前类
 * 
 *         2011-10-24 likuan 增加单选文本和带图片文本的dialog，使用方法见注释
 * 
 *         2011-11-22 zhangchen 1.添加设置是否全屏方法setIsFullScreen 2 OnCreat（）添加判断是否全屏
 * 
 *         2011-11-30 zhangchen
 *         修改OnKeyDown方法，先判断是否是返回键，然后在对应相应操作，解决按android手机其他按键bug问题
 * 
 */

public abstract class BaseActivity extends AppCompatActivity implements OnClickListener {
	private AlertDialog.Builder adb;
	TextView title;// 顶部标题栏
	LinearLayout line_back;
	private LoadingDialog dialog;
	WebUtils webutils;
	private MyApp app;
	public Builder builder;
	public String ACTION_FINISH = "finish";
    public MaterialDialog mLoadingDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 判断是否全屏 0未全屏 其他为不全屏
		// 全屏
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// //透明导航栏 沉嵌是
  		customProgressDialog = new CustomProgressDialog(this);
		dialog = new LoadingDialog(this, R.layout.view_tips_loading);
      /*  RongIM.setConnectionStatusListener(new RongIMClient.ConnectionStatusListener() {
            @Override
            public void onChanged(ConnectionStatus connectionStatus) {
                MyLog.i("下线呢了");
            }
        });*/
		builder = new Builder(this);

		webutils = new WebUtils();
		app = (MyApp) getApplication();
	}
    public void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new MaterialDialog.Builder(this)
                    .widgetColorRes(R.color.colorPrimary)
                    .progress(true, 0)
                    .cancelable(false)
                    .build();
        }
        mLoadingDialog.setContent("数据加载中...");
        mLoadingDialog.show();
    }
    public void showLoadingDialog(String msg) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new MaterialDialog.Builder(this)
                    .widgetColorRes(R.color.colorPrimary)
                    .progress(true, 0)
                    .cancelable(false)
                    .build();
        }
        mLoadingDialog.setContent(msg);
        mLoadingDialog.show();
    }

    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }
	public void showdialog() {
		// TODO Auto-generated method stub
		try {
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void hidedialogs() {
		// TODO Auto-generated method stub
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	public String get_sharePreferences_name() {
		// TODO Auto-generated method stub
		SharedPreferences preferences = getSharedPreferences("user",
				Context.MODE_PRIVATE);
		String name = preferences.getString("name", "");
		return name;
	}

	public String get_app_set() {
		// TODO Auto-generated method stub
		SharedPreferences preferences = getSharedPreferences("user",
				Context.MODE_PRIVATE);
		String set = preferences.getString("set", "");
		return set;
	}

	public String get_sharePreferences_token() {
		// TODO Auto-generated method stub
		SharedPreferences preferences = getSharedPreferences("user",
				Context.MODE_PRIVATE);
		String token = preferences.getString("token", "");
		return token;
	}

	public String get_sharePreferences_paw() {
		// TODO Auto-generated method stub
		SharedPreferences preferences = getSharedPreferences("user",
				Context.MODE_PRIVATE);
		String pass = preferences.getString("pass", "");
		return pass;
	}

	public String get_sharePreferences_appname() {
		// TODO Auto-generated method stub
		SharedPreferences preferences = getSharedPreferences("user",
				Context.MODE_PRIVATE);
		String token = preferences.getString("vsOutData0", "");
		return token;
	}

	public String get_sharePreferences_tvname() {
		// TODO Auto-generated method stub
		SharedPreferences preferences = getSharedPreferences("user",
				Context.MODE_PRIVATE);
		String token = preferences.getString("vsOutData1", "");
		return token;
	}

	public String get_sharePreferences_fuwuqi() {
		// TODO Auto-generated method stub
		SharedPreferences preferences = getSharedPreferences("user",
				Context.MODE_PRIVATE);
		String token = preferences.getString("vsOutData2", "");
//		token = "http://192.168.2.59:8010" ; //内网测试用
		Log.v("ceshi", "ip:"+token);
		return token;
	}

	public String get_sharePreferences_pagenum() {
		// TODO Auto-generated method stub
		SharedPreferences preferences = getSharedPreferences("user",
				Context.MODE_PRIVATE);
		String token = preferences.getString("vsOutData4", "");
		return token;
	}

	public String get_sharePreferences_pic_width() {
		// TODO Auto-generated method stub
		SharedPreferences preferences = getSharedPreferences("user",
				Context.MODE_PRIVATE);
		String token = preferences.getString("vsOutData3", "");
		return token;
	}

	public String get_sharePreferences_about() {
		// TODO Auto-generated method stub
		SharedPreferences preferences = getSharedPreferences("user",
				Context.MODE_PRIVATE);
		String token = preferences.getString("vsOutData5", "");
		return token;
	}

	public String get_sharePreferences_is_have_init() {
		// TODO Auto-generated method stub
		SharedPreferences preferences = getSharedPreferences("user",
				Context.MODE_PRIVATE);
		String token = preferences.getString("is_have", "0"); // 0 失败 1： 成功
		return token;
	}
    public String get_sharePreferences_liveurl() {
        // TODO Auto-generated method stub
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        String token = preferences.getString("liveurl", "");
        return token;
    }
	public void write_sharePreferences(String name, String pass, String token) {
		// TODO Auto-generated method stub
		SharedPreferences preferences = getSharedPreferences("user",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("name", name);
		editor.putString("pass", pass);
		editor.putString("token", token);
        long timeStamp = System.currentTimeMillis()/1000;
        editor.putLong("time",timeStamp);
		editor.commit();
	}
    public void write_live_url(String liveurl) {
        // TODO Auto-generated method stub
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString("liveurl", liveurl);
        editor.commit();
    }
    public void write_permission(String rededit, String redexamine) {
        // TODO Auto-generated method stub
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString("rededit", rededit);
        editor.putString("redexamine", redexamine);

        editor.commit();
    }

    public long get_sharePreferences_timestamp() {
        // TODO Auto-generated method stub
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        long token = preferences.getLong("time", System.currentTimeMillis()); // 0 失败 1： 成功
        return token;
    }
    @Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityManagerTool.getActivityManager().removeActivity(this);// 将当前acitivity移除activity管理集合中
		if (isBinded) {
			System.out.println(" onDestroy   unbindservice");
			unbindService(conn);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}



	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	/**
	 * 初始化顶部的标题栏
	 */
	public void initTitleBar() {
		line_back = (LinearLayout) findViewById(R.id.line_back);
		title = (TextView) findViewById(R.id.title);
		line_back.setOnClickListener(this);
	}

	/**
	 * 初始化页面组件
	 */
	public abstract void initPages();

	public abstract void onClickListener(View v);


	/**
	 * 处理单击事件，子类必须实现该方法，用来处理页面中控件的单击事件
	 */

	/**
	 * 显示网络不可用对话框，该对话框有一个"确定"按钮，点击确定按钮，返回到给定的Activity
	 *
	 * @param title
	 *            对话框标题
	 * @param des
	 *            对话框内容
	 * @param goClass
	 *            将要跳转到的页面
	 */

	/**
	 * 普通的提示框，只有确定按钮，点击关闭对话框，停留当前页
	 *
	 * @param title
	 *            对话框标题
	 * @param des
	 *            对话框内容
	 */


	/**
	 * OnItemSelected接口（用于返回选中id）
	 *
	 * @author lik
	 *
	 */
	public interface OnItemSelected {
		public void itemSelected(int which);
	}


	@Override
	public void onClick(View v) {

		onClickListener(v);
	}

	/**
	 * 显示加载对话框,该对话框无title,自定义内容； 也可以调用默认内容的加载对话框{@link #showProgressDialog()}
	 *
	 * @author zoutiao
	 * @param message
	 *            加载对话框的内容
	 */
	CustomProgressDialog customProgressDialog;

	/**
	 * 底部导航栏点击返回跳到首页操作，如果是首页则执行退出操作
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		ActivityManagerTool.getActivityManager();
		// 先判断是否是返回键
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// 判断是不是首页
			if (this.getClass() != ActivityManagerTool.indexActivity) {
				// 如果不是首页但是底部导航则执行跳转到首页操作
				if (ActivityManagerTool.getActivityManager().isBottomActivity(
						this)) {
					ActivityManagerTool.getActivityManager().backIndex(this);
				} else {
					return super.onKeyDown(keyCode, event);
				}
			} else {
				// 首页按返回键提示是否退出
				showExitDialog();
			}
		}
		return false;
	}

	/*
	 * 弹出退出对话框
	 */
	protected void showExitDialog() {
		Dialog dialog = new AlertDialog.Builder(this).setMessage("您确定要退出？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						ActivityManagerTool.getActivityManager().exit();
						finish();
					}

				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

				}).create();
		dialog.show();
	}

    protected void showNotifyDialog(String msg) {
        Dialog dialog = new AlertDialog.Builder(this).setMessage(msg)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                }).create();
        dialog.show();
    }

	/**
	 * 设置全屏
	 *
	 * @return 是否全屏 0未全屏 其他为不全屏
	 */

	// 更新app
	/**
	 * 我定义一个 where_show 如果 是 0 就表示 更新 apk 的 时候 需要 展示 如果 是 1 就表示 需要下载 yyp2p视频监控
	 *
	 * @param where_show
	 */
	private boolean isDestroy = true;
	private boolean isBinded;
	private DownloadBinder binder;
	public AlertDialog myDialog;

	public void updata_dialog() {
		// TODO Auto-generated method stub

		myDialog = new AlertDialog.Builder(mContext).create();
		myDialog.show();
		myDialog.getWindow().setContentView(R.layout.popu_eorr2);
		myDialog.getWindow().findViewById(R.id.txt_save_no)
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						myDialog.dismiss();
					}
				});
		myDialog.getWindow().findViewById(R.id.txt_save)
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						app.setDownload(true);
						if (isDestroy && app.isDownload()) {
							Intent it = new Intent(mContext,
									UPDATADownloadService.class);
							startService(it);
							bindService(it, conn, Context.BIND_AUTO_CREATE);
						}
						myDialog.dismiss();
					}
				});
	}
	public void delete_dialog() {
		// TODO Auto-generated method stub

		myDialog = new AlertDialog.Builder(mContext).create();
		myDialog.show();
		myDialog.getWindow().setContentView(R.layout.popu_delete);

		myDialog.getWindow().findViewById(R.id.txt_save_no)
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				});
		myDialog.getWindow().findViewById(R.id.txt_save)
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						app.setDownload(true);
						if (isDestroy && app.isDownload()) {
							Intent it = new Intent(mContext,
									UPDATADownloadService.class);
							startService(it);
							bindService(it, conn, Context.BIND_AUTO_CREATE);
						}
						myDialog.dismiss();
					}
				});
	}

	ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			isBinded = false;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			binder = (DownloadBinder) service;
			System.out.println("服务启动!!!");
			// 开始下载
			isBinded = true;
			// binder.addCallback(callback);
			binder.start();

		}
	};
	protected Activity mProgressBar;
	private Context mContext = BaseActivity.this;

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		if (isDestroy && app.isDownload()) {
			Intent it = new Intent(mContext, UPDATADownloadService.class);
			startService(it);
			bindService(it, conn, Context.BIND_AUTO_CREATE);
		}
	}

	private ICallbackResult callback = new ICallbackResult() {

		@Override
		public void OnBackResult(Object result) {
			// TODO Auto-generated method stub
			if ("finish".equals(result)) {
				finish();
				return;
			}
			int i = (Integer) result;
			mProgressBar.setProgress(i);
			// tv_progress.setText("当前进度 =>  "+i+"%");
			// tv_progress.postInvalidate();
			mHandler.sendEmptyMessage(i);
		}
	};

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			// tv_progress.setText("当前进度 ： " + msg.what + "%");
			System.out.println("当前进度 ： " + msg.what + "%");

		};
	};

	public interface ICallbackResult {
		public void OnBackResult(Object result);
	}
    public String mMyAccount;
    public String mSubscriber;
    public TextView mCallTitle;

    private StringBuffer mCallNumberText = new StringBuffer("");

    private String channelName = "channelid";


    private final int REQUEST_CODE = 0x01;

    public String str_call_phone;


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }


 /*   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MyLog.i( "onActivityResult requestCode: " + requestCode + " resultCode: " + resultCode);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getStringExtra("result").equals("finish")) {
                finish();
            }
        }
    }*/


    public void write_sharePreferencesrong(String rong_name, String rong_pass, String rong_token,String id,String img,String ry_video_id) {
        // TODO Auto-generated method stub
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("rong_name", rong_name);
        editor.putString("rong_pass", rong_pass);
        editor.putString("rong_token", rong_token);
        editor.putString("rong_id", id);
        editor.putString("rong_img", img);
        editor.putString("ry_video_id", ry_video_id);
        editor.commit();
    }
    public void write_sharePreferencesrongimg(String img) {
        // TODO Auto-generated method stub
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString("rong_img", img);
        editor.commit();
    }
    public String get_sharePreferences_ronguserid() {
        // TODO Auto-generated method stub
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        String token = preferences.getString("rong_id", "");
        return token;
    }

    public String get_sharePreferences_ronguserimg() {
        // TODO Auto-generated method stub
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        String token = preferences.getString("rong_img", "");
        return token;
    }

    public String get_sharePreferences_ronguser() {
        // TODO Auto-generated method stub
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        String token = preferences.getString("rong_name", "");
        return token;
    }

    public String get_sharePreferences_rongpas() {
        // TODO Auto-generated method stub
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        String token = preferences.getString("rong_pass", "");
        return token;

    }
    //融云的token
    public String get_sharePreferences_rongtoken() {
        // TODO Auto-generated method stub
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        String token = preferences.getString("rong_token", "");
        return token;
    }


    public String get_sharePreferences_rongkey() {
        // TODO Auto-generated method stub
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        String token = preferences.getString("rykey", "");
        return token;
    }

    public String get_sharePreferences_shengwangkey() {
        // TODO Auto-generated method stub
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        String token = preferences.getString("swkey", "");
        return token;
    }
    public String get_sharePreferences_zimeitiip() {
        //邓勇的 ip
        // TODO Auto-generated method stub
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        String token = preferences.getString("zimeiti_ip", "");
        return token;
    }
    public String get_sharePreferences_zimeitikey() {
        //邓勇的 ip
        // TODO Auto-generated method stub
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        String token = preferences.getString("zimeiti_key", "");
        return token;
    }
    public String get_sharePreferences_zimeitiupload() {
        //邓勇的的 上传
        // TODO Auto-generated method stub
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        String token = preferences.getString("zimeiti_upload", "");
        return token;
    }
    public boolean get_sharePreferences_caijian() {
        //邓勇的的 上传
        // TODO Auto-generated method stub
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        boolean token = preferences.getBoolean("is_caijian", false);
        return token;
    }
    public boolean get_sharePreferences_vidcaijian() {
        //邓勇的的 上传
        // TODO Auto-generated method stub
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        boolean token = preferences.getBoolean("is_vid_caijian", false);
        return token;
    }
    public String get_sharePreferences_rongip() {
        //松仁的 ip
        // TODO Auto-generated method stub
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        String token = preferences.getString("vsOutData6", "");
        return token;
    }

    public String get_sharePreferences_userinofo() {
        // TODO Auto-generated method stub
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        String token = preferences.getString("rong_userinfo", "");
        return token;
    }
    public void write_sharePreferUserToken(String token) {
        // TODO Auto-generated method stub
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_token", token);
        editor.commit();
    }
    public String get_sharePreferences_UserToken() {
        //松仁的token
        // TODO Auto-generated method stub
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        String token = preferences.getString("user_token", "");
        return token;
    }
    public String get_sharePreferences_ry_video_id() {
        // TODO Auto-generated method stub
        SharedPreferences preferences = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        String token = preferences.getString("ry_video_id", "");
        return token;
    }
    public  AlertDialog result_dialog;
    public  void result_dialog(String msg) {
        // TODO Auto-generated method stub

        result_dialog = new AlertDialog.Builder(mContext).create();
        result_dialog.show();
        result_dialog.getWindow().setContentView(R.layout.uploadresult_dialog);
        TextView tx= result_dialog.getWindow().findViewById(R.id.title);
        tx.setText(msg);
        result_dialog.getWindow().findViewById(R.id.cancel).setVisibility(View.GONE);
               /* .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        result_dialog.dismiss();
                    }
                });*/
        result_dialog.getWindow().findViewById(R.id.sure)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        result_dialog.dismiss();

                    }
                });
    }
}
