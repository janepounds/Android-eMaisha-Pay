package com.cabral.emaishapay.network;

import com.cabral.emaishapay.models.AccountResponse;
import com.cabral.emaishapay.models.CancelLoanResponse;
import com.cabral.emaishapay.models.WalletAuthenticationResponse;
import com.cabral.emaishapay.models.address_model.AddressData;
import com.cabral.emaishapay.models.address_model.Countries;
import com.cabral.emaishapay.models.address_model.Regions;
import com.cabral.emaishapay.models.address_model.Zones;
import com.cabral.emaishapay.models.coupons_model.CouponsData;
import com.cabral.emaishapay.models.BalanceResponse;
import com.cabral.emaishapay.models.InitiateTransferResponse;
import com.cabral.emaishapay.models.LoanListResponse;
import com.cabral.emaishapay.models.LoanPayResponse;
import com.cabral.emaishapay.models.MerchantInfoResponse;
import com.cabral.emaishapay.models.RequestLoanresponse;
import com.cabral.emaishapay.models.TokenResponse;
import com.cabral.emaishapay.models.WalletAuthentication;
import com.cabral.emaishapay.models.WalletLoanAddPicResponse;
import com.cabral.emaishapay.models.WalletPurchaseConfirmResponse;
import com.cabral.emaishapay.models.WalletPurchaseResponse;
import com.cabral.emaishapay.models.WalletTransactionReceiptResponse;
import com.cabral.emaishapay.models.WalletTransactionResponse;
import com.cabral.emaishapay.models.WalletUserRegistration;
import com.cabral.emaishapay.models.pages_model.PagesData;
import com.cabral.emaishapay.models.user_model.UserData;
import com.cabral.emaishapay.models.WalletTransaction;

import org.json.JSONObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * APIRequests contains all the Network Request Methods with relevant API Endpoints
 **/

public interface APIRequests {
    //******************** News Data ********************//

    //Update User
    @POST("update/{id}/{oldPassword}")
    Call<UserData> update(@Field("id") String id,
                          @Field("firstname") String firstname,
                          @Field("lastname") String lastname,
                          @Field("country") String country,
                          @Field("addressCountry") String addressCountry,
                          @Field("addressStreet") String addressStreet,
                          @Field("addressCityOrTown") String addressCityOrTown,
                          @Field("email") String email,
                          @Field("farmname") String farmname,
                          @Field("countryCode") String countryCode,
                          @Field("oldPassword") String oldPassword,
                          @Field("latitude") String latitude,
                          @Field("longitude") String longitude,
                          @Field("password") String password
    );

    /**************  WALLET REQUESTS *******************************/
    @FormUrlEncoded
    @POST("user/check_account")
    Call<TokenResponse> checkWalletAccount(@Field("email") String email, @Field("phoneNumber") String phonenumber);

//    //wallet authentication
//    @FormUrlEncoded
//    @POST("emaishawallet/user/authenticate")
//    Call<WalletAuthentication> authenticate(@Field("email") String email,
//                                            @Field("password") String password
//    );

    //wallet authentication
    @FormUrlEncoded
    @POST("authenticate/emaishapay_app_user/login")
    Call<WalletAuthenticationResponse>authenticate(@Field("phoneNumber")String phoneNumber);

    @FormUrlEncoded
    @POST("emaishawallet/user/authenticate")
    Call<WalletAuthentication>authenticate(@Field("phoneNumber")String phoneNumber,@Field("password")String pin);

    //wallet registration
    @FormUrlEncoded
    @POST("emaishawallet/user/create")
    Call<WalletUserRegistration> create(@Field("firstname") String firstname,
                                        @Field("lastname") String lastname,
                                        @Field("email") String email,
                                        @Field("password") String password,
                                        @Field("phoneNumber") String phoneNumber,
                                        @Field("addressStreet") String addressStreet,
                                        @Field("addressCityOrTown") String addressCityOrTown
    );

    //refresh token
    @FormUrlEncoded
    @POST("wallet/token/get")
    Call<TokenResponse> getToken(
            @Field("phoneNumber") String phoneNumber,
            @Field("password") String password
    );

    //request balance
    @GET("wallet/balance/request")
    Call<BalanceResponse> requestBalance(@Header("Authorization") String token
    );

    //initiate transfer
    @FormUrlEncoded
    @POST("wallet/transfer/initiate")
    Call<InitiateTransferResponse> initiateTransfer(@Header("Authorization") String token,
                                                    @Field("amount") Double amount,
                                                    @Field("receiverPhoneNumber") String receiverPhoneNumber
    );

    //wallet transaction list
    @GET("wallet/transactions/list")
    Call<WalletTransactionResponse> transactionList(@Header("Authorization") String token);
    //wallet transaction list2

    @FormUrlEncoded
    @Headers({"Accept: application/json"})
    @POST("wallet/transactions")
    Call<WalletTransactionResponse> transactionList2(@Header("Authorization") String token, @Field("limit") int limit);

    //make transaction
    @FormUrlEncoded
    @Headers({"Accept: application/json"})
    @POST("wallet/payments/merchant")
    Call<WalletPurchaseResponse> makeTransaction(@Header("Authorization") String token,
                                                 @Field("merchantId") int merchantId,
                                                 @Field("amount") Double amount,
                                                 @Field("coupon") String coupon
    );

    //confirm payment
    @FormUrlEncoded
    @POST("wallet/payments/comfirm_paymerchant")
    Call<WalletPurchaseConfirmResponse> confirmPayment(
            @Field("merchantId") int merchantId,
            @Field("amount") double amount,
            @Field("coupon") String coupon
    );

    //get merchant information
    @GET("wallet/merchant/{merchantId}")
    Call<MerchantInfoResponse> getMerchant(@Header("Authorization") String token,
                                           @Path("merchantId") int merchantId
    );

    //get merchant information
    @GET("wallet/user/get/receiver_by_phone/{phonenumber}")
    Call<MerchantInfoResponse> getUserBusinessName(@Header("Authorization") String token,
                                                   @Path("phonenumber") String phonenumber
    );

    //get merchant receipt
    @GET("wallet/payments/receipt/{referenceNumber}")
    Call<WalletTransactionReceiptResponse> getReceipt(@Header("Authorization") String token,
                                                      @Path("referenceNumber") String referenceNumber
    );

    //get user loans
    @GET("wallet/loan/user/loans")
    Call<LoanListResponse> getUserLoans(@Query("userId") String userId
            /*@Header("Authorization") String token*/
    );

    //cancel loan

    @POST("wallet/loan/cancelRequest")
    Call<CancelLoanResponse> cancelLoanRequest(@Query("userId") String userId
            /*@Header("Authorization") String token*/
    );



    //request loans
    @POST("wallet/loan/user/request")
    Call<RequestLoanresponse> requestLoans(@Header("Authorization") String token,
                                           @Body JSONObject object
    );


    //create user credit
    @FormUrlEncoded
    @POST("wallet/flutter/payment/credituser")
    Call<WalletTransaction> creditUser(@Header("Authorization") String token,
                                       @Field("email") String email,
                                       @Field("amount") Double amount,
                                       @Field("referenceNumber") String referenceNumber
    );

    //voucher deposit
    @FormUrlEncoded
    @POST("wallet/payment/voucherdeposit")
    Call<CouponsData> voucherDeposit(@Header("Authorization") String token,
                                     @Field("email") String email,
                                     @Field("phoneNumber") String phoneNumber,
                                     @Field("codeEntered") String codeEntered
    );

    //loan pay
    @FormUrlEncoded
    @Headers({"Accept: application/json"})
    @POST("wallet/payments/loanpay")
    Call<LoanPayResponse> loanPay(@Header("Authorization") String token,
                                  @Field("amount") double amount,
                                  @Field("userId") String userId);

    @Multipart
    @POST("processregistration")
    Call<UserData> processRegistration(
            @Part("firstname") RequestBody firstName,
            @Part("lastname") RequestBody lastName,
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part("country_code") RequestBody countryCode,
            @Part("phoneNumber") RequestBody phoneNumber,
            @Part("addressStreet") RequestBody addressStreet,
            @Part("addressCityOrTown") RequestBody addressCityOrTown,
            @Part("address_district") RequestBody addressDistrict);

    @FormUrlEncoded
    @POST("processforgotpassword")
    Call<UserData> processForgotPassword(@Field("email") String customers_email_address);

    @FormUrlEncoded
    @POST("updatecustomerinfo")
    Call<UserData> updateCustomerInfo(@Field("customers_id") String customers_id,
                                      @Field("customers_firstname") String customers_firstname,
                                      @Field("customers_lastname") String customers_lastname,
                                      @Field("customers_gender") String customers_gender,
                                      @Field("customers_telephone") String customers_telephone,
                                      @Field("customers_dob") String customers_dob,
                                      @Field("image_id") String image_id);

    //******************** Address Data ********************//
    @POST("getcountries")
    Call<Countries> getCountries();

    @FormUrlEncoded
    @POST("getzones")
    Call<Zones> getZones(@Field("zone_country_id") String zone_country_id);

    @FormUrlEncoded
    @POST("getalladdress")
    Call<AddressData> getAllAddress(@Field("customers_id") String customers_id);

    @FormUrlEncoded
    @POST("getregions")
    Call<Regions> getAllRegions(@Field("latest_id") int latest_id);


    //******************** Static Pages Data ********************//
    @FormUrlEncoded
    @POST("getallpages")
    Call<PagesData> getStaticPages(@Field("language_id") int language_id);

    //store personal info
    @FormUrlEncoded
    @POST("store_personal_info")
    Call<AccountResponse> storePersonalInfo(
            @Field("user_id") String user_id,
            @Field("dob") String dob,
            @Field("gender") String gender,
            @Field("next_of_kin") String next_of_kin,
            @Field("next_of_kin_contact") String next_of_kin_contact,
            @Field("pic") String picture
    );

    //getAccount info
    @GET("user/account_data/{userId}")
    Call<AccountResponse>getAccountInfo(
            @Path("userId") String userId
    );

    //store id info
    @FormUrlEncoded
    @POST("store_user_id_info")
    Call<AccountResponse> storeIdInfo(
            @Field("user_id") String user_id,
            @Field("id_type") String id_type,
            @Field("id_number") String id_number,
            @Field("expiry_date") String expiry_date,
            @Field("front") String front,
            @Field("back") String back
    );

    //store employment info
    @FormUrlEncoded
    @POST("store_user_employment_info")
    Call<AccountResponse> storeEmploymentInfo(
            @Field("user_id") String user_id,
            @Field("employer") String employer,
            @Field("designation") String designation,
            @Field("location") String location,
            @Field("employment_contact") String contact,
            @Field("employee_id") String employee_id
    );

    //store business info
    @FormUrlEncoded
    @POST("store_user_business_info")
    Call<AccountResponse> storeBusinessInfo(
            @Field("user_id") String user_id,
            @Field("business_name") String business_name,
            @Field("business_location") String location,
            @Field("registration_no") String reg_no,
            @Field("trade_license") String trade_license,
            @Field("license_no") String license_number,
            @Field("registration_cert") String reg_certificate
    );
}
