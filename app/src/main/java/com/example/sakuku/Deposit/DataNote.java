package com.example.sakuku.Deposit;

public class DataNote {
    int nominal,no;
    String deposit;

    public DataNote(int no,int nominal, String deposit) {
        this.no = no;
        this.nominal = nominal;
        this.deposit = deposit;
    }

    public int getNo() { return no;}

    public int getNominal() { return nominal;}

    public String getDeposit() {
        return deposit;
    }



}
