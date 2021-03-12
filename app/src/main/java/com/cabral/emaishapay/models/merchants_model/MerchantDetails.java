package com.cabral.emaishapay.models.merchants_model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;


public class MerchantDetails {
    @SerializedName("shop_id")
    @Expose
    private long merchantId;
    @SerializedName("shop_name")
    @Expose
    private String businessName;
    @SerializedName("shop_contact")
    @Expose
    private String phoneNumber;
    @SerializedName("shop_email")
    @Expose
    private String email;
    @SerializedName("shop_address")
    @Expose
    private String address;
    @SerializedName("shop_currency")
    @Expose
    private String currency;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("created_at")
    @Expose
    private String created_at;
    @SerializedName("updated_at")
    @Expose
    private String updated_at;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("productPrices")
    @Expose
    private Map<String, String[]> productPrices;
    @SerializedName("emaisha_wallet")
    @Expose
    private String merchant_wallet_id;
    @SerializedName("emaisha_wallet_business_id")
    @Expose
    private String merchant_id;

    public final static Parcelable.Creator<MerchantDetails> CREATOR = new Parcelable.Creator<MerchantDetails>() {

        @SuppressWarnings({
                "unchecked"
        })
        public MerchantDetails createFromParcel(Parcel in) {
            return new MerchantDetails(in);
        }

        public MerchantDetails[] newArray(int size) {
            return (new MerchantDetails[size]);
        }

    };

    protected MerchantDetails(Parcel in) {
        this.merchantId = ((long) in.readValue((long.class.getClassLoader())));
        this.businessName = ((String) in.readValue((String.class.getClassLoader())));
        this.phoneNumber = ((String) in.readValue((String.class.getClassLoader())));
        this.email = ((String) in.readValue((String.class.getClassLoader())));
        this.address = ((String) in.readValue((String.class.getClassLoader())));
        this.currency = ((String) in.readValue((String.class.getClassLoader())));
        this.latitude = ((String) in.readValue((String.class.getClassLoader())));
        this.longitude = ((String) in.readValue((String.class.getClassLoader())));
        this.created_at = ((String) in.readValue((String.class.getClassLoader())));
        this.updated_at = ((String) in.readValue((String.class.getClassLoader())));
        this.distance = ((String) in.readValue((String.class.getClassLoader())));
        this.productPrices = ((Map) in.readValue((Map.class.getClassLoader())));
        this.merchant_wallet_id = ((String) in.readValue((String.class.getClassLoader())));
        this.merchant_id = ((String) in.readValue((String.class.getClassLoader())));
    }

    public long getMerchantId() {
        return merchantId;
    }

    public String getBusinessName() {
        return businessName;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getCurrency() {
        return currency;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getDistance() {
        return distance;
    }

    public Map<String, String[]> getProductPrices() {
        return productPrices;
    }

    public int getTotalOrderPrice() {
        int total=0;
        for (Map.Entry<String,String[]> entry : this.productPrices.entrySet())  {
            if(entry.getValue()!=null)
                total+= ( Integer.parseInt(entry.getValue()[0])*Integer.parseInt(entry.getValue()[1]) );
        }
        return total;
    }

    public String getMerchant_wallet_id() {
        return merchant_wallet_id;
    }

    public String getMerchant_id() {
        return merchant_id;
    }
}
