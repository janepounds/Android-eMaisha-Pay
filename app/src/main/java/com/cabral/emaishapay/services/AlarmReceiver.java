package com.cabral.emaishapay.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cabral.emaishapay.activities.AuthActivity;
import com.cabral.emaishapay.app.MyAppPrefsManager;
import com.cabral.emaishapay.utils.NotificationHelper;
import com.cabral.emaishapay.utils.NotificationScheduler;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

                NotificationScheduler.setReminder(context, AlarmReceiver.class);
            }
        }


        Intent notificationIntent = new Intent(context, AuthActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(context);


        //Trigger the notification
        NotificationHelper.showNewNotification
                (
                        context,
                        notificationIntent,
                        myAppPrefsManager.getLocalNotificationsTitle(),
                        myAppPrefsManager.getLocalNotificationsDescription(),
                        null
                );

    }
}
