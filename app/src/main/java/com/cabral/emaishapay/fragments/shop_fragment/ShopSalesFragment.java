package com.cabral.emaishapay.fragments.shop_fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.cabral.emaishapay.modelviews.ShopOrdersModelView;
import com.cabral.emaishapay.network.db.entities.ShopOrder;
import com.cabral.emaishapay.network.db.relations.ShopOrderWithProducts;
import com.cabral.emaishapay.utils.Resource;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class ShopSalesFragment extends Fragment {
    private Context context;
    private SalesAdapter salesAdapter;
    ShopOrdersModelView viewModel;
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
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Sales");



        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(ShopOrdersModelView.class);

        salesAdapter = new SalesAdapter(context);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        binding.recyclerSales.setLayoutManager(linearLayoutManager); // set LayoutManager to binding.recyclerSales
        binding.recyclerSales.setHasFixedSize(true);
        binding.recyclerSales.setAdapter(salesAdapter);

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

        viewModel.setSalesQuery(binding.etxtSearchOrder.getText());
    }


    private void subscribeToOrderList(LiveData<List<ShopOrderWithProducts>> sales) {
        binding.recyclerSales.setVisibility(View.VISIBLE);
        binding.imageNoProduct.setVisibility(View.GONE);
        binding.txtNoProducts.setVisibility(View.GONE);

        sales.observe(getViewLifecycleOwner(), myOrderSales -> {

            if (myOrderSales != null && myOrderSales.size() > 0) {
                binding.recyclerSales.setVisibility(View.VISIBLE);
                binding.imageNoProduct.setVisibility(View.GONE);
                binding.txtNoProducts.setVisibility(View.GONE);
                Log.d("debug", "Sales------->>>>"+myOrderSales.size());
                salesAdapter.setSalesList(myOrderSales);
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
