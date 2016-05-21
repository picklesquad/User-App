package picklenostra.user_app;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;

import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import picklenostra.user_app.helper.DateTimeConverter;
import picklenostra.user_app.helper.PickleFormatter;
import picklenostra.user_app.helper.VolleyController;

public class WithdrawFormActivity extends ActionBarActivity {

    private TextView etNominal, etTanggal, etJam, tvBankName, tvSaldo, errorNominal, errorTanggal, errorWaktu;
    private ProgressBar progressBar;
    private String nominal, tanggal, jam;
    private Button submitButton;
    private String URL = "";
    private int currentDay, currentYear, currentMonth, currentHour, currentMinute;
    private int chosenDay, chosenYear, chosenMonth, chosenHour, chosenMinute;
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
        chosenYear = currentYear = calendar.get(Calendar.YEAR);
        chosenMonth = currentMonth = calendar.get(Calendar.MONTH);
        chosenDay = currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        chosenHour = currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        chosenMinute = currentMinute = calendar.get(Calendar.MINUTE);

        URL = getResources().getString(R.string.API_URL) + "/requestWithdraw";

        etNominal = (TextView) findViewById(R.id.etNominal);
        etTanggal = (TextView) findViewById(R.id.etTanggal);
        etJam = (TextView) findViewById(R.id.etTime);
        tvBankName = (TextView) findViewById(R.id.tvNamaBank);
        tvSaldo = (TextView) findViewById(R.id.tvSaldo);
        errorNominal = (TextView) findViewById(R.id.errorNominal);
        errorTanggal = (TextView) findViewById(R.id.errorTanggal);
        errorWaktu = (TextView) findViewById(R.id.errorWaktu);
        submitButton = (Button) findViewById(R.id.btnNext);
        progressBar = (ProgressBar) findViewById(R.id.withdraw_loading);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFF80CBC4, android.graphics.PorterDuff.Mode.SRC_ATOP);

        showDate(currentDay, currentMonth, currentYear);
        showTime(currentHour, currentMinute);

        tvBankName.setText(getIntent().getExtras().getString("namaBank"));
        final double saldo = getIntent().getExtras().getDouble("saldo");
        tvSaldo.setText("Saldo Anda: " + PickleFormatter.formatHarga(saldo));

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
                boolean invalid = false;
                if (!validateNominal(saldo)) {
                    invalid = true;
                }
                if (!validateTanggal() || !validateWaktu()) {
                    invalid = true;
                }

                if (invalid) {
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

                    volleyRequest(token, idUser + "", idBank, nominal, waktu + "");
                }
            }
        }
    };

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int selectedYear, int selectedMonth, int selectedDay) {
            chosenDay = selectedDay;
            chosenMonth = selectedMonth;
            chosenYear = selectedYear;
            showDate(selectedDay, selectedMonth, selectedYear);
        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int selectedHour,
                                      int selectedMinute) {
                    chosenHour = selectedHour;
                    chosenMinute = selectedMinute;
                    showTime(selectedHour, selectedMinute);
                }
            };

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == DATEPICKER_ID) {
            return new DatePickerDialog(this, myDateListener, chosenYear, chosenMonth, chosenDay);
        } else if (id == TIMEPICKER_ID) {
            return new TimePickerDialog(this, timePickerListener, chosenHour, chosenMinute, true);
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

    private boolean validateTanggal() {
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH);
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        Log.e("day skrg", currentDay + "");
        Log.e("day dipilih", chosenDay + "");
        Log.e("month skrg", currentDay + "");
        Log.e("month dipilih", chosenMonth + "");
        Log.e("year skrg", currentYear + "");
        Log.e("year dipilih", chosenYear + "");
        boolean result;
        if (chosenYear < currentYear) {
            result = false;
        } else if (chosenYear == currentYear){
            if (chosenMonth < currentMonth) {
                result = false;
            } else if (chosenMonth == currentMonth) {
                result = chosenDay >= currentDay;
            } else {
                result = true;
            }
        } else {
            result = true;
        }

        if (result) {
            errorTanggal.setText(null);
        } else {
            errorTanggal.setText("Tanggal tidak valid");
        }
        return result;
    }

    private boolean validateWaktu() {
        currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        currentMinute = calendar.get(Calendar.MINUTE);
        Log.e("hour skrg", currentHour + "");
        Log.e("hour dipilih", chosenHour + "");
        Log.e("minute skrg", currentMinute + "");
        Log.e("minute dipilih", chosenMinute + "");
        boolean result;
        if (chosenDay == currentDay) {
            if (chosenHour < currentHour) {
                result = false;
            } else if (chosenHour == currentHour) {
                result = chosenMinute >= currentMinute;
            } else {
                result = true;
            }
        } else {
            result = true;
        }

        if (result) {
            errorWaktu.setText(null);
        } else {
            errorWaktu.setText("Jam tidak valid");
        }
        return result;
    }


    private void volleyRequest(final String token, final String idUser, final String idBank,
                               final String jumlah, final String waktu) {
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
                    Crashlytics.logException(e);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Crashlytics.logException(error.getCause());
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
        startActivity(new Intent(this, ListLanggananActivity.class));
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
