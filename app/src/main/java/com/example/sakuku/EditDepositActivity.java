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

public class EditDepositActivity extends AppCompatActivity {

    EditText etnama,etsaldo;
    Button btntambah,btnkembali;
    Context ma;
    DataHelper dbcenter;
    public static Activity EditDepositActivity;
    int idDeposit,saldoDeposit;
    String namaDeposit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_deposit);

        EditDepositActivity = this;
        ma = this;
        dbcenter = new DataHelper(this);

        etnama = (EditText) findViewById(R.id.editText1);
        etsaldo = (EditText) findViewById(R.id.editText2);
        btntambah = (Button) findViewById(R.id.button1);
        btnkembali = (Button) findViewById(R.id.button2);

        idDeposit = getIntent().getIntExtra("EXTRA_NO", 0);
        namaDeposit = getIntent().getStringExtra("EXTRA_NAMA");
        saldoDeposit = getIntent().getIntExtra("EXTRA_SALDO",0);

        etnama.setText(namaDeposit);
        etsaldo.setText(String.valueOf(saldoDeposit));

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
                    update();
                    MainActivity.MainActivity.finish();
                    finish();
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(ma, "Edit Saku Berhasil !", Toast.LENGTH_SHORT).show();
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

    public void update(){
        String nama = etnama.getText().toString();
        int saldo = Integer.valueOf(etsaldo.getText().toString());

        SQLiteDatabase db = dbcenter.getWritableDatabase();
        db.execSQL("update deposit set nama='"+
                nama +"',saldo='"+saldo+"' where id ='"+idDeposit+"'");
    }
}
