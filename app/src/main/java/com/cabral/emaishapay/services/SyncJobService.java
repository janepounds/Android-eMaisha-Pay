package com.cabral.emaishapay.services;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.cabral.emaishapay.AppExecutors;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.network.StartAppRequests;

public class SyncJobService extends JobService {
    private static final String TAG = "SyncJobService";
    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        doBackgroundWork(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    private void doBackgroundWork(final JobParameters params) {

        AppExecutors.getInstance().NetworkIO().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: product sync started");
                        WalletHomeActivity.SyncProductData();
                        Log.d(TAG, "Job finished");
                        jobFinished(params, false);
                    }
                }
        );

    }




}
