package picklenostra.user_app;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import picklenostra.user_app.adapter.ListBankAdapter;
import picklenostra.user_app.model.BankModel;

/**
 * Created by marteinstein on 08/05/2016.
 */
public class ListBankActivity extends ActionBarActivity {

    ListView listBank;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_bank);
        getSupportActionBar().setTitle("My Subscribetion");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        listBank = (ListView)findViewById(R.id.id_list_bank);

        ArrayList<BankModel> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list.add(new BankModel(i*3, "Bank Pickle "+i , i * 30    , "www."+i+"asemelele.com") );
        }
        final ListBankAdapter adapter = new ListBankAdapter(list,this);

        listBank.setAdapter(adapter);
        listBank.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ListBankActivity.this, "bank " + ((BankModel) adapter.getItem(position)).getBankName(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
