package com.cabral.emaishapay.adapters.buyInputsAdapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import com.cabral.emaishapay.activities.WalletBuySellActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.app.EmaishaPayApp;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.database.User_Recents_BuyInputsDB;
import com.cabral.emaishapay.fragments.buy_fragments.Product_Description;
import com.cabral.emaishapay.models.product_model.ProductDetails;
import com.cabral.emaishapay.utils.Utilities;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

/**
 * ProductAdapter is the adapter class of RecyclerView holding List of Products in All_Products and other Product relevant Classes
 **/

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
    private static final String TAG = "ProductAdapter";
    private Activity context;
    private String customerID;
    private Boolean isGridView;
    private Boolean isHorizontal;
    private Boolean isFlash;
    public Fragment currentFragment;
    private FragmentManager fragmentManager;

    private User_Recents_BuyInputsDB recents_db;
    private List<ProductDetails> productList;

    long start;
    long end;
    long server;
    CountDownTimer mCountDownTimer;

    public ProductAdapter(Activity context, FragmentManager fragmentManager, List<ProductDetails> productList, Boolean isHorizontal, Boolean isFlash) {
        this.context = context;
        this.productList = productList;
        this.isHorizontal = isHorizontal;
        this.isFlash = isFlash;
        recents_db = new User_Recents_BuyInputsDB();
        this.fragmentManager = fragmentManager;
        customerID = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, context);

    }

    public ProductAdapter(Activity context, List<ProductDetails> productList, Boolean isHorizontal) {
        this.context = context;
        this.productList = productList;
        this.isHorizontal = isHorizontal;
        recents_db = new User_Recents_BuyInputsDB();
        customerID = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, context);
    }

    //********** Called to Inflate a Layout from XML and then return the Holder *********//

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.buy_input_product_home_card, parent, false);

//        currentFragment = fragmentManager.getPrimaryNavigationFragment();
        // Return a new holder instance
        return new MyViewHolder(itemView);
    }

    //********** Called by RecyclerView to display the Data at the specified Position *********//

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        if (position != productList.size()) {
            Log.e("ProductSIZE---ptr",""+productList.size());
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
            final String discount = Utilities.checkDiscount(product.getProductsMeasure().get(0).getProducts_price(), product.getDiscountPrice());

            NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());

            if (discount != null && product.getDiscountPrice()!=null && product.getProductsPrice()!=null) {
                // Set Product's Price
                holder.product_price_old.setVisibility(View.VISIBLE);
                try {
                    holder.product_price_old.setText(ConstantValues.CURRENCY_SYMBOL + " " +  nf.format( nf.parse(product.getProductsPrice()) ));
                    holder.product_price_new.setText(ConstantValues.CURRENCY_SYMBOL + " " +  nf.format( nf.parse(product.getDiscountPrice()) ));
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            } else if( product.getProductsMeasure()!=null && product.getProductsMeasure().size()!=0) {

                // Hide Discount Text and Set Product's Price
                holder.product_price_old.setVisibility(View.GONE);


                double thisprice = Double.parseDouble(product.getProductsMeasure().get(0).getProducts_price().replace(",", ""));
                Log.e("NumberFormat: ", thisprice + "");
                holder.product_price_new.setText(ConstantValues.CURRENCY_SYMBOL + " " + nf.format(thisprice));
            }else{
                holder.product_price_old.setVisibility(View.VISIBLE);
                holder.product_price_new.setVisibility(View.VISIBLE);
            }

//
//            holder.product_like_layout.setOnCheckedChangeListener(null);
//
//            // Check if Product is Liked
//            if (product.getIsLiked().equalsIgnoreCase("1")) {
//                holder.product_like_layout.setChecked(true);
//            } else {
//                holder.product_like_layout.setChecked(false);
//            }

            // Handle the Click event of product_like_layout ToggleButton
            holder.product_like_layout.setOnClickListener(view -> {

                // Check if the User is Authenticated
                if (ConstantValues.IS_USER_LOGGED_IN) {


//                        if(holder.product_like_layout.isChecked()) {
//                            product.setIsLiked("1");
//                            holder.product_like_layout.setChecked(true);
//
//                            // Like the Product for the User with the static method of Product_Description
//                            Product_Description.LikeProduct(product.getProductsId(), customerID, context, view);
//                        }
//                        else {
//                            product.setIsLiked("0");
//                            holder.product_like_layout.setChecked(false);
//
//                            // Unlike the Product for the User with the static method of Product_Description
//                            Product_Description.UnlikeProduct(product.getProductsId(), customerID, context, view);
//                        }

                } else {
                    // Keep the Like Button Unchecked
//                        holder.product_like_layout.setChecked(false);

                    // Navigate to Login Activity
                    Intent i = new Intent(context, AuthActivity.class);
                    context.startActivity(i);
                    ((WalletBuySellActivity) context).finish();
                    ((WalletBuySellActivity) context).overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
                }
            });


            // Handle the Click event of product_thumbnail ImageView
            holder.product_thumbnail.setOnClickListener(view -> {

                // Get Product Info
                Bundle itemInfo = new Bundle();
                itemInfo.putParcelable("productDetails", product);

                // Save the AddressDetails
                ((EmaishaPayApp) context.getApplicationContext()).setProductDetails(product);
                // Navigate to Product_Description of selected Product
                Fragment fragment = new Product_Description(holder.product_checked, isFlash, start, server);
                fragment.setArguments(itemInfo);
                //MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
//                if (((WalletBuySellActivity) context).currentFragment != null)
//                    fragmentManager.beginTransaction()
//                            .hide(((WalletBuySellActivity) context).currentFragment)
//                            .add(R.id.nav_host_fragment, fragment)
//                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                            .addToBackStack(null).commit();
//                else
                    fragmentManager.beginTransaction()
                            .replace(R.id.nav_host_fragment2, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .addToBackStack(null).commit();


                // Add the Product to User's Recently Viewed Products
                if (!recents_db.getUserRecents().contains(product.getProductsId())) {
                    recents_db.insertRecentItem(product.getProductsId());
                }
            });


            // Check the Button's Visibility
            if (!ConstantValues.IS_PRODUCT_CHECKED) {

//                if (product.getProductsType() != 0) {
//                    holder.product_add_cart_btn.setText(context.getString(R.string.view_product));
//                    holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_button_green));
//                } else {
//                    if (product.getProductsDefaultStock() < 1) {
//                        holder.product_add_cart_btn.setText(context.getString(R.string.outOfStock));
//                        holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_button_red));
//                    } else {
//                        holder.product_add_cart_btn.setText(context.getString(R.string.addToCart));
//                        holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_button_green));
//                    }
//                }

                if (isFlash) {
                    start = Long.parseLong(product.getFlashStartDate()) * 1000L;
                    end = Long.parseLong(product.getFlashExpireDate()) * 1000L;

                    server = Long.parseLong(product.getServerTime()) * 1000L;

                    //  remainingTime =  end-server ;

                    if (server > start) {
                        holder.product_price_new.setText(ConstantValues.CURRENCY_SYMBOL + " " + nf.format(product.getFlashPrice()));
                        holder.product_price_old.setText(ConstantValues.CURRENCY_SYMBOL + " " + nf.format(product.getProductsPrice()));

                        mCountDownTimer = new CountDownTimer(end, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {


                                server = server - 1;
                                Long serverUptimeSeconds =
                                        (millisUntilFinished - server) / 1000;

                                String daysLeft = String.format("%d", serverUptimeSeconds / 86400);
                                String hoursLeft = String.format("%d", (serverUptimeSeconds % 86400) / 3600);
                                String minutesLeft = String.format("%d", ((serverUptimeSeconds % 86400) % 3600) / 60);
                                String secondsLeft = String.format("%d", ((serverUptimeSeconds % 86400) % 3600) % 60);
                            }

                            @Override
                            public void onFinish() {
                            }
                        }.start();
                    } else {
                        holder.product_price_new.setText(ConstantValues.CURRENCY_SYMBOL + " " + nf.format(product.getFlashPrice()));
                        if (product.getProductsMeasure().size() > 0)
                            holder.product_price_old.setText(ConstantValues.CURRENCY_SYMBOL + " " + nf.format(product.getProductsMeasure().get(0).getProducts_price()));

                    }

                    holder.product_price_old.setVisibility(View.VISIBLE);
                }

                Log.d(TAG, "onBindViewHolder: Product Type = " + product.getProductsType());

                holder.product_container.setOnClickListener(view -> {
                        // Get Product Info
                        Bundle itemInfo = new Bundle();
                        itemInfo.putParcelable("productDetails", product);

                        // Navigate to Product_Description of selected Product
                        Fragment fragment = new Product_Description(holder.product_checked, isFlash, start, server);
                        fragment.setArguments(itemInfo);
                        //MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
                        FragmentManager fragmentManager = ((WalletBuySellActivity) context).getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.nav_host_fragment2, fragment)
//                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .addToBackStack(null).commit();

                        // Add the Product to User's Recently Viewed Products
                        if (!recents_db.getUserRecents().contains(product.getProductsId())) {
                            recents_db.insertRecentItem(product.getProductsId());
                        }


                });

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

            }

            //check for product type in order to show or hide percentage sale off
//            if(getAllProducts.getType().equalsIgnoreCase("special")){
//                holder.percentageOff.setVisibility(View.VISIBLE);
//            }else {
//                holder.percentageOff.setVisibility(View.GONE);
//            }

          //  Log.d(TAG, "onBindViewHolder: PRODUCT TYPE IN ADAPTER: " +getAllProducts.getType());

        }

    }


    //********** Returns the total number of items in the data set *********//

    @Override
    public int getItemCount() {
        return productList.size();
    }


    //********** Toggles the RecyclerView LayoutManager *********//

    public void toggleLayout(Boolean isGridView) {
        this.isGridView = isGridView;
    }


    /********** Custom ViewHolder provides a direct reference to each of the Views within a Data_Item *********/

    public static class MyViewHolder extends RecyclerView.ViewHolder {


        ImageView product_checked;
        RelativeLayout product_like_layout;
        LinearLayout product_container;
        ImageView product_thumbnail;
        TextView product_title, product_price_old, product_price_new,percentageOff;
        Spinner product_weight_spn;
        ShimmerFrameLayout shimmerProgress;

        public MyViewHolder(final View itemView) {
            super(itemView);

            product_weight_spn = itemView.findViewById(R.id.spinner_produce_quantity);
            product_checked = itemView.findViewById(R.id.product_checked);
//            product_card_img_btn = itemView.findViewById(R.id.product_card_img_btn);
            product_like_layout = itemView.findViewById(R.id.product_like_layout);
            product_container = itemView.findViewById(R.id.product_container);
            product_title = itemView.findViewById(R.id.product_title);
//            product_category = itemView.findViewById(R.id.product_category);
            product_price_old = itemView.findViewById(R.id.product_price_old);
            product_price_new = itemView.findViewById(R.id.product_price_new);
            product_thumbnail = itemView.findViewById(R.id.product_cover);
            // product_tag_new = itemView.findViewById(R.id.product_item_tag_new);
            percentageOff = itemView.findViewById(R.id.percentage_sale_off);

            shimmerProgress = itemView.findViewById(R.id.shimmerFrame);
        }

    }


}

