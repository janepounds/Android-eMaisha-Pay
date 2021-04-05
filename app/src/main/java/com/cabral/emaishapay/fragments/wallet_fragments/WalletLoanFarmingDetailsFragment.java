package com.cabral.emaishapay.fragments.wallet_fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.ui.AppBarConfiguration;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.models.LoanApplication;

import java.security.acl.LastOwnerException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;


public class WalletLoanFarmingDetailsFragment extends Fragment {
    private static final String TAG = "WalletLoanFarmingDetail";

    Toolbar toolbar;
    Button previousBtn, nextBtn;
    TextView harvesting_unit_txt, per_harvesting_unit_txt, txtCrops, txtPoultry,txtPiggery, txtExperiencePoultryMonths,txtExperiencePiggeryMonths,txtExperiencePiggeryYears, txtExperiencePoultryYears,tv_poultry_date_purchased,tv_poultry_date_of_hatch,et_poultry_expected_disposal;
    Spinner harvesting_unit_spn, crop_spn, from_insurance_spn, poultryFarmVetPersonnelsp, piggeryFarmVetPersonnelsp;
    EditText crop_area_edt, expected_yield_edt, expected_revenue_edt,et_poultry_no_of_birds,et_poultry_cost_per_chick,sp_poultry_experience,tv_poultry_experience_years,tv_poultry_experience_months,et_poultry_second_name,et_poultry_vet_first_name,et_piggery_vet_first_name,et_piggery_second_name,
    et_piggery_total_animals,et_piggery_females,et_poultry_no_of_birds_males,et_piggery_annual_revenue,et_piggery_experience;
    CheckBox equipments_cb, seeds_cb, Fertilizers_cb, crop_protection_cb,vaccination_cb,production_cb,mortality_records_cb,feed_consumption_cb,disease_incidences_cb,poultry_none_cb,poultry_feeds_cb,poultry_medication_cb,poultry_purchase_chicks_cb,poultry_shed_construction_cb,poultry_equipment_purchase_cb,
    selling_piglets_cb ,selling_breeding_cb,meat_production_cb ,feed_records_cb,incomes_expenses_cb ,medical_records_cb,breeding_records_cb, piggery_none_cb, piggery_feeds_cb, piggery_medication_cb,piggery_equipment_purchase_cb, breeding_stock_purchase_cb ;

    private StateProgressBar loanProgressBarId,loanApplicationStateProgressBar;
    String[] descriptionData = {"Loan\nDetails", "Farming\nDetails", "Preview", "KYC\nDetails"};
    String[] descriptionData2 = {"User\nDetails","Loan\nDetails", "Farming\nDetails", "Preview", "KYC\nDetails"};
    LoanApplication loanApplication;
    Float interest;
    AppBarConfiguration appBarConfiguration;
    LinearLayout layoutCrop,layoutPoultry,layoutPiggery,layoutVetDetails,layoutVetDetailsPiggery;
    ConstraintLayout layoutRBCrop,layoutRBPoultry,layoutRBPiggery;
    ImageButton rbCrop,rbPoultry,rbPiggery;
    View viewCropsSelected,viewPoultrySelected,viewPiggerySelected;
    Spinner sp_poultry_type_of_birds,sp_poultry_source,sp_poultry_housing_system,sp_poultry_source_of_seeds,sp_poultry_farm_vet_personnel,
            sp_piggery_source_of_seeds,sp_piggery_farm_vet_personnel,sp_poultry_qualifications,sp_piggery_qualifications;

    private String title;
    final String applicantType="Applicant_Type";

    public WalletLoanFarmingDetailsFragment() {
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
        this.loanApplication= (LoanApplication) getArguments().getSerializable("loanApplication");
        this.title=getArguments().getString(applicantType);
        this.interest=getArguments().getFloat("interest");

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

        layoutCrop= view.findViewById(R.id.layout_form_crops);
        layoutPoultry= view.findViewById(R.id.layout_form_poultry);
        layoutPiggery= view.findViewById(R.id.layout_form_piggery);

        layoutRBCrop= view.findViewById(R.id.layout_crops);
        layoutRBPoultry= view.findViewById(R.id.layout_poultry);
        layoutRBPiggery= view.findViewById(R.id.layout_piggery);
        rbCrop= view.findViewById(R.id.radio_btn_crops);
        rbPoultry= view.findViewById(R.id.radio_btn_poultry);
        rbPiggery= view.findViewById(R.id.radio_btn_piggery);

        txtCrops= view.findViewById(R.id.text_crops);
        txtPoultry= view.findViewById(R.id.text_poultry);
        txtPiggery= view.findViewById(R.id.text_piggery);

        viewCropsSelected= view.findViewById(R.id.crops_selected);
        viewPoultrySelected= view.findViewById(R.id.poultry_selected);
        viewPiggerySelected= view.findViewById(R.id.piggery_selected);

        txtExperiencePoultryMonths = view.findViewById(R.id.tv_poultry_experience_months);
        txtExperiencePoultryYears = view.findViewById(R.id.tv_poultry_experience_years);

        txtExperiencePiggeryMonths = view.findViewById(R.id.tv_piggery_experience_months);
        txtExperiencePiggeryYears = view.findViewById(R.id.tv_piggery_experience_years);

        poultryFarmVetPersonnelsp = view.findViewById(R.id.sp_poultry_farm_vet_personnel);
        piggeryFarmVetPersonnelsp = view.findViewById(R.id.sp_piggery_farm_vet_personnel);

        layoutVetDetails = view.findViewById(R.id.layout_poultry_vet_details);
        layoutVetDetailsPiggery = view.findViewById(R.id.layout_piggery_vet_details);

        sp_poultry_type_of_birds=view.findViewById(R.id.sp_poultry_type_of_birds);
        tv_poultry_date_of_hatch = view.findViewById(R.id.tv_poultry_date_of_hatch);
        tv_poultry_date_purchased = view.findViewById(R.id.tv_poultry_date_purchased);
        et_poultry_no_of_birds = view.findViewById(R.id.et_poultry_no_of_birds);
        et_poultry_cost_per_chick = view.findViewById(R.id.et_poultry_cost_per_chick);
        sp_poultry_source = view.findViewById(R.id.sp_poultry_source);
        et_poultry_expected_disposal = view.findViewById(R.id.et_poultry_expected_disposal);
        sp_poultry_housing_system = view.findViewById(R.id.sp_poultry_housing_system);
        sp_poultry_source_of_seeds = view.findViewById(R.id.sp_poultry_source_of_seeds);
        sp_poultry_experience = view.findViewById(R.id.sp_poultry_experience);
        sp_poultry_farm_vet_personnel = view.findViewById(R.id.sp_poultry_farm_vet_personnel);
        et_poultry_vet_first_name = view.findViewById(R.id.et_poultry_vet_first_name);
        et_poultry_second_name = view.findViewById(R.id.et_poultry_second_name);
        sp_poultry_qualifications = view.findViewById(R.id.sp_poultry_qualifications);
        vaccination_cb = view.findViewById(R.id.vaccination_cb);
        production_cb = view.findViewById(R.id.production_cb);
        mortality_records_cb = view.findViewById(R.id.mortality_records_cb);
        feed_consumption_cb = view.findViewById(R.id.feed_consumption_cb);
        disease_incidences_cb = view.findViewById(R.id.disease_incidences_cb);

        poultry_feeds_cb = view.findViewById(R.id.poultry_feeds_cb);
        poultry_medication_cb = view.findViewById(R.id.poultry_medication_cb);
        poultry_purchase_chicks_cb = view.findViewById(R.id.poultry_purchase_chicks_cb);
        poultry_shed_construction_cb = view.findViewById(R.id.poultry_shed_construction_cb);
        poultry_equipment_purchase_cb = view.findViewById(R.id.poultry_equipment_purchase_cb);


        et_piggery_total_animals = view.findViewById(R.id.et_piggery_total_animals);
        et_piggery_females = view.findViewById(R.id.et_piggery_females);
        et_poultry_no_of_birds_males = view.findViewById(R.id.et_poultry_no_of_birds_males);
        et_piggery_annual_revenue = view.findViewById(R.id.et_piggery_annual_revenue);
        et_piggery_experience = view.findViewById(R.id.et_piggery_experience);
        sp_piggery_source_of_seeds = view.findViewById(R.id.sp_piggery_source_of_seeds);
        sp_piggery_farm_vet_personnel = view.findViewById(R.id.sp_piggery_farm_vet_personnel);
        et_piggery_vet_first_name = view.findViewById(R.id.et_piggery_vet_first_name);
        et_piggery_second_name = view.findViewById(R.id.et_piggery_second_name);
        sp_piggery_qualifications = view.findViewById(R.id.sp_piggery_qualifications);
        selling_piglets_cb = view.findViewById(R.id.selling_piglets_cb);
        selling_breeding_cb = view.findViewById(R.id.selling_breeding_cb);
        meat_production_cb = view.findViewById(R.id.meat_production_cb);
        feed_records_cb = view.findViewById(R.id.feed_records_cb);
        incomes_expenses_cb = view.findViewById(R.id.incomes_expenses_cb);
        medical_records_cb = view.findViewById(R.id.medical_records_cb);
        breeding_records_cb = view.findViewById(R.id.breeding_records_cb);

        piggery_feeds_cb = view.findViewById(R.id.piggery_feeds_cb);
        piggery_medication_cb = view.findViewById(R.id.piggery_medication_cb);
        piggery_equipment_purchase_cb = view.findViewById(R.id.piggery_equipment_purchase_cb);
        breeding_stock_purchase_cb = view.findViewById(R.id.breeding_stock_purchase_cb);




        loanApplicationStateProgressBar = view.findViewById(R.id.loan_application_state_progress_bar_farming_details);

        if(title.equalsIgnoreCase("Merchant Loan Details")){
            loanApplicationStateProgressBar.setVisibility(View.VISIBLE);
            loanProgressBarId.setVisibility(View.GONE);
            loanApplicationStateProgressBar.setStateDescriptionData(descriptionData2);
            loanApplicationStateProgressBar.setStateDescriptionTypeface("fonts/JosefinSans-SemiBold.ttf");


        }else {
            loanProgressBarId.setStateDescriptionData(descriptionData);
            loanProgressBarId.setStateDescriptionTypeface("fonts/JosefinSans-SemiBold.ttf");

        }


        //Second hidden progress bar for loan application with 5 states

//        loanApplicationStateProgressBar.setStateDescriptionData(descriptionData2);
//        loanApplicationStateProgressBar.setStateDescriptionTypeface("fonts/JosefinSans-SemiBold.ttf");

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Apply for Loan");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);



        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

           tv_poultry_date_of_hatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDatePicker2(tv_poultry_date_of_hatch, getActivity());
            }
        });
        tv_poultry_date_purchased.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDatePicker2(tv_poultry_date_purchased, getActivity());
            }
        });

           et_poultry_expected_disposal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDatePicker2(et_poultry_expected_disposal, getActivity());
            }
        });

        rbCrop.setSelected(true);
        layoutRBCrop.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 rbCrop.setSelected(true);
                 rbPoultry.setSelected(false);
                 rbPiggery.setSelected(false);
                 viewCropsSelected.setVisibility(View.VISIBLE);
                 layoutCrop.setVisibility(View.VISIBLE);
                 layoutPoultry.setVisibility(View.GONE);
                 layoutPiggery.setVisibility(View.GONE);
                 viewPoultrySelected.setVisibility(View.INVISIBLE);
                 viewPiggerySelected.setVisibility(View.INVISIBLE);
                 rbCrop.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.radio_button_selected, null));
                 rbPoultry.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.radio_button_not_selected, null));
                 rbPiggery.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.radio_button_not_selected, null));
                 rbPoultry.setAlpha((float) 0.5);
                 rbPiggery.setAlpha((float) 0.5);
                 rbCrop.setAlpha((float) 1.0);
                 txtPoultry.setAlpha((float) 0.5);
                 txtPiggery.setAlpha((float) 0.5);
                 txtCrops.setAlpha((float) 1.0);
             }
         });
        layoutRBPoultry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rbPoultry.setSelected(true);
                rbCrop.setSelected(false);
                rbPiggery.setSelected(false);
                viewPoultrySelected.setVisibility(View.VISIBLE);
                layoutPoultry.setVisibility(View.VISIBLE);
                layoutCrop.setVisibility(View.GONE);
                layoutPiggery.setVisibility(View.GONE);
                viewCropsSelected.setVisibility(View.INVISIBLE);
                viewPiggerySelected.setVisibility(View.INVISIBLE);
                rbPoultry.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.radio_button_selected, null));
                rbCrop.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.radio_button_not_selected, null));
                rbPiggery.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.radio_button_not_selected, null));
                rbPiggery.setAlpha((float) 0.5);
                rbCrop.setAlpha((float) 0.5);
                rbPoultry.setAlpha((float) 1.0);
                txtCrops.setAlpha((float) 0.5);
                txtPiggery.setAlpha((float) 0.5);
                txtPoultry.setAlpha((float) 1.0);
            }
        });


        layoutRBPiggery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        rbPiggery.setSelected(true);
                        rbCrop.setSelected(false);
                        rbPoultry.setSelected(false);
                        viewPiggerySelected.setVisibility(View.VISIBLE);
                        layoutPiggery.setVisibility(View.VISIBLE);
                        layoutCrop.setVisibility(View.GONE);
                        layoutPoultry.setVisibility(View.GONE);
                        viewCropsSelected.setVisibility(View.INVISIBLE);
                        viewPoultrySelected.setVisibility(View.INVISIBLE);
                        rbPiggery.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.radio_button_selected, null));
                        rbCrop.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.radio_button_not_selected, null));
                        rbPoultry.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.radio_button_not_selected, null));
                        rbPoultry.setAlpha((float) 0.5);
                        rbCrop.setAlpha((float) 0.5);
                        rbPiggery.setAlpha((float) 1.0);
                        txtCrops.setAlpha((float) 0.5);
                        txtPoultry.setAlpha((float) 0.5);
                        txtPiggery.setAlpha((float) 1.0);
                    }
                });

        txtExperiencePoultryMonths.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorStateList oldColors =  txtExperiencePoultryMonths.getTextColors();
                txtExperiencePoultryMonths.setTextColor(getResources().getColor(R.color.white));
                txtExperiencePoultryMonths.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                txtExperiencePoultryYears.setTextColor(oldColors);
                txtExperiencePoultryYears.setBackgroundResource(0);
            }
        });

        txtExperiencePoultryYears.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorStateList oldColors =  txtExperiencePoultryYears.getTextColors();
                txtExperiencePoultryYears.setTextColor(getResources().getColor(R.color.white));
                txtExperiencePoultryYears.setBackgroundResource(R.drawable.edittext_right_corners_green);
                txtExperiencePoultryMonths.setTextColor(oldColors);
                txtExperiencePoultryMonths.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
        });


        txtExperiencePiggeryMonths.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorStateList oldColors =  txtExperiencePiggeryMonths.getTextColors();
                txtExperiencePiggeryMonths.setTextColor(getResources().getColor(R.color.white));
                txtExperiencePiggeryMonths.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                txtExperiencePiggeryYears.setTextColor(oldColors);
                txtExperiencePiggeryYears.setBackgroundResource(0);
            }
        });

        txtExperiencePiggeryYears.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorStateList oldColors =  txtExperiencePoultryYears.getTextColors();
                txtExperiencePiggeryYears.setTextColor(getResources().getColor(R.color.white));
                txtExperiencePiggeryYears.setBackgroundResource(R.drawable.edittext_right_corners_green);
                txtExperiencePiggeryMonths.setTextColor(oldColors);
                txtExperiencePiggeryMonths.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
        });

        poultryFarmVetPersonnelsp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position==1){
                    layoutVetDetails.setVisibility(View.VISIBLE);
                }
                else {
                    layoutVetDetails.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        piggeryFarmVetPersonnelsp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position==1){
                    layoutVetDetailsPiggery.setVisibility(View.VISIBLE);
                }
                else {
                    layoutVetDetailsPiggery.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



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

        previousBtn.setOnClickListener(view2 -> getParentFragmentManager().popBackStack());


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rbCrop.isSelected()){
                    loanApplication.setCheck_selected("Crops");
                    loanApplication.setCrop_data(new LoanApplication.Crop());
                    loanApplication.getCrop_data().setCrop(crop_spn.getSelectedItem().toString());
                    if(crop_area_edt.getText().toString().isEmpty()){
                        loanApplication.getCrop_data().setCrop_area(0);
                    }else{
                        loanApplication.getCrop_data().setCrop_area( Double.parseDouble(crop_area_edt.getText().toString()));
                    }

                    loanApplication.getCrop_data().setCrop_area_unit(getString(R.string.default_crop_area_units));
                    loanApplication.getCrop_data().setYeild_units(harvesting_unit_spn.getSelectedItem().toString());
                    if(expected_yield_edt.getText().toString().isEmpty()){
                        loanApplication.getCrop_data().setExpected_yield(0);
                    }else{
                        loanApplication.getCrop_data().setExpected_yield(Double.parseDouble(expected_yield_edt.getText().toString()));
                    }

                    if(expected_revenue_edt.getText().toString().isEmpty()){

                        loanApplication.getCrop_data().setExpected_revenue(0);

                    }else{
                        loanApplication.getCrop_data().setExpected_revenue(Integer.parseInt(expected_revenue_edt.getText().toString()));

                    }


                    if(from_insurance_spn.getSelectedItem().toString().equalsIgnoreCase("yes")){
                        loanApplication.getCrop_data().setFrom_insurance(true);
                    }
                    loanApplication.getCrop_data().setPurpose_for_crop_protection(crop_protection_cb.isChecked());
                    loanApplication.getCrop_data().setPurpose_for_equipments(equipments_cb.isChecked());
                    loanApplication.getCrop_data().setPurpose_for_seeds(seeds_cb.isChecked());
                    loanApplication.getCrop_data().setPurpose_for_fetilizer(Fertilizers_cb.isChecked());

                }else if(rbPoultry.isSelected()){
                    loanApplication.setCheck_selected("Poultry");

                    loanApplication.setPoultry_data(new LoanApplication.Poultry());
                    loanApplication.getPoultry_data().setType_of_birds(sp_poultry_type_of_birds.getSelectedItem().toString());
                    loanApplication.getPoultry_data().setDate_of_hatch(tv_poultry_date_of_hatch.getText().toString());
                    loanApplication.getPoultry_data().setDate_purchased(tv_poultry_date_purchased.getText().toString());
                    loanApplication.getPoultry_data().setNo_of_birds_purchased(Integer.parseInt(et_poultry_no_of_birds.getText().toString()));
                    if(et_poultry_cost_per_chick.getText().toString().isEmpty()){
                        loanApplication.getPoultry_data().setCost_per_chick(0);
                    }else{
                        loanApplication.getPoultry_data().setCost_per_chick(Double.parseDouble(et_poultry_cost_per_chick.getText().toString()));
                    }

                    loanApplication.getPoultry_data().setSource(sp_poultry_source.getSelectedItem().toString());
                    loanApplication.getPoultry_data().setExpected_disposal(et_poultry_expected_disposal.getText().toString());
                    loanApplication.getPoultry_data().setHousing_system(sp_poultry_housing_system.getSelectedItem().toString());
                    loanApplication.getPoultry_data().setSource_of_feeds(sp_poultry_source_of_seeds.getSelectedItem().toString());
                    loanApplication.getPoultry_data().setExperience(sp_poultry_experience.getText().toString());
                    loanApplication.getPoultry_data().setFarm_vet_personnel(sp_poultry_farm_vet_personnel.getSelectedItem().toString());
                    loanApplication.getPoultry_data().setFarm_vet_personnel_firstname(et_poultry_vet_first_name.getText().toString());
                    loanApplication.getPoultry_data().setFarm_vet_personnel_lastname(et_poultry_second_name.getText().toString());
                    loanApplication.getPoultry_data().setFarm_vet_personnel_qualifications(sp_poultry_qualifications.getSelectedItem().toString());
                    loanApplication.getPoultry_data().setRecords_kept_vaccination(vaccination_cb.isChecked());
                    loanApplication.getPoultry_data().setRecords_kept_production(production_cb.isChecked());
                    loanApplication.getPoultry_data().setRecords_kept_mortality_records(mortality_records_cb.isChecked());
                    loanApplication.getPoultry_data().setRecords_kept_feed_consumption(production_cb.isChecked());
                    loanApplication.getPoultry_data().setRecords_kept_disease_incidences(disease_incidences_cb.isChecked());
                    loanApplication.getPoultry_data().setLoan_purpose_feeds(poultry_feeds_cb.isChecked());
                    loanApplication.getPoultry_data().setLoan_purpose_medication(poultry_medication_cb.isChecked());
                    loanApplication.getPoultry_data().setLoan_purpose_purchase_chicks(poultry_purchase_chicks_cb.isChecked());
                    loanApplication.getPoultry_data().setLoan_purpose_shed_construction(poultry_shed_construction_cb.isChecked());
                    loanApplication.getPoultry_data().setLoan_purpose_equipment_purchase(poultry_equipment_purchase_cb.isChecked());


                    Log.d(TAG, "onClick: "+loanApplication.getPoultry_data().getType_of_birds());


                }else if(rbPiggery.isSelected()){
                    loanApplication.setCheck_selected("Piggery");
                    loanApplication.setPiggery_data(new LoanApplication.Piggery());
                  loanApplication.getPiggery_data().setTotal_Animals(Integer.parseInt(et_piggery_total_animals.getText().toString()));
                  loanApplication.getPiggery_data().setNo_of_females(Integer.parseInt(et_piggery_females.getText().toString()));
                  loanApplication.getPiggery_data().setNo_of_males(Integer.parseInt(et_poultry_no_of_birds_males.getText().toString()));

                  if(et_piggery_annual_revenue.getText().toString().isEmpty()) {
                      loanApplication.getPiggery_data().setAnnual_revenue(0);
                  }else{
                      loanApplication.getPiggery_data().setAnnual_revenue(Double.parseDouble(et_piggery_annual_revenue.getText().toString()));
                  }
                  loanApplication.getPiggery_data().setExperience(et_piggery_experience.getText().toString());
                  loanApplication.getPiggery_data().setSource_of_feeds(sp_piggery_source_of_seeds.getSelectedItem().toString());
                  loanApplication.getPiggery_data().setFarm_vet_personnel(sp_piggery_farm_vet_personnel.getSelectedItem().toString());
                  loanApplication.getPiggery_data().setFarm_vet_personnel_firstname(et_piggery_vet_first_name.getText().toString());
                  loanApplication.getPiggery_data().setFarm_vet_personnel_lastname(et_piggery_second_name.getText().toString());
                  loanApplication.getPiggery_data().setFarm_vet_personnel_qualifications(sp_piggery_qualifications.getSelectedItem().toString());
                  loanApplication.getPiggery_data().setBusiness_model_selling_piglets(selling_piglets_cb.isChecked());
                  loanApplication.getPiggery_data().setBusiness_model_meat_production(meat_production_cb.isChecked());
                  loanApplication.getPiggery_data().setBusiness_model_selling_breeding_stock(selling_breeding_cb.isChecked());
                  loanApplication.getPiggery_data().setRecords_kept_feeds(feed_records_cb.isChecked());
                  loanApplication.getPiggery_data().setRecords_kept_income_expenses(incomes_expenses_cb.isChecked());
                  loanApplication.getPiggery_data().setRecords_kept_medical(medical_records_cb.isChecked());
                  loanApplication.getPiggery_data().setRecords_kept_breeding(breeding_records_cb.isChecked());
                  loanApplication.getPiggery_data().setLoan_purpose_feeds(piggery_feeds_cb.isChecked());
                  loanApplication.getPiggery_data().setLoan_purpose_medication(piggery_medication_cb.isChecked());
                  loanApplication.getPiggery_data().setLoan_purpose_equipment_purchase(piggery_equipment_purchase_cb.isChecked());
                  loanApplication.getPiggery_data().setLoan_purpose_breeding_stock_purchase(breeding_stock_purchase_cb.isChecked());


                }



                //expected_revenue_edt
                Bundle bundle = new Bundle();
                bundle.putFloat("interest", interest);
                bundle.putSerializable("loanApplication", loanApplication);

                if(title.equalsIgnoreCase("Merchant Loan Details")){
                    //To WalletLoanPreviewRequestFragment
                    bundle.putString(applicantType, getString(R.string.merchant_loan_details));
                    WalletHomeActivity.navController.navigate(R.id.action_walletLoanFarmingDetailsFragment_to_walletLoanPreviewRequestFragment,bundle);


                }else{
                    bundle.putString(applicantType, getString(R.string.default_loan_details));
                    WalletHomeActivity.navController.navigate(R.id.action_walletLoanFarmingDetailsFragment_to_walletLoanPreviewRequestFragment,bundle);
                }
        }
        });


    }
    public void addDatePicker2(final TextView ed_, final Context context) {
        ed_.setOnClickListener(view -> {
            Calendar mCurrentDate = Calendar.getInstance();
            int mYear = mCurrentDate.get(Calendar.YEAR);
            int mMonth = mCurrentDate.get(Calendar.MONTH);
            int mDay = mCurrentDate.get(Calendar.DAY_OF_MONTH);

            final DatePickerDialog mDatePicker = new DatePickerDialog(context, (datePicker, selectedYear, selectedMonth, selectedDay) -> {

                int month = selectedMonth + 1;
                NumberFormat formatter = new DecimalFormat("00");
                ed_.setText(selectedYear + "-" + formatter.format(month) + "-" + formatter.format(selectedDay));


            }, mYear, mMonth, mDay);

            mDatePicker.show();
        });
        ed_.setInputType(InputType.TYPE_NULL);
    }
    }