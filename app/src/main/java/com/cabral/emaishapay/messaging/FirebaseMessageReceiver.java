package com.cabral.emaishapay.messaging;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.fragments.WalletHomeFragment;
import com.cabral.emaishapay.network.StartAppRequests;
import com.cabral.emaishapay.utils.NotificationHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FirebaseMessageReceiver extends FirebaseMessagingService {


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
//        DashboardActivity.sendFirebaseToken(s,this);

        Log.e("NEW_TOKEN", s);
        if (ConstantValues.DEFAULT_NOTIFICATION.equalsIgnoreCase("fcm")) {

            StartAppRequests.RegisterDeviceForFCM(getApplicationContext());

        }


    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        Bitmap notificationBitmap = null;
        String notification_title, notification_message, notification_image = "";


        if (remoteMessage.getData().size() > 0) {
            notification_title = remoteMessage.getData().get("title");
            notification_message = remoteMessage.getData().get("body");
            Log.w("MessageReceiption", "Message: " + notification_message);

            //notification_image = remoteMessage.getData().get("image");
        } else {
            notification_title = remoteMessage.getNotification().getTitle();
            notification_message = remoteMessage.getNotification().getBody();
        }

        //notificationBitmap =BitmapFactory.decodeResource(getResources(), R.drawable.emaishapay_logo_icon);
        notificationBitmap=getBitmapFromUrl(notification_image);

        Intent notificationIntent = new Intent(getApplicationContext(), WalletHomeActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        NotificationHelper.showNewNotification
                (
                        getApplicationContext(),
                        notificationIntent,
                        notification_title,
                        notification_message,
                        notificationBitmap
                );

    }


    public Bitmap getBitmapFromUrl(String imageUrl) {
        if ("".equalsIgnoreCase(imageUrl)) {
            return null;
        }
        else {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

}

