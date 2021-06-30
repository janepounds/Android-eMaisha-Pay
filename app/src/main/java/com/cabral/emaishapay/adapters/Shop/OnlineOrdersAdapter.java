package com.cabral.emaishapay.adapters.Shop;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.ShopActivity;
import com.cabral.emaishapay.network.db.entities.ShopOrder;

import java.util.List;

public class OnlineOrdersAdapter extends RecyclerView.Adapter<OnlineOrdersAdapter.MyViewHolder> {
    Context context;
    private List<ShopOrder> orderData;

    public OnlineOrdersAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public com.cabral.emaishapay.adapters.Shop.OnlineOrdersAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.online_order_item, parent, false);
        return new OnlineOrdersAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull com.cabral.emaishapay.adapters.Shop.OnlineOrdersAdapter.MyViewHolder holder, int position) {
        String customer_name = orderData.get(position).getCustomer_name();
        String orderId = orderData.get(position).getOrder_id();
        String order_status = orderData.get(position).getOrder_status();
        String customer_address = orderData.get(position).getCustomer_address();

        holder.txt_customer_name.setText(customer_name);
        holder.txt_order_status.setText(order_status);
        holder.txt_customer_address.setText(customer_address);
    }

    @Override
    public int getItemCount() {
        return orderData==null? 0 : orderData.size();
    }

    public void setOrderList(List<ShopOrder> shopOrderLists) {
        if ( this.orderData== null) {
            this.orderData =  shopOrderLists;
            notifyItemRangeInserted(0, shopOrderLists.size());
        }
//        else {
//            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
//                @Override
//                public int getOldListSize() {
//                    return orderData.size();
//                }
//
//                @Override
//                public int getNewListSize() {
//                    return shopOrderLists.size();
//                }
//
//                @Override
//                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
//                    return orderData.get(oldItemPosition).getOrder_id() ==
//                            shopOrderLists.get(newItemPosition).getOrder_id();
//                }
//
//                @Override
//                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
//                    ShopOrder newProduct = shopOrderLists.get(newItemPosition);
//                    ShopOrder oldProduct = orderData.get(oldItemPosition);
//                    return newProduct.getOrder_id() == oldProduct.getOrder_id()
//                            && TextUtils.equals(newProduct.getCustomer_address(), oldProduct.getCustomer_address())
//                            && TextUtils.equals(newProduct.getCustomer_name(), oldProduct.getCustomer_name())
//                            && TextUtils.equals(newProduct.getOrder_status(), oldProduct.getOrder_status())
//                            && newProduct.getDelivery_fee() == oldProduct.getDelivery_fee();
//                }
//            });
//            orderData = shopOrderLists;
//            result.dispatchUpdatesTo(this);
//        }
        Log.d("AdapterSize","########"+this.getItemCount());
    }



    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txt_customer_name, txt_customer_address, txt_order_status;
        ImageView imgDelete;

        public MyViewHolder(View itemView) {
            super(itemView);

            txt_customer_name = itemView.findViewById(R.id.txt_order_customer_name);
            txt_customer_address = itemView.findViewById(R.id.txt_order_customer_address);
            txt_order_status = itemView.findViewById(R.id.txt_order_status);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            Bundle bundle = new Bundle();
            bundle.putSerializable("order_details", orderData.get(getBindingAdapterPosition()));
            ShopActivity.navController.navigate(R.id.action_shopOrdersFragment_to_onlineOrderDetailsFragment,bundle);

        }
            

    }
}
