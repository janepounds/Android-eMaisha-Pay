package com.cabral.emaishapay.network.api_helpers;


import android.content.Context;
import android.util.Log;

import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.activities.WalletHomeActivity;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.cabral.emaishapay.constants.ConstantValues.WALLET_DOMAIN;


/**
 * APIClient handles all the Network API Requests using Retrofit Library
 **/

public class APIClient {

    private static APIRequests apiRequests;
    private  final String TAG="Retrofit2 Errors";

    // Singleton Instance of APIRequests
    public static APIRequests getWalletInstance(Context context) {
        if (apiRequests == null) {

            HttpLoggingInterceptor httpLoggingInterceptor =
                    new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                       @Override
                       public void log(String message) {
                           Log.e("Retrofit2 Errors", "message: "+message);
                       }
                   });
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            String request_id = WalletHomeActivity.generateRequestId();
            String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,context);

            EmaishapayAPI_Interceptor apiInterceptor = new EmaishapayAPI_Interceptor.Builder()
                    .consumerKey(BuildConfig.EMAISHAPAY_API_KEY)
                    .requestId(request_id)
                    .category(category)
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
                    .baseUrl(WALLET_DOMAIN)
                    .client(okHttpClient)
                    .callFactory(new Call.Factory() {
                        @Override
                        public Call newCall(Request request) {
                            Set<String> paramList=request.url().queryParameterNames();
                            //Log.w("RequestLogged", request.url().toString());
                            //Request encryptRequest = EncryptCallHelper.encryptRequest(request);
                            return okHttpClient.newCall(request);
                        }
                    })
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


