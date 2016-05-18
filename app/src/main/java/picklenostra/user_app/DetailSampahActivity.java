package picklenostra.user_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by marteinstein on 11/05/2016.
 */
public class DetailSampahActivity extends AppCompatActivity {

    TextView sampahKertas, sampahBesi, sampahBotol, sampahPlastik;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_sampah);
        getSupportActionBar().setTitle("Detail Sampah");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sampahKertas = (TextView) findViewById(R.id.jumlah_sampah_kertas);
        sampahPlastik = (TextView)findViewById(R.id.jumlah_sampah_plastik);
        sampahBesi = (TextView) findViewById(R.id.jumlah_sampah_besi);
        sampahBotol = (TextView) findViewById(R.id.jumlah_sampah_botol);
        display();


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

    public void display() {
        int plastik = getIntent().getExtras().getInt("sampahPlastik");
        sampahPlastik.setText(plastik + " kg");

        int besi = getIntent().getExtras().getInt("sampahBesi");
        sampahBesi.setText(besi + " kg");

        int kertas = getIntent().getExtras().getInt("sampahKertas");
        sampahKertas.setText(kertas + " kg");

        int botol = getIntent().getExtras().getInt("sampahBotol");
        sampahBotol.setText(botol + " botol");



    }

}