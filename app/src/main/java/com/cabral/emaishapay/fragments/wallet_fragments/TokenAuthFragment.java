package com.cabral.emaishapay.fragments.wallet_fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.databinding.FragmentTokenAuthBinding;
import com.cabral.emaishapay.models.TokenResponse;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.APIRequests;
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
    private  String pin;
    static FragmentTokenAuthBinding binding;

    private SparseArray<String> keyValues = new SparseArray<>();
    private static InputConnection inputConnection;



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_token_auth, container, false);

        keyValues.put(R.id.tv_key_0, "0");
        keyValues.put(R.id.tv_key_1, "1");
        keyValues.put(R.id.tv_key_2, "2");
        keyValues.put(R.id.tv_key_3, "3");
        keyValues.put(R.id.tv_key_4, "4");
        keyValues.put(R.id.tv_key_5, "5");
        keyValues.put(R.id.tv_key_6, "6");
        keyValues.put(R.id.tv_key_7, "7");
        keyValues.put(R.id.tv_key_8, "8");
        keyValues.put(R.id.tv_key_9, "9");

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        WalletHomeActivity.disableNavigation();
        binding.tvKey0.setOnClickListener(this);
        binding.tvKey1.setOnClickListener(this);
        binding.tvKey2.setOnClickListener(this);
        binding.tvKey3.setOnClickListener(this);
        binding.tvKey4.setOnClickListener(this);
        binding.tvKey5.setOnClickListener(this);
        binding.tvKey6.setOnClickListener(this);
        binding.tvKey7.setOnClickListener(this);
        binding.tvKey8.setOnClickListener(this);
        binding.tvKey9.setOnClickListener(this);
        binding.tvKeyBackspace.setOnClickListener(this);
        binding.tvKeyClear.setOnClickListener(this);


        binding.pinCode1Edt.setRawInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        binding.pinCode1Edt.setTextIsSelectable(true);
        setInputConnection(binding.pinCode1Edt);
        binding.pinCode1Edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                binding.pinCode2Edt.requestFocus();
                binding.pinCode1Edt.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_dark_blue_bg, null));
            }
        });


        binding.pinCode2Edt.setRawInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        binding.pinCode2Edt.setTextIsSelectable(true);
        binding.pinCode2Edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                binding.pinCode3Edt.requestFocus();
                binding.pinCode2Edt.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_dark_blue_bg, null));
            }
        });

        binding.pinCode3Edt.setRawInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        binding.pinCode3Edt.setTextIsSelectable(true);
        binding.pinCode3Edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                binding.pinCode4Edt.requestFocus();
                binding.pinCode3Edt.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_dark_blue_bg, null));
            }
        });

        binding.pinCode4Edt.setRawInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        binding.pinCode4Edt.setTextIsSelectable(true);
        binding.pinCode4Edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                binding.pinCode4Edt.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_dark_blue_bg, null));
                pin = binding.pinCode1Edt.getText().toString() + binding.pinCode2Edt.getText().toString() + binding.pinCode3Edt.getText().toString() + binding.pinCode4Edt.getText().toString();
                pin = pin.replaceAll("\\s+", "");
                if (pin.length() >= 4) {
                    DialogLoader dialogLoader = new DialogLoader(context);

                    String WalletPass = WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION + pin;

                    //login and get token
                    Log.d(TAG, "attempting user login " + WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_USER_EMAIL, context));

                    if (Connectivity.isConnected(context)) {
                        getLoginToken(WalletPass, WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_PHONE_NUMBER, context), context, dialogLoader);

                        //getActivity().overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);


                    } else {
                        Snackbar.make(binding.textForgotPin, getString(R.string.internet_connection_error), Snackbar.LENGTH_LONG).show();

                    }

                } else {
                    Toast.makeText(context, "Enter PIN!", Toast.LENGTH_SHORT).show();

                }
            }

        });


    }





    public void setInputConnection(EditText editText) {
        InputConnection ic = editText.onCreateInputConnection(new EditorInfo());
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
                        clearPin(binding);
                        if (dialogLoader != null)
                            dialogLoader.hideProgressDialog();

                    }
                    else {
                        Log.d(TAG, "OnSuccess running");
                        TokenResponse tokenResponse = response.body();

                        String accessToken = tokenResponse.getData().getAccess_token();
                        String accountRole = tokenResponse.getData().getAccountRole();
                        Log.d(TAG, accessToken);
                        WalletHomeActivity.WALLET_ACCESS_TOKEN = accessToken;
                        WalletHomeActivity.savePreferences(PREFERENCES_WALLET_ACCOUNT_ROLE, accountRole, context);
                        if (dialogLoader != null)
                            dialogLoader.hideProgressDialog();
                        WalletHomeActivity.navController.navigate(R.id.action_tokenAuthFragment_to_walletHomeFragment2);

                        //now you can go to next wallet page
                    }

                } else {

                    if (response.code() == 403) {

                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();

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

        if (v.getId() == R.id.tv_key_backspace) {
            pin = binding.pinCode1Edt.getText().toString() + binding.pinCode2Edt.getText().toString() + binding.pinCode3Edt.getText().toString() + binding.pinCode4Edt.getText().toString();
            pin = pin.replaceAll("\\s+", "");

            switch (pin.length()){
                case 1:
                    binding.pinCode1Edt.setText("");
                    setInputConnection( binding.pinCode1Edt);
                    binding.pinCode1Edt.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_light_blue_bg, null));

                    break;
                case 2:
                    binding.pinCode2Edt.setText("");
                    setInputConnection( binding.pinCode2Edt);
                    binding.pinCode2Edt.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_light_blue_bg, null));

                    break;
                case 3:
                    binding.pinCode3Edt.setText("");
                    setInputConnection( binding.pinCode3Edt);
                    binding.pinCode3Edt.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_light_blue_bg, null));

                    break;
                case 4:
                    binding.pinCode4Edt.setText("");
                    setInputConnection( binding.pinCode4Edt);
                    binding.pinCode4Edt.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_light_blue_bg, null));

                    break;
            }
        }
        else if(v.getId() == R.id.tv_key_clear){
            clearPin(binding);
            binding.pinCode1Edt.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_light_blue_bg, null));
            binding.pinCode2Edt.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_light_blue_bg, null));
            binding.pinCode3Edt.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_light_blue_bg, null));
            binding.pinCode4Edt.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.round_light_blue_bg, null));
        }
       else {
            String value = keyValues.get(v.getId()).toString();
            inputConnection.commitText(value, 1);

            pin = binding.pinCode1Edt.getText().toString() + binding.pinCode2Edt.getText().toString() + binding.pinCode3Edt.getText().toString() + binding.pinCode4Edt.getText().toString();
            pin = pin.replaceAll("\\s+", "");

            switch (pin.length()){
                case 0:
                    setInputConnection( binding.pinCode1Edt);
                    break;
                case 1:
                    setInputConnection( binding.pinCode2Edt);
                    break;
                case 2:
                    setInputConnection( binding.pinCode3Edt);
                    break;
                case 3:
                    setInputConnection( binding.pinCode4Edt);
                    break;
            }
        }



    }

    private static void  clearPin(FragmentTokenAuthBinding binding){
        binding.pinCode1Edt.setText("");
        binding.pinCode2Edt.setText("");
        binding.pinCode3Edt.setText("");
        binding.pinCode4Edt.setText("");

        InputConnection ic = binding.pinCode1Edt.onCreateInputConnection(new EditorInfo());
        inputConnection = ic;
    }

}
