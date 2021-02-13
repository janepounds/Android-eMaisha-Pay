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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cabral.emaishapay.DailogFragments.DepositPayments;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.adapters.WalletTransactionsListAdapter;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.databinding.EmaishaPayHomeBinding;
import com.cabral.emaishapay.models.BalanceResponse;
import com.cabral.emaishapay.models.WalletTransactionResponse;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;
import com.cabral.emaishapay.utils.CryptoUtil;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletHomeFragment extends Fragment {
    private static final String TAG = "WalletHomeFragment";
    private EmaishaPayHomeBinding binding;
    private Context context;
    private ProgressDialog progressDialog;
    private final int transactions_limit=4;
    private List<WalletTransactionResponse.TransactionData.Transactions> models = new ArrayList<>();
    public static double balance = 0, commisionbalance=0,totalBalance=0;
    public static FragmentManager fm;
    DialogLoader dialog;
    public WalletHomeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.emaisha_pay_home, container, false);
        dialog = new DialogLoader(getContext());

        fm = requireActivity().getSupportFragmentManager();

        getTransactionsData();
        getBalanceAndCommission();

        String name=ucf(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_FIRST_NAME, context));

        binding.username.setText("Hi, "+ ucf(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_FIRST_NAME, context))+" "+ucf(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_LAST_NAME, context)));



        String role = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,context);
        if(role.equalsIgnoreCase("agent")){
            WalletHomeActivity.disableNavigation();
            WalletHomeActivity.setUpMasterAgentNav();
            binding.layoutTransactWithCustomers.setVisibility(View.VISIBLE);
            binding.labelTransact.setVisibility(View.VISIBLE);
            binding.layoutTransfer.setVisibility(View.INVISIBLE);
            binding.layoutSettle.setVisibility(View.VISIBLE);
            binding.cardBalanceLabel.setText("Commission");

        }else if(role.equalsIgnoreCase("merchant")){
            WalletHomeActivity.disableNavigation();
            WalletHomeActivity.setUpMasterAgentNav();
            binding.layoutTransactWithCustomers.setVisibility(View.VISIBLE);
            binding.labelTransact.setVisibility(View.VISIBLE);
            binding.layoutTransfer.setVisibility(View.INVISIBLE);
            binding.layoutSettle.setVisibility(View.VISIBLE);
            binding.cardBalanceLabel.setText("Commission");

        }else if(role.equalsIgnoreCase("agent merchant")){
            WalletHomeActivity.disableNavigation();
            WalletHomeActivity.setUpMasterAgentNav();
            binding.layoutTransactWithCustomers.setVisibility(View.VISIBLE);
            binding.labelTransact.setVisibility(View.VISIBLE);
            binding.layoutTransfer.setVisibility(View.INVISIBLE);
            binding.layoutSettle.setVisibility(View.VISIBLE);
            binding.cardBalanceLabel.setText("Commission");


        }else{
            binding.layoutTransactWithCustomers.setVisibility(View.GONE);
            binding.labelTransact.setVisibility(View.GONE);
            binding.layoutTransfer.setVisibility(View.VISIBLE);
            binding.layoutSettle.setVisibility(View.INVISIBLE);
            binding.cardBalanceLabel.setText("Card");
            Log.d(TAG, "onCreateView: *"+role+"*");
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


//        btnWalletDeposit.setOnClickListener(view19 -> navController.navigate(R.id.action_walletHomeFragment_to_depositPayments, bundle));
//        layoutWalletTransactions.setOnClickListener(view111 -> navController.navigate(R.id.action_walletHomeFragment_to_walletTransactionsListFragment));
//        btnWalletTransactions.setOnClickListener(view14 -> navController.navigate(R.id.action_walletHomeFragment_to_walletTransactionsListFragment));
        binding.layoutTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new TransferMoney(balance,getString(R.string.transactions));
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
        binding.layoutSettle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new TransferMoney(balance,getString(R.string.settlements));
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
        binding.layoutTopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction ft = fm.beginTransaction();
                Fragment prev = fm.findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                DialogFragment depositDialog = new DepositPayments( WalletHomeFragment.balance);
                depositDialog.show(ft, "dialog");
            }
        });

        binding.layoutLoan.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {

                  Fragment fragment = new WalletLoansListFragment();
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
          }
        );
        binding.layoutPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new PayFragment();
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
        binding.moreTransactionCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To WalletTrasactionListFragment
                Fragment fragment= new WalletTransactionsListFragment(getString(R.string.transactions));
                FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
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
        DialogLoader dialog;
        dialog = new DialogLoader(context);
        dialog.showProgressDialog();

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
                            binding.noTransactionCards.setVisibility(View.VISIBLE);
                        }
                    }


                    dialog.hideProgressDialog();
                } else if (response.code() == 401) {

                    TokenAuthActivity.startAuth(getContext(), true);
                    if (response.errorBody() != null) {
                        Log.e("info", new String(String.valueOf(response.errorBody())));
                    } else {
                        Log.e("info", "Something got very very wrong");
                    }
                    dialog.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<WalletTransactionResponse> call, Throwable t) {
                dialog.hideProgressDialog();
            }
        });


    }

    public void getBalanceAndCommission() {
        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<BalanceResponse> call = apiRequests.requestBalance(access_token);
        call.enqueue(new Callback<BalanceResponse>() {
            @Override
            public void onResponse(@NotNull Call<BalanceResponse> call, @NotNull Response<BalanceResponse> response) {
                dialog.hideProgressDialog();

                if (response.code() == 200) {


                  balance =  response.body().getData().getBalance();
                  commisionbalance = response.body().getData().getCommission();
                  totalBalance = response.body().getData().getTotalBalance();

                  //set balance and commision
                    binding.totalBalance.setText(getString(R.string.currency)+" "+ NumberFormat.getInstance().format(totalBalance));
                    binding.walletBalance.setText(getString(R.string.currency)+" "+ NumberFormat.getInstance().format(balance));
                    binding.cardBalance.setText(getString(R.string.currency)+" "+ NumberFormat.getInstance().format(commisionbalance));

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
                dialog.hideProgressDialog();
                Log.e("info : ", new String(String.valueOf(t.getMessage())));
                Toast.makeText(context, "An error occurred Try again Later", Toast.LENGTH_LONG).show();
                TokenAuthActivity.startAuth(context, false);
            }
        });
    }










  
}