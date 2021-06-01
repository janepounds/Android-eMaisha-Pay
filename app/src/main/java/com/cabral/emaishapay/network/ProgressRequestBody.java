package com.cabral.emaishapay.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class ProgressRequestBody extends RequestBody {
    private File mFile;
    private String mPath;
    private UploadCallbacks mListener;
    private String content_type;

    private static final int DEFAULT_BUFFER_SIZE = 2048;


    public ProgressRequestBody(final File file, String content_type,  final  UploadCallbacks listener) {
        this.content_type = content_type;
        mFile = file;
        mListener = listener;
    }



    @Override
    public MediaType contentType() {
        return MediaType.parse(content_type+"/*");
    }

    @Override
    public long contentLength() throws IOException {
        return mFile.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        long fileLength = mFile.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream in = new FileInputStream(mFile);
        long uploaded = 0;
        Log.w("updateProgress","---->"+uploaded);
        try {
            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            while ((read = in.read(buffer)) != -1) {

                // update progress on UI thread
                handler.post(new ProgressUpdater(uploaded, fileLength));

                uploaded += read;
                //byte encodedBuff[] = Base64.encodeBase64(buffer);
                sink.write(buffer, 0, read);
                Log.w("updateProgress","---->"+read);
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.e("ProgressError","---->"+e.getMessage());
        }finally {
            in.close();
        }
    }


    private class ProgressUpdater implements Runnable {
        private long mUploaded;
        private long mTotal;
        public ProgressUpdater(long uploaded, long total) {
            mUploaded = uploaded;
            mTotal = total;
        }

        @Override
        public void run() {
            mListener.onProgressUpdate((int)(100 * mUploaded / mTotal));
        }
    }

    public interface UploadCallbacks {
        void onProgressUpdate(int percentage);

        void onError();

        void onFinish();
    }

}
