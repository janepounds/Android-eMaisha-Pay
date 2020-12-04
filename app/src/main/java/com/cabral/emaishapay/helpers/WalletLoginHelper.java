package com.cabral.emaishapay.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.activities.WalletAuthActivity;
import com.cabral.emaishapay.models.WalletAuthentication;
import com.cabral.emaishapay.models.WalletUserRegistration;
import com.cabral.emaishapay.models.ApiPaths;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletLoginHelper extends AppCompatActivity {


    public static void checkLogin(final String rawpassword, final Context context, final TextView errorTextView, final ProgressDialog dialog, SharedPreferences sharedPreferences) {


        /*****RETROFIT IMPLEMENTATION*******/
        final String email = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_USER_EMAIL, context);
        final String phoneNumber = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_PHONE_NUMBER, context);


        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<WalletAuthentication> call = apiRequests.authenticate(email, rawpassword);
        Log.w("email_log", email);

        dialog.show();
        call.enqueue(new Callback<WalletAuthentication>() {
            @Override
            public void onResponse(@NotNull Call<WalletAuthentication> call, @NotNull Response<WalletAuthentication> response) {
                if (response.code() == 200) {
                    try {
                        Gson gson = new Gson();
                        String user = gson.toJson(response.body().getData());
                        JSONObject userobject = new JSONObject(user);
                        //userobject.getInt("id")
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, userobject.getString("id"));
                        editor.apply();

                        Log.w("WALLET_ID", WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, context));
                        WalletAuthActivity.getLoginToken(rawpassword, email, phoneNumber, context);
                    } catch (Exception e) {
                        Log.e("response", response.toString());
                        e.printStackTrace();
                    } finally {

                        dialog.dismiss();
                    }
                } else {
                    dialog.dismiss();
                    if (errorTextView != null) {
                        errorTextView.setText(response.body().getMessage());
                        errorTextView.setVisibility(View.VISIBLE);
                        errorTextView.requestFocus();
                    } else if (response.body() != null) {
                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    } else {

                        Log.e("response", response.toString());
                    }


                }


            }

            @Override
            public void onFailure(Call<WalletAuthentication> call, Throwable t) {
                Log.e("info2 : ", t.getMessage());
                dialog.dismiss();
            }
        });


    }

    public static void userRegister(final ProgressDialog dialog, final Context context, final String rawPassword) {

        /******RETROFIT IMPLEMENTATION*********/

        String email = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_USER_EMAIL, context);
        String firstname = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_FIRST_NAME, context);
        String lastname = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_LAST_NAME, context);
        String addressStreet = WalletHomeActivity.getPreferences(WalletHomeActivity.STREET_PREFERENCES_ID, context);
        String addressCityOrTown = WalletHomeActivity.getPreferences(WalletHomeActivity.CITY_PREFERENCES_ID, context);
        String phoneNumber = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_PHONE_NUMBER, context);
        dialog.show();
        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<WalletUserRegistration> call = apiRequests.create(firstname, lastname, email, rawPassword, phoneNumber, addressStreet, addressCityOrTown);
        call.enqueue(new Callback<WalletUserRegistration>() {
            @Override
            public void onResponse(@NotNull Call<WalletUserRegistration> call, @NotNull Response<WalletUserRegistration> response) {
                if (response.code() == 200) {
                    try {
                        //get user data
                        WalletUserRegistration.ResponseData userdata = response.body().getData();
                        Gson gson = new Gson();
                        String user = gson.toJson(userdata);
                        JSONObject object = new JSONObject(user);
                        Toast.makeText(context, "Successfully Logged in..", Toast.LENGTH_SHORT).show();

                        WalletHomeActivity.savePreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, object.getString("id"), context);
                        WalletAuthActivity.getLoginToken(rawPassword, email, null, context);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("WalletREgResponseError:", e.getMessage());
                    } finally {
                        dialog.dismiss();
                    }
                } else {
                    dialog.dismiss();
                    if (response.body().getMessage() != null) {
                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

            }


            @Override
            public void onFailure(Call<WalletUserRegistration> call, Throwable t) {
                Log.e("info : ", t.getMessage());
                dialog.dismiss();
            }

        });


    }


}
