package com.cabral.emaishapay.adapters.buyInputsAdapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;


import com.cabral.emaishapay.R;

import com.cabral.emaishapay.activities.WalletHomeActivity;
import com.cabral.emaishapay.app.EmaishaPayApp;
import com.cabral.emaishapay.constants.ConstantValues;
import com.cabral.emaishapay.models.ratings.GetRatings;
import com.cabral.emaishapay.models.ratings.ProductReviews;
import com.cabral.emaishapay.network.BuyInputsAPIClient;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * ProductReviewsAdapter is the adapter class of RecyclerView holding List of Product Reviews in Product_Description
 **/

public class UserProductReviewsAdapter extends RecyclerView.Adapter<UserProductReviewsAdapter.MyViewHolder> {

    Context context;
    private List<ProductReviews> reviewsList;


    public UserProductReviewsAdapter(Context context, List<ProductReviews> reviewsList) {
        this.context = context;
        this.reviewsList = reviewsList;
    }



    //********** Called to Inflate a Layout from XML and then return the Holder *********//

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        // Inflate the custom layout
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rating_review_list_card, parent, false);

        return new MyViewHolder(itemView);
    }



    //********** Called by RecyclerView to display the Data at the specified Position *********//

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        
        // Get the data model based on Position
        final ProductReviews review = reviewsList.get(position);
        
        holder.authore_name.setText(review.getCustomers_name());
        holder.authore_message.setText(review.getReview());
        
//

        DateFormat parseFormat = new SimpleDateFormat( "yyyy-mm-dd");
        //2021-03-30 07:52:57


        DateFormat parseFormatResult = new SimpleDateFormat( "MMMM dd, yyyy");

        try {
            Date date = parseFormat.parse(review.getDateCreated()+"");
            String stringDate = parseFormatResult.format(date);

            holder.authore_date.setText(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        holder.rating.setRating((float) review.getRating());

        holder.number_of_people.setText(review.getReads()+" People found this review helpful");
        holder.userLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeReview(review.getId()+"",holder.userLike);
            }
        });
        
    }



    //********** Returns the total number of items in the data set *********//

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }



    /********** Custom ViewHolder provides a direct reference to each of the Views within a Data_Item *********/

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RatingBar rating;
        TextView authore_name, authore_date, authore_message,number_of_people;
        ImageView userLike;

        public MyViewHolder(final View itemView) {
            super(itemView);

            authore_name = itemView.findViewById(R.id.rating_person);
            rating = itemView.findViewById(R.id.individual_rating);
            authore_date = itemView.findViewById(R.id.rating_date);
            authore_message = itemView.findViewById(R.id.review_text);
            userLike = itemView.findViewById(R.id.like);
            number_of_people = itemView.findViewById(R.id.number_of_people);

        }
    }



    private void likeReview(final String reviews_id, ImageView userLike) {

        userLike.setImageResource(R.drawable.ic_like_blue);

        String customerID = WalletHomeActivity.getPreferences(WalletHomeActivity.PREFERENCES_WALLET_USER_ID, context);
        String access_token = WalletHomeActivity.WALLET_ACCESS_TOKEN;
        Call<GetRatings> call = BuyInputsAPIClient.getInstance()
                .likeReview
                        (       access_token,
                                customerID,
                                reviews_id,
                                "" + ConstantValues.LANGUAGE_ID
                        );

        call.enqueue(new Callback<GetRatings>() {
            @Override
            public void onResponse(Call<GetRatings> call, Response<GetRatings> response) {


                if (response.isSuccessful()) {
                    reviewsList.addAll(response.body().getData());
                    notifyDataSetChanged();

                } else {
                    Toast.makeText(EmaishaPayApp.getContext(), response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GetRatings> call, Throwable t) {
                Toast.makeText(EmaishaPayApp.getContext(), "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }
}
