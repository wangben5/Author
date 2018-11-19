package com.shadt.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;

public class BitmapUtils {
	public static Bitmap getBitmap(byte[] data) {
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}

	public static Bitmap getBitmap(byte[] data, int scale) {
		Options opts = new Options();
		opts.inSampleSize = scale;
		opts.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
	}

	public static Bitmap getBitmap(byte[] data, int width, int height) {
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, opts);
		int scaleX = opts.outWidth / width;
		int scaleY = opts.outHeight / height;
		int scale = scaleX > scaleY ? scaleX : scaleY;
		return getBitmap(data, scale);
	}

	public static Bitmap getBitmap(String path) {
		File file = new File(path);
		if (!file.getParentFile().exists()) {
			return null;
		}
		return BitmapFactory.decodeFile(path);
	}

	// ����ͼƬ��SD��ָ��·��
	public static void saveBitmap(Bitmap bitmap, String path) {
		// ���·�������ļ�����?
		File file = new File(path);
		// �����ļ������ڣ��򴴽����ļ�
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		// ����ָ����ļ������������
		FileOutputStream stream;
		try {
			stream = new FileOutputStream(file);
			// ����ͼƬ�����ļ�
			bitmap.compress(CompressFormat.JPEG, 100, stream);
		} catch (FileNotFoundException e) {
			Log.i("main", "save bitmap failure");
		}
	}

}
