package com.cabral.emaishapay.fragments.buyandsell;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
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

public class WalletBuySellFragment extends Fragment {
    private Context context;
    private static final String TAG = "WalletBuySellFragment";
    StartAppRequests startAppRequests;

    List<ProductDetails> specialDealsList = new ArrayList<>();
    List<ProductDetails> popularProductsList = new ArrayList<>();
    List<CategoryDetails> allCategoriesList = new ArrayList<>();
    FragmentManager fragmentManager;



    FragmentManager fm;
    private Toolbar toolbar;
    @SuppressLint("StaticFieldLeak")
    public static EditText searchView;
    public static ImageView searchIcon;
    PopularProductsFragment popularProducts;
    TopDealsFragment topDeals;





    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy_and_sell_home, container, false);


        setHasOptionsMenu(true);
        toolbar = view.findViewById(R.id.toolbar_orders_home);
        searchView = view.findViewById(R.id.buy_inputs_search_view);
        searchIcon = view.findViewById(R.id.buy_inputs_search_icon);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setVisibility(View.VISIBLE);
        //toolbar.setTitle("Buy and Sell");
//        ((AppCompatActivity) requireActivity()).getSupportActionBar().setElevation(0.5f);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setElevation(0.5f);
        // Get FragmentManager
        FragmentManager fragmentManager = getFragmentManager();

        // Add Top_Seller Fragment to specified FrameLayout
        popularProducts = new PopularProductsFragment();
        fragmentManager.beginTransaction().replace(R.id.layout_most_popular, popularProducts).commit();

        //Add Deals Fragment to specified FrameLayout
        topDeals = new TopDealsFragment();
        fragmentManager.beginTransaction().replace(R.id.layout_deals,topDeals).commit();

        // Disable the bottom navigation from showing when you come back from payment methods fragment
        WalletHomeActivity walletHomeActivity = new WalletHomeActivity();
        walletHomeActivity.disableNavigation();

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bttm_navigation);
        bottomNavigationView.setItemIconTintList(null);



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
        fragmentManager = getFragmentManager();

        Bundle categoryBundle = new Bundle();
        categoryBundle.putBoolean("isHeaderVisible", false);
        categoryBundle.putBoolean("isMenuItem", false);
        categoryBundle.putBoolean("home_9", true);
        Fragment categories = new Categories_3();
        categories.setArguments(categoryBundle);
        fragmentManager.beginTransaction().replace(R.id.home9_categories, categories).commit();

        Bundle bundleInfo = new Bundle();
        bundleInfo.putString("sortBy", "Newest");


    }





    private class MyTask extends AsyncTask<String, Void, String> {

        DialogLoader dialogLoader = new DialogLoader(getContext());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogLoader.showProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {
            // Check for Internet Connection from the static method of Helper class
            if (Utilities.hasActiveInternetConnection(getContext())) {
                // Call the method of StartAppRequests class to process App Startup Requests
                startAppRequests.RequestAllCategories();

                startAppRequests.RequestSpecialDeals();

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
}
