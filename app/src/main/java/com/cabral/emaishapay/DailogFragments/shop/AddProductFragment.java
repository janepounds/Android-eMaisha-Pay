package com.cabral.emaishapay.DailogFragments.shop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import es.dmoral.toasty.Toasty;
import in.mayanknagwanshi.imagepicker.ImageSelectActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.ShopActivity;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.database.DbHandlerSingleton;
import com.cabral.emaishapay.models.shop_model.CategoriesResponse;
import com.cabral.emaishapay.models.shop_model.Category;
import com.cabral.emaishapay.models.shop_model.Manufacturer;
import com.cabral.emaishapay.models.shop_model.ManufacturersResponse;
import com.cabral.emaishapay.models.shop_model.Product;
import com.cabral.emaishapay.models.shop_model.ProductResponse;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.BuyInputsAPIClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class AddProductFragment extends DialogFragment {
    private static final String TAG = "AddProductFragment";

    Context context;
    public static EditText etxtProductCode;
    EditText  etxtProductBuyPrice, etxtProductSellPrice, etxtProductStock, etxtProductSupplier;
    TextView txtAddProdcut;
    TextView  etxtProductName, etxtProductCategory,etxtProductManufucturer;
    String mediaPath, encodedImage = "N/A";
    Spinner quantityUnit;
    ArrayAdapter<String> categoryAdapter, supplierAdapter, productAdapter, manufacturersAdapter;
    List<String> categoryNames, supplierNames, weightUnitNames;
    Integer selectedCategoryID;
    Integer selectedManufacturersID;
    Integer selectedProductID;
    String selectedSupplierID;

    String selectectedCategoryName, selectedProductName, selectedManufacturerName;
    private List<Category> categories;
    private List<Product> products;
    private List<String> catNames;
    private List<Manufacturer> manufacturers;
    private List<String> productNames;
    private List<String> manufacturersNames;
    private List<String> offlinemanufacturersNames;
    private List<String> offlineCategoryNames;
    private List<String> offlineProductsName;
    private String measure_id;
    private DbHandlerSingleton dbHandler;
    private ImageView produce_image;
    private ArrayList<HashMap<String, String>>offlineManufacturers;
    private ArrayList<HashMap<String, String>>offlineCategories;
    private ArrayList<HashMap<String, String>>offlineProductNames;
    DialogLoader dialogLoader;



    public AddProductFragment(List<Manufacturer> manufacturers) {
        this.manufacturers=manufacturers;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialogLoader = new DialogLoader(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.fragment_add_product, null);
        dbHandler = DbHandlerSingleton.getHandlerInstance(context);
        etxtProductName = view.findViewById(R.id.etxt_product_name);
        etxtProductCode = view.findViewById(R.id.etxt_product_code);
        etxtProductCategory = view.findViewById(R.id.etxt_product_category);
        etxtProductBuyPrice = view.findViewById(R.id.etxt_buy_price);
        etxtProductSellPrice = view.findViewById(R.id.etxt_product_sell_price);
        etxtProductStock = view.findViewById(R.id.etxt_product_stock);
        etxtProductSupplier = view.findViewById(R.id.etxt_supplier);
        etxtProductManufucturer = view.findViewById(R.id.etxt_product_manufucturer);
        quantityUnit = view.findViewById(R.id.product_units);
        TextView quantitySellUnit = view.findViewById(R.id.txt_selling_units);
        TextView quantityPurchaseUnit = view.findViewById(R.id.txt_purchase_units);
        produce_image = view.findViewById(R.id.product_image);
        txtAddProdcut = view.findViewById(R.id.tx_add_product);
        ImageView close = view.findViewById(R.id.add_product_close);
        close.setOnClickListener(v -> dismiss());



        quantityUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem=quantityUnit.getSelectedItem().toString();

                if(position==0){
                    quantitySellUnit.setText("/unit");
                    quantityPurchaseUnit.setText("/unit");

                }
                else if(position==1){
                    quantitySellUnit.setText("/kg");
                    quantityPurchaseUnit.setText("/kg");
                }
                else if(position==2){
                    quantitySellUnit.setText("/box");
                    quantityPurchaseUnit.setText("/box");
                }
                else if(position==3){
                    quantitySellUnit.setText("/ltr");
                    quantityPurchaseUnit.setText("/ltr");
                }
                else if(position==4){
                    quantitySellUnit.setText("/tonne");
                    quantityPurchaseUnit.setText("/tonne");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //get offline manufacturers;
        offlineManufacturers = new ArrayList<>();
        offlineManufacturers = dbHandler.getOfflineManufacturers();
        //get offline product categories
        offlineCategories = new ArrayList<>();
        offlineCategories = dbHandler.getOfflineProductCategories();
        //get offline product names
        offlineProductNames = new ArrayList<>();
        offlineProductNames = dbHandler.getOfflineProductNames();
        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;

        Call<CategoriesResponse> call = BuyInputsAPIClient
                .getInstance()
                .getCategories(access_token);
        call.enqueue(new Callback<CategoriesResponse>() {
            @Override
            public void onResponse(Call<CategoriesResponse> call, Response<CategoriesResponse> response) {
                if (response.isSuccessful()) {
                    categories = response.body().getCategories();
                    saveList(categories);
                    Log.d("Categories", String.valueOf(categories));

                } else {
                    Log.d("Failed", "Categories failed");
                }
            }

            @Override
            public void onFailure(Call<CategoriesResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("Failed", "Categories failed");

            }
        });


        categoryNames = new ArrayList<>();
        supplierNames = new ArrayList<>();
        weightUnitNames = new ArrayList<>();


        produce_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        //get data from local database
        final List<HashMap<String, String>> productCategory, productSupplier, weightUnit;
        productCategory = dbHandler.getProductCategory();

        //need to open database in every query to get data from local db
        productSupplier = dbHandler.getProductSupplier();


        //need to open database in every query to get data from local db
        weightUnit = dbHandler.getWeightUnit();

        for (int i = 0; i < productCategory.size(); i++) {
            // Get the ID of selected Country
            categoryNames.add(productCategory.get(i).get("category_name"));

        }

        for (int i = 0; i < productSupplier.size(); i++) {
            // Get the ID of selected supplier
            supplierNames.add(productSupplier.get(i).get("suppliers_name"));

        }

        for (int i = 0; i < weightUnit.size(); i++) {
            // Get the ID of selected weight unit
            weightUnitNames.add(weightUnit.get(i).get("weight_unit"));

        }

        etxtProductManufucturer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Categories", String.valueOf(categories));
                manufacturersNames = new ArrayList<>();
                if(manufacturers!=null) {
                    for (int i = 0; i < manufacturers.size(); i++) {
                        manufacturersNames.add(manufacturers.get(i).getManufacturer_name());

                    }
                }

                manufacturersAdapter = new ArrayAdapter<String>(getContext(), R.layout.list_row);
                manufacturersAdapter.addAll(manufacturersNames);

                //add offline manufacturers
                offlinemanufacturersNames = new ArrayList<>();

                for(int i=0;i<offlineManufacturers.size();i++){
                    String manufacturer = offlineManufacturers.get(i).get("manufacturer_name");
                    offlinemanufacturersNames.add(manufacturer);

                }
                manufacturersAdapter.addAll(offlinemanufacturersNames);

                Log.d(TAG, "onClick: offline manufacturers"+offlineManufacturers);

                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialog_button = dialogView.findViewById(R.id.dialog_button);
                EditText dialog_input = dialogView.findViewById(R.id.dialog_input);
                TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
                ListView dialog_list = dialogView.findViewById(R.id.dialog_list);
                TextView dialog_add_btn = dialogView.findViewById(R.id.tv_add_new_item);
                EditText dialog_add_edit_text = dialogView.findViewById(R.id.et_add_new_item);
                Button update = dialogView.findViewById(R.id.button_update);


                dialog_title.setText("Manufacturers");
                dialog_list.setVerticalScrollBarEnabled(true);
                dialog_list.setAdapter(manufacturersAdapter);

                dialog_add_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_add_edit_text.setVisibility(View.VISIBLE);
                    }
                });

                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!dialog_add_edit_text.getText().toString().isEmpty()){
                            boolean check = dbHandler.addManufacturers(dialog_add_edit_text.getText().toString());
                            if(check){

                                manufacturersAdapter.add(dialog_add_edit_text.getText().toString());
                                manufacturersAdapter.notifyDataSetChanged();
                                dialog_add_edit_text.getText().clear();
                            }else{
                                Toast.makeText(context,"Failed to update",Toast.LENGTH_LONG);
                            }

                        }
                    }
                });
                dialog_input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        manufacturersAdapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });


                final AlertDialog alertDialog = dialog.create();

                dialog_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();


                dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        alertDialog.dismiss();
                        final String selectedItem = manufacturersAdapter.getItem(position);

                        Integer manufacturers_id = 0;
                        String manufacturers_name = "";
                        etxtProductManufucturer.setText(selectedItem);


                        for (int i = 0; i < manufacturersNames.size(); i++) {
                            if (manufacturersNames.get(i).equalsIgnoreCase(selectedItem)) {
                                // Get the ID of selected Country
                                manufacturers_id = manufacturers.get(i).getManufacturers_id();
                                manufacturers_name = manufacturers.get(i).getManufacturer_name();
                            }
                        }


                        selectedManufacturersID = manufacturers_id;
                        selectedManufacturerName = manufacturers_name;

                        Log.d("Manufucturer_id", String.valueOf(manufacturers_id));
                    }
                });


            }
        });

        etxtProductCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Categories", String.valueOf(categories));
                catNames = new ArrayList<>();
                if (validateManufacturer()) {
                    if(categories!=null) {
                        for (int i = 0; i < categories.size(); i++) {
                            catNames.add(categories.get(i).getCategories_slug());
                        }
                    }

                    categoryAdapter = new ArrayAdapter<String>(getContext(), R.layout.list_row);
                    categoryAdapter.addAll(catNames);
                    //add offline categories
                    offlineCategoryNames = new ArrayList<>();
                    for(int i=0;i<offlineCategories.size();i++){
                        String category_name = offlineCategories.get(i).get("category_name");
                        offlineCategoryNames.add(category_name);
                    }
                    categoryAdapter.addAll(offlineCategoryNames);

                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                    dialog.setView(dialogView);
                    dialog.setCancelable(false);

                    Button dialog_button = dialogView.findViewById(R.id.dialog_button);
                    EditText dialog_input = dialogView.findViewById(R.id.dialog_input);
                    TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
                    ListView dialog_list = dialogView.findViewById(R.id.dialog_list);
                    TextView dialog_add_btn = dialogView.findViewById(R.id.tv_add_new_item);
                    EditText dialog_add_edit_text = dialogView.findViewById(R.id.et_add_new_item);
                    Button update = dialogView.findViewById(R.id.button_update);

                    dialog_title.setText(R.string.product_category);
                    dialog_list.setVerticalScrollBarEnabled(true);
                    dialog_list.setAdapter(categoryAdapter);

                    dialog_add_btn.setText("Add New Category");
                    dialog_add_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog_add_edit_text.setVisibility(View.VISIBLE);
                        }
                    });

                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!dialog_add_edit_text.getText().toString().isEmpty()){
                                boolean check = dbHandler.addProductCategory(dialog_add_edit_text.getText().toString());
                                if(check){

                                    categoryAdapter.add(dialog_add_edit_text.getText().toString());
                                    categoryAdapter.notifyDataSetChanged();
                                    dialog_add_edit_text.getText().clear();
                                }else{
                                    Toast.makeText(context,"Failed to update",Toast.LENGTH_LONG);
                                }

                            }
                        }
                    });

                    dialog_input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                            categoryAdapter.getFilter().filter(charSequence);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });


                    final AlertDialog alertDialog = dialog.create();

                    dialog_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();


                    dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            alertDialog.dismiss();
                            final String selectedItem = categoryAdapter.getItem(position);

                            Integer category_id = 0;
                            String category_name = "";
                            etxtProductCategory.setText(selectedItem);


                            for (int i = 0; i < catNames.size(); i++) {
                                if (catNames.get(i).equalsIgnoreCase(selectedItem)) {
                                    // Get the ID of selected Country
                                    category_id = categories.get(i).getCategories_id();
                                    category_name = categories.get(i).getCategories_slug();
                                }
                            }

                            dialogLoader.showProgressDialog();
                            String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
                            Call<ProductResponse> call = BuyInputsAPIClient
                                    .getInstance()
                                    .getProducts(
                                            access_token,
                                            category_id,
                                            selectedManufacturersID
                                    );
                            call.enqueue(new Callback<ProductResponse>() {
                                @Override
                                public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                                    if (response.isSuccessful()) {
                                        products = response.body().getProducts();
                                        savePtdList(products);
                                        Log.d("Products", String.valueOf(products));
                                    } else {
                                        Log.d("Product Fetch", "Product Fetch failed");
                                    }
                                    dialogLoader.hideProgressDialog();
                                }

                                @Override
                                public void onFailure(Call<ProductResponse> call, Throwable t) {
                                    t.printStackTrace();
                                    dialogLoader.hideProgressDialog();
                                }
                            });


                            selectedCategoryID = category_id;
                            selectectedCategoryName = category_name;

                            Log.d("category_id", String.valueOf(category_id));
                        }
                    });
                }
            }
        });

        etxtProductName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productNames = new ArrayList<>();
                Log.d("Products", String.valueOf(products));
                if (validateProductCategory()) {
                    if(products!=null) {
                        for (int i = 0; i < products.size(); i++) {
                            productNames.add(products.get(i).getProducts_name() + " " + products.get(i).getProducts_weight() + products.get(i).getProducts_weight_unit());
                            measure_id = products.get(i).getMeasure_id();
                        }
                    }

                    productAdapter = new ArrayAdapter<String>(getContext(), R.layout.list_row);
                    productAdapter.addAll(productNames);
                    //add offline product names
                    offlineProductsName = new ArrayList<>();
                    for(int i=0;i<offlineProductNames.size();i++){
                        String product_name = offlineProductNames.get(i).get("product_name");
                        offlineProductsName.add(product_name);
                    }
                    productAdapter.addAll(offlineProductsName);

                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                    dialog.setView(dialogView);
                    dialog.setCancelable(false);

                    Button dialog_button = dialogView.findViewById(R.id.dialog_button);
                    EditText dialog_input = dialogView.findViewById(R.id.dialog_input);
                    TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
                    ListView dialog_list = dialogView.findViewById(R.id.dialog_list);
                    TextView dialog_add_btn = dialogView.findViewById(R.id.tv_add_new_item);
                    Button dialog_update_button = dialogView.findViewById(R.id.button_update);
                    EditText add_product = dialogView.findViewById(R.id.et_add_new_item);



                    dialog_title.setText("Products");
                    dialog_list.setVerticalScrollBarEnabled(true);
                    dialog_list.setAdapter(productAdapter);
                    dialog_add_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            add_product.setVisibility(View.VISIBLE);

                        }
                    });

                    dialog_update_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!add_product.getText().toString().isEmpty()){
                                boolean check = dbHandler.addProductName(add_product.getText().toString());
                                if(check){
                                    productAdapter.add(add_product.getText().toString());
                                    productAdapter.notifyDataSetChanged();
                                    add_product.getText().clear();
                                }else{
                                    Toast.makeText(context,"Failed to update",Toast.LENGTH_LONG);
                                }

                            }
                        }
                    });


                    dialog_input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                            productAdapter.getFilter().filter(charSequence);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });


                    final AlertDialog alertDialog = dialog.create();

                    dialog_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();


                    dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            alertDialog.dismiss();
                            final String selectedItem = productAdapter.getItem(position);

                            Integer product_id = 0;
                            String product_name = "";
                            etxtProductName.setText(selectedItem);


                            for (int i = 0; i < productNames.size(); i++) {
                                if (productNames.get(i).equalsIgnoreCase(selectedItem)) {
                                    // Get the ID of selected Country
                                    product_id = products.get(i).getProducts_id();
                                    product_name = products.get(i).getProducts_name()+ " "+ products.get(i).getProducts_weight()+ products.get(i).getProducts_weight_unit();
                                }
                            }

                            selectedProductID = product_id;
                            selectedProductName = product_name;
                            Log.d("Product ID", String.valueOf(product_id));
                        }
                    });
                }
            }
        });


        etxtProductSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supplierAdapter = new ArrayAdapter<String>(getContext(), R.layout.list_row);
                supplierAdapter.addAll(supplierNames);

                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialog_button = (Button) dialogView.findViewById(R.id.dialog_button);
                EditText dialog_input = (EditText) dialogView.findViewById(R.id.dialog_input);
                TextView dialog_title = (TextView) dialogView.findViewById(R.id.dialog_title);
                ListView dialog_list = (ListView) dialogView.findViewById(R.id.dialog_list);

//                dialog_title.setText(getString(R.string.zone));
                dialog_title.setText(R.string.suppliers);
                dialog_list.setVerticalScrollBarEnabled(true);
                dialog_list.setAdapter(supplierAdapter);

                dialog_input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                        supplierAdapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });


                final AlertDialog alertDialog = dialog.create();

                dialog_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();


                dialog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        alertDialog.dismiss();
                        final String selectedItem = supplierAdapter.getItem(position);

                        String supplier_id = "0";
                        etxtProductSupplier.setText(selectedItem);


                        for (int i = 0; i < supplierNames.size(); i++) {
                            if (supplierNames.get(i).equalsIgnoreCase(selectedItem)) {
                                // Get the ID of selected Country
                                supplier_id = productSupplier.get(i).get("suppliers_id");
                            }
                        }


                        selectedSupplierID = supplier_id;

                    }
                });
            }
        });


        txtAddProdcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toasty.warning(AddProductActivity.this, "Add Product is disable in demo version. Please purchase from Codecanyon.Thank you ", Toast.LENGTH_SHORT).show();

                String userId = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String unique_id = userId.replaceAll(" ", "") + "PDT" + timestamp.toString().replaceAll(" ", "");
                Integer product_id = selectedProductID;
                String product_name = etxtProductName.getText().toString().trim();
                String product_code = etxtProductCode.getText().toString().trim();
                String product_category_name = etxtProductCategory.getText().toString().trim();
                String product_category_id = selectedCategoryID + "";
                String product_buy_price = etxtProductBuyPrice.getText().toString().trim();
                String product_sell_price = etxtProductSellPrice.getText().toString().trim();
                String product_stock = etxtProductStock.getText().toString().trim();
                String product_supplier_name = etxtProductSupplier.getText().toString().trim();
                String product_supplier = selectedSupplierID;
                String manufacturer_name = etxtProductManufucturer.getText().toString().trim();
                String units =  quantityUnit.getSelectedItem().toString().trim();
                String sync_status = "0";

                Log.d(TAG, "onClick: timestamp"+timestamp);


                String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
                if (product_name == null || product_name.isEmpty()) {
                    etxtProductName.setError(getString(R.string.product_name_cannot_be_empty));
                    etxtProductName.requestFocus();
                } else if (product_code == null || product_code.isEmpty()) {
                    etxtProductCode.setError(getString(R.string.product_code_cannot_be_empty));
                    etxtProductCode.requestFocus();
                } else if (product_category_name == null || product_category_id == null || product_category_name.isEmpty() || product_category_id.isEmpty()) {
                    etxtProductCategory.setError(getString(R.string.product_category_cannot_be_empty));
                    etxtProductCategory.requestFocus();
                } else if (product_buy_price == null || product_buy_price.isEmpty()) {
                    etxtProductBuyPrice.setError(getString(R.string.product_buy_price_cannot_be_empty));
                    etxtProductBuyPrice.requestFocus();
                } else if (product_sell_price == null || product_sell_price.isEmpty()) {
                    etxtProductSellPrice.setError(getString(R.string.product_sell_price_cannot_be_empty));
                    etxtProductSellPrice.requestFocus();
                } else if (product_stock == null || product_stock.isEmpty()) {
                    etxtProductStock.setError(getString(R.string.product_stock_cannot_be_empty));
                    etxtProductStock.requestFocus();
                } else if (Integer.parseInt(product_stock) <= 0) {
                    etxtProductStock.setError("Stock should be greater than zero");
                    etxtProductStock.requestFocus();
                }else{
//                } else if (product_supplier_name == null || product_supplier == null || product_supplier_name.isEmpty() || product_supplier.isEmpty()) {
//                    etxtProductSupplier.setError(getString(R.string.product_supplier_cannot_be_empty));
//                    etxtProductSupplier.requestFocus();
//                } else {

                    ProgressDialog progressDialog = new ProgressDialog(getContext());
                    progressDialog.setMessage("Loading...");
                    progressDialog.setTitle("Please Wait");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.show();

                    //save product info in the database
                    boolean check = dbHandler.addProduct(
                            unique_id,
                            measure_id,
                            userId,
                            product_id.toString(),
                            product_name,
                            product_code,
                            product_category_id,
                            product_buy_price,
                            product_sell_price,
                            product_stock,
                            product_supplier,
                            encodedImage,
                            units,
                            manufacturer_name,
                            product_category_name,sync_status
                           );


                    if (check) {
                        progressDialog.dismiss();
                        Toasty.success(getContext(), R.string.product_successfully_added, Toast.LENGTH_SHORT).show();

                      Intent intent = new Intent(getContext(), ShopActivity.class);
                      startActivity(intent);
                        // finish();
                    } else {
                        progressDialog.dismiss();
                        Toasty.error(getContext(), R.string.failed, Toast.LENGTH_SHORT).show();

                    }


                }

            }
        });

        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        setCancelable(true);
        return dialog;

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            // When an Image is picked
            if (resultCode == Activity.RESULT_OK && requestCode == 1) {


                mediaPath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
                Bitmap selectedImage = BitmapFactory.decodeFile(mediaPath);


                encodedImage = encodeImage(selectedImage);

                Glide.with(requireContext()).asBitmap().load(Base64.decode(encodedImage, Base64.DEFAULT)).placeholder(R.drawable.add_default_image).into(produce_image);
            }


        } catch (Exception e) {
            Toast.makeText(getContext(), R.string.something_went_wrong, Toast.LENGTH_LONG).show();

        }

    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    private void chooseImage() {
        Intent intent = new Intent(getActivity(), ImageSelectActivity.class);
        intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true); // Default is true
        intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);   // Default is true
        intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);  // Default is true
        startActivityForResult(intent, 1);
    }

    public void saveList(List<Category> categories) {
        this.categories = categories;
    }

    public void savePtdList(List<Product> products) {
        this.products = products;
    }

    public boolean validateManufacturer() {
        if (etxtProductManufucturer.getText().toString().isEmpty()) {
            etxtProductManufucturer.setError(getString(R.string.select_manufacturer));
            return false;
        } else {
            etxtProductManufucturer.setError(null);
            return true;
        }
    }

    public boolean validateProductCategory() {
        if (etxtProductCategory.getText().toString().isEmpty()) {
            etxtProductCategory.setError(getString(R.string.select_product_category));
            return false;
        } else {
            etxtProductCategory.setError(null);
            return true;
        }
    }

}