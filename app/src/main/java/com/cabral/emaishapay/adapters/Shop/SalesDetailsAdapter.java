package com.cabral.emaishapay.adapters.Shop;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.cabral.emaishapay.R;
import com.cabral.emaishapay.database.DbHandlerSingleton;


import java.util.HashMap;
import java.util.List;

public class SalesDetailsAdapter extends RecyclerView.Adapter<SalesDetailsAdapter.MyViewHolder> {


    Context context;
    private List<HashMap<String, String>> orderData;
    private DbHandlerSingleton dbHandler;

    public SalesDetailsAdapter(Context context, List<HashMap<String, String>> orderData) {
        this.context = context;
        this.orderData = orderData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_details_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        dbHandler = DbHandlerSingleton.getHandlerInstance(context);

        holder.txt_product_name.setText(orderData.get(position).get("product_name"));

        holder.txt_product_qty.setText(context.getString(R.string.quantity) + "  " + orderData.get(position).get("product_qty"));
        holder.txt_product_Weight.setText(context.getString(R.string.weight) + "  " + orderData.get(position).get("product_weight"));
        String base64Image = orderData.get(position).get("product_image");


        String unit_price = orderData.get(position).get("product_price");
        String qty = orderData.get(position).get("product_qty");
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