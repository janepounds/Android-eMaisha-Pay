package com.cabral.emaishapay.fragments.wallet_fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.adapters.WalletTransactionsListAdapter;
import com.cabral.emaishapay.models.WalletTransactionResponse;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.api_helpers.APIRequests;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletTransactionsListFragment extends Fragment {
    private static final String TAG = "WalletTransactionsList";
    private Context context;


    RecyclerView.Adapter statementAdapter;
    private List<WalletTransactionResponse.TransactionData.Transactions> dataList = new ArrayList<>();
    private String appTitle;
    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView arrowCashIn,arrowCashOut;
    TextView cashInText,cashOutText,walletCashIn,walletCashOut;
    String key_title="KEY_TITLE";

    public WalletTransactionsListFragment(){

    }
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.transaction_list, container, false);
        WalletHomeActivity.bottom_navigation_shop.setVisibility(View.GONE);
        if( getArguments()!=null)
            appTitle=getArguments().getString(key_title);

        toolbar = view.findViewById(R.id.toolbar_wallet_transactions_list);
        recyclerView = view.findViewById(R.id.statement_recycler_view);
        arrowCashIn = view.findViewById(R.id.arrow_cash_in);
        arrowCashOut = view.findViewById(R.id.arrow_cash_out);
        cashInText = view.findViewById(R.id.txt_cash_in);
        cashOutText = view.findViewById(R.id.text_cash_out);
        walletCashIn = view.findViewById(R.id.wallet_cash_in);
        walletCashOut = view.findViewById(R.id.wallet_cash_out);

        if(appTitle.equalsIgnoreCase("settlements")){
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            toolbar.setTitle(this.appTitle);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);

            arrowCashIn.setImageResource(R.drawable.bank);
            arrowCashOut.setImageResource(R.drawable.ic_account_opening);
            cashInText.setText("Mobile Money");
            cashOutText.setText("Bank");
            walletCashIn.setTextColor(getResources().getColor(R.color.textRed));
            walletCashOut.setTextColor(getResources().getColor(R.color.textRed));
            getSettlements();
        }else{
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            toolbar.setTitle(this.appTitle);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

            arrowCashIn.setImageResource(R.drawable.ic_cashin);
            arrowCashOut.setImageResource(R.drawable.ic_diagonal_arrow);
            cashInText.setText("Cash In");
            cashOutText.setText("Cash Out");
            walletCashIn.setTextColor(getResources().getColor(R.color.colorPrimary));
            walletCashOut.setTextColor(getResources().getColor(R.color.textRed));
            actualStatementData();
        }



        return view;
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void actualStatementData() {
        ProgressDialog dialog;
        dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(false);
        dialog.show();
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());


        /**********RETROFIT IMPLEMENTATION************/
        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        Call<WalletTransactionResponse> call = apiRequests.transactionList(access_token,request_id,category,"getTransactionLogs");

        call.enqueue(new Callback<WalletTransactionResponse>() {
            @Override
            public void onResponse(Call<WalletTransactionResponse> call, Response<WalletTransactionResponse> response) {
                if (response.code() == 200) {
                    try {
                        WalletTransactionResponse.TransactionData walletTransactionResponseData = response.body().getData();
                        dataList = walletTransactionResponseData.getTransactions();
                        double cashout = walletTransactionResponseData.getCashout();
                        double cashin = walletTransactionResponseData.getCashin();
                        walletCashIn.setText(getString(R.string.currency)+" "+ NumberFormat.getInstance().format(cashin) );
                        walletCashOut.setText(getString(R.string.currency)+" "+NumberFormat.getInstance().format(cashout));
                        Log.e("Cahsout_In",cashout+" "+cashin);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        Log.w("TamsactionsList",dataList.size()+"**********");

                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        statementAdapter = new WalletTransactionsListAdapter(dataList, requireActivity().getSupportFragmentManager());
                        recyclerView.setAdapter(statementAdapter);
                        statementAdapter.notifyDataSetChanged();
                    }


                    dialog.dismiss();
                } else if (response.code() == 401) {

                    TokenAuthFragment.startAuth( true);

                    if (response.errorBody() != null) {
                        Log.e("info", new String(String.valueOf(response.errorBody())));
                    } else {
                        Log.e("info", "Something got very very wrong");
                    }
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<WalletTransactionResponse> call, Throwable t) {
                dialog.dismiss();
            }
        });


    }

    private void getSettlements(){
        ProgressDialog dialog;
        dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(false);
        dialog.show();
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
        /**********RETROFIT IMPLEMENTATION************/
        APIRequests apiRequests = APIClient.getWalletInstance(getContext());
        Call<WalletTransactionResponse> call = apiRequests.getSettlements(access_token,request_id,category,"getSettlements");

        call.enqueue(new Callback<WalletTransactionResponse>() {
            @Override
            public void onResponse(Call<WalletTransactionResponse> call, Response<WalletTransactionResponse> response) {
                if (response.code() == 200) {
                    try {
                        WalletTransactionResponse.TransactionData walletTransactionResponseData = response.body().getData();
                        dataList = walletTransactionResponseData.getTransactions();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        Log.w("Settlements",dataList.size()+"**********");

                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        statementAdapter = new WalletTransactionsListAdapter(dataList, requireActivity().getSupportFragmentManager());
                        recyclerView.setAdapter(statementAdapter);
                        statementAdapter.notifyDataSetChanged();
                    }


                    dialog.dismiss();
                } else if (response.code() == 401) {

                    TokenAuthFragment.startAuth( true);

                    if (response.errorBody() != null) {
                        Log.e("info", new String(String.valueOf(response.errorBody())));
                    } else {
                        Log.e("info", "Something got very very wrong");
                    }
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<WalletTransactionResponse> call, Throwable t) {
                dialog.dismiss();
            }
        });
    }
}