package com.cabral.emaishapay.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.ConfirmActivity;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.models.TokenResponse;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;
import com.cabral.emaishapay.network.Connectivity;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.cabral.emaishapay.activities.WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE;

public class TokenAuthFragment extends Fragment {
    private static final String TAG = "TokenAuthFragment";
    private Context context;
    private EditText code1,code2,code3,code4;
    private  String pin;
    static TextView errorTextView;
    public static String WALLET_ACCESS_TOKEN = null;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =   inflater.inflate(R.layout.fragment_token_auth, container, false);
        code1 = view.findViewById(R.id.pin_code1_et);
        code2 = view.findViewById(R.id.pin_code2_et);
        code3 = view.findViewById(R.id.pin_code3_et);
        code4 = view.findViewById(R.id.pin_code4_et);
        errorTextView = view.findViewById(R.id.text_view_crop_user_error);


        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        code1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                code2.requestFocus();
            }
        });

        code2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                code3.requestFocus();
            }
        });

        code3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                code4.requestFocus();
            }
        });

        code4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TokenAuthFragment.WALLET_ACCESS_TOKEN == null) {

                    pin = code1.getText().toString() + code2.getText().toString() + code3.getText().toString() + code4.getText().toString();
                    pin = pin.replaceAll("\\s+", "");
                    if (pin.length() >= 4) {
                        DialogLoader dialogLoader = new DialogLoader(context);

                        String WalletPass = WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION + pin;

                        //login and get token
                        Log.d(TAG, "attempting user login " + WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_USER_EMAIL, context));

                        if (Connectivity.isConnected(context)) {
                            getLoginToken(WalletPass, WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_PHONE_NUMBER, context), context, dialogLoader);

                            getActivity().overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);


                        } else {
                            Snackbar.make(errorTextView, getString(R.string.internet_connection_error), Snackbar.LENGTH_LONG).show();

                        }

                    } else {
                        Toast.makeText(context, "Enter PIN!", Toast.LENGTH_SHORT).show();

                    }

                }else{

                    WalletHomeActivity.startHome(context);
                    getActivity().finish();

                }
            }

        });


    }


    public static void getLoginToken(final String password, String phonenumber, final Context context, DialogLoader dialogLoader) {
        if(dialogLoader!=null)
            dialogLoader.showProgressDialog();

        /****RETROFIT IMPLEMENTATION*******/
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,context);
        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<TokenResponse> call = apiRequests.getToken(phonenumber, password,request_id,category,"getAuthToken");
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
        //call fragment
        Intent authenticate = new Intent(context, TokenAuthActivity.class);
        authenticate.putExtra("sessionExpired", sessionExpired);
        authenticate.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(authenticate);
    }
}
