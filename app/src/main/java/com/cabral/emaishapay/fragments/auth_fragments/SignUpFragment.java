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

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.AuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.database.DbHandlerSingleton;
import com.cabral.emaishapay.databinding.SignupFragmentBinding;
import com.cabral.emaishapay.models.CropSpinnerItem;
import com.cabral.emaishapay.models.SecurityQnsResponse;
import com.cabral.emaishapay.models.address_model.RegionDetails;
import com.cabral.emaishapay.network.api_helpers.APIClient;
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

    private DbHandlerSingleton dbHandler;
    private Context context;
    private String phoneNumber;
    DialogLoader dialogLoader;


    // Firebase auth object
    private int pickedDistrictId;
    private int pickedSubCountyId;
    private ArrayList<CropSpinnerItem> subCountyList = new ArrayList<>();
    private ArrayList<String> villageList = new ArrayList<>();
    private List<SecurityQnsResponse.SecurityQns> securityQnsList = new ArrayList();
    ArrayList<String> securityQns = new ArrayList<>();
    ArrayList<String> securityQnsSubList1 = new ArrayList<>();
    ArrayList<String> securityQnsSubList2 = new ArrayList<>();
    ArrayList<String> securityQnsSubList3 = new ArrayList<>();
    
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
        //get security qns
        RequestSecurityQns();

        ArrayList<CropSpinnerItem> districtList = new ArrayList<>();
        dbHandler = DbHandlerSingleton.getHandlerInstance(context);
        try {
            for (RegionDetails x : dbHandler.getRegionDetails("district")) {
                districtList.add(new CropSpinnerItem() {
                    @Override
                    public String getId() {
                        return String.valueOf(x.getId());
                    }

                    @Override
                    public String getUnits() {
                        return null;
                    }

                    @NonNull
                    @Override
                    public String toString() {
                        return x.getRegion();
                    }
                });

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "onCreate: " + districtList + districtList.size());
        ArrayAdapter<CropSpinnerItem> districtListAdapter = new ArrayAdapter<CropSpinnerItem>(context, android.R.layout.simple_dropdown_item_1line, districtList);
        binding.districtSpinner.setThreshold(1);
        binding.districtSpinner.setAdapter(districtListAdapter);
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
                for (int i = 0; i < districtList.size(); i++) {

                    if (districtList.get(i).toString().equals(binding.districtSpinner.getText().toString())) {
                        pickedDistrictId = Integer.parseInt(districtList.get(i).getId());

                        Log.d(TAG, "onCreate: " + pickedDistrictId);

                        subCountyList.clear();
                        try {
                            for (RegionDetails x : dbHandler.getSubcountyDetails(String.valueOf(pickedDistrictId), "subcounty")) {
                                subCountyList.add(new CropSpinnerItem() {
                                    @Override
                                    public String getId() {
                                        return String.valueOf(x.getId());
                                    }

                                    @Override
                                    public String getUnits() {
                                        return null;
                                    }

                                    @NonNull
                                    @Override
                                    public String toString() {
                                        return x.getRegion();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "onCreate: " + subCountyList);
                        ArrayAdapter<CropSpinnerItem> subCountyListAdapter = new ArrayAdapter<CropSpinnerItem>(context, android.R.layout.simple_dropdown_item_1line, subCountyList);
                        binding.subCountySpinner.setThreshold(1);
                        binding.subCountySpinner.setAdapter(subCountyListAdapter);
                    }

                }
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

                for (int i = 0; i < subCountyList.size(); i++) {

                    if (subCountyList.get(i).toString().equals(binding.subCountySpinner.getText().toString())) {
                        pickedSubCountyId = Integer.parseInt(subCountyList.get(i).getId());

                        Log.d(TAG, "onCreate: " + pickedSubCountyId);

                        villageList.clear();
                        try {
                            for (RegionDetails x : dbHandler.getVillageDetails(String.valueOf(pickedSubCountyId), "village")) {
                                villageList.add(x.getRegion());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "onCreate: " + villageList);
                        ArrayAdapter<String> villageListAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, villageList);
                        binding.villageSpinner.setThreshold(1);
                        binding.villageSpinner.setAdapter(villageListAdapter);
                    }


                }
            }
        });

        binding.villageSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

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
                    args.putString("secondSecurityQn",binding.idNumber.getText().toString());
                    args.putString("thirdSecurityQn",binding.idNumber.getText().toString());

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
        }else if (binding.idType.getSelectedItem().toString().equalsIgnoreCase("national id") && binding.idNumber.getText().toString().length()<15) {
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


    public void RequestSecurityQns(){

        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,context);

        dialogLoader.showProgressDialog();
        /******************RETROFIT IMPLEMENTATION***********************/
        Call<SecurityQnsResponse> call = APIClient.getWalletInstance(getContext()).getSecurityQns(request_id,category,"getSecurityQns");
        call.enqueue(new Callback<SecurityQnsResponse>() {
            @Override
            public void onResponse(Call<SecurityQnsResponse> call, Response<SecurityQnsResponse> response) {
                if(response.isSuccessful()){
                    dialogLoader.hideProgressDialog();
                    try {
                        securityQnsList = response.body().getSecurity_qnsList();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        Log.d(TAG,securityQnsList.size()+"**********");

                        //set security qns adapter
                        for(int i=0;i<securityQnsList.size();i++){
                            String security_Qn_name = securityQnsList.get(i).getSecurity_qn_name();
                            securityQns.add(security_Qn_name);
                        }
                        for(int i=0;i<3;i++) {
                            securityQnsSubList1.add(securityQns.get(i));
                        }for(int i=3;i<6;i++){
                            securityQnsSubList2.add(securityQns.get(i));

                        }for(int i=6;i<9;i++){
                            securityQnsSubList3.add(securityQns.get(i));

                        }
                        //set list in beneficiary spinner
                        ArrayAdapter<String> beneficiariesAdapter1 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, securityQnsSubList1);
                        ArrayAdapter<String> beneficiariesAdapter2 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, securityQnsSubList2);
                        ArrayAdapter<String> beneficiariesAdapter3 = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, securityQnsSubList3);
                        binding.spFirstSecurityQn.setAdapter(beneficiariesAdapter1);
                        binding.spSecondSecurityQn.setAdapter(beneficiariesAdapter2);
                        binding.spThirdSecurityQn.setAdapter(beneficiariesAdapter3);

                    }

                }else if (response.code() == 401) {
                    Log.e("info", "Something got very wrong");
                }

            }

            @Override
            public void onFailure(Call<SecurityQnsResponse> call, Throwable t){
                Log.e("info", "Something got very very wrong");
            }
        });

    }


}
