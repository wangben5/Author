package com.shadt.ui.adapater;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shadt.application.MyApp;
import com.shadt.caibian_news.R;
import com.shadt.ui.db.MediaInfo;
import com.shadt.util.MyLog;

import io.rong.imageloader.core.DisplayImageOptions;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imageloader.core.display.FadeInBitmapDisplayer;


/**
 * Created by zhangxutong .
 * Date: 16/08/28
 */

public class NewListFragmentAdapter extends BaseAdapter {
    protected Context mContext;
    protected MediaInfo mDatas;
    protected LayoutInflater mInflater;
    private  DisplayImageOptions options;

    public NewListFragmentAdapter(Context mContext, MediaInfo mTongxunlu) {
        this.mContext = mContext;
        this.mDatas = mTongxunlu;
        mInflater = LayoutInflater.from(mContext);
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.miorentup)
                .showImageOnFail(R.drawable.miorentup)
                .showImageOnLoading(R.drawable.miorentup)
                .displayer(new FadeInBitmapDisplayer(300))
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }


    @Override
    public int getCount() {
        return mDatas.getDATA().size();
    }

    @Override
    public Object getItem(int i) {
        return mDatas.getDATA().get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHodel hodel = null;
        if (convertView == null) {
            hodel = new ViewHodel();
            convertView = mInflater.inflate(R.layout.item_newlistfragment,
                    null);
            hodel.avatar = ((ImageView) convertView
                    .findViewById(R.id.ivAvatar));
            hodel.text_biaoqian = (TextView) convertView
                    .findViewById(R.id.text_biaoqian);
            hodel.text_user = (TextView) convertView
                    .findViewById(R.id.text_user);
            hodel.text_time = (TextView) convertView
                    .findViewById(R.id.text_time);
            convertView.setTag(hodel);
        } else {
            hodel = (ViewHodel) convertView.getTag();
        }
        if (!TextUtils.isEmpty(mDatas.getDATA().get(position).get(7))) {
             if (mDatas.getDATA().get(position).get(7).equals("img")){
                hodel.text_user.setText("文件类型:图片");
            }else if(mDatas.getDATA().get(position).get(7).equals("vid")){
                hodel.text_user.setText("文件类型:视频");
            }else if(mDatas.getDATA().get(position).get(7).equals("aud")){
                hodel.text_user.setText("文件类型:音频");
            }else{
                 hodel.text_user.setText("文件类型:"+mDatas.getDATA().get(position).get(7));
             }
        }else{
            hodel.text_user.setText("文件类型:");
        }
        if (!TextUtils.isEmpty(mDatas.getDATA().get(position).get(1))) {

            hodel.text_biaoqian.setText("名称:"+mDatas.getDATA().get(position).get(1));
        }
        if (!TextUtils.isEmpty(mDatas.getDATA().get(position).get(33))) {
            hodel.text_time.setText("时间:"+mDatas.getDATA().get(position).get(33));
        }
        if (!TextUtils.isEmpty(mDatas.getDATA().get(position).get(23))) {

            ImageLoader.getInstance().displayImage(mDatas.getDATA().get(position).get(23), hodel.avatar, options);
        }
        return convertView;
    }

    class ViewHodel {
        TextView text_biaoqian;
        TextView text_user;
        TextView text_time;
        ImageView avatar;
    }
}
