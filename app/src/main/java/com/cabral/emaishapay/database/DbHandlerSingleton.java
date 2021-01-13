package com.cabral.emaishapay.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cabral.emaishapay.R;
import com.cabral.emaishapay.models.address_model.RegionDetails;
import com.cabral.emaishapay.models.marketplace.MarketPrice;
import com.cabral.emaishapay.models.marketplace.MarketPriceSubItem;
import com.cabral.emaishapay.models.marketplace.MyProduce;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class DbHandlerSingleton extends SQLiteOpenHelper {
    private static final String TAG = "emaishapayHandler";

    public static final String DATABASE_NAME = "emaishapay.db";
    private static int database_version = 1;

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

    public static final String MARKET_PRICE_TABLE_NAME = "market_price";
    public static final String MARKET_PRICE_ID = "id";
    public static final String MARKET_PRICE_CROP = "crop";
    public static final String MARKET_PRICE_TABLE_MARKET = "market";
    public static final String MARKET_PRICE_RETAIL = "retail";
    public static final String MARKET_PRICE_WHOLESALE = "wholesale";


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
                ADD_PRODUCE_DATE + " TEXT NOT NULL, " + ADD_PRODUCE_IMAGE + " TEXT NOT NULL " + " ) ";

        String market_price_insert_query = " CREATE TABLE IF NOT EXISTS " + MARKET_PRICE_TABLE_NAME + " ( " + MARKET_PRICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                MARKET_PRICE_CROP + " TEXT NOT NULL, " + MARKET_PRICE_TABLE_MARKET + " TEXT NOT NULL, " + MARKET_PRICE_RETAIL + " TEXT NOT NULL, " + MARKET_PRICE_WHOLESALE + " TEXT NOT NULL " + " ) ";

        database.execSQL(regions_details_insert_query);
        database.execSQL(add_produce_insert_query);
        database.execSQL(market_price_insert_query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        onCreate(db);

    }

    public com.cabral.emaishapay.database.DbHandlerSingleton openDB() throws SQLException {

        database = this.getWritableDatabase();
//        onCreate(database);

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
        database.insert(ADD_PRODUCE_TABLE_NAME, null, contentValues);

        closeDB();
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
}


