package com.cabral.emaishapay.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.Auth2Activity;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.models.LoanApplication;
import com.cabral.emaishapay.models.MerchantInfoResponse;
import com.cabral.emaishapay.models.WalletAuthenticationResponse;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;
import com.google.android.material.snackbar.Snackbar;
import com.kofigyan.stateprogressbar.StateProgressBar;

public class UserDetailsFragment extends Fragment {
    private Context context;
    Bundle localBundle;
    String[] descriptionData = {"User\nDetails","Loan\nDetails", "Farming\nDetails", "Preview", "KYC\nDetails"};

    private Toolbar toolbar;
    private StateProgressBar loanApplicationStateProgressBar;
    private Button nextBtn;
    private EditText etxtFirstName,etxtSecondName,etxteMaishaAcc;
    private String businessName,account_name,phone;
    private Dialog dialog;
    private EditText code1,code2,code3,code4,code5,code6;
    TextView resendtxtview;
    private  String code, sms_code;
    RelativeLayout parentLayout;

    private float interest;

    public UserDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_user_details, container, false);
        loanApplicationStateProgressBar = view.findViewById(R.id.loan_application_state_progress_bar_loan_details);
        loanApplicationStateProgressBar.setStateDescriptionData(descriptionData);
        loanApplicationStateProgressBar.setStateDescriptionTypeface("fonts/JosefinSans-SemiBold.ttf");

        toolbar = view.findViewById(R.id.toolbar_loan_application_user_details);
        nextBtn = view.findViewById(R.id.txt_next_two);
        etxtFirstName = view.findViewById(R.id.etxt_first_name);
        etxtSecondName = view.findViewById(R.id.etxt_lastname);
        etxteMaishaAcc = view.findViewById(R.id.etxt_emaisha_account);
        parentLayout = view.findViewById(R.id.parent_layout);



        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Apply for Loan");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        initializeViews();


        return view;
    }

    public void initializeViews(){


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check whether account exists
                phone = "0"+etxteMaishaAcc.getText().toString();
                account_name =  etxtFirstName.getText().toString() + " "+  etxtSecondName.getText().toString();

                //check whether account exists

                getReceiverName(phone);


            }
        });



    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.wallet_home_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    public void getReceiverName(String receiverPhoneNumber){
        /***************RETROFIT IMPLEMENTATION***********************/
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;

        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<MerchantInfoResponse> call = apiRequests.getUserBusinessName(access_token,receiverPhoneNumber,"MerchantLoanApplication");
        call.enqueue(new Callback<MerchantInfoResponse>() {
            @Override
            public void onResponse(Call<MerchantInfoResponse> call, Response<MerchantInfoResponse> response) {
                if(response.code()==200){

                    businessName = response.body().getData().getBusinessName();

                    if(businessName.equalsIgnoreCase(account_name)){


                        //account exists
                        //otp verification

                        sms_code =response.body().getData().getSmsCode();

                        //call otp dialog
                        dialog  = new Dialog(context);
                        dialog.setContentView(R.layout.login_dialog_otp);
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.setCancelable(false);



                        code1= dialog.findViewById(R.id.otp_code1_et);
                        code2= dialog.findViewById(R.id.otp_code2_et);
                        code3= dialog.findViewById(R.id.otp_code3_et);
                        code4= dialog.findViewById(R.id.otp_code4_et);
                        code5=dialog.findViewById(R.id.otp_code5_et);
                        code6= dialog.findViewById(R.id.otp_code6_et);
                        resendtxtview= dialog.findViewById(R.id.login_otp_resend_code);


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
                                validateEnteredCode(code1.getText().toString()+code2.getText().toString()+code3.getText().toString()+code4.getText().toString()+code5.getText().toString()+code6.getText().toString(),sms_code);
                            }
                        });

                        dialog.findViewById(R.id.login_otp_resend_code).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getReceiverName("0"+etxteMaishaAcc.getText().toString());
                            }
                        });
                        dialog.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                code = code1.getText().toString() + code2.getText().toString()+code3.getText().toString()+code4.getText().toString()+code5.getText().toString()+code6.getText().toString();

                                validateEnteredCode(code,sms_code);
                            }
                        });
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        WindowManager.LayoutParams params = dialog.getWindow().getAttributes(); // change this to your dialog.

                        params.x = 100; // Here is the param to set your dialog position. Same with params.x
                        dialog.getWindow().setAttributes(params);
                        dialog.show();

                    }



                    progressDialog.dismiss();
                }else if(response.code()==412) {
                     //Toast.makeText(context,"account does not exist",Toast.LENGTH_LONG).show();
                    final Snackbar snackBar = Snackbar.make(parentLayout, "Account does not exist!", Snackbar.LENGTH_LONG);
                            snackBar.setAction("DISMISS", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //snackBar.dismiss();
                                }
                            });
                            snackBar.show();

                    //redirect to home
//                    Intent intent = new Intent(context, WalletHomeActivity.class);
//                    startActivity(intent);

                }
                else if(response.code()==401){
                    TokenAuthActivity.startAuth(context, true);
                }
                if (response.errorBody() != null) {
                    Log.e("info", String.valueOf(response.errorBody()));
                } else {
                    Log.e("info", "Something got very very wrong");
                }

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<MerchantInfoResponse> call, Throwable t) {

                Log.e("info : ", t.getMessage());
                Log.e("info : ", "Something got very very wrong");

                progressDialog.dismiss();
            }
        });


    }

    private void validateEnteredCode(String code,String sent_code) {
        String phone ="0"+etxteMaishaAcc.getText().toString();

        if (sent_code.equalsIgnoreCase(code)) {

            dialog.dismiss();
            //navigate to next fragment
            LoanApplication loanApplication = new LoanApplication();
            loanApplication.setPhone(phone);
            Bundle bundle = new Bundle();
            bundle.putSerializable("loanApplication", loanApplication);
            bundle.putFloat("interest", interest);
            WalletLoanDetailsFragment loanDetailsFragment = new WalletLoanDetailsFragment(bundle,getString(R.string.merchant_loan_details));
            openFragment(loanDetailsFragment);
        } else {
            Toast.makeText(context, "Enter valid code", Toast.LENGTH_LONG).show();
        }
    }


}