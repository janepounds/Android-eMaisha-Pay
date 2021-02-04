package com.cabral.emaishapay.fragments;

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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.models.LoanApplication;
import com.cabral.emaishapay.models.MerchantInfoResponse;
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
    RelativeLayout parentLayout;
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
        ProgressDialog dialog;
        dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(false);
        dialog.show();
        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;

        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<MerchantInfoResponse> call = apiRequests.getUserBusinessName(access_token,receiverPhoneNumber);
        call.enqueue(new Callback<MerchantInfoResponse>() {
            @Override
            public void onResponse(Call<MerchantInfoResponse> call, Response<MerchantInfoResponse> response) {
                if(response.code()==200){
                    businessName = response.body().getData().getBusinessName();
                    if(businessName.equalsIgnoreCase(account_name)){
                        LoanApplication loanApplication = new LoanApplication();
                        loanApplication.setPhone(phone);
                        //account exists
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("loanApplication", loanApplication);
                        WalletLoanDetailsFragment loanDetailsFragment = new WalletLoanDetailsFragment(bundle,getString(R.string.merchant_loan_details));
                        openFragment(loanDetailsFragment);

                    }



                    dialog.dismiss();
                }else if(response.code()==412) {
                     //Toast.makeText(context,"account does not exist",Toast.LENGTH_LONG).show();
                    final Snackbar snackBar = Snackbar.make(parentLayout, "Account does not exist!", Snackbar.LENGTH_LONG);
                            snackBar.setAction("DISMISS", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    snackBar.dismiss();
                                }
                            })
                            .show();

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

                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<MerchantInfoResponse> call, Throwable t) {

                Log.e("info : ", t.getMessage());
                Log.e("info : ", "Something got very very wrong");

                dialog.dismiss();
            }
        });


    }
}