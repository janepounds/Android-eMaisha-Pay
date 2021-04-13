package com.cabral.emaishapay.fragments.shop_fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.cabral.emaishapay.activities.ShopActivity;
import com.cabral.emaishapay.adapters.Shop.OnlineOrdersAdapter;
import com.cabral.emaishapay.adapters.Shop.OrderAdapter;
import com.cabral.emaishapay.database.DatabaseAccess;
import com.cabral.emaishapay.database.DbHandlerSingleton;
import com.cabral.emaishapay.modelviews.ShopOrdersModelView;
import com.cabral.emaishapay.modelviews.ShopPOSModelView;
import com.cabral.emaishapay.network.db.entities.EcProduct;
import com.cabral.emaishapay.network.db.entities.ShopOrderList;
import com.cabral.emaishapay.utils.Resource;

import java.util.HashMap;
import java.util.List;


public class ShopOrdersFragment extends Fragment {

    ImageView imgNoProduct;
    TextView txtNoProducts;
    EditText etxtSearch;
    private RecyclerView recyclerView;
    private OnlineOrdersAdapter orderAdapter;
    private Context context;
    Toolbar toolbar;
    private ShopOrdersModelView viewModel;


    @Override
    public void onAttach(@NonNull Context context) {
        this.context=context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shop_orders, container, false);
        recyclerView = view.findViewById(R.id.recycler);
        imgNoProduct = view.findViewById(R.id.image_no_product);

        txtNoProducts = view.findViewById(R.id.txt_no_products);
        etxtSearch = view.findViewById(R.id.etxt_search_order);

        imgNoProduct.setVisibility(View.GONE);
        txtNoProducts.setVisibility(View.GONE);
        toolbar = view.findViewById(R.id.toolbar_orders);

        viewModel = new ViewModelProvider(this).get(ShopOrdersModelView.class);
        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView

        recyclerView.setHasFixedSize(true);
        orderAdapter = new OnlineOrdersAdapter(getContext());

        recyclerView.setAdapter(orderAdapter);

        subscribeToOrderList(viewModel.getOrderList());


        etxtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {



            }

            @Override
            public void afterTextChanged(Editable s) {

            }


        });



        return  view;
    }


    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                requireActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void subscribeToOrderList(LiveData<Resource<List<ShopOrderList>>> orders) {
        orders.observe(getViewLifecycleOwner(), searchedOrders -> {
            //dialogLoader.showProgressDialog();
            Log.d("debug", "Orders------->>>>");
            if (searchedOrders.data != null && searchedOrders.data.size() <= 0) {
                recyclerView.setVisibility(View.VISIBLE);
                imgNoProduct.setVisibility(View.GONE);
                orderAdapter.setOrderList(searchedOrders.data);
            } else {
                imgNoProduct.setImageResource(R.drawable.ic_delivery_cuate);
                txtNoProducts.setVisibility(View.VISIBLE);
            }
        });
    }

    private void subscribeToSearchedOrders(LiveData<Resource<List<ShopOrderList>>> products) {
        products.observe(getViewLifecycleOwner(), searchedProducts -> {
            //dialogLoader.showProgressDialog();
            Log.d("debug", "------->>>>");
            if (searchedProducts.data != null && searchedProducts.data.size() <= 0) {
                recyclerView.setVisibility(View.GONE);
                imgNoProduct.setVisibility(View.VISIBLE);
                imgNoProduct.setImageResource(R.drawable.no_data);


            } else {
                recyclerView.setVisibility(View.VISIBLE);
                imgNoProduct.setVisibility(View.GONE);


//                OrderAdapter supplierAdapter = new OrderAdapter(context, searchOrder);

//                recyclerView.setAdapter(supplierAdapter);


            }
        });
    }



    }
