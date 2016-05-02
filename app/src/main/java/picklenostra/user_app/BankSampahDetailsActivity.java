package picklenostra.user_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class BankSampahDetailsActivity extends AppCompatActivity {

    private TextView namaBank, lokasiBank, jumlahNasabahBank, jenisSampahBank;
    private int idBank;
    private final String URL = "http://private-74bbc-apiuser1.apiary-mock.com/bank/%1$s";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_sampah_details);

        //Initialize Action Bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Details");

        //Initialize
        namaBank = (TextView) findViewById(R.id.banksampahdetails_namabank);
        lokasiBank = (TextView) findViewById(R.id.banksampahdetails_lokasi);
        jumlahNasabahBank = (TextView) findViewById(R.id.banksampahdetails_jumlahnasabah);
        jenisSampahBank = (TextView)findViewById(R.id.banksampahdetails_jenissampah);

        //Receive idBank from issued intent
        idBank = getIntent().getIntExtra("idBank",0);

        volleyRequest(idBank);
    }


    private void volleyRequest(int idBank){
        String params = String.format(URL,idBank+"");
        StringRequest request =  new StringRequest(Request.Method.GET, params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONObject bank = responseObject.getJSONObject("bank");
                    String namaBankSampah = bank.getString("namaBank");
                    String alamatBankSampah = bank.getString("alamatBank");
                    int totalNasabah = bank.getInt("totalNasabah");

                    namaBank.setText(namaBankSampah);
                    lokasiBank.setText(alamatBankSampah);
                    jumlahNasabahBank.setText(totalNasabah+" Orang");

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
