package com.example.sakuku;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "Data.db";
    private static final int DATABASE_VERSION = 1;

    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String sql = "create table deposit(" +
                "id integer primary key autoincrement," +
                "nama text, " +
                "saldo integer null);";
        Log.d("Data", "onCreate: " + sql);
        db.execSQL(sql);
        String sql2 = "create table transaksi(" +
                "id integer primary key autoincrement," +
                "nama text, " +
                "nominal integer null, " +
                "tanggal DATE, " +
                "tipe integer, " +
                "transfer_id integer , " +
                "transfer_deposit integer , " +
                "deposit_id integer, " +
                "CONSTRAINT fk_deposit" +
                "    FOREIGN KEY (deposit_id)" +
                "    REFERENCES deposit(id)" +
                "    ON DELETE CASCADE);";
        Log.d("Data", "onCreate: " + sql2);
        db.execSQL(sql2);
        sql = "INSERT INTO deposit (nama, saldo) VALUES ('BANK', '0');";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
    }
}