package com.cabral.emaishapay.fragments.buy_fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.adapters.buyInputsAdapters.ProductAdapter;
import com.cabral.emaishapay.models.product_model.GetAllProducts;
import com.cabral.emaishapay.models.product_model.ProductData;
import com.cabral.emaishapay.models.product_model.ProductDetails;
import com.cabral.emaishapay.network.api_helpers.BuyInputsAPIClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class PopularProductsFragment extends Fragment {
    private static final String TAG = "PopularProductsFragment";
    View rootView;
    List<ProductDetails> popularProductsList= new ArrayList<>();
    private RecyclerView recyclerView;
    private Context context;
    FragmentManager fragmentManager;
    private ProductAdapter popularProductsAdapter;

    public PopularProductsFragment(FragmentManager fragmentManager) {
        this.fragmentManager=fragmentManager;
    }

    public PopularProductsFragment() {
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.top_deals_fragment, container, false);
        recyclerView = rootView.findViewById(R.id.layout_top_deals);

        // RecyclerView has fixed Size
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        // Initialize the CategoryListAdapter for RecyclerView
        popularProductsAdapter = new ProductAdapter(getActivity(),fragmentManager,popularProductsList,false,false);
        // Set the Adapter and LayoutManager to the RecyclerView
        recyclerView.setAdapter(popularProductsAdapter);


        RequestTopSellers();


        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void invalidateProducts(){
        popularProductsAdapter.notifyDataSetChanged();
    }

    public void RequestTopSellers() {

        GetAllProducts getAllProducts = new GetAllProducts();
        getAllProducts.setLimit(3);
        getAllProducts.setPageNumber(0);
        getAllProducts.setLanguageId(ConstantValues.LANGUAGE_ID);
        getAllProducts.setCustomersId(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, context));
        getAllProducts.setType("top seller");
        getAllProducts.setCurrencyCode(ConstantValues.CURRENCY_CODE);
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;

        Call<ProductData> networkCall= BuyInputsAPIClient.getInstance()
                .getAllProducts
                        (access_token,
                                getAllProducts
                        );

        networkCall.enqueue(new Callback<ProductData>() {
            @Override
            public void onResponse(Call<ProductData> call, retrofit2.Response<ProductData> response) {

                if (response.isSuccessful()) {

                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        popularProductsList.addAll(response.body().getProductData());
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

}
