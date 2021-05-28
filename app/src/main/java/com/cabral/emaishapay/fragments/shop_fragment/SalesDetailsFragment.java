package com.cabral.emaishapay.fragments.shop_fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.adapters.Shop.SalesDetailsAdapter;

import com.cabral.emaishapay.network.db.entities.ShopOrderProducts;
import com.cabral.emaishapay.network.db.relations.ShopOrderWithProducts;

public class SalesDetailsFragment extends Fragment {

    ImageView imgNoProduct;
    TextView txtNoProducts, txtTotalPrice;
    String  customer_name;
    String longText, shortText;
    private RecyclerView recyclerView;
    private SalesDetailsAdapter salesDetailsAdapter;
    private String[] header = {"Description", "Price"};

    private Context context;
    Toolbar toolbar;
    ShopOrderWithProducts salesDetails;

    @Override
    public void onAttach(@NonNull Context context) {
        this.context=context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sales_details, container, false);
        toolbar = view.findViewById(R.id.toolbar_sales_details);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Products");

        recyclerView = view.findViewById(R.id.sales_Products_recycler);
        imgNoProduct = view.findViewById(R.id.image_no_product);
        txtTotalPrice = view.findViewById(R.id.txt_total_price);


        txtNoProducts = view.findViewById(R.id.txt_no_products);

        shortText = "Customer Name: Mr/Mrs. " + customer_name;

        longText = "Thanks for purchase. Visit again";

        if( getArguments()!=null)
        this.salesDetails=(ShopOrderWithProducts) getArguments().getSerializable("salesDetails");
        double totalProductPrice=0.0;
        int totalProductSize=0;
        String currency=context.getString(R.string.currency);
        for (ShopOrderProducts product:salesDetails.getOrderProducts()) {
            totalProductSize+=Integer.parseInt(product.getProduct_qty());
            double subTotalPrice=Double.parseDouble(product.getProduct_price())*Double.parseDouble(product.getProduct_qty());
            totalProductPrice+=subTotalPrice;
        }

        txtTotalPrice.setText(currency+" "+totalProductPrice);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        salesDetailsAdapter = new SalesDetailsAdapter(context);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(salesDetailsAdapter);

        salesDetailsAdapter.setProductList(salesDetails.getOrderProducts());

    }

}