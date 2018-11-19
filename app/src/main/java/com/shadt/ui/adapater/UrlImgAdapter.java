package com.shadt.ui.adapater;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;


import com.shadt.caibian_news.R;
import com.shadt.ui.WebActivity;
import com.shadt.ui.widget.lbanners.LMBanners;
import com.shadt.ui.widget.lbanners.adapter.LBaseAdapter;

import java.util.List;

import io.rong.imageloader.core.ImageLoader;


/**
 * Created by luomin on 16/7/12.
 */
public class UrlImgAdapter implements LBaseAdapter<String> {
    private Context mContext;
    private List<String> url;
    public UrlImgAdapter(Context context,List<String> url) {
        mContext=context;
        this.url=url;
    }



    @Override
    public View getView(final LMBanners lBanners, final Context context, final int position, String data) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.id_image);
        ImageLoader.getInstance().displayImage(data,imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(mContext, WebActivity.class);
                it.putExtra("url",url.get(position));
                mContext.startActivity(it);
//                        MainActivity.this.startActivity(new Intent(MainActivity.this,SeconedAc.class));
            }
        });
        return view;
    }



}
