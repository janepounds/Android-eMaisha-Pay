package com.cabral.emaishapay.DailogFragments.shop;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.database.DbHandlerSingleton;
import com.cabral.emaishapay.models.shop_model.CategoriesResponse;
import com.cabral.emaishapay.models.shop_model.Category;
import com.cabral.emaishapay.models.shop_model.Manufacturer;
import com.cabral.emaishapay.models.shop_model.ManufacturersResponse;
import com.cabral.emaishapay.models.shop_model.Product;
import com.cabral.emaishapay.models.shop_model.ProductResponse;
import com.cabral.emaishapay.network.APIClient;

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


    Context context;
    public static EditText etxtProductCode;
    ProgressDialog loading;
    EditText etxtProductName, etxtProductCategory, etxtProductDescription, etxtProductBuyPrice, etxtProductSellPrice, etxtProductStock, etxtProductSupplier, etxtProdcutWeightUnit, etxtProductWeight, etxtProductManufucturer;
    TextView txtAddProdcut;

    String mediaPath, encodedImage = "N/A";
    ArrayAdapter<String> categoryAdapter, supplierAdapter, productAdapter, manufacturersAdapter;
    List<String> categoryNames, supplierNames, weightUnitNames;
    Integer selectedCategoryID;
    Integer selectedManufacturersID;
    Integer selectedProductID;
    String selectedSupplierID;
    String selectedWeightUnitID;
    String selectectedCategoryName, selectedProductName, selectedManufacturerName;
    private List<Category> categories;
    private List<Product> products;
    private List<String> catNames;
    private List<Manufacturer> manufacturers;
    private List<String> productNames;
    private List<String> manufacturersNames;
    private int measure_id;
    private DbHandlerSingleton dbHandler;



    public AddProductFragment() {
        // Required empty public constructor
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


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_add_product, container, false);
//    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

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
//        etxtProductDescription = view.findViewById(R.id.etxt_product_description);
        etxtProductBuyPrice = view.findViewById(R.id.etxt_buy_price);
        etxtProductSellPrice = view.findViewById(R.id.etxt_product_sell_price);
        etxtProductStock = view.findViewById(R.id.etxt_product_stock);
        etxtProductSupplier = view.findViewById(R.id.etxt_supplier);
        etxtProductManufucturer = view.findViewById(R.id.etxt_product_manufucturer);

        txtAddProdcut = view.findViewById(R.id.txt_add_product);


        Call<ManufacturersResponse> call1 = APIClient
                .getInstance()
                .getManufacturers();
        call1.enqueue(new Callback<ManufacturersResponse>() {
            @Override
            public void onResponse(Call<ManufacturersResponse> call, Response<ManufacturersResponse> response) {
                if (response.isSuccessful()) {
                    manufacturers = response.body().getManufacturers();
                    saveManufacturersList(manufacturers);
                    Log.d("Categories", String.valueOf(categories));

                } else {
                    Log.d("Failed", "Manufacturers Fetch failed");

                }
            }

            @Override
            public void onFailure(Call<ManufacturersResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("Failed", "Manufacturers Fetch failed");

            }
        });

        Call<CategoriesResponse> call = APIClient
                .getInstance()
                .getCategories();
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


                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);

                Button dialog_button = dialogView.findViewById(R.id.dialog_button);
                EditText dialog_input = dialogView.findViewById(R.id.dialog_input);
                TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
                ListView dialog_list = dialogView.findViewById(R.id.dialog_list);


                dialog_title.setText("Manufacturers");
                dialog_list.setVerticalScrollBarEnabled(true);
                dialog_list.setAdapter(manufacturersAdapter);

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
                    for (int i = 0; i < categories.size(); i++) {
                        catNames.add(categories.get(i).getCategories_slug());
                    }

                    categoryAdapter = new ArrayAdapter<String>(getContext(), R.layout.list_row);
                    categoryAdapter.addAll(catNames);


                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                    dialog.setView(dialogView);
                    dialog.setCancelable(false);

                    Button dialog_button = dialogView.findViewById(R.id.dialog_button);
                    EditText dialog_input = dialogView.findViewById(R.id.dialog_input);
                    TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
                    ListView dialog_list = dialogView.findViewById(R.id.dialog_list);


                    dialog_title.setText(R.string.product_category);
                    dialog_list.setVerticalScrollBarEnabled(true);
                    dialog_list.setAdapter(categoryAdapter);


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

                            Call<ProductResponse> call = APIClient
                                    .getInstance()
                                    .getProducts(
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
                                }

                                @Override
                                public void onFailure(Call<ProductResponse> call, Throwable t) {
                                    t.printStackTrace();
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
                    for (int i = 0; i < products.size(); i++) {
                        productNames.add(products.get(i).getProducts_name()+ " "+ products.get(i).getProducts_weight()+ products.get(i).getProducts_weight_unit());
                        measure_id = products.get(i).getMeasure_id();
                    }

                    productAdapter = new ArrayAdapter<String>(getContext(), R.layout.list_row);
                    productAdapter.addAll(productNames);


                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_list_search, null);
                    dialog.setView(dialogView);
                    dialog.setCancelable(false);

                    Button dialog_button = dialogView.findViewById(R.id.dialog_button);
                    EditText dialog_input = dialogView.findViewById(R.id.dialog_input);
                    TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
                    ListView dialog_list = dialogView.findViewById(R.id.dialog_list);


                    dialog_title.setText("Products");
                    dialog_list.setVerticalScrollBarEnabled(true);
                    dialog_list.setAdapter(productAdapter);


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

                List<HashMap<String, String>> shop_information = dbHandler.getShopInformation();
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String shop_name = shop_information.get(0).get("shop_name");
                String id = shop_name.replaceAll(" ", "") + "PDT" + timestamp.toString().replaceAll(" ", "");
                Integer shop_id = Integer.parseInt(WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCE_SHOP_ID, context));
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
                } else if (product_supplier_name == null || product_supplier == null || product_supplier_name.isEmpty() || product_supplier.isEmpty()) {
                    etxtProductSupplier.setError(getString(R.string.product_supplier_cannot_be_empty));
                    etxtProductSupplier.requestFocus();
                } else {

                    Call<ResponseBody> call = APIClient
                            .getInstance()
                            .postProduct(
                                    id,
                                    measure_id,
                                    shop_id,
                                    product_id,
                                    product_buy_price,
                                    product_sell_price,
                                    product_supplier,
                                    Integer.parseInt(product_stock)
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
                                progressDialog.dismiss();
                                Log.d("Product Save", "Product successfully saved");
                                String s = null;
                                try {
                                    s = response.body().string();
                                    if (s != null) {
                                        JSONObject jsonObject = new JSONObject(s);
                                        boolean check = dbHandler.addProduct(product_id.toString(), product_name, product_code, product_category_id, jsonObject.getJSONObject("data").getString("product_description"), product_buy_price, product_sell_price, product_stock, product_supplier, jsonObject.getJSONObject("data").getString("product_image"), jsonObject.getJSONObject("data").getString("product_weight_unit"), jsonObject.getJSONObject("data").getString("products_weight"));

                                        if (check) {
                                            Toasty.success(getContext(), R.string.product_successfully_added, Toast.LENGTH_SHORT).show();

                                            getChildFragmentManager().popBackStack();
//                                            Intent intent = new Intent(getContext(), ProductActivity.class);
//                                            startActivity(intent);
                                            // finish();
                                        } else {
                                            Toasty.error(getContext(), R.string.failed, Toast.LENGTH_SHORT).show();

                                        }
                                    }

                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }


                            } else {
                                progressDialog.dismiss();
                                Toasty.error(getContext(), "Failed", Toast.LENGTH_SHORT).show();


                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                            Log.e("Add Product Error:", t.getMessage());

                        }
                    });


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
            if (requestCode == 1213 && resultCode == RESULT_OK && null != data) {


                mediaPath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
                Bitmap selectedImage = BitmapFactory.decodeFile(mediaPath);


                encodedImage = encodeImage(selectedImage);


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




    public void saveList(List<Category> categories) {
        this.categories = categories;
    }

    public void savePtdList(List<Product> products) {
        this.products = products;
    }

    public void saveManufacturersList(List<Manufacturer> manufacturers) {
        this.manufacturers = manufacturers;
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