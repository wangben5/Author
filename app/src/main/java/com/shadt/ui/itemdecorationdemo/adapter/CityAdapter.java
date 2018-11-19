package com.shadt.ui.itemdecorationdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shadt.application.MyApp;
import com.shadt.caibian_news.R;
import com.shadt.ui.DeatailFriendActivity;
import com.shadt.ui.contacts.Contact;
import com.shadt.ui.db.Tongxunlu;
import com.shadt.ui.widget.CircleImageView;


/**
 * Created by zhangxutong .
 * Date: 16/08/28
 */

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {
    protected Context mContext;
    protected Tongxunlu mDatas;
    protected LayoutInflater mInflater;

    public CityAdapter(Context mContext, Tongxunlu mTongxunlu) {
        this.mContext = mContext;
        this.mDatas = mTongxunlu;
        mInflater = LayoutInflater.from(mContext);

    }

    public Tongxunlu getDatas() {
        return mDatas;
    }

    public CityAdapter setDatas(Tongxunlu datas) {
        mDatas = datas;
        return this;
    }

    @Override
    public CityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_city, parent, false));
    }

    @Override
    public void onBindViewHolder(final CityAdapter.ViewHolder holder, final int position) {

        holder.tvCity.setText(mDatas.getResult().get(position).getName());

        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDatas.getResult().get(position).getName().equals("聊天室")) {
                    io.rong.imkit.RongIM.getInstance().startConversation(mContext, io.rong.imlib.model.Conversation.ConversationType.CHATROOM, "" + position, "聊天室");

                } else {
                    Intent it = new Intent(mContext, DeatailFriendActivity.class);
                    Bundle mBundle = new Bundle();
                    Tongxunlu.ResultBean mResultbean = mDatas.getResult().get(position);
                    mBundle.putSerializable("content", mResultbean);
                    it.putExtras(mBundle);
                    mContext.startActivity(it);
                }
//
            }
        });
        if (mDatas.getResult().get(position).getName().equals("聊天室")) {
            holder.avatar.setImageResource(R.drawable.img_chat_room);
        } else {
            if (!TextUtils.isEmpty("" + mDatas.getResult().get(position).getPortraituri())) {

                Glide.with(mContext)
                        .load(Contact.rong_ip + mDatas.getResult().get(position).getPortraituri())
                        .apply(MyApp.getGlideOptions())
                        .into(holder.avatar);

            }
        }
//        holder.avatar.setImageResource(R.drawable.de_default_portrait);
    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.getResult().size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCity;
        CircleImageView avatar;
        View content;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCity = (TextView) itemView.findViewById(R.id.tvCity);
            avatar = (CircleImageView) itemView.findViewById(R.id.ivAvatar);
            content = itemView.findViewById(R.id.content);
        }
    }
}
