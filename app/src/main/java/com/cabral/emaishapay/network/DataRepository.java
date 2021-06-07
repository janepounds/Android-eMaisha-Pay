package com.cabral.emaishapay.network;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.cabral.emaishapay.AppExecutors;
import com.cabral.emaishapay.models.cart_model.CartProduct;
import com.cabral.emaishapay.network.api_helpers.BuyInputsAPIClient;
import com.cabral.emaishapay.network.db.daos.DefaultAddressDao;
import com.cabral.emaishapay.network.db.EmaishapayDb;
import com.cabral.emaishapay.network.db.daos.EcManufacturerDao;
import com.cabral.emaishapay.network.db.daos.EcSupplierDao;
import com.cabral.emaishapay.network.db.daos.ShopOrderDao;
import com.cabral.emaishapay.network.db.daos.ShopOrderProductsDao;
import com.cabral.emaishapay.network.db.daos.EcProductCategoryDao;
import com.cabral.emaishapay.network.db.daos.EcProductWeightDao;
import com.cabral.emaishapay.network.db.daos.EcProductsDao;
import com.cabral.emaishapay.network.db.daos.UserCartAttributesDao;
import com.cabral.emaishapay.network.db.daos.UserCartDao;
import com.cabral.emaishapay.network.db.daos.RegionDetailsDao;
import com.cabral.emaishapay.network.db.entities.DefaultAddress;
import com.cabral.emaishapay.network.db.entities.EcManufacturer;
import com.cabral.emaishapay.network.db.entities.EcProduct;
import com.cabral.emaishapay.network.db.entities.EcProductCategory;
import com.cabral.emaishapay.network.db.entities.EcProductWeight;
import com.cabral.emaishapay.network.db.entities.EcSupplier;
import com.cabral.emaishapay.network.db.entities.RegionDetails;
import com.cabral.emaishapay.network.db.entities.ShopOrder;
import com.cabral.emaishapay.network.db.entities.ShopOrderProducts;
import com.cabral.emaishapay.network.db.entities.UserCart;
import com.cabral.emaishapay.network.db.relations.ShopOrderWithProducts;
import com.cabral.emaishapay.utils.NetworkBoundResource;
import com.cabral.emaishapay.utils.Resource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

public class DataRepository {
    private static final String TAG = "DataRepository";

    private static DataRepository ourInstance;
    private final EmaishapayDb dbInstance;
    private final DefaultAddressDao mDefaultAddressDao;
    private final EcManufacturerDao mEcManufacturerDao;
    private final ShopOrderProductsDao mShopOrderProductDao;
    private final ShopOrderDao mShopOrderDao;
    private final EcProductCategoryDao mEcProductCategoryDao;
    private final EcProductsDao mEcProductsDao;
    private final EcProductWeightDao mEcProductWeightDao;
    private final UserCartAttributesDao mUserCartAttributesDao;
    private final UserCartDao mUserCartDao;
    private final RegionDetailsDao mRegionDetailsDao;
    private final EcSupplierDao mEcSupplierDao;

    private DataRepository(Context context) {
        this.dbInstance = EmaishapayDb.getDatabase(context);
        mEcSupplierDao =dbInstance.supplierDao();
        mDefaultAddressDao = dbInstance.defaultAddressDao();
        mEcManufacturerDao = dbInstance.ecManufacturerDao();
        mShopOrderProductDao = dbInstance.shopOrderProductsDao();
        mShopOrderDao = dbInstance.shopOrderDao();
        mEcProductCategoryDao = dbInstance.ecProductCategoryDao();
        mEcProductsDao = dbInstance.ecProductsDao();
        mEcProductWeightDao = dbInstance.ecProductWeightDao();
        mUserCartAttributesDao = dbInstance.userCartAttributesDao();
        mUserCartDao = dbInstance.userCartDao();
        mRegionDetailsDao = dbInstance.regionDetailsDao();
    }

    public static DataRepository getOurInstance(Context context) {
        if (ourInstance == null) {
            synchronized (DataRepository.class) {
                if (ourInstance == null) {
                    ourInstance = new DataRepository(context);
                }
            }
        }
        return ourInstance;
    }

    //*********** Insert default address Item ********//
    public void insertDefaultAddress(String customer_id, String entry_first_name, String entry_last_name, String entry_street_address, String entry_postal_code, String entry_city, String entry_country_id, String entry_contact, String latitude, String longitude, String is_default) {
        // get and open SQLiteDatabase Instance from static method of DB_Manager class

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDefaultAddressDao.insertDefaultAddress(new DefaultAddress(
                        customer_id, entry_first_name,
                        entry_last_name, entry_street_address,
                        entry_postal_code, entry_city,
                        entry_country_id, entry_contact,
                        latitude, longitude, is_default
                ));
            }
        });


    }


    //***********Default Address ********//
    public LiveData<List<DefaultAddress>> getDefaultAddress(String customer_id) {


        return  mDefaultAddressDao.getDefaultAddress(customer_id);
    }


    //get unsynced products
    public List<EcProduct> getUnsyncedProducts(String sync_status) {

        return mEcProductsDao.getUnsyncedProducts(sync_status);
    }



    //get offline product names
    public ArrayList<HashMap<String, String>> getOfflineProductNames() {
        ArrayList<HashMap<String, String>> productnames = new ArrayList<>();

        for (EcProduct product : mEcProductsDao.getOfflineProductNames()) {
            HashMap<String, String> map = new HashMap();
            map.put("product_name", product.getProduct_name());
            productnames.add(map);
        }
        return productnames;
    }

    //get product supplier
    public ArrayList<HashMap<String, String>> getProductSupplier() {
        ArrayList<HashMap<String, String>> suppliers = new ArrayList<>();

        for (EcSupplier supplier : mEcSupplierDao.getProductSupplier()) {
            HashMap<String, String> map = new HashMap();
            map.put("suppliers_name", supplier.getSuppliers_name());
            suppliers.add(map);
        }
        return suppliers;
    }

    //get product weight
    public ArrayList<HashMap<String, String>> getWeightUnit() {
        ArrayList<HashMap<String, String>> productWeights = new ArrayList<>();

        for (EcProductWeight productWeight : mEcProductWeightDao.getWeightUnit()) {
            HashMap<String, String> map = new HashMap();
            map.put("weight_unit", productWeight.getWeight_unit());

            productWeights.add(map);
        }
        return productWeights;
    }


    //get offline product categories
    public ArrayList<HashMap<String, String>> getOfflineProductCategories() {
        ArrayList<HashMap<String, String>> categories = new ArrayList<>();

        for (EcProductCategory category : mEcProductCategoryDao.getOfflineProductCategories()) {
            HashMap<String, String> map = new HashMap();
            map.put("category_name", category.getCategory_name());
            categories.add(map);
        }

        return categories;
    }

    public void insertRegionDetails(List<RegionDetails> regionDetails) {
        mRegionDetailsDao.insertRegionDetails(regionDetails);
    }

    //*********GET LATEST ID FROM REGIONS DETAILS*************//
    public int getMaxRegionId() {
        return mRegionDetailsDao.getMaxRegionId();
    }

    //******GET DISTRICTS*****//

    public LiveData<List<RegionDetails>> getRegionDetails(String type) {
        return mRegionDetailsDao.getRegionDetails(type);
    }

    //******GET SUB COUNTIES**********//
    public LiveData<List<RegionDetails>> getSubcountyDetails(String belongs_to, String subcounty) {
        return mRegionDetailsDao.getSubcountyDetails(belongs_to, subcounty);
    }

    //*********GET VILLAGES*************//
    public LiveData<List<RegionDetails>> getVillageDetails(String belongs_to, String subcounty) {
        return mRegionDetailsDao.getVillageDetails(belongs_to, subcounty);
    }

    public LiveData<RegionDetails> getRegionDetail(String name) {
        return mRegionDetailsDao.getRegionDetail(name);
    }

    //**********GET OFFLINE MANUFACTURERS************//
    public ArrayList<HashMap<String, String>> getOfflineManufacturers() {
        ArrayList<HashMap<String, String>> manufacturers = new ArrayList<>();
        for (EcManufacturer manufacturer : mEcManufacturerDao.getOfflineManufacturers()) {
            HashMap<String, String> map = new HashMap();
            map.put("manufacturer", manufacturer.getManufacturer_name());
            manufacturers.add(map);
        }

        return manufacturers;
    }

    //**********ADD PRODUCT NAME *******************//
    public long addProduct(EcProduct product) {

        return mEcProductsDao.addProduct(product);
    }

    //**********ADD PRODUCT CATEGORY *******************//
    public long addProductCategory(EcProductCategory productCategory) {

        return   mEcProductCategoryDao.addProductCategory(productCategory);
    }

    //**********ADD MANUFACTURERS *******************//
    public long addManufacturers(EcManufacturer manufacturer) {
       return mEcManufacturerDao.addManufacturers(manufacturer);
    }


    //*********UPDATE PRODUCT QTY*****************//
    public void updateProductQty(String product_id, String qty) {

        mUserCartDao.updateProductQty(product_id, qty);

    }

    //******GET TOTAL PRODUCT PRICE ********//
    public double getTotalOrderPrice(String type) {

        List<ShopOrderProducts> order_details = new ArrayList<>();
        double total_price = 0;


        if (type.equals("monthly")) {

            String currentMonth = new SimpleDateFormat("MM", Locale.ENGLISH).format(new Date());

            order_details = mShopOrderProductDao.getMonthlyTotalPrice(currentMonth);

        } else if (type.equals("yearly")) {

            String currentYear = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date());
            order_details = mShopOrderProductDao.getYearlyTotalPrice(currentYear);

        } else if (type.equals("daily")) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());

            order_details = mShopOrderProductDao.getDailyTotalPrice(currentDate);

        } else {
            order_details = mShopOrderProductDao.getTotalPrice();

        }


        if (order_details.size() > 0) {
            for (int i = 0; i < order_details.size(); i++) {
                double price = Double.parseDouble(order_details.get(i).getProduct_price());
                int qty = Integer.parseInt(order_details.get(i).getProduct_qty());
                double sub_total = price * qty;
                total_price = total_price + sub_total;
            }


        } else {
            total_price = 0;
        }

        return total_price;
    }


    //************GET CART ITEM COUNT ************//

    public int getCartItemCount() {
        return mUserCartDao.getCartItemCount();
    }

    //**************DELETE PRODUCT FROM CART********************************//
    public void deleteProductFromCart(UserCart userCart) {

        mUserCartDao.deleteCartItem(userCart);

    }


    //*****************GET TOTAL PRICE**************************************************//
    public double getTotalPrice() {
        List<UserCart> products = mUserCartDao.getCartItems();

        double total_price = 0;
        if (products != null) {

            for (int i = 0; i < products.size(); i++) {
                double price = Double.parseDouble(products.get(i).getProduct_price());
                int qty = Integer.parseInt(products.get(i).getProduct_quantity());
                double sub_total = price * qty;
                total_price = total_price + sub_total;
            }
        } else {
            total_price = 0;
        }

        return total_price;
    }

    //**************GET PRODUCT NAME****************************************//

    public List<EcProduct> getProductName(String product_id) {

        return mEcProductsDao.getProductName(product_id);
    }

    //***********GET PRODUCT IMAGE***************************************************//
    public ArrayList<HashMap<String, String>> getProductImage(String product_id) {
        ArrayList<HashMap<String, String>> productnames = new ArrayList<>();

        for (EcProduct product : mEcProductsDao.getProductImage(product_id)) {
            HashMap<String, String> map = new HashMap();
            map.put("product_image", product.getProduct_image());
            productnames.add(map);
        }

        return productnames;

    }

    //**********ADD PRODUCT NAME *******************//
    public long updateOrder(String order_id,String status) {

        return mShopOrderDao.updateOrder(order_id,status);
    }



    private LiveData<Integer> insertIfDoesnotexist(List<UserCart> mycartProduct, UserCart userCartProduct) {
        MutableLiveData<Integer> results = new MutableLiveData<>();

        if (mycartProduct.size() < 0) {
            results.setValue(new Integer(2 + ""));


        } else {
            //if data insert success, its return 1, if failed return -1
            if (mUserCartDao.insertCartProduct(userCartProduct) == -1) {
                results.setValue(new Integer(-1 + ""));

            } else {
                results.setValue(new Integer(1 + ""));
            }

        }
        return results;
    }


    public LiveData<Resource<List<EcProduct>>> getProducts(String wallet_id, CharSequence key) {


        return new NetworkBoundResource<List<EcProduct>, List<EcProduct>>() {
            @Override
            protected void saveCallResult(@NonNull List<EcProduct> productList) {

               mEcProductsDao.addProduct(productList);
            }

            @NonNull
            @Override
            protected LiveData<List<EcProduct>> loadFromDb() {
                if (TextUtils.isEmpty(key)) {
                    return mEcProductsDao.getProducts();
                }
                LiveData<List<EcProduct>> results = mEcProductsDao.getSearchProducts("*" + key + "*");

                return results;
            }

            @Override
            protected boolean shouldFetch(@Nullable List<EcProduct> data) {
                return TextUtils.isEmpty(key);
            }

            @NonNull
            @Override
            protected Call<List<EcProduct>> createCall() {

                Call<List<EcProduct>> call = BuyInputsAPIClient.getInstance().getMerchantProducts(wallet_id);
                return call;
            }
        }.getAsLiveData();

    }


    //********************GET ORDER LIST *************************//
    public LiveData<Resource<List<ShopOrder>>> getOrderList(String wallet_id, CharSequence key) {

        return new NetworkBoundResource<List<ShopOrder>, List<ShopOrder>>() {
            @Override
            protected void saveCallResult(@NonNull List<ShopOrder> orderList) {

                for (ShopOrder shopOrder : orderList) {

                    ShopOrderWithProducts orderData = new ShopOrderWithProducts();
                    orderData.setShopOrder(shopOrder);
                    orderData.setOrderProducts(shopOrder.getProducts());

                    EmaishapayDb.insertShopOrderWithProducts(dbInstance, orderData);
                }

            }

            @NonNull
            @Override
            protected LiveData<List<ShopOrder>> loadFromDb() {
                if (TextUtils.isEmpty(key)) {
                    LiveData<List<ShopOrderWithProducts>> load = mShopOrderDao.getOrderList();


                    LiveData<List<ShopOrder>> resultantOrderData =
                            Transformations.switchMap(load, myOrderData -> changeToShopOrderFormat(myOrderData));


                    return resultantOrderData;
                }

                return mShopOrderDao.searchOrderList("*" + key + "*");
            }

            @Override
            protected boolean shouldFetch(@Nullable List<ShopOrder> data) {
                return TextUtils.isEmpty(key);
            }

            @NonNull
            @Override
            protected Call<List<ShopOrder>> createCall() {

                Call<List<ShopOrder>> call = BuyInputsAPIClient.getInstance().getOrders(wallet_id);
                return call;
            }
        }.getAsLiveData();

    }


    public LiveData<List<ShopOrderWithProducts>> getOrderSales() {

        return  mShopOrderDao.getAllSalesItems();
    }

    public LiveData<List<ShopOrderWithProducts>> SearchOrderSales(CharSequence query) {
        return mShopOrderDao.searchOrderSales("*"+query+"*");
    }

    //*****************GET ORDER DETAILS LIST ***********************//
    public LiveData<List<ShopOrderProducts>> getOrderDetailsList(String order_id) {
        return mShopOrderProductDao.getOrderDetailsList(order_id);
    }

    private LiveData<List<ShopOrder>> changeToShopOrderFormat(List<ShopOrderWithProducts> myOrderData) {
        MutableLiveData<List<ShopOrder>> results = new MutableLiveData<>();
        List<ShopOrder> oderList = new ArrayList<>();

        for (ShopOrderWithProducts order : myOrderData) {

            ShopOrder mShopOrder = order.shopOrder;
            mShopOrder.setProducts(order.getOrderProducts());
            oderList.add(mShopOrder);
        }
        results.setValue(oderList);
        return results;
    }

    public void deleteProductStock(EcProduct product) {
        //product_id

        mEcProductsDao.deleteProduct(product);
    }

    public long updateProductStock(String product_id,String product_code, String product_category,String product_description,String product_buy_price,String product_sell_price,String product_supplier,String product_image, String product_stock,String product_weight_unit,String product_weight,String manufacturer){

     return mEcProductsDao.updateProductStock(product_id, product_code,  product_category, product_description, product_buy_price, product_sell_price, product_supplier, product_image,  product_stock, product_weight_unit, product_weight, manufacturer);

    }

    public long updateProductSyncStatus(String product_id,String status){

        return mEcProductsDao.updateProductSyncStatus(product_id, status);

    }
    //******************GET ORDER HISTORY DATA****************************************************//

    public LiveData<Integer> addToCart(String product_id, UserCart userCartProduct) {
        LiveData<List<UserCart>> cartProducts = mUserCartDao.selectCartProduct(product_id);

        LiveData<Integer> result = Transformations.switchMap(
                cartProducts, mycartProduct ->insertIfDoesnotexist(mycartProduct, userCartProduct) );

        return result;
    }

    public void  clearCart(){
        mUserCartDao.clearCart();
    }

    public int getLastCartID() {
        return mUserCartDao.getLastCartID();
    }

    //*********** Insert New Cart Item ********//
    public void addCartItem(CartProduct cart) {
        //CartItem cartItem= new CartItem(cart.getCustomersBasketProduct(),cart.getCustomersBasketProductAttributes());
        //EmaishapayDb.insertCartItem(dbInstance,cartItem);
    }

    //*********** Update Existing Cart Item *****//
    public void updateCart(CartProduct cart){
        //CartItem cartItem= new CartItem(cart.getCustomersBasketProduct(),cart.getCustomersBasketProductAttributes());
        //EmaishapayDb.updateCartItem(dbInstance,cartItem);

    }


    //*********** Update Price and Quantity of Existing Cart Item ********//

    public void updateCartItem(CartProduct cart) {
        //mUserCartDao.updateCartItem(cart.getCustomersBasketProduct());
    }

    public long restockProductStock(String id, int product_stock) {
        return mEcProductsDao.restockProductStock(id,product_stock);
    }


    //*****************GET DEFAULT ADDRESS *************************//
//    public LiveData<Resource<List<DefaultAddress>>> getDefaultAddress(String customer_id) {
//
//
//        return new NetworkBoundResource<List<DefaultAddress>, DefaultAddress>() {
//            @Override
//            protected void saveCallResult(@NonNull DefaultAddress defaultAddress) {
//                mDefaultAddressDao.insertDefaultAddress(defaultAddress);
//            }
//
//            @NonNull
//            @Override
//            protected LiveData<List<DefaultAddress>> loadFromDb() {
//
//                    return mDefaultAddressDao.getDefaultAddress(customer_id);
//
//            }
//
//            @NonNull
//            @Override
//            protected Call<DefaultAddress> createCall() {
//                return null;
//            }
//
//
//        }.getAsLiveData();



}


