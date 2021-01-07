package com.cabral.emaishapay.fragments.buyandsell;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.cabral.emaishapay.R;


public class ProductRatingReviewFragment extends Fragment {


    public ProductRatingReviewFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Review");

        ((AppCompatActivity)requireActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        return inflater.inflate(R.layout.fragment_product_rating_review, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Hide Cart Icon in the Toolbar
//        MenuItem languageItem = menu.findItem(R.id.toolbar_ic_language);
//        MenuItem currencyItem = menu.findItem(R.id.toolbar_ic_currency);
//        MenuItem profileItem = menu.findItem(R.id.toolbar_edit_profile);
//        MenuItem searchItem = menu.findItem(R.id.toolbar_ic_search);
        MenuItem cartItem = menu.findItem(R.id.toolbar_ic_cart);
//        profileItem.setVisible(false);
//        languageItem.setVisible(false);
//        currencyItem.setVisible(false);
//        searchItem.setVisible(false);
        cartItem.setVisible(false);
    }
}