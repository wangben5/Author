package com.shadt.ui.adapater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;



import com.shadt.caibian_news.R;
import com.shadt.ui.widget.lbanners.LMBanners;
import com.shadt.ui.widget.lbanners.adapter.LBaseAdapter;

import java.util.List;

/**
 * Created by luomin on 16/7/12.
 */
public class LocalImgAdapter implements LBaseAdapter<Integer> {
    private Context mContext;
    private  List<String> url;
    public LocalImgAdapter(Context context, List<String> url) {
        mContext=context;
        this.url=url;
    }

    @Override
    public View getView(final LMBanners lBanners, final Context context, final int position, Integer data) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.id_image);
        imageView.setImageResource(data);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"111111111+"+url.get(position),0).show();
            }
        });
        return view;
    }


}
