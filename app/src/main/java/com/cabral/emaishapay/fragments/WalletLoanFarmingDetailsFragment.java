package com.cabral.emaishapay.fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
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


public class WalletLoanFarmingDetailsFragment extends Fragment {

    Toolbar toolbar;
    Button previousBtn, nextBtn;
    TextView harvesting_unit_txt, per_harvesting_unit_txt, txtCrops, txtPoultry,txtPiggery, txtExperiencePoultryMonths,txtExperiencePiggeryMonths,txtExperiencePiggeryYears, txtExperiencePoultryYears;
    Spinner harvesting_unit_spn, crop_spn, from_insurance_spn, poultryFarmVetPersonnelsp, piggeryFarmVetPersonnelsp ;
    EditText crop_area_edt, expected_yield_edt, expected_revenue_edt;
    CheckBox equipments_cb, seeds_cb, Fertilizers_cb, crop_protection_cb;
    Bundle localBundle;
    private StateProgressBar loanProgressBarId,loanApplicationStateProgressBar;
    String[] descriptionData = {"Loan\nDetails", "Farming\nDetails", "Preview", "KYC\nDetails"};
    String[] descriptionData2 = {"User\nDetails","Loan\nDetails", "Farming\nDetails", "Preview", "KYC\nDetails"};
    LoanApplication loanApplication;
    Float interest;
    AppBarConfiguration appBarConfiguration;
    LinearLayout layoutCrop,layoutPoultry,layoutPiggery,layoutVetDetails;
    ConstraintLayout layoutRBCrop,layoutRBPoultry,layoutRBPiggery;
    ImageButton rbCrop,rbPoultry,rbPiggery;
    View viewCropsSelected,viewPoultrySelected,viewPiggerySelected;

    public WalletLoanFarmingDetailsFragment(Bundle bundle) {
       this.localBundle=bundle;
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


        loanProgressBarId.setStateDescriptionData(descriptionData);
        loanProgressBarId.setStateDescriptionTypeface("fonts/JosefinSans-SemiBold.ttf");

        //Second hidden progress bar for loan application with 5 states
        loanApplicationStateProgressBar = view.findViewById(R.id.loan_application_state_progress_bar_user_details);
        loanApplicationStateProgressBar.setStateDescriptionData(descriptionData2);
        loanApplicationStateProgressBar.setStateDescriptionTypeface("fonts/JosefinSans-SemiBold.ttf");

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Apply for Loan");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);



        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
         if(localBundle != null){
            loanApplication= (LoanApplication) localBundle.getSerializable("loanApplication");
            interest=localBundle.getFloat("interest");
        }


        rbCrop.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
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
        rbPoultry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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


        rbPiggery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
//                navController.navigate(R.id.action_walletLoanFarmingDetailsFragment_to_walletLoanPreviewRequestFragment,bundle);

                Fragment fragment = new WalletLoanPreviewRequestFragment();
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
        });


    }

    }