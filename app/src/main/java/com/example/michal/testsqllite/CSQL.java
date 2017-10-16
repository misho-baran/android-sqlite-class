package com.example.michal.testsqllite;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import static android.R.attr.name;

/**
 * Created by michal on 18.8.2017.
 */

public class CSQL extends SQLiteOpenHelper {

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private String err_msg = "";
    private long counter_row = -1;
    private SQLiteDatabase db_obj = null;
    private static final int DATABASE_VERSION = 8;
    private static final String DATABASE_NAME = "TestSQLite.db";
    private Cursor cursor = null;
    private Cipher cipher = null;

    private static final String[] SQL_CREATE_TABLES = {
            "CREATE TABLE IF NOT EXISTS app_config (id_config INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,value TEXT);",
            "CREATE TABLE IF NOT EXISTS app_users (id_user INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,pass TEXT);"
    };

    private static final String[] SQL_DELETE_TABLES = {
            "DROP TABLE IF EXISTS app_config;",
            "DROP TABLE IF EXISTS app_users;",
    };

    //constructor
    public CSQL(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        init_crypt();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        for(String sql_table: SQL_CREATE_TABLES) {
            sqLiteDatabase.execSQL(sql_table);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        for(String sql_table: SQL_DELETE_TABLES) {
            sqLiteDatabase.execSQL(sql_table);
        }
        onCreate(sqLiteDatabase);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private boolean init_crypt(){

        boolean ret = false;

        String Key = "SuPeR753SeCrEtE951PaSsWoRd";
        byte[] KeyData = Key.getBytes();
        SecretKeySpec KS = new SecretKeySpec(KeyData, "Blowfish");

        try {
            cipher = Cipher.getInstance("Blowfish");
            cipher.init(Cipher.ENCRYPT_MODE, KS);
            KeyData  = null;
            Key = "";
            ret = true;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            ret = false;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            ret = false;
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            ret = false;
        }

        return ret;
    }

    private String crypt_data(String str_data) {
        byte[] encodedPasswdBytes = null;
        try {
            encodedPasswdBytes = cipher.doFinal(str_data.getBytes());
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return bytesToHex(encodedPasswdBytes);
    }

    private boolean open_db(boolean readOnly) {

        try {

            if((readOnly) && (db_obj == null)) {
                db_obj = this.getReadableDatabase();
                return true;
            }

            if((!readOnly) && (db_obj == null)) {
                db_obj = this.getWritableDatabase();
                return true;
            }

            if((readOnly) && ((!db_obj.isOpen()) || (!db_obj.isReadOnly())) ){
                close_db();
                db_obj = this.getReadableDatabase();
                return true;
            }

            if((!readOnly) && ((!db_obj.isOpen()) || (db_obj.isReadOnly())) ){
                close_db();
                db_obj = this.getWritableDatabase();
                return true;
            }

        }catch (SQLiteException ex){
            add_err_msg(ex.getMessage());
            return false;
        }

        if(db_obj.isOpen()){
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

    public void create_db() {
        open_db(false);
        for(String sql_table: SQL_CREATE_TABLES) {
            db_obj.execSQL(sql_table);
        }
        close_db();
    }

    public void delete_db() {
        open_db(false);
        for(String sql_table: SQL_DELETE_TABLES)
            db_obj.execSQL(sql_table);
        close_db();
    }

    private boolean delete_data(String name_table, String where_clause, String[] where_args) {

        try{
            db_obj.delete(name_table, where_clause, where_args);
        }catch(SQLiteException ex){
            add_err_msg(ex.getMessage());
            return false;
        }

        return true;
    }

    private boolean insert_update_data(String name_table, ContentValues values) {
        return insert_update_data( name_table, null, null, values);
    }

    private boolean insert_update_data(String name_table, String where_clause,
                                       String[] where_args, ContentValues values) {

        try {
            if( (where_clause == null || where_args == null) || (where_clause.isEmpty() || where_args.length < 1) ) {
                //insert data
                db_obj.insert(name_table, null, values);

            }else{

                //check exist data
                boolean is_exist_data = check_data_exist(name_table, where_clause, where_args);
                if(check_sql_error()) return false;

                if(is_exist_data) {
                    //update data
                    db_obj.update(name_table, values, where_clause, where_args);
                }else{
                    //insert data
                    db_obj.insert(name_table, null, values);
                }
            }
        }catch(SQLiteException ex){
            add_err_msg(ex.getMessage());
            return false;
        }

        return true;
    }


    private boolean select_data(String name_table, String[] name_columns,
                               String where_clause, String[] where_args, String limit_row) {
        counter_row = -1;

        try{
            cursor = db_obj.query(name_table, name_columns, where_clause, where_args, null, null, null, limit_row);
            counter_row = cursor.getCount();
            if(counter_row > 0) {cursor.moveToFirst();}
            return true;
        }catch(SQLiteException ex){
            add_err_msg(ex.getMessage());
            cursor = null;
            return false;
        }
    }

    private String select_get_row_data(String name_column, boolean next_row) {

        try {
            if(cursor == null) return "";
            if(next_row){cursor.moveToNext();}
            int valueColumneIndex = cursor.getColumnIndex(name_column);
            return cursor.getString(valueColumneIndex);
        }catch(SQLiteException ex){
            add_err_msg(ex.getMessage());
            cursor = null;
            return "";
        }
    }

    public long get_count_row(){
        return counter_row;
    }

    public boolean check_data_exist(String name_table, String where_clause, String[] where_args) {

        select_data(name_table, null, where_clause, where_args, "1");
        if(cursor == null ){
            add_err_msg("Check exist data: Cursor is null.");
            return false;
        }else{
            cursor.close();
        }

        if(get_count_row() > 0) {
            return true;
        }else{
            return false;
        }
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

    public boolean config_save(String name_config, String value_config) {
        String name_table = "app_config";
        String where_clause = "name = ?";
        String[] where_args = { name_config };
        ContentValues cont_values = new ContentValues();
        boolean return_value = false;
        clean_sql_error();

        if(!open_db(false)){
            add_err_msg("Database is not open.");
            return return_value;
        }

        cont_values.put("name",name_config);
        cont_values.put("value",value_config);
        return_value = insert_update_data(name_table, where_clause, where_args , cont_values );
        close_db();

        if(check_sql_error())
            return return_value;

        return return_value;
    }

    public String config_load(String name_config) {
        String str_return = "";
        String name_table = "app_config";
        String where_clause = "name = ?";
        String[] name_columns = { "value" };
        String[] where_args = { name_config };
        clean_sql_error();

        if(!open_db(true)) {
            add_err_msg("Database is not open.");
            return str_return;
        }

        boolean exist_data = select_data(name_table, name_columns, where_clause, where_args, "1");

        if(exist_data && get_count_row() > 0) {
            str_return = select_get_row_data("value", false);
            cursor.close();
        }

        if(check_sql_error()) {
            add_err_msg("Config load: select data method return error.");
            return "";
        }

        close_db();

        return str_return;
    }

    public void config_remove(String name_config){

        String name_table = "app_config";
        String where_clause = "name = ?";
        String[] where_args = { name_config };
        open_db(false);
        delete_data(name_table, where_clause, where_args);
        close_db();
    }


    public boolean user_add(String name, String passwd){
        String name_table = "app_users";
        String where_clause = "name = ?";
        String[] where_args = { name };
        ContentValues cont_values = new ContentValues();
        cont_values.put("name", name);
        cont_values.put("pass", crypt_data(passwd));
        clean_sql_error();

        if(user_check_exist(name)) {
            add_err_msg("Error add user: User name exist.");
            return false;
        }

        if(!open_db(false)){
            add_err_msg("Database is not open.");
            return false;
        }

        insert_update_data(name_table, where_clause, where_args , cont_values );
        close_db();

        if(check_sql_error()  ) {
            return false;
        }

        return true;
    }


    public boolean user_check_exist(String name) {
        String str_return = "";
        int valueColumneIndex = 0;
        String name_table = "app_users";
        String where_clause = "name = ?";
        String[] name_columns = { "name" };
        String[] where_args = { name };
        clean_sql_error();

        if(!open_db(true)){
            add_err_msg("Database is not open.");
            return false;
        }

        boolean exist_data = select_data(name_table, name_columns, where_clause, where_args, "1");

        if(exist_data && get_count_row() > 0) {
            str_return = select_get_row_data("name", false);
            cursor.close();
        }

        if(check_sql_error()  ) {
            add_err_msg("User check exist: select data method return error.");
            return false;
        }
        close_db();

        if(str_return.equals(name)){
            return true;
        }

        return false;
    }


    public boolean user_check_passwd(String name, String passwd) {
        String str_return_name = "";
        String str_return_pass = "";
        String crypt_pass = crypt_data(passwd);
        String name_table = "app_users";
        String where_clause = "name = ?";
        String[] name_columns = {"name, pass"};
        String[] where_args = {name};
        clean_sql_error();

        if(!open_db(true)){
            add_err_msg("Database is not open.");
            return false;
        }

        boolean exist_data = select_data(name_table, name_columns, where_clause, where_args, "1");

        if(exist_data && get_count_row() > 0) {
            str_return_name = select_get_row_data("name", false);
            str_return_pass = select_get_row_data("pass", false);
            cursor.close();
        }

        if(check_sql_error() ) {
            str_return_name = "";
            str_return_pass = "";
            return false;
        }
        close_db();

        if(str_return_name.equals(name) && str_return_pass.equals(crypt_pass)){
            str_return_name = "";
            str_return_pass = "";
            return true;
        }

        str_return_name = "";
        str_return_pass = "";
        return false;
    }

    public void user_remove(String name_user){
        String name_table = "app_users";
        String where_clause = "name = ?";
        String[] where_args = { name_user };
        open_db(false);
        delete_data(name_table, where_clause, where_args);
        close_db();
    }

}
