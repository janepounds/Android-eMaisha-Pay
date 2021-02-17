package com.cabral.emaishapay.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.models.TokenResponse;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;
import com.cabral.emaishapay.network.Connectivity;
import com.google.android.material.snackbar.Snackbar;
import com.venmo.android.pin.PinFragment;
import com.venmo.android.pin.PinFragmentConfiguration;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.cabral.emaishapay.activities.WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE;

public class TokenAuthActivity extends AppCompatActivity implements PinFragment.Listener {

    private static final String TAG = "TokenAuthActivity";
    static TextView errorTextView;
    private Context context;
    public static String WALLET_ACCESS_TOKEN = null;
    public static String WALLET_ACCOUNT_ROLE = null;
    private static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_authentication_manager);

        sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);

        errorTextView = findViewById(R.id.text_view_crop_user_error);
        context = TokenAuthActivity.this;

        if ( TokenAuthActivity.WALLET_ACCESS_TOKEN == null) {

            PinFragmentConfiguration pinConfig = new PinFragmentConfiguration(TokenAuthActivity.this)
                    .validator(submission -> {

                        DialogLoader dialogLoader = new DialogLoader(this);

                        String WalletPass = WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION + submission;

                        if (submission.length() < 4) {
                            Toast.makeText(TokenAuthActivity.this, "Enter PIN!", Toast.LENGTH_SHORT).show();

                        }
                            else {
                            //login and get token
                            Log.d(TAG, "attempting user login " + WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_USER_EMAIL, TokenAuthActivity.this));

                            if (Connectivity.isConnected(context)) {
                                TokenAuthActivity.getLoginToken(WalletPass, WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_PHONE_NUMBER, TokenAuthActivity.this),  context,dialogLoader);

                                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);


                            }else{
                                Snackbar.make(errorTextView,getString(R.string.internet_connection_error),Snackbar.LENGTH_LONG).show();

                            }


                        }
                        return WalletPass.equals(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_USER_PASSWORD, context));
                        // return submission.equals(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_USER_PIN, context)); // ...check against where you saved the pin

                    });

            PinFragment toShow = PinFragment.newInstanceForVerification(pinConfig);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, toShow)
                    .commit();
        }
        else {
            WalletHomeActivity.startHome(getApplicationContext());
            this.finish();
        }

    }

    public static void getLoginToken(final String password, String phonenumber, final Context context, DialogLoader dialogLoader) {
        if(dialogLoader!=null)
            dialogLoader.showProgressDialog();

        /****RETROFIT IMPLEMENTATION*******/
        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<TokenResponse> call = apiRequests.getToken(phonenumber, password);
        call.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getStatus() == 0) {
                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        if (dialogLoader != null)
                            dialogLoader.hideProgressDialog();

                    } else {
                        Log.d(TAG, "OnSuccess running");
                        TokenResponse tokenResponse = response.body();

                        String accessToken = tokenResponse.getData().getAccess_token();
                        String accountRole = tokenResponse.getData().getAccountRole();
                        Log.d(TAG, accessToken);
                        WALLET_ACCESS_TOKEN = accessToken;
                        WalletHomeActivity.savePreferences(PREFERENCES_WALLET_ACCOUNT_ROLE, accountRole, context);
                        if (dialogLoader != null)
                            dialogLoader.hideProgressDialog();
                        WalletHomeActivity.startHome(context);

                        //now you can go to next wallet page
                    }

                } else {

                    if (response.code() == 403) {
                        //Toast.makeText(context, errorResponse.getString("message"), Toast.LENGTH_LONG).show();
                        if (errorTextView != null) {
                            errorTextView.setText(response.body().getMessage());
                            errorTextView.setVisibility(View.VISIBLE);
                            errorTextView.requestFocus();
                        }

                    }
                    if (response.errorBody() != null) {

                        Log.e(TAG, new String(response.message()));
                    } else {
//                        Toast.makeText(context,response.body().getMessage(),Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Something got very very wrong");
                    }
                    if (dialogLoader != null)
                        dialogLoader.hideProgressDialog();
                }
            }


            @Override
            public void onFailure(@NotNull Call<TokenResponse> call, @NotNull Throwable t) {
                Log.e(TAG, String.valueOf(t.getMessage()));
                if (dialogLoader != null)
                    dialogLoader.hideProgressDialog();
               if(!Connectivity.isConnectedFast(context)){
                    Toast.makeText(context, "Slow internet connection", Toast.LENGTH_LONG).show();

                }
                else {
                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void startAuth(Activity context, boolean sessionExpired) {
        Intent authenticate = new Intent(context, TokenAuthActivity.class);
        authenticate.putExtra("sessionExpired", sessionExpired);
        authenticate.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(authenticate);
        context.finish();
    }

    @Override
    public void onValidated() {
        Log.w(TAG, "Pin validated");
    }

    @Override
    public void onPinCreated() {
        Log.w(TAG, "Pin created");
    }
}
