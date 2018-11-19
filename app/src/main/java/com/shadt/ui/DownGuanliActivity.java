package com.shadt.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.shadt.caibian_news.R;
import com.shadt.ui.download.DownloadInfo;
import com.shadt.ui.download.DownloadJobListener;
import com.shadt.ui.download.DownloadManager;
import com.shadt.ui.utils.FileManager;
import com.shadt.util.MyLog;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class DownGuanliActivity extends BaseActivity {

    private boolean isEditMode;
    private View hint;
    private View header;
    private View footer;
    private TextView summary;
    private TextView select;
    private DownloadAdapter adapter;
    private RecyclerView recyclerView;
    private List<DownloadInfo> downloads;
    private DownloadManager manager;
    private FileManager fileManager;
    private DownloadJobListener jobListener = new DownloadJobListener() {
        @Override
        public void onCreated(DownloadInfo info) {
            //nothing to do
        }

        @Override
        public void onStarted(DownloadInfo info) {
            //nothing to do
        }

        @Override
        public void onCompleted(boolean finished, DownloadInfo info) {
            if (finished) {
                if (fileManager.isTxt(fileManager.getExtension(info.name)) == false) {
                    downloads.add(0, info);
                    if (isEditMode) adapter.checks.add(0, false);
                    adapter.notifyItemInserted(0);
                    updateUI();
                }
            }
        }
    };
    String type;
    private List<LocalMedia> selectImages = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downguanli);
        type = getIntent().getStringExtra("type");
        Bundle bundle = getIntent().getBundleExtra("content");
        selectImages = (List<LocalMedia>) bundle.getSerializable(PictureConfig.EXTRA_SELECT_LIST);
        MyLog.i("typed222" + type+"selectImages"+selectImages.size());
        downloads = new ArrayList<>();

        fileManager = new FileManager(this);
        manager = DownloadManager.getInstance();
        manager.addDownloadJobListener(jobListener);
        List<DownloadInfo> infos = manager.getAllInfo();
        for (DownloadInfo info : infos) {
            if (!info.isFinished()) continue;
            if (type.equals("1")) {
                if (fileManager.isImg(fileManager.getExtension(info.name)) == true) {
                    downloads.add(info);
                }
            } else if (type.equals("2")) {
                if (fileManager.isVideo(fileManager.getExtension(info.name)) == true) {
                    downloads.add(info);
                }
            } else if (type.equals("3")) {
                MyLog.i(fileManager.getExtension(info.name) + ">" + fileManager.isMusic(fileManager.getExtension(info.name)));
                if (fileManager.isMusic(fileManager.getExtension(info.name)) == true) {
                    downloads.add(info);
                }
            }

        }
        initPages();
    }


    @Override
    public void initPages() {


        line_back = (LinearLayout) findViewById(R.id.line_back);
        line_back.setOnClickListener(this);
        title = findViewById(R.id.title);
        title.setText("我的下载");
        hint = findViewById(R.id.download_hint);
        header = findViewById(R.id.download_header);
        footer = findViewById(R.id.download_footer);
        summary = (TextView) findViewById(R.id.download_summary);
        select = (TextView) findViewById(R.id.download_select);
        recyclerView = (RecyclerView) findViewById(R.id.download_recycler_view);
        select.setOnClickListener(this);
        findViewById(R.id.download_delete).setOnClickListener(this);
        findViewById(R.id.download_manager).setOnClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DownloadAdapter();
        recyclerView.setAdapter(adapter);
        updateUI();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        manager.removeDownloadJobListener(jobListener);
        downloads.clear();
    }


    private void updateUI() {
        Resources resources = getResources();
        summary.setText(resources.getString(R.string.download_summary, downloads.size()));
        boolean hasDownloads = downloads.size() > 0;
        if (!hasDownloads) {
            if (header.getVisibility() == View.VISIBLE) header.setVisibility(View.GONE);
            if (recyclerView.getVisibility() == View.VISIBLE) recyclerView.setVisibility(View.GONE);
            if (hint.getVisibility() != View.VISIBLE) hint.setVisibility(View.VISIBLE);
        } else {
            if (header.getVisibility() != View.VISIBLE) header.setVisibility(View.VISIBLE);
            if (recyclerView.getVisibility() != View.VISIBLE)
                recyclerView.setVisibility(View.VISIBLE);
            if (hint.getVisibility() == View.VISIBLE) hint.setVisibility(View.GONE);
        }
    }

    private void enterEditMode() {
        if (isEditMode) return;
        isEditMode = true;
        header.setVisibility(View.GONE);
        footer.setVisibility(View.VISIBLE);
        adapter.enterEditMode();
    }

    private void exitEditMode() {
        if (!isEditMode) return;
        isEditMode = false;
        header.setVisibility(View.VISIBLE);
        footer.setVisibility(View.GONE);
        adapter.exitEditMode();
    }

    private void deleteSelections() {
        final List<DownloadInfo> selections = adapter.getSelections();
        if (selections.size() == 0) {
            Toast.makeText(this, R.string.download_select_waring, Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (selectImages.size()+selections.size()>9){
                Toast.makeText(this, "最多选择9张图片", Toast.LENGTH_SHORT).show();
                return;
            }
            for (int i=0;i<selections.size();i++){
                MyLog.i("》》》》"+selections.get(i).path);
                LocalMedia mLocalMedia = new LocalMedia();
                String extras = selections.get(i).extras;//下载地址
                String path = selections.get(i).path;
                 String name = selections.get(i).name;
                mLocalMedia.setPictureType(isType(name));
                mLocalMedia.setPath(path);
                if (mtype2 == 1) {
                    if (get_sharePreferences_caijian()==true){
                        mLocalMedia.setCompressPath(path);
                    }else{
                        mLocalMedia.setPath(path);
                    }

                    mLocalMedia.setMimeType(1);
                } else if (mtype2 == 2) {
                    mLocalMedia.setDuration(getVideoDuration(path));

                    mLocalMedia.setMimeType(2);
                } else if (mtype2 == 3) {
                    mLocalMedia.setMimeType(3);
                    mLocalMedia.setDuration(getVideoDuration(path));
                }
                mLocalMedia.setPosition(1);
                mLocalMedia.setNum(1);
                selectImages.add(mLocalMedia);
             }

            Intent it = new Intent();
            Bundle mBundle = new Bundle();


            mBundle.putSerializable(PictureConfig.EXTRA_SELECT_LIST, (Serializable) selectImages);
            MyLog.i("最后多大："+selections.size());
            it.setAction("update");
            it.putExtra("content", mBundle);
            sendBroadcast(it);
            finish();
        }

    }

    @Override
    public void onClickListener(View v) {
        int id = v.getId();
        if (R.id.download_manager == id) {
            enterEditMode();
        } else if (R.id.download_delete == id) {
            deleteSelections();
        } else if (R.id.download_select == id) {
            adapter.select();
        } else if (R.id.line_back == id) {
            if (isEditMode) {
                exitEditMode();
            } else {
                finish();
            }
        }
    }

    private class DownloadAdapter extends RecyclerView.Adapter<DownloadViewHolder> {

        private LayoutInflater inflater = LayoutInflater.from(getApplication());
        private SimpleDateFormat format = new SimpleDateFormat(getResources().getString(R.string.download_date_format), Locale.CHINA);
        private List<Boolean> checks;

        @Override
        public DownloadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = inflater.inflate(R.layout.downloaded_list_item, parent, false);
            return new DownloadViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(DownloadViewHolder holder, int position) {
            if (isEditMode) {
                if (holder.check.getVisibility() != View.VISIBLE)
                    holder.check.setVisibility(View.VISIBLE);
                if (holder.close.getVisibility() == View.VISIBLE) {
                    holder.close.setVisibility(View.GONE);
                }
                holder.check.setChecked(checks.get(position));
            } else {
                if (holder.check.getVisibility() == View.VISIBLE)
                    holder.check.setVisibility(View.GONE);
                if (holder.close.getVisibility() != View.VISIBLE) {
                    holder.close.setVisibility(View.GONE);
                }
            }
            DownloadInfo info = downloads.get(position);
            holder.name.setText(info.name);
            MyLog.i("info" + info.path);
            holder.timestamp.setText(format.format(new Date(info.createTime)));
            holder.size.setText(String.format(Locale.US, "%.1fMB", info.contentLength / 1048576.0f));
            String extension = fileManager.getExtension(info.name);
            if (fileManager.isApk(extension)) {
                holder.icon.setImageResource(R.drawable.format_apk);
            } else if (fileManager.isMusic(extension)) {
                holder.icon.setImageResource(R.drawable.format_music);
            } else if (fileManager.isVideo(extension)) {
                holder.icon.setImageResource(R.drawable.format_vedio);
            } else if (fileManager.isZip(extension) || fileManager.isRar(extension)) {
                holder.icon.setImageResource(R.drawable.format_zip);
            } else if (fileManager.isImg(extension)) {
                holder.icon.setImageResource(R.drawable.format_img);
            } else {
                holder.icon.setImageResource(R.drawable.format_unknown);
            }
        }

        private void updateMenu() {
            if (isAllSelected()) {
                select.setText(R.string.label_select_none);
            } else {
                select.setText(R.string.label_select_all);
            }
        }

        private boolean isAllSelected() {
            for (Boolean bool : checks) {
                if (!bool) return false;
            }
            return true;
        }

        private void select() {
            boolean next = !isAllSelected();
            Collections.fill(checks, next);
            if (next) {
                select.setText(R.string.label_select_none);
            } else {
                select.setText(R.string.label_select_all);
            }
            notifyDataSetChanged();
        }

        private void enterEditMode() {
            int count = getItemCount();
            checks = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                checks.add(false);
            }
            select.setText(R.string.label_select_all);
            notifyDataSetChanged();
        }

        private void exitEditMode() {
            checks.clear();
            checks = null;
            notifyDataSetChanged();
        }
        private void toggle(int position) {
            if (position < 0 || position >= getItemCount()) return;
            checks.set(position, !checks.get(position));
            notifyItemChanged(position);
            updateMenu();
        }
        /*     private void toggle(int position) {
                 if (position < 0 || position >= getItemCount()) return;

                 if (myposition == -1) {

                 } else {
                     checks.set(myposition, !checks.get(myposition));
                     notifyItemChanged(myposition);
                 }

                 myposition = position;
                 checks.set(position, !checks.get(position));
                 notifyItemChanged(position);
                 updateMenu();
             }
     */
        private List<DownloadInfo> getSelections() {
            if (!isEditMode) return new ArrayList<>();
            List<DownloadInfo> result = new ArrayList<>();
            for (int i = 0, count = getItemCount(); i < count; i++) {
                if (checks.get(i)) result.add(downloads.get(i));
            }
            return result;
        }

        @Override
        public int getItemCount() {
            return downloads == null ? 0 : downloads.size();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 1235645488 - 0 > 3000

            if (isEditMode) {
                exitEditMode();
                return true;
            } else {
                finish();
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    private class DownloadViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CheckBox check;
        ImageView icon;
        TextView name;
        TextView size;
        TextView timestamp;
        View close;

        DownloadViewHolder(View itemView) {
            super(itemView);
            check = (CheckBox) itemView.findViewById(R.id.download_checkbox);
            icon = (ImageView) itemView.findViewById(R.id.download_icon);
            name = (TextView) itemView.findViewById(R.id.download_name);
            timestamp = (TextView) itemView.findViewById(R.id.download_timestamp);
            size = (TextView) itemView.findViewById(R.id.download_size);
            close = itemView.findViewById(R.id.download_close);
            close.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            final int position = getAdapterPosition();
            if (v == itemView) {
                if (isEditMode) {
                    adapter.toggle(position);
                } else {
                    DownloadInfo info = downloads.get(position);
                    fileManager.open(info.name, info.path);
                }
            } else if (R.id.download_close == v.getId()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setMessage(R.string.download_delete_waring)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DownloadInfo info = downloads.get(position);
                                manager.delete(info);
                                downloads.remove(info);
                                adapter.notifyItemRemoved(position);
                                updateUI();
                                dialog.dismiss();
                            }
                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
            }
        }


    }

    public int mtype2 = 0;

    public String isType(String pictureType) {
        String type = "";
        if (pictureType.contains("png")) {
            type = "image/png";
            mtype2 = 1;
        } else if (pictureType.contains("PNG")) {
            type = "image/PNG";
            mtype2 = 1;
        } else if (pictureType.contains("jpg")) {
            type = "image/jpg";
            mtype2 = 1;
        } else if (pictureType.contains("jpeg")) {
            type = "image/jpeg";
            mtype2 = 1;
        } else if (pictureType.contains("JPEG")) {
            type = "image/JPEG";
            mtype2 = 1;
        } else if (pictureType.contains("webp")) {
            type = "image/webp";
            mtype2 = 1;
        } else if (pictureType.contains("WEBP")) {
            type = "image/WEBP";
            mtype2 = 1;
        } else if (pictureType.contains("gif")) {
            type = "image/gif";
            mtype2 = 1;
        } else if (pictureType.contains("3gp")) {
            type = "video/3gp";
            mtype2 = 2;
        } else if (pictureType.contains("3gpp")) {
            type = "video/3gpp";
            mtype2 = 2;
        } else if (pictureType.contains("3gpp2")) {
            type = "video/3gpp2";
            mtype2 = 2;
        } else if (pictureType.contains("avi")) {
            type = "video/avi";
            mtype2 = 2;
        } else if (pictureType.contains("mp4")) {
            type = "video/mp4";
            mtype2 = 2;
        } else if (pictureType.contains("quicktime")) {
            type = "video/quicktime";
            mtype2 = 2;
        } else if (pictureType.contains("x-msvideo")) {
            type = "video/x-msvideo";
            mtype2 = 2;
        } else if (pictureType.contains("x-matroska")) {
            type = "video/x-matroska";
            mtype2 = 2;
        } else if (pictureType.contains("mpeg")) {
            type = "video/mpeg";
            mtype2 = 2;
        } else if (pictureType.contains("webm")) {
            type = "video/webm";
            mtype2 = 2;
        } else if (pictureType.contains("mp2ts")) {
            type = "video/mp2ts";
            mtype2 = 2;
        }
         /*   else if(pictureType.contains("mpeg")){

            }*/
        else if (pictureType.contains("mp3")) {
            type = "audio/mp3";
            mtype2 = 3;
        } else if (pictureType.contains("x-ms-wma")) {
            type = "audio/x-ms-wma";
            mtype2 = 3;
        } else if (pictureType.contains("x-wav")) {
            type = "audio/x-wav";
            mtype2 = 3;
        }
       /* else if (pictureType.contains("3gpp2")) {
            type="audio/3gpp2";
        } */
        else if (pictureType.contains("amr")) {
            type = "audio/amr";
            mtype2 = 3;
        } else if (pictureType.contains("wav")) {
            type = "audio/wav";
            mtype2 = 3;
        } else if (pictureType.contains("aac")) {
            type = "audio/aac";
            mtype2 = 3;
        } else if (pictureType.contains("x-msvideo")) {
            type = "audio/x-msvideo";
            mtype2 = 3;
        }
        return type;
    }

    //获取视频总时长
    private int getVideoDuration(String path) {
        MediaMetadataRetriever MediaStore = new MediaMetadataRetriever();
        MediaStore.setDataSource(path);
        String duration = MediaStore.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); //
        return Integer.parseInt(duration);
    }


}
