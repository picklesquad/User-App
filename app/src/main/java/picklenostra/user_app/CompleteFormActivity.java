package picklenostra.user_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CompleteFormActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhoneNumber, etBirthday, etGender;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_form);

        //Initialize all vars
        etName = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etBirthday = (EditText) findViewById(R.id.etBirthday);
        etGender = (EditText) findViewById(R.id.etGender);
        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        btnNext = (Button) findViewById(R.id.btnNext);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences shared = getSharedPreferences(getResources().getString(R.string.KEY_SHARED_PREF), MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.putString("phoneNumber", etPhoneNumber.getText().toString());
                editor.putString("birthday", etBirthday.getText().toString());
                editor.putString("gender", etGender.getText().toString());
                editor.commit();

                Intent in = new Intent(CompleteFormActivity.this, DashboardActivity.class);
                startActivity(in);
                finish();
            }
        });
    }
}
