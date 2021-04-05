package com.cabral.emaishapay.fragments.wallet_fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.adapters.LoanPaymentHistoryListAdapter;
import com.cabral.emaishapay.adapters.LoansListAdapter;

import java.util.ConcurrentModificationException;

public class WalletLoanPaymentHistory extends Fragment {
    private Context context;
    private RecyclerView recyclerView;
    private LoanPaymentHistoryListAdapter loanPaymentHistoryListAdapter;
    private Toolbar toolbar;
    AppBarConfiguration appBarConfiguration;



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.payment_history, container, false);
        recyclerView = view.findViewById(R.id.payment_history_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        loanPaymentHistoryListAdapter = new LoanPaymentHistoryListAdapter();
        recyclerView.setAdapter(loanPaymentHistoryListAdapter);

        toolbar = view.findViewById(R.id.toolbar_payment_history);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Payment History");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        return view;
    }


}
