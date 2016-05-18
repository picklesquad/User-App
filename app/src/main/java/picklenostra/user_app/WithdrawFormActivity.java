package picklenostra.user_app;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;

import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import picklenostra.user_app.helper.DateTimeConverter;
import picklenostra.user_app.helper.RupiahFormatter;
import picklenostra.user_app.helper.VolleyController;

public class WithdrawFormActivity extends ActionBarActivity {

    private TextView etNominal, etTanggal, etJam, tvBankName, tvSaldo, errorNominal;
    private ProgressBar progressBar;
    private String nominal, tanggal, jam;
    private Button submitButton;
    private String URL = "";
    int day, year, month, hour, minute;
    private Calendar calendar;
    private final int DATEPICKER_ID = 1;
    private final int TIMEPICKER_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_form);
        getSupportActionBar().setTitle("Permintaan Withdraw");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        URL = getResources().getString(R.string.API_URL) + "/requestWithdraw";

        etNominal = (TextView) findViewById(R.id.etNominal);
        etTanggal = (TextView) findViewById(R.id.etTanggal);
        etJam = (TextView) findViewById(R.id.etTime);
        tvBankName = (TextView) findViewById(R.id.tvNamaBank);
        tvSaldo = (TextView) findViewById(R.id.tvSaldo);
        errorNominal = (TextView) findViewById(R.id.errorNominal);
        submitButton = (Button) findViewById(R.id.btnNext);
        progressBar = (ProgressBar) findViewById(R.id.withdraw_loading);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFF80CBC4, android.graphics.PorterDuff.Mode.SRC_ATOP);

        showDate(day, month, year);
        showTime(hour, minute);

        tvBankName.setText(getIntent().getExtras().getString("namaBank"));
        final double saldo = getIntent().getExtras().getDouble("saldo");
        tvSaldo.setText("Saldo Anda: " + RupiahFormatter.format(saldo));

        etTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATEPICKER_ID);
            }
        });
        etJam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(TIMEPICKER_ID);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (!validateNominal(saldo)) {
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                submitButton.setVisibility(View.GONE);
                Handler myHandler = new Handler();
                myHandler.postDelayed(mMyRunnable, 1000);
            }
        });
    }

    private Runnable mMyRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            nominal = etNominal.getText().toString();
            tanggal = etTanggal.getText().toString();
            jam = etJam.getText().toString();

            boolean error = false;
            long waktu = 0;
            try {
                waktu = DateTimeConverter.dateTimeToMillis(tanggal + " " + jam);
            } catch (ParseException e) {
                error = true;
            } finally {
                if (error) {
                    Toast.makeText(getApplicationContext(), "Tanggal atau jam tidak valid", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences shared = getSharedPreferences(getResources().getString(R.string.KEY_SHARED_PREF), MODE_PRIVATE);
                    String token = shared.getString("token", "");
                    int idUser = shared.getInt("idUser", 0);
                    String idBank = "" + getIntent().getExtras().getInt("idBank");

                    Log.e("test", token + " " + idUser + " " + idBank + " " + nominal + " " + waktu);
                    volleyRequest(token, idUser + "", idBank, nominal, waktu + "");
                }
            }
        }
    };

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int year, int month, int day) {
            showDate(day, month, year);
        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int selectedHour,
                                      int selectedMinute) {
                    showTime(selectedHour, selectedMinute);
                }
            };

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == DATEPICKER_ID) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        } else if (id == TIMEPICKER_ID) {
            return new TimePickerDialog(this, timePickerListener, hour, minute, true);
        }
        return null;
    }

    private boolean validateNominal(double saldo) {
        String nominal = etNominal.getText().toString();

        if (nominal.isEmpty()) {
            errorNominal.setText("Masukkan nominal");
            return false;
        } else if (Double.parseDouble(nominal) > saldo) {
            errorNominal.setText("Saldo tidak cukup");
            return false;
        } else {
            errorNominal.setText(null);
            return true;
        }
    }

    private void volleyRequest(final String token,
                               final String idUser,
                               final String idBank,
                               final String jumlah,
                               final String waktu) {
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    Log.e("response", jsonResponse.toString());
                    int status = jsonResponse.getInt("status");

                    if (status == 201) {
                        Toast.makeText(getApplicationContext(), "Permintaan berhasil terkirim", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(WithdrawFormActivity.this, DashboardActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idBank", idBank);
                params.put("jumlah", jumlah);
                params.put("waktu", waktu);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("token", token);
                headers.put("idUser", idUser);
                return headers;
            }
        };
        VolleyController.getInstance().addToRequestQueue(request);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, ListBankActivity.class));
    }

    private void showDate(int day, int month, int year) {
        String dayStr = day + "";
        String monthStr = (month + 1) + "";
        String yearStr = year + "";

        if (day < 10) {
            dayStr = "0" + dayStr;
        }
        if (month < 10) {
            monthStr = "0" + monthStr;
        }
        etTanggal.setText(dayStr + "/" + monthStr + "/" + yearStr);
    }

    private void showTime(int hour, int minute) {
        String hourStr = hour + "";
        String minuteStr = minute + "";

        if (hour < 10) {
            hourStr = "0" + hourStr;
        }
        if (minute < 10) {
            minuteStr = "0" + minuteStr;
        }
        etJam.setText(hourStr + ":" + minuteStr);
    }
}
