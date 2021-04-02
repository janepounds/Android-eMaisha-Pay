package com.cabral.emaishapay.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.google.android.material.snackbar.Snackbar;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.models.LoanApplication;

public class WalletLoanDetailsFragment extends Fragment {
    private static final String TAG = "WalletLoanAppInitiateFr";
    private Context context;
    String[] descriptionData = {"Loan\nDetails", "Farming\nDetails", "Preview", "KYC\nDetails"};
    String[] descriptionData2 = {"User\nDetails","Loan\nDetails", "Farming\nDetails", "Preview", "KYC\nDetails"};
    private Toolbar toolbar;
    private StateProgressBar loanProgressBarId,loanApplicationStateProgressBar;
    private Button btnLoanNextStep;
    private EditText txtLoanApplicationAmount,loanpayments_edtxt;
    TextView loanpaymentfrequency,amount_due_txtview,txt_loan_application_duration,loanpayment_duration_units,txtv_maximum;
    private Spinner spLoanApplicationType;
    FrameLayout layoutPreviousBtn;
    Button previous_btn;

    Float interest=0F;
    final String applicantType="Applicant_Type";
    private String title;
    LoanApplication loanApplication;

    public WalletLoanDetailsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            interest=getArguments().getFloat("interest");
            title=  getArguments().getString(applicantType);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet_loan_details, container, false);
        toolbar = view.findViewById(R.id.toolbar_wallet_loan_app_initiate);
        loanpaymentfrequency = view.findViewById(R.id.loanpaymentfrequency);
        amount_due_txtview = view.findViewById(R.id.amount_due_txtview);
        txt_loan_application_duration = view.findViewById(R.id.txt_loan_application_duration);
        loanpayment_duration_units = view.findViewById(R.id.loanpayment_duration_units);
        loanProgressBarId = view.findViewById(R.id.loan_progress_bar_id);
        btnLoanNextStep = view.findViewById(R.id.btn_loan_next_step);
        txtLoanApplicationAmount = view.findViewById(R.id.txt_loan_application_amount);
        spLoanApplicationType = view.findViewById(R.id.sp_loan_application_type);
        loanpayments_edtxt= view.findViewById(R.id.loanpayments_edtxt);
        loanApplicationStateProgressBar = view.findViewById(R.id.loan_application_state_progress_bar_loan_details);
        layoutPreviousBtn = view.findViewById(R.id.layout_previous_btn);
        previous_btn = view.findViewById(R.id.previous_btn);
        txtv_maximum = view.findViewById(R.id.txtv_maximum);


        //Second hidden progress bar for loan application with 5 states

//        loanApplicationStateProgressBar.setStateDescriptionData(descriptionData2);
//        loanApplicationStateProgressBar.setStateDescriptionTypeface("fonts/JosefinSans-SemiBold.ttf");
//

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Apply for Loan");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(title.equalsIgnoreCase("Merchant Loan Details")){
           loanApplicationStateProgressBar.setVisibility(View.VISIBLE);
           loanProgressBarId.setVisibility(View.GONE);
           layoutPreviousBtn.setVisibility(View.VISIBLE);
           loanApplicationStateProgressBar.setStateDescriptionData(descriptionData2);
           loanApplicationStateProgressBar.setStateDescriptionTypeface("fonts/JosefinSans-SemiBold.ttf");
           loanApplication= (LoanApplication) getArguments().getSerializable("loanApplication");

        }else {
            loanProgressBarId.setStateDescriptionData(descriptionData);
            loanProgressBarId.setStateDescriptionTypeface("fonts/JosefinSans-SemiBold.ttf");
            layoutPreviousBtn.setVisibility(View.GONE);
        }


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       
        
        spLoanApplicationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    //Change selected text color
                    ((TextView) view).setTextColor(getResources().getColor(R.color.textColor));
                    ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);//Change selected text size
                } catch (Exception e) {

                }
                if(position==0){
                    loanpaymentfrequency.setText(getString(R.string.per_day));
                    loanpayment_duration_units.setText(getString(R.string.days));
                }
                else if(position==1){
                    loanpaymentfrequency.setText(getString(R.string.per_week));
                    loanpayment_duration_units.setText(getString(R.string.weeks));
                }
                else if(position==2){
                    loanpaymentfrequency.setText(getString(R.string.per_month));
                    loanpayment_duration_units.setText(getString(R.string.months));
                }
                else if(position==3){
                    loanpaymentfrequency.setText(getString(R.string.per_after3months));
                    loanpayment_duration_units.setText(getString(R.string.quarteryear));
                }
                else if(position==4){
                    loanpaymentfrequency.setText(getString(R.string.per_after6months));
                    loanpayment_duration_units.setText(getString(R.string.halfyear));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String[] maximum_amount = txtv_maximum.getText().toString().split("\\s+");
        txtLoanApplicationAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!txtLoanApplicationAmount.getText().toString().isEmpty()) {
                    int applicationAmount = Integer.parseInt(txtLoanApplicationAmount.getText().toString());
                    if (applicationAmount > Integer.parseInt(maximum_amount[1])) {

                        txtLoanApplicationAmount.getText().clear();
                        txtLoanApplicationAmount.setError("Your loan limit " + maximum_amount[1]);
                        txtLoanApplicationAmount.requestFocus();
                    }else{
                        txtLoanApplicationAmount.setError(null);
                        int amountDue = (int) ((int) applicationAmount * ((interest + 100) / 100));
                        amount_due_txtview.setText(amountDue + "");
                        Log.e("intrestbug", interest + "");
                    }


                }
                if (!amount_due_txtview.toString().isEmpty() && !loanpayments_edtxt.getText().toString().isEmpty()) {

                    Float amount_due = Float.parseFloat(amount_due_txtview.getText().toString());
                    Float loanpayments = Float.parseFloat(loanpayments_edtxt.getText().toString());
                    int loanduration_integer = (int) Math.ceil(amount_due / loanpayments);


                    txt_loan_application_duration.setText(loanduration_integer + "");
                }

            }
        });

        loanpayments_edtxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!amount_due_txtview.toString().isEmpty() && !loanpayments_edtxt.getText().toString().isEmpty()){

                    Float amount_due= Float.parseFloat(amount_due_txtview.getText().toString());
                    Float loanpayments = Float.parseFloat( loanpayments_edtxt.getText().toString());
                    int loanduration_integer=(int) Math.ceil(amount_due/loanpayments);


                    txt_loan_application_duration.setText(loanduration_integer+"");
                }
                if (!txtLoanApplicationAmount.getText().toString().isEmpty() ){
                    int applicationAmount= Integer.parseInt(txtLoanApplicationAmount.getText().toString());
                    int amountDue= (int) ((int) applicationAmount*( (interest+100)/100));
                    amount_due_txtview.setText(amountDue+"");
                    Log.e("intrestbug",interest+"");
                }
            }
        });


        previous_btn.setOnClickListener(view2 -> getParentFragmentManager().popBackStack());
        btnLoanNextStep.setOnClickListener(v -> {
            if (txtLoanApplicationAmount.getText().toString().trim() == null || txtLoanApplicationAmount.getText().toString().trim().isEmpty()) {
                txtLoanApplicationAmount.setError("Please enter value");

            } else if (loanpayments_edtxt.getText().toString().trim() == null || txt_loan_application_duration.getText().toString().trim().isEmpty()) {
                loanpayments_edtxt.setError("Please enter value");
            } else {
                if(title.equalsIgnoreCase("Merchant Loan Details")){
                    loanApplication.setAmount(Float.valueOf(txtLoanApplicationAmount.getText().toString()));
                    loanApplication.setDuration(Integer.parseInt(txt_loan_application_duration.getText().toString()));
                    loanApplication.setLoanType(spLoanApplicationType.getSelectedItem().toString());
                    loanApplication.setPayment_amount_on_schedule(Double.parseDouble(loanpayments_edtxt.getText().toString()));

                    Bundle bundle = new Bundle();
                    bundle.putFloat("interest", interest);
                    bundle.putSerializable("loanApplication", loanApplication);
                    bundle.putString("Applicant_Type", getString(R.string.merchant_loan_details));

                    //To WalletLoanFarmingDetailsFragment
                    WalletHomeActivity.navController.navigate(R.id.action_walletLoanDetailsFragment_to_walletLoanFarmingDetailsFragment,bundle);

                }else{
                    LoanApplication loanApplication = new LoanApplication();
                    loanApplication.setAmount(Float.valueOf(txtLoanApplicationAmount.getText().toString()));
                    loanApplication.setDuration(Integer.parseInt(txt_loan_application_duration.getText().toString()));
                    loanApplication.setLoanType(spLoanApplicationType.getSelectedItem().toString());
                    loanApplication.setPayment_amount_on_schedule(Double.parseDouble(loanpayments_edtxt.getText().toString()));

                    Bundle bundle = new Bundle();
                    bundle.putFloat("interest", interest);
                    bundle.putSerializable("loanApplication", loanApplication);
                    bundle.putString("Applicant_Type", getString(R.string.default_loan_details));

                    //To WalletLoanFarmingDetailsFragment
                    WalletHomeActivity.navController.navigate(R.id.action_walletLoanDetailsFragment_to_walletLoanFarmingDetailsFragment,bundle);

                }


                }

        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}