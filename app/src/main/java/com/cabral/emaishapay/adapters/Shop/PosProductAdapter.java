package com.cabral.emaishapay.adapters.Shop;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.database.DbHandlerSingleton;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class PosProductAdapter extends RecyclerView.Adapter<PosProductAdapter.MyViewHolder> {


    MediaPlayer player;
    private List<HashMap<String, String>> productData;
    private Context context;
    private DbHandlerSingleton dbHandler;

    public PosProductAdapter(Context context, List<HashMap<String, String>> productData) {
        this.context = context;
        this.productData = productData;
      //  player = MediaPlayer.create(context, R.raw.delete_sound);

    }


    @NonNull
    @Override
    public PosProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final PosProductAdapter.MyViewHolder holder, int position) {

        dbHandler = DbHandlerSingleton.getHandlerInstance(context);
        final String product_id = productData.get(position).get("product_id");
        String name = productData.get(position).get("product_name");
        String category = productData.get(position).get("product_category");
        String supplier_id = productData.get(position).get("product_supplier");
        String buy_price = productData.get(position).get("product_buy_price");
        String sell_price = productData.get(position).get("product_sell_price");
        String base64Image = productData.get(position).get("product_image");
        String productstock = productData.get(position).get("product_stock");
        String currency =context.getString(R.string.currency);
        final String product_weight = productData.get(position).get("product_weight");
        final String product_price = productData.get(position).get("product_sell_price");
        final String weight_unit_name = productData.get(position).get("product_weight_unit");


        String supplier_name = dbHandler.getSupplierName(supplier_id);

        holder.txtProductName.setText(name);
        holder.categoryName.setText(category);
        holder.txt_product_stock.setText(productstock);
        holder.txtSupplierName.setText(supplier_name);
        holder.txtBuyPrice.setText(currency + " " + buy_price);
        holder.txtSellPrice.setText(currency + " " + sell_price);
        holder.txt_per_unit.setText(productData.get(position).get("product_weight_unit"));

        if (base64Image != null) {
            if (base64Image.length() < 6) {
                Log.d("64base", base64Image);
                holder.product_image.setImageResource(R.drawable.image_placeholder);
            } else {


                byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
                holder.product_image.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

            }
        }



        holder.product_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                Log.d("w_id", weight_unit_i+" ");
              //  databaseAccess.open();

                int check = dbHandler.addToCart(product_id, product_weight, weight_unit_name, product_price, 1);

                if (check == 1) {
                    Toasty.success(context, "Product Added to cart", Toast.LENGTH_SHORT).show();
                    player.start();
                } else if (check == 2) {

                    Toasty.info(context, "Product already added to cart", Toast.LENGTH_SHORT).show();

                } else {
                    Toasty.error(context, "Product added to cart failed try again", Toast.LENGTH_SHORT).show();

                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return productData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtProductName, txtSupplierName, txtBuyPrice, txtSellPrice, txt_product_stock, categoryName,txt_per_unit;
       // Button btnAddToCart;
        ImageView product_image, imgDelete;
        CardView cardProductItem;
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
            categoryName = itemView.findViewById(R.id.category_name);
            txt_per_unit= itemView.findViewById(R.id.txt_per_unit);
            imgDelete = itemView.findViewById(R.id.img_delete);
            imgDelete.setVisibility(View.GONE);
           // btnAddToCart = itemView.findViewById(R.id.btn_add_cart);
            cardProductItem = itemView.findViewById(R.id.card_product_item);

            cardProductItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    ViewGroup viewGroup = v.findViewById(android.R.id.content);
                    View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_pos_product_details, viewGroup, false);
                    builder.setView(dialogView);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    TextView cancel = dialogView.findViewById(R.id.btn_cancel);
                    TextView update = dialogView.findViewById(R.id.btn_update);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            alertDialog.dismiss();
                        }
                    });
                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                        }
                    });
                }
            });

        }
    }


}
