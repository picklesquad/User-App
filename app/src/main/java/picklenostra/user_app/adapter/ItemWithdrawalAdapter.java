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
import picklenostra.user_app.model.ItemWithdrawalModel;
import picklenostra.user_app.R;

/**
 * Created by Syukri Mullia Adil P on 5/19/2016.
 */
public class ItemWithdrawalAdapter extends BaseAdapter {

    Activity activity;
    ArrayList<ItemWithdrawalModel> listItemWithdrawalModel;

    public ItemWithdrawalAdapter(Activity activity, ArrayList<ItemWithdrawalModel> listItemWithdrawalModel) {
        this.activity = activity;
        this.listItemWithdrawalModel = listItemWithdrawalModel;
    }

    @Override
    public int getCount() {
        return listItemWithdrawalModel.size();
    }

    @Override
    public Object getItem(int position) {
        return listItemWithdrawalModel.get(position);
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
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_withdrawalfragment,null);
            viewHolder.statusWithdrawal = (TextView) view.findViewById(R.id.withdrawal_status);
            viewHolder.namaBankSampah = (TextView) view.findViewById(R.id.withdrawal_namaBankSampah);
            viewHolder.nominalWithdrawal = (TextView) view.findViewById(R.id.withdrawal_nominalWithdrawal);
            viewHolder.tanggalWithdrawal = (TextView) view.findViewById(R.id.withdrawal_tanggal);
            viewHolder.waktuWithdrawal = (TextView) view.findViewById(R.id.withdrawal_waktu);
            view.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) view.getTag();
        }

        ItemWithdrawalModel itemWithdrawalModel = (ItemWithdrawalModel) getItem(position);
        //Set Status dan Backgroundnya
        int statusWithdrawal = itemWithdrawalModel.getStatusInteger();
        if (statusWithdrawal == 0){
            viewHolder.statusWithdrawal.setText("Waiting");
            viewHolder.statusWithdrawal.setBackgroundResource(R.drawable.style_rounded_textview_yellow);
        }
        else if(statusWithdrawal == 1){
            viewHolder.statusWithdrawal.setText("Accepted");
            viewHolder.statusWithdrawal.setBackgroundResource(R.drawable.style_rounded_textview_green);
        }
        else if(statusWithdrawal == 2){
            viewHolder.statusWithdrawal.setText("Done");
            viewHolder.statusWithdrawal.setBackgroundResource(R.drawable.style_rounded_textview_blue);
        }
        else{
            viewHolder.statusWithdrawal.setText("Rejected");
            viewHolder.statusWithdrawal.setBackgroundResource(R.drawable.style_rounded_textview_red);
        }

        viewHolder.namaBankSampah.setText(PickleFormatter.formatTextLength(itemWithdrawalModel.getNamaBank(), 23));
        viewHolder.nominalWithdrawal.setText(PickleFormatter.formatHarga(itemWithdrawalModel.getNominalWithdrawal()));

        String[] tanggalWaktu = DateTimeConverter.generateTanggalWaktu(itemWithdrawalModel.getWaktu());
        viewHolder.tanggalWithdrawal.setText(tanggalWaktu[0]);
        viewHolder.waktuWithdrawal.setText(tanggalWaktu[1]);
        return view;
    }

    static class ViewHolder{
        TextView statusWithdrawal, namaBankSampah, nominalWithdrawal, tanggalWithdrawal, waktuWithdrawal;
    }
}
