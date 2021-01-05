package com.cabral.emaishapay.models.product_model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



public class ProductMeasure implements Parcelable {

    @SerializedName("product_id")
    @Expose
    private String 	product_id;

    @SerializedName("products_weight_unit")
    @Expose
    private String products_weight_unit;

    @SerializedName("products_weight")
    @Expose
    private String products_weight;

    @SerializedName("products_price")
    @Expose
    private String products_price;

    private boolean checked;

    public ProductMeasure() {

    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
//********** Describes the kinds of Special Objects contained in this Parcelable Instance's marshaled representation *********//

    @Override
    public int describeContents() {
        return 0;
    }



    //********** Writes the values to the Parcel *********//

    @Override
    public void writeToParcel(Parcel parcel_out, int flags) {
        parcel_out.writeString(product_id);
        parcel_out.writeString(products_weight_unit);
        parcel_out.writeString(products_weight);
        parcel_out.writeString(products_price);
    }



    //********** Generates Instances of Parcelable class from a Parcel *********//

    public static final Creator<ProductMeasure> CREATOR = new Creator<ProductMeasure>() {
        // Creates a new Instance of the Parcelable class, Instantiating it from the given Parcel
        @Override
        public ProductMeasure createFromParcel(Parcel parcel_in) {
            return new ProductMeasure(parcel_in);
        }

        // Creates a new array of the Parcelable class
        @Override
        public ProductMeasure[] newArray(int size) {
            return new ProductMeasure[size];
        }
    };



    //********** Retrieves the values from the Parcel *********//

    public ProductMeasure(Parcel parcel_in) {
        this.product_id = parcel_in.readString();
        this.products_weight_unit = parcel_in.readString();
        this.products_weight = parcel_in.readString();
        this.products_price = parcel_in.readString();
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getProducts_weight_unit() {
        return products_weight_unit;
    }

    public String getProducts_weight() {
        return products_weight;
    }

    public String getProducts_price() {
        return products_price;
    }

    public static Creator<ProductMeasure> getCREATOR() {
        return CREATOR;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public void setProducts_weight_unit(String products_weight_unit) {
        this.products_weight_unit = products_weight_unit;
    }

    public void setProducts_weight(String products_weight) {
        this.products_weight = products_weight;
    }

    public void setProducts_price(String products_price) {
        this.products_price = products_price;
    }
}
