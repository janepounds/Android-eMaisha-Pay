package com.cabral.emaishapay.fragments.buy_fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.AuthActivity;
import com.cabral.emaishapay.activities.WalletBuySellActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.adapters.buyInputsAdapters.ProductAttributesAdapter;
import com.cabral.emaishapay.adapters.buyInputsAdapters.ProductMeasureAdapter;
import com.cabral.emaishapay.app.EmaishaPayApp;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.database.User_Cart_BuyInputsDB;
import com.cabral.emaishapay.databinding.BuyInputsProductDescriptionBinding;
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
import com.cabral.emaishapay.network.api_helpers.BuyInputsAPIClient;
import com.cabral.emaishapay.utils.Utilities;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.animations.DescriptionAnimation;
import com.glide.slider.library.indicators.PagerIndicator;
import com.glide.slider.library.slidertypes.TextSliderView;
import com.glide.slider.library.tricks.ViewPagerEx;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import am.appwise.components.ni.NoInternetDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Product_Description extends Fragment  {
    private static final String TAG = "Product_Description";
    public static int productMeasureAdapterCalls=0;
    View rootView;
    int productID;
    String customerID;
    double attributesPrice;
    double productBasePrice;
    double productFinalPrice;

    ImageView sliderImageView, increaseQty, reduceQty;

    WebView product_description_webView;
    AppCompatButton addToCart,buy_now;

    DialogLoader dialogLoader;
    static ProductDetails productDetails;
    ProductAttributesAdapter attributesAdapter;

    Toolbar toolbar;
    List<Image> itemImages = new ArrayList<>();
    List<Attribute> attributesList = new ArrayList<>();
    List<CartProductAttributes> selectedAttributesList;
    private RelativeLayout product_rating_bar;
    private RecyclerView recyclerView;
    private ProductMeasureAdapter productMeasureAdapter;
    private List<ProductMeasure> productMeasures;
    private Context context;
    public static String selected_measure,selected_price;
    private  String []products_price;

    ImageView checkImageView;

    private Boolean isFlash,isChecked;
    private long start, server;
    BuyInputsProductDescriptionBinding binding;

    List<CartProduct> cartItemsList = new ArrayList<>();
    User_Cart_BuyInputsDB user_cart_BuyInputs_db = new User_Cart_BuyInputsDB();
    List<String> stocks = new ArrayList<>();
    ArrayList<Boolean>booleanArrayList= new ArrayList<>();


    public Product_Description() {
        //no-args constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater,R.layout.buy_inputs_product_description, container, false);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbarProductHome);
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

        binding.productRatingBar.setOnClickListener((View.OnClickListener) v -> {
            Fragment fragment;
            if(productDetails.getRatingDataLists().size()>0){
                fragment = new ProductRatingReviewListFragment();
            }else {
                fragment = new ProductRatingReviewFragment();
            }

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment2, fragment)
                    .addToBackStack(null).commit();
        });


        if(getArguments()!=null){
            isFlash = getArguments().getBoolean("isFlash");
            start = getArguments().getLong("start");
            server = getArguments().getLong("server");

        }




        binding.productPriceOld.setVisibility(View.GONE);
        binding.productTagNew.setVisibility(View.GONE);
        binding.productTagDiscount.setVisibility(View.GONE);
        binding.productAttributes.setVisibility(View.VISIBLE);
       ;
//        continue_shopping_btn = rootView.findViewById(R.id.continue_shopping_btn);

        binding.productAttributesRecycler.setNestedScrollingEnabled(false);

        // Set Paint flag on price_old TextView that applies a strike-through decoration to price_old Text
        binding.productPriceOld.setPaintFlags(binding.productPriceOld.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


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

        binding.increaseQuantity.setOnClickListener(view -> {
            // Get the text on the text view and change to an Integer
            int num = Integer.parseInt(binding.productQuantity.getText().toString());

            // increment it by one
            num += 1;

            // set back the incremented value
            binding.productQuantity.setText(String.valueOf(num));
        });

        //implement reduce qty
        binding.reduceQuantity.setOnClickListener(view -> {
            // Get the text on the text view and change to an Integer
            int num = Integer.parseInt(binding.productQuantity.getText().toString());

            if (num > 1) {
                // Decrement it by one
                num -= 1;

                // set back the decremented value
                binding.productQuantity.setText(String.valueOf(num));
            }
        });

        binding.productCartBtn.setOnClickListener(view -> {
            if (!My_Cart.checkCartHasProductAndMeasure(productDetails.getProductsId())) {
                if (isFlash) {
                    if (start > server) {
                        Snackbar.make(view, context.getString(R.string.cannot_add_upcoming), Snackbar.LENGTH_SHORT).show();
                    } else {
                        Utilities.animateCartMenuIcon(context, (WalletHomeActivity) context);
                        // Add Product to User's Cart
//                            if(productMeasureAdapter.checkSelectedMeasure()) {
                        addProductToCart(productDetails);
//                            }
                    }

                } else {
                    if (productDetails.getProductsDefaultStock() < 1) {

                        Snackbar.make(view, context.getString(R.string.outOfStock), Snackbar.LENGTH_SHORT).show();
                    } else {
                        Utilities.animateCartMenuIcon(context.getApplicationContext(), (WalletBuySellActivity) context);
                        // Add Product to User's Cart
//                            if(productMeasureAdapter.checkSelectedMeasure()) {
                        addProductToCart(productDetails);
//                            }
                    }
                }

            }
            else {
                Snackbar.make(view, "Item already in your cart", Snackbar.LENGTH_SHORT).show();
            }
        });


        binding.buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!My_Cart.checkCartHasProductAndMeasure(productDetails.getProductsId())) {
                    if (isFlash) {
                        if (start > server) {
                            Snackbar.make(v, context.getString(R.string.cannot_add_upcoming), Snackbar.LENGTH_SHORT).show();
                        } else {
                            Utilities.animateCartMenuIcon(context, (WalletHomeActivity) context);
                            // Add Product to User's Cart
//                            if(productMeasureAdapter.checkSelectedMeasure()) {
                                buyNow(productDetails);
//                            }
                        }

                    } else {
                        if (productDetails.getProductsDefaultStock() < 1) {

                            Snackbar.make(v, context.getString(R.string.outOfStock), Snackbar.LENGTH_SHORT).show();
                        } else {
                            Utilities.animateCartMenuIcon(context.getApplicationContext(), (WalletBuySellActivity) context);
                            // Add Product to User's Cart
//                            if(productMeasureAdapter.checkSelectedMeasure()) {
                                buyNow(productDetails);
//                            }
                        }
                    }

                }
                else {
                    Snackbar.make(v, "Item already in your cart", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        return binding.getRoot();
    }
    //********** Adds the Product to User's Cart *********//

    private void buyNow(ProductDetails product) {
        addProductToCart(product);

        Log.d(TAG, "onCreateView: Product Type = " + productDetails.getProductsType());
        // Navigate to My_Cart Fragment
        WalletBuySellActivity.navController.navigate(R.id.action_productDescription_to_myCart);

    }

    private  void  addProductToCart(ProductDetails product){
        if(Product_Description.selected_measure==null || Product_Description.selected_price==null ){
            Snackbar.make(requireActivity().findViewById(android.R.id.content), context.getString(R.string.select_product_weight), Snackbar.LENGTH_SHORT).show();

            return;
        }
        CartProduct cartProduct = new CartProduct();

        double productBasePrice, productFinalPrice = 0.0, attributesPrice = 0;
        List<CartProductAttributes> selectedAttributesList = new ArrayList<>();
        product.setProductsPrice(Product_Description.selected_price);
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
        products_price = binding.productPriceNew.getText().toString().split("\\s+");
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
            productFinalPrice = Double.parseDouble(products_price[1]) + attributesPrice;
        }




        // Set Product's Price and Quantity
        product.setCustomersBasketQuantity(Integer.parseInt(binding.productQuantity.getText().toString()));
        product.setProductsPrice(String.valueOf(products_price[1]));
        product.setAttributesPrice(String.valueOf(attributesPrice));
        product.setProductsFinalPrice(String.valueOf(productFinalPrice));
        //set selected measure/weight

//
        product.setSelectedProductsWeight(selected_measure);
        Log.d(TAG, "buyNow:measure"+selected_measure +"price"+products_price[1]);
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

//        Snackbar.make(requireActivity().findViewById(android.R.id.content), context.getString(R.string.item_added_to_cart), Snackbar.LENGTH_SHORT).show();



        // Recreate the OptionsMenu
        ((WalletBuySellActivity) context).invalidateOptionsMenu();
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


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Hide Cart Icon in the Toolbar
        MenuItem cartItem = menu.findItem(R.id.ic_cart_item);
        cartItem.setVisible(true);

        //set badge value
        User_Cart_BuyInputsDB user_cart_BuyInputs_db = new User_Cart_BuyInputsDB();
        List<CartProduct> cartItemsList;
        cartItemsList = user_cart_BuyInputs_db.getCartItems();
        TextView badge = (TextView) cartItem.getActionView().findViewById(R.id.cart_badge);
        badge.setText(String.valueOf(cartItemsList.size()));
    }

    //*********** Adds Product's Details to the Views ********//

    private void setProductDetails(final ProductDetails productDetails) {

        // Get Product Images and Attributes
        itemImages = productDetails.getImages();
        attributesList = productDetails.getAttributes();

        // Setup the ImageSlider of Product Images
        ImageSlider(productDetails.getProductsImage(), itemImages);

        // Set Product's Information
        binding.productTitle.setText(productDetails.getProductsName());

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

        binding.productCategory.setText(productDetails.getCategoryNames());


        if (productDetails.getProductsLiked() > 0) {
            binding.productTotalLikes.setText(getString(R.string.likes) + " (" + productDetails.getProductsLiked() + ")");
        } else {
            binding.productTotalLikes.setText(getString(R.string.likes) + " (0)");
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
            binding.productTagNew.setVisibility(View.VISIBLE);
        } else {
            binding.productTagNew.setVisibility(View.GONE);
        }

        String description = productDetails.getProductsDescription();
        String styleSheet = "<style> " + "@font-face {font-family: 'nunito'; src: url('file:///android_asset/font/nunito.ttf');} " +
                "body{background:#FFFFFF; margin:0; padding:0;font-family: 'nunito';} " +
                "p{color:#757575;} " +
                "img{display:inline; height:auto; max-width:100%;}" +
                "</style>";
        description = description.replace("\\", "");

        binding.productDescriptionWebView.setHorizontalScrollBarEnabled(false);
        binding.productDescriptionWebView.getSettings().setJavaScriptEnabled(true);
        binding.productDescriptionWebView.loadDataWithBaseURL(null, styleSheet + description, "text/html", "utf-8", null);


        // Set Product's Prices
        attributesPrice = 0;


        if (productDetails.getProductsType() == 1) {
            if (attributesList.size() > 0) {
                binding.productAttributes.setVisibility(View.VISIBLE);

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
                binding.productAttributesRecycler.setAdapter(attributesAdapter);
                binding.productAttributesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                attributesAdapter.notifyDataSetChanged();


                RequestProductStock(productDetails.getProductsId(), attributesAdapter.getAttributeIDs());

            } else {
                binding.productAttributes.setVisibility(View.GONE);
            }
        } else {
            binding.productAttributes.setVisibility(View.GONE);
        }

        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
        productFinalPrice = productBasePrice + attributesPrice;
        binding.productPriceNew.setText(ConstantValues.CURRENCY_SYMBOL +" "+ nf.format(productFinalPrice));

        // Check if the User has Liked the Product
        if (productDetails.getIsLiked().equalsIgnoreCase("1")) {
            binding.productLikeBtn.setChecked(true);
        } else {
            binding.productLikeBtn.setChecked(false);
        }

        // Handle Click event of product_share_btn Button
        binding.productShareBtn.setOnClickListener(view -> {

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
        binding.productLikeBtn.setOnClickListener(view -> {

            // Check if the User is Authenticated
            if (ConstantValues.IS_USER_LOGGED_IN) {

                // Check if the User has Checked the Like Button
                if (binding.productLikeBtn.isChecked()) {
                    productDetails.setIsLiked("1");
                    binding.productLikeBtn.setChecked(true);

                    // Request the Server to Like the Product for the User
                    LikeProduct(productDetails.getProductsId(), customerID, getContext(), view);

                } else {
                    productDetails.setIsLiked("0");
                    binding.productLikeBtn.setChecked(false);

                    // Request the Server to Unlike the Product for the User
                    UnlikeProduct(productDetails.getProductsId(), customerID, getContext(), view);
                }

            } else {
                // Keep the Like Button Unchecked
                binding.productLikeBtn.setChecked(false);

                // Navigate to Login Activity
                Intent i = new Intent(getContext(), AuthActivity.class);
                getContext().startActivity(i);
                ((WalletHomeActivity) getContext()).finish();
                ((WalletHomeActivity) getContext()).overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
            }
        });

    }

    public void showMeasuresRecyclerView() {
        if(productMeasures.size()>0){
            productMeasureAdapter = new ProductMeasureAdapter(context, productMeasures, binding.productPriceNew, this,booleanArrayList);
            binding.measureRecyclerview.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            binding.measureRecyclerview.setAdapter(productMeasureAdapter);

            if(productMeasures.size()==1 && productMeasureAdapterCalls==1){//listener to refresh Measures Adapter after setting a solo measure checked
                binding.measureRecyclerview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        productMeasureAdapter.notifyDataSetChanged();
                    }
                });
            }
        }

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
        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
        productFinalPrice = productBasePrice + attributesPrice;
        binding.productPriceNew.setText(ConstantValues.CURRENCY_SYMBOL +" "+ nf.format(productFinalPrice));

    }

    //*********** Update Product's Stock ********//

    public void updateProductStock(String stock) {

        productDetails.setProductsQuantity(Integer.parseInt(stock));
        productDetails.setProductsDefaultStock(Integer.parseInt(stock));

        // Check if the Product is Out of Stock
        if (stock.equalsIgnoreCase("0")) {
            binding.productStock.setText(getString(R.string.outOfStock));
            addToCart.setText(getString(R.string.outOfStock));
            // product_stock.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccentRed));
            // productCartBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_corners_button_red));

        } else {
            binding.productStock.setText(getString(R.string.in_stock));
            binding.productCartBtn.setText(getString(R.string.addToCart));
            //  product_stock.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccentBlue));
            //   productCartBtn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_corners_button_accent));
        }

        // Check if product from flash sale
        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
        if (productDetails.getFlashPrice() != null) {
            if (!productDetails.getFlashPrice().isEmpty()) {
                binding.productPriceNew.setText(ConstantValues.CURRENCY_SYMBOL +" "+ nf.format(Double.parseDouble(productDetails.getFlashPrice())));
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
            slider_covers.put("a", "" + R.drawable.new_product);

        } else if (images.length == 0) {
            slider_covers.put("a", ConstantValues.ECOMMERCE_WEB + itemThumbnail);

        } else {
            slider_covers.put("a", ConstantValues.ECOMMERCE_WEB + itemThumbnail);

            for (int i = 0; i < images.length; i++) {
                slider_covers.put("b" + i, ConstantValues.ECOMMERCE_WEB + images[i]);
            }
        }


        for (String name : slider_covers.keySet()) {

            // Initialize DefaultSliderView
            TextSliderView defaultSliderView = new TextSliderView(context);

            RequestOptions requestOptions = new RequestOptions();

            requestOptions.centerInside();

            // Set Attributes(Name, Placeholder, Image, Type etc) to DefaultSliderView
            defaultSliderView
                    //.description(name)
                    .setRequestOption(requestOptions)
                    .image(slider_covers.get(name));

            // Add DefaultSliderView to the SliderLayout
            binding.productCoverSlider.addSlider(defaultSliderView);
        }

        // Set PresetTransformer type of the SliderLayout
        binding.productCoverSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        binding.productCoverSlider.setCustomAnimation(new DescriptionAnimation());
        binding.productCoverSlider.setDuration(4000);
        binding.productCoverSlider.addOnPageChangeListener(new ViewPagerEx.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        binding.productCoverSlider.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);
        // Check if the size of Images in the Slider is less than 2
//        if (slider_covers.size() < 2) {
//            // Disable PagerTransformer
//            sliderLayout.setPagerTransformer(false, new BaseTransformer() {
//                @Override
//                protected void onTransform(View view, float v) {
//
//                }
//            });
//
//            // Hide Slider PagerIndicator
//            sliderLayout.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);
//
//        } else {
//            // Set custom PagerIndicator to the SliderLayout
//            sliderLayout.setCustomIndicator(pagerIndicator);
//            // Make PagerIndicator Visible
//            sliderLayout.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Visible);
//        }

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
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        Call<ProductData> call = BuyInputsAPIClient.getInstance()
                .getAllProducts
                        (access_token,
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

        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();

        Call<ProductStock> call = BuyInputsAPIClient.getInstance()
                .getProductStock
                        (access_token,
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
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;

        Call<ProductData> call = BuyInputsAPIClient.getInstance()
                .likeProduct
                        (access_token,
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
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        Call<ProductData> call = BuyInputsAPIClient.getInstance()
                .unlikeProduct
                        (access_token,
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


    @Override
    public void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        binding.productCoverSlider.stopAutoCycle();
        super.onStop();
    }

    //*********** Setup the ImageSlider with the given List of Product Images ********//




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
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;

        Call<ProductStock> call = BuyInputsAPIClient.getInstance().getProductStock(access_token,getStockParams);
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


    public boolean isAnyBooleanTrue(ArrayList<Boolean> booleanArrayList) {
        boolean isAnyChecked = false;

        for (int i = 0; i < booleanArrayList.size(); i++) {
            if (booleanArrayList.get(i).booleanValue()==true) {
                isAnyChecked = true;
            }else{
                Toast.makeText(context, "please select a measure before proceeding", Toast.LENGTH_SHORT).show();
            }

        }

        return isAnyChecked;
    }


}

