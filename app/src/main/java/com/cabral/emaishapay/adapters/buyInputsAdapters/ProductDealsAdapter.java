package com.cabral.emaishapay.adapters.buyInputsAdapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.AuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.models.product_model.ProductDetails;
import com.cabral.emaishapay.utils.Utilities;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.text.DecimalFormat;
import java.util.List;

public class ProductDealsAdapter extends RecyclerView.Adapter<ProductDealsAdapter.MyViewHolder>{
    private Context context;
    private String customerID;
    private List<ProductDetails> productList;
    public ProductDealsAdapter(List<ProductDetails> productList,Context context){
        this.productList = productList;
        this.context = context;
        customerID = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, context);

    }

    @Override
    public ProductDealsAdapter.MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.buy_input_product_home_card, parent, false);

        return new ProductDealsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProductDealsAdapter.MyViewHolder holder, final int position){
        if (position != productList.size()) {

            // Get the data model based on Position
            final ProductDetails product = productList.get(position);



            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.new_product)
                    .error(R.drawable.new_product)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH);
            // Set Product Image on ImageView with Glide Library
            Glide.with(context)
                    .setDefaultRequestOptions(options)
                    .load(ConstantValues.ECOMMERCE_WEB + product.getProductsImage())
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.shimmerProgress.setVisibility(View.GONE);
                            holder.shimmerProgress.stopShimmer();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.shimmerProgress.setVisibility(View.GONE);
                            holder.shimmerProgress.stopShimmer();
                            return false;
                        }
                    })
                    .into(holder.product_thumbnail);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.product_thumbnail.setClipToOutline(true);
//                holder.product_tag_new.setClipToOutline(true);
            }

            holder.product_title.setText(product.getProductsName().toUpperCase().substring(0, 1) + product.getProductsName().toLowerCase().substring(1));




            holder.product_price_old.setPaintFlags(holder.product_price_old.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            String[] categoryIDs = new String[product.getCategories().size()];
            String[] categoryNames = new String[product.getCategories().size()];
            if (product.getCategories().size() > 0) {

                for (int i = 0; i < product.getCategories().size(); i++) {
                    categoryIDs[i] = String.valueOf(product.getCategories().get(i).getCategoriesId());
                    categoryNames[i] = product.getCategories().get(i).getCategoriesName();
                }
                product.setCategoryIDs(TextUtils.join(",", categoryIDs));
                product.setCategoryNames(TextUtils.join(",", categoryNames));
            } else {
                product.setCategoryIDs("");
                product.setCategoryNames("");
            }

            //holder.product_category.setText(product.getCategoryNames());

            // Calculate the Discount on Product with static method of Helper class
            final String discount = Utilities.checkDiscount(product.getProductsPrice(), product.getDiscountPrice());

            if (discount != null) {
                // Set Product's Price
                holder.product_price_old.setVisibility(View.VISIBLE);
                holder.product_price_old.setText(ConstantValues.CURRENCY_SYMBOL + "" + new DecimalFormat("#0.00").format(Double.valueOf(product.getProductsPrice())));
                holder.product_price_new.setText(ConstantValues.CURRENCY_SYMBOL + "" + new DecimalFormat("#0.00").format(Double.valueOf(product.getDiscountPrice())));

//                holder.product_tag_new.setVisibility(View.GONE);

                // Set Discount Tag and its Text
                holder.layoutSale.setVisibility(View.VISIBLE);


            } else {

                // Check if the Product is Newly Added with the help of static method of Helper class
                //if (Utilities.checkNewProduct(product.getProductsDateAdded())) {
                // Set New Tag and its Text
                //    holder.product_tag_new.setVisibility(View.VISIBLE);
                // } else {
                //    holder.product_tag_new.setVisibility(View.GONE);
                // }

                // Hide Discount Text and Set Product's Price
                holder.layoutSale.setVisibility(View.GONE);
                holder.product_price_old.setVisibility(View.GONE);
/*
                NumberFormat nf = NumberFormat.getInstance(LocaleHelper.getSystemLocale( context.getResources().getConfiguration())); //or "nb","No" - for Norway
                String thisprice = nf.format(Double.parseDouble());
*/

                double thisprice = Double.parseDouble(product.getProductsPrice().replace(",", ""));
                Log.e("NumberFormat: ", thisprice + "");
                holder.product_price_new.setText(ConstantValues.CURRENCY_SYMBOL + "" + new DecimalFormat("#0.00").format(thisprice));
            }



            // Handle the Click event of product_like_layout ToggleButton
            holder.product_like_layout.setOnClickListener(view -> {

                // Check if the User is Authenticated
                if (ConstantValues.IS_USER_LOGGED_IN) {



                } else {
                    // Keep the Like Button Unchecked
//                        holder.product_like_layout.setChecked(false);

                    // Navigate to Login Activity
                    Intent i = new Intent(context, AuthActivity.class);
                    context.startActivity(i);
                    ((WalletHomeActivity) context).finish();
                    ((WalletHomeActivity) context).overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
                }
            });


            // Handle the Click event of product_thumbnail ImageView
//            holder.product_thumbnail.setOnClickListener(view -> {
//
//                // Get Product Info
//                Bundle itemInfo = new Bundle();
//                itemInfo.putParcelable("productDetails", product);
//
//                // Save the AddressDetails
//                ((EmaishaPayApp) context.getApplicationContext()).setProductDetails(product);
//                // Navigate to Product_Description of selected Product
//                Fragment fragment = new Product_Description(holder.product_checked, isFlash, start, server);
//                fragment.setArguments(itemInfo);
//                //MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
//                FragmentManager fragmentManager = ((WalletHomeActivity) context).getSupportFragmentManager();
////                if (((WalletHomeActivity) context).currentFragment != null)
////                    fragmentManager.beginTransaction()
////                            .hide(((WalletHomeActivity) context).currentFragment)
////                            .add(R.id.main_fragment_container, fragment)
////                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
////                            .addToBackStack(null).commit();
////                else
//                fragmentManager.beginTransaction()
//                        .add(R.id.main_fragment_container, fragment)
//                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                        .addToBackStack(null).commit();
//
//
//                // Add the Product to User's Recently Viewed Products
//                if (!recents_db.getUserRecents().contains(product.getProductsId())) {
//                    recents_db.insertRecentItem(product.getProductsId());
//                }
//            });


            // Handle the Click event of product_checked ImageView
//            holder.product_title.setOnClickListener(view -> {
//
//                // Get Product Info
//                Bundle itemInfo = new Bundle();
//                itemInfo.putParcelable("productDetails", product);
//
//                // Navigate to Product_Description of selected Product
//                Fragment fragment = new Product_Description(holder.product_checked, isFlash, start, server);
//                fragment.setArguments(itemInfo);
//                //MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
//                FragmentManager fragmentManager = ((WalletHomeActivity) context).getSupportFragmentManager();
//                Fragment currentFragment = fragmentManager.getPrimaryNavigationFragment();
//
//                if (currentFragment != null)
//                    fragmentManager.beginTransaction()
//                            .hide(currentFragment)
//                            .add(R.id.main_fragment_container, fragment)
//                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                            .addToBackStack(null).commit();
//                else
//                    fragmentManager.beginTransaction()
//                            .add(R.id.main_fragment_container, fragment)
//                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                            .addToBackStack(null).commit();
//
//
//            });

            // Check the Button's Visibility
            if (!ConstantValues.IS_PRODUCT_CHECKED) {

//                holder.product_add_cart_btn.setVisibility(View.VISIBLE);
//                holder.product_add_cart_btn.setOnClickListener(null);




//                holder.product_checked.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (product.getProductsType() != 0) {
//
//                            // Get Product Info
//                            Bundle itemInfo = new Bundle();
//                            itemInfo.putParcelable("productDetails", product);
//
//                            // Navigate to Product_Description of selected Product
//                            Fragment fragment = new Product_Description(holder.product_checked);
//                            fragment.setArguments(itemInfo);
//                            //MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
//                            FragmentManager fragmentManager = ((DashboardActivity) context).getSupportFragmentManager();
//                            fragmentManager.beginTransaction()
//                                    .hide(((DashboardActivity)context).currentFragment)
//                                    .add(R.id.main_fragment_container, fragment)
//                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                                    .addToBackStack(null).commit();
//
//                            // Add the Product to User's Recently Viewed Products
//                            if (!recents_db.getUserRecents().contains(product.getProductsId())) {
//                                recents_db.insertRecentItem(product.getProductsId());
//                            }
//                        }
//                        else {
//
//                            if (isFlash) {
//                                if (start > server) {
//                                    Snackbar.make(v, context.getString(R.string.cannot_add_upcoming), Snackbar.LENGTH_SHORT).show();
//                                }
//                                else {
//                                    Utilities.animateCartMenuIcon(context, (DashboardActivity) context);
//                                    // Add Product to User's Cart
//                                    addProductToCart(product);
//
//                                    holder.product_checked.setVisibility(View.VISIBLE);
//                                    //disable add to cart button
//                                    holder.product_add_cart_btn.setVisibility(View.GONE);
//
//                                    Snackbar.make(v, context.getString(R.string.item_added_to_cart), Snackbar.LENGTH_SHORT).show();
//
//                                }
//                            }
//                            else {
//
//                                if(product.getProductsDefaultStock()<1){
//
//                                    Snackbar.make(v, context.getString(R.string.outOfStock), Snackbar.LENGTH_SHORT).show();
//                                }
//                                else {
//                                    Utilities.animateCartMenuIcon(context, (DashboardActivity) context);
//                                    // Add Product to User's Cart
//                                    addProductToCart(product);
//
//                                    holder.product_checked.setVisibility(View.VISIBLE);
//                                    holder.product_add_cart_btn.setVisibility(View.GONE);
//
//                                    Snackbar.make(v, context.getString(R.string.item_added_to_cart), Snackbar.LENGTH_SHORT).show();
//                                }
//
//                            }
//                        }
//                    }
//                });

            } else {
                // Make the Button Invisible


            }

        }
    }

    @Override
    public int getItemCount() {
        return this.productList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {


        RelativeLayout product_like_layout;
        ImageView product_thumbnail, product_tag_new;
        TextView product_title, product_price_old, product_price_new,produce_overall_rating;
        LinearLayout layoutSale;
        ShimmerFrameLayout shimmerProgress;
        public MyViewHolder(final View itemView) {
            super(itemView);



            product_like_layout = itemView.findViewById(R.id.product_like_layout);
            product_title = itemView.findViewById(R.id.product_title);

            product_price_old = itemView.findViewById(R.id.product_price_old);
            product_price_new = itemView.findViewById(R.id.product_price_new);
            product_thumbnail = itemView.findViewById(R.id.product_cover);
            shimmerProgress = itemView.findViewById(R.id.shimmerFrame);
            produce_overall_rating = itemView.findViewById(R.id.produce_overall_rating);






        }
    }
}
