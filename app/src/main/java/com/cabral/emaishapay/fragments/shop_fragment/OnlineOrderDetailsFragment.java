package com.cabral.emaishapay.fragments.shop_fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.ShopActivity;
import com.cabral.emaishapay.adapters.Shop.OnlineOrderDetailsAdapter;
import com.cabral.emaishapay.database.DatabaseAccess;
import com.cabral.emaishapay.network.BuyInputsAPIClient;

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OnlineOrderDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnlineOrderDetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    String order_id, customer_name, order_status, currency, customer_email, customer_cell, customer_address, delivery_fee;
    double total_price;
    Toolbar toolbar;
    LinearLayout email_layout,reject_approve_layout;
    TextView txtSubTotal, txtCustomerName, txtOrderStatus, txtApprove, txtReject, txtDelivery, txtCustomerPhone, txtCustomerEmail, txtOverallTotal;
    private RecyclerView recyclerView;
    private OnlineOrderDetailsAdapter onlineOrderDetailsAdapter;


    public OnlineOrderDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OnlineOrderDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OnlineOrderDetailsFragment newInstance(String param1, String param2) {
        OnlineOrderDetailsFragment fragment = new OnlineOrderDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            order_id = getArguments().getString("order_id");
            customer_name = getArguments().getString("customer_name");
            customer_email = getArguments().getString("customer_email");
            customer_cell = getArguments().getString("customer_cell");
            customer_address = getArguments().getString("customer_address");
            delivery_fee = getArguments().getString("delivery_fee");
            order_status = getArguments().getString("order_status");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_online_order_details, container, false);
        toolbar = view.findViewById(R.id.toolbar_orders);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)requireActivity()).getSupportActionBar().setHomeButtonEnabled(true); //for back button
        ((AppCompatActivity)requireActivity()). getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        ((AppCompatActivity)requireActivity()).getSupportActionBar().setTitle(R.string.order_details);
        ShopActivity.bottomNavigationView.setVisibility(View.GONE);
        recyclerView = view.findViewById(R.id.recycler);
        txtSubTotal = view.findViewById(R.id.txt_online_total_price);
        txtOrderStatus = view.findViewById(R.id.txt_online_order_status);
        txtCustomerName = view.findViewById(R.id.txt_online_order_customer_name);
        txtApprove = view.findViewById(R.id.txt_approve_online);
        txtDelivery = view.findViewById(R.id.txt_online_delivery_price);
        txtCustomerPhone = view.findViewById(R.id.txt_online_order_customer_phone);
        txtCustomerEmail = view.findViewById(R.id.txt_online_order_customer_email);
        txtOverallTotal = view.findViewById(R.id.txt_online_overall_total_price);
        txtReject = view.findViewById(R.id.txt_reject_online);
        email_layout= view.findViewById(R.id.email_layout);
        reject_approve_layout= view.findViewById(R.id.reject_approve_layout);


        txtOrderStatus.setText(order_status);
        if(!order_status.equalsIgnoreCase("Pending")){
            reject_approve_layout.setVisibility(View.GONE);
        }else if(order_status.equalsIgnoreCase("Cancel")){
            txtOrderStatus.setText("Cancelled");
        }
        txtCustomerName.setText(customer_name);
        if(customer_email==null || customer_email.equalsIgnoreCase("null")){
            email_layout.setVisibility(View.GONE);
        }
        txtCustomerEmail.setText(customer_email);
        txtCustomerPhone.setText(customer_cell);
        txtDelivery.setText(delivery_fee);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView


        recyclerView.setHasFixedSize(true);

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getContext());
        databaseAccess.open();


        //get data from local database
        List<HashMap<String, String>> orderDetailsList;
        orderDetailsList = databaseAccess.getOrderDetailsList(order_id);
        Log.d("Order List", String.valueOf(orderDetailsList));

        onlineOrderDetailsAdapter = new OnlineOrderDetailsAdapter(getContext(), orderDetailsList);

        recyclerView.setAdapter(onlineOrderDetailsAdapter);

        databaseAccess.open();
        //get data from local database
        List<HashMap<String, String>> shopData;
        shopData = databaseAccess.getShopInformation();
        currency=getString(R.string.currency);
        if(shopData.size()>0)
         currency = shopData.get(0).get("shop_currency");


        databaseAccess.open();
        total_price = databaseAccess.totalOrderPrice(order_id);
        Double delivery_cost = Double.parseDouble(txtDelivery.getText().toString());
        Log.d("Delivery Cost", String.valueOf(delivery_cost));
        Double total = total_price + delivery_cost;
        txtSubTotal.setText(currency + " " + total_price);
        txtOverallTotal.setText(currency + " " + total);

        txtReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                ViewGroup viewGroup = view.findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.custom_reject_order_dialog, viewGroup, false);
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                EditText input = dialogView.findViewById(R.id.dialog_reject_comment);
                TextView txt_reject_order = dialogView.findViewById(R.id.custom_reject_order);
                TextView txt_cancel_order = dialogView.findViewById(R.id.custom_reject_cancel);

                txt_cancel_order.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });

                txt_reject_order.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Call<ResponseBody> call = BuyInputsAPIClient
                                .getInstance()
                                .updateOrderStatus(
                                        order_id,
                                        input.getText().toString().trim(),
                                        3
                                );
                        ProgressDialog progressDialog = new ProgressDialog(getContext());
                        progressDialog.setMessage("Loading...");
                        progressDialog.setTitle("Please Wait");
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.show();

                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {

                                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getContext());
                                    databaseAccess.open();
                                    boolean check = databaseAccess.updateOrder(order_id, "Cancelled");
                                    txtOrderStatus.setText("Cancelled");
                                    reject_approve_layout.setVisibility(View.GONE);
                                    alertDialog.cancel();
                                    if (check) {
                                        progressDialog.dismiss();
                                        ShopActivity.bottomNavigationView.setVisibility(View.GONE);
                                        Toasty.success(getContext(), "Order Successfully Rejected", Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressDialog.dismiss();
                                        Toasty.error(getContext(), "Order Rejection failed", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    progressDialog.dismiss();
                                    Toasty.error(getContext(), "Order Rejection failed", Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                progressDialog.dismiss();
                                t.printStackTrace();
                                Toasty.error(getContext(), "Order Rejection failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });


        txtApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<ResponseBody> call = BuyInputsAPIClient
                        .getInstance()
                        .updateOrderStatus(
                                order_id,
                                " ",
                                2
                        );
                ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Loading...");
                progressDialog.setTitle("Please Wait");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {

                            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getContext());
                            databaseAccess.open();
                            boolean check = databaseAccess.updateOrder(order_id, "Completed");
                            txtOrderStatus.setText("Completed");
                            reject_approve_layout.setVisibility(View.GONE);
                            if (check) {
                                progressDialog.dismiss();
                                ShopActivity.bottomNavigationView.setVisibility(View.GONE);
                                txtApprove.setVisibility(View.GONE);
                                Toasty.success(getContext(), "Order Succesfully Approved", Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                Toasty.error(getContext(), "Order Approval failed", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            progressDialog.dismiss();
                            Toasty.error(getContext(), "Order Approval failed", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        progressDialog.dismiss();
                        t.printStackTrace();
                        Toasty.error(getContext(), "Order Approval failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResume() {
        ShopActivity.bottomNavigationView.setVisibility(View.GONE);
        super.onResume();
    }
}