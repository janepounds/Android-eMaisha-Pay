package com.cabral.emaishapay.network;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by muneeb.vectorcoder@gmail.com on 30-Jan-18.
 */

public class EmaishapayAPI_Interceptor implements Interceptor {

    private static final String EMAISHAPAY_CONSUMER_KEY = "authorizationKey";

    private String consumerKey;



    private EmaishapayAPI_Interceptor(String consumerKey) {
        this.consumerKey = consumerKey;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        HttpUrl originalHttpUrl = original.url();


        HttpUrl url = originalHttpUrl.newBuilder().build();

        // Request customization: add request headers
        Request.Builder requestBuilder = original.newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader(EMAISHAPAY_CONSUMER_KEY, consumerKey)
                .url(url);


        Request request = requestBuilder.build();
        return chain.proceed(request);
    }


    private static String getRandomNonce(final int sizeOfRandomString) {

        final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm-";

        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder(sizeOfRandomString);

        for(int i=0;i<sizeOfRandomString;++i)
            randomStringBuilder.append(ALLOWED_CHARACTERS.charAt(generator.nextInt(ALLOWED_CHARACTERS.length())));

        return randomStringBuilder.toString();
    }





    public static final class Builder {

        private String consumerKey;

        public Builder consumerKey(String consumerKey) {
            if (consumerKey == null) throw new NullPointerException("consumerKey = null");
            this.consumerKey = consumerKey;
            return this;
        }


        public EmaishapayAPI_Interceptor build() {

            if (consumerKey == null) throw new IllegalStateException("consumerKey not set");

            return new EmaishapayAPI_Interceptor(consumerKey);
        }
    }


}
