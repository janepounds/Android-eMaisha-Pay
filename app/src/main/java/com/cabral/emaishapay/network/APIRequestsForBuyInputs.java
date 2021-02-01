package com.cabral.emaishapay.network;


import com.cabral.emaishapay.models.address_model.AddressData;
import com.cabral.emaishapay.models.address_model.Countries;
import com.cabral.emaishapay.models.address_model.Regions;
import com.cabral.emaishapay.models.address_model.Zones;
import com.cabral.emaishapay.models.banner_model.BannerData;
import com.cabral.emaishapay.models.category_model.CategoryData;
import com.cabral.emaishapay.models.contact_model.ContactUsData;
import com.cabral.emaishapay.models.coupons_model.CouponsData;
import com.cabral.emaishapay.models.currency_model.CurrencyModel;
import com.cabral.emaishapay.models.device_model.AppSettingsData;
import com.cabral.emaishapay.models.filter_model.get_filters.FilterData;
import com.cabral.emaishapay.models.googleMap.GoogleAPIResponse;
import com.cabral.emaishapay.models.language_model.LanguageData;
import com.cabral.emaishapay.models.merchants_model.MerchantData;
import com.cabral.emaishapay.models.news_model.all_news.NewsData;
import com.cabral.emaishapay.models.news_model.news_categories.NewsCategoryData;
import com.cabral.emaishapay.models.order_model.OrderData;
import com.cabral.emaishapay.models.order_model.PostOrder;
import com.cabral.emaishapay.models.pages_model.PagesData;
import com.cabral.emaishapay.models.payment_model.GetBrainTreeToken;
import com.cabral.emaishapay.models.payment_model.PaymentMethodsData;
import com.cabral.emaishapay.models.product_model.GetAllProducts;
import com.cabral.emaishapay.models.product_model.GetStock;
import com.cabral.emaishapay.models.product_model.ProductData;
import com.cabral.emaishapay.models.product_model.ProductStock;
import com.cabral.emaishapay.models.ratings.GetRatings;
import com.cabral.emaishapay.models.ratings.GiveRating;
import com.cabral.emaishapay.models.search_model.SearchData;
import com.cabral.emaishapay.models.shipping_model.PostTaxAndShippingData;
import com.cabral.emaishapay.models.shipping_model.ShippingRateData;
import com.cabral.emaishapay.models.shop_model.CategoriesResponse;
import com.cabral.emaishapay.models.shop_model.ManufacturersResponse;
import com.cabral.emaishapay.models.shop_model.ProductResponse;
import com.cabral.emaishapay.models.uploadimage.UploadImageModel;
import com.cabral.emaishapay.models.user_model.UserData;


import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * APIRequests contains all the Network Request Methods with relevant API Endpoints
 **/

public interface APIRequestsForBuyInputs {

    //******************** User Data ********************//
    @FormUrlEncoded
    @POST("allnewscategories")
    Call<NewsCategoryData> allNewsCategories(@Field("language_id") int language_id,
                                             @Field("page_number") int page_number);

    @FormUrlEncoded
    @POST("getallnews")
    Call<NewsData> getAllNews(@Field("language_id") int language_id,
                              @Field("page_number") int page_number,
                              @Field("is_feature") int is_feature,
                              @Field("categories_id") String categories_id);

    @Multipart
    @POST("processregistration")
    Call<UserData> processRegistration(
            @Part("customers_firstname") RequestBody firstName,
            @Part("customers_lastname") RequestBody lastName,
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part("country_code") RequestBody countryCode,
            @Part("customers_telephone") RequestBody phoneNumber,
            @Part("addressStreet") RequestBody addressStreet,
            @Part("addressCityOrTown") RequestBody addressCityOrTown,
            @Part("address_district") RequestBody addressDistrict);

    @FormUrlEncoded
    @POST("processlogin")
    Call<UserData> processLogin(@Field("email") String customers_email_address,
                                @Field("password") String customers_password);

    @FormUrlEncoded
    @POST("facebookregistration")
    Call<UserData> facebookRegistration(@Field("access_token") String access_token);

    @FormUrlEncoded
    @POST("googleregistration")
    Call<UserData> googleRegistration(@Field("idToken") String idToken,
                                      @Field("customers_id") String userId,
                                      @Field("givenName") String givenName,
                                      @Field("familyName") String familyName,
                                      @Field("email") String email,
                                      @Field("imageUrl") String imageUrl);

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

    @FormUrlEncoded
    @POST("addshippingaddress")
    Call<AddressData> addUserAddress(@Field("customers_id") String customers_id,
                                     @Field("entry_firstname") String entry_firstname,
                                     @Field("entry_lastname") String entry_lastname,
                                     @Field("entry_street_address") String entry_street_address,
                                     @Field("entry_postcode") String entry_postcode,
                                     @Field("entry_city") String entry_city,
                                     @Field("entry_country_id") String entry_country_id,
                                     @Field("entry_latitude") String latitude,
                                     @Field("entry_longitude") String longitude,
                                     @Field("entry_contact") String contact,
                                     @Field("is_default") String customers_default_address_id);

    @FormUrlEncoded
    @POST("updateshippingaddress")
    Call<AddressData> updateUserAddress(@Field("customers_id") String customers_id,
                                        @Field("address_id") String address_id,
                                        @Field("entry_firstname") String entry_firstname,
                                        @Field("entry_lastname") String entry_lastname,
                                        @Field("entry_street_address") String entry_street_address,
                                        @Field("entry_postcode") String entry_postcode,
                                        @Field("entry_city") String entry_city,
                                        @Field("entry_country_id") String entry_country_id,
                                        @Field("entry_latitude") String latitude,
                                        @Field("entry_longitude") String longitude,
                                        @Field("entry_contact") String contact,
                                        @Field("is_default") String customers_default_address_id);

    @FormUrlEncoded
    @POST("updatedefaultaddress")
    Call<AddressData> updateDefaultAddress(@Field("customers_id") String customers_id,
                                           @Field("address_book_id") String address_book_id);

    @FormUrlEncoded
    @POST("deleteshippingaddress")
    Call<AddressData> deleteUserAddress(@Field("customers_id") String customers_id,
                                        @Field("address_book_id") String address_book_id);
//Merchant shop methods from

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
    //******************** OrderProductCategory Data ********************//
    @FormUrlEncoded
    @POST("allcategories")
    Call<CategoryData> getAllCategories(@Field("language_id") int language_id);

    //******************** Product Data ********************//

    @POST("getallproducts")
    Call<ProductData> getAllProducts(@Body GetAllProducts getAllProducts);

    @POST("getquantity")
    Call<ProductStock> getProductStock(@Body GetStock getStock);

    @FormUrlEncoded
    @POST("likeproduct")
    Call<ProductData> likeProduct(@Field("liked_products_id") int liked_products_id,
                                  @Field("liked_customers_id") String liked_customers_id);

    @FormUrlEncoded
    @POST("unlikeproduct")
    Call<ProductData> unlikeProduct(@Field("liked_products_id") int liked_products_id,
                                    @Field("liked_customers_id") String liked_customers_id);

    @FormUrlEncoded
    @POST("getfilters")
    Call<FilterData> getFilters(@Field("categories_id") int categories_id,
                                @Field("language_id") int language_id);

    @FormUrlEncoded
    @POST("getsearchdata")
    Call<SearchData> getSearchData(@Field("searchValue") String searchValue,
                                   @Field("language_id") int language_id,
                                   @Field("currency_code") String currency_code);

    //******************** Order Data ********************//

    @POST("addtoorder")
    Call<OrderData> addToOrder(@Body PostOrder postOrder);

    @FormUrlEncoded
    @POST("getorders")
    Call<OrderData> getOrders(@Field("customers_id") String customers_id,
                              @Field("language_id") int language_id,
                              @Field("currency_code") String currency_code);

    @FormUrlEncoded
    @POST("updatestatus")
    Call<OrderData> updatestatus(@Field("customers_id") String customers_id,
                                 @Field("orders_id") int orders_id);

    @FormUrlEncoded
    @POST("getcoupon")
    Call<CouponsData> getCouponInfo(@Field("code") String code);

    @FormUrlEncoded
    @POST("getpaymentmethods")
    Call<PaymentMethodsData> getPaymentMethods(@Field("language_id") int language_id);

    @GET("getbanners")
    Call<BannerData> getBanners();

    //******************** Tax & Shipping Data ********************//

    @POST("getrate")
    Call<ShippingRateData> getShippingMethodsAndTax(@Body PostTaxAndShippingData postTaxAndShippingData);

    //******************** Contact Us Data ********************//

    @FormUrlEncoded
    @POST("contactus")
    Call<ContactUsData> contactUs(@Field("name") String name,
                                  @Field("email") String email,
                                  @Field("message") String message);

    //******************** Languages Data ********************//

    @GET("getlanguages")
    Call<LanguageData> getLanguages();

    //******************** App Settings Data ********************//

    @GET("sitesetting")
    Call<AppSettingsData> getAppSetting();


    //******************** Static Pages Data ********************//

    @FormUrlEncoded
    @POST("getallpages")
    Call<PagesData> getStaticPages(@Field("language_id") int language_id);

    //******************** Notifications Data ********************//

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

    @FormUrlEncoded
    @POST("notify_me")
    Call<ContactUsData> notify_me(@Field("is_notify") String is_notify,
                                  @Field("device_id") String device_id);

    @GET("generatebraintreetoken")
    Call<GetBrainTreeToken> generateBraintreeToken();

    @FormUrlEncoded
    @POST("givereview")
    Call<GiveRating> giveRating(@FieldMap Map<String, String> stringMap);

    @GET("getreviews")
    Call<GetRatings> getProductReviews(@Query("products_id") String product_id,
                                       @Query("languages_id") String languages_id);

    // This Api will give us City bounds
    @GET("getlocation")
    Call<GoogleAPIResponse> getCityBounds(@Query(value = "address", encoded = true) String address);

    // Upload Image
    @Multipart
    @POST("uploadimage")
    Call<UploadImageModel> uploadImage(@Part MultipartBody.Part filePart);

    // Update Password
    @GET("updatepassword")
    Call<UserData> updatePassword(@Query("oldpassword") String oldPassword,
                                  @Query("newpassword") String newPassword,
                                  @Query("customers_id") String customers_id);

    //Change Currency
    @GET("getcurrencies")
    Call<CurrencyModel> getCurrency();

    @GET("get_feasible_selling_shops")
    Call<MerchantData> getNearbyMerchants(@Query("latitude") String latitude, @Query("longitude") String longitude, @Query("productlist") String productlist);

    @GET("get_sellPrices_by_shopId")
    Call<MerchantData> getMerchantsProductData(@Query("shopID") String shopID, @Query("productlist") String productlist);


}
