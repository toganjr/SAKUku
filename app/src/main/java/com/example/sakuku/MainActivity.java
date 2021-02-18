package com.example.sakuku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sakuku.Deposit.DataNote;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ListAdapter mListadapter;
    RecyclerView listview;
    FloatingActionButton fab_add;
    public static MainActivity ma;
    protected Cursor cursor;
    DataHelper dbcenter;
    int[] no,saldo;
    String[] detail;
    public static Activity MainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("SAKU Ku");

        listview = (RecyclerView) findViewById(R.id.list);
        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listview.setLayoutManager(layoutManager);

        MainActivity = this;
        ma = this;
        dbcenter = new DataHelper(this);
        initListView();

        fab_add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ma, TambahDepositActivity.class);
                startActivity(intent);
            }
        });
    }

    private void SelectChoice(final int idDeposit, final int position){
        final CharSequence[] items={"Edit","Hapus","Transfer", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Pilihan");

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Edit")) {
                    Edit(position);
                } else if (items[i].equals("Hapus")) {

                    Hapus(idDeposit);
                } else if (items[i].equals("Transfer")) {

                    Transfer(position);
                } else {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }


    public void Edit(int position) {
        Intent intent = new Intent(this, EditDepositActivity.class);
        intent.putExtra("EXTRA_NO", no[position]);
        intent.putExtra("EXTRA_NAMA", detail[position]);
        intent.putExtra("EXTRA_SALDO", saldo[position]);
        startActivity(intent);
    }

    public void Hapus(int ID) {
        final int id =  ID;
        new AlertDialog.Builder(ma)
                .setTitle("Hapus Saku")
                .setMessage("Apa anda yakin ingin menghapus saku ini ?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation

                        SQLiteDatabase db = dbcenter.getReadableDatabase();
                        cursor = db.rawQuery("DELETE FROM deposit WHERE id = '" +
                                id + "'",null);
                        cursor.moveToFirst();

                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                        Toast.makeText(ma, "Transaksi berhasil dihapus !", Toast.LENGTH_SHORT).show();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    public void Transfer(int position){
        Intent intent = new Intent(this, TransferDepositActivity.class);
        intent.putExtra("EXTRA_NO_DEPOSIT", no[position]);
        intent.putExtra("EXTRA_NAMA", detail[position]);
        startActivity(intent);
    }


    public void initListView(){
        SQLiteDatabase db = dbcenter.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM deposit",null);
        cursor.moveToFirst();
        no = new int[cursor.getCount()];
        detail = new String[cursor.getCount()];
        saldo = new int[cursor.getCount()];
        for (int cc=0; cc < cursor.getCount(); cc++){
            cursor.moveToPosition(cc);
            no[cc] = cursor.getInt(0);
            detail[cc] = cursor.getString(1).toString();
            saldo[cc] = cursor.getInt(2);
        }

        ArrayList data = new ArrayList<DataNote>();
        for (int i = 0; i < cursor.getCount(); i++)
        {
            data.add(
                    new DataNote
                            (
                                    no[i],
                                    saldo[i],
                                    detail[i]
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
            TextView textViewDeposit;
            TextView textViewSaldo;
            Button btnOption;
            RelativeLayout LayoutDeposit;

            public ViewHolder(View itemView)
            {
                super(itemView);
                this.textViewDeposit = (TextView) itemView.findViewById(R.id.item_title);
                this.textViewSaldo = (TextView) itemView.findViewById(R.id.item_detail);
                this.btnOption = (Button) itemView.findViewById(R.id.btn_option);
                this.LayoutDeposit = (RelativeLayout) itemView.findViewById(R.id.CardView);
            }
        }

        @Override
        public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dompet_cardview, parent, false);

            ListAdapter.ViewHolder viewHolder = new ListAdapter.ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ListAdapter.ViewHolder holder, final int position)
        {
            String saldo = NumberFormat.getNumberInstance(Locale.US).format((dataList.get(position).getNominal())); //add
            holder.textViewDeposit.setText(dataList.get(position).getDeposit());
            holder.textViewSaldo.setText(String.valueOf("Rp. "+saldo));
            if (position % 3 == 0){
                float scale = getResources().getDisplayMetrics().density;
                int pad = (int) (10*scale + 0.5f);
                holder.LayoutDeposit.setBackgroundResource(R.drawable.rounded_green);
                holder.LayoutDeposit.setPadding(pad,pad,pad,pad);

            } else if (position % 3 == 1) {
                float scale = getResources().getDisplayMetrics().density;
                int pad = (int) (10 * scale + 0.5f);
                holder.LayoutDeposit.setBackgroundResource(R.drawable.rounded_orange);
                holder.LayoutDeposit.setPadding(pad, pad, pad, pad);
            } else if (position % 3 == 2) {
                float scale = getResources().getDisplayMetrics().density;
                int pad = (int) (10 * scale + 0.5f);
                holder.LayoutDeposit.setBackgroundResource(R.drawable.rounded_blue);
                holder.LayoutDeposit.setPadding(pad, pad, pad, pad);
            } else {
                //nothing happen
            }
            holder.btnOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   SelectChoice(dataList.get(position).getNo(),position);
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(getBaseContext(), TransaksiActivity.class);
                    intent.putExtra("EXTRA_NO", dataList.get(position).getNo());
                    intent.putExtra("EXTRA_NAMA", dataList.get(position).getDeposit());
                    startActivity(intent);
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

