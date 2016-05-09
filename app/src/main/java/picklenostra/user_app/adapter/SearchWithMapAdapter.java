package picklenostra.user_app.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import picklenostra.user_app.R;
import picklenostra.user_app.model.SearchWithMapModel;

/**
 * Created by Edwin on 4/30/2016.
 */
public class SearchWithMapAdapter extends BaseAdapter {

    ArrayList<SearchWithMapModel> listBankSampah;
    Activity activity;

    public SearchWithMapAdapter(Activity activity, ArrayList<SearchWithMapModel> listBankSampah){
        this.activity = activity;
        this.listBankSampah = listBankSampah;
    }

    @Override
    public int getCount() {
        return listBankSampah.size();
    }

    @Override
    public Object getItem(int position) {
        return listBankSampah.get(position);
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
            view = inflater.inflate(R.layout.item_searchwithmap,null);
            viewHolder.namaBank = (TextView) view.findViewById(R.id.searchwithmap_namaBank);
            viewHolder.namaJalan = (TextView) view.findViewById(R.id.searchwithmap_namaJalan);
            viewHolder.jarak = (TextView) view.findViewById(R.id.searchwithmap_jarak);
            viewHolder.picture = (ImageView) view.findViewById(R.id.searchwithmap_picture);
            view.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)view.getTag();
        }

        SearchWithMapModel searchWithMapModel = (SearchWithMapModel)getItem(position);
        viewHolder.namaBank.setText(searchWithMapModel.getNamaBank());
        viewHolder.namaJalan.setText(searchWithMapModel.getNamaJalan());
        viewHolder.jarak.setText(searchWithMapModel.getJarak() + " Km");
        Picasso.with(activity).load(searchWithMapModel.getpictureUrl()).into(viewHolder.picture);

        return view;
    }


    static class ViewHolder{
        ImageView picture;
        TextView namaBank, namaJalan, jarak;
    }
}
