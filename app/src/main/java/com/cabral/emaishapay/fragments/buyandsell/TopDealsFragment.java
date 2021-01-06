package com.cabral.emaishapay.fragments.buyandsell;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.models.product_model.ProductDetails;

import java.util.ArrayList;
import java.util.List;

public class TopDealsFragment extends Fragment {
    View rootView;
    List<ProductDetails> allProductList = new ArrayList<>();
    private RecyclerView recyclerView;
    public TopDealsFragment(List<ProductDetails> productDetails) {
        this.allProductList = productDetails;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.top_deals_fragment, container, false);
//        recyclerView = rootView.findViewById(R.id.);



        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
