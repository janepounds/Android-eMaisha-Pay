package com.cabral.emaishapay.network.api_helpers;


import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * APIClient handles all the Network API Requests using Retrofit Library
 **/

public class ExternalAPIClient {

    public final static String LOCAL_URL = "http://10.0.2.2:8000";
    // Base URL for API Requests
    private static final String BASE_URL ="https://emaishashop.api.emaisha.com/api/";
    private static ExternalAPIRequests apiRequests;
    private  final String TAG="Retrofit2 Errors";


    public static ExternalAPIRequests getInstance() {
        if (apiRequests == null) {

            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new
                                                                       HttpLoggingInterceptor.Logger() {
                                                                           @Override
                                                                           public void log(String message) {
                                                                               Log.e("Retrofit2 Errors", "message: "+message);
                                                                           }
                                                                       });
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);



            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(2, TimeUnit.MINUTES)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    //.addInterceptor(apiInterceptor)
                    .addInterceptor(httpLoggingInterceptor)
                    .addNetworkInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request().newBuilder()
                                    // .addHeader(Constant.Header, authToken)
                                    .build();
                            return chain.proceed(request);
                        }
                    })
                    .build();


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();


            apiRequests = retrofit.create(ExternalAPIRequests.class);

            return apiRequests;
        }
        else {
            return apiRequests;
        }
    }


}


