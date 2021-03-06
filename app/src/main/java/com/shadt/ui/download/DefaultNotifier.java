package com.shadt.ui.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;

import com.shadt.caibian_news.R;
import com.shadt.util.MyLog;

import java.util.List;

import static com.shadt.ui.download.DownloadState.STATE_RUNNING;


public class DefaultNotifier implements DownloadNotifier {

    private Context context;
    private NotificationManager notificationManager;
    private static final int NOTIFY_ID = 10000;
    private static final int REQUEST_CODE = 100;

    public DefaultNotifier(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void notify(List<DownloadInfo> infos) {
        int activeCount = infos.size();
        if (activeCount == 0) {

            notificationManager.cancel(NOTIFY_ID);

        } else {
            Notification.Builder builder = new Notification.Builder(context);
            Intent intent = new Intent();
            intent.setAction(DownloadManager.INTENT_ACTION_DOWNLOAD);
            PendingIntent downloadIntent = PendingIntent.getActivity(context, REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            Resources resources = context.getResources();
            String title = resources.getString(R.string.download_notification_title);
            String summary = resources.getString(R.string.download_notification_summary, activeCount);
            builder.setSmallIcon(R.drawable.notify_download)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentTitle(title)
                    .setContentText(summary)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(downloadIntent)
                    .setOngoing(true);
            Notification.InboxStyle style = new Notification.InboxStyle();
            style.setBigContentTitle(title);
            style.setSummaryText(summary);
            for (DownloadInfo info : infos) {
                if (info.state == STATE_RUNNING) {
                    style.addLine(info.name);
                }
            }
            builder.setStyle(style);
            notificationManager.notify(NOTIFY_ID, builder.build());
        }
    }
}
