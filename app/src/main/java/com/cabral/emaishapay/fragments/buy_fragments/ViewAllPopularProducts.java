package com.cabral.emaishapay.fragments.buy_fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.AppExecutors;
import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.adapters.buyInputsAdapters.ProductAdapter;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.customs.EndlessRecyclerViewScroll;
import com.cabral.emaishapay.database.User_Cart_BuyInputsDB;
import com.cabral.emaishapay.databinding.LayoutViewAllProductsBinding;
import com.cabral.emaishapay.models.cart_model.CartProduct;
import com.cabral.emaishapay.models.filter_model.post_filters.PostFilterData;
import com.cabral.emaishapay.models.product_model.GetAllProducts;
import com.cabral.emaishapay.models.product_model.ProductData;
import com.cabral.emaishapay.models.product_model.ProductDetails;
import com.cabral.emaishapay.network.api_helpers.BuyInputsAPIClient;
import com.cabral.emaishapay.network.Connectivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class ViewAllPopularProducts extends Fragment {
    View rootView;
    List<ProductDetails> popularProductsList= new ArrayList<>();
    private Context context;
    private ProductAdapter popularProductsAdapter;
    PostFilterData filters = null;
    Call<ProductData> productsCall;
    int pageNo = 0;
    LayoutViewAllProductsBinding binding;

    public ViewAllPopularProducts() {
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.layout_view_all_products, container, false);

        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbarViewAll);
        binding.toolbarViewAll.setTitle("Popular Products");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
        RequestTopSellers(pageNo);
        // Initialize the CategoryListAdapter for RecyclerView
        popularProductsAdapter = new ProductAdapter(getActivity(),getActivity().getSupportFragmentManager(),popularProductsList,false,false);
        // Set the Adapter and LayoutManager to the RecyclerView
        binding.viewAllRecycler.setAdapter(popularProductsAdapter);

        // Set the Adapter and LayoutManager to the RecyclerView
        binding.viewAllRecycler.setAdapter(popularProductsAdapter);
        binding.viewAllRecycler.setLayoutManager(new GridLayoutManager(getContext(), 3));

        // Handle the Scroll event of Product's RecyclerView
        binding.viewAllRecycler.addOnScrollListener(new EndlessRecyclerViewScroll() {
            @Override
            public void onLoadMore(final int current_page) {

                binding.loadingBar.setVisibility(View.VISIBLE);

                // Load More Products from Server without Filters
                loadMoreTask(current_page, filters);
            }
        });



        popularProductsAdapter.notifyDataSetChanged();



        return  binding.getRoot();
    }

    public void RequestTopSellers(int pagenumber) {

        GetAllProducts getAllProducts = new GetAllProducts();
        getAllProducts.setPageNumber(pagenumber);
        getAllProducts.setLanguageId(ConstantValues.LANGUAGE_ID);
        getAllProducts.setCustomersId(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, context));
        getAllProducts.setType("top seller");
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

//                        popularDealsProduct.setPopularproductList(
//                                response.body().getProductData());

                        popularProductsList.addAll(response.body().getProductData());
                         //Log.w("PopularProductzSIZE", ""+popularProductsList.size());



                        popularProductsAdapter.notifyDataSetChanged();
                    }
                    else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        // Products haven't been returned

                    }
                    // Hide the ProgressBar
                    binding.progressBar.setVisibility(View.GONE);
                    binding.loadingBar.setVisibility(View.GONE);
                }


            }

            @Override
            public void onFailure(Call<ProductData> call, Throwable t) {
                if (!productsCall.isCanceled()) {
                    Toast.makeText(context, "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    /*********** LoadMoreTask Used to Load more Products from the Server in the Background Thread using AsyncTask ********/

    private void loadMoreTask(int page_number, PostFilterData postFilterData) {

        AppExecutors.getInstance().NetworkIO().
                execute(new Runnable() {
                    @Override
                    public void run() {
                        // Request for Products of given OrderProductCategory, based on PageNo.
                        if(Connectivity.isConnected(context)){
                            RequestTopSellers(page_number);

                        }else {
                            Toast.makeText(context,getString(R.string.internet_connection_error),Toast.LENGTH_LONG).show();


                        }

                    }
         });

    }

    @Override
    public void onPause() {
        super.onPause();
        if (productsCall.isExecuted())
            productsCall.cancel();

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
