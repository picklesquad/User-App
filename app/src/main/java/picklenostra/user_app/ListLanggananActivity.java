package picklenostra.user_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import picklenostra.user_app.adapter.ItemLanggananAdapter;
import picklenostra.user_app.helper.VolleyController;
import picklenostra.user_app.model.ItemLanggananModel;

/**
 * Created by marteinstein on 08/05/2016.
 */
public class ListLanggananActivity extends ActionBarActivity {

    private ListView listBank;
    private String url = "";
    private ArrayList<ItemLanggananModel> list = new ArrayList<>();
    ListLanggananActivity thisClass = this;
    private ItemLanggananAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvNotFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_langganan);
        getSupportActionBar().setTitle("Langganan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.list_bank_loading);
        tvNotFound = (TextView) findViewById(R.id.not_found);

        progressBar.setVisibility(View.VISIBLE);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFF80CBC4, android.graphics.PorterDuff.Mode.SRC_ATOP);
        url = getResources().getString(R.string.API_URL) + "/balances";

        listBank = (ListView) findViewById(R.id.id_list_bank);

        SharedPreferences shared = getSharedPreferences(getResources().getString(R.string.KEY_SHARED_PREF), MODE_PRIVATE);
        String token = shared.getString("token", "");
        int idUser = shared.getInt("idUser", 0);

        volleyRequest(token, idUser);

        adapter = new ItemLanggananAdapter(list, ListLanggananActivity.this);
        listBank.setAdapter(adapter);

        listBank.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("tes", "tessss");
                Toast.makeText(ListLanggananActivity.this, "bank " + ((ItemLanggananModel) adapter.getItem(position)).getBankName(), Toast.LENGTH_SHORT).show();
            }
        });
        listBank.setItemsCanFocus(true);
    }

    private void volleyRequest(final String token, final int idUser){
        StringRequest request =  new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray arrayBalance = responseObject.getJSONArray("data");
//                    Log.e("array", responseObject.toString());
                    for (int i = 0; i < arrayBalance.length(); i++) {
                        JSONObject bankObject = arrayBalance.getJSONObject(i);
                        ItemLanggananModel bankModel = new ItemLanggananModel();
                        bankModel.setId(bankObject.getInt("idBank"));
                        bankModel.setBankName(bankObject.getString("namaBank"));
                        bankModel.setSaldoInBank(bankObject.getInt("balance"));
                        list.add(bankModel);
                        adapter.notifyDataSetChanged();
//                        Log.e("bankModel", bankModel.toString());
//                        Log.e("add", "" + list.add(bankModel));
                    }
//                    Log.e("jumlah2", list.size() + "");
                    progressBar.setVisibility(View.GONE);
                    if (list.isEmpty()) {
                        tvNotFound.setVisibility(View.VISIBLE);
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
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("token", token);
                headers.put("idUser", idUser + "");
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
