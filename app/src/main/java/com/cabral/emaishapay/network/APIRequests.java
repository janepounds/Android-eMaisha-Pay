package com.cabral.emaishapay.network;

import com.cabral.emaishapay.models.AccountResponse;
import com.cabral.emaishapay.models.CancelLoanResponse;
import com.cabral.emaishapay.models.CardResponse;
import com.cabral.emaishapay.models.InitiateTransferResponse;
import com.cabral.emaishapay.models.InitiateWithdrawResponse;
import com.cabral.emaishapay.models.WalletAuthenticationResponse;
import com.cabral.emaishapay.models.address_model.AddressData;
import com.cabral.emaishapay.models.address_model.Countries;
import com.cabral.emaishapay.models.address_model.Regions;
import com.cabral.emaishapay.models.address_model.Zones;
import com.cabral.emaishapay.models.coupons_model.CouponsData;
import com.cabral.emaishapay.models.BalanceResponse;
import com.cabral.emaishapay.models.LoanListResponse;
import com.cabral.emaishapay.models.LoanPayResponse;
import com.cabral.emaishapay.models.MerchantInfoResponse;
import com.cabral.emaishapay.models.RequestLoanresponse;
import com.cabral.emaishapay.models.TokenResponse;
import com.cabral.emaishapay.models.WalletAuthentication;
import com.cabral.emaishapay.models.WalletPurchaseConfirmResponse;
import com.cabral.emaishapay.models.WalletPurchaseResponse;
import com.cabral.emaishapay.models.WalletTransactionReceiptResponse;
import com.cabral.emaishapay.models.WalletTransactionResponse;
import com.cabral.emaishapay.models.WalletUserRegistration;
import com.cabral.emaishapay.models.external_transfer_model.SettlementResponse;
import com.cabral.emaishapay.models.pages_model.PagesData;
import com.cabral.emaishapay.models.shop_model.CategoriesResponse;
import com.cabral.emaishapay.models.shop_model.ManufacturersResponse;
import com.cabral.emaishapay.models.shop_model.ProductResponse;
import com.cabral.emaishapay.models.user_model.UserData;
import com.cabral.emaishapay.models.WalletTransaction;

import org.json.JSONObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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
                                        @Field("district") String district,
                                        @Field("subCounty") String subCounty,
                                        @Field("village") String village
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

    //initiate transfer
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
                                                      @Field("third_party_id") String third_party_id
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

    //make transaction
    @FormUrlEncoded
    @Headers({"Accept: application/json"})
    @POST("wallet/external_payment/merchant")
    Call<WalletPurchaseResponse> creditMerchantSale(@Header("Authorization") String token,
                                                 @Field("merchantId") int merchantId,
                                                 @Field("amount") Double amount,
                                                 @Field("thirdParty") String thirdParty,
                                                 @Field("reference") String reference
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
                                       @Field("user_id") String user_id,
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



    @FormUrlEncoded
    @POST("apply_for_business")
    Call<AccountResponse> applyForBusiness(
            @Field("user_id") String user_id,
            @Field("business_name") String business_name,
            @Field("registration_no") String reg_no,
            @Field("registration_cert") String reg_certificate,
            @Field("trade_license") String trade_license,
            @Field("proprietor_name") String proprietor_name,
            @Field("proprietor_nin") String proprietor_nin,
            @Field("national_id_front") String national_id_front,
            @Field("national_id_back") String national_id_back,
            @Field("role") String role
    );


    @FormUrlEncoded
    @POST("wallet/add_device_info")
    Call<UserData> registerDeviceToFCM(
            @Field("device_id") String device_id,
            @Field("device_type") String device_type,
            @Field("user_id") String user_id,
            @Field("device_ram") String device_ram,
            @Field("processor") String processor,
            @Field("device_os") String device_os,
            @Field("location") String location,
            @Field("device_model") String device_model,
            @Field("manufacturer") String manufacturer,
            @Field("operating_system") String operating_system

    );

    //store card info
    @FormUrlEncoded
    @POST("wallet/add_card_info")
    Call<CardResponse>saveCardInfo(
            @Field("identifier") String user_id,
            @Field("card_number") String card_number,
            @Field("cvv") String cvv,
            @Field("expiry") String expiry,
            @Field("account_name") String account_name



    );

    //update card info
    @FormUrlEncoded
    @POST("wallet/update_card_info")
    Call<CardResponse>updateCardInfo(
            @Header("Authorization")String token,
            @Field("id") String id,
            @Field("identifier") String user_id,
            @Field("card_number") String card_number,
            @Field("cvv") String cvv,
            @Field("expiry") String expiry,
            @Field("account_name") String account_name


    );
    @FormUrlEncoded
    @POST("wallet_delete_card")
    Call<CardResponse>deleteCard(
            @Field("id") String id,
            @Header("Authorization") String token
    );





    //get card info
    @GET("wallet/cards/list")
    Call<CardResponse>getCards(@Header("Authorization") String token);



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
            @Field("longitude") String longitude
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
            @Field("pin") String pin
    );


    @FormUrlEncoded
    @POST("registerMerchant")
    Call<ResponseBody> registerShop(
            @Field("shop_name") String shop_name,
            @Field("shop_contact") String shop_contact,
            @Field("shop_email") String shop_email,
            @Field("shop_address") String shop_address,
            @Field("shop_currency") String shop_currency,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("loginMerchant")
    Call<ResponseBody> loginShop(
            @Field("shop_contact") String shop_contact,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("postCustomer")
    Call<ResponseBody> postCustomer(
            @Field("shop_id") Integer shop_id,
            @Field("customer_id") String customer_id,
            @Field("customer_name") String customer_name,
            @Field("customer_cell") String customer_cell,
            @Field("customer_email") String customer_email,
            @Field("customer_address") String customer_address,
            @Field("customer_address_two") String customer_address_two,
            @Field("customer_image") String customer_image
    );

    @FormUrlEncoded
    @POST("postSuppliers")
    Call<ResponseBody> postSupplier(
            @Field("shop_id") Integer shop_id,
            @Field("suppliers_id") String suppliers_id,
            @Field("suppliers_name") String suppliers_name,
            @Field("suppliers_contact_person") String suppliers_contact_person,
            @Field("suppliers_cell") String suppliers_cell,
            @Field("suppliers_email") String suppliers_email,
            @Field("suppliers_address") String suppliers_address,
            @Field("suppliers_address_two") String suppliers_address_two,
            @Field("suppliers_image") String suppliers_image
    );

    @FormUrlEncoded
    @POST("postExpenses")
    Call<ResponseBody> postExpense(
            @Field("shop_id") Integer shop_id,
            @Field("expense_id") String expense_id,
            @Field("expense_name") String expense_name,
            @Field("expense_note") String expense_note,
            @Field("expense_amount") String expense_amount,
            @Field("expense_date") String expense_date,
            @Field("expense_time") String expense_time
    );

    @FormUrlEncoded
    @POST("postProductCategories")
    Call<ResponseBody> postCategory(
            @Field("shop_id") Integer shop_id,
            @Field("category_id") String category_id,
            @Field("category_name") String category_name
    );

    @FormUrlEncoded
    @POST("stockMerchantProduct")
    Call<ResponseBody> postProduct(
            @Field("id") String id,
            @Field("measure_id") int measure_id,
            @Field("shop_id") Integer shop_id,
            @Field("product_id") Integer product_id,
            @Field("product_buy_price") String product_buy_price,
            @Field("product_sell_price") String product_sell_price,
            @Field("product_supplier") String product_supplier,
            @Field("product_stock") int product_stock
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

    @GET("getCategories")
    Call<CategoriesResponse> getCategories();

    @GET("getProductsByCategoryAndManufacturer/{category_id}/{manufacturer_id}")
    Call<ProductResponse> getProducts(
            @Path("category_id") int category_id,
            @Path("manufacturer_id") int manufacturer_id
    );

    @GET("getManufacturers")
    Call<ManufacturersResponse> getManufacturers();

    @GET("getBackup/{shop_id}")
    Call<ResponseBody> getBackup(
            @Path("shop_id") int id
    );

    @GET("getEMaishaAppOrders/{shop_id}")
    Call<ResponseBody> getOrders(
            @Path("shop_id") int id
    );

    @FormUrlEncoded
    @POST("updatestatus_merchant")
    Call<ResponseBody> updateOrderStatus(
            @Field("orders_id") String order_id,
            @Field("comment") String comment,
            @Field("statuscode") Integer statuscode


    );

    @FormUrlEncoded
    @POST("registermerchantdevices")
    Call<UserData> registerDeviceToFCM(@Field("device_id") String device_id,
                                       @Field("device_type") String device_type,
                                       @Field("ram") String ram,
                                       @Field("processor") String processor,
                                       @Field("device_os") String device_os,
                                       @Field("location") String location,
                                       @Field("device_model") String device_model,
                                       @Field("manufacturer") String manufacturer,
                                       @Field("shop_id") String shop_id);


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

    @GET("wallet/settlements/list")
    Call<WalletTransactionResponse> getSettlements(
            @Header("Authorization") String token
    );
    @FormUrlEncoded
    @POST("wallet/merchant/initiate_withdraw")
    Call<InitiateWithdrawResponse> initiateWithdraw(
            @Header("Authorization") String token,
            @Field("amount") int amount,
            @Field("pin") String pin,
            @Field("customerPhoneNumber") String customerPhoneNumber

    );



}
