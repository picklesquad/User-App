package picklenostra.user_app.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import picklenostra.user_app.helper.DateTimeConverter;
import picklenostra.user_app.helper.PickleFormatter;
import picklenostra.user_app.model.ItemTransaksiModel;
import picklenostra.user_app.R;

/**
 * Created by Syukri Mullia Adil P on 5/19/2016.
 */
public class ItemTransaksiAdapter extends BaseAdapter {
    ArrayList<ItemTransaksiModel> listItemTransaksiModel;
    Activity activity;

    public ItemTransaksiAdapter(Activity activity, ArrayList<ItemTransaksiModel> listItemTransaksiModel) {
        this.listItemTransaksiModel = listItemTransaksiModel;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return listItemTransaksiModel.size();
    }

    @Override
    public Object getItem(int position) {
        return listItemTransaksiModel.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder = null;

        if (view == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_transaksifragment,null);
            viewHolder.tvNamaBankSampah = (TextView) view.findViewById(R.id.transaksi_namaBankSampah);
            viewHolder.tvJumlahSampah = (TextView) view.findViewById(R.id.transaksi_jumlahSampah);
            viewHolder.tvNominalTransaksi = (TextView) view.findViewById(R.id.transaksi_nominalTransaksi);
            viewHolder.tvTanggalTransaksi = (TextView) view.findViewById(R.id.transaksi_tanggal);
            viewHolder.tvWaktuTransaksi = (TextView) view.findViewById(R.id.transaksi_waktu);
            viewHolder.tvStatusTransaksi = (TextView) view.findViewById(R.id.transaksi_status);
            viewHolder.tvStatusTransaksiDone = (TextView) view.findViewById(R.id.transaksi_status_done);
            view.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)view.getTag();
        }

        ItemTransaksiModel itemTransaksiModel = (ItemTransaksiModel)getItem(position);
        viewHolder.tvNamaBankSampah.setText(PickleFormatter.formatTextLength(itemTransaksiModel.getNamaBankSampah(), 28));
        viewHolder.tvJumlahSampah.setText(itemTransaksiModel.getJumlahSampah());
        viewHolder.tvNominalTransaksi.setText(PickleFormatter.formatHarga(itemTransaksiModel.getNominalTransaksi()));

        String[] tanggalWaktu = DateTimeConverter.generateTanggalWaktu(itemTransaksiModel.getWaktu());
        viewHolder.tvTanggalTransaksi.setText(tanggalWaktu[0]);
        viewHolder.tvWaktuTransaksi.setText(tanggalWaktu[1]);

//        Log.e(tanggalWaktu[1], "" + itemTransaksiModel.getStatusTransaksi());
        if (itemTransaksiModel.getStatusTransaksi() == 0) {
            viewHolder.tvStatusTransaksi.setVisibility(View.VISIBLE);
            viewHolder.tvStatusTransaksiDone.setVisibility(View.GONE);
        } else {
            viewHolder.tvStatusTransaksi.setVisibility(View.GONE);
            viewHolder.tvStatusTransaksiDone.setVisibility(View.VISIBLE);
            if (itemTransaksiModel.getStatusTransaksi() == 1) {
                viewHolder.tvStatusTransaksiDone.setText("Sudah disetujui");
            } else {
                viewHolder.tvStatusTransaksiDone.setText("Sudah ditolak");
            }
        }
        return view;
    }

    static class ViewHolder{
        TextView tvNamaBankSampah, tvJumlahSampah, tvNominalTransaksi, tvTanggalTransaksi,
                tvWaktuTransaksi, tvStatusTransaksi, tvStatusTransaksiDone;
    }
}