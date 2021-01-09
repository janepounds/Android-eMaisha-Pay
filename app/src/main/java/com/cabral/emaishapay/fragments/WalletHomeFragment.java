package com.cabral.emaishapay.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.adapters.WalletTransactionsListAdapter;
import com.cabral.emaishapay.databinding.EmaishaPayHomeBinding;
import com.cabral.emaishapay.models.BalanceResponse;
import com.cabral.emaishapay.models.WalletTransactionResponse;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
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
    private final int transactions_limit=4;
    private List<WalletTransactionResponse.TransactionData.Transactions> models = new ArrayList<>();
    public static double balance = 0;
    public static FragmentManager fm;
    public WalletHomeFragment(){}

    public WalletHomeFragment(Context context, FragmentManager supportFragmentManager) {
        super();
        this.context = context;
        this.fm = supportFragmentManager;

    }
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

        binding.walletBalance.setText("UGX " +WalletHomeActivity.getPreferences(String.valueOf(WalletHomeActivity.PREFERENCE_WALLET_BALANCE),context));
        new MyTask(WalletHomeFragment.this).execute();

        binding.username.setText("Hi, " + ucf(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_LAST_NAME, context)) + " " + ucf(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_FIRST_NAME, context)));

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);


//        btnWalletDeposit.setOnClickListener(view19 -> navController.navigate(R.id.action_walletHomeFragment_to_depositPayments, bundle));
//        layoutWalletTransactions.setOnClickListener(view111 -> navController.navigate(R.id.action_walletHomeFragment_to_walletTransactionsListFragment));
//        btnWalletTransactions.setOnClickListener(view14 -> navController.navigate(R.id.action_walletHomeFragment_to_walletTransactionsListFragment));
        binding.layoutTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putDouble("balance",balance);
                navController.navigate(R.id.action_walletHomeFragment_to_transferMoney, bundle);
            }
        });
        binding.layoutTopUp.setOnClickListener(view16 -> navController.navigate(R.id.action_walletHomeFragment_to_depositPayments));
        binding.layoutLoan.setOnClickListener(view13 -> navController.navigate(R.id.action_walletHomeFragment_to_walletLoansListFragment));
        binding.layoutPay.setOnClickListener(view1 -> navController.navigate(R.id.action_walletHomeFragment_to_payFragment));
        binding.moreTransactionCards.setOnClickListener(view11 -> navController.navigate(R.id.action_walletHomeFragment_to_walletTransactionsListFragment));

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

    private void getTransactionsData() {
        ProgressDialog dialog;
        dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(false);
        dialog.show();

        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;

        /**********RETROFIT IMPLEMENTATION************/
        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<WalletTransactionResponse> call = apiRequests.transactionList2(access_token,transactions_limit);

        call.enqueue(new Callback<WalletTransactionResponse>() {
            @Override
            public void onResponse(Call<WalletTransactionResponse> call, Response<WalletTransactionResponse> response) {
                if (response.code() == 200) {
                    try {
                        WalletTransactionResponse.TransactionData walletTransactionResponseData = response.body().getData();
                        List<WalletTransactionResponse.TransactionData.Transactions> transactions = walletTransactionResponseData.getTransactions();
                        models.clear();
                        if(transactions.size()!=0){
                            int loop_limit=transactions_limit;
                            if(transactions.size()<transactions_limit)
                                loop_limit=transactions.size();

                            for (int i = 0; i < loop_limit; i++) {
                                WalletTransactionResponse.TransactionData.Transactions res = transactions.get(i);
                                models.add( res );
                            }
                        }


                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    finally {
                        if(models.size()>0){
                            WalletTransactionsListAdapter adapter = new WalletTransactionsListAdapter( models, getActivity().getSupportFragmentManager());
                            binding.recyclerView.setAdapter(adapter);
                            binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                            binding.recyclerView.setHasFixedSize(true);
                        } else{
                            binding.moreTransactionCards.setVisibility(View.GONE);
                        }
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

    public void updateBalance() {
        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<BalanceResponse> call = apiRequests.requestBalance(access_token);
        call.enqueue(new Callback<BalanceResponse>() {
            @Override
            public void onResponse(@NotNull Call<BalanceResponse> call, @NotNull Response<BalanceResponse> response) {
                progressDialog.dismiss();

                if (response.code() == 200) {
                    balance = response.body().getData().getBalance();

                    Log.d(TAG, "onSuccess: Balance = " + balance);

                    //save balance in shared preference
                     WalletHomeActivity.savePreferences(String.valueOf(WalletHomeActivity.PREFERENCE_WALLET_BALANCE), String.valueOf(balance), context);

                    Log.d(TAG, "onResponse: "+WalletHomeActivity.getPreferences(String.valueOf(WalletHomeActivity.PREFERENCE_WALLET_BALANCE), context));


                } else if (response.code() == 401) {
                    Toast.makeText(context, "Session Expired", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getContext(), TokenAuthActivity.class));
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
                TokenAuthActivity.startAuth(context, false);
            }
        });
    }


    private class MyTask extends AsyncTask<String, Void, String> {
        private WeakReference<WalletHomeFragment> fragmentReference;
        private Context context;

        // only retain a weak reference to the activity
        MyTask(WalletHomeFragment context) {
            fragmentReference = new WeakReference<>(context);
            progressDialog = new ProgressDialog(context.context);
            this.context = context.context;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            //update balance
            updateBalance();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fragmentReference.get().getActivity().runOnUiThread(() -> {
                                //get balanace from shared preference and update it in textview

                                binding.walletBalance.setText("UGX " + WalletHomeActivity.getPreferences(String.valueOf(WalletHomeActivity.PREFERENCE_WALLET_BALANCE), context));

                                Log.d(TAG, "run: reached !!");

                            });
                        }
                    });



                }
            }, 3000);



        }
    }

  
}