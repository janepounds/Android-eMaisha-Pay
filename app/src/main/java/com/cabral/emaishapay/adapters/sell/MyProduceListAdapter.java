package com.cabral.emaishapay.adapters.sell;

import android.app.Dialog;
import android.content.Context;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cabral.emaishapay.DailogFragments.AgentCustomerDeposits;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.fragments.sell_fragment.MyProduceFragment;
import com.cabral.emaishapay.models.marketplace.MyProduce;


import java.util.ArrayList;

public class MyProduceListAdapter extends RecyclerView.Adapter<MyProduceListAdapter.MyProduceListViewHolder> {
    Context context;
    private ArrayList<MyProduce> myProduceArrayList;
    FragmentManager fm;

    public static class MyProduceListViewHolder extends RecyclerView.ViewHolder {
        public TextView name, variety, quantity, price, date;
        public ImageView image;
        public CardView produceCard;


        public MyProduceListViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_produce_name);
            variety = itemView.findViewById(R.id.item_produce_variety);
            quantity = itemView.findViewById(R.id.item_produce_quantity);
            price = itemView.findViewById(R.id.item_produce_price);
            date = itemView.findViewById(R.id.item_produce_date);
            image = itemView.findViewById(R.id.item_produce_image);
            produceCard = itemView.findViewById(R.id.produce_card);



        }
    }

    public MyProduceListAdapter(Context context, ArrayList<MyProduce> myProduceArrayList) {
        this.context = context;
        this.myProduceArrayList = myProduceArrayList;
    }

    @NonNull
    @Override
    public MyProduceListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_produce_list_item, parent, false);

        return new MyProduceListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyProduceListViewHolder holder, int position) {
        MyProduce myProduce = myProduceArrayList.get(position);

        holder.name.setText(myProduce.getName());
        holder.variety.setText(myProduce.getVariety());
        holder.quantity.setText(myProduce.getQuantity());
        holder.price.setText(myProduce.getPrice());
        holder.date.setText(myProduce.getDate());

        Glide.with(context).load(Base64.decode(myProduce.getImage(), Base64.DEFAULT)).into(holder.image);

        holder.produceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMyProduce();
            }
        });


    }

    @Override
    public int getItemCount() {
        return myProduceArrayList.size();
    }

    private void editMyProduce() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
        //LayoutInflater inflater = requireActivity().getLayoutInflater();
        View addProduceDialog = View.inflate(context,R.layout.add_produce_dialog,null);


        ImageView close = addProduceDialog.findViewById(R.id.produce_close);
        Spinner name = addProduceDialog.findViewById(R.id.produce_name);
        EditText variety = addProduceDialog.findViewById(R.id.produce_variety);
        Spinner quantityUnit = addProduceDialog.findViewById(R.id.produce_quantity_unit);
        EditText quantity = addProduceDialog.findViewById(R.id.produce_quantity);
        TextView quantityMeasure = addProduceDialog.findViewById(R.id.produce_quantity_measure);
        EditText price = addProduceDialog.findViewById(R.id.produce_price);
        CardView cardView = addProduceDialog.findViewById(R.id.image_view_holder);
        ImageView image = addProduceDialog.findViewById(R.id.produce_image);
        Button submit = addProduceDialog.findViewById(R.id.produce_submit_button);
        LinearLayout layoutSubmitBtn = addProduceDialog.findViewById(R.id.layout_submit_button);
        LinearLayout layoutEditBtns = addProduceDialog.findViewById(R.id.edit_buttons);
        TextView produceTitle = addProduceDialog.findViewById(R.id.produce_title);


        produceTitle.setText("Edit Produce");
        layoutSubmitBtn.setVisibility(View.GONE);
        layoutEditBtns.setVisibility(View.VISIBLE);


        builder.setView(addProduceDialog);
       Dialog dialog = builder.create();
        builder.setCancelable(false);

        close.setOnClickListener(view1 -> dialog.dismiss());
       // dialog = builder.create();
        dialog.show();

    }

}
