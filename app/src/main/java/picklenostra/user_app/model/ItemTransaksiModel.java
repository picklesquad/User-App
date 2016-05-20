package picklenostra.user_app.model;

/**
 * Created by Syukri Mullia Adil P on 5/19/2016.
 */
public class ItemTransaksiModel {

    private int idTransaksi;
    private String namaBankSampah, jumlahSampah, nominalTransaksi;
    private double sampahPlastik, sampahKertas, sampahBesi;
    private int sampahBotol;
    private long waktu;

    public double getSampahPlastik() {
        return sampahPlastik;
    }

    public void setSampahPlastik(double jumlah) {
        sampahPlastik = jumlah;
    }

    public double getSampahKertas() {
        return sampahKertas;
    }

    public void setSampahKertas(double jumlah) {
        sampahKertas = jumlah;
    }

    public double getSampahBesi() {
        return sampahBesi;
    }

    public void setSampahBesi(double jumlah) {
        sampahBesi = jumlah;
    }

    public int getSampahBotol() {
        return sampahBotol;
    }

    public void setSampahBotol(int jumlah) {
        sampahBotol = jumlah;
    }

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
