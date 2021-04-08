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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.AuthActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.databinding.FragmentGetStartedSignUpBinding;
import com.cabral.emaishapay.utils.ValidateInputs;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class GetStartedSignUpFragment extends Fragment {
    FragmentGetStartedSignUpBinding binding;
    Context context;

    private EditText code1,code2,code3,code4,code5,code6;
    private TextView tvTimer, tvChangeNumber;
    private RelativeLayout layoutResendCode;
    private Dialog dialog;
    private DialogLoader dialogLoader;
    private FirebaseAuth mAuth;
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
        FirebaseApp.initializeApp(context);
        //initializing objects
        mAuth = FirebaseAuth.getInstance();
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
                    sendVerificationCode(getResources().getString(R.string.ugandan_code) + binding.userMobile.getText().toString().trim());

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
    public void showOTPDialog(Activity activity, String msg) {
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

                verifyVerificationCode(code);
            }
        });
        dialog.findViewById(R.id.login_otp_resend_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCode(getResources().getString(R.string.ugandan_code)+binding.userMobile.getText().toString().trim());
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


    //the method is sending verification code
    //the country id is concatenated
    //you can take the country id as user input as well
    private void sendVerificationCode(String mobile) {
        showOTPDialog(getActivity(), "");

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(mobile)                        // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS)      // Timeout and unit
                        .setActivity(getActivity())                             // Activity (for callback binding)
                        .setCallbacks(mCallbacks)                      // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();
            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(context, "Verification failed : " + e.getMessage(), Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }

        @Override
        public void onCodeSent(@NotNull String s, @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };

    private void verifyVerificationCode(String code) {
        dialogLoader.showProgressDialog();
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    //*********** This method is invoked for every call on requestPermissions(Activity, String[], int) ********//

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dialogLoader.hideProgressDialog();
                        if (task.isSuccessful()) {
                            //verification go to registration form
                            dialog.dismiss();
                            Bundle args =new Bundle();
                            args.putString("phone","0"+binding.userMobile.getText().toString().trim());
                            AuthActivity.navController.navigate(R.id.action_getStartedSignUpFragment_to_signUpFragment,args);

                        } else {
                            //verification unsuccessful.. display an error message
                            String message = "Something is wrong, we will fix it soon...";
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }
                            Snackbar snackbar = Snackbar.make(binding.layoutEnterPhoneNumber, message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", v -> snackbar.dismiss());
                            snackbar.show();
                        }
                    }
                });
    }

}
