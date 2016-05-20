package picklenostra.user_app;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import picklenostra.user_app.helper.RupiahFormatter;
import picklenostra.user_app.helper.VolleyController;


public class BankSampahDetailsActivity extends AppCompatActivity {

    private TextView namaBank, lokasiBank, lokasiBankDesc, jumlahNasabahBank, tvIsSubscribed, narahubungBank;
    private Button subsButton;
    private ProgressBar progressBar;
    private LinearLayout bankLayout;
    private ImageView bankPict;

    private int idBank;
    private String urlBankDetails = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_sampah_details);

        //Initialize Action Bar.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Detail Bank Sampah");
        actionBar.setElevation(0);

        //Initialize
        bankLayout = (LinearLayout) findViewById(R.id.bank_data);
        namaBank = (TextView) findViewById(R.id.banksampahdetails_namabank);
        lokasiBank = (TextView) findViewById(R.id.banksampahdetails_lokasi);
        lokasiBankDesc = (TextView) findViewById(R.id.banksampahdetails_lokasiDesc);
        jumlahNasabahBank = (TextView) findViewById(R.id.banksampahdetails_jumlahnasabah);
        tvIsSubscribed = (TextView) findViewById(R.id.tvIsSubscribed);
        narahubungBank = (TextView) findViewById(R.id.banksampahdetails_narahubung);
        subsButton = (Button) findViewById(R.id.subscribe_button);
        bankPict = (ImageView) findViewById(R.id.image_bank);
        progressBar = (ProgressBar) findViewById(R.id.bank_loading);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFF80CBC4, android.graphics.PorterDuff.Mode.SRC_ATOP);

        //Receive idBank from issued intent
        SharedPreferences shared = getSharedPreferences(getResources().getString(R.string.KEY_SHARED_PREF), MODE_PRIVATE);
        String token = shared.getString("token", "");
        int idUser = shared.getInt("idUser", 0);

        Log.e("token", token);
        Log.e("idUser", idUser+ "");
        idBank = getIntent().getExtras().getInt("idBank",0);
        urlBankDetails = getResources().getString(R.string.API_URL) + "/bank/" + idBank + "/details";
        volleyRequest(idBank, token, idUser);
    }


    private void volleyRequest(final int idBank, final String token, final int idUser){
        Log.e("url bank", urlBankDetails);
        Log.e("token", token);
        Log.e("idUser", idUser+ "");
        StringRequest request =  new StringRequest(Request.Method.GET, urlBankDetails, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("dapet", response);
                try {
                    Log.e("masuk", "msk");
                    JSONObject responseObject = new JSONObject(response);
                    JSONObject data = responseObject.getJSONObject("data");
                    final String namaBankSampah = data.getString("namaBank");
                    String alamatBankSampah = data.getString("lokasi");
                    String alamatDescBankSampah = data.getString("lokasiDesc");
                    String narahubung = data.getString("narahubung");
                    String phoneNumber = data.getString("phoneNumber");
                    int jumlahNasabah = data.getInt("jumlahNasabah");
                    boolean isSubscribed = data.getBoolean("isSubscribed");

                    Log.e("tes", "tes");
                    namaBank.setText(namaBankSampah);
                    lokasiBank.setText(alamatBankSampah);
                    lokasiBankDesc.setText(alamatDescBankSampah);
                    narahubungBank.setText(narahubung + " (" + phoneNumber + ")");
                    jumlahNasabahBank.setText(jumlahNasabah + " Orang");

                    if (isSubscribed) {
                        subsButton.setVisibility(View.GONE);
                        tvIsSubscribed.setText("(Berlangganan)");
                    } else {
                        subsButton.setVisibility(View.VISIBLE);
                        subsButton.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                final Dialog dialog = new Dialog(BankSampahDetailsActivity.this);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.dialog_berlangganan);
                                dialog.setTitle("Konfirmasi");

                                TextView namaBank = (TextView) dialog.findViewById(R.id.berlangganan_nama_bank);
                                Button batalBtn = (Button) dialog.findViewById(R.id.berlangganan_batal);
                                Button yaBtn = (Button) dialog.findViewById(R.id.berlangganan_ya);

                                namaBank.setText(namaBankSampah);

                                dialog.show();
                                // if decline button is clicked, close the custom dialog_konfirm_transaksi
                                batalBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });

                                yaBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        progressBar.setVisibility(View.VISIBLE);
                                        volleyRequestBerlangganan(token, idUser, idBank);
                                        dialog.dismiss();
                                        subsButton.setVisibility(View.GONE);
                                    }
                                });
                            }
                        });
                    }
                    progressBar.setVisibility(View.GONE);
                    bankLayout.setVisibility(View.VISIBLE);
                    namaBank.setVisibility(View.VISIBLE);
                    bankPict.setVisibility(View.VISIBLE);
                    Log.e("test", "load profile complete");
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
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

    private void volleyRequestBerlangganan(final String token, final int idUser, final int idBank){
        String url = getResources().getString(R.string.API_URL) + "/bank/subscribe";
        StringRequest request =  new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    String pesan = responseObject.getString("message");
                    Toast.makeText(BankSampahDetailsActivity.this, pesan, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
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
                params.put("idBank", "" + idBank);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("token", token);
                headers.put("idUser", "" + idUser);
                return headers;
            }
        };
        VolleyController.getInstance().addToRequestQueue(request);
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
