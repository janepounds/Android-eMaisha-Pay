package com.cabral.emaishapay.fragments.buyandsell;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.adapters.buyInputsAdapters.CategoryListAdapter_3;
import com.cabral.emaishapay.adapters.buyInputsAdapters.PopularProductsAdapter;
import com.cabral.emaishapay.adapters.buyInputsAdapters.ProductDealsAdapter;
import com.cabral.emaishapay.app.EmaishaPayApp;
import com.cabral.emaishapay.models.product_model.ProductDetails;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class PopularProductsFragment extends Fragment {
    private static final String TAG = "PopularProductsFragment";
    View rootView;
    List<ProductDetails> allProductList;
    List<ProductDetails> popularProductsList;
    private RecyclerView recyclerView;
    private Context context;
    private PopularProductsAdapter popularProductsAdapter;
    private EmaishaPayApp emaishaPayApp = new EmaishaPayApp();


    public PopularProductsFragment(Context context){
        emaishaPayApp =(EmaishaPayApp) context.getApplicationContext();
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


        allProductList = new ArrayList<>();

        // Get CategoriesList from ApplicationContext
        allProductList = emaishaPayApp.getPopularProducts();
        Log.d(TAG, "onCreateView: allproducts"+allProductList);

        popularProductsList = new ArrayList<>();

        for (int i = 0; i < allProductList.size(); i++) {

            popularProductsList.add(allProductList.get(i));
            }

        // Initialize the CategoryListAdapter for RecyclerView
        popularProductsAdapter = new PopularProductsAdapter( popularProductsList, context);

        // Set the Adapter and LayoutManager to the RecyclerView
        recyclerView.setAdapter(popularProductsAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        popularProductsAdapter.notifyDataSetChanged();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
