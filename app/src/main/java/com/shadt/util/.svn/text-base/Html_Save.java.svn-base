package com.shadt.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.jsoup.nodes.Document;

import android.text.format.Time;
import android.util.Log;

public class Html_Save {
	public static String html_save(	Document doc){
		String path = null;
		try{
		Time t=new Time();
        t.setToNow();
        int year=t.year;
        int month=t.month;
        int day=t.monthDay;
        int hour=t.hour;
        int minute=t.minute;
        int second=t.second;
        Log.i("mytag", "time==="+ year + month + day+hour+minute+second);
        String filename=""+year+month+day+hour+minute+second;
		 path = "/sdcard/News/image/"+filename+".html";
		FileOutputStream fos;
		fos = new FileOutputStream(path, true);
		OutputStreamWriter osw = new OutputStreamWriter(fos,
				"utf-8");
		String titles = "<!DOCTYPE html PUBLIC\" -//W3C//DTD XHTML 1.0 Transitional//EN\" \"quot http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
		StringBuffer htmlString = new StringBuffer();
		htmlString.append(titles);
		htmlString.append(doc.html());
		osw.write(htmlString.toString());
		osw.close();
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		return path;
	}
}
