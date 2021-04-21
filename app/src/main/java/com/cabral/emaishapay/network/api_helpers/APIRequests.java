package com.cabral.emaishapay.network.api_helpers;

import com.cabral.emaishapay.models.AccountResponse;
import com.cabral.emaishapay.models.BeneficiaryResponse;
import com.cabral.emaishapay.models.CancelLoanResponse;
import com.cabral.emaishapay.models.CardResponse;
import com.cabral.emaishapay.models.ChangePinResponse;
import com.cabral.emaishapay.models.InitiateTransferResponse;
import com.cabral.emaishapay.models.InitiateWithdrawResponse;
import com.cabral.emaishapay.models.SecurityQnsResponse;
import com.cabral.emaishapay.models.WalletAuthenticationResponse;
import com.cabral.emaishapay.models.address_model.AddressData;
import com.cabral.emaishapay.models.address_model.Countries;
import com.cabral.emaishapay.models.address_model.Regions;
import com.cabral.emaishapay.models.address_model.Zones;
import com.cabral.emaishapay.models.coupons_model.CouponsData;
import com.cabral.emaishapay.models.BalanceResponse;
import com.cabral.emaishapay.models.LoanListResponse;
import com.cabral.emaishapay.models.LoanPayResponse;
import com.cabral.emaishapay.models.ConfirmationDataResponse;
import com.cabral.emaishapay.models.RequestLoanresponse;
import com.cabral.emaishapay.models.TokenResponse;
import com.cabral.emaishapay.models.WalletAuthentication;
import com.cabral.emaishapay.models.WalletPurchaseConfirmResponse;
import com.cabral.emaishapay.models.WalletPurchaseResponse;
import com.cabral.emaishapay.models.WalletTransactionReceiptResponse;
import com.cabral.emaishapay.models.WalletTransactionResponse;
import com.cabral.emaishapay.models.external_transfer_model.SettlementResponse;
import com.cabral.emaishapay.models.user_model.UserData;
import com.cabral.emaishapay.models.WalletTransaction;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
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

    ////wallet authentication
    @FormUrlEncoded
    @POST("authenticate/emaishapay_app_user/login")
    Call<WalletAuthenticationResponse>authenticate(@Field("phoneNumber")String phoneNumber,
                                                   @Field("password")String password,
                                                   @Field("request_id") String request_id,
                                                   @Field("category") String category,
                                                   @Field("action_id") String action_id

    );

    ////wallet authentication
    @FormUrlEncoded
    @POST("user/resend/otp")
    Call<WalletAuthenticationResponse>resendOtp(@Field("phoneNumber")String phoneNumber,
                                                   @Field("password")String password,
                                                   @Field("request_id") String request_id,
                                                   @Field("action_id") String action_id

    );

    @FormUrlEncoded
    @POST("authenticate/verify/code")
    Call<WalletAuthentication>confirmLogin(@Field("phoneNumber")String phoneNumber,
                                           @Field("otp") String otp,
                                           @Field("password")String pin,
                                           @Field("request_id") String request_id,
                                           @Field("category") String category,
                                           @Field("action_id")String action_id);


   // //refresh token
    @FormUrlEncoded
    @POST("wallet/token/get")
    Call<TokenResponse> getToken(
            @Field("phoneNumber") String phoneNumber,
            @Field("password") String password,
            @Field("request_id") String request_id,
            @Field("category") String category,
            @Field("action_id")String action_id
    );

    // //request balance

    @GET("wallet/balance/request")
    Call<BalanceResponse> requestBalance(@Header("Authorization") String token,
                                         @Query("request_id") String request_id,
                                         @Query("category") String category,
                                         @Query("action_id")String action_id
    );


    // //initiate transfer
    @FormUrlEncoded
    @POST("wallet/transfer/initiate")
    Call<InitiateTransferResponse> initiateTransfer(@Header("Authorization") String token,
                                                    @Field("amount") Double amount,
                                                    @Field("receiverPhoneNumber") String receiverPhoneNumber,
                                                    @Field("request_id") String request_id,
                                                    @Field("category") String category,
                                                    @Field("service_code") String service_code,
                                                    @Field("action_id")String action_id
    );

    // //initiate transfer settlement
    @FormUrlEncoded
    @POST("wallet/transfer/settlement")
    Call<SettlementResponse> recordSettlementTransfer(@Header("Authorization") String token,
                                                      @Field("amount") Double amount,
                                                      @Field("thirdParty") String thirdParty,
                                                      @Field("third_party_fee") Double third_party_fee,
                                                      @Field("destination_type") String destination_type,
                                                      @Field("destination_account_no") String destination_account_no,
                                                      @Field("beneficiary_name") String beneficiary_name,
                                                      @Field("destination_name") String destination_name,
                                                      @Field("reference") String reference,
                                                      @Field("third_party_status") String third_party_status,
                                                      @Field("third_party_id") String third_party_id,
                                                      @Field("request_id") String request_id,
                                                      @Field("category") String category,
                                                      @Field("action_id")String action_id
                                                      );

    // //wallet transaction list
    @FormUrlEncoded
    @POST("wallet/transactions/list")
    Call<WalletTransactionResponse> transactionList(@Header("Authorization") String token,
                                                    @Field("request_id") String request_id,
                                                    @Field("category") String category,
                                                    @Field("action_id")String action_id);

    // //wallet transaction list2
    @FormUrlEncoded
    @POST("wallet/transactions/list")
    Call<WalletTransactionResponse> transactionList2(@Header("Authorization") String token, @Field("limit") int limit, @Field("request_id") String request_id, @Field("category") String category,
                                                     @Field("action_id")String action_id);

    // //make transaction
    @FormUrlEncoded
    @Headers({"Accept: application/json"})
    @POST("wallet/payments/merchant")
    Call<WalletPurchaseResponse> makeTransaction(@Header("Authorization") String token,
                                                 @Field("merchantId") String merchantId,
                                                 @Field("amount") Double amount,
                                                 @Field("coupon") String coupon,
                                                 @Field("request_id") String request_id,
                                                 @Field("category") String category,
                                                 @Field("action_id")String action_id,
                                                    @Field("service_code") String service_code
    );

    // /confirm payment
    @FormUrlEncoded
    @POST("wallet/payments/comfirm_paymerchant")
    Call<WalletPurchaseConfirmResponse> confirmPayment(
            @Field("merchantId") int merchantId,
            @Field("amount") double amount,
            @Field("coupon") String coupon,
            @Field("request_id") String request_id
    );

    // //get merchant information
    @GET("wallet/agent/get-name/{AgentId}")
    Call<ConfirmationDataResponse> getMerchant(@Header("Authorization") String token,
                                               @Path("AgentId") String merchantId,
                                               @Query("request_id") String request_id,
                                               @Query("category") String category,
                                               @Query("action_id")String action_id
    );

  // //get user business name
  @GET("wallet/user/get/receiver_by_phone/{phonenumber}/{purpose}")
  Call<ConfirmationDataResponse> getUserBusinessName(@Header("Authorization") String token,
                                                     @Path("phonenumber") String phonenumber,
                                                     @Path("purpose") String purpose,
                                                     @Query("request_id") String request_id,
                                                     @Query("action_id")String action_id
  );


  //get receiver business name,sender business name and Transaction charge
  @FormUrlEncoded
  @POST("wallet/customer/metadata")
  Call<ConfirmationDataResponse> validateAgentFundsTransfer(@Header("Authorization") String token,
                                                            @Field("senderNumber") String senderNumber,
                                                            @Field("receiverNumber") String receiverNumber,
                                                            @Field("amount") double amount,
                                                            @Field("request_id") String request_id,
                                                            @Field("category") String category,
                                                            @Field("action_id")String action_id
  );

    //get merchant receipt
    @GET("wallet/payments/receipt/{referenceNumber}")
    Call<WalletTransactionReceiptResponse> getReceipt(@Header("Authorization") String token,
                                                      @Path("referenceNumber") String referenceNumber,
                                                      @Query("request_id") String request_id,
                                                      @Query("category") String category,
                                                      @Query("action_id")String action_id
    );

    // //get user loans
    @GET("wallet/loan/user/loans")
    Call<LoanListResponse> getUserLoans(
            @Header("Authorization") String token,
            @Query("userId") String userId,
             @Query("request_id") String request_id,
            @Query("category") String category,
            @Query("action_id")String action_id
            /*@Header("Authorization") String token*/
    );

    // //cancel loan
    @POST("wallet/loan/cancelRequest")
    Call<CancelLoanResponse> cancelLoanRequest(
            @Header("Authorization") String token,
            @Query("userId") String userId,
            @Field("request_id") String request_id,
            @Field("category") String category,
            @Field("action_id")String action_id
            /*@Header("Authorization") String token*/
    );



    // //request loans
    @POST("wallet/loan/user/request")
    Call<RequestLoanresponse> requestLoans(@Header("Authorization") String token,
                                           @Body JSONObject object,
                                           @Field("request_id") String request_id,
                                           @Field("category") String category,
                                           @Field("action_id")String action_id
    );


    // //create user credit
    @FormUrlEncoded
    @POST("wallet/payment/credituser")
    Call<WalletTransaction> creditUser(@Header("Authorization") String token,
                                       @Field("merchant_id") String receiver_id,
                                       @Field("amount") Double amount,
                                       @Field("referenceNumber") String referenceNumber,
                                       @Field("type") String type,
                                       @Field("thirdParty") String thirdParty,
                                       @Field("thirdParty_id") String thirdParty_id,
                                       @Field("isPending") Boolean isPending,
                                       @Field("request_id") String request_id,
                                       @Field("category") String category,
                                       @Field("action_id")String action_id
    );
    //wallet/thirdy-part-payment/credituser
    @FormUrlEncoded
    @POST("wallet/third-party-payment/credituser")
    Call<WalletTransaction> thirdpartyCreditUser(@Header("Authorization") String token,
                                                 @Field("merchant_id") String receiver_id,
                                                 @Field("amount") Double amount,
                                                 @Field("referenceNumber") String referenceNumber,
                                                 @Field("thirdParty") String thirdParty,
                                                 @Field("thirdParty_id") String thirdParty_id,
                                                 @Field("isPending") Boolean isPending,
                                                 @Field("request_id") String request_id,
                                                 @Field("category") String category,
                                                 @Field("action_id")String action_id
    );
    // //create user credit
    @FormUrlEncoded
    @POST("wallet/payment/eMaishaPayPayment")
    Call<WalletTransaction> eMaishaPayUserPayment(@Header("Authorization") String token,
                                       @Field("merchant_wallet_id") String receiver_id,
                                       @Field("amount") Double amount,
                                       @Field("referenceNumber") String referenceNumber,
                                       @Field("isPending") Boolean isPending,
                                       @Field("request_id") String request_id,
                                        @Field("category") String category,
                                        @Field("action_id")String action_id
    );

    // //voucher deposit
    @FormUrlEncoded
    @POST("wallet/payment/voucherdeposit")
    Call<CouponsData> voucherDeposit(@Header("Authorization") String token,
                                     @Field("codeEntered") String codeEntered,
                                     @Field("request_id") String request_id,
                                     @Field("category") String category,
                                     @Field("action_id")String action_id
    );

    // //loan pay
    @FormUrlEncoded
    @Headers({"Accept: application/json"})
    @POST("wallet/payments/loanpay")
    Call<LoanPayResponse> loanPay(@Header("Authorization") String token,
                                  @Field("amount") double amount,
                                  @Field("userId") String userId,
                                  @Field("request_id") String request_id,
                                  @Field("category") String category,
                                  @Field("action_id")String action_id);

    @FormUrlEncoded
    @POST("processregistration")
    Call<WalletAuthentication> processRegistration(
            @Field("firstname") String firstName,
            @Field("lastname") String lastName,
            @Field("password") String password,
            @Field("country_code") String countryCode,
            @Field("phoneNumber") String phoneNumber,
            @Field("addressStreet") String addressStreet,
            @Field("addressCityOrTown") String addressCityOrTown,
            @Field("address_district") String addressDistrict,
            @Field("id_type") String idType,
            @Field("id_number") String idNo,
            @Field("sec_qn_one") String first_security_qn,
            @Field("sec_qn_two") String second_security_qn,
            @Field("sec_qn_three") String third_security_qn,
            @Field("sec_ans_one") String first_qn_answer,
            @Field("sec_ans_two") String second_qn_answer,
            @Field("sec_ans_three") String third_qn_answer,
            @Field("request_id") String request_id,
            @Field("category") String category,
            @Field("action_id")String action_id);

    // /forgot password
    @FormUrlEncoded
    @POST("processforgotpassword")
    Call<UserData> processForgotPassword(
            @Header("Authorization") String token,
            @Field("email") String customers_email_address,
            @Field("request_id") String request_id,
            @Field("category") String category,
            @Field("action_id")String action_id);

    @FormUrlEncoded
    @POST("updatecustomerinfo")
    Call<UserData> updateCustomerInfo(@Field("customers_id") String customers_id,
                                      @Field("customers_firstname") String customers_firstname,
                                      @Field("customers_lastname") String customers_lastname,
                                      @Field("customers_gender") String customers_gender,
                                      @Field("customers_telephone") String customers_telephone,
                                      @Field("customers_dob") String customers_dob,
                                      @Field("image_id") String image_id,
                                      @Field("request_id") String request_id);

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



    // /store personal info
    @FormUrlEncoded
    @POST("store_personal_info")
    Call<AccountResponse> storePersonalInfo(
            @Header("Authorization") String token,
            @Field("user_id") String user_id,
            @Field("dob") String dob,
            @Field("gender") String gender,
            @Field("next_of_kin") String next_of_kin,
            @Field("next_of_kin_contact") String next_of_kin_contact,
            @Field("pic") String picture,
            @Field("request_id") String request_id,
            @Field("category") String category,
            @Field("action_id")String action_id
    );

    // //getAccount info
    @GET("user/account_data/{userId}")
    Call<AccountResponse>getAccountInfo(
            @Header("Authorization") String token,
            @Path("userId") String userId,
            @Query("request_id") String request_id,
            @Query("category") String category,
            @Query("action_id")String action_id
    );

    // //store id info
    @FormUrlEncoded
    @POST("store_user_id_info")
    Call<AccountResponse> storeIdInfo(
            @Header("Authorization")String token,
            @Field("user_id") String user_id,
            @Field("id_type") String id_type,
            @Field("id_number") String id_number,
            @Field("expiry_date") String expiry_date,
            @Field("front") String front,
            @Field("back") String back,
            @Field("request_id") String request_id,
            @Field("category") String category,
            @Field("action_id")String action_id
    );

    // //store employment info
    @FormUrlEncoded
    @POST("store_user_employment_info")
    Call<AccountResponse> storeEmploymentInfo(
            @Header("Authorization")String token,
            @Field("user_id") String user_id,
            @Field("employer") String employer,
            @Field("designation") String designation,
            @Field("location") String location,
            @Field("employment_contact") String contact,
            @Field("employee_id") String employee_id,
            @Field("request_id") String request_id,
            @Field("category") String category,
            @Field("action_id")String action_id
    );

    // //store business info
    @FormUrlEncoded
    @POST("store_user_business_info")
    Call<AccountResponse> storeBusinessInfo(
            @Header("Authorization")String token,
            @Field("user_id") String user_id,
            @Field("business_name") String business_name,
            @Field("business_location") String location,
            @Field("registration_no") String reg_no,
            @Field("trade_license") String trade_license,
            @Field("license_no") String license_number,
            @Field("registration_cert") String reg_certificate,
            @Field("request_id") String request_id,
            @Field("category") String category,
            @Field("action_id")String action_id
    );


    // / apply for business account
    @FormUrlEncoded
    @POST("apply_for_business")
    Call<AccountResponse> applyForBusiness(
            @Header("Authorization") String token,
            @Field("role") String role,
            @Field("user_id") String user_id,
            @Field("business_name") String business_name,
            @Field("registration_no") String reg_no,
            @Field("registration_cert") String reg_certificate,
            @Field("trade_license") String trade_license,
            @Field("proprietor_name") String proprietor_name,
            @Field("proprietor_nin") String proprietor_nin,
            @Field("national_id_front") String national_id_front,
            @Field("national_id_back") String national_id_back,
            @Field("latitude") double latitude,
            @Field("longitude") double longitude,
            @Field("request_id") String request_id,
            @Field("category") String category,
            @Field("action_id")String action_id
    );

    // /
    @FormUrlEncoded
    @POST("wallet/add_device_info")
    Call<UserData> registerDeviceToFCM(
            @Header("Authorization") String token,
            @Field("device_id") String device_id,
            @Field("device_type") String device_type,
            @Field("user_id") String user_id,
            @Field("device_ram") String device_ram,
            @Field("processor") String processor,
            @Field("device_os") String device_os,
            @Field("location") String location,
            @Field("device_model") String device_model,
            @Field("manufacturer") String manufacturer,
            @Field("operating_system") String operating_system,
            @Field("request_id") String request_id,
            @Field("category") String category,
            @Field("action_id")String action_id

    );

    // //store card info
    @FormUrlEncoded
    @POST("wallet/add_card_info")
    Call<CardResponse>saveCardInfo(
            @Header("Authorization")String token,
            @Field("identifier") String user_id,
            @Field("card_number") String card_number,
            @Field("cvv") String cvv,
            @Field("expiry") String expiry,
            @Field("account_name") String account_name,
            @Field("request_id") String request_id,
            @Field("category") String category,
            @Field("action_id")String action_id

    );

    // //update card info
    @FormUrlEncoded
    @POST("wallet/update_card_info")
    Call<CardResponse>updateCardInfo(
            @Header("Authorization")String token,
            @Field("id") String id,
            @Field("identifier") String user_id,
            @Field("card_number") String card_number,
            @Field("cvv") String cvv,
            @Field("expiry") String expiry,
            @Field("account_name") String account_name,
            @Field("request_id") String request_id,
            @Field("category") String category,
            @Field("action_id")String action_id


    );

    // //delete card
    @FormUrlEncoded
    @POST("wallet_delete_card")
    Call<CardResponse>deleteCard(
            @Field("id") String id,
            @Header("Authorization") String token,
            @Field("request_id") String request_id,
            @Field("category") String category,
            @Field("action_id")String action_id
    );





    //get card info
    @GET("wallet/cards/list")
    Call<CardResponse>getCards(@Header("Authorization") String token, @Query("request_id") String request_id, @Query("category") String category,
                               @Query("action_id")String action_id);





    /***********************SHOP REQUESTS**********************************/
    @FormUrlEncoded
    @POST("postMerchantShops")
    Call<ResponseBody> postShop(
            @Field("shop_name") String shop_name,
            @Field("shop_contact") String shop_contact,
            @Field("shop_email") String shop_email,
            @Field("shop_address") String shop_address,
            @Field("shop_currency") String shop_currency,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("request_id") String request_id
    );

    @FormUrlEncoded
    @POST("merchant/wallet/account/create")
    Call<ResponseBody> createAccount(
            @Header("Authorization") String token,
            @Field("firstName") String first_name,
            @Field("lastName") String last_name,
            @Field("middleName") String middle_name,
            @Field("gender") String gender,
            @Field("dob") String date_of_birth,
            @Field("district") String district,
            @Field("subCounty") String sub_county,
            @Field("village") String village,
            @Field("addressStreet") String landmark,
            @Field("phoneNumber") String phone_number,
            @Field("email") String email,
            @Field("nextOfKinFirstName") String next_of_kin_name,
            @Field("nextOfKinLastName") String next_of_kin_second_name,
            @Field("nextOfKinRelationship") String next_of_kin_relationship,
            @Field("nextOfKinContact") String next_of_kin_contact,
            @Field("nin") String nin,
            @Field("nin_expiry") String national_id_valid_upto,
            @Field("nationalId") String national_id_photo,
            @Field("customerPhoto") String customer_photo,
            @Field("photoWithId") String photo_with_national_id,
            @Field("accountNumber") String account_number,
            @Field("cardNumber") String card_number,
            @Field("cardExpiryDate") String expiry_date,
            @Field("Cvv") String cvv,
            @Field("pin") String pin,
            @Field("request_id") String request_id
    );


    @FormUrlEncoded
    @POST("postPaymentMethod")
    Call<ResponseBody> postPaymentMethod(
            @Field("shop_id") Integer shop_id,
            @Field("payment_method_id") String payment_method_id,
            @Field("payment_method_name") String payment_method_name
    );

    @FormUrlEncoded
    @POST("postCart")
    Call<ResponseBody> postCart(
            @Field("shop_id") Integer shop_id,
            @Field("cart_id") String cart_id,
            @Field("product_id") String product_id,
            @Field("product_weight") String product_weight,
            @Field("product_weight_unit") String product_weight_unit,
            @Field("product_price") String product_price,
            @Field("product_qty") String product_qty
    );

    @FormUrlEncoded
    @POST("postProductWeight")
    Call<ResponseBody> postWeight(
            @Field("shop_id") Integer shop_id,
            @Field("weight_id") String weight_id,
            @Field("weight_unit") String weight_unit
    );


    @FormUrlEncoded
    @POST("postOrderList")
    Call<ResponseBody> postOrderList(
            @Field("shop_id") Integer shop_id,
            @Field("order_id") String order_id,
            @Field("invoice_id") String invoice_id,
            @Field("order_date") String order_date,
            @Field("order_time") String order_time,
            @Field("order_type") String order_type,
            @Field("order_payment_method") String order_payment_method,
            @Field("customer_name") String customer_name
    );

    @FormUrlEncoded
    @POST("postOrderType")
    Call<ResponseBody> postOrderType(
            @Field("shop_id") Integer shop_id,
            @Field("order_type_id") String order_type_id,
            @Field("order_type_name") String order_type_name
    );


//
//    @FormUrlEncoded
//    @POST("registermerchantdevices")
//    Call<UserData> registerDeviceToFCM(@Field("device_id") String device_id,
//                                       @Field("device_type") String device_type,
//                                       @Field("ram") String ram,
//                                       @Field("processor") String processor,
//                                       @Field("device_os") String device_os,
//                                       @Field("location") String location,
//                                       @Field("device_model") String device_model,
//                                       @Field("manufacturer") String manufacturer,
//                                       @Field("shop_id") String shop_id);


    @FormUrlEncoded
    @POST("merchant/user/deposit")
    Call<ResponseBody> depositAmountNumber(
            @Field("phone_number") String number,
            @Field("amount") String amount


    );

    @FormUrlEncoded
    @POST("merchant/user/deposit")
    Call<ResponseBody> depositAmountAccount(
            @Field("account") String account,
            @Field("amount_number") String amount

    );

    // //get settlements
    @GET("wallet/settlements/list")
    Call<WalletTransactionResponse> getSettlements(
            @Header("Authorization") String token,
            @Query("request_id") String request_id,
            @Query("category") String category,
            @Query("action_id")String action_id
    );



    // //initiate deposit
    @FormUrlEncoded
    @POST("wallet/agent/confirm-deposit")
    Call<InitiateWithdrawResponse>confrmDeposit(
            @Header("Authorization") String token,
            @Field("amount") double amount,
            @Field("pin") String pin,
            @Field("customerPhoneNumber") String customerPhoneNumber,
            @Field("request_id") String request_id,
            @Field("category") String category,
            @Field("action_id")String action_id
    );


 @FormUrlEncoded
 @POST("wallet/agent/confirm-customer-transfer")
 Call<InitiateWithdrawResponse>confirmAgentTransfer(
         @Header("Authorization") String token,
         @Field("amount") double amount,
         @Field("otp") String otpCode,
         @Field("customerPhoneNumber") String customerPhoneNumber,
         @Field("receiverPhoneNumber") String receiverPhoneNumber,
         @Field("request_id") String request_id,
         @Field("category") String category,
         @Field("action_id")String action_id

 );

    // //initiate withdraw
    @FormUrlEncoded
    @POST("wallet/agent/confirm-withdraw")
    Call<InitiateWithdrawResponse> confirmAgentWithdraw(
            @Header("Authorization") String token,
            @Field("amount") double amount,
            @Field("otp") String otp_code,
            @Field("customerPhoneNumber") String customerPhoneNumber,
            @Field("request_id") String request_id,
            @Field("category") String category,
            @Field("action_id")String action_id

    );

  //initiate agent transaction
  @FormUrlEncoded
  @POST("wallet/agent/initiate-customer-transfer")
  Call<InitiateTransferResponse> initiateAgentTransaction(@Header("Authorization") String token,
                                                          @Field("amount") Double amount,
                                                          @Field("customerPhoneNumber") String customerPhoneNumber,
                                                          @Field("type") String type,
                                                          @Field("request_id") String request_id,
                                                          @Field("category") String category,
                                                          @Field("action_id")String action_id,
                                                          @Field("service_code")String service_code
  );

  // Comfirm Accept Payment
  @FormUrlEncoded
  @POST("wallet/agent/confirm-payment")
  Call<InitiateWithdrawResponse>confirmAcceptPayment(
          @Header("Authorization") String token,
          @Field("amount") double amount,
          @Field("customerPhoneNumber") String customerPhoneNumber,
          @Field("otp") String OTPCode,
          @Field("request_id") String request_id,
          @Field("category") String category,
          @Field("action_id")String action_id

  );

    // //balance inquiry
    @FormUrlEncoded
    @POST("wallet/agent/balance-inquiry")
    Call<InitiateWithdrawResponse>balanceInquiry(
            @Header("Authorization") String token,
            @Field("pin") String pin,
            @Field("customerPhoneNumber") String customerPhoneNumber,
            @Field("request_id") String request_id,
            @Field("category") String category,
            @Field("action_id")String action_id
    );

    
    @POST("wallet/agent/account-opening")
    Call<InitiateWithdrawResponse>openAccount(@Header("Authorization") String token, @Body JSONObject object, @Field("request_id") String request_id, @Field("category") String category,
                                              @Field("action_id")String action_id
    );


    // //update card info
    @FormUrlEncoded
    @POST("wallet/add_beneficiary")
    Call<CardResponse>saveBeneficiary(
            @Header("Authorization")String token,
            @Field("identifier") String user_id,
            @Field("transaction_type") String beneficary_type,
            @Field("bank") String bank,
            @Field("bank_branch") String bank_branch,
            @Field("account_name") String account_name,
            @Field("account_number") String account_number,
            @Field("request_id") String request_id,
            @Field("category") String category,
            @Field("action_id")String action_id




    );

    // //delete card
    @FormUrlEncoded
    @POST("wallet/delete_beneficiary")
    Call<CardResponse>deleteBeneficiary(
            @Header("Authorization") String token,
            @Field("id") String id,
            @Field("request_id") String request_id
    );

    @FormUrlEncoded
    @POST("wallet/update_beneficiary")
    Call<CardResponse>updateBeneficiary(
            @Header("Authorization")String token,
            @Field("id") String beneficiary_id,
            @Field("transaction_type") String transaction_type,
            @Field("bank") String bank,
            @Field("bank_branch") String bank_branch,
            @Field("account_name") String account_name,
            @Field("account_number") String account_number,
            @Field("request_id")String request_id


    );

    //get beneficiaries info
    @GET("wallet/beneficiaries/list")
    Call<BeneficiaryResponse>getBeneficiaries(@Header("Authorization") String token, @Query("transaction_type") String transaction_type,
                                              @Query("request_id") String request_id);





    //get beneficiaries info
    @GET("wallet/security-qns")
    Call<SecurityQnsResponse>getSecurityQns( @Query("request_id") String request_id
            ,  @Query("category") String category,@Query("action_id") String action_id
                                           );


    //change passowrd
    @FormUrlEncoded
    @POST("wallet/customer/change-pin")
    Call<ChangePinResponse>changePassword(@Header("Authorization") String token,
                                          @Field("phoneNumber") String phoneNumber,
                                          @Field("currentPin") String currentPin,
                                          @Field("newPin") String newPin,
                                          @Field("comfirmNewPin") String comfirmNewPin,
                                          @Field("request_id") String request_id,
                                          @Field("action_id") String action_id);





}
