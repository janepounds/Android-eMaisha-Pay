package com.cabral.emaishapay.activities;

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
import com.cabral.emaishapay.models.TokenResponse;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;
import com.venmo.android.pin.PinFragment;
import com.venmo.android.pin.PinFragmentConfiguration;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletAuthActivity extends AppCompatActivity implements PinFragment.Listener {
    private static final String TAG = "WalletAuthActivity";
    static TextView errorTextView;
    private Context context;
    public static String WALLET_ACCESS_TOKEN = null;
    private static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_authentication_manager);

        sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);

        errorTextView = findViewById(R.id.text_view_crop_user_error);
        context = com.cabral.emaishapay.activities.WalletAuthActivity.this;

        if ( WalletAuthActivity.WALLET_ACCESS_TOKEN == null) {

            PinFragmentConfiguration pinConfig = new PinFragmentConfiguration(com.cabral.emaishapay.activities.WalletAuthActivity.this)
                    .validator(submission -> {

                        final ProgressDialog dialog = new ProgressDialog(context);
                        dialog.setIndeterminate(true);
                        dialog.setMessage("Please Wait..");
                        dialog.setCancelable(false);
                        String WalletPass = WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION + submission;

                        if (submission.length() < 4) {
                            Toast.makeText(com.cabral.emaishapay.activities.WalletAuthActivity.this, "Enter PIN!", Toast.LENGTH_SHORT).show();
                        } else {
                            //login and get token
                            Log.d(TAG, "attempting user login " + WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_USER_EMAIL, com.cabral.emaishapay.activities.WalletAuthActivity.this));


                            WalletAuthActivity.getLoginToken(WalletPass, WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_PHONE_NUMBER, com.cabral.emaishapay.activities.WalletAuthActivity.this),  context,dialog);

                            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);

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

    public static void getLoginToken(final String password, String phonenumber, final Context context,ProgressDialog dialog) {
        if(dialog!=null)
            dialog.show();

        /****RETROFIT IMPLEMENTATION*******/
        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<TokenResponse> call = apiRequests.getToken(phonenumber, password);
        call.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.code() == 200) {
                    Log.d(TAG, "OnSuccess running");
                    TokenResponse tokenResponse = response.body();

                    String accessToken = tokenResponse.getData().getAccess_token();
                    Log.d(TAG, accessToken);
                    WALLET_ACCESS_TOKEN = accessToken;
                    if(dialog!=null)
                        dialog.dismiss();
                    WalletHomeActivity.startHome(context);
                    //now you can go to next wallet page
                }
                else {

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
                        Log.e(TAG, "Something got very very wrong");
                    }
                    if(dialog!=null)
                        dialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NotNull Call<TokenResponse> call, @NotNull Throwable t) {
                Log.e(TAG, String.valueOf(t.getMessage()));
                if(dialog!=null)
                    dialog.dismiss();
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void startAuth(Context context, boolean sessionExpired) {
        Intent authenticate = new Intent(context, com.cabral.emaishapay.activities.WalletAuthActivity.class);
        authenticate.putExtra("sessionExpired", sessionExpired);
        context.startActivity(authenticate);
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
