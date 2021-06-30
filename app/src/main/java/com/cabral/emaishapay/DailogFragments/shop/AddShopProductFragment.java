package com.cabral.emaishapay.DailogFragments.shop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import es.dmoral.toasty.Toasty;
import in.mayanknagwanshi.imagepicker.ImageSelectActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cabral.emaishapay.AppExecutors;
import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.models.shop_model.CategoriesResponse;
import com.cabral.emaishapay.models.shop_model.Category;
import com.cabral.emaishapay.models.shop_model.Product;
import com.cabral.emaishapay.models.shop_model.ProductResponse;
import com.cabral.emaishapay.modelviews.ShopProductsModelView;
import com.cabral.emaishapay.network.api_helpers.BuyInputsAPIClient;
import com.cabral.emaishapay.network.db.entities.EcManufacturer;
import com.cabral.emaishapay.network.db.entities.EcProduct;
import com.cabral.emaishapay.network.db.entities.EcProductCategory;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AddShopProductFragment extends DialogFragment {
    private static final String TAG = "AddProductFragment";
    Context context;
    public static EditText etxtProductCode;
    EditText  etxtProductBuyPrice, etxtProductSellPrice, etxtProductStock, etxtProductSupplier,etxtproductMeasurement;
    TextView  etxtProductName, etxtProductCategory,etxtProductManufucturer,tvAddProduct,txtAddProdcut;
    Spinner quantityUnit;
    LinearLayout measurement_layout;
    ArrayAdapter<String> categoryAdapter, supplierAdapter, productAdapter, manufacturersAdapter;
    List<HashMap<String, String>> productCategory=new ArrayList<>(), productSupplier =new ArrayList<>(), weightUnit=new ArrayList<>();
    Integer selectedProductID,selectedManufacturersID,selectedCategoryID;
    double selected_weight;
    String selectectedCategoryName, selectedProductName, selectedManufacturerName,selected_weight_units,selectedSupplierID,productImage,selected_measure_id,mediaPath, encodedImage = null,updateId,product_id,measure_id;
    private List<Category> categories;
    private List<Product> products;
    private final List<EcManufacturer> manufacturers;
    private List<String> productNames,manufacturersNames,offlinemanufacturersNames,offlineCategoryNames,offlineProductsName,catNames,categoryNames, supplierNames, weightUnitNames;
    private final String key;
    private ImageView produce_image;
    private ArrayList<HashMap<String, String>>offlineManufacturers=new ArrayList<>(),offlineCategories= new ArrayList<>(),offlineProductNames = new ArrayList<>();
    DialogLoader dialogLoader;
    private final ShopProductsModelView viewModel;
    ActivityResultLauncher<Intent> someActivityResultLauncher;


    public AddShopProductFragment(List<EcManufacturer> manufacturers,String key,ShopProductsModelView viewModel) {
        this.manufacturers=manufacturers;
        this.key = key;
        this.viewModel = viewModel;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                            // There are no request codes
                           showActivityResult(result);

                    }
                });

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
        initializeViews(view);

        if(getArguments()!=null){
            tvAddProduct.setText("Edit Product");
            txtAddProdcut.setText("Update");
            etxtProductManufucturer.setText(getArguments().getString("manufacturer"));
            etxtProductCategory.setText(getArguments().getString("category"));
            etxtProductName.setText(getArguments().getString("product_name"));
            etxtProductCode.setText(getArguments().getString("product_code"));
            etxtProductBuyPrice.setText(getArguments().getString("buy_price"));
            etxtProductSellPrice.setText(getArguments().getString("sell_price"));
            etxtProductStock.setText(getArguments().getString("stock"));
            etxtproductMeasurement.setText(getArguments().getString("weight"));
            product_id = getArguments().getString("product_id");
            updateId = getArguments().getString("id");
            WalletHomeActivity.selectSpinnerItemByValue(quantityUnit,getArguments().getString("weight_unit"));


            try {
                //set product image
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.add_default_image)
                        .error(R.drawable.add_default_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.HIGH);

                Glide.with(context).load(Base64.decode(getArguments().getString("image") != null ? getArguments().getString("image") : "", Base64.DEFAULT)).apply(options).into(produce_image);
            }catch (IllegalArgumentException e){
                e.printStackTrace();

            }


        }


        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                //get offline manufacturers
                offlineManufacturers =viewModel.getOfflineManufacturers();
                //get offline product categories
                offlineCategories =viewModel.getOfflineProductCategories();
                //get offline product names
                offlineProductNames = viewModel.getOfflineProductNames();


            }
        });

        //get categories
       getCategories();


        categoryNames = new ArrayList<>();
        supplierNames = new ArrayList<>();
        weightUnitNames = new ArrayList<>();


        produce_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                //get product category
                productCategory  =viewModel.getOfflineProductCategories();
                //get product supplier
                productSupplier =viewModel.getProductSupplier();
                //get weight unit
                weightUnit = viewModel.getWeightUnit();


            }
        });


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
               //get manufacturers
                getManufacturers();

            }
        });

        etxtProductCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //get product categories
                getProductCategories();
            }
        });

        etxtProductName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productNames = new ArrayList<>();

                if (validateProductCategory()) {
                    //get product names
                    getProducts();
                }
            }
        });


        etxtProductSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //get suppliers
                getSuppliers();
            }
        });


        txtAddProdcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save product
                saveProduct();
             }
        });

        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        setCancelable(true);
        return dialog;

    }

    private void initializeViews(View view) {
        etxtProductName = view.findViewById(R.id.etxt_product_name);
        etxtProductCode = view.findViewById(R.id.etxt_product_code);
        etxtProductCategory = view.findViewById(R.id.etxt_product_category);
        etxtProductBuyPrice = view.findViewById(R.id.etxt_buy_price);
        etxtProductSellPrice = view.findViewById(R.id.etxt_product_sell_price);
        etxtProductStock = view.findViewById(R.id.etxt_product_stock);
        etxtProductSupplier = view.findViewById(R.id.etxt_supplier);
        etxtProductManufucturer = view.findViewById(R.id.etxt_product_manufucturer);
        quantityUnit = view.findViewById(R.id.product_units);
        etxtproductMeasurement = view.findViewById(R.id.etxt_product_measurement);
        measurement_layout= view.findViewById(R.id.measurement_layout);
        tvAddProduct = view.findViewById(R.id.add_product_tv);
        produce_image = view.findViewById(R.id.product_image);
        txtAddProdcut = view.findViewById(R.id.tx_add_product);
        ImageView close = view.findViewById(R.id.add_product_close);
        close.setOnClickListener(v -> dismiss());
    }


    /***********************FUNCTIONS******************************************/
    private void getCategories() {
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();


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
    }

    private void getManufacturers() {
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
                //update manufacturer
                saveManufacturer(dialog_add_edit_text);
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
                        manufacturers_id = Integer.parseInt(manufacturers.get(i).getManufacturers_id());
                        manufacturers_name = manufacturers.get(i).getManufacturer_name();
                    }
                }


                selectedManufacturersID = manufacturers_id;
                selectedManufacturerName = manufacturers_name;

                Log.d("Manufucturer_id", String.valueOf(manufacturers_id));
            }
        });

    }

    private void saveManufacturer(EditText dialog_add_edit_text) {
        if(!dialog_add_edit_text.getText().toString().isEmpty()){
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    //add manufacturer
                    long addManufacturer= viewModel.addManufacturer(new EcManufacturer(
                            dialog_add_edit_text.getText().toString()
                    ));


                    AppExecutors.getInstance().mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            if (addManufacturer>0) {
                                manufacturersAdapter.add(dialog_add_edit_text.getText().toString());
                                manufacturersAdapter.notifyDataSetChanged();
                                dialog_add_edit_text.getText().clear();
                            } else {

                                Toasty.error(getContext(), R.string.failed, Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            });


        }
    }

    private void getProductCategories() {
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
                    //save product categories
                    saveProductCategory(dialog_add_edit_text);
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

                    if(selectedManufacturersID > 0){
                        getProductByManufacturer(category_id);
                    }




                    selectedCategoryID = category_id;
                    selectectedCategoryName = category_name;

                    Log.d("category_id", String.valueOf(category_id));
                }
            });
        }
    }

    private void saveProductCategory(EditText dialog_add_edit_text) {
        if(!dialog_add_edit_text.getText().toString().isEmpty()){
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    //add manufacturer
                    long category =viewModel.addProductCategory(new EcProductCategory(
                            dialog_add_edit_text.getText().toString()
                    ));


                    AppExecutors.getInstance().mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            if (category>0) {
                                categoryAdapter.add(dialog_add_edit_text.getText().toString());
                                categoryAdapter.notifyDataSetChanged();
                                dialog_add_edit_text.getText().clear();
                            } else {

                                Toasty.error(getContext(), R.string.failed, Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            });


        }
    }

    private void getProducts() {

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

                    //save product
                    saveProductOffline(add_product);

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


            //show product list
                showProductList(alertDialog,position);

            }
        });
    }

    private void showProductList(AlertDialog alertDialog, int position) {
        alertDialog.dismiss();
        final String selectedItem = productAdapter.getItem(position);

        etxtProductName.setText(selectedItem);



        //Need to use hashMap to reduce Order to O(1)
        for (int i = 0; i < productNames.size(); i++) {
            if (productNames.get(i).equalsIgnoreCase(selectedItem)) {
                // Get the ID of selected Country
                selectedProductID = products.get(i).getProducts_id();
                selected_measure_id= products.get(i).getMeasure_id();
                selectedProductName = products.get(i).getProducts_name()+ " "+ products.get(i).getProducts_weight()+ products.get(i).getProducts_weight_unit();
                selected_weight = products.get(i).getProducts_weight();
                selected_weight_units = products.get(i).getProducts_weight_unit();
                productImage =products.get(i).getImageUrl();


                String image_url = ConstantValues.ECOMMERCE_WEB +productImage;
                Log.d(TAG, "onItemClick: image"+image_url);

                //set image using Glide
                Glide.with(context).load(image_url).into(produce_image);

                //encode image

                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try  {
                            //Your code goes here
                            URL url = new URL(image_url);
                            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            encodedImage = encodeImage(image);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();

                Log.d(TAG, "onItemClick: encodedImage"+encodedImage);

                etxtproductMeasurement.setText(products.get(i).getProducts_weight()+"");
                etxtProductCode.setText(selectedProductName);
                etxtProductSellPrice.setText(products.get(i).getProducts_price()+"");
                etxtProductBuyPrice.setText(products.get(i).getProducts_price()+"");
                setSelectionValue(products.get(i).getProducts_weight_unit(),quantityUnit,R.array.product_measurement_unit);
                //String encodedImage=products.get(i).getImage();

            }
        }
    }

    private void saveProductOffline(EditText add_product) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                String userId = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());
                String unique_id = userId+"_"+System.currentTimeMillis();
                //add manufacturer
                long product = viewModel.addProduct(new EcProduct(
                        unique_id,
                        "",
                        add_product.getText().toString(),
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        ""
                ));


                AppExecutors.getInstance().mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (product>0) {
                            productAdapter.add(add_product.getText().toString());
                            productAdapter.notifyDataSetChanged();
                            add_product.getText().clear();
                        } else {

                            Toasty.error(getContext(), R.string.failed, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });
    }

    private void getSuppliers() {
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

                //show supplier list
                showSupplierList(alertDialog,position);

            }
        });
    }

    private void showSupplierList(AlertDialog alertDialog, int position) {
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

    private void saveProduct() {
        dialogLoader.showProgressDialog();

        String userId = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());

        String unique_id = userId+"_"+System.currentTimeMillis();

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
        int product_id = selectedProductID;
        String units = etxtproductMeasurement.getText().toString().trim() + quantityUnit.getSelectedItem().toString().trim();
        String sync_status = "0";
        if (quantityUnit.getSelectedItem().toString().equalsIgnoreCase("Select") || etxtproductMeasurement.getText().toString().equalsIgnoreCase("")) {
            units = null;
        }

        Log.d(TAG, "onClick: timestamp" + unique_id);


        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        if(!is_validAddProductForm()){
            dialogLoader.hideProgressDialog();
            return;
        }
        else {


            if (key.equalsIgnoreCase("update")) {


                Call<ResponseBody> call = BuyInputsAPIClient
                        .getInstance()
                        .updateProduct(access_token,updateId,measure_id,userId,product_id+"",product_buy_price,product_sell_price,product_supplier,Integer.parseInt(product_stock),manufacturer_name,product_category_name,product_name);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    long update_product =   viewModel.updateProductStock(
                                            product_id+"",
                                            product_buy_price,
                                            product_sell_price,
                                            product_supplier,
                                            Integer.parseInt(product_stock),
                                            manufacturer_name,
                                            product_category_name,
                                            product_name,
                                            product_code,
                                            encodedImage,
                                            selected_weight_units,
                                            selected_weight+""
                                    );

                                    AppExecutors.getInstance().mainThread().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (update_product>0) {
                                                dialogLoader.hideProgressDialog();
                                                AddShopProductFragment.this.dismiss();
                                                Toasty.success(getContext(), R.string.product_successfully_updated, Toast.LENGTH_SHORT).show();

                                                //Intent intent = new Intent(getContext(), ShopActivity.class);
                                                //startActivity(intent);
                                                // finish();
                                            } else {
                                                dialogLoader.hideProgressDialog();
                                                AddShopProductFragment.this.dismiss();
                                                Toasty.error(getContext(), R.string.failed, Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                                }
                            });
                            //Log.d("Categories", String.valueOf(categories));

                        } else {
                            Log.d("Failed", "Manufacturers Fetch failed");

                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();

                    }
                });






            }
            else{

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        //Log.w("savedProduct", product_name);
                        long checkAddedProduct=viewModel.addProduct(new EcProduct(
                                unique_id,
                                product_id+"",
                                product_name,
                                product_code,
                                product_category_name,
                                "",
                                product_buy_price,
                                product_sell_price,
                                product_supplier,
                                encodedImage,
                                product_stock,
                                selected_weight_units,
                                selected_weight + "",
                                manufacturer_name
                        ));

                        AppExecutors.getInstance().mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                if (checkAddedProduct>0) {
                                    dialogLoader.hideProgressDialog();
                                    Toasty.success(getContext(), R.string.product_successfully_added, Toast.LENGTH_SHORT).show();

                                    //start product sync
                                    initiateSnc();

                                    AddShopProductFragment.this.dismiss();
                                } else {
                                    dialogLoader.hideProgressDialog();
                                    Toasty.error(getContext(), R.string.failed, Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                    }
                });

                //save product info in the database


            }

        }
    }

    public  void initiateSnc() {
        AppExecutors.getInstance().NetworkIO().execute(
                new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: product sync started");
                        WalletHomeActivity.SyncProductData();

                    }
                }
        );
    }

    public void showActivityResult(ActivityResult result){
        Intent data = result.getData();
        try {

            // When an Image is picked
            if (result.getResultCode() == Activity.RESULT_OK) {


                mediaPath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
                Bitmap selectedImage = BitmapFactory.decodeFile(mediaPath);


                encodedImage = encodeImage(selectedImage);

                Glide.with(requireContext()).asBitmap().load(Base64.decode(encodedImage, Base64.DEFAULT)).placeholder(R.drawable.add_default_image).into(produce_image);
            }


        } catch (Exception e) {
            Toast.makeText(getContext(), R.string.something_went_wrong, Toast.LENGTH_LONG).show();

        }
    }

    private void getProductByManufacturer(Integer category_id) {

        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
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
    }

    private boolean is_validAddProductForm() {

        String product_category_id = selectedCategoryID + "";

        if ( TextUtils.isEmpty(etxtProductManufucturer.getText()) ) {
            etxtProductManufucturer.setError(getString(R.string.product_manfacturer_is_empty));
            etxtProductManufucturer.requestFocus();
            return false;
        }else if ( TextUtils.isEmpty(etxtProductCategory.getText()) || product_category_id == null || product_category_id.isEmpty()) {
            etxtProductCategory.setError(getString(R.string.product_category_cannot_be_empty));
            etxtProductCategory.requestFocus();
            return false;
        }else if (TextUtils.isEmpty(etxtProductName.getText())) {
            etxtProductName.setError(getString(R.string.product_name_cannot_be_empty));
            etxtProductName.requestFocus();
            return false;
        } else  if ( TextUtils.isEmpty(etxtProductBuyPrice.getText())  ) {
            etxtProductBuyPrice.setError(getString(R.string.product_buy_price_cannot_be_empty));
            etxtProductBuyPrice.requestFocus();
            return false;
        } else if (  TextUtils.isEmpty(etxtProductSellPrice.getText()) ) {
            etxtProductSellPrice.setError(getString(R.string.product_sell_price_cannot_be_empty));
            etxtProductSellPrice.requestFocus();
            return false;
        } else if ( TextUtils.isEmpty(etxtProductStock.getText()) ) {
            etxtProductStock.setError(getString(R.string.product_stock_cannot_be_empty));
            etxtProductStock.requestFocus();
        } else if (Integer.parseInt(etxtProductStock.getText().toString()) <= 0) {
            etxtProductStock.setError("Stock should be greater than zero");
            etxtProductStock.requestFocus();
            return false;
        }

        return true;
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
        someActivityResultLauncher.launch(intent);
       // startActivityForResult(intent, 1);
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

    private void setSelectionValue(String compareValue,Spinner mSpinner, int array_ressouce_id){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,array_ressouce_id, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        if (compareValue != null) {
            int spinnerPosition = adapter.getPosition(compareValue);
            mSpinner.setSelection(spinnerPosition);
        }
    }

}