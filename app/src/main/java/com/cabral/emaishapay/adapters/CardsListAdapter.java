package com.cabral.emaishapay.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cabral.emaishapay.DailogFragments.AddCardFragment;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.CircularImageView;
import com.cabral.emaishapay.models.CardResponse;

import java.util.List;

public class CardsListAdapter extends RecyclerView.Adapter<CardsListAdapter.MyViewHolder> {
    private static final String TAG = "CardsListAdapter";
    private final List<CardResponse.Cards> dataList;
    private final FragmentManager fm;
    Context context;
    private String account_name, card_number,cvv,expiry_date,id;


    public  class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircularImageView cardImage;
        TextView accountNme,accountNumberFirst,accountNumberLast,expiry;
        CardView payment_card;

        public MyViewHolder(View v, FragmentManager fm) {
            super(v);
            accountNme = v.findViewById(R.id.text_card_account_name);
            accountNumberFirst = v.findViewById(R.id.text_card_account_number_first);
            accountNumberLast = v.findViewById(R.id.text_card_account_number_end);
            expiry = v.findViewById(R.id.text_card_expiry);
            cardImage = v.findViewById(R.id.item_card_image);
            payment_card = v.findViewById(R.id.payment_card);
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
        account_name = data.getAccount_name();
        cvv =  data.getCvv();
        expiry_date = data.getExpiry();
        holder.accountNme.setText(account_name);
        holder.expiry.setText("Exp. Date: "+expiry_date);
        card_number =  data.getCard_number();
        id= data.getId();
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_visa)
                .error(R.drawable.ic_error_card)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

        //check if card number is 5 or 4 or 62
        String first_value = String.valueOf(card_number.charAt(0));
        String second_value = String.valueOf(card_number.charAt(1));
        if(Integer.parseInt(first_value)==4){

          //  Glide.with(context).load(R.drawable.ic_visa).apply(options).into(holder.cardImage);
            holder.cardImage.setImageResource(R.drawable.ic_visa);

        }else if (Integer.parseInt(first_value)==5){
            //Glide.with(context).load(R.drawable.ic_mastercard).apply(options).into(holder.cardImage);
            holder.cardImage.setImageResource(R.drawable.ic_mastercard);
        }else if (Integer.parseInt(first_value)==6 && Integer.parseInt(second_value)==2 ){
            //Glide.with(context).load(R.drawable.ic_unionpay).apply(options).into(holder.cardImage);
            holder.cardImage.setImageResource(R.drawable.ic_union_pay);
        }
        else {
            holder.cardImage.setImageResource(R.drawable.ic_default_card);
        }


        //set card number
        if(card_number.length()>4) {

            holder.accountNumberFirst.setText(card_number.substring(0,  4));
            holder.accountNumberLast.setText(card_number.substring(card_number.length() - 4));
            Log.d(TAG, "onBindViewHolder: "+card_number.substring(0, 4));
        }

        holder.payment_card.setOnClickListener(v -> {

            editPaymentCard(data);
        });

    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }


    public void editPaymentCard(CardResponse.Cards data){
        //call card dialog
        //nvigate to add card fragment
        Bundle bundle = new Bundle();
        bundle.putString("account_name", data.getAccount_name());
        bundle.putString("account_number", data.getCard_number());
        bundle.putString("cvv", data.getCvv());
        bundle.putString("expiry", data.getExpiry());

        bundle.putString("id",data.getId());
        FragmentManager fm = ((WalletHomeActivity) context).getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev =fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        DialogFragment addCardDialog =new AddCardFragment();
        addCardDialog.setArguments(bundle);
        addCardDialog.show( ft, "dialog");

    }


}
