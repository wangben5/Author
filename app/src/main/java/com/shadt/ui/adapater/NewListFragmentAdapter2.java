package com.shadt.ui.adapater;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shadt.caibian_news.R;
import com.shadt.ui.Zimeiti_Activity;
import com.shadt.ui.db.MediaInfo;

import io.rong.imageloader.core.DisplayImageOptions;


/**
 * Created by zhangxutong .
 * Date: 16/08/28
 */

public class NewListFragmentAdapter2 extends RecyclerView.Adapter<NewListFragmentAdapter2.ViewHolder> {
    protected Context mContext;
    protected MediaInfo mDatas;
    protected LayoutInflater mInflater;
    private DisplayImageOptions options;
    private RequestOptions Glideoptions;

    public NewListFragmentAdapter2(Context mContext, MediaInfo mTongxunlu) {
        this.mContext = mContext;
        this.mDatas = mTongxunlu;
        mInflater = LayoutInflater.from(mContext);

        Glideoptions = new RequestOptions()
                .centerCrop()
                .placeholder(R.color.color_f6)
                .error(R.drawable.miorentup)
                .placeholder(R.drawable.miorentup)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
    }

    public MediaInfo getDatas() {
        return mDatas;
    }

    public NewListFragmentAdapter2 setDatas(MediaInfo datas) {
        mDatas = datas;
        return this;
    }

    @Override
    public NewListFragmentAdapter2.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewListFragmentAdapter2.ViewHolder(mInflater.inflate(R.layout.item_newlistfragment, parent, false));
    }

    @Override
    public void onBindViewHolder(final NewListFragmentAdapter2.ViewHolder holder, final int position) {

        if (!TextUtils.isEmpty(mDatas.getDATA().get(position).get(7))) {
            if (mDatas.getDATA().get(position).get(7).equals("img")) {
                if (!TextUtils.isEmpty(mDatas.getDATA().get(position).get(23))) {
                    Glide.with(mContext)
                            .load(mDatas.getDATA().get(position).get(23))
                            .apply(Glideoptions)
                            .into(holder.avatar);
                }
                holder.text_user.setText("图片");
            } else if (mDatas.getDATA().get(position).get(7).equals("vid")) {
                if (!TextUtils.isEmpty(mDatas.getDATA().get(position).get(23))) {
                    Glide.with(mContext)
                            .load(mDatas.getDATA().get(position).get(23))
                            .apply(Glideoptions)
                            .into(holder.avatar);
                }
                holder.text_user.setText("视频");
            } else if (mDatas.getDATA().get(position).get(7).equals("aud")) {
                holder.text_user.setText("音频");
                if (!TextUtils.isEmpty(mDatas.getDATA().get(position).get(23))) {
                    Glide.with(mContext)
                            .load(mDatas.getDATA().get(position).get(23))
                            .apply(Glideoptions)
                            .into(holder.avatar);
                }
            } else if (mDatas.getDATA().get(position).get(7).equals("doc")) {
                holder.text_user.setText("文稿");
                 Glide.with(mContext)
                        .load(R.drawable.img_txt)
                        .apply(Glideoptions)
                        .into(holder.avatar);
            } else {
                holder.text_user.setText(mDatas.getDATA().get(position).get(7));
            }
        } else {

        }

        if (!TextUtils.isEmpty(mDatas.getDATA().get(position).get(1))) {
            holder.text_biaoqian.setText(mDatas.getDATA().get(position).get(1));
        } else {

        }
        if (!TextUtils.isEmpty(mDatas.getDATA().get(position).get(33))) {
            holder.text_time.setText(mDatas.getDATA().get(position).get(33));
        } else {

        }

        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(mContext, Zimeiti_Activity.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("content", mDatas);
                mBundle.putInt("position", position);
                it.putExtras(mBundle);
                mContext.startActivity(it);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.getDATA().size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text_biaoqian;
        TextView text_user;
        TextView text_time;
        ImageView avatar;
        LinearLayout content;


        public ViewHolder(View itemView) {
            super(itemView);

            avatar = ((ImageView) itemView
                    .findViewById(R.id.ivAvatar));
            text_biaoqian = (TextView) itemView
                    .findViewById(R.id.text_biaoqian);
            text_user = (TextView) itemView
                    .findViewById(R.id.text_user);
            text_time = (TextView) itemView
                    .findViewById(R.id.text_time);
            content = (LinearLayout) itemView
                    .findViewById(R.id.content);
        }
    }
}
