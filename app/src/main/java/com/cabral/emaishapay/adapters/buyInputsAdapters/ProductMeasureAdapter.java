package com.cabral.emaishapay.adapters.buyInputsAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.cabral.emaishapay.R;
import com.cabral.emaishapay.fragments.buy_fragments.Product_Description;
import com.cabral.emaishapay.models.product_model.ProductMeasure;

import java.util.List;

public class ProductMeasureAdapter extends RecyclerView.Adapter<ProductMeasureAdapter.ProductMeasureViewHolder> {
    private static final String TAG = "ProductMeasureAdapter";
    private Context context;
    private List<ProductMeasure> productMeasures;
    private String selected_measure;
    private String measure;
    private TextView new_price;
    private Product_Description productDescription;
    private boolean isChecked;
    private int result = 0;


    public ProductMeasureAdapter(Context context, List<ProductMeasure> productMeasureList, String product_measure, TextView new_price, Product_Description productDescription) {
        this.context = context;
        this.productMeasures = productMeasureList;
        this.selected_measure = product_measure;
        this.new_price = new_price;
        this.productDescription = productDescription;

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
            checkSelectedMeasure();
            measure = productMeasure.getProducts_weight() + " " + productMeasure.getProducts_weight_unit();
            holder.weight.setText(measure);
        }

        if (productMeasure.isChecked()) {
            holder.weight.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_rectangle_green_background));
            holder.weight.setTextColor(ContextCompat.getColor(context, R.color.white));
            new_price.setText("UGX " + productMeasure.getProducts_price());
            isChecked=true;

        } else {
            holder.weight.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_rectangle_mild_gray_background));
            holder.weight.setTextColor(ContextCompat.getColor(context, R.color.mild_gray));
            isChecked = false;

        }

        holder.weight.setOnClickListener(v -> {
            for (ProductMeasure productMeasure1 : productMeasures) {
                productMeasure1.setChecked(false);

                Log.d(TAG, "onBindViewHolder: ClickCheck = " + productMeasure1.isChecked());
                Log.d(TAG, "onBindViewHolder: ClickCheckischecked= " + isChecked);

            }

            productMeasure.setChecked(true);
            holder.weight.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_rectangle_green_background));
            holder.weight.setTextColor(ContextCompat.getColor(context, R.color.white));
            new_price.setText("UGX " + productMeasure.getProducts_price());
            isChecked = true;

            productDescription.showMeasuresRecyclerView();



            for (ProductMeasure productMeasure1 : productMeasures) {
                Log.d(TAG, "onBindViewHolder: AfterClickCheck = " + productMeasure1.isChecked());
                Log.d(TAG, "onBindViewHolder: AfterClickCheckischecked = " + isChecked);


            }
        });
    }

    @Override
    public int getItemCount() {
        return productMeasures.size();
    }

    public class ProductMeasureViewHolder extends RecyclerView.ViewHolder {
        private TextView weight;
        private ImageView check;

        public ProductMeasureViewHolder(View itemView) {
            super(itemView);
            weight = itemView.findViewById(R.id.weight1);


        }



    }

    public boolean checkSelectedMeasure(){
        if(isChecked==true){
            return true;


        }else {
            Toast.makeText(context,"Please select a measure before proceeding",Toast.LENGTH_LONG).show();
            return false;

        }
    }
}
