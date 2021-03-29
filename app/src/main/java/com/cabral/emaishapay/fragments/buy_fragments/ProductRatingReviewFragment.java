package com.cabral.emaishapay.fragments.buy_fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.activities.Login;
import com.cabral.emaishapay.activities.TokenAuthActivity;
import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.adapters.buyInputsAdapters.ProductReviewsAdapter;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.models.ratings.GiveRating;
import com.cabral.emaishapay.network.BuyInputsAPIClient;
import com.cabral.emaishapay.utils.ValidateInputs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hyogeun.github.com.colorratingbarlib.ColorRatingBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProductRatingReviewFragment extends Fragment {


    View rootView;
    Toolbar toolbar;

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
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.Review));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        return rootView;
    }


//    public void showRatingsAndReviewsOfProduct() {
//
//        int rating_1_count = productDetails.getOne_ratio();
//        int rating_2_count = productDetails.getTwo_ratio();
//        int rating_3_count = productDetails.getThree_ratio();
//        int rating_4_count = productDetails.getFour_ratio();
//        int rating_5_count = productDetails.getFive_ratio();
//
//        final Dialog reviews_ratings_dialog = new Dialog(getContext(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
//        reviews_ratings_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        reviews_ratings_dialog.setCancelable(true);
//        reviews_ratings_dialog.setContentView(R.layout.dialog_product_rating_reviews);
//        reviews_ratings_dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
//
//        // Bind Dialog Views
//        TextView average_rating = reviews_ratings_dialog.findViewById(R.id.average_rating);
//        TextView total_rating_count = reviews_ratings_dialog.findViewById(R.id.total_rating_count);
//        ProgressBar rating_progress_5 = reviews_ratings_dialog.findViewById(R.id.rating_progress_5);
//        ProgressBar rating_progress_4 = reviews_ratings_dialog.findViewById(R.id.rating_progress_4);
//        ProgressBar rating_progress_3 = reviews_ratings_dialog.findViewById(R.id.rating_progress_3);
//        ProgressBar rating_progress_2 = reviews_ratings_dialog.findViewById(R.id.rating_progress_2);
//        ProgressBar rating_progress_1 = reviews_ratings_dialog.findViewById(R.id.rating_progress_1);
//        Button rate_product_button = reviews_ratings_dialog.findViewById(R.id.rate_product);
//        ImageButton dialog_back_button = reviews_ratings_dialog.findViewById(R.id.dialog_button);
//        RecyclerView reviews_list_recycler = reviews_ratings_dialog.findViewById(R.id.reviews_list_recycler);
//
//        reviews_list_recycler.setNestedScrollingEnabled(false);
//        ViewCompat.setNestedScrollingEnabled(reviews_list_recycler, false);
//
//        average_rating.setText("" + productDetails.getRating());
//        total_rating_count.setText(String.valueOf(productDetails.getTotal_user_rated()));
//
//        rating_progress_1.setProgress(rating_1_count);
//        rating_progress_2.setProgress(rating_2_count);
//        rating_progress_3.setProgress(rating_3_count);
//        rating_progress_4.setProgress(rating_4_count);
//        rating_progress_5.setProgress(rating_5_count);
//
//        dialogLoader = new DialogLoader(getContext());
//        productReviews = new ArrayList<>();
//        // Initialize the ReviewsAdapter for RecyclerView
//        ProductReviewsAdapter reviewsAdapter = new ProductReviewsAdapter(getContext(), productReviews);
//
//        // Set the Adapter and LayoutManager to the RecyclerView
//        reviews_list_recycler.setAdapter(reviewsAdapter);
//        reviews_list_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
//        reviews_list_recycler.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
//        getProductReviews("" + productDetails.getProductsId(), reviewsAdapter);
//
//        rate_product_button.setOnClickListener(v -> {
//            if (ConstantValues.IS_USER_LOGGED_IN) {
//                showRateProductDialog();
//            } else {
//                getContext().startActivity(new Intent(getContext(), Login.class));
//                ((WalletHomeActivity) getContext()).finish();
//                ((WalletHomeActivity) getContext()).overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
//            }
//        });
//
//        dialog_back_button.setOnClickListener(v -> reviews_ratings_dialog.dismiss());
//
//        reviews_ratings_dialog.show();
//    }
//
//    public void showRateProductDialog() {
//
//        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
//        View dialogView = getLayoutInflater().inflate(R.layout.dialog_rate_product, null);
//        dialog.setView(dialogView);
//        dialog.setCancelable(true);
//
//        final ColorRatingBar dialog_rating_bar = dialogView.findViewById(R.id.dialog_rating_bar);
//        final EditText dialog_author_name = dialogView.findViewById(R.id.dialog_author_name);
//        final EditText dialog_author_message = dialogView.findViewById(R.id.dialog_author_message);
//        final Button dialog_button = dialogView.findViewById(R.id.dialog_button);
//
//        final AlertDialog rateProductDialog = dialog.create();
//
//        dialog_button.setOnClickListener(v -> {
//            if (ValidateInputs.isValidName(dialog_author_name.getText().toString())) {
//                if (!"".equalsIgnoreCase(dialog_author_message.getText().toString())) {
//
//                    rateProductDialog.dismiss();
//
//                    RequestGiveRating(
//                            String.valueOf(productDetails.getProductsId()),
//                            String.valueOf(customerID),
//                            dialog_author_name.getText().toString().trim(),
//                            String.valueOf((int) dialog_rating_bar.getRating()),
//                            String.valueOf(productDetails.getVendors_id()),
//                            String.valueOf(productDetails.getLanguageId()),
//                            dialog_author_message.getText().toString().trim()
//                    );
//                       /* getNonceForProductRating
//                                (
//                                        String.valueOf(productDetails.getId()),
//                                        String.valueOf(dialog_rating_bar.getRating()),
//                                        dialog_author_name.getText().toString().trim(),
//                                        dialog_author_email.getText().toString().trim(),
//                                        dialog_author_message.getText().toString().trim()
//                                );*/
//
//                } else {
//                    dialog_author_message.setError(getContext().getString(R.string.enter_message));
//                }
//
//            } else {
//                dialog_author_name.setError(getContext().getString(R.string.enter_name));
//            }
//        });
//
//        rateProductDialog.show();
//    }


    private void RequestGiveRating(String products_id, String customers_id, String customers_nam, String reviews_rating,
                                   String vendors_id, String languages_id, String reviews_text) {

        Map<String, String> map = new HashMap<>();
        map.put("products_id", products_id);
        map.put("customers_id", customers_id);
        map.put("customers_name", customers_nam);
        map.put("reviews_rating", reviews_rating);
        map.put("vendors_id",vendors_id);
        map.put("languages_id", languages_id);
        map.put("reviews_text", reviews_text);

        String access_token = TokenAuthActivity.WALLET_ACCESS_TOKEN;
        Call<GiveRating> call = BuyInputsAPIClient.getInstance().giveRating(access_token,map);

        call.enqueue(new Callback<GiveRating>() {
            @Override
            public void onResponse(Call<GiveRating> call, Response<GiveRating> response) {

                if (response.isSuccessful()) {

                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<GiveRating> call, Throwable t) {
                Toast.makeText(getContext(), "NetworkCallFailure: " + t, Toast.LENGTH_SHORT).show();
            }
        });
    }


}