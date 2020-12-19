package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.Auth2Activity;

public class LoginOtpDialog extends DialogFragment {
    Context activity;
    FragmentManager fm;
    private EditText code1, code2, code3, code4;
    private Button btn_submit;
    private Context context;
    private  String phonenumber;

    public LoginOtpDialog(Context context, FragmentManager supportFragmentManager,String phonenumber) {
        this.activity = context;
        this.fm = supportFragmentManager;
        this.phonenumber=phonenumber;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.login_dialog_otp, null);

        builder.setView(view);

        initializeForm(view);

//        ImageView close = view.findViewById(R.id.wallet_transfer_money_close);
//        close.setOnClickListener(v -> dismiss());

        return builder.create();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void initializeForm(View view) {
        code1 = view.findViewById(R.id.otp_code1_et);
        code2 = view.findViewById(R.id.otp_code2_et);
        code3 = view.findViewById(R.id.otp_code3_et);
        code4 = view.findViewById(R.id.otp_code4_et);
        btn_submit = view.findViewById(R.id.btn_submit);

        btn_submit.setOnClickListener(v -> {

            String code = code1.getText().toString() + code2.getText().toString() + code3.getText().toString() + code4.getText().toString();


            String sms_code = getArguments().getString("sms_code");
            if (sms_code.equalsIgnoreCase(code)) {
                //fetch session data
                Auth2Activity.startAuth(context,phonenumber,1);
            } else {
                Toast.makeText(context, "Enter valid code", Toast.LENGTH_LONG).show();
            }

        });


    }


}