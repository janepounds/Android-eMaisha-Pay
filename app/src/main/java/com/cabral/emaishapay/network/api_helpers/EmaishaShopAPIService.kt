package com.cabral.emaishapay.network.api_helpers

import android.util.Log
import com.cabral.emaishapay.constants.ConstantValues
import com.cabral.emaishapay.models.address_model.AddressData
import com.cabral.emaishapay.models.category_model.CategoryData
import com.cabral.emaishapay.models.contact_model.ContactUsData
import com.cabral.emaishapay.models.coupons_model.CouponsData
import com.cabral.emaishapay.models.currency_model.CurrencyModel
import com.cabral.emaishapay.models.filter_model.get_filters.FilterData
import com.cabral.emaishapay.models.googleMap.GoogleAPIResponse
import com.cabral.emaishapay.models.language_model.LanguageData
import com.cabral.emaishapay.models.merchants_model.MerchantData
import com.cabral.emaishapay.models.order_model.OrderData
import com.cabral.emaishapay.models.order_model.PostOrder
import com.cabral.emaishapay.models.payment_model.GetBrainTreeToken
import com.cabral.emaishapay.models.product_model.GetAllProducts
import com.cabral.emaishapay.models.product_model.GetStock
import com.cabral.emaishapay.models.product_model.ProductData
import com.cabral.emaishapay.models.product_model.ProductStock
import com.cabral.emaishapay.models.ratings.GetRatings
import com.cabral.emaishapay.models.ratings.GiveRating
import com.cabral.emaishapay.models.search_model.SearchData
import com.cabral.emaishapay.models.shop_model.CategoriesResponse
import com.cabral.emaishapay.models.shop_model.ManufacturersResponse
import com.cabral.emaishapay.models.shop_model.ProductResponse
import com.cabral.emaishapay.models.uploadimage.UploadImageModel
import com.cabral.emaishapay.models.user_model.UserData
import com.cabral.emaishapay.network.db.entities.EcProduct
import com.cabral.emaishapay.network.db.entities.ShopOrder
import com.cabral.emaishapay.utils.Utilities
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.IOException
import java.util.concurrent.TimeUnit

interface EmaishaShopAPIService {

    @FormUrlEncoded
    @POST("getalladdress")
    fun getAllAddress(
            @Header("Authorization") token: String?,
            @Field("customers_id") customers_id: String?): Call<AddressData?>?


    @FormUrlEncoded
    @POST("addshippingaddress")
    fun addUserAddress(
            @Header("Authorization") token: String?,
            @Field("customers_id") customers_id: String?,
            @Field("entry_firstname") entry_firstname: String?,
            @Field("entry_lastname") entry_lastname: String?,
            @Field("entry_street_address") entry_street_address: String?,
            @Field("entry_postcode") entry_postcode: String?,
            @Field("entry_city") entry_city: String?,
            @Field("entry_country_id") entry_country_id: String?,
            @Field("entry_latitude") latitude: String?,
            @Field("entry_longitude") longitude: String?,
            @Field("entry_contact") contact: String?,
            @Field("is_default") customers_default_address_id: String?): Call<AddressData?>?

    @FormUrlEncoded
    @POST("updateshippingaddress")
    fun updateUserAddress(
            @Header("Authorization") token: String?,
            @Field("customers_id") customers_id: String?,
            @Field("address_id") address_id: String?,
            @Field("entry_firstname") entry_firstname: String?,
            @Field("entry_lastname") entry_lastname: String?,
            @Field("entry_street_address") entry_street_address: String?,
            @Field("entry_postcode") entry_postcode: String?,
            @Field("entry_city") entry_city: String?,
            @Field("entry_country_id") entry_country_id: String?,
            @Field("entry_latitude") latitude: String?,
            @Field("entry_longitude") longitude: String?,
            @Field("entry_contact") contact: String?,
            @Field("is_default") customers_default_address_id: String?): Call<AddressData?>?

    @FormUrlEncoded
    @POST("updatedefaultaddress")
    fun updateDefaultAddress(
            @Header("Authorization") token: String?,
            @Field("customers_id") customers_id: String?,
            @Field("address_book_id") address_book_id: String?): Call<AddressData?>?

    @FormUrlEncoded
    @POST("deleteshippingaddress")
    fun deleteUserAddress(
            @Header("Authorization") token: String?,
            @Field("customers_id") customers_id: String?,
            @Field("address_book_id") address_book_id: String?
    ): Call<AddressData?>?

    @FormUrlEncoded
    @POST("stockMerchantProduct")
    fun postProduct(
            @Header("Authorization") token: String?,
            @Field("id") id: String?,
            @Field("wallet_id") wallet_id: String?,
            @Field("product_id") product_id: String?,
            @Field("product_buy_price") product_buy_price: String?,
            @Field("product_sell_price") product_sell_price: String?,
            @Field("product_supplier") product_supplier: String?,
            @Field("product_stock") product_stock: Int,
            @Field("new_manufacturer_name") new_manufacturer_name: String?,
            @Field("new_category_name") new_category_name: String?,
            @Field("new_product_name") new_product_name: String?
    ): Call<ResponseBody?>?

    @GET("getCategories")
    fun getCategories(@Header("Authorization") token: String?): Call<CategoriesResponse?>?

    @GET("getProductsByCategoryAndManufacturer/{category_id}/{manufacturer_id}")
    fun getProducts(
            @Header("Authorization") token: String?,
            @Path("category_id") category_id: Int,
            @Path("manufacturer_id") manufacturer_id: Int
    ): Call<ProductResponse?>?


    @GET("getManufacturers")
    fun getManufacturers(@Header("Authorization") token: String?): Call<ManufacturersResponse?>?

    @GET("getMerchantProducts/{wallet_id}")
    fun getMerchantProducts(
            @Path("wallet_id") id: String?
    ): Call<List<EcProduct?>?>?

    @GET("getMerchantOrders/{wallet_id}/{page}/{per_page}")
    suspend fun getpagedMerchantOrders(
            @Path("wallet_id") id: Int,
            @Path("page") page: Int,
            @Path("per_page") per_page: Int
    ): List<ShopOrder> = emptyList()

    @FormUrlEncoded
    @POST("updatestatus_merchant")
    fun updateOrderStatus(
            @Field("orders_id") order_id: String?,
            @Field("comment") comment: String?,
            @Field("statuscode") statuscode: Int?
    ): Call<ResponseBody?>?

    //******************** OrderProductCategory Data ********************//
    @FormUrlEncoded
    @POST("allcategories")
    fun getAllCategories(
            @Header("Authorization") token: String?,
            @Field("language_id") language_id: Int): Call<CategoryData?>?

    //******************** Product Data ********************//

    //******************** Product Data ********************//
    @POST("getallproducts")
    fun getAllProducts(
            @Header("Authorization") token: String?,
            @Body getAllProducts: GetAllProducts?): Call<ProductData?>?

    @POST("getquantity")
    fun getProductStock(
            @Header("Authorization") token: String?,
            @Body getStock: GetStock?): Call<ProductStock?>?

    @FormUrlEncoded
    @POST("likeproduct")
    fun likeProduct(
            @Header("Authorization") token: String?,
            @Field("liked_products_id") liked_products_id: Int,
            @Field("liked_customers_id") liked_customers_id: String?): Call<ProductData?>?

    @FormUrlEncoded
    @POST("unlikeproduct")
    fun unlikeProduct(
            @Header("Authorization") token: String?,
            @Field("liked_products_id") liked_products_id: Int,
            @Field("liked_customers_id") liked_customers_id: String?): Call<ProductData?>?

    @FormUrlEncoded
    @POST("getfilters")
    fun getFilters(
            @Header("Authorization") token: String?,
            @Field("categories_id") categories_id: Int,
            @Field("language_id") language_id: Int): Call<FilterData?>?

    @FormUrlEncoded
    @POST("getsearchdata")
    fun getSearchData(
            @Header("Authorization") token: String?,
            @Field("searchValue") searchValue: String?,
            @Field("language_id") language_id: Int,
            @Field("currency_code") currency_code: String?): Call<SearchData?>?

    //******************** Order Data ********************//
    @POST("addtoorder")
    fun addToOrder(
            @Header("ePAuthorization") token: String?,
            @Body postOrder: PostOrder?): Call<OrderData?>?

    @FormUrlEncoded
    @POST("getorders")
    suspend fun getPagedOrders(
            @Header("Authorization") token: String,
            @Field("customers_id") customers_id: String,
            @Field("language_id") language_id: Int,
            @Field("currency_code") currency_code: String,
            @Field("position") position: Int,
            @Field("page_number") page_number: Int): MerchantOdersResponse

    @FormUrlEncoded
    @POST("updatestatus")
    fun updatestatus(
            @Header("Authorization") token: String?,
            @Field("customers_id") customers_id: String?,
            @Field("orders_id") orders_id: String?): Call<OrderData?>?

    @FormUrlEncoded
    @POST("getcoupon")
    fun getCouponInfo(
            @Header("Authorization") token: String?,
            @Field("code") code: String?
    ): Call<CouponsData?>?

    //******************** Languages Data ********************//

    //******************** Languages Data ********************//
    @GET("getlanguages")
    fun getLanguages(@Header("Authorization") token: String?): Call<LanguageData?>?


    @FormUrlEncoded
    @POST("notify_me")
    fun notify_me(
            @Header("Authorization") token: String?,
            @Field("is_notify") is_notify: String?,
            @Field("device_id") device_id: String?
    ): Call<ContactUsData?>?

    @GET("generatebraintreetoken")
    fun generateBraintreeToken(@Header("Authorization") token: String?): Call<GetBrainTreeToken?>?

    @FormUrlEncoded
    @POST("givereview")
    fun giveRating(
            @Header("Authorization") token: String?,
            @FieldMap stringMap: Map<String?, String?>?
    ): Call<GiveRating?>?

    @FormUrlEncoded
    @POST("product-review-read")
    fun likeReview(
            @Header("Authorization") token: String?,
            @Field("customers_id") customers_id: String?,
            @Field("reviews_id") reviews_id: String?,
            @Field("languages_id") languages_id: String?
    ): Call<GetRatings?>?

    @GET("getreviews")
    fun getProductReviews(
            @Query("products_id") product_id: String?,
            @Query("languages_id") languages_id: String?
    ): Call<GetRatings?>?

    // This Api will give us City bounds
    @GET("getlocation")
    fun getCityBounds(
            @Header("Authorization") token: String?,
            @Query(value = "address", encoded = true) address: String?
    ): Call<GoogleAPIResponse?>?

    // Upload Image
    @Multipart
    @POST("uploadimage")
    fun uploadImage(@Part filePart: MultipartBody.Part?): Call<UploadImageModel?>?

    // Update Password
    @GET("updatepassword")
    fun updatePassword(
            @Header("Authorization") token: String?,
            @Query("oldpassword") oldPassword: String?,
            @Query("newpassword") newPassword: String?,
            @Query("customers_id") customers_id: String?): Call<UserData?>?

    //Change Currency
    @GET("getcurrencies")
    fun getCurrency(@Header("Authorization") token: String?): Call<CurrencyModel?>?

    @GET("get_feasible_selling_shops")
    fun getNearbyMerchants(
            @Header("Authorization") token: String?,
            @Query("latitude") latitude: String?, @Query("longitude") longitude: String?, @Query("productlist") productlist: String?): Call<MerchantData?>?

    @GET("get_sellPrices_by_shopId")
    fun getMerchantsProductData(@Query("shopID") shopID: String?, @Query("productlist") productlist: String?): Call<MerchantData?>?


    @DELETE("deleteMerchantProduct")
    fun deleteMerchantProduct(@Query("product_id") product_id: String?, @Query("wallet_id") user_id: String?): Call<ResponseBody?>?


    @FormUrlEncoded
    @POST("updateMerchantProductStock")
    fun updateProduct(
            @Header("Authorization") token: String?,
            @Field("id") id: String?,
            @Field("measure_id") measure_id: String?,
            @Field("wallet_id") wallet_id: String?,
            @Field("product_id") product_id: String?,
            @Field("product_buy_price") product_buy_price: String?,
            @Field("product_sell_price") product_sell_price: String?,
            @Field("product_supplier") product_supplier: String?,
            @Field("product_stock") product_stock: Int,
            @Field("new_manufacturer_name") new_manufacturer_name: String?,
            @Field("new_category_name") new_category_name: String?,
            @Field("new_product_name") new_product_name: String?
    ): Call<ResponseBody?>?

    @FormUrlEncoded
    @POST("restockMerchantProductStock")
    fun restockProduct(
            @Header("Authorization") token: String?,
            @Field("id") id: String?,
            @Field("wallet_id") wallet_id: String?,
            @Field("product_id") product_id: String?,
            @Field("product_stock") product_stock: Int
    ): Call<ProductResponse?>?


    companion object {
        private const val BASE_URL = ConstantValues.ECOMMERCE_URL + "api/"

        fun create(): EmaishaShopAPIService {
            val httpLoggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    Log.e("Retrofit2 Errors", "message: $message")
                }
            })
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            val apiInterceptor = API_Interceptor.Builder()
                    .consumerKey(Utilities.getMd5Hash(ConstantValues.ECOMMERCE_CONSUMER_KEY))
                    .consumerSecret(Utilities.getMd5Hash(ConstantValues.ECOMMERCE_CONSUMER_SECRET))
                    .consumerIP(ConstantValues.getLocalIpAddress())
                    .build()

            val basicOAuthWoocommerce = BasicOAuth.Builder()
                    .consumerKey(ConstantValues.ECOMMERCE_CONSUMER_KEY)
                    .consumerSecret(ConstantValues.ECOMMERCE_CONSUMER_SECRET)
                    .consumerIP(ConstantValues.getLocalIpAddress())
                    .build()

            val okHttpClient = OkHttpClient().newBuilder()
                    .connectTimeout(5, TimeUnit.MINUTES)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS) //.addInterceptor(apiInterceptor)
                    .addInterceptor(httpLoggingInterceptor)
                    .addNetworkInterceptor(object : Interceptor {
                        @Throws(IOException::class)
                        override fun intercept(chain: Interceptor.Chain): Response {
                            val request = chain.request().newBuilder() // .addHeader(Constant.Header, authToken)
                                    .build()
                            return chain.proceed(request)
                        }
                    })
                    .addInterceptor(if (BASE_URL.startsWith("https://")) apiInterceptor else basicOAuthWoocommerce)
                    .build()


            val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            return retrofit.create<EmaishaShopAPIService>(EmaishaShopAPIService::class.java)
        }
    }

}