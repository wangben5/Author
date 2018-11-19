package com.shadt.ui.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.shadt.caibian_news.R;
import com.shadt.util.MyLog;


public class FenbianlvPopupWindow extends PopupWindow implements View.OnClickListener {
    private LinearLayout ll_root;
    private Animation animationIn, animationOut;
    private boolean isDismiss = false;
    private RelativeLayout rela1, rela2, rela3, rela4, rela5;
    private ImageView img1, img2, img3, img4, img5;
    private FrameLayout fl_content;
    Context mContext;
    SharedPreferences preferences;
    int fenbianlv=0;
    public FenbianlvPopupWindow(Context context) {
        super(context);
        mContext=context;
        preferences = mContext.getSharedPreferences("user",
                Context.MODE_PRIVATE);
        fenbianlv = preferences.getInt("fenbianlv", 3);

        View inflate = LayoutInflater.from(context).inflate(R.layout.pop_fenbianl, null);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setBackgroundDrawable(new ColorDrawable());
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        this.setBackgroundDrawable(new ColorDrawable());
        this.setContentView(inflate);
        animationIn = AnimationUtils.loadAnimation(context, R.anim.up_in);
        animationOut = AnimationUtils.loadAnimation(context, R.anim.down_out);
        ll_root = (LinearLayout) inflate.findViewById(R.id.ll_root);
        fl_content = (FrameLayout) inflate.findViewById(R.id.fl_content);
        rela1 = (RelativeLayout) inflate.findViewById(R.id.fbl1);
        rela2 = (RelativeLayout) inflate.findViewById(R.id.fbl2);
        rela3 = (RelativeLayout) inflate.findViewById(R.id.fbl3);
        rela4 = (RelativeLayout) inflate.findViewById(R.id.fbl4);
        rela5 = (RelativeLayout) inflate.findViewById(R.id.fbl5);
        img1 = (ImageView) inflate.findViewById(R.id.img1);
        img2 = (ImageView) inflate.findViewById(R.id.img2);
        img3 = (ImageView) inflate.findViewById(R.id.img3);
        img4 = (ImageView) inflate.findViewById(R.id.img4);
        img5 = (ImageView) inflate.findViewById(R.id.img5);
        rela1.setOnClickListener(this);
        rela2.setOnClickListener(this);
        rela3.setOnClickListener(this);
        rela4.setOnClickListener(this);
        rela5.setOnClickListener(this);
        fl_content.setOnClickListener(this);



        if (fenbianlv==1){
            img1.setImageResource(R.drawable.circular_check);
        }else if(fenbianlv==2){
            img2.setImageResource(R.drawable.circular_check);
        }else if(fenbianlv==3){
            img3.setImageResource(R.drawable.circular_check);
        }else if(fenbianlv==4){
            img4.setImageResource(R.drawable.circular_check);
        }else if(fenbianlv==5){
            img5.setImageResource(R.drawable.circular_check);
        }
    }

    public void initresource() {
        img1.setImageResource(R.drawable.circular_not_check);
        img2.setImageResource(R.drawable.circular_not_check);
        img3.setImageResource(R.drawable.circular_not_check);
        img4.setImageResource(R.drawable.circular_not_check);
        img5.setImageResource(R.drawable.circular_not_check);
    }

     public void showAsDropDown(View parent,int selecd) {
        try {
             if (selecd==1){
                 initresource();
                 img1.setImageResource(R.drawable.circular_check);
             }else if(selecd==2){
                 initresource();
                 img2.setImageResource(R.drawable.circular_check);
             }else if(selecd==3){
                 initresource();
                 img3.setImageResource(R.drawable.circular_check);
             }else if(selecd==4){
                 initresource();
                 img4.setImageResource(R.drawable.circular_check);
             }else if(selecd==5){
                 initresource();
                 img5.setImageResource(R.drawable.circular_check);
             }

            if (Build.VERSION.SDK_INT >= 24) {
                int[] location = new int[2];
                parent.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1] + parent.getHeight();
                this.showAtLocation(parent, Gravity.BOTTOM, x, y);
            } else {
                this.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            }

            isDismiss = false;
            ll_root.startAnimation(animationIn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        if (isDismiss) {
            return;
        }
        isDismiss = true;
        ll_root.startAnimation(animationOut);
        dismiss();
        animationOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isDismiss = false;
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                    dismiss4Pop();
                } else {
                    FenbianlvPopupWindow.super.dismiss();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }




    private void dismiss4Pop() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                FenbianlvPopupWindow.super.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.fbl1) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(0);


                FenbianlvPopupWindow.super.dismiss();
            }
        }
        if (id == R.id.fbl2) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(1);

                FenbianlvPopupWindow.super.dismiss();
            }
        }
        if (id == R.id.fbl3) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(2);

                FenbianlvPopupWindow.super.dismiss();
            }
        }
        if (id == R.id.fbl4) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(3);


                FenbianlvPopupWindow.super.dismiss();
            }
        }
        if (id == R.id.fbl5) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(4);

                FenbianlvPopupWindow.super.dismiss();
            }
        }
        dismiss();

    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int positon);
    }
}
