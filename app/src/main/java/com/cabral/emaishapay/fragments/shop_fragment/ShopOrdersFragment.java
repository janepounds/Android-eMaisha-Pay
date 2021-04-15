package com.cabral.emaishapay.fragments.shop_fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.adapters.Shop.OnlineOrdersAdapter;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.databinding.FragmentShopOrdersBinding;
import com.cabral.emaishapay.modelviews.ShopOrdersModelView;
import com.cabral.emaishapay.network.db.entities.ShopOrder;
import com.cabral.emaishapay.utils.Resource;

import java.util.List;


public class ShopOrdersFragment extends Fragment {

    private OnlineOrdersAdapter orderAdapter;
    private Context context;
    FragmentShopOrdersBinding binding;
    private ShopOrdersModelView viewModel;
    DialogLoader dialogLoader;


    @Override
    public void onAttach(@NonNull Context context) {
        this.context=context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_shop_orders,container,false);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbarOrders);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setHomeButtonEnabled(false);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(R.string.orders);

        dialogLoader = new DialogLoader(getContext());
        return  binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(this).get(ShopOrdersModelView.class);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        binding.ordersRecycler.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
        binding.ordersRecycler.setHasFixedSize(true);
        orderAdapter = new OnlineOrdersAdapter(context);
        binding.ordersRecycler.setAdapter(orderAdapter);

        dialogLoader.showProgressDialog();
        subscribeToOrderList(viewModel.getOrderList());

        binding.etxtSearchOrder.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setQuery(s);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }


        });

    }



    private void subscribeToOrderList(LiveData<Resource<List<ShopOrder>>> orders) {
        orders.observe(getViewLifecycleOwner(), myOrders -> {

            if (myOrders.data != null && myOrders.data.size() > 0) {
                binding.ordersRecycler.setVisibility(View.VISIBLE);
                binding.imageNoProduct.setVisibility(View.GONE);
                binding.txtNoProducts.setVisibility(View.GONE);
                Log.d("debug", "Orders------->>>>"+myOrders.data.size());
                orderAdapter.setOrderList(myOrders.data);
            } else {
                binding.imageNoProduct.setImageResource(R.drawable.ic_delivery_cuate);
                binding.imageNoProduct.setVisibility(View.VISIBLE);
                binding.txtNoProducts.setVisibility(View.VISIBLE);
                binding.ordersRecycler.setVisibility(View.GONE);

            }
            dialogLoader.hideProgressDialog();
        });
    }


    }
