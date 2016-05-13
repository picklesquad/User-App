package picklenostra.user_app.Model;

/**
 * Created by Edwin on 5/10/2016.
 */
public class ItemTransaksiModel {

    private int idTransaksi;
    private String namaBankSampah, jumlahSampah, nominalTransaksi;
    private long waktu;

    public int getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(int idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public int getStatusTransaksi() {
        return statusTransaksi;
    }

    public void setStatusTransaksi(int statusTransaksi) {
        this.statusTransaksi = statusTransaksi;
    }

    private int statusTransaksi;

    public String getNamaBankSampah() {
        return namaBankSampah;
    }

    public void setNamaBankSampah(String namaBankSampah) {
        this.namaBankSampah = namaBankSampah;
    }

    public String getJumlahSampah() {
        return jumlahSampah;
    }

    public void setJumlahSampah(String jumlahSampah) {
        this.jumlahSampah = jumlahSampah;
    }

    public String getNominalTransaksi() {
        return nominalTransaksi;
    }

    public void setNominalTransaksi(String nominalTransaksi) {
        this.nominalTransaksi = nominalTransaksi;
    }

    public long getWaktu() {
        return waktu;
    }

    public void setWaktu(long waktu) {
        this.waktu = waktu;
    }
}
