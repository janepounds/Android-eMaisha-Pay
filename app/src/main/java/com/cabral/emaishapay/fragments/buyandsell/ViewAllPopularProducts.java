package com.cabral.emaishapay.fragments.buyandsell;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.adapters.buyInputsAdapters.ProductAdapter;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.models.product_model.GetAllProducts;
import com.cabral.emaishapay.models.product_model.ProductData;
import com.cabral.emaishapay.models.product_model.ProductDetails;
import com.cabral.emaishapay.network.BuyInputsAPIClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class ViewAllPopularProducts extends Fragment {
    View rootView;
    List<ProductDetails> popularProductsList= new ArrayList<>();
    private RecyclerView recyclerView;
    private Context context;
    private ProductAdapter popularProductsAdapter;
    Toolbar toolbar;

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
        rootView = inflater.inflate(R.layout.layout_view_all_products, container, false);
        recyclerView = rootView.findViewById(R.id.view_all_recycler);
        toolbar = rootView.findViewById(R.id.toolbar_view_all);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Popular Products");
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Initialize the CategoryListAdapter for RecyclerView
        popularProductsAdapter = new ProductAdapter(getActivity(),getActivity().getSupportFragmentManager(),popularProductsList,false,false);
        // Set the Adapter and LayoutManager to the RecyclerView
        recyclerView.setAdapter(popularProductsAdapter);

        // Set the Adapter and LayoutManager to the RecyclerView
        recyclerView.setAdapter(popularProductsAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        popularProductsAdapter.notifyDataSetChanged();


        RequestTopSellers();
        return  rootView;
    }

    public void RequestTopSellers() {

        GetAllProducts getAllProducts = new GetAllProducts();
        getAllProducts.setPageNumber(0);
        getAllProducts.setLanguageId(ConstantValues.LANGUAGE_ID);
        getAllProducts.setCustomersId(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, context));
        getAllProducts.setType("top seller");
        getAllProducts.setCurrencyCode(ConstantValues.CURRENCY_CODE);


        Call<ProductData> networkCall= BuyInputsAPIClient.getInstance()
                .getAllProducts
                        (
                                getAllProducts
                        );

        networkCall.enqueue(new Callback<ProductData>() {
            @Override
            public void onResponse(Call<ProductData> call, retrofit2.Response<ProductData> response) {

                if (response.isSuccessful()) {

                    if (response.body().getSuccess().equalsIgnoreCase("1")) {
                        // Products have been returned. Add Products to the dealProductsList

//                        popularDealsProduct.setPopularproductList(
//                                response.body().getProductData());

                        popularProductsList.addAll(response.body().getProductData());
                        Log.w("PopularProductzSIZE", ""+popularProductsList.size());



                        popularProductsAdapter.notifyDataSetChanged();
                    }
                    else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        // Products haven't been returned

                    }

                }


            }

            @Override
            public void onFailure(Call<ProductData> call, Throwable t) {
                if (!networkCall.isCanceled()) {
                    Toast.makeText(context, "NetworkCallFailure : "+t, Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.home:
                FragmentManager fm = getFragmentManager();

                // Check if BackStack has some Fragments
                if (fm.getBackStackEntryCount() > 0) {
                    // Pop previous Fragment
                    fm.popBackStack();

                }
                break;
        }
        return true;
    }
}
