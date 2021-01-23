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

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.adapters.buyInputsAdapters.AddressListAdapter;
import com.cabral.emaishapay.adapters.buyInputsAdapters.ProductDealsAdapter;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.customs.CircularImageView;
import com.cabral.emaishapay.models.CardResponse;
import com.cabral.emaishapay.models.LoanApplication;
import com.cabral.emaishapay.models.WalletTransactionResponse;
import com.cabral.emaishapay.singletons.WalletSettingsSingleton;
import com.cabral.emaishapay.utils.CryptoUtil;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.MyViewHolder>{
    private static final String TAG = "CardListAdapter";
    private List<CardResponse.Cards> dataList;
    private Context context;

    public CardListAdapter(List<CardResponse.Cards> cardsList, Context context){
        this.dataList = cardsList;
        this.context = context;

    }


    @Override
    public CardListAdapter.MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_card, parent, false);

        return new CardListAdapter.MyViewHolder(itemView);


    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CardListAdapter.MyViewHolder holder, int position) {
        CardResponse.Cards data = dataList.get(position);

        //decript values
        CryptoUtil encrypter =new CryptoUtil(BuildConfig.ENCRYPTION_KEY,context.getString(R.string.iv));
        holder.accountNme.setText(encrypter.decrypt(data.getAccount_name()));
        holder.expiry.setText(encrypter.decrypt(data.getExpiry()));
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


        Log.d(TAG, "onBindViewHolder: "+dataList);

    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircularImageView cardImage;
        TextView accountNme,accountNumberFirst,accountNumberLast,expiry;


        public MyViewHolder(View v) {
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
}
