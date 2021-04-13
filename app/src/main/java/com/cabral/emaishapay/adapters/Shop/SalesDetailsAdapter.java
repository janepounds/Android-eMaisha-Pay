package com.cabral.emaishapay.adapters.Shop;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.cabral.emaishapay.R;
import com.cabral.emaishapay.database.DbHandlerSingleton;
import com.cabral.emaishapay.network.db.entities.ShopOrderProducts;


import java.util.HashMap;
import java.util.List;

public class SalesDetailsAdapter extends RecyclerView.Adapter<SalesDetailsAdapter.MyViewHolder> {


    Context context;
    private List<? extends ShopOrderProducts> orderData;
    private DbHandlerSingleton dbHandler;

    public SalesDetailsAdapter(Context context) {
        this.context = context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_details_item, parent, false);
        return new MyViewHolder(view);
    }


    public void setProductList(final List<? extends ShopOrderProducts> orderDetails) {
        if ( this.orderData== null) {
            this.orderData =  orderDetails;
            notifyItemRangeInserted(0, orderData.size());
        }
        else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return orderData.size();
                }

                @Override
                public int getNewListSize() {
                    return orderData.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return orderData.get(oldItemPosition).getOrder_details_id() ==
                            orderData.get(newItemPosition).getOrder_details_id();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    ShopOrderProducts newProduct = orderDetails.get(newItemPosition);
                    ShopOrderProducts oldProduct = orderData.get(oldItemPosition);
                    return newProduct.getProduct_weight() == oldProduct.getProduct_weight()
                            && TextUtils.equals(newProduct.getProduct_name(), oldProduct.getProduct_name())
                            && TextUtils.equals(newProduct.getProduct_qty(), oldProduct.getProduct_qty());

                }
            });
            orderData = orderDetails;
            result.dispatchUpdatesTo(this);
        }
        Log.d("AdapterSize","########"+this.getItemCount());
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        dbHandler = DbHandlerSingleton.getHandlerInstance(context);

        holder.txt_product_name.setText(orderData.get(position).getProduct_name());

        holder.txt_product_qty.setText(context.getString(R.string.quantity) + "  " + orderData.get(position).getProduct_qty());
        holder.txt_product_Weight.setText(context.getString(R.string.weight) + "  " + orderData.get(position).getProduct_weight());
        String base64Image = orderData.get(position).getProduct_image();


        String unit_price = orderData.get(position).getProduct_price();
        String qty = orderData.get(position).getProduct_qty();
        double price = Double.parseDouble(unit_price);
        int quantity = Integer.parseInt(qty);
        double cost = quantity * price;


        String currency = dbHandler.getCurrency();

        holder.txt_total_cost.setText(currency + " " + unit_price + " x " + qty + " = " + currency + " " + cost);

        if (base64Image != null) {
            if (base64Image.isEmpty() || base64Image.length() < 6) {
                holder.imgProduct.setImageResource(R.drawable.image_placeholder);
            } else {


                byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
                holder.imgProduct.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

            }
        }

    }

    @Override
    public int getItemCount() {
        return orderData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_product_name, txt_product_price, txt_product_qty, txt_product_Weight, txt_total_cost;
        ImageView imgProduct;


        public MyViewHolder(View itemView) {
            super(itemView);

            txt_product_name = itemView.findViewById(R.id.txt_product_name);
            txt_product_price = itemView.findViewById(R.id.txt_price);
            txt_product_qty = itemView.findViewById(R.id.txt_qty);
            txt_product_Weight = itemView.findViewById(R.id.txt_weight);
            imgProduct = itemView.findViewById(R.id.img_product);
            txt_total_cost = itemView.findViewById(R.id.txt_total_cost);


        }


    }


}