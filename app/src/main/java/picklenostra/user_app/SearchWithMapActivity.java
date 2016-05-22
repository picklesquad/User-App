package picklenostra.user_app;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import picklenostra.user_app.adapter.SearchWithMapAdapter;
import picklenostra.user_app.helper.VolleyController;
import picklenostra.user_app.model.ItemLanggananModel;
import picklenostra.user_app.model.SearchWithMapModel;

public class SearchWithMapActivity extends ActionBarActivity implements
        LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener ,
        GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;
    private Location location;
    private LocationRequest locationRequest;
    private ProgressBar progressBar;
    private SearchWithMapAdapter adapter;
    private ListView listView;

    private ArrayList<SearchWithMapModel> listBankSampah;
    private HashMap<LatLng, SearchWithMapModel> markerData;

    private double curLat, curLong;
    private final int INTERVAL = 1000 * 200;
    private String URL = "";
    private final int EARTH_RADIUS = 6371;
    private final double CAM_ADJUST = 0.0032;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_with_map);
        getSupportActionBar().setTitle("Cari Bank Sampah");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initialize
        listView = (ListView) findViewById(R.id.searchwithmap_listview);
        progressBar = (ProgressBar) findViewById(R.id.search_loading);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFF80CBC4, android.graphics.PorterDuff.Mode.SRC_ATOP);

        listBankSampah = new ArrayList<>();
        markerData = new HashMap<>();

        adapter = new SearchWithMapAdapter(this, listBankSampah);
        URL = getResources().getString(R.string.API_URL) + "/search?query=";

        buildGoogleApiClient();
        initalizeMap();

        SharedPreferences shared = getSharedPreferences(getResources().getString(R.string.KEY_SHARED_PREF), MODE_PRIVATE);
        String token = shared.getString("token", "");
        int idUser = shared.getInt("idUser", 0);
        volleyRequest(token, idUser);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.e("tes", "tessss");
//                Toast.makeText(SearchWithMapActivity.this, "bank " + ((SearchWithMapModel) adapter.getItem(position)).getNamaBank(), Toast.LENGTH_SHORT).show();
                SearchWithMapModel model = (SearchWithMapModel) adapter.getItem(position);
//                Intent in = new Intent(SearchWithMapActivity.this, BankSampahDetailsActivity.class);
//                in.putExtra("idBank", model.getId());
                setPositionToCamera(model.getLatitude() + CAM_ADJUST, model.getLongitude());
                Log.e("modellat", model.getLatitude() + "");
                Log.e("modellong", model.getLongitude() + "");
                Marker marker = markerData.get(new LatLng(model.getLatitude(), model.getLongitude())).getMarker();
                marker.showInfoWindow();
//                onInfoWindowClick(markerData.get(new LatLng(model.getLatitude(), model.getLongitude())).getMarker());
//                startActivity(in);
            }
        });
        setPositionToCamera(curLat, curLong);
    }

    synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void initalizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setPositionToCamera(double latitude, double longitude){
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(14).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void setMarker(double latitude, double longitude, String namaBank, String alamatBank, SearchWithMapModel model) {
        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude))
                .title(namaBank)
                .snippet(alamatBank)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bank_marker));
        Marker added = googleMap.addMarker(marker);
        LatLng latLng = new LatLng(latitude, longitude);
        model.setMarker(added);
        markerData.put(latLng, model);
        ;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("LOCATION", "Location services connected");
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if(location!=null){
            curLat = location.getLatitude();
            curLong = location.getLongitude();
            setPositionToCamera(curLat, curLong);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("LOCATION", "Location services suspended");
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null) {
            curLat = location.getLatitude();
            curLong = location.getLongitude();
            setPositionToCamera(curLat + CAM_ADJUST, curLong);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("LOCATION", "Location services failed, check your internet connection");
    }

    private void volleyRequest(final String token, final int idUser){
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray banks = responseObject.getJSONArray("data");
                    for(int i = 0; i < banks.length(); i++){
                        JSONObject bank = (JSONObject) banks.get(i);
                        int idBank = bank.getInt("idBank");
                        String namaBank = bank.getString("namaBank");
                        String alamatBank = bank.getString("locationName");
                        double latitude = bank.getDouble("locationLat");
                        double longitude = bank.getDouble("locationLng");
                        double jarak = distance(curLat,curLong,latitude,longitude);

                        //Create Model
                        SearchWithMapModel searchWithMapModel = new SearchWithMapModel();
                        searchWithMapModel.setId(idBank);
                        searchWithMapModel.setNamaBank(namaBank);
                        searchWithMapModel.setNamaJalan(alamatBank);
                        searchWithMapModel.setJarak(jarak);
                        searchWithMapModel.setpictureUrl("https://avatars0.githubusercontent.com/u/10989633?v=3&s=400");
                        searchWithMapModel.setLatitude(latitude);
                        searchWithMapModel.setLongitude(longitude);

                        //SetMarker
                        setMarker(latitude, longitude, namaBank, alamatBank, searchWithMapModel);

                        listBankSampah.add(searchWithMapModel);
                        adapter.notifyDataSetChanged();
                    }
                    Collections.sort(listBankSampah, new Comparator<SearchWithMapModel>() {
                        @Override
                        public int compare(SearchWithMapModel lhs, SearchWithMapModel rhs) {
                            double l = lhs.getJarak();
                            double r = rhs.getJarak();
                            return l < r ? -1 : l == r ? 0 : 1;
                        }
                    });

                    progressBar.setVisibility(View.GONE);
                    googleMap.setOnInfoWindowClickListener(SearchWithMapActivity.this);
                    googleMap.setOnMarkerClickListener(SearchWithMapActivity.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Crashlytics.logException(error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("token", token);
                headers.put("idUser", idUser + "");
                return headers;
            }
        };
        VolleyController.getInstance().addToRequestQueue(request);
    }

    private double distance(double startLat, double startLong,
                            double endLat, double endLong) {

        double dLat  = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat   = Math.toRadians(endLat);

        double a = haversin(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversin(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return round(EARTH_RADIUS * c,1); // <-- d
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private double haversin(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(googleApiClient != null && googleApiClient.isConnected())
            googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient.isConnected())
            googleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!googleApiClient.isConnected())
            googleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        googleApiClient.disconnect();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
//        Toast.makeText(this, "Info window clicked", Toast.LENGTH_SHORT).show();
        SearchWithMapModel model = markerData.get(marker.getPosition());
        if (model != null) {
            Intent in = new Intent(SearchWithMapActivity.this, BankSampahDetailsActivity.class);
            in.putExtra("idBank", model.getId());
            startActivity(in);
        } else {
            Toast.makeText(this, "Data tidak ada", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        setPositionToCamera(marker.getPosition().latitude + CAM_ADJUST, marker.getPosition().longitude);
        marker.showInfoWindow();
        return true;
    }
}