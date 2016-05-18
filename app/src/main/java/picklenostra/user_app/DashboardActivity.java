package picklenostra.user_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.facebook.login.LoginManager;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import picklenostra.user_app.helper.DateTimeConverter;
import picklenostra.user_app.helper.RupiahFormatter;
import picklenostra.user_app.helper.UserSessionManager;
import picklenostra.user_app.helper.VolleyController;

public class DashboardActivity extends ActionBarActivity {

    private DrawerLayout drawerLayout;
    private ImageView profilePicture;
    private TextView tvProfileName, tvMemberSince, tvMemberExperience, tvBalanceContent, tvTrashContent, tvLevelContent;
    private CardView cardBalance, cardSampah;
    private UserSessionManager session;
    private String url = "";
    private String email, nama, photoUrl, level, token, exp, regid;
    private int id, star, sampahPlastik, sampahKertas, sampahBotol, sampahBesi, saldo;
    private long memberSince;

    private final String PROJECT_NUMBER = "810813850020";
    private GoogleCloudMessaging gcm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().setTitle("Dashboard");

        url = getResources().getString(R.string.API_URL) + "/login";

        //Initialize all vars
        session = new UserSessionManager(getApplicationContext());
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        profilePicture = (ImageView) findViewById(R.id.dashboard_profile_picture);
        tvProfileName = (TextView) findViewById(R.id.dashboard_profile_name);
        tvMemberSince = (TextView) findViewById(R.id.dashboard_member_since);
        tvMemberExperience = (TextView) findViewById(R.id.dashboard_member_experience);
        tvBalanceContent = (TextView) findViewById(R.id.balance_content);
        tvTrashContent = (TextView) findViewById(R.id.trash_content);
        tvLevelContent = (TextView) findViewById(R.id.level_content);
        cardBalance = (CardView) findViewById(R.id.cardBalance);
        cardSampah = (CardView) findViewById(R.id.cardTrash);

        cardBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, picklenostra.user_app.ListBankActivity.class));
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
        getMenuInflater().inflate(R.menu.dashboard_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_search){
            startActivity(new Intent(DashboardActivity.this, SearchWithMapActivity.class));
        }
        if(id == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        if(id == R.id.action_settings){
            return true;
        }
        if(id == R.id.action_logout){
            session.logoutUser();
            LoginManager.getInstance().logOut();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void volleyRequest(final String email){
        StringRequest login =  new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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
                    Log.e("memberSince", memberSince + "");

                    Picasso.with(getApplicationContext()).load(photoUrl).into(profilePicture);
                    tvProfileName.setText(nama);
                    tvMemberSince.setText("Anggota sejak " + DateTimeConverter.generateTanggalWaktu(memberSince)[0]);
                    tvMemberExperience.setText("Exp: " + exp);
                    tvBalanceContent.setText(RupiahFormatter.format(saldo));
                    tvTrashContent.setText(sampahPlastik + sampahKertas + sampahBesi + " kg dan " + sampahBotol + " botol");
                    tvLevelContent.setText(level);

                    getRegId();
                } catch (JSONException e) {

                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

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

    public void getRegId(){
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
                    msg = "Error " + e.getMessage();
                    Log.e("msg", msg);
                }
                msg = "Device registered, registration ID=" + regid;
                Log.i("GCM",  msg);

                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.e("msg", msg + "\n");
            }
        }.execute(null, null, null);
    }
}