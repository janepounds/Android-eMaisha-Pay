package com.cabral.emaishapay.fragments.buy_fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.AppExecutors;
import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.adapters.buyInputsAdapters.ProductAdapter;
import com.cabral.emaishapay.customs.EndlessRecyclerViewScroll;
import com.cabral.emaishapay.database.User_Cart_BuyInputsDB;
import com.cabral.emaishapay.models.cart_model.CartProduct;
import com.cabral.emaishapay.models.filter_model.post_filters.PostFilterData;
import com.cabral.emaishapay.models.product_model.GetAllProducts;
import com.cabral.emaishapay.models.product_model.ProductData;
import com.cabral.emaishapay.models.product_model.ProductDetails;
import com.cabral.emaishapay.network.api_helpers.BuyInputsAPIClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;


public class ViewAllTopDeals extends Fragment {
    View rootView;
    List<ProductDetails> topDealsList= new ArrayList<>();
    private RecyclerView recyclerView;
    private Context context;
    private ProductAdapter topDealsAdapter;
    Toolbar toolbar;
    ProgressBar progressBar,mainProgress;
    PostFilterData filters = null;
    Call<ProductData> productsCall;
    int pageNo = 0;

    public ViewAllTopDeals() {
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_view_all_products, container, false);
        recyclerView = rootView.findViewById(R.id.view_all_recycler);
        toolbar = rootView.findViewById(R.id.toolbar_view_all);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Top Deals");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = rootView.findViewById(R.id.loading_bar);
        mainProgress = rootView.findViewById(R.id.progressBar);
        setHasOptionsMenu(true);
        RequestSpecialDeals(pageNo);
        // Initialize the CategoryListAdapter for RecyclerView
        topDealsAdapter = new ProductAdapter(getActivity(),getActivity().getSupportFragmentManager(),topDealsList,false,false);
        // Set the Adapter and LayoutManager to the RecyclerView
        recyclerView.setAdapter(topDealsAdapter);

        // Set the Adapter and LayoutManager to the RecyclerView
        recyclerView.setAdapter(topDealsAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        // Handle the Scroll event of Product's RecyclerView
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScroll() {
            @Override
            public void onLoadMore(final int current_page) {

                progressBar.setVisibility(View.VISIBLE);

                // Initialize LoadMoreTask to Load More Products from Server without Filters
                //loadMoreTask = new ViewAllTopDeals.LoadMoreTask(current_page, filters);
                AppExecutors.getInstance().NetworkIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        RequestSpecialDeals(current_page);
                    }
                });
            }
        });

        topDealsAdapter.notifyDataSetChanged();



        return  rootView;
    }
    public void RequestSpecialDeals(int pagenumber) {


        GetAllProducts getAllProducts = new GetAllProducts();
        getAllProducts.setPageNumber(pagenumber);
        getAllProducts.setLanguageId(ConstantValues.LANGUAGE_ID);
        getAllProducts.setCustomersId(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, context));
        getAllProducts.setType("special");
        getAllProducts.setCurrencyCode(ConstantValues.CURRENCY_CODE);
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;

        productsCall= BuyInputsAPIClient.getInstance()
                .getAllProducts
                        (access_token,
                                getAllProducts
                        );

        productsCall.enqueue(new Callback<ProductData>() {
            @Override
            public void onResponse(Call<ProductData> call, retrofit2.Response<ProductData> response) {

                if (response.isSuccessful()) {

                    if (response.body().getSuccess().equalsIgnoreCase("1")) {
                        // Products have been returned. Add Products to the dealProductsList

                        topDealsList.addAll(response.body().getProductData());

                        topDealsAdapter.notifyDataSetChanged();



                    }
                    else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        // Products haven't been returned

                    }
                    // Hide the ProgressBar
                    progressBar.setVisibility(View.GONE);
                    mainProgress.setVisibility(View.GONE);

                }


            }

            @Override
            public void onFailure(Call<ProductData> call, Throwable t) {
                if (!productsCall.isCanceled()) {
                    Toast.makeText(context, "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();
                    mainProgress.setVisibility(View.GONE);

                }
            }
        });
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
