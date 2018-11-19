package com.shadt.ui.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class WebUtils {
	private static final String TAG = WebUtils.class.getSimpleName();
	private static WebUtils webUtils = new WebUtils();

	public synchronized static WebUtils getInstance() {
		return webUtils;
	}

	/**
	 * split value by &.
	 * 
	 * @param result
	 *            info from server.
	 * @return split result.
	 */
	public static String[] getResultToString(Object result) {
		String[] str = null;
		if (result != null && !result.equals("")) {
			str = result.toString().split("&");
		}

		return str;
	}

	/**
	 * 判断网络是否可用.
	 * 
	 * @param context
	 * @return boolean.
	 */
	public boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager)

			context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

}