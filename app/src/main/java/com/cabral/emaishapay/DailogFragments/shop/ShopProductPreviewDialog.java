package com.cabral.emaishapay.DailogFragments.shop;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.cabral.emaishapay.R;

import com.cabral.emaishapay.modelviews.ShopProductsModelView;

import com.cabral.emaishapay.network.db.entities.EcManufacturer;
import com.cabral.emaishapay.network.db.entities.EcProduct;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import java.util.List;


public class ShopProductPreviewDialog extends DialogFragment {
    private static final String TAG = "ProductPreviewDialog";
    TextView produce_title_txt, product_manufacturer_txt, product_category_txt, product_code_txt, product_sell_price_txt, product_purchase_price_txt, product_stock_txt;
    private Context context;
    private EcProduct  productData;
    Button delete_button,update_button;
    ShopProductsModelView viewModel;
    private List<EcManufacturer> manufacturers=new ArrayList<>();

    public ShopProductPreviewDialog(EcProduct productData, ShopProductsModelView viewModel) {
       this.viewModel=viewModel;
        this.productData =productData;
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.product_details, null);
        builder.setView(view);

        initializeView(view);
        return builder.create();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context =context;
    }
    public void initializeView(View view){

        produce_title_txt = view.findViewById(R.id.produce_title);
        product_manufacturer_txt = view.findViewById(R.id.product_manufacturer);
        product_category_txt = view.findViewById(R.id.product_category);
        product_code_txt = view.findViewById(R.id.product_code);
        product_sell_price_txt = view.findViewById(R.id.product_sell_price);
        product_purchase_price_txt = view.findViewById(R.id.product_purchase_price);
        product_stock_txt = view.findViewById(R.id.product_stock);
        delete_button = view.findViewById(R.id.btn_delete);
        update_button = view.findViewById(R.id.btn_edit);

        ImageView close = view.findViewById(R.id.product_close);
        close.setOnClickListener(v -> dismiss());

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call delete method

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.want_to_delete_product)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                viewModel.deleteProduct(productData);
                                ShopProductPreviewDialog.this.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Perform Your Task Here--When No is pressed
                                dialog.cancel();
                            }
                        }).show();


            }
        });

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear current dialog
                ShopProductPreviewDialog.this.dismiss();

                //call product dialog
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                DialogFragment addProductDialog = new AddShopProductFragment(manufacturers,getString(R.string.update_product),viewModel);
                Bundle bundle = new Bundle();
                bundle.putString("manufacturer",productData.getManufacturer());
                bundle.putString("category",productData.getProduct_category());
                bundle.putString("product_name",productData.getProduct_name());
                bundle.putString("product_code",productData.getProduct_code());
                bundle.putString("sell_price",productData.getProduct_sell_price());
                bundle.putString("buy_price",productData.getProduct_buy_price());
                bundle.putString("stock",productData.getProduct_stock());
                bundle.putString("weight",productData.getProduct_weight());
                bundle.putString("weight_unit",productData.getProduct_weight_unit());
                bundle.putString("image",productData.getProduct_image());
                bundle.putString("product_id",productData.getProduct_id());
                addProductDialog.setArguments(bundle);
                addProductDialog.show(ft, "dialog");



            }
        });



        produce_title_txt.setText(this.productData.getProduct_name());
        product_manufacturer_txt.setText(this.productData.getManufacturer());
        product_category_txt.setText(this.productData.getProduct_category());
        product_code_txt.setText(this.productData.getProduct_code());
        product_sell_price_txt.setText(this.productData.getProduct_sell_price());
        product_purchase_price_txt.setText(  this.productData.getProduct_buy_price());//NumberFormat.getInstance().format()
        product_stock_txt.setText(this.productData.getProduct_stock());

    }

//    private void subscribeToManufacturer(LiveData<List<EcManufacturer>> onlineManufacturers) {
//        onlineManufacturers.observe(getViewLifecycleOwner(), myManfacturers->{
//            dialogLoader.showProgressDialog();
//            if(myManfacturers!=null && myManfacturers.size()!=0){
//
//                saveManufacturersList(myManfacturers);
//                if(productAdapter!=null){
//                    productAdapter.notifyDataSetChanged();
//                }
//                dialogLoader.hideProgressDialog();
//
//            }
//        });
//    }
//
//
//    public void saveManufacturersList(List<EcManufacturer> manufacturers) {
//        this.manufacturers = manufacturers;
//    }



}
