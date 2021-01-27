package com.cabral.emaishapay.fragments.shop_fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.ShopActivity;
import com.cabral.emaishapay.adapters.Shop.SalesAdapter;
import com.cabral.emaishapay.database.DbHandlerSingleton;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class ShopSalesFragment extends Fragment {
    FragmentManager fm;
    Activity shop;
    private Context context;
    private DbHandlerSingleton dbHandler;
    Toolbar toolbar;
    ImageView imgNoProduct;
    TextView txtNoProducts;
    EditText etxtSearch;
    private RecyclerView recyclerView;
    private SalesAdapter salesAdapter;


    public ShopSalesFragment(ShopActivity shopActivity, FragmentManager supportFragmentManager) {
        this.fm = supportFragmentManager;
        this.shop = shopActivity;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shop_sales, container, false);
        toolbar = view.findViewById(R.id.toolbar_shop_products);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Products");

        dbHandler = DbHandlerSingleton.getHandlerInstance(getContext());
        recyclerView = view.findViewById(R.id.recycler);
        imgNoProduct = view.findViewById(R.id.image_no_product);

        txtNoProducts = view.findViewById(R.id.txt_no_products);
        etxtSearch = view.findViewById(R.id.etxt_search_order);

        imgNoProduct.setVisibility(View.GONE);
        txtNoProducts.setVisibility(View.GONE);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
        recyclerView.setHasFixedSize(true);

        List<HashMap<String, String>> orderList;
        orderList = dbHandler.getOrderList();

        if (orderList.size() <= 0) {
            //if no data in local db, then load data from server
            Toasty.info(getContext(), "No Order Found", Toast.LENGTH_SHORT).show();
            recyclerView.setVisibility(View.GONE);
            imgNoProduct.setVisibility(View.VISIBLE);
            imgNoProduct.setImageResource(R.drawable.ic_delivery_cuate);
            txtNoProducts.setVisibility(View.VISIBLE);
        } else {
            salesAdapter = new SalesAdapter(getContext(), orderList);

            recyclerView.setAdapter(salesAdapter);
        }


        etxtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                //  searchData(s.toString());

//                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(OrdersActivity.this);
//                databaseAccess.open();
                //get data from local database
                List<HashMap<String, String>> searchOrder;

                searchOrder = dbHandler.searchOrderList(s.toString());


                if (searchOrder.size() <= 0) {
                    recyclerView.setVisibility(View.GONE);
                    imgNoProduct.setVisibility(View.VISIBLE);
                    imgNoProduct.setImageResource(R.drawable.no_data);


                } else {


                    recyclerView.setVisibility(View.VISIBLE);
                    imgNoProduct.setVisibility(View.GONE);


                    SalesAdapter supplierAdapter = new SalesAdapter(getContext(), searchOrder);

                    recyclerView.setAdapter(supplierAdapter);


                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }


        });



        return view;
    }
}
