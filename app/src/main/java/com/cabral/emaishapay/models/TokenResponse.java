package com.cabral.emaishapay.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenResponse {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private TokenData data = null;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TokenData getData() {
        return data;
    }

    public void setData(TokenData data) {
        this.data = data;
    }


    public class TokenData{
        @SerializedName("access_token")
        @Expose
        private String access_token;
        @SerializedName("accountRole")
        @Expose
        private String accountRole;

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public String getAccountRole() {
            return accountRole;
        }

        public void setAccountRole(String accountRole) {
            this.accountRole = accountRole;
        }
    }
}
