package picklenostra.user_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchWithMapActivity extends ActionBarActivity implements
        LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;
    private Location location;
    private LocationRequest locationRequest;
    private double curLat, curLong;

    private ListView listView;
    private ArrayList<SearchWithMapModel> listBankSampah;
    private SearchWithMapAdapter adapter;

    private final int INTERVAL = 1000 * 30;
    private final String URL = "http://private-74bbc-apiuser1.apiary-mock.com/search?loc=location";
    private final int EARTH_RADIUS = 6371;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_with_map);

        //Initialize Action Bar
        getSupportActionBar().setTitle("Search Bank Sampah");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initialize
        listView = (ListView) findViewById(R.id.searchwithmap_listview);
        listBankSampah = new ArrayList<>();
        adapter = new SearchWithMapAdapter(this, listBankSampah);

        buildGoogleApiClient();
        initalizeMap();
        volleyRequest();
        listView.setAdapter(adapter);
        setPositionToCamera(curLat, curLong);

        //ListView Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int idBank = listBankSampah.get(position).getIdBank();
                Intent intent = new Intent(SearchWithMapActivity.this, BankSampahDetailsActivity.class);
                intent.putExtra("idBank",idBank);
                startActivity(intent);
            }
        });
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


    private void setBlueCircleRadius(double latitude, double longitude){
        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(latitude,longitude))
                .radius(500)
                .fillColor(Color.argb(20,0,154,225))
                .strokeColor(Color.TRANSPARENT);
        googleMap.addCircle(circleOptions);
    }

    private void setPositionToCamera(double latitude, double longitude){
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude,longitude)).zoom(15).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void setMarker(double latitude, double longitude, String namaBank, String alamatBank) {
        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude))
                .title(namaBank)
                .snippet(alamatBank)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bank_marker_green));
        googleMap.addMarker(marker);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("LOCATION", "Location services connected");
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(INTERVAL);

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
            setPositionToCamera(curLat,curLong);
            setBlueCircleRadius(curLat,curLong);
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
            setPositionToCamera(curLat,curLong);
            setBlueCircleRadius(curLat,curLong);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("LOCATION", "Location services failed, check your internet connection");
    }

    private void volleyRequest(){
        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray banks = responseObject.getJSONArray("bank");
                    for(int i = 0; i < banks.length(); i++){
                        JSONObject bank = (JSONObject) banks.get(i);
                        int idBank = bank.getInt("id");
                        String namaBank = bank.getString("namaBank");
                        String alamatBank = bank.getString("alamatBank");
                        double latitude = bank.getDouble("latitude");
                        double longitude = bank.getDouble("longitude");
                        double jarak = distance(curLat,curLong,latitude,longitude);

                        //SetMarker
                        setMarker(latitude,longitude,namaBank, alamatBank);

                        //Create Model
                        SearchWithMapModel searchWithMapModel = new SearchWithMapModel();
                        searchWithMapModel.setIdBank(idBank);
                        searchWithMapModel.setNamaBank(namaBank);
                        searchWithMapModel.setNamaJalan(alamatBank);
                        searchWithMapModel.setJarak(jarak);
                        searchWithMapModel.setpictureUrl("https://avatars0.githubusercontent.com/u/10989633?v=3&s=400");
                        listBankSampah.add(searchWithMapModel);
                        adapter.notifyDataSetChanged();
                    }
                    Collections.sort(listBankSampah, new Comparator<SearchWithMapModel>() {
                        @Override
                        public int compare(SearchWithMapModel lhs, SearchWithMapModel rhs) {
                            return Double.toString(lhs.getJarak()).compareTo(Double.toString(rhs.getJarak()));
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
