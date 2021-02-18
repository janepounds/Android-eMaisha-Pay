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
    private static final String ECOMMERCE_CONSUMER_SECRET = "consumer-secret";
    private static final String ECOMMERCE_CONSUMER_NONCE = "consumer-nonce";
    private static final String ECOMMERCE_CONSUMER_DEVICE_ID = "consumer-device-id";
    private static final String ECOMMERCE_COMSUMER_IP = "consumer-ip";

    private String consumerKey;
    private String consumerSecret;
    private String consumerNonce;
    private String consumerDeviceID;
    private String consumerIP;



    private EmaishapayAPI_Interceptor(String consumerKey, String consumerSecret, String consumerIP) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.consumerIP = consumerIP;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        HttpUrl originalHttpUrl = original.url();

        consumerNonce = getRandomNonce(32);


        HttpUrl url = originalHttpUrl.newBuilder().build();

        // Request customization: add request headers
        Request.Builder requestBuilder = original.newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader(EMAISHAPAY_CONSUMER_KEY, consumerKey)
//                .addHeader(ECOMMERCE_CONSUMER_SECRET, consumerSecret)
//                .addHeader(ECOMMERCE_COMSUMER_IP,consumerIP)
//                .addHeader(ECOMMERCE_CONSUMER_NONCE, consumerNonce)
//                .addHeader(ECOMMERCE_CONSUMER_DEVICE_ID, "")

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
        private String consumerSecret;
        private String consumerIP;

        public Builder consumerKey(String consumerKey) {
            if (consumerKey == null) throw new NullPointerException("consumerKey = null");
            this.consumerKey = consumerKey;
            return this;
        }

        public Builder consumerSecret(String consumerSecret) {
            if (consumerSecret == null) throw new NullPointerException("consumerSecret = null");
            this.consumerSecret = consumerSecret;
            return this;
        }

        public Builder consumerIP(String consumerIP){
            if (consumerIP == null) throw new NullPointerException("consumer-ip = null");
            this.consumerIP = consumerIP;
            return this;
        }


        public EmaishapayAPI_Interceptor build() {

            if (consumerKey == null) throw new IllegalStateException("consumerKey not set");
            if (consumerSecret == null) throw new IllegalStateException("consumerSecret not set");
            if (consumerIP == null) throw new IllegalStateException("consumerIP not set");

            return new EmaishapayAPI_Interceptor(consumerKey, consumerSecret,consumerIP);
        }
    }


}
