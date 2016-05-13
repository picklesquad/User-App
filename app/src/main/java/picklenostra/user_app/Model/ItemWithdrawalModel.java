package picklenostra.user_app.Model;

/**
 * Created by Edwin on 5/13/2016.
 */
public class ItemWithdrawalModel {
    private int id;
    private int statusInteger;
    private String namaBank, statusText;
    private int nominalWithdrawal;
    private long waktu;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamaBank() {
        return namaBank;
    }

    public void setNamaBank(String namaBank) {
        this.namaBank = namaBank;
    }

    public int getNominalWithdrawal() {
        return nominalWithdrawal;
    }

    public void setNominalWithdrawal(int nominalWithdrawal) {
        this.nominalWithdrawal = nominalWithdrawal;
    }

    public long getWaktu() {
        return waktu;
    }

    public void setWaktu(long waktu) {
        this.waktu = waktu;
    }

    public int getStatusInteger() {
        return statusInteger;
    }

    public void setStatusInteger(int statusInteger) {
        this.statusInteger = statusInteger;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }
}
