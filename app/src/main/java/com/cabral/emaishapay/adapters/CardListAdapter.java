package com.cabral.emaishapay.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.models.CardResponse;
import com.cabral.emaishapay.models.LoanApplication;
import com.cabral.emaishapay.singletons.WalletSettingsSingleton;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.MyViewHolder>{
    private ArrayList<CardResponse>cardResponseArrayList;
    private Context context;

    public CardListAdapter(ArrayList<CardResponse>cardResponseArrayList, Context context){
        this.cardResponseArrayList = cardResponseArrayList;
        this.context = context;

    }


    @Override
    public CardListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loan_card, parent, false);
        CardListAdapter.MyViewHolder holder = new CardListAdapter.MyViewHolder(view);
        return holder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CardListAdapter.MyViewHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return cardResponseArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {



        public MyViewHolder(View v) {
            super(v);

        }

        @Override
        public void onClick(View v) {

        }
    }
}
