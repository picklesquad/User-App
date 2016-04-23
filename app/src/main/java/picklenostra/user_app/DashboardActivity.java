package picklenostra.user_app;

import android.content.SharedPreferences;
import android.media.Image;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class DashboardActivity extends ActionBarActivity {

    private DrawerLayout drawerLayout;
    private ImageView profilePicture;
    private TextView profileName, memberSince, memberExperience, balanceContent,
        trashContent, levelContent;
    private UserSessionManager session;
    private String URL = "http://private-74bbc-apiuser1.apiary-mock.com/login";
    private String email, nama, photoUrl, level, memberSince2;
    private int id, star, exp, sampahPlastik, sampahKertas, sampahBotol, sampahBesi, saldo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Initialize all vars
        session = new UserSessionManager(getApplicationContext());
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        profilePicture = (ImageView) findViewById(R.id.dashboard_profile_picture);
        profileName = (TextView) findViewById(R.id.dashboard_profile_name);
        memberSince = (TextView) findViewById(R.id.dashboard_member_since);
        memberExperience = (TextView) findViewById(R.id.dashboard_member_experience);
        balanceContent = (TextView) findViewById(R.id.balance_content);
        trashContent = (TextView) findViewById(R.id.trash_content);
        levelContent = (TextView) findViewById(R.id.level_content);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences shared = getSharedPreferences("PICKLEUSER", MODE_PRIVATE);
        email = shared.getString("email","");

        volleyRequest(email);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

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

    private void volleyRequest(final String email) {
        JsonObjectRequest request = new JsonObjectRequest(Method.POST, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject data = response.getJSONObject("data");
                    id = data.getInt("id");
                    nama = data.getString("nama");
                    photoUrl = data.getString("photo");
                    level = data.getString("level");
                    star = data.getInt("star");
                    exp = data.getInt("exp");
                    memberSince2 = data.getString("memberSince");
                    sampahPlastik = data.getInt("sampahPlastik");
                    sampahKertas = data.getInt("sampahKertas");
                    sampahBotol = data.getInt("sampahBotol");
                    sampahBesi = data.getInt("sampahBesi");
                    saldo = data.getInt("saldo");


                    Picasso.with(getApplicationContext()).load(photoUrl).into(profilePicture);
                    profileName.setText(nama);
                    memberSince.setText(memberSince2);
                    memberExperience.setText(exp+"/100");
                    balanceContent.setText("Rp "+saldo);
                    trashContent.setText(sampahBotol+sampahPlastik+sampahKertas+sampahBesi+" Kg");
                    levelContent.setText(level+"");

                    Log.e("INIAPA",id + " " + nama + " " + photoUrl + " " + saldo + " " + level + (sampahBotol+sampahPlastik+sampahKertas+sampahBesi));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email",email);
                return params;
            }
        };
        VolleyController.getInstance().addToRequestQueue(request);
    }

}