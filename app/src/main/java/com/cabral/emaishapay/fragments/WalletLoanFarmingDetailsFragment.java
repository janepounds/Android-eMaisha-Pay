package com.cabral.emaishapay.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.kofigyan.stateprogressbar.StateProgressBar;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.models.LoanApplication;


public class WalletLoanFarmingDetailsFragment extends Fragment {

    Toolbar toolbar;
    Button previousBtn, nextBtn;
    TextView harvesting_unit_txt, per_harvesting_unit_txt;
    Spinner harvesting_unit_spn, crop_spn, from_insurance_spn;
    EditText crop_area_edt, expected_yield_edt, expected_revenue_edt;
    CheckBox equipments_cb, seeds_cb, Fertilizers_cb, crop_protection_cb;
    NavController navController;
    private StateProgressBar loanProgressBarId;
    String[] descriptionData = {"Loan\nDetails", "Farming\nDetails", "Preview", "KYC\nDetails"};
    LoanApplication loanApplication;
    Float interest;
    AppBarConfiguration appBarConfiguration;

    public WalletLoanFarmingDetailsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_wallet_loan_farming_details, container, false);

        toolbar = view.findViewById(R.id.toolbar_wallet_loan_app_initiate);
        previousBtn = view.findViewById(R.id.btn_previous);
        nextBtn = view.findViewById(R.id.btn_loan_next_step);
        loanProgressBarId = view.findViewById(R.id.loan_progress_bar_id);

        from_insurance_spn= view.findViewById(R.id.from_insurance_spn);
        harvesting_unit_spn= view.findViewById(R.id.harvesting_unit_spn);
        crop_spn= view.findViewById(R.id.crop_spn);
        harvesting_unit_txt= view.findViewById(R.id.harvesting_unit_txt);
        per_harvesting_unit_txt= view.findViewById(R.id.per_harvesting_unit_txt);

        expected_revenue_edt= view.findViewById(R.id.expected_revenue_edt);
        expected_yield_edt= view.findViewById(R.id.expected_yield_edt);
        crop_area_edt= view.findViewById(R.id.crop_area_edt);
        Fertilizers_cb= view.findViewById(R.id.Fertilizers_cb);
        crop_protection_cb= view.findViewById(R.id.crop_protection_cb);
        equipments_cb= view.findViewById(R.id.equipments_cb);
        seeds_cb= view.findViewById(R.id.seeds_cb);

        loanProgressBarId.setStateDescriptionData(descriptionData);
        loanProgressBarId.setStateDescriptionTypeface("fonts/JosefinSans-SemiBold.ttf");
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(view);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        if(getArguments() != null){
            loanApplication= (LoanApplication) getArguments().getSerializable("loanApplication");
            interest=getArguments().getFloat("interest");
        }

        previousBtn.setOnClickListener(view2 -> navController.popBackStack());


        nextBtn.setOnClickListener(view1 -> navController.navigate(R.id.action_walletLoanFarmingDetailsFragment_to_walletLoanPreviewRequestFragment));

        harvesting_unit_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String selectedItem=harvesting_unit_spn.getSelectedItem().toString();
//
//                if(!selectedItem.isEmpty()){
//                    harvesting_unit_txt.setText(selectedItem);
//                    per_harvesting_unit_txt.setText("/"+selectedItem);
//                }

                if(position==0){
                    harvesting_unit_txt.setText(getString(R.string.units));
                    per_harvesting_unit_txt.setText(getString(R.string.per_unit));
                }
                else if(position==1){
                    harvesting_unit_txt.setText("Box(es)");
                    per_harvesting_unit_txt.setText("/ box");
                }
                else if(position==2){
                    harvesting_unit_txt.setText("Kg");
                    per_harvesting_unit_txt.setText("/ kg");
                }
                else if(position==3){
                    harvesting_unit_txt.setText("Tonne(s)");
                    per_harvesting_unit_txt.setText("/ tonne");
                }
                else if(position==4){
                    harvesting_unit_txt.setText("Bushel(s)");
                    per_harvesting_unit_txt.setText("/ bushel");
                }
                else if(position==5){
                    harvesting_unit_txt.setText("Bag(s)");
                    per_harvesting_unit_txt.setText("/ bag");
                }
                else if(position==6){
                    harvesting_unit_txt.setText("Bunch(es)");
                    per_harvesting_unit_txt.setText("/ bunch");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        previousBtn.setOnClickListener(view2 -> navController.popBackStack());


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loanApplication.setCrop(crop_spn.getSelectedItem().toString());
                loanApplication.setCrop_area( Double.parseDouble(crop_area_edt.getText().toString()) );
                loanApplication.setCrop_area_unit(getString(R.string.default_crop_area_units));
                loanApplication.setYeild_units(harvesting_unit_spn.getSelectedItem().toString());
                loanApplication.setExpected_yield(Double.parseDouble(expected_yield_edt.getText().toString()));
                loanApplication.setExpected_revenue(Integer.parseInt(expected_revenue_edt.getText().toString()));

                if(from_insurance_spn.getSelectedItem().toString().equalsIgnoreCase("yes")){
                    loanApplication.setFrom_insurance(true);
                }
                loanApplication.setPurpose_for_crop_protection(crop_protection_cb.isChecked());
                loanApplication.setPurpose_for_equipments(equipments_cb.isChecked());
                loanApplication.setPurpose_for_seeds(seeds_cb.isChecked());
                loanApplication.setPurpose_for_fetilizer(Fertilizers_cb.isChecked());

                //expected_revenue_edt
                Bundle bundle = new Bundle();
                bundle.putFloat("interest", interest);
                bundle.putSerializable("loanApplication", loanApplication);
                navController.navigate(R.id.action_walletLoanFarmingDetailsFragment_to_walletLoanPreviewRequestFragment,bundle);
            }
        });


    }

    }