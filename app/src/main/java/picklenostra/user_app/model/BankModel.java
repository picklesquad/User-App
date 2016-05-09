package picklenostra.user_app.model;

/**
 * Created by marteinstein on 08/05/2016.
 */

public class BankModel {
    private long id;
    private String bankName;
    private double saldoInBank;
    private String urlPhotobank;

    public BankModel(long id, String bankName, double saldoInBank, String urlPhotobank) {
        this.id = id;
        this.bankName = bankName;
        this.saldoInBank = saldoInBank;
        this.urlPhotobank = urlPhotobank;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
}

