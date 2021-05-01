package com.cabral.emaishapay.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.cabral.emaishapay.network.StartAppRequests;

public class SyncService extends Service {

    @Override
    public void onCreate() {
        startService(new Intent(this, SyncService.class));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        StartAppRequests.SyncProductData();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, SyncService.class));
    }

}
