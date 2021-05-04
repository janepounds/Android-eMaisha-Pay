package com.cabral.emaishapay.network.api_helpers;


import android.util.Log;


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

public class BuyInputsAPIClient {


    // Base URL for API Requests
    private static final String BASE_URL = ConstantValues.ECOMMERCE_URL + "api/";

    private static APIRequestsForBuyInputs apiRequests;
    private  final String TAG="Retrofit2 Errors";


    // Singleton Instance of APIRequests
    public static APIRequestsForBuyInputs getInstance() {
        if (apiRequests == null) {

            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new
                                                       HttpLoggingInterceptor.Logger() {
                                                           @Override public void log(String message) {
                                                               Log.e("Retrofit2 Errors", "message: "+message);
                                                           }
                                                       });
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            API_Interceptor apiInterceptor = new API_Interceptor.Builder()
                    .consumerKey(Utilities.getMd5Hash(ConstantValues.ECOMMERCE_CONSUMER_KEY))
                    .consumerSecret(Utilities.getMd5Hash(ConstantValues.ECOMMERCE_CONSUMER_SECRET))
                    .consumerIP(ConstantValues.getLocalIpAddress())
                    .build();

            BasicOAuth basicOAuthWoocommerce = new BasicOAuth.Builder()
                    .consumerKey(ConstantValues.ECOMMERCE_CONSUMER_KEY)
                    .consumerSecret(ConstantValues.ECOMMERCE_CONSUMER_SECRET)
                    .consumerIP(ConstantValues.getLocalIpAddress())
                    .build();

            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(5, TimeUnit.MINUTES)
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
                    .addInterceptor(BASE_URL.startsWith("https://")?  apiInterceptor : basicOAuthWoocommerce)
                    .build();


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();


            apiRequests = retrofit.create(APIRequestsForBuyInputs.class);

            return apiRequests;
        }
        else {
            return apiRequests;
        }
    }


}


