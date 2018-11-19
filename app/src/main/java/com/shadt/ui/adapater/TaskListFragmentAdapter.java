package com.shadt.ui.adapater;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.shadt.caibian_news.R;
import com.shadt.ui.PictureActivity;
import com.shadt.ui.db.TaskInfo;
import com.shadt.ui.utils.help.OnDragListener;
import com.shadt.ui.utils.help.OnItemClickListener;
import com.shadt.ui.utils.help.OnMoveAndSwipedListener;
import com.shadt.ui.utils.help.OnStateChangedListener;
import com.shadt.util.MyLog;


/**
 * @description:历史记录的适配器
 * @author:袁东华 created at 2016/8/30 0030 下午 2:00
 */
public class TaskListFragmentAdapter extends Adapter<TaskListFragmentAdapter.ViewHolder> implements OnMoveAndSwipedListener {
    private OnItemClickListener mOnItemClickListener;
    private Activity activity;
    private TaskInfo mTaskInfo;
    private Handler handler;

    public TaskListFragmentAdapter(Activity activity, Handler handler) {
        this.activity = activity;
        this.handler = handler;
    }

    public void setList(TaskInfo mTaskInfo) {
        this.mTaskInfo = mTaskInfo;
        notifyDataSetChanged();
    }

    private OnDragListener onDragListener;

    public void setOnDragListener(OnDragListener onDragListener) {
        this.onDragListener = onDragListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = null;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itme_task, parent, false);
        viewHolder = new ViewHolder(view, mOnItemClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
     /*   holder.txt_content.setText(list.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it=new Intent(activity, Edite_txt_Activity.class);
                it.putExtra("name",list.get(position).getName());
                //新建还是编辑    false 是编辑
                it.putExtra("new",false);
                activity.startActivity(it);
            }
        });*/

        holder.address.setText("任务地点:" + mTaskInfo.getResult().getList().get(position).getAddress());
        holder.title.setText(mTaskInfo.getResult().getList().get(position).getTitle());
        holder.time.setText("创建时间:" + mTaskInfo.getResult().getList().get(position).getCtime());
        holder.zhipairen.setText("指派人:" + mTaskInfo.getResult().getList().get(position).getAssignUser());
        String name = "";
        for (int i = 0; i < mTaskInfo.getResult().getList().get(position).getExecuteUser().size(); i++) {
            if (i == mTaskInfo.getResult().getList().get(position).getExecuteUser().size() - 1) {
                name = name + mTaskInfo.getResult().getList().get(position).getExecuteUser().get(i).getName();
            } else {
                name = name + mTaskInfo.getResult().getList().get(position).getExecuteUser().get(i).getName() + ",";
            }
        }
        holder.zhixingren.setText("指派人:" + name);
        holder.zhuangtai.setText("状态:" + mTaskInfo.getResult().getList().get(position).getStatusName());
        holder.btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent();
                MyLog.i("id" + "" + mTaskInfo.getResult().getList().get(position).getTaskId());
                it.putExtra("id", "" + mTaskInfo.getResult().getList().get(position).getTaskId());
                it.putExtra("title", "" + mTaskInfo.getResult().getList().get(position).getTitle());
                activity.setResult(10086, it);
                activity.finish();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mTaskInfo != null ? mTaskInfo.getResult().getList().size() : 0;
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
//        Collections.swap(list, fromPosition, toPosition);
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

        //删除该条目的数据
  /*      FileUtils.deleteSingleFile(list.get(position).getAbsolutePath());
        list.remove(position);
        //删除recyclerView中的该条目
        notifyItemRemoved(position);

        if (list.size()==0){
            handler.sendEmptyMessage(1);
        }*/
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, OnStateChangedListener {

        TextView title;
        TextView time;
        TextView address;
        TextView zhipairen;
        TextView zhixingren;
        TextView zhuangtai;
        Button btn_ok;

        private OnItemClickListener onItemClickListener;


        public ViewHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            this.onItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.title);
            time = (TextView) itemView.findViewById(R.id.time);
            address = (TextView) itemView.findViewById(R.id.address);
            zhipairen = (TextView) itemView.findViewById(R.id.zhipairen);
            zhuangtai = (TextView) itemView.findViewById(R.id.zhuangtai);

            zhixingren = (TextView) itemView.findViewById(R.id.zhixingren);

            btn_ok = (Button) itemView.findViewById(R.id.btn_ok);

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
