package com.shadt.util;

public class Contacts {

	public static String AREA_ID = "editor";
	public static String UPDATA = "http://www.chinashadt.com:8010/ottadsys/servlet/AdsysServlet?interface=generalApp&vsDtype=10000";

	public static String IP = "/smartcommunityott";

	public static String Init_data = "http://shanghai.chinashadt.com:8010/ottadsys/servlet/AdsysServlet?interface=generalApp&vsDtype=20000";

	/*
	 * 登陆
	 */
	public static String LOGIN = IP + "/AppLogin";
	/*
	 * 获取新闻类别
	 */
	public static String GET_TYPE = IP + "/AppGetUserChannelList";
	/*
	 * 获取类别下的栏目
	 */
	public static String GET_CHANNEL = IP + "/AppGetChannelField";
	/*
	 * 提交图片
	 */
	public static String DO_PIC = IP + "/AppFileUpload";
	/*
	 * 提交整个新闻
	 */
	public static String DO_NEWS = IP + "/AppSaveRecord";

	/*
	 * 、获取当前用户可审核记录列表
	 */
	public static String GET_EXAMINE = IP + "/AppGetUserRecordList";
	/*
	 * 、获取当前用户已经审核记录列表
	 */
	public static String GET_EXAMINE_YES = IP + "/AppGetUserExamineRecordList";
	/*
	 * 获取文章接口
	 */
	public static String GET_NEWS_DETAIL = IP + "/AppGetRecord";
	/*
	 * 审核新闻
	 */
	public static String DO_EXAMINE_NEWS = IP + "/AppRecordToExamine";
	/*
	 * 、获取我的新闻列表
	 */
	public static String GET_MY_NEWS = IP + "/AppMyRecordList";
	/*
	 * 、更改我的新闻
	 */
	public static String DO_MY_NEWS = IP + "/AppUpdateRecord";
	/*
	 * 、修改密码
	 */
	public static String CHANGE_PWD = IP + "/AppUpdateUserPassword";
	/*
	 * 图文上传图片
	 */
	public static String  HUNBIAN_DO_PIC= IP + "/AppUeditorFileUpload";

	

}
