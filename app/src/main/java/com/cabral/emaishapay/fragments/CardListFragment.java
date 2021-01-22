package com.cabral.emaishapay.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.cabral.emaishapay.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class CardListFragment extends Fragment {

    FloatingActionButton btnAddCard;
    LinearLayout layoutCardViewEmpty;
    RecyclerView cardRecycler;
    public CardListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_card_list, container, false);
    }
}