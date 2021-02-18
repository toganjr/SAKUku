package com.example.sakuku;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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

public class TransferDepositActivity extends AppCompatActivity {

    Spinner spinner_tipe;
    Button btnTransfer, btnKembali;
    EditText etNama,etNominal;
    Context ma;
    DataHelper dbcenter;
    protected Cursor cursor;
    int posisi,idDeposit;
    private Calendar calendar;
    public static Activity TransferDepositActivity;
    String namaDeposit;
    int[] id,saldo;
    String[] nama;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_deposit);

        TransferDepositActivity = this;
        ma = this;
        dbcenter = new DataHelper(this);

        idDeposit = getIntent().getIntExtra("EXTRA_NO_DEPOSIT",0);
        namaDeposit = getIntent().getStringExtra("EXTRA_NAMA");

        getSupportActionBar().setTitle(namaDeposit);

        etNama = (EditText) findViewById(R.id.editText1);
        etNominal = (EditText) findViewById(R.id.editText2);
        btnTransfer = (Button) findViewById(R.id.button1);
        btnKembali = (Button) findViewById(R.id.button2);
        spinner_tipe = findViewById(R.id.spinner1);

        getSpinnerList(idDeposit);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, nama);
        spinner_tipe.setAdapter(adapter);


        spinner_tipe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                posisi = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // nothing happen
            }
        });

        btnTransfer.setOnClickListener(new View.OnClickListener()
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
                    if (posisi >= 0){
                        transfer(posisi,idDeposit);
                        MainActivity.MainActivity.finish();
                        finish();
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(ma, "Transfer Berhasil !", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ma, "Silahkan Masukkan Tujuan Transfer", Toast.LENGTH_SHORT).show();
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

    public void getSpinnerList(int ID){
        SQLiteDatabase db = dbcenter.getReadableDatabase();

        cursor = db.rawQuery("SELECT id, nama, saldo FROM deposit " +
                "EXCEPT " +
                "SELECT id, nama, saldo FROM deposit WHERE id = '"+ID+"'",null);
        cursor.moveToFirst();
        id = new int[cursor.getCount()+1];
        nama = new String[cursor.getCount()+1];
        saldo = new int[cursor.getCount()+1];
        nama[0] = "Pilih Tujuan Transfer";
        for (int i = 1; i <= cursor.getCount(); i++){
            cursor.moveToPosition(i-1);
            id[i] = cursor.getInt(0);
            nama[i] = cursor.getString(1);
            saldo[i] = cursor.getInt(2);
            Log.d("nama ", "getSpinnerList: "+nama[i]);
        }
    }

    public void transfer(int ID, int idDeposit){
        SQLiteDatabase db = dbcenter.getReadableDatabase();
        String nama = etNama.getText().toString();
        int nominal = Integer.valueOf(etNominal.getText().toString());

        cursor = db.rawQuery("SELECT id, saldo FROM deposit WHERE id = '"+idDeposit+"'",null);
        cursor.moveToFirst();

        int idDepositAsal = cursor.getInt(0);
        int saldoDepositAsal = cursor.getInt(1);

        int newSaldoDepositAsal = saldoDepositAsal - nominal;
        int newSaldoDepositTujuan = saldo[ID] + nominal;

        db.execSQL("update deposit set saldo='"+
                newSaldoDepositAsal +"' where id ='"+idDepositAsal+"'");

        db.execSQL("update deposit set saldo='"+
                newSaldoDepositTujuan +"' where id ='"+id[ID]+"'");

        calendar = Calendar.getInstance();
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);


        cursor = db.rawQuery("INSERT INTO transaksi (nama, nominal, tanggal, tipe, deposit_id, transfer_deposit) VALUES ('"+nama+"', '"+nominal+"', '"+formattedDate+"', '1', '"+idDepositAsal+"', '"+id[ID]+"')",null);
        cursor.moveToFirst();

        cursor = db.rawQuery("SELECT id from transaksi ORDER BY id DESC limit 1",null);
        cursor.moveToFirst();

        Integer lastidTransaksi;
        if (cursor.getCount() != 0) {
            lastidTransaksi = cursor.getInt(0);
        } else {
            lastidTransaksi = 0;
        }

        int tujuanlastidTransaksi = lastidTransaksi + 1;

        db.execSQL("update transaksi set transfer_id='"+
                tujuanlastidTransaksi +"' where id ='"+lastidTransaksi+"'");

        cursor = db.rawQuery("INSERT INTO transaksi (nama, nominal, tanggal, tipe, deposit_id, transfer_deposit) VALUES ('"+nama+"', '"+nominal+"', '"+formattedDate+"', '0', '"+id[ID]+"', '"+idDepositAsal+"')",null);
        cursor.moveToFirst();

        cursor = db.rawQuery("SELECT id from transaksi ORDER BY id DESC limit 1",null);
        cursor.moveToFirst();

        Integer lastidTransaksi2;
        if (cursor.getCount() != 0) {
            lastidTransaksi2 = cursor.getInt(0);
        } else {
            lastidTransaksi2 = 0;
        }

        int tujuanlastidTransaksi2 = lastidTransaksi2 - 1;

        db.execSQL("update transaksi set transfer_id='"+
                tujuanlastidTransaksi2 +"' where id ='"+lastidTransaksi2+"'");

        Log.d("Count -", "transfer: "+String.valueOf(cursor.getCount()));
        Log.d("ID -", "transfer: "+String.valueOf(lastidTransaksi));
        Log.d("tujuan -", "transfer: "+String.valueOf(tujuanlastidTransaksi));
        Log.d("asal -", "transfer: "+String.valueOf(tujuanlastidTransaksi2));
    }
}
