package com.cabral.emaishapay.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.cabral.emaishapay.R;


public class AccountOpeningFragment extends Fragment {


    Toolbar toolbar;
    public AccountOpeningFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_opening, container, false);
        toolbar = view.findViewById(R.id.toolbar_account_opening);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Account Opening");

        openFragment(new PersonalDetailsFragment());
        return view;
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction =getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.open_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    @Override
//    public void onBackPressed() {
////        if (getFragmentManager().getBackStackEntryCount() == 1) {
////            moveTaskToBack(false);
////        } else {
////           super.onBackPressed();
////        }
//        getActivity().finish();
//    }
}