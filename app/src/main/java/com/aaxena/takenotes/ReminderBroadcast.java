package com.aaxena.takenotes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.graphics.Color.BLUE;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Reminder")
                    .setSmallIcon(R.drawable.logo_dark)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_dark))
                    .setContentTitle(context.getString(R.string.status))
                    .setContentText("Take Notes...")
                    .setAutoCancel(true)
                    .setColor(BLUE)
                    .setColorized(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(200, builder.build());

        }
    }
}
