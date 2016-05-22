package picklenostra.user_app.model;

import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;

import picklenostra.user_app.Manifest;

/**
 * Created by Edwin on 4/30/2016.
 */
public class SearchWithMapModel implements Serializable {
    
    private int id;
    private String pictureUrl;
    private String namaBank;
    private String namaJalan;
    private double jarak;
    private double latitude;
    private double longitude;
    private Marker marker;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

}
