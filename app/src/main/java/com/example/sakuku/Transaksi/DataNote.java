package com.example.sakuku.Transaksi;

public class DataNote {
    int nominal,no,tipe,deposit_no;
    String catatan,tanggal;

    public DataNote(int no,int nominal, String catatan, String tanggal, int tipe, int deposit_no) {
        this.no = no;
        this.nominal = nominal;
        this.catatan = catatan;
        this.tanggal = tanggal;
        this.tipe = tipe;
        this.deposit_no = deposit_no;
    }

    public int getNo() { return no;}

    public int getNominal() { return nominal;}

    public String getCatatan() {
        return catatan;
    }

    public String getTanggal() { return tanggal;}

    public int getTipe() { return tipe;}

    public int getDeposit_no() { return deposit_no; }
}
