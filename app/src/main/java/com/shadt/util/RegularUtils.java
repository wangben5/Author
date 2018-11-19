package com.shadt.util;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式
 */
public class RegularUtils {

	static final String LOG_TAG = "RegularUtils";

	/** 密码格式 */
	public static boolean isPasswordFormat(String str) {
		Pattern p = null;
		Matcher m = null;
		boolean b = false;
		String regexPassword = "[0-9A-Za-z]*";
		p = Pattern.compile(regexPassword);
		m = p.matcher(str);
		b = m.matches();
		return b;
	}

	/**
	 * 手机
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isMobile(String str) {
		Pattern p = null;
		Matcher m = null;
		boolean b = false;
		String regexMobile = "^(13[0-9]|14[0|1|2|3|4|5|6|7]|15[0-9]|16[0-9]|17[0-9]|18[0-9])\\d{8}$";
		p = Pattern.compile(regexMobile); // 验证手机号
		m = p.matcher(str);
		b = m.matches();
		return b;
	}

	/**
	 * 邮箱
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		boolean flag = false;
		try {
			String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}

		return flag;
	}

	/**
	 * 格式化数字
	 * 
	 * @param num
	 * @return
	 */
	public static String formatNumber(double num) {
		String format = new DecimalFormat("0.00").format(num);
		return format;
	}

	/** 是否字母组合 */
	public static boolean isAlpha(String str) {
		if (str == null) {
			return false;
		}
		byte abyte0[] = str.getBytes();
		for (int i = 0; i < abyte0.length; i++) {
			if ((abyte0[i] < 65 || abyte0[i] > 90)
					&& (abyte0[i] < 97 || abyte0[i] > 122)) {
				return false;
			}
		}
		return true;
	}

	/** 是否字母或数字组合 */
	public static boolean isAlphaNumeric(String s) {
		if (s == null) {
			return false;
		}
		byte abyte0[] = s.getBytes();
		for (int i = 0; i < abyte0.length; i++)
			if ((abyte0[i] < 48 || abyte0[i] > 57)
					&& (abyte0[i] < 65 || abyte0[i] > 90)
					&& (abyte0[i] < 97 || abyte0[i] > 122))
				return false;

		return true;
	}

	/** 是否纯数字 */
	public static boolean isNumeric(String s) {
		if (s == null) {
			return false;
		}
		byte abyte0[] = s.getBytes();
		if (abyte0.length == 0) {
			return false;
		}
		for (int i = 0; i < abyte0.length; i++) {
			if (abyte0[i] < 48 || abyte0[i] > 57) {
				return false;
			}
		}
		return true;
	}

	public static boolean havefuhao(String str1) {
		String s = "!@#$%^&*.~/{}|()'\"?><,.`+-=_[]:";
		for (int i = 0; i < s.toString().length(); i++) {
			if (str1.contains("" + s.charAt(i)) == true) {
				if (havezimu(str1) == true) {
					return true;
				} else if (havenum(str1) == true) {
					return true;
				} else {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean havezimu(String str1) {
		String s = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
		for (int i = 0; i < s.toString().length(); i++) {
			if (str1.contains("" + s.charAt(i)) == true) {
				return true;
			}
		}
		return false;
	}

	public static boolean havenum(String str1) {
		String s = "1234567890";
		for (int i = 0; i < s.toString().length(); i++) {
			if (str1.contains("" + s.charAt(i)) == true) {
				return true;
			}
		}
		return false;
	}
}
