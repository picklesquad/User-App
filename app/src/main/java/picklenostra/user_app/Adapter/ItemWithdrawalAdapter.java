package picklenostra.user_app.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

import picklenostra.user_app.Model.ItemWithdrawalModel;
import picklenostra.user_app.R;

/**
 * Created by Edwin on 5/13/2016.
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
            viewHolder.statusWithdrawal.setBackgroundColor(Color.rgb(230,138,0));
        }
        else if(statusWithdrawal == 1){
            viewHolder.statusWithdrawal.setText("Accepted");
            viewHolder.statusWithdrawal.setBackgroundColor(Color.rgb(0,153,51));
        }
        else if(statusWithdrawal == 2){
            viewHolder.statusWithdrawal.setText("Rejected");
            viewHolder.statusWithdrawal.setBackgroundColor(Color.rgb(153,0,0));
        }
        else{
            viewHolder.statusWithdrawal.setText("Accepted");
            viewHolder.statusWithdrawal.setBackgroundColor(Color.rgb(0,71,179));
        }

        viewHolder.namaBankSampah.setText(itemWithdrawalModel.getNamaBank());
        viewHolder.nominalWithdrawal.setText("Rp "+itemWithdrawalModel.getNominalWithdrawal()+",-");
        //Convert Long into Date
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(itemWithdrawalModel.getWaktu());

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

        viewHolder.tanggalWithdrawal.setText(date+"/"+month+"/"+cal.get(Calendar.YEAR));
        viewHolder.waktuWithdrawal.setText(hour + ":" + minute);
        return view;
    }

    static class ViewHolder{
        TextView statusWithdrawal, namaBankSampah, nominalWithdrawal, tanggalWithdrawal, waktuWithdrawal;
    }
}
