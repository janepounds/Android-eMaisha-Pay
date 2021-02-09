package com.cabral.emaishapay.fragments.buy_fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.adapters.buyInputsAdapters.ProductAdapter;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.models.search_model.SearchData;
import com.cabral.emaishapay.network.BuyInputsAPIClient;
import com.google.android.material.snackbar.Snackbar;


import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";
    private Context context;

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ProductAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.buy_inputs_search_fragment, container, false);

        progressBar = view.findViewById(R.id.buy_inputs_search_progress_bar);
        toolbar = view.findViewById(R.id.toolbar_search_fragment);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.search));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = view.findViewById(R.id.buy_inputs_search_recycler_view);

        setHasOptionsMenu(true);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle(getString(R.string.actionSearch));

        Bundle bundle = getArguments();
        assert bundle != null;
        requestSearchData(bundle.getString("searchKey"));

        return view;
    }

    //*********** Request Search Results from the Server based on the given Query ********//

    public void requestSearchData(String searchValue) {
        progressBar.setVisibility(View.VISIBLE);
        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
        Call<SearchData> call = BuyInputsAPIClient.getInstance()
                .getSearchData
                        (access_token,
                                searchValue,
                                ConstantValues.LANGUAGE_ID,
                                ConstantValues.CURRENCY_CODE
                        );

        call.enqueue(new Callback<SearchData>() {
            @Override
            public void onResponse(Call<SearchData> call, retrofit2.Response<SearchData> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {

                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                        adapter = new ProductAdapter(requireActivity(), requireActivity().getSupportFragmentManager(), response.body().getProductData().getProducts(), false, false);

                        recyclerView.setAdapter(adapter);

                        Log.d(TAG, "onResponse: Result = " + response.body().getProductData().getProducts().get(0).getProductsName());

                    } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        Snackbar.make(requireView(), response.body().getMessage(), Snackbar.LENGTH_LONG).show();

                    } else {
                        // Unable to get Success status
                        Snackbar.make(requireView(), getString(R.string.unexpected_response), Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SearchData> call, Throwable t) {
                Toast.makeText(getContext(), "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, @NotNull MenuInflater inflater) {
        // Hide Search Icon in the Toolbar
        MenuItem cartItem = menu.findItem(R.id.ic_cart_item);
//        MenuItem searchItem = menu.findItem(R.id.toolbar_ic_search);
        cartItem.setVisible(true);
//        searchItem.setVisible(false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}



