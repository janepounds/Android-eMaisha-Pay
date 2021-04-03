package com.cabral.emaishapay.fragments.buy_fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.fragments.TokenAuthFragment;
import com.cabral.emaishapay.models.ratings.GiveRating;
import com.cabral.emaishapay.network.BuyInputsAPIClient;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProductRatingReviewFragment extends Fragment {


    View rootView;
    Toolbar toolbar;
    RatingBar ratingBar;
    EditText review_text;
    AppCompatButton submitBtn;

    public ProductRatingReviewFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_product_rating_review, container, false);

        toolbar = rootView.findViewById(R.id.toolbar_reviewRating);
        ratingBar = rootView.findViewById(R.id.rating_bar);
        review_text = rootView.findViewById(R.id.review_text);
        submitBtn= rootView.findViewById(R.id.submitBtn);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String customerID = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, requireContext());

                String customerName = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_FIRST_NAME, requireContext());

                if(ratingBar.getRating()==0.0){
                    Snackbar.make(rootView, "Rate Product", Snackbar.LENGTH_SHORT).show();
                }else if( review_text.getText().toString().isEmpty()){
                    Snackbar.make(rootView, "Write Review", Snackbar.LENGTH_SHORT).show();
                }

                requestGiveRating(
                        Product_Description.productDetails.getProductsId()+"",
                        customerID,
                        customerName,
                        ratingBar.getRating()+"",
                        Product_Description.productDetails.getLanguageId()+"",
                        review_text.getText().toString()

                        );
            }
        });

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.AddReview));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        return rootView;
    }



    private void requestGiveRating(String products_id, String customers_id, String customers_nam, String reviews_rating,
                                    String languages_id, String reviews_text) {

        Map<String, String> map = new HashMap<>();
        map.put("products_id", products_id);
        map.put("customers_id", customers_id);
        map.put("customers_name", customers_nam);
        map.put("reviews_rating", reviews_rating);
        //map.put("vendors_id",vendors_id);
        map.put("languages_id", languages_id);
        map.put("reviews_text", reviews_text);

        String access_token = TokenAuthFragment.WALLET_ACCESS_TOKEN;
        Call<GiveRating> call = BuyInputsAPIClient.getInstance().giveRating(access_token,map);

        call.enqueue(new Callback<GiveRating>() {
            @Override
            public void onResponse(Call<GiveRating> call, Response<GiveRating> response) {

                if (response.isSuccessful()) {

                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_LONG).show();
                }
                getActivity().getSupportFragmentManager().popBackStack();

            }

            @Override
            public void onFailure(Call<GiveRating> call, Throwable t) {
                Toast.makeText(getContext(), "NetworkCallFailure: " + t, Toast.LENGTH_LONG).show();
            }
        });
    }


}