package ua.sunstones.sunstones_accounts_2;






import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataBase extends SQLiteOpenHelper {

    private static DataBase sInstance;
    private static final String DATABASE_NAME           = "mobile_data_1c_accounts";
    private static final String TABLE_ACCOUNTS          = "accounts";
    private static final String TABLE_ACCOUNT_HISTORY   = "account_history";
    private static final String TABLE_SESSION           = "session_var";
    private static final int DATABASE_VERSION           = 3;
//    private static SQLiteDatabase db;

    SQLiteDatabase db;

    public static DataBase getInstance(Context context) {

        if (sInstance == null) {
            // sInstance = new Data(MyApplication.getContext());
            sInstance = new DataBase(context);
        }
        return sInstance;
    }

    private DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        super(this.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        this.createTableAccounts(db);
        this.createTableSessionVar(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSION);
            onCreate(db);
        }

    }

    private void createTableAccounts(SQLiteDatabase db){

        db.execSQL("create table " + TABLE_ACCOUNTS + " ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "account_number text,"
                + "sum_turnover integer,"
                + "sum_plus DECIMAL(10,2),"
                + "sum_minus DECIMAL(10,2)" + ");");

    }

    private void createTableAccountHistory(SQLiteDatabase db){

        db.execSQL("create table " + TABLE_ACCOUNTS + " ("
                + "id integer primary key autoincrement,"
                + "date_time date,"
                + "account_number text,"
                + "sum DECIMAL(10,2)" + ");");

    }

    private void createTableSessionVar(SQLiteDatabase db){

        db.execSQL("create table " + TABLE_SESSION + " ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "value text"
                + ");");

    }


    ///////////////////////////////////////////////////////////////////////////////////
    //Session

    public void setCredential(String credential){
        setSessionVariable("credential", credential);
    }

    public String getCredential(){
        return getSessionVariable("credential");
    }


    public void setLogin(String login){
        setSessionVariable("login", login);
    }

    public String getLogin(){
        return getSessionVariable("login");
    }


    public void setPassword(String password){
        setSessionVariable("password", password);
    }

    public String getPassword(){
        return getSessionVariable("password");
    }


    public void setAuthorizationStatus(String authorization_status){
        setSessionVariable("authorization_status", authorization_status);
    }

    public String getAuthorizationStatus(){
        return getSessionVariable("authorization_status");
    }


    public String getSessionVariable(String name){

        String value  = "";
        Cursor userCursor = db.rawQuery("select value from " + TABLE_SESSION + " where  name = ?", new String[]{name});
        if (userCursor.moveToFirst()) {
            value  = userCursor.getString(0);
        }

        return value;

    }

    public void setSessionVariable(String name, String value){

        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("value", value);

        Cursor userCursor = db.rawQuery("select * from " + TABLE_SESSION + " where  name = ?", new String[]{name});
        if (userCursor.moveToFirst()) {
            db.update(TABLE_SESSION, cv, "name= ?", new String[]{name});
            Log.d("myLog", "setSessionVariable update " + name);
        } else {
            db.insert(TABLE_SESSION, null, cv);
            Log.d("myLog", "setSessionVariable insert " + name);
        }

    }





    ///////////////////////////////////////////////////////////////////////////////////
    //Accounts

    public void add_account(Account account){

        ContentValues cv = new ContentValues();
        cv.put("name", account.name);
        cv.put("account_number", account.account_number);
        cv.put("sum_turnover", account.sum_turnover);
        cv.put("sum_plus", account.sum_plus);
        cv.put("sum_minus", account.sum_minus);

        Cursor userCursor = db.rawQuery("select * from " + TABLE_ACCOUNTS + " where  account_number = ?", new String[]{account.account_number});
        if (userCursor.moveToFirst()) {
            db.update(TABLE_ACCOUNTS, cv, "account_number= ?", new String[]{account.account_number});
        } else {
            db.insert(TABLE_ACCOUNTS, null, cv);
        }

    }

    public List<Account> get_accounts(){

        Cursor c = db.rawQuery("SELECT name, account_number, sum_turnover, sum_plus, sum_minus, id  FROM " + TABLE_ACCOUNTS, null);
        List<Account> list = new ArrayList<>();

        int counter = 1;
        // iterate over the result set, adding every table name to a list
        while (c.moveToNext()) {

            Account acc = new Account();
            acc.name            = c.getString(0);
            acc.account_number  = c.getString(1);
            acc.sum_turnover    = c.getDouble(2);
            acc.sum_plus        = c.getDouble(3);
            acc.sum_minus       = c.getDouble(4);

            list.add(acc);
        }

        return list;
    }

    public Account get_account(String account_number){

        Cursor c = db.rawQuery("SELECT name, account_number, sum_turnover, sum_plus, sum_minus, id  FROM " + TABLE_ACCOUNTS + " WHERE account_number=? ", new String[]{String.valueOf(account_number)});

        if (c.moveToNext()) {

            Account acc = new Account();
            acc.name            = c.getString(0);
            acc.account_number  = c.getString(1);
            acc.sum_turnover    = c.getDouble(2);
            acc.sum_plus        = c.getDouble(3);
            acc.sum_minus       = c.getDouble(4);
            return acc;
        }

        return null;

    }


}




