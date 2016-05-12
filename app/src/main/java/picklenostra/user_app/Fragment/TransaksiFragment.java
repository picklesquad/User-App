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

import picklenostra.user_app.Model.ItemTransaksiModel;
import picklenostra.user_app.R;

public class TransaksiFragment extends Fragment {

    private ListView listView;
    private ArrayList<ItemTransaksiModel> listItemTransaksi;
    private String URL = "http://private-74bbc-apiuser1.apiary-mock.com/bank/%1$s/transactions";

    public TransaksiFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transaksi_fragment, container, false);

        listView = (ListView) view.findViewById(R.id.transaksi_list_view);
        listItemTransaksi = new ArrayList<>();



        return inflater.inflate(R.layout.transaksi_fragment, container, false);
    }

    private void volleyRequest(int id){
        String params = String.format(URL, id);

        StringRequest request = new StringRequest(Request.Method.GET, params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray datas = responseObject.getJSONArray("data");
                    for(int i = 0; i < datas.length(); i++){
                        JSONObject data = (JSONObject) datas.get(i);
                        int idTransaksi = data.getInt("idTransaksi");
                        long waktu = data.getLong("waktu");
                        double nominal = data.getDouble("nominal");
                        int statusTransaksi = data.getInt("status");
                        int sampahPlastik = data.getInt("sampahPlastik");
                        int sampahBotol = data.getInt("sampahBotol");
                        int sampahBesi = data.getInt("sampahBesi");
                        int sampahKertas = data.getInt("sampahKertas");



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

    }

}
