package picklenostra.user_app.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import picklenostra.user_app.Adapter.ItemWithdrawalAdapter;
import picklenostra.user_app.Helper.VolleyController;
import picklenostra.user_app.Model.ItemTransaksiModel;
import picklenostra.user_app.Model.ItemWithdrawalModel;
import picklenostra.user_app.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WithdrawalFragment extends Fragment {

    private ListView listView;
    private ArrayList<ItemWithdrawalModel> listItemWithdrawalModel;
    private String URL = "http://private-74bbc-apiuser1.apiary-mock.com/bank/%1$s/withdrawals";
    private ItemWithdrawalAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.withdrawal_fragment, container, false);
        listView = (ListView)view.findViewById(R.id.withdrawal_list_view);
        listItemWithdrawalModel = new ArrayList<>();
        volleyRequest(1);
        adapter = new ItemWithdrawalAdapter(this.getActivity(), listItemWithdrawalModel);
        listView.setAdapter(adapter);
        return view;
    }

    private void volleyRequest(final int idBank){
        String params = String.format(URL, idBank);
        StringRequest request = new StringRequest(Request.Method.GET, params, new Response.Listener<String>() {
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

}
