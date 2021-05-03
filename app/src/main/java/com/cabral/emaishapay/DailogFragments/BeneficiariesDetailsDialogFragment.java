package com.cabral.emaishapay.DailogFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.fragments.wallet_fragments.BeneficiariesListFragment;
import com.cabral.emaishapay.models.BeneficiaryResponse;
import com.cabral.emaishapay.models.CardResponse;
import com.cabral.emaishapay.models.WalletTransactionResponse;
import com.cabral.emaishapay.network.api_helpers.APIClient;

import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BeneficiariesDetailsDialogFragment extends DialogFragment {
    TextView text_title,text_ben_name,text_ben_mobile,text_ben_bank,text_ben_branch,text_ben_city,text_ben_country,text_account_name,text_account_number;
    LinearLayout layout_name,layout_mobile,layout_bank,layout_bank_branch,layout_city,layout_country,layout_account_name,layout_account_number;

    private Context context;
    private List<BeneficiaryResponse.Beneficiaries> dataList =new ArrayList<>();

    public BeneficiariesDetailsDialogFragment(){
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(getDialog()).getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.layout_beneficiaries_details, null);
        builder.setView(view);

        initializeView(view);
        return builder.create();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
      //  getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context =context;
    }
    public void initializeView(View view){

        text_title = view.findViewById(R.id.ben_details_title);
        text_ben_name = view.findViewById(R.id.text_ben_name);
        text_ben_mobile = view.findViewById(R.id.text_ben_mobile);
        text_ben_bank = view.findViewById(R.id.text_ben_bank);
        text_ben_branch = view.findViewById(R.id.text_ben_branch);
        text_ben_city = view.findViewById(R.id.text_ben_city);
        text_ben_country = view.findViewById(R.id.text_ben_country);
        text_account_name = view.findViewById(R.id.text_account_name);
        text_account_number = view.findViewById(R.id.text_account_number);

        //layouts

        layout_name = view.findViewById(R.id.layout_name);
        layout_mobile = view.findViewById(R.id.layout_mobile);
        layout_bank = view.findViewById(R.id.layout_bank);
        layout_bank_branch = view.findViewById(R.id.layout_bank_branch);
        layout_city = view.findViewById(R.id.layout_city);
        layout_country = view.findViewById(R.id.layout_country);
        layout_account_name = view.findViewById(R.id.layout_account_name);
        layout_account_number = view.findViewById(R.id.layout_account_number);



        ImageView close = view.findViewById(R.id.ben_details_close);
        Button ok = view.findViewById(R.id.btn_ok);
        Button delete = view.findViewById(R.id.btn_delete);

        if(getArguments()!= null){
           String beneficiary_type = getArguments().getString("beneficiary_type") ;
            String beneficiary_name = getArguments().getString("beneficiary_name") ;
            String beneficiary_no = getArguments().getString("beneficiary_no") ;
            String beneficiary_phone = getArguments().getString("beneficiary_phone") ;
            String bank = getArguments().getString("bank") ;
            String branch = getArguments().getString("branch") ;
            String city = getArguments().getString("city") ;
            String country = getArguments().getString("country") ;



            if (beneficiary_type.equalsIgnoreCase("mobile money")) {
                //HIDE BANK DETAILS
                layout_account_name.setVisibility(View.GONE);
                layout_account_number.setVisibility(View.GONE);
                layout_bank.setVisibility(View.GONE);
                layout_bank_branch.setVisibility(View.GONE);
                layout_city.setVisibility(View.GONE);
                layout_country.setVisibility(View.GONE);
                layout_name.setVisibility(View.VISIBLE);
                layout_mobile.setVisibility(View.VISIBLE);

                //SET MM TEXT VIEWS
                text_title.setText(beneficiary_type + " BENEFICIARY");
                text_ben_name.setText(beneficiary_name);
                text_ben_mobile.setText(beneficiary_no);



            }
            else if(beneficiary_type.equalsIgnoreCase("bank")){

                //HIDE MM DETAILS
                layout_name.setVisibility(View.GONE);
                layout_account_name.setVisibility(View.VISIBLE);
                layout_account_number.setVisibility(View.VISIBLE);
                layout_bank.setVisibility(View.VISIBLE);
                layout_bank_branch.setVisibility(View.VISIBLE);
                layout_city.setVisibility(View.VISIBLE);
                layout_country.setVisibility(View.GONE);

                //SET BANK DETAILS
                text_title.setText(beneficiary_type + " BENEFICIARY");
                text_account_name.setText(beneficiary_name);
                text_account_number.setText(beneficiary_no);
                text_ben_mobile.setText(beneficiary_phone);
                text_ben_bank.setText(bank);
                text_ben_branch.setText(branch);
                text_ben_city.setText(city);
                text_ben_country.setText(country);

            }
        }



        close.setOnClickListener(v -> dismiss());
        ok.setOnClickListener(v->dismiss());

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete Beneficiary?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.dismiss();

                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                             //   deleteBeneficiary(data.getId());
                                // notifyItemChanged(fragment);
                                dialogInterface.dismiss();
                            }


                        })
                        .show();


            }
        });


    }

    public void deleteBeneficiary(String id){
        //call endpoint for deleting beneficiary
        ProgressDialog dialog;
        dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(false);
        dialog.show();
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,context);

        /*************RETROFIT IMPLEMENTATION**************/
        Call<CardResponse> call = APIClient.getWalletInstance(context).deleteBeneficiary(access_token,id,request_id,category,"deleteBeneficiary                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                ");
        call.enqueue(new Callback<CardResponse>() {
            @Override
            public void onResponse(Call<CardResponse> call, Response<CardResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 0) {
                        dialog.dismiss();
                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();

                    } else {
                        String message = response.body().getMessage();
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();


                        dialog.dismiss();
                    }

                }else if(response.code()==401){
                    dialog.dismiss();
                    Toast.makeText(context, "session expired", Toast.LENGTH_LONG).show();

                    //redirect to auth
//                    TokenAuthActivity.startAuth(, true);
//                    fm..finishAffinity();
                }
            }


            @Override
            public void onFailure(Call<CardResponse> call, Throwable t) {
                dialog.dismiss();

            }
        });




    }

}
