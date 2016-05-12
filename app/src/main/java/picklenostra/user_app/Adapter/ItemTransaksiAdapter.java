package picklenostra.user_app.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import picklenostra.user_app.Model.ItemTransaksiModel;
import picklenostra.user_app.R;

/**
 * Created by Edwin on 5/10/2016.
 */
public class ItemTransaksiAdapter extends BaseAdapter {
    ArrayList<ItemTransaksiModel> listItemTransaksiModel;
    Activity activity;

    public ItemTransaksiAdapter(ArrayList<ItemTransaksiModel> listItemTransaksiModel, Activity activity) {
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
            viewHolder.tvNamaBankSampah = (TextView)view.findViewById(R.id.transaksi_namaBankSampah);
            viewHolder.tvJumlahSampah = (TextView)view.findViewById(R.id.transaksi_jumlahSampah);
            viewHolder.tvNominalTransaksi = (TextView)view.findViewById(R.id.transaksi_nominalTransaksi);
            viewHolder.tvWaktuTransaksi = (TextView)view.findViewById(R.id.transaksi_waktuTransaksi);
            view.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)view.getTag();
        }

        ItemTransaksiModel itemTransaksiModel = new ItemTransaksiModel();
        viewHolder.tvNamaBankSampah.setText(itemTransaksiModel.getNamaBankSampah());
        viewHolder.tvJumlahSampah.setText(itemTransaksiModel.getJumlahSampah());
        viewHolder.tvNominalTransaksi.setText(itemTransaksiModel.getNominalTransaksi());
        viewHolder.tvWaktuTransaksi.setText(String.valueOf(itemTransaksiModel.getWaktu()));
        return view;
    }

    static class ViewHolder{
        TextView tvNamaBankSampah, tvJumlahSampah, tvNominalTransaksi, tvWaktuTransaksi;
    }
}
