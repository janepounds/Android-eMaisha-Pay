package com.cabral.emaishapay.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.activities.WalletAuthActivity;
import com.cabral.emaishapay.adapters.WalletTransactionsListAdapter;
import com.cabral.emaishapay.models.WalletTransactionResponse;
import com.cabral.emaishapay.models.WalletTransaction;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletTransactionsListFragment extends Fragment {
    private static final String TAG = "WalletTransactionsList";
    private Context context;

    AppBarConfiguration appBarConfiguration;

    RecyclerView.Adapter statementAdapter;
    private List<WalletTransaction> dataList = new ArrayList<>();

    Toolbar toolbar;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet_transactions_list, container, false);

        toolbar = view.findViewById(R.id.toolbar_wallet_transactions_list);
        recyclerView = view.findViewById(R.id.statement_recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        statementAdapter = new WalletTransactionsListAdapter(dataList, requireActivity().getSupportFragmentManager());
        recyclerView.setAdapter(statementAdapter);

        actualStatementData();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(view);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
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
        String access_token = WalletAuthActivity.WALLET_ACCESS_TOKEN;

        /**********RETROFIT IMPLEMENTATION************/
        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<WalletTransactionResponse> call = apiRequests.transactionList(access_token);

        call.enqueue(new Callback<WalletTransactionResponse>() {
            @Override
            public void onResponse(Call<WalletTransactionResponse> call, Response<WalletTransactionResponse> response) {
                if (response.code() == 200) {
                    try {
                        WalletTransaction data = null;
                        WalletTransactionResponse.TransactionData walletTransactionResponseData = response.body().getData();
                        List<WalletTransactionResponse.TransactionData.Transactions> transactions = walletTransactionResponseData.getTransactions();
                        for (int i = 0; i < transactions.size(); i++) {
                            WalletTransactionResponse.TransactionData.Transactions res = transactions.get(i);
                            Gson gson = new Gson();
                            String ress = gson.toJson(res);
                            JSONObject record = new JSONObject(ress);
                            //type
                            if (record.getString("type").equalsIgnoreCase("Charge")) {
                                data = new WalletTransaction(record.getString("date"), record.getString("receiver"), "debit", record.getDouble("amount"), record.getString("referenceNumber"));
                            } else if (record.getString("type").equalsIgnoreCase("Purchase")) {
                                data = new WalletTransaction(record.getString("date"), record.getString("receiver"), "debit", record.getDouble("amount"), record.getString("referenceNumber"));
                                data.setIsPurchase(true);
                            } else if (record.getString("type").equalsIgnoreCase("Deposit")) {
                                data = new WalletTransaction(record.getString("date"), record.getString("receiver"), "credit", record.getDouble("amount"), record.getString("referenceNumber"));

                            } else if (record.getString("type").equalsIgnoreCase("Transfer")) {
                                String userName = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_FIRST_NAME, context) + " " + WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_LAST_NAME, context);

                                if (userName.equals(record.getString("sender"))) {
                                    data = new WalletTransaction(record.getString("date"), record.getString("receiver"), "debit", record.getDouble("amount"), record.getString("referenceNumber"));
                                } else {
                                    data = new WalletTransaction(record.getString("date"), record.getString("sender"), "credit", record.getDouble("amount"), record.getString("referenceNumber"));
                                }
                            } else if (record.getString("type").equalsIgnoreCase("Withdraw")) {
                                data = new WalletTransaction(record.getString("date"), record.getString("receiver"), "debit", record.getDouble("amount"), record.getString("referenceNumber"));
                            }
                            if (data != null) {
                                dataList.add(data);
                            }
                        }
                        statementAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    dialog.dismiss();
                } else if (response.code() == 401) {

                    WalletAuthActivity.startAuth(context, true);
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