package com.cabral.emaishapay.fragments.buy_fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.adapters.buyInputsAdapters.CategoryListAdapter_3;
import com.cabral.emaishapay.app.EmaishaPayApp;
import com.cabral.emaishapay.databinding.BuyInputsCategoriesBinding;
import com.cabral.emaishapay.models.category_model.CategoryDetails;

import java.util.ArrayList;
import java.util.List;

public class Categories_3 extends Fragment {
    private Context context;
    Boolean isMenuItem = true;
    Boolean isHeaderVisible = false;
    CategoryListAdapter_3 categoryListAdapter;
    List<CategoryDetails> allCategoriesList;
    List<CategoryDetails> mainCategoriesList;
    BuyInputsCategoriesBinding binding;

    private EditText searchView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.buy_inputs_categories, container, false);


        if (getArguments() != null) {
            if (getArguments().containsKey("isHeaderVisible")) {
                isHeaderVisible = getArguments().getBoolean("isHeaderVisible");
            }

            if (getArguments().containsKey("isMenuItem")) {
                isMenuItem = getArguments().getBoolean("isMenuItem", true);
            }
        }

        if (isMenuItem) {
            // Enable Drawer Indicator with static variable actionBarDrawerToggle of MainActivity
            //MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.categories));
        }

        allCategoriesList = new ArrayList<>();

        // Get CategoriesList from ApplicationContext
        allCategoriesList = ((EmaishaPayApp) requireContext().getApplicationContext()).getCategoriesList();


        // Hide some of the Views
        binding.emptyRecordText.setVisibility(View.GONE);

        // Check if Header must be Invisible or not
        if (!isHeaderVisible) {
            // Hide the Header of CategoriesList
            binding.categoriesHeader.setVisibility(View.GONE);
        }
        else {
            binding.categoriesHeader.setText(getString(R.string.categories));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                binding.categoriesHeader.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_categories, 0, 0, 0);
            } else {
                binding.categoriesHeader.setCompoundDrawables(getResources().getDrawable(R.drawable.ic_categories), null, null, null);
            }
        }

        mainCategoriesList = new ArrayList<>();

        for (int i = 0; i < allCategoriesList.size(); i++) {
            if (allCategoriesList.get(i).getParentId().equalsIgnoreCase("0")) {
                mainCategoriesList.add(allCategoriesList.get(i));
            }
        }

        // Initialize the CategoryListAdapter for RecyclerView
        categoryListAdapter = new CategoryListAdapter_3(getActivity(), mainCategoriesList, false);

        // Set the Adapter and LayoutManager to the RecyclerView
        binding.categoriesRecycler.setAdapter(categoryListAdapter);
        binding.categoriesRecycler.setLayoutManager(new GridLayoutManager(getContext(), 3));

        categoryListAdapter.notifyDataSetChanged();

        WalletBuyFragment.searchIcon.setOnClickListener(v -> {
            if ((WalletBuyFragment.searchView.getText().toString().trim().isEmpty()) || (WalletBuyFragment.searchView.getText().toString().trim() == null)) {
                Toast.makeText(context, "Search Text is empty", Toast.LENGTH_SHORT).show();
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("searchKey", WalletBuyFragment.searchView.getText().toString());
                // Navigate to SearchFragment Fragment
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                Fragment searchFragment = new SearchFragment();
                searchFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .add(R.id.nav_host_fragment2, searchFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(null).commit();
            }
        });


            return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
