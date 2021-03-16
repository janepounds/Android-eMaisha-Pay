package com.cabral.emaishapay.fragments;

import android.app.ProgressDialog;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.DailogFragments.PayLoan;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.adapters.LoansListAdapter;
import com.cabral.emaishapay.models.CancelLoanResponse;
import com.cabral.emaishapay.models.LoanListResponse;
import com.cabral.emaishapay.models.LoanApplication;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletLoansListFragment extends Fragment {
    private static final String TAG = "WalletLoansList";
    private Context context;

    AppBarConfiguration appBarConfiguration;

    LoansListAdapter statementAdapter;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    private List<LoanApplication> dataList = new ArrayList<>();
    private float interest;
    private String possible_action;

    private Toolbar toolbar;
    private RelativeLayout walletApplyLoanLayout;
    private FrameLayout walletPayLoanLayout;
    private RecyclerView loansListRecyclerView;
    private Button walletApplyLoanBtn, walletPayLoanBtn;
    ProgressDialog dialog;

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

        walletApplyLoanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putFloat("interest", interest);
                bundle.putString("possible_action", possible_action);

                Fragment fragment = new WalletLoanDetailsFragment(bundle,getString(R.string.default_loan_details));
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                if (((WalletHomeActivity) getActivity()).currentFragment != null)
                    fragmentManager.beginTransaction()
                            .hide(((WalletHomeActivity) getActivity()).currentFragment)
                            .add(R.id.wallet_home_container, fragment)
                            .addToBackStack(null).commit();
                else
                    fragmentManager.beginTransaction()
                            .add(R.id.wallet_home_container, fragment)
                            .addToBackStack(null).commit();
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

        dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(false);
        dialog.show();

        /*************RETROFIT IMPLEMENTATION********************/

        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String userId = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, context);
        APIRequests apiRequests = APIClient.getWalletInstance();

        Call<LoanListResponse> call = apiRequests.getUserLoans(access_token,userId,request_id);

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

                                        Log.d(TAG, "onClick: interest"+interest);
                                        Fragment fragment = new WalletLoanDetailsFragment(bundle,getString(R.string.default_loan_details));
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        if (((WalletHomeActivity) getActivity()).currentFragment != null)
                                            fragmentManager.beginTransaction()
                                                    .hide(((WalletHomeActivity) getActivity()).currentFragment)
                                                    .add(R.id.wallet_home_container, fragment)
                                                    .addToBackStack(null).commit();
                                        else
                                            fragmentManager.beginTransaction()
                                                    .add(R.id.wallet_home_container, fragment)
                                                    .addToBackStack(null).commit();

                                }
                                });

                            }else if(possible_action.equalsIgnoreCase("Cancel Loan")){
                                walletApplyLoanBtn.setText(possible_action);
                                walletApplyLoanBtn.setOnClickListener(view2 ->
                                        {
                                            dialog.show();
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

                dialog.dismiss();
            }

            @Override
            public void onFailure(@NotNull Call<LoanListResponse> call, @NotNull Throwable t) {

                Log.e("info : ", new String(String.valueOf(t.getMessage())));
                Log.e("info : ", "Something got very wrong");

                dialog.dismiss();
                TokenAuthActivity.startAuth(getActivity(), true);
                getActivity().finishAffinity();
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

        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String userId = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, context);
        APIRequests apiRequests = APIClient.getWalletInstance();

        Call<CancelLoanResponse> call = apiRequests.cancelLoanRequest(access_token,userId,request_id);
        call.enqueue(new Callback<CancelLoanResponse>() {
            @Override
            public void onResponse(Call<CancelLoanResponse> call, Response<CancelLoanResponse> response) {
                if(response.isSuccessful()){
                    String message = response.body().getMessage();
                    Toast.makeText(context,message,Toast.LENGTH_LONG);
                }else{
                    Log.d(TAG, "onResponse: failed");
                }
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<CancelLoanResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());

            }
        });
        dialog.dismiss();

    }

    @Override
    public void onResume() {
        super.onResume();
        dataList.clear();
    }



}