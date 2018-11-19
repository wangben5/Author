package com.shadt.util;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class ConnServer extends AsyncTask<String, String, String> {

	/**
	 * 
	 * 判断服务器是否开启
	 * 
	 * @param path
	 *            网络服务器地址
	 * @return 服务器开启 服务器未开启
	 */

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub

		try {
			HttpGet get = new HttpGet(params[0]);
			Log.i("doInBack1...", params[0]);
			HttpResponse response = new DefaultHttpClient().execute(get);

			Log.i("doInBack2...", params[0]);
			if (response.getStatusLine().getStatusCode() == 200) {
				return "success";
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "ClientError";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "ServerError";
		}
		return "success";
	}
}