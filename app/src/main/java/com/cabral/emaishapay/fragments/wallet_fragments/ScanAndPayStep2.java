package com.cabral.emaishapay.fragments.wallet_fragments;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.ShopActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.databinding.LayoutScanAndPayProcessStep2Binding;
import com.cabral.emaishapay.models.ConfirmationDataResponse;
import com.cabral.emaishapay.models.WalletTransactionInitiation;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.APIRequests;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanAndPayStep2 extends Fragment implements View.OnClickListener{
    private Context context;
    private String merchantId,merchant_name;
    private SparseArray<String> keyValues = new SparseArray<>();
    private static InputConnection inputConnection;
    private LayoutScanAndPayProcessStep2Binding binding;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.layout_scan_and_pay_process_step_2,container,false);


        if(getArguments()!=null){

             merchantId = getArguments().getString("merchant_id");
            binding.textMerchantId.setText(merchantId);
           getMerchantName();

        }

        setKeyValues();
        setKeyListeningEvents();



        return binding.getRoot();
    }

    private void setKeyValues() {
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
    }

    private void setKeyListeningEvents() {
        binding.tvKey0.setOnClickListener( this);
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
        binding.tvKeyEnter.setOnClickListener(this);
        binding.txtBillTotal.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        setInputConnection(binding.txtBillTotal);
        binding.txtBillTotal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


    }

    public void setInputConnection(TextView editText) {
        InputConnection ic = editText.onCreateInputConnection(new EditorInfo());
        inputConnection = ic;
    }

    private void getMerchantName() {
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        Call<ConfirmationDataResponse> call = apiRequests.
                getMerchant(access_token,merchantId,request_id,category,"getMerchantForUser");
        call.enqueue(new Callback<ConfirmationDataResponse>() {
            @Override
            public void onResponse(Call<ConfirmationDataResponse> call, Response<ConfirmationDataResponse> response) {
                if(response.code()==200){
                    if(response.body().getStatus().equalsIgnoreCase("1")) {
                        merchant_name = response.body().getData().getBusinessName();
                        binding.merchantName.setText(merchant_name);
                        binding.textMerchantInitials.setText(getNameInitials(merchant_name));
                    }else{
                        Toast.makeText(context,response.body().getMessage(),Toast.LENGTH_LONG).show();
                    }

                }else if(response.code()==412) {
//                    businessName = response.body().getMessage();
                    binding.merchantName.setText("Unknown Merchant");
                    binding.merchantName.setTextColor( getResources().getColor(R.color.textRed));


                    // confirmBtn.setEnabled(true);
                }
                else if(response.code()==401){
                    TokenAuthFragment.startAuth( true);

                }


            }

            @Override
            public void onFailure(Call<ConfirmationDataResponse> call, Throwable t) {

                Log.e("info : ", t.getMessage());
                Log.e("info : ", "Something got very very wrong");


            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.layoutBtnPay.setOnClickListener(v -> {
            //navigate to pay merchant step 3

            Bundle bundle = new Bundle();
            bundle.putString("amount", binding.txtBillTotal.getText().toString());
            bundle.putString("merchant_name",merchant_name);
            bundle.putString("merchant_id",merchantId);
            WalletHomeActivity.navController.navigate(R.id.action_scanAndPayStep2_to_scanAndPayStep3,bundle);

        });
    }



    private String getNameInitials(String name){
        if(name==null || name.isEmpty())
            return "";
        String ini = ""+name.charAt(0);
        // we use ini to return the output
        for (int i=0; i<name.length(); i++){
            if ( name.charAt(i)==' ' && i+1 < name.length() && name.charAt(i+1)!=' ' && ini.length()!=2 ){
                //if i+1==name.length() you will have an indexboundofexception
                //add the initials
                ini+=name.charAt(i+1);
            }
        }
        //after getting "ync" => return "YNC"
        return ini.toUpperCase();
    }

    @Override
    public void onClick(View v) {
        if (inputConnection == null)
            return;

        if(  v.getId()==R.id.tv_key_backspace ){
            binding.txtBillTotal.setText( binding.txtBillTotal.getText().toString());
            CharSequence selectedText = inputConnection.getSelectedText(0);

            if (TextUtils.isEmpty(selectedText)) {
                inputConnection.deleteSurroundingText(1, 0);
            } else {
                inputConnection.commitText( "", 1);
            }

        }else if( v.getId()==R.id.tv_key_enter  ){
           //navigate to pay merchant step 3
            Bundle bundle = new Bundle();
            bundle.putString("amount", binding.txtBillTotal.getText().toString());
            bundle.putString("merchant_name",merchant_name);
            bundle.putString("merchant_id",merchantId);
            WalletHomeActivity.navController.navigate(R.id.action_scanAndPayStep2_to_scanAndPayStep3,bundle);

        }else{
            String value = keyValues.get(v.getId()).toString();
            inputConnection.commitText(value, 1);
            setInputConnection( binding.txtBillTotal);
        }
    }
}
