package com.cabral.emaishapay.fragments.buy_fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cabral.emaishapay.R;

import com.cabral.emaishapay.adapters.buyInputsAdapters.UserProductReviewsAdapter;
import com.cabral.emaishapay.app.EmaishaPayApp;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.customs.DialogLoader;
import com.cabral.emaishapay.fragments.TokenAuthFragment;
import com.cabral.emaishapay.models.ratings.GetRatings;
import com.cabral.emaishapay.network.BuyInputsAPIClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.cabral.emaishapay.models.ratings.ProductReviews;

import static com.cabral.emaishapay.fragments.buy_fragments.Product_Description.productDetails;

public class ProductRatingReviewListFragment extends Fragment {

    FrameLayout writeReview;
    AppCompatButton writeReviewBtn;
    View rootView;
    Toolbar toolbar;
    DialogLoader dialogLoader;

    public List<ProductReviews> productReviews=new ArrayList<>();

    public ProductRatingReviewListFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_product_rating_review_list, container, false);

        toolbar = rootView.findViewById(R.id.toolbar_review);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.Review));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        dialogLoader = new DialogLoader(getContext());

        writeReview = rootView.findViewById(R.id.write_review_layout_button);
        writeReviewBtn = rootView.findViewById(R.id.write_review_button);
        


        //average_rating
        writeReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ProductRatingReviewFragment();

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment2, fragment)
                        .addToBackStack(null).commit();
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showRatingsAndReviewsOfProduct(view);
        
    }

    private void getProductReviews(final String productID, final UserProductReviewsAdapter adapter) {

        dialogLoader.showProgressDialog();
        Call<GetRatings> call = BuyInputsAPIClient.getInstance()
                .getProductReviews
                        (
                                productID,
                                "" + ConstantValues.LANGUAGE_ID
                        );

        call.enqueue(new Callback<GetRatings>() {
            @Override
            public void onResponse(Call<GetRatings> call, Response<GetRatings> response) {

                dialogLoader.hideProgressDialog();

                String str = response.raw().request().url().toString();

                // Check if the Response is successful
                if (response.isSuccessful()) {
                    String strGson = new Gson().toJson(response.body().getData());

                    productReviews.addAll(response.body().getData());
                    int size = productReviews.size();
                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(EmaishaPayApp.getContext(), response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GetRatings> call, Throwable t) {
                dialogLoader.hideProgressDialog();
                Toast.makeText(EmaishaPayApp.getContext(), "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }


    public void showRatingsAndReviewsOfProduct(View view) {

        int rating_1_count = productDetails.getOne_ratio();
        int rating_2_count = productDetails.getTwo_ratio();
        int rating_3_count = productDetails.getThree_ratio();
        int rating_4_count = productDetails.getFour_ratio();
        int rating_5_count = productDetails.getFive_ratio();

        // Bind Dialog Views
        RatingBar average_rating = view.findViewById(R.id.average_rating);
        TextView average_rating_figure = view.findViewById(R.id.average_rating_figure);
        TextView total_rating_count = view.findViewById(R.id.total_rating_count);
        TextView total_review_count = view.findViewById(R.id.total_review_count);
        ProgressBar rating_progress_5 = view.findViewById(R.id.rating_progress_5);
        ProgressBar rating_progress_4 = view.findViewById(R.id.rating_progress_4);
        ProgressBar rating_progress_3 = view.findViewById(R.id.rating_progress_3);
        ProgressBar rating_progress_2 = view.findViewById(R.id.rating_progress_2);
        ProgressBar rating_progress_1 = view.findViewById(R.id.rating_progress_1);

//        RecyclerView reviews_list_recycler = view.findViewById(R.id.reviews_list_recycler);
//
//        reviews_list_recycler.setNestedScrollingEnabled(false);
//        ViewCompat.setNestedScrollingEnabled(reviews_list_recycler, false);

        average_rating.setRating(productDetails.getRating());
        average_rating_figure.setText( productDetails.getRating()+"" );
        total_rating_count.setText( "Based on "+productDetails.getTotal_user_rated()+" Rating" );
        total_review_count.setText( "and "+productDetails.getTotal_user_rated()+" Reviews" );

        rating_progress_1.setProgress(rating_1_count);
        rating_progress_2.setProgress(rating_2_count);
        rating_progress_3.setProgress(rating_3_count);
        rating_progress_4.setProgress(rating_4_count);
        rating_progress_5.setProgress(rating_5_count);

        TextView star5_total_number = view.findViewById(R.id.star5_total_number);
        TextView star4_total_number = view.findViewById(R.id.star4_total_number);
        TextView star3_total_number = view.findViewById(R.id.star3_total_number);
        TextView star2_total_number = view.findViewById(R.id.star2_total_number);
        TextView star1_total_number = view.findViewById(R.id.star1_total_number);

        star1_total_number.setText( ((int) (rating_1_count*productDetails.getTotal_user_rated()/100) )+""  );
        star2_total_number.setText( ((int) (rating_2_count*productDetails.getTotal_user_rated()/100) )+""  );
        star3_total_number.setText( ((int) (rating_3_count*productDetails.getTotal_user_rated()/100) )+""  );
        star4_total_number.setText( ((int) (rating_4_count*productDetails.getTotal_user_rated()/100) )+""  );
        star5_total_number.setText( ((int) (rating_5_count*productDetails.getTotal_user_rated()/100) )+""  );

        productReviews = new ArrayList<>();
         //Initialize the ReviewsAdapter for RecyclerView
        RecyclerView reviews_list_recycler=view.findViewById(R.id.ratings_review_recycler);
        UserProductReviewsAdapter reviewsAdapter = new UserProductReviewsAdapter(getContext(), productReviews);

        // Set the Adapter and LayoutManager to the RecyclerView
        reviews_list_recycler.setAdapter(reviewsAdapter);
        reviews_list_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        reviews_list_recycler.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        getProductReviews("" + productDetails.getProductsId(), reviewsAdapter);

    }


}