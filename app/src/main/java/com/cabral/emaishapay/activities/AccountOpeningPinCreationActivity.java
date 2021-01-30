package com.cabral.emaishapay.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.models.AccountCreation;
import com.cabral.emaishapay.models.InitiateWithdrawResponse;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.utils.CryptoUtil;
import com.venmo.android.pin.PinFragment;
import com.venmo.android.pin.PinFragmentConfiguration;
import com.venmo.android.pin.PinSaver;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountOpeningPinCreationActivity extends AppCompatActivity implements PinFragment.Listener {
    private Context context;
    private AccountCreation accountCreation;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_auth2);
        context = AccountOpeningPinCreationActivity.this;

        PinFragmentConfiguration pinConfig = new PinFragmentConfiguration(context)
                .validator(submission -> {

                    if (submission.length() < 4) {
                        Toast.makeText(context, "Enter PIN!", Toast.LENGTH_SHORT).show();
                    }
                    return submission.length() == 4;
                }).pinSaver(new PinSaver() {
                    @Override
                    public void save(String pin) {
                       String actual_pin = "12"+pin;

                        String user_id = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, context);
                        //submit registration details to server
                        /***************RETROFIT IMPLEMENTATION FOR ACCOUNT CREATION************************/
                        JSONObject requestObject = new JSONObject();
                        accountCreation = new AccountCreation();

                        //set account creation values

                            accountCreation.setPin(actual_pin);
                            accountCreation.setUser_id(user_id);

                        try {
                            requestObject.put("accountParams", accountCreation);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Call<InitiateWithdrawResponse> call = APIClient.getWalletInstance()
                                .openAccount(requestObject);
                        call.enqueue(new Callback<InitiateWithdrawResponse>() {
                            @Override
                            public void onResponse(Call<InitiateWithdrawResponse> call, Response<InitiateWithdrawResponse> response) {
                                if (response.isSuccessful()) {
                                    if (response.body().getStatus().equalsIgnoreCase("1")) {
                                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                        //success message
                                        Intent intent = new Intent(context, WalletHomeActivity.class);
                                        startActivity(intent);


                                    } else {
                                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                        //redirect to home;
                                        Intent intent = new Intent(context, WalletHomeActivity.class);
                                        startActivity(intent);



                                    }
                                }


                            }

                            @Override
                            public void onFailure(Call<InitiateWithdrawResponse> call, Throwable t) {
                                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(context, WalletHomeActivity.class);
                                startActivity(intent);
                            }
                        });



                    }
                });

        PinFragment toShow = PinFragment.newInstanceForCreation(pinConfig);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, toShow)
                .commit();
    }

    @Override
    public void onValidated() {

    }

    @Override
    public void onPinCreated() {

    }
}
