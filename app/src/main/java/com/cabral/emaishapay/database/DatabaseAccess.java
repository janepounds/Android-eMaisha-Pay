package com.cabral.emaishapay.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class DatabaseAccess {
    private static com.cabral.emaishapay.database.DatabaseAccess instance;
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;

    /**
     * Private constructor to avoid object creation from outside classes.
     *
     * @param context
     */
    private DatabaseAccess(Context context) {
        this.openHelper = new com.cabral.emaishapay.database.DatabaseOpenHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static com.cabral.emaishapay.database.DatabaseAccess getInstance(Context context) {


        if (instance == null) {
            instance = new com.cabral.emaishapay.database.DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    //insert customer
    public boolean addCustomer(String customer_name, String customer_cell, String customer_email, String customer_address, String customer_address_two, String customer_image) {

        ContentValues values = new ContentValues();


        values.put("customer_name", customer_name);
        values.put("customer_cell", customer_cell);
        values.put("customer_email", customer_email);
        values.put("customer_address", customer_address);
        values.put("customer_address_two", customer_address_two);
        values.put("customer_image", customer_image);

        long check = database.insert("customers", null, values);
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //insert category
    public boolean addCategory(String category_name) {

        ContentValues values = new ContentValues();


        values.put("category_name", category_name);


        long check = database.insert("product_category", null, values);
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //insert payment method
    public boolean addPaymentMethod(String payment_method_name) {

        ContentValues values = new ContentValues();


        values.put("payment_method_name", payment_method_name);


        long check = database.insert("payment_method", null, values);
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //update category
    public boolean updateCategory(String category_id, String category_name) {

        ContentValues values = new ContentValues();


        values.put("category_name", category_name);


        long check = database.update("product_category", values, "category_id=? ", new String[]{category_id});
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //update payment method
    public boolean updatePaymentMethod(String payment_method_id, String payment_method_name) {

        ContentValues values = new ContentValues();


        values.put("payment_method_name", payment_method_name);


        long check = database.update("payment_method", values, "payment_method_id=? ", new String[]{payment_method_id});
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //update customer
    public boolean updateCustomer(String customer_id, String customer_name, String customer_cell, String customer_email, String customer_address, String customer_address_two, String customer_image) {

        ContentValues values = new ContentValues();


        values.put("customer_name", customer_name);
        values.put("customer_cell", customer_cell);
        values.put("customer_email", customer_email);
        values.put("customer_address", customer_address);
        values.put("customer_address_two", customer_address_two);
        values.put("customer_image", customer_image);

        long check = database.update("customers", values, " customer_id=? ", new String[]{customer_id});
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //update shop information
    public boolean updateShopInformation(String shop_name, String shop_contact, String shop_email, String shop_address, String shop_currency, String latitude, String longitude) {


        String shop_id = "1";
        ContentValues values = new ContentValues();


        values.put("shop_name", shop_name);
        values.put("shop_contact", shop_contact);
        values.put("shop_email", shop_email);
        values.put("shop_address", shop_address);
        values.put("shop_currency", shop_currency);
        values.put("latitude", latitude);
        values.put("longitude", longitude);

        long check = database.update("shop", values, "shop_id=? ", new String[]{shop_id});
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean addShopInformation(String shop_name, String shop_contact, String shop_email, String shop_address, String shop_currency, String latitude, String longitude) {


        ContentValues values = new ContentValues();
        values.put("shop_id", 1);
        values.put("shop_name", shop_name);
        values.put("shop_contact", shop_contact);
        values.put("shop_email", shop_email);
        values.put("shop_address", shop_address);
        values.put("shop_currency", shop_currency);
        values.put("latitude", latitude);
        values.put("longitude", longitude);

        long check = database.insert("shop", null, values);
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //insert products
    public boolean addProduct(String product_id, String product_name, String product_code, String product_category, String product_description, String product_buy_price, String product_sell_price, String product_stock, String product_supplier, String product_image, String weight_unit, String product_weight) {

        ContentValues values = new ContentValues();
        this.database = openHelper.getWritableDatabase();
        values.put("product_id", product_id);
        values.put("product_name", product_name);
        values.put("product_code", product_code);
        values.put("product_category", product_category);
        values.put("product_description", product_description);
        values.put("product_buy_price", product_buy_price);
        values.put("product_sell_price", product_sell_price);
        values.put("product_supplier", product_supplier);
        values.put("product_image", product_image);
        values.put("product_stock", product_stock);
        values.put("product_weight_unit", weight_unit);
        values.put("product_weight", product_weight);

        long check = database.insert("products", null, values);
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //insert products
    public boolean updateProduct(String product_name, String product_code, String product_category, String product_description, String product_buy_price, String product_sell_price, String product_stock, String product_supplier, String product_image, String weight_unit_id, String product_weight, String product_id) {

        ContentValues values = new ContentValues();


        values.put("product_name", product_name);
        values.put("product_code", product_code);
        values.put("product_category", product_category);
        values.put("product_description", product_description);
        values.put("product_buy_price", product_buy_price);
        values.put("product_sell_price", product_sell_price);

        values.put("product_supplier", product_supplier);
        values.put("product_image", product_image);
        values.put("product_stock", product_stock);
        values.put("product_weight_unit_id", weight_unit_id);

        values.put("product_weight", product_weight);


        long check = database.update("products", values, "product_id=?", new String[]{product_id});
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //insert expense
    public boolean addExpense(String expense_name, String expense_amount, String expense_note, String date, String time) {

        ContentValues values = new ContentValues();


        values.put("expense_name", expense_name);
        values.put("expense_amount", expense_amount);
        values.put("expense_note", expense_note);
        values.put("expense_date", date);
        values.put("expense_time", time);


        long check = database.insert("expense", null, values);
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //update expense
    public boolean updateExpense(String expense_id, String expense_name, String expense_amount, String expense_note, String date, String time) {

        ContentValues values = new ContentValues();


        values.put("expense_name", expense_name);
        values.put("expense_amount", expense_amount);
        values.put("expense_note", expense_note);
        values.put("expense_date", date);
        values.put("expense_time", time);


        long check = database.update("expense", values, "expense_id=?", new String[]{expense_id});
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //insert Suppliers
    public boolean addSuppliers(String suppliers_name, String suppliers_contact_person, String suppliers_cell, String suppliers_email, String suppliers_address, String suppliers_address_two, String suppliers_image) {

        ContentValues values = new ContentValues();


        values.put("suppliers_name", suppliers_name);
        values.put("suppliers_contact_person", suppliers_contact_person);
        values.put("suppliers_cell", suppliers_cell);
        values.put("suppliers_email", suppliers_email);
        values.put("suppliers_address", suppliers_address);
        values.put("suppliers_address_two", suppliers_address_two);
        values.put("suppliers_image", suppliers_image);

        long check = database.insert("suppliers", null, values);
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //update Suppliers
    public boolean updateSuppliers(String suppliers_id, String suppliers_name, String suppliers_contact_person, String suppliers_cell, String suppliers_email, String suppliers_address, String suppliers_address_two, String suppliers_image) {

        ContentValues values = new ContentValues();


        values.put("suppliers_name", suppliers_name);
        values.put("suppliers_contact_person", suppliers_contact_person);
        values.put("suppliers_cell", suppliers_cell);
        values.put("suppliers_email", suppliers_email);
        values.put("suppliers_address", suppliers_address);
        values.put("suppliers_address_two", suppliers_address_two);
        values.put("suppliers_image", suppliers_image);

        long check = database.update("suppliers", values, "suppliers_id=?", new String[]{suppliers_id});
        database.close();

        //if data insert success, its return 1, if failed return -1
        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //get product image base 64
    public String getProductImage(String product_id) {

        String image = "n/a";
        Cursor cursor = database.rawQuery("SELECT * FROM products WHERE product_id='" + product_id + "'", null);


        if (cursor.moveToFirst()) {
            do {


                image = cursor.getString(8);


            } while (cursor.moveToNext());
        }


        cursor.close();
        database.close();
        return image;
    }


    //get product weight unit name
    public String getWeightUnitName(String weight_unit_id) {

        String weight_unit_name = "n/a";
        Cursor cursor = database.rawQuery("SELECT * FROM product_weight WHERE weight_id=" + weight_unit_id + "", null);


        if (cursor.moveToFirst()) {
            do {


                weight_unit_name = cursor.getString(1);


            } while (cursor.moveToNext());
        }


        cursor.close();
        database.close();
        return weight_unit_name;
    }


    //get product weight unit name
    public String getSupplierName(String supplier_id) {

        String supplier_name = "n/a";
        Cursor cursor = database.rawQuery("SELECT * FROM suppliers WHERE suppliers_id=" + supplier_id + "", null);


        if (cursor.moveToFirst()) {
            do {


                supplier_name = cursor.getString(1);


            } while (cursor.moveToNext());
        }


        cursor.close();
        database.close();
        return supplier_name;
    }


    //get product weight unit name
    public String getCategoryName(String category_id) {

        String product_category = "n/a";
        Cursor cursor = database.rawQuery("SELECT * FROM product_category WHERE category_id=" + category_id + "", null);


        if (cursor.moveToFirst()) {
            do {


                product_category = cursor.getString(1);


            } while (cursor.moveToNext());
        }


        cursor.close();
        database.close();
        return product_category;
    }


    //Add product into cart
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
        this.database = openHelper.getWritableDatabase();
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

    private int getID(String id) {
        this.database = openHelper.getWritableDatabase();
        Cursor c = database.query("order_list", new String[]{"invoice_id"}, "invoice_id =? ", new String[]{id}, null, null, null, null);
        if (c.moveToFirst()) //if the row exist then return the id
            return c.getInt(c.getColumnIndex("invoice_id"));

        c.close();
        return -1;
    }

    public boolean addOrder(JSONObject obj) {
        this.database = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        ContentValues values2 = new ContentValues();
        long check = -1;
        String order_id = null;
        try {
            order_id = obj.getString("order_id");
            String order_date = obj.getString("order_date");
            String order_time = obj.getString("order_time");
            String order_type = obj.getString("order_type");
            String order_payment_method = obj.getString("order_payment_method");
            String customer_name = obj.getString("customer_name");
            String customer_address = obj.getString("customer_address");
            String customer_email = obj.getString("customer_email");
            String customer_cell = obj.getString("customer_cell");
            String delivery_fee = obj.getString("delivery_fee");
            String storage_status = obj.getString("storage_status");
            String order_status = obj.getString("order_status");


            values.put("invoice_id", order_id);
            values.put("order_date", order_date);
            values.put("order_time", order_time);
            values.put("order_type", order_type);
            values.put("order_payment_method", order_payment_method);
            values.put("customer_name", customer_name);
            values.put("storage_status", storage_status);
            values.put("order_status", order_status);
            values.put("customer_address", customer_address);
            values.put("customer_cell", customer_cell);
            values.put("delivery_fee", delivery_fee);
            values.put("customer_email", customer_email);


            int id = getID(order_id);


            if (id == -1) {
                check = database.insert("order_list", null, values);
                try {

                    JSONArray result = obj.getJSONArray("products");

                    for (int i = 0; i < result.length(); i++) {
                        JSONObject jo = result.getJSONObject(i);
                        String product_name = jo.getString("product_name"); //ref
                        String product_weight = jo.getString("product_weight");
                        String product_qty = jo.getString("product_qty");
                        String product_price = jo.getString("product_price");
                        String product_image = null;
                        try {

                            product_image = jo.getString("product_image");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        String product_order_date = jo.getString("product_order_date");


                        values2.put("invoice_id", order_id);
                        values2.put("product_name", product_name);
                        values2.put("product_weight", product_weight);
                        values2.put("product_qty", product_qty);
                        values2.put("product_price", product_price);
                        values2.put("product_image", product_image);
                        values2.put("product_order_date", product_order_date);


                        database.insert("order_details", null, values2);

                        return true;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                check = database.update("order_list", values, "invoice_id=?", new String[]{order_id});

            }

            database.delete("product_cart", null, null);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        database.close();

        if (check == -1) {
            return false;
        } else {
            return true;
        }

    }


    //insert order in order list
    public boolean insertOrder(String order_id, JSONObject obj) {

        ContentValues values = new ContentValues();
        ContentValues values2 = new ContentValues();

        try {
            String order_date = obj.getString("order_date");
            String order_time = obj.getString("order_time");
            String order_type = obj.getString("order_type");
            String order_payment_method = obj.getString("order_payment_method");
            String customer_name = obj.getString("customer_name");


            values.put("invoice_id", order_id);
            values.put("order_date", order_date);
            values.put("order_time", order_time);
            values.put("order_type", order_type);
            values.put("order_payment_method", order_payment_method);
            values.put("customer_name", customer_name);


            database.insert("order_list", null, values);

            database.delete("product_cart", null, null);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {

            JSONArray result = obj.getJSONArray("lines");

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String product_name = jo.getString("product_name"); //ref
                String product_weight = jo.getString("product_weight");
                String product_qty = jo.getString("product_qty");
                String product_price = jo.getString("product_price");
                String product_image = null;
                try {

                    product_image = jo.getString("product_image");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String product_order_date = jo.getString("product_order_date");


                values2.put("invoice_id", order_id);
                values2.put("product_name", product_name);
                values2.put("product_weight", product_weight);
                values2.put("product_qty", product_qty);
                values2.put("product_price", product_price);
                values2.put("product_image", product_image);
                values2.put("product_order_date", product_order_date);


                database.insert("order_details", null, values2);

                return true;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        database.close();

        return false;
    }


    public ArrayList<HashMap<String, String>> getOrderList() {
        ArrayList<HashMap<String, String>> orderList = new ArrayList<>();
        this.database = openHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM order_list ORDER BY order_id DESC", null);
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

    public ArrayList<HashMap<String, String>> getOnlineOrderList() {
        ArrayList<HashMap<String, String>> orderList = new ArrayList<>();
        this.database = openHelper.getWritableDatabase();
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

//    public ArrayList<HashMap<String, String>> searchCustomer(String s) {
//        ArrayList<HashMap<String, String>> orderList = new ArrayList<>();
//        Cursor cursor = database.rawQuery("SELECT * FROM customer WHERE customer_name LIKE '%" + s + "%'", null);
//        if (cursor.moveToFirst()) {
//            do {
//                HashMap<String, String> map = new HashMap<String, String>();
//
//
//                map.put("invoice_id", cursor.getString(1));
//                map.put("order_date", cursor.getString(2));
//                map.put("order_time", cursor.getString(3));
//                map.put("order_type", cursor.getString(4));
//                map.put("order_payment_method", cursor.getString(5));
//                map.put("customer_name", cursor.getString(6));
//
//
//                orderList.add(map);
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        database.close();
//        return orderList;
//    }


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
        this.database = openHelper.getWritableDatabase();
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
    public ArrayList<HashMap<String, String>> getAllSalesItems() {
        ArrayList<HashMap<String, String>> orderDetailsList = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT order_details.*,order_list.order_payment_method FROM order_details INNER JOIN order_list ON order_details.invoice_id=order_list.invoice_id WHERE order_list.order_status='Completed' ORDER BY order_details_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();


                map.put("invoice_id", cursor.getString(1));
                map.put("product_name", cursor.getString(2));
                map.put("product_weight", cursor.getString(3));
                map.put("product_qty", cursor.getString(4));
                map.put("product_price", cursor.getString(5));
                map.put("product_image", cursor.getString(6));
                map.put("product_order_date", cursor.getString(7));
                map.put("order_payment_method", cursor.getString(8));

                orderDetailsList.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return orderDetailsList;
    }


    //get order history data
    public ArrayList<HashMap<String, String>> getSalesReport(String type) {
        ArrayList<HashMap<String, String>> orderDetailsList = new ArrayList<>();
        Cursor cursor = null;
        if (type.equals("all")) {
            cursor = database.rawQuery("SELECT * FROM order_details  ORDER BY order_details_id DESC", null);
        } else if (type.equals("daily")) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());

            cursor = database.rawQuery("SELECT * FROM order_details WHERE   product_order_date='" + currentDate + "' ORDER BY order_Details_id DESC", null);

        } else if (type.equals("monthly")) {

            String currentMonth = new SimpleDateFormat("MM", Locale.ENGLISH).format(new Date());
            String sql = "SELECT * FROM order_details WHERE strftime('%m', product_order_date) = '" + currentMonth + "' ";

            cursor = database.rawQuery(sql, null);

        } else if (type.equals("yearly")) {

            String currentYear = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date());
            Log.d("YEAR", currentYear);
            String sql = "SELECT * FROM order_details WHERE strftime('%Y', product_order_date) = '" + currentYear + "' ";

            cursor = database.rawQuery(sql, null);

        }


        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();


                map.put("product_name", cursor.getString(2));
                map.put("product_weight", cursor.getString(3));
                map.put("product_qty", cursor.getString(4));
                map.put("product_price", cursor.getString(5));
                map.put("product_image", cursor.getString(6));
                map.put("product_order_date", cursor.getString(7));

                orderDetailsList.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return orderDetailsList;
    }


    //get order history data
    public ArrayList<HashMap<String, String>> getExpenseReport(String type) {
        ArrayList<HashMap<String, String>> orderDetailsList = new ArrayList<>();
        Cursor cursor = null;
        if (type.equals("all")) {
            cursor = database.rawQuery("SELECT * FROM expense  ORDER BY expense_id DESC", null);
        } else if (type.equals("daily")) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());

            cursor = database.rawQuery("SELECT * FROM expense WHERE   expense_date='" + currentDate + "' ORDER BY expense_id DESC", null);

        } else if (type.equals("monthly")) {

            String currentMonth = new SimpleDateFormat("MM", Locale.ENGLISH).format(new Date());
            String sql = "SELECT * FROM expense WHERE strftime('%m', expense_date) = '" + currentMonth + "' ";

            cursor = database.rawQuery(sql, null);

        } else if (type.equals("yearly")) {

            String currentYear = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date());
            String sql = "SELECT * FROM expense WHERE strftime('%Y', expense_date) = '" + currentYear + "' ";

            cursor = database.rawQuery(sql, null);

        }


        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("expense_id", cursor.getString(cursor.getColumnIndex("expense_id")));
                map.put("expense_name", cursor.getString(cursor.getColumnIndex("expense_name")));
                map.put("expense_note", cursor.getString(cursor.getColumnIndex("expense_note")));
                map.put("expense_amount", cursor.getString(cursor.getColumnIndex("expense_amount")));
                map.put("expense_date", cursor.getString(cursor.getColumnIndex("expense_date")));
                map.put("expense_time", cursor.getString(cursor.getColumnIndex("expense_time")));

                orderDetailsList.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return orderDetailsList;
    }


    //calculate total price in month
    public float getMonthlySalesAmount(String month, String getYear) {


        float total_price = 0;
        Cursor cursor = null;


        String year = getYear;


        String sql = "SELECT * FROM order_details WHERE strftime('%m', product_order_date) = '" + month + "' AND strftime('%Y', product_order_date) = '" + year + "'  ";

        cursor = database.rawQuery(sql, null);


        if (cursor.moveToFirst()) {
            do {

                float price = Float.parseFloat(cursor.getString(4));
                int qty = Integer.parseInt(cursor.getString(5));
                float sub_total = price * qty;
                total_price = total_price + sub_total;


            } while (cursor.moveToNext());
        } else {
            total_price = 0;
        }
        cursor.close();
        database.close();

        Log.d("total_price", "" + total_price);
        return total_price;
    }


    //calculate total price in month
    public float getMonthlyExpenseAmount(String month, String getYear) {


        float total_cost = 0;
        Cursor cursor = null;


        String year = getYear;


        String sql = "SELECT * FROM expense WHERE strftime('%m', expense_date) = '" + month + "' AND strftime('%Y', expense_date) = '" + year + "'  ";

        cursor = database.rawQuery(sql, null);


        if (cursor.moveToFirst()) {
            do {

                float cost = Float.parseFloat(cursor.getString(3));

                total_cost = total_cost + cost;


            } while (cursor.moveToNext());
        } else {
            total_cost = 0;
        }
        cursor.close();
        database.close();

        Log.d("total_price", "" + total_cost);
        return total_cost;
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


    //delete product from cart
    public void updateProductQty(String id, String qty) {

        ContentValues values = new ContentValues();

        values.put("product_qty", qty);

        long check = database.update("product_cart", values, "cart_id=?", new String[]{id});


    }

    public boolean updateOrder(String id, String status) {
        this.database = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("order_status", status);

        long check = database.update("order_list", values, "invoice_id=?", new String[]{id});

        if (check == -1) {
            return false;
        } else {
            return true;
        }
    }


    //get product name
    public String getProductName(String product_id) {

        String product_name = "n/a";
        Cursor cursor = database.rawQuery("SELECT * FROM products WHERE product_id='" + product_id + "'", null);


        if (cursor.moveToFirst()) {
            do {


                product_name = cursor.getString(1);


            } while (cursor.moveToNext());
        }


        cursor.close();
        database.close();
        return product_name;
    }


    //get product name
    public String getCurrency() {

        String currency = "n/a";
        Cursor cursor = database.rawQuery("SELECT * FROM shop", null);


        if (cursor.moveToFirst()) {
            do {


                currency = cursor.getString(5);


            } while (cursor.moveToNext());
        }


        cursor.close();
        database.close();
        return currency;
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


    //calculate total price of product
    public double totalOrderPrice(String invoice_id) {


        double total_price = 0;


        Cursor cursor = database.rawQuery("SELECT * FROM order_details WHERE invoice_id='" + invoice_id + "'", null);


        if (cursor.moveToFirst()) {
            do {

                double price = Double.parseDouble(cursor.getString(5));
                int qty = Integer.parseInt(cursor.getString(4));
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


    //calculate total price of expense
    public double getTotalExpense(String type) {


        double total_cost = 0;
        Cursor cursor = null;


        if (type.equals("monthly")) {

            String currentMonth = new SimpleDateFormat("MM", Locale.ENGLISH).format(new Date());

            String sql = "SELECT * FROM expense WHERE strftime('%m', expense_date) = '" + currentMonth + "' ";

            cursor = database.rawQuery(sql, null);

        } else if (type.equals("yearly")) {

            String currentYear = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(new Date());
            String sql = "SELECT * FROM expense WHERE strftime('%Y', expense_date) = '" + currentYear + "' ";

            cursor = database.rawQuery(sql, null);

        } else if (type.equals("daily")) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());

            cursor = database.rawQuery("SELECT * FROM expense WHERE   expense_date='" + currentDate + "' ORDER BY expense_id DESC", null);

        } else {
            cursor = database.rawQuery("SELECT * FROM expense", null);

        }

        if (cursor.moveToFirst()) {
            do {

                double expense = Double.parseDouble(cursor.getString(3));

                total_cost = total_cost + expense;


            } while (cursor.moveToNext());
        } else {
            total_cost = 0;
        }
        cursor.close();
        database.close();
        return total_cost;
    }


    //get customer data
    public ArrayList<HashMap<String, String>> getCustomers() {
        ArrayList<HashMap<String, String>> customer = new ArrayList<>();
        this.database = openHelper.getWritableDatabase();
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
        this.database = openHelper.getWritableDatabase();
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
        this.database = openHelper.getWritableDatabase();
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


    //get customer data
    public ArrayList<HashMap<String, String>> searchSuppliers(String s) {
        ArrayList<HashMap<String, String>> customer = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM suppliers WHERE suppliers_name LIKE '%" + s + "%' ORDER BY suppliers_id DESC", null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();


                map.put("suppliers_id", cursor.getString(0));
                map.put("suppliers_name", cursor.getString(1));
                map.put("suppliers_contact_person", cursor.getString(2));
                map.put("suppliers_cell", cursor.getString(3));
                map.put("suppliers_email", cursor.getString(4));
                map.put("suppliers_address", cursor.getString(5));


                customer.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return customer;
    }


    //get shop information
    public ArrayList<HashMap<String, String>> getShopInformation() {
        ArrayList<HashMap<String, String>> shop_info = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM shop", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("shop_id", cursor.getString(0));
                map.put("shop_name", cursor.getString(1));
                map.put("shop_contact", cursor.getString(2));
                map.put("shop_email", cursor.getString(3));
                map.put("shop_address", cursor.getString(4));
                map.put("shop_currency", cursor.getString(5));
                map.put("latitude", cursor.getString(6));
                map.put("longitude", cursor.getString(7));


                shop_info.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return shop_info;
    }


    //get product data
    public ArrayList<HashMap<String, String>> getProducts() {
        ArrayList<HashMap<String, String>> product = new ArrayList<>();
        this.database = openHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM products ORDER BY product_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();


                map.put("product_id", cursor.getString(0));
                map.put("product_name", cursor.getString(1));
                map.put("product_code", cursor.getString(2));
                map.put("product_category", cursor.getString(3));
                map.put("product_description", cursor.getString(4));
                map.put("product_buy_price", cursor.getString(5));
                map.put("product_sell_price", cursor.getString(6));
                map.put("product_supplier", cursor.getString(7));
                map.put("product_image", cursor.getString(8));
                map.put("product_stock", cursor.getString(9));
                map.put("product_weight_unit", cursor.getString(10));
                map.put("product_weight", cursor.getString(11));


                product.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return product;
    }


    //get product data
    public ArrayList<HashMap<String, String>> getProductsInfo(String product_id) {
        ArrayList<HashMap<String, String>> product = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM products WHERE product_id='" + product_id + "'", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();


                map.put("product_id", cursor.getString(0));
                map.put("product_name", cursor.getString(1));
                map.put("product_code", cursor.getString(2));
                map.put("product_category", cursor.getString(3));
                map.put("product_description", cursor.getString(4));
                map.put("product_buy_price", cursor.getString(5));
                map.put("product_sell_price", cursor.getString(6));
                map.put("product_supplier", cursor.getString(7));
                map.put("product_image", cursor.getString(8));
                map.put("product_stock", cursor.getString(9));
                map.put("product_weight_unit_id", cursor.getString(10));
                map.put("product_weight", cursor.getString(11));


                product.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return product;
    }


    //get product data
    public ArrayList<HashMap<String, String>> getAllExpense() {
        ArrayList<HashMap<String, String>> product = new ArrayList<>();
        this.database = openHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM expense ORDER BY expense_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();


                map.put("expense_id", cursor.getString(cursor.getColumnIndex("expense_id")));
                map.put("expense_name", cursor.getString(cursor.getColumnIndex("expense_name")));
                map.put("expense_note", cursor.getString(cursor.getColumnIndex("expense_note")));
                map.put("expense_amount", cursor.getString(cursor.getColumnIndex("expense_amount")));
                map.put("expense_date", cursor.getString(cursor.getColumnIndex("expense_date")));
                map.put("expense_time", cursor.getString(cursor.getColumnIndex("expense_time")));


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
        this.database = openHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM product_category ORDER BY category_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();


                map.put("category_id", cursor.getString(0));
                map.put("category_name", cursor.getString(1));

                product_category.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return product_category;
    }


    //get product category data
    public ArrayList<HashMap<String, String>> searchProductCategory(String s) {
        ArrayList<HashMap<String, String>> product_category = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM product_category WHERE category_name LIKE '%" + s + "%' ORDER BY category_id DESC ", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();


                map.put("category_id", cursor.getString(0));
                map.put("category_name", cursor.getString(1));

                product_category.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return product_category;
    }


    //get product payment method
    public ArrayList<HashMap<String, String>> searchPaymentMethod(String s) {
        ArrayList<HashMap<String, String>> payment_method = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM payment_method WHERE payment_method_name LIKE '%" + s + "%' ORDER BY payment_method_id DESC ", null);
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


    //get product supplier data
    public ArrayList<HashMap<String, String>> getProductSupplier() {
        ArrayList<HashMap<String, String>> product_suppliers = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM suppliers", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();


                map.put("suppliers_id", cursor.getString(0));
                map.put("suppliers_name", cursor.getString(1));

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
        this.database = openHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM product_weight", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();


                map.put("weight_id", cursor.getString(0));
                map.put("weight_unit", cursor.getString(1));

                product_weight_unit.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return product_weight_unit;
    }

    //get product data
    public ArrayList<HashMap<String, String>> searchExpense(String s) {
        ArrayList<HashMap<String, String>> product = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM expense WHERE expense_name LIKE '%" + s + "%' ORDER BY expense_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("expense_id", cursor.getString(cursor.getColumnIndex("expense_id")));
                map.put("expense_name", cursor.getString(cursor.getColumnIndex("expense_name")));
                map.put("expense_note", cursor.getString(cursor.getColumnIndex("expense_note")));
                map.put("expense_amount", cursor.getString(cursor.getColumnIndex("expense_amount")));
                map.put("expense_date", cursor.getString(cursor.getColumnIndex("expense_date")));
                map.put("expense_time", cursor.getString(cursor.getColumnIndex("expense_time")));


                product.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return product;
    }


    //get product data
    public ArrayList<HashMap<String, String>> getSearchProducts(String s) {
        ArrayList<HashMap<String, String>> product = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM products WHERE product_name LIKE '%" + s + "%' OR product_code LIKE '%" + s + "%' ORDER BY product_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("product_id", cursor.getString(0));
                map.put("product_name", cursor.getString(1));
                map.put("product_code", cursor.getString(2));
                map.put("product_category", cursor.getString(3));
                map.put("product_description", cursor.getString(4));
                map.put("product_buy_price", cursor.getString(5));
                map.put("product_sell_price", cursor.getString(6));
                map.put("product_supplier", cursor.getString(7));
                map.put("product_image", cursor.getString(8));
                map.put("product_stock", cursor.getString(9));
                map.put("product_weight_unit_id", cursor.getString(10));
                map.put("product_weight", cursor.getString(11));


                product.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return product;
    }


    //Add product into cart
    public int addToCart(String product_name, String price, String weight, int qty, String base64Image, String ref, String tva_tx, String product_id) {

        Cursor result = database.rawQuery("SELECT * FROM cart WHERE product_name='" + product_name + "' AND price='" + price + "' AND weight='" + weight + "'", null);
        if (result.getCount() >= 1) {

            return 2;
        } else {
            ContentValues values = new ContentValues();
            values.put("product_name", product_name);
            values.put("price", price);
            values.put("weight", weight);
            values.put("qty", qty);
            values.put("image", base64Image);

            values.put("ref", ref); //desc
            values.put("tva_tx", tva_tx);
            values.put("fk_product", product_id);


            long check = database.insert("cart", null, values);

            database.close();


            //if data insert success, its return 1, if failed return -1
            if (check == -1) {
                return -1;
            } else {
                return 1;
            }

        }

    }


    //get suppliers data
    public ArrayList<HashMap<String, String>> getSuppliers() {
        ArrayList<HashMap<String, String>> supplier = new ArrayList<>();
        this.database = openHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM suppliers ORDER BY suppliers_id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();


                map.put("suppliers_id", cursor.getString(0));
                map.put("suppliers_name", cursor.getString(1));
                map.put("suppliers_contact_person", cursor.getString(2));
                map.put("suppliers_cell", cursor.getString(3));
                map.put("suppliers_email", cursor.getString(4));
                map.put("suppliers_address", cursor.getString(5));
                map.put("suppliers_address_two", cursor.getString(6));
                map.put("suppliers_image", cursor.getString(7));


                supplier.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return supplier;
    }


    //delete customer
    public boolean deleteCustomer(String customer_id) {


        long check = database.delete("customers", "customer_id=?", new String[]{customer_id});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }


    //delete category
    public boolean deleteCategory(String category_id) {


        long check = database.delete("product_category", "category_id=?", new String[]{category_id});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }


    //delete payment method
    public boolean deletePaymentMethod(String payment_method_id) {


        long check = database.delete("payment_method", "payment_method_id=?", new String[]{payment_method_id});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }


    //delete order
    public boolean deleteOrder(String invoice_id) {


        long check = database.delete("order_list", "invoice_id=?", new String[]{invoice_id});
        long check2 = database.delete("order_details", "invoice_id=?", new String[]{invoice_id});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }


    //delete product
    public boolean deleteProduct(String product_id) {


        long check = database.delete("products", "product_id=?", new String[]{product_id});
        long check2 = database.delete("product_cart", "product_id=?", new String[]{product_id});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }


    //delete product
    public boolean deleteExpense(String expense_id) {


        long check = database.delete("expense", "expense_id=?", new String[]{expense_id});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }


    //delete supplier
    public boolean deleteSupplier(String customer_id) {


        long check = database.delete("suppliers", "suppliers_id=?", new String[]{customer_id});

        database.close();

        if (check == 1) {
            return true;
        } else {
            return false;
        }

    }
}