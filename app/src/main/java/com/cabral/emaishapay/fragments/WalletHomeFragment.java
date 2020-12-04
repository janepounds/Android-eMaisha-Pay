package com.cabral.emaishapay.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.cabral.emaishapay.DailogFragments.Buy;
import com.cabral.emaishapay.DailogFragments.TransferMoney;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.activities.WalletAuthActivity;
import com.cabral.emaishapay.models.BalanceResponse;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;

import java.text.NumberFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletHomeFragment extends Fragment {
    private static final String TAG = "WalletHomeFragment";
    private Context context;
    private NavController navController = null;

    public static double balance = 0;
    ActionBar actionBar;
    public static FragmentManager fm;

    //Imageucf
    private ProgressDialog progressDialog;

    Toolbar toolbar;
    TextView walletBalance, usernameWalletHome;
    LinearLayout layoutWalletTransfer, layoutWalletTransactions, layoutWalletLoans, layoutWalletCoupons, layoutWalletBuy;
    ImageView btnWalletDeposit, btnWalletTransfer, btnWalletTransactions, btnWalletLoans, btnWalletCoupons, btnWalletBuy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.emaisha_pay_home, container, false);

//        progressDialog = new ProgressDialog(context);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Please Wait..");
//        progressDialog.setCancelable(false);
//
//        toolbar = view.findViewById(R.id.toolbar_wallet_home);
//        walletBalance = view.findViewById(R.id.wallet_balance);
//        usernameWalletHome = view.findViewById(R.id.username_wallet_home);
//        btnWalletDeposit = view.findViewById(R.id.btn_wallet_deposit);
//        layoutWalletTransfer = view.findViewById(R.id.layout_wallet_transfer);
//        layoutWalletTransactions = view.findViewById(R.id.layout_wallet_transactions);
//        layoutWalletLoans = view.findViewById(R.id.layout_wallet_loans);
//        layoutWalletCoupons = view.findViewById(R.id.layout_wallet_coupons);
//        layoutWalletBuy = view.findViewById(R.id.layout_wallet_buy);
//        btnWalletTransfer = view.findViewById(R.id.btn_wallet_transfer);
//        btnWalletTransactions = view.findViewById(R.id.btn_wallet_transactions);
//        btnWalletLoans = view.findViewById(R.id.btn_wallet_loans);
//        btnWalletCoupons = view.findViewById(R.id.btn_wallet_coupons);
//        btnWalletBuy = view.findViewById(R.id.btn_wallet_buy);
//
//        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
//        setHasOptionsMenu(true);
//
//        actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
//        assert actionBar != null;
//        actionBar.setTitle(getResources().getString(R.string.my_wallet));
//
//        actionBar.setHomeButtonEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);
//
//        fm = requireActivity().getSupportFragmentManager();
//
//        walletBalance.setText("UGX " + NumberFormat.getInstance().format(balance));
//
//        updateBalance();
//
//        usernameWalletHome.setText(ucf(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_LAST_NAME, context)) + " " + ucf(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_FIRST_NAME, context)));
//
//        Log.d(TAG, "onCreateView: Name = " + ucf(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_LAST_NAME, context)) + " " + ucf(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_FIRST_NAME, context)));

        // return view;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

//        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
//
//        Bundle bundle = new Bundle();
//        bundle.putDouble("balance", balance);
//
//        btnWalletDeposit.setOnClickListener(view19 -> navController.navigate(R.id.action_walletHomeFragment_to_depositPayments, bundle));
//        layoutWalletTransfer.setOnClickListener(view110 -> openTransfer());
//        layoutWalletTransactions.setOnClickListener(view111 -> navController.navigate(R.id.action_walletHomeFragment_to_walletTransactionsListFragment));
//        layoutWalletLoans.setOnClickListener(view112 -> navController.navigate(R.id.action_walletHomeFragment_to_walletLoansListFragment));
//        layoutWalletCoupons.setOnClickListener(view18 -> comingSoon());
//        layoutWalletBuy.setOnClickListener(view17 -> openBuy());
//        btnWalletDeposit.setOnClickListener(view16 -> navController.navigate(R.id.action_walletHomeFragment_to_depositPayments, bundle));
//        btnWalletTransfer.setOnClickListener(view15 -> openTransfer());
//        btnWalletTransactions.setOnClickListener(view14 -> navController.navigate(R.id.action_walletHomeFragment_to_walletTransactionsListFragment));
//        btnWalletLoans.setOnClickListener(view13 -> navController.navigate(R.id.action_walletHomeFragment_to_walletLoansListFragment));
//        btnWalletCoupons.setOnClickListener(view12 -> comingSoon());
//        btnWalletBuy.setOnClickListener(view1 -> openBuy());
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
        DialogFragment transferDailog = new TransferMoney(context, balance, getActivity().getSupportFragmentManager());
        transferDailog.show(ft, "dialog");
    }

    public void updateBalance() {
        progressDialog.show();

        String access_token = WalletAuthActivity.WALLET_ACCESS_TOKEN;
        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<BalanceResponse> call = apiRequests.requestBalance(access_token);
        call.enqueue(new Callback<BalanceResponse>() {
            @Override
            public void onResponse(Call<BalanceResponse> call, Response<BalanceResponse> response) {
                progressDialog.dismiss();

                if (response.code() == 200) {
                    balance = response.body().getData().getBalance();

                    Log.d(TAG, "onSuccess: Balance = " + balance);

                    walletBalance.setText("UGX " + NumberFormat.getInstance().format(balance));

                } else if (response.code() == 401) {
                    Toast.makeText(context, "Session Expired", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getContext(), WalletAuthActivity.class));
                    getActivity().overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
                } else {
                    Log.e("info", new String(String.valueOf(response.body().getMessage())));
                }
            }

            @Override
            public void onFailure(Call<BalanceResponse> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("info : ", new String(String.valueOf(t.getMessage())));
                Toast.makeText(context, "An error occurred Try again Later", Toast.LENGTH_LONG).show();
                WalletAuthActivity.startAuth(context, false);
            }
        });
    }

    public void openBuy() {
        FragmentTransaction ft = this.fm.beginTransaction();
        Fragment prev = this.fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment buyfoodDailog = new Buy(context, getActivity().getSupportFragmentManager());
        buyfoodDailog.show(ft, "dialog");
    }

    public void comingSoon() {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setIndeterminate(false);
        dialog.setMessage("Coming Soon ..!!");
        dialog.setCancelable(true);
        dialog.show();
    }
}