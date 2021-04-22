package com.cabral.emaishapay.fragments.auth_fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.AuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.databinding.FragmentGetStartedSignUpBinding;
import com.cabral.emaishapay.utils.ValidateInputs;

public class GetStartedSignUpFragment extends Fragment {
    FragmentGetStartedSignUpBinding binding;
    Context context;

    private EditText code1,code2,code3,code4,code5,code6;
    private TextView tvTimer, tvChangeNumber;
    private RelativeLayout layoutResendCode;
    private Dialog dialog;
    private DialogLoader dialogLoader;
    // Verification id that will be sent to the user
    private String mVerificationId;

    @Override
    public void onAttach(@NonNull Context context) {
        this.context=context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_get_started_sign_up,container,false);
        dialogLoader=new DialogLoader(context);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        View decorView = getActivity().getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        binding.getStartedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate Login Form Inputs
                boolean isValidData = validateForm();
                if (isValidData) {
                    // Proceed User Registration
                    //sendVerificationCode(getResources().getString(R.string.ugandan_code) + binding.userMobile.getText().toString().trim());

                }
                //navigate to Sign Up Fragment


            }
        });

        binding.layoutSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navigate to Sign Up Fragment
                AuthActivity.navController.navigate(R.id.action_getStartedSignUpFragment_to_loginFragment);

            }
        });
    }


    private boolean validateForm() {
        if (!ValidateInputs.isValidNumber(binding.userMobile.getText().toString().trim())) {
            binding.userMobile.setError(getString(R.string.invalid_contact));
            return false;
        }
        else {
            return true;
        }
    }


    /// Custom dialog for OTP
    public void showOTPDialog(Activity activity) {
        //call success dialog
        dialog  = new Dialog(activity);
        dialog.setContentView(R.layout.login_dialog_otp);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(false);
        code1= dialog.findViewById(R.id.otp_code1_et);
        code2= dialog.findViewById(R.id.otp_code2_et);
        code3= dialog.findViewById(R.id.otp_code3_et);
        code4= dialog.findViewById(R.id.otp_code4_et);
        code5=dialog.findViewById(R.id.otp_code5_et);
        code6= dialog.findViewById(R.id.otp_code6_et);
        tvTimer= dialog.findViewById(R.id.tv_timer);
        layoutResendCode= dialog.findViewById(R.id.layout_resend_code);
        tvChangeNumber = dialog.findViewById(R.id.text_view_change_number);

        CountDownTimer timer = new CountDownTimer(90000, 1000) {

            public void onTick(long millisUntilFinished) {
                tvTimer.setText(millisUntilFinished / 1000 + " Seconds" );
            }

            public void onFinish() {
                layoutResendCode.setVisibility(View.VISIBLE);
            }
        };
        timer.start();
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
                code5.requestFocus();
            }
        });

        code5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                code6.requestFocus();
            }
        });


        code6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String code = code1.getText().toString() + code2.getText().toString()+code3.getText().toString()+code4.getText().toString()+code5.getText().toString()+code6.getText().toString();

                //verifyVerificationCode(code);
            }
        });
        dialog.findViewById(R.id.login_otp_resend_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendVerificationCode(getResources().getString(R.string.ugandan_code)+binding.userMobile.getText().toString().trim());
                layoutResendCode.setVisibility(View.VISIBLE);
            }
        });

        dialog.findViewById(R.id.text_view_change_number).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

    }


}
