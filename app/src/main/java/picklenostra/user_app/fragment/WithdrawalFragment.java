package picklenostra.user_app.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
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

import picklenostra.user_app.adapter.ItemWithdrawalAdapter;
import picklenostra.user_app.helper.VolleyController;
import picklenostra.user_app.model.ItemWithdrawalModel;
import picklenostra.user_app.R;

/**
 * Created by Syukri Mullia Adil P on 5/19/2016.
 */
public class WithdrawalFragment extends Fragment {

    private ListView listView;
    private ArrayList<ItemWithdrawalModel> listItemWithdrawalModel;
    private String URL = "http://104.155.206.184:8080/api/user/withdrawals";
    private ItemWithdrawalAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvNotFound;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.withdrawal_fragment, container, false);
        listView = (ListView)view.findViewById(R.id.withdrawal_list_view);
        progressBar = (ProgressBar) view.findViewById(R.id.withdrawal_loading);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFF80CBC4, android.graphics.PorterDuff.Mode.SRC_ATOP);
        tvNotFound = (TextView) view.findViewById(R.id.withdrawal_not_found);

        listItemWithdrawalModel = new ArrayList<>();

        SharedPreferences shared = getActivity()
                .getSharedPreferences(getResources()
                        .getString(R.string.KEY_SHARED_PREF), getActivity().MODE_PRIVATE);
        String token = shared.getString("token", "");
        int idUser = shared.getInt("idUser", 0);

        volleyRequest(idUser, token);
        adapter = new ItemWithdrawalAdapter(this.getActivity(), listItemWithdrawalModel);
        listView.setAdapter(adapter);
        return view;
    }

    private void volleyRequest(final int idUser, final String token){
        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject objectResponse = new JSONObject(response);
                    JSONArray datas = objectResponse.getJSONArray("data");
                    for (int i = 0; i < datas.length(); i++){
                        JSONObject withdrawals = datas.getJSONObject(i);
                        int idWithdrawal = withdrawals.getInt("idWithdraw");
                        String namaBank = withdrawals.getString("namaBank");
                        long waktu = withdrawals.getLong("waktu");
                        int nominal = withdrawals.getInt("nominal");
                        int status = withdrawals.getInt("status");

                        ItemWithdrawalModel itemWithdrawalModel = new ItemWithdrawalModel();
                        itemWithdrawalModel.setId(idWithdrawal);
                        itemWithdrawalModel.setNamaBank(namaBank);
                        itemWithdrawalModel.setWaktu(waktu);
                        itemWithdrawalModel.setNominalWithdrawal(nominal);
                        itemWithdrawalModel.setStatusInteger(status);
                        listItemWithdrawalModel.add(itemWithdrawalModel);
                        adapter.notifyDataSetChanged();
                    }

                    progressBar.setVisibility(View.GONE);
                    if (datas.length() == 0) {
                        tvNotFound.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Crashlytics.logException(error);
            }
        }){
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

}
