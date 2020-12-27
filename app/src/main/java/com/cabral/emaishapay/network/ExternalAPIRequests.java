package com.cabral.emaishapay.network;


import com.cabral.emaishapay.models.address_model.AddressData;
import com.cabral.emaishapay.models.address_model.Countries;
import com.cabral.emaishapay.models.address_model.Regions;
import com.cabral.emaishapay.models.address_model.Zones;
import com.cabral.emaishapay.models.external_transfer_model.BankBranchInfoResponse;
import com.cabral.emaishapay.models.external_transfer_model.BankTransferResponse;
import com.cabral.emaishapay.models.external_transfer_model.BanksInfoResponse;
import com.cabral.emaishapay.models.pages_model.PagesData;
import com.cabral.emaishapay.models.user_model.UserData;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * APIRequests contains all the Network Request Methods with relevant API Endpoints
 **/

public interface ExternalAPIRequests {

    @FormUrlEncoded
    @POST("registerdevices")
    Call<UserData> registerDeviceToFCM(@Field("device_id") String device_id,
                                       @Field("device_type") String device_type,
                                       @Field("ram") String ram,
                                       @Field("processor") String processor,
                                       @Field("device_os") String device_os,
                                       @Field("location") String location,
                                       @Field("device_model") String device_model,
                                       @Field("manufacturer") String manufacturer,
                                       @Field("customers_id") String customers_id);

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

    //get all Banks
    @GET("banks/{country}")
    Call<BanksInfoResponse> getTransferBanks(@Path("country") String country, @Query("public_key") String public_key );

    //get all Banks
    @GET("banks/branches/{id}")
    Call<BankBranchInfoResponse> getTransferBankBranches(@Path("id") String Bank_id, @Query("public_key") String public_key );

    @FormUrlEncoded
    @POST("transfers")
    Call<BankTransferResponse> bankTransferOuts(@Header("Authorization") String secKey,
                                                @Field("reference") String reference,
                                                @Field("beneficiary_name") String beneficiary_name,
                                                @Field("currency") String currency,
                                                @Field("narration") String narration,
                                                @Field("amount") String amount,
                                                @Field("account_number") String account_number,
                                                @Field("destination_branch_code") String destination_branch_code,
                                                @Field("account_bank") String account_bank);

    //get tranfer Data
    @GET("transfers/{id}")
    Call<BankTransferResponse> getTransferData(@Path("id") String transfer_id, @Header("Authorization") String secKey );

}
