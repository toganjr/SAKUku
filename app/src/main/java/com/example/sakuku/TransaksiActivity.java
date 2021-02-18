package com.example.sakuku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sakuku.Transaksi.DataNote;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TransaksiActivity extends AppCompatActivity {

    ListAdapter mListadapter;
    RecyclerView listview;
    FloatingActionButton fab_add;
    public static TransaksiActivity ma;
    protected Cursor cursor;
    DataHelper dbcenter;
    int[] id,nominal,tipe,deposit_id;
    String[] detail,tanggal;
    String namaDeposit;
    int idDeposit,newSaldoDeposit;
    public static Activity TransaksiActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi);

        idDeposit = getIntent().getIntExtra("EXTRA_NO",0);
        namaDeposit = getIntent().getStringExtra("EXTRA_NAMA");

        getSupportActionBar().setTitle(namaDeposit);

        listview = (RecyclerView) findViewById(R.id.list_transaksi);
        fab_add = (FloatingActionButton) findViewById(R.id.transaksi_fab_add);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listview.setLayoutManager(layoutManager);

        TransaksiActivity = this;
        ma = this;
        dbcenter = new DataHelper(this);
        initListView(idDeposit);



        fab_add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), TambahTransaksiActivity.class);
                intent.putExtra("EXTRA_NAMA", namaDeposit);
                intent.putExtra("EXTRA_NO_DEPOSIT", idDeposit);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        MainActivity.MainActivity.finish();
        finish();
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
    }

    public void delete(int ID){
        final int id =  ID;
        new AlertDialog.Builder(ma)
                .setTitle("Hapus Transaksi")
                .setMessage("Apa anda yakin ingin menghapus transaksi ini ?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation

                        SQLiteDatabase db = dbcenter.getReadableDatabase();
                        cursor = db.rawQuery("SELECT id,nominal,tipe,transfer_id,transfer_deposit FROM transaksi WHERE id = '"+id+"'",null);
                        cursor.moveToFirst();
                        int idTransaksi = cursor.getInt(0);
                        int nominalTransaksi = cursor.getInt(1);
                        int tipeTransaksi = cursor.getInt(2);
                        int idTransfer = cursor.getInt(3);
                        int transferDeposit = cursor.getInt(4);
                        Log.d("idTransaksi", "transfer : "+String.valueOf(idTransaksi));
                        Log.d("idTransfer", "transfer : "+String.valueOf(idTransfer));
                        Log.d("transferDeposit", "transfer : "+String.valueOf(transferDeposit));

                        if (idTransfer == 0) {
                            cursor = db.rawQuery("DELETE FROM transaksi WHERE id = '" +
                                    id + "'",null);
                            cursor.moveToFirst();

                            cursor = db.rawQuery("SELECT saldo FROM deposit WHERE id = '"+idDeposit+"'",null);
                            cursor.moveToFirst();
                            int saldoDeposit = cursor.getInt(0);
                            if (tipeTransaksi == 0){
                                newSaldoDeposit = saldoDeposit - nominalTransaksi;

                            } else if (tipeTransaksi == 1){
                                newSaldoDeposit = saldoDeposit + nominalTransaksi;

                            } else {
                                // do nothing
                            }

                            db.execSQL("update deposit set saldo='"+
                                    newSaldoDeposit +"' where id ='"+idDeposit+"'");
                        } else {

                            Log.d("idTransfer2", "transfer : "+String.valueOf(idTransfer));

                            cursor = db.rawQuery("DELETE FROM transaksi WHERE id = '" +
                                    id + "'",null);
                            cursor.moveToFirst();

                            cursor = db.rawQuery("DELETE FROM transaksi WHERE id = '" +
                                    idTransfer + "'",null);
                            cursor.moveToFirst();

                            Log.d("delete : ", String.valueOf(idTransfer));

                            cursor = db.rawQuery("SELECT saldo FROM deposit WHERE id = '"+idDeposit+"'",null);
                            cursor.moveToFirst();
                            int saldoDeposit1 = cursor.getInt(0);
                            if (tipeTransaksi == 0){
                                newSaldoDeposit = saldoDeposit1 - nominalTransaksi;

                            } else if (tipeTransaksi == 1){
                                newSaldoDeposit = saldoDeposit1 + nominalTransaksi;

                            } else {
                                // do nothing
                            }

                            db.execSQL("update deposit set saldo='"+
                                    newSaldoDeposit +"' where id ='"+idDeposit+"'");

                            cursor = db.rawQuery("SELECT saldo FROM deposit WHERE id = '"+transferDeposit+"'",null);
                            cursor.moveToFirst();
                            int saldoDeposit2 = cursor.getInt(0);
                            if (tipeTransaksi == 1){
                                newSaldoDeposit = saldoDeposit2 - nominalTransaksi;

                            } else if (tipeTransaksi == 0){
                                newSaldoDeposit = saldoDeposit2 + nominalTransaksi;

                            } else {
                                // do nothing
                            }

                            db.execSQL("update deposit set saldo='"+
                                    newSaldoDeposit +"' where id ='"+transferDeposit+"'");
                        }

                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                        Toast.makeText(TransaksiActivity.this, "Transaksi berhasil dihapus !", Toast.LENGTH_SHORT).show();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .show();

    }

    public void initListView(int idTransaksi){
        SQLiteDatabase db = dbcenter.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM transaksi WHERE deposit_id = '"+idTransaksi+"' order by id DESC",null);
        cursor.moveToFirst();
        id = new int[cursor.getCount()];
        detail = new String[cursor.getCount()];
        nominal = new int[cursor.getCount()];
        tanggal = new String[cursor.getCount()];
        tipe = new int[cursor.getCount()];
        deposit_id = new int[cursor.getCount()];
        for (int cc=0; cc < cursor.getCount(); cc++){
            cursor.moveToPosition(cc);
            id[cc] = cursor.getInt(0);
            detail[cc] = cursor.getString(1).toString();
            nominal[cc] = cursor.getInt(2);
            tanggal[cc] = cursor.getString(3);
            tipe[cc] = cursor.getInt(4);
            deposit_id[cc] = cursor.getInt(5);
        }

        ArrayList data = new ArrayList<DataNote>();
        for (int i = 0; i < cursor.getCount(); i++)
        {
            data.add(
                    new DataNote
                            (
                                    id[i],
                                    nominal[i],
                                    detail[i],
                                    tanggal[i],
                                    tipe[i],
                                    deposit_id[i]
                            ));
        }

        mListadapter = new ListAdapter(data);
        listview.setAdapter(mListadapter);


    }

    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>
    {
        private ArrayList<DataNote> dataList;

        public ListAdapter(ArrayList<DataNote> data)
        {
            this.dataList = data;
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            TextView textViewCatatan;
            TextView textViewSaldo;
            TextView textViewTanggal;
            RelativeLayout layoutTransaksi;
            Button btnDelete;

            public ViewHolder(View itemView)
            {
                super(itemView);
                this.textViewCatatan = (TextView) itemView.findViewById(R.id.transaksi_detail);
                this.textViewSaldo = (TextView) itemView.findViewById(R.id.transaksi_title);
                this.textViewTanggal = (TextView) itemView.findViewById(R.id.transaksi_tanggal);
                this.btnDelete = (Button) itemView.findViewById(R.id.btn_delete);
                this.layoutTransaksi = (RelativeLayout) itemView.findViewById(R.id.CardViewTransaksi);
            }
        }

        @Override
        public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaksi_cardview, parent, false);

            ListAdapter.ViewHolder viewHolder = new ListAdapter.ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ListAdapter.ViewHolder holder, final int position)
        {
            String saldo = NumberFormat.getNumberInstance(Locale.US).format((dataList.get(position).getNominal())); //add
            if (dataList.get(position).getTipe() == 0){
                float scale = getResources().getDisplayMetrics().density;
                int pad = (int) (5*scale + 0.5f);
                holder.layoutTransaksi.setBackgroundResource(R.drawable.rounded_green);
                holder.layoutTransaksi.setPadding(pad,pad,pad,pad);
                holder.textViewSaldo.setText(String.valueOf("+ Rp. "+saldo));

            } else if (dataList.get(position).getTipe() == 1){
                float scale = getResources().getDisplayMetrics().density;
                int pad = (int) (5*scale + 0.5f);
                holder.layoutTransaksi.setBackgroundResource(R.drawable.rounded_redlight);
                holder.layoutTransaksi.setPadding(pad,pad,pad,pad);
                holder.textViewSaldo.setText(String.valueOf("- Rp. "+saldo));
            } else {
                //nothing happen
            }
            holder.textViewCatatan.setText(dataList.get(position).getCatatan());
            holder.textViewTanggal.setText(dataList.get(position).getTanggal());
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
 //
                                       delete(dataList.get(position).getNo());
                }
            });

        }

        @Override
        public int getItemCount()
        {
            return dataList.size();
        }
    }
}
