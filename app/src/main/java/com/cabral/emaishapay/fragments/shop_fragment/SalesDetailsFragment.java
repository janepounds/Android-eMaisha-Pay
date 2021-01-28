package com.cabral.emaishapay.fragments.shop_fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.adapters.Shop.SalesDetailsAdapter;
import com.cabral.emaishapay.database.DbHandlerSingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;


public class SalesDetailsFragment extends Fragment {

    ImageView imgNoProduct;
    TextView txtNoProducts, txtTotalPrice, txtPdfReceipt;
    String order_id, order_date, order_time, customer_name;
    double total_price;
    String longText, shortText, order_status, storage_status;
    String currency;
    private RecyclerView recyclerView;
    private SalesDetailsAdapter salesDetailsAdapter;
    //how many headers or column you need, add here by using ,
    //headers and get clients para meter must be equal
    private String[] header = {"Description", "Price"};

    FragmentManager fm;
    Activity shop;
    private Context context;
    Toolbar toolbar;
    private DbHandlerSingleton dbHandler;

    public SalesDetailsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_sales_details, container, false);
        toolbar = view.findViewById(R.id.toolbar_sales_details);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Products");
        dbHandler = DbHandlerSingleton.getHandlerInstance(getContext());

        recyclerView = view.findViewById(R.id.recycler);
        imgNoProduct = view.findViewById(R.id.image_no_product);
        txtTotalPrice = view.findViewById(R.id.txt_total_price);



        txtNoProducts = view.findViewById(R.id.txt_no_products);
//        order_id = getIntent().getExtras().getString("order_id");
//        order_date = getIntent().getExtras().getString("order_date");
//        order_time = getIntent().getExtras().getString("order_time");
//        customer_name = getIntent().getExtras().getString("customer_name");
//        order_status = getIntent().getExtras().getString("order_status");
//        storage_status = getIntent().getExtras().getString("storage_status");

        imgNoProduct.setVisibility(View.GONE);
        txtNoProducts.setVisibility(View.GONE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
        recyclerView.setHasFixedSize(true);




        //get data from local database
        List<HashMap<String, String>> orderDetailsList;
        orderDetailsList = dbHandler.getOrderDetailsList(order_id);

        if (orderDetailsList.size() <= 0) {
            //if no data in local db, then load data from server
            Toasty.info(context, "No Data Found", Toast.LENGTH_SHORT).show();
        } else {
            salesDetailsAdapter = new SalesDetailsAdapter(context, orderDetailsList);

            recyclerView.setAdapter(salesDetailsAdapter);


        }



        //get data from local database
        List<HashMap<String, String>> shopData;
        shopData = dbHandler.getShopInformation();

        String shop_name = shopData.get(0).get("shop_name");
        String shop_contact = shopData.get(0).get("shop_contact");
        String shop_email = shopData.get(0).get("shop_email");
        String shop_address = shopData.get(0).get("shop_address");
        currency = shopData.get(0).get("shop_currency");


        total_price = dbHandler.getTotalOrderPrice(order_id);
        txtTotalPrice.setText(currency + total_price);


        //for pdf report

        shortText = "Customer Name: Mr/Mrs. " + customer_name;

        longText = "Thanks for purchase. Visit again";




        return view;
    }



}