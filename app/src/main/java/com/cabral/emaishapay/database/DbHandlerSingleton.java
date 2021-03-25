package com.cabral.emaishapay.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cabral.emaishapay.models.address_model.RegionDetails;
import com.cabral.emaishapay.models.marketplace.MarketPrice;
import com.cabral.emaishapay.models.marketplace.MarketPriceSubItem;
import com.cabral.emaishapay.models.marketplace.MyProduce;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DbHandlerSingleton extends SQLiteOpenHelper {
    private static final String TAG = "emaishapayHandler";

    public static final String DATABASE_NAME = "emaishapay.db";
    private static int database_version = 2;

    public static final String REGIONS_DETAILS_TABLE_NAME = "regionDetails";
    public static final String REGIONS_DETAILS_TABLE_ID = "tableId";
    public static final String REGIONS_DETAILS_ID = "id";
    public static final String REGIONS_DETAILS_REGION_TYPE = "regionType";
    public static final String REGIONS_DETAILS_REGION = "region";
    public static final String REGIONS_DETAILS_BELONGS_TO = "belongs_to";

    public static final String ADD_PRODUCE_TABLE_NAME = "produce";
    public static final String ADD_PRODUCE_ID = "id";
    public static final String ADD_PRODUCE_NAME = "name";
    public static final String ADD_PRODUCE_VARIETY = "variety";
    public static final String ADD_PRODUCE_QUANTITY = "quantity";
    public static final String ADD_PRODUCE_PRICE = "price";
    public static final String ADD_PRODUCE_DATE = "date";
    public static final String ADD_PRODUCE_IMAGE = "image";
    public static final String ADD_PRODUCE_UNITS = "units";

    public static final String MARKET_PRICE_TABLE_NAME = "market_price";
    public static final String MARKET_PRICE_ID = "id";
    public static final String MARKET_PRICE_CROP = "crop";
    public static final String MARKET_PRICE_TABLE_MARKET = "market";
    public static final String MARKET_PRICE_RETAIL = "retail";
    public static final String MARKET_PRICE_WHOLESALE = "wholesale";

    public static final String PRODUCTS_TABLE_NAME = "products";
    public static final String PRODUCT_ID = "product_id";
    public static final String PRODUCT_UNIQUE_ID = "unique_id";
    public static final String PRODUCT_MEASURE_ID = "measure_id";
    public static final String PRODUCT_USER_ID = "user_id";
    public static final String SELECTED_PRODUCT_ID = "selected_product_id";
    public static final String PRODUCT_MANUFACTURER = "product_manufacturer";
    public static final String PRODUCT_NAME = "product_name";
    public static final String PRODUCT_CODE = "product_code";
    public static final String SELECT_PRODUCT_CATEGORY_ID = "selected_category_id";
    public static final String PRODUCT_CATEGORY = "product_category";
    public static final String PRODUCT_BUY_PRICE = "product_buy_price";
    public static final String PRODUCT_SELL_PRICE = "product_sell_price";
    public static final String PRODUCT_SUPPLIER = "product_supplier";
    public static final String PRODUCT_IMAGE = "product_image";
    public static final String PRODUCT_STOCK = "product_stock";
    public static final String PRODUCT_UNITS = "product_unit";
    public static final String SYNC_STATUS = "sync_status";



    public static final String PRODUCT_CATEGORY_TABLE_NAME = "product_category";
    public static final String CATEGORY_ID = "category_id";
    public static final String CATEGORY_NAME = "category_name";


    public static final String SUPPLIERS_TABLE_NAME = "suppliers";
    public static final String SUPPLIERS_ID = "suppliers_id";
    public static final String SUPPLIERS_NAME = "suppliers_name";
    public static final String SUPPLIERS_CONTACT_PERSON = "suppliers_contact_person";
    public static final String SUPPLIERS_CELL = "suppliers_cell";
    public static final String SUPPLIERS_EMAIL = "suppliers_email";
    public static final String SUPPLIERS_ADDRESS = "suppliers_address";
    public static final String SUPPLIERS_ADDRESS_TWO = "suppliers_address_two";
    public static final String SUPPLIERS_IMAGE = "suppliers_image";


    public static final String PRODUCT_WEIGHT_TABLE_NAME = "product_weight";
    public static final String WEIGHT_ID = "weight_id";
    public static final String WEIGHT_UNIT = "weight_unit";


    public static final String SHOP_TABLE_NAME = "shop";
    public static final String SHOP_ID = "shop_id";
    public static final String SHOP_NAME = "shop_name";
    public static final String SHOP_CONTACT = "shop_contact";
    public static final String SHOP_EMAIL = "shop_email";
    public static final String SHOP_ADDRESS = "shop_address";
    public static final String SHOP_CURRENCY = "shop_currency";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";


    public static final String ORDER_LIST_TABLE_NAME = "order_list";
    public static final String ORDER_ID = "order_id";
    public static final String INVOICE_ID = "invoice_id";
    public static final String ORDER_DATE = "order_date";
    public static final String ORDER_TIME = "order_time";
    public static final String ORDER_TYPE = "order_type";
    public static final String ORDER_PAYMENT_METHOD = "order_payment_method";
    public static final String CUSTOMER_NAME = "customer_name";
    public static final String STORAGE_STATUS = "storage_status";
    public static final String DISCOUNT = "discount";
    public static final String ORDER_STATUS = "order_status";
    public static final String CUSTOMER_ADDRESS = "customer_address";
    public static final String CUSTOMER_CELL = "customer_cell";
    public static final String DELIVERY_FEE = "delivery_fee";
    public static final String CUSTOMER_EMAIL = "customer_email";


    public static final String ORDER_DETAILS_TABLE_NAME = "order_details";
    public static final String ORDER_DETAILS_ID = "order_details_id";
    public static final String ORDER_INVOICE_ID = "invoice_id";
    public static final String ORDER_PRODUCT_NAME = "product_name";
    public static final String ORDER_PRODUCT_WEIGHT = "product_weight";
    public static final String ORDER_PRODUCT_QTY = "product_qty";
    public static final String PRODUCT_PRICE = "product_price";
    public static final String ORDER_PRODUCT_IMAGE = "product_image";
    public static final String PRODUCT_ORDER_DATE = "product_order_date";


    public static final String ORDER_TYPE_TABLE_NAME = "order_type";
    public static final String ORDER_TYPE_ID = "order_type_id";
    public static final String ORDER_TYPE_NAME = "order_type_name";


    public static final String PRODUCT_CART_TABLE_NAME = "product_cart";
    public static final String CART_ID = "cart_id";
    public static final String CART_PRODUCT_ID = "product_id";
    public static final String CART_PRODUCT_WEIGHT = "product_weight";
    public static final String CART_PRODUCT_WEIGHT_UNIT = "product_weight_unit";
    public static final String CART_PRODUCT_PRICE = "product_price";
    public static final String PRODUCT_QTY = "product_qty";

    public static final String CUSTOMERS_TABLE_NAME = "customers";
    public static final String CUSTOMER_ID = "customer_id";
    public static final String CUSTOMER_CUSTOMER_NAME = "customer_name";
    public static final String CUSTOMER_CUSTOMER_CELL = "customer_cell";
    public static final String CUSTOMER_CUSTOMER_EMAIL = "customer_email";
    public static final String CUSTOMER_CUSTOMER_ADDRESS = "customer_address";
    public static final String CUSTOMER_ADDRESS_TWO = "customer_address_two";
    public static final String CUSTOMER_IMAGE = "customer_image";


    public static final String PAYMENT_METHOD_TABLE_NAME = "payment_method";
    public static final String PAYMENT_METHOD_ID = "payment_method_id";
    public static final String PAYMENT_METHOD_NAME = "payment_method_name";


    public static final String PRODUCT_MANUFACTURER_TABLE_NAME = "manufacturers";
    public static final String MANUFACTURER_ID = "manufacturer_id";
    public static final String MANUFACTURER_NAME = "manufacturer_name";


    public static final String PRODUCT_NAME_TABLE_NAME = "product_names";
    public static final String PRODUCT_NAME_ID = "product_id";
    public static final String PRODUCT_NAME_NAME = "product_name";


    public static  final String TABLE_DEFAULT_ADDRESS ="default_address";
    //Table columns
    public static  final String DEFAULT_ID ="default_id";
    public static  final String ENTRY_CUSTOMER_ID ="customers_id";
    public static  final String ENTRY_FIRST_NAME ="entry_firstname";
    public static  final String ENTRY_LAST_NAME ="entry_lastname";
    public static  final String ENTRY_STREET_ADDRESS ="entry_street_address";
    public static  final String ENTRY_POSTAL_CODE ="entry_postcode";
    public static  final String ENTRY_CITY ="entry_city";
    public static  final String ENTRY_COUNTRY_ID ="entry_country_id";
    public static  final String ENTRY_LATITUDE ="entry_latitude";
    public static  final String ENTRY_LONGITUDE ="entry_longitude";
    public static  final String ENTRY_CONTACT ="entry_contact";
    public static  final String IS_DEFAULT ="is_default";

    private static com.cabral.emaishapay.database.DbHandlerSingleton DbHandlerSingleton;
    SQLiteDatabase database;
    Context context;

    private DbHandlerSingleton(Context context) {

        super(context, DATABASE_NAME, null, database_version);
        this.context = context;
    }

    public static com.cabral.emaishapay.database.DbHandlerSingleton getHandlerInstance(Context context) {
        if (DbHandlerSingleton == null) {
            DbHandlerSingleton = new com.cabral.emaishapay.database.DbHandlerSingleton(context);
        }
        return DbHandlerSingleton;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        database = db;
        String regions_details_insert_query = " CREATE TABLE IF NOT EXISTS " + REGIONS_DETAILS_TABLE_NAME + " ( " + REGIONS_DETAILS_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " + REGIONS_DETAILS_ID + " INTEGER , " + REGIONS_DETAILS_REGION_TYPE + " TEXT, " + REGIONS_DETAILS_REGION + " TEXT, " + REGIONS_DETAILS_BELONGS_TO + " TEXT " + " ) ";

        String add_produce_insert_query = " CREATE TABLE IF NOT EXISTS " + ADD_PRODUCE_TABLE_NAME + " ( " + ADD_PRODUCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                ADD_PRODUCE_NAME + " TEXT, " + ADD_PRODUCE_VARIETY + " TEXT NOT NULL, " + ADD_PRODUCE_QUANTITY + " TEXT NOT NULL, " + ADD_PRODUCE_PRICE + " TEXT, " +
                ADD_PRODUCE_DATE + " TEXT NOT NULL, " + ADD_PRODUCE_IMAGE + " TEXT NOT NULL, "+ ADD_PRODUCE_UNITS + " TEXT NOT NULL ) ";

        String market_price_insert_query = " CREATE TABLE IF NOT EXISTS " + MARKET_PRICE_TABLE_NAME + " ( " + MARKET_PRICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                MARKET_PRICE_CROP + " TEXT NOT NULL, " + MARKET_PRICE_TABLE_MARKET + " TEXT NOT NULL, " + MARKET_PRICE_RETAIL + " TEXT NOT NULL, " + MARKET_PRICE_WHOLESALE + " TEXT NOT NULL " + " ) ";

        String products_table_insert_query = "CREATE TABLE IF NOT EXISTS " + PRODUCTS_TABLE_NAME + "( " + PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                PRODUCT_UNIQUE_ID + " TEXT, "+ PRODUCT_MEASURE_ID + " TEXT, "+ PRODUCT_USER_ID +" TEXT, " + SELECTED_PRODUCT_ID + " TEXT, "+ PRODUCT_MANUFACTURER + " TEXT, "+
                PRODUCT_NAME + " TEXT, " + PRODUCT_CODE + " TEXT , "+SELECT_PRODUCT_CATEGORY_ID + " TEXT, " + PRODUCT_CATEGORY + " TEXT , " +
                PRODUCT_BUY_PRICE + " TEXT, " + PRODUCT_SELL_PRICE + " TEXT , " + PRODUCT_SUPPLIER + " TEXT , " + PRODUCT_IMAGE + " TEXT ," +
                PRODUCT_STOCK + " TEXT, " + PRODUCT_UNITS + " TEXT , " + SYNC_STATUS +" TEXT " + " ) ";



        String product_category_table_insert_query = "CREATE TABLE IF NOT EXISTS " + PRODUCT_CATEGORY_TABLE_NAME + "( " + CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                CATEGORY_NAME + " TEXT " + " ) ";


        String suppliers_table_insert_query = "CREATE TABLE IF NOT EXISTS " + SUPPLIERS_TABLE_NAME + "( " + SUPPLIERS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                SUPPLIERS_NAME + " TEXT, " + SUPPLIERS_CONTACT_PERSON + " TEXT , " + SUPPLIERS_CELL + " TEXT , " + SUPPLIERS_EMAIL + " TEXT ," +
                SUPPLIERS_ADDRESS + " TEXT, " + SUPPLIERS_ADDRESS_TWO + " TEXT , " + SUPPLIERS_IMAGE + " TEXT   " + " ) ";

        String product_weight_table_insert_query = "CREATE TABLE IF NOT EXISTS " + PRODUCT_WEIGHT_TABLE_NAME + "( " + WEIGHT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                WEIGHT_UNIT + " TEXT " + " ) ";


        String shop_insert_query = "CREATE TABLE IF NOT EXISTS " + SHOP_TABLE_NAME + "( " + SHOP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                SHOP_NAME + " TEXT, " + SHOP_CONTACT + " TEXT , " + SHOP_EMAIL + " TEXT , " + SHOP_ADDRESS + " TEXT ," +
                SHOP_CURRENCY + " TEXT, " + LATITUDE + " TEXT , " + LONGITUDE + " TEXT  " + " ) ";


        String order_list_table_insert_query = "CREATE TABLE IF NOT EXISTS " + ORDER_LIST_TABLE_NAME + "( " + ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                INVOICE_ID + " TEXT, " + ORDER_DATE + " TEXT , " + ORDER_TIME + " TEXT , " + ORDER_TYPE + " TEXT ," +
                ORDER_PAYMENT_METHOD + " TEXT, " + CUSTOMER_NAME + " TEXT , " + STORAGE_STATUS + " TEXT , " + DISCOUNT + " TEXT ," +
                ORDER_STATUS + " TEXT, " + CUSTOMER_ADDRESS + " TEXT , " + CUSTOMER_CELL + " TEXT ," + DELIVERY_FEE + " TEXT , " + CUSTOMER_EMAIL + " TEXT " + " ) ";


        String order_details_table_insert_query = "CREATE TABLE IF NOT EXISTS " + ORDER_DETAILS_TABLE_NAME + "( " + ORDER_DETAILS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                ORDER_INVOICE_ID + " TEXT, " + ORDER_PRODUCT_NAME + " TEXT , " + ORDER_PRODUCT_WEIGHT + " TEXT , " + ORDER_PRODUCT_QTY + " TEXT ," +
                PRODUCT_PRICE + " TEXT, " + ORDER_PRODUCT_IMAGE + " TEXT , " + PRODUCT_ORDER_DATE + " TEXT  " + " ) ";


        String order_type_table_insert_query = "CREATE TABLE IF NOT EXISTS " + ORDER_TYPE_TABLE_NAME + "( " + ORDER_TYPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                ORDER_TYPE_NAME + " TEXT " + " ) ";


        String product_cart_table_insert_query = "CREATE TABLE IF NOT EXISTS " + PRODUCT_CART_TABLE_NAME + "( " + CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                CART_PRODUCT_ID + " TEXT, " + CART_PRODUCT_WEIGHT + " TEXT , " + CART_PRODUCT_WEIGHT_UNIT + " TEXT , " + CART_PRODUCT_PRICE + " TEXT ," +
                PRODUCT_QTY + " TEXT " + " ) ";

        String customers_table_insert_query = "CREATE TABLE IF NOT EXISTS " + CUSTOMERS_TABLE_NAME + "( " + CUSTOMER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                CUSTOMER_CUSTOMER_NAME + " TEXT, " + CUSTOMER_CUSTOMER_CELL + " TEXT , " + CUSTOMER_CUSTOMER_EMAIL + " TEXT , " + CUSTOMER_CUSTOMER_ADDRESS + " TEXT ," +
                CUSTOMER_ADDRESS_TWO + " TEXT, " + CUSTOMER_IMAGE + " TEXT " + " ) ";

        String payment_method_table_insert_query = "CREATE TABLE IF NOT EXISTS " + PAYMENT_METHOD_TABLE_NAME + "( " + PAYMENT_METHOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                PAYMENT_METHOD_NAME + " TEXT " + " ) ";


        String product_manufacturer_table_insert_query = "CREATE TABLE IF NOT EXISTS " + PRODUCT_MANUFACTURER_TABLE_NAME + "( " + MANUFACTURER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                MANUFACTURER_NAME + " TEXT " + " ) ";

        String product_names_table_insert_query = "CREATE TABLE IF NOT EXISTS " + PRODUCT_NAME_TABLE_NAME + "( " + PRODUCT_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                PRODUCT_NAME_NAME + " TEXT " + " ) ";

        String default_address_table_insert_query = "CREATE TABLE IF NOT EXISTS "+ TABLE_DEFAULT_ADDRESS +
        "(" +
                DEFAULT_ID                  + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ENTRY_CUSTOMER_ID           + " TEXT," +
                ENTRY_FIRST_NAME            + " TEXT," +
                ENTRY_LAST_NAME             + " TEXT," +
                ENTRY_STREET_ADDRESS        + " TEXT," +
                ENTRY_POSTAL_CODE           + " TEXT," +
                ENTRY_CITY                  + " TEXT," +
                ENTRY_COUNTRY_ID            + " TEXT," +
                ENTRY_LATITUDE              + " TEXT," +
                ENTRY_LONGITUDE             + " TEXT," +
                ENTRY_CONTACT               + " TEXT," +
                IS_DEFAULT                  + " TEXT" +
                ")";



        database.execSQL(regions_details_insert_query);
        database.execSQL(add_produce_insert_query);
        database.execSQL(market_price_insert_query);
        database.execSQL(products_table_insert_query);
        database.execSQL(product_category_table_insert_query);
        database.execSQL(suppliers_table_insert_query);
        database.execSQL(product_weight_table_insert_query);
        database.execSQL(shop_insert_query);
        database.execSQL(order_list_table_insert_query);
        database.execSQL(order_details_table_insert_query);
        database.execSQL(order_type_table_insert_query);
        database.execSQL(product_cart_table_insert_query);
        database.execSQL(customers_table_insert_query);
        database.execSQL(payment_method_table_insert_query);
        database.execSQL(product_manufacturer_table_insert_query);
        database.execSQL(product_names_table_insert_query);
        database.execSQL(default_address_table_insert_query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        String default_address_table_insert_query = "CREATE TABLE IF NOT EXISTS "+ TABLE_DEFAULT_ADDRESS +
                "(" +
                DEFAULT_ID                  + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ENTRY_CUSTOMER_ID           + " TEXT," +
                ENTRY_FIRST_NAME            + " TEXT," +
                ENTRY_LAST_NAME             + " TEXT," +
                ENTRY_STREET_ADDRESS        + " TEXT," +
                ENTRY_POSTAL_CODE           + " TEXT," +
                ENTRY_CITY                  + " TEXT," +
                ENTRY_COUNTRY_ID            + " TEXT," +
                ENTRY_LATITUDE              + " TEXT," +
                ENTRY_LONGITUDE             + " TEXT," +
                ENTRY_CONTACT               + " TEXT," +
                IS_DEFAULT                  + " TEXT" +
                ")";

        switch(oldVersion) {
            case 1:
                onCreate(db);
                // we want both updates, so no break statement here...
            case 2:
                database.execSQL(default_address_table_insert_query);
        }

    }

    public com.cabral.emaishapay.database.DbHandlerSingleton openDB() throws SQLException {

        database = this.getWritableDatabase();
        //onUpgrade(database,1,2);

        return this;
    }

    public void closeDB() throws SQLException {
        this.close();
    }


    //******INSERT REGIONS************//
    public void insertRegionDetails(List<RegionDetails> regionDetails) {
        openDB();
        ContentValues contentValues = new ContentValues();
        for (RegionDetails regionDetail : regionDetails) {
            contentValues.put(REGIONS_DETAILS_ID, regionDetail.getId());
            contentValues.put(REGIONS_DETAILS_REGION_TYPE, regionDetail.getRegionType());
            contentValues.put(REGIONS_DETAILS_REGION, regionDetail.getRegion());
            contentValues.put(REGIONS_DETAILS_BELONGS_TO, regionDetail.getBelongs_to());
            database.insert(REGIONS_DETAILS_TABLE_NAME, null, contentValues);
        }
        closeDB();
    }

    //*********GET LATEST ID FROM REGIONS DETAILS*************//
    public int getMaxRegionId() {
        openDB();
        SQLiteDatabase db = this.getReadableDatabase();
        final String getRegionId = "SELECT MAX(" + REGIONS_DETAILS_ID + ") FROM " + REGIONS_DETAILS_TABLE_NAME;

        Cursor cur = db.rawQuery(getRegionId, null);
        cur.moveToFirst();

        int regionId = cur.getInt(0);

        // close cursor and DB
        cur.close();
        closeDB();

        return regionId;

    }

    //******GET DISTRICTS*****//

    public ArrayList<RegionDetails> getRegionDetails(String district) throws JSONException {
        openDB();
        ArrayList<RegionDetails> array_list = new ArrayList();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + REGIONS_DETAILS_TABLE_NAME + " where " + REGIONS_DETAILS_REGION_TYPE + " = '" + district + "'", null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            RegionDetails regionDetails = new RegionDetails();
            regionDetails.setTableId(Integer.parseInt(res.getString(res.getColumnIndex(REGIONS_DETAILS_TABLE_ID))));
            regionDetails.setId(Integer.parseInt(res.getString(res.getColumnIndex(REGIONS_DETAILS_ID))));
            regionDetails.setRegionType(res.getString(res.getColumnIndex(REGIONS_DETAILS_REGION_TYPE)));
            regionDetails.setRegion(res.getString(res.getColumnIndex(REGIONS_DETAILS_REGION)));
            regionDetails.setBelongs_to(res.getString(res.getColumnIndex(REGIONS_DETAILS_BELONGS_TO)));
            array_list.add(regionDetails);
            res.moveToNext();
        }

        res.close();
        closeDB();
        Log.d("RegionDetails ", array_list.size() + "");
        return array_list;
    }

    //******GET DISTRICT ID****//
    public int getDistrictId(String region) throws JSONException {
        openDB();
        int regionId = 0;


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select " + REGIONS_DETAILS_ID + " from " + REGIONS_DETAILS_TABLE_NAME + " where " + REGIONS_DETAILS_REGION + " = '" + region + "'", null);
        res.moveToFirst();


        while (res.moveToNext()) {
            if (res.isFirst()) {
                //Your code goes here in your case
                return res.getInt(res.getColumnIndex(REGIONS_DETAILS_ID));
            }
        }


        res.close();
        closeDB();
        Log.d("RegionDetails ", String.valueOf(regionId));
        return regionId;
    }

    //******GET SUB COUNTIES**********//
    public ArrayList<RegionDetails> getSubcountyDetails(String belongs_to, String subcounty) throws JSONException {
        openDB();
        ArrayList<RegionDetails> array_list = new ArrayList();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + REGIONS_DETAILS_TABLE_NAME + " where " + REGIONS_DETAILS_BELONGS_TO + " = '" + belongs_to + "'" + " AND " + REGIONS_DETAILS_REGION_TYPE + " = '" + subcounty + "'", null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            RegionDetails regionDetails = new RegionDetails();
            regionDetails.setTableId(Integer.parseInt(res.getString(res.getColumnIndex(REGIONS_DETAILS_TABLE_ID))));
            regionDetails.setId(Integer.parseInt(res.getString(res.getColumnIndex(REGIONS_DETAILS_ID))));
            regionDetails.setRegionType(res.getString(res.getColumnIndex(REGIONS_DETAILS_REGION_TYPE)));
            regionDetails.setRegion(res.getString(res.getColumnIndex(REGIONS_DETAILS_REGION)));
            regionDetails.setBelongs_to(res.getString(res.getColumnIndex(REGIONS_DETAILS_BELONGS_TO)));
            array_list.add(regionDetails);
            res.moveToNext();
        }

        res.close();
        closeDB();
        Log.d("RegionDetails ", array_list.size() + "");
        return array_list;
    }

    //*********GET VILLAGES*************//
    public ArrayList<RegionDetails> getVillageDetails(String belongs_to, String subcounty) throws JSONException {
        openDB();
        ArrayList<RegionDetails> array_list = new ArrayList();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + REGIONS_DETAILS_TABLE_NAME + " where " + REGIONS_DETAILS_BELONGS_TO + " = '" + belongs_to + "'" + " AND " + REGIONS_DETAILS_REGION_TYPE + " = '" + subcounty + "'", null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            RegionDetails regionDetails = new RegionDetails();
            regionDetails.setTableId(Integer.parseInt(res.getString(res.getColumnIndex(REGIONS_DETAILS_TABLE_ID))));
            regionDetails.setId(Integer.parseInt(res.getString(res.getColumnIndex(REGIONS_DETAILS_ID))));
            regionDetails.setRegionType(res.getString(res.getColumnIndex(REGIONS_DETAILS_REGION_TYPE)));
            regionDetails.setRegion(res.getString(res.getColumnIndex(REGIONS_DETAILS_REGION)));
            regionDetails.setBelongs_to(res.getString(res.getColumnIndex(REGIONS_DETAILS_BELONGS_TO)));
            array_list.add(regionDetails);
            res.moveToNext();
        }

        res.close();
        closeDB();
        Log.d("VillageDetails ", array_list.size() + "");
        return array_list;
    }

    public void insertProduce(MyProduce produce) {
        openDB();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ADD_PRODUCE_NAME, produce.getName());
        contentValues.put(ADD_PRODUCE_VARIETY, produce.getVariety());
        contentValues.put(ADD_PRODUCE_QUANTITY, produce.getQuantity());
        contentValues.put(ADD_PRODUCE_PRICE, produce.getPrice());
        contentValues.put(ADD_PRODUCE_DATE, produce.getDate());
        contentValues.put(ADD_PRODUCE_IMAGE, produce.getImage());
        contentValues.put(ADD_PRODUCE_UNITS, produce.getUnits());
        database.insert(ADD_PRODUCE_TABLE_NAME, null, contentValues);

        closeDB();
    }

    public Boolean updateProduce(MyProduce produce,String produce_id) {
        openDB();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ADD_PRODUCE_NAME, produce.getName());
        contentValues.put(ADD_PRODUCE_VARIETY, produce.getVariety());
        contentValues.put(ADD_PRODUCE_QUANTITY, produce.getQuantity());
        contentValues.put(ADD_PRODUCE_PRICE, produce.getPrice());
        contentValues.put(ADD_PRODUCE_DATE, produce.getDate());
        contentValues.put(ADD_PRODUCE_IMAGE, produce.getImage());
        contentValues.put(ADD_PRODUCE_UNITS, produce.getUnits());


        long check = database.update(ADD_PRODUCE_TABLE_NAME, contentValues, ADD_PRODUCE_ID+"=?", new String[]{produce_id});


        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<MyProduce> getAllProduce() {
        openDB();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + ADD_PRODUCE_TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<MyProduce> produceArrayList = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    MyProduce model = new MyProduce();

                    model.setName(cursor.getString(cursor.getColumnIndex(ADD_PRODUCE_NAME)));
                    model.setVariety(cursor.getString(cursor.getColumnIndex(ADD_PRODUCE_VARIETY)));
                    model.setQuantity(cursor.getString(cursor.getColumnIndex(ADD_PRODUCE_QUANTITY)));
                    model.setPrice(cursor.getString(cursor.getColumnIndex(ADD_PRODUCE_PRICE)));
                    model.setDate(cursor.getString(cursor.getColumnIndex(ADD_PRODUCE_DATE)));
                    model.setImage(cursor.getString(cursor.getColumnIndex(ADD_PRODUCE_IMAGE)));
                    model.setUnits(cursor.getString(cursor.getColumnIndex(ADD_PRODUCE_UNITS)));

                    produceArrayList.add(model);

                } while (cursor.moveToNext());
            }
        }

        return produceArrayList;
    }

    public void insertMarketPrice(MarketPrice marketPrice) {
        openDB();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MARKET_PRICE_CROP, marketPrice.getCrop());
        contentValues.put(MARKET_PRICE_TABLE_MARKET, marketPrice.getMarket());
        contentValues.put(MARKET_PRICE_RETAIL, marketPrice.getRetail());
        contentValues.put(MARKET_PRICE_WHOLESALE, marketPrice.getWholesale());
        database.insert(MARKET_PRICE_TABLE_NAME, null, contentValues);

        closeDB();
    }

    public ArrayList<MarketPrice> getAllMarketPrices() {
        openDB();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + MARKET_PRICE_TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<MarketPrice> marketPriceArrayList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                MarketPrice model = new MarketPrice();

                model.setCrop(cursor.getString(cursor.getColumnIndex(MARKET_PRICE_CROP)));
                model.setMarket(cursor.getString(cursor.getColumnIndex(MARKET_PRICE_TABLE_MARKET)));
                model.setRetail(cursor.getString(cursor.getColumnIndex(MARKET_PRICE_RETAIL)));
                model.setWholesale(cursor.getString(cursor.getColumnIndex(MARKET_PRICE_WHOLESALE)));

                marketPriceArrayList.add(model);

            } while (cursor.moveToNext());
        }

        return marketPriceArrayList;
    }

    public ArrayList<MarketPrice> filterMarketPrices(String crop) {
        openDB();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + MARKET_PRICE_TABLE_NAME + " WHERE " + MARKET_PRICE_CROP + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{crop});

        ArrayList<MarketPrice> marketPriceArrayList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                MarketPrice model = new MarketPrice();

                model.setCrop(cursor.getString(cursor.getColumnIndex(MARKET_PRICE_CROP)));
                model.setMarket(cursor.getString(cursor.getColumnIndex(MARKET_PRICE_TABLE_MARKET)));
                model.setRetail(cursor.getString(cursor.getColumnIndex(MARKET_PRICE_RETAIL)));
                model.setWholesale(cursor.getString(cursor.getColumnIndex(MARKET_PRICE_WHOLESALE)));

                marketPriceArrayList.add(model);

            } while (cursor.moveToNext());
        }

        return marketPriceArrayList;
    }

    public ArrayList<MarketPriceSubItem> filterMarketPriceSubItem(String crop) {
        openDB();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + MARKET_PRICE_TABLE_NAME + " WHERE " + MARKET_PRICE_CROP + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{crop});

        ArrayList<MarketPriceSubItem> marketPriceArrayList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                MarketPriceSubItem model = new MarketPriceSubItem();

                model.setMarket(cursor.getString(cursor.getColumnIndex(MARKET_PRICE_TABLE_MARKET)));
                model.setRetail(cursor.getString(cursor.getColumnIndex(MARKET_PRICE_RETAIL)));
                model.setWholesale(cursor.getString(cursor.getColumnIndex(MARKET_PRICE_WHOLESALE)));

                marketPriceArrayList.add(model);

            } while (cursor.moveToNext());
        }

        return marketPriceArrayList;
    }


    //get product data
    public ArrayList<HashMap<String, String>> getProducts(String userId) {
        ArrayList<HashMap<String, String>> product = new ArrayList<>();
        this.database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + PRODUCTS_TABLE_NAME + " WHERE " + PRODUCT_USER_ID + " = "+userId+ " ORDER BY " + PRODUCT_ID + " DESC ", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();


                map.put(PRODUCT_ID, cursor.getString(0));
                map.put(PRODUCT_UNIQUE_ID, cursor.getString(1));
                map.put(PRODUCT_MEASURE_ID, cursor.getString(2));
                map.put(PRODUCT_USER_ID, cursor.getString(3));
                map.put(SELECTED_PRODUCT_ID, cursor.getString(4));
                map.put(PRODUCT_MANUFACTURER, cursor.getString(5));
                map.put(PRODUCT_NAME, cursor.getString(6));
                map.put(PRODUCT_CODE, cursor.getString(7));
                map.put(SELECT_PRODUCT_CATEGORY_ID, cursor.getString(8));
                map.put(PRODUCT_CATEGORY, cursor.getString(9));
                map.put(PRODUCT_BUY_PRICE, cursor.getString(10));
                map.put(PRODUCT_SELL_PRICE, cursor.getString(11));
                map.put(PRODUCT_SUPPLIER, cursor.getString(12));
                map.put(PRODUCT_IMAGE, cursor.getString(13));
                map.put(PRODUCT_STOCK, cursor.getString(14));
                map.put(PRODUCT_UNITS, cursor.getString(15));
                map.put(SYNC_STATUS, cursor.getString(16));



                product.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return product;
    }

    //get product data
    public ArrayList<HashMap<String, String>> getSearchProducts(String s, String userId) {
        ArrayList<HashMap<String, String>> product = new ArrayList<>();
        this.database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM products WHERE ( product_name LIKE '%" + s + "%' OR product_code LIKE '%" + s + "%' ) AND "+ PRODUCT_USER_ID + " = "+userId+" ORDER BY product_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put(PRODUCT_ID, cursor.getString(0));
                map.put(PRODUCT_UNIQUE_ID, cursor.getString(1));
                map.put(PRODUCT_MEASURE_ID, cursor.getString(2));
                map.put(PRODUCT_USER_ID, cursor.getString(3));
                map.put(SELECTED_PRODUCT_ID, cursor.getString(4));
                map.put(PRODUCT_MANUFACTURER, cursor.getString(5));
                map.put(PRODUCT_NAME, cursor.getString(6));
                map.put(PRODUCT_CODE, cursor.getString(7));
                map.put(SELECT_PRODUCT_CATEGORY_ID, cursor.getString(8));
                map.put(PRODUCT_CATEGORY, cursor.getString(9));
                map.put(PRODUCT_BUY_PRICE, cursor.getString(10));
                map.put(PRODUCT_SELL_PRICE, cursor.getString(11));
                map.put(PRODUCT_SUPPLIER, cursor.getString(12));
                map.put(PRODUCT_IMAGE, cursor.getString(13));
                map.put(PRODUCT_STOCK, cursor.getString(14));
                map.put(PRODUCT_UNITS, cursor.getString(15));


                product.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return product;
    }

    //get product category data
    public ArrayList<HashMap<String, String>> getProductCategory() {
        ArrayList<HashMap<String, String>> product_category = new ArrayList<>();
        this.database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + PRODUCT_CATEGORY_TABLE_NAME + " ORDER BY " + CATEGORY_ID + " DESC ", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();


                map.put(CATEGORY_ID, cursor.getString(0));
                map.put(CATEGORY_NAME, cursor.getString(1));

                product_category.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return product_category;
    }


    //get product supplier data
    public ArrayList<HashMap<String, String>> getProductSupplier() {
        ArrayList<HashMap<String, String>> product_suppliers = new ArrayList<>();
        this.database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + SUPPLIERS_TABLE_NAME + "", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();


                map.put(SUPPLIERS_ID, cursor.getString(0));
                map.put(SUPPLIERS_NAME, cursor.getString(1));

                product_suppliers.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return product_suppliers;
    }

    //get product supplier data
    public ArrayList<HashMap<String, String>> getWeightUnit() {
        ArrayList<HashMap<String, String>> product_weight_unit = new ArrayList<>();
        this.database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + PRODUCT_WEIGHT_TABLE_NAME + "", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();


                map.put(WEIGHT_ID, cursor.getString(0));
                map.put(WEIGHT_UNIT, cursor.getString(1));

                product_weight_unit.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return product_weight_unit;
    }

    //get shop information
    public ArrayList<HashMap<String, String>> getShopInformation() {
        ArrayList<HashMap<String, String>> shop_info = new ArrayList<>();
        this.database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + SHOP_TABLE_NAME + "", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put(SHOP_ID, cursor.getString(0));
                map.put(SHOP_NAME, cursor.getString(1));
                map.put(SHOP_CONTACT, cursor.getString(2));
                map.put(SHOP_EMAIL, cursor.getString(3));
                map.put(SHOP_ADDRESS, cursor.getString(4));
                map.put(SHOP_CURRENCY, cursor.getString(5));
                map.put(LATITUDE, cursor.getString(6));
                map.put(LONGITUDE, cursor.getString(7));


                shop_info.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return shop_info;
    }


    //insert products
    public boolean addProduct(String product_unique_id, String measure_id, String userId, String selected_product_id, String product_name, String product_code, String selected_category_id, String product_buy_price, String product_sell_price, String product_stock,String product_supplier , String product_image, String product_unit,String product_manufacturer,String product_category,String sync_status) {

        ContentValues values = new ContentValues();
        this.database = this.getWritableDatabase();
        values.put(PRODUCT_UNIQUE_ID, product_unique_id);
        values.put(PRODUCT_MEASURE_ID, measure_id);
        values.put(PRODUCT_USER_ID, userId);
        values.put(SELECTED_PRODUCT_ID, selected_product_id);
        values.put(PRODUCT_NAME, product_name);
        values.put(PRODUCT_CODE, product_code);
        values.put(SELECT_PRODUCT_CATEGORY_ID, selected_category_id);
        values.put(PRODUCT_BUY_PRICE, product_buy_price);
        values.put(PRODUCT_SELL_PRICE, product_sell_price);
        values.put(PRODUCT_STOCK, product_stock);
        values.put(PRODUCT_SUPPLIER, product_supplier);
        values.put(PRODUCT_IMAGE, product_image);
        values.put(PRODUCT_UNITS, product_unit);
        values.put(PRODUCT_MANUFACTURER, product_manufacturer);
        values.put(PRODUCT_CATEGORY, product_category);
        values.put(SYNC_STATUS, sync_status);


        long check = database.insert(PRODUCTS_TABLE_NAME, null, values);
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }

    //get product name
    public String getCurrency() {
        this.database = this.getWritableDatabase();
        String currency = "n/a";
        Cursor cursor = database.rawQuery("SELECT * FROM " + SHOP_TABLE_NAME + "", null);


        if (cursor.moveToFirst()) {
            do {


                currency = cursor.getString(5);


            } while (cursor.moveToNext());
        }


        cursor.close();
        database.close();
        return currency;
    }

    //get product weight unit name
    public String getSupplierName(String supplier_id) {
        this.database = this.getWritableDatabase();
        String supplier_name = "n/a";
        Cursor cursor = database.rawQuery("SELECT * FROM " + SUPPLIERS_TABLE_NAME + " WHERE " + SUPPLIERS_ID + "=" + supplier_id + "", null);


        if (cursor.moveToFirst()) {
            do {


                supplier_name = cursor.getString(1);


            } while (cursor.moveToNext());
        }


        cursor.close();
        database.close();
        return supplier_name;
    }

    //delete product
    public boolean deleteProduct(String product_id) {


        long check = database.delete(PRODUCTS_TABLE_NAME, PRODUCT_ID + " =? ", new String[]{product_id});
        long check2 = database.delete("product_cart", "product_id=?", new String[]{product_id});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }


    //delete order
    public boolean deleteOrder(String invoice_id) {


        long check = database.delete(ORDER_LIST_TABLE_NAME, ORDER_ID + "=?", new String[]{invoice_id});
        long check2 = database.delete("order_details", "invoice_id=?", new String[]{invoice_id});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }

    public ArrayList<HashMap<String, String>> getOrderList() {
        ArrayList<HashMap<String, String>> orderList = new ArrayList<>();
        this.database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + ORDER_LIST_TABLE_NAME + " ORDER BY " + ORDER_ID + " DESC ", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();


                map.put(INVOICE_ID, cursor.getString(1));
                map.put(ORDER_DATE, cursor.getString(2));
                map.put(ORDER_TIME, cursor.getString(3));
                map.put(ORDER_TYPE, cursor.getString(4));
                map.put(ORDER_PAYMENT_METHOD, cursor.getString(5));
                map.put(CUSTOMER_NAME, cursor.getString(6));
                map.put(STORAGE_STATUS, cursor.getString(7));
                map.put(ORDER_STATUS, cursor.getString(9));
                map.put(CUSTOMER_ADDRESS, cursor.getString(10));
                map.put(CUSTOMER_CELL, cursor.getString(11));
                map.put(DELIVERY_FEE, cursor.getString(12));
                map.put(CUSTOMER_EMAIL, cursor.getString(13));


                orderList.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return orderList;
    }

    public ArrayList<HashMap<String, String>> getOnlineOrderList() {
        ArrayList<HashMap<String, String>> orderList = new ArrayList<>();
        this.database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM order_list WHERE order_status LIKE'Pending%' AND storage_status LIKE '%online%' ORDER BY order_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();


                map.put("invoice_id", cursor.getString(1));
                map.put("order_date", cursor.getString(2));
                map.put("order_time", cursor.getString(3));
                map.put("order_type", cursor.getString(4));
                map.put("order_payment_method", cursor.getString(5));
                map.put("customer_name", cursor.getString(6));
                map.put("storage_status", cursor.getString(7));
                map.put("order_status", cursor.getString(9));
                map.put("customer_address", cursor.getString(10));
                map.put("customer_cell", cursor.getString(11));
                map.put("delivery_fee", cursor.getString(12));
                map.put("customer_email", cursor.getString(13));


                orderList.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return orderList;
    }

    public ArrayList<HashMap<String, String>> searchOrderList(String s) {
        ArrayList<HashMap<String, String>> orderList = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM order_list WHERE customer_name LIKE '%" + s + "%' OR invoice_id LIKE '%" + s + "%' ORDER BY order_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();


                map.put("invoice_id", cursor.getString(1));
                map.put("order_date", cursor.getString(2));
                map.put("order_time", cursor.getString(3));
                map.put("order_type", cursor.getString(4));
                map.put("order_payment_method", cursor.getString(5));
                map.put("customer_name", cursor.getString(6));


                orderList.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return orderList;
    }

    //get order history data
    public ArrayList<HashMap<String, String>> getOrderDetailsList(String order_id) {
        ArrayList<HashMap<String, String>> orderDetailsList = new ArrayList<>();
        this.database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM order_details WHERE invoice_id='" + order_id + "' ORDER BY order_details_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();


                map.put("product_name", cursor.getString(2));
                map.put("product_weight", cursor.getString(3));
                map.put("product_qty", cursor.getString(4));
                map.put("product_price", cursor.getString(5));
                map.put("product_image", cursor.getString(6));

                orderDetailsList.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return orderDetailsList;
    }

    //get order history data
    public int addToCart(String product_id, String weight, String weight_unit, String price, int qty) {


        Cursor result = database.rawQuery("SELECT * FROM product_cart WHERE product_id='" + product_id + "'", null);
        if (result.getCount() >= 1) {

            return 2;

        } else {
            ContentValues values = new ContentValues();
            values.put("product_id", product_id);
            values.put("product_weight", weight);
            values.put("product_weight_unit", weight_unit);
            values.put("product_price", price);
            values.put("product_qty", qty);

            long check = database.insert("product_cart", null, values);


            database.close();


            //if data insert success, its return 1, if failed return -1
            if (check == -1) {
                return -1;
            } else {
                return 1;
            }
        }

    }


    //get cart product
    public ArrayList<HashMap<String, String>> getCartProduct() {
        ArrayList<HashMap<String, String>> product = new ArrayList<>();
        this.database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM product_cart", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();


                map.put("cart_id", cursor.getString(0));
                map.put("product_id", cursor.getString(1));
                map.put("product_weight", cursor.getString(2));
                map.put("product_weight_unit", cursor.getString(3));
                map.put("product_price", cursor.getString(4));
                map.put("product_qty", cursor.getString(5));


                product.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return product;
    }

    //get product name
    public String getProductName(String product_id) {

        String product_name = "n/a";
        Cursor cursor = database.rawQuery("SELECT * FROM products WHERE product_id='" + product_id + "'", null);


        if (cursor.moveToFirst()) {
            do {


                product_name = cursor.getString(6);


            } while (cursor.moveToNext());
        }


        cursor.close();
        database.close();
        return product_name;
    }

    //get product image base 64
    public String getProductImage(String product_id) {

        String image = "n/a";
        Cursor cursor = database.rawQuery("SELECT * FROM products WHERE product_id='" + product_id + "'", null);


        if (cursor.moveToFirst()) {
            do {


                image = cursor.getString(13);


            } while (cursor.moveToNext());
        }


        cursor.close();
        database.close();
        return image;
    }

    //calculate total price of product
    public double getTotalPrice() {


        double total_price = 0;

        Cursor cursor = database.rawQuery("SELECT * FROM product_cart", null);
        if (cursor.moveToFirst()) {
            do {

                double price = Double.parseDouble(cursor.getString(4));
                int qty = Integer.parseInt(cursor.getString(5));
                double sub_total = price * qty;
                total_price = total_price + sub_total;


            } while (cursor.moveToNext());
        } else {
            total_price = 0;
        }
        cursor.close();
        database.close();
        return total_price;
    }


    //delete product from cart
    public boolean deleteProductFromCart(String id) {


        long check = database.delete("product_cart", "cart_id=?", new String[]{id});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }

    //get cart item count
    public int getCartItemCount() {

        Cursor cursor = database.rawQuery("SELECT * FROM product_cart", null);
        int itemCount = cursor.getCount();


        cursor.close();
        database.close();
        return itemCount;
    }


    //calculate total price of product
    public double getTotalOrderPrice(String type) {


        double total_price = 0;
        Cursor cursor = null;


        if (type.equals("monthly")) {

            String currentMonth = new SimpleDateFormat("MM", Locale.ENGLISH).format(new Date());

            String sql = "SELECT * FROM order_details WHERE strftime('%m', product_order_date) = '" + currentMonth + "' ";

            cursor = database.rawQuery(sql, null);

        } else if (type.equals("yearly")) {

            String currentYear = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date());
            String sql = "SELECT * FROM order_details WHERE strftime('%Y', product_order_date) = '" + currentYear + "' ";

            cursor = database.rawQuery(sql, null);

        } else if (type.equals("daily")) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());

            cursor = database.rawQuery("SELECT * FROM order_details WHERE   product_order_date='" + currentDate + "' ORDER BY order_Details_id DESC", null);

        } else {
            cursor = database.rawQuery("SELECT * FROM order_details", null);

        }

        if (cursor.moveToFirst()) {
            do {

                double price = Double.parseDouble(cursor.getString(4));
                int qty = Integer.parseInt(cursor.getString(5));
                double sub_total = price * qty;
                total_price = total_price + sub_total;


            } while (cursor.moveToNext());
        } else {
            total_price = 0;
        }
        cursor.close();
        database.close();
        return total_price;
    }


    //delete product from cart
    public void updateProductQty(String id, String qty) {

        ContentValues values = new ContentValues();

        values.put("product_qty", qty);

        long check = database.update("product_cart", values, "cart_id=?", new String[]{id});


    }

    //get customer data
    public ArrayList<HashMap<String, String>> getCustomers() {
        ArrayList<HashMap<String, String>> customer = new ArrayList<>();
        this.database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM customers ORDER BY customer_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("customer_id", cursor.getString(0));
                map.put("customer_name", cursor.getString(1));
                map.put("customer_cell", cursor.getString(2));
                map.put("customer_email", cursor.getString(3));
                map.put("customer_address", cursor.getString(4));
                map.put("customer_address_two", cursor.getString(5));
                map.put("customer_image", cursor.getString(6));


                customer.add(map);
            } while (cursor.moveToNext());
        }
//        cursor.close();
//        database.close();
        return customer;
    }

    //get order type data
    public ArrayList<HashMap<String, String>> getOrderType() {
        ArrayList<HashMap<String, String>> order_type = new ArrayList<>();
        this.database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM order_type ORDER BY order_type_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("order_type_id", cursor.getString(0));
                map.put("order_type_name", cursor.getString(1));


                order_type.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return order_type;
    }


    //get order type data
    public ArrayList<HashMap<String, String>> getPaymentMethod() {
        ArrayList<HashMap<String, String>> payment_method = new ArrayList<>();
        this.database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM payment_method ORDER BY payment_method_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();


                map.put("payment_method_id", cursor.getString(0));
                map.put("payment_method_name", cursor.getString(1));


                payment_method.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return payment_method;
    }


    //get customer data
    public ArrayList<HashMap<String, String>> searchCustomers(String s) {
        ArrayList<HashMap<String, String>> customer = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM customers WHERE customer_name LIKE '%" + s + "%' ORDER BY customer_id DESC", null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();


                map.put("customer_id", cursor.getString(0));
                map.put("customer_name", cursor.getString(1));
                map.put("customer_cell", cursor.getString(2));
                map.put("customer_email", cursor.getString(3));
                map.put("customer_address", cursor.getString(4));


                customer.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return customer;
    }

    //add manufacturers
    public boolean addManufacturers(String manufacturer_name) {

        ContentValues values = new ContentValues();
        this.database = this.getWritableDatabase();

        values.put(MANUFACTURER_NAME, manufacturer_name);


        long check = database.insert(PRODUCT_MANUFACTURER_TABLE_NAME, null, values);
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }

    //add product categories
    public boolean addProductCategory(String category_name) {

        ContentValues values = new ContentValues();
        this.database = this.getWritableDatabase();

        values.put(CATEGORY_NAME, category_name);


        long check = database.insert(PRODUCT_CATEGORY_TABLE_NAME, null, values);
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }

    //add product names
    public boolean addProductName(String product_name) {

        ContentValues values = new ContentValues();
        this.database = this.getWritableDatabase();

        values.put(PRODUCT_NAME_NAME, product_name);


        long check = database.insert(PRODUCT_NAME_TABLE_NAME, null, values);
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }

    //get offline manufacturers
    //get customer data
    public ArrayList<HashMap<String, String>> getOfflineManufacturers() {
        ArrayList<HashMap<String, String>> manufacturers = new ArrayList<>();
        this.database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + PRODUCT_MANUFACTURER_TABLE_NAME +  " ORDER BY " + MANUFACTURER_NAME + " DESC ", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put(MANUFACTURER_NAME, cursor.getString(1));




                manufacturers.add(map);
            } while (cursor.moveToNext());
        }
//        cursor.close();
//        database.close();
        return manufacturers;
    }

    //get offline product categories
    public ArrayList<HashMap<String, String>> getOfflineProductCategories() {
        ArrayList<HashMap<String, String>> categories = new ArrayList<>();
        this.database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + PRODUCT_CATEGORY_TABLE_NAME +  " ORDER BY " + CATEGORY_NAME + " DESC ", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put(CATEGORY_NAME, cursor.getString(1));



                categories.add(map);
            } while (cursor.moveToNext());
        }
//        cursor.close();
//        database.close();
        return categories;
    }
    //get offline product names
    public ArrayList<HashMap<String, String>> getOfflineProductNames() {
        ArrayList<HashMap<String, String>> productnames = new ArrayList<>();
        this.database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + PRODUCT_NAME_TABLE_NAME +  " ORDER BY " + PRODUCT_NAME_NAME + " DESC ", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put(PRODUCT_NAME_NAME, cursor.getString(1));



                productnames.add(map);
            } while (cursor.moveToNext());
        }
//        cursor.close();
//        database.close();
        return productnames;
    }


    //get unsynced products
    public ArrayList<HashMap<String, String>> getUnsyncedProducts(String sync_status) {
        ArrayList<HashMap<String, String>> product = new ArrayList<>();
        this.database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + PRODUCTS_TABLE_NAME +" WHERE "+ SYNC_STATUS + " = '" + sync_status + "'", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();


                map.put(PRODUCT_ID, cursor.getString(0));
                map.put(PRODUCT_UNIQUE_ID, cursor.getString(1));
                map.put(PRODUCT_MEASURE_ID, cursor.getString(2));
                map.put(PRODUCT_USER_ID, cursor.getString(3));
                map.put(SELECTED_PRODUCT_ID, cursor.getString(4));
                map.put(PRODUCT_MANUFACTURER, cursor.getString(5));
                map.put(PRODUCT_NAME, cursor.getString(6));
                map.put(PRODUCT_CODE, cursor.getString(7));
                map.put(SELECT_PRODUCT_CATEGORY_ID, cursor.getString(8));
                map.put(PRODUCT_CATEGORY, cursor.getString(9));
                map.put(PRODUCT_BUY_PRICE, cursor.getString(10));
                map.put(PRODUCT_SELL_PRICE, cursor.getString(11));
                map.put(PRODUCT_SUPPLIER, cursor.getString(12));
                map.put(PRODUCT_IMAGE, cursor.getString(13));
                map.put(PRODUCT_STOCK, cursor.getString(14));
                map.put(PRODUCT_UNITS, cursor.getString(15));
                map.put(SYNC_STATUS, cursor.getString(16));



                product.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return product;
    }

    //delete product
    public boolean updateProductSyncStatus(String product_id,String sync_status) {
        ContentValues values = new ContentValues();
        this.database = this.getWritableDatabase();

        values.put("sync_status", sync_status);

        long check = database.update("products", values, "product_id=?", new String[]{product_id});


        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }

    //*********** Insert default address Item ********//

    public void insertDefaultAddress(String customer_id,String entry_first_name,String entry_last_name,String entry_street_address,String entry_postal_code,String entry_city,String entry_country_id,String entry_contact,String latitude,String longitude,String is_default) {
        // get and open SQLiteDatabase Instance from static method of DB_Manager class
        this.database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ENTRY_CUSTOMER_ID,      customer_id);
        values.put(ENTRY_FIRST_NAME,      entry_first_name);
        values.put(ENTRY_LAST_NAME,      entry_last_name);
        values.put(ENTRY_STREET_ADDRESS,      entry_street_address);
        values.put(ENTRY_POSTAL_CODE,      entry_postal_code);
        values.put(ENTRY_CITY,      entry_city);
        values.put(ENTRY_COUNTRY_ID,      entry_country_id);
        values.put(ENTRY_LATITUDE,      latitude);
        values.put(ENTRY_LONGITUDE,      longitude);
        values.put(ENTRY_CONTACT,      entry_contact);
        values.put(IS_DEFAULT,      is_default);

        database.insert(TABLE_DEFAULT_ADDRESS, null, values);

        // close the Database
        database.close();
    }



    //*********** Fetch All Default Address ********//

    public ArrayList<String> getDefaultAddress(String customer_id){
        // get and open SQLiteDatabase Instance from static method of DB_Manager class
        openDB();

        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<String> address = new ArrayList<String>();
        Cursor cursor = db.rawQuery( "SELECT * FROM "+ TABLE_DEFAULT_ADDRESS +" WHERE "+ ENTRY_CUSTOMER_ID +" =?", new String[] {customer_id});

        if (cursor.moveToFirst()) {
            do {
                address.add(cursor.getString(4));
                address.add(cursor.getString(6));
                address.add(cursor.getString(7));

            } while (cursor.moveToNext());
        }


        // close the Database
       closeDB();

        return address;
    }
}



