package com.shadt.adpter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.shadt.bean.ExamineBean;
import com.shadt.bean.PindaoBean;
import com.shadt.caibian_news.R;
import com.shadt.util.Contacts;

public class ExamineAdapter extends BaseAdapter {
	List<ExamineBean> List;
	Context context;
	BitmapUtils bitmapUtils;
	String ip = "";

	public ExamineAdapter(Context context, List<ExamineBean> List, String ip) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.List = List;
		bitmapUtils = new BitmapUtils(context);
		bitmapUtils.configDefaultLoadFailedImage(R.drawable.img_no_pic);
		this.ip = ip;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (List != null) {
			return List.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return List.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.examine_item, null);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.user = (TextView) convertView.findViewById(R.id.user);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			holder.checkStatus = (TextView) convertView
					.findViewById(R.id.checkStatus);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 1：审核中 0 ：编辑 中 -1：待发布状态
		Log.v("ceshi", "ex_size:" + List.get(position).getUpdateUser());
		holder.title.setText(List.get(position).getRecordtitle());
		holder.user.setText(List.get(position).getUpdateUser());
		holder.time.setText(List.get(position).getUpdateTime());
		if (TextUtils.isEmpty(List.get(position).getCheckStatus())) {

		} else {
			if (List.get(position).getCheckStatus().equals("0")) {
				holder.checkStatus.setText("编辑中");
			} else if (List.get(position).getCheckStatus().equals("1")) {
				holder.checkStatus.setText("审核中");
			} else if (List.get(position).getCheckStatus().equals("-1")) {
				holder.checkStatus.setText("审核通过");
			}
		}
		
		Log.i("OTH", "image_url:"+List.get(position).getImg());
		
		if (TextUtils.isEmpty(List.get(position).getImg())) {
			Log.i("OTH", "image_url:1111111111111111");
			holder.img.setImageResource(R.drawable.img_no_pic);
		} else {
			
			if(List.get(position).getImg().contains("http") || List.get(position).getImg().contains("HTTP")){
				bitmapUtils.display(holder.img,List.get(position).getImg());
				Log.i("OTH", "image_url:22222222222");
			}else{
				bitmapUtils.display(holder.img,ip + Contacts.IP + "/" + List.get(position).getImg());
				Log.i("OTH", "image_url:33333333333333");
			}
			
			
		}
		return convertView;
	}

	class ViewHolder {
		TextView title, user, time, checkStatus;
		ImageView img;
	}

}
