package picklenostra.user_app.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import picklenostra.user_app.Model.ItemTransaksiModel;
import picklenostra.user_app.R;

/**
 * Created by Edwin on 5/10/2016.
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
            viewHolder.tvNamaBankSampah = (TextView)view.findViewById(R.id.transaksi_namaBankSampah);
            viewHolder.tvJumlahSampah = (TextView)view.findViewById(R.id.transaksi_jumlahSampah);
            viewHolder.tvNominalTransaksi = (TextView)view.findViewById(R.id.transaksi_nominalTransaksi);
            viewHolder.tvTanggalTransaksi = (TextView)view.findViewById(R.id.transaksi_tanggal);
            viewHolder.tvWaktuTransaksi = (TextView)view.findViewById(R.id.transaksi_waktu);
            view.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)view.getTag();
        }

        ItemTransaksiModel itemTransaksiModel = (ItemTransaksiModel)getItem(position);
        viewHolder.tvNamaBankSampah.setText(itemTransaksiModel.getNamaBankSampah());
        viewHolder.tvJumlahSampah.setText("Jumlah: " + itemTransaksiModel.getJumlahSampah());
        viewHolder.tvNominalTransaksi.setText("Rp "+itemTransaksiModel.getNominalTransaksi() + ",-");
        //Convert long to date
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(itemTransaksiModel.getWaktu());
        //Ubah bilangan yang gak ada 0 didepannya -> 19/1/2015 -> 19/01/2015

        String date = cal.get(Calendar.DAY_OF_MONTH)+"";
        String month = cal.get(Calendar.MONTH)+"";
        String hour = cal.get(Calendar.HOUR_OF_DAY)+"";
        String minute = cal.get(Calendar.MINUTE)+"";

        if(date.length()==1)
            date = "0"+date;
        if(month.length()==1)
            month = "0"+month;
        if(hour.length()==1)
            hour = "0"+hour;
        if(minute.length()==1)
            minute = "0"+minute;

        viewHolder.tvTanggalTransaksi.setText(date+"/"+month+"/"+cal.get(Calendar.YEAR));
        viewHolder.tvWaktuTransaksi.setText(hour + ":" + minute);
        return view;
    }

    static class ViewHolder{
        TextView tvNamaBankSampah, tvJumlahSampah, tvNominalTransaksi, tvTanggalTransaksi, tvWaktuTransaksi;
    }
}
