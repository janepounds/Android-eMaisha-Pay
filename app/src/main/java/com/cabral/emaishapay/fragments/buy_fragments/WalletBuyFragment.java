package com.cabral.emaishapay.fragments.buy_fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletBuySellActivity;
import com.cabral.emaishapay.app.EmaishaPayApp;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.database.User_Cart_BuyInputsDB;
import com.cabral.emaishapay.models.cart_model.CartProduct;
import com.cabral.emaishapay.models.category_model.CategoryDetails;
import com.cabral.emaishapay.models.product_model.ProductDetails;
import com.cabral.emaishapay.network.StartAppRequests;
import com.cabral.emaishapay.utils.Utilities;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import am.appwise.components.ni.NoInternetDialog;

public class WalletBuyFragment extends Fragment implements Animation.AnimationListener{
    private Context context;
    private static final String TAG = "WalletBuyFragment";
    StartAppRequests startAppRequests;

    List<ProductDetails> specialDealsList = new ArrayList<>();
    List<ProductDetails> popularProductsList = new ArrayList<>();
    List<CategoryDetails> allCategoriesList = new ArrayList<>();
    FragmentManager fragmentManager;

    private Toolbar toolbar;
    @SuppressLint("StaticFieldLeak")
    public static EditText searchView;
    public static ImageView searchIcon;
    PopularProductsFragment popularProducts;
    ViewAllPopularProducts viewAllPopularProducts;
    TopDealsFragment topDeals;
    ViewAllTopDeals viewAllTopDeals;
    TextView view_all_most_popular,view_all_deals,text_categories_placeholder;
    Animation animRotate;
    ImageView imgCategoriesPlaceholder;
    LinearLayout layoutCategoriesPlaceholder;

    public WalletBuyFragment(Context context, FragmentManager fragmentManager) {
        this.context=context;
        this.fragmentManager=fragmentManager;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy_home, container, false);
        setHasOptionsMenu(true);

        toolbar = view.findViewById(R.id.toolbar_orders_home);
        searchView = view.findViewById(R.id.buy_inputs_search_view);
        searchIcon = view.findViewById(R.id.buy_inputs_search_icon);
        view_all_most_popular = view.findViewById(R.id.btn_view_all_most_popular);
        view_all_deals = view.findViewById(R.id.btn_view_all_deals);
        text_categories_placeholder = view.findViewById(R.id.text_categories_placeholder);
        imgCategoriesPlaceholder = view.findViewById(R.id.img_categories_placeholder);
        layoutCategoriesPlaceholder = view.findViewById(R.id.layout_categories_placeholder);

        animRotate = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        animRotate.setAnimationListener(this);


        //text_categories_placeholder
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setVisibility(View.VISIBLE);
        //toolbar.setTitle("Buy and Sell");
//        ((AppCompatActivity) requireActivity()).getSupportActionBar().setElevation(0.5f);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setElevation(0.5f);

        // Add Top_Seller Fragment to specified FrameLayout
        popularProducts = new PopularProductsFragment(fragmentManager);
        fragmentManager.beginTransaction().replace(R.id.layout_most_popular, popularProducts).commit();

        //Add Deals Fragment to specified FrameLayout
        topDeals = new TopDealsFragment(fragmentManager);
        fragmentManager.beginTransaction().replace(R.id.layout_deals,topDeals).commit();


        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bttm_navigation);
        bottomNavigationView.setItemIconTintList(null);

        //navigatigate to view all most popular
        view_all_most_popular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAllPopularProducts = new ViewAllPopularProducts();
                fragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment2, viewAllPopularProducts)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null).commit();
                WalletBuySellActivity.bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });

        //navigate to view all deals
        view_all_deals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAllTopDeals = new ViewAllTopDeals();
                fragmentManager.beginTransaction().replace(R.id.nav_host_fragment2,viewAllTopDeals)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null).commit();
                WalletBuySellActivity.bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });

        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(getContext()).build();
        //noInternetDialog.show();
        startAppRequests = new StartAppRequests(requireContext());

        allCategoriesList = ((EmaishaPayApp) requireContext().getApplicationContext()).getCategoriesList();

        Log.d(TAG, "onCreateView: Popular"+popularProductsList);
        Log.d(TAG, "onCreateView: special"+specialDealsList);


        // Binding Layout View

        if (allCategoriesList.isEmpty()) {
            new MyTask().execute();

        }

        else
            continueSetup();

        return view;
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void continueSetup() {
        allCategoriesList = ((EmaishaPayApp) getContext().getApplicationContext()).getCategoriesList();
        // Add corresponding ViewPagers to TabLayouts

        Bundle categoryBundle = new Bundle();
        categoryBundle.putBoolean("isHeaderVisible", false);
        categoryBundle.putBoolean("isMenuItem", false);
        categoryBundle.putBoolean("home_9", true);
        Fragment categories = new Categories_3();
        categories.setArguments(categoryBundle);
        fragmentManager.beginTransaction().replace(R.id.main_fragment_container, categories).commit();

        Bundle bundleInfo = new Bundle();
        bundleInfo.putString("sortBy", "Newest");
        imgCategoriesPlaceholder.clearAnimation();
        layoutCategoriesPlaceholder.setVisibility(View.GONE);
    }




    private class MyTask extends AsyncTask<String, Void, String> {

        DialogLoader dialogLoader = new DialogLoader(getContext());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogLoader.showProgressDialog();
            imgCategoriesPlaceholder.startAnimation(animRotate);
        }

        @Override
        protected String doInBackground(String... params) {
            // Check for Internet Connection from the static method of Helper class
            if (Utilities.hasActiveInternetConnection(getContext())) {
                // Call the method of StartAppRequests class to process App Startup Requests
                startAppRequests.RequestAllCategories();

                return "1";
            } else {
                return "0";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equalsIgnoreCase("1")) {
                continueSetup();
                dialogLoader.hideProgressDialog();
            }
        }
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
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
