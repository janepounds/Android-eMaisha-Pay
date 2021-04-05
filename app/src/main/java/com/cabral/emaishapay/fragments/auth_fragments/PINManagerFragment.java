package com.cabral.emaishapay.fragments.auth_fragments;


import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.databinding.FragmentTokenAuthBinding;

//This fragment is used for creating or picking a user's PIN.
public class PINManagerFragment  extends Fragment implements View.OnClickListener{

    private static final String TAG = "TokenAuthFragment";
    private Context context;
    private  String pin, pin1;
    static FragmentTokenAuthBinding binding;

    public static int ACTION;
    private SparseArray<String> keyValues = new SparseArray<>();
    private static InputConnection inputConnection;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
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
        if(getArguments()!=null){
            ACTION=getArguments().getInt("action");
            if(ACTION==2)
                binding.pinTitle.setText(getString(R.string.create_pin));
            else
                binding.pinTitle.setText(getString(R.string.enter_pin));
        }


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
                if (WalletHomeActivity.WALLET_ACCESS_TOKEN == null) {

                    pin = binding.pinCode1Edt.getText().toString() + binding.pinCode2Edt.getText().toString() + binding.pinCode3Edt.getText().toString() + binding.pinCode4Edt.getText().toString();
                    pin = pin.replaceAll("\\s+", "");
                    if (pin.length() >= 4) {


                        //if Action 1 login , if 2 proceed with registration
                        if(ACTION==1){
                            String WalletPass = WalletHomeActivity.PREFERENCES_PREPIN_ENCRYPTION + pin;
                        }else if(ACTION==2){
                            if(pin1.length()==0){
                                pin1=pin;
                                binding.pinTitle.setText(getString(R.string.comfirm_pin));
                            }else if(pin1.length()==4 && pin.equals(pin1)){

                            }

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
                    break;
                case 2:
                    binding.pinCode2Edt.setText("");
                    setInputConnection( binding.pinCode2Edt);
                    break;
                case 3:
                    binding.pinCode3Edt.setText("");
                    setInputConnection( binding.pinCode3Edt);
                    break;
            }
        }
        else if(v.getId() == R.id.tv_key_clear){
            clearPin(binding);
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

    public void setInputConnection(EditText editText) {
        InputConnection ic = editText.onCreateInputConnection(new EditorInfo());
        inputConnection = ic;
    }

}
