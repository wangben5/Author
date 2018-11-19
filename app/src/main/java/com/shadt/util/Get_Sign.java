package com.shadt.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.shadt.wxpay.util.MD5;

public class Get_Sign {
	public static String paixu_map(Map<String, String> map, String token) {
		List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(
				map.entrySet());
		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		// 然后通过比较器来实现排序
		Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
			// 升序排序
			public int compare(Entry<String, String> o1,
					Entry<String, String> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		for (Map.Entry<String, String> mapping : list) {
			Log.v("ceshi", "mapping.getKey():" + mapping.getKey());
			signParams.add(new BasicNameValuePair(mapping.getKey(), mapping
					.getValue()));
		}
		String sign = genAppSign(signParams, token);
		return sign;
	}

	public static String genAppSign(List<NameValuePair> params, String token) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=" + token);
		String appSign = MD5.getMessageDigest(sb.toString().getBytes());
		exChange(appSign);
		appSign = exChange(appSign);
		Log.e("ceshi", "----" + appSign);
		return appSign;
	}

	public static String exChange(String str) {
		String s = str.toUpperCase();
		return s;
	}

	// 如果b ==true 表示有需更改的字符串
	public static String data(String string, String start, String end,
			boolean b, String str_before, String str_after) {

		String result = null;
		result = string.substring(string.indexOf(start) + start.length(),
				string.indexOf(end));
		if (b == true) {
			result = result.replace(str_before, str_after);
		} else {

		}
		Log.v("ceshi", "sub:" + result);
		return result;
	}
}
