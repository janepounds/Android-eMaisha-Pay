package com.cabral.emaishapay.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cabral.emaishapay.DailogFragments.AddBeneficiaryFragment;
import com.cabral.emaishapay.DailogFragments.AddCardFragment;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.adapters.BeneficiariesListAdapter;
import com.cabral.emaishapay.adapters.CardsListAdapter;
import com.cabral.emaishapay.models.BeneficiaryResponse;
import com.cabral.emaishapay.models.CardResponse;
import com.cabral.emaishapay.network.APIClient;
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
    private List<BeneficiaryResponse.Beneficiaries> beneficiariesList = new ArrayList();

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
        // Inflate the layout for this fragment
        View rootView =inflater.inflate(R.layout.fragment_beneficiaries_list, container, false);
        btnAddBeneficiary = rootView.findViewById(R.id.btn_add_beneficiary);
        recyclerView   =rootView.findViewById(R.id.recyclerView_beneficiaries_fragment);
        toolbar = rootView.findViewById(R.id.toolbar_beneficiaries_list);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("My Beneficiaries");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        RequestBeneficiaries();

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
            DialogFragment addCardDialog =new AddBeneficiaryFragment();
            addCardDialog.show( ft, "dialog");

        });
        return rootView;
    }

    public void RequestBeneficiaries(){
        ProgressDialog dialog;
        dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(false);
        dialog.show();
        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
        String request_id = WalletHomeActivity.generateRequestId();
        String category = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_ACCOUNT_ROLE,requireContext());
        /******************RETROFIT IMPLEMENTATION***********************/
        Call<BeneficiaryResponse> call = APIClient.getWalletInstance().getBeneficiaries(access_token,"",request_id);
        call.enqueue(new Callback<BeneficiaryResponse>() {
            @Override
            public void onResponse(Call<BeneficiaryResponse> call, Response<BeneficiaryResponse> response) {
                if(response.isSuccessful()){

                    try {

                        beneficiariesList = response.body().getBeneficiariesList();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        Log.d(TAG,beneficiariesList.size()+"**********");

                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        beneficiariesListAdapter = new BeneficiariesListAdapter(beneficiariesList,requireActivity().getSupportFragmentManager());
                        recyclerView.setAdapter(beneficiariesListAdapter);
                        beneficiariesListAdapter.notifyDataSetChanged();

                    }
                    dialog.dismiss();
                }else if (response.code() == 401) {

                    TokenAuthActivity.startAuth(getActivity(), true);
                    getActivity().finishAffinity();
                    if (response.errorBody() != null) {
                        Log.e("info", new String(String.valueOf(response.errorBody())));
                    } else {
                        Log.e("info", "Something got very very wrong");
                    }
                    dialog.dismiss();
                }

            }

            @Override
            public void onFailure(Call<BeneficiaryResponse> call, Throwable t) {
                dialog.dismiss();
            }
        });





    }
}