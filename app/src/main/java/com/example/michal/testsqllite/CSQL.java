package com.example.michal.testsqllite;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
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

    private static String err_msg = "";
    private SQLiteDatabase db_obj = null;
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "sqltest.db";

    private static final String SQL_CREATE_TABLES =
            "CREATE TABLE IF NOT EXISTS app_config (" +
                    "id_config INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,value TEXT);" +
             "CREATE TABLE IF NOT EXISTS app_users (" +
                    "id_user INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,value TEXT);";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS app_config;" +
            "DROP TABLE IF EXISTS app_users;";


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

        try {
            if(readOnly) {
                db_obj = this.getReadableDatabase();
            }else{
                db_obj = this.getWritableDatabase();
            }
        }catch (SQLiteException ex){
            add_err_msg(ex.getMessage());
            return false;
        }


        if(db_obj.isOpen()) {
            return true;
        }else{
            return false;
        }
    }

    private void close_db() {
        if(db_obj != null && db_obj.isOpen() ) {
            db_obj.close();
        }

    }


    private boolean delete_data(String name_table, String where_clause, String[] where_args){

        if (!db_obj.isOpen() || db_obj.isReadOnly() )
            open_db(false);

        if(check_sql_error())
            return false;

        int ret = db_obj.delete(name_table, where_clause, where_args);

        if(ret < 1){
            return false;
        }

        return false;
    }

    private boolean insert_update_data(String name_table, ContentValues values) {
        return insert_update_data( name_table, null, null, values, false);
    }

    private boolean insert_update_data(String name_table, String where_clause,
                                       String[] where_args, ContentValues values, boolean unique_data) {

        open_db(false);

        if( (values.size() < 1)  || name_table.isEmpty() || check_sql_error())
            return false;

        long return_val = 0;
        if( (where_clause == null || where_args == null) && (!unique_data) ) {
            //insert data
            long ret_val = db_obj.insert(name_table, null, values);

        }else{
            boolean is_exist_data = check_exist_data(name_table, where_clause, where_args);
            if(check_sql_error()) return false;

            if(is_exist_data) {
                //update data
                long ret_val = db_obj.update(name_table, values, where_clause, where_args);
            }else{
                //insert data
                long ret_val = db_obj.insert(name_table, null, values);
            }
        }

        if(return_val < 0 || check_sql_error()) {
            return false;
        }

        return true;
    }


    private boolean check_exist_data(String name_table, String where_clause, String[] where_args) {

        Cursor cursor = select_data(name_table, null, where_clause, where_args, "1", null);

        if(cursor.getCount() > 0){
            cursor.close();
            return true;
        }else {
            cursor.close();
            return false;
        }


    }

    private Cursor select_data(String name_table, String[] name_columns, String where_clause, String[] where_args, String limit_number, String orderBy) {

        if (!db_obj.isOpen() || !db_obj.isReadOnly() )
            open_db(true);

        Cursor cursor = null;
        try{
            cursor = db_obj.query(name_table, name_columns, where_clause, where_args, null, null, orderBy  );
        }catch(SQLiteException ex){
            add_err_msg(ex.getMessage());
            cursor = null;
            close_db();
        }

        return cursor;
    }

    private void add_err_msg(String message){
        err_msg += message;
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

    public boolean save_config(String name_config, String value_config) {

        String name_table = "app_config";
        String where_clause = "name=?";
        String[] where_args = { name_config };
        ContentValues cont_values = new ContentValues();
        cont_values.put("name",name_config);
        cont_values.put("value",value_config);
        boolean return_value = false;
        clean_sql_error();

        if(name_config.isEmpty() || value_config.isEmpty()) {
            add_err_msg("Config name or value is Empty!");
            return return_value;
        }

        return_value = insert_update_data(name_table, where_clause, where_args , cont_values, true );
        close_db();

        if(check_sql_error())
            return return_value;

        return return_value;
    }

    public String load_config(String name_config) {

        Cursor cursor = null;
        String str_return = "";
        int valueColumneIndex = 0;
        String name_table = "app_config";
        String where_clause = "name=?";
        String[] name_columns = { "value" };
        String[] where_args = { name_config };

        clean_sql_error();
        cursor = select_data(name_table, name_columns, where_clause, where_args, "1", null);

        if(check_sql_error())
            return "";

        valueColumneIndex = cursor.getColumnIndex("value");
        str_return = cursor.getString(valueColumneIndex);
        //cursor.close();
        close_db();

        return str_return;
    }

    public void delete_db() {
        open_db(false);
        db_obj.execSQL(SQL_DELETE_ENTRIES);
        close_db();
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
