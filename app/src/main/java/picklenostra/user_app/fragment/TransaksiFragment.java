package picklenostra.user_app.fragment;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import picklenostra.user_app.adapter.ItemTransaksiAdapter;
import picklenostra.user_app.helper.RupiahFormatter;
import picklenostra.user_app.helper.VolleyController;
import picklenostra.user_app.model.ItemTransaksiModel;
import picklenostra.user_app.R;

/**
 * Created by Syukri Mullia Adil P on 5/19/2016.
 */
public class TransaksiFragment extends Fragment {

    private ListView listView;
    private ArrayList<ItemTransaksiModel> listItemTransaksi;

    private String urlTransactions = "";
    private String urlUpdate = "";

    ProgressBar progressBar;
    private TextView tvNotFound;

    private ItemTransaksiAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transaksi_fragment, container, false);

        listView = (ListView) view.findViewById(R.id.transaksi_list_view);
        progressBar = (ProgressBar) view.findViewById(R.id.transaksi_loading);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFF80CBC4, android.graphics.PorterDuff.Mode.SRC_ATOP);
        tvNotFound = (TextView) view.findViewById(R.id.transaksi_not_found);

        urlTransactions = getActivity().getResources().getString(R.string.API_URL) + "/transactions";
        urlUpdate = getActivity().getResources().getString(R.string.API_URL) + "/updateTransaction";

        listItemTransaksi = new ArrayList<>();

        SharedPreferences shared = getActivity()
                .getSharedPreferences(getResources()
                        .getString(R.string.KEY_SHARED_PREF), getActivity().MODE_PRIVATE);
        final String token = shared.getString("token", "");
        final int idUser = shared.getInt("idUser", 0);

        volleyRequest(idUser, token);

        adapter = new ItemTransaksiAdapter(this.getActivity(), listItemTransaksi);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("tes", "tessss");
                final ItemTransaksiModel transaksi = (ItemTransaksiModel) adapter.getItem(position);
                if(transaksi.getStatusTransaksi() == 0) {
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_konfirm_transaksi);
                    dialog.setTitle("Konfirmasi");

                    TextView namaBank = (TextView) dialog.findViewById(R.id.konfirm_nama_bank);
                    TextView sampahPlastik = (TextView) dialog.findViewById(R.id.konfirm_plastik);
                    TextView sampahKertas = (TextView) dialog.findViewById(R.id.konfirm_kertas);
                    TextView sampahBesi = (TextView) dialog.findViewById(R.id.konfirm_besi);
                    TextView sampahBotol = (TextView) dialog.findViewById(R.id.konfirm_botol);
                    TextView harga = (TextView) dialog.findViewById(R.id.konfirm_harga);

                    namaBank.setText(transaksi.getNamaBankSampah());
                    sampahPlastik.setText(transaksi.getSampahPlastik() + " kg plastik");
                    sampahKertas.setText(transaksi.getSampahKertas() + " kg kertas");
                    sampahBesi.setText(transaksi.getSampahBesi() + " kg besi");
                    sampahBotol.setText(transaksi.getSampahBotol() + " buah botol");
                    harga.setText("Harga: " + RupiahFormatter.format(transaksi.getNominalTransaksi()));

                    Button tolak = (Button) dialog.findViewById(R.id.konfirm_tolak);
                    Button terima = (Button) dialog.findViewById(R.id.konfirm_terima);

                    dialog.show();
                    // if decline button is clicked, close the custom dialog_konfirm_transaksi
                    tolak.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progressBar.setVisibility(View.VISIBLE);
                            volleyRequestUpdate(token, idUser, transaksi, 2, dialog);
                        }
                    });

                    terima.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progressBar.setVisibility(View.VISIBLE);
                            volleyRequestUpdate(token, idUser, transaksi, 1, dialog);
                        }
                    });
                }
            }
        });

        return view;
    }

    private void volleyRequest(final int idUser, final String token){
        StringRequest request = new StringRequest(Request.Method.GET, urlTransactions, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray datas = responseObject.getJSONArray("data");
                    for(int i = 0; i < datas.length(); i++){
                        JSONObject data = (JSONObject) datas.get(i);
                        int idTransaksi = data.getInt("idTransaksi");
                        long waktu = data.getLong("waktu");
                        String namaBank = data.getString("namaBank");
                        double nominal = data.getDouble("harga");
                        int statusTransaksi = data.getInt("status");
                        double sampahPlastik = data.getDouble("sampahPlastik");
                        int sampahBotol = data.getInt("sampahBotol");
                        double sampahBesi = data.getDouble("sampahBesi");
                        double sampahKertas = data.getDouble("sampahKertas");

                        ItemTransaksiModel itemTransaksiModel = new ItemTransaksiModel();
                        itemTransaksiModel.setIdTransaksi(idTransaksi);
                        itemTransaksiModel.setNamaBankSampah(namaBank);
                        itemTransaksiModel.setSampahPlastik(sampahPlastik);
                        itemTransaksiModel.setSampahBesi(sampahBesi);
                        itemTransaksiModel.setSampahKertas(sampahKertas);
                        itemTransaksiModel.setSampahBotol(sampahBotol);
                        itemTransaksiModel.setJumlahSampah((sampahPlastik + sampahKertas + sampahBesi) + " kg dan " + sampahBotol + " botol");
                        itemTransaksiModel.setNominalTransaksi(nominal + "");
                        itemTransaksiModel.setWaktu(waktu);
                        itemTransaksiModel.setStatusTransaksi(statusTransaksi);

//                        Log.e("response", data.toString());
//                        Log.e("status", statusTransaksi + "");
//                        Log.e("status applied", itemTransaksiModel.getStatusTransaksi() + "");
                        listItemTransaksi.add(itemTransaksiModel);
                        adapter.notifyDataSetChanged();
                    }
                    progressBar.setVisibility(View.GONE);
                    if (datas.length() == 0) {
                        tvNotFound.setVisibility(View.VISIBLE);
                    }

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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("token", token);
                headers.put("idUser", "" + idUser);
                return headers;
            }
        };
        VolleyController.getInstance().addToRequestQueue(request);
    }

    private void volleyRequestUpdate(final String token, final int idUser, final ItemTransaksiModel transaksi,
                                     final int status, final Dialog dialog){
        StringRequest request =  new StringRequest(Request.Method.PUT, urlUpdate, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONObject data = responseObject.getJSONObject("data");
                    String hasil = "";
                    if (data.getInt("status") == 1) {
                        hasil = " diterima";
                    } else {
                        hasil = " ditolak";
                    }
                    Toast.makeText(getActivity(), "Transaksi " + data.getInt("id") + hasil, Toast.LENGTH_SHORT).show();

                    listItemTransaksi.remove(transaksi);
                    transaksi.setStatusTransaksi(data.getInt("status"));
                    listItemTransaksi.add(transaksi);
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    dialog.dismiss();
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("idTransaksi", "" + transaksi.getIdTransaksi());
                headers.put("status", "" + status);
                headers.put("token", token);
                headers.put("idUser", "" + idUser);
                return headers;
            }
        };
        VolleyController.getInstance().addToRequestQueue(request);
    }
}
