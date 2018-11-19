package com.shadt.wxpay.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Get {
	public static byte[] getPictureData(URL url) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// 设置 URL 请求的方法
		conn.setRequestMethod("GET");
		// 设置一个指定的超时值（以毫秒为单位），
		conn.setConnectTimeout(5 * 1000);
		InputStream inStream = conn.getInputStream();
		byte[] data = readInputStream(inStream);
		String html = new String(data);
		return data;
	}

	public static String getPictureData2(URL url) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// 设置 URL 请求的方法
		conn.setRequestMethod("GET");
		// 设置一个指定的超时值（以毫秒为单位），
		conn.setConnectTimeout(5 * 1000);
		InputStream inStream = conn.getInputStream();
		byte[] data = readInputStream(inStream);
		String html = new String(data);
		return html;
	}

	private static byte[] readInputStream(InputStream inStream)
			throws IOException {
		// TODO Auto-generated method stub
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		inStream.close();
		return outStream.toByteArray();
	}
}