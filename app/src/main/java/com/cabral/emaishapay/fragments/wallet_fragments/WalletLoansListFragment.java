package com.cabral.emaishapay.fragments.wallet_fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.DailogFragments.PayLoan;
import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.adapters.LoansListAdapter;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.models.CancelLoanResponse;
import com.cabral.emaishapay.models.LoanListResponse;
import com.cabral.emaishapay.models.LoanApplication;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.APIRequests;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletLoansListFragment extends Fragment {
    private static final String TAG = "WalletLoansList";
    private Context context;

    final String applicantType="Applicant_Type";
    LoansListAdapter statementAdapter;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    private final List<LoanApplication> dataList = new ArrayList<>();
    private float interest;
    private String possible_action;

    private Toolbar toolbar;
    private RelativeLayout walletApplyLoanLayout;
    private FrameLayout walletPayLoanLayout;
    private RecyclerView loansListRecyclerView;
    private Button walletApplyLoanBtn, walletPayLoanBtn;
    DialogLoader dialogLoader;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet_loans_list, container, false);
        WalletHomeActivity.bottomNavigationView.setVisibility(View.GONE);
        toolbar = view.findViewById(R.id.toolbar_wallet_loans_list);
        walletApplyLoanLayout = view.findViewById(R.id.wallet_apply_loan_layout);
        walletPayLoanLayout = view.findViewById(R.id.wallet_pay_loan_layout);
        loansListRecyclerView = view.findViewById(R.id.loans_list_recycler_view);
        walletApplyLoanBtn = view.findViewById(R.id.wallet_apply_loan_btn);
        walletPayLoanBtn = view.findViewById(R.id.wallet_pay_loan_btn);

        walletPayLoanLayout.setVisibility(View.GONE);
        loansListRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        statementAdapter = new LoansListAdapter(getContext(),dataList);
        loansListRecyclerView.setAdapter(statementAdapter);
        dialogLoader = new DialogLoader(context);

        walletApplyLoanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putFloat("interest", interest);
                bundle.putString(applicantType, getString(R.string.default_loan_details));

                //To  WalletLoanDetailsFragment
                WalletHomeActivity.navController.navigate(R.id.action_walletLoansListFragment_to_walletLoanDetailsFragment,bundle);
            }
        });

        actualStatementData();

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Loan History");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);


        return view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void actualStatementData() {

        dialogLoader.showProgressDialog();

        /*************RETROFIT IMPLEMENTATION********************/

        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String userId = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, context);
        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());

        Call<LoanListResponse> call = apiRequests.getUserLoans(access_token,userId,request_id,category,"getUserLoans");

        call.enqueue(new Callback<LoanListResponse>() {
            @Override
            public void onResponse(@NotNull Call<LoanListResponse> call, @NotNull Response<LoanListResponse> response) {
                Log.d(TAG, "onResponse: Call Successful");
                Log.d(TAG, "onResponse: Code = " + response.code());


                if (response.code() == 200) {

                        List<LoanApplication> loans = response.body().getLoans();

                        interest = (float) response.body().getInterest();
                        String possible_action = response.body().getPossible_action();

                        for (int i = 0; i < loans.size(); i++) {

                            LoanApplication record = loans.get(i);
                            dataList.add(record);


                            Log.d(TAG, "onResponse: "+possible_action);

                            //check possible action

                            if(possible_action.equalsIgnoreCase("Apply For Loan")){
                                walletApplyLoanBtn.setText(possible_action);
                                walletApplyLoanBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle bundle = new Bundle();
                                        bundle.putFloat("interest", interest);
                                        bundle.putString(applicantType, getString(R.string.default_loan_details));

                                        //To WalletLoanDetailsFragment
                                        WalletHomeActivity.navController.navigate(R.id.action_walletLoansListFragment_to_walletLoanDetailsFragment,bundle);

                                }
                                });

                            }else if(possible_action.equalsIgnoreCase("Cancel Loan")){
                                walletApplyLoanBtn.setText(possible_action);
                                walletApplyLoanBtn.setOnClickListener(view2 ->
                                        {
                                            dialogLoader.hideProgressDialog();
                                            cancelLoan() ;
                                        });
                            }else if(possible_action.equalsIgnoreCase("Pay Loan")){
                                walletApplyLoanBtn.setText(possible_action);
                                walletApplyLoanBtn.setOnClickListener(view2 -> payLoan());

                            }
                            //set possible action



                            Log.d(TAG, "onResponse: Data = " + record);
                        }

                        statementAdapter.notifyDataSetChanged();

                }
                else if (response.code() == 401) {

                    if (response.errorBody() != null) {
                        Log.e("info", String.valueOf(response.errorBody()));
                    } else {
                        Log.e("info", "Something got very very wrong");
                    }
                }

                dialogLoader.hideProgressDialog();
            }

            @Override
            public void onFailure(@NotNull Call<LoanListResponse> call, @NotNull Throwable t) {

                Log.e("info : ", String.valueOf(t.getMessage()));
                Log.e("info : ", "Something got very wrong");

                dialogLoader.hideProgressDialog();
                TokenAuthFragment.startAuth( true);

            }
        });
    }

    public void payLoan() {
        FragmentManager fm = requireActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment payLoandialog = new PayLoan(context, fm);
        payLoandialog.show(ft, "dialog");
    }

    public void cancelLoan(){

        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String userId = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, context);
        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
        Call<CancelLoanResponse> call = apiRequests.cancelLoanRequest(access_token,userId,request_id,category,"cancelRequest");
        call.enqueue(new Callback<CancelLoanResponse>() {
            @Override
            public void onResponse(Call<CancelLoanResponse> call, Response<CancelLoanResponse> response) {
                if(response.isSuccessful()){
                    String message = response.body().getMessage();
                    Toast.makeText(context,message,Toast.LENGTH_LONG);
                }else{
                    Log.d(TAG, "onResponse: failed");
                }
                dialogLoader.hideProgressDialog();

            }

            @Override
            public void onFailure(Call<CancelLoanResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());

            }
        });
        dialogLoader.hideProgressDialog();

    }

    @Override
    public void onResume() {
        super.onResume();
        dataList.clear();
    }



}