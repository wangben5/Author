package com.shadt.ui.interfaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import com.shadt.activity.ImagePagerActivity;
import com.shadt.util.MyLog;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

public class AndroidJSInterface {
	// 如果要重写 js 方法 ，api版本 17 以上的 就要 写 注解 @JavascriptInterface
	public static final String ACTION_INTENT_TEST = "webplayer";
	public static String ACTION_INTENT_GO_LOGIN = "com.shadt.shanghai_go_login";
	public static String ACTION_INTENT_TEST1 = "shanghai_share";
	public static String ACTION_INTENT_SHARE_ACTIVITY = "shanghai";
	public static String ACTION_INTENT_CAMEARA = "shanghai";
	public static String ACTION_INTENT_WEBTIXIAN = "shanghai"; //web 微信提现  发票二维码红包
	public static String ACTION_INTENT_FACEONLINE = "com.shadt.shanghai_faceonline";

	private Context mContext;
	private String[] imageUrls;
	List<String> str;
	
	public AndroidJSInterface(Context context) {
		mContext = context;
	}

	// 需要解析的网页字符串
	/**
	 * 获取需要放大的图片
	 * @param content
	 */
	@JavascriptInterface
	public void PicturebrowseA(String content) {
		Log.v("ceshi", content);
		if (content.startsWith("<|*|>")) {
			content=content.substring(5);
		}
		if (!TextUtils.isEmpty(content)) {
			imageUrls=content.split("\\<\\|\\*\\|\\>");
		}
	}
	int a=0;

	/**
	 * 点击放大图片
	 * @param img
	 */
	@JavascriptInterface
	public void openImage(String img) {
		// imageUrls=StringUtils.returnImageUrlsFromHtml("");
		a=0;
		for (int i = 0; i < imageUrls.length; i++) {
			if(imageUrls[i].toString().contains("_JMP")){

			}else if (img.equals(imageUrls[i].toString()) || img.contains(imageUrls[i].toString())) {
				a=i;
				Intent intent = new Intent();
				intent.putExtra("image_urls", imageUrls);
				intent.putExtra("image_index", a);
				intent.setClass(mContext, ImagePagerActivity.class);
				mContext.startActivity(intent);

				break;
			}
		}

	}

	@JavascriptInterface
	public void to_share() {
		Intent intent = new Intent(ACTION_INTENT_SHARE_ACTIVITY);
		mContext.sendBroadcast(intent);
	}

	// 跳转到播放器
	@JavascriptInterface
	public void showToast2(String s) {
		// web_player.path = s;
		// Intent intent = new Intent(ACTION_INTENT_TEST);
		// mContext.sendBroadcast(intent);
	}

	@JavascriptInterface
	public String getInterface() {
		return "android";
	}


	/**
	 * 用户提现请求
	 * @return
	 */
	@JavascriptInterface
	public void weChatTX () {

		MyLog.i("发起微信提现广播");
		Intent intent = new Intent(ACTION_INTENT_WEBTIXIAN);
		mContext.sendBroadcast(intent);

	}
	
	
	/**
	 * 调起摄像头
	 */
	@JavascriptInterface
	public void camera() {
		Log.v("ceshi", "摄像头");
		
		Intent intent = new Intent(ACTION_INTENT_CAMEARA);
		mContext.sendBroadcast(intent);
		
	}
	
	/**
	 * 获取位置信息
	 * @return
	 */
	@JavascriptInterface
	public String getLocation() {
		SharedPreferences preferences = mContext.getSharedPreferences("user",
				Context.MODE_PRIVATE);
		String location = preferences.getString("location", "");
		String longitude = preferences.getString("x", "");
		String latitude = preferences.getString("y", "");
		return "{'location':'" + location + "','longitude':'" + longitude
				+ "','latitude':'" + latitude + "'}";
	}


	/**
	 * 获取用户信息
	 * @return
	 */
	@JavascriptInterface
	public String getUserInfo() {
		

		
		SharedPreferences preferences = mContext.getSharedPreferences("user",
				Context.MODE_PRIVATE);
		String rong_userinfo = preferences.getString("rong_userinfo", "");
        MyLog.i("JS获取用户信息*******************************"+rong_userinfo);
		return rong_userinfo ;
		
	}
	
	/**七果游戏获取用户信息*/
	@JavascriptInterface
	public String getUserInfo_game() {
		SharedPreferences preferences = mContext.getSharedPreferences("user",
				Context.MODE_PRIVATE);
		String username = preferences.getString("name", "");
		String userphone = preferences.getString("phone", "");
		String userimg = preferences.getString("imghead", "");
		String userpassword = preferences.getString("password", "");
		String userid = preferences.getString("id", "");
//		return "{Appid:" + userid + ",userImg:" + userimg + ",userName:" + username +"}";
		 String JS_UserInfo = null ;
		try {
			
			JSONObject jsonObject = new JSONObject();
			
			jsonObject.put("Appid", userid);
			jsonObject.put("userImg", userimg);
			jsonObject.put("userName", username);
			JS_UserInfo = jsonObject.toString();
		} catch (JSONException e) {
			Log.i("OTH", "JS获取用户信息错误");
		}
		
		
		return JS_UserInfo ;
	}
	



	/**
	 * 通知登录2   登录成功之后  会刷新页面
	 */
	@JavascriptInterface
	public void goLogin() {
		Intent intent = new Intent(ACTION_INTENT_GO_LOGIN);
		Log.v("ceshi", "登录");
		mContext.sendBroadcast(intent);
	}

	/*X
	 * @JavascriptInterface public String getAppid(){ return
	 * "{'Appid':'"+Myurl.Area_id+"'}";
	 * 
	 * }
	 */
	public boolean isWifi(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkINfo = cm.getActiveNetworkInfo();
		if (networkINfo != null
				&& networkINfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	public boolean iswifi() {
		return isWifi(mContext);
	}

	// 跳转到 人脸识别
	@JavascriptInterface
	public void FaceOnline(String FaceValue) {

		Intent intent = new Intent();
		intent.setAction(ACTION_INTENT_FACEONLINE);
		intent.putExtra("faceValue",FaceValue);
		mContext.sendBroadcast(intent);
	}

	//返回人脸识别信息
	@JavascriptInterface
	public String getIdcardInfo() {

		SharedPreferences preferences = mContext.getSharedPreferences("user",
				Context.MODE_PRIVATE);
		String idcardusername = preferences.getString("idcardusername", "");
		String idcardnumber = preferences.getString("idcardnumber", "");
		String idcardsigndate = preferences.getString("idcardsigndate", "");
		String idcardExpirydate = preferences.getString("idcardExpirydate", "");
		String idcardside = preferences.getString("idcardside", "");
		String idcardbirthday = preferences.getString("idcardbirthday", "");
		String idcaeraddress = preferences.getString("idcaeraddress", "");
		String idcardgender = preferences.getString("idcardgender", "");
		String idcardethnic = preferences.getString("idcardethnic", "");
		String JS_UserInfo = null ;
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("idcardusername", idcardusername);
			jsonObject.put("idcardnumber", idcardnumber);
			jsonObject.put("idcardsigndate", idcardsigndate);
			jsonObject.put("idcardExpirydate", idcardExpirydate);
			jsonObject.put("idcardside", idcardside);
			jsonObject.put("idcardbirthday",idcardbirthday );
			jsonObject.put("idcaeraddress",idcaeraddress );
			jsonObject.put("idcardgender", idcardgender);
			jsonObject.put("idcardethnic", idcardethnic);
			JS_UserInfo = jsonObject.toString();
		} catch (JSONException e) {
			Log.i("OTH", "JS获取用户信息错误");
		}
//		Log.v("aaa",JS_UserInfo);
		return JS_UserInfo ;
	}

}
