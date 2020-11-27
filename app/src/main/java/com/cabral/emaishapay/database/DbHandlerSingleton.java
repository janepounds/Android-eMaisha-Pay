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

        database.execSQL(regions_details_insert_query);
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
}


