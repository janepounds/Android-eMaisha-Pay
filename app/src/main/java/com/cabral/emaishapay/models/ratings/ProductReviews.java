package com.cabral.emaishapay.models.ratings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by muneeb.vectorcoder@gmail.com on 07/05/2018.
 */

public class ProductReviews {
    
    @SerializedName("reviews_id")
    @Expose
    private int id;
    @SerializedName("created_at")
    @Expose
    private String dateCreated;
    @SerializedName("comments")
    @Expose
    private String review;
    @SerializedName("rating")
    @Expose
    private int rating;
//    @SerializedName("email")
//    @Expose
//    private String email;
//    @SerializedName("first_name")
//    @Expose
//    private String first_name;

    @SerializedName("customers_name")
    @Expose
    private String customers_name;

    @SerializedName("reviews_read")
    @Expose
    private int reads;


    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getDateCreated() {
        return dateCreated;
    }
    
    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
    

    public String getReview() {
        return review;
    }
    
    public void setReview(String review) {
        this.review = review;
    }
    
    public int getRating() {
        return rating;
    }
    
    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getCustomers_name() {
        return customers_name;
    }

    public void setCustomers_name(String customers_name) {
        this.customers_name = customers_name;
    }

    public int getReads() {
        return  this.reads;
    }
}
