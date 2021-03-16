package com.cabral.emaishapay.adapters.Shop;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.cabral.emaishapay.DailogFragments.shop.AddProductFragment;
import com.cabral.emaishapay.DailogFragments.shop.ProductPreviewDialog;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.database.DbHandlerSingleton;
import com.cabral.emaishapay.models.WalletTransactionResponse;
import com.cabral.emaishapay.models.shop_model.Manufacturer;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.cabral.emaishapay.app.EmaishaPayApp.getContext;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {


    private List<HashMap<String, String>> productData;
    private Context context;
    private DbHandlerSingleton dbHandler;
    private List<Manufacturer> manufacturers;
    FragmentManager fm;


    public ProductAdapter(Context context, List<HashMap<String, String>> productData, FragmentManager supportFragmentManager, List<Manufacturer> manufacturers) {
        this.context = context;
        this.productData = productData;
        this.fm=supportFragmentManager;
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
        String category = productData.get(position).get("product_category");
        String supplier_id = productData.get(position).get("product_supplier");
        String buy_price = productData.get(position).get("product_buy_price");
        String sell_price = productData.get(position).get("product_sell_price");
        String base64Image = productData.get(position).get("product_image");
        String productstock = productData.get(position).get("product_stock");
        String currency =context.getString(R.string.currency);

        String supplier_name = dbHandler.getSupplierName(supplier_id);

        holder.txtProductName.setText(name);
        holder.categoryName.setText(category);
        holder.txt_product_stock.setText(productstock);
        holder.txtSupplierName.setText(supplier_name);
        holder.txtBuyPrice.setText(currency + " " + buy_price);
        holder.txtSellPrice.setText(currency + " " + sell_price);
        if(productData.get(position).get("product_weight")!=null && !productData.get(position).get("product_weight").equalsIgnoreCase("null") ){
            holder.txt_per_unit.setText("/"+productData.get(position).get("product_weight")+productData.get(position).get("product_weight_unit"));
            holder.txtProductName.setText(holder.txtProductName.getText()+" "+productData.get(position).get("product_weight")+productData.get(position).get("product_weight_unit"));
        }


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

        TextView txtProductName, txtSupplierName, txtBuyPrice, txtSellPrice, txt_product_stock, categoryName,txt_per_unit;
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
            categoryName = itemView.findViewById(R.id.category_name);
            txt_per_unit= itemView.findViewById(R.id.txt_per_unit);
            itemView.setOnClickListener(this);


        }


        @Override
        public void onClick(View view) {

            HashMap<String, String> productDetails = productData.get(getAdapterPosition());
            //Log.e("Reference Number", transaction.getReferenceNumber()+" is "+transaction.isPurchase());
            if (fm!=null ){
                ProductPreviewDialog productPreviewDialog = new ProductPreviewDialog(productDetails);

                productPreviewDialog.show(fm, "productPreviewDialog");

            }

//            FragmentTransaction ft = fm.beginTransaction();
//            Fragment prev = fm.findFragmentByTag("dialog");
//            if (prev != null) {
//                ft.remove(prev);
//            }
//            ft.addToBackStack(null);
//
//            // Create and show the dialog.
//            DialogFragment addProductDialog = new AddProductFragment(manufacturers,productData);
//            addProductDialog.show(ft, "dialog");


        }
    }


}
