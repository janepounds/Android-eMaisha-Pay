package com.cabral.emaishapay.fragments.shop_fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cabral.emaishapay.AppExecutors;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.MerchantShopActivity;
import com.cabral.emaishapay.adapters.Shop.OrderDetailProductsAdapter;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.databinding.FragmentOnlineOrderDetailsBinding;
import com.cabral.emaishapay.modelviews.ShopOrdersModelView;
import com.cabral.emaishapay.network.api_helpers.BuyInputsAPIClient;
import com.cabral.emaishapay.network.db.entities.MerchantOrder;
import com.cabral.emaishapay.network.db.entities.ShopOrderProducts;

import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OnlineOrderDetailsFragment extends Fragment {

    String order_id, customer_name, order_status, currency, customer_email, customer_cell, customer_address, delivery_fee,payment_method;
    double total_price;
    FragmentOnlineOrderDetailsBinding binding;
    private OrderDetailProductsAdapter onlineOrderDetailsAdapter;
    MerchantOrder shopOrderDetails;
    private ShopOrdersModelView viewModel;
    DialogLoader dialogLoader;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_online_order_details,container,false);


        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbarOrders);
        ((AppCompatActivity)requireActivity()).getSupportActionBar().setHomeButtonEnabled(true); //for back button
        ((AppCompatActivity)requireActivity()). getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        ((AppCompatActivity)requireActivity()).getSupportActionBar().setTitle(R.string.order_details);
        MerchantShopActivity.bottomNavigationView.setVisibility(View.GONE);

        if (getArguments() != null) {
            this.shopOrderDetails= (MerchantOrder) getArguments().getSerializable("order_details");
            order_id =shopOrderDetails.getOrder_id();
            customer_name = shopOrderDetails.getCustomer_name();
            customer_email = shopOrderDetails.getCustomer_email();
            customer_cell = shopOrderDetails.getCustomer_cell();
            customer_address = shopOrderDetails.getCustomer_address();
            delivery_fee = shopOrderDetails.getDelivery_fee();
            order_status = shopOrderDetails.getOrder_status();
            payment_method = shopOrderDetails.getOrder_payment_method();


        }

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(this).get(ShopOrdersModelView.class);
        binding.txtOnlineOrderStatus.setText(order_status);

        if(order_status!=null){
            if(  !order_status.equalsIgnoreCase("Pending")){
                binding.rejectApproveLayout.setVisibility(View.GONE);
                //show delivered layout
                binding.cancelDeliveredLayout.setVisibility(View.VISIBLE);
                binding.txtCancelOnline.setOnClickListener(v -> {
                    //cancel order and initiate refund


                });
                binding.txtDeliveredOnline.setOnClickListener(v -> {
                    //confirm delivered order and initiate payment

                        Call<ResponseBody> call = BuyInputsAPIClient
                                .getInstance()
                                .updateOrderStatus(
                                        order_id,
                                        " ",
                                        5
                                );
                        dialogLoader.showProgressDialog();

                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {

                                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            long updateOrder = viewModel.updateOrder(order_id, "Delivered");

                                            AppExecutors.getInstance().mainThread().execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (updateOrder>0) {
                                                        dialogLoader.hideProgressDialog();
                                                        binding.txtOnlineOrderStatus.setText("Delivered");
                                                        binding.rejectApproveLayout.setVisibility(View.GONE);
                                                        binding.cancelDeliveredLayout.setVisibility(View.GONE);
                                                        dialogLoader.hideProgressDialog();
                                                        MerchantShopActivity.bottomNavigationView.setVisibility(View.GONE);
                                                        binding.txtApproveOnline.setVisibility(View.GONE);
                                                        binding.txtDeliveredOnline.setVisibility(View.GONE);
                                                        Toasty.success(getContext(), "Order Succesfully Delivered", Toast.LENGTH_SHORT).show();
                                                    } else {

                                                        dialogLoader.hideProgressDialog();
                                                        Toasty.error(getContext(), "Order Delivery failed", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });
                                        }
                                    });


                                } else {
                                    dialogLoader.hideProgressDialog();
                                    Toasty.error(getContext(), "Order Delivery failed", Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                dialogLoader.hideProgressDialog();
                                t.printStackTrace();
                                Toasty.error(getContext(), "Order Delivery failed", Toast.LENGTH_SHORT).show();
                            }
                        });

                });


            }else if( order_status.equalsIgnoreCase("Cancel")){
                binding.txtOnlineOrderStatus.setText("Cancelled");
            }
        }


        binding.txtOnlineOrderCustomerName.setText(customer_name);
        if(customer_email==null || customer_email.equalsIgnoreCase("null")){
            binding.emailLayout.setVisibility(View.GONE);
        }
        binding.txtOnlineOrderCustomerEmail.setText(customer_email);
        binding.txtOnlineOrderCustomerPhone.setText(customer_cell);
        binding.txtOnlineDeliveryPrice.setText(delivery_fee);
        binding.txtOnlineOrderId.setText(order_id);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recycler.setLayoutManager(linearLayoutManager); // set LayoutManager to binding.recycler


        binding.recycler.setHasFixedSize(true);

        onlineOrderDetailsAdapter = new OrderDetailProductsAdapter(getContext(), shopOrderDetails.getProducts());

        binding.recycler.setAdapter(onlineOrderDetailsAdapter);


        currency=getString(R.string.currency);

        dialogLoader = new DialogLoader(getContext());
        total_price = calculatTotalOrderPrice(shopOrderDetails.getProducts());

        Double delivery_cost = Double.parseDouble(binding.txtOnlineDeliveryPrice.getText().toString());
        Log.d("Delivery Cost", String.valueOf(delivery_cost));
        Double total = total_price + delivery_cost;

        binding.txtSubTotalPrice.setText(currency + " " + total_price);
        binding.txtOnlineOverallTotalPrice.setText(currency + " " + total);
        binding.paymentMethod.setText(payment_method);

        binding.txtRejectOnline.setOnClickListener(new View.OnClickListener() {
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
                        dialogLoader.showProgressDialog();

                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {

                                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            long updateOrder = viewModel.updateOrder(order_id, "Cancelled");

                                                    AppExecutors.getInstance().mainThread().execute(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (updateOrder>0) {
                                                                dialogLoader.hideProgressDialog();
                                                                binding.txtOnlineOrderStatus.setText("Cancelled");
                                                                binding.rejectApproveLayout.setVisibility(View.GONE);
                                                                alertDialog.cancel();
                                                                MerchantShopActivity.bottomNavigationView.setVisibility(View.GONE);
                                                                Toasty.success(getContext(), "Order Successfully Rejected", Toast.LENGTH_SHORT).show();
                                                            } else {

                                                                dialogLoader.hideProgressDialog();
                                                                Toasty.error(getContext(), "Order Rejection failed", Toast.LENGTH_SHORT).show();

                                                            }
                                                        }
                                                    });
                                        }
                                    });


                                } else {
                                    dialogLoader.hideProgressDialog();
                                    Toasty.error(getContext(), "Order Rejection failed", Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                dialogLoader.hideProgressDialog();
                                t.printStackTrace();
                                Toasty.error(getContext(), "Order Rejection failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });


        binding.txtApproveOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<ResponseBody> call = BuyInputsAPIClient
                        .getInstance()
                        .updateOrderStatus(
                                order_id,
                                " ",
                                2
                        );
                dialogLoader.showProgressDialog();

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {

                            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    long updateOrder = viewModel.updateOrder(order_id, "Approved");

                                    AppExecutors.getInstance().mainThread().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (updateOrder>0) {
                                                dialogLoader.hideProgressDialog();
                                                binding.txtOnlineOrderStatus.setText("Approved");
                                                binding.rejectApproveLayout.setVisibility(View.GONE);
                                                dialogLoader.hideProgressDialog();
                                                MerchantShopActivity.bottomNavigationView.setVisibility(View.GONE);
                                                binding.txtApproveOnline.setVisibility(View.GONE);
                                                Toasty.success(getContext(), "Order Succesfully Approved", Toast.LENGTH_SHORT).show();
                                            } else {

                                                dialogLoader.hideProgressDialog();
                                                Toasty.error(getContext(), "Order Approval failed", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                                }
                            });


                        } else {
                            dialogLoader.hideProgressDialog();
                            Toasty.error(getContext(), "Order Approval failed", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        dialogLoader.hideProgressDialog();
                        t.printStackTrace();
                        Toasty.error(getContext(), "Order Approval failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private double calculatTotalOrderPrice(List<ShopOrderProducts> products) {
        double total=0;
        for (ShopOrderProducts product:products) {
            total+=Double.parseDouble(product.getProduct_price());
        }
        return total;
    }

    @Override
    public void onResume() {
        MerchantShopActivity.bottomNavigationView.setVisibility(View.GONE);
        super.onResume();
    }
}