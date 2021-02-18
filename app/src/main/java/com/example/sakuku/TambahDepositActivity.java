package com.example.sakuku;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TambahDepositActivity extends AppCompatActivity {

    EditText etnama,etsaldo;
    Button btntambah,btnkembali;
    Context ma;
    DataHelper dbcenter;
    protected Cursor cursor;
    public static Activity TambahDepositActivity;
    int idDeposit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_deposit);

        TambahDepositActivity = this;
        ma = this;
        dbcenter = new DataHelper(this);

        etnama = (EditText) findViewById(R.id.editText1);
        etsaldo = (EditText) findViewById(R.id.editText2);
        btntambah = (Button) findViewById(R.id.button1);
        btnkembali = (Button) findViewById(R.id.button2);


        btntambah.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String nama = etnama.getText().toString();
                String saldo = etsaldo.getText().toString();
                if (nama.equalsIgnoreCase("")){
                    Toast.makeText(ma, "Silahkan Masukkan Nama Saku", Toast.LENGTH_SHORT).show();
                } else if (saldo.equalsIgnoreCase("")) {
                    Toast.makeText(ma, "Silahkan Masukkan Saldo", Toast.LENGTH_SHORT).show();
                } else {
                    tambah();
                }

            }
        });
        btnkembali.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
    }

    public void tambah(){
        String nama = etnama.getText().toString();
        int saldo = Integer.valueOf(etsaldo.getText().toString());

        SQLiteDatabase db = dbcenter.getReadableDatabase();
        cursor = db.rawQuery("INSERT INTO deposit (nama, saldo) VALUES ('"+nama+"', '"+saldo+"')",null);
        cursor.moveToFirst();

        if (saldo > 0) {
            cursor = db.rawQuery("SELECT id FROM deposit order by id DESC limit 1",null);
            cursor.moveToFirst();
            idDeposit = cursor.getInt(0);

            Calendar calendar = Calendar.getInstance();
            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);

            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            String formattedDate = df.format(c);

            cursor = db.rawQuery("INSERT INTO transaksi (nama, nominal, tanggal, tipe, deposit_id) VALUES ('Saku dibuat', '"+saldo+"', '"+formattedDate+"', '0', '"+idDeposit+"')",null);
            cursor.moveToFirst();
        }

        finish();
        overridePendingTransition(0, 0);
        Intent intent = new Intent(ma, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        Toast.makeText(ma, "Penambahan Saku Berhasil !", Toast.LENGTH_SHORT).show();
    }
}
