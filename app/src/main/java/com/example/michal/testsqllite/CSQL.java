package com.example.michal.testsqllite;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import static android.R.attr.name;

/**
 * Created by michal on 18.8.2017.
 */

public class CSQL extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "sqltest.db";

    private static final String SQL_CREATE_TABLES =
            "CREATE TABLE IF NOT EXISTS app_config (" +
                    "id_config INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,value TEXT);" +
             "CREATE TABLE IF NOT EXISTS app_users (" +
                    "id_user INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,value TEXT);";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS app_config;" +
            "DROP TABLE IF EXISTS app_users;";

    private static String err_msg = "";

    private SQLiteDatabase db_obj = null;

    //constructor
    public CSQL(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private void init_crypt(){
        String Key = "Something";
        byte[] KeyData = Key.getBytes();
        SecretKeySpec KS = new SecretKeySpec(KeyData, "Blowfish");

        try {
            Cipher cipher = Cipher.getInstance("Blowfish");
            cipher.init(Cipher.ENCRYPT_MODE, KS);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    private boolean open_db(boolean readOnly) {

        close_db();

        if(readOnly) {
            db_obj = this.getReadableDatabase();
        }else{
            db_obj = this.getWritableDatabase();
        }

        if(db_obj.isOpen()) {
            return true;
        }else{
            return false;
        }
    }

    private void close_db() {
        if(db_obj.isOpen()) {
            db_obj.close();
        }

    }


    private boolean check_exist_data(String[] name_table, String[] names){
        return false;
    }

    private boolean import_data(String name_table, String[] names, String[] values, boolean update_values) {
/*
        if(values == null || values.size() < 0)
            return false;

        boolean is_exist_data = check_exist_data(name_table, names);

        if(!update_values && is_exist_data){
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        long id_ret = 0;

        if(is_exist_data) {
            values.put("name",name);
            values.put("value",value);
            id_ret = db.insert(name_table, null, values);
        }else{
            values.put("value",value);
            id_ret = db.update(name_table,values,"name='"+name+"'",null);

        }
        db.close();

        if(id_ret >= 0) {
            return true;
        }else*/{
            return false;
        }
    }

    private boolean delete_data(String name_table, String where_clause, String[] where_args){

        SQLiteDatabase db = this.getWritableDatabase();
        int ret = db.delete(name_table, where_clause, where_args);
        db.close();

        if(ret > 0){
            return true;
        }else{
            return false;
        }

    }



    private String[] load_data_once(String name_table, String[] return_names, String[] where_names, String[] where_values ){

        String[] str_return = { };
        Cursor cursor = null;
        int ValueColumneIndex = 0;
/*
        SQLiteDatabase db = this.getReadableDatabase();

        if(!db.isOpen()) {
            err_msg = "Error: DB is not opened.";
            return str_return;
        }

        String[] tableColumns = new String[] { "value" };
        String whereClause = "name = ?";
        String[] whereArgs = new String[] { name };
        String orderBy = "id_config";

        try {
            //cursor = db.rawQuery("SELECT value FROM app_config WHERE name='" + name + "' LIMIT 1;", null);
            cursor = db.query("app_config",tableColumns,whereClause,whereArgs,null,null,orderBy);
            ValueColumneIndex = cursor.getColumnIndex("value");
        }catch(android.database.sqlite.SQLiteException ex){
            err_msg = ex.getMessage();
            db.close();
            return str_return;
        }


        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            str_return = cursor.getString(ValueColumneIndex);
        }else{
            str_return = "";
        }
        cursor.close();
        db.close();
*/
        return str_return;

    }


    public boolean check_sql_error() {
        if(err_msg.isEmpty()) { return false; }else{ return true; }
    }

    public void clean_sql_error() {
        err_msg = "";
    }

    public String get_sql_error() {
        return err_msg;
    }


    public boolean save_config(String name, String value) {

        String check_conf = load_config(name);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cont_values = new ContentValues();
        long id_ret = 0;

        if(check_conf.isEmpty()) {
            cont_values.put("name",name);
            cont_values.put("value",value);
            id_ret = db.insert("app_config", null, cont_values);
        }else{
            cont_values.put("value",value);
            id_ret = db.update("app_config",cont_values,"name='"+name+"'",null);
        }
        db.close();

        if(id_ret >= 0) {
            return true;
        }else{
            return false;
        }
    }

    public String load_config(String name) {
        String str_return = "";
        Cursor cursor = null;
        int ValueColumneIndex = 0;

        SQLiteDatabase db = this.getReadableDatabase();

        if(!db.isOpen()) {
            err_msg = "Error: DB is not opened.";
            return "";
        }

        String[] tableColumns = new String[] { "value" };
        String whereClause = "name = ?";
        String[] whereArgs = new String[] { name };
        String orderBy = "id_config";

        try {
            //cursor = db.rawQuery("SELECT value FROM app_config WHERE name='" + name + "' LIMIT 1;", null);
            cursor = db.query("app_config",tableColumns,whereClause,whereArgs,null,null,orderBy);
            ValueColumneIndex = cursor.getColumnIndex("value");
        }catch(android.database.sqlite.SQLiteException ex){
            err_msg = ex.getMessage();
            db.close();
            return "";
        }


        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            str_return = cursor.getString(ValueColumneIndex);
        }else{
            str_return = "";
        }
        cursor.close();
        db.close();

        return str_return;
    }

    public void delete_db() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQL_DELETE_ENTRIES);
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
