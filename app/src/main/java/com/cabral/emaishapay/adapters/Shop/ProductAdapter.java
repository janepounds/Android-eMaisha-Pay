package com.cabral.emaishapay.adapters.Shop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
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

import com.cabral.emaishapay.DailogFragments.shop.ProductPreviewDialog;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.database.DbHandlerSingleton;
import com.cabral.emaishapay.databinding.ProductItemBinding;
import com.cabral.emaishapay.network.db.entities.EcManufacturer;
import com.cabral.emaishapay.network.db.entities.EcProduct;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {


    private List<? extends EcProduct> productData;
    private Context context;ProductItemBinding binding;
    //private List<EcManufacturer> manufacturers;


    public ProductAdapter(Context context) {
        this.context = context;
        setHasStableIds(true);
    }


    @NonNull
    @Override
    public ProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.product_item,  parent, false);

        return new MyViewHolder(binding);
    }

    public void setProductList(final List<? extends EcProduct> productList) {
        if ( this.productData== null) {
            this.productData =  productList;
            notifyItemRangeInserted(0, productList.size());
        }
        else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return productData.size();
                }

                @Override
                public int getNewListSize() {
                    return productList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return productData.get(oldItemPosition).getProduct_id() ==
                            productList.get(newItemPosition).getProduct_id();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    EcProduct newProduct = productList.get(newItemPosition);
                    EcProduct oldProduct = productData.get(oldItemPosition);
                    return newProduct.getProduct_weight() == oldProduct.getProduct_weight()
                            && TextUtils.equals(newProduct.getProduct_name(), oldProduct.getProduct_name())
                            && TextUtils.equals(newProduct.getProduct_weight_unit(), oldProduct.getProduct_weight_unit())
                            && newProduct.getProduct_sell_price() == oldProduct.getProduct_sell_price();
                }
            });
            productData = productList;
            result.dispatchUpdatesTo(this);
        }
        Log.d("AdapterSize","########"+this.getItemCount());
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.setProductData(productData.get(position));
        holder.binding.executePendingBindings();
    }

    public void onBindViewHolder0000(@NonNull final MyViewHolder holder, int position) {

        holder.binding.setProductData(productData.get(position));
        holder.binding.executePendingBindings();

        //dbHandler = DbHandlerSingleton.getHandlerInstance(context);
        final String product_id = productData.get(position).getProduct_id();
        String name = productData.get(position).getProduct_name();
        String category = productData.get(position).getProduct_category();
        String supplier_id = productData.get(position).getProduct_supplier();
        String buy_price = productData.get(position).getProduct_buy_price();
        String sell_price = productData.get(position).getProduct_sell_price();
        String base64Image = productData.get(position).getProduct_image();
        String productstock = productData.get(position).getProduct_stock();
        String currency =context.getString(R.string.currency);

        //String supplier_name = dbHandler.getSupplierName(supplier_id);

//        holder.binding.txtProductName.setText(name);
//        holder.binding.categoryName.setText(category);
//        holder.binding.txtProductStock.setText(productstock);
//       // holder.txtSupplierName.setText(supplier_name);
//        holder.binding.txtProductBuyPriceValue.setText(currency + " " + buy_price);
//        holder.binding.txtProductSellPriceValue.setText(currency + " " + sell_price);
//
//        if(productData.get(position).getProduct_weight()!=null && !productData.get(position).getProduct_weight().equalsIgnoreCase("null") ){
//            holder.binding.txtPerUnit.setText("/"+productData.get(position).getProduct_weight()+productData.get(position).getProduct_weight_unit());
//            holder.binding.txtProductName.setText(holder.binding.txtProductName.getText()+" "+productData.get(position).getProduct_weight()+productData.get(position).getProduct_weight_unit());
//        }
//
//
//        if (base64Image != null) {
//            if (base64Image.length() < 6) {
//                Log.d("64base", base64Image);
//                holder.binding.productImage.setImageResource(R.drawable.image_placeholder);
//            } else {
//
//
//                byte[] bytes = Base64.decode(base64Image, Base64.DEFAULT);
//                holder.binding.productImage.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
//
//            }
//        }
//
//        holder.binding.imgDelete.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                holder.binding.imgDeleteShadow.setVisibility(View.VISIBLE);
//                return false;
//            }
//        });
//
//        holder.binding.imgDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setMessage(R.string.want_to_delete_product)
//                        .setCancelable(false)
//                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//
//
////                                boolean deleteProduct = dbHandler.deleteProduct(product_id);
////
////                                if (deleteProduct) {
////                                    Toasty.error(context, R.string.product_deleted, Toast.LENGTH_SHORT).show();
////
////                                    productData.remove(holder.getAdapterPosition());
////
////                                    // Notify that item at position has been removed
////                                    notifyItemRemoved(holder.getAdapterPosition());
////
////                                } else {
////                                    Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show();
////                                }
////                                dialog.cancel();
//
//                            }
//                        })
//                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                // Perform Your Task Here--When No is pressed
//                                dialog.cancel();
//                            }
//                        }).show();
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
       return productData == null ? 0 : productData.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void onViewRecycled(@NonNull MyViewHolder holder) {
        Log.e("ErrorMJ",holder.binding.txtProductName.getText().toString());
        super.onViewRecycled(holder);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        final ProductItemBinding binding;

        public MyViewHolder(ProductItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }



}
