package com.cabral.emaishapay.adapters.Shop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.database.DbHandlerSingleton;


import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.MyViewHolder> {


    Context context;
    private List<HashMap<String, String>> orderData;
    private DbHandlerSingleton dbHandler;


    public SalesAdapter(Context context, List<HashMap<String, String>> orderData) {
        this.context = context;
        this.orderData = orderData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        dbHandler = DbHandlerSingleton.getHandlerInstance(context);
        int invoice_id = Integer.parseInt(orderData.get(position).get("invoice_id"));
        String order_date = orderData.get(position).get("product_order_date");
        String product_qty = orderData.get(position).get("product_qty");
        String payment_method = orderData.get(position).get("order_payment_method");
        String product_price = orderData.get(position).get("product_price");
        double total=Double.parseDouble(product_price)*Double.parseDouble(product_qty);
        String customer_name = orderData.get(position).get("customer_name");
        String currency=context.getString(R.string.currency);


        holder.txt_customer_name.setText(customer_name);
        holder.txt_order_id.setText( String.format("%08d", invoice_id) );
        holder.txt_payment_method.setText(payment_method);

        holder.txt_product_price.setText(currency+" "+total);
        holder.txt_date.setText(order_date);

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
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


    }

    @Override
    public int getItemCount() {
        return orderData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txt_customer_name, txt_order_id, txt_product_price, txt_payment_method, txt_date;
        ImageView imgDelete, imgProduct;

        public MyViewHolder(View itemView) {
            super(itemView);
            //cart_product_image
            txt_customer_name = itemView.findViewById(R.id.txt_customer_name);
            txt_order_id = itemView.findViewById(R.id.txt_order_id_value);
            txt_product_price = itemView.findViewById(R.id.product_price);
            txt_payment_method = itemView.findViewById(R.id.txt_order_payment_method);
            txt_date = itemView.findViewById(R.id.txt_date);
            imgDelete = itemView.findViewById(R.id.img_delete);
            imgProduct = itemView.findViewById(R.id.cart_product_image);

            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
//            Intent i = new Intent(context, OrderDetailsActivity.class);
//            Bundle bundle = new Bundle();
//            i.putExtra("order_id", orderData.get(getAdapterPosition()).get("invoice_id"));
//            i.putExtra("customer_name", orderData.get(getAdapterPosition()).get("customer_name"));
//            i.putExtra("order_date", orderData.get(getAdapterPosition()).get("order_date"));
//            i.putExtra("order_time", orderData.get(getAdapterPosition()).get("order_time"));
//            i.putExtra("order_status", orderData.get(getAdapterPosition()).get("order_status"));
//            i.putExtra("storage_status", orderData.get(getAdapterPosition()).get("storage_status"));
//            context.startActivity(i);
        }
    }


}