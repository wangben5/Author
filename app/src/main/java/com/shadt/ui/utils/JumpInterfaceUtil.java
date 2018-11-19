package com.shadt.ui.utils;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import com.shadt.activity.CaptureActivity;

import com.shadt.application.MyApp;

import com.shadt.ui.WebActivity;
import com.shadt.util.MyLog;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


/**
 * @author wangliang
 * 点击跳转到那个界面  监听字段判断跳转位置
 */
public class JumpInterfaceUtil {

	private static String mtype;
	public static boolean isOurIP(String url){

		if(url.contains("chinashadt")
					|| url.contains("192.168.2")  //方便公司测试
					|| url.contains("xinzouping")  //邹平
					|| url.contains("app.qatv") //迁安（）貌似不用了
					|| url.contains("app.qdntv") //黔东南
					|| url.contains("app.xygdcm") //孝义
					|| url.contains("zhtrapp") //铜仁
					|| url.contains("app.sxgd") //沙县 app.sxgd.org.cn
					|| url.contains("lxtvapp") //滦州
				    || url.contains("guangdianyun") // 奥点云直播

					){

			return  true ;
		}

		return  false ;
	}


	/**
	 * @param context  上下文
	 * @param url     监听的字段或网址
	 * @param mPublicDialog   对应页面的loading Dialog   没有的话填null
	 * @param view   webactivity中对应页面的WebView   没有的话填null
	 * @return  网页端 需要返回
	 */
	public static boolean setData (final Context context ,String url ,RelativeLayout mPublicDialog , WebView view ){

		//测试用 不用时删掉
//		url = "callapp:toDownload;http://hnly.chinashadt.com/parkinglot.apk" ;
//		url = "callapp:toPalyVideo;http://hbzq.chinashadt.com:1937/live/1013.stream_360p/playlist.m3u8?myTokenPrefixCustomParameter=78&myTokenPrefixendtime=1531018994&myTokenPrefixhash=j5cAYiTdwRRFCJ4SczdVjv29puuMRy8vfSTBvtE-Kqc=" ;
//		url = "callapp:toPalyVideo;http://sdbz.chinashadt.com:2035/vod/_definst_/mp4:1binchengxianfeng/20180611binchengxianfeng.mp4/playlist.m3u8?myTokenPrefixCustomParameter=402&myTokenPrefixendtime=1531022847&myTokenPrefixhash=ZNP1wxegj6h669mziyiD0t2njFmLZ7etUsunOvNnKJw=" ;

		MyLog.w("监听到字段："+ url);

		if(TextUtils.isEmpty(url)){
			MyLog.i("跳转地址为空");
			return true;
		}

		if(url.startsWith("http") && url.endsWith(".apk")){  //.apk结尾的文件，交给自带的浏览器取下载

			try {
				Intent intent= new Intent();
			    intent.setAction("android.intent.action.VIEW");
			    Uri content_url = Uri.parse(url);
			    intent.setData(content_url);
			    context.startActivity(intent);
			    MyLog.i("交给自带的浏览器去处理");
				return false;

			} catch (Exception e) {
				// TODO: handle exception

			    return true ;

			}

		}


		if(url.startsWith("supervlite")){//跳转视频通话   汶上     以后需要搞个对应的 专门对接第三方
			try {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				context.startActivity(intent);
				return true;
			} catch (Exception e) { //失败的话 去下载
				// TODO: handle exception

				Intent intent= new Intent();
			    intent.setAction("android.intent.action.VIEW");
			    Uri content_url = Uri.parse("https://www.pgyer.com/supervlitea");
			    intent.setData(content_url);
			    context.startActivity(intent);
			    MyLog.i("交给自带的浏览器去处理");
			    return true ;

			}

		}

		if (url.startsWith("alipays") || url.startsWith("alipay")) { //支付宝支付
			try {
				context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
			} catch (Exception e) {
				// TODO: handle exception
				AlertDialog.Builder build =	new AlertDialog.Builder(context)
						.setMessage("未检测到支付宝客户端，请安装后重试。")
						.setPositiveButton("立即安装",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(
											DialogInterface dialog,
											int which) {
										// TODO Auto-generated method
										// stub
										Uri alipayuri = Uri.parse("https://d.alipay.com");
										context.startActivity(new Intent("android.intent.action.VIEW",alipayuri));
									}
								}).setNegativeButton("取消", null);

				if(context != null && build != null){
					build.show() ;
				}
				return true;

			}
			return true;
		}

		if (url.startsWith("weixin://wap/pay?")) {   //微信支付
			try {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(url));
				context.startActivity(intent);

			} catch (Exception e) {
				// TODO: handle exception
				Toast.makeText(context, "您还没有安装微信，请先安装微信客户端", Toast.LENGTH_SHORT).show();

				return true;

			}
			return true;
		}

		if(url.contains("wx.tenpay.com") ){
			return false ;
		}

		if (url.contains("callapp:zhibo")) { //跳转老板直播
//
//			Intent it = new Intent(context, Zhibo_Activity.class);
////			Intent it = new Intent(context, NewZhiBoActivity.class);
//			context.startActivity(it);


		} else if (url.contains("callapp:newzhibo")) { //跳转新版直播

			/*Intent it = new Intent(context, NewZhiBoActivity.class);
			context.startActivity(it);*/

		} else if (url.contains("callapp:dianbo")) { //跳转点播

			/*Intent it = new Intent(context, Dianbo_Activitys.class);
			context.startActivity(it);
*/
		} else if (url.equals("callapp:djdianbo")) {//浏阳党建专用
		/*	Intent it = new Intent(context, Dianbo_Activitys.class);
			it.putExtra("isDJ", true);
			context.startActivity(it);*/

		} else if (url.contains("Callsm")  && url.contains("token") ) { //跳转党建签到
			SharedPreferences preferences = context.getSharedPreferences("user",
					Context.MODE_PRIVATE);
			String phone = preferences.getString("phone", "");
			Intent it = new Intent(context, CaptureActivity.class);
			it.putExtra("is_qiandao", true);
			it.putExtra("phone", phone);
			context.startActivity(it);

		} else if (url.contains("Callsm")) { //跳转二维码扫描

			Intent it = new Intent(context, CaptureActivity.class);
			context.startActivity(it);

		} else if (url.contains("callcms")) { //跳转随手拍

			// 获取 share 存储 用户的 信息
			SharedPreferences preferences = context.getSharedPreferences(
					"user", Context.MODE_PRIVATE);
			String userid = preferences.getString("id", "");
			String address = preferences.getString("location", "");
			String channelKey = "" ;
			if(url .contains("callcms:") && url .length() > 8){
				try {
					channelKey = url.substring(url.indexOf(":")+1, url.length());
				} catch (Exception e) {
					// TODO: handle exception
					channelKey = "";
				}
			}else{
				channelKey = "";
			}

			if (TextUtils.isEmpty(userid)) {
				Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
			} else {
				// 随手拍
			/*	Intent it = new Intent(context, MainActivity.class);
				it.putExtra("userid", userid);
				it.putExtra("address", address);
				it.putExtra("channelKey", channelKey);
				context.startActivity(it);*/
			}

		} else if (url.contains("calldp:qb") || url.contains("calldp:dl")) { //跳转单独的点播页面

//			url = "calldp:qb:e253d99a-773d-485c-b830-b67344001fc9:平江新闻" ;
//			url = "calldp:dl:da9dc36a-3260-4c26-a62c-c9e4faaeaa56:九月" ;
		/*	Intent it = new Intent();
			String str = url.substring(url.indexOf(":") + 1, url.length());
			String[] vodData = str.split(":");
			if (vodData != null && vodData.length >= 3) {
				it.putExtra("vodData", vodData);
				it.setClass(context, DianBoNewsActivity.class);
				context.startActivity(it);
			}else{
				return false ;
			}*/

		} else if (url.contains("callapp;BasePage") ) {  // 跳转原生二级页面

			Intent it = new Intent();
			String str = url.substring(url.indexOf(";") + 1, url.length());
			String[] pageData = str.split(";");
		/*	if (pageData != null && pageData.length >= 3) {
				it.putExtra("pageData", pageData);
				it.setClass(context, BasePagerActivity.class);
				context.startActivity(it);
			}*/

		} else if (url.contains("callapp:integral")) { //调转我的积分

			// 获取 share 存储 用户的 信息
		/*	SharedPreferences preferences = context.getSharedPreferences(
					"user", Context.MODE_PRIVATE);
			String userid = preferences.getString("id", "");
			// 我的积分
			if (!TextUtils.isEmpty(userid)) {
				Intent score = new Intent(context,
						my_score_resourceAcitivty.class);
				context.startActivity(score);
			} else {
				Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
			}*/

		}else if(url.contains("callQMXB")){   //跳转全民寻宝

			/*isAreaOpened(context ,Myurl.Area_id ,mPublicDialog);*/

		}else if(url.contains("callapp:WeChatTX")){  //跳转微信提现

			// 获取 share 存储 用户的 信息
		/*	SharedPreferences preferences = context.getSharedPreferences(
								"user", Context.MODE_PRIVATE);
			String userid = preferences.getString("id", "");
			// 我的积分
			if (!TextUtils.isEmpty(userid)) {
				Intent it = new Intent(context, WeChatTiXianActivity.class);
				context.startActivity(it);
			} else {
				Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
			}*/

		}else if(url.contains("callapp:ZNKJ")){  //跳转智能看家


		}else if(url.contains("callapp:Game")){  // 跳转游戏

			Toast.makeText(context, "暂未开通此功能，敬请期待...", Toast.LENGTH_SHORT).show();

		}else if(url.contains("callapp:Lision")){ //跳转聆视界

			Toast.makeText(context, "暂未开通此功能，敬请期待...", Toast.LENGTH_SHORT).show();

		}else if(url.contains("callapp:IntegralMall")){ //跳转积分商城

		/*	if (TextUtils.isEmpty(com.shadt.activity.MainActivity.shop_score_url)) {
				Toast.makeText(context, "未开放功能", Toast.LENGTH_SHORT).show();
			} else {
				Intent its = new Intent(context, WebShopActivity.class);
				// shop_score_url="http://192.168.0.121:8080/shadt/appController/home.do";
				its.putExtra("url", com.shadt.activity.MainActivity.shop_score_url);
				context.startActivity(its);
			}
*/
		}else if(url.contains("callapp:electrombile")){ //跳转电动车

			/*SharedPreferences preferences = context.getSharedPreferences(
					"user", Context.MODE_PRIVATE);
			String my_id = preferences.getString("id", "");
			userPhone = preferences.getString("phone", "");
			if (!TextUtils.isEmpty(my_id)) {//1、是否登录 2、是否绑定手机 3、有无信息
				if(userPhone!=null && userPhone.length() != 0){
					if(mPublicDialog != null){
						mPublicDialog.setVisibility(View.VISIBLE);
					}
					isUserExist(context , mPublicDialog) ;
				}else{
					BycleNoticeContentDialog bycleNoticeContentDialog = new BycleNoticeContentDialog(context, "绑定手机" );
					bycleNoticeContentDialog.show();
				}

			} else {
				Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show();
			}*/

		}else if(url.contains("callapp:AnyChatVideoCall")){ //视频通话

/*
			startVieoCall(context);
*/


		}else if(url.contains("callapp:AnyChatCallCenter")){ //视频呼叫系统

		/*	JumpInterfaceInfo.startCallCenter(context);*/


		}else if(url.contains("callapp:AnyChatMeeting")){ //视频会议

			try {

				/*跳转视频会议*/
				String userName = "" ;
				if(TextUtils.isEmpty(userName)){
					userName = "" ;
				}
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("userName", userName);
				intent.setClassName("com.com.shadt.ui.example.anychatmeeting_zp","com.bairuitech.main.LoginActivity");
				intent.putExtras(bundle);
				context.startActivity(intent);

			} catch (Exception e) {
				// TODO: handle exception
				Toast.makeText(context, "您还未安装视频会议插件", Toast.LENGTH_SHORT).show();
			}

		}else if(url.contains("xxtx.cszhjt.com") || url.contains("callapp:parking") ){ //跳转浏阳停车场 callapp:parking

//			startParking(context);
		/*	JumpInterfaceInfo.startParking(context);*/

		}else if(url.contains("callapp:GridManage")){ //跳转浏阳青村网格化管理


		}else if(url.startsWith("tel:")){ //跳转拨打电话

			try {
				Intent intent = new Intent(Intent.ACTION_DIAL);
				Uri data = Uri.parse(url);
				intent.setData(data);
				context.startActivity(intent);
			} catch (Exception e) {
				Log.i("OTH", "返回的拨号格式不对，当前的格式为"+ url +".正确格式为tel:15022222222");
				return true;
			}

		}else if(url.contains("life.ccb")){
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {  //19
				Intent intent= new Intent();
			    intent.setAction("android.intent.action.VIEW");
			    Uri content_url = Uri.parse(url);
			    intent.setData(content_url);
			    context.startActivity(intent);
			    MyLog.i("交给自带的浏览器去处理");
			    return true ;
			}
		}else if(url.contains("callapp:shortVideo")){

		/*	JumpInterfaceInfo.startShortVideo(context);*/

		}else if(url.contains("callapp:toDownload")){

//			callapp:toDownload;http://hnly.chinashadt.com/parkinglot.apk

			String myurl = url ;
			// 转码
			try {
				myurl = URLDecoder.decode(url, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				myurl = url ;
			}

			MyLog.i("解码前："+url);
			MyLog.i("解码后："+myurl);


			if(url.contains("/") && url.contains(";") && !url.endsWith("/")&& !url.endsWith(";")){
				try {

					String  downloadUrl = url.substring(url.lastIndexOf(";")+1);
					//获取解码后的中文名
					String  downloadFileName = myurl.substring(myurl.lastIndexOf("/")+1) ;

//					showUpdateDialog(context, 3, "文件下载", "准备下载："+ downloadFileName+"\n\n文件将保存至/sdcard/A_QCZL_Download/" , downloadUrl);


//					Intent intent = new Intent(context, DownloadMainActivity.class);
//					intent.putExtra("loadUrl", downloadUrl);
//					intent.putExtra("fileName", downloadFileName);
//					context.startActivity(intent);

				} catch (Exception e) {
					// TODO: handle exception
					Toast.makeText(context, "提示：下载路径不正确。", Toast.LENGTH_SHORT).show();
				}


			}else{
				Toast.makeText(context, "提示：下载路径不正确", Toast.LENGTH_SHORT).show();
				return true ;
			}


		}else if(url.startsWith("http")   // 链接直接是播放地址 直接跳转播放器播放
				&& (url.endsWith(".m3u8") ||  url.endsWith(".mp4")     )){

			try {

				/*if(TbsVideo.canUseTbsPlayer(context) ){
					TbsVideo.openVideo(context , url );
				}*/

			} catch (Exception e) {
				MyLog.i("播放路径异常");
			}

		}else if(url.contains("callapp:toPalyVideo")){

			if(url.contains("callapp:toPalyVideo;") &&  url.contains("http") ){
				try {

					String videourl = url.substring(url.indexOf(";")+1 ,url.length());

					if(!TextUtils.isEmpty(videourl) && videourl.startsWith("http")  ){
					/*	if(TbsVideo.canUseTbsPlayer(context) ){
							TbsVideo.openVideo(context ,videourl );
						}*/
					}

				} catch (Exception e) {
					MyLog.i("播放路径异常");
				}


			}

		}


		else {

			//七果游戏要加
		/*	if(url.contains("guoyouxi")){
				if (!url.contains("onapp=yes")) {
					if (url.contains("?")) {
						url = url + "&onapp=yes";
					} else {
						url = url + "?onapp=yes";
					}
				}

				if(view == null){
					Intent it = new Intent(context, WebActivity.class);
					it.putExtra("url", url);
					it.putExtra("type","游戏");
					context.startActivity(it);
				}else{
					MyLog.i("跳转地址："+url);
					view.loadUrl(url);
				}

				return true ;

			}*/
			//跳转积分商城
			/*if(url.contains("home.do")){

				//自己的服务器要加
				if (!url.contains("onapp=yes")) {
					if (url.contains("?")) {
						url = url + "&onapp=yes&uuid="+ GetUUID.getMyUUID(context);
					} else {
						url = url + "?onapp=yes&uuid="+ GetUUID.getMyUUID(context);
					}
				}
				MyLog.i("跳转地址："+url);
				Intent it = new Intent(context, WebShopActivity.class);

				it.putExtra("url", url);
				context.startActivity(it);
				return true ;
			}*/

			//自己的服务器
			if (isOurIP(url)) {
				//自己的服务器要加
				if (!url.contains("onapp=yes")) {
					if (url.contains("?")) {
						url = url + "&onapp=yes&";
					} else {
						url = url + "?onapp=yes";
					}
				}
				MyLog.i("跳转地址："+url);

				if (url.startsWith("http")|| url.startsWith("www")) {

					if(view == null){
						Intent it = new Intent(context, WebActivity.class);
						it.putExtra("url", url);

						it.putExtra("type",mtype);
						context.startActivity(it);
					}else{


						if(url .contains("guangdianyun")){

							return  false ;

						}else{
							view.loadUrl(url);
						}



					}


				}

			}else{ //第三方服务器

				MyLog.i("跳转地址："+url);

				if (url.startsWith("http")|| url.startsWith("www")) {
					if(view == null){
						Intent it = new Intent(context, WebActivity.class);
						it.putExtra("url", url);

						it.putExtra("type",mtype);
						context.startActivity(it);
					}else{
//						view.loadUrl(url);
						return false ;  // 不做处理 交由网页自己处理
					}


				}

			}



		}

		return true ;
	}



	/**
	 * 获取指定包名的版本号
	 *
	 * @param context
	 *            本应用程序上下文
	 * @param packageName
	 *            你想知道版本信息的应用程序的包名
	 * @return
	 * @throws Exception
	 */
	public static String getVersionName(Context context, String packageName) throws Exception {
	    // 获取packagemanager的实例
	    PackageManager packageManager = context.getPackageManager();
	    PackageInfo packInfo = packageManager.getPackageInfo(packageName, 0);
	    String version = packInfo.versionName;
	    return version;
	}








	public static void setDate2(final Context context ,String url ,RelativeLayout mPublicDialog , WebView view ,String type){

		mtype=type;
		setData( context , url , mPublicDialog ,  view);

	}
}
