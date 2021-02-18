package com.cabral.emaishapay.network;


import android.util.Log;

import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.utils.Utilities;

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

public class APIClient {

    public final static String LOCAL_URL = "http://10.0.2.2:8000";
    private static final String BASE_URL_WALLET ="http://emaishapayapi.emaisha.com/api/";

    private static APIRequests apiRequests;
    private  final String TAG="Retrofit2 Errors";

    // Singleton Instance of APIRequests
    public static APIRequests getWalletInstance() {
        if (apiRequests == null) {

            HttpLoggingInterceptor httpLoggingInterceptor =
                    new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                       @Override
                       public void log(String message) {
                           Log.e("Retrofit2 Errors", "message: "+message);
                       }
                   });
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            EmaishapayAPI_Interceptor apiInterceptor = new EmaishapayAPI_Interceptor.Builder()
                    .consumerKey(BuildConfig.EMAISHAPAY_API_KEY)
                    .consumerSecret(Utilities.getMd5Hash(ConstantValues.ECOMMERCE_CONSUMER_SECRET))
                    .consumerIP(ConstantValues.getLocalIpAddress())
                    .build();
                    

            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .addInterceptor(httpLoggingInterceptor)
                    .addNetworkInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request().newBuilder()
                                     //.addHeader(header, authToken)
                                    .build();
                            return chain.proceed(request);
                        }
                    })
                    .addInterceptor(apiInterceptor)
                    .build();


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL_WALLET)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();


            apiRequests = retrofit.create(APIRequests.class);

            return apiRequests;
        }
        else {
            return apiRequests;
        }
    }



}


