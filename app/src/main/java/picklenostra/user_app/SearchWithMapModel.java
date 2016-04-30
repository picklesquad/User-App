package picklenostra.user_app;

import java.io.Serializable;

/**
 * Created by Edwin on 4/30/2016.
 */
public class SearchWithMapModel implements Serializable {
    
    private String pictureUrl;
    private String namaBank;
    private String namaJalan;
    private double jarak;


    public String getpictureUrl() {
        return pictureUrl;
    }

    public void setpictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }
    
    public String getNamaBank() {
        return namaBank;
    }

    public void setNamaBank(String namaBank) {
        this.namaBank = namaBank;
    }

    public String getNamaJalan() {
        return namaJalan;
    }

    public void setNamaJalan(String namaJalan) {
        this.namaJalan = namaJalan;
    }

    public double getJarak() {
        return jarak;
    }

    public void setJarak(double jarak) {
        this.jarak = jarak;
    }
}
