package com.cabral.emaishapay.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.cabral.emaishapay.AppExecutors;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.network.StartAppRequests;

public class SyncService extends Service {
    private static final String TAG = "SyncService";

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");

//        startService(new Intent(this, SyncService.class));

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: service started");
                AppExecutors.getInstance().NetworkIO().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: product sync started");
                        WalletHomeActivity.SyncProductData();
                    }
                }
        );


        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: service stopped");
        stopService(new Intent(this, SyncService.class));
    }

}
