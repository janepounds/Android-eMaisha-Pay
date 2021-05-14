package com.cabral.emaishapay.fragments.buy_fragments;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cabral.emaishapay.AppExecutors;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletBuySellActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.app.EmaishaPayApp;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.database.User_Cart_BuyInputsDB;
import com.cabral.emaishapay.databinding.NewFragmentBuyHomeBinding;
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
    NewFragmentBuyHomeBinding binding;

    List<ProductDetails> specialDealsList = new ArrayList<>();
    List<ProductDetails> popularProductsList = new ArrayList<>();
    List<CategoryDetails> allCategoriesList = new ArrayList<>();
    FragmentManager fragmentManager;

    @SuppressLint("StaticFieldLeak")
    public static EditText searchView;
    public static ImageView searchIcon;
    PopularProductsFragment popularProducts;
    ViewAllPopularProducts viewAllPopularProducts;
    TopDealsFragment topDeals;
    ViewAllTopDeals viewAllTopDeals;
    TextView view_all_most_popular,view_all_deals,text_categories_placeholder;
    Animation animRotate;


    public WalletBuyFragment(Context context, FragmentManager fragmentManager) {
        this.context=context;
        this.fragmentManager=fragmentManager;
    }

    //no args-constructor
    public WalletBuyFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.new_fragment_buy_home,container,false);

        setHasOptionsMenu(true);
        searchView = binding.buyInputsSearchView;
        searchIcon = binding.buyInputsSearchIcon;


        animRotate = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        animRotate.setAnimationListener(this);

        //text_categories_placeholder
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbarOrdersHome);
        binding.toolbarOrdersHome.setVisibility(View.VISIBLE);
        binding.toolbarOrdersHome.setTitle("Shop");
//        ((AppCompatActivity) requireActivity()).getSupportActionBar().setElevation(0.5f);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayShowTitleEnabled(true);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setElevation(0.5f);

        // Add Top_Seller Fragment to specified FrameLayout
        popularProducts = new PopularProductsFragment(fragmentManager);
        getChildFragmentManager().beginTransaction().replace(R.id.layout_most_popular, popularProducts).commit();

        //Add Deals Fragment to specified FrameLayout
        topDeals = new TopDealsFragment(fragmentManager);
        getChildFragmentManager().beginTransaction().replace(R.id.layout_deals,topDeals).commit();

        binding.bttmNavigation.setItemIconTintList(null);

        //navigate to view all most popular
        binding.btnViewAllMostPopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navigate to view all most popular
                WalletBuySellActivity.navController.navigate(R.id.action_walletBuyFragment_to_viewAllPopularProducts);
                WalletBuySellActivity.bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });

        //navigate to view all deals
        binding.btnViewAllDeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navigate to top deals
                WalletBuySellActivity.navController.navigate(R.id.action_walletBuyFragment_to_viewAllTopDeals);
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
            loadCategories();

        }

        else
            continueSetup();

        return binding.getRoot();
    }

    private void loadCategories() {
        DialogLoader dialogLoader = new DialogLoader(getContext());
        dialogLoader.showProgressDialog();
        binding.imgCategoriesPlaceholder.startAnimation(animRotate);

                AppExecutors.getInstance().NetworkIO().execute(
                        new Runnable() {
                            @Override
                            public void run() {
                                if (Utilities.hasActiveInternetConnection(getContext())) {
                                    // Call the method of StartAppRequests class to process App Startup Requests
                                    startAppRequests.RequestAllCategories();

                                    AppExecutors.getInstance().mainThread().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                                continueSetup();
                                                dialogLoader.hideProgressDialog();

                                        }
                                    });

                                }

                            }

                        }
                );

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
        getChildFragmentManager().beginTransaction().replace(R.id.main_fragment_container, categories).commit();

        Bundle bundleInfo = new Bundle();
        bundleInfo.putString("sortBy", "Newest");
        binding.imgCategoriesPlaceholder.clearAnimation();
        binding.layoutCategoriesPlaceholder.setVisibility(View.GONE);
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
