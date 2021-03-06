package com.shadt.adpter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shadt.bean.PindaoBean;
import com.shadt.caibian_news.R;

public class PopAdapter extends BaseAdapter {
	List<PindaoBean> List;
	Context context;
	public int selectedItem = -1;

	public PopAdapter(Context context, List<PindaoBean> List) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.List = List;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return List.size();
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

	public void setSelectedItem(int selectedItems) {
		this.selectedItem = selectedItems;
		notifyDataSetChanged();
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.pop_menu_list_item, null);
			holder.txt = (TextView) convertView.findViewById(R.id.txt);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.txt.setText(List.get(position).getTitle());
		Log.v("ceshi", "aaaa" + selectedItem);
		if (selectedItem == position) {
			Log.v("ceshi", "bbbb" + selectedItem);
			holder.txt.setTextColor(context.getResources().getColor(
					R.color.qianlan));
		} else {
			Log.v("ceshi", "ccccc" + selectedItem);
			holder.txt.setTextColor(context.getResources().getColor(
					R.color.black));
		}
		return convertView;
	}

	class ViewHolder {
		TextView txt;
	}
}
