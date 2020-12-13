package com.cabral.emaishapay.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cabral.emaishapay.DailogFragments.Buy;
import com.cabral.emaishapay.DailogFragments.TransferMoney;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletAuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.adapters.WalletTransactionAdapter;
import com.cabral.emaishapay.databinding.EmaishaPayHomeBinding;
import com.cabral.emaishapay.models.BalanceResponse;
import com.cabral.emaishapay.models.TransactionModel;
import com.cabral.emaishapay.models.WalletTransaction;
import com.cabral.emaishapay.models.WalletTransactionResponse;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletHomeFragment extends Fragment {
    private static final String TAG = "WalletHomeFragment";
    private EmaishaPayHomeBinding binding;
    private Context context;
    private NavController navController = null;
    private ProgressDialog progressDialog;
    private List<TransactionModel> models = new ArrayList<>();
    public static double balance = 0;
    public static FragmentManager fm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.emaisha_pay_home, container, false);

        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);

        fm = requireActivity().getSupportFragmentManager();
        getTransactionsData();
        binding.walletBalance.setText("UGX " + NumberFormat.getInstance().format(balance));
        updateBalance();

        binding.username.setText("Hi, " + ucf(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_LAST_NAME, context)) + " " + ucf(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_FIRST_NAME, context)));




        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        Bundle bundle = new Bundle();
        bundle.putDouble("balance", balance);

//        btnWalletDeposit.setOnClickListener(view19 -> navController.navigate(R.id.action_walletHomeFragment_to_depositPayments, bundle));
//        layoutWalletTransactions.setOnClickListener(view111 -> navController.navigate(R.id.action_walletHomeFragment_to_walletTransactionsListFragment));
//        btnWalletTransactions.setOnClickListener(view14 -> navController.navigate(R.id.action_walletHomeFragment_to_walletTransactionsListFragment));
        binding.layoutTransfer.setOnClickListener(view110 -> openTransfer());
        binding.layoutTopUp.setOnClickListener(view16 -> navController.navigate(R.id.action_walletHomeFragment_to_depositPayments, bundle));
        binding.layoutLoan.setOnClickListener(view13 -> navController.navigate(R.id.action_walletHomeFragment_to_walletLoansListFragment));
        binding.layoutPay.setOnClickListener(view1 -> openBuy());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private String ucf(String str) {
        if (str == null || str.length() < 2)
            return str;

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public void openTransfer() {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }

        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment transferDialog = new TransferMoney(context, balance, getActivity().getSupportFragmentManager());
        transferDialog.show(ft, "dialog");
    }



    private void getTransactionsData() {
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
                                models.add( new TransactionModel(getNameInitials( record.getString("receiver")),  record.getString("receiver"), record.getString("date"), "-"+record.getDouble("amount")) );
                            } else if (record.getString("type").equalsIgnoreCase("Purchase")) {
                                 models.add( new TransactionModel(getNameInitials( record.getString("receiver")),  record.getString("receiver"), record.getString("date"), "-"+record.getDouble("amount")) );

                            } else if (record.getString("type").equalsIgnoreCase("Deposit")) {
                                models.add( new TransactionModel(getNameInitials(record.getString("sender")), record.getString("sender"), record.getString("date"), "-"+record.getDouble("amount")) );

                                //data = new WalletTransaction(record.getString("date"), record.getString("receiver"), "credit", record.getDouble("amount"), record.getString("referenceNumber"));

                            } else if (record.getString("type").equalsIgnoreCase("Transfer")) {
                                String userName = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_FIRST_NAME, context) + " " + WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_LAST_NAME, context);

                                if (userName.equals(record.getString("sender"))) {
                                    models.add( new TransactionModel(getNameInitials( record.getString("receiver")),  record.getString("receiver"), record.getString("date"), "-"+record.getDouble("amount")) );
                                } else {
                                    models.add( new TransactionModel(getNameInitials(record.getString("sender")), record.getString("sender"), record.getString("date"), "+"+record.getDouble("amount")) );
                                }
                            } else if (record.getString("type").equalsIgnoreCase("Withdraw")) {
                                models.add( new TransactionModel(getNameInitials( record.getString("receiver")),  record.getString("receiver"), record.getString("date"), "-"+record.getDouble("amount")) );
                                //data = new WalletTransaction(, record.getString("receiver"), "debit", record.getDouble("amount"), record.getString("referenceNumber"));
                            }

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }finally {
                        if(models.size()>0){
                            WalletTransactionAdapter adapter = new WalletTransactionAdapter(requireContext(), models);
                            binding.recyclerView.setAdapter(adapter);
                            binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                            binding.recyclerView.setHasFixedSize(true);
                        }
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

    private String getNameInitials(String name){
        if(name.isEmpty())
            return null;
        String ini = ""+name.charAt(0);
        // we use ini to return the output
        for (int i=0; i<name.length(); i++){
            if ( name.charAt(i)==' ' && i+1 < name.length() && name.charAt(i+1)!=' '){
                //if i+1==name.length() you will have an indexboundofexception
                //add the initials
                ini+=name.charAt(i+1);
            }
        }
        //after getting "ync" => return "YNC"
        return ini.toUpperCase();
    }
    public void updateBalance() {
        progressDialog.show();

        String access_token = WalletAuthActivity.WALLET_ACCESS_TOKEN;
        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<BalanceResponse> call = apiRequests.requestBalance(access_token);
        call.enqueue(new Callback<BalanceResponse>() {
            @Override
            public void onResponse(@NotNull Call<BalanceResponse> call, @NotNull Response<BalanceResponse> response) {
                progressDialog.dismiss();

                if (response.code() == 200) {
                    balance = response.body().getData().getBalance();

                    Log.d(TAG, "onSuccess: Balance = " + balance);

                    binding.walletBalance.setText("UGX " + NumberFormat.getInstance().format(balance));

                } else if (response.code() == 401) {
                    Toast.makeText(context, "Session Expired", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getContext(), WalletAuthActivity.class));
                    getActivity().overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
                } else {
                    Log.e("info", new String(String.valueOf(response.body().getMessage())));
                }
            }

            @Override
            public void onFailure(@NotNull Call<BalanceResponse> call, @NotNull Throwable t) {
                progressDialog.dismiss();
                Log.e("info : ", new String(String.valueOf(t.getMessage())));
                Toast.makeText(context, "An error occurred Try again Later", Toast.LENGTH_LONG).show();
                WalletAuthActivity.startAuth(context, false);
            }
        });
    }

    public void openBuy() {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment buyFoodDialog = new Buy(context, requireActivity().getSupportFragmentManager());
        buyFoodDialog.show(ft, "dialog");
    }

    public void comingSoon() {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setIndeterminate(false);
        dialog.setMessage("Coming Soon ..!!");
        dialog.setCancelable(true);
        dialog.show();
    }
}