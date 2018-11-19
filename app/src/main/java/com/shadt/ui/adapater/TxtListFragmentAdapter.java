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
import com.shadt.ui.Edite_txt_Activity;
import com.shadt.ui.Zimeiti_Activity;
import com.shadt.ui.db.MediaInfo;
import com.shadt.ui.utils.help.OnMoveAndSwipedListener;
import com.shadt.ui.widget.CircleImageView;
import com.shadt.util.MyLog;

import java.io.File;
import java.util.List;

import io.rong.imageloader.core.DisplayImageOptions;


/**
 * Created by zhangxutong .
 * Date: 16/08/28
 */

public class TxtListFragmentAdapter extends RecyclerView.Adapter<TxtListFragmentAdapter.ViewHolder> implements OnMoveAndSwipedListener {
    protected Context mContext;
    protected  List<File> mlist;
    protected LayoutInflater mInflater;
    private DisplayImageOptions options;
    private  RequestOptions Glideoptions;
    public TxtListFragmentAdapter(Context mContext, List<File> list) {
        this.mContext = mContext;
        this.mlist = list;
        mInflater = LayoutInflater.from(mContext);

        Glideoptions = new RequestOptions()
                .centerCrop()
                .placeholder(R.color.color_f6)
                .error(R.drawable.miorentup)
                .placeholder(R.drawable.miorentup)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

     }

    public List<File > getDatas() {
        return mlist;
    }

    public TxtListFragmentAdapter setDatas(List<File > list) {
        mlist = list;
        return this;
    }

    @Override
    public TxtListFragmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TxtListFragmentAdapter.ViewHolder(mInflater.inflate(R.layout.itme_txt, parent, false));
    }

    @Override
    public void onBindViewHolder(final TxtListFragmentAdapter.ViewHolder holder, final int position) {



        holder.txt_content.setText(mlist.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it=new Intent(mContext, Edite_txt_Activity.class);
                it.putExtra("name",mlist.get(position).getName());
                //新建还是编辑    false 是编辑
                it.putExtra("new",false);
                mContext.startActivity(it);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mlist != null ? mlist.size() : 0;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDelete(int position) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_content;



        public ViewHolder(View itemView) {
            super(itemView);
            txt_content = (TextView) itemView.findViewById(R.id.txt_content);
        }
    }
}
