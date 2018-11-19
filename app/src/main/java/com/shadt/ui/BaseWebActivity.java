package com.shadt.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.shadt.ui.interfaces.PermissionListener;
import com.shadt.util.CustomDialog2;
import com.shadt.util.LoadingDialog;
import com.shadt.util.WebUtils;

import java.util.ArrayList;
import java.util.List;

/**
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

public abstract class BaseWebActivity extends Activity  {
	private AlertDialog.Builder adb;
	TextView title;// 顶部标题栏
	LinearLayout line_back;
	private LoadingDialog dialog;
	WebUtils webutils;
	public CustomDialog2.Builder builder;
	public String ACTION_FINISH = "finish";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 判断是否全屏 0未全屏 其他为不全屏
		// 全屏
		// //透明导航栏 沉嵌是
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow()
				.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  //状态栏透明
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);    //导航栏透明
	}
	

	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    if (newConfig.fontScale != 1)//非默认值
	        getResources();    
	    super.onConfigurationChanged(newConfig);
	}

	@Override
	public Resources getResources() {
	     Resources res = super.getResources();
	     if (res.getConfiguration().fontScale != 1) {//非默认值
	        Configuration newConfig = new Configuration();       
	        newConfig.setToDefaults();//设置默认        
	        res.updateConfiguration(newConfig, res.getDisplayMetrics()); 
	     }    
	     return res;
	}


	private PermissionListener mlistener ;

	/**
	 * 权限申请
	 * @param permissions 待申请的权限集合
	 * @param listener  申请结果监听事件
	 */
	public void requestRunTimePermission(String[] permissions,PermissionListener listener){
		this.mlistener = listener;

		//用于存放为授权的权限
		List<String> permissionList = new ArrayList<>();
		//遍历传递过来的权限集合
		for (String permission : permissions) {
			//判断是否已经授权
			if (ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
				//未授权，则加入待授权的权限集合中
				permissionList.add(permission);
			}
		}

		//判断集合
		if (!permissionList.isEmpty()){  //如果集合不为空，则需要去授权
			ActivityCompat.requestPermissions(this,permissionList.toArray(new String[permissionList.size()]),1);
		}else{  //为空，则已经全部授权
			listener.onGranted();
		}
	}

	/**
	 * 权限申请结果
	 * @param requestCode  请求码
	 * @param permissions  所有的权限集合
	 * @param grantResults 授权结果集合
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case 1:
				if (grantResults.length > 0){
					//被用户拒绝的权限集合
					List<String> deniedPermissions = new ArrayList<>();
					//用户通过的权限集合
					List<String> grantedPermissions = new ArrayList<>();
					for (int i = 0; i < grantResults.length; i++) {
						//获取授权结果，这是一个int类型的值
						int grantResult = grantResults[i];

						if (grantResult != PackageManager.PERMISSION_GRANTED){ //用户拒绝授权的权限
							String permission = permissions[i];
							deniedPermissions.add(permission);
						}else{  //用户同意的权限
							String permission = permissions[i];
							grantedPermissions.add(permission);
						}
					}

					if (deniedPermissions.isEmpty()){  //用户拒绝权限为空
						mlistener.onGranted();
					}else {  //不为空
						//回调授权成功的接口
						mlistener.onDenied(deniedPermissions);
						//回调授权失败的接口
						mlistener.onGranted(grantedPermissions);
					}
				}
				break;
			default:
				break;
		}
	}

}
