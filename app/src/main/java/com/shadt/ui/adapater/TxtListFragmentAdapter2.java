package com.shadt.ui.adapater;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shadt.caibian_news.R;
import com.shadt.ui.Edite_txt_Activity;
import com.shadt.ui.utils.FileUtils;
import com.shadt.ui.utils.help.OnDragListener;
import com.shadt.ui.utils.help.OnItemClickListener;
import com.shadt.ui.utils.help.OnMoveAndSwipedListener;
import com.shadt.ui.utils.help.OnStateChangedListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;



/**
 * @description:历史记录的适配器
 * @author:袁东华 created at 2016/8/30 0030 下午 2:00
 */
public class TxtListFragmentAdapter2 extends Adapter<TxtListFragmentAdapter2.ViewHolder> implements OnMoveAndSwipedListener {
    private OnItemClickListener mOnItemClickListener;
    private Activity activity;
    private ArrayList<File> list;
    private Handler handler;

    public TxtListFragmentAdapter2(Activity activity, Handler handler) {
        this.activity = activity;
        this.handler = handler;
    }

    public void setList(ArrayList<File> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    private OnDragListener onDragListener;

    public void setOnDragListener(OnDragListener onDragListener) {
        this.onDragListener = onDragListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = null;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itme_txt, parent, false);
        viewHolder = new ViewHolder(view, mOnItemClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.txt_content.setText(list.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it=new Intent(activity, Edite_txt_Activity.class);
                it.putExtra("name",list.get(position).getName());
                //新建还是编辑    false 是编辑
                it.putExtra("new",false);
                activity.startActivity(it);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    /**
     * @param fromPosition
     * @param toPosition
     * @description:拖拽条目
     * @author:袁东华 created at 2016/8/30 0030 上午 11:26
     */
    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        //交换数据的位置
        Collections.swap(list, fromPosition, toPosition);
        //交换RecyclerView中条目的位置
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    /**
     * @param position
     * @description:删除条目
     * @author:袁东华 created at 2016/8/30 0030 上午 11:26
     */
    @Override
    public void onItemDelete(int position) {
        //删除该历史记录
//        FilmDataHttp.getInstance().resHistoryDelWt(list.get(position).getHistoryId(), handler,2,-2);
        //删除该条目的数据
        FileUtils.deleteSingleFile(list.get(position).getAbsolutePath());
        list.remove(position);
        //删除recyclerView中的该条目
        notifyItemRemoved(position);

        if (list.size()==0){
            handler.sendEmptyMessage(1);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,OnStateChangedListener {

        TextView txt_content;
        private OnItemClickListener onItemClickListener;

        public ViewHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            this.onItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
            txt_content = (TextView) itemView.findViewById(R.id.txt_content);

        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, getLayoutPosition());

            }
        }

        /**
         * @description:拖拽,滑动item时调用,可以做改变背景色操作
         * @author:袁东华 created at 2016/8/31 0031 下午 1:51
         */
        @Override
        public void onSelectedChanged() {
            itemView.setAlpha(0.5f);
        }

        /**
         * @description:条目正常状态:拖拽,滑动结束后,背景色恢复正常
         * @author:袁东华 created at 2016/8/31 0031 下午 2:37
         */
        @Override
        public void clearView() {

            itemView.setAlpha(1.0f);
        }
    }

    /**
     * @Description:设置条目点击监听,供外部调用
     */
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;

    }



}
