package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CardResponse {
    private static final String TAG = "CardResponse";
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("cards")
    @Expose
    private List<Cards> cardsList;

    public List<Cards> getCardsList() {
        return cardsList;
    }

    public void setCardsList(List<Cards> cardsList) {
        this.cardsList = cardsList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


        public class Cards {
            @SerializedName("id")
            @Expose
            public String id;
            @SerializedName("account_name")
            @Expose
            private String account_name;

            @SerializedName("card_number")
            @Expose
            private String card_number;
            @SerializedName("cvv")
            @Expose
            private String cvv;
            @SerializedName("expiry")
            @Expose
            private String expiry;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getAccount_name() {
                return account_name;
            }

            public void setAccount_name(String account_name) {
                this.account_name = account_name;
            }

            public String getCard_number() {
                return card_number;
            }

            public void setCard_number(String card_number) {
                this.card_number = card_number;
            }

            public String getCvv() {
                return cvv;
            }

            public void setCvv(String cvv) {
                this.cvv = cvv;
            }

            public String getExpiry() {
                return expiry;
            }

            public void setExpiry(String expiry) {
                this.expiry = expiry;
            }
        }
    }

