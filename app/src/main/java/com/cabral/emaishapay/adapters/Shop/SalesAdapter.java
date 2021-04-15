package com.cabral.emaishapay.adapters.Shop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.ShopActivity;
import com.cabral.emaishapay.databinding.OrderSalesItemBinding;
import com.cabral.emaishapay.network.db.entities.ShopOrder;
import com.cabral.emaishapay.network.db.entities.ShopOrderProducts;
import com.cabral.emaishapay.network.db.relations.ShopOrderWithProducts;


import java.util.List;

public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.MyViewHolder> {


    Context context;
    private List<ShopOrderWithProducts> orderData;
    OrderSalesItemBinding binding;


    public SalesAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.order_sales_item,  parent, false);

        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        String invoice_id = orderData.get(position).shopOrder.getOrder_id();
        String order_date = orderData.get(position).shopOrder.getOrder_date();
        String payment_method = orderData.get(position).shopOrder.getOrder_payment_method();

        int totalProductSize=0;
        double totalProductPrice=0.0;
        for (ShopOrderProducts product:orderData.get(position).getOrderProducts()) {
            totalProductSize+=Integer.parseInt(product.getProduct_qty());
            double subTotalPrice=Double.parseDouble(product.getProduct_price())*Double.parseDouble(product.getProduct_qty());
            totalProductPrice+=subTotalPrice;
        }

        String customer_name = orderData.get(position).shopOrder.getCustomer_name();
        String currency=context.getString(R.string.currency);


        holder.binding.txtCustomerName.setText(customer_name);
        holder.binding.txtOrderIdValue.setText( invoice_id);
        holder.binding.txtOrderPaymentMethod.setText(payment_method);

        holder.binding.productPrice.setText(currency+" "+totalProductPrice);
        holder.binding.txtDate.setText(order_date);

        holder.binding.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Want to Delete Order ?")
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {



                               // boolean delete_order = dbHandler.deleteOrder(invoice_id);

//                                if (delete_order) {
//                                    Toasty.error(context, "Order Deleted", Toast.LENGTH_SHORT).show();
//
//                                    orderData.remove(holder.getAdapterPosition());
//
//                                    // Notify that item at position has been removed
//                                    notifyItemRemoved(holder.getAdapterPosition());
//
//                                } else {
//                                    Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show();
//                                }
                                dialog.cancel();

                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Perform Your Task Here--When No is pressed
                                dialog.cancel();
                            }
                        }).show();

            }
        });

        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return orderData==null? 0 : orderData.size();
    }

    public void setSalesList(List<ShopOrderWithProducts> orderSales) {
        if ( this.orderData== null) {
            this.orderData =  orderSales;
            notifyItemRangeInserted(0, orderSales.size());
        }
        else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return orderData.size();
                }

                @Override
                public int getNewListSize() {
                    return orderSales.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return orderData.get(oldItemPosition).shopOrder.getOrder_id() ==
                            orderSales.get(newItemPosition).shopOrder.getOrder_id();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    ShopOrder newOrder = orderSales.get(newItemPosition).shopOrder;
                    ShopOrder oldOrder = orderData.get(oldItemPosition).shopOrder;
                    return newOrder.getOrder_id() == oldOrder.getOrder_id()
                            && TextUtils.equals(newOrder.getCustomer_name(), oldOrder.getCustomer_name())
                            && TextUtils.equals(newOrder.getOrder_type(), oldOrder.getOrder_type())
                            && TextUtils.equals(newOrder.getOrder_payment_method(), oldOrder.getOrder_payment_method())
                            && TextUtils.equals(newOrder.getOrder_time(), oldOrder.getOrder_time())
                            && TextUtils.equals(newOrder.getOrder_status(), oldOrder.getOrder_status())
                            && newOrder.getProducts().size()==oldOrder.getProducts().size();
                }
            });
            orderData = orderSales;
            result.dispatchUpdatesTo(this);
        }
        Log.d("SalesAdapterSize","########"+this.getItemCount());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        final OrderSalesItemBinding binding;

        public MyViewHolder(OrderSalesItemBinding binding) {
            super(binding.getRoot());


            binding.getRoot().setOnClickListener(this);
            this.binding = binding;

        }

        @Override
        public void onClick(View view) {
            Bundle args = new Bundle();
            args.putSerializable("salesDetails",orderData.get(getAdapterPosition()));
            ShopActivity.navController.navigate(R.id.action_shopSalesFragment_to_salesDetailsFragment,args);
        }
    }


}