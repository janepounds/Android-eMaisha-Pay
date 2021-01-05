package com.cabral.emaishapay.adapters.buyInputsAdapters;

import android.app.Activity;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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
import com.cabral.emaishapay.activities.Login;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.adapters.CropSpinnerAdapter;
import com.cabral.emaishapay.app.EmaishaPayApp;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.database.User_Recents_BuyInputsDB;
import com.cabral.emaishapay.fragments.buyandsell.My_Cart;
import com.cabral.emaishapay.fragments.buyandsell.Product_Description;
import com.cabral.emaishapay.models.CropSpinnerItem;
import com.cabral.emaishapay.models.cart_model.CartProduct;
import com.cabral.emaishapay.models.cart_model.CartProductAttributes;
import com.cabral.emaishapay.models.product_model.Option;
import com.cabral.emaishapay.models.product_model.ProductDetails;
import com.cabral.emaishapay.models.product_model.ProductMeasure;
import com.cabral.emaishapay.models.product_model.Value;
import com.cabral.emaishapay.utils.Utilities;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
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
    private List<ProductMeasure> productweights;

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
        View itemView
                = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_major, parent, false);

        currentFragment = fragmentManager.getPrimaryNavigationFragment();
        // Return a new holder instance
        return new MyViewHolder(itemView);
    }

    //********** Called by RecyclerView to display the Data at the specified Position *********//

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        if (position != productList.size()) {

            // Get the data model based on Position
            final ProductDetails product = productList.get(position);

            if (product.getProductsMeasure().size() > 0) {
                // Check if the Product is already in the Cart with its measure
                String weight = product.getProductsMeasure().get(0).getProducts_weight();
                if (!holder.product_weight_spn.getSelectedItem().toString().equalsIgnoreCase(" ")) {
                    String[] splited = holder.product_weight_spn.getSelectedItem().toString().split("\\s+");
                    weight = splited[0];
                }

                if (My_Cart.checkCartHasProductAndMeasure(product.getProductsId())) {
                    holder.product_checked.setVisibility(View.VISIBLE);
                    holder.product_add_cart_btn.setVisibility(View.GONE);
                } else {
                    holder.product_checked.setVisibility(View.GONE);
                    holder.product_add_cart_btn.setVisibility(View.VISIBLE);
                }
            }

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH);
            // Set Product Image on ImageView with Glide Library
            Glide.with(context)
                    .setDefaultRequestOptions(options)
                    .load(ConstantValues.ECOMMERCE_URL + product.getProductsImage())
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

            ArrayList<CropSpinnerItem> weightItems = new ArrayList<>();
            int k = 0;
            for (ProductMeasure x : product.getProductsMeasure()) {

                if (k == 0)
                    weightItems.add(0, new CropSpinnerItem() {
                        @Override
                        public String getId() {
                            return x + "";
                        }

                        @Override
                        public String getUnits() {
                            return x.getProducts_weight_unit();
                        }

                        @NonNull
                        @Override
                        public String toString() {
                            return " ";
                        }
                    });

                weightItems.add(new CropSpinnerItem() {
                    @Override
                    public String getId() {
                        return x + "";
                    }

                    @Override
                    public String toString() {
                        return x.getProducts_weight() + " " + x.getProducts_weight_unit();
                    }

                    @Override
                    public String getUnits() {
                        return x.getProducts_weight_unit();
                    }
                });
                k++;
            }

            CropSpinnerAdapter weightSpinnerAdapter = new CropSpinnerAdapter(weightItems, null, context);
            holder.product_weight_spn.setAdapter(weightSpinnerAdapter);

            ((ArrayAdapter) holder.product_weight_spn.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_item);
            //set on item selected on weight spinner
            holder.product_weight_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    String selection = parent.getItemAtPosition(position).toString();

                    int k = 0;
                    for (ProductMeasure x : product.getProductsMeasure()) {
                        if (selection.equalsIgnoreCase(x.getProducts_weight() + " " + x.getProducts_weight_unit())) {

                            product.setSelectedProductsWeight(x.getProducts_weight());
                            product.setSelectedProductsWeightUnit(x.getProducts_weight_unit());
                            product.setProductsPrice(x.getProducts_price());
                            holder.product_price_new.setText(ConstantValues.CURRENCY_SYMBOL + " " + new DecimalFormat("#0.00").format(Double.valueOf(x.getProducts_price())));

                            //set selected weight
                            holder.selected_weight.setText(selection);

                        } else if (selection.equalsIgnoreCase(" ")) {

                            product.setSelectedProductsWeight(product.getProductsMeasure().get(0).getProducts_weight());
                            product.setSelectedProductsWeightUnit(product.getProductsMeasure().get(0).getProducts_weight_unit());
                            product.setProductsPrice(product.getProductsMeasure().get(0).getProducts_price());
                            holder.product_price_new.setText(ConstantValues.CURRENCY_SYMBOL + " " + new DecimalFormat("#0.00").format(Double.valueOf(product.getProductsMeasure().get(0).getProducts_price())));

                            //set selected weight
                            holder.selected_weight.setText(product.getProductsMeasure().get(0).getProducts_weight() + " " + product.getProductsMeasure().get(0).getProducts_weight_unit());

                        } else if (holder.product_checked.getVisibility() == View.VISIBLE && (!holder.product_weight_spn.getSelectedItem().equals(holder.selected_weight))) {
                            holder.product_add_cart_btn.setVisibility(View.VISIBLE);
                            Log.d(TAG, "onBindViewHolder: spinner changed");
                        }
                        k++;
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            //

            if (product.getProductsModel() != null)
                holder.product_ingredient.setText(product.getProductsModel());
            else
                holder.product_ingredient.setVisibility(View.GONE);

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
                holder.product_tag_discount_text.setText(discount);

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
                    Intent i = new Intent(context, Login.class);
                    context.startActivity(i);
                    ((WalletHomeActivity) context).finish();
                    ((WalletHomeActivity) context).overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
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
                FragmentManager fragmentManager = ((WalletHomeActivity) context).getSupportFragmentManager();
//                if (((WalletHomeActivity) context).currentFragment != null)
//                    fragmentManager.beginTransaction()
//                            .hide(((WalletHomeActivity) context).currentFragment)
//                            .add(R.id.main_fragment_container, fragment)
//                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                            .addToBackStack(null).commit();
//                else
                    fragmentManager.beginTransaction()
                            .add(R.id.main_fragment_container, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .addToBackStack(null).commit();


                // Add the Product to User's Recently Viewed Products
                if (!recents_db.getUserRecents().contains(product.getProductsId())) {
                    recents_db.insertRecentItem(product.getProductsId());
                }
            });


            // Handle the Click event of product_checked ImageView
            holder.product_title.setOnClickListener(view -> {

                // Get Product Info
                Bundle itemInfo = new Bundle();
                itemInfo.putParcelable("productDetails", product);

                // Navigate to Product_Description of selected Product
                Fragment fragment = new Product_Description(holder.product_checked, isFlash, start, server);
                fragment.setArguments(itemInfo);
                //MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
                FragmentManager fragmentManager = ((WalletHomeActivity) context).getSupportFragmentManager();
                Fragment currentFragment = fragmentManager.getPrimaryNavigationFragment();

                if (currentFragment != null)
                    fragmentManager.beginTransaction()
                            .hide(currentFragment)
                            .add(R.id.main_fragment_container, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .addToBackStack(null).commit();
                else
                    fragmentManager.beginTransaction()
                            .add(R.id.main_fragment_container, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .addToBackStack(null).commit();

                // Add the Product to User's Recently Viewed Products
                if (!recents_db.getUserRecents().contains(product.getProductsId())) {
                    recents_db.insertRecentItem(product.getProductsId());
                }
            });

            // Check the Button's Visibility
            if (!ConstantValues.IS_PRODUCT_CHECKED) {

//                holder.product_add_cart_btn.setVisibility(View.VISIBLE);
//                holder.product_add_cart_btn.setOnClickListener(null);

                if (product.getProductsType() != 0) {
                    holder.product_add_cart_btn.setText(context.getString(R.string.view_product));
                    holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_button_green));
                } else {
                    if (product.getProductsDefaultStock() < 1) {
                        holder.product_add_cart_btn.setText(context.getString(R.string.outOfStock));
                        holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_button_red));
                    } else {
                        holder.product_add_cart_btn.setText(context.getString(R.string.addToCart));
                        holder.product_add_cart_btn.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_corners_button_green));
                    }
                }

                if (isFlash) {
                    start = Long.parseLong(product.getFlashStartDate()) * 1000L;
                    end = Long.parseLong(product.getFlashExpireDate()) * 1000L;

                    server = Long.parseLong(product.getServerTime()) * 1000L;

                    //  remainingTime =  end-server ;

                    if (server > start) {
                        holder.product_price_new.setText(ConstantValues.CURRENCY_SYMBOL + "" + new DecimalFormat("#0.00").format(Double.valueOf(product.getFlashPrice())));
                        holder.product_price_old.setText(ConstantValues.CURRENCY_SYMBOL + "" + new DecimalFormat("#0.00").format(Double.valueOf(product.getProductsPrice())));

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
                                holder.product_add_cart_btn.setText(daysLeft + "D:" + hoursLeft + "H:" + minutesLeft + "M:" + secondsLeft + "S");
                            }

                            @Override
                            public void onFinish() {
                                holder.product_add_cart_btn.setText(context.getResources().getString(R.string.upcoming));
                                holder.product_add_cart_btn.setBackgroundResource(R.drawable.rounded_corners_button_red);
                            }
                        }.start();
                    } else {
                        holder.product_price_new.setText(ConstantValues.CURRENCY_SYMBOL + " " + new DecimalFormat("#0.00").format(Double.valueOf(product.getFlashPrice())));
                        if (product.getProductsMeasure().size() > 0)
                            holder.product_price_old.setText(ConstantValues.CURRENCY_SYMBOL + " " + new DecimalFormat("#0.00").format(Double.valueOf(product.getProductsMeasure().get(0).getProducts_price())));

                        holder.product_add_cart_btn.setText(context.getResources().getString(R.string.upcoming));
                        holder.product_add_cart_btn.setBackgroundResource(R.drawable.rounded_corners_button_red);
                    }

                    holder.product_price_old.setVisibility(View.VISIBLE);
                }

                Log.d(TAG, "onBindViewHolder: Product Type = " + product.getProductsType());

                holder.product_add_cart_btn.setOnClickListener(view -> {

                    if (product.getProductsType() != 0) {

                        // Get Product Info
                        Bundle itemInfo = new Bundle();
                        itemInfo.putParcelable("productDetails", product);

                        // Navigate to Product_Description of selected Product
                        Fragment fragment = new Product_Description(holder.product_checked, isFlash, start, server);
                        fragment.setArguments(itemInfo);
                        //MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
                        FragmentManager fragmentManager = ((WalletHomeActivity) context).getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .add(R.id.main_fragment_container, fragment)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .addToBackStack(null).commit();

                        // Add the Product to User's Recently Viewed Products
                        if (!recents_db.getUserRecents().contains(product.getProductsId())) {
                            recents_db.insertRecentItem(product.getProductsId());
                        }
                    } else {

                        if (isFlash) {
                            if (start > server) {
                                Snackbar.make(view, context.getString(R.string.cannot_add_upcoming), Snackbar.LENGTH_SHORT).show();
                            } else {
                                Utilities.animateCartMenuIcon(context, (WalletHomeActivity) context);
                                // Add Product to User's Cart
                                addProductToCart(product);

                                holder.product_checked.setVisibility(View.VISIBLE);
                                //disable add to cart button
                                holder.product_add_cart_btn.setVisibility(View.GONE);

                                Snackbar.make(view, context.getString(R.string.item_added_to_cart), Snackbar.LENGTH_SHORT).show();

                            }
                        } else {

                            if (product.getProductsDefaultStock() < 1) {

                                Snackbar.make(view, context.getString(R.string.outOfStock), Snackbar.LENGTH_SHORT).show();
                            } else {
                                Utilities.animateCartMenuIcon(context.getApplicationContext(), (WalletHomeActivity) context);
                                // Add Product to User's Cart
                                addProductToCart(product);

                                holder.product_checked.setVisibility(View.VISIBLE);
                                //set add to cart button disabled
                                holder.product_add_cart_btn.setVisibility(View.GONE);
                                Snackbar.make(view, context.getString(R.string.item_added_to_cart), Snackbar.LENGTH_SHORT).show();
                            }

                        }
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

            } else {
                // Make the Button Invisible
                holder.product_add_cart_btn.setVisibility(View.GONE);

            }

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
        Button product_add_cart_btn;
        ImageButton product_card_img_btn;
        RelativeLayout product_like_layout;
        ImageView product_thumbnail, product_tag_new;
        TextView product_title, product_ingredient, product_price_old, product_price_new, product_tag_discount_text, selected_weight;
        LinearLayout layoutSale;
        Spinner product_weight_spn;
        ShimmerFrameLayout shimmerProgress;

        public MyViewHolder(final View itemView) {
            super(itemView);

            product_weight_spn = itemView.findViewById(R.id.spinner_produce_quantity);
            product_checked = itemView.findViewById(R.id.product_checked);
            product_ingredient = itemView.findViewById(R.id.active_ingredient);

            product_add_cart_btn = itemView.findViewById(R.id.product_card_Btn);
//            product_card_img_btn = itemView.findViewById(R.id.product_card_img_btn);
            product_like_layout = itemView.findViewById(R.id.product_like_layout);
            product_title = itemView.findViewById(R.id.product_title);
//            product_category = itemView.findViewById(R.id.product_category);
            product_price_old = itemView.findViewById(R.id.product_price_old);
            product_price_new = itemView.findViewById(R.id.product_price_new);
            product_thumbnail = itemView.findViewById(R.id.product_cover);
            // product_tag_new = itemView.findViewById(R.id.product_item_tag_new);
            product_tag_discount_text = itemView.findViewById(R.id.productItemTagOff);
            layoutSale = itemView.findViewById(R.id.saleLayout);
            shimmerProgress = itemView.findViewById(R.id.shimmerFrame);
            selected_weight = itemView.findViewById(R.id.selected_weight);
        }

    }

    //********** Adds the Product to User's Cart *********//

    private void addProductToCart(ProductDetails product) {

        CartProduct cartProduct = new CartProduct();

        double productBasePrice, productFinalPrice = 0.0, attributesPrice = 0;
        List<CartProductAttributes> selectedAttributesList = new ArrayList<>();


        // Check Discount on Product with the help of static method of Helper class
        final String discount = Utilities.checkDiscount(product.getProductsPrice(), product.getDiscountPrice());

        // Get Product's Price based on Discount
        if (discount != null) {
            product.setIsSaleProduct("1");
            productBasePrice = Double.parseDouble(product.getDiscountPrice());
        } else {
            product.setIsSaleProduct("0");
            productBasePrice = Double.parseDouble(product.getProductsPrice());
        }


        // Get Default Attributes from AttributesList
        for (int i = 0; i < product.getAttributes().size(); i++) {

            CartProductAttributes productAttribute = new CartProductAttributes();

            // Get Name and First Value of current Attribute
            Option option = product.getAttributes().get(i).getOption();
            Value value = product.getAttributes().get(i).getValues().get(0);


            // Add the Attribute's Value Price to the attributePrices
            String attrPrice = value.getPricePrefix() + value.getPrice();
            attributesPrice += Double.parseDouble(attrPrice);


            // Add Value to new List
            List<Value> valuesList = new ArrayList<>();
            valuesList.add(value);


            // Set the Name and Value of Attribute
            productAttribute.setOption(option);
            productAttribute.setValues(valuesList);


            // Add current Attribute to selectedAttributesList
            selectedAttributesList.add(i, productAttribute);
        }

        if (isFlash) {
            productFinalPrice = Double.parseDouble(product.getFlashPrice()) + attributesPrice;
        } else {
            // Add Attributes Price to Product's Final Price
            productFinalPrice = productBasePrice + attributesPrice;
        }


        // Set Product's Price and Quantity
        product.setCustomersBasketQuantity(1);
        product.setProductsPrice(String.valueOf(productBasePrice));
        product.setAttributesPrice(String.valueOf(attributesPrice));
        product.setProductsFinalPrice(String.valueOf(productFinalPrice));
        //set selected measure/weight
//
//        product.setSelectedProductsWeight(we);
//        product.setSelectedProductsWeightUnit(product.getProductsWeightUnit().get(0));

        product.setProductsQuantity(product.getProductsDefaultStock());

        // Set Product's OrderProductCategory Info
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
        // product.setCategoryNames(product.getCategoryNames());

        product.setTotalPrice(String.valueOf(productFinalPrice));

        // Set Customer's Basket Product and selected Attributes Info
        cartProduct.setCustomersBasketProduct(product);
        cartProduct.setCustomersBasketProductAttributes(selectedAttributesList);

        // Add the Product to User's Cart with the help of static method of My_Cart class
        My_Cart.AddCartItem
                (
                        cartProduct
                );

        // Recreate the OptionsMenu
        ((WalletHomeActivity) context).invalidateOptionsMenu();
    }

    public String nFormate(double d) {
        NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
        nf.setMaximumFractionDigits(10);
        String st = nf.format(d);
        return st;
    }
}

