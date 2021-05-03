package com.cabral.emaishapay.fragments.auth_fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.AuthActivity;

import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.customs.DialogLoader;

import com.cabral.emaishapay.databinding.SignupFragmentBinding;
import com.cabral.emaishapay.models.SpinnerItem;
import com.cabral.emaishapay.models.SecurityQnsResponse;
import com.cabral.emaishapay.modelviews.SignUpModelView;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.cabral.emaishapay.network.db.entities.RegionDetails;
import com.cabral.emaishapay.utils.ValidateInputs;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpFragment  extends Fragment {
    private static final String TAG = "SignUp";
    private SignupFragmentBinding binding;


    private Context context;
    private String phoneNumber;
    DialogLoader dialogLoader;


    // Firebase auth object
    private int pickedDistrictId;
    private int pickedSubCountyId;
    private ArrayList<SpinnerItem> subCountyList = new ArrayList<>();
    private ArrayList<SpinnerItem> villageList = new ArrayList<>();
    
    @Override
    public void onAttach(@NonNull Context context) {
        this.context=context;
        phoneNumber=getArguments().getString("phone");
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.signup_fragment,container,false);


        View decorView = getActivity().getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final SignUpModelView viewModel = new ViewModelProvider(this).get(SignUpModelView.class);
        //get security qns
        subscribeToSecurityQns(viewModel.getSecurityQuestions());

        ArrayList<SpinnerItem> districtList = new ArrayList<>();

        subscribeDistrictList(viewModel.getDistricts(),districtList);

        binding.districtSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.districtSpinner.showDropDown();
                String districtInput =binding.districtSpinner.getText().toString();

                if(districtInput.length()<4)
                    return;

                viewModel.getRegionDetail(districtInput).observe(getViewLifecycleOwner(),myRegion->{
                    if(myRegion!=null){
                        pickedDistrictId=myRegion.getId();

                        subCountyList.clear();
                        subscribeSubcountyList(viewModel.getSubcountyDetails(String.valueOf(pickedDistrictId)));
                    }

                });


            }
        });

        binding.subCountySpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.subCountySpinner.showDropDown();
                String subCountyInput =binding.subCountySpinner.getText().toString();

                if(subCountyInput.length()<4)
                    return;

                viewModel.getRegionDetail(subCountyInput).observe(getViewLifecycleOwner(),myRegion->{
                    if(myRegion!=null){
                        pickedSubCountyId=myRegion.getId();

                        villageList.clear();
                        subscribeVillageList(viewModel.getVillageDetails(String.valueOf(pickedSubCountyId)));
                    }

                });


            }
        });


        dialogLoader = new DialogLoader(context);

        binding.textPrivacyPolicy.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_webview_fullscreen, null);
            dialog.setView(dialogView);
            dialog.setCancelable(true);

            final ImageButton dialog_button = dialogView.findViewById(R.id.dialog_button);
            final TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
            final WebView dialog_webView = dialogView.findViewById(R.id.dialog_webView);

            dialog_title.setText(getString(R.string.privacy_policy));


            String description = ConstantValues.PRIVACY_POLICY;
            String styleSheet = "<style> " +
                    "body{background:#eeeeee; margin:10; padding:10} " +
                    "p{color:#757575;} " +
                    "img{display:inline; height:auto; max-width:100%;}" +
                    "</style>";

            dialog_webView.setVerticalScrollBarEnabled(true);
            dialog_webView.setHorizontalScrollBarEnabled(false);
            dialog_webView.setBackgroundColor(Color.TRANSPARENT);
            dialog_webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            dialog_webView.loadDataWithBaseURL(null, styleSheet + description, "text/html", "utf-8", null);


            final AlertDialog alertDialog = dialog.create();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                alertDialog.getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            }

            dialog_button.setOnClickListener(v1 -> alertDialog.dismiss());

            alertDialog.show();

        });

        binding.textTermsOfService.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_webview_fullscreen, null);
            dialog.setView(dialogView);
            dialog.setCancelable(true);

            final ImageButton dialog_button = dialogView.findViewById(R.id.dialog_button);
            final TextView dialog_title = dialogView.findViewById(R.id.dialog_title);
            final WebView dialog_webView = dialogView.findViewById(R.id.dialog_webView);

            dialog_title.setText(getString(R.string.service_terms));


            String description = ConstantValues.TERMS_SERVICES;
            String styleSheet = "<style> " +
                    "body{background:#eeeeee; margin:10; padding:10} " +
                    "p{color:#757575;} " +
                    "img{display:inline; height:auto; max-width:100%;}" +
                    "</style>";

            dialog_webView.setVerticalScrollBarEnabled(true);
            dialog_webView.setHorizontalScrollBarEnabled(false);
            dialog_webView.setBackgroundColor(Color.TRANSPARENT);
            dialog_webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            dialog_webView.loadDataWithBaseURL(null, styleSheet + description, "text/html", "utf-8", null);


            final AlertDialog alertDialog = dialog.create();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                alertDialog.getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            }

            dialog_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            alertDialog.show();
        });

        // Handle Click event of signUpBtn Button
        binding.signUpBtn.setOnClickListener(v -> {
            // Validate Login Form Inputs
            boolean isValidData = validateForm();

            if (isValidData) {
                // Proceed User Registration
                binding.selectedSignUp.setVisibility(View.INVISIBLE);
                binding.selectedSecurityQns.setVisibility(View.VISIBLE);
                binding.textUserInfo.setAlpha((float) 0.6);
                binding.textSecurityQnsLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.layoutSignUp.setVisibility(View.GONE);
                binding.layoutSecurityQns.setVisibility(View.VISIBLE);

            }

        });

        binding.btnSubmitSecurityQn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                boolean isValidData = validateSecurityQns() && validateForm();
                if (isValidData) {
                    // Proceed User Registration with creating user PIN
                    Bundle args=new Bundle();

                    args.putInt("action",2);
                    args.putString("phone",phoneNumber);
                    args.putString("userFirstname",binding.userFirstname.getText().toString());
                    args.putString("userLastname",binding.userLastname.getText().toString());
                    args.putString("village",binding.villageSpinner.getText().toString());
                    args.putString("idType",binding.idType.getSelectedItem().toString());
                    args.putString("subCounty",binding.subCountySpinner.getText().toString());
                    args.putString("district",binding.districtSpinner.getText().toString());
                    args.putString("idNo",binding.idNumber.getText().toString());
                    ;
                    args.putString("firstSecurityQn",binding.spFirstSecurityQn.getSelectedItem().toString());
                    args.putString("secondSecurityQn",binding.spSecondSecurityQn.getSelectedItem().toString());
                    args.putString("thirdSecurityQn",binding.spThirdSecurityQn.getSelectedItem().toString());

                    args.putString("firstQnAnswer",binding.etxtFirstSecurityQn.getText().toString());
                    args.putString("secondQnAnswer",binding.etxtSecondSecurityQn.getText().toString());
                    args.putString("thirdQnAnswer",binding.etxtThirdSecurityQn.getText().toString());


                    AuthActivity.navController.navigate(R.id.action_signUpFragment_to_PINManagerFragment,args);
                }

            }
        });


        binding.btnBack.setOnClickListener(v -> {
            binding.selectedSignUp.setVisibility(View.VISIBLE);
            binding.selectedSecurityQns.setVisibility(View.INVISIBLE);
            binding.textSecurityQnsLabel.setAlpha((float) 0.6);
            binding.textUserInfo.setTextColor(getResources().getColor(R.color.colorPrimary));
            binding.layoutSignUp.setVisibility(View.VISIBLE);
            binding.layoutSecurityQns.setVisibility(View.GONE);
        });
    }



    //*********** Validate SignUp Form Inputs ********//

    private boolean validateForm() {
        if (!ValidateInputs.isValidName(binding.userFirstname.getText().toString().trim())) {
            binding.userFirstname.setError(getString(R.string.invalid_first_name));
            return false;
        } else if (!ValidateInputs.isValidName(binding.userLastname.getText().toString().trim())) {
            binding.userLastname.setError(getString(R.string.invalid_last_name));
            return false;
        } else if (binding.districtSpinner.getText().toString().equals("District")) {
            Toast.makeText(context, "Please select District", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.subCountySpinner.getText().toString().equals("Sub County")) {
            Toast.makeText(context, "Please select Sub County", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.villageSpinner.getText().toString().equals("Village")) {
            Toast.makeText(context, "Please select Village", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.idType.getSelectedItem().toString().equalsIgnoreCase("select")) {
            Toast.makeText(context, "Please select IdType", Toast.LENGTH_SHORT).show();
            return false;
        }else if (binding.idNumber.getText().toString().isEmpty()) {
            Toast.makeText(context, "Please enter Id no", Toast.LENGTH_SHORT).show();
            return false;
        }else if (binding.idType.getSelectedItem().toString().equalsIgnoreCase("national id") && binding.idNumber.getText().toString().length()<14) {
            Toast.makeText(context, "Please enter valid id", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }

    //*********** Validate Security Qns Form Inputs ********//
    private boolean validateSecurityQns() {

       if (binding.etxtFirstSecurityQn.getText().toString().isEmpty()) {
          Toast.makeText(context, "Please enter 1st security question", Toast.LENGTH_SHORT).show();
          return false;
      }else if (binding.etxtSecondSecurityQn.getText().toString().isEmpty()) {
           Toast.makeText(context, "Please enter 2nd security question", Toast.LENGTH_SHORT).show();
           return false;
       }else if (binding.etxtThirdSecurityQn.getText().toString().isEmpty()) {
           Toast.makeText(context, "Please enter 3rd security question", Toast.LENGTH_SHORT).show();
           return false;
       }else{
          return  true;

      }
    }
     private void subscribeDistrictList(LiveData<List<RegionDetails>> districts, ArrayList<SpinnerItem> districtList){
         districts.observe(getViewLifecycleOwner(),myDistricts->{
             if(myDistricts!=null && myDistricts.size()!=0){
                 for (RegionDetails x : myDistricts) {
                     districtList.add(new SpinnerItem() {
                         @Override
                         public String getId() {
                             return ""+x.getId();
                         }

                         @Override
                         public String toString() {
                             return x.getRegion();
                         }
                     });

                 }

                 //Log.d(TAG, "onCreate: " + districtList + districtList.size());
                 ArrayAdapter<SpinnerItem> districtListAdapter = new ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, districtList);
                 binding.districtSpinner.setThreshold(1);
                 binding.districtSpinner.setAdapter(districtListAdapter);
             }
         });
     }

    private void subscribeSubcountyList(LiveData<List<RegionDetails>> subcounties){
        subcounties.observe(getViewLifecycleOwner(),mySubcounties->{
            if(mySubcounties!=null && mySubcounties.size()!=0){
                for (RegionDetails x : mySubcounties) {
                    subCountyList.add(new SpinnerItem() {
                        @Override
                        public String getId() {
                            return ""+x.getId();
                        }

                        @Override
                        public String toString() {
                            return x.getRegion();
                        }
                    } );

                }

                //Log.d(TAG, "onCreate: " + subCountyList);
                ArrayAdapter<SpinnerItem> subCountyListAdapter = new ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, subCountyList);
                binding.subCountySpinner.setThreshold(1);
                binding.subCountySpinner.setAdapter(subCountyListAdapter);
            }
        });
    }

    private void subscribeVillageList(LiveData<List<RegionDetails>> villageDetailsList) {
        villageDetailsList.observe(getViewLifecycleOwner(),myVillages->{
            if(myVillages!=null && myVillages.size()!=0){
                for (RegionDetails x : myVillages) {
                    villageList.add(new SpinnerItem() {
                        @Override
                        public String getId() {
                            return ""+x.getId();
                        }

                        @Override
                        public String toString() {
                            return x.getRegion();
                        }
                    });

                }

                //Log.d(TAG, "onCreate: " + villageList);
                ArrayAdapter<SpinnerItem> villageListAdapter = new ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, villageList);
                binding.villageSpinner.setThreshold(1);
                binding.villageSpinner.setAdapter(villageListAdapter);
            }
        });
    }

    private void subscribeToSecurityQns(LiveData<List<ArrayList<String>>> securityQuestions) {
        securityQuestions.observe(getViewLifecycleOwner(), myQns->{
            //set list in beneficiary spinner
            if(myQns!=null){
                ArrayAdapter<String> beneficiariesAdapter1 = new ArrayAdapter(context, android.R.layout.simple_spinner_item, myQns.get(0));
                ArrayAdapter<String> beneficiariesAdapter2 = new ArrayAdapter(context, android.R.layout.simple_spinner_item, myQns.get(1));
                ArrayAdapter<String> beneficiariesAdapter3 = new ArrayAdapter(context, android.R.layout.simple_spinner_item, myQns.get(2));
                binding.spFirstSecurityQn.setAdapter(beneficiariesAdapter1);
                binding.spSecondSecurityQn.setAdapter(beneficiariesAdapter2);
                binding.spThirdSecurityQn.setAdapter(beneficiariesAdapter3);
            }

        });
    }


}
