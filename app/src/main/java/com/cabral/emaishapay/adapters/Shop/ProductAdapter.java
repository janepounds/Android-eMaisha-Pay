package com.cabral.emaishapay.adapters.Shop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.database.DbHandlerSingleton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {


    private List<HashMap<String, String>> productData;
    private Context context;
    private DbHandlerSingleton dbHandler;


    public ProductAdapter(Context context, List<HashMap<String, String>> productData) {
        this.context = context;
        this.productData = productData;

    }


    @NonNull
    @Override
    public ProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ProductAdapter.MyViewHolder holder, int position) {


        dbHandler = DbHandlerSingleton.getHandlerInstance(context);
        final String product_id = productData.get(position).get("product_id");
        String name = productData.get(position).get("product_name");
        String supplier_id = productData.get(position).get("product_supplier");
        String buy_price = productData.get(position).get("product_buy_price");
        String sell_price = productData.get(position).get("product_sell_price");
        String base64Image = productData.get(position).get("product_image");
        String productstock = productData.get(position).get("product_stock");


        String currency = dbHandler.getCurrency();

        String supplier_name = dbHandler.getSupplierName(supplier_id);

        holder.txtProductName.setText(name);
        holder.txt_product_stock.setText(productstock);
        holder.txtSupplierName.setText(supplier_name);
        holder.txtBuyPrice.setText(currency + " " + buy_price);
        holder.txtSellPrice.setText(currency + " " + sell_price);


        if (base64Image != null) {
            if (base64Image.length() < 6) {
                Log.d("64base", base64Image);
                holder.product_image.setImageResource(R.drawable.image_placeholder);
            } else {


                byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
                holder.product_image.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

            }
        }

        holder.imgDelete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                holder.img_delete_shadow.setVisibility(View.VISIBLE);
                return false;
            }
        });

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.want_to_delete_product)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                boolean deleteProduct = dbHandler.deleteProduct(product_id);

                                if (deleteProduct) {
                                    Toasty.error(context, R.string.product_deleted, Toast.LENGTH_SHORT).show();

                                    productData.remove(holder.getAdapterPosition());

                                    // Notify that item at position has been removed
                                    notifyItemRemoved(holder.getAdapterPosition());

                                } else {
                                    Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show();
                                }
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
        return productData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtProductName, txtSupplierName, txtBuyPrice, txtSellPrice, txt_product_stock;
        ImageView imgDelete, product_image;
        LinearLayout img_delete_shadow;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtProductName = itemView.findViewById(R.id.txt_product_name);
            txtSupplierName = itemView.findViewById(R.id.txt_product_supplier_value);
            txtBuyPrice = itemView.findViewById(R.id.txt_product_buy_price_value);
            txtSellPrice = itemView.findViewById(R.id.txt_product_sell_price_value);
            txt_product_stock = itemView.findViewById(R.id.txt_product_stock_value);
            img_delete_shadow = itemView.findViewById(R.id.img_delete_shadow);

            imgDelete = itemView.findViewById(R.id.img_delete);
            product_image = itemView.findViewById(R.id.product_image);

            itemView.setOnClickListener(this);


        }


        @Override
        public void onClick(View view) {
            //got to edit product
//            Intent i = new Intent(context, EditProductActivity.class);
//            i.putExtra("product_id", productData.get(getAdapterPosition()).get("product_id"));
//            context.startActivity(i);

        }
    }


}
