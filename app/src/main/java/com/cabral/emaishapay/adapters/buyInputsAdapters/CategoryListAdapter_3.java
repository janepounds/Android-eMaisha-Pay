package com.cabral.emaishapay.adapters.buyInputsAdapters;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletBuySellActivity;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.fragments.buy_fragments.Category_Products;
import com.cabral.emaishapay.fragments.buy_fragments.SubCategories_3;
import com.cabral.emaishapay.models.category_model.CategoryDetails;



import java.util.ArrayList;
import java.util.List;

/**
 * CategoryListAdapter is the adapter class of RecyclerView holding List of Categories in MainCategories
 **/

public class CategoryListAdapter_3 extends RecyclerView.Adapter<CategoryListAdapter_3.MyViewHolder> implements Filterable {
    boolean isSubCategory;
    Activity context;
    List<CategoryDetails> categoriesList;
    List<CategoryDetails> categoriesListFull;


    public CategoryListAdapter_3(Activity context, List<CategoryDetails> categoriesList, boolean isSubCategory) {
        this.context = context;
        this.isSubCategory = isSubCategory;
        this.categoriesList = categoriesList;
        categoriesListFull = new ArrayList<>(categoriesList);
    }


    //********** Called to Inflate a Layout from XML and then return the Holder *********//

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        // Inflate the custom layout
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.buy_inputs_categories_3, parent, false);

        return new MyViewHolder(itemView);
    }


    //********** Called by RecyclerView to display the Data at the specified Position *********//

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // Get the data model based on Position
        final CategoryDetails categoryDetails = categoriesList.get(position);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.new_product)
                .error(R.drawable.new_product)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);
        // Set OrderProductCategory Image on ImageView with Glide Library
        Glide.with(context)
                .setDefaultRequestOptions(options)
                .load(ConstantValues.ECOMMERCE_URL + categoryDetails.getImage())
                .into(holder.category_icon);


        holder.category_title.setText(categoryDetails.getName());
        holder.category_products.setText("(" + categoryDetails.getTotalProducts() + ")");
    }


    //********** Returns the total number of items in the data set *********//

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<CategoryDetails> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList = categoriesListFull;
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (CategoryDetails detail : categoriesListFull) {
                    if (detail.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(detail);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            categoriesList.clear();
            categoriesList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    /********** Custom ViewHolder provides a direct reference to each of the Views within a Data_Item *********/

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ConstraintLayout layout_category;
        ImageView category_icon;
        TextView category_title, category_products;

        public MyViewHolder(final View itemView) {
            super(itemView);

            category_icon = itemView.findViewById(R.id.category_icon);
            category_title = itemView.findViewById(R.id.category_title);
            category_products = itemView.findViewById(R.id.category_products);
            layout_category = itemView.findViewById(R.id.layout_category);

            layout_category.setOnClickListener(this);
        }

        // Handle Click Listener on OrderProductCategory item
        @Override
        public void onClick(View view) {
            // Get OrderProductCategory Info
            Bundle categoryInfo = new Bundle();
            categoryInfo.putInt("CategoryID", Integer.parseInt(categoriesList.get(getAdapterPosition()).getId()));
            categoryInfo.putString("CategoryName", categoriesList.get(getAdapterPosition()).getName());
            categoryInfo.putString("TotalProducts", categoriesList.get(getAdapterPosition()).getTotalProducts());
            String sortBy = "Newest";
            categoryInfo.putString("sortBy", sortBy);
            Fragment fragment;

            if (!isSubCategory) {
                // Navigate to Products Fragment
                //fragment = new Products();
                // Initialize Category_Products Fragment with specified arguments
                fragment = new Category_Products();

            } else {
                // Navigate to SubCategories Fragment
                fragment = new SubCategories_3();
            }

            fragment.setArguments(categoryInfo);
            FragmentManager fragmentManager = ((WalletBuySellActivity) context).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment2, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(null).commit();
        }
    }
}

