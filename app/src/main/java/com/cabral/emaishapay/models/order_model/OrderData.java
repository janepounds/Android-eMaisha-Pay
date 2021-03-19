package com.cabral.emaishapay.models.order_model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class OrderData {

    @SerializedName("success")
    @Expose
    private String success;
    @SerializedName("order_id")
    @Expose
    private String order_id;
    @SerializedName("data")
    @Expose
    private List<OrderDetails> data = new ArrayList<OrderDetails>();
    @SerializedName("message")
    @Expose
    private String message;

    /**
     *
     * @return
     * The success
     */
    public String getSuccess() {
        return success;
    }

    /**
     *
     * @param success
     * The success
     */
    public void setSuccess(String success) {
        this.success = success;
    }

    /**
     *
     * @return
     * The data
     */
    public List<OrderDetails> getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(List<OrderDetails> data) {
        this.data = data;
    }

    /**
     *
     * @return
     * The message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     * The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }
}
