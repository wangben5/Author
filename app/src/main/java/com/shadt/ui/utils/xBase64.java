package com.shadt.ui.utils;

import android.util.Base64;

public class xBase64 {
	// 64加密
	public static String getBase64(String s) {
		String d = new String(Base64.encode(s.getBytes(), Base64.DEFAULT));
		return d;
	}
	// 64解密
	public static String getBase64frmat(String s) {
		String d = new String(Base64.decode(s.getBytes(), Base64.DEFAULT));
		return d;
	}
}
