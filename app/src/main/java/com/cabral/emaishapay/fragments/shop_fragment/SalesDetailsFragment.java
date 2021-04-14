package com.cabral.emaishapay.fragments.shop_fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.adapters.Shop.ProductAdapter;
import com.cabral.emaishapay.adapters.Shop.SalesDetailsAdapter;
import com.cabral.emaishapay.database.DbHandlerSingleton;
import com.cabral.emaishapay.modelviews.ShopOrdersModelView;
import com.cabral.emaishapay.modelviews.ShopProductsModelView;
import com.cabral.emaishapay.modelviews.ShopSalesModelView;
import com.cabral.emaishapay.network.db.entities.ShopOrderProducts;
import com.cabral.emaishapay.utils.Resource;

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

    private Context context;
    Toolbar toolbar;
    private ShopSalesModelView viewModel;

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

        recyclerView = view.findViewById(R.id.recycler);
        imgNoProduct = view.findViewById(R.id.image_no_product);
        txtTotalPrice = view.findViewById(R.id.txt_total_price);
        viewModel = new ViewModelProvider(this).get(ShopSalesModelView.class);


        txtNoProducts = view.findViewById(R.id.txt_no_products);



//        currency = shopData.get(0).get("shop_currency");
//        total_price = dbHandler.getTotalOrderPrice(order_id);
//        txtTotalPrice.setText(currency + total_price);


        //for pdf report

        shortText = "Customer Name: Mr/Mrs. " + customer_name;

        longText = "Thanks for purchase. Visit again";




        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        salesDetailsAdapter = new SalesDetailsAdapter(context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView
        recyclerView.setHasFixedSize(true);
        subscribeToOrderDetailsList(viewModel.getOrderDetailsList());
    }

    private void subscribeToOrderDetailsList(LiveData<List<ShopOrderProducts>> orderDetails) {
        imgNoProduct.setVisibility(View.GONE);
        txtNoProducts.setVisibility(View.GONE);

        orderDetails.observe(this.getViewLifecycleOwner(), orderdetails->{
            // dialogLoader.showProgressDialog();

            if(orderdetails!=null && orderdetails.size()>0){

                salesDetailsAdapter.setProductList( orderdetails);

                //dialogLoader.hideProgressDialog();
            }else {;

                Toasty.info(context, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        });
    }
}