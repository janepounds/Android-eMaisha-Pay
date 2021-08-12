package com.cabral.emaishapay.fragments.wallet_fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cabral.emaishapay.DailogFragments.AddBeneficiary;
import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.adapters.BeneficiariesListAdapter;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.models.BeneficiaryResponse;
import com.cabral.emaishapay.network.api_helpers.APIClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BeneficiariesListFragment extends Fragment {
    private static final String TAG = "BeneficiariesListFragme";
    private Context context;
    private RecyclerView recyclerView;
    FloatingActionButton btnAddBeneficiary;
    private BeneficiariesListAdapter beneficiariesListAdapter;
    private ConstraintLayout layoutPlaceholder;
    private List<BeneficiaryResponse.Beneficiaries> beneficiariesList = new ArrayList();
    ImageView aboutBeneficiaries;
    DialogLoader dialogLoader;

    Toolbar toolbar;
    public BeneficiariesListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View rootView =inflater.inflate(R.layout.fragment_beneficiaries_list, container, false);
        WalletHomeActivity.bottomNavigationView.setVisibility(View.GONE);
        WalletHomeActivity.scanCoordinatorLayout.setVisibility(View.GONE);
        WalletHomeActivity.bottomNavigationShop.setVisibility(View.GONE);

        btnAddBeneficiary = rootView.findViewById(R.id.btn_add_beneficiary);
        recyclerView   =rootView.findViewById(R.id.recyclerView_beneficiaries_fragment);
        toolbar = rootView.findViewById(R.id.toolbar_beneficiaries_list);
        aboutBeneficiaries = rootView.findViewById(R.id.about_beneficiary);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Beneficiaries");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        dialogLoader = new DialogLoader(context);
        RequestBeneficiaries();

        layoutPlaceholder = rootView.findViewById(R.id.beneficiaries_place_holder);


        aboutBeneficiaries.setOnClickListener(v->{

            //Go to coming soon
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
            View dialogView = getLayoutInflater().inflate(R.layout.layout_coming_soon, null);
            dialog.setView(dialogView);
            dialog.setCancelable(true);

            ImageView close = dialogView.findViewById(R.id.coming_soon_close);




            final android.app.AlertDialog alertDialog = dialog.create();

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });



            alertDialog.show();


        });


        btnAddBeneficiary.setOnClickListener(v -> {
            //nvigate to add beneficiaries fragment
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment prev =fm.findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            // Create and show the dialog.
            DialogFragment addCardDialog =new AddBeneficiary(null);
            addCardDialog.show( ft, "dialog");

        });
        return rootView;
    }

    public void RequestBeneficiaries(){

        dialogLoader.showProgressDialog();
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
        /******************RETROFIT IMPLEMENTATION***********************/
        Call<BeneficiaryResponse> call = APIClient.getWalletInstance(getContext()).getBeneficiaries(
                access_token,
                "",
                request_id,
                "getBeneficiaries");
        call.enqueue(new Callback<BeneficiaryResponse>() {
            @Override
            public void onResponse(Call<BeneficiaryResponse> call, Response<BeneficiaryResponse> response) {
                dialogLoader.hideProgressDialog();
                if(response.isSuccessful()){

                    try {

                        beneficiariesList = response.body().getBeneficiariesList();
                        Log.d(TAG, "onResponse: beneficiaries"+beneficiariesList.size());
                        if(beneficiariesList.size()!=0){
                            layoutPlaceholder.setVisibility(View.GONE);
                        }else {
                            layoutPlaceholder.setVisibility(View.VISIBLE);
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {

                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        beneficiariesListAdapter = new BeneficiariesListAdapter(beneficiariesList,requireActivity().getSupportFragmentManager());
                        recyclerView.setAdapter(beneficiariesListAdapter);
                        beneficiariesListAdapter.notifyDataSetChanged();

                    }

                }else if (response.code() == 401) {
                    TokenAuthFragment.startAuth( true);

                    if (response.errorBody() != null) {
                        Log.e("info", String.valueOf(response.errorBody()));
                    } else {
                        Log.e("info", "Something got very very wrong");
                    }

                }

            }

            @Override
            public void onFailure(Call<BeneficiaryResponse> call, Throwable t) {
                dialogLoader.hideProgressDialog();
            }
        });





    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        inflater.inflate(R.menu.about_beneficiaries_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.aboutBeneficiary) {
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getContext());
            View dialogView = getLayoutInflater().inflate(R.layout.beneficiaries_placeholder, null);
            //dialogView.setLayoutParams(new ActionBar.LayoutParams());
            dialog.setView(dialogView);
            dialog.setCancelable(true);

            ImageView close = dialogView.findViewById(R.id.coming_soon_close);


            final android.app.AlertDialog alertDialog = dialog.create();

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });


            alertDialog.show();
        }

        return false;
    }




}