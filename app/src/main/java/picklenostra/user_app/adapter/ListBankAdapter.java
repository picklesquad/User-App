package picklenostra.user_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



import java.util.ArrayList;

import picklenostra.user_app.R;
import picklenostra.user_app.model.BankModel;

/**
 * Created by marteinstein on 08/05/2016.
 */
public class ListBankAdapter extends BaseAdapter {

    private ArrayList<BankModel> listBank ;
    private Context context;

    public ListBankAdapter(ArrayList<BankModel> listBank, Context context) {
        this.listBank = listBank;
        this.context = context;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        //jika kosong
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_list_bank,parent,false);
        }
        ImageView photoBank = (ImageView) convertView.findViewById(R.id.id_photo_bank);
        TextView bankName = (TextView) convertView.findViewById(R.id.id_bank_name);
        TextView saldoInBank = (TextView) convertView.findViewById(R.id.id_saldo_in_bank);

        //untuk foto nanti pake volley method sendiri bodo ahmad
        bankName.setText(listBank.get(position).getBankName());
        saldoInBank.setText("Rp "+listBank.get(position).getSaldoInBank());

        return convertView;
    }
}

