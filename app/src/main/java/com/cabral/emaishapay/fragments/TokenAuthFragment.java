package com.cabral.emaishapay.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.cabral.emaishapay.R;
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

public class TokenAuthFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TokenAuthFragment";
    private Context context;
    private EditText code1,code2,code3,code4;
    private  String pin;
    static TextView errorTextView;
    private TextView keyboard0,keyboard1,keyboard2,keyboard3,keyboard4,keyboard5,keyboard6,keyboard7,keyboard8,keyboard9;

    public static String WALLET_ACCESS_TOKEN = null;
    private SparseArray<String> keyValues = new SparseArray<>();
    private InputConnection inputConnection;



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
        keyboard0 = view.findViewById(R.id.t9_key_0);
        keyboard1 = view.findViewById(R.id.t9_key_1);
        keyboard2 = view.findViewById(R.id.t9_key_2);
        keyboard3 = view.findViewById(R.id.t9_key_3);
        keyboard4 = view.findViewById(R.id.t9_key_4);
        keyboard5 = view.findViewById(R.id.t9_key_5);
        keyboard6 = view.findViewById(R.id.t9_key_6);
        keyboard7 = view.findViewById(R.id.t9_key_7);
        keyboard8 = view.findViewById(R.id.t9_key_8);
        keyboard9 = view.findViewById(R.id.t9_key_9);

        keyValues.put(R.id.t9_key_1, "1");
        keyValues.put(R.id.t9_key_2, "2");
        keyValues.put(R.id.t9_key_3, "3");
        keyValues.put(R.id.t9_key_4, "4");
        keyValues.put(R.id.t9_key_5, "5");
        keyValues.put(R.id.t9_key_6, "6");
        keyValues.put(R.id.t9_key_7, "7");
        keyValues.put(R.id.t9_key_8, "8");
        keyValues.put(R.id.t9_key_9, "9");




        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        WalletHomeActivity.disableNavigation();
        keyboard0.setOnClickListener(this);
        keyboard1.setOnClickListener(this);
        keyboard2.setOnClickListener(this);
        keyboard3.setOnClickListener(this);
        keyboard4.setOnClickListener(this);
        keyboard5.setOnClickListener(this);
        keyboard6.setOnClickListener(this);
        keyboard7.setOnClickListener(this);
        keyboard8.setOnClickListener(this);
        keyboard9.setOnClickListener(this);

        code1.setRawInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        code1.setTextIsSelectable(true);

        InputConnection ic = code1.onCreateInputConnection(new EditorInfo());
        setInputConnection(ic);

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


        code2.setRawInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        code2.setTextIsSelectable(true);

        InputConnection icc = code2.onCreateInputConnection(new EditorInfo());
        setInputConnection(icc);

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

        code3.setRawInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        code3.setTextIsSelectable(true);

        InputConnection iccc = code3.onCreateInputConnection(new EditorInfo());
        setInputConnection(iccc);
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

        code4.setRawInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        code4.setTextIsSelectable(true);

        InputConnection icccc = code4.onCreateInputConnection(new EditorInfo());
        setInputConnection(icccc);
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

                    WalletHomeActivity.navController.navigate(R.id.action_tokenAuthFragment_to_walletHomeFragment2);

                }
            }

        });


    }





    public void setInputConnection(InputConnection ic) {
        inputConnection = ic;
    }


    public static void getLoginToken(final String password, String phonenumber, final Context context, DialogLoader dialogLoader) {
        if(dialogLoader!=null)
            dialogLoader.showProgressDialog();

        /****RETROFIT IMPLEMENTATION*******/
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,context);
        APIRequests apiRequests = APIClient.getWalletInstance(context);
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

    public static void startAuth( boolean sessionExpired) {
        //call fragment
        WalletHomeActivity.navController.popBackStack(R.id.walletHomeFragment2,false);
        WalletHomeActivity.navController.navigate(R.id.action_walletHomeFragment2_to_tokenAuthFragment);

    }

    @Override
    public void onClick(View v) {
        if (inputConnection == null)
            return;

        if (v.getId() == R.id.t9_key_backspace) {
            CharSequence selectedText = inputConnection.getSelectedText(0);

            if (TextUtils.isEmpty(selectedText)) {
                inputConnection.deleteSurroundingText(1, 0);
            } else {
                inputConnection.commitText("", 1);
            }
        } else {
            String value = keyValues.get(v.getId());
            inputConnection.commitText(value, 1);
        }
    }
}
