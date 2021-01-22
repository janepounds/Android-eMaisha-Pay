package com.cabral.emaishapay.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.adapters.CardListAdapter;
import com.cabral.emaishapay.adapters.LoansListAdapter;
import com.cabral.emaishapay.models.CardResponse;
import com.cabral.emaishapay.network.APIClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CardListFragment extends Fragment {

    FloatingActionButton btnAddCard;
    LinearLayout layoutCardViewEmpty;
    RecyclerView cardRecycler;
    private Context context;
    private CardListAdapter cardListAdapter;
    private ArrayList<CardResponse>cardlists = new ArrayList();
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
        cardRecycler.setLayoutManager(new LinearLayoutManager(context));
        RequestCards();

        cardListAdapter = new CardListAdapter(cardlists,context);
        cardRecycler.setAdapter(cardListAdapter);

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
//            DialogFragment addCardDialog =new SearchMerchantForPairing();
//            addCardDialog.show( ft, "dialog");

        });
        return rootView;
    }


    public void RequestCards(){
        /******************RETROFIT IMPLEMENTATION***********************/
        Call<CardResponse> call = APIClient.getInstance().getCards();
        call.enqueue(new Callback<CardResponse>() {
            @Override
            public void onResponse(Call<CardResponse> call, Response<CardResponse> response) {
                if(response.isSuccessful()){
                    cardlists.add(response.body());
                    cardListAdapter.notifyDataSetChanged();
                    updateCardView(cardlists.size());

                }
            }

            @Override
            public void onFailure(Call<CardResponse> call, Throwable t) {

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