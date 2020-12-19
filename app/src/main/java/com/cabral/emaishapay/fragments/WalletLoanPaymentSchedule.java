package com.cabral.emaishapay.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.adapters.LoanPaymentHistoryListAdapter;
import com.cabral.emaishapay.adapters.LoanPaymentScheduleListAdapter;

public class WalletLoanPaymentSchedule extends Fragment {
    private Context context;
    private RecyclerView recyclerView;
    private LoanPaymentScheduleListAdapter loanPaymentScheduleListAdapter;
    private NavController navController;
    private Toolbar toolbar;
    AppBarConfiguration appBarConfiguration;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.payment_schedule, container, false);
        recyclerView = view.findViewById(R.id.loan_payment_schedule_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        loanPaymentScheduleListAdapter = new LoanPaymentScheduleListAdapter();
        recyclerView.setAdapter(loanPaymentScheduleListAdapter);

        toolbar = view.findViewById(R.id.toolbar_payment_schedule);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController  = Navigation.findNavController(view);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();

        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
    }
}
