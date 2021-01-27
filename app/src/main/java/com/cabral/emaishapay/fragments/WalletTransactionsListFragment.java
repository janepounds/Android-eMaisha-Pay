package com.cabral.emaishapay.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.adapters.WalletTransactionsListAdapter;
import com.cabral.emaishapay.models.WalletTransactionResponse;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;

import org.jetbrains.annotations.NotNull;

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

    public WalletTransactionsListFragment(String appTitle){
        this.appTitle=appTitle;
    }
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.transaction_list, container, false);

        toolbar = view.findViewById(R.id.toolbar_wallet_transactions_list);
        recyclerView = view.findViewById(R.id.statement_recycler_view);

        actualStatementData();

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(this.appTitle);
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
        ProgressDialog dialog;
        dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(false);
        dialog.show();
        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;

        /**********RETROFIT IMPLEMENTATION************/
        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<WalletTransactionResponse> call = apiRequests.transactionList(access_token);

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
                        Log.w("TamsactionsList",dataList.size()+"**********");

                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        statementAdapter = new WalletTransactionsListAdapter(dataList, requireActivity().getSupportFragmentManager());
                        recyclerView.setAdapter(statementAdapter);
                        statementAdapter.notifyDataSetChanged();
                    }


                    dialog.dismiss();
                } else if (response.code() == 401) {

                    TokenAuthActivity.startAuth(context, true);
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