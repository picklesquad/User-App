package picklenostra.user_app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import picklenostra.user_app.DashboardActivity;
import picklenostra.user_app.R;
import picklenostra.user_app.WithdrawFormActivity;
import picklenostra.user_app.helper.RupiahFormatter;
import picklenostra.user_app.helper.VolleyController;
import picklenostra.user_app.model.BankModel;

/**
 * Created by marteinstein on 08/05/2016.
 */
public class ListBankAdapter extends BaseAdapter {

    private ArrayList<BankModel> listBank ;
    private Activity activity;

    public ListBankAdapter(ArrayList<BankModel> listBank, Activity activity) {
        this.listBank = listBank;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return listBank.size();
    }

    @Override
    public Object getItem(int position) {
        return listBank.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listBank.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //jika kosong
        if(convertView==null){
            convertView = LayoutInflater.from(activity).inflate(R.layout.adapter_list_bank,parent,false);
        }

        TextView bankName = (TextView) convertView.findViewById(R.id.id_bank_name);
        TextView saldoInBank = (TextView) convertView.findViewById(R.id.id_saldo_in_bank);
        final Button withdrawBtn = (Button) convertView.findViewById(R.id.id_withdraw_button);
        final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.request_loading);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFF80CBC4, android.graphics.PorterDuff.Mode.SRC_ATOP);

        withdrawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("test", "test button " + listBank.get(position).getBankName());
                progressBar.setVisibility(View.VISIBLE);
                withdrawBtn.setVisibility(View.GONE);
                volleyRequestCheckFirstTransaction(listBank.get(position).getId(), position,
                        progressBar, withdrawBtn);
            }
        });

        //untuk foto nanti pake volley method sendiri bodo ahmad
        String bankNameStr = listBank.get(position).getBankName();
        if (bankNameStr.length() > 21) bankNameStr = bankNameStr.substring(0,19) + "...";
        bankName.setText(bankNameStr);

        double saldo = listBank.get(position).getSaldoInBank();
        saldoInBank.setText(RupiahFormatter.format(saldo));

        return convertView;
    }

    private void volleyRequestCheckFirstTransaction(final int idBank, final int position,
                                                    final ProgressBar pb, final Button btn){
        String url = activity.getResources().getString(R.string.API_URL) + "/requestWithdraw/check";
//        Log.e("test url", url);

        StringRequest request =  new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONObject data = responseObject.getJSONObject("data");
                    Log.e("data", data.toString());
                    if (data.getBoolean("boolean")) {
                        Intent in = new Intent(activity, WithdrawFormActivity.class);
                        in.putExtra("idBank", listBank.get(position).getId());
                        in.putExtra("namaBank", listBank.get(position).getBankName());
                        in.putExtra("saldo", listBank.get(position).getSaldoInBank());
                        Log.e("idBank", listBank.get(position).getId() + " ");
                        activity.finish();
                        activity.startActivity(in);
                    } else {
                        Toast.makeText(activity, "Minimal 90 hari", Toast.LENGTH_SHORT).show();
                        pb.setVisibility(View.GONE);
                        btn.setVisibility(View.VISIBLE);
                    }
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
                params.put("idBank", idBank + "");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferences shared = activity.getSharedPreferences("PICKLEUSER", activity.MODE_PRIVATE);
                String token = shared.getString("token", "");
                int idUser = shared.getInt("idUser",0);
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("token", token);
                headers.put("idUser", idUser+"");
                return headers;
            }
        };
        VolleyController.getInstance().addToRequestQueue(request);
    }
}

