package com.example.sakuku;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TambahTransaksiActivity extends AppCompatActivity {

    Spinner spinner_tipe;
    Button btnTambah, btnKembali;
    EditText etNama,etNominal;
    Context ma;
    DataHelper dbcenter;
    protected Cursor cursor;
    int tipe,idDeposit,saldoDeposit;
    private Calendar calendar;
    public static Activity TambahTransaksiActivity;
    String namaDeposit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_transaksi);



        TambahTransaksiActivity = this;
        ma = this;
        dbcenter = new DataHelper(this);

        idDeposit = getIntent().getIntExtra("EXTRA_NO_DEPOSIT",0);
        namaDeposit = getIntent().getStringExtra("EXTRA_NAMA");

        getSupportActionBar().setTitle(namaDeposit);

        etNama = (EditText) findViewById(R.id.editText1);
        etNominal = (EditText) findViewById(R.id.editText2);
        btnTambah = (Button) findViewById(R.id.button1);
        btnKembali = (Button) findViewById(R.id.button2);
        spinner_tipe = findViewById(R.id.spinner1);
        String[] items = new String[]{"Pilih Jenis Transaksi","Debet", "Kredit"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner_tipe.setAdapter(adapter);


        spinner_tipe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                tipe = position-1;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // nothing happen
            }
        });

        btnTambah.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String nama = etNama.getText().toString();
                String nominal = etNominal.getText().toString();

                if (nama.equalsIgnoreCase("")){
                    Toast.makeText(ma, "Silahkan Masukkan Nama Tranaksi", Toast.LENGTH_SHORT).show();
                } else if (nominal.equalsIgnoreCase("")) {
                    Toast.makeText(ma, "Silahkan Masukkan Nominal", Toast.LENGTH_SHORT).show();
                } else {

                    if (tipe == 0){
                        debet(idDeposit);
                        TransaksiActivity.TransaksiActivity.finish();
                        finish();
                        Intent intent = new Intent(getBaseContext(), TransaksiActivity.class);
                        intent.putExtra("EXTRA_NO", idDeposit);
                        startActivity(intent);
                        Toast.makeText(ma, "Transaksi Debet Berhasil !", Toast.LENGTH_SHORT).show();
                    } else if (tipe == 1){
                        kredit();
                        TransaksiActivity.TransaksiActivity.finish();
                        finish();
                        Intent intent = new Intent(getBaseContext(), TransaksiActivity.class);
                        intent.putExtra("EXTRA_NO", idDeposit);
                        startActivity(intent);
                        Toast.makeText(ma, "Transaksi Kredit Berhasil !", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ma, "Silahkan Masukkan Tipe Transaksi", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        btnKembali.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void debet(int idDeposit){
        String nama = etNama.getText().toString();
        int nominal = Integer.valueOf(etNominal.getText().toString());

        SQLiteDatabase db = dbcenter.getReadableDatabase();
        cursor = db.rawQuery("SELECT saldo FROM deposit WHERE id = '"+idDeposit+"'",null);
        cursor.moveToFirst();
        saldoDeposit = cursor.getInt(0);
        int newSaldoDeposit = saldoDeposit + nominal;

        db.execSQL("update deposit set saldo='"+
                newSaldoDeposit +"' where id ='"+idDeposit+"'");

        calendar = Calendar.getInstance();
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);



        cursor = db.rawQuery("INSERT INTO transaksi (nama, nominal, tanggal, tipe, deposit_id) VALUES ('"+nama+"', '"+nominal+"', '"+formattedDate+"', '0', '"+idDeposit+"')",null);
        cursor.moveToFirst();

    }

    public void kredit(){
        String nama = etNama.getText().toString();
        int nominal = Integer.valueOf(etNominal.getText().toString());

        SQLiteDatabase db = dbcenter.getReadableDatabase();
        cursor = db.rawQuery("SELECT saldo FROM deposit WHERE id = '"+idDeposit+"'",null);
        cursor.moveToFirst();
        saldoDeposit = cursor.getInt(0);
        int newSaldoDeposit = saldoDeposit - nominal;


        db.execSQL("update deposit set saldo='"+
                newSaldoDeposit +"' where id ='"+idDeposit+"'");

        calendar = Calendar.getInstance();
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);


        cursor = db.rawQuery("INSERT INTO transaksi (nama, nominal, tanggal, tipe, deposit_id) VALUES ('"+nama+"', '"+nominal+"', '"+formattedDate+"', '1', '"+idDeposit+"')",null);
        cursor.moveToFirst();

    }
}
