package com.home.skiffdro.common;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import com.home.skiffdro.R;
import com.home.skiffdro.MainActivity;

public class Notifier {
    private Context context;

    public Notifier(Context context)
    {
        this.context = context;
    }

    public void ShowMarkAlarm(String MarkName)
    {
        try {
            Context mContext = context;
            NotificationManager mNotificationManager;

            Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ context.getPackageName() + "/" + R.raw.markbell);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext.getApplicationContext(), "Засечки");
            Intent ii = new Intent(mContext.getApplicationContext(), MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, ii, 0);

            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setSmallIcon(R.drawable.notification_on);
            mBuilder.setContentTitle(MarkName);
            mBuilder.setContentText("Достигнут район засечки!");
            mBuilder.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.markbell));

            mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();

                String channelId = "1";
                NotificationChannel channel = new NotificationChannel(
                        channelId, "Оповещение о достижении засечки",
                        NotificationManager.IMPORTANCE_HIGH);
                channel.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.markbell), audioAttributes);
                mNotificationManager.createNotificationChannel(channel);
                mBuilder.setChannelId(channelId);
            }

            mNotificationManager.notify(0, mBuilder.build());

            Handler h = new Handler();
            long delayInMilliseconds = 3000;
            h.postDelayed(() -> mNotificationManager.cancel(0), delayInMilliseconds);
        }
        catch (Exception ex) {
        }
    }
}
