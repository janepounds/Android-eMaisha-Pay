package com.cabral.emaishapay.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.models.LoanApplication;

import java.text.NumberFormat;

public class WalletLoanPreviewRequestFragment extends Fragment {
    private static final String TAG = "WalletLoanPreviewRequest";

    private Context context;

    String[] descriptionData = {"Loan\nDetails", "Farming\nDetails", "Preview", "KYC\nDetails"};
    String[] descriptionData2 = {"User\nDetails","Loan\nDetails", "Farming\nDetails", "Preview", "KYC\nDetails"};
    LoanApplication loanApplication;
    ProgressDialog dialog;
    private String title;

    private Toolbar toolbar;
    private StateProgressBar loanProgressBarId,loanApplicationStateProgressBar;
    private TextView textViewLoanPreviewAmount, textViewLoanPreviewInterestRate, textViewLoanPreviewDuration, textViewLoanPreviewDueDate,
            textViewLoanPreviewDueAmount, loan_type_or_schedule_txt,textViewErrorMessage, loan_purpose_txt;
    private Button btnLoanNextStep, btnPrevious;

    public WalletLoanPreviewRequestFragment(String title){
        this.title = title;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet_loan_preview_request, container, false);

        toolbar = view.findViewById(R.id.toolbar_wallet_loan_preview_request);
        loanProgressBarId = view.findViewById(R.id.loan_preview_request_progress_bar_id);
        textViewLoanPreviewAmount = view.findViewById(R.id.text_view_loan_preview_amount);
        textViewLoanPreviewInterestRate = view.findViewById(R.id.text_view_loan_preview_interest_rate);
        textViewLoanPreviewDuration = view.findViewById(R.id.text_view_loan_preview_duration);
        textViewLoanPreviewDueDate = view.findViewById(R.id.text_view_loan_preview_due_date);
        textViewLoanPreviewDueAmount = view.findViewById(R.id.text_view_loan_preview_due_amount);
        loan_type_or_schedule_txt = view.findViewById(R.id.loan_type_or_schedule_txt);
        textViewErrorMessage = view.findViewById(R.id.text_view_error_message);
        loan_purpose_txt= view.findViewById(R.id.loan_purpose_txt);
        btnLoanNextStep = view.findViewById(R.id.btn_loan_next_step);
        btnPrevious = view.findViewById(R.id.previous_btn);

        loanApplicationStateProgressBar = view.findViewById(R.id.loan_application_state_progress_bar_confirm_details);
//        loanApplicationStateProgressBar.setStateDescriptionData(descriptionData2);

        if(title.equalsIgnoreCase("Merchant Loan Details")){
            loanApplicationStateProgressBar.setVisibility(View.VISIBLE);
            loanProgressBarId.setVisibility(View.GONE);
            loanApplicationStateProgressBar.setStateDescriptionData(descriptionData2);
            loanApplicationStateProgressBar.setStateDescriptionTypeface("fonts/JosefinSans-SemiBold.ttf");

        }else{
            loanProgressBarId.setStateDescriptionData(descriptionData);
            loanProgressBarId.setStateDescriptionTypeface("fonts/JosefinSans-SemiBold.ttf");
        }

       if(getArguments() != null){
           loanApplication = (LoanApplication) getArguments().getSerializable("loanApplication");
           float interest=getArguments().getFloat("interest");
           getArguments().clear();
           loanApplication.setInterestRate((float) interest);
       }

        if (loanApplication != null) {

            initializeActivity();
        }

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Apply for Loan");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
          btnPrevious.setOnClickListener(view1 -> getParentFragmentManager().popBackStack());

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void initializeActivity() {
        dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(false);

        textViewLoanPreviewAmount.setText("UGX " + NumberFormat.getInstance().format(loanApplication.getAmount()));
        textViewLoanPreviewInterestRate.setText(NumberFormat.getInstance().format(loanApplication.getInterestRate()) + "%");
        loan_type_or_schedule_txt.setText(loanApplication.getLoanType());
        textViewLoanPreviewDuration.setText(loanApplication.getDurationWithUnits());

        textViewLoanPreviewDueDate.setText(loanApplication.computeDueDate());
        textViewLoanPreviewDueAmount.setText("UGX " + NumberFormat.getInstance().format(loanApplication.computeDueAmount()));
        if(loanApplication.getCrop_data()!=null){
            if(loanApplication.getCrop_data().isPurpose_for_fetilizer()){
                loan_purpose_txt.setText(getString(R.string.fertilizer_title));
                if(loan_purpose_txt.getText().toString().isEmpty())
                    loan_purpose_txt.setText(loan_purpose_txt.getText().toString()+", "+getString(R.string.fertilizer_title));
                else
                    loan_purpose_txt.setText(getString(R.string.fertilizer_title));
            }
            if(loanApplication.getCrop_data().isPurpose_for_crop_protection()){
                if(loan_purpose_txt.getText().toString().isEmpty())
                    loan_purpose_txt.setText(loan_purpose_txt.getText().toString()+", "+getString(R.string.crop_protection));
                else
                    loan_purpose_txt.setText(getString(R.string.crop_protection));
            }
            if(loanApplication.getCrop_data().isPurpose_for_equipments()){
                if(loan_purpose_txt.getText().toString().isEmpty())
                    loan_purpose_txt.setText(loan_purpose_txt.getText().toString()+", "+getString(R.string.equipments));
                else
                    loan_purpose_txt.setText(getString(R.string.equipments));
            }
            if(loanApplication.getCrop_data().isPurpose_for_seeds()){
                if(loan_purpose_txt.getText().toString().isEmpty())
                    loan_purpose_txt.setText(loan_purpose_txt.getText().toString()+", "+getString(R.string.seeds));
                else
                    loan_purpose_txt.setText(getString(R.string.seeds));
            }
        }
        else if(loanApplication.getPiggery_data()!=null){
            if(loanApplication.getPiggery_data().isLoan_purpose_breeding_stock_purchase()){
                if(loan_purpose_txt.getText().toString().isEmpty())
                    loan_purpose_txt.setText(loan_purpose_txt.getText().toString()+", "+getString(R.string.breeding_stock_purchase));
                else
                    loan_purpose_txt.setText(getString(R.string.breeding_stock_purchase));
            }
            if(loanApplication.getPiggery_data().isLoan_purpose_feeds()){
                if(loan_purpose_txt.getText().toString().isEmpty())
                    loan_purpose_txt.setText(loan_purpose_txt.getText().toString()+", "+getString(R.string.feeds));
                else
                    loan_purpose_txt.setText(getString(R.string.feeds));
            }
            if(loanApplication.getPiggery_data().isLoan_purpose_medication()){
                if(loan_purpose_txt.getText().toString().isEmpty())
                    loan_purpose_txt.setText(loan_purpose_txt.getText().toString()+", "+getString(R.string.medication));
                else
                    loan_purpose_txt.setText(getString(R.string.medication));
            }
            if(loanApplication.getPiggery_data().isLoan_purpose_equipment_purchase()){
                if(loan_purpose_txt.getText().toString().isEmpty())
                    loan_purpose_txt.setText(loan_purpose_txt.getText().toString()+", "+getString(R.string.equipments));
                else
                    loan_purpose_txt.setText(getString(R.string.equipments));
            }
        }
        else if(loanApplication.getPoultry_data()!=null){
            if(loanApplication.getPoultry_data().isLoan_purpose_purchase_chicks()){
                if(loan_purpose_txt.getText().toString().isEmpty())
                    loan_purpose_txt.setText(loan_purpose_txt.getText().toString()+", "+getString(R.string.chick_purchase));
                else
                    loan_purpose_txt.setText(getString(R.string.chick_purchase));
            }
            if(loanApplication.getPoultry_data().isLoan_purpose_feeds()){
                if(loan_purpose_txt.getText().toString().isEmpty())
                    loan_purpose_txt.setText(loan_purpose_txt.getText().toString()+", "+getString(R.string.feeds));
                else
                    loan_purpose_txt.setText(getString(R.string.feeds));
            }
            if(loanApplication.getPoultry_data().isLoan_purpose_medication()){
                if(loan_purpose_txt.getText().toString().isEmpty())
                    loan_purpose_txt.setText(loan_purpose_txt.getText().toString()+", "+getString(R.string.medication));
                else
                    loan_purpose_txt.setText(getString(R.string.medication));
            }
            if(loanApplication.getPoultry_data().isLoan_purpose_equipment_purchase()){
                if(loan_purpose_txt.getText().toString().isEmpty())
                    loan_purpose_txt.setText(loan_purpose_txt.getText().toString()+", "+getString(R.string.equipments));
                else
                    loan_purpose_txt.setText(getString(R.string.equipments));
            }
            if(loanApplication.getPoultry_data().isLoan_purpose_shed_construction()){
                if(loan_purpose_txt.getText().toString().isEmpty())
                    loan_purpose_txt.setText(loan_purpose_txt.getText().toString()+", "+getString(R.string.shed_construction));
                else
                    loan_purpose_txt.setText(getString(R.string.shed_construction));
            }
        }


        btnPrevious.setOnClickListener(view1 -> getParentFragmentManager().popBackStack());

        btnLoanNextStep.setOnClickListener(view -> AddPhotos());
    }

    public void AddPhotos() {

        Bundle bundle = new Bundle();
        bundle.putSerializable("loanApplication", loanApplication);
       // navController.navigate(R.id.action_walletLoanPreviewRequestFragment_to_walletLoanAppPhotosFragment,bundle);

        if(title.equalsIgnoreCase("Merchant Loan Details")){
            Fragment fragment = new WalletLoanKycDetailsFragment(getString(R.string.merchant_loan_details));
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            if (((WalletHomeActivity) getActivity()).currentFragment != null)
                fragmentManager.beginTransaction()
                        .hide(((WalletHomeActivity) getActivity()).currentFragment)
                        .add(R.id.wallet_home_container, fragment)
                        .addToBackStack(null).commit();
            else
                fragmentManager.beginTransaction()
                        .add(R.id.wallet_home_container, fragment)
                        .addToBackStack(null).commit();
        }else {

            Fragment fragment = new WalletLoanKycDetailsFragment(getString(R.string.default_loan_details));
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            if (((WalletHomeActivity) getActivity()).currentFragment != null)
                fragmentManager.beginTransaction()
                        .hide(((WalletHomeActivity) getActivity()).currentFragment)
                        .add(R.id.wallet_home_container, fragment)
                        .addToBackStack(null).commit();
            else
                fragmentManager.beginTransaction()
                        .add(R.id.wallet_home_container, fragment)
                        .addToBackStack(null).commit();
        }


    }
}
