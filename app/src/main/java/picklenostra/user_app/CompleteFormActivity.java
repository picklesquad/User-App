package picklenostra.user_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import picklenostra.user_app.helper.VolleyController;

public class CompleteFormActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhoneNumber, etAlamat;
    private Button btnNext;
    private TextView errorName, errorEmail, errorPhoneNumber, errorAlamat, errorGender;
    private String nama, email, phoneNum, gender, alamat, url, token, photo;
    Bundle extras;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_form);
        getSupportActionBar().setTitle("Registrasi");

        //Initialize all vars
        url = getResources().getString(R.string.API_URL) + "/addUser";
        extras = getIntent().getExtras();

        etName = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etEmail.setKeyListener(null);
        etAlamat = (EditText) findViewById(R.id.etAlamat);
        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);

        btnNext = (Button) findViewById(R.id.btnNext);
        errorName =(TextView) findViewById(R.id.errorName);
        errorEmail = (TextView) findViewById(R.id.errorEmail);
        errorPhoneNumber = (TextView) findViewById(R.id.errorPhoneNumber);
        errorAlamat = (TextView) findViewById(R.id.errorAlamat);
        errorGender = (TextView) findViewById(R.id.errorGender);
        progressBar = (ProgressBar) findViewById(R.id.register_loading);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFF80CBC4, android.graphics.PorterDuff.Mode.SRC_ATOP);

        SharedPreferences shared = getSharedPreferences(getResources().getString(R.string.KEY_SHARED_PREF), MODE_PRIVATE);
        etName.setText(shared.getString("nama", ""));
        etEmail.setText(shared.getString("email", ""));
        token = getIntent().getExtras().getString("facebookToken", "");
        photo = getIntent().getExtras().getString("facebookPhoto", "");

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validation = true;
                nama = etName.getText().toString();
                email = etEmail.getText().toString();
                phoneNum = etPhoneNumber.getText().toString();
                alamat = etAlamat.getText().toString();

                Log.e("token", "token: " + token);
                Log.e("pict", "photo: " + photo);
//                Log.e("hanjerr", nama + " email " + email + " Numb " + phoneNum + " g " + gender + " b " + alamat);
                if (!validateName()) {
                    validation = false;
                }
                if (!validateEmail()) {
                    validation = false;
                }
                if (!validateAlamat()) {
                    validation= false;
                }
                if (!validatePhoneNumber()) {
                    validation = false;
                }
                if (gender == null) {
                    errorGender.setText("Pilih jenis kelamin");
                    validation = false;
                }

                if(validation){
                    progressBar.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.GONE);

                    errorGender.setText(null);
                    Log.e("photo1", "photo :" + photo);
//                    Toast.makeText(getApplicationContext(), "Thank You!", Toast.LENGTH_SHORT).show();
                    volleyRequestRegister(nama, email, phoneNum, alamat, gender, photo, token);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Input ada yang salah", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private boolean validateEmail() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            errorEmail.setText("Email tidak valid");
            return false;
        } else {
            errorEmail.setText(null);
        }

        return true;
    }

    private boolean validateName() {
        if (etName.getText().toString().trim().isEmpty()) {
            errorName.setText("Nama tidak boleh kosong");
            return false;
        } else if (etName.getText().toString().trim().length() < 6) {
            errorName.setText("Nama minimal 6 karakter");
            return false;
        } else {
            errorName.setText(null);
            return true;
        }
    }

    private boolean validatePhoneNumber() {
        Log.e("tes nope", etPhoneNumber.getText().toString() + " " + etPhoneNumber.getText().toString().trim().length());
        if (etPhoneNumber.getText().toString().trim().isEmpty()) {
            errorPhoneNumber.setText("No. HP tidak boleh kosong");
            return false;
        } else if (etPhoneNumber.getText().toString().trim().length() > 12 ||
                etPhoneNumber.getText().toString().trim().length() < 8 ||
                etPhoneNumber.getText().toString().trim().charAt(0) != '0') {
            errorPhoneNumber.setText("No. HP tidak valid");
            return false;
        } else {
            errorPhoneNumber.setText(null);
            return true;
        }
    }

    private boolean validateAlamat() {
        if (etAlamat.getText().toString().trim().isEmpty()) {
            errorAlamat.setText("Alamat tidak boleh kosong");
            return false;
        } else {
            errorAlamat.setText(null);

            return true;
        }
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onBackPressed() {
        LoginManager.getInstance().logOut();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_laki_laki:
                gender =  "laki-laki";
                break;
            case R.id.radio_perempuan:
                gender = "perempuan";
                break;
        }
    }

    private void volleyRequestRegister(final String nama, final String email, final String phoneNum,
                                       final String alamat, final String gender, final String photo,
                                       final String fbToken){
        Log.e("photo2", "photo :" + photo);
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
                        SharedPreferences shared = getSharedPreferences(getResources().getString(R.string.KEY_SHARED_PREF), MODE_PRIVATE);
                        SharedPreferences.Editor editor = shared.edit();
                        editor.putString("nama", nama);
                        editor.putString("email", email);
                        editor.commit();

                        Intent in = new Intent(CompleteFormActivity.this, DashboardActivity.class);
                        startActivity(in);
                        finish();
                    }

                    Log.e("response", response);
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
                params.put("nama", nama);
                params.put("email", email);
                params.put("phoneNumber", phoneNum);
                params.put("dateOfBirth", "10-10-2010");

                if (gender.equals("laki-laki")) {
                    params.put("gender", "0");
                } else {
                    params.put("gender", "1");
                }
                params.put("alamat", alamat);
                params.put("fbToken", fbToken);
                params.put("facebookPhoto", photo);
                return params;
            }
        };
        VolleyController.getInstance().addToRequestQueue(request);
    }

}
