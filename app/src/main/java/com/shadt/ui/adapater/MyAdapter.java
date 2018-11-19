package com.shadt.ui.adapater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shadt.caibian_news.R;
import com.shadt.ui.db.WenjianjiaInfo;

public class MyAdapter extends BaseAdapter {


    private Context context;
    private WenjianjiaInfo list;

    public MyAdapter(Context context, WenjianjiaInfo list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.getDATA().size();
    }


    @Override
    public Object getItem(int position) {
        return position;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list, null);
            vh.textview = (TextView) convertView.findViewById(R.id.tv_name);
            vh.img = (ImageView) convertView.findViewById(R.id.ivAvatar);

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.textview.setText(list.getDATA().get(position).get(1) + "");
        vh.img.setImageResource(R.drawable.format_bar_button_ol_highlighted);
        return convertView;
    }

    static class ViewHolder {
        TextView textview;
        ImageView img;
    }
}

