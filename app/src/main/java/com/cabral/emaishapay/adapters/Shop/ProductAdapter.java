package com.cabral.emaishapay.adapters.Shop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cabral.emaishapay.DailogFragments.shop.ShopProductPreviewDialog;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.databinding.ProductItemBinding;
import com.cabral.emaishapay.modelviews.ShopProductsModelView;
import com.cabral.emaishapay.network.db.entities.EcProduct;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
    private static final String TAG = "ProductAdapter";

    private List<? extends EcProduct> productData;
    private Context context;
    private FragmentManager fm;
    ProductItemBinding binding;
    ShopProductsModelView viewModel;
    //private List<EcManufacturer> manufacturers;


    public ProductAdapter(Context context, ShopProductsModelView viewModel,FragmentManager fm ) {
        this.context = context;
        this.viewModel=viewModel;
        this.fm=fm;
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

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.new_product)
                .error(R.drawable.new_product)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);


        Glide.with(context).load(Base64.decode( productData.get(position).getProduct_image()!=null?productData.get(position).getProduct_image():"", Base64.DEFAULT)).apply(options).into(holder.binding.productImage);

        holder.binding.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.want_to_delete_product)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                try  {
                                    viewModel.deleteProduct( productData.get(position) );
                                    Toasty.error(context, R.string.product_deleted, Toast.LENGTH_SHORT).show();

                                    productData.remove(holder.getAdapterPosition());

                                    // Notify that item at position has been removed
                                    notifyItemRemoved(holder.getAdapterPosition());

                                } catch (Exception e){
                                    e.printStackTrace();
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



        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = fm.beginTransaction();
                Fragment prev = fm.findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                DialogFragment productPreviewDialog = new ShopProductPreviewDialog(productData.get(position),viewModel);
                productPreviewDialog.show(ft, "dialog");
            }
        });

        holder.binding.executePendingBindings();
        Log.d(TAG, "onBindViewHolder: product_image"+productData.get(position).getProduct_image());

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
