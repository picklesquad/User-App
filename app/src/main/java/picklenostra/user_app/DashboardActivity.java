package picklenostra.user_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import picklenostra.user_app.helper.DateTimeConverter;
import picklenostra.user_app.helper.PickleFormatter;
import picklenostra.user_app.helper.UserSessionManager;
import picklenostra.user_app.helper.VolleyController;

public class DashboardActivity extends ActionBarActivity {

    private DrawerLayout drawerLayout;
    private ImageView profilePicture;
    private TextView tvProfileName, tvMemberSince, tvMemberExperience, tvBalanceContent, tvTrashContent, tvLevelContent;
    private CardView cardBalance, cardSampah;
    private ProgressBar progressBar;
    private LinearLayout dashboard;

    private UserSessionManager session;
    private GoogleCloudMessaging gcm;
    private NavigationView navView;

    private String urlLogin = "";
    private String urlSendGCM = "";
    private String email, nama, photoUrl, level, token, exp, regid;
    private int id, star, sampahPlastik, sampahKertas, sampahBotol, sampahBesi, saldo;
    private long memberSince;

    private final String PROJECT_NUMBER = "810813850020";
    private boolean drawOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().setTitle("Dashboard");

        urlLogin = getResources().getString(R.string.API_URL) + "/login";
        urlSendGCM = getResources().getString(R.string.API_URL) + "/gcmRegister";

        //Initialize all vars
        session = new UserSessionManager(getApplicationContext());
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        dashboard = (LinearLayout) findViewById(R.id.dashboard);
        dashboard.setVisibility(View.GONE);
        profilePicture = (ImageView) findViewById(R.id.dashboard_profile_picture);
        tvProfileName = (TextView) findViewById(R.id.dashboard_profile_name);
        tvMemberSince = (TextView) findViewById(R.id.dashboard_member_since);
        tvMemberExperience = (TextView) findViewById(R.id.dashboard_member_experience);
        tvBalanceContent = (TextView) findViewById(R.id.balance_content);
        tvTrashContent = (TextView) findViewById(R.id.trash_content);
        tvLevelContent = (TextView) findViewById(R.id.level_content);
        cardBalance = (CardView) findViewById(R.id.cardBalance);
        cardSampah = (CardView) findViewById(R.id.cardTrash);
        navView = (NavigationView) findViewById(R.id.navigation_view);
        progressBar = (ProgressBar) findViewById(R.id.dashboard_loading);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFF80CBC4, android.graphics.PorterDuff.Mode.SRC_ATOP);
        progressBar.setVisibility(View.VISIBLE);

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();
                switch (itemId){
                    case R.id.navigation_item_logout:
                        session.logoutUser();
                        LoginManager.getInstance().logOut();
                        finish();
                        return true;
                    case R.id.navigation_item_histori:
                        startActivity(new Intent(DashboardActivity.this, HistoryActivity.class));
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        item.setChecked(false);
                        return true;
                    default:
                        return false;
                }
            }
        });

        cardBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, ListLanggananActivity.class));
            }
        });

        cardSampah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(DashboardActivity.this, picklenostra.user_app.DetailSampahActivity.class);
                in.putExtra("sampahPlastik", sampahPlastik);
                in.putExtra("sampahBesi", sampahBesi);
                in.putExtra("sampahKertas", sampahKertas);
                in.putExtra("sampahBotol", sampahBotol);
                startActivity(in);
            }
        });

        //Initialize Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences shared = getSharedPreferences(getResources().getString(R.string.KEY_SHARED_PREF), MODE_PRIVATE);
        email = shared.getString("email","");
        volleyRequest(email);
        session = new UserSessionManager(getApplicationContext());
        session.createUserLogin(email, "password");
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_search){
            startActivity(new Intent(DashboardActivity.this, SearchWithMapActivity.class));
        }
        if(id == android.R.id.home){
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            drawOpened = !drawOpened;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void volleyRequest(final String email){
        StringRequest login =  new StringRequest(Request.Method.POST, urlLogin, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONObject data = responseObject.getJSONObject("data");
                    id = data.getInt("id");
                    token = data.getString("apiToken");
                    nama = data.getString("nama");
                    photoUrl = data.getString("photo");
                    level = data.getString("level");
                    star = data.getInt("stars");
                    exp = data.getString("exp");
                    memberSince = data.getLong("memberSince");
                    sampahPlastik = data.getInt("sampahPlastik");
                    sampahKertas = data.getInt("sampahKertas");
                    sampahBotol = data.getInt("sampahBotol");
                    sampahBesi = data.getInt("sampahBesi");
                    saldo = data.getInt("saldo");

//                    Log.e("token", data.getString("apiToken"));
//                    Log.e("id", "" + data.getInt("id"));

                    SharedPreferences shared = getSharedPreferences(getResources().getString(R.string.KEY_SHARED_PREF), MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putString("token", token);
                    editor.putInt("idUser", id);
                    editor.commit();

                    Log.e("token", token);

                    Picasso.with(getApplicationContext()).load(photoUrl).into(profilePicture);
                    tvProfileName.setText(nama);
                    tvMemberSince.setText("Anggota sejak " + DateTimeConverter.generateTanggalWaktu(memberSince)[0]);
                    tvMemberExperience.setText("Exp: " + exp);
                    tvBalanceContent.setText(PickleFormatter.formatHarga(saldo));
                    tvTrashContent.setText(sampahPlastik + sampahKertas + sampahBesi + " kg dan " + sampahBotol + " botol");
                    tvLevelContent.setText(level);

                    getRegId(id);
                    progressBar.setVisibility(View.GONE);
                    dashboard.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    Crashlytics.logException(e);
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Crashlytics.logException(error.getCause());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email",email);
                return params;
            }
        };
        VolleyController.getInstance().addToRequestQueue(login);
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.navigation_item_edit_profil) {
            // Handle the camera action
        } else if (id == R.id.navigation_item_histori) {

        } else if (id == R.id.navigation_item_logout) {
            session.logoutUser();
            LoginManager.getInstance().logOut();
            finish();
        } else if (id == R.id.navigation_item_settings) {

        }
        return false;
    }

    public void getRegId(final int idUser){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                }
                try{
                    regid = gcm.register(PROJECT_NUMBER);
                }catch (IOException e){
                    Crashlytics.logException(e);
                    msg = "Error " + e.getMessage();
                    Log.e("msg", msg);
                }
                msg = "Device registered, registration ID=" + regid;
                Log.i("GCM",  msg);
                volleySendGCMId(regid, idUser);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.e("msg", msg + "\n");
            }
        }.execute(null, null, null);
    }

    private void volleySendGCMId(final String key, final int idUser) {
        StringRequest request =  new StringRequest(Request.Method.PUT, urlSendGCM, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    if (responseObject.get("data") == null) {
                        Log.e("error", "GCM Null");
                    } else {
                        Log.e("berhasil", "GCM berhasil");
                    }
                } catch (JSONException e) {
                    Crashlytics.logException(e);
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Crashlytics.logException(error.getCause());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("key", key);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("idUser", idUser + "");
                return headers;
            }
        };
        VolleyController.getInstance().addToRequestQueue(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}