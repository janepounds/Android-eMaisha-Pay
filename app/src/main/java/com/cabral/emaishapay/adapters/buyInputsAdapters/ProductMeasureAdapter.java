package com.cabral.emaishapay.adapters.buyInputsAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.cabral.emaishapay.R;
import com.cabral.emaishapay.fragments.buy_fragments.Product_Description;
import com.cabral.emaishapay.models.product_model.ProductDetails;
import com.cabral.emaishapay.models.product_model.ProductMeasure;

import java.util.ArrayList;
import java.util.List;

public class ProductMeasureAdapter extends RecyclerView.Adapter<ProductMeasureAdapter.ProductMeasureViewHolder> {
    private static final String TAG = "ProductMeasureAdapter";
    private final Context context;
    private final List<ProductMeasure> productMeasures;

    private String measure;
    private final TextView new_price;
    private final Product_Description productDescription;
    private boolean isChecked;
    private final int result = 0;
    //create array list to add checked measures
    ArrayList<Boolean> checkedmesure;


    public ProductMeasureAdapter(Context context, List<ProductMeasure> productMeasureList, TextView new_price, Product_Description productDescription,ArrayList<Boolean>booleanArrayList) {
        this.context = context;
        this.productMeasures = productMeasureList;
        this.new_price = new_price;
        this.productDescription = productDescription;
        this.checkedmesure=booleanArrayList;
        Product_Description.productMeasureAdapterCalls++;
    }

    @NonNull
    @Override
    public ProductMeasureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.buy_inputs_product_measure, parent, false);

        return new ProductMeasureAdapter.ProductMeasureViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductMeasureViewHolder holder, int position) {
        final ProductMeasure productMeasure = productMeasures.get(position);
        Log.d(TAG, "onBindViewHolder: " + productMeasure);

        for (int i = 0; i < productMeasures.size(); i++) {
            ProductMeasure productMeasure1 = productMeasures.get(i);
            isChecked = productMeasure1.isChecked();
            Log.d(TAG, "onBindViewHolder: Checked = " + productMeasure1.isChecked());
            Log.d(TAG, "onBindViewHolder: Checkedischecked = " + productMeasure1.isChecked());

            checkedmesure = new ArrayList<>();
            checkedmesure.add(productMeasure1.isChecked());


            measure = productMeasure.getProducts_weight() + " " + productMeasure.getProducts_weight_unit();
            holder.weight.setText(measure);

        }


        if (productMeasure.isChecked()) {
            holder.weight.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_rectangle_green_background));
            holder.weight.setTextColor(ContextCompat.getColor(context, R.color.white));
            //set the weights nd units
            String measure = holder.weight.getText().toString();
            String []weight = measure.split("\\s+");
            Product_Description.selected_measure = holder.weight.getText().toString();
            new_price.setText("UGX " + productMeasure.getProducts_price());
            Product_Description.selected_price=productMeasure.getProducts_price();

            String []products_price = new_price.getText().toString().split("\\s+");

            ProductDetails productDetails = new ProductDetails();
            productDetails.setSelectedProductsWeight(Product_Description.selected_measure);
            productMeasure.setProducts_weight(weight[0]);
            productMeasure.setProducts_weight_unit(weight[1]);
            productMeasure.setProducts_price(products_price[1]);


            isChecked=true;
            Log.d(TAG, "onBindViewHolder: prodctsprice"+products_price[1] + "measure"+Product_Description.selected_measure);

        } else {
            holder.weight.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_rectangle_mild_gray_background));
            holder.weight.setTextColor(ContextCompat.getColor(context, R.color.mild_gray));
            isChecked = false;

        }

        holder.weight.setOnClickListener(v -> {
            selectProductWeight(productMeasure,holder);
            notifyDataSetChanged();
        });

        if(getItemCount()==1){
            selectProductWeight(productMeasure,holder);
        }
    }

    private void selectProductWeight(ProductMeasure productMeasure, ProductMeasureViewHolder holder){
        for (ProductMeasure productMeasure1 : productMeasures) {
            productMeasure1.setChecked(false);
            Log.d(TAG, "onBindViewHolder: ClickCheck = " + productMeasure1.isChecked());
            Log.d(TAG, "onBindViewHolder: ClickCheckischecked= " + isChecked);

        }

//            // Check Discount on Product with the help of static method of Helper class

        productMeasure.setChecked(true);
        holder.weight.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_rectangle_green_background));
        holder.weight.setTextColor(ContextCompat.getColor(context, R.color.white));

        Product_Description.selected_measure=holder.weight.getText().toString();
        new_price.setText("UGX " + productMeasure.getProducts_price());
        isChecked = true;

    }

    @Override
    public int getItemCount() {
        return productMeasures.size();
    }

    public class ProductMeasureViewHolder extends RecyclerView.ViewHolder {
        private final TextView weight;
        private ImageView check;

        public ProductMeasureViewHolder(View itemView) {
            super(itemView);
            weight = itemView.findViewById(R.id.weight1);
        }

    }




}
