package com.shadt.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import android.util.Log;
import android.util.Xml;
import com.shadt.activity.AddNewsActivity;
import com.shadt.activity.MyNewsDetailActivity;
import com.shadt.bean.ChannelBean;
import com.shadt.bean.ExamineBean;
import com.shadt.bean.PindaoBean;
import com.shadt.bean.ResultBean;
import com.shadt.bean.UpdateInfo;
import com.shadt.bean.UserBean;
import com.shadt.ui.db.UploadResultInfo;

/**
 * XML�ĵ�����������
 * 
 * @author Royal
 * 
 */
public class XMLParserUtil {


	public static ResultBean parse_result(InputStream is) throws Exception {
		List<ResultBean> mList = null;
		ResultBean beauty = null;
		// 由android.util.Xml创建一个XmlPullParser实例
		XmlPullParser xpp = Xml.newPullParser();
		// 设置输入流 并指明编码方式
		xpp.setInput(is, "UTF-8");
		// 产生第一个事件
		int eventType = xpp.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			// 判断当前事件是否为文档开始事件
			case XmlPullParser.START_DOCUMENT:
				mList = new ArrayList<ResultBean>(); // 初始化books集合
				break;
			// 判断当前事件是否为标签元素开始事件
			case XmlPullParser.START_TAG:

				if (xpp.getName().equals("xml")) { // 判断开始标签元素是否是book
					beauty = new ResultBean();
				}

				else if (xpp.getName().equals("return_msg")) {
					eventType = xpp.next();// 让解析器指向name属性的值
					// 得到name标签的属性值，并设置beauty的name
					beauty.setRetrun_msg(xpp.getText());
				} else if (xpp.getName().equals("return_code")) { // 判断开始标签元素是否是book
					eventType = xpp.next();// 让解析器指向age属性的值
					// 得到age标签的属性值，并设置beauty的age
					beauty.setRetrun_code(xpp.getText());
				}

				break;

			// 判断当前事件是否为标签元素结束事件
			case XmlPullParser.END_TAG:
				if (xpp.getName().equals("xml")) { // 判断结束标签元素是否是book
					// mList.add(beauty); // 将book添加到books集合
					// beauty = null;
				}
				break;

			}
			// 进入下一个元素并触发相应事件
			eventType = xpp.next();
		}
		return beauty;
	}

	public static UserBean parse(InputStream is) throws Exception {
		List<UserBean> mList = null;
		UserBean beauty = null;
		// 由android.util.Xml创建一个XmlPullParser实例
		XmlPullParser xpp = Xml.newPullParser();
		// 设置输入流 并指明编码方式
		xpp.setInput(is, "UTF-8");
		// 产生第一个事件
		int eventType = xpp.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			// 判断当前事件是否为文档开始事件
			case XmlPullParser.START_DOCUMENT:
				mList = new ArrayList<UserBean>(); // 初始化books集合
				break;
			// 判断当前事件是否为标签元素开始事件
			case XmlPullParser.START_TAG:

				if (xpp.getName().equals("xml")) { // 判断开始标签元素是否是book
					beauty = new UserBean();
				}

				else if (xpp.getName().equals("return_msg")) {
					eventType = xpp.next();// 让解析器指向name属性的值
					// 得到name标签的属性值，并设置beauty的name
					beauty.setRetrun_msg(xpp.getText());
				} else if (xpp.getName().equals("return_code")) { // 判断开始标签元素是否是book
					eventType = xpp.next();// 让解析器指向age属性的值
					// 得到age标签的属性值，并设置beauty的age
					beauty.setRetrun_code(xpp.getText());
				} else if (xpp.getName().equals("token")) { // 判断开始标签元素是否是book
					eventType = xpp.next();// 让解析器指向age属性的值
					// 得到age标签的属性值，并设置beauty的age
					beauty.setToken(xpp.getText());
				} else if (xpp.getName().equals("username")) { // 判断开始标签元素是否是book
					eventType = xpp.next();// 让解析器指向age属性的值
					// 得到age标签的属性值，并设置beauty的age
					beauty.setUser_name(xpp.getText());
					Log.v("ceshi", "name:" + xpp.getText());
				} else if (xpp.getName().equals("recordedit")) {
					eventType = xpp.next();// 让解析器指向age属性的值
					// 得到age标签的属性值，并设置beauty的age
					beauty.setRecordedit(xpp.getText());
				} else if (xpp.getName().equals("recordaudit")) {
					eventType = xpp.next();// 让解析器指向age属性的值
					// 得到age标签的属性值，并设置beauty的age
					beauty.setRecordaudit(xpp.getText());
				}
				break;

			// 判断当前事件是否为标签元素结束事件
			case XmlPullParser.END_TAG:
				if (xpp.getName().equals("xml")) { // 判断结束标签元素是否是book
					// mList.add(beauty); // 将book添加到books集合
					// beauty = null;
				}
				break;

			}
			// 进入下一个元素并触发相应事件
			eventType = xpp.next();
		}
		return beauty;
	}

	public static List<PindaoBean> parse_pindao(InputStream is)
			throws Exception {
		List<PindaoBean> mList = null;
		PindaoBean beauty = null;
		// 由android.util.Xml创建一个XmlPullParser实例
		XmlPullParser xpp = Xml.newPullParser();
		// 设置输入流 并指明编码方式
		xpp.setInput(is, "UTF-8");
		// 产生第一个事件
		int eventType = xpp.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			// 判断当前事件是否为文档开始事件
			case XmlPullParser.START_DOCUMENT:
				mList = new ArrayList<PindaoBean>(); // 初始化books集合
				break;
			// 判断当前事件是否为标签元素开始事件
			case XmlPullParser.START_TAG:

				if (xpp.getName().equals("Channel")) { // 判断开始标签元素是否是book
					beauty = new PindaoBean();
				}

				else if (xpp.getName().equals("id")) {
					eventType = xpp.next();// 让解析器指向name属性的值
					// 得到name标签的属性值，并设置beauty的name
					beauty.setId(xpp.getText());
				} else if (xpp.getName().equals("key")) { // 判断开始标签元素是否是book
					eventType = xpp.next();// 让解析器指向age属性的值
					// 得到age标签的属性值，并设置beauty的age
					beauty.setKey(xpp.getText());
				} else if (xpp.getName().equals("title")) {
					eventType = xpp.next();// 让解析器指向age属性的值
					// 得到age标签的属性值，并设置beauty的age
					beauty.setTitle(xpp.getText());
				}
				break;
			// 判断当前事件是否为标签元素结束事件
			case XmlPullParser.END_TAG:
				if (xpp.getName().equals("Channel")) { // 判断结束标签元素是否是book
					mList.add(beauty); // 将book添加到books集合
					beauty = null;
				}
				break;

			}
			// 进入下一个元素并触发相应事件
			eventType = xpp.next();
		}
		return mList;
	}

	public static List<ChannelBean> Channelparse(InputStream is)
			throws Exception {
		List<ChannelBean> mList = null;
		ChannelBean beauty = null;

		// 由android.util.Xml创建一个XmlPullParser实例
		XmlPullParser xpp = Xml.newPullParser();
		// 设置输入流 并指明编码方式
		xpp.setInput(is, "UTF-8");
		// 产生第一个事件
		int eventType = xpp.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			// 判断当前事件是否为文档开始事件
			case XmlPullParser.START_DOCUMENT:
				mList = new ArrayList<ChannelBean>(); // 初始化books集合
				break;
			// 判断当前事件是否为标签元素开始事件
			case XmlPullParser.START_TAG:

				if (xpp.getName().equals("field")) { // 判断开始标签元素是否是book
					beauty = new ChannelBean();
				} else if (xpp.getName().equals("fieldkey")) {
					eventType = xpp.next();// 让解析器指向name属性的值
					// 得到name标签的属性值，并设置beauty的name
					beauty.setFieldkey(xpp.getText());
				} else if (xpp.getName().equals("fieldtype")) { // 判断开始标签元素是否是book
					eventType = xpp.next();// 让解析器指向age属性的值
					// 得到age标签的属性值，并设置beauty的age
					beauty.setFieldtype(xpp.getText());
				} else if (xpp.getName().equals("fieldtitle")) { // 判断开始标签元素是否是book
					eventType = xpp.next();// 让解析器指向age属性的值
					// 得到age标签的属性值，并设置beauty的age
					beauty.setFieldtitle(xpp.getText());
				} else if (xpp.getName().equals("title")) { // 判断开始标签元素是否是book
					eventType = xpp.next();// 让解析器指向age属性的值
					// 得到age标签的属性值，并设置beauty的age
					beauty.setFieldtitle(xpp.getText());
				} else if (xpp.getName().equals("content")) { // 判断开始标签元素是否是book
					eventType = xpp.next();// 让解析器指向age属性的值
					// 得到age标签的属性值，并设置beauty的age
					beauty.setFieldcontext(xpp.getText());
				} else if (xpp.getName().equals("fieldType")) { // 判断开始标签元素是否是book
					eventType = xpp.next();// 让解析器指向age属性的值
					// 得到age标签的属性值，并设置beauty的age
					beauty.setFieldtype(xpp.getText());
				}
				break;

			// 判断当前事件是否为标签元素结束事件
			case XmlPullParser.END_TAG:
				if (xpp.getName().equals("field")) { // 判断结束标签元素是否是book
					mList.add(beauty); // 将book添加到books集合
					beauty = null;
				}
			}
			// 进入下一个元素并触发相应事件
			eventType = xpp.next();
		}
		return mList;
	}

	public static List<ExamineBean> examineparse(InputStream is)
			throws Exception {
		List<ExamineBean> mList = null;
		ExamineBean beauty = null;

		// 由android.util.Xml创建一个XmlPullParser实例
		XmlPullParser xpp = Xml.newPullParser();
		// 设置输入流 并指明编码方式
		xpp.setInput(is, "UTF-8");
		// 产生第一个事件
		int eventType = xpp.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			// 判断当前事件是否为文档开始事件
			case XmlPullParser.START_DOCUMENT:
				mList = new ArrayList<ExamineBean>(); // 初始化books集合
				break;
			// 判断当前事件是否为标签元素开始事件
			case XmlPullParser.START_TAG:

				if (xpp.getName().equals("record")) { // 判断开始标签元素是否是book
					beauty = new ExamineBean();
				} else if (xpp.getName().equals("id")) {
					eventType = xpp.next();// 让解析器指向name属性的值
					// 得到name标签的属性值，并设置beauty的name
					beauty.setId(xpp.getText());
				} else if (xpp.getName().equals("uuid")) { // 判断开始标签元素是否是book
					eventType = xpp.next();// 让解析器指向age属性的值
					// 得到age标签的属性值，并设置beauty的age
					beauty.setUuid(xpp.getText());
				} else if (xpp.getName().equals("updateTime")) { // 判断开始标签元素是否是book
					eventType = xpp.next();// 让解析器指向age属性的值
					// 得到age标签的属性值，并设置beauty的age
					beauty.setUpdateTime(xpp.getText());
				} else if (xpp.getName().equals("updateUser")) { // 判断开始标签元素是否是book
					eventType = xpp.next();// 让解析器指向age属性的值
					// 得到age标签的属性值，并设置beauty的age
					beauty.setUpdateUser(xpp.getText());
					Log.v("ceshi2", "xpp.getText():" + xpp.getText());
				} else if (xpp.getName().equals("recordTitle")) { // 判断开始标签元素是否是book
					eventType = xpp.next();// 让解析器指向age属性的值
					// 得到age标签的属性值，并设置beauty的age
					beauty.setRecordtitle(xpp.getText());
				} else if (xpp.getName().equals("recordAnimationImg")) {
					eventType = xpp.next();// 让解析器指向age属性的值
					// 得到age标签的属性值，并设置beauty的age
					beauty.setImg(xpp.getText());
				} else if (xpp.getName().equals("checkStatus")) {
					eventType = xpp.next();// 让解析器指向age属性的值
					// 得到age标签的属性值，并设置beauty的age
					beauty.setCheckStatus(xpp.getText());
				}
				break;

			// 判断当前事件是否为标签元素结束事件
			case XmlPullParser.END_TAG:
				if (xpp.getName().equals("record")) { // 判断结束标签元素是否是book
					mList.add(beauty); // 将book添加到books集合
					beauty = null;
				}
			}
			// 进入下一个元素并触发相应事件
			eventType = xpp.next();
		}
		return mList;
	}

	

	public static List<ChannelBean> parse_json2(String jsonData) {

		try {
			JSONArray arr = new JSONArray(jsonData);
			for (int i = 0; i < arr.length(); i++) {
				JSONObject temp = (JSONObject) arr.get(i);
				String key = temp.getString(MyNewsDetailActivity.channel_list2
						.get(Integer
								.parseInt(MyNewsDetailActivity.position_item
										.get(i).toString())).getFieldkey());
				MyNewsDetailActivity.channel_list2.get(
						Integer.parseInt(MyNewsDetailActivity.position_item
								.get(i).toString())).setFieldcontext(key);

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return MyNewsDetailActivity.channel_list2;
	}

	public static UpdateInfo Parser_version(String jsonStr)
			throws JSONException {
		JSONTokener jsonParser = new JSONTokener(jsonStr);
		JSONObject person;
		person = new JSONObject(jsonStr).getJSONObject("data");
		UpdateInfo main = new UpdateInfo();
		main.setVnResult(person.isNull("vnResult") ? "" : person
				.optString("vnResult"));
		main.setVsOutData0(person.isNull("vsOutData0") ? "" : person
				.optString("vsOutData0"));
		main.setVsOutData1(person.isNull("vsOutData1") ? "" : person
				.optString("vsOutData1"));
		main.setVsOutData2(person.isNull("vsOutData2") ? "" : person
				.optString("vsOutData2"));
		main.setVsOutData3(person.isNull("vsOutData3") ? "" : person
				.optString("vsOutData3"));
		main.setVsOutData4(person.isNull("vsOutData4") ? "" : person
				.optString("vsOutData4"));
		main.setVsOutData5(person.isNull("vsOutData5") ? "" : person
				.optString("vsOutData5"));
		main.setVsOutData6(person.isNull("vsOutData6") ? "" : person
				.optString("vsOutData6"));
		main.setVsOutData7(person.isNull("vsOutData7") ? "" : person
				.optString("vsOutData7"));
		main.setVsOutData8(person.isNull("vsOutData8") ? "" : person
				.optString("vsOutData8"));
		main.setVsOutData9(person.isNull("vsOutData9") ? "" : person
				.optString("vsOutData9"));
		main.setVsOutData10(person.isNull("vsOutData10") ? "" : person
				.optString("vsOutData10"));
		main.setVsOutData11(person.isNull("vsOutData9") ? "" : person
				.optString("vsOutData11"));
		main.setVsOutData12(person.isNull("vsOutData12") ? "" : person
				.optString("vsOutData12"));
		main.setVsOutData13(person.isNull("vsOutData9") ? "" : person
				.optString("vsOutData13"));
		main.setVsOutData14(person.isNull("vsOutData14") ? "" : person
				.optString("vsOutData14"));
		main.setVsOutData15(person.isNull("vsOutData15") ? "" : person
				.optString("vsOutData15"));
		main.setVsResultmsg(person.isNull("vsResultmsg") ? "" : person
				.optString("vsResultmsg"));
		return main;
	}
	public static List<String> parse_img(String jsonData){
		List<String> str=new ArrayList<String>();
		JSONArray arr;
		try {
			arr = new JSONArray(jsonData);
			for (int i = 0; i < arr.length(); i++) {  
			    JSONObject temp = (JSONObject) arr.get(i);  
			    String id = temp.isNull("Ueditor")?"":temp.getString("Ueditor");  
			    str.add(id);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return str;
	}
	public static String html_start = "<!DOCTYPE html>\n" + "<html lang=\"en\">\n"
			+ "<head>\n" + "<meta charset=\"UTF-8\">\n"
			+ "<title>Document</title>\n" + "</head>\n" + "<body>\n";
	public static String html_end = "</body>\n" + "</html>\n";
	public static ArrayList<String> listimg;
	public static String parsehtml(String htmlcontent) {
		Document doc = Jsoup.parse(html_start + htmlcontent + html_end);
		Elements eles = doc.getElementsByTag("div");// 将a标签的列表存储成元素集合
		listimg = new ArrayList<String>();
		Elements pngs = doc.select("img");
		// 遍历元素
		for (Element e : pngs) {
			String src = e.attr("src");// 获取img中的src路径
			listimg.add(src);
		}
		if (listimg.size()!=0) {
			return listimg.get(0);
		}else{
			return null;
		}
		
	}


    public static List<UploadResultInfo> parse_upload_result(InputStream is)
            throws Exception {
        List<UploadResultInfo> mList = null;
        UploadResultInfo beauty = null;
        // 由android.util.Xml创建一个XmlPullParser实例
        XmlPullParser xpp = Xml.newPullParser();
        // 设置输入流 并指明编码方式
        xpp.setInput(is, "UTF-8");
        // 产生第一个事件
        int eventType = xpp.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                // 判断当前事件是否为文档开始事件
                case XmlPullParser.START_DOCUMENT:
                    mList = new ArrayList<UploadResultInfo>(); // 初始化books集合
                    break;
                // 判断当前事件是否为标签元素开始事件
                case XmlPullParser.START_TAG:

                    if (xpp.getName().equals("Response")) { // 判断开始标签元素是否是book
                        beauty = new UploadResultInfo();
                    }

                    else if (xpp.getName().equals("responsecode")) {
                        eventType = xpp.next();// 让解析器指向name属性的值
                        // 得到name标签的属性值，并设置beauty的name
                        beauty.setResponsecode(xpp.getText());
                    } else if (xpp.getName().equals("message")) { // 判断开始标签元素是否是book
                        eventType = xpp.next();// 让解析器指向age属性的值
                        // 得到age标签的属性值，并设置beauty的age
                        beauty.setMessage(xpp.getText());
                    }else if(xpp.getName().equals("assetid")){
                        eventType = xpp.next();// 让解析器指向name属性的值
                        // 得到name标签的属性值，并设置beauty的name
                        beauty.setAssetid(xpp.getText());
                    }
                    break;
                // 判断当前事件是否为标签元素结束事件
                case XmlPullParser.END_TAG:
                    if (xpp.getName().equals("Response")) { // 判断结束标签元素是否是book
                        mList.add(beauty); // 将book添加到books集合
                        beauty = null;
                    }
                    break;

            }
            // 进入下一个元素并触发相应事件
            eventType = xpp.next();
        }
        return mList;
    }

}