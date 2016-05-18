package picklenostra.user_app.model;

import android.view.View;
import android.widget.ProgressBar;

/**
 * Created by marteinstein on 08/05/2016.
 */

public class BankModel {
    private int id;
    private String bankName;
    private double saldoInBank;
    private String urlPhotobank;

    public BankModel(int id, String bankName, double saldoInBank, String urlPhotobank) {
        this.id = id;
        this.bankName = bankName;
        this.saldoInBank = saldoInBank;
        this.urlPhotobank = urlPhotobank;
    }

    public BankModel(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public double getSaldoInBank() {
        return saldoInBank;
    }

    public void setSaldoInBank(double saldoInBank) {
        this.saldoInBank = saldoInBank;
    }

    public String getUrlPhotobank() {
        return urlPhotobank;
    }

    public void setUrlPhotobank(String urlPhotobank) {
        this.urlPhotobank = urlPhotobank;
    }

    public String toString() {
        return bankName + " " + getSaldoInBank();
    }

}

