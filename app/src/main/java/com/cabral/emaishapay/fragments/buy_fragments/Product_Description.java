package com.cabral.emaishapay.fragments.buy_fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.Login;
import com.cabral.emaishapay.activities.WalletBuySellActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.adapters.buyInputsAdapters.ProductAttributesAdapter;
import com.cabral.emaishapay.adapters.buyInputsAdapters.ProductMeasureAdapter;
import com.cabral.emaishapay.adapters.buyInputsAdapters.ProductReviewsAdapter;
import com.cabral.emaishapay.app.EmaishaPayApp;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.database.User_Cart_BuyInputsDB;
import com.cabral.emaishapay.models.cart_model.CartProduct;
import com.cabral.emaishapay.models.cart_model.CartProductAttributes;
import com.cabral.emaishapay.models.product_model.Attribute;
import com.cabral.emaishapay.models.product_model.GetAllProducts;
import com.cabral.emaishapay.models.product_model.GetStock;
import com.cabral.emaishapay.models.product_model.Image;
import com.cabral.emaishapay.models.product_model.Option;
import com.cabral.emaishapay.models.product_model.ProductData;
import com.cabral.emaishapay.models.product_model.ProductDetails;
import com.cabral.emaishapay.models.product_model.ProductMeasure;
import com.cabral.emaishapay.models.product_model.ProductStock;
import com.cabral.emaishapay.models.product_model.Value;
import com.cabral.emaishapay.models.ratings.GetRatings;
import com.cabral.emaishapay.models.ratings.GiveRating;
import com.cabral.emaishapay.models.ratings.ProductReviews;
import com.cabral.emaishapay.network.BuyInputsAPIClient;
import com.cabral.emaishapay.utils.Utilities;
import com.cabral.emaishapay.utils.ValidateInputs;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Transformers.BaseTransformer;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import am.appwise.components.ni.NoInternetDialog;
import hyogeun.github.com.colorratingbarlib.ColorRatingBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Product_Description extends Fragment {
    private static final String TAG = "Product_Description";
    View rootView;
    int productID;
    String customerID;
    double attributesPrice;
    double productBasePrice;
    double productFinalPrice;

    ImageView sliderImageView, increaseQty, reduceQty;
    SliderLayout sliderLayout;
    PagerIndicator pagerIndicator;
    ImageButton product_share_btn;
    ToggleButton product_like_btn;
    LinearLayout product_attributes;
    RecyclerView attribute_recycler;
    WebView product_description_webView;
    TextView title, category, price_new, price_old, product_stock, product_likes, product_tag_new, product_tag_discount, product_ratings_count;
    EditText pdtQty;
    AppCompatButton addToCart, continue_shopping_btn;

    DialogLoader dialogLoader;
    static ProductDetails productDetails;
    ProductAttributesAdapter attributesAdapter;

    Toolbar toolbar;
    List<Image> itemImages = new ArrayList<>();
    List<Attribute> attributesList = new ArrayList<>();
    List<CartProductAttributes> selectedAttributesList;
    public List<ProductReviews> productReviews;
    private RelativeLayout product_rating_bar;
    private RecyclerView recyclerView;
    private ProductMeasureAdapter productMeasureAdapter;
    private List<ProductMeasure> productMeasures;
    private Context context;
    private String selected_measure;

    ImageView checkImageView;

    private Boolean isFlash;
    private long start, server;

    List<CartProduct> cartItemsList = new ArrayList<>();
    User_Cart_BuyInputsDB user_cart_BuyInputs_db = new User_Cart_BuyInputsDB();
    List<String> stocks = new ArrayList<>();

    public Product_Description(ImageView checkedImageView, Boolean isFlash, long start, long server) {
        this.checkImageView = checkedImageView;
        this.isFlash = isFlash;
        this.start = start;
        this.server = server;
    }

    public Product_Description() {
        this.checkImageView = null;
    }

    public Product_Description(String selcted_measure) {
        this.selected_measure = selcted_measure;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.buy_inputs_product_description, container, false);
        toolbar = rootView.findViewById(R.id.toolbar_product_home);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.product_description));
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        WalletBuySellActivity.bottomNavigationView.setVisibility(View.GONE);
        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(getContext()).build();
        // noInternetDialog.show();

        cartItemsList = user_cart_BuyInputs_db.getCartItems();

        // Get the CustomerID from SharedPreferences
        customerID = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());

        product_rating_bar = rootView.findViewById(R.id.product_rating_bar);

        product_rating_bar.setOnClickListener((View.OnClickListener) v -> {
            Fragment fragment = new ProductRatingReviewListFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment2, fragment)
                    .addToBackStack(null).commit();
        });

        title = rootView.findViewById(R.id.product_title);
        category = rootView.findViewById(R.id.product_category);
        price_old = rootView.findViewById(R.id.product_price_old);
        price_new = rootView.findViewById(R.id.product_price_new);
        product_stock = rootView.findViewById(R.id.product_stock);
        product_likes = rootView.findViewById(R.id.product_total_likes);
        product_tag_new = rootView.findViewById(R.id.product_tag_new);
        product_tag_discount = rootView.findViewById(R.id.product_tag_discount);
        product_description_webView = rootView.findViewById(R.id.product_description_webView);
        sliderLayout = rootView.findViewById(R.id.product_cover_slider);
        pagerIndicator = rootView.findViewById(R.id.product_slider_indicator);
        product_like_btn = rootView.findViewById(R.id.product_like_btn);
        product_share_btn = rootView.findViewById(R.id.product_share_btn);
        product_attributes = rootView.findViewById(R.id.product_attributes);
        attribute_recycler = rootView.findViewById(R.id.product_attributes_recycler);

        product_ratings_count = rootView.findViewById(R.id.product_ratings_count);

        product_tag_new.setVisibility(View.GONE);
        product_tag_discount.setVisibility(View.GONE);
        product_attributes.setVisibility(View.VISIBLE);
        increaseQty = rootView.findViewById(R.id.increase_quantity);
        reduceQty = rootView.findViewById(R.id.reduce_quantity);
        pdtQty = rootView.findViewById(R.id.product_quantity);
        addToCart = rootView.findViewById(R.id.product_cart_btn);
        recyclerView = rootView.findViewById(R.id.measure_recyclerview);
        continue_shopping_btn = rootView.findViewById(R.id.continue_shopping_btn);

        attribute_recycler.setNestedScrollingEnabled(false);

        // Set Paint flag on price_old TextView that applies a strike-through decoration to price_old Text
        price_old.setPaintFlags(price_old.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        dialogLoader = new DialogLoader(getContext());


        selectedAttributesList = new ArrayList<>();

        // Get product Info from bundle arguments
        if (getArguments() != null) {
            if (getArguments().containsKey("itemID")) {
                productID = getArguments().getInt("itemID");

                productDetails = ((EmaishaPayApp) getContext().getApplicationContext()).getProductDetails();
                //  productDetails = getArguments().getParcelable("itemID");
                // Request Product Details
                RequestProductDetail(productID);

            } else if (getArguments().containsKey("productDetails")) {
                productDetails = getArguments().getParcelable("productDetails");
                // Set Product Details
                setProductDetails(productDetails);
            }
        }

        // Handle Click event of product_reviews_ratings Button
//        product_reviews_ratings.setOnClickListener(v -> showRatingsAndReviewsOfProduct());

        increaseQty.setOnClickListener(view -> {
            // Get the text on the text view and change to an Integer
            int num = Integer.parseInt(pdtQty.getText().toString());

            // increment it by one
            num += 1;

            // set back the incremented value
            pdtQty.setText(String.valueOf(num));
        });

        //implement reduce qty
        reduceQty.setOnClickListener(view -> {
            // Get the text on the text view and change to an Integer
            int num = Integer.parseInt(pdtQty.getText().toString());

            if (num > 1) {
                // Decrement it by one
                num -= 1;

                // set back the decremented value
                pdtQty.setText(String.valueOf(num));
            }
        });

        addToCart.setOnClickListener(view -> {
            if (!My_Cart.checkCartHasProductAndMeasure(productDetails.getProductsId())) {
                if (isFlash) {
                    if (start > server) {
                        Snackbar.make(view, context.getString(R.string.cannot_add_upcoming), Snackbar.LENGTH_SHORT).show();
                    } else {
                        Utilities.animateCartMenuIcon(context, (WalletHomeActivity) context);
                        // Add Product to User's Cart
                        addProductToCart(productDetails);
                    }

                } else {
                    if (productDetails.getProductsDefaultStock() < 1) {

                        Snackbar.make(view, context.getString(R.string.outOfStock), Snackbar.LENGTH_SHORT).show();
                    } else {
                        Utilities.animateCartMenuIcon(context.getApplicationContext(), (WalletBuySellActivity) context);
                        // Add Product to User's Cart
                        addProductToCart(productDetails);
                    }
                }

            } else {
                Snackbar.make(view, "Item already in your cart", Snackbar.LENGTH_SHORT).show();
            }
        });

        continue_shopping_btn.setOnClickListener(view -> {
            Log.e("CheckoutWarning: ", "checkout  " + ConstantValues.MAINTENANCE_MODE);

            if (ConstantValues.MAINTENANCE_MODE != null) {
                if (ConstantValues.MAINTENANCE_MODE.equalsIgnoreCase("Maintenance"))
                    showDialog(ConstantValues.MAINTENANCE_TEXT);
                else {
                    // Check if cartItemsList isn't empty
                    if (cartItemsList.size() != 0) {

                        // Check if User is Logged-In
                        if (ConstantValues.IS_USER_LOGGED_IN) {
                            Log.e("VC_Shop", "checkout executes  ");
                            new Product_Description.CheckStockTask().execute();
                        } else {
                            // Navigate to Login Activity
                            Intent i = new Intent(getContext(), Login.class);
                            getContext().startActivity(i);
                            ((WalletHomeActivity) getContext()).finish();
                            ((WalletHomeActivity) getContext()).overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
                        }
                    }

                }
            }
        });

        return rootView;
    }

    private void showDialog(String str) {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.buy_inputs_dialog_maintenance, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        final TextView dialog_message = dialogView.findViewById(R.id.maintenanceText);
        final Button dialog_button_positive = dialogView.findViewById(R.id.dialog_button);

        dialog_message.setText(str);

        final android.app.AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        dialog_button_positive.setOnClickListener(v -> alertDialog.dismiss());
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

        Snackbar.make(requireActivity().findViewById(android.R.id.content), context.getString(R.string.item_added_to_cart), Snackbar.LENGTH_SHORT).show();

        // Recreate the OptionsMenu
        ((WalletBuySellActivity) context).invalidateOptionsMenu();

        Log.d(TAG, "onCreateView: Product Type = " + productDetails.getProductsType());
        // Navigate to My_Cart Fragment
        Fragment fragment = new My_Cart();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.main_fragment_container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(getString(R.string.actionHome)).commit();
    }

    //*********** Adds Product's Details to the Views ********//

    private void setProductDetails(final ProductDetails productDetails) {

        // Get Product Images and Attributes
        itemImages = productDetails.getImages();
        attributesList = productDetails.getAttributes();

        // Setup the ImageSlider of Product Images
        ImageSlider(productDetails.getProductsImage(), itemImages);

        // Set Product's Information
        title.setText(productDetails.getProductsName());

//        product_rating_bar.setRating(productDetails.getRating());
//        product_ratings_count.setText("" + productDetails.getTotal_user_rated());

        //  set product weights
        productMeasures = productDetails.getProductsMeasure();

        showMeasuresRecyclerView();

        // Set Product's OrderProductCategory Info
        String[] categoryIDs = new String[productDetails.getCategories().size()];
        String[] categoryNames = new String[productDetails.getCategories().size()];
        if (productDetails.getCategories().size() > 0) {

            for (int i = 0; i < productDetails.getCategories().size(); i++) {
                categoryIDs[i] = String.valueOf(productDetails.getCategories().get(i).getCategoriesId());
                categoryNames[i] = productDetails.getCategories().get(i).getCategoriesName();
            }

            productDetails.setCategoryIDs(TextUtils.join(",", categoryIDs));
            productDetails.setCategoryNames(TextUtils.join(",", categoryNames));
        } else {
            productDetails.setCategoryIDs("");
            productDetails.setCategoryNames("");
        }

        category.setText(productDetails.getCategoryNames());


        if (productDetails.getProductsLiked() > 0) {
            product_likes.setText(getString(R.string.likes) + " (" + productDetails.getProductsLiked() + ")");
        } else {
            product_likes.setText(getString(R.string.likes) + " (0)");
        }


        // Check Discount on Product with the help of static method of Helper class
        String discount = Utilities.checkDiscount(productDetails.getProductsPrice(), productDetails.getDiscountPrice());

        if (discount != null) {
            productDetails.setIsSaleProduct("1");

            // Set Discount Tag
            product_tag_discount.setVisibility(View.VISIBLE);
            product_tag_discount.setText(discount + " " + getString(R.string.OFF));
            // Set Price info based on Discount
            price_old.setVisibility(View.VISIBLE);
            price_old.setText(ConstantValues.CURRENCY_SYMBOL + productDetails.getProductsPrice());
            productBasePrice = Double.parseDouble(productDetails.getDiscountPrice().replace(",", ""));

        } else {
            productDetails.setIsSaleProduct("0");

            price_old.setVisibility(View.GONE);
            product_tag_discount.setVisibility(View.GONE);
            productBasePrice = Double.parseDouble(productDetails.getProductsPrice());
        }

        // Check if the Product is Out of Stock
        if (productDetails.getProductsType() == 0)
            RequestProductStock(productDetails.getProductsId(), null);

        if (productDetails.getProductsType() == 2) {
            // productCartBtn.setText(getString(R.string.view_product));
            // productCartBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_corners_button_green));
        }

        // Check if the Product is Newly Added with the help of static method of Helper class
        if (Utilities.checkNewProduct(productDetails.getProductsDateAdded())) {
            product_tag_new.setVisibility(View.VISIBLE);
        } else {
            product_tag_new.setVisibility(View.GONE);
        }

        String description = productDetails.getProductsDescription();
        String styleSheet = "<style> " + "@font-face {font-family: 'JosefinSans-Regular'; src: url('file:///android_asset/fonts/JosefinSans-Regular.ttf');} " +
                "body{background:#FFFFFF; margin:0; padding:0;font-family: 'JosefinSans-Regular';} " +
                "p{color:#757575;} " +
                "img{display:inline; height:auto; max-width:100%;}" +
                "</style>";
        description = description.replace("\\", "");

        product_description_webView.setHorizontalScrollBarEnabled(false);
        product_description_webView.getSettings().setJavaScriptEnabled(true);
        product_description_webView.loadDataWithBaseURL(null, styleSheet + description, "text/html", "utf-8", null);


        // Set Product's Prices
        attributesPrice = 0;


        if (productDetails.getProductsType() == 1) {
            if (attributesList.size() > 0) {
                product_attributes.setVisibility(View.VISIBLE);

                for (int i = 0; i < attributesList.size(); i++) {

                    CartProductAttributes productAttribute = new CartProductAttributes();

                    // Get Name and First Value of current Attribute
                    Option option = attributesList.get(i).getOption();
                    Value value = attributesList.get(i).getValues().get(0);


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


                // Initialize the ProductAttributesAdapter for RecyclerView
                attributesAdapter = new ProductAttributesAdapter(getContext(), Product_Description.this, attributesList, selectedAttributesList);

                // Set the Adapter and LayoutManager to the RecyclerView
                attribute_recycler.setAdapter(attributesAdapter);
                attribute_recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                attributesAdapter.notifyDataSetChanged();


                RequestProductStock(productDetails.getProductsId(), attributesAdapter.getAttributeIDs());

            } else {
                product_attributes.setVisibility(View.GONE);
            }
        } else {
            product_attributes.setVisibility(View.GONE);
        }

        productFinalPrice = productBasePrice + attributesPrice;
        price_new.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(productFinalPrice));

        // Check if the User has Liked the Product
        if (productDetails.getIsLiked().equalsIgnoreCase("1")) {
            product_like_btn.setChecked(true);
        } else {
            product_like_btn.setChecked(false);
        }

        // Handle Click event of product_share_btn Button
        product_share_btn.setOnClickListener(view -> {

            // Share Product with the help of static method of Helper class
            Utilities.shareProduct
                    (
                            getContext(),
                            productDetails.getProductsName(),
                            sliderImageView,
                            productDetails.getProductsUrl()
                    );
        });

        // Handle Click event of product_like_btn Button
        product_like_btn.setOnClickListener(view -> {

            // Check if the User is Authenticated
            if (ConstantValues.IS_USER_LOGGED_IN) {

                // Check if the User has Checked the Like Button
                if (product_like_btn.isChecked()) {
                    productDetails.setIsLiked("1");
                    product_like_btn.setChecked(true);

                    // Request the Server to Like the Product for the User
                    LikeProduct(productDetails.getProductsId(), customerID, getContext(), view);

                } else {
                    productDetails.setIsLiked("0");
                    product_like_btn.setChecked(false);

                    // Request the Server to Unlike the Product for the User
                    UnlikeProduct(productDetails.getProductsId(), customerID, getContext(), view);
                }

            } else {
                // Keep the Like Button Unchecked
                product_like_btn.setChecked(false);

                // Navigate to Login Activity
                Intent i = new Intent(getContext(), Login.class);
                getContext().startActivity(i);
                ((WalletHomeActivity) getContext()).finish();
                ((WalletHomeActivity) getContext()).overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
            }
        });

//        // Handle Click event of productCartBtn Button
//        addCart.setOnClickListener(view -> {
//
//            if (productDetails.getProductsType() == 2) {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(productDetails.getProductsUrl())));
//            } else {
//                if (Integer.parseInt(pdtQty.getText().toString()) > 0) {
//
//                    CartProduct cartProduct = new CartProduct();
//
//                    // Set Product's Price, Quantity and selected Attributes Info
//                    double finalPrice = productFinalPrice;
//                    productDetails.setCustomersBasketQuantity(1);
//                    productDetails.setProductsPrice(String.valueOf(productBasePrice));
//                    productDetails.setAttributesPrice(String.valueOf(attributesPrice));
//                    productDetails.setProductsFinalPrice(String.valueOf(productFinalPrice));
//                    productDetails.setTotalPrice(String.valueOf(productFinalPrice));
//                    cartProduct.setCustomersBasketProduct(productDetails);
//                    cartProduct.setCustomersBasketProductAttributes(selectedAttributesList);
//
//
//                    // Add the Product to User's Cart with the help of static method of My_Cart class
//                    My_Cart.AddCartItem
//                            (
//                                    cartProduct
//                            );
//
//
//                    // Recreate the OptionsMenu
//                    ((DashboardActivity) getContext()).invalidateOptionsMenu();
//
//                    Snackbar.make(view, getContext().getString(R.string.item_added_to_cart), Snackbar.LENGTH_SHORT).show();
//                    checkImageView.setVisibility(View.VISIBLE);
//                }
//            }
//
//            CartProduct cartProduct = new CartProduct();
//
//            // Set Product's Price, Quantity and selected Attributes Info
//            double finalPrice = productFinalPrice;
//            productDetails.setCustomersBasketQuantity(1);
//            productDetails.setProductsPrice(String.valueOf(productBasePrice));
//            productDetails.setAttributesPrice(String.valueOf(attributesPrice));
//            productDetails.setProductsFinalPrice(String.valueOf(productFinalPrice));
//            productDetails.setTotalPrice(String.valueOf(productFinalPrice));
//            cartProduct.setCustomersBasketProduct(productDetails);
//            cartProduct.setCustomersBasketProductAttributes(selectedAttributesList);
//
//            // Add the Product to User's Cart with the help of static method of My_Cart class
//            My_Cart.AddCartItem
//                    (
//                            cartProduct
//                    );
//
//            // Recreate the OptionsMenu
//            ((DashboardActivity) getContext()).invalidateOptionsMenu();
//
//            Snackbar.make(view, getContext().getString(R.string.item_added_to_cart), Snackbar.LENGTH_SHORT).show();
//            checkImageView.setVisibility(View.VISIBLE);
//        });
    }

    public void showMeasuresRecyclerView() {
        productMeasureAdapter = new ProductMeasureAdapter(context, productMeasures, selected_measure, price_new, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(productMeasureAdapter);
    }

    //*********** Update Product's final Price based on selected Attributes ********//

    public void updateProductPrice() {

        RequestProductStock(productDetails.getProductsId(), attributesAdapter.getAttributeIDs());

        attributesPrice = 0;

        // Get Attribute's Prices List from ProductAttributesAdapter
        String[] attributePrices = attributesAdapter.getAttributePrices();

        double attributesTotalPrice = 0.0;

        for (int i = 0; i < attributePrices.length; i++) {
            // Get the Price of Attribute at given Position in attributePrices array
            double price = Double.parseDouble(attributePrices[i]);

            attributesTotalPrice += price;
        }

        attributesPrice = attributesTotalPrice;


        // Check if product from flash sale
        // Calculate and Set Product's total Price
        productFinalPrice = productBasePrice + attributesPrice;
        price_new.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(productFinalPrice));

    }

    //*********** Update Product's Stock ********//

    public void updateProductStock(String stock) {

        productDetails.setProductsQuantity(Integer.parseInt(stock));
        productDetails.setProductsDefaultStock(Integer.parseInt(stock));

        // Check if the Product is Out of Stock
        if (stock.equalsIgnoreCase("0")) {
            product_stock.setText(getString(R.string.outOfStock));
            addToCart.setText(getString(R.string.outOfStock));
            // product_stock.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccentRed));
            // productCartBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_corners_button_red));

        } else {
            product_stock.setText(getString(R.string.in_stock));
            addToCart.setText(getString(R.string.addToCart));
            //  product_stock.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccentBlue));
            //   productCartBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_corners_button_accent));
        }

        // Check if product from flash sale

        if (productDetails.getFlashPrice() != null) {
            if (!productDetails.getFlashPrice().isEmpty()) {
                price_new.setText(ConstantValues.CURRENCY_SYMBOL + new DecimalFormat("#0.00").format(Double.parseDouble(productDetails.getFlashPrice())));
                long serverTime = Long.parseLong(productDetails.getServerTime()) * 1000L;
                long startDate = Long.parseLong(productDetails.getFlashStartDate()) * 1000L;
                productBasePrice = Double.parseDouble(productDetails.getFlashPrice());
                productFinalPrice = productBasePrice + attributesPrice;
                if (startDate > serverTime) {
                    addToCart.setEnabled(false);
                    // productCartBtn.setBackgroundResource(R.drawable.rounded_corners_button_red);

                }
            }

        }
    }

    //*********** Setup the ImageSlider with the given List of Product Images ********//

    private void ImageSlider(String itemThumbnail, List<Image> itemImages) {

        // Initialize new HashMap<ImageName, ImagePath>
        final HashMap<String, String> slider_covers = new HashMap<>();
        // Initialize new Array for Image's URL
        final String[] images = new String[itemImages.size()];


        if (itemImages.size() > 0) {
            for (int i = 0; i < itemImages.size(); i++) {
                // Get Image's URL at given Position from itemImages List
                images[i] = itemImages.get(i).getImage();
            }
        }


        // Put Image's Name and URL to the HashMap slider_covers
        if (itemThumbnail.equalsIgnoreCase("")) {
            slider_covers.put("a", "" + R.drawable.placeholder);

        } else if (images.length == 0) {
            slider_covers.put("a", ConstantValues.ECOMMERCE_URL + itemThumbnail);

        } else {
            slider_covers.put("a", ConstantValues.ECOMMERCE_URL + itemThumbnail);

            for (int i = 0; i < images.length; i++) {
                slider_covers.put("b" + i, ConstantValues.ECOMMERCE_URL + images[i]);
            }
        }


        for (String name : slider_covers.keySet()) {

            // Initialize DefaultSliderView
            DefaultSliderView defaultSliderView = new DefaultSliderView(getContext()) {
                @Override
                public View getView() {
                    View v = LayoutInflater.from(getContext()).inflate(com.daimajia.slider.library.R.layout.render_type_default, null);

                    // Get daimajia_slider_image ImageView of DefaultSliderView
                    sliderImageView = v.findViewById(com.daimajia.slider.library.R.id.daimajia_slider_image);

                    // Set ScaleType of ImageView
                    sliderImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    bindEventAndShow(v, sliderImageView);

                    return v;
                }
            };

            // Set Attributes(Name, Placeholder, Image, Type etc) to DefaultSliderView
            defaultSliderView
                    .description(name)
                    .empty(R.drawable.placeholder)
                    .image(slider_covers.get(name))
                    .setScaleType(DefaultSliderView.ScaleType.FitCenterCrop);

            // Add DefaultSliderView to the SliderLayout
            sliderLayout.addSlider(defaultSliderView);
        }

        // Set PresetTransformer type of the SliderLayout
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);


        // Check if the size of Images in the Slider is less than 2
        if (slider_covers.size() < 2) {
            // Disable PagerTransformer
            sliderLayout.setPagerTransformer(false, new BaseTransformer() {
                @Override
                protected void onTransform(View view, float v) {
                }
            });

            // Hide Slider PagerIndicator
            sliderLayout.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);

        } else {
            // Set custom PagerIndicator to the SliderLayout
            sliderLayout.setCustomIndicator(pagerIndicator);
            // Make PagerIndicator Visible
            sliderLayout.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Visible);
        }
    }

    //*********** Request Product Details from the Server based on productID ********//

    public void RequestProductDetail(final int productID) {

        dialogLoader.showProgressDialog();


        GetAllProducts getAllProducts = new GetAllProducts();
        getAllProducts.setPageNumber(0);
        getAllProducts.setLanguageId(ConstantValues.LANGUAGE_ID);
        getAllProducts.setCustomersId(customerID);
        getAllProducts.setProductsId(String.valueOf(productID));
        getAllProducts.setCurrencyCode(ConstantValues.CURRENCY_CODE);

        Call<ProductData> call = BuyInputsAPIClient.getInstance()
                .getAllProducts
                        (
                                getAllProducts
                        );

        call.enqueue(new Callback<ProductData>() {
            @Override
            public void onResponse(Call<ProductData> call, Response<ProductData> response) {

                dialogLoader.hideProgressDialog();

                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        // Product's Details has been returned
                        setProductDetails(response.body().getProductData().get(0));

                    } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        Snackbar.make(rootView, response.body().getMessage(), Snackbar.LENGTH_LONG).show();

                    } else {
                        // Unable to get Success status
                        Snackbar.make(rootView, getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EmaishaPayApp.getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductData> call, Throwable t) {
                Toast.makeText(EmaishaPayApp.getContext(), "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    //*********** Request Product's Stock from the Server based on productID and Attributes ********//

    public void RequestProductStock(int productID, List<String> attributes) {

        dialogLoader.showProgressDialog();

        GetStock getStock = new GetStock();
        getStock.setProductsId(String.valueOf(productID));
        getStock.setAttributes(attributes);


        Call<ProductStock> call = BuyInputsAPIClient.getInstance()
                .getProductStock
                        (
                                getStock
                        );

        call.enqueue(new Callback<ProductStock>() {
            @Override
            public void onResponse(Call<ProductStock> call, Response<ProductStock> response) {

                dialogLoader.hideProgressDialog();

                // Check if the Response is successful
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        updateProductStock(response.body().getStock());

                    } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        Snackbar.make(rootView, response.body().getMessage(), Snackbar.LENGTH_LONG).show();

                    } else {
                        // Unable to get Success status
                        Snackbar.make(rootView, getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EmaishaPayApp.getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductStock> call, Throwable t) {
                Toast.makeText(EmaishaPayApp.getContext(), "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    //*********** Request the Server to Like the Product based on productID and customerID ********//

    public static void LikeProduct(int productID, String customerID, final Context context, final View view) {

        Call<ProductData> call = BuyInputsAPIClient.getInstance()
                .likeProduct
                        (
                                productID,
                                customerID
                        );

        call.enqueue(new Callback<ProductData>() {
            @Override
            public void onResponse(Call<ProductData> call, Response<ProductData> response) {
                // Check if the Response is successful
                if (response.isSuccessful()) {

                    // Check the Success status
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        // Product has been Liked. Show the message to User
                        Snackbar.make(view, context.getString(R.string.added_to_favourites), Snackbar.LENGTH_SHORT).show();

                    } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        Snackbar.make(view, response.body().getMessage(), Snackbar.LENGTH_LONG).show();

                    } else {
                        // Unable to get Success status
                        Snackbar.make(view, context.getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductData> call, Throwable t) {
                Toast.makeText(context, "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    //*********** Request the Server to Unlike the Product based on productID and customerID ********//

    public static void UnlikeProduct(int productID, String customerID, final Context context, final View view) {

        Call<ProductData> call = BuyInputsAPIClient.getInstance()
                .unlikeProduct
                        (
                                productID,
                                customerID
                        );

        call.enqueue(new Callback<ProductData>() {
            @Override
            public void onResponse(Call<ProductData> call, Response<ProductData> response) {
                // Check if the Response is successful
                if (response.isSuccessful()) {

                    // Check the Success status
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        // Product has been Disliked. Show the message to User
                        Snackbar.make(view, context.getString(R.string.removed_from_favourites), Snackbar.LENGTH_SHORT).show();

                    } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        Snackbar.make(view, response.body().getMessage(), Snackbar.LENGTH_LONG).show();

                    } else {
                        // Unable to get Success status
                        Snackbar.make(view, context.getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProductData> call, Throwable t) {
                Toast.makeText(context, "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    //*********** Setup the ImageSlider with the given List of Product Images ********//

    public void showRatingsAndReviewsOfProduct() {

        int rating_1_count = productDetails.getOne_ratio();
        int rating_2_count = productDetails.getTwo_ratio();
        int rating_3_count = productDetails.getThree_ratio();
        int rating_4_count = productDetails.getFour_ratio();
        int rating_5_count = productDetails.getFive_ratio();

        final Dialog reviews_ratings_dialog = new Dialog(getContext(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        reviews_ratings_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        reviews_ratings_dialog.setCancelable(true);
        reviews_ratings_dialog.setContentView(R.layout.dialog_product_rating_reviews);
        reviews_ratings_dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        // Bind Dialog Views
        TextView average_rating = reviews_ratings_dialog.findViewById(R.id.average_rating);
        TextView total_rating_count = reviews_ratings_dialog.findViewById(R.id.total_rating_count);
        ProgressBar rating_progress_5 = reviews_ratings_dialog.findViewById(R.id.rating_progress_5);
        ProgressBar rating_progress_4 = reviews_ratings_dialog.findViewById(R.id.rating_progress_4);
        ProgressBar rating_progress_3 = reviews_ratings_dialog.findViewById(R.id.rating_progress_3);
        ProgressBar rating_progress_2 = reviews_ratings_dialog.findViewById(R.id.rating_progress_2);
        ProgressBar rating_progress_1 = reviews_ratings_dialog.findViewById(R.id.rating_progress_1);
        Button rate_product_button = reviews_ratings_dialog.findViewById(R.id.rate_product);
        ImageButton dialog_back_button = reviews_ratings_dialog.findViewById(R.id.dialog_button);
        RecyclerView reviews_list_recycler = reviews_ratings_dialog.findViewById(R.id.reviews_list_recycler);

        reviews_list_recycler.setNestedScrollingEnabled(false);
        ViewCompat.setNestedScrollingEnabled(reviews_list_recycler, false);

        average_rating.setText("" + productDetails.getRating());
        total_rating_count.setText(String.valueOf(productDetails.getTotal_user_rated()));

        rating_progress_1.setProgress(rating_1_count);
        rating_progress_2.setProgress(rating_2_count);
        rating_progress_3.setProgress(rating_3_count);
        rating_progress_4.setProgress(rating_4_count);
        rating_progress_5.setProgress(rating_5_count);

        dialogLoader = new DialogLoader(getContext());
        productReviews = new ArrayList<>();
        // Initialize the ReviewsAdapter for RecyclerView
        ProductReviewsAdapter reviewsAdapter = new ProductReviewsAdapter(getContext(), productReviews);

        // Set the Adapter and LayoutManager to the RecyclerView
        reviews_list_recycler.setAdapter(reviewsAdapter);
        reviews_list_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        reviews_list_recycler.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        getProductReviews("" + productDetails.getProductsId(), reviewsAdapter);

        rate_product_button.setOnClickListener(v -> {
            if (ConstantValues.IS_USER_LOGGED_IN) {
                showRateProductDialog();
            } else {
                getContext().startActivity(new Intent(getContext(), Login.class));
                ((WalletHomeActivity) getContext()).finish();
                ((WalletHomeActivity) getContext()).overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
            }
        });

        dialog_back_button.setOnClickListener(v -> reviews_ratings_dialog.dismiss());

        reviews_ratings_dialog.show();
    }

    //*********** Setup the ImageSlider with the given List of Product Images ********//

    public void showRateProductDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_rate_product, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);

        final ColorRatingBar dialog_rating_bar = dialogView.findViewById(R.id.dialog_rating_bar);
        final EditText dialog_author_name = dialogView.findViewById(R.id.dialog_author_name);
        final EditText dialog_author_message = dialogView.findViewById(R.id.dialog_author_message);
        final Button dialog_button = dialogView.findViewById(R.id.dialog_button);

        final AlertDialog rateProductDialog = dialog.create();

        dialog_button.setOnClickListener(v -> {
            if (ValidateInputs.isValidName(dialog_author_name.getText().toString())) {
                if (!"".equalsIgnoreCase(dialog_author_message.getText().toString())) {

                    rateProductDialog.dismiss();

                    RequestGiveRating(
                            String.valueOf(productDetails.getProductsId()),
                            String.valueOf(customerID),
                            dialog_author_name.getText().toString().trim(),
                            String.valueOf((int) dialog_rating_bar.getRating()),
                            String.valueOf(productDetails.getVendors_id()),
                            String.valueOf(productDetails.getLanguageId()),
                            dialog_author_message.getText().toString().trim()
                    );
                       /* getNonceForProductRating
                                (
                                        String.valueOf(productDetails.getId()),
                                        String.valueOf(dialog_rating_bar.getRating()),
                                        dialog_author_name.getText().toString().trim(),
                                        dialog_author_email.getText().toString().trim(),
                                        dialog_author_message.getText().toString().trim()
                                );*/

                } else {
                    dialog_author_message.setError(getContext().getString(R.string.enter_message));
                }

            } else {
                dialog_author_name.setError(getContext().getString(R.string.enter_name));
            }
        });

        rateProductDialog.show();
    }

    //*********** Proceed User Registration Request ********//

    private void getProductReviews(final String productID, final ProductReviewsAdapter adapter) {

        dialogLoader.showProgressDialog();

        Call<GetRatings> call = BuyInputsAPIClient.getInstance()
                .getProductReviews
                        (
                                productID,
                                "" + ConstantValues.LANGUAGE_ID
                        );

        call.enqueue(new Callback<GetRatings>() {
            @Override
            public void onResponse(Call<GetRatings> call, Response<GetRatings> response) {

                dialogLoader.hideProgressDialog();

                String str = response.raw().request().url().toString();

                // Check if the Response is successful
                if (response.isSuccessful()) {
                    String strGson = new Gson().toJson(response.body().getData());

                    productReviews.addAll(response.body().getData());
                    int size = productReviews.size();
                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(EmaishaPayApp.getContext(), response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GetRatings> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(EmaishaPayApp.getContext(), "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void RequestGiveRating(String products_id, String customers_id, String customers_nam, String reviews_rating,
                                   String vendors_id, String languages_id, String reviews_text) {

        Map<String, String> map = new HashMap<>();
        map.put("products_id", products_id);
        map.put("customers_id", customers_id);
        map.put("customers_name", customers_nam);
        map.put("reviews_rating", reviews_rating);
        //map.put("vendors_id",vendors_id);
        map.put("languages_id", languages_id);
        map.put("reviews_text", reviews_text);


        Call<GiveRating> call = BuyInputsAPIClient.getInstance().giveRating(map);

        call.enqueue(new Callback<GiveRating>() {
            @Override
            public void onResponse(Call<GiveRating> call, Response<GiveRating> response) {

                if (response.isSuccessful()) {

                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<GiveRating> call, Throwable t) {
                Toast.makeText(getContext(), "NetworkCallFailure: " + t, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class CheckStockTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogLoader.showProgressDialog();
            stocks.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < cartItemsList.size(); i++) {
                requestProductStock2(cartItemsList.get(i).getCustomersBasketProduct().getProductsId(), getSelectedAttributesIds(cartItemsList.get(i).getCustomersBasketProductAttributes()), i);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialogLoader.hideProgressDialog();
            if (isAllStockValid(stocks)) {
                Fragment fragment = new My_Addresses(new My_Cart());
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().add(R.id.main_fragment_container, fragment)
                        .addToBackStack(getString(R.string.actionAddresses)).commit();

            } else {
                Toast.makeText(getContext(), "Your Product in the cart is out of stock.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isAllStockValid(List<String> stocks) {
        for (int i = 0; i < stocks.size(); i++) {
            if (Integer.parseInt(stocks.get(i)) <= 0)
                return false;
        }
        return true;
    }

    private void requestProductStock2(int productID, List<String> attributes, int position) {
        GetStock getStockParams = new GetStock();
        getStockParams.setProductsId(productID + "");
        getStockParams.setAttributes(attributes);
        Call<ProductStock> call = BuyInputsAPIClient.getInstance().getProductStock(getStockParams);
        try {
            Response<ProductStock> response = call.execute();
            if (response.isSuccessful()) {
                stocks.add(position, response.body().getStock());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> getSelectedAttributesIds(List<CartProductAttributes> selectedAttributes) {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < selectedAttributes.size(); i++) {
            ids.add(String.valueOf(selectedAttributes.get(i).getValues().get(0).getProducts_attributes_id()));
        }
        return ids;
    }
}

