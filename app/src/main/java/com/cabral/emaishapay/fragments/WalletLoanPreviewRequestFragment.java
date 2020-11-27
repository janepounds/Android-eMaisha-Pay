package com.cabral.emaishapay.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.activities.WalletAuthActivity;
import com.cabral.emaishapay.models.RequestLoanresponse;
import com.cabral.emaishapay.models.LoanApplication;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.network.APIRequests;

import java.text.NumberFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletLoanPreviewRequestFragment extends Fragment {
    private static final String TAG = "WalletLoanPreviewRequest";

    private Context context;

    String[] descriptionData = {"Loan\nDetails", "Farming\nDetails", "Preview", "KYC\nDetails"};
    LoanApplication loanApplication;
    ProgressDialog dialog;
    private NavController navController;

    private Toolbar toolbar;
    private StateProgressBar loanProgressBarId;
    private TextView textViewLoanPreviewAmount, textViewLoanPreviewInterestRate, textViewLoanPreviewDuration, textViewLoanPreviewDueDate,
            textViewLoanPreviewDueAmount, loan_type_or_schedule_txt,textViewErrorMessage, loan_purpose_txt;
    private Button btnLoanNextStep, btnPrevious;
    AppBarConfiguration appBarConfiguration;

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

        loanProgressBarId.setStateDescriptionData(descriptionData);
        loanProgressBarId.setStateDescriptionTypeface("fonts/JosefinSans-SemiBold.ttf");
       if(getArguments() != null)
        loanApplication = (LoanApplication) getArguments().getSerializable("loanApplication");

        if (loanApplication != null) {
            float interest=getArguments().getFloat("interest");
            loanApplication.setInterestRate((float) interest);
            initializeActivity();
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(view);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        btnPrevious.setOnClickListener(view1 -> navController.popBackStack());

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
        textViewLoanPreviewDuration.setText(loanApplication.getDuration() + " " + loanApplication.getDurationLabel());
        textViewLoanPreviewDueDate.setText(loanApplication.computeDueDate());
        textViewLoanPreviewDueAmount.setText("UGX " + NumberFormat.getInstance().format(loanApplication.computeDueAmount()));
        if(loanApplication.isPurpose_for_fetilizer()){
            loan_purpose_txt.setText(getString(R.string.fertilizer_title));
            if(loan_purpose_txt.getText().toString().isEmpty())
                loan_purpose_txt.setText(loan_purpose_txt.getText().toString()+", "+getString(R.string.fertilizer_title));
            else
                loan_purpose_txt.setText(getString(R.string.fertilizer_title));
        }
        if(loanApplication.isPurpose_for_crop_protection()){
            if(loan_purpose_txt.getText().toString().isEmpty())
                loan_purpose_txt.setText(loan_purpose_txt.getText().toString()+", "+getString(R.string.crop_protection));
            else
                loan_purpose_txt.setText(getString(R.string.crop_protection));
        }
        if(loanApplication.isPurpose_for_equipments()){
            if(loan_purpose_txt.getText().toString().isEmpty())
                loan_purpose_txt.setText(loan_purpose_txt.getText().toString()+", "+getString(R.string.equipments));
            else
                loan_purpose_txt.setText(getString(R.string.equipments));
        }

        if(loanApplication.isPurpose_for_seeds()){
            if(loan_purpose_txt.getText().toString().isEmpty())
                loan_purpose_txt.setText(loan_purpose_txt.getText().toString()+", "+getString(R.string.seeds));
            else
                loan_purpose_txt.setText(getString(R.string.seeds));
        }

        btnPrevious.setOnClickListener(view1 -> navController.popBackStack());

        btnLoanNextStep.setOnClickListener(view -> comfirmLoanApplication());
    }

    public void comfirmLoanApplication() {
        /*****************RETROFIT IMPLEMENTATION*******************/
        String access_token = WalletAuthActivity.WALLET_ACCESS_TOKEN;
        String userId = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, context);
        float amount = loanApplication.getAmount();
        int duration = loanApplication.getDuration();
        String loanType = loanApplication.getLoanType();
        double interest = loanApplication.getInterestRate();

        APIRequests apiRequests = APIClient.getWalletInstance();
        Call<RequestLoanresponse> call = apiRequests.comfirmLoanApplication(access_token, userId, amount, duration, interest, loanType);
        call.enqueue(new Callback<RequestLoanresponse>() {
            @Override
            public void onResponse(Call<RequestLoanresponse> call, Response<RequestLoanresponse> response) {
                if (response.code() == 200) {
                    if( response.body().getData().getStatus().equals("0") ){
                        Log.e("info 200", new String(String.valueOf(response.toString())) + ", code: " + response.code());
                        textViewErrorMessage.setText(response.body().getData().getMessage());
                    }else {
                        try {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("loanApplication", loanApplication);
                            navController.navigate(R.id.action_walletLoanPreviewRequestFragment_to_walletLoanAppPhotosFragment,bundle);
                        }catch (Exception exception){
                            exception.printStackTrace();
                        }

                    }

                } else if (response.code() == 401) {
                    WalletAuthActivity.startAuth(context, true);
                } else if (response.code() == 500) {
                    textViewErrorMessage.setText("Error Occurred Try again later");
                    Log.e("info 500", new String(String.valueOf(response.errorBody())) + ", code: " + response.code());
                } else if (response.code() == 400) {
                    Log.e("info 400", new String(String.valueOf(response.toString())) + ", code: " + response.code());
                    textViewErrorMessage.setText(response.body().getData().getMessage());
                } else if (response.code() == 406) {
                    textViewErrorMessage.setText(response.errorBody().toString());
                    Log.e("info 406", new String(String.valueOf(response.toString())) + ", code: " + response.code());
                } else {
                    textViewErrorMessage.setText("Error Occurred Try again later");
                    if (response.errorBody() != null) {
                        Log.e("info", new String(String.valueOf(response.errorBody())) + ", code: " + response.code());
                    } else {
                        Log.e("info", "Something got very very wrong, code: " + response.code());
                    }
                }
                textViewErrorMessage.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }


            @Override
            public void onFailure(Call<RequestLoanresponse> call, Throwable t) {

                Log.e("info : ", "Something got very very wrong");

                textViewErrorMessage.setText("Error Occurred Try again later");
                textViewErrorMessage.setVisibility(View.VISIBLE);
                dialog.dismiss();

            }
        });

    }
}
