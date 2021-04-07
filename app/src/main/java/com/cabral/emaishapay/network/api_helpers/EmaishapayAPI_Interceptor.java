package com.cabral.emaishapay.network.api_helpers;

import java.io.IOException;
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
    private static final String EMAISHAPAY_REQUEST_ID = "request_id";
    private static final String EMAISHAPAY_CATEGORY = "category";

    private String consumerKey;
    private String requestId;
    private String category;



    private EmaishapayAPI_Interceptor(String consumerKey, String requestId, String category) {
        this.consumerKey = consumerKey;
        this.requestId = requestId;
        this.category = category;
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
                .addHeader(EMAISHAPAY_REQUEST_ID, requestId)
                .addHeader(EMAISHAPAY_CATEGORY, category)
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
        private String requestId;
        private String category;

        public Builder consumerKey(String consumerKey) {
            if (consumerKey == null) throw new NullPointerException("consumerKey = null");
            this.consumerKey = consumerKey;
            return this;
        }
        public Builder requestId(String requestId) {
            if (requestId == null) throw new NullPointerException("requestId = null");
            this.requestId = requestId;
            return this;
        }
        public Builder category(String category) {
            if (category == null) throw new NullPointerException("category = null");
            this.category = category;
            return this;
        }


        public EmaishapayAPI_Interceptor build() {

            if (consumerKey == null) throw new IllegalStateException("consumerKey not set");
            if (requestId == null) throw new IllegalStateException("requestId not set");
            if (category == null) throw new IllegalStateException("category not set");

            return new EmaishapayAPI_Interceptor(consumerKey,requestId,category);
        }
    }


}
