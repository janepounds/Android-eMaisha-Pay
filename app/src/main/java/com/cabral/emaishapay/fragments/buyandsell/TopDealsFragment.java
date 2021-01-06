package com.cabral.emaishapay.fragments.buyandsell;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.adapters.LoansListAdapter;
import com.cabral.emaishapay.adapters.buyInputsAdapters.ProductDealsAdapter;
import com.cabral.emaishapay.models.product_model.ProductDetails;

import java.util.ArrayList;
import java.util.List;

public class TopDealsFragment extends Fragment {
    View rootView;
    List<ProductDetails> allProductList = new ArrayList<>();
    private RecyclerView recyclerView;
    private Context context;
    private ProductDealsAdapter topDealsAdapter;

    public TopDealsFragment(List<ProductDetails> productDetails) {
        this.allProductList = productDetails;
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

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        topDealsAdapter = new ProductDealsAdapter(allProductList);
        recyclerView.setAdapter(topDealsAdapter);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
