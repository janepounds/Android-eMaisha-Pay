package com.cabral.emaishapay.DailogFragments.shop;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
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
import androidx.fragment.app.FragmentManager;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.database.DbHandlerSingleton;
import com.cabral.emaishapay.fragments.CardListFragment;
import com.cabral.emaishapay.fragments.shop_fragment.ShopProductsFragment;
import com.cabral.emaishapay.models.WalletTransactionResponse;
import com.cabral.emaishapay.singletons.WalletSettingsSingleton;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import es.dmoral.toasty.Toasty;

public class ProductPreviewDialog extends DialogFragment {

    TextView produce_title_txt, product_manufacturer_txt, product_category_txt, product_code_txt, product_sell_price_txt, product_purchase_price_txt, product_stock_txt;
    private Context context;
    private HashMap<String, String>  productData;
    Button delete_button;
    DbHandlerSingleton dbHandler;

   public ProductPreviewDialog(HashMap<String, String> productData){
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

        ImageView close = view.findViewById(R.id.product_close);
        close.setOnClickListener(v -> dismiss());

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call delete method
                dbHandler = DbHandlerSingleton.getHandlerInstance(context);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.want_to_delete_product)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                boolean deleteProduct = dbHandler.deleteProduct(productData.get("unique_id"));

                                if (deleteProduct) {
                                    Toasty.error(context, R.string.product_deleted, Toast.LENGTH_SHORT).show();

                                   //redirect to product List
                                    getActivity().getSupportFragmentManager().popBackStack();

                                    Fragment fragment = new ShopProductsFragment();
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    fragmentManager.beginTransaction()
                                            .hide(((WalletHomeActivity) getActivity()).currentFragment)
                                            .replace(R.id.wallet_home_container, fragment)
                                            .addToBackStack(null).commit();


                                } else {
                                    Toast.makeText(context, R.string.failed, Toast.LENGTH_SHORT).show();
                                }
                                dialog.cancel();

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

        produce_title_txt.setText(this.productData.get("product_name"));
        product_manufacturer_txt.setText(this.productData.get("product_manufacturer"));
        product_category_txt.setText(this.productData.get("product_category"));
        product_code_txt.setText(this.productData.get("product_code"));
        product_sell_price_txt.setText(this.productData.get("product_sell_price"));
        product_purchase_price_txt.setText(  this.productData.get("product_buy_price"));//NumberFormat.getInstance().format()
        product_stock_txt.setText(this.productData.get("product_stock"));

    }



}
