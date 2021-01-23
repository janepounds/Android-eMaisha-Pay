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
import android.widget.Button;
import android.widget.LinearLayout;

import com.cabral.emaishapay.DailogFragments.AddCardFragment;
import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.adapters.CardListAdapter;
import com.cabral.emaishapay.adapters.LoansListAdapter;
import com.cabral.emaishapay.adapters.WalletTransactionsListAdapter;
import com.cabral.emaishapay.models.CardResponse;
import com.cabral.emaishapay.models.WalletTransactionResponse;
import com.cabral.emaishapay.network.APIClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CardListFragment extends Fragment {
    private static final String TAG = "CardListFragment";

    FloatingActionButton btnAddCard;
    LinearLayout layoutCardViewEmpty;
    RecyclerView cardRecycler;
    private Context context;
    private CardListAdapter cardListAdapter;
    private List<CardResponse.Cards> cardlists = new ArrayList();
    Toolbar toolbar;
    public CardListFragment() {
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
        View rootView =inflater.inflate(R.layout.fragment_card_list, container, false);
        cardRecycler   =rootView.findViewById(R.id.recyclerView_card_fragment);
        layoutCardViewEmpty = rootView.findViewById(R.id.card_view_empty);
        btnAddCard = rootView.findViewById(R.id.btn_add_card);
        toolbar = rootView.findViewById(R.id.toolbar_card_list);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("My Cards");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        cardRecycler.setLayoutManager(new LinearLayoutManager(context));
        RequestCards();


        btnAddCard.setOnClickListener(v -> {
        //nvigate to add card fragment
            FragmentManager fm = CardListFragment.this.getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment prev =fm.findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            // Create and show the dialog.
            DialogFragment addCardDialog =new AddCardFragment();
            addCardDialog.show( ft, "dialog");

        });
        return rootView;
    }


    public void RequestCards(){
        ProgressDialog dialog;
        dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setMessage("Please Wait..");
        dialog.setCancelable(false);
        dialog.show();
        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
        /******************RETROFIT IMPLEMENTATION***********************/
        Call<CardResponse> call = APIClient.getWalletInstance().getCards(access_token);
        call.enqueue(new Callback<CardResponse>() {
            @Override
            public void onResponse(Call<CardResponse> call, Response<CardResponse> response) {
                if(response.isSuccessful()){

                    try {

                        cardlists = response.body().getCardsList();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        Log.d(TAG,cardlists.size()+"**********");

                        cardRecycler.setLayoutManager(new LinearLayoutManager(context));
                        cardListAdapter = new CardListAdapter(cardlists,context);
                        cardRecycler.setAdapter(cardListAdapter);
                        cardListAdapter.notifyDataSetChanged();
                        updateCardView(cardlists.size());
                    }
                    dialog.dismiss();
                }else if (response.code() == 401) {

                    TokenAuthActivity.startAuth(context, true);
                    if (response.errorBody() != null) {
                        Log.e("info", new String(String.valueOf(response.errorBody())));
                    } else {
                        Log.e("info", "Something got very very wrong");
                    }
                    dialog.dismiss();
                }

            }

            @Override
            public void onFailure(Call<CardResponse> call, Throwable t) {
                dialog.dismiss();
            }
        });





    }
//*********** Change the Layout View of My_Cart Fragment based on Cart Items ********//

    public void updateCardView(int cardListSize) {
        // Check if Cart has some Items
        if (cardListSize != 0) {
            layoutCardViewEmpty.setVisibility(View.VISIBLE);
            layoutCardViewEmpty.setVisibility(View.GONE);
        } else {
            layoutCardViewEmpty.setVisibility(View.GONE);
            layoutCardViewEmpty.setVisibility(View.VISIBLE);
        }
    }

}