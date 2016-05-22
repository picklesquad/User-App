package picklenostra.user_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import picklenostra.user_app.helper.VolleyController;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etName, etPhoneNumber, etAddress;
    private Button btnNext;
    private TextView tvName, tvPhoneNumber, tvAddress;
    private TextView tvErrorName, tvErrorPhoneNumber, tvErrorAddress;
    private String name, phoneNumber, address, url;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setTitle("Edit Profil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        url = getResources().getString(R.string.API_URL) + "/editProfile";

        etName = (EditText) findViewById(R.id.etName);
        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        etAddress = (EditText) findViewById(R.id.etAlamat);
        progressBar = (ProgressBar) findViewById(R.id.edit_profile_loading);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFF80CBC4, android.graphics.PorterDuff.Mode.SRC_ATOP);

        btnNext = (Button) findViewById(R.id.btnNext);

        tvErrorName =(TextView) findViewById(R.id.errorName);
        tvErrorPhoneNumber = (TextView) findViewById(R.id.errorPhoneNumber);
        tvErrorAddress = (TextView) findViewById(R.id.errorAlamat);

        //Receive idBank from issued intent
        SharedPreferences shared = getSharedPreferences(getResources().getString(R.string.KEY_SHARED_PREF), MODE_PRIVATE);
        final String token = shared.getString("token", "");
        final int idUser = shared.getInt("idUser", 0);

        etName.setText(getIntent().getExtras().getString("nama", ""));
        etPhoneNumber.setText(getIntent().getExtras().getString("hp",""));
        etAddress.setText(getIntent().getExtras().getString("alamat", ""));;

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = etName.getText().toString();
                phoneNumber = etPhoneNumber.getText().toString();
                address = etAddress.getText().toString();

                boolean validation = true;
                if (!validateName()) {
                    validation = false;
                }
                if (!validateAlamat()) {
                    validation= false;
                }
                if (!validatePhoneNumber()) {
                    validation = false;
                }

                if (validation) {
                    progressBar.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.GONE);
                    volleyRequest(token, idUser, name, phoneNumber, address);
                } else {
                    Toast.makeText(getApplicationContext(), "Input ada yang salah", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, DashboardActivity.class));
    }

    private void volleyRequest(final String token, final int idUser, final String nama, final String phoneNumber,
                               final String alamat){
        StringRequest request =  new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONObject responseObject = new JSONObject(response);
                    if (responseObject.getInt("status") != 201) {
                        Toast.makeText(getApplicationContext(), "No. HP sudah terdaftar", Toast.LENGTH_SHORT).show();
                        btnNext.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getApplicationContext(), "Berhasil edit profil", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditProfileActivity.this, DashboardActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    Crashlytics.logException(e);
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Crashlytics.logException(error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("nama", nama);
                params.put("phoneNumber", phoneNumber);
                params.put("address", alamat);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("token", token);
                headers.put("idUser", idUser + "");
                return headers;
            }
        };
        VolleyController.getInstance().addToRequestQueue(request);
    }

    private boolean validateName() {
        if (etName.getText().toString().trim().isEmpty()) {
            tvErrorName.setText("Nama tidak boleh kosong");
            return false;
        } else if (etName.getText().toString().trim().length() < 6) {
            tvErrorName.setText("Nama minimal 6 karakter");
            return false;
        } else {
            tvErrorName.setText(null);
            return true;
        }
    }

    private boolean validatePhoneNumber() {
        Log.e("tes nope", etPhoneNumber.getText().toString() + " " + etPhoneNumber.getText().toString().trim().length());
        if (etPhoneNumber.getText().toString().trim().isEmpty()) {
            tvErrorPhoneNumber.setText("No. HP tidak boleh kosong");
            return false;
        } else if (etPhoneNumber.getText().toString().trim().length() > 12 ||
                etPhoneNumber.getText().toString().trim().length() < 8 ||
                etPhoneNumber.getText().toString().trim().charAt(0) != '0') {
            tvErrorPhoneNumber.setText("No. HP tidak valid");
            return false;
        } else {
            tvErrorPhoneNumber.setText(null);
            return true;
        }
    }

    private boolean validateAlamat() {
        if (etAddress.getText().toString().trim().isEmpty()) {
            tvErrorAddress.setText("Alamat tidak boleh kosong");
            return false;
        } else {
            tvErrorAddress.setText(null);
            return true;
        }
    }
}
