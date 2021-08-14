package com.cabral.emaishapay.network.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.RewriteQueriesToDropUnusedColumns;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(foreignKeys = {
        @ForeignKey(entity = Transactions.class,
                parentColumns = "id",
                childColumns = "user_transaction_id",
                onDelete = ForeignKey.CASCADE)},
        indices = {@Index(value = "user_transaction_id")
        })
public class UserTransactions implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "user_transaction_id")
    private String user_transaction_id;
    @ColumnInfo(name = "type")
    private String type;
    @ColumnInfo(name ="amount")
    private double amount;
    @ColumnInfo(name ="ft_discount")
    private double discount;
    @ColumnInfo(name = "charge")
    private double charge;
    @ColumnInfo(name = "created_at")
    private String dateCompleted;
    @ColumnInfo(name = "trans_message")
    private String status;
    @ColumnInfo(name = "referenceNumber")
    private String referenceNumber;
    @ColumnInfo(name = "phoneNumber")
    private String phoneNumber;
    @ColumnInfo(name = "date")
    private String date;
    @ColumnInfo(name = "receiver")
    private String receiver;
    @ColumnInfo(name = "sender")
    private String sender;
    @ColumnInfo(name = "receiptNumber")
    private String receiptNumber;
    @ColumnInfo(name = "trans_currency")
    private String trans_currency;
    @ColumnInfo(name = "senderUserId")
    private String senderUserId;
    @ColumnInfo(name = "receiverUserId")
    private String receiverUserId;

    public UserTransactions(int id, String user_transaction_id, String type, double amount, double discount, double charge, String dateCompleted, String status, String referenceNumber, String phoneNumber, String date, String receiver, String sender, String receiptNumber, String trans_currency, String senderUserId, String receiverUserId) {
        this.id = id;
        this.user_transaction_id = user_transaction_id;
        this.type = type;
        this.amount = amount;
        this.discount = discount;
        this.charge = charge;
        this.dateCompleted = dateCompleted;
        this.status = status;
        this.referenceNumber = referenceNumber;
        this.phoneNumber = phoneNumber;
        this.date = date;
        this.receiver = receiver;
        this.sender = sender;
        this.receiptNumber = receiptNumber;
        this.trans_currency = trans_currency;
        this.senderUserId = senderUserId;
        this.receiverUserId = receiverUserId;
    }

    protected UserTransactions(Parcel in) {
        id = in.readInt();
        user_transaction_id = in.readString();
        type = in.readString();
        amount = in.readDouble();
        discount = in.readDouble();
        charge = in.readDouble();
        dateCompleted = in.readString();
        status = in.readString();
        referenceNumber = in.readString();
        phoneNumber = in.readString();
        date = in.readString();
        receiver = in.readString();
        sender = in.readString();
        receiptNumber = in.readString();
        trans_currency = in.readString();
        senderUserId = in.readString();
        receiverUserId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(user_transaction_id);
        dest.writeString(type);
        dest.writeDouble(amount);
        dest.writeDouble(discount);
        dest.writeDouble(charge);
        dest.writeString(dateCompleted);
        dest.writeString(status);
        dest.writeString(referenceNumber);
        dest.writeString(phoneNumber);
        dest.writeString(date);
        dest.writeString(receiver);
        dest.writeString(sender);
        dest.writeString(receiptNumber);
        dest.writeString(trans_currency);
        dest.writeString(senderUserId);
        dest.writeString(receiverUserId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserTransactions> CREATOR = new Creator<UserTransactions>() {
        @Override
        public UserTransactions createFromParcel(Parcel in) {
            return new UserTransactions(in);
        }

        @Override
        public UserTransactions[] newArray(int size) {
            return new UserTransactions[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_transaction_id() {
        return user_transaction_id;
    }

    public void setUser_transaction_id(String user_transaction_id) {
        this.user_transaction_id = user_transaction_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getCharge() {
        return charge;
    }

    public void setCharge(double charge) {
        this.charge = charge;
    }

    public String getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(String dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getTrans_currency() {
        return trans_currency;
    }

    public void setTrans_currency(String trans_currency) {
        this.trans_currency = trans_currency;
    }

    public String getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(String senderUserId) {
        this.senderUserId = senderUserId;
    }

    public String getReceiverUserId() {
        return receiverUserId;
    }

    public void setReceiverUserId(String receiverUserId) {
        this.receiverUserId = receiverUserId;
    }
}
