package com.cabral.emaishapay.adapters.buyInputsAdapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.fragments.buy_fragments.My_Orders;
import com.cabral.emaishapay.fragments.buy_fragments.Order_Details;
import com.cabral.emaishapay.models.order_model.OrderDetails;

import java.util.List;


/**
 * OrdersListAdapter is the adapter class of RecyclerView holding List of Orders in My_Orders
 **/

public class OrdersListAdapter extends RecyclerView.Adapter<OrdersListAdapter.MyViewHolder> {

    Context context;
    String customerID;
    List<OrderDetails> ordersList;
    My_Orders fragment;


    //declare interface
    private OnItemClicked onClick;

    //make interface like this
    public interface OnItemClicked {
        void onItemClick(int position);
    }


    public OrdersListAdapter(Context context, String customerID, List<OrderDetails> ordersList, My_Orders fragment) {
        this.context = context;
        this.customerID = customerID;
        this.ordersList = ordersList;
        this.fragment = fragment;
    }

    //********** Called to Inflate a Layout from XML and then return the Holder *********//

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        // Inflate the custom layout
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.buy_inputs_card_orders, parent, false);

        return new MyViewHolder(itemView);
    }

    //********** Called by RecyclerView to display the Data at the specified Position *********//

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        // Get the data model based on Position
        final OrderDetails orderDetails = ordersList.get(position);

        int noOfProducts = 0;
        for (int i = 0; i < orderDetails.getProducts().size(); i++) {
            // Count no of Products
            noOfProducts += orderDetails.getProducts().get(i).getProductsQuantity();
        }

        switch (orderDetails.getOrdersStatus()) {
            case "Cancel":
                holder.orderStatusBackground.setBackgroundResource(R.drawable.order_status_cancelled_background);
                break;
            case "Completed":
                holder.orderStatusBackground.setBackgroundResource(R.drawable.order_status_completed_background);
                break;
            case "Pending":
                holder.orderStatusBackground.setBackgroundResource(R.drawable.order_status_pending_background);
                break;
        }

        holder.order_id.setText(String.valueOf(orderDetails.getOrdersId()));
        holder.order_status.setText(orderDetails.getOrdersStatus());
        holder.order_price.setText(ConstantValues.CURRENCY_SYMBOL + orderDetails.getOrderPrice());
        holder.order_date.setText(orderDetails.getDatePurchased());
        holder.order_product_count.setText(String.valueOf(noOfProducts));


        // Check Order's status
/*
        if (orderDetails.getOrdersStatus().equalsIgnoreCase("Pending")) {
            holder.order_status.setTextColor(ContextCompat.getColor(context, R.color.colorAccentBlue));
            holder.cancle_order_btn.setVisibility(View.VISIBLE);
        } else if (orderDetails.getOrdersStatus().equalsIgnoreCase("Completed")) {
            holder.order_status.setTextColor(ContextCompat.getColor(context, R.color.colorAccentGreen));
            holder.cancle_order_btn.setVisibility(View.GONE);
        }else if (orderDetails.getOrdersStatus().equalsIgnoreCase("Cancelled")) {
            holder.order_status.setTextColor(ContextCompat.getColor(context, R.color.colorAccentRed));
            holder.cancle_order_btn.setVisibility(View.GONE);
        } else {
            holder.order_status.setTextColor(ContextCompat.getColor(context, R.color.colorAccentRed));
            holder.cancle_order_btn.setVisibility(View.VISIBLE);
        }
*/

        
/*
        holder.cancle_order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.order_dialog_prompt))
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                
                                dialogInterface.dismiss();
                                
                            }})
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                fragment.RequestMyOrdersCancel(Integer.parseInt(orderDetails.getOrdersId()+""));
                                // notifyItemChanged(fragment);
                                dialogInterface.dismiss();
                            }
    
                           
                        })
                        .show();
            }
        });
*/

        holder.itemView.setOnClickListener(v -> {
            // Get Order Info
            Bundle itemInfo = new Bundle();
            itemInfo.putParcelable("orderDetails", orderDetails);

            // Navigate to Order_Details Fragment
            Fragment fragment = new Order_Details();
            fragment.setArguments(itemInfo);
            //MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
            FragmentManager fragmentManager = ((WalletHomeActivity) context).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.main_fragment_container, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(null).commit();
        });
    }

    //********** Returns the total number of items in the data set *********//

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    /********** Custom ViewHolder provides a direct reference to each of the Views within a Data_Item *********/

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView order_id, order_product_count, order_status, order_price, order_date;
        private RelativeLayout orderStatusBackground;

        public MyViewHolder(final View itemView) {
            super(itemView);

            order_id = itemView.findViewById(R.id.order_id);
            order_product_count = itemView.findViewById(R.id.order_products_count);
            order_status = itemView.findViewById(R.id.order_status);
            order_price = itemView.findViewById(R.id.order_price);
            order_date = itemView.findViewById(R.id.order_date);
            orderStatusBackground = itemView.findViewById(R.id.order_status_background);
        }
    }

    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }

    private void notifyItemChanged(My_Orders fragment) {

        int currPosition = ordersList.indexOf(fragment);
        notifyItemChanged(currPosition);
    }
}

