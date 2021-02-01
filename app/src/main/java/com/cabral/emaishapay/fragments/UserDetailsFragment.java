package com.cabral.emaishapay.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.cabral.emaishapay.R;
import com.kofigyan.stateprogressbar.StateProgressBar;

public class UserDetailsFragment extends Fragment {
    private Context context;
    Bundle localBundle;
    String[] descriptionData = {"User\nDetails","Loan\nDetails", "Farming\nDetails", "Preview", "KYC\nDetails"};

    private Toolbar toolbar;
    private StateProgressBar loanApplicationStateProgressBar;
    private Button nextBtn;
    private EditText etxtFirstName,etxtSecondName,etxteMaishaAcc;

    public UserDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_user_details, container, false);
        loanApplicationStateProgressBar = view.findViewById(R.id.loan_application_state_progress_bar_user_details);
        loanApplicationStateProgressBar.setStateDescriptionData(descriptionData);
        loanApplicationStateProgressBar.setStateDescriptionTypeface("fonts/JosefinSans-SemiBold.ttf");

        toolbar = view.findViewById(R.id.toolbar_loan_application_user_details);
        nextBtn = view.findViewById(R.id.txt_next_two);
        etxtFirstName = view.findViewById(R.id.etxt_first_name);
        etxtSecondName = view.findViewById(R.id.etxt_lastname);
        etxteMaishaAcc = view.findViewById(R.id.etxt_emaisha_account);




        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Apply for Loan");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);


        return view;
    }
}