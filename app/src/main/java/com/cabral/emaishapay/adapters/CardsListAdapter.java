package com.cabral.emaishapay.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.DailogFragments.WalletTransactionsReceiptDialog;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.CircularImageView;
import com.cabral.emaishapay.models.CardResponse;
import com.cabral.emaishapay.models.LoanApplication;
import com.cabral.emaishapay.models.WalletTransactionResponse;
import com.cabral.emaishapay.singletons.WalletSettingsSingleton;
import com.cabral.emaishapay.utils.CryptoUtil;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

public class CardsListAdapter extends RecyclerView.Adapter<CardsListAdapter.MyViewHolder> {
    private static final String TAG = "CardsListAdapter";
    private List<CardResponse.Cards> dataList;
    private FragmentManager fm;
    Context context;


    public  class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircularImageView cardImage;
        TextView accountNme,accountNumberFirst,accountNumberLast,expiry;

        public MyViewHolder(View v, FragmentManager fm) {
            super(v);
            accountNme = v.findViewById(R.id.text_card_account_name);
            accountNumberFirst = v.findViewById(R.id.text_card_account_number_first);
            accountNumberLast = v.findViewById(R.id.text_card_account_number_end);
            expiry = v.findViewById(R.id.text_card_expiry);
            cardImage = v.findViewById(R.id.item_card_image);
        }


        @Override
        public void onClick(View v) {

        }
    }

    public CardsListAdapter(List<CardResponse.Cards> dataList,FragmentManager supportFragmentManager) {
        this.dataList = dataList;
        fm=supportFragmentManager;

    }

    @Override
    public CardsListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_card,parent,false);
        context=parent.getContext();
        CardsListAdapter.MyViewHolder holder = new CardsListAdapter.MyViewHolder(view,fm);
        return holder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CardsListAdapter.MyViewHolder holder, int position) {
        CardResponse.Cards data = dataList.get(position);

        //decript values
        CryptoUtil encrypter =new CryptoUtil(BuildConfig.ENCRYPTION_KEY,context.getString(R.string.iv));
        holder.accountNme.setText(encrypter.decrypt(data.getAccount_name()));
        holder.expiry.setText("Expiry Date: "+encrypter.decrypt(data.getExpiry()));
        String card_number = encrypter.decrypt(data.getCard_number());
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_visa)
                .error(R.drawable.ic_visa)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

        //check if card number is 5 or 4
        String first_value = String.valueOf(card_number.charAt(0));
        if(Integer.parseInt(first_value)==5){

            Glide.with(context).load(R.drawable.ic_mastercard).apply(options).into(holder.cardImage);

        }else{
            Glide.with(context).load(R.drawable.ic_visa).apply(options).into(holder.cardImage);

        }

        //set card number
        if(card_number.length()>4) {

            holder.accountNumberFirst.setText(card_number.substring(0,  4));
            holder.accountNumberLast.setText(card_number.substring(card_number.length() - 4));
            Log.d(TAG, "onBindViewHolder: "+card_number.substring(0, 4));
        }


    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }


}
