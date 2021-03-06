package com.cabral.emaishapay.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cabral.emaishapay.DailogFragments.AddBeneficiaryFragment;
import com.cabral.emaishapay.DailogFragments.AddCardFragment;
import com.cabral.emaishapay.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class BeneficiariesListFragment extends Fragment {

    FloatingActionButton btnAddBeneficiary;

    Toolbar toolbar;
    public BeneficiariesListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =inflater.inflate(R.layout.fragment_beneficiaries_list, container, false);
        btnAddBeneficiary = rootView.findViewById(R.id.btn_add_beneficiary);
        toolbar = rootView.findViewById(R.id.toolbar_beneficiaries_list);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("My Beneficiaries");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);


        btnAddBeneficiary.setOnClickListener(v -> {
            //nvigate to add beneficiaries fragment
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment prev =fm.findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            // Create and show the dialog.
            DialogFragment addCardDialog =new AddBeneficiaryFragment();
            addCardDialog.show( ft, "dialog");

        });
        return rootView;
    }
}