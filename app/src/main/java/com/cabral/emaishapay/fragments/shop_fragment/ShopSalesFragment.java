package com.cabral.emaishapay.fragments.shop_fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.adapters.Shop.SalesAdapter;
import com.cabral.emaishapay.databinding.FragmentShopSalesBinding;
import com.cabral.emaishapay.modelviews.ShopSalesModelView;
import com.cabral.emaishapay.network.db.relations.ShopOrderWithProducts;
import java.util.List;

public class ShopSalesFragment extends Fragment {
    private Context context;
    private SalesAdapter salesAdapter;
    ShopSalesModelView viewModel;
    FragmentShopSalesBinding binding;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_shop_sales, container, false);
        
        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbarSales);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setHomeButtonEnabled(false);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Sales");


        viewModel = new ViewModelProvider(this).get(ShopSalesModelView.class);


        salesAdapter = new SalesAdapter(context);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        binding.recyclerSales.setLayoutManager(linearLayoutManager); // set LayoutManager to binding.recyclerSales
        binding.recyclerSales.setHasFixedSize(false);
        binding.recyclerSales.setAdapter(salesAdapter);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


       subscribeToOrderList(viewModel.getOrderSales());



        binding.etxtSearchOrder.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSalesQuery(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });


    }


    private void subscribeToOrderList(LiveData<List<ShopOrderWithProducts>> sales) {
        binding.recyclerSales.setVisibility(View.VISIBLE);
        binding.imageNoProduct.setVisibility(View.GONE);
        binding.txtNoProducts.setVisibility(View.GONE);

        sales.observe(getViewLifecycleOwner(), myOrderSales -> {

            if (myOrderSales != null && myOrderSales.size() > 0) {

                Log.d("debug", "Sales------->>>>"+myOrderSales.size());
                salesAdapter.setSalesList(myOrderSales);

                binding.recyclerSales.setVisibility(View.VISIBLE);
                binding.imageNoProduct.setVisibility(View.GONE);
                binding.txtNoProducts.setVisibility(View.GONE);
            } else {
                binding.imageNoProduct.setImageResource(R.drawable.ic_delivery_cuate);
                binding.imageNoProduct.setVisibility(View.VISIBLE);
                binding.txtNoProducts.setVisibility(View.VISIBLE);
                binding.recyclerSales.setVisibility(View.GONE);

            }
            binding.executePendingBindings();
        });


    }
}
