package com.cabral.emaishapay.fragments.buyandsell;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.cabral.emaishapay.R;
import com.cabral.emaishapay.adapters.buyInputsAdapters.CategoryListAdapter_3;
import com.cabral.emaishapay.app.EmaishaPayApp;
import com.cabral.emaishapay.models.category_model.CategoryDetails;

import java.util.ArrayList;
import java.util.List;

import am.appwise.components.ni.NoInternetDialog;


public class SubCategories_3 extends Fragment {

    int parentCategoryID;
    Boolean isHeaderVisible;

    TextView emptyText, headerText;
    RecyclerView category_recycler;

    CategoryListAdapter_3 categoryListAdapter;

    List<CategoryDetails> allCategoriesList;
    List<CategoryDetails> subCategoriesList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.buy_inputs_categories, container, false);

        // Get CategoryID from Bundle arguments
        parentCategoryID = getArguments().getInt("CategoryID");


        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getArguments().getString("CategoryName", getString(R.string.actionCategory)));

        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(getContext()).build();
        //noInternetDialog.show();

        // Get CategoriesList from ApplicationContext
        allCategoriesList = new ArrayList<>();
        allCategoriesList = ((EmaishaPayApp) getContext().getApplicationContext()).getCategoriesList();


        // Binding Layout Views
        emptyText = rootView.findViewById(R.id.empty_record_text);
        headerText = rootView.findViewById(R.id.categories_header);
        category_recycler = rootView.findViewById(R.id.categories_recycler);


        // Hide some of the Views
        headerText.setVisibility(View.GONE);
        emptyText.setVisibility(View.GONE);


        subCategoriesList = new ArrayList<>();

        for (int i = 0; i < allCategoriesList.size(); i++) {
            if (allCategoriesList.get(i).getParentId().equalsIgnoreCase(String.valueOf(parentCategoryID))) {
                subCategoriesList.add(allCategoriesList.get(i));
            }
        }


        // Initialize the CategoryListAdapter and LayoutManager for RecyclerView
        categoryListAdapter = new CategoryListAdapter_3(getActivity(), subCategoriesList, true);
        category_recycler.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Set the Adapter to the RecyclerView
        category_recycler.setAdapter(categoryListAdapter);

        categoryListAdapter.notifyDataSetChanged();


        return rootView;
    }

}

