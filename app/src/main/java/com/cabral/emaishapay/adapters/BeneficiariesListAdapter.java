package com.cabral.emaishapay.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.cabral.emaishapay.BuildConfig;
import com.cabral.emaishapay.DailogFragments.AddBeneficiaryFragment;
import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.fragments.BeneficiariesListFragment;
import com.cabral.emaishapay.fragments.CardListFragment;
import com.cabral.emaishapay.models.BeneficiaryResponse;
import com.cabral.emaishapay.models.CardResponse;
import com.cabral.emaishapay.network.APIClient;
import com.cabral.emaishapay.utils.CryptoUtil;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BeneficiariesListAdapter extends RecyclerView.Adapter<BeneficiariesListAdapter.MyViewHolder> {
    private List<BeneficiaryResponse.Beneficiaries> dataList;
    private FragmentManager fm;
    Context context;
    String decripted_name,decripted_number;
    private String beneficary_name, initials,cvv,expiry_date,id;


    public  class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView initials,benefaciary_name,date,beneficiary_type,beneficiary_number;
        ImageView close;
        ConstraintLayout constraintLayoutAmount,card;

        public MyViewHolder(View v, FragmentManager fm) {
            super(v);
            initials = v.findViewById(R.id.initials);
            benefaciary_name = v.findViewById(R.id.user_name);
            beneficiary_type  = v.findViewById(R.id.beneficiary_type);
            beneficiary_number = v.findViewById(R.id.beneficiary_number);
            date = v.findViewById(R.id.date);
            close = v.findViewById(R.id.img_beneficiary_close);
            constraintLayoutAmount = v.findViewById(R.id.layout_amount);
            card = v.findViewById(R.id.layout_transaction_list_card);

        }


        @Override
        public void onClick(View v) {

        }
    }

    public BeneficiariesListAdapter(List<BeneficiaryResponse.Beneficiaries> dataList, FragmentManager supportFragmentManager) {
        this.dataList = dataList;
        fm=supportFragmentManager;

    }

    @Override
    public BeneficiariesListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_transaction_card,parent,false);
        context=parent.getContext();
        BeneficiariesListAdapter.MyViewHolder holder = new BeneficiariesListAdapter.MyViewHolder(view,fm);
        return holder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(BeneficiariesListAdapter.MyViewHolder holder, int position) {
        BeneficiaryResponse.Beneficiaries data = dataList.get(position);
        holder.constraintLayoutAmount.setVisibility(View.GONE);
        holder.date.setVisibility(View.GONE);
        holder.close.setVisibility(View.VISIBLE);
        holder.beneficiary_type.setVisibility(View.VISIBLE);

        holder.beneficiary_type.setText(data.getTransaction_type());


        if (data.getTransaction_type().equalsIgnoreCase("bank")) {
            //decript name and account no
            CryptoUtil encrypter = new CryptoUtil(BuildConfig.ENCRYPTION_KEY, context.getString(R.string.iv));
            decripted_name = encrypter.decrypt(data.getAccount_name());
            decripted_number = encrypter.decrypt(data.getAccount_number());
            holder.initials.setText(getNameInitials(decripted_name));
            holder.benefaciary_name.setText(decripted_name);
            holder.beneficiary_number.setText(decripted_number);
        } else {
            holder.initials.setText(getNameInitials(data.getAccount_name()));
            holder.benefaciary_name.setText(data.getAccount_name());
            holder.beneficiary_number.setText(data.getAccount_number());

        }

        holder.close.setOnClickListener(new View.OnClickListener() {
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
                                deleteBeneficiary(data.getId());
                                // notifyItemChanged(fragment);
                                dialogInterface.dismiss();
                            }


                        })
                        .show();


            }
        });

        holder.card.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            //decript name and account no
                            CryptoUtil encrypter = new CryptoUtil(BuildConfig.ENCRYPTION_KEY, context.getString(R.string.iv));
                            decripted_name = encrypter.decrypt(data.getAccount_name());
                            decripted_number = encrypter.decrypt(data.getAccount_number());
                            updateBeneficiary(data.getTransaction_type(), decripted_name, getNameInitials(decripted_name), decripted_number);






                    }
                });
    }



    @Override
    public int getItemCount() {
        return 0;
    }



    public void deleteBeneficiary(String id){


        //call endpoint for deleting beneficiary
        ProgressDialog dialog;
        dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(false);
        dialog.show();
        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,context);
        /*************RETROFIT IMPLEMENTATION**************/
        Call<CardResponse> call = APIClient.getWalletInstance().deleteBeneficiary(id,access_token,request_id,category,"");
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

                        fm.popBackStack();

                        Fragment fragment = new CardListFragment();
                        fm.beginTransaction()
//                                .hide(((WalletHomeActivity) fm.g).currentFragment)
                                .replace(R.id.wallet_home_container, fragment)
                                .addToBackStack(null).commit();

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

    public void updateBeneficiary(String beneficiary_type,String beneficary_name,String initials,String beneficiary_no){
        //call add beneficiary fragment
        //nvigate to add beneficiaries fragment
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev =fm.findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        // Create and show the dialog.
        DialogFragment addCardDialog =new AddBeneficiaryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("beneficiary_type",beneficiary_type);
        bundle.putString("beneficiary_name",beneficary_name);
        bundle.putString("initials",initials);
        bundle.putString("beneficiary_no",beneficiary_no);
        addCardDialog.setArguments(bundle);
        addCardDialog.show( ft, "dialog");

    }


    private String getNameInitials(String name){
        if(name==null || name.isEmpty())
            return "";
        String ini = ""+name.charAt(0);
        // we use ini to return the output
        for (int i=0; i<name.length(); i++){
            if ( name.charAt(i)==' ' && i+1 < name.length() && name.charAt(i+1)!=' ' && ini.length()!=2 ){
                //if i+1==name.length() you will have an indexboundofexception
                //add the initials
                ini+=name.charAt(i+1);
            }
        }
        //after getting "ync" => return "YNC"
        return ini.toUpperCase();
    }
}
