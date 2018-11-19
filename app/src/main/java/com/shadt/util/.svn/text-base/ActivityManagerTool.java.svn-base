package com.shadt.util;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 用来关闭�?��的activity
 * 
 * @author HuNan
 *
 */
public class ActivityManagerTool extends Application {

	private List<Activity> activities = new LinkedList<Activity>();

	private static ActivityManagerTool manager;

	private boolean isExist = false;// activity 存在标志

	public static Class<?> indexActivity; // 首页�?��的activity�?��应的类名，必须在打开首页设置此项

	public static List<Class<?>> bottomActivities = new LinkedList<Class<?>>();// 底部导航类集�?

	/**
	 * 获得 activity管理对象
	 * 
	 * @return
	 */
	public static ActivityManagerTool getActivityManager() {
		if (null == manager) {
			manager = new ActivityManagerTool();
		}
		return manager;
	}

	/**
	 * 添加新的activity
	 * 
	 * @param activity
	 * @return
	 */
	public boolean add(Activity activity) {

		int position = 0;
		// 导航栏activity进栈，删除非导航栏activity
		if (isBottomActivity(activity)) {
			for (int i = 0; i < activities.size() - 1; i++) {

				if (!isBottomActivity(activities.get(i))) {
					popActivity(activities.get(i));
					i--;
				}
				if (i > 0) {
					// 获得重复activity位置
					if (activities.get(i).getClass()
							.equals(activity.getClass())) {
						isExist = true;
						position = i;
					}
				}
			}

			// //获得重复activity位置
			// for (int i = 0; i < activities.size() - 1; i++) {
			// if (activities.get(i).getClass().equals(activity.getClass())) {
			// isExist = true;
			// position = i;
			// }
			// }

		}

		if (!activities.add(activity)) {
			return false;
		}
		// 删除重复activity
		if (isExist) {
			isExist = false;
			activities.remove(position);
		}

		return true;
	}

	/**
	 * 关闭除参数activity外的�?��activity
	 * 
	 * @param activity
	 */
	public void finish(Activity activity) {
		for (Activity iterable : activities) {
			if (activity != iterable) {
				iterable.finish();
			}
		}
	}

	/**
	 * 关闭�?��的activity
	 */
	public void exit() {
		for (Activity activity : activities) {
			if (activity != null) {
				activity.finish();
			}
		}
		// System.out.println("�?��系统");
		System.exit(0);
	}

	/**
	 * 删除指定activity
	 * 
	 * @param activity
	 */
	public void popActivity(Activity activity) {

		if (activity != null) {
			activity.finish();
			activities.remove(activity);
		}

	}

	/**
	 * 获得当前activity
	 * 
	 * @return
	 */
	public Activity currentActivity() {
		Activity activity = activities.get(activities.size() - 1);

		return activity;
	}

	/**
	 * activity是否为底部导�?
	 * 
	 * @return
	 */
	public boolean isBottomActivity(Activity activity) {

		for (int i = 0; i < bottomActivities.size(); i++) {
			if (activity.getClass() != bottomActivities.get(i)) {

			} else {
				return true;
			}
		}

		return false;
	}

	/**
	 * 如需返回IndexActivity则返回IndexActivity
	 * 
	 * @param activity
	 * @param context
	 */
	public void backIndex(Context context) {

		if (activities.size() <= 0) {
			return;
		}

		if (isBottomActivity(activities.get(activities.size() - 1))) {
			Intent intent = new Intent();
			intent.setClass(context, indexActivity);
			context.startActivity(intent);
		}
	}

	/**
	 * 删除已经finish的activity
	 * 
	 * @param activity
	 */
	public void removeActivity(Activity activity) {

		if (activity != null) {
			activities.remove(activity);
		}
	}

	/**
	 * 初始化，存储底部导航�?
	 * 
	 * @param activityClass
	 */
	public void setBottomActivities(Class<?> activityClass) {
		if (activityClass != null) {
			bottomActivities.add(activityClass);
		}
	}
}
